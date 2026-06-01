package trungdevcode.latra.common.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import trungdevcode.latra.entity.User;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        // Mặc định là "USER" thay vì "ROLE_USER"
        String role = "USER";
        if (user.getRole() != null) {
            // Trim để xóa khoảng trắng thừa, ToUpperCase cho chuẩn đồng bộ
            role = user.getRole().trim().toUpperCase();
        }

        return Jwts.builder()
                .subject(user.getUsername())
                .claim("userId", user.getId())
                .claim("email", user.getEmail())
                // TRUYỀN THẲNG tên Role (VD: "OWNER", "MANAGER") vào token, không chèn thêm gì cả!
                .claim("roles", role)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public List<GrantedAuthority> getAuthoritiesFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        String role = claims.get("roles", String.class);
        if (role == null) {
            role = "USER"; // Xóa chữ ROLE_ ở phần dự phòng
        }

        // Cấp quyền cho Spring Security bằng Role tinh khiết ("OWNER", "MANAGER")
        return List.of(new SimpleGrantedAuthority(role.toUpperCase()));
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException ex) {
            System.out.println("Token đã hết hạn: " + ex.getMessage());
        } catch (JwtException | IllegalArgumentException ex) {
            System.out.println("Token không hợp lệ hoặc bị lỗi: " + ex.getMessage());
        }
        return false;
    }
}