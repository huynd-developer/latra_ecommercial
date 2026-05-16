package trungdevcode.latra.modules.cart.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MergeCartRequestDTO {
    @NotEmpty(message = "Danh sách giỏ hàng gộp không được để trống")
    @Valid
    private List<CartItemRequestDTO> items;
}