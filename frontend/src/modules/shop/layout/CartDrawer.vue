<template>
  <div>
    <div v-if="cartStore.isOpen" class="offcanvas-backdrop fade show" @click="cartStore.toggleCart"></div>

    <div class="offcanvas offcanvas-end shadow-lg" tabindex="-1" style="width: 400px; transition: transform 0.3s ease-in-out;" 
         :class="{ 'show d-block': cartStore.isOpen }"
         :style="{ transform: cartStore.isOpen ? 'translateX(0)' : 'translateX(100%)' }">
      
      <div class="offcanvas-header border-bottom py-3 px-4">
        <h5 class="fw-bold mb-0">Giỏ Hàng ({{ cartStore.totalItemsCount }})</h5>
        <button type="button" class="btn-close shadow-none" @click="cartStore.toggleCart"></button>
      </div>

      <div class="offcanvas-body px-4 py-4 d-flex flex-column">
        
        <div v-if="!cartStore.cartData || cartStore.cartData.items.length === 0" class="text-center py-5 my-auto">
          <i class="bi bi-cart-x display-1 text-muted mb-3 d-block"></i>
          <p class="text-secondary fs-5">Giỏ hàng của bạn đang trống.</p>
          <router-link to="/shop" class="btn btn-dark rounded-pill px-4 mt-3 fw-bold" @click="cartStore.toggleCart">Tiếp tục mua sắm</router-link>
        </div>

        <div v-else class="flex-grow-1 overflow-auto custom-scrollbar pe-2">
          <div v-for="item in cartStore.cartData.items" :key="item.id" class="d-flex gap-3 mb-4 position-relative border-bottom pb-3">
            
            <div class="bg-light rounded-3 d-flex align-items-center justify-content-center p-2" style="width: 80px; height: 80px;">
              <img :src="`http://localhost:8080/uploads/${item.imageUrl}`" class="img-fluid mix-blend-multiply object-fit-contain" alt="Product">
            </div>
            
            <div class="flex-grow-1">
              <h6 class="fw-bold mb-1 pe-4 line-clamp-2" style="font-size: 0.9rem;">{{ item.productName }}</h6>
              <div class="text-secondary small mb-2">{{ item.variantName }}</div>
              
              <div class="d-flex justify-content-between align-items-center">
                <div class="text-muted small">SL: {{ item.quantity }}</div>
                <div class="fw-bold text-dark">{{ formatMoney(item.itemTotal) }}</div>
              </div>
            </div>

            <i class="bi bi-x fs-4 position-absolute top-0 end-0 cursor-pointer text-muted hover-danger" @click="cartStore.removeItem(item.id)"></i>
          </div>
        </div>

        <div v-if="cartStore.cartData && cartStore.cartData.items.length > 0" class="border-top pt-4 mt-auto">
          <div class="d-flex justify-content-between fw-bold fs-5 mb-4">
            <span class="text-secondary">Tổng cộng:</span>
            <span class="text-danger fw-black">{{ formatMoney(cartStore.cartData.subTotal) }}</span>
          </div>
          
          <router-link to="/cart" class="btn btn-outline-dark w-100 py-2 rounded-pill fw-bold mb-2" @click="cartStore.toggleCart">
            XEM GIỎ HÀNG
          </router-link>
          
          <button @click="handleCheckout" class="btn btn-dark w-100 py-3 rounded-pill fw-bold" :disabled="cartStore.hasInvalidItems">
            THANH TOÁN
          </button>
        </div>
      </div>

    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router';
import { useCartStore } from '../feature/cart/stores/cartStore';

const router = useRouter();
const cartStore = useCartStore();

const formatMoney = (val: number) => new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(val);

const handleCheckout = () => {
  cartStore.toggleCart(); // Đóng drawer
  router.push('/checkout'); // Chuyển sang Checkout
};
</script>

<style scoped>
.cursor-pointer { cursor: pointer; }
.hover-danger:hover { color: #dc3545 !important; }
.mix-blend-multiply { mix-blend-mode: multiply; }
.line-clamp-2 { display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.custom-scrollbar::-webkit-scrollbar { width: 4px; }
.custom-scrollbar::-webkit-scrollbar-thumb { background: #cbd5e1; border-radius: 10px; }
</style>