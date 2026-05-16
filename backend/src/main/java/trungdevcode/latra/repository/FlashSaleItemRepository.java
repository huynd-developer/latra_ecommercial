package trungdevcode.latra.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import trungdevcode.latra.entity.FlashSaleItem;

import java.time.LocalDateTime;
import java.util.Optional;

public interface FlashSaleItemRepository extends JpaRepository<FlashSaleItem, Long> {

    // Câu lệnh kiểm tra xem Phiên bản này có đang nằm trong đợt Sale nào bị TRÙNG GIỜ không
    @Query("SELECT COUNT(fsi) FROM FlashSaleItem fsi " +
            "WHERE fsi.productVariant.id = :variantId " +
            "AND fsi.flashSale.id != :excludeSaleId " + // Bỏ qua chính nó (dùng khi Update)
            "AND fsi.flashSale.startTime < :newEndTime " +
            "AND fsi.flashSale.endTime > :newStartTime")
    long countOverlappingSales(@Param("variantId") Long variantId,
                               @Param("newStartTime") LocalDateTime newStartTime,
                               @Param("newEndTime") LocalDateTime newEndTime,
                               @Param("excludeSaleId") Long excludeSaleId);


    @Query("SELECT fsi FROM FlashSaleItem fsi " +
            "JOIN fsi.flashSale fs " +
            "WHERE fsi.productVariant.id = :variantId " +
            "AND fs.status = 'ACTIVE' " +
            "AND fs.startTime <= CURRENT_TIMESTAMP " +
            "AND fs.endTime >= CURRENT_TIMESTAMP")
    Optional<FlashSaleItem> findActiveSaleByVariantId(@Param("variantId") Long variantId);

    @Modifying
    @Query("UPDATE FlashSaleItem fsi SET fsi.quantityLimit = fsi.quantityLimit - :qty " +
            "WHERE fsi.flashSale.id = :flashSaleId AND fsi.productVariant.id = :variantId AND fsi.quantityLimit >= :qty")
    int decrementFlashSaleQuantity(@Param("flashSaleId") Long flashSaleId, @Param("variantId") Long variantId, @Param("qty") Integer qty);
}