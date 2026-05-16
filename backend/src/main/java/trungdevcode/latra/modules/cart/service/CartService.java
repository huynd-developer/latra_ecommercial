// backend/src/main/java/trungdevcode/latra/modules/cart/service/CartService.java
package trungdevcode.latra.modules.cart.service;

import trungdevcode.latra.entity.Cart;
import trungdevcode.latra.modules.cart.dto.request.CartItemRequestDTO;
import trungdevcode.latra.modules.cart.dto.response.CartResponseDTO;

import java.util.List;

public interface CartService {

    Cart getCartByUserId(Long userId);

    Cart addToCart(Long userId, CartItemRequestDTO dto);

    Cart updateCartItemQuantity(Long userId, Long cartItemId, Integer newQuantity);

    Cart removeCartItem(Long userId, Long cartItemId);
    CartResponseDTO getCartResponse(Long userId);
    CartResponseDTO mergeGuestCart(Long userId, List<CartItemRequestDTO> guestItems);
}