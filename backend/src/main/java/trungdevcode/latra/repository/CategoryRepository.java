package trungdevcode.latra.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import trungdevcode.latra.entity.Category;

// backend/src/main/java/trungdevcode/latra/repository/CategoryRepository.java
public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByNameIgnoreCaseAndStatusNot(String name, Integer status);
    boolean existsByNameIgnoreCaseAndIdNotAndStatusNot(String name, Long id, Integer status);
    boolean existsByParentIdAndStatusNot(Long parentId, Integer status);
}