package trungdevcode.latra.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Nationalized;

import java.sql.Types;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    @JdbcTypeCode(Types.VARCHAR)
    private String username;

    @Column(nullable = false, length = 100)
    @Nationalized
    private String fullName;

    @Column(nullable = false, length = 150, unique = true)
    @JdbcTypeCode(Types.VARCHAR)
    private String email;

    @Column(nullable = false, length = 64)
    @JdbcTypeCode(Types.VARCHAR)
    private String password;

    @Column(length = 15, unique = true)
    @JdbcTypeCode(Types.VARCHAR)
    private String phone;

    @Nationalized
    @Column(length = 255)
    private String address;

    @JdbcTypeCode(Types.TINYINT)
    @Column(columnDefinition = "tinyint default 1")
    private Integer status;

    // ĐÃ THÊM CỘT ROLE KHỚP VỚI DATABASE
    @Column(length = 20, nullable = false)
    @JdbcTypeCode(Types.VARCHAR)
    private String role = "USER";

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;
}