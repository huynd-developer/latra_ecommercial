package trungdevcode.latra.modules.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import trungdevcode.latra.common.config.JwtTokenProvider;
import trungdevcode.latra.common.exception.BusinessException;
import trungdevcode.latra.entity.User;
import trungdevcode.latra.modules.auth.dto.AuthResponse;
import trungdevcode.latra.modules.auth.dto.LoginRequest;
import trungdevcode.latra.modules.auth.dto.RegisterRequest;
import trungdevcode.latra.repository.UserRepository;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final StringRedisTemplate redisTemplate;

    public AuthResponse login(LoginRequest request) {
        String badCredentialsMessage = "Tên đăng nhập, email, số điện thoại hoặc mật khẩu không chính xác.";

        User user = userRepository.findByLoginId(request.getLoginId())
                .orElseThrow(() -> new BusinessException(401, badCredentialsMessage));

        if (user.getStatus() != null && user.getStatus() == 2) {
            throw new BusinessException(401, "Tài khoản không tồn tại hoặc đã bị vô hiệu hóa.");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(401, badCredentialsMessage);
        }

        // ĐÃ XÓA ĐOẠN GHI ĐÈ ROLE Ở ĐÂY.
        // Lúc này user.getRole() vẫn giữ nguyên giá trị "OWNER" từ Database.

        String token = jwtTokenProvider.generateToken(user);

        return AuthResponse.builder()
                .accessToken(token)
                .username(user.getUsername())
                .role(user.getRole())
                .build();
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // FIX: Đã sửa lại dùng hàm check trùng cơ bản giống file UserRepository của bạn
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("Tên đăng nhập đã tồn tại! Vui lòng chọn tên khác.");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email này đã được sử dụng!");
        }
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new BusinessException(400, "Số điện thoại đã được sử dụng!");
        }

        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setEmail(request.getEmail());
        newUser.setPhone(request.getPhone());
        newUser.setFullName(request.getFullName());
        newUser.setStatus(1);
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));

        User savedUser = userRepository.save(newUser);

        // Gán quyền vào bảng liên kết
        userRepository.assignCustomerRole(savedUser.getId());

        // Nạp chữ quyền vào bộ nhớ để sinh Token ngay lập tức không bị lỗi quyền trống
        savedUser.setRole("USER");

        String token = jwtTokenProvider.generateToken(savedUser);

        return AuthResponse.builder()
                .accessToken(token)
                .username(savedUser.getUsername())
                .build();
    }

    public void logout(String token) {
        String blacklistKey = "blacklist:" + token;
        redisTemplate.opsForValue().set(blacklistKey, "true", 1, TimeUnit.DAYS);
    }

    // Hàm phụ trợ hỗ trợ dịch nhanh Role từ ID sang String
    private String mapRoleIdToName(Long roleId) {
        if (roleId == null) return "USER";
        if (roleId == 1) return "OWNER";
        if (roleId == 2) return "MANAGER";
        if (roleId == 3) return "CASHIER";
        return "USER";
    }
}