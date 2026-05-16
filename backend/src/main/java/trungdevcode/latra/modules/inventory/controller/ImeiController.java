// backend/src/main/java/trungdevcode/latra/modules/inventory/controller/ImeiController.java
package trungdevcode.latra.modules.inventory.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import trungdevcode.latra.common.response.ApiResponse;
import trungdevcode.latra.modules.inventory.dto.request.ImeiImportRequestDTO;
import trungdevcode.latra.modules.inventory.dto.response.ImeiResponseDTO;
import trungdevcode.latra.modules.inventory.service.ImeiService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/imeis/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173") // Lưu ý cấu hình CORS toàn cục sau này
public class ImeiController {

    private final ImeiService imeiService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ImeiResponseDTO>>> getAllImeis() {
        return ResponseEntity.ok(ApiResponse.success(imeiService.findAllImeis(), "Lấy danh sách IMEI thành công"));
    }

    @PostMapping("/import")
    public ResponseEntity<ApiResponse<String>> importImeis(@Valid @RequestBody ImeiImportRequestDTO request) {
        imeiService.addImeisToVariant(request.getVariantId(), request.getImeis());
        return ResponseEntity.ok(ApiResponse.success(null, "Nhập kho thành công " + request.getImeis().size() + " thiết bị!"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteImei(@PathVariable Long id) {
        imeiService.deleteImei(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Đã xóa mã IMEI khỏi hệ thống!"));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<String>> changeImeiStatus(
            @PathVariable Long id,
            @RequestParam("status") trungdevcode.latra.enums.ImeiStatus newStatus) {
        imeiService.changeImeiStatus(id, newStatus);
        return ResponseEntity.ok(ApiResponse.success(null, "Đã cập nhật trạng thái thiết bị thành: " + newStatus.name()));
    }
}