<script setup lang="ts">
import { usePos } from '../composables/usePos'

const { filteredProducts, autoPickImei, handleImageError, formatMoney } = usePos()
</script>

<template>
  <div class="flex-grow-1 overflow-y-auto pe-1 custom-scrollbar" style="max-height: calc(100vh - 120px);">
    <div class="row row-cols-2 row-cols-md-3 row-cols-xl-4 g-3">
      <div v-for="item in filteredProducts" :key="item.variantId" class="col">
        <div 
          class="card h-100 product-card rounded-4 shadow-sm hover-lift bg-white position-relative overflow-hidden"
          :class="{ 'grayscale opacity-60': item.stock === 0 }"
          @click="autoPickImei(item.variantId)"
        >
          <div class="ratio ratio-1x1 bg-slate-50 d-flex align-items-center justify-content-center p-3 overflow-hidden">
            <img 
              :src="item.image" 
              class="img-fluid product-img object-fit-contain rounded-3" 
              @error="handleImageError" 
              alt="product"
            />
          </div>
          
          <div class="card-body p-3 d-flex flex-column justify-content-between">
            <div>
              <h6 class="card-title text-slate-900 fw-bold mb-1 text-truncate" :title="item.name">{{ item.name }}</h6>
              <div class="d-flex gap-1 mb-2">
                <span class="badge bg-slate-100 text-slate-600 border border-slate-200 rounded-pill px-2 py-1 text-xs fw-semibold">{{ item.storage }}</span>
                <span class="badge bg-slate-100 text-slate-600 border border-slate-200 rounded-pill px-2 py-1 text-xs fw-semibold">{{ item.color }}</span>
              </div>
            </div>
            
            <div class="d-flex align-items-center justify-content-between mt-2 pt-2 border-top border-slate-50">
              <span class="text-slate-900 fw-black tracking-tight">{{ formatMoney(item.price) }}</span>
              <span 
                class="badge rounded-pill px-2 py-1 text-xs"
                :class="item.stock > 0 ? 'bg-success-subtle text-success fw-bold' : 'bg-danger-subtle text-danger fw-bold'"
              >
                {{ item.stock > 0 ? `Còn ${item.stock} máy` : 'Hết hàng' }}
              </span>
            </div>
          </div>

          <div class="position-absolute end-0 top-0 m-2">
            <button class="btn btn-light-peel d-flex align-items-center justify-content-center p-0" :disabled="item.stock === 0">
              <i class="bi bi-plus-lg fs-6 fw-bold"></i>
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>