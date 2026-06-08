package trungdevcode.latra.modules.vouchers.controller; // Lưu ý package giữ theo cấu hình của hệ thống

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import trungdevcode.latra.common.response.ApiResponse;
import trungdevcode.latra.modules.promotion.dto.request.VoucherRequestDTO;
import trungdevcode.latra.entity.Voucher;
import trungdevcode.latra.modules.promotion.service.VoucherService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/vouchers/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class VoucherController {

    private final VoucherService voucherService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('CASHIER', 'MANAGER', 'OWNER')") // Đã sửa tại đây
    public ResponseEntity<ApiResponse<List<Voucher>>> getAllVouchers() {
        return ResponseEntity.ok(ApiResponse.success(voucherService.getAllVouchers(), "Lấy danh sách Voucher thành công"));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('MANAGER', 'OWNER')") // Đã sửa tại đây
    public ResponseEntity<ApiResponse<Voucher>> createVoucher(@Valid @RequestBody VoucherRequestDTO request) {
        return ResponseEntity.ok(ApiResponse.success(voucherService.createVoucher(request), "Tạo Voucher thành công"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('MANAGER', 'OWNER')") // Đã sửa tại đây
    public ResponseEntity<ApiResponse<Voucher>> updateVoucher(
            @PathVariable("id") Long id,
            @Valid @RequestBody VoucherRequestDTO request) {
        return ResponseEntity.ok(ApiResponse.success(voucherService.updateVoucher(id, request), "Cập nhật Voucher thành công"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('OWNER')") // Đã sửa tại đây
    public ResponseEntity<ApiResponse<String>> deleteVoucher(@PathVariable("id") Long id) {
        voucherService.deleteVoucher(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Đã xóa mã Voucher thành công!"));
    }
    @GetMapping("/check")
    @PreAuthorize("hasAnyAuthority('CASHIER', 'MANAGER', 'OWNER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkVoucher(
            @RequestParam("code") String code,
            // 🛠️ Tối ưu: Dùng BigDecimal thay vì Double để tính tiền không bị sai số thập phân
            @RequestParam("total") BigDecimal totalAmount) {

        // 🔥 Gọi trực tiếp hàm checkVoucher siêu xịn đã có sẵn trong VoucherServiceImpl.
        // Truyền null cho userId vì khách vãng lai mua tại quầy POS thường chưa có tài khoản hệ thống.
        Map<String, Object> voucherResult = voucherService.checkVoucher(code, totalAmount, null);

        return ResponseEntity.ok(ApiResponse.success(voucherResult, "Áp dụng mã thành công"));
    }
}