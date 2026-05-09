package trungdevcode.latra.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import trungdevcode.latra.Service.DashboardService;

@RestController
@RequestMapping("/api/v1/dashboard")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor // Tối ưu: Dùng cái này thay cho @Autowired
public class DashboardController {

    private final DashboardService dashboardService; // Thêm chữ final, bỏ @Autowired

    @GetMapping
    public ResponseEntity<?> getDashboard(
            // SỬA LỖI CHÍNH Ở ĐÂY 👇: Khai báo rõ value = "timeRange"
            @RequestParam(value = "timeRange", defaultValue = "7days") String timeRange) {
        return ResponseEntity.ok(dashboardService.getDashboardData(timeRange));
    }
}