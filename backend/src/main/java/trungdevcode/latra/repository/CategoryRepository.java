package trungdevcode.latra.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import trungdevcode.latra.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}