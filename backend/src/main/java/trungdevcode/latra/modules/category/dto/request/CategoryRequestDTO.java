package trungdevcode.latra.modules.category.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryRequestDTO {
    @NotBlank(message = "Tên danh mục không được để trống")
    private String name;
    private Long parentId; // Có thể null nếu là danh mục gốc
    private Integer status = 1;
}