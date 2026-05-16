// backend/src/main/java/trungdevcode/latra/modules/category/controller/CategoryController.java
package trungdevcode.latra.modules.category.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ApiResponse<List<Category>>> getAllCategories() {
        return ResponseEntity.ok(ApiResponse.success(categoryService.getAllCategories(), "Lấy danh sách thành công"));
    }

    @PostMapping("/admin")
    public ResponseEntity<ApiResponse<Category>> createCategory(@Valid @RequestBody CategoryRequestDTO request) {
        return ResponseEntity.ok(ApiResponse.success(categoryService.createCategory(request), "Tạo danh mục thành công"));
    }

    @PutMapping("/admin/{id}")
    public ResponseEntity<ApiResponse<Category>> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequestDTO request) {
        return ResponseEntity.ok(ApiResponse.success(categoryService.updateCategory(id, request), "Cập nhật thành công"));
    }

    @DeleteMapping("/admin/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Xóa danh mục thành công"));
    }
}