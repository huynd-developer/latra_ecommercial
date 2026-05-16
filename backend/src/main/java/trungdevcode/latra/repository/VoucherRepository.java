// backend/src/main/java/trungdevcode/latra/repository/VoucherRepository.java
package trungdevcode.latra.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import trungdevcode.latra.entity.Voucher;
import trungdevcode.latra.modules.promotion.dto.response.VoucherAnalyticsDTO;

import java.util.List;
import java.util.Optional;

public interface VoucherRepository extends JpaRepository<Voucher, Long> {
    Optional<Voucher> findByCode(String code);

    // 🔥 WOW: Trừ lượt sử dụng Voucher ngay dưới DB (Atomic Update)
    // Chỉ trừ được nếu usage_limit > 0. Nếu trả về 0 -> Tức là đã hết lượt.
    @Modifying
    @Query("UPDATE Voucher v SET v.usageLimit = v.usageLimit - 1 WHERE v.code = :code AND v.usageLimit > 0")
    int decrementVoucherUsage(@Param("code") String code);

    // 🔥 WOW: Hoàn lại lượt sử dụng nếu khách HỦY đơn hàng (Refund)
    @Modifying
    @Query("UPDATE Voucher v SET v.usageLimit = v.usageLimit + 1 WHERE v.code = :code")
    void incrementVoucherUsage(@Param("code") String code);

    @Query("SELECT new trungdevcode.latra.modules.promotion.dto.response.VoucherAnalyticsDTO(" +
            "v.code, COUNT(ov.id), SUM(ov.discountAmount), SUM(o.totalAmount)) " +
            "FROM Voucher v " +
            "LEFT JOIN OrderVoucher ov ON v.id = ov.voucher.id " +
            "LEFT JOIN ov.order o " +
            "WHERE o.status = 'COMPLETED' " +
            "GROUP BY v.id, v.code")
    List<VoucherAnalyticsDTO> getVoucherAnalytics();

    boolean existsByCode(String code);

    boolean existsByCodeAndIdNot(String code, Long id);
    Optional<Voucher> findByCodeAndStatus(String code, Integer status);
    List<Voucher> findByStatus(Integer status);
}