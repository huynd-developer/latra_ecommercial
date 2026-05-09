package trungdevcode.latra.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import trungdevcode.latra.entity.Cart;
import trungdevcode.latra.entity.CartItem;
import trungdevcode.latra.entity.ProductVariant; // Đổi từ Product sang ProductVariant
import trungdevcode.latra.repository.CartItemRepository;
import trungdevcode.latra.repository.CartRepository;
import trungdevcode.latra.repository.ProductVariantRepository; // Thêm Repo mới

import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductVariantRepository productVariantRepository;

    public Cart getCartByUserId(Long userId) {
        return cartRepository.findByUserId(userId).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUserId(userId);
            return cartRepository.save(newCart);
        });
    }

    @Transactional
    public Cart addToCart(Long userId, main.java.trungdevcode.latra.Dto.CartItemRequestDTO dto) {
        Cart cart = getCartByUserId(userId);

        ProductVariant variant = productVariantRepository.findById(dto.getVariantId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiên bản máy này!"));

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getVariant().getId().equals(dto.getVariantId()))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + dto.getQuantity());
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setVariant(variant);
            newItem.setQuantity(dto.getQuantity());
            cart.getItems().add(newItem);
        }

        return cartRepository.save(cart);
    }

    @Transactional
    public Cart removeCartItem(Long userId, Long cartItemId) {
        Cart cart = getCartByUserId(userId);
        cart.getItems().removeIf(item -> item.getId().equals(cartItemId));
        cartItemRepository.deleteById(cartItemId);
        return cartRepository.save(cart);
    }
    @Transactional
    public Cart updateCartItemQuantity(Long userId, Long cartItemId, Integer newQuantity) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giỏ hàng!"));

        CartItem itemToUpdate = cart.getItems().stream()
                .filter(item -> item.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại trong giỏ hàng!"));

        if (newQuantity <= 0) {
            cart.getItems().remove(itemToUpdate);
            return cartRepository.save(cart);
        }

        trungdevcode.latra.entity.ProductVariant variant = itemToUpdate.getVariant();
        if (variant.getStock() < newQuantity) {
            throw new RuntimeException("Kho không đủ! Sản phẩm này chỉ còn " + variant.getStock() + " chiếc.");
        }

        itemToUpdate.setQuantity(newQuantity);
        return cartRepository.save(cart);
    }
}