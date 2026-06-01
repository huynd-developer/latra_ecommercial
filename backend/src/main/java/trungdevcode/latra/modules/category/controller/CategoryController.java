package trungdevcode.latra.modules.category.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import trungdevcode.latra.common.response.ApiResponse;
import trungdevcode.latra.entity.Category;
import trungdevcode.latra.modules.category.dto.request.CategoryRequestDTO;
import trungdevcode.latra.modules.category.service.CategoryService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    // Ai cũng có thể gọi (User, Cashier, Manager, Owner)
    public ResponseEntity<ApiResponse<List<Category>>> getAllCategories() {
        return ResponseEntity.ok(ApiResponse.success(categoryService.getAllCategories(), "Lấy danh sách thành công"));
    }

    @PostMapping
    // 🛠️ ĐÃ SỬA: Bỏ chữ /admin thừa ở URL, kiểm soát hoàn toàn bằng quyền
    @PreAuthorize("hasAnyAuthority('MANAGER', 'OWNER')")
    public ResponseEntity<ApiResponse<Category>> createCategory(@Valid @RequestBody CategoryRequestDTO request) {
        return ResponseEntity.ok(ApiResponse.success(categoryService.createCategory(request), "Tạo danh mục thành công"));
    }

    @PutMapping("/{id}")
    // 🛠️ ĐÃ SỬA: URL gọn gàng theo chuẩn RESTful /categories/{id}
    @PreAuthorize("hasAnyAuthority('MANAGER', 'OWNER')")
    public ResponseEntity<ApiResponse<Category>> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequestDTO request) {
        return ResponseEntity.ok(ApiResponse.success(categoryService.updateCategory(id, request), "Cập nhật thành công"));
    }

    @DeleteMapping("/{id}")
    // 🛠️ ĐÃ SỬA: Gọn gàng, chỉ có OWNER mới được vả vào endpoint này
    @PreAuthorize("hasAuthority('OWNER')")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Xóa danh mục thành công"));
    }
}