package trungdevcode.latra.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import trungdevcode.latra.entity.Order;
import trungdevcode.latra.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);

    @Query(value = "SELECT COUNT(*) FROM orders WHERE user_id = ?1 AND CAST(created_at AS DATE) = CAST(GETDATE() AS DATE)", nativeQuery = true)
    int countOrdersTodayByUserId(Long userId);

    // 2. Tính tổng tiền các đơn đã hoàn thành của nhân viên trong ngày hôm nay
    @Query(value = "SELECT SUM(total_amount) FROM orders WHERE user_id = ?1 AND status = 'COMPLETED' AND CAST(created_at AS DATE) = CAST(GETDATE() AS DATE)", nativeQuery = true)
    BigDecimal sumRevenueTodayByUserId(Long userId);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.user.id = :userId AND o.voucher.id = :voucherId AND o.status <> trungdevcode.latra.enums.OrderStatus.CANCELLED")
    long countVoucherUsageByUser(@Param("userId") Long userId, @Param("voucherId") Long voucherId);
    List<Order> findByStatusAndCreatedAtBefore(OrderStatus status, LocalDateTime createdAtBefore);
}
