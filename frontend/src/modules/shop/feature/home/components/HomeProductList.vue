<!-- frontend/src/modules/shop/feature/home/components/HomeProductList.vue -->
<template>
  <div class="py-5 bg-white">
    <div class="container px-4 px-lg-5">
      <div class="d-flex justify-content-between align-items-end mb-4 border-bottom pb-3">
        <div>
          <h2 class="fw-bold text-dark display-6" style="letter-spacing: -1px;">Sản Phẩm Mới Nhất</h2>
          <p class="text-secondary mb-0">Cập nhật công nghệ, nâng tầm phong cách.</p>
        </div>
        <router-link to="/shop" class="btn btn-outline-dark rounded-pill px-4 fw-medium text-nowrap d-none d-md-inline-block">
          Xem Tất Cả
        </router-link>
      </div>

      <div class="row g-4">
        <!-- TÁI SỬ DỤNG LẠI COMPONENT CỦA CATALOG -->
        <div class="col-6 col-md-4 col-lg-3" v-for="product in products" :key="product.id">
          <ProductCard :product="product" />
        </div>
      </div>
      
      <!-- Nút xem tất cả cho Mobile -->
      <div class="text-center mt-4 d-md-none">
        <router-link to="/shop" class="btn btn-outline-dark rounded-pill px-5 fw-medium w-100">
          Xem Tất Cả Sản Phẩm
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { Product } from '@/modules/admin/feature/product/types/product.type';
import ProductCard from '../../catalog/components/ProductCard.vue';

defineProps<{ products: Product[] }>();
</script>