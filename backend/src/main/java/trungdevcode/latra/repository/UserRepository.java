package trungdevcode.latra.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import trungdevcode.latra.modules.user.dto.EmployeeDTO;
import trungdevcode.latra.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);
    User findByPhone(String phone);
    User findByUsername(String username);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
    boolean existsByEmailAndIdNot(String email, Long id);
    boolean existsByPhoneAndIdNot(String phone, Long id);

    @Query(value = "SELECT * FROM users WHERE (role = 'USER' OR role IS NULL) AND status != 2 ORDER BY id DESC", nativeQuery = true)
    List<User> findAllCustomersOnly();

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO user_roles (user_id, role_id) VALUES (:userId, 4)", nativeQuery = true)
    void assignCustomerRole(@Param("userId") Long userId);

    // FIX: Bỏ ADMIN, chỉ quét đúng 3 role nhân viên thực tế của bạn
    @Query(value = "SELECT * FROM users WHERE role IN ('OWNER', 'MANAGER', 'CASHIER') AND status != 2 ORDER BY id ASC", nativeQuery = true)
    List<User> findAllEmployeesOnly();

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO user_roles (user_id, role_id) VALUES (:userId, :roleId)", nativeQuery = true)
    void assignRoleToUser(@Param("userId") Long userId, @Param("roleId") Long roleId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE user_roles SET role_id = :roleId WHERE user_id = :userId", nativeQuery = true)
    void updateUserRole(@Param("userId") Long userId, @Param("roleId") Long roleId);

    @Query(value = "SELECT TOP 1 role_id FROM user_roles WHERE user_id = :userId", nativeQuery = true)
    Long findRoleIdByUserId(@Param("userId") Long userId);

    default List<EmployeeDTO> getAllEmployees() {
        List<User> users = findAllEmployeesOnly();

        return users.stream().map(u -> {
            EmployeeDTO dto = new EmployeeDTO();
            dto.setId(u.getId());
            dto.setUsername(u.getUsername());
            dto.setFullName(u.getFullName());
            dto.setPhone(u.getPhone());
            dto.setEmail(u.getEmail());
            dto.setAddress(u.getAddress());
            dto.setStatus(u.getStatus() != null ? u.getStatus() : 1);

            // FIX LOGIC: Chỉ map đúng 3 role thực tế
            String currentRole = u.getRole();
            Long roleId = 3L; // Mặc định không khớp thì là CASHIER

            if (currentRole != null) {
                switch (currentRole.toUpperCase()) {
                    case "OWNER":
                        roleId = 1L;
                        break;
                    case "MANAGER":
                        roleId = 2L;
                        break;
                    case "CASHIER":
                        roleId = 3L;
                        break;
                }
            }
            dto.setRoleId(roleId);

            return dto;
        }).collect(Collectors.toList());
    }

    @Query("SELECT u FROM User u WHERE u.username = :loginId OR u.email = :loginId OR u.phone = :loginId")
    Optional<User> findByLoginId(@Param("loginId") String loginId);
}