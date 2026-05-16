// backend/src/main/java/trungdevcode/latra/entity/ProductVariant.java
package trungdevcode.latra.entity;

import trungdevcode.latra.entity.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "product_variants", indexes = {
        @Index(name = "idx_sku", columnList = "sku", unique = true),
        @Index(name = "idx_product_id", columnList = "product_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariant extends BaseEntity {

    @Column(name = "color", columnDefinition = "NVARCHAR(50)")
    private String color;

    @Column(name = "storage", columnDefinition = "NVARCHAR(50)")
    private String storage;

    @Column(name = "price", nullable = false, precision = 15, scale = 2)
    private BigDecimal price;

    @Column(name = "stock")
    private Integer stock = 0;

    @Column(name = "sku", unique = true)
    private String sku;
    
    @Column(name = "condition", length = 50)
    private String condition;
    
    @ManyToOne
    @JoinColumn(name = "product_id")
    @JsonIgnoreProperties("variants")
    private Product product;

    @OneToMany(mappedBy = "variant")
    @JsonIgnore 
    private List<Imei> imeis;
}