package trungdevcode.latra.entity;

import trungdevcode.latra.entity.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// NÂNG CẤP BẢO MẬT: Tự động hóa Xóa mềm thay vì xóa thật dưới Database
@org.hibernate.annotations.SQLDelete(sql = "UPDATE products SET is_deleted = true WHERE id = ?")
@org.hibernate.annotations.SQLRestriction("is_deleted = false")
public class Product extends BaseEntity {

    @Column(name = "name", nullable = false, columnDefinition = "NVARCHAR(150)")
    private String name;

    @Column(name = "description", columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @Column(name = "status")
    private Integer status = 1;

    @Column(name = "brand_id")
    private Long brandId;

    @Column(name = "category_id")
    private Long categoryId;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<ProductVariant> variants;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<ProductImage> images;
}