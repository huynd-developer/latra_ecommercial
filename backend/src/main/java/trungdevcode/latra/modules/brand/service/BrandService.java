package trungdevcode.latra.modules.brand.service;

import org.springframework.web.multipart.MultipartFile;
import trungdevcode.latra.entity.Brand;
import trungdevcode.latra.modules.brand.dto.request.BrandRequestDTO;

import java.util.List;

public interface BrandService {
    List<Brand> getAllBrands();
    Brand createBrand(BrandRequestDTO request);
    Brand updateBrand(Long id, BrandRequestDTO request);
    void deleteBrand(Long id);
    String uploadLogo(Long brandId, MultipartFile file) throws Exception;
}