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
     * LƯU Ý ĐẶC BIỆT: Giữ nguyên KHÔNG thêm @PreAuthorize ở đây.
     * Vì API này do Server VNPAY gọi ngầm (họ không có JWT Token của chúng ta),
     * việc cấu hình mở public ở SecurityConfig là hoàn toàn chính xác theo nghiệp vụ thực tế.
     */
    @GetMapping("/vnpay-ipn")
    public Map<String, String> vnpayIpn(@RequestParam Map<String, String> params) {
        return paymentService.processVnPayIpn(params);
    }
}