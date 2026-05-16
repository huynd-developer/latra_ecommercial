// backend/src/main/java/trungdevcode/latra/modules/category/service/impl/CategoryServiceImpl.java
package trungdevcode.latra.modules.category.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import trungdevcode.latra.common.exception.BusinessException;
import trungdevcode.latra.entity.Category;
import trungdevcode.latra.modules.category.dto.request.CategoryRequestDTO;
import trungdevcode.latra.modules.category.service.CategoryService;
import trungdevcode.latra.repository.CategoryRepository;
import trungdevcode.latra.repository.ProductRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    @Transactional
    public Category createCategory(CategoryRequestDTO request) {
        // 👉 ĐÃ SỬA: Bỏ qua các danh mục đã bị xóa (status = 0)
        if (categoryRepository.existsByNameIgnoreCaseAndStatusNot(request.getName().trim(), 0)) {
            throw new BusinessException("Danh mục '" + request.getName() + "' đã tồn tại và đang hoạt động!");
        }

        // Kiểm tra Danh mục cha có tồn tại không
        if (request.getParentId() != null) {
            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new BusinessException(404, "Danh mục cha không tồn tại!"));

            if (parent.getStatus() != null && parent.getStatus() == 0) {
                throw new BusinessException("Danh mục cha này đã bị vô hiệu hóa, không thể thêm danh mục con vào!");
            }
        }

        Category category = new Category();
        category.setName(request.getName().trim());
        category.setParentId(request.getParentId());
        category.setStatus(1); // Mặc định là Hoạt động
        return categoryRepository.save(category);
    }

    @Override
    @Transactional
    public Category updateCategory(Long id, CategoryRequestDTO request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "Không tìm thấy danh mục!"));

        if (categoryRepository.existsByNameIgnoreCaseAndIdNotAndStatusNot(request.getName().trim(), id, 0)) {
            throw new BusinessException("Tên danh mục bị trùng với một danh mục khác đang hoạt động!");
        }

        // 👉 KIỂM TRA VÒNG LẶP (CIRCULAR REFERENCE)
        if (request.getParentId() != null) {
            if (request.getParentId().equals(id)) {
                throw new BusinessException("Không thể chọn danh mục cha là chính nó!");
            }

            // Dò ngược lên trên xem có bị nhận "Cháu" làm "Cha" không
            Long currentParentId = request.getParentId();
            int depth = 0; // Chống lặp vô tận trong DB
            while (currentParentId != null && depth < 10) {
                Category parentCat = categoryRepository.findById(currentParentId)
                        .orElseThrow(() -> new BusinessException(404, "Danh mục cha không tồn tại!"));

                if (parentCat.getId().equals(id)) {
                    throw new BusinessException("Lỗi cấu trúc: Bạn không thể đặt danh mục này làm con của một danh mục đang nằm bên trong chính nó!");
                }
                currentParentId = parentCat.getParentId();
                depth++;
            }
        }

        category.setName(request.getName().trim());
        category.setParentId(request.getParentId());
        return categoryRepository.save(category);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "Không tìm thấy danh mục!"));
        if (categoryRepository.existsByParentIdAndStatusNot(id, 0)) {
            throw new BusinessException("Không thể khóa! Vẫn còn danh mục con đang hoạt động nằm trong danh mục này.");
        }
        if (productRepository.existsByCategoryIdAndStatusNot(id, 0)) {
            throw new BusinessException("Không thể khóa! Đang có sản phẩm hoạt động thuộc danh mục này. Hãy xóa sản phẩm trước.");
        }

        // 3. Thực thi Xóa mềm
        category.setStatus(0);
        categoryRepository.save(category);
    }
}