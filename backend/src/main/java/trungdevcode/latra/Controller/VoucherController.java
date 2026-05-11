package trungdevcode.latra.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import trungdevcode.latra.Dto.VoucherRequestDTO;
import trungdevcode.latra.entity.Voucher;
import trungdevcode.latra.Service.VoucherService;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/vouchers/admin")
@RequiredArgsConstructor // 🔥 CHUẨN MỚI: Tự động tiêm Service, bỏ @Autowired
@CrossOrigin(origins = "http://localhost:5173") // 🔥 Giữ nguyên cái này để Vue gọi
public class VoucherController {

    private final VoucherService voucherService; // 🔥 Thêm final, bỏ @Autowired

    @GetMapping
    public ResponseEntity<List<Voucher>> getAllVouchers() {
        return ResponseEntity.ok(voucherService.getAllVouchers());
    }

    @PostMapping
    public ResponseEntity<Voucher> createVoucher(@Valid @RequestBody VoucherRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(voucherService.createVoucher(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Voucher> updateVoucher(
            @PathVariable("id") Long id, // 🔥 FIX LỖI 400
            @Valid @RequestBody VoucherRequestDTO request) {
        return ResponseEntity.ok(voucherService.updateVoucher(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteVoucher(
            @PathVariable("id") Long id) { // 🔥 FIX LỖI 400
        voucherService.deleteVoucher(id);
        return ResponseEntity.ok("Đã xóa mã Voucher thành công!");
    }

    // 🔥 ĐOẠN NÀY LÀ MỚI THÊM: Cổng API cho nút "Áp dụng" bên POS
    @GetMapping("/check")
    public ResponseEntity<?> checkVoucher(
            @RequestParam("code") String code,   // 🔥 FIX LỖI 400: Thêm "code"
            @RequestParam("total") BigDecimal total) { // 🔥 FIX LỖI 400: Thêm "total"

        // 🔥 Đã dọn sạch try-catch, ném lỗi thẳng cho GlobalExceptionHandler lo!
        return ResponseEntity.ok(voucherService.checkVoucher(code, total));
    }
}