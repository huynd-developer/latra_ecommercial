package trungdevcode.latra.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import trungdevcode.latra.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByBrandIdAndStatusNot(Long brandId, Integer status);
    Page<Product> findByStatus(Integer status, Pageable pageable);
    // Check xem có sản phẩm nào đang SỐNG thuộc Danh mục này không?
    boolean existsByCategoryIdAndStatusNot(Long categoryId, Integer status);
}
