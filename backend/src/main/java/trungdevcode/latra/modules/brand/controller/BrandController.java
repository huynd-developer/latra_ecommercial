// backend/src/main/java/trungdevcode/latra/modules/brand/controller/BrandController.java
package trungdevcode.latra.modules.brand.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import trungdevcode.latra.common.response.ApiResponse;
import trungdevcode.latra.entity.Brand;
import trungdevcode.latra.modules.brand.dto.request.BrandRequestDTO;
import trungdevcode.latra.modules.brand.service.BrandService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/brands")
@RequiredArgsConstructor
public class BrandController {

    private final BrandService brandService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Brand>>> getAllBrands() {
        return ResponseEntity.ok(ApiResponse.success(brandService.getAllBrands(), "Lấy danh sách thành công"));
    }

    @PostMapping("/admin")
    public ResponseEntity<ApiResponse<Brand>> createBrand(@Valid @RequestBody BrandRequestDTO request) {
        return ResponseEntity.ok(ApiResponse.success(brandService.createBrand(request), "Tạo thương hiệu thành công"));
    }

    @PutMapping("/admin/{id}")
    public ResponseEntity<ApiResponse<Brand>> updateBrand(
            @PathVariable Long id,
            @Valid @RequestBody BrandRequestDTO request) {
        return ResponseEntity.ok(ApiResponse.success(brandService.updateBrand(id, request), "Cập nhật thành công"));
    }

    @DeleteMapping("/admin/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBrand(@PathVariable Long id) {
        brandService.deleteBrand(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Đã khóa thương hiệu thành công"));
    }
    @PostMapping("/admin/{id}/logo")
    public ResponseEntity<ApiResponse<String>> uploadLogo(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) throws Exception {
        String fileName = brandService.uploadLogo(id, file);
        return ResponseEntity.ok(ApiResponse.success(fileName, "Upload logo thành công"));
    }
}