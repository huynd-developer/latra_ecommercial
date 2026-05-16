// backend/src/main/java/trungdevcode/latra/modules/shipment/service/impl/ShipmentServiceImpl.java
package trungdevcode.latra.modules.shipment.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import trungdevcode.latra.common.exception.BusinessException;
import trungdevcode.latra.entity.Order;
import trungdevcode.latra.entity.Shipment;
import trungdevcode.latra.enums.OrderStatus;
import trungdevcode.latra.enums.ShipmentStatus;
import trungdevcode.latra.enums.ShippingCarrier;
import trungdevcode.latra.modules.shipment.dto.request.UpdateShipmentRequestDTO;
import trungdevcode.latra.modules.shipment.service.ShipmentService;
import trungdevcode.latra.repository.OrderRepository;
import trungdevcode.latra.repository.ShipmentRepository;

@Service
@RequiredArgsConstructor
public class ShipmentServiceImpl implements ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final OrderRepository orderRepository;

    @Override
    public Shipment getShipmentByOrderId(Long orderId) {
        return shipmentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new BusinessException(404, "Đơn hàng này không có thông tin vận chuyển!"));
    }

    // ==========================================
    // CASE 1: KHÁCH HÀNG / ADMIN YÊU CẦU ĐỔI ĐỊA CHỈ NHẬN
    // ==========================================
    @Transactional
    public Shipment updateShippingAddress(Long orderId, String newName, String newPhone, String newAddress) {
        Shipment shipment = getShipmentByOrderId(orderId);

        // 🔥 CHỐT CHẶN: Đã đưa cho Shipper thì không được đổi nữa
        if (shipment.getStatus() != ShipmentStatus.PREPARING) {
            throw new BusinessException("Không thể đổi địa chỉ! Đơn hàng đang ở trạng thái: " + shipment.getStatus().name() + ". Vui lòng liên hệ trực tiếp đơn vị vận chuyển.");
        }

        shipment.setRecipientName(newName.trim());
        shipment.setRecipientPhone(newPhone.trim());
        shipment.setFullAddress(newAddress.trim());

        return shipmentRepository.save(shipment);
    }

    // ==========================================
    // CASE 2 & 3: CẬP NHẬT VẬN ĐƠN (Thường do Admin hoặc Webhook GHN/GHTK gọi)
    // ==========================================
    @Override
    @Transactional
    public Shipment updateTrackingInfo(Long shipmentId, UpdateShipmentRequestDTO request) {
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new BusinessException(404, "Không tìm thấy phiên giao hàng!"));

        // Cập nhật Tracking Code
        if (request.getTrackingCode() != null && !request.getTrackingCode().isBlank()) {
            String newTracking = request.getTrackingCode().trim();
            if (!newTracking.equals(shipment.getTrackingCode()) && shipmentRepository.existsByTrackingCode(newTracking)) {
                throw new BusinessException("Mã vận đơn " + newTracking + " đã tồn tại trên một đơn hàng khác!");
            }
            shipment.setTrackingCode(newTracking);
        }

        // ĐÃ FIX LỖI CARRIER: ÉP ENUM VỀ LẠI STRING
        if (request.getCarrier() != null && !request.getCarrier().isBlank()) {
            try {
                // Kiểm tra xem mã gửi lên có đúng chuẩn Enum không
                ShippingCarrier carrierEnum = ShippingCarrier.valueOf(request.getCarrier().toUpperCase());
                // Gán giá trị String vào Entity
                shipment.setCarrier(carrierEnum.name());
            } catch (IllegalArgumentException e) {
                throw new BusinessException("Mã hãng vận chuyển không hợp lệ!");
            }
        } else {
            shipment.setCarrier(null);
        }

        // 🔥 AUTOMATION: ĐỒNG BỘ TRẠNG THÁI VỚI BẢNG ORDER
        if (request.getStatus() != null && shipment.getStatus() != request.getStatus()) {
            shipment.setStatus(request.getStatus());

            Order order = shipment.getOrder();
            boolean orderChanged = false;

            switch (request.getStatus()) {
                case DELIVERED:
                    order.setStatus(OrderStatus.COMPLETED);
                    orderChanged = true;
                    break;
                case RETURNED:
                    order.setStatus(OrderStatus.REFUNDED);
                    orderChanged = true;
                    // TODO: Gọi OrderService hoặc ImeiService để nhả IMEI từ SOLD -> RETURNING
                    break;
                case CANCELLED:
                    order.setStatus(OrderStatus.CANCELLED);
                    orderChanged = true;
                    // TODO: Gọi ImeiService chuyển máy sang trạng thái LOST/DEFECTIVE
                    break;
                default:
                    if (order.getStatus() != OrderStatus.SHIPPED) {
                        order.setStatus(OrderStatus.SHIPPED);
                        orderChanged = true;
                    }
                    break;
            }

            if (orderChanged) {
                orderRepository.save(order);
            }
        }

        return shipmentRepository.save(shipment);
    }
}