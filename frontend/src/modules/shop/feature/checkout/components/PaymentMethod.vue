<script setup lang="ts">
import { useCheckoutStore } from '../stores/checkoutStore';
const checkoutStore = useCheckoutStore();
</script>

<template>
  <div v-if="checkoutStore?.formState" class="bg-white p-4 p-lg-5 rounded-4 shadow-sm mb-4 border border-slate-200">
    <h4 class="fw-bold mb-4 text-dark">Phương thức thanh toán</h4>
    
    <div class="form-check mb-3 p-3 border rounded-3 transition-all cursor-pointer" 
         :class="{'border-primary bg-primary bg-opacity-10': checkoutStore.formState.paymentMethod === 'COD'}"
         @click="checkoutStore.formState.paymentMethod = 'COD'">
      <input v-model="checkoutStore.formState.paymentMethod" value="COD" class="form-check-input ms-1" type="radio" name="payment">
      <label class="form-check-label ms-3 fw-medium d-flex align-items-center cursor-pointer">
        <i class="bi bi-cash-stack fs-4 text-success me-3"></i>
        Thanh toán khi nhận hàng (COD)
      </label>
    </div>

    <div class="form-check p-3 border rounded-3 transition-all cursor-pointer"
         :class="{'border-primary bg-primary bg-opacity-10': checkoutStore.formState.paymentMethod === 'VNPAY'}"
         @click="checkoutStore.formState.paymentMethod = 'VNPAY'">
      <input v-model="checkoutStore.formState.paymentMethod" value="VNPAY" class="form-check-input ms-1" type="radio" name="payment">
      <label class="form-check-label ms-3 fw-medium d-flex align-items-center cursor-pointer">
        <i class="bi bi-qr-code-scan fs-4 text-primary me-3"></i>
        Thanh toán trực tuyến (VNPAY / Thẻ tín dụng / QR Code)
      </label>
    </div>
  </div>
</template>

<style scoped>
.cursor-pointer { cursor: pointer; }
.transition-all { transition: all 0.2s ease; }
</style>