package trungdevcode.latra.modules.promotion.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import trungdevcode.latra.common.response.ApiResponse;
import trungdevcode.latra.modules.promotion.dto.request.FlashSaleRequestDTO;
import trungdevcode.latra.entity.FlashSale;
import trungdevcode.latra.modules.promotion.service.FlashSaleService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/flash-sales/admin")
@RequiredArgsConstructor
public class FlashSaleController {

    private final FlashSaleService flashSaleService;

    @PostMapping
    public ResponseEntity<ApiResponse<FlashSale>> createFlashSale(@Valid @RequestBody FlashSaleRequestDTO request) {
        return ResponseEntity.ok(ApiResponse.success(flashSaleService.createFlashSale(request), "Tạo Flash Sale thành công"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<FlashSale>>> getAllFlashSales() {
        return ResponseEntity.ok(ApiResponse.success(flashSaleService.getAllFlashSales(), "Lấy danh sách Flash Sale thành công"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FlashSale>> updateFlashSale(
            @PathVariable Long id,
            @Valid @RequestBody FlashSaleRequestDTO request) {
        return ResponseEntity.ok(ApiResponse.success(flashSaleService.updateFlashSale(id, request), "Cập nhật Flash Sale thành công"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteFlashSale(@PathVariable Long id) {
        flashSaleService.deleteFlashSale(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Đã hủy chương trình Flash Sale thành công!"));
    }
}