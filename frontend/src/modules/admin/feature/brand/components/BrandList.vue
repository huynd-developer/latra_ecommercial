<template>
  <div class="container-fluid py-3 px-0">
    <div v-if="isLoading" class="d-flex justify-content-center align-items-center py-5 text-secondary">
      <div class="spinner-border spinner-border-sm me-2" role="status"></div>
      <span class="fw-medium">Đang tải...</span>
    </div>

    <div v-else-if="brands.length === 0" class="text-center py-5 text-muted bg-light rounded-4 border-0">
      <svg class="mb-3 opacity-50" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1" stroke-linecap="round" stroke-linejoin="round">
        <line x1="16.5" y1="9.4" x2="7.5" y2="4.21"></line>
        <path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"></path>
        <polyline points="3.27 6.96 12 12.01 20.73 6.96"></polyline>
        <line x1="12" y1="22.08" x2="12" y2="12"></line>
      </svg>
      <h6 class="fw-medium">Không có dữ liệu</h6>
    </div>

    <div v-else class="row row-cols-1 row-cols-sm-2 row-cols-md-3 row-cols-lg-4 g-4">
      <div class="col" v-for="brand in brands" :key="brand.id">
        <div class="card h-100 border-0 bg-white rounded-4 brand-card position-relative shadow-sm">
          
          <span 
            class="badge position-absolute top-0 end-0 m-3 rounded-pill fw-normal px-2 py-1"
            :class="brand.status === 1 ? 'bg-success bg-opacity-10 text-success' : 'bg-secondary bg-opacity-10 text-secondary'"
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

            <h6 class="card-title fw-semibold text-truncate mb-1" :title="brand.name">
              {{ brand.name }}
            </h6>
            
            <p class="card-text text-muted small d-flex align-items-center justify-content-center gap-1 mb-0 opacity-75">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"></path>
                <circle cx="12" cy="10" r="3"></circle>
              </svg>
              {{ brand.country }}
            </p>
          </div>

          <div class="card-footer bg-transparent border-0 pb-3 pt-0 px-3 d-flex justify-content-center gap-2">
            <button @click="$emit('edit', brand)" class="btn btn-sm btn-light rounded-pill px-3 d-flex align-items-center gap-2">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
                <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
              </svg>
              Sửa
            </button>
            <button 
              v-if="brand.status === 1" 
              @click="handleDelete(brand)" 
              class="btn btn-sm btn-light text-danger rounded-pill px-3 d-flex align-items-center gap-2"
            >
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                <rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect>
                <path d="M7 11V7a5 5 0 0 1 10 0v4"></path>
              </svg>
              Khóa
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
const emit = defineEmits<{ (e: 'edit', brand: Brand): void; (e: 'delete', id: number): void; }>();

// Đã cập nhật: Xử lý hiển thị trực tiếp link từ Cloudinary
const getImageUrl = (url: string) => {
  if (!url) return '';
  // Nếu url đã là link http (cloudinary) thì dùng luôn, nếu là file cũ thì nối chuỗi local (để dự phòng)
  return url.startsWith('http') ? url : `http://localhost:8080/uploads/brands/${url}`;
};

const handleDelete = (brand: Brand) => {
  if (confirm(`Bạn có chắc chắn muốn khóa thương hiệu "${brand.name}" không?`)) {
    emit('delete', brand.id);
  }
};
</script>

<style scoped>
.brand-card {
  transition: all 0.2s cubic-bezier(0.165, 0.84, 0.44, 1);
}
.brand-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.05) !important;
}
</style>