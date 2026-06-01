package trungdevcode.latra.modules.dashboard.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import trungdevcode.latra.modules.dashboard.dto.response.ShiftSummaryDTO;
import trungdevcode.latra.repository.OrderRepository;
import trungdevcode.latra.repository.UserRepository;
import trungdevcode.latra.entity.User;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/shifts")
@CrossOrigin(origins = "http://localhost:5173")
public class ShiftController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/summary")
    @PreAuthorize("hasAnyAuthority('CASHIER', 'MANAGER', 'OWNER')") // Thay đổi ở đây
    public ResponseEntity<ShiftSummaryDTO> getShiftSummary(@RequestParam Long userId) {
        ShiftSummaryDTO summary = new ShiftSummaryDTO();

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            summary.setStaffName(userOpt.get().getFullName() + " (" + userOpt.get().getUsername() + ")");
        } else {
            summary.setStaffName("Unknown Staff");
        }

        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        summary.setStartTime("08:00 - " + today);

        int totalOrders = orderRepository.countOrdersTodayByUserId(userId);
        BigDecimal totalRevenue = orderRepository.sumRevenueTodayByUserId(userId);

        summary.setTotalOrders(totalOrders);
        summary.setTotalRevenue(totalRevenue != null ? totalRevenue : BigDecimal.ZERO);

        return ResponseEntity.ok(summary);
    }
}