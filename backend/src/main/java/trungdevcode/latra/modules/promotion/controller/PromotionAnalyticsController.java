package trungdevcode.latra.modules.promotion.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import trungdevcode.latra.common.response.ApiResponse;
import trungdevcode.latra.modules.promotion.service.VoucherService;

@RestController
@RequestMapping("/api/v1/promotions/analytics/admin")
@RequiredArgsConstructor
public class PromotionAnalyticsController {

    private final VoucherService voucherService;

    @GetMapping("/vouchers")
    @PreAuthorize("hasAnyAuthority('MANAGER', 'OWNER')")
    public ResponseEntity<ApiResponse<?>> getVoucherAnalytics() {
        return ResponseEntity.ok(ApiResponse.success(voucherService.getVoucherAnalytics(), "Lấy thống kê thành công"));
    }
}