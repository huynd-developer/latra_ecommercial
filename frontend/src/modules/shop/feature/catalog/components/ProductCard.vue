<!-- frontend/src/modules/shop/feature/catalog/components/ProductCard.vue -->
<template>
  <div class="product-card position-relative shadow-sm rounded-4 bg-white p-3 h-100 d-flex flex-column transition-all hover-lift">
    
    <!-- Huy hiệu Flash Sale (Gắn tạm logic hiển thị) -->
    <div v-if="isFlashSale" class="position-absolute top-0 start-0 m-2 z-2">
      <span class="badge bg-danger rounded-pill px-2 py-1 shadow-sm"><i class="bi bi-lightning-fill"></i> Giảm Sốc</span>
    </div>

    <!-- Hình ảnh -->
    <div class="image-wrapper bg-light rounded-3 overflow-hidden position-relative mb-3 d-flex justify-content-center align-items-center" style="aspect-ratio: 1/1;">
      <router-link :to="'/product/' + product.id" class="d-block w-100 h-100">
        <img :src="getImageUrl(product.mainImageUrl)" class="img-fluid w-100 h-100 object-fit-contain p-3 main-img mix-blend-multiply" alt="Product">
      </router-link>
    </div>

    <!-- Thông tin -->
    <div class="product-info text-start d-flex flex-column flex-grow-1">
      <div class="text-secondary small mb-1 text-uppercase fw-bold" style="font-size: 0.7rem;">{{ getBrandName(product.brandId) }}</div>
      <router-link :to="'/product/' + product.id" class="text-decoration-none text-dark flex-grow-1">
        <h6 class="fw-bold mb-2 product-title line-clamp-2" style="font-size: 0.95rem;">{{ product.name }}</h6>
      </router-link>
      
      <div class="price-wrap mt-auto">
        <span class="fw-black text-danger fs-5">{{ formatMoney(minPrice) }}</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';
import type { Product } from '@/modules/admin/feature/product/types/product.type';

const props = defineProps<{ product: Product; brandList?: any[]; }>();
const emit = defineEmits<{ (e: 'quick-view', product: Product): void }>();

// Tạm gán isFlashSale = false, sau này nối API sẽ bắt ID
const isFlashSale = ref(false); 

const getImageUrl = (url?: string) => {
  if (!url) return 'https://via.placeholder.com/300?text=No+Image';
  return url.startsWith('http') ? url : `http://localhost:8080/uploads/${url}`;
};

const getBrandName = (id: number) => {
  if (!props.brandList) return 'Sản phẩm';
  const b = props.brandList.find(x => x.id === id);
  return b ? b.name : 'Sản phẩm';
};

const formatMoney = (val: number) => new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(val);

const minPrice = computed(() => {
  if (!props.product.variants || props.product.variants.length === 0) return 0;
  return Math.min(...props.product.variants.map(v => v.price));
});
</script>

<style scoped>
.product-card { cursor: pointer; border: 1px solid #f1f5f9; }
.hover-lift:hover { transform: translateY(-5px); box-shadow: 0 10px 25px rgba(0,0,0,0.05) !important; border-color: #e2e8f0; }
.mix-blend-multiply { mix-blend-mode: multiply; }

/* Animation cho nút Mua */
.add-to-cart-btn { opacity: 0; bottom: -10px; transition: all 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275); z-index: 2; font-size: 0.85rem; }
.product-card:hover .add-to-cart-btn { opacity: 1; bottom: 10px; }

.product-title { transition: color 0.2s; }
.product-card:hover .product-title { color: #dc3545 !important; }
/* Trong phần <style> của ProductCard.vue, sửa class .line-clamp-2 thành: */
.line-clamp-2 { 
  display: -webkit-box; 
  line-clamp: 2; /* Thêm chuẩn mới này */
  -webkit-line-clamp: 2; 
  -webkit-box-orient: vertical; 
  overflow: hidden; 
}
</style>