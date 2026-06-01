import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { checkoutService } from '../services/checkoutService';
import { useCartStore } from '../../cart/stores/cartStore';
import { useAppStore } from '@/common/store/app.store';
import type { CheckoutFormState, CheckoutRequest } from '../types/checkout.type';

export const useCheckoutStore = defineStore('checkoutStore', () => {
  const cartStore = useCartStore();
  const appStore = useAppStore();

  const formState = ref<CheckoutFormState>({
    customerName: '', customerPhone: '', customerEmail: '',
    province: '', district: '', ward: '', street: '',
    paymentMethod: 'VNPAY', voucherCode: null, note: '',
    isRequestVat: false, taxId: '', companyName: '', companyEmail: '', companyAddress: ''
  });

  const availableVouchers = ref<any[]>([]); 
  const agreeToTerms = ref(false);
  const appliedDiscount = ref<number>(0);
  const isProcessing = ref(false);

  const finalTotal = computed(() => {
    const subTotal = cartStore.cartData?.subTotal || 0;
    let total = subTotal - appliedDiscount.value;
    return total > 0 ? total : 0;
  });

  const fetchActiveVouchers = async () => {
    try {
      availableVouchers.value = await checkoutService.getActiveVouchers();
    } catch (error) {
      console.error("Lỗi lấy voucher:", error);
    }
  };

  const autoFillMockData = () => {
    formState.value.customerName = 'Nguyễn Kiều Diễm Ly';
    formState.value.customerPhone = '0901234567';
    formState.value.customerEmail = 'diemly@gmail.com';
    formState.value.province = 'Hà Nội';
    formState.value.district = 'Cầu Giấy';
    formState.value.ward = 'Dịch Vọng';
    formState.value.street = '123 Đường Cầu Giấy';
    agreeToTerms.value = true;
  };

  const applyVoucher = async (code: string) => {
    const subTotal = cartStore.cartData?.subTotal || 0;
    if (subTotal === 0) return appStore.notifyError("Giỏ hàng đang trống!");
    
    try {
      appStore.startLoading();
      const res = await checkoutService.checkVoucher(code, subTotal);
      appliedDiscount.value = res.discountAmount;
      formState.value.voucherCode = res.voucherCode;
      appStore.notifySuccess("Áp dụng mã giảm giá thành công!");
    } catch (error: any) {
      appliedDiscount.value = 0;
      formState.value.voucherCode = null;
      appStore.notifyError(error.response?.data?.message || "Mã không hợp lệ hoặc đơn chưa đủ điều kiện");
    } finally {
      appStore.stopLoading();
    }
  };

  const removeVoucher = () => {
    appliedDiscount.value = 0;
    formState.value.voucherCode = null;
  };

  const processCheckout = async () => {
    if (!agreeToTerms.value) throw new Error("Vui lòng đồng ý với Điều khoản dịch vụ!");
    if (!formState.value.customerName?.trim()) throw new Error("Vui lòng nhập họ tên!");
    if (!cartStore.cartData || !cartStore.cartData?.items?.length) throw new Error("Giỏ hàng trống!");

    const fullAddress = `${formState.value.street || ''}, ${formState.value.ward || ''}, ${formState.value.district || ''}, ${formState.value.province || ''}`;
    let finalNote = `[KH: ${formState.value.customerName} - ${formState.value.customerPhone}] ${formState.value.note || ''}`;
    
    const payload: CheckoutRequest = {
      userId: 1, 
      paymentMethod: formState.value.paymentMethod,
      shippingAddress: fullAddress,
      voucherCode: formState.value.voucherCode,
      note: finalNote.trim()
    };

    try {
      isProcessing.value = true;
      const response = await checkoutService.submitOrder(payload);
      return response; 
    } catch (error: any) {
      const errorMsg = error.response?.data?.message || "Lỗi giao tiếp máy chủ, không thể tạo đơn!";
      console.error("LỖI CHECKOUT NGHIÊM TRỌNG:", error.response?.data);
      throw new Error(errorMsg);
    } finally {
      isProcessing.value = false;
    }
  };

  return { 
    formState, agreeToTerms, appliedDiscount, finalTotal, isProcessing, availableVouchers,
    fetchActiveVouchers, applyVoucher, removeVoucher, processCheckout, autoFillMockData
  };
});