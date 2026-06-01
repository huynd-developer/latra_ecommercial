<script setup lang="ts">
import type { Product } from '../types/product.type';

defineProps<{ paginatedData: Product[]; brandList: any[]; }>();

const emit = defineEmits<{
  (e: 'edit', product: Product): void;
  (e: 'upload-image', product: Product): void;
  (e: 'stop-selling', id: number): void;
}>();

const getBrandName = (id: number, brandList: any[]) => {
  const brand = brandList.find((b) => b.id === id);
  return brand ? brand.name : `Hãng #${id}`;
};

const calculateTotalStock = (variants: any[]) => {
  if (!variants || variants.length === 0) return 0;
  return variants.reduce((total, v) => total + (v.stock || 0), 0);
};

// 👉 Fix link Cloudinary
const getImageUrl = (url: string) => {
  if (!url) return '';
  return url.startsWith('http') ? url : `http://localhost:8080/uploads/${url}`;
};
</script>

<template>
  <div class="p-0 flex-grow-1 overflow-auto custom-scrollbar">
    <table class="table align-middle mb-0 table-hover border-0">
      <thead class="bg-white text-muted small fw-medium tracking-wider" style="position: sticky; top: 0; z-index: 1">
        <tr>
          <th class="ps-4 py-3 border-bottom font-weight-normal" width="80">Hình Ảnh</th>
          <th class="py-3 border-bottom font-weight-normal">Mã SP</th>
          <th class="py-3 border-bottom font-weight-normal">Tên Sản Phẩm</th>
          <th class="py-3 border-bottom font-weight-normal">Thương Hiệu</th>
          <th class="py-3 border-bottom font-weight-normal text-center">Tồn Kho</th>
          <th class="py-3 border-bottom font-weight-normal">Trạng Thái</th>
          <th class="text-end pe-4 py-3 border-bottom font-weight-normal">Thao tác</th>
        </tr>
      </thead>
      <tbody>
        <tr v-if="paginatedData.length === 0">
          <td colspan="7" class="text-center py-5 text-muted fw-medium">Chưa có sản phẩm nào.</td>
        </tr>
        <tr v-for="product in paginatedData" :key="product.id">
          <td class="ps-4 py-3 border-bottom border-light">
            <img v-if="product.mainImageUrl" :src="getImageUrl(product.mainImageUrl)" alt="img" style="width: 48px; height: 48px; object-fit: cover; border-radius: 12px; border: 1px solid rgba(0,0,0,0.05);" />
            <div v-else class="bg-light text-muted d-flex align-items-center justify-content-center" style="width: 48px; height: 48px; border-radius: 12px; font-size: 10px;">Trống</div>
          </td>
          <td class="py-3 border-bottom border-light text-muted fw-medium small">#{{ product.id }}</td>
          <td class="py-3 border-bottom border-light text-dark fw-semibold">{{ product.name }}</td>
          <td class="py-3 border-bottom border-light">
            <span class="badge bg-light text-dark border-0 rounded-pill px-3 py-2 fw-normal">{{ getBrandName(product.brandId, brandList) }}</span>
          </td>
          <td class="py-3 border-bottom border-light text-center">
            <span class="fw-semibold" :class="calculateTotalStock(product.variants) > 0 ? 'text-primary' : 'text-danger'">
              {{ calculateTotalStock(product.variants) }}
            </span>
          </td>
          <td class="py-3 border-bottom border-light">
            <span v-if="product.status === 1" class="badge bg-success bg-opacity-10 text-success rounded-pill px-3 py-2 fw-normal">Đang bán</span>
            <span v-else class="badge bg-danger bg-opacity-10 text-danger rounded-pill px-3 py-2 fw-normal">Ngừng bán</span>
          </td>
          <td class="text-end pe-4 py-3 border-bottom border-light">
            <button v-permission="['OWNER', 'MANAGER']" @click="emit('upload-image', product)" class="btn btn-sm btn-light rounded-circle p-2 mx-1 text-primary" title="Tải ảnh">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="3" width="18" height="18" rx="2" ry="2"></rect><circle cx="8.5" cy="8.5" r="1.5"></circle><polyline points="21 15 16 10 5 21"></polyline></svg>
            </button>
            
            <button v-permission="['OWNER', 'MANAGER']" @click="emit('edit', product)" class="btn btn-sm btn-light rounded-circle p-2 mx-1 text-dark" title="Sửa">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path></svg>
            </button>
            
            <button v-permission="['OWNER']" v-if="product.status === 1" @click="emit('stop-selling', product.id)" class="btn btn-sm btn-light rounded-circle p-2 mx-1 text-danger" title="Tạm ngừng">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><rect x="6" y="4" width="4" height="16"></rect><rect x="14" y="4" width="4" height="16"></rect></svg>
            </button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<style scoped>
.custom-scrollbar::-webkit-scrollbar { width: 6px; height: 6px; }
.custom-scrollbar::-webkit-scrollbar-thumb { background: #e2e8f0; border-radius: 10px; }
.btn-light { background-color: #f8fafc; border: 1px solid transparent; transition: all 0.2s; }
.btn-light:hover { background-color: #fff; border-color: #e2e8f0; box-shadow: 0 2px 4px rgba(0,0,0,0.05); transform: translateY(-1px); }
</style>