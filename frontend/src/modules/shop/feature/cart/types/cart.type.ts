// frontend/src/modules/shop/feature/cart/types/cart.type.ts
export interface CartItemRequest {
  variantId: number;
  quantity: number;
}

export interface CartItemResponse {
  id: number; // cartItemId
  variantId: number;
  productName: string;
  variantName: string;
  imageUrl: string | null;
  quantity: number;
  originalPrice: number;
  salePrice: number | null; // Nếu có Flash Sale
  itemTotal: number;
  isAvailable: boolean; // BE trả về false nếu hết hàng/ngừng bán
  warningMessage: string | null;
}

export interface CartResponse {
  cartId: number;
  items: CartItemResponse[];
  subTotal: number;
}