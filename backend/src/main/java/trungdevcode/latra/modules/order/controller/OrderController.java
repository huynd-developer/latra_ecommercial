package trungdevcode.latra.modules.order.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
@CrossOrigin(origins = "http://localhost:5173")
public class OrderController {

    private final OrderService orderService;
    private final PaymentService paymentService;

    // ==========================================
    // API MỚI CHO LUỒNG BÁN HÀNG POS (QUÉT SÚNG)
    // ==========================================
    @PostMapping("/checkout-pos")
    public ResponseEntity<?> checkoutPOS(@RequestBody CheckoutRequestDTO request) {
        // 🔥 Bỏ try-catch vì GlobalExceptionHandler sẽ tự hứng lỗi 400
        String result = orderService.checkoutPOS(request);
        return ResponseEntity.ok(result);
    }
    @PostMapping("/checkout")
    public ResponseEntity<ApiResponse<Map<String, Object>>> createWebOrder(
            @RequestBody WebCheckoutRequestDTO request,
            HttpServletRequest httpRequest) { // Lấy Request để check IP

        // 1. Tạo đơn hàng
        Order order = orderService.createWebOrder(request);

        Map<String, Object> result = new HashMap<>();
        result.put("orderId", order.getId());
        result.put("totalAmount", order.getTotalAmount());

        // 2. KHỞI ĐỘNG LUỒNG PAYMENT
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

    // ==========================================
    // CÁC API CŨ TẠM THỜI ĐÓNG BĂNG ĐỂ TRÁNH LỖI ĐỎ
    // ==========================================
    /*
    @PostMapping("/checkout/{userId}")
    public ResponseEntity<?> checkout(@PathVariable("userId") Long userId) { // 🔥 Fix sẵn lỗi 400
        Order order = orderService.checkout(userId);
        return ResponseEntity.ok("Đặt hàng thành công! Mã đơn hàng: " + order.getId());
    }

    @GetMapping
    public ResponseEntity<?> getAllOrders(
            // 🔥 Fix sẵn lỗi 400
            @RequestParam(value = "status", required = false) String status,
            org.springframework.data.domain.Pageable pageable) {
        return ResponseEntity.ok(orderService.getOrders(status, pageable));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderDetails(@PathVariable("orderId") Long orderId) { // 🔥 Fix sẵn lỗi 400
        return ResponseEntity.ok(orderService.getOrderDetails(orderId));
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable("orderId") Long orderId, // 🔥 Fix sẵn lỗi 400
            @RequestBody trungdevcode.latra.donHang.dto.DonHangDTO.UpdateStatus request) {
        orderService.updateOrderStatus(orderId, request);
        return ResponseEntity.ok("Cập nhật trạng thái thành công!");
    }
    */
}