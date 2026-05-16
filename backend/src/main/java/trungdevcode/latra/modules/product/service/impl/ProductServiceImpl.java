// backend/src/main/java/trungdevcode/latra/modules/product/service/impl/ProductServiceImpl.java
package trungdevcode.latra.modules.product.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.dao.DataIntegrityViolationException;

import trungdevcode.latra.entity.Product;
import trungdevcode.latra.entity.ProductImage;
import trungdevcode.latra.entity.ProductVariant;
import trungdevcode.latra.modules.product.dto.request.ProductRequest;
import trungdevcode.latra.modules.product.dto.response.ProductResponse;
import trungdevcode.latra.modules.product.service.ProductService;
import trungdevcode.latra.repository.*;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import java.util.Map;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ProductImageRepository productImageRepository;
    private final ImeiRepository imeiRepository; // Thêm repo này để check
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final Cloudinary cloudinary;

    @Value("${app.upload.product.dir:uploads/}")
    private String productUploadDir;
    private void validateBrandAndCategory(Long brandId, Long categoryId) {
        if (!brandRepository.existsById(brandId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Thương hiệu (Brand) không tồn tại!");
        }
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Danh mục (Category) không tồn tại!");
        }
    }
    @Override
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy sản phẩm!"));
        return mapToResponseDTO(product);
    }

    @Override
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        validateBrandAndCategory(request.getBrandId(), request.getCategoryId());
        validateUniqueVariants(request.getVariants());
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setBrandId(request.getBrandId());
        product.setCategoryId(request.getCategoryId());
        product.setStatus(request.getStatus());

        if (request.getVariants() != null) {
            List<ProductVariant> variantList = request.getVariants().stream().map(vDto -> {
                ProductVariant variant = new ProductVariant();
                variant.setColor(vDto.getColor());
                variant.setStorage(vDto.getStorage());
                variant.setPrice(vDto.getPrice());

                // 🔥 ĐÃ SỬA: Ép tồn kho = 0. Tồn kho chỉ được sinh ra từ module IMEI
                variant.setStock(0);

                variant.setSku(vDto.getSku().toUpperCase().trim());
                variant.setCondition(vDto.getCondition());
                variant.setProduct(product);
                return variant;
            }).collect(Collectors.toList());

            product.setVariants(variantList);
        }

        try {
            Product savedProduct = productRepository.saveAndFlush(product);
            return mapToResponseDTO(savedProduct);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Lỗi: Mã SKU đã tồn tại trong hệ thống!");
        }
    }

    @Override
    public Map<String, Object> getAllProducts(int page, int size) {
        Pageable paging = PageRequest.of(page, size);
        Page<Product> pageProducts = productRepository.findAll(paging);

        List<ProductResponse> productDTOs = pageProducts.getContent().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("products", productDTOs);
        response.put("currentPage", pageProducts.getNumber());
        response.put("totalItems", pageProducts.getTotalElements());
        response.put("totalPages", pageProducts.getTotalPages());
        return response;
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy sản phẩm!"));
        validateBrandAndCategory(request.getBrandId(), request.getCategoryId());
        validateUniqueVariants(request.getVariants());

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setBrandId(request.getBrandId());
        product.setCategoryId(request.getCategoryId());
        product.setStatus(request.getStatus());

        // 🔥 LOGIC CẬP NHẬT VARIANT CHUẨN: Match bằng ID
        Map<Long, ProductRequest.VariantRequestDTO> requestMap = new HashMap<>();
        List<ProductRequest.VariantRequestDTO> newVariantsToCreate = new ArrayList<>();

        if (request.getVariants() != null) {
            for (var v : request.getVariants()) {
                if (v.getId() != null) {
                    requestMap.put(v.getId(), v); // Variant cũ cần update
                } else {
                    newVariantsToCreate.add(v);   // Variant mới hoàn toàn
                }
            }
        }

        Iterator<ProductVariant> iterator = product.getVariants().iterator();
        while (iterator.hasNext()) {
            ProductVariant existingVariant = iterator.next();
            Long varId = existingVariant.getId();

            if (requestMap.containsKey(varId)) {
                // 1. Cập nhật Variant cũ
                var vDto = requestMap.get(varId);
                existingVariant.setColor(vDto.getColor());
                existingVariant.setStorage(vDto.getStorage());
                existingVariant.setPrice(vDto.getPrice());
                existingVariant.setSku(vDto.getSku().toUpperCase().trim());
                existingVariant.setCondition(vDto.getCondition());
                // KHÔNG CẬP NHẬT STOCK Ở ĐÂY
            } else {
                // 2. Admin muốn Xóa Variant này
                // 🔥 CHỐT CHẶN: Kiểm tra có IMEI nào đang nối với Variant này không
                long imeiCount = imeiRepository.countByVariantAndStatus(existingVariant, trungdevcode.latra.enums.ImeiStatus.AVAILABLE);
                if (existingVariant.getStock() > 0 || imeiCount > 0) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Không thể xóa phiên bản SKU: " + existingVariant.getSku() + " vì vẫn còn " + existingVariant.getStock() + " thiết bị trong kho!");
                }
                iterator.remove();
            }
        }

        // 3. Thêm các Variant mới
        for (var vDto : newVariantsToCreate) {
            ProductVariant newVariant = new ProductVariant();
            newVariant.setColor(vDto.getColor());
            newVariant.setStorage(vDto.getStorage());
            newVariant.setPrice(vDto.getPrice());
            newVariant.setStock(0); // 🔥 Bắt buộc = 0
            newVariant.setSku(vDto.getSku().toUpperCase().trim());
            newVariant.setCondition(vDto.getCondition());
            newVariant.setProduct(product);
            product.getVariants().add(newVariant);
        }

        try {
            productRepository.saveAndFlush(product);
            return mapToResponseDTO(product);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Không thể cập nhật! Mã SKU bị trùng lặp.");
        }
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy sản phẩm!"));

        // Tính tổng tồn kho của tất cả phiên bản
        int totalStock = product.getVariants().stream()
                .mapToInt(ProductVariant::getStock)
                .sum();

        if (totalStock > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Không thể ẩn sản phẩm này! Trong kho hiện vẫn còn tổng cộng " + totalStock + " thiết bị chưa xuất.");
        }

        product.setStatus(0); // Xóa mềm an toàn
        productRepository.save(product);
    }
    @Transactional
    public void deleteProductImage(Long imageId) {
        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy ảnh!"));

        if (image.getIsMain()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Không thể xóa ảnh chính. Hãy đặt ảnh khác làm ảnh chính trước!");
        }

        // (Tùy chọn: Dùng java.nio.file.Files để xóa file vật lý trong thư mục uploads/)

        productImageRepository.delete(image);
    }
    @Transactional
    public void setMainImage(Long productId, Long imageId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sản phẩm không tồn tại!"));

        // Cập nhật tất cả ảnh của product này thành isMain = false
        product.getImages().forEach(img -> img.setIsMain(false));

        // Tìm ảnh được chọn và set isMain = true
        ProductImage selectedImage = product.getImages().stream()
                .filter(img -> img.getId().equals(imageId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ảnh này không thuộc về sản phẩm trên!"));

        selectedImage.setIsMain(true);
        productImageRepository.saveAll(product.getImages());
    }

    @Override
    public String uploadImage(Long productId, MultipartFile file) throws Exception {
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File upload bắt buộc phải là định dạng hình ảnh!");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy sản phẩm!"));

        // Upload lên folder "latra/products"
        Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                "folder", "latra/products"
        ));

        // Lấy link HTTPS
        String secureUrl = uploadResult.get("secure_url").toString();

        ProductImage productImage = new ProductImage();
        productImage.setProduct(product);
        // Lưu thẳng link http vào cột imageUrl
        productImage.setImageUrl(secureUrl);
        productImage.setIsMain(product.getImages() == null || product.getImages().isEmpty());

        productImageRepository.save(productImage);
        return "Upload thành công! URL: " + secureUrl;
    }
    // Hàm tiện ích map Entity sang ResponseDTO cho gọn
    private ProductResponse mapToResponseDTO(Product product) {
        ProductResponse dto = new ProductResponse();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setBrandId(product.getBrandId());
        dto.setCategoryId(product.getCategoryId());
        dto.setStatus(product.getStatus());

        if (product.getImages() != null && !product.getImages().isEmpty()) {
            String img = product.getImages().stream()
                    .filter(ProductImage::getIsMain)
                    .map(ProductImage::getImageUrl)
                    .findFirst()
                    .orElse(product.getImages().get(0).getImageUrl());
            dto.setMainImageUrl(img);
        }

        if (product.getVariants() != null) {
            List<ProductResponse.VariantDTO> variantDTOs = product.getVariants().stream().map(v -> {
                ProductResponse.VariantDTO vDto = new ProductResponse.VariantDTO();
                vDto.setVariantId(v.getId());
                vDto.setColor(v.getColor());
                vDto.setStorage(v.getStorage());
                vDto.setPrice(v.getPrice());
                vDto.setStock(v.getStock()); // Trả về stock thực tế để Vue hiển thị
                vDto.setSku(v.getSku());
                vDto.setCondition(v.getCondition());
                return vDto;
            }).collect(Collectors.toList());
            dto.setVariants(variantDTOs);
        }
        return dto;
    }

    private void validateUniqueVariants(List<ProductRequest.VariantRequestDTO> variants) {
        if (variants == null || variants.isEmpty()) return;

        Set<String> uniqueSkus = new HashSet<>();
        Set<String> uniqueAttributes = new HashSet<>();

        for (var v : variants) {
            // Check trùng SKU
            String sku = v.getSku() != null ? v.getSku().trim().toUpperCase() : "";
            if (!uniqueSkus.add(sku)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Lỗi: Mã SKU '" + sku + "' bị trùng lặp trong danh sách!");
            }

            // Check trùng Tổ hợp: Màu + Dung lượng + Tình trạng
            String attrKey = String.format("%s-%s-%s",
                    v.getColor().trim().toUpperCase(),
                    v.getStorage() != null ? v.getStorage().trim().toUpperCase() : "NONE",
                    v.getCondition() != null ? v.getCondition().trim().toUpperCase() : "NEW");

            if (!uniqueAttributes.add(attrKey)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Lỗi: Bạn đang tạo 2 phiên bản có cùng [Màu sắc + Dung lượng + Tình trạng] (" + v.getColor() + " - " + v.getStorage() + "). Vui lòng gộp lại!");
            }
        }
    }
}