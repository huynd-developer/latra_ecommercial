package trungdevcode.latra.modules.order.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import trungdevcode.latra.common.response.ApiResponse;
import trungdevcode.latra.entity.Order;
import trungdevcode.latra.enums.PaymentMethod;
import trungdevcode.latra.modules.order.dto.request.CheckoutRequestDTO;
import trungdevcode.latra.modules.order.dto.request.WebCheckoutRequestDTO;
import trungdevcode.latra.modules.order.service.OrderService;
import trungdevcode.latra.modules.payment.service.PaymentService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
// 🛠️ ĐÃ XÓA @CrossOrigin hardcode ở đây để sau này quản lý tập trung tại SecurityConfig hoặc WebMvcConfig
public class OrderController {

    private final OrderService orderService;
    private final PaymentService paymentService;

    @PostMapping("/checkout-pos")
    @PreAuthorize("hasAnyAuthority('CASHIER', 'MANAGER', 'OWNER')")
    // 🛠️ ĐÃ SỬA: Kiểu trả về đồng bộ dữ liệu ApiResponse<String> giúp FE dễ bắt kết quả
    public ResponseEntity<ApiResponse<String>> checkoutPOS(@RequestBody CheckoutRequestDTO request) {
        String result = orderService.checkoutPOS(request);
        return ResponseEntity.ok(ApiResponse.success(result, "Tạo hóa đơn tại quầy thành công!"));
    }

    @PostMapping("/checkout")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> createWebOrder(
            @RequestBody WebCheckoutRequestDTO request,
            HttpServletRequest httpRequest) {

        Order order = orderService.createWebOrder(request);

        Map<String, Object> result = new HashMap<>();
        result.put("orderId", order.getId());
        result.put("totalAmount", order.getTotalAmount());

        if (order.getPaymentMethod() == PaymentMethod.VNPAY) {
            String paymentUrl = paymentService.createVnPayPaymentUrl(order, httpRequest);
            result.put("paymentUrl", paymentUrl);
            result.put("redirect", true);
        } else {
            result.put("paymentUrl", null);
            result.put("redirect", false);
        }

        return ResponseEntity.ok(ApiResponse.success(result, "Tạo đơn hàng thành công!"));
    }
}