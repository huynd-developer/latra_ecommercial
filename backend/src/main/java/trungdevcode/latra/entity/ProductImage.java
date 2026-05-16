// backend/src/main/java/trungdevcode/latra/entity/ProductImage.java
package trungdevcode.latra.entity;

import trungdevcode.latra.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductImage extends BaseEntity {

    @Column(name = "image_url", length = 255)
    private String imageUrl;

    @Column(name = "is_main")
    private Boolean isMain = false;

    @Column(name = "alt_text", length = 255)
    private String altText; // Dành cho SEO (VD: "iPhone 15 Pro Max Titan Tự nhiên")

    @Column(name = "display_order")
    private Integer displayOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
}