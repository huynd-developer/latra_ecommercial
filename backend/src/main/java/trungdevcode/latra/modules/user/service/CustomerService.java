package trungdevcode.latra.modules.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import trungdevcode.latra.modules.user.dto.CustomerDTO;
import trungdevcode.latra.entity.User;
import trungdevcode.latra.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final UserRepository userRepository;

    // ĐÃ FIX: Inject PasswordEncoder để băm mật khẩu
    private final PasswordEncoder passwordEncoder;

    public List<CustomerDTO> getAllCustomers() {
        return userRepository.findAllCustomersOnly().stream()
                .filter(u -> u.getStatus() == null || u.getStatus() != 2)
                .map(u -> {
                    CustomerDTO dto = new CustomerDTO();
                    dto.setId(u.getId());
                    dto.setUsername(u.getUsername());
                    dto.setName(u.getFullName());
                    dto.setPhone(u.getPhone());
                    dto.setEmail(u.getEmail());
                    dto.setAddress(u.getAddress());
                    dto.setStatus(u.getStatus() != null ? u.getStatus() : 1);
                    return dto;
                })
                .toList();
    }

    @Transactional
    public void addCustomer(CustomerDTO dto) {
        validateCustomerData(dto, false);

        User user = new User();
        user.setUsername(dto.getUsername().trim());

        // ĐÃ FIX: Mã hóa mật khẩu mặc định trước khi lưu vào DB
        user.setPassword(passwordEncoder.encode("123456"));

        user.setFullName(dto.getName().trim());
        user.setEmail(dto.getEmail().trim());
        user.setPhone(dto.getPhone().trim());
        user.setAddress(dto.getAddress());
        user.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);

        User savedUser = userRepository.save(user);
        userRepository.assignCustomerRole(savedUser.getId());
    }

    @Transactional
    public void updateCustomer(Long id, CustomerDTO dto) {
        validateCustomerData(dto, true);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng!"));

        user.setFullName(dto.getName().trim());
        user.setPhone(dto.getPhone().trim());
        user.setEmail(dto.getEmail().trim());

        if (dto.getAddress() != null) user.setAddress(dto.getAddress());
        if (dto.getStatus() != null) user.setStatus(dto.getStatus());

        userRepository.save(user);
    }

    @Transactional
    public void deleteCustomer(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng!"));
        user.setStatus(2); // Soft Delete
        userRepository.save(user);
    }

    private void validateCustomerData(CustomerDTO dto, boolean isUpdate) {
        if (!isUpdate) {
            if (dto.getUsername() == null || !dto.getUsername().trim().matches("^[a-zA-Z0-9_]{4,20}$")) {
                throw new RuntimeException("Username từ 4-20 ký tự, không chứa dấu cách hoặc ký tự đặc biệt!");
            }
        }

        if (dto.getName() == null || !dto.getName().trim().matches("^[\\p{L}\\s]+$")) {
            throw new RuntimeException("Họ tên không được chứa số hoặc ký tự đặc biệt!");
        }

        if (dto.getPhone() == null || !dto.getPhone().trim().matches("^0[35789][0-9]{8}$")) {
            throw new RuntimeException("Số điện thoại Việt Nam không hợp lệ (Phải gồm 10 số, bắt đầu bằng 03, 05, 07, 08, 09)!");
        }

        if (dto.getEmail() == null || !dto.getEmail().trim().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            throw new RuntimeException("Email không đúng định dạng (Ví dụ: nguyenvana@gmail.com)!");
        }
    }
}