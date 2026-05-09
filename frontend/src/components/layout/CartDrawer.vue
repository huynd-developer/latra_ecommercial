<template>
  <div>
    <div v-if="cartStore.isOpen" class="offcanvas-backdrop fade show" @click="cartStore.toggleCart"></div>

    <div class="offcanvas offcanvas-end shadow-lg" :class="{ 'show': cartStore.isOpen }" tabindex="-1" style="width: 400px; transition: transform 0.3s ease-in-out;" :style="{ transform: cartStore.isOpen ? 'translateX(0)' : 'translateX(100%)' }">
      
      <div class="offcanvas-header border-bottom py-4 px-4">
        <h5 class="fw-bold mb-0">Giỏ Hàng ({{ cartCount }})</h5>
        <button type="button" class="btn-close" @click="cartStore.toggleCart"></button>
      </div>

      <div class="offcanvas-body px-4 py-4 d-flex flex-column">
        <div v-if="cartStore.items.length === 0" class="text-center py-5 my-auto">
          <i class="bi bi-cart-x display-1 text-muted mb-3 d-block"></i>
          <p class="text-secondary fs-5">Giỏ hàng của bạn đang trống.</p>
          <button class="btn btn-dark rounded-pill px-4 mt-3" @click="cartStore.toggleCart">Tiếp tục mua sắm</button>
        </div>

        <div v-else class="flex-grow-1 overflow-auto">
          <div v-for="item in cartStore.items" :key="item.product.id" class="d-flex gap-3 mb-4 position-relative">
            <img :src="item.product.image" class="rounded-3 bg-light object-fit-contain p-2" style="width: 80px; height: 80px;" alt="Product">
            <div class="flex-grow-1">
              <h6 class="fw-bold mb-1 pe-4" style="font-size: 0.9rem;">{{ item.product.name }}</h6>
              <div class="text-secondary small mb-2">SL: {{ item.quantity }}</div>
              <div class="fw-bold">${{ (item.product.price * item.quantity).toFixed(2) }}</div>
            </div>
            <i class="bi bi-x fs-4 position-absolute top-0 end-0 cursor-pointer text-muted" @click="cartStore.removeItem(item.product.id)"></i>
          </div>
        </div>

        <div v-if="cartStore.items.length > 0" class="border-top pt-4 mt-auto">
          <div class="d-flex justify-content-between fw-bold fs-5 mb-4">
            <span>Tổng cộng:</span>
            <span>${{ cartTotal.toFixed(2) }}</span>
          </div>
          <router-link to="/checkout" class="btn btn-dark w-100 py-3 rounded-pill fw-bold" @click="cartStore.toggleCart">
            THANH TOÁN
          </router-link>
        </div>
      </div>

    </div>
  </div>
</template>

<script setup lang="ts">
import { cartStore, cartCount, cartTotal } from '../../store/cartStore'
</script>

<style scoped>
.offcanvas.show { visibility: visible; }
.cursor-pointer { cursor: pointer; }
</style>