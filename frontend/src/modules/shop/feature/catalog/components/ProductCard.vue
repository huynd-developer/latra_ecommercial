<template>
  <div class="product-luxury-card h-100 d-flex flex-column position-relative">
    
    <div v-if="isFlashSale" class="position-absolute top-0 start-0 m-2 z-3">
      <span class="badge badge-sale text-uppercase px-2 py-1"><i class="bi bi-lightning-fill"></i> Giảm Sốc</span>
    </div>

    <div class="image-container overflow-hidden position-relative mb-3 d-flex justify-content-center align-items-center">
      <router-link :to="'/product/' + product.id" class="d-block w-100 h-100 p-4">
        <img :src="getImageUrl(product.mainImageUrl)" class="img-fluid w-100 h-100 object-fit-contain main-img" alt="Product">
      </router-link>

      <button @click.stop.prevent="emit('quick-view', product)" class="quick-view-btn d-flex align-items-center justify-content-center border-0 shadow-sm" aria-label="Quick View">
        <svg xmlns="http://www.w3.org/2000/svg" width="15" height="15" fill="currentColor" class="bi bi-search" viewBox="0 0 16 16">
          <path d="M11.742 10.344a6.5 6.5 0 1 0-1.397 1.398h-.001q.044.06.098.115l3.85 3.85a1 1 0 0 0 1.415-1.414l-3.85-3.85a1 1 0 0 0-.115-.1zM12 6.5a5.5 5.5 0 1 1-11 0 5.5 5.5 0 0 1 11 0"/>
        </svg>
      </button>

      <div class="add-to-cart-overlay text-center font-weight-semibold">
        {{ (product.variants && product.variants.length > 1) ? 'Choose Options' : 'Add To Cart' }}
      </div>
    </div>

    <div class="product-info-box text-start d-flex flex-column flex-grow-1">
      <div class="brand-label text-uppercase mb-1">{{ getBrandName(product.brandId) }}</div>
      
      <router-link :to="'/product/' + product.id" class="text-decoration-none text-dark flex-grow-1">
        <h6 class="product-title line-clamp-2 mb-2">{{ product.name }}</h6>
      </router-link>
      
      <div class="price-wrap mt-auto d-flex align-items-center gap-2">
        <span class="price-current">{{ formatMoney(minPrice) }}</span>
        <span class="price-old text-decoration-line-through" style="font-size: 0.8rem; color: #999;">
          {{ formatMoney(minPrice * 1.15) }}
        </span>
      </div>

      <div class="color-swatches d-flex gap-1.5 mt-2">
        <span class="swatch-dot swatch-matte-black active"></span>
        <span class="swatch-dot swatch-ash-gray"></span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';
import type { Product } from '@/modules/admin/feature/product/types/product.type';

const props = defineProps<{ product: Product; brandList?: any[]; }>();
const emit = defineEmits<{ (e: 'quick-view', product: Product): void }>();

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
.product-luxury-card {
  background: transparent;
  background-color: #ffffff;
}
.image-container {
  background-color: #f4f4f4 !important;
  border-radius: 10px;
  aspect-ratio: 1 / 1;
  transition: background-color 0.35s ease;
}
.main-img {
  transition: transform 0.5s cubic-bezier(0.25, 0.46, 0.45, 0.94);
}
.quick-view-btn {
  position: absolute;
  top: 12px;
  right: 12px;
  width: 36px;
  height: 36px;
  background-color: #ffffff;
  color: #222222;
  border-radius: 50%;
  opacity: 0;
  transform: scale(0.85);
  transition: all 0.3s cubic-bezier(0.25, 0.46, 0.45, 0.94);
  z-index: 5;
}
.quick-view-btn:hover {
  background-color: #111111;
  color: #ffffff;
}
.add-to-cart-overlay {
  position: absolute;
  bottom: 12px;
  left: 12px;
  right: 12px;
  background-color: #ffffff;
  color: #111111;
  padding: 10px 0;
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 0.5px;
  text-transform: uppercase;
  border-radius: 6px;
  opacity: 0;
  transform: translateY(12px);
  transition: all 0.3s cubic-bezier(0.25, 0.46, 0.45, 0.94);
  cursor: pointer;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  z-index: 4;
}
.add-to-cart-overlay:hover {
  background-color: #111111;
  color: #ffffff;
}

.product-luxury-card:hover .image-container {
  background-color: #eaeaea !important; 
}
.product-luxury-card:hover .main-img {
  transform: scale(1.04);
}
.product-luxury-card:hover .quick-view-btn {
  opacity: 1;
  transform: scale(1);
}
.product-luxury-card:hover .add-to-cart-overlay {
  opacity: 1;
  transform: translateY(0);
}

.badge-sale {
  background-color: #e23e2a;
  color: #ffffff;
  font-size: 10px;
  font-weight: 600;
  border-radius: 4px;
}

.product-info-box {
  padding: 4px 2px 0 2px;
}
.brand-label {
  font-size: 11px;
  color: #777777;
  font-weight: 500;
  letter-spacing: 0.05em;
}
.product-title {
  font-size: 14px;
  font-weight: 600;
  color: #111111;
  line-height: 1.4;
  transition: opacity 0.2s;
}
.product-luxury-card:hover .product-title {
  opacity: 0.8; 
}

.price-current {
  color: #d9381e;
  font-weight: 700;
  font-size: 15px;
}

.swatch-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  display: inline-block;
  cursor: pointer;
  position: relative;
}
.swatch-dot.active::after {
  content: '';
  position: absolute;
  top: -3px;
  left: -3px;
  right: -3px;
  bottom: -3px;
  border: 1px solid #111111;
  border-radius: 50%;
}
.swatch-matte-black { background-color: #2b2b2b; }
.swatch-ash-gray { background-color: #b9bbb6; }

.line-clamp-2 { 
  display: -webkit-box; 
  line-clamp: 2; 
  -webkit-line-clamp: 2; 
  -webkit-box-orient: vertical; 
  overflow: hidden; 
}

@media (max-width: 768px) {
  .quick-view-btn {
    width: 32px;
    height: 32px;
    top: 8px;
    right: 8px;
  }
  .add-to-cart-overlay {
    padding: 8px 0;
    font-size: 11px;
    bottom: 8px;
    left: 8px;
    right: 8px;
  }
  .product-title {
    font-size: 13px;
  }
  .price-current {
    font-size: 13px;
  }
}
</style>