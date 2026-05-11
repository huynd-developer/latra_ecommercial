package trungdevcode.latra.donHang.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import trungdevcode.latra.donHang.dto.DonHangDTO;
import trungdevcode.latra.Service.OrderService;

@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173") // 🔥 THÊM CORS CHO VUE.JS GỌI ĐƯỢC
public class DonHangController {

    private final OrderService orderService;

    // API 1: Lấy danh sách đơn hàng
    @GetMapping
    public ResponseEntity<Page<DonHangDTO.DanhSach>> getOrders(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "status", required = false) String status) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(orderService.getOrders(status, pageable));
    }

    // API 2: Xem chi tiết 1 đơn hàng
    @GetMapping("/{id}")
    public ResponseEntity<DonHangDTO.ChiTiet> getOrderDetails(
            @PathVariable("id") Long id) { // 🔥 FIX LỖI 400: Thêm "id" vào ngoặc
        return ResponseEntity.ok(orderService.getOrderDetails(id));
    }

    // API 3: Cập nhật trạng thái đơn (Duyệt / Giao / Hủy)
    @PutMapping("/{id}/status")
    public ResponseEntity<String> updateOrderStatus(
            @PathVariable("id") Long id, // 🔥 FIX LỖI 400: Thêm "id" vào ngoặc
            @RequestBody DonHangDTO.CapNhatTrangThai request) {

        orderService.updateOrderStatus(id, request);
        return ResponseEntity.ok("Cập nhật trạng thái đơn hàng thành công!");
    }
}