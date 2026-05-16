// backend/src/main/java/trungdevcode/latra/modules/product/controller/ProductController.java
package trungdevcode.latra.modules.product.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import trungdevcode.latra.common.response.ApiResponse;
import trungdevcode.latra.modules.product.dto.request.ProductRequest;
import trungdevcode.latra.modules.product.dto.response.ProductResponse;
import trungdevcode.latra.modules.product.service.ProductService;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductDetail(@PathVariable("id") Long id) {
        return ResponseEntity.ok(ApiResponse.success(productService.getProductById(id), "Lấy chi tiết sản phẩm thành công"));
    }

    @PostMapping("/admin")
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(@Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(ApiResponse.success(productService.createProduct(request), "Tạo sản phẩm thành công"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAllProducts(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(ApiResponse.success(productService.getAllProducts(page, size), "Lấy danh sách sản phẩm thành công"));
    }

    @PutMapping("/admin/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable("id") Long id,
            @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(ApiResponse.success(productService.updateProduct(id, request), "Cập nhật sản phẩm thành công"));
    }

    @DeleteMapping("/admin/{id}")
    public ResponseEntity<ApiResponse<String>> deleteProduct(@PathVariable("id") Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Đã ẩn sản phẩm thành công!"));
    }

    @PostMapping("/admin/{id}/images")
    public ResponseEntity<ApiResponse<String>> uploadProductImage(
            @PathVariable("id") Long id,
            @RequestParam("file") MultipartFile file) throws Exception {
        String result = productService.uploadImage(id, file);
        return ResponseEntity.ok(ApiResponse.success(result, "Upload ảnh thành công"));
    }
}