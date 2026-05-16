// backend/src/main/java/trungdevcode/latra/modules/cart/dto/response/CartItemResponseDTO.java
package trungdevcode.latra.modules.cart.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponseDTO {
    private Long id; // cartItemId
    private Long variantId;
    private String productName;
    private String variantName; // VD: Đen - 256GB
    private String imageUrl;

    private Integer quantity;
    private BigDecimal originalPrice;
    private BigDecimal salePrice; // Sẽ có giá trị nếu đang Flash Sale
    private BigDecimal itemTotal; // = quantity * (salePrice or originalPrice)

    // 🔥 CHỐT CHẶN CUỐI CÙNG: Dùng 'Boolean' (Object) thay vì 'boolean' (Nguyên thủy)
    // Lombok sẽ tạo hàm getIsAvailable(), giúp Jackson giữ nguyên được chữ "is"
    @JsonProperty("isAvailable")
    private Boolean isAvailable;

    private String warningMessage; // Chứa câu thông báo lỗi nếu có
}