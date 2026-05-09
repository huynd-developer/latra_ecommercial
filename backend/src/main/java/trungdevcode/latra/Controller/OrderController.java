package trungdevcode.latra.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import trungdevcode.latra.Dto.CheckoutRequestDTO;
import trungdevcode.latra.Service.OrderService;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class OrderController {

    private final OrderService orderService;

    // ==========================================
    // API MỚI CHO LUỒNG BÁN HÀNG POS (QUÉT SÚNG)
    // ==========================================
    @PostMapping("/checkout-pos")
    public ResponseEntity<?> checkoutPOS(@RequestBody CheckoutRequestDTO request) {
        // 🔥 Bỏ try-catch vì GlobalExceptionHandler sẽ tự hứng lỗi 400
        String result = orderService.checkoutPOS(request);
        return ResponseEntity.ok(result);
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
            @RequestBody trungdevcode.latra.donHang.dto.DonHangDTO.CapNhatTrangThai request) {
        orderService.updateOrderStatus(orderId, request);
        return ResponseEntity.ok("Cập nhật trạng thái thành công!");
    }
    */
}