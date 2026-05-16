// backend/src/main/java/trungdevcode/latra/entity/OrderDetail.java
// (GIỮ NGUYÊN vì dùng Composite Key - @EmbeddedId)
package trungdevcode.latra.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "order_details")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class OrderDetail {
    @EmbeddedId
    private OrderDetailKey id = new OrderDetailKey();

    @ManyToOne
    @MapsId("orderId")
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @MapsId("variantId")
    @JoinColumn(name = "variant_id")
    private ProductVariant variant;

    private Integer quantity;
    private BigDecimal price;
}