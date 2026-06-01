package trungdevcode.latra.modules.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import trungdevcode.latra.modules.user.dto.EmployeeDTO;
import trungdevcode.latra.modules.user.service.EmployeeService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/nhanViens")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('OWNER')")
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    public ResponseEntity<List<EmployeeDTO>> getAll() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    @PostMapping
    public ResponseEntity<String> add(@RequestBody EmployeeDTO dto) {
        employeeService.addEmployee(dto);
        return ResponseEntity.ok("Thêm nhân viên thành công!");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> update(
            @PathVariable("id") Long id,
            @RequestBody EmployeeDTO dto) {
        employeeService.updateEmployee(id, dto);
        return ResponseEntity.ok("Cập nhật thông tin nhân viên thành công!");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") Long id) {
        employeeService.deleteEmployee(id);
        // ĐÃ FIX: Sửa lại chữ cho đúng bản chất Soft Delete bên dưới Service
        return ResponseEntity.ok("Đã chuyển trạng thái nhân viên thành ngừng hoạt động!");
    }
}