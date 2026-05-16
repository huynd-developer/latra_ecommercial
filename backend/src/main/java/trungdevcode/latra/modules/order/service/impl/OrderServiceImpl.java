// backend/src/main/java/trungdevcode/latra/modules/order/service/impl/OrderServiceImpl.java
package trungdevcode.latra.modules.order.service.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import trungdevcode.latra.common.exception.BusinessException;
import trungdevcode.latra.common.exception.ErrorCode;
import trungdevcode.latra.entity.*;
import trungdevcode.latra.enums.*;
import trungdevcode.latra.modules.order.dto.request.CheckoutRequestDTO;
import trungdevcode.latra.modules.order.dto.request.WebCheckoutRequestDTO;
import trungdevcode.latra.modules.order.dto.response.OrderResponse;
import trungdevcode.latra.modules.order.service.OrderService;
import trungdevcode.latra.modules.payment.service.PaymentService; // 🔥 IMPORT THÊM PAYMENT SERVICE
import trungdevcode.latra.repository.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ImeiRepository imeiRepository;
    private final ProductVariantRepository variantRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final ShipmentRepository shipmentRepository;
    private final VoucherRepository voucherRepository;
    private final PaymentService paymentService; // 🔥 INJECT PAYMENT SERVICE VÀO ĐÂY
    private final ModelMapper modelMapper;

    // =========================================================================
    // 1. LUỒNG WEB CHECKOUT: Khách hàng tự đặt online
    // =========================================================================
    @Override
    @Transactional
    public Order createWebOrder(WebCheckoutRequestDTO request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "User ID"));

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CART_EMPTY));
        if (cart.getItems().isEmpty()) {
            throw new BusinessException(ErrorCode.CART_EMPTY);
        }

        BigDecimal rawTotal = BigDecimal.ZERO;
        List<OrderDetail> orderDetails = new ArrayList<>();
        List<Imei> imeisToLock = new ArrayList<>();
        Order order = new Order();

        for (CartItem item : cart.getItems()) {
            ProductVariant variant = item.getVariant();
            int quantityNeeded = item.getQuantity();

            BigDecimal actualPrice = variant.getPrice();
            rawTotal = rawTotal.add(actualPrice.multiply(BigDecimal.valueOf(quantityNeeded)));

            // 🔥 FIX LỖI A: Sử dụng hàm lấy IMEI có bọc Pessimistic Lock
            List<Imei> availableImeis = imeiRepository.findAvailableImeisWithLock(variant, ImeiStatus.AVAILABLE);
            if (availableImeis.size() < quantityNeeded) {
                throw new BusinessException(ErrorCode.OUT_OF_STOCK, variant.getSku());
            }

            for (int i = 0; i < quantityNeeded; i++) {
                Imei imei = availableImeis.get(i);
                imei.setStatus(ImeiStatus.LOCKED_FOR_PAYMENT);
                imei.setOrder(order);
                imeisToLock.add(imei);
            }

            variant.setStock(variant.getStock() - quantityNeeded);
            variantRepository.save(variant);

            OrderDetail detail = new OrderDetail();
            detail.setVariant(variant);
            detail.setQuantity(quantityNeeded);
            detail.setPrice(actualPrice);
            orderDetails.add(detail);
        }

        BigDecimal finalTotal = rawTotal;
        Voucher appliedVoucher = null;

        if (request.getVoucherCode() != null && !request.getVoucherCode().isBlank()) {
            appliedVoucher = voucherRepository.findByCodeAndStatus(request.getVoucherCode().trim(), 1)
                    .orElseThrow(() -> new BusinessException(ErrorCode.VOUCHER_NOT_FOUND));

            LocalDateTime now = LocalDateTime.now();
            if (now.isBefore(appliedVoucher.getStartDate()) || now.isAfter(appliedVoucher.getExpiredAt())) {
                throw new BusinessException(ErrorCode.VOUCHER_EXPIRED);
            }
            if (appliedVoucher.getUsageLimit() <= 0) {
                throw new BusinessException(ErrorCode.VOUCHER_OUT_OF_USAGE);
            }
            if (rawTotal.compareTo(appliedVoucher.getMinOrderValue()) < 0) {
                throw new BusinessException(ErrorCode.VOUCHER_MIN_ORDER_NOT_MET);
            }

            BigDecimal discountAmount = BigDecimal.ZERO;
            if (appliedVoucher.getDiscountType() == DiscountType.FIXED_AMOUNT) {
                discountAmount = appliedVoucher.getValue();
            } else if (appliedVoucher.getDiscountType() == DiscountType.PERCENTAGE) {
                discountAmount = rawTotal.multiply(appliedVoucher.getValue()).divide(BigDecimal.valueOf(100));
                if (appliedVoucher.getMaxDiscountAmount() != null && discountAmount.compareTo(appliedVoucher.getMaxDiscountAmount()) > 0) {
                    discountAmount = appliedVoucher.getMaxDiscountAmount();
                }
            }

            finalTotal = rawTotal.subtract(discountAmount).max(BigDecimal.ZERO);

            // 🔥 FIX LỖI B: Gọi Query Atomic để trừ database trực tiếp thay vì save()
            int updatedRows = voucherRepository.decrementVoucherUsage(appliedVoucher.getCode());
            if (updatedRows == 0) {
                // Nếu 2 người cùng áp mã cuối cùng, người chậm hơn 1 mili-giây sẽ văng lỗi này và Rollback toàn bộ đơn hàng
                throw new BusinessException(ErrorCode.VOUCHER_OUT_OF_USAGE, "Mã giảm giá đã hết lượt sử dụng ngay trước đó!");
            }
        }

        // ... (Phần logic lưu Order, Shipment, Cart giữ nguyên như cũ) ...
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING_PAYMENT);
        order.setCreatedAt(LocalDateTime.now());
        order.setTotalAmount(finalTotal);
        order.setNote(request.getNote());
        order.setVoucher(appliedVoucher);

        PaymentMethod method;
        try {
            method = PaymentMethod.valueOf(request.getPaymentMethod().toUpperCase());
        } catch (Exception e) {
            method = PaymentMethod.CASH;
        }
        order.setPaymentMethod(method);

        Order savedOrder = orderRepository.save(order);

        for (OrderDetail detail : orderDetails) {
            detail.setId(new OrderDetailKey(savedOrder.getId(), detail.getVariant().getId()));
            detail.setOrder(savedOrder);
        }
        orderDetailRepository.saveAll(orderDetails);
        imeiRepository.saveAll(imeisToLock);

        Shipment shipment = new Shipment();
        shipment.setOrder(savedOrder);
        shipment.setRecipientName(user.getFullName());
        shipment.setRecipientPhone(user.getPhone());
        shipment.setFullAddress(request.getShippingAddress());
        shipment.setStatus(ShipmentStatus.PREPARING);
        shipmentRepository.save(shipment);

        cart.getItems().clear();
        cartRepository.save(cart);

        return savedOrder;
    }

    // =========================================================================
    // 2. LUỒNG POS CHECKOUT: Nhân viên quét mã vạch bán tại quầy
    // =========================================================================
    @Override
    @Transactional
    public String checkoutPOS(CheckoutRequestDTO request) {
        if (request.getScannedImeis() == null || request.getScannedImeis().isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_DATA, "Chưa quét mã IMEI nào");
        }

        User customer = null;
        if (request.getCustomerPhone() != null && !request.getCustomerPhone().isBlank()) {
            customer = userRepository.findByPhone(request.getCustomerPhone());
            if (customer == null) {
                customer = new User();
                customer.setFullName(request.getCustomerName() != null ? request.getCustomerName() : "Khách lẻ");
                customer.setPhone(request.getCustomerPhone());
                customer.setUsername(request.getCustomerPhone());
                customer.setPassword("NO_LOGIN_ALLOWED");
                customer.setStatus(1);
                customer = userRepository.save(customer);
            }
        }

        BigDecimal backendCalculatedTotal = BigDecimal.ZERO;
        List<Imei> imeisToUpdate = new ArrayList<>();
        Map<ProductVariant, Integer> variantCountMap = new HashMap<>();

        for (String code : request.getScannedImeis()) {
            Imei imei = imeiRepository.findByImeiCode(code)
                    .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Mã IMEI " + code));

            if (imei.getStatus() != ImeiStatus.AVAILABLE) {
                throw new BusinessException(ErrorCode.IMEI_NOT_AVAILABLE, code);
            }

            backendCalculatedTotal = backendCalculatedTotal.add(imei.getVariant().getPrice());

            variantCountMap.put(imei.getVariant(), variantCountMap.getOrDefault(imei.getVariant(), 0) + 1);
            imeisToUpdate.add(imei);
        }

        if (request.getTotalAmount().compareTo(backendCalculatedTotal) != 0) {
            throw new BusinessException(ErrorCode.PRICE_MISMATCH, "FE gửi: " + request.getTotalAmount() + ", Server tính: " + backendCalculatedTotal);
        }

        Order order = new Order();
        if (request.getUserId() != null) {
            order.setEmployee(userRepository.findById(request.getUserId()).orElse(null));
        }
        order.setUser(customer);
        order.setTotalAmount(backendCalculatedTotal);
        order.setNote(request.getNote());
        order.setStatus(OrderStatus.COMPLETED);
        order.setCreatedAt(LocalDateTime.now());

        PaymentMethod method;
        try {
            method = PaymentMethod.valueOf(request.getPaymentMethod().toUpperCase());
        } catch (Exception e) {
            method = PaymentMethod.CASH;
        }
        order.setPaymentMethod(method);

        if (method == PaymentMethod.SPLIT) {
            order.setCashAmount(request.getCashAmount());
            order.setTransferAmount(request.getTransferAmount());
        } else if (method == PaymentMethod.CASH) {
            order.setCashAmount(backendCalculatedTotal);
            order.setTransferAmount(BigDecimal.ZERO);
        } else {
            order.setCashAmount(BigDecimal.ZERO);
            order.setTransferAmount(backendCalculatedTotal);
        }

        Order savedOrder = orderRepository.save(order);

        List<OrderDetail> orderDetails = new ArrayList<>();
        for (Map.Entry<ProductVariant, Integer> entry : variantCountMap.entrySet()) {
            ProductVariant variant = entry.getKey();
            int qty = entry.getValue();

            variant.setStock(variant.getStock() - qty);
            variantRepository.save(variant);

            OrderDetail detail = new OrderDetail();
            detail.setId(new OrderDetailKey(savedOrder.getId(), variant.getId()));
            detail.setOrder(savedOrder);
            detail.setVariant(variant);
            detail.setQuantity(qty);
            detail.setPrice(variant.getPrice());
            orderDetails.add(detail);
        }

        for (Imei imei : imeisToUpdate) {
            imei.setStatus(ImeiStatus.SOLD);
            imei.setOrder(savedOrder);
        }

        orderDetailRepository.saveAll(orderDetails);
        imeiRepository.saveAll(imeisToUpdate);

        return "Chốt đơn thành công! Mã Hóa Đơn: #" + savedOrder.getId();
    }

    // =========================================================================
    // 3. QUẢN LÝ ĐƠN HÀNG (STATE MACHINE)
    // =========================================================================

    @Override
    public Page<OrderResponse.DanhSach> getOrders(String statusStr, Pageable pageable) {
        OrderStatus status = null;
        if (statusStr != null && !statusStr.isBlank()) {
            try {
                status = OrderStatus.valueOf(statusStr.toUpperCase());
            } catch (Exception e) {
                throw new BusinessException(ErrorCode.INVALID_DATA, "Trạng thái không tồn tại");
            }
        }

        Page<Order> orders = (status != null)
                ? orderRepository.findByStatus(status, pageable)
                : orderRepository.findAll(pageable);

        return orders.map(order -> {
            OrderResponse.DanhSach dto = modelMapper.map(order, OrderResponse.DanhSach.class);
            dto.setCustomerName(order.getUser() != null ? order.getUser().getFullName() : "Khách lẻ");
            dto.setCustomerPhone(order.getUser() != null ? order.getUser().getPhone() : "");
            dto.setStatus(order.getStatus().name());
            return dto;
        });
    }

    @Override
    public OrderResponse.OrderDetail getOrderDetails(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Đơn hàng " + orderId));

        OrderResponse.OrderDetail dto = modelMapper.map(order, OrderResponse.OrderDetail.class);
        dto.setCustomerName(order.getUser() != null ? order.getUser().getFullName() : "Khách lẻ");
        dto.setCustomerPhone(order.getUser() != null ? order.getUser().getPhone() : "");
        dto.setEmployeeName(order.getEmployee() != null ? order.getEmployee().getFullName() : "Online");
        dto.setPaymentMethod(order.getPaymentMethod().name());
        dto.setStatus(order.getStatus().name());

        if (order.getShipment() != null) {
            dto.setShippingAddress(order.getShipment().getFullAddress());
        }

        List<Imei> imeisOfOrder = imeiRepository.findByOrder(order);
        List<OrderResponse.Item> items = order.getOrderDetails().stream().map(detail -> {
            OrderResponse.Item itemDto = new OrderResponse.Item();
            itemDto.setVariantId(detail.getVariant().getId());
            itemDto.setProductName(detail.getVariant().getProduct() != null ? detail.getVariant().getProduct().getName() : "Unknown");
            itemDto.setColor(detail.getVariant().getColor());
            itemDto.setStorage(detail.getVariant().getStorage());
            itemDto.setQuantity(detail.getQuantity());
            itemDto.setPrice(detail.getPrice());

            String imeiCodes = imeisOfOrder.stream()
                    .filter(imei -> imei.getVariant().getId().equals(detail.getVariant().getId()))
                    .map(Imei::getImeiCode)
                    .collect(Collectors.joining(", "));
            itemDto.setImeiCode(imeiCodes.isEmpty() ? "N/A" : imeiCodes);
            return itemDto;
        }).collect(Collectors.toList());

        dto.setItems(items);
        return dto;
    }

    @Override
    @Transactional
    public void updateOrderStatus(Long orderId, OrderResponse.UpdateStatus request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Đơn hàng"));

        OrderStatus oldStatus = order.getStatus();
        OrderStatus newStatus;
        try {
            newStatus = OrderStatus.valueOf(request.getStatus().toUpperCase());
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.INVALID_DATA, "Trạng thái truyền lên không hợp lệ");
        }

        // VALIDATE CỖ MÁY TRẠNG THÁI
        if (oldStatus == OrderStatus.COMPLETED || oldStatus == OrderStatus.CANCELLED) {
            throw new BusinessException(ErrorCode.INVALID_STATUS_TRANSITION, "Đơn hàng đã chốt, không thể thay đổi");
        }

        // LOGIC HỦY ĐƠN
        if (newStatus == OrderStatus.CANCELLED) {

            // 1. Phục hồi tồn kho Variant
            for (OrderDetail detail : order.getOrderDetails()) {
                ProductVariant variant = detail.getVariant();
                variant.setStock(variant.getStock() + detail.getQuantity());
                variantRepository.save(variant);
            }

            // 2. Phục hồi lượt sử dụng Voucher (Nếu có)
            if (order.getVoucher() != null) {
                Voucher voucher = order.getVoucher();
                voucher.setUsageLimit(voucher.getUsageLimit() + 1);
                voucherRepository.save(voucher);
            }

            // 3. Nếu đơn chưa trả tiền (PENDING_PAYMENT), chỉ cần nhả kho IMEI
            if (oldStatus == OrderStatus.PENDING_PAYMENT) {
                List<Imei> lockedImeis = imeiRepository.findByOrder(order);
                for (Imei imei : lockedImeis) {
                    imei.setStatus(ImeiStatus.AVAILABLE);
                    imei.setOrder(null);
                }
                imeiRepository.saveAll(lockedImeis);
            }

            // 4. 🔥 TÍCH HỢP PAYMENT SERVICE: Gọi hoàn tiền tự động qua VNPAY
            if (oldStatus == OrderStatus.PAID && order.getPaymentMethod() == PaymentMethod.VNPAY) {
                paymentService.refundOrder(order.getId());
            }
        }

        // LOGIC ĐANG GIAO HÀNG
        if (newStatus == OrderStatus.SHIPPED && order.getShipment() != null) {
            order.getShipment().setStatus(ShipmentStatus.DELIVERING);
            shipmentRepository.save(order.getShipment());
        }

        order.setStatus(newStatus);
        orderRepository.save(order);
    }
}