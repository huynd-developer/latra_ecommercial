// backend/src/main/java/trungdevcode/latra/modules/promotion/dto/response/VoucherStackingResponseDTO.java
package trungdevcode.latra.modules.promotion.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
public class VoucherStackingResponseDTO {
    private BigDecimal originalTotal;     // Tiền gốc
    private BigDecimal finalTotal;        // Tiền phải trả sau cùng
    private BigDecimal totalDiscount;     // Tổng tiền được giảm
    // Chi tiết từng mã giảm bao nhiêu (VD: {"TET2026": 50000, "FREESHIP": 30000})
    private Map<String, BigDecimal> discountDetails;
}