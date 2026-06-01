package trungdevcode.latra.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import trungdevcode.latra.entity.Imei;
import trungdevcode.latra.entity.Order;
import trungdevcode.latra.entity.ProductVariant;
import trungdevcode.latra.enums.ImeiStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImeiRepository extends JpaRepository<Imei, Long> {

    Optional<Imei> findByImeiCode(String imeiCode);

    long countByVariantAndStatus(ProductVariant variant, ImeiStatus status);
    List<Imei> findByVariantAndStatus(ProductVariant variant, ImeiStatus status);
    boolean existsByImeiCode(String imeiCode);
    List<Imei> findByOrder(Order order);
    List<Imei> findByOrderId(Long orderId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Imei i WHERE i.variant = :variant AND i.status = :status")
    List<Imei> findAvailableImeisWithLock(@Param("variant") ProductVariant variant, @Param("status") ImeiStatus status);

    // =========================================================================
    // HÀM MỚI BỔ SUNG ĐỂ TÌM TẤT CẢ IMEI CỦA MỘT SẢN PHẨM (DỰA VÀO PRODUCT_ID)
    // =========================================================================
    List<Imei> findByVariantProductIdAndStatus(Long productId, ImeiStatus status);
}