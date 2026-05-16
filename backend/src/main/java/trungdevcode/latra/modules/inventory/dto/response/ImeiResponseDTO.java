// backend/src/main/java/trungdevcode/latra/modules/inventory/dto/response/ImeiResponseDTO.java
package trungdevcode.latra.modules.inventory.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class ImeiResponseDTO {
    private Long id;
    private String imeiCode;
    private String status;
    private LocalDateTime createdAt;

    // Thông tin ăn theo để hiển thị trên bảng
    private String productName;
    private String variantName; // VD: Trắng - 256GB
    private BigDecimal price;
}