// backend/src/main/java/trungdevcode/latra/entity/Brand.java
package trungdevcode.latra.entity;

import trungdevcode.latra.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "brands")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Brand extends BaseEntity {

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "country", length = 50)
    private String country;

    @Column(name = "status")
    private Integer status = 1;
    private String logo;
}