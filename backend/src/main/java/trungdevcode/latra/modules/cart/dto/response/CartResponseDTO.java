// backend/src/main/java/trungdevcode/latra/modules/cart/dto/response/CartResponseDTO.java
package trungdevcode.latra.modules.cart.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class CartResponseDTO {
    private Long cartId;
    private List<CartItemResponseDTO> items;
    private BigDecimal subTotal; // Tổng tiền tạm tính của cả giỏ hàng
}