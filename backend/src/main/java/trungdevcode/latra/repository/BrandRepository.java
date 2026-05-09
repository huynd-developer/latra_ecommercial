package trungdevcode.latra.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import trungdevcode.latra.entity.Brand;

public interface BrandRepository extends JpaRepository<Brand, Long> {
}