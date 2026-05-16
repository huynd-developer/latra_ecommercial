package trungdevcode.latra.entity;

import jakarta.persistence.*;
import lombok.*;
import trungdevcode.latra.entity.base.BaseEntity;
import trungdevcode.latra.enums.PaymentMethod;
import trungdevcode.latra.enums.PaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_transactions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class PaymentTransaction extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Enumerated(EnumType.STRING)
    private PaymentMethod provider;

    private BigDecimal amount;

    @Column(unique = true)
    private String gatewayTransactionNo; // vnp_TxnRef

    @Enumerated(EnumType.STRING)
    private PaymentStatus status = PaymentStatus.PENDING;

    private LocalDateTime payDate;

    @Column(length = 1000)
    private String paymentUrl;
}