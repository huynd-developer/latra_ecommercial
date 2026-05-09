package trungdevcode.latra.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import trungdevcode.latra.entity.Imei;
import trungdevcode.latra.entity.Order;
import trungdevcode.latra.entity.ProductVariant;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImeiRepository extends JpaRepository<Imei, Long> {

    Optional<Imei> findByImeiCode(String imeiCode);

    long countByVariantAndStatus(ProductVariant variant, String status);

    List<Imei> findByVariantAndStatus(ProductVariant variant, String status);
    boolean existsByImeiCode(String imeiCode);
    List<Imei> findByOrder(Order order);
}