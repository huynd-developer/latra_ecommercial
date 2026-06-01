<template>
  <div class="container-fluid py-3 px-0">
    <div v-if="isLoading" class="d-flex justify-content-center align-items-center py-5 text-secondary">
      <div class="spinner-border spinner-border-sm me-2" role="status"></div>
      <span class="fw-medium">Đang tải dữ liệu...</span>
    </div>

    <div v-else-if="brands.length === 0" class="text-center py-5 text-muted bg-white rounded-4 border shadow-sm">
      <i class="bi bi-inbox fs-1 opacity-50 mb-3 d-block"></i>
      <h6 class="fw-medium">Không tìm thấy thương hiệu nào</h6>
    </div>

    <div v-else class="row row-cols-1 row-cols-sm-2 row-cols-md-3 row-cols-lg-4 g-4">
      <div class="col" v-for="brand in brands" :key="brand.id">
        <div class="card h-100 border-0 bg-white rounded-4 brand-card position-relative shadow-sm">
          
          <span 
            class="badge position-absolute top-0 end-0 m-3 rounded-pill fw-normal px-2 py-1"
            :class="brand.status === 1 ? 'bg-success bg-opacity-10 text-success' : 'bg-danger bg-opacity-10 text-danger'"
          >
            {{ brand.status === 1 ? 'Hoạt động' : 'Đã khóa' }}
          </span>

          <div class="card-body text-center pt-4 pb-2">
            <div class="mb-3 d-flex justify-content-center">
              <img 
                v-if="brand.logo" 
                :src="getImageUrl(brand.logo)" 
                alt="Logo" 
                class="rounded-circle object-fit-cover shadow-sm"
                style="width: 72px; height: 72px; border: 1px solid rgba(0,0,0,0.05);"
              />
              <div 
                v-else 
                class="rounded-circle bg-light d-flex align-items-center justify-content-center text-muted small fw-medium"
                style="width: 72px; height: 72px;"
              >
                Trống
              </div>
            </div>

            <h6 class="card-title fw-bold text-truncate mb-1" :title="brand.name">
              {{ brand.name }}
            </h6>
            
            <p class="card-text text-muted small d-flex align-items-center justify-content-center gap-1 mb-0 opacity-75">
              <i class="bi bi-geo-alt"></i> {{ brand.country }}
            </p>
          </div>

          <div class="card-footer bg-transparent border-0 pb-3 pt-0 px-3 d-flex justify-content-center gap-2">
            <button v-permission="['OWNER', 'MANAGER']" @click="$emit('edit', brand)" class="btn btn-sm btn-light rounded-pill px-3 fw-medium">
              <i class="bi bi-pencil me-1"></i> Sửa
            </button>
            
            <button 
              v-permission="['OWNER']"
              v-if="brand.status === 1" 
              @click="$emit('delete', brand)" 
              class="btn btn-sm btn-light text-danger rounded-pill px-3 fw-medium"
            >
              <i class="bi bi-lock me-1"></i> Khóa
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { Brand } from '../types/brand.type';

defineProps<{ brands: Brand[]; isLoading: boolean; }>();
defineEmits<{ (e: 'edit', brand: Brand): void; (e: 'delete', brand: Brand): void; }>();

// 🛠️ ĐÃ FIX: Lấy URL từ biến môi trường, không fix cứng localhost nữa
const getImageUrl = (url: string) => {
  if (!url) return '';
  if (url.startsWith('http')) return url;
  
  // Đọc từ file .env, nếu không có thì fallback tạm về localhost
  const baseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';
  return `${baseUrl}/uploads/brands/${url}`;
};
</script>

<style scoped>
.brand-card { transition: all 0.25s ease; }
.brand-card:hover { transform: translateY(-5px); box-shadow: 0 12px 24px rgba(0, 0, 0, 0.08) !important; }
</style>