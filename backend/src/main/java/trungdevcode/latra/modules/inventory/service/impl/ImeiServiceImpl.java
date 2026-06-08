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

import java.math.BigDecimal;
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
        // 🛠️ Đã tối ưu: Dùng hàm convertToDTO dùng chung cho sạch code
        return imeiRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public List<ImeiResponseDTO> getAvailableImeisByProductId(Long productId) {
        List<Imei> imeis = imeiRepository.findByVariantProductIdAndStatus(productId, ImeiStatus.AVAILABLE);
        if (imeis == null || imeis.isEmpty()) {
            return new ArrayList<>();
        }
        // 🛠️ Đã tối ưu: Dùng hàm convertToDTO dùng chung
        return imeis.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // =========================================================================
    // 🔥 RUỘT CỦA HÀM QUÉT MÃ IMEI (CHO POS)
    // =========================================================================
    @Override
    @Transactional(readOnly = true) // Thêm cái này để đọc data nhanh hơn
    public ImeiResponseDTO getAvailableImeiByCode(String imeiCode) {
        Imei imei = imeiRepository.findByImeiCodeAndStatus(imeiCode.trim().toUpperCase(), ImeiStatus.AVAILABLE)
                .orElseThrow(() -> new RuntimeException("Mã IMEI '" + imeiCode + "' không tồn tại hoặc đã bị bán!"));

        // 🛠️ THÊM ĐOẠN NÀY CHẶN LỖI NGHIỆP VỤ: Chặn quét mã các sản phẩm đã ngừng kinh doanh
        if (imei.getVariant() != null && imei.getVariant().getProduct() != null) {
            if (imei.getVariant().getProduct().getStatus() != 1) { // 1 là đang bán, khác 1 là ngừng bán
                throw new RuntimeException("Sản phẩm này đã ngừng kinh doanh, không thể bán!");
            }
        }

        return convertToDTO(imei);
    }

    // =========================================================================
    // 🛠️ HÀM PHỤ TRỢ: Chuyển đổi dữ liệu và BƠM LINK ẢNH VÀO DTO (BẢN CHUẨN 100%)
    // =========================================================================
    private ImeiResponseDTO convertToDTO(Imei imei) {
        ProductVariant variant = imei.getVariant();
        String prodName = "N/A";
        String varName = "N/A";
        String imgUrl = "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9"; // Ảnh mặc định dự phòng

        if (variant != null) {
            varName = variant.getColor() + " - " + variant.getStorage();
            if (variant.getProduct() != null) {
                prodName = variant.getProduct().getName();

                // 🔥 ĐÃ SỬA CHUẨN THEO ENTITY MỚI CỦA MÀY:
                // Lấy ra danh sách ảnh của Sản phẩm
                var images = variant.getProduct().getImages();

                // Kiểm tra xem danh sách ảnh có tồn tại và có ảnh nào bên trong không
                if (images != null && !images.isEmpty()) {
                    // Móc lấy cái ảnh đầu tiên (index 0) làm ảnh đại diện
                    // ⚠️ LƯU Ý: Đổi .getImageUrl() thành đúng tên getter trong file ProductImage.java nhé!
                    imgUrl = images.get(0).getImageUrl();
                }
            }
        }

        return ImeiResponseDTO.builder()
                .id(imei.getId())
                .imeiCode(imei.getImeiCode())
                .status(imei.getStatus().name())
                .createdAt(imei.getCreatedAt())
                .productName(prodName)
                .variantName(varName)
                .price(variant != null ? variant.getPrice() : java.math.BigDecimal.ZERO)
                .imageUrl(imgUrl) // Bơm link ảnh vào đây
                .build();
    }

    // -------------------------------------------------------------------------
    // CÁC HÀM BÊN DƯỚI GIỮ NGUYÊN CODE CỦA MÀY
    // -------------------------------------------------------------------------
    @Override
    @Transactional
    public void addImeisToVariant(Long variantId, List<String> imeiCodes) {
        // ... (Code cũ của mày)
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
            imei.setStatus(ImeiStatus.AVAILABLE);
            newImeis.add(imei);
        }

        imeiRepository.saveAll(newImeis);
        updateVariantStock(variant);
    }

    @Override
    @Transactional
    public void deleteImei(Long id) {
        // ... (Code cũ của mày)
        Imei imei = imeiRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mã IMEI không tồn tại!"));

        if (imei.getStatus() != ImeiStatus.AVAILABLE) {
            throw new RuntimeException("Không thể xóa. Máy này đang ở trạng thái: " + imei.getStatus().name());
        }

        ProductVariant variant = imei.getVariant();
        imeiRepository.delete(imei);

        imeiRepository.flush();
        updateVariantStock(variant);
    }

    @Override
    @Transactional
    public void changeImeiStatus(Long id, ImeiStatus newStatus) {
        // ... (Code cũ của mày)
        Imei imei = imeiRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mã IMEI không tồn tại!"));
        if (newStatus == ImeiStatus.SOLD) {
            throw new RuntimeException("Không được phép tự ý chuyển trạng thái sang ĐÃ BÁN thủ công! Vui lòng tạo đơn hàng.");
        }
        if (imei.getStatus() == ImeiStatus.SOLD &&
                (newStatus != ImeiStatus.RETURNING && newStatus != ImeiStatus.WARRANTY)) {
            throw new RuntimeException("Máy đã bán chỉ có thể chuyển sang trạng thái ĐỔI TRẢ hoặc BẢO HÀNH!");
        }

        if (imei.getStatus() == ImeiStatus.DEFECTIVE && newStatus == ImeiStatus.AVAILABLE) {
            throw new RuntimeException("Hàng đã đánh dấu LỖI không được phép đưa lại lên kệ bán!");
        }

        imei.setStatus(newStatus);
        imeiRepository.save(imei);
        updateVariantStock(imei.getVariant());
    }

    private void updateVariantStock(ProductVariant variant) {
        long availableCount = imeiRepository.countByVariantAndStatus(variant, ImeiStatus.AVAILABLE);
        variant.setStock((int) availableCount);
        variantRepository.save(variant);
    }

    @Override
    @Transactional
    public void releaseImeisAfterRefund(Long orderId) {
        // ... (Code cũ của mày)
        List<Imei> imeis = imeiRepository.findByOrderId(orderId);
        for (Imei imei : imeis) {
            imei.setStatus(ImeiStatus.AVAILABLE);
            imei.setOrder(null);
        }
        imeiRepository.saveAll(imeis);

        Set<ProductVariant> variants = imeis.stream()
                .map(Imei::getVariant)
                .collect(Collectors.toSet());
        for (ProductVariant v : variants) {
            updateVariantStock(v);
        }
    }
}