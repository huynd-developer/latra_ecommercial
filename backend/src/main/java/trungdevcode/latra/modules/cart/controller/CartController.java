// backend/src/main/java/trungdevcode/latra/modules/cart/controller/CartController.java
package trungdevcode.latra.modules.cart.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import trungdevcode.latra.common.response.ApiResponse;
import trungdevcode.latra.modules.cart.dto.request.CartItemRequestDTO;
import trungdevcode.latra.modules.cart.dto.request.MergeCartRequestDTO;
import trungdevcode.latra.modules.cart.dto.response.CartResponseDTO;
import trungdevcode.latra.modules.cart.service.CartService;

@RestController
@RequestMapping("/api/v1/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<CartResponseDTO>> getCart(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success(cartService.getCartResponse(userId), "Lấy giỏ hàng thành công"));
    }

    @PostMapping("/user/{userId}/add")
    public ResponseEntity<ApiResponse<CartResponseDTO>> addToCart(
            @PathVariable Long userId,
            @Valid @RequestBody CartItemRequestDTO dto) {
        cartService.addToCart(userId, dto);
        return ResponseEntity.ok(ApiResponse.success(cartService.getCartResponse(userId), "Thêm vào giỏ thành công"));
    }

    @PutMapping("/user/{userId}/update/{cartItemId}")
    public ResponseEntity<ApiResponse<CartResponseDTO>> updateCartItemQuantity(
            @PathVariable Long userId,
            @PathVariable Long cartItemId,
            @RequestParam Integer quantity) {
        cartService.updateCartItemQuantity(userId, cartItemId, quantity);
        return ResponseEntity.ok(ApiResponse.success(cartService.getCartResponse(userId), "Cập nhật số lượng thành công"));
    }

    @DeleteMapping("/user/{userId}/remove/{cartItemId}")
    public ResponseEntity<ApiResponse<CartResponseDTO>> removeCartItem(
            @PathVariable Long userId,
            @PathVariable Long cartItemId) {
        cartService.removeCartItem(userId, cartItemId);
        return ResponseEntity.ok(ApiResponse.success(cartService.getCartResponse(userId), "Đã xóa sản phẩm khỏi giỏ"));
    }

    @PostMapping("/user/{userId}/merge")
    public ResponseEntity<ApiResponse<CartResponseDTO>> mergeGuestCart(
            @PathVariable Long userId,
            @Valid @RequestBody MergeCartRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.success(cartService.mergeGuestCart(userId, dto.getItems()), "Gộp giỏ hàng thành công"));
    }
}