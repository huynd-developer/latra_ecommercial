// backend/src/main/java/trungdevcode/latra/modules/inventory/service/ImeiService.java
package trungdevcode.latra.modules.inventory.service;

import trungdevcode.latra.enums.ImeiStatus;
import trungdevcode.latra.modules.inventory.dto.response.ImeiResponseDTO;
import java.util.List;

public interface ImeiService {
    List<ImeiResponseDTO> findAllImeis();
    void addImeisToVariant(Long variantId, List<String> imeiCodes);
    void deleteImei(Long id);

    // Chuyển newStatus thành kiểu Enum để Spring tự ép kiểu, chặn lỗi nhập linh tinh
    void changeImeiStatus(Long id, ImeiStatus newStatus);
    void releaseImeisAfterRefund(Long orderId);
}