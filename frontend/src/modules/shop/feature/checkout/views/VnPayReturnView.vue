<script setup lang="ts">
import { computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useCartStore } from '../../cart/stores/cartStore';

const route = useRoute();
const router = useRouter();
const cartStore = useCartStore();

// Đọc param trả về trên URL từ hệ thống VNPAY
const responseCode = computed(() => route.query.vnp_ResponseCode as string);
const orderId = computed(() => route.query.vnp_TxnRef as string);

// Nếu mã lỗi = '00' là thanh toán thành công
const isSuccess = computed(() => responseCode.value === '00');

onMounted(() => {
  // Nếu thành công thì reset lại giỏ hàng UI
  if (isSuccess.value) {
    cartStore.cartData = null; 
  }
});
</script>

<template>
  <div class="min-vh-100 d-flex align-items-center justify-content-center bg-light py-5">
    <div class="bg-white p-5 rounded-4 shadow-sm text-center border" style="max-width: 500px; width: 100%;">
      
      <div v-if="isSuccess">
        <div class="mb-4">
          <i class="bi bi-check-circle-fill text-success" style="font-size: 5rem;"></i>
        </div>
        <h2 class="fw-bold text-dark mb-2">Thanh Toán Thành Công!</h2>
        <p class="text-secondary mb-4">Cảm ơn bạn đã mua sắm. Đơn hàng <span class="fw-bold text-dark">#{{ orderId }}</span> của bạn đã được thanh toán qua VNPAY.</p>
        <button @click="router.push('/')" class="btn btn-dark w-100 rounded-pill py-3 fw-bold mt-2">Tiếp tục mua sắm</button>
      </div>

      <div v-else>
        <div class="mb-4">
          <i class="bi bi-x-circle-fill text-danger" style="font-size: 5rem;"></i>
        </div>
        <h2 class="fw-bold text-dark mb-2">Giao Dịch Thất Bại!</h2>
        <p class="text-secondary mb-4">Rất tiếc, giao dịch VNPAY của bạn không thành công hoặc đã bị hủy.</p>
        <p class="small text-danger mb-4 fw-medium bg-danger bg-opacity-10 py-2 rounded-2">Lỗi Code: {{ responseCode }}</p>
        
        <div class="d-flex flex-column gap-3">
          <button @click="router.push('/checkout')" class="btn btn-dark rounded-pill py-3 fw-bold">Thử thanh toán lại</button>
          <button @click="router.push('/cart')" class="btn btn-outline-secondary rounded-pill py-3 fw-bold">Về Giỏ Hàng</button>
        </div>
      </div>
      
    </div>
  </div>
</template>

<style scoped>
/* Không cần style đặc biệt vì đã dùng full Bootstrap Utilities */
</style>