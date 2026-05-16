<!-- frontend/src/modules/shop/feature/checkout/views/CheckoutView.vue -->
<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';
import { useCartStore } from '../../cart/stores/cartStore';
import { useCheckoutStore } from '../stores/checkoutStore';
import { useAppStore } from '@/common/store/app.store';

import CheckoutForm from '../components/CheckoutForm.vue';
import PaymentMethod from '../components/PaymentMethod.vue';
import OrderSummary from '../components/OrderSummary.vue';

const router = useRouter();
const cartStore = useCartStore();
const checkoutStore = useCheckoutStore();
const appStore = useAppStore();

const isLoggedIn = ref(false); 

onMounted(() => {
  if (isLoggedIn.value) cartStore.fetchCart();
});

onUnmounted(() => { checkoutStore.removeVoucher(); });
const handleCheckout = async () => {
  if (!isLoggedIn.value) {
    appStore.notifyError("Vui lòng đăng nhập để thanh toán!");
    return;
  }
  
  try {
    const res = await checkoutStore.processCheckout();
    if (checkoutStore.formState.paymentMethod === 'VNPAY' && res.paymentUrl) {
      window.location.href = res.paymentUrl;
      return;
    }
    cartStore.cartData = null; 
    appStore.notifySuccess('Đặt hàng thành công!');
    router.push({ name: 'OrderSuccess', query: { orderId: res.orderId } }); 
  } catch (err: any) {
    alert("CÓ LỖI XẢY RA: " + err.message); // Fallback cứng nếu Toast bị chết
    appStore.notifyError(err.message);
  }
};

const handleMockLogin = () => {
  isLoggedIn.value = true;
  cartStore.fetchCart();
  checkoutStore.autoFillMockData(); // 👉 Tự động điền sạch sẽ form để test cực nhanh
};
</script>

<template>
  <div class="checkout-page py-5" style="background-color: #f8f9fa; min-height: 100vh;">
    <div class="container px-4 px-lg-5 max-w-7xl mx-auto">
      
      <div class="text-center mb-5">
        <h1 class="display-5 fw-bold text-dark tracking-tight">Thanh Toán</h1>
        <p class="text-secondary">Giao dịch của bạn được mã hóa và bảo mật an toàn 100%.</p>
      </div>

      <div v-if="!isLoggedIn" class="row justify-content-center">
        <!-- (Giữ nguyên form cảnh báo chặn đăng nhập ở câu trước) -->
         <div class="col-md-6 col-lg-5">
          <div class="bg-white p-5 rounded-4 shadow-sm text-center border border-warning">
            <i class="bi bi-shield-lock text-warning display-1 mb-3 d-block"></i>
            <h4 class="fw-bold text-dark mb-3">Bạn chưa đăng nhập!</h4>
            <p class="text-secondary mb-4">Theo chính sách hệ thống, chỉ thành viên có tài khoản mới được phép đặt hàng để đảm bảo quyền lợi bảo hành và theo dõi đơn.</p>
            <button disabled class="btn btn-dark w-100 rounded-pill py-3 fw-bold mb-3">Đăng nhập / Đăng ký (Đang bảo trì)</button>
            <button @click="handleMockLogin" class="btn btn-outline-secondary w-100 rounded-pill py-2 btn-sm">[Dev Tool] Giả lập đã đăng nhập</button>
          </div>
        </div>
      </div>

      <div v-else class="row gx-lg-5">
        <!-- CỘT TRÁI: THÔNG TIN GIAO HÀNG & THANH TOÁN -->
        <div class="col-lg-7 mb-5 mb-lg-0">
          <form @submit.prevent="handleCheckout">
            <CheckoutForm />
            <PaymentMethod />
            
            <!-- XÁC NHẬN ĐIỀU KHOẢN & BẢO MẬT TRƯỚC KHI ĐẶT HÀNG -->
            <div class="bg-white p-4 rounded-4 shadow-sm mt-4 border border-slate-200">
              <div class="form-check d-flex align-items-start mb-0">
                <input v-model="checkoutStore.agreeToTerms" class="form-check-input mt-1 me-3 shadow-none border-dark" type="checkbox" id="termsCheck">
                <label class="form-check-label text-secondary small lh-base" for="termsCheck">
                  Bằng việc bấm Xác nhận đặt hàng, tôi xác nhận đã đọc và đồng ý với 
                  <a href="#" class="text-primary fw-bold text-decoration-none">Điều khoản dịch vụ</a> và 
                  <a href="#" class="text-primary fw-bold text-decoration-none">Chính sách bảo mật</a> của hệ thống. 
                  Thông tin thu thập sẽ được xử lý tuân thủ quy định pháp luật.
                </label>
              </div>
            </div>

            <button 
              type="submit" 
              class="btn btn-dark w-100 py-3 mt-4 rounded-pill fw-bold fs-5 shadow-lg d-flex justify-content-center align-items-center gap-2 transition-all"
              :class="{'opacity-50 cursor-not-allowed': !checkoutStore.agreeToTerms}"
              :disabled="checkoutStore.isProcessing || cartStore.hasInvalidItems || cartStore.cartData?.items.length === 0 || !checkoutStore.agreeToTerms"
              style="background-color: #111;"
            >
              <span v-if="checkoutStore.isProcessing" class="spinner-border spinner-border-sm"></span>
              <i v-if="!checkoutStore.isProcessing" class="bi bi-shield-lock-fill"></i>
              {{ checkoutStore.isProcessing ? 'ĐANG MÃ HÓA GIAO DỊCH...' : 'XÁC NHẬN ĐẶT HÀNG' }}
            </button>
          </form>
        </div>

        <!-- CỘT PHẢI: GIỎ HÀNG -->
        <div class="col-lg-5">
          <OrderSummary />
        </div>
      </div>

    </div>
  </div>
</template>

<style scoped>
.transition-all { transition: all 0.3s ease; }
</style>