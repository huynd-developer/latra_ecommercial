// backend/src/main/java/trungdevcode/latra/modules/promotion/controller/PublicPromotionController.java
package trungdevcode.latra.modules.promotion.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import trungdevcode.latra.common.response.ApiResponse;
import trungdevcode.latra.entity.Voucher;
import trungdevcode.latra.modules.promotion.service.VoucherService;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/promotions/public")
@RequiredArgsConstructor
public class PublicPromotionController {

    private final VoucherService voucherService;

    // Lấy danh sách mã đang chạy để Frontend làm Dropdown chọn
    @GetMapping("/vouchers/active")
    public ResponseEntity<ApiResponse<List<Voucher>>> getActiveVouchers() {
        return ResponseEntity.ok(ApiResponse.success(voucherService.getActiveVouchers(), "Thành công"));
    }

    // Check mã hợp lệ (Chuyển hàm Check từ Admin sang đây để Khách vãng lai check được)
    @GetMapping("/vouchers/check")
    public ResponseEntity<ApiResponse<?>> checkVoucher(
            @RequestParam("code") String code,
            @RequestParam("total") BigDecimal total,
            @RequestParam(value = "userId", required = false) Long userId) {
        return ResponseEntity.ok(ApiResponse.success(voucherService.checkVoucher(code, total, userId), "Mã hợp lệ"));
    }
}