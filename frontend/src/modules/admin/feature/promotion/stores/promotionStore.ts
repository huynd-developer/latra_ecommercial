// frontend/src/modules/admin/feature/promotion/stores/promotionStore.ts
import { defineStore } from 'pinia';
import { ref } from 'vue';
import { promotionService } from '../services/promotionService';
import { useAppStore } from '@/common/store/app.store';
import type { FlashSale, Voucher, VoucherAnalytics } from '../types/promotion.type';

export const usePromotionStore = defineStore('promotionStore', () => {
  const flashSales = ref<FlashSale[]>([]);
  const vouchers = ref<Voucher[]>([]);
  const appStore = useAppStore();
  const voucherAnalytics = ref<VoucherAnalytics[]>([]);

  const fetchVoucherAnalytics = async () => {
    try {
      voucherAnalytics.value = await promotionService.getVoucherAnalytics();
    } catch (err: any) {
      appStore.notifyError('Lỗi tải thống kê Voucher');
    }
  };
  const fetchFlashSales = async () => {
    try {
      flashSales.value = await promotionService.getFlashSales();
    } catch (err: any) {
      appStore.notifyError(err.response?.data?.message || 'Lỗi tải Flash Sale');
    }
  };

  const fetchVouchers = async () => {
    try {
      vouchers.value = await promotionService.getVouchers();
    } catch (err: any) {
      appStore.notifyError(err.response?.data?.message || 'Lỗi tải Voucher');
    }
  };
return { flashSales, vouchers, voucherAnalytics, fetchFlashSales, fetchVouchers, fetchVoucherAnalytics };
});