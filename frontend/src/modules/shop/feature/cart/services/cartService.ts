// frontend/src/modules/shop/feature/cart/services/cartService.ts
import request from '@/common/services/request';
import type { CartItemRequest, CartResponse } from '../types/cart.type';
import type { ApiResponse } from '@/common/types/api.type';

export const cartService = {
  getCart: async (userId: number): Promise<CartResponse> => {
    const res = await request.get<any, ApiResponse<CartResponse>>(`/carts/user/${userId}`);
    return res.data;
  },

  addToCart: async (userId: number, payload: CartItemRequest): Promise<CartResponse> => {
    const res = await request.post<any, ApiResponse<CartResponse>>(`/carts/user/${userId}/add`, payload);
    return res.data;
  },

  updateQuantity: async (userId: number, cartItemId: number, quantity: number): Promise<CartResponse> => {
    const res = await request.put<any, ApiResponse<CartResponse>>(`/carts/user/${userId}/update/${cartItemId}?quantity=${quantity}`);
    return res.data;
  },

  removeItem: async (userId: number, cartItemId: number): Promise<CartResponse> => {
    const res = await request.delete<any, ApiResponse<CartResponse>>(`/carts/user/${userId}/remove/${cartItemId}`);
    return res.data;
  },

  mergeCart: async (userId: number, items: CartItemRequest[]): Promise<CartResponse> => {
    const res = await request.post<any, ApiResponse<CartResponse>>(`/carts/user/${userId}/merge`, { items });
    return res.data;
  }
};