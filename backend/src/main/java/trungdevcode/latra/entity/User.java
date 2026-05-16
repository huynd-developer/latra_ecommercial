// backend/src/main/java/trungdevcode/latra/entity/User.java
package trungdevcode.latra.entity;

import lombok.EqualsAndHashCode;
import trungdevcode.latra.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Nationalized;

@Entity
@Table(name = "users")
@Data
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    private String email;
    private String phone;

    @Nationalized
    @Column(name = "full_name")
    private String fullName;

    @Nationalized
    private String address;

    private Integer status;
}