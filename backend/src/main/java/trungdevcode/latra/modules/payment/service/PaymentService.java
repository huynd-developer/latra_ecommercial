// backend/src/main/java/trungdevcode/latra/modules/payment/service/PaymentService.java
package trungdevcode.latra.modules.payment.service;

import jakarta.servlet.http.HttpServletRequest;
import trungdevcode.latra.entity.Order;
import java.util.Map;

public interface PaymentService {
    // 1. Sinh Link thanh toán VNPAY
    String createVnPayPaymentUrl(Order order, HttpServletRequest request);

    // 2. Xử lý phản hồi ngầm từ VNPAY (IPN Webhook)
    Map<String, String> processVnPayIpn(Map<String, String> params);
    void refundOrder(Long orderId);
}