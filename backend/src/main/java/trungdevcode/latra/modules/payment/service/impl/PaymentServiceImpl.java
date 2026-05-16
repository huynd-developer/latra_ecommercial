// backend/src/main/java/trungdevcode/latra/modules/payment/service/impl/PaymentServiceImpl.java
package trungdevcode.latra.modules.payment.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import trungdevcode.latra.common.config.VnpayConfig;
import trungdevcode.latra.common.exception.BusinessException;
import trungdevcode.latra.common.exception.ErrorCode;
import trungdevcode.latra.entity.Imei;
import trungdevcode.latra.entity.Order;
import trungdevcode.latra.entity.PaymentTransaction;
import trungdevcode.latra.enums.ImeiStatus;
import trungdevcode.latra.enums.OrderStatus;
import trungdevcode.latra.enums.PaymentMethod;
import trungdevcode.latra.enums.PaymentStatus;
import trungdevcode.latra.modules.inventory.service.ImeiService;
import trungdevcode.latra.modules.payment.service.PaymentService;
import trungdevcode.latra.repository.ImeiRepository;
import trungdevcode.latra.repository.OrderRepository;
import trungdevcode.latra.repository.PaymentTransactionRepository;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j // Dùng để log các giao dịch tài chính
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final VnpayConfig vnpayConfig;
    private final OrderRepository orderRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final ImeiRepository imeiRepository;
    private final ImeiService imeiService;

    @Override
    @Transactional
    public String createVnPayPaymentUrl(Order order, HttpServletRequest request) {
        String txnRef = vnpayConfig.getRandomNumber(8);

        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setOrder(order);
        transaction.setProvider(PaymentMethod.VNPAY);
        transaction.setAmount(order.getTotalAmount());
        transaction.setGatewayTransactionNo(txnRef);
        transaction.setStatus(PaymentStatus.PENDING);
        paymentTransactionRepository.save(transaction);

        long amount = order.getTotalAmount().longValue() * 100L;

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnpayConfig.getVersion());
        vnp_Params.put("vnp_Command", "pay");
        vnp_Params.put("vnp_TmnCode", vnpayConfig.getTmnCode());
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", txnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang Latra: " + order.getId());
        vnp_Params.put("vnp_OrderType", "other");
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", vnpayConfig.getReturnUrl());
        vnp_Params.put("vnp_IpAddr", vnpayConfig.getIpAddress(request));

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        vnp_Params.put("vnp_CreateDate", formatter.format(cld.getTime()));

        cld.add(Calendar.MINUTE, 15);
        vnp_Params.put("vnp_ExpireDate", formatter.format(cld.getTime()));

        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        try {
            Iterator<String> itr = fieldNames.iterator();
            while (itr.hasNext()) {
                String fieldName = itr.next();
                String fieldValue = vnp_Params.get(fieldName);
                if ((fieldValue != null) && (!fieldValue.isEmpty())) {
                    hashData.append(fieldName).append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString())).append('=')
                            .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    if (itr.hasNext()) {
                        query.append('&');
                        hashData.append('&');
                    }
                }
            }
        } catch (Exception e) {
            log.error("Lỗi mã hóa tham số VNPAY: ", e);
            throw new BusinessException(ErrorCode.INVALID_DATA, "Lỗi hệ thống khi khởi tạo thanh toán");
        }

        String queryUrl = query.toString();
        String vnp_SecureHash = vnpayConfig.hmacSHA512(vnpayConfig.getSecretKey(), hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;

        String paymentUrl = vnpayConfig.getPayUrl() + "?" + queryUrl;

        transaction.setPaymentUrl(paymentUrl);
        paymentTransactionRepository.save(transaction);

        log.info("Khởi tạo VNPAY URL thành công cho OrderID: {}", order.getId());
        return paymentUrl;
    }

    @Override
    @Transactional
    public Map<String, String> processVnPayIpn(Map<String, String> params) {
        Map<String, String> response = new HashMap<>();
        try {
            String vnp_SecureHash = params.get("vnp_SecureHash");
            params.remove("vnp_SecureHash");
            params.remove("vnp_SecureHashType");

            List<String> fieldNames = new ArrayList<>(params.keySet());
            Collections.sort(fieldNames);
            StringBuilder hashData = new StringBuilder();

            Iterator<String> itr = fieldNames.iterator();
            while (itr.hasNext()) {
                String fieldName = itr.next();
                String fieldValue = params.get(fieldName);
                if ((fieldValue != null) && (!fieldValue.isEmpty())) {
                    hashData.append(fieldName).append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    if (itr.hasNext()) hashData.append('&');
                }
            }

            String signValue = vnpayConfig.hmacSHA512(vnpayConfig.getSecretKey(), hashData.toString());

            if (!signValue.equals(vnp_SecureHash)) {
                log.warn("VNPAY IPN Checksum thất bại! Dữ liệu nghi ngờ bị giả mạo.");
                response.put("RspCode", "97");
                response.put("Message", "Invalid Checksum");
                return response;
            }

            String txnRef = params.get("vnp_TxnRef");
            String responseCode = params.get("vnp_ResponseCode");
            String vnpAmountStr = params.get("vnp_Amount");

            PaymentTransaction transaction = paymentTransactionRepository.findByGatewayTransactionNo(txnRef)
                    .orElse(null);

            if (transaction == null) {
                response.put("RspCode", "01");
                response.put("Message", "Order not found");
                return response;
            }

            // 🔥 BẢO MẬT ENTERPRISE: Kiểm tra số tiền VNPAY trả về có khớp với DB không
            long vnpAmount = Long.parseLong(vnpAmountStr);
            long dbAmount = transaction.getAmount().longValue() * 100L;
            if (vnpAmount != dbAmount) {
                log.error("VNPAY IPN Sai lệch số tiền! Yêu cầu: {}, VNPAY báo: {}", dbAmount, vnpAmount);
                response.put("RspCode", "04");
                response.put("Message", "Invalid Amount");
                return response;
            }

            if (transaction.getStatus() != PaymentStatus.PENDING) {
                response.put("RspCode", "02");
                response.put("Message", "Order already confirmed");
                return response;
            }

            Order order = transaction.getOrder();

            if ("00".equals(responseCode)) {
                log.info("VNPAY IPN Thành công cho TxnRef: {}", txnRef);
                transaction.setStatus(PaymentStatus.SUCCESS);
                transaction.setPayDate(LocalDateTime.now());
                order.setStatus(OrderStatus.PAID);

                List<Imei> lockedImeis = imeiRepository.findByOrder(order);
                for (Imei imei : lockedImeis) {
                    imei.setStatus(ImeiStatus.SOLD);
                }
                imeiRepository.saveAll(lockedImeis);
            } else {
                log.info("VNPAY IPN Giao dịch thất bại / Bị hủy cho TxnRef: {}", txnRef);
                transaction.setStatus(PaymentStatus.FAILED);
                order.setStatus(OrderStatus.CANCELLED);

                // Giải phóng tồn kho và IMEI
                imeiService.releaseImeisAfterRefund(order.getId());
            }

            paymentTransactionRepository.save(transaction);
            orderRepository.save(order);

            response.put("RspCode", "00");
            response.put("Message", "Confirm Success");

        } catch (Exception e) {
            log.error("Lỗi khi xử lý VNPAY IPN: ", e);
            response.put("RspCode", "99");
            response.put("Message", "Unknown error");
        }
        return response;
    }

    @Override
    @Transactional
    public void refundOrder(Long orderId) {
        PaymentTransaction successTxn = paymentTransactionRepository
                .findByOrderIdAndStatus(orderId, PaymentStatus.SUCCESS)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Không tìm thấy giao dịch đã thanh toán thành công"));

        boolean isVnpayRefundSuccess = executeVnpayRefund(successTxn);

        if (isVnpayRefundSuccess) {
            successTxn.setStatus(PaymentStatus.REFUNDED);
            successTxn.getOrder().setStatus(OrderStatus.REFUNDED);
            paymentTransactionRepository.save(successTxn);

            // Mở khóa IMEI và hoàn tồn kho
            imeiService.releaseImeisAfterRefund(orderId);
            log.info("Đã hoàn tiền và nhả kho thành công cho OrderID: {}", orderId);
        } else {
            throw new BusinessException(ErrorCode.INVALID_DATA, "Lệnh hoàn tiền bị VNPAY từ chối");
        }
    }

    // =================================================================================
    // 🔥 BẢO MẬT ENTERPRISE: LUỒNG GỌI API SERVER-TO-SERVER HOÀN TIỀN VNPAY
    // =================================================================================
    private boolean executeVnpayRefund(PaymentTransaction txn) {
        try {
            // URL gọi API hoàn tiền của VNPAY Sandbox
            String vnp_ApiUrl = "https://sandbox.vnpayment.vn/merchant_webapi/api/transaction";

            String vnp_RequestId = UUID.randomUUID().toString();
            String vnp_Version = "2.1.0";
            String vnp_Command = "refund";
            String vnp_TmnCode = vnpayConfig.getTmnCode();
            String vnp_TransactionType = "02"; // 02: Hoàn tiền toàn phần
            String vnp_TxnRef = txn.getGatewayTransactionNo();
            long amount = txn.getAmount().longValue() * 100L;
            String vnp_Amount = String.valueOf(amount);
            String vnp_OrderInfo = "Hoan tien don hang " + txn.getOrder().getId();
            String vnp_TransactionNo = ""; // Có thể để trống nếu dùng TxnRef

            // Format ngày giao dịch cũ và ngày tạo lệnh
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            String vnp_TransactionDate = txn.getPayDate() != null ? txn.getPayDate().format(formatter) : LocalDateTime.now().format(formatter);
            String vnp_CreateDate = LocalDateTime.now().format(formatter);
            String vnp_CreateBy = "admin";
            String vnp_IpAddr = "127.0.0.1"; // Hoặc lấy IP server thực tế

            // Chuỗi dữ liệu băm (Phải tuyệt đối chính xác theo tài liệu VNPAY)
            String hashData = String.join("|",
                    vnp_RequestId, vnp_Version, vnp_Command, vnp_TmnCode, vnp_TransactionType,
                    vnp_TxnRef, vnp_Amount, vnp_TransactionNo, vnp_TransactionDate,
                    vnp_CreateBy, vnp_CreateDate, vnp_IpAddr, vnp_OrderInfo
            );

            String vnp_SecureHash = vnpayConfig.hmacSHA512(vnpayConfig.getSecretKey(), hashData);

            // Xây dựng JSON Body
            Map<String, String> requestData = new HashMap<>();
            requestData.put("vnp_RequestId", vnp_RequestId);
            requestData.put("vnp_Version", vnp_Version);
            requestData.put("vnp_Command", vnp_Command);
            requestData.put("vnp_TmnCode", vnp_TmnCode);
            requestData.put("vnp_TransactionType", vnp_TransactionType);
            requestData.put("vnp_TxnRef", vnp_TxnRef);
            requestData.put("vnp_Amount", vnp_Amount);
            requestData.put("vnp_TransactionNo", vnp_TransactionNo);
            requestData.put("vnp_TransactionDate", vnp_TransactionDate);
            requestData.put("vnp_CreateBy", vnp_CreateBy);
            requestData.put("vnp_CreateDate", vnp_CreateDate);
            requestData.put("vnp_IpAddr", vnp_IpAddr);
            requestData.put("vnp_OrderInfo", vnp_OrderInfo);
            requestData.put("vnp_SecureHash", vnp_SecureHash);

            // Gọi RestTemplate
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestData, headers);

            log.info("Bắt đầu gửi lệnh Refund sang VNPAY cho TxnRef: {}", vnp_TxnRef);
            ResponseEntity<Map> response = restTemplate.postForEntity(vnp_ApiUrl, entity, Map.class);
            Map<?, ?> responseBody = response.getBody();

            if (responseBody != null && "00".equals(responseBody.get("vnp_ResponseCode"))) {
                log.info("VNPAY chấp nhận lệnh Refund cho TxnRef: {}", vnp_TxnRef);
                return true;
            } else {
                log.error("VNPAY từ chối lệnh Refund. Response: {}", responseBody);
                return false;
            }
        } catch (Exception e) {
            log.error("Lỗi khi gọi API Refund của VNPAY: ", e);
            return false;
        }
    }
}