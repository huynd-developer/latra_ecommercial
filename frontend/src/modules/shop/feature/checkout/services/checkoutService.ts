import request from '@/common/services/request';
import type { CheckoutRequest, CheckoutResponse, VoucherCheckResponse } from '../types/checkout.type';
import type { ApiResponse } from '@/common/types/api.type';

export const checkoutService = {
  getActiveVouchers: async (): Promise<any[]> => {
    const res = await request.get<any, ApiResponse<any[]>>('/promotions/public/vouchers/active');
    return res.data;
  },

  checkVoucher: async (code: string, total: number): Promise<VoucherCheckResponse> => {
    const res = await request.get<any, ApiResponse<VoucherCheckResponse>>(`/promotions/public/vouchers/check?code=${code}&total=${total}`);
    return res.data;
  },

  submitOrder: async (payload: CheckoutRequest): Promise<CheckoutResponse> => {
    const res = await request.post<any, ApiResponse<CheckoutResponse>>('/orders/checkout', payload);
    return res.data;
  }
};