package trungdevcode.latra.modules.category.service;

import trungdevcode.latra.entity.Category;
import trungdevcode.latra.modules.category.dto.request.CategoryRequestDTO;

import java.util.List;

public interface CategoryService {
    List<Category> getAllCategories();
    Category createCategory(CategoryRequestDTO request);
    Category updateCategory(Long id, CategoryRequestDTO request);
    void deleteCategory(Long id);
}