package trungdevcode.latra.modules.promotion.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import trungdevcode.latra.common.response.ApiResponse;
import trungdevcode.latra.modules.promotion.dto.request.VoucherRequestDTO;
import trungdevcode.latra.entity.Voucher;
import trungdevcode.latra.modules.promotion.service.VoucherService;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/vouchers/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class VoucherController {

    private final VoucherService voucherService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Voucher>>> getAllVouchers() {
        return ResponseEntity.ok(ApiResponse.success(voucherService.getAllVouchers(), "Lấy danh sách Voucher thành công"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Voucher>> createVoucher(@Valid @RequestBody VoucherRequestDTO request) {
        return ResponseEntity.ok(ApiResponse.success(voucherService.createVoucher(request), "Tạo Voucher thành công"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Voucher>> updateVoucher(
            @PathVariable("id") Long id,
            @Valid @RequestBody VoucherRequestDTO request) {
        return ResponseEntity.ok(ApiResponse.success(voucherService.updateVoucher(id, request), "Cập nhật Voucher thành công"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteVoucher(@PathVariable("id") Long id) {
        voucherService.deleteVoucher(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Đã xóa mã Voucher thành công!"));
    }

}