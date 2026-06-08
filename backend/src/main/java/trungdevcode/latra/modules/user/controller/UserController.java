package trungdevcode.latra.modules.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import trungdevcode.latra.common.response.ApiResponse;
import trungdevcode.latra.modules.user.service.UserService;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    // 🌟 CHUẨN THỰC TẾ: Controller gọi Service, tuyệt đối không gọi Repository
    private final UserService userService;

    /**
     * API lấy thông tin profile an toàn của chính người dùng đang đăng nhập
     * URL: GET http://localhost:8080/api/v1/users/me
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUserProfile() {
        try {
            // 1. Bóc tách người dùng đang gọi API từ JWT Token
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String username;

            if (principal instanceof UserDetails) {
                username = ((UserDetails) principal).getUsername();
            } else {
                username = principal.toString();
            }

            // 2. Giao toàn bộ việc tìm kiếm và đóng gói dữ liệu cho tầng Service xử lý
            Map<String, Object> safeUserProfile = userService.getCurrentUserProfile(username);

            // 3. Controller chỉ làm đúng 1 việc cuối cùng: Trả JSON về cho Frontend
            return ResponseEntity.ok(ApiResponse.success(safeUserProfile, "Lấy thông tin tài khoản thành công!"));

        } catch (RuntimeException e) {
            // Nếu không tìm thấy user, Service sẽ ném lỗi ra đây
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("message", "Lỗi hệ thống: " + e.getMessage()));
        }
    }
}