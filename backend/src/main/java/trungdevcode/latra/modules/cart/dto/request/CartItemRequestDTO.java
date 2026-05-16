// backend/src/main/java/trungdevcode/latra/modules/cart/dto/request/CartItemRequestDTO.java
package trungdevcode.latra.modules.cart.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemRequestDTO {
    @NotNull(message = "Vui lòng truyền mã phiên bản (variantId)")
    private Long variantId;

    @NotNull(message = "Vui lòng nhập số lượng")
    @Min(value = 1, message = "Số lượng thêm vào phải lớn hơn 0")
    @Max(value = 10, message = "Không được mua quá 10 sản phẩm cùng lúc")
    private Integer quantity;
}