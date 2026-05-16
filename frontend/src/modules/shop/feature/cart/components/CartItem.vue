<script setup lang="ts">
import type { CartItemResponse } from '../types/cart.type';
import { useCartStore } from '../stores/cartStore';

const props = defineProps<{ item: CartItemResponse }>();
const cartStore = useCartStore();

const formatVND = (val: number) => new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(val);

const updateQty = (newQty: number) => {
  if (newQty <= 0) {
    if(confirm('Bạn muốn xóa sản phẩm này khỏi giỏ?')) cartStore.removeItem(props.item.id);
    return;
  }
  if (newQty > 5) return alert('Hệ thống chỉ cho phép mua tối đa 5 chiếc cho mỗi phiên bản!');
  cartStore.updateItemQty(props.item.id, newQty);
};
</script>

<template>
  <div class="cart-item-row p-3 p-md-4 border-bottom bg-white transition-all" :class="{'opacity-50 bg-light': item.isAvailable === false}">
    <div class="row align-items-center g-3">
      
      <div class="col-3 col-md-2">
        <div class="bg-light rounded-3 border d-flex justify-content-center align-items-center overflow-hidden ratio-1x1 p-2">
          <img v-if="item.imageUrl" :src="`http://localhost:8080/uploads/${item.imageUrl}`" class="img-fluid mix-blend-multiply object-fit-contain" alt="Product">
          <i v-else class="bi bi-image text-muted fs-3"></i>
        </div>
      </div>

      <div class="col-9 col-md-4">
        <h6 class="fw-bold text-dark mb-1 line-clamp-2">{{ item.productName || 'Sản phẩm' }}</h6>
        <div class="small text-secondary mb-2">Phân loại: <span class="fw-bold text-dark">{{ item.variantName || 'Mặc định' }}</span></div>

        <div v-if="item.isAvailable === false" class="small fw-bold text-danger bg-danger bg-opacity-10 px-2 py-1 d-inline-block rounded-2">
          <i class="bi bi-exclamation-triangle-fill me-1"></i> {{ item.warningMessage || 'Hết hàng / Ngừng bán' }}
        </div>
      </div>

      <div class="col-4 col-md-2 text-md-center">
        <div v-if="item.salePrice" class="mb-1">
          <div class="fw-bold text-danger">{{ formatVND(item.salePrice) }}</div>
          <div class="small text-muted text-decoration-line-through">{{ formatVND(item.originalPrice) }}</div>
        </div>
        <div v-else class="fw-bold text-dark">
          {{ formatVND(item.originalPrice || 0) }}
        </div>
      </div>

      <div class="col-4 col-md-2 d-flex justify-content-md-center">
        <div class="input-group input-group-sm rounded-pill border bg-white shadow-sm flex-nowrap" style="width: 100px;" :class="{'pe-none opacity-50': item.isAvailable === false}">
          <button @click="updateQty(item.quantity - 1)" class="btn btn-light text-dark fw-bold border-0 rounded-start-pill px-2"><i class="bi bi-dash"></i></button>
          <input :value="item.quantity" class="form-control text-center border-0 bg-transparent fw-bold px-0" readonly>
          <button @click="updateQty(item.quantity + 1)" :disabled="item.quantity >= 5" class="btn btn-light text-dark fw-bold border-0 rounded-end-pill px-2"><i class="bi bi-plus"></i></button>
        </div>
      </div>

      <div class="col-4 col-md-2 d-flex flex-column align-items-end justify-content-center">
        <div class="fw-black text-danger mb-2">{{ formatVND(item.itemTotal || (item.quantity * (item.salePrice || item.originalPrice || 0))) }}</div>
        <button @click="cartStore.removeItem(item.id)" class="btn btn-link text-muted hover-danger small p-0 text-decoration-none">
          <i class="bi bi-trash3"></i> Xóa
        </button>
      </div>

    </div>
  </div>
</template>

<style scoped>
.mix-blend-multiply { mix-blend-mode: multiply; }
.transition-all { transition: all 0.2s ease; }
.line-clamp-2 { display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.ratio-1x1 { aspect-ratio: 1 / 1; }
.hover-danger:hover { color: #dc3545 !important; }
</style>