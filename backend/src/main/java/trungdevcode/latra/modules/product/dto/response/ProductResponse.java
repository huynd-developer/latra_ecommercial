package trungdevcode.latra.modules.product.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductResponse {
    private Long id;
    private String name;
    private String description;

    private Long brandId;
    private Long categoryId;
    private Integer status;
    private String mainImageUrl;
    private List<VariantDTO> variants;


    @Data
    public static class VariantDTO {
        private Long variantId;
        private String color;
        private String storage;
        private String condition;
        private BigDecimal price;
        private Integer stock;
        private String sku;
    }
}
