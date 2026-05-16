// backend/src/main/java/trungdevcode/latra/modules/promotion/service/VoucherService.java
package trungdevcode.latra.modules.promotion.service;

import trungdevcode.latra.modules.promotion.dto.request.VoucherRequestDTO;
import trungdevcode.latra.entity.Voucher;
import trungdevcode.latra.modules.promotion.dto.response.VoucherAnalyticsDTO;
import trungdevcode.latra.modules.promotion.dto.response.VoucherStackingResponseDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface VoucherService {
    List<Voucher> getAllVouchers();
    Voucher createVoucher(VoucherRequestDTO request);
    Voucher updateVoucher(Long id, VoucherRequestDTO request);
    void deleteVoucher(Long id);
    Map<String, Object> checkVoucher(String code, BigDecimal orderTotal, Long userId);
    VoucherStackingResponseDTO calculateMultipleVouchers(List<String> codes, BigDecimal orderTotal, Long userId);
    List<VoucherAnalyticsDTO> getVoucherAnalytics();
    List<Voucher> getActiveVouchers();
}