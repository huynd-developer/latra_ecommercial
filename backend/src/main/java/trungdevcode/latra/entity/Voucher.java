// backend/src/main/java/trungdevcode/latra/entity/Voucher.java
package trungdevcode.latra.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import trungdevcode.latra.entity.base.BaseEntity;
import trungdevcode.latra.enums.DiscountType;
import trungdevcode.latra.enums.VoucherCategory;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "vouchers")
@Data
@EqualsAndHashCode(callSuper = true)
public class Voucher extends BaseEntity {

    @Column(unique = true, nullable = false, length = 50)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false)
    private DiscountType discountType; // PERCENTAGE hoặc FIXED_AMOUNT

    @Column(nullable = false)
    private BigDecimal value; // Nếu là PERCENTAGE thì value = 10 (tức 10%). Nếu FIXED thì = 50000 (50k)

    @Column(name = "usage_limit", nullable = false)
    private Integer usageLimit;

    @Column(name = "min_order_value", nullable = false)
    private BigDecimal minOrderValue;

    // Giới hạn giảm tối đa (Chỉ có ý nghĩa khi discountType = PERCENTAGE)
    @Column(name = "max_discount_amount")
    private BigDecimal maxDiscountAmount;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "expired_at", nullable = false)
    private LocalDateTime expiredAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private VoucherCategory category = VoucherCategory.DISCOUNT;

    // BẮT BUỘC PHẢI CÓ STATUS ĐỂ TẮT BẬT VOUCHER KHẨN CẤP (Ví dụ: Setup nhầm mã)
    @Column(nullable = false)
    private Integer status = 1;
}