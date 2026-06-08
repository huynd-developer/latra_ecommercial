<template>
  <div class="py-5" style="background-color: #f8f9fa; min-height: 100vh;">
    <div class="container max-w-3xl mx-auto text-center px-4">
      <div class="bg-white p-5 rounded-4 shadow-sm border border-success">
        <i class="bi bi-check-circle-fill text-success d-block mb-4" style="font-size: 5rem;"></i>
        <h2 class="fw-bold text-success mb-3">Đặt hàng thành công!</h2>
        
        <p class="text-secondary mb-4">
          Cảm ơn bạn đã tin tưởng và mua sắm tại LATRA. <br>
          Mã đơn hàng của bạn là: <strong class="text-dark">#{{ orderId }}</strong>
        </p>
        <p class="text-secondary mb-4">
          Chúng tôi sẽ sớm liên hệ với bạn để xác nhận và giao hàng.
        </p>

        <div class="d-flex justify-content-center gap-3 mt-4">
          <router-link to="/" class="btn btn-outline-dark px-4 py-2 rounded-pill fw-bold">
            Tiếp tục mua sắm
          </router-link>
          <router-link to="/profile/orders" class="btn btn-dark px-4 py-2 rounded-pill fw-bold">
            Xem đơn hàng
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useRoute } from 'vue-router';

const route = useRoute();
// Lấy orderId từ query url (ví dụ: ?orderId=19)
const orderId = computed(() => route.query.orderId || 'N/A');
</script>