// backend/src/main/java/trungdevcode/latra/entity/OrderVoucher.java
package trungdevcode.latra.entity;

import trungdevcode.latra.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "order_vouchers")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class OrderVoucher extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voucher_id", nullable = false)
    private Voucher voucher;

    // 🔥 Cột sống còn: Ghi lại số tiền THỰC TẾ mà mã này đã giảm cho đơn này
    @Column(name = "discount_amount", nullable = false)
    private BigDecimal discountAmount;
}