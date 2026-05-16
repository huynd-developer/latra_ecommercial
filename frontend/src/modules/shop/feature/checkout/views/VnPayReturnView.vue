<!-- frontend/src/modules/shop/feature/checkout/views/VnpayReturnView.vue -->
<script setup lang="ts">
import { computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useCartStore } from '../../cart/stores/cartStore';

const route = useRoute();
const router = useRouter();
const cartStore = useCartStore();

// Đọc param trên URL
const responseCode = computed(() => route.query.vnp_ResponseCode as string);
const orderId = computed(() => route.query.vnp_TxnRef as string);

// Nếu = '00' là thanh toán thành công
const isSuccess = computed(() => responseCode.value === '00');

onMounted(() => {
  // Nếu thành công thì xóa sạch giỏ hàng UI
  if (isSuccess.value) {
    cartStore.cartData = null; 
  }
});
</script>

<template>
  <div class="min-h-screen d-flex align-items-center justify-content-center bg-gray-50 py-10">
    <div class="bg-white p-5 rounded-4 shadow-sm text-center max-w-md w-full border">
      
      <!-- Giao diện Thành Công -->
      <div v-if="isSuccess">
        <div class="mb-4">
          <i class="bi bi-check-circle-fill text-success" style="font-size: 5rem;"></i>
        </div>
        <h2 class="fw-bold text-slate-800 mb-2">Thanh Toán Thành Công!</h2>
        <p class="text-slate-600 mb-4">Cảm ơn bạn đã mua sắm. Đơn hàng <span class="fw-bold">#{{ orderId }}</span> của bạn đã được thanh toán qua VNPAY.</p>
        <button @click="router.push('/')" class="btn btn-dark rounded-pill px-5 py-2 fw-bold">Tiếp tục mua sắm</button>
      </div>

      <!-- Giao diện Thất Bại -->
      <div v-else>
        <div class="mb-4">
          <i class="bi bi-x-circle-fill text-danger" style="font-size: 5rem;"></i>
        </div>
        <h2 class="fw-bold text-slate-800 mb-2">Giao Dịch Thất Bại!</h2>
        <p class="text-slate-600 mb-4">Rất tiếc, giao dịch VNPAY của bạn không thành công hoặc đã bị hủy.</p>
        <p class="small text-danger mb-4">Lỗi Code: {{ responseCode }}</p>
        <div class="d-flex justify-content-center gap-2">
          <button @click="router.push('/cart')" class="btn btn-outline-dark rounded-pill px-4 fw-bold">Về Giỏ Hàng</button>
          <button @click="router.push('/checkout')" class="btn btn-dark rounded-pill px-4 fw-bold">Thử thanh toán lại</button>
        </div>
      </div>
      
    </div>
  </div>
</template>