// backend/src/main/java/trungdevcode/latra/entity/Shipment.java
package trungdevcode.latra.entity;

import trungdevcode.latra.entity.base.BaseEntity;
import trungdevcode.latra.enums.ShipmentStatus;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "shipments")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Shipment extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    // 1. Thông tin người nhận thực tế (Cực kỳ quan trọng để shipper gọi)
    @Column(name = "recipient_name", nullable = false, columnDefinition = "NVARCHAR(100)")
    private String recipientName;

    @Column(name = "recipient_phone", nullable = false, length = 20)
    private String recipientPhone;

    @Column(name = "full_address", nullable = false, columnDefinition = "NVARCHAR(500)")
    private String fullAddress;

    // 2. Thông tin đơn vị vận chuyển
    @Column(name = "shipping_fee", nullable = false)
    private BigDecimal shippingFee = BigDecimal.ZERO;

    @Column(name = "carrier", length = 100)
    private String carrier; // VD: GHN, GHTK, Viettel Post

    @Column(name = "tracking_code", length = 100, unique = true)
    private String trackingCode; // Mã vận đơn để khách tra cứu

    // 3. Trạng thái giao hàng
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private ShipmentStatus status = ShipmentStatus.PREPARING;

    // Ghi chú riêng cho Shipper (VD: "Giao giờ hành chính")
    @Column(name = "shipping_note", columnDefinition = "NVARCHAR(255)")
    private String shippingNote;
}