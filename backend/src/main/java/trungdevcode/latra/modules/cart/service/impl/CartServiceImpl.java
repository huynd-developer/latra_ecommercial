// backend/src/main/java/trungdevcode/latra/modules/cart/service/impl/CartServiceImpl.java
package trungdevcode.latra.modules.cart.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import trungdevcode.latra.entity.Cart;
import trungdevcode.latra.entity.CartItem;
import trungdevcode.latra.entity.FlashSaleItem;
import trungdevcode.latra.entity.ProductVariant;
import trungdevcode.latra.modules.cart.dto.request.CartItemRequestDTO;
import trungdevcode.latra.modules.cart.dto.response.CartItemResponseDTO;
import trungdevcode.latra.modules.cart.dto.response.CartResponseDTO;
import trungdevcode.latra.modules.cart.service.CartService;
import trungdevcode.latra.repository.CartItemRepository;
import trungdevcode.latra.repository.CartRepository;
import trungdevcode.latra.repository.FlashSaleItemRepository;
import trungdevcode.latra.repository.ProductVariantRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductVariantRepository productVariantRepository;
    private final FlashSaleItemRepository flashSaleItemRepository;

    private static final int MAX_QTY_PER_VARIANT = 5;

    @Override
    public Cart getCartByUserId(Long userId) {
        return cartRepository.findByUserIdWithDetails(userId).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUserId(userId);
            return cartRepository.save(newCart);
        });
    }

    @Override
    @Transactional
    public Cart addToCart(Long userId, CartItemRequestDTO dto) {
        Cart cart = getCartByUserId(userId);

        ProductVariant variant = productVariantRepository.findById(dto.getVariantId())
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại!"));

        if (variant.getProduct().getStatus() != 1) {
            throw new RuntimeException("Sản phẩm này đã ngừng kinh doanh.");
        }

        Optional<CartItem> existingItemOpt = cart.getItems().stream()
                .filter(item -> item.getVariant().getId().equals(dto.getVariantId()))
                .findFirst();

        int currentQty = existingItemOpt.map(CartItem::getQuantity).orElse(0);
        int finalQty = currentQty + dto.getQuantity();

        if (finalQty > variant.getStock()) {
            throw new RuntimeException(String.format("Kho không đủ! Chỉ còn %d sản phẩm.", variant.getStock()));
        }

        if (finalQty > MAX_QTY_PER_VARIANT) {
            throw new RuntimeException(String.format("Bạn chỉ được mua tối đa %d chiếc cho sản phẩm này.", MAX_QTY_PER_VARIANT));
        }

        if (existingItemOpt.isPresent()) {
            CartItem item = existingItemOpt.get();
            item.setQuantity(finalQty);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setVariant(variant);
            newItem.setQuantity(dto.getQuantity());
            cart.getItems().add(newItem);
        }

        return cartRepository.save(cart);
    }

    @Override
    @Transactional
    public Cart updateCartItemQuantity(Long userId, Long cartItemId, Integer newQuantity) {
        Cart cart = cartRepository.findByUserIdWithDetails(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giỏ hàng!"));

        CartItem itemToUpdate = cart.getItems().stream()
                .filter(item -> item.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại trong giỏ hàng!"));

        if (newQuantity <= 0) {
            return removeCartItem(userId, cartItemId);
        }

        ProductVariant variant = itemToUpdate.getVariant();

        if (newQuantity > variant.getStock()) {
            throw new RuntimeException(String.format("Kho không đủ! Sản phẩm này chỉ còn %d chiếc.", variant.getStock()));
        }

        if (newQuantity > MAX_QTY_PER_VARIANT) {
            throw new RuntimeException(String.format("Bạn chỉ được mua tối đa %d chiếc cho sản phẩm này.", MAX_QTY_PER_VARIANT));
        }

        itemToUpdate.setQuantity(newQuantity);
        return cartRepository.save(cart);
    }

    @Override
    @Transactional
    public Cart removeCartItem(Long userId, Long cartItemId) {
        Cart cart = getCartByUserId(userId);
        boolean removed = cart.getItems().removeIf(item -> item.getId().equals(cartItemId));

        if (removed) {
            cartItemRepository.deleteById(cartItemId);
        }
        return cartRepository.save(cart);
    }

    @Override
    public CartResponseDTO getCartResponse(Long userId) {
        Cart cart = getCartByUserId(userId);
        BigDecimal subTotal = BigDecimal.ZERO;
        List<CartItemResponseDTO> itemDTOs = new ArrayList<>();

        for (CartItem item : cart.getItems()) {
            ProductVariant variant = item.getVariant();
            boolean isAvailable = true;
            String warning = null;

            // 1. Kiểm tra tồn kho
            if (variant.getProduct().getStatus() != 1) {
                isAvailable = false;
                warning = "Sản phẩm đã ngừng kinh doanh";
            } else if (variant.getStock() <= 0) {
                isAvailable = false;
                warning = "Sản phẩm đã hết hàng";
            } else if (item.getQuantity() > variant.getStock()) {
                isAvailable = false;
                warning = "Chỉ còn " + variant.getStock() + " sản phẩm trong kho";
            }

            // 2. Xử lý giá Flash Sale
            BigDecimal finalPrice = variant.getPrice();
            BigDecimal salePrice = null;

            Optional<FlashSaleItem> activeSale = flashSaleItemRepository.findActiveSaleByVariantId(variant.getId());
            if (activeSale.isPresent()) {
                salePrice = activeSale.get().getSalePrice();
                finalPrice = salePrice;
            }

            // 3. Tính tiền
            BigDecimal itemTotal = finalPrice.multiply(BigDecimal.valueOf(item.getQuantity()));
            if (isAvailable) {
                subTotal = subTotal.add(itemTotal);
            }

            // 👉 ĐÃ FIX LỖI TẠI ĐÂY: Trích xuất ảnh an toàn từ List<ProductImage>
            String imageUrl = null;
            if (variant.getProduct().getImages() != null && !variant.getProduct().getImages().isEmpty()) {
                // Tùy thuộc vào tên biến trong ProductImage.java của bạn là imageUrl hay url
                // Nếu Entity ProductImage dùng tên khác, bạn hãy đổi hàm getImageUrl() cho khớp nhé!
                imageUrl = variant.getProduct().getImages().get(0).getImageUrl();
            }

            // 4. Build DTO trả về FE
            itemDTOs.add(CartItemResponseDTO.builder()
                    .id(item.getId())
                    .variantId(variant.getId())
                    .productName(variant.getProduct().getName())
                    .variantName(variant.getColor() + " - " + variant.getStorage())
                    .imageUrl(imageUrl) // Truyền biến string imageUrl vừa lấy được ở trên vào đây
                    .quantity(item.getQuantity())
                    .originalPrice(variant.getPrice())
                    .salePrice(salePrice)
                    .itemTotal(itemTotal)
                    .isAvailable(isAvailable)
                    .warningMessage(warning)
                    .build());
        }

        return CartResponseDTO.builder()
                .cartId(cart.getId())
                .items(itemDTOs)
                .subTotal(subTotal)
                .build();
    }

    @Override
    @Transactional
    public CartResponseDTO mergeGuestCart(Long userId, List<CartItemRequestDTO> guestItems) {
        for (CartItemRequestDTO guestItem : guestItems) {
            try {
                this.addToCart(userId, guestItem);
            } catch (RuntimeException e) {
                System.err.println("Merge lỗi variantId " + guestItem.getVariantId() + ": " + e.getMessage());
            }
        }
        return this.getCartResponse(userId);
    }
}