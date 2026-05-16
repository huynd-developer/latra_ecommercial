// backend/src/main/java/trungdevcode/latra/modules/inventory/dto/request/ImeiImportRequestDTO.java
package trungdevcode.latra.modules.inventory.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class ImeiImportRequestDTO {
    @NotNull(message = "Thiếu ID của phiên bản sản phẩm")
    private Long variantId;

    @NotEmpty(message = "Danh sách mã IMEI không được để trống")
    private List<String> imeis;
}