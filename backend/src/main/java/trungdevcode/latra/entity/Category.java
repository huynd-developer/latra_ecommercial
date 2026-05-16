// backend/src/main/java/trungdevcode/latra/entity/Category.java
package trungdevcode.latra.entity;

import trungdevcode.latra.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Category extends BaseEntity {

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(name = "status", nullable = false)
    private Integer status = 1;
}