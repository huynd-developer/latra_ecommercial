<template>
  <div class="product-card position-relative">
    <div class="image-wrapper bg-light rounded-2 overflow-hidden position-relative mb-3" style="aspect-ratio: 1/1; background-color: #e8e8e8 !important;">
      <router-link :to="'/product/' + product.id" class="d-block w-100 h-100 text-decoration-none">
        <img :src="product.image" class="img-fluid w-100 h-100 object-fit-contain p-4 main-img mix-blend-multiply" alt="Product">
        <img :src="(product.gallery && product.gallery.length > 0) ? product.gallery[0] : product.image" 
             class="img-fluid w-100 h-100 object-fit-contain p-4 hover-img position-absolute top-0 start-0 mix-blend-multiply" alt="Product Hover">
      </router-link>
      
      <button class="btn shadow-sm fw-bold rounded-pill add-to-cart-btn position-absolute start-50 translate-middle-x" 
              data-bs-toggle="offcanvas" data-bs-target="#globalCartSidebar" 
              @click="$emit('add-to-cart', product)">
        Thêm Vào Giỏ
      </button>

      <button class="btn btn-white bg-white shadow-sm rounded-circle d-flex justify-content-center align-items-center quick-view-btn position-absolute top-0 end-0 m-3" 
              style="width: 40px; height: 40px;" data-bs-toggle="offcanvas" data-bs-target="#quickViewSidebar" 
              @click="$emit('quick-view', product)">
        <i class="bi bi-search text-dark"></i>
      </button>
    </div>

    <div class="product-info text-start">
      <div class="text-secondary small mb-1" style="font-size: 0.8rem;">{{ product.type }}</div>
      <router-link :to="'/product/' + product.id" class="text-decoration-none text-dark">
        <h6 class="fw-medium mb-2 product-title lh-base" style="font-size: 0.9rem;">{{ product.name }}</h6>
      </router-link>
      <div class="price-wrap fw-bold" style="font-size: 0.9rem;">
        <span style="color: #d12a2f;" class="me-2">${{ product.price.toFixed(2) }}</span>
        <span class="text-decoration-line-through text-muted fw-normal" v-if="product.oldPrice">${{ product.oldPrice.toFixed(2) }}</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
// ĐÃ FIX: Dùng đường dẫn tương đối trỏ chính xác vào file db
import { type Product } from '../../data/db'
defineProps<{ product: Product }>()
defineEmits(['add-to-cart', 'quick-view'])
</script>

<style scoped>
.product-card { cursor: pointer; font-family: 'Inter', sans-serif;}
.mix-blend-multiply { mix-blend-mode: multiply; }
/* --- FIX ẢNH CHỒNG ĐÈ --- */
.product-card .main-img { opacity: 1; transition: opacity 0.3s ease; }
.product-card:hover .main-img { opacity: 0; }
.product-card .hover-img { opacity: 0; transition: opacity 0.3s ease; }
.product-card:hover .hover-img { opacity: 1; }
/* ------------------------ */
.product-card .add-to-cart-btn { opacity: 0; bottom: 10px; width: 85%; padding: 12px 0; transition: all 0.3s ease; z-index: 2; font-size: 0.9rem; background-color: #ffffff; color: #000000; border: none; }
.product-card:hover .add-to-cart-btn { opacity: 1; bottom: 20px; }
.product-card .add-to-cart-btn:hover { background-color: #000000 !important; color: #ffffff !important; }
.product-card:hover .product-title { text-decoration: underline; text-underline-offset: 3px; }
.quick-view-btn { opacity: 0; transform: scale(0.9); transition: all 0.2s ease; z-index: 3; }
.product-card:hover .quick-view-btn { opacity: 1; transform: scale(1); }
.quick-view-btn:hover { transform: scale(1.1) !important; }
</style>