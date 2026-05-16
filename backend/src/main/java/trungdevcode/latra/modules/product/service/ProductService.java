// backend/src/main/java/trungdevcode/latra/modules/product/service/ProductService.java
package trungdevcode.latra.modules.product.service;

import org.springframework.web.multipart.MultipartFile;
import trungdevcode.latra.modules.product.dto.request.ProductRequest;
import trungdevcode.latra.modules.product.dto.response.ProductResponse;

import java.util.Map;

public interface ProductService {
    ProductResponse getProductById(Long id);
    ProductResponse createProduct(ProductRequest request);
    Map<String, Object> getAllProducts(int page, int size);
    ProductResponse updateProduct(Long id, ProductRequest request);
    void deleteProduct(Long id);
    String uploadImage(Long productId, MultipartFile file) throws Exception;
    void deleteProductImage(Long imageId);
    void setMainImage(Long productId, Long imageId);
}