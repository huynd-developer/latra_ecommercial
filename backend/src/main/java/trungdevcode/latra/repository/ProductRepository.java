package trungdevcode.latra.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import trungdevcode.latra.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
