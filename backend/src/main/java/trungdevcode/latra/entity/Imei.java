// backend/src/main/java/trungdevcode/latra/entity/Imei.java
package trungdevcode.latra.entity;

import trungdevcode.latra.entity.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import trungdevcode.latra.enums.ImeiStatus;

@Entity
@Table(name = "imeis", indexes = {
        @Index(name = "idx_imei_code", columnList = "imei_code", unique = true),
        @Index(name = "idx_variant_status", columnList = "variant_id, status") // Index gộp cho câu Query Pessimistic Lock của bạn
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Imei extends BaseEntity {

    @Column(name = "imei_code", unique = true, nullable = false)
    private String imeiCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30, nullable = false)
    private ImeiStatus status = ImeiStatus.AVAILABLE;

    @ManyToOne
    @JoinColumn(name = "variant_id", nullable = false)
    private ProductVariant variant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;
}