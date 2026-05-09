<template>
  <div class="product-info py-lg-3">
    <div class="text-secondary small mb-2 text-uppercase fw-bold" style="letter-spacing: 1px;">{{ product.type }}</div>
    <h1 class="fw-bold text-dark display-5 mb-3" style="letter-spacing: -1px;">{{ product.name }}</h1>
    
    <div class="d-flex align-items-center gap-3 mb-4">
      <div class="text-warning">
        <i class="bi bi-star-fill"></i><i class="bi bi-star-fill"></i><i class="bi bi-star-fill"></i><i class="bi bi-star-fill"></i><i class="bi bi-star-half"></i>
      </div>
      <span class="text-secondary small fw-medium">4.8 (124 đánh giá)</span>
    </div>

    <div class="price-wrap d-flex align-items-center gap-3 mb-4">
      <span class="fw-bold fs-2 text-danger">${{ product.price.toFixed(2) }}</span>
      <span class="text-decoration-line-through text-muted fs-5" v-if="product.oldPrice">${{ product.oldPrice.toFixed(2) }}</span>
    </div>

    <p class="text-secondary mb-5 lh-lg" style="font-size: 1.05rem;">
      {{ product.description || 'Bảo vệ thiết bị của bạn với chất lượng cao cấp nhất. Thiết kế tối giản, tinh tế và ôm sát từng đường nét, mang lại trải nghiệm hoàn hảo như không dùng ốp/kính.' }}
    </p>

    <div class="d-flex flex-column flex-sm-row gap-3 mb-4">
      <div class="quantity-selector d-flex align-items-center justify-content-between border border-dark-subtle rounded-pill px-3 py-3" style="width: 140px;">
        <i class="bi bi-dash fs-5 cursor-pointer" @click="quantity > 1 ? quantity-- : null"></i>
        <span class="fw-bold fs-5">{{ quantity }}</span>
        <i class="bi bi-plus fs-5 cursor-pointer" @click="quantity++"></i>
      </div>
      
      <button class="btn btn-dark flex-grow-1 rounded-pill fw-bold fs-5 shadow-lg add-to-cart-btn py-3" 
              @click="$emit('add-to-cart', { product, quantity })"
              data-bs-toggle="offcanvas" data-bs-target="#globalCartSidebar">
        Thêm Vào Giỏ - ${(product.price * quantity).toFixed(2)}
      </button>
    </div>

    <ul class="list-unstyled text-secondary mt-5 pt-4 border-top lh-lg">
      <li class="mb-2"><i class="bi bi-truck me-3 fs-5 text-dark"></i> Miễn phí vận chuyển toàn quốc.</li>
      <li class="mb-2"><i class="bi bi-arrow-return-left me-3 fs-5 text-dark"></i> Đổi trả dễ dàng trong 30 ngày.</li>
      <li><i class="bi bi-shield-check me-3 fs-5 text-dark"></i> Bảo hành chính hãng 1 năm.</li>
    </ul>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { type Product } from '../../data/db'

defineProps<{ product: Product }>()
defineEmits(['add-to-cart'])

const quantity = ref(1)
</script>

<style scoped>
.cursor-pointer { cursor: pointer; }
.add-to-cart-btn { transition: transform 0.2s ease, background-color 0.2s ease; }
.add-to-cart-btn:hover { transform: translateY(-3px); background-color: #222; }
</style>