package trungdevcode.latra.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import trungdevcode.latra.Dto.ProductRequestDTO;
import trungdevcode.latra.Dto.ProductResponseDTO;
import trungdevcode.latra.Service.ProductService;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/products")
@CrossOrigin(origins = "http://localhost:5173") // 🔥 THÊM CÁI NÀY VÀO ĐỂ VUE.JS KHÔNG BỊ CHẶN
@RequiredArgsConstructor // 🔥 CHUẨN MỚI: Thay cho @Autowired
public class ProductController {

    private final ProductService productService;

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProductDetail(@PathVariable("id") Long id) { // 🔥 FIX LỖI 400
        ProductResponseDTO response = productService.getProductById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/admin")
    public ResponseEntity<ProductResponseDTO> createProduct(@Valid @RequestBody ProductRequestDTO request) {
        ProductResponseDTO response = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllProducts(
            // 🔥 FIX LỖI 400: Thêm value = "page" và value = "size"
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        Map<String, Object> response = productService.getAllProducts(page, size);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/admin/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @PathVariable("id") Long id, // 🔥 FIX LỖI 400
            @Valid @RequestBody ProductRequestDTO request) {
        ProductResponseDTO response = productService.updateProduct(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/admin/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable("id") Long id) { // 🔥 FIX LỖI 400
        productService.deleteProduct(id);
        return ResponseEntity.ok("Đã ẩn sản phẩm thành công!");
    }

    @PostMapping("/admin/{id}/images")
    public ResponseEntity<String> uploadProductImage(
            @PathVariable("id") Long id,
            @RequestParam("file") MultipartFile file) throws Exception { // 🔥 THÊM "throws Exception" VÀO DÒNG NÀY

        // Đã xóa try-catch, nếu có lỗi thì GlobalExceptionHandler sẽ tự động tóm gọn!
        String result = productService.uploadImage(id, file);
        return ResponseEntity.ok(result);
    }
}