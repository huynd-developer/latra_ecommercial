package trungdevcode.latra.modules.user.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import trungdevcode.latra.entity.User;
import trungdevcode.latra.modules.user.service.UserService;
import trungdevcode.latra.repository.UserRepository;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public Map<String, Object> getCurrentUserProfile(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("Không tìm thấy thông tin tài khoản người dùng!");
        }

        // Đóng gói dữ liệu an toàn trả về cho Frontend điền form
        Map<String, Object> safeProfile = new HashMap<>();
        safeProfile.put("id", user.getId());
        safeProfile.put("fullName", user.getFullName());
        safeProfile.put("email", user.getEmail() != null ? user.getEmail() : "");
        safeProfile.put("phone", user.getPhone() != null ? user.getPhone() : "");
        safeProfile.put("address", user.getAddress() != null ? user.getAddress() : "");

        return safeProfile;
    }
}