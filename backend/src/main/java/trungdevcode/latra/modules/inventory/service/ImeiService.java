package trungdevcode.latra.modules.inventory.service;

import trungdevcode.latra.enums.ImeiStatus;
import trungdevcode.latra.modules.inventory.dto.response.ImeiResponseDTO;
import java.util.List;

public interface ImeiService {
    List<ImeiResponseDTO> findAllImeis();
    void addImeisToVariant(Long variantId, List<String> imeiCodes);
    void deleteImei(Long id);
    void changeImeiStatus(Long id, ImeiStatus newStatus);
    void releaseImeisAfterRefund(Long orderId);
    List<ImeiResponseDTO> getAvailableImeisByProductId(Long productId);

    // 🔥 THÊM HÀM NÀY: Phục vụ riêng cho súng quét Barcode/IMEI tại quầy POS
    ImeiResponseDTO getAvailableImeiByCode(String imeiCode);
}