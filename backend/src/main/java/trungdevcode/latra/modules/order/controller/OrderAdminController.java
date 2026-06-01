package trungdevcode.latra.modules.order.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import trungdevcode.latra.common.response.ApiResponse;
import trungdevcode.latra.modules.order.dto.response.OrderResponse;
import trungdevcode.latra.modules.order.service.OrderService;

@RestController
@RequestMapping("/api/v1/orders/admin")
@RequiredArgsConstructor
// 🛠️ ĐÃ XÓA @PreAuthorize ở cấp Class để tránh vơ đũa cả nắm quyền hạn
public class OrderAdminController {

    private final OrderService orderService;

    @GetMapping
    // Thu ngân, Quản lý, Chủ shop đều có quyền xem danh sách đơn hàng công việc
    @PreAuthorize("hasAnyAuthority('CASHIER', 'MANAGER', 'OWNER')")
    public ResponseEntity<ApiResponse<Page<OrderResponse.DanhSach>>> getOrders(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "100") int size,
            @RequestParam(value = "status", required = false) String status) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(ApiResponse.success(orderService.getOrders(status, pageable), "Lấy danh sách thành công"));
    }

    @GetMapping("/{id}")
    // Thu ngân cần xem chi tiết đơn để hỗ trợ khách hoặc bàn giao vận chuyển
    @PreAuthorize("hasAnyAuthority('CASHIER', 'MANAGER', 'OWNER')")
    public ResponseEntity<ApiResponse<OrderResponse.OrderDetail>> getOrderDetails(@PathVariable("id") Long id) {
        return ResponseEntity.ok(ApiResponse.success(orderService.getOrderDetails(id), "Chi tiết đơn hàng"));
    }

    @PutMapping("/{id}/status")
    // 🔥 QUAN TRỌNG: Chỉ MANAGER và OWNER mới được phép can thiệp đổi trạng thái đơn hàng (Hủy, Hoàn tiền, duyệt...)
    @PreAuthorize("hasAnyAuthority('MANAGER', 'OWNER')")
    public ResponseEntity<ApiResponse<String>> updateOrderStatus(
            @PathVariable("id") Long id,
            @RequestBody OrderResponse.UpdateStatus request) {

        orderService.updateOrderStatus(id, request);
        return ResponseEntity.ok(ApiResponse.success(null, "Cập nhật trạng thái đơn hàng thành công!"));
    }
}