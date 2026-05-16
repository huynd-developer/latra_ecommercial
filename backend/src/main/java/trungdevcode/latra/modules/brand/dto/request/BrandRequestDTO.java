package trungdevcode.latra.modules.brand.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BrandRequestDTO {
    @NotBlank(message = "Tên thương hiệu không được để trống")
    @Size(max = 100, message = "Tên thương hiệu không được dài quá 100 ký tự") // THÊM DÒNG NÀY
    private String name;
    private String country;
    private Integer status = 1;
}