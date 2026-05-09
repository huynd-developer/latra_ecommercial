package trungdevcode.latra.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import trungdevcode.latra.Dto.EmployeeDTO;
import trungdevcode.latra.Service.EmployeeService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/nhanViens")
@CrossOrigin(origins = "http://localhost:5173") // 🔥 THÊM CORS ĐỂ VUE.JS GỌI ĐƯỢC
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    public ResponseEntity<List<EmployeeDTO>> getAll() {
        // 🔥 SỬA CHUẨN KIẾN TRÚC: Phải gọi hàm của Service, không gọi tắt qua Repository
        // LƯU Ý: Nếu trong EmployeeService chưa có hàm này, ông nhớ tạo thêm nhé!
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    @PostMapping
    public ResponseEntity<String> add(@RequestBody EmployeeDTO dto) {
        // 🔥 Đã xóa try-catch, GlobalExceptionHandler sẽ tự bắt lỗi ném về Vue
        employeeService.addEmployee(dto);
        return ResponseEntity.ok("Thêm nhân viên thành công!");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> update(
            @PathVariable("id") Long id, // 🔥 FIX LỖI 400: Thêm "id" vào ngoặc
            @RequestBody EmployeeDTO dto) {
        employeeService.updateEmployee(id, dto);
        return ResponseEntity.ok("Cập nhật thành công!");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") Long id) { // 🔥 FIX LỖI 400: Thêm "id" vào ngoặc
        employeeService.deleteEmployee(id);
        return ResponseEntity.ok("Xóa nhân viên vĩnh viễn thành công!");
    }
}