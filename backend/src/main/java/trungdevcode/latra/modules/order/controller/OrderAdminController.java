// backend/src/main/java/trungdevcode/latra/modules/order/controller/OrderAdminController.java
package trungdevcode.latra.modules.order.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import trungdevcode.latra.common.response.ApiResponse;
import trungdevcode.latra.modules.order.dto.response.OrderResponse;
import trungdevcode.latra.modules.order.service.OrderService;

@RestController
@RequestMapping("/api/v1/orders/admin") // 🔥 SỬA 1: Đổi thành /api/v1/orders/admin cho khớp với Frontend
@RequiredArgsConstructor
public class OrderAdminController {

    private final OrderService orderService;

    // API 1: Lấy danh sách đơn hàng
    @GetMapping
    public ResponseEntity<ApiResponse<Page<OrderResponse.DanhSach>>> getOrders(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "100") int size, // Nới rộng size để hiển thị trên bảng
            @RequestParam(value = "status", required = false) String status) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        // 🔥 SỬA 2: Bọc bằng ApiResponse.success()
        return ResponseEntity.ok(ApiResponse.success(orderService.getOrders(status, pageable), "Lấy danh sách thành công"));
    }

    // API 2: Xem chi tiết 1 đơn hàng
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse.OrderDetail>> getOrderDetails(
            @PathVariable("id") Long id) {

        // 🔥 SỬA 2: Bọc bằng ApiResponse.success()
        return ResponseEntity.ok(ApiResponse.success(orderService.getOrderDetails(id), "Chi tiết đơn hàng"));
    }

    // API 3: Cập nhật trạng thái đơn (Duyệt / Giao / Hủy)
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<String>> updateOrderStatus(
            @PathVariable("id") Long id,
            @RequestBody OrderResponse.UpdateStatus request) {

        orderService.updateOrderStatus(id, request);

        // 🔥 SỬA 2: Bọc bằng ApiResponse.success()
        return ResponseEntity.ok(ApiResponse.success(null, "Cập nhật trạng thái đơn hàng thành công!"));
    }
}