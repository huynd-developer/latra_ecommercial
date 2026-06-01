package trungdevcode.latra.modules.user.controller;

import lombok.RequiredArgsConstructor;
import trungdevcode.latra.modules.user.dto.CustomerDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import trungdevcode.latra.entity.User;
import trungdevcode.latra.repository.UserRepository;
import trungdevcode.latra.modules.user.service.CustomerService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/khachHangs")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('CASHIER', 'MANAGER', 'OWNER')")
public class CustomerController {

    private final CustomerService customerService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<CustomerDTO>> getCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @PostMapping
    public ResponseEntity<?> addCustomer(@RequestBody CustomerDTO dto) {
        try {
            // ĐÃ FIX: Thêm check trùng Username để tránh lỗi 500 từ Database
            if (dto.getUsername() != null) {
                // Lưu ý: Đảm bảo trong UserRepository mày có hàm findByUsername(String username) nhé
                User exUsername = userRepository.findByUsername(dto.getUsername().trim());
                if (exUsername != null) return ResponseEntity.badRequest().body(Map.of("message", "Tên đăng nhập này đã tồn tại!"));
            }

            if (dto.getEmail() != null) {
                User exEmail = userRepository.findByEmail(dto.getEmail().trim());
                if (exEmail != null) return ResponseEntity.badRequest().body(Map.of("message", "Email này đã được đăng ký!"));
            }

            if (dto.getPhone() != null) {
                User exPhone = userRepository.findByPhone(dto.getPhone().trim());
                if (exPhone != null) return ResponseEntity.badRequest().body(Map.of("message", "Số điện thoại này đã được đăng ký!"));
            }

            customerService.addCustomer(dto);
            return ResponseEntity.ok(Map.of("message", "Thêm thành công!"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("message", "Lỗi server: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCustomer(@PathVariable Long id, @RequestBody CustomerDTO dto) {
        try {
            if (dto.getEmail() != null) {
                User exEmail = userRepository.findByEmail(dto.getEmail().trim());
                if (exEmail != null && !exEmail.getId().equals(id)) {
                    return ResponseEntity.badRequest().body(Map.of("message", "Email đã được sử dụng bởi khách hàng khác!"));
                }
            }

            if (dto.getPhone() != null) {
                User exPhone = userRepository.findByPhone(dto.getPhone().trim());
                if (exPhone != null && !exPhone.getId().equals(id)) {
                    return ResponseEntity.badRequest().body(Map.of("message", "Số điện thoại đã được sử dụng bởi khách hàng khác!"));
                }
            }

            customerService.updateCustomer(id, dto);
            return ResponseEntity.ok(Map.of("message", "Cập nhật thành công!"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("message", "Lỗi server: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('MANAGER', 'OWNER')")
    public ResponseEntity<?> deleteCustomer(@PathVariable Long id) {
        try {
            customerService.deleteCustomer(id);
            return ResponseEntity.ok(Map.of("message", "Xóa thành công!"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("message", "Lỗi server: " + e.getMessage()));
        }
    }
}