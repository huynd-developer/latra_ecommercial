package trungdevcode.latra.modules.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import trungdevcode.latra.common.response.ApiResponse;
import trungdevcode.latra.modules.auth.dto.AuthResponse;
import trungdevcode.latra.modules.auth.dto.LoginRequest;
import trungdevcode.latra.modules.auth.dto.RegisterRequest;
import trungdevcode.latra.modules.auth.service.AuthService;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Đăng nhập thành công!"));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Đăng ký tài khoản thành công!"));
    }

    /**
     * API Đăng xuất: Nhận Token từ Header và ném vào Redis Blacklist
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(HttpServletRequest request) {
        // Lấy token từ header của request gửi lên
        String token = getJwtFromRequest(request);

        if (StringUtils.hasText(token)) {
            // Gọi qua Service để ném Token vào Redis
            authService.logout(token);
            return ResponseEntity.ok(ApiResponse.success(null, "Đăng xuất thành công, token đã bị vô hiệu hóa!"));
        }

        // Trả về mặc định nếu người dùng gửi request không có token
        return ResponseEntity.ok(ApiResponse.success(null, "Không tìm thấy phiên đăng nhập để đăng xuất."));
    }

    /**
     * Hàm phụ trợ bóc tách chuỗi Token nguyên bản từ chữ "Bearer "
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        // Kiểm tra xem header Authorization có chứa thông tin jwt không
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Cắt bỏ 7 ký tự "Bearer " để lấy đúng chuỗi token
        }
        return null;
    }
}