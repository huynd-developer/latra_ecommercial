package trungdevcode.latra.common.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final StringRedisTemplate redisTemplate; // <-- TÍCH HỢP REDIS QUẢN LÝ BLACKLIST

    // Bỏ qua lọc với các API Đăng ký / Đăng nhập để tránh vòng lặp lỗi 401
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        return path.startsWith("/api/v1/auth/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            // 1. Lấy token từ request
            String jwt = getJwtFromRequest(request);

            if (StringUtils.hasText(jwt)) {

                // ================= LAYER 3: GÁC CỔNG REDIS =================
                // Kiểm tra xem Token này có nằm trong danh sách đen (bị khóa/đăng xuất) không
                String blacklistKey = "blacklist:" + jwt;
                if (Boolean.TRUE.equals(redisTemplate.hasKey(blacklistKey))) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"code\": 401, \"message\": \"Token đã bị thu hồi hoặc tài khoản đã bị khóa!\"}");
                    return; // Chặn đứng tại đây, KHÔNG CHO ĐI TIẾP VÀO DATABASE
                }

                // 2. Nếu token hợp lệ về mặt chữ ký và thời gian
                if (jwtTokenProvider.validateToken(jwt)) {

                    // 3. Lấy username từ token
                    String username = jwtTokenProvider.getUsernameFromToken(jwt);

                    // ================= LAYER 1: PHÂN QUYỀN RBAC =================
                    // 4. Lấy danh sách quyền THẬT SỰ từ Token ra
                    List<GrantedAuthority> authorities = jwtTokenProvider.getAuthoritiesFromToken(jwt);

                    // 5. Set thông tin người dùng và quyền vào SecurityContext
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            username, null, authorities);

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception ex) {
            logger.error("Không thể thiết lập xác thực người dùng trong Security Context", ex);
        }

        // 6. Cho phép request đi tiếp
        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        // Kiểm tra xem header Authorization có chứa thông tin jwt không
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}