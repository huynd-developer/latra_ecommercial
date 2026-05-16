<script setup lang="ts">
import { onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useCartStore } from '../stores/cartStore';
import CartItem from '../components/CartItem.vue';
import { useAppStore } from '@/common/store/app.store';

const cartStore = useCartStore();
const appStore = useAppStore();
const router = useRouter();

onMounted(() => {
  cartStore.fetchCart();
});

const formatVND = (val: number) => new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(val);

const proceedToCheckout = () => {
  // Check nếu BE trả về có món nào isAvailable === false
  const hasError = cartStore.cartData?.items.some(i => i.isAvailable === false);
  
  if (hasError) {
    appStore.notifyError("Giỏ hàng có sản phẩm hết hàng. Vui lòng xóa trước khi thanh toán!");
    return;
  }
  // Mở khóa bay sang trang Checkout!
  router.push('/checkout');
};
</script>

<template>
  <div class="bg-gray-50 py-5" style="min-height: 80vh; background-color: #f8f9fa;">
    <div class="container max-w-7xl mx-auto px-lg-4">
      
      <div class="mb-4 d-flex align-items-baseline">
        <h2 class="fw-bolder text-dark mb-0 tracking-tight">Giỏ hàng</h2>
        <span class="ms-3 text-secondary fw-medium">({{ cartStore.totalItemsCount }} sản phẩm)</span>
      </div>

      <div class="row g-4">
        <div class="col-lg-8">
          <div class="bg-white rounded-4 shadow-sm border border-slate-100 overflow-hidden">
            
            <div class="d-none d-md-flex row px-4 py-3 bg-light border-bottom text-muted small fw-bold text-uppercase">
              <div class="col-6">Sản phẩm</div>
              <div class="col-2 text-center">Đơn giá</div>
              <div class="col-2 text-center">Số lượng</div>
              <div class="col-2 text-end">Thành tiền</div>
            </div>

            <div v-if="cartStore.isLoading" class="p-5 text-center text-muted">
              <span class="spinner-border text-secondary mb-3"></span>
              <p>Đang tải giỏ hàng...</p>
            </div>
            
            <div v-else-if="!cartStore.cartData || cartStore.cartData.items.length === 0" class="p-5 text-center my-5">
              <i class="bi bi-cart-x display-1 text-slate-300 mb-4 d-block"></i>
              <h5 class="fw-bold text-dark">Giỏ hàng của bạn đang trống</h5>
              <p class="text-secondary mb-4">Hãy chọn thêm sản phẩm để mua sắm nhé!</p>
              <router-link to="/shop" class="btn btn-dark rounded-pill px-5 py-2 fw-bold shadow-sm">Tiếp tục mua sắm</router-link>
            </div>
            
            <div v-else>
              <CartItem 
                v-for="item in cartStore.cartData.items" 
                :key="item.id" 
                :item="item" 
              />
            </div>

          </div>
        </div>

        <div class="col-lg-4">
          <div class="bg-white rounded-4 shadow-sm border border-slate-100 p-4 sticky-top" style="top: 100px;">
            <h5 class="fw-bold text-dark mb-4 border-bottom pb-3">Tóm tắt đơn hàng</h5>
            
            <div class="d-flex justify-content-between mb-3 text-secondary">
              <span class="fw-medium">Tạm tính:</span>
              <span class="fw-bold text-dark">{{ formatVND(cartStore.cartData?.subTotal || 0) }}</span>
            </div>
            
            <div class="d-flex justify-content-between mb-4 text-secondary small">
              <span>Phí vận chuyển & Voucher:</span>
              <span class="fw-medium text-primary text-end">Tính ở bước<br>thanh toán</span>
            </div>

            <div class="border-top pt-3 mb-4 d-flex justify-content-between align-items-center">
              <span class="fw-bold text-dark fs-5">Tổng cộng:</span>
              <span class="fs-3 fw-black text-danger">{{ formatVND(cartStore.cartData?.subTotal || 0) }}</span>
            </div>

            <button 
              @click="proceedToCheckout"
              class="btn w-100 py-3 rounded-pill fw-bold fs-6 shadow d-flex justify-content-center align-items-center gap-2 transition-all"
              :class="(cartStore.cartData?.items.some(i => i.isAvailable === false) || !cartStore.cartData?.items.length) ? 'btn-secondary disabled' : 'btn-danger text-white'"
            >
              TIẾN HÀNH THANH TOÁN
            </button>

            <div v-if="cartStore.cartData?.items.some(i => i.isAvailable === false)" class="alert alert-danger mt-3 mb-0 small fw-medium py-2 text-center border-0 bg-danger bg-opacity-10 rounded-3">
              <i class="bi bi-exclamation-triangle-fill"></i> Vui lòng loại bỏ các sản phẩm hết hàng để tiếp tục!
            </div>
            
            <div class="mt-4 pt-3 border-top text-center text-muted small">
              <div class="d-flex justify-content-center gap-3 fs-4 mb-2">
                <i class="bi bi-shield-check text-success"></i>
                <i class="bi bi-credit-card-2-front text-primary"></i>
                <i class="bi bi-truck text-info"></i>
              </div>
              Thanh toán bảo mật & Giao hàng toàn quốc
            </div>
          </div>
        </div>
      </div>

    </div>
  </div>
</template>

<style scoped>
.transition-all { transition: all 0.3s ease; }
</style>