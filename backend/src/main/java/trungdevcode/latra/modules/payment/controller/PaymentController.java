// backend/src/main/java/trungdevcode/latra/modules/payment/controller/PaymentController.java
package trungdevcode.latra.modules.payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import trungdevcode.latra.modules.payment.service.PaymentService;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * VNPAY IPN (Instant Payment Notification)
     * API này VNPAY tự động gọi ngầm tới Server của bạn, Front-end KHÔNG GỌI API NÀY.
     * VNPAY gửi dữ liệu dưới dạng GET request với Query Params.
     */
    @GetMapping("/vnpay-ipn")
    public Map<String, String> vnpayIpn(@RequestParam Map<String, String> params) {
        // Trả về JSON theo đúng chuẩn mà VNPAY yêu cầu
        return paymentService.processVnPayIpn(params);
    }
}