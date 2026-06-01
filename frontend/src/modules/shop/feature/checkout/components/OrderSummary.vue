<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { useCartStore } from '../../cart/stores/cartStore';
import { useCheckoutStore } from '../stores/checkoutStore';

const cartStore = useCartStore();
const checkoutStore = useCheckoutStore();

const voucherInput = ref('');
const showVouchers = ref(false); 

const formatVND = (val: number) => new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(val);

const handleApplyVoucher = (code: string) => {
  if (!code.trim()) return;
  voucherInput.value = code; 
  showVouchers.value = false;
  checkoutStore.applyVoucher(code.trim());
};

onMounted(() => {
  if (checkoutStore && typeof checkoutStore.fetchActiveVouchers === 'function') {
    checkoutStore.fetchActiveVouchers();
  }
});
</script>

<template>
  <div class="bg-white p-4 p-lg-5 rounded-4 shadow-sm border border-slate-200 sticky-top" style="top: 24px;">
    <h4 class="fw-bold mb-4 text-dark">Tóm tắt đơn hàng</h4>
    
    <div v-if="!cartStore.cartData || !cartStore.cartData?.items?.length" class="text-center py-5">
      <i class="bi bi-cart-x display-1 text-slate-300 mb-3 d-block"></i>
      <p class="text-danger fw-bold mb-0">Giỏ hàng của bạn đang trống!</p>
    </div>
    
    <div v-else>
      <div v-if="cartStore.hasInvalidItems" class="alert alert-danger d-flex align-items-start border-0 bg-danger bg-opacity-10 mb-4 p-3 rounded-3">
        <i class="bi bi-exclamation-triangle-fill fs-5 text-danger me-3 mt-1"></i>
        <div>
          <div class="fw-bold text-danger mb-1">Có sản phẩm bị lỗi / hết hàng</div>
          <div class="small text-danger opacity-75 mb-2">Vui lòng loại bỏ sản phẩm lỗi khỏi giỏ để tiếp tục.</div>
        </div>
      </div>

      <div class="overflow-auto custom-scrollbar pe-2 mb-4" style="max-height: 400px;">
        <div v-for="item in cartStore.cartData?.items" :key="item.id" 
             class="d-flex justify-content-between align-items-center mb-3 pb-3 border-bottom transition-all"
             :class="{'opacity-50 grayscale': item.isAvailable === false}">
             
          <div class="d-flex align-items-center gap-3">
            <div class="position-relative bg-light rounded-4 border p-2 d-flex justify-content-center align-items-center" style="width: 80px; height: 80px;">
              <img :src="`http://localhost:8080/uploads/${item.imageUrl}`" class="img-fluid mix-blend-multiply object-fit-contain" alt="Product">
              <span class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-dark shadow-sm">{{ item.quantity }}</span>
            </div>
            <div>
              <div class="fw-bold text-dark line-clamp-2" style="max-width: 170px; font-size: 0.95rem;">{{ item.productName }}</div>
              <div class="text-sm text-secondary mt-1">{{ item.variantName }}</div>
              <div v-if="item.isAvailable === false" class="text-xs text-danger fw-bold mt-1"><i class="bi bi-x-circle-fill"></i> {{ item.warningMessage }}</div>
            </div>
          </div>
          
          <div class="text-end ms-3">
            <div class="fw-bold text-dark">{{ formatVND(item.itemTotal) }}</div>
            <div v-if="item.salePrice" class="badge bg-danger bg-opacity-10 text-danger mt-1">Đang Flash Sale</div>
          </div>
        </div>
      </div>

      <div class="mb-4 bg-slate-50 p-3 rounded-4 border border-slate-200">
        <label class="form-label small fw-bold text-dark mb-2 d-flex justify-content-between align-items-center position-relative">
          <span>Mã giảm giá / Voucher</span>
          
          <div class="position-relative">
            <button @click="showVouchers = !showVouchers" class="btn btn-link btn-sm p-0 text-decoration-none fw-bold text-primary" type="button">
              Chọn mã có sẵn
            </button>
            <ul v-if="showVouchers" class="dropdown-menu show position-absolute end-0 shadow border-slate-200 w-100 p-2 z-3" style="min-width: 250px; top: 100%;">
              <li v-if="!checkoutStore.availableVouchers || checkoutStore.availableVouchers.length === 0" class="text-center p-3 text-muted small">
                <i class="bi bi-ticket-perforated fs-3 d-block mb-2"></i>
                Hiện chưa có mã giảm giá nào phù hợp
              </li>
              <li v-else v-for="v in checkoutStore.availableVouchers" :key="v.code" class="mb-2">
                <a class="dropdown-item p-2 rounded-3 bg-light hover-bg-primary" href="#" @click.prevent="handleApplyVoucher(v.code)">
                  <div class="fw-bold text-dark">{{ v.code }} <span class="badge bg-danger ms-1" v-if="v.discountType === 'PERCENTAGE'">-{{ v.value }}%</span></div>
                  <div class="small text-muted" style="font-size: 0.75rem;">Đơn tối thiểu: {{ formatVND(v.minOrderValue) }}</div>
                </a>
              </li>
            </ul>
          </div>
        </label>
        
        <div class="d-flex gap-2">
          <input v-model="voucherInput" type="text" class="form-control text-uppercase shadow-none border-slate-300 fw-bold" placeholder="Nhập mã...">
          <button @click="handleApplyVoucher(voucherInput)" type="button" class="btn btn-dark text-nowrap px-4 fw-medium" :disabled="!voucherInput.trim()">Áp dụng</button>
        </div>
        
        <div v-if="checkoutStore.formState?.voucherCode" class="mt-3 p-2 bg-success bg-opacity-10 rounded-3 border border-success border-opacity-25 d-flex justify-content-between align-items-center">
          <span class="small fw-bold text-success"><i class="bi bi-check-circle-fill me-1"></i> Đã áp dụng: {{ checkoutStore.formState.voucherCode }}</span>
          <button @click="checkoutStore.removeVoucher" type="button" class="btn btn-link text-danger text-decoration-none p-0 small fw-bold"><i class="bi bi-x"></i> Gỡ</button>
        </div>
      </div>

      <div class="pt-3">
        <div class="d-flex justify-content-between text-secondary mb-2">
          <span class="fw-medium">Tạm tính giỏ hàng</span>
          <span class="fw-bold text-dark">{{ formatVND(cartStore.cartData?.subTotal || 0) }}</span>
        </div>
        
        <div v-if="checkoutStore.appliedDiscount > 0" class="d-flex justify-content-between text-success mb-2">
          <span class="fw-medium">Giảm giá (Voucher)</span>
          <span class="fw-bold">- {{ formatVND(checkoutStore.appliedDiscount) }}</span>
        </div>
        
        <div class="d-flex justify-content-between text-secondary mb-3 pb-3 border-bottom">
          <span class="fw-medium">Phí vận chuyển</span>
          <span class="text-success fw-bold">Miễn phí</span>
        </div>
        
        <div class="d-flex justify-content-between align-items-center pt-2">
          <span class="fw-bold fs-5 text-dark">Tổng thanh toán</span>
          <span class="fw-black fs-3 text-danger">{{ formatVND(checkoutStore.finalTotal) }}</span>
        </div>
      </div>

    </div>
  </div>
</template>

<style scoped>
.custom-scrollbar::-webkit-scrollbar { width: 5px; }
.custom-scrollbar::-webkit-scrollbar-thumb { background: #cbd5e1; border-radius: 10px; }
.grayscale { filter: grayscale(100%); }
.transition-all { transition: all 0.3s ease; }
.hover-bg-primary:hover { background-color: #e0e7ff !important; }
.mix-blend-multiply { mix-blend-mode: multiply; }
.z-3 { z-index: 1050; }
</style>