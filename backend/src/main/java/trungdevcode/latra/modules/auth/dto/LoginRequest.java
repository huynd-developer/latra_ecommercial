package trungdevcode.latra.modules.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "Tài khoản đăng nhập không được để trống")
    private String loginId;

    @NotBlank(message = "Mật khẩu không được để trống")
    private String password;
}