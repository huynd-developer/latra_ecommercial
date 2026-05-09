package trungdevcode.latra.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import trungdevcode.latra.entity.ProductImage;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
}