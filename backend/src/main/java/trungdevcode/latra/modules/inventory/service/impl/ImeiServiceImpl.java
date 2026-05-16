// backend/src/main/java/trungdevcode/latra/modules/inventory/service/impl/ImeiServiceImpl.java
package trungdevcode.latra.modules.inventory.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import trungdevcode.latra.entity.Imei;
import trungdevcode.latra.entity.ProductVariant;
import trungdevcode.latra.enums.ImeiStatus;
import trungdevcode.latra.modules.inventory.dto.response.ImeiResponseDTO;
import trungdevcode.latra.modules.inventory.service.ImeiService;
import trungdevcode.latra.repository.ImeiRepository;
import trungdevcode.latra.repository.ProductVariantRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImeiServiceImpl implements ImeiService {

    private final ImeiRepository imeiRepository;
    private final ProductVariantRepository variantRepository;

    @Override
    public List<ImeiResponseDTO> findAllImeis() {
        return imeiRepository.findAll().stream().map(imei -> {
            ProductVariant variant = imei.getVariant();
            String prodName = (variant != null && variant.getProduct() != null) ? variant.getProduct().getName() : "N/A";
            String varName = (variant != null) ? (variant.getColor() + " - " + variant.getStorage()) : "N/A";

            return ImeiResponseDTO.builder()
                    .id(imei.getId())
                    .imeiCode(imei.getImeiCode())
                    .status(imei.getStatus().name()) // Trả về String cho Front-end dễ hiển thị
                    .createdAt(imei.getCreatedAt())
                    .productName(prodName)
                    .variantName(varName)
                    .price(variant != null ? variant.getPrice() : null)
                    .build();
        }).collect(Collectors.toList());
    }

    // BÀI TOÁN 1 (VALIDATION): Chống rác, chống trùng IMEI khi nhập kho
    @Override
    @Transactional
    public void addImeisToVariant(Long variantId, List<String> imeiCodes) {
        ProductVariant variant = variantRepository.findById(variantId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiên bản sản phẩm!"));

        List<String> cleanedCodes = imeiCodes.stream()
                .filter(code -> code != null && !code.trim().isEmpty())
                .map(code -> code.trim().toUpperCase())
                .collect(Collectors.toList());

        if (cleanedCodes.isEmpty()) throw new RuntimeException("Danh sách IMEI hợp lệ trống!");

        Set<String> uniqueCheckSet = new HashSet<>();
        List<Imei> newImeis = new ArrayList<>();

        for (String code : cleanedCodes) {
            if (!uniqueCheckSet.add(code)) {
                throw new RuntimeException("Phát hiện mã IMEI '" + code + "' bị tít trùng lặp!");
            }
            if (imeiRepository.existsByImeiCode(code)) {
                throw new RuntimeException("Mã IMEI '" + code + "' đã tồn tại trong hệ thống!");
            }

            Imei imei = new Imei();
            imei.setImeiCode(code);
            imei.setVariant(variant);
            imei.setStatus(ImeiStatus.AVAILABLE); // Dùng Enum chuẩn
            newImeis.add(imei);
        }

        imeiRepository.saveAll(newImeis);
        updateVariantStock(variant);
    }

    // BÀI TOÁN 3 (DATA INTEGRITY): Chống xóa mã đã bán
    @Override
    @Transactional
    public void deleteImei(Long id) {
        Imei imei = imeiRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mã IMEI không tồn tại!"));

        // 🔥 SO SÁNH ENUM: Tuyệt đối an toàn, không sợ sai chính tả
        if (imei.getStatus() != ImeiStatus.AVAILABLE) {
            throw new RuntimeException("Không thể xóa. Máy này đang ở trạng thái: " + imei.getStatus().name());
        }

        ProductVariant variant = imei.getVariant();
        imeiRepository.delete(imei);

        imeiRepository.flush();
        updateVariantStock(variant);
    }

    // BÀI TOÁN 4 (ASSET LOSS/DEFECT): Đổi trạng thái khi hàng lỗi/mất cắp
    @Override
    @Transactional
    public void changeImeiStatus(Long id, ImeiStatus newStatus) {
        Imei imei = imeiRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mã IMEI không tồn tại!"));

        // LUẬT DOANH NGHIỆP: Không cho phép đổi trạng thái máy ĐÃ BÁN (SOLD) bừa bãi
        // trừ khi khách đem trả (RETURNING) hoặc bảo hành (WARRANTY).
        if (imei.getStatus() == ImeiStatus.SOLD &&
                (newStatus != ImeiStatus.RETURNING && newStatus != ImeiStatus.WARRANTY)) {
            throw new RuntimeException("Máy đã bán chỉ có thể chuyển sang trạng thái ĐỔI TRẢ hoặc BẢO HÀNH!");
        }

        // LUẬT DOANH NGHIỆP: Không cho phép chuyển máy LỖI (DEFECTIVE) về lại CÓ SẴN (AVAILABLE)
        // để tránh vô tình bán máy lỗi cho khách khác.
        if (imei.getStatus() == ImeiStatus.DEFECTIVE && newStatus == ImeiStatus.AVAILABLE) {
            throw new RuntimeException("Hàng đã đánh dấu LỖI không được phép đưa lại lên kệ bán!");
        }

        imei.setStatus(newStatus);
        imeiRepository.save(imei);

        // Đếm lại tồn kho (Nếu đổi từ AVAILABLE sang DEFECTIVE, kho tự động trừ đi 1)
        updateVariantStock(imei.getVariant());
    }

    // BÀI TOÁN 2 (DYNAMIC STOCKING): Tồn kho động
    private void updateVariantStock(ProductVariant variant) {
        // Chỉ đếm những máy đang có trạng thái Enum là AVAILABLE
        long availableCount = imeiRepository.countByVariantAndStatus(variant, ImeiStatus.AVAILABLE);
        variant.setStock((int) availableCount);
        variantRepository.save(variant);
    }
    @Override
    @Transactional
    public void releaseImeisAfterRefund(Long orderId) {
        // Tìm các IMEI thuộc đơn hàng này
        List<Imei> imeis = imeiRepository.findByOrderId(orderId);
        for (Imei imei : imeis) {
            imei.setStatus(ImeiStatus.AVAILABLE);
            imei.setOrder(null);
        }
        imeiRepository.saveAll(imeis);

        // Cập nhật lại tồn kho cho các Variant liên quan
        Set<ProductVariant> variants = imeis.stream()
                .map(Imei::getVariant)
                .collect(Collectors.toSet());
        for (ProductVariant v : variants) {
            updateVariantStock(v);
        }
    }
}