package trungdevcode.latra.entity;

import jakarta.persistence.*;
import lombok.*;
import trungdevcode.latra.entity.base.BaseEntity;
import trungdevcode.latra.enums.RefundStatus;
import java.math.BigDecimal;

@Entity
@Table(name = "refund_requests")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class RefundRequest extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "imei_id")
    private Imei returnedImei;

    @Column(columnDefinition = "NVARCHAR(500)")
    private String reason;

    private BigDecimal refundAmount;

    @Enumerated(EnumType.STRING)
    private RefundStatus status;

    private String proofImageUrl;
}