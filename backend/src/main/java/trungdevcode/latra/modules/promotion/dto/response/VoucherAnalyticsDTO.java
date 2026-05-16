// backend/src/main/java/trungdevcode/latra/modules/promotion/dto/response/VoucherAnalyticsDTO.java
package trungdevcode.latra.modules.promotion.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class VoucherAnalyticsDTO {
    private String voucherCode;
    private Long totalUsage;               // Tổng số lần được áp dụng thành công
    private BigDecimal totalDiscountGiven; // Công ty đã tốn bao nhiêu tiền cho mã này
    private BigDecimal totalRevenue;       // Mã này đã kéo về bao nhiêu doanh thu
}