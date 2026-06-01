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
                    .status(imei.getStatus().name())
                    .createdAt(imei.getCreatedAt())
                    .productName(prodName)
                    .variantName(varName)
                    .price(variant != null ? variant.getPrice() : null)
                    .build();
        }).collect(Collectors.toList());
    }

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
            imei.setStatus(ImeiStatus.AVAILABLE);
            newImeis.add(imei);
        }

        imeiRepository.saveAll(newImeis);
        updateVariantStock(variant);
    }

    @Override
    @Transactional
    public void deleteImei(Long id) {
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
        Imei imei = imeiRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mã IMEI không tồn tại!"));

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

    // =========================================================================
    // HÀM MỚI BỔ SUNG CHO POS (Giải quyết triệt để 404)
    // =========================================================================
    @Override
    public List<ImeiResponseDTO> getAvailableImeisByProductId(Long productId) {
        List<Imei> imeis = imeiRepository.findByVariantProductIdAndStatus(productId, ImeiStatus.AVAILABLE);

        // NẾU HẾT HÀNG, TRẢ VỀ MẢNG RỖNG THAY VÌ BÁO LỖI
        if (imeis == null || imeis.isEmpty()) {
            return new ArrayList<>();
        }

        return imeis.stream().map(imei -> {
            ProductVariant variant = imei.getVariant();
            String prodName = (variant != null && variant.getProduct() != null) ? variant.getProduct().getName() : "N/A";
            String varName = (variant != null) ? (variant.getColor() + " - " + variant.getStorage()) : "N/A";

            return ImeiResponseDTO.builder()
                    .id(imei.getId())
                    .imeiCode(imei.getImeiCode())
                    .status(imei.getStatus().name())
                    .createdAt(imei.getCreatedAt())
                    .productName(prodName)
                    .variantName(varName)
                    .price(variant != null ? variant.getPrice() : null)
                    .build();
        }).collect(Collectors.toList());
    }
}