<template>
  <div class="payment-result-page py-5" style="background-color: #f8f9fa; min-height: 100vh;">
    <div class="container px-4 max-w-3xl mx-auto">
      <div class="bg-white p-5 rounded-4 shadow-sm text-center border" :class="isSuccess ? 'border-success' : 'border-danger'">
        
        <i v-if="isSuccess" class="bi bi-check-circle-fill text-success d-block mb-4" style="font-size: 5rem;"></i>
        <i v-else class="bi bi-x-circle-fill text-danger d-block mb-4" style="font-size: 5rem;"></i>

        <h2 class="fw-bold mb-3" :class="isSuccess ? 'text-success' : 'text-danger'">
          {{ isSuccess ? 'Thanh toán thành công!' : 'Giao dịch thất bại hoặc đã bị hủy!' }}
        </h2>
        
        <p class="text-secondary mb-4">
          {{ isSuccess ? 'Cảm ơn bạn đã mua sắm tại LATRA. Đơn hàng của bạn đang được xử lý.' : 'Rất tiếc, giao dịch của bạn không thể hoàn tất. Vui lòng thử lại.' }}
        </p>

        <div class="card bg-light border-0 rounded-3 text-start mx-auto mb-4" style="max-width: 500px;">
          <div class="card-body p-4">
            <div class="d-flex justify-content-between mb-3">
              <span class="text-muted">Mã giao dịch (VNPAY):</span>
              <span class="fw-bold">{{ transactionNo || 'N/A' }}</span>
            </div>
            <div class="d-flex justify-content-between mb-3">
              <span class="text-muted">Mã đơn hàng:</span>
              <span class="fw-bold">{{ orderRef || 'N/A' }}</span>
            </div>
            <div class="d-flex justify-content-between mb-3">
              <span class="text-muted">Ngân hàng thanh toán:</span>
              <span class="fw-bold">{{ bankCode || 'N/A' }}</span>
            </div>
            <div class="d-flex justify-content-between">
              <span class="text-muted">Số tiền:</span>
              <span class="fw-bold text-danger fs-5">{{ formattedAmount }}</span>
            </div>
          </div>
        </div>

        <div class="d-flex justify-content-center gap-3 mt-4">
          <button @click="router.push('/')" class="btn btn-outline-dark px-4 py-2 rounded-pill fw-bold">
            Về trang chủ
          </button>
          <button v-if="!isSuccess" @click="router.push('/checkout')" class="btn btn-dark px-4 py-2 rounded-pill fw-bold">
            Thử thanh toán lại
          </button>
          <button v-else @click="router.push('/profile/orders')" class="btn btn-dark px-4 py-2 rounded-pill fw-bold">
            Xem đơn hàng
          </button>
        </div>

      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useCartStore } from '../../cart/stores/cartStore';

const route = useRoute();
const router = useRouter();
const cartStore = useCartStore();

// 1. Đọc các tham số từ URL do VNPAY trả về
const responseCode = computed(() => route.query.vnp_ResponseCode as string);
const transactionNo = computed(() => route.query.vnp_TransactionNo as string);
const bankCode = computed(() => route.query.vnp_BankCode as string);
const orderRef = computed(() => route.query.vnp_TxnRef as string);

// 2. Tính toán số tiền (VNPAY trả về số tiền nhân với 100, nên phải chia 100)
const formattedAmount = computed(() => {
  const amount = route.query.vnp_Amount;
  if (!amount) return '0 đ';
  return (Number(amount) / 100).toLocaleString('vi-VN') + ' đ';
});

// 3. Logic: Mã 00 là giao dịch thành công theo chuẩn VNPAY
const isSuccess = computed(() => responseCode.value === '00');

onMounted(() => {
  // Logic thực tế: Nếu thanh toán thành công, xóa sạch giỏ hàng ở Frontend
  if (isSuccess.value) {
    cartStore.cartData = null; 
    // Nếu có API fetch lại giỏ hàng thì gọi ở đây: cartStore.fetchCart();
  }
});
</script>