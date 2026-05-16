// backend/src/main/java/trungdevcode/latra/modules/brand/service/impl/BrandServiceImpl.java
package trungdevcode.latra.modules.brand.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import trungdevcode.latra.common.exception.BusinessException;
import trungdevcode.latra.entity.Brand;
import trungdevcode.latra.modules.brand.dto.request.BrandRequestDTO;
import trungdevcode.latra.modules.brand.service.BrandService;
import trungdevcode.latra.repository.BrandRepository;
import trungdevcode.latra.repository.ProductRepository;
import java.util.List;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;
    private final ProductRepository productRepository;
    private final Cloudinary cloudinary;

    @Override
    public List<Brand> getAllBrands() {
        return brandRepository.findAll();
    }

    @Override
    @Transactional
    public Brand createBrand(BrandRequestDTO request) {
        if (brandRepository.existsByNameIgnoreCaseAndStatusNot(request.getName().trim(), 0)) {
            throw new BusinessException("Thương hiệu '" + request.getName() + "' đã tồn tại và đang hoạt động!");
        }

        Brand brand = new Brand();
        brand.setName(request.getName().trim());
        brand.setCountry(request.getCountry());
        brand.setStatus(request.getStatus() != null ? request.getStatus() : 1);
        return brandRepository.save(brand);
    }
    @Override
    @Transactional
    public Brand updateBrand(Long id, BrandRequestDTO request) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "Không tìm thấy thương hiệu!"));

        if (brandRepository.existsByNameIgnoreCaseAndIdNotAndStatusNot(request.getName().trim(), id, 0)) {
            throw new BusinessException("Tên thương hiệu đã bị trùng với một hãng khác đang hoạt động!");
        }

        brand.setName(request.getName().trim());
        brand.setCountry(request.getCountry());
        brand.setStatus(request.getStatus());
        return brandRepository.save(brand);
    }
    @Override
    @Transactional
    public String uploadLogo(Long brandId, MultipartFile file) throws Exception {
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BusinessException("File upload bắt buộc phải là định dạng hình ảnh!");
        }

        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(() -> new BusinessException(404, "Không tìm thấy thương hiệu!"));

        // Gọi Cloudinary để đẩy file lên folder "latra/brands"
        Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                "folder", "latra/brands"
        ));

        // Lấy link HTTPS trả về từ Cloudinary
        String secureUrl = uploadResult.get("secure_url").toString();

        // Lưu đường link này vào database
        brand.setLogo(secureUrl);
        brandRepository.save(brand);

        return secureUrl;
    }
    @Override
    @Transactional
    public void deleteBrand(Long id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "Không tìm thấy thương hiệu!"));

        if (productRepository.existsByBrandIdAndStatusNot(id, 0)) {
            throw new BusinessException("Không thể khóa! Đang có sản phẩm hoạt động thuộc thương hiệu này. Hãy chuyển hãng cho sản phẩm trước.");
        }

        // Thực thi Xóa mềm
        brand.setStatus(0);
        brandRepository.save(brand);
    }
}