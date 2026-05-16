package trungdevcode.latra.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import trungdevcode.latra.entity.Brand;

// backend/src/main/java/trungdevcode/latra/repository/BrandRepository.java
public interface BrandRepository extends JpaRepository<Brand, Long> {
    // Chỉ check trùng tên với những hãng ĐANG HOẠT ĐỘNG (status != 0)
    boolean existsByNameIgnoreCaseAndStatusNot(String name, Integer status);
    boolean existsByNameIgnoreCaseAndIdNotAndStatusNot(String name, Long id, Integer status);
}
