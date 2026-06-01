package trungdevcode.latra.modules.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import trungdevcode.latra.modules.user.dto.EmployeeDTO;
import trungdevcode.latra.entity.User;
import trungdevcode.latra.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<EmployeeDTO> getAllEmployees() {
        return userRepository.getAllEmployees();
    }

    @Transactional
    public void addEmployee(EmployeeDTO dto) {
        validateEmployeeData(dto, false);

        if(userRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("Mã nhân viên (Username) đã tồn tại!");
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email này đã được sử dụng!");
        }
        if (userRepository.existsByPhone(dto.getPhone())) {
            throw new RuntimeException("Số điện thoại này đã được sử dụng!");
        }

        User user = new User();
        user.setUsername(dto.getUsername().trim());
        user.setFullName(dto.getFullName().trim());
        user.setPhone(dto.getPhone().trim());
        user.setEmail(dto.getEmail().trim());
        user.setAddress(dto.getAddress());
        user.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);

        // 🔥 FIX LOGIC 1: Ép chuỗi Role vào bảng users dựa theo RoleId để câu lệnh SELECT cũ không bị NULL
        String roleStr = "CASHIER"; // Mặc định là Thu ngân nếu không truyền
        if (dto.getRoleId() != null) {
            if (dto.getRoleId() == 1L) roleStr = "OWNER";
            else if (dto.getRoleId() == 2L) roleStr = "MANAGER";
            else if (dto.getRoleId() == 3L) roleStr = "CASHIER";
        }
        user.setRole(roleStr);

        user.setPassword(passwordEncoder.encode("123456"));

        User savedUser = userRepository.save(user);

        // 🔥 FIX LOGIC 2: Bắn data vào bảng trung gian user_roles đồng bộ với ID
        Long finalRoleId = dto.getRoleId() != null ? dto.getRoleId() : 3L;
        userRepository.assignRoleToUser(savedUser.getId(), finalRoleId);
    }

    @Transactional
    public void updateEmployee(Long id, EmployeeDTO dto) {
        if (id == 1L) {
            throw new RuntimeException("Lỗi bảo mật: Bất khả xâm phạm! Không thể sửa thông tin của Super Admin.");
        }

        validateEmployeeData(dto, true);

        if (userRepository.existsByEmailAndIdNot(dto.getEmail(), id)) {
            throw new RuntimeException("Email này đã bị người khác sử dụng!");
        }
        if (userRepository.existsByPhoneAndIdNot(dto.getPhone(), id)) {
            throw new RuntimeException("Số điện thoại này đã bị người khác sử dụng!");
        }

        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên!"));

        user.setFullName(dto.getFullName().trim());
        user.setPhone(dto.getPhone().trim());
        user.setEmail(dto.getEmail().trim());
        user.setAddress(dto.getAddress());
        if (dto.getStatus() != null) user.setStatus(dto.getStatus());

        // 🔥 FIX LOGIC 3: Cập nhật lại chuỗi Role ở bảng users khi nhân viên được đổi chức vụ (Sửa lỗi lệch data)
        if (dto.getRoleId() != null) {
            String roleStr = "CASHIER";
            if (dto.getRoleId() == 1L) roleStr = "OWNER";
            else if (dto.getRoleId() == 2L) roleStr = "MANAGER";
            else if (dto.getRoleId() == 3L) roleStr = "CASHIER";

            user.setRole(roleStr); // Lưu lại chuỗi mới vào bảng users
            userRepository.updateUserRole(id, dto.getRoleId()); // Cập nhật bảng user_roles
        }

        userRepository.save(user);
    }

    @Transactional
    public void deleteEmployee(Long id) {
        if (id == 1L) {
            throw new RuntimeException("Lỗi bảo mật: Bất khả xâm phạm! Không thể xóa tài khoản Super Admin.");
        }

        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên!"));

        user.setStatus(2);
        userRepository.save(user);
    }

    private void validateEmployeeData(EmployeeDTO dto, boolean isUpdate) {
        if (!isUpdate) {
            if (dto.getUsername() == null || !dto.getUsername().trim().matches("^[a-zA-Z0-9_]{3,20}$"))
                throw new RuntimeException("Mã NV phải từ 3-20 ký tự, không dấu cách!");
        }
        if (dto.getFullName() == null || !dto.getFullName().trim().matches("^[\\p{L}\\s]+$"))
            throw new RuntimeException("Họ tên không được chứa số hoặc ký tự đặc biệt!");
        if (dto.getPhone() == null || !dto.getPhone().trim().matches("^(0[3|5|7|8|9])[0-9]{8}$"))
            throw new RuntimeException("SĐT VN 10 số không hợp lệ!");
        if (dto.getEmail() == null || !dto.getEmail().trim().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[a-zA-Z]{2,}$"))
            throw new RuntimeException("Email không hợp lệ!");
    }
}