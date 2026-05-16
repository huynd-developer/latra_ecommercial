// frontend/src/modules/admin/feature/promotion/services/promotionService.ts
import request from '@/common/services/request';
import type { FlashSale, Voucher, VoucherAnalytics } from '../types/promotion.type';
import type { ApiResponse } from '@/common/types/api.type';

export const promotionService = {
  // FLASH SALE
  getFlashSales: async (): Promise<FlashSale[]> => {
    const res = await request.get<any, ApiResponse<FlashSale[]>>('/flash-sales/admin');
    return res.data;
  },
  createFlashSale: async (payload: FlashSale): Promise<string> => {
    const res = await request.post<any, ApiResponse<FlashSale>>('/flash-sales/admin', payload);
    return res.message;
  },
  updateFlashSale: async (id: number, payload: FlashSale): Promise<string> => {
    const res = await request.put<any, ApiResponse<FlashSale>>(`/flash-sales/admin/${id}`, payload);
    return res.message;
  },
  deleteFlashSale: async (id: number): Promise<string> => {
    const res = await request.delete<any, ApiResponse<string>>(`/flash-sales/admin/${id}`);
    return res.message;
  },
  getVoucherAnalytics: async (): Promise<VoucherAnalytics[]> => {
    const res = await request.get<any, ApiResponse<VoucherAnalytics[]>>('/promotions/analytics/admin/vouchers');
    return res.data;
  },

  // VOUCHER
  getVouchers: async (): Promise<Voucher[]> => {
    const res = await request.get<any, ApiResponse<Voucher[]>>('/vouchers/admin');
    return res.data;
  },
  createVoucher: async (payload: Voucher): Promise<string> => {
    const res = await request.post<any, ApiResponse<Voucher>>('/vouchers/admin', payload);
    return res.message;
  },
  updateVoucher: async (id: number, payload: Voucher): Promise<string> => {
    const res = await request.put<any, ApiResponse<Voucher>>(`/vouchers/admin/${id}`, payload);
    return res.message;
  },
  deleteVoucher: async (id: number): Promise<string> => {
    const res = await request.delete<any, ApiResponse<string>>(`/vouchers/admin/${id}`);
    return res.message;
  }
};