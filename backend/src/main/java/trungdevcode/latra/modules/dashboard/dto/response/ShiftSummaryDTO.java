package trungdevcode.latra.modules.dashboard.dto.response;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ShiftSummaryDTO {
    private String staffName;
    private String startTime;
    private int totalOrders;
    private BigDecimal totalRevenue;
}