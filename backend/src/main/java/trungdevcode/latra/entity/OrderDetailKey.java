// backend/src/main/java/trungdevcode/latra/entity/OrderDetailKey.java
// (GIỮ NGUYÊN vì là Composite Key)
package trungdevcode.latra.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailKey implements Serializable {
    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "variant_id")
    private Long variantId;
}