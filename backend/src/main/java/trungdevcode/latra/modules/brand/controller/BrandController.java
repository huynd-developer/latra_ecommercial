package trungdevcode.latra.modules.brand.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    // Không chặn quyền: POS của Thu ngân hoặc App của Khách hàng đều gọi được để load bộ lọc
    public ResponseEntity<ApiResponse<List<Brand>>> getAllBrands() {
        return ResponseEntity.ok(ApiResponse.success(brandService.getAllBrands(), "Lấy danh sách thành công"));
    }

    @PostMapping
    // 🛠️ ĐÃ SỬA BỎ /admin: URL gọn gàng -> POST /api/v1/brands
    @PreAuthorize("hasAnyAuthority('MANAGER', 'OWNER')")
    public ResponseEntity<ApiResponse<Brand>> createBrand(@Valid @RequestBody BrandRequestDTO request) {
        return ResponseEntity.ok(ApiResponse.success(brandService.createBrand(request), "Tạo thương hiệu thành công"));
    }

    @PutMapping("/{id}")
    // 🛠️ ĐÃ SỬA BỎ /admin: URL gọn gàng -> PUT /api/v1/brands/{id}
    @PreAuthorize("hasAnyAuthority('MANAGER', 'OWNER')")
    public ResponseEntity<ApiResponse<Brand>> updateBrand(
            @PathVariable Long id,
            @Valid @RequestBody BrandRequestDTO request) {
        return ResponseEntity.ok(ApiResponse.success(brandService.updateBrand(id, request), "Cập nhật thành công"));
    }

    @DeleteMapping("/{id}")
    // 🛠️ ĐÃ SỬA BỎ /admin: URL gọn gàng -> DELETE /api/v1/brands/{id}
    @PreAuthorize("hasAuthority('OWNER')")
    public ResponseEntity<ApiResponse<Void>> deleteBrand(@PathVariable Long id) {
        brandService.deleteBrand(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Đã khóa thương hiệu thành công"));
    }

    @PostMapping("/{id}/logo")
    // 🛠️ ĐÃ SỬA BỎ /admin: URL gọn gàng -> POST /api/v1/brands/{id}/logo
    @PreAuthorize("hasAnyAuthority('MANAGER', 'OWNER')")
    public ResponseEntity<ApiResponse<String>> uploadLogo(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) throws Exception {
        String fileName = brandService.uploadLogo(id, file);
        return ResponseEntity.ok(ApiResponse.success(fileName, "Upload logo thành công"));
    }
}