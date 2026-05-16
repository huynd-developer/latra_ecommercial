// backend/src/main/java/trungdevcode/latra/common/config/WebConfig.java
package trungdevcode.latra.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                // Cho phép cả port 5173 (Vue/Vite) và 3000 (React) nếu bạn chạy nhiều FE
                .allowedOrigins("http://localhost:5173", "http://127.0.0.1:5173", "http://localhost:3000")
                // THÊM OPTIONS VÀ PATCH ĐỂ TRÁNH LỖI PREFLIGHT
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*") // Cho phép mọi header (Token, Content-Type...)
                .allowCredentials(true);
    }
}