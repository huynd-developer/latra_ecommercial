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

const checkAuth = () => {
  const token = localStorage.getItem('accessToken') || localStorage.getItem('token') || localStorage.getItem('jwt');
  const userRaw = localStorage.getItem('currentUser') || localStorage.getItem('user') || localStorage.getItem('userInfo');

  if (token) {
    isLoggedIn.value = true;
    cartStore.fetchCart(); 

    if (userRaw && checkoutStore.formState) {
      try {
        const user = JSON.parse(userRaw);
        
        // 1. Điền thông tin cá nhân cơ bản
        checkoutStore.formState.customerName = user.fullName || user.name || '';
        checkoutStore.formState.customerPhone = user.phone || user.phoneNumber || '';
        checkoutStore.formState.customerEmail = user.email || '';
        
        // 2. Điền thông tin địa chỉ phân cấp mới
        checkoutStore.formState.province = user.province || user.city || '';
        checkoutStore.formState.district = user.district || '';
        checkoutStore.formState.ward = user.ward || '';
        checkoutStore.formState.street = user.street || user.address || '';
        
      } catch (e) {
        console.error("Lỗi khi đọc thông tin User để tự động điền:", e);
      }
    }
  } else {
    isLoggedIn.value = false;
  }
};

onMounted(() => {
  checkAuth();
});

onUnmounted(() => { 
  checkoutStore.removeVoucher(); 
});

const goToLogin = () => {
  router.push({ path: '/login', query: { redirect: '/checkout' } });
};

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
    appStore.notifyError(err.message || "Đặt hàng thất bại, vui lòng thử lại!");
  }
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
         <div class="col-md-6 col-lg-5">
          <div class="bg-white p-5 rounded-4 shadow-sm text-center border border-warning">
            <i class="bi bi-shield-lock text-warning display-1 mb-3 d-block"></i>
            <h4 class="fw-bold text-dark mb-3">Bạn chưa đăng nhập!</h4>
            <p class="text-secondary mb-4">Theo chính sách hệ thống, chỉ thành viên có tài khoản mới được phép đặt hàng để đảm bảo quyền lợi bảo hành và theo dõi đơn.</p>
            
            <button 
              @click="goToLogin" 
              class="btn btn-dark w-100 rounded-pill py-3 fw-bold mb-3 transition-all shadow-sm"
            >
              Đăng nhập để tiếp tục
            </button>
            <p class="text-muted small mt-2">
              Chưa có tài khoản? <router-link to="/register" class="text-primary fw-bold text-decoration-none">Đăng ký ngay</router-link>
            </p>
          </div>
        </div>
      </div>

      <div v-else class="row gx-lg-5">
        <div class="col-lg-7 mb-5 mb-lg-0">
          <form @submit.prevent="handleCheckout">
            <CheckoutForm />
            <PaymentMethod />
            
            <div class="bg-white p-4 rounded-4 shadow-sm mt-4 border border-slate-200">
              <div class="form-check d-flex align-items-start mb-0">
                <input v-model="checkoutStore.agreeToTerms" class="form-check-input mt-1 me-3 shadow-none border-dark" type="checkbox" id="termsCheck">
                <label class="form-check-label text-secondary small lh-base" for="termsCheck">
                  Bằng việc bấm Xác nhận đặt hàng, tôi xác nhận đã đọc và đồng ý với 
                  <a href="#" class="text-primary fw-bold text-decoration-none">Điều khoản dịch vụ</a> và 
                  <a href="#" class="text-primary fw-bold text-decoration-none">Chính sách bảo mật</a> của hệ thống.
                </label>
              </div>
            </div>

            <button 
              type="submit" 
              class="btn btn-dark w-100 py-3 mt-4 rounded-pill fw-bold fs-5 shadow-lg d-flex justify-content-center align-items-center gap-2 transition-all"
              :class="{'opacity-50 cursor-not-allowed': !checkoutStore.agreeToTerms}"
              :disabled="checkoutStore.isProcessing || cartStore.hasInvalidItems || !cartStore.cartData?.items?.length || !checkoutStore.agreeToTerms"
              style="background-color: #111;"
            >
              <span v-if="checkoutStore.isProcessing" class="spinner-border spinner-border-sm"></span>
              <i v-if="!checkoutStore.isProcessing" class="bi bi-shield-lock-fill"></i>
              {{ checkoutStore.isProcessing ? 'ĐANG MÃ HÓA GIAO DỊCH...' : 'XÁC NHẬN ĐẶT HÀNG' }}
            </button>
          </form>
        </div>

        <div class="col-lg-5">
          <OrderSummary />
        </div>
      </div>

    </div>
  </div>
</template>

<style scoped>
.transition-all { transition: all 0.3s ease; }
.cursor-not-allowed { cursor: not-allowed !important; }
</style>