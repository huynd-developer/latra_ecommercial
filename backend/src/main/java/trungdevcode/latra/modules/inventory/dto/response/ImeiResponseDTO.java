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

    private String productName;
    private String variantName;
    private BigDecimal price;

    // 🔥 THÊM DÒNG NÀY: Để Frontend có link ảnh mà hiển thị
    private String imageUrl;
}