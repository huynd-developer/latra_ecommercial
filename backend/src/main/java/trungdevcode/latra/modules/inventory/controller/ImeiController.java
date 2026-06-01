package trungdevcode.latra.modules.inventory.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import trungdevcode.latra.common.response.ApiResponse;
import trungdevcode.latra.modules.inventory.dto.request.ImeiImportRequestDTO;
import trungdevcode.latra.modules.inventory.dto.response.ImeiResponseDTO;
import trungdevcode.latra.modules.inventory.service.ImeiService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/imeis")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class ImeiController {

    private final ImeiService imeiService;

    // 🛠️ ĐÃ SỬA: Gộp chung thành 1 endpoint GET gốc, bỏ hẳn cái đuôi /admin thừa thãi
    @GetMapping
    @PreAuthorize("hasAnyAuthority('CASHIER', 'MANAGER', 'OWNER')")
    public ResponseEntity<ApiResponse<List<ImeiResponseDTO>>> getAllImeis() {
        return ResponseEntity.ok(ApiResponse.success(imeiService.findAllImeis(), "Lấy danh sách IMEI thành công"));
    }

    // 🛠️ ĐÃ SỬA: Xóa chữ /admin. URL giờ thành POST /api/v1/imeis/import
    @PostMapping("/import")
    @PreAuthorize("hasAnyAuthority('MANAGER', 'OWNER')")
    public ResponseEntity<ApiResponse<String>> importImeis(@Valid @RequestBody ImeiImportRequestDTO request) {
        imeiService.addImeisToVariant(request.getVariantId(), request.getImeis());
        return ResponseEntity.ok(ApiResponse.success(null, "Nhập kho thành công " + request.getImeis().size() + " thiết bị!"));
    }

    // 🛠️ ĐÃ SỬA: Xóa chữ /admin. URL giờ thành DELETE /api/v1/imeis/{id}
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('OWNER')")
    public ResponseEntity<ApiResponse<String>> deleteImei(@PathVariable Long id) {
        imeiService.deleteImei(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Đã xóa mã IMEI khỏi hệ thống!"));
    }

    // 🛠️ ĐÃ SỬA: Xóa chữ /admin. URL giờ thành PUT /api/v1/imeis/{id}/status
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyAuthority('CASHIER', 'MANAGER', 'OWNER')")
    public ResponseEntity<ApiResponse<String>> changeImeiStatus(
            @PathVariable Long id,
            @RequestParam("status") trungdevcode.latra.enums.ImeiStatus newStatus) {
        imeiService.changeImeiStatus(id, newStatus);
        return ResponseEntity.ok(ApiResponse.success(null, "Đã cập nhật trạng thái thiết bị thành: " + newStatus.name()));
    }

    // Cái này đã chuẩn RESTful ngay từ đầu rồi, giữ nguyên!
    @GetMapping("/product/{productId}/available")
    @PreAuthorize("hasAnyAuthority('CASHIER', 'MANAGER', 'OWNER')")
    public ResponseEntity<ApiResponse<List<ImeiResponseDTO>>> getAvailableImeisByProduct(
            @PathVariable("productId") Long productId) {
        List<ImeiResponseDTO> availableImeis = imeiService.getAvailableImeisByProductId(productId);
        return ResponseEntity.ok(ApiResponse.success(availableImeis, "Lấy danh sách IMEI sẵn sàng thành công"));
    }
}