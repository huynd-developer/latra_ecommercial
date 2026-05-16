<!-- frontend/src/modules/admin/feature/brand/views/BrandView.vue -->
<template>
  <div class="container-fluid py-4 bg-light min-vh-100">
    <!-- Header & Công cụ -->
    <div class="d-flex flex-column flex-md-row justify-content-between align-items-md-center mb-4 gap-3">
      <div>
        <h3 class="fw-bold text-dark mb-0">Quản lý Thương hiệu</h3>
        <span class="text-muted small">Quản lý danh sách các hãng sản xuất</span>
      </div>
      
      <div class="d-flex gap-2">
        <!-- Thanh tìm kiếm -->
        <div class="input-group" style="max-width: 300px;">
          <span class="input-group-text bg-white border-end-0"><i class="bi bi-search text-muted"></i></span>
          <input 
            v-model="searchQuery" 
            type="text" 
            class="form-control border-start-0 ps-0" 
            placeholder="Tìm tên thương hiệu..."
          >
        </div>
        
        <!-- Nút Thêm mới -->
        <button @click="openModalForCreate" class="btn btn-dark d-flex align-items-center gap-2 shadow-sm text-nowrap">
          <i class="bi bi-plus-lg"></i> Thêm mới
        </button>
      </div>
    </div>

    <!-- Cảnh báo lỗi từ Store -->
    <div v-if="brandStore.error" class="alert alert-danger border-start border-danger border-4 d-flex align-items-center mb-4" role="alert">
      <i class="bi bi-exclamation-triangle-fill me-3 fs-5"></i>
      <div>{{ brandStore.error }}</div>
    </div>

    <!-- Data Table -->
    <BrandList 
      :brands="filteredBrands" 
      :is-loading="brandStore.isLoading"
      @edit="openModalForEdit"
      @delete="handleDeleteBrand"
    />

    <!-- Modal Form -->
    <BrandModal 
      v-if="isModalOpen"
      :brand-selected="selectedBrand"
      @close="closeModal"
      @refresh="refreshData"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useBrandStore } from '../stores/brandStore';
import { brandService } from '../services/brandService';
import type { Brand } from '../types/brand.type';
import Swal from 'sweetalert2'; // Dùng SweetAlert thay cho alert thường

import BrandList from '../components/BrandList.vue';
import BrandModal from '../components/BrandModal.vue';

const brandStore = useBrandStore();

// Quản lý state
const isModalOpen = ref(false);
const selectedBrand = ref<Brand | null>(null);
const searchQuery = ref('');

// Computed property để LỌC (SEARCH) danh sách
const filteredBrands = computed(() => {
  if (!searchQuery.value) return brandStore.brands;
  const lowerCaseQuery = searchQuery.value.toLowerCase();
  return brandStore.brands.filter(b => 
    b.name.toLowerCase().includes(lowerCaseQuery) || 
    b.country.toLowerCase().includes(lowerCaseQuery)
  );
});

onMounted(() => { refreshData(); });

const refreshData = async () => { await brandStore.fetchBrands(); };

const openModalForCreate = () => {
  selectedBrand.value = null;
  isModalOpen.value = true;
};

const openModalForEdit = (brand: Brand) => {
  selectedBrand.value = { ...brand };
  isModalOpen.value = true;
};

const closeModal = () => {
  isModalOpen.value = false;
  selectedBrand.value = null;
};

const handleDeleteBrand = async (id: number) => {
  try {
    await brandService.deleteBrand(id);
    Swal.fire({ icon: 'success', title: 'Đã khóa!', text: 'Thương hiệu đã được khóa thành công.', timer: 1500, showConfirmButton: false });
    await refreshData();
  } catch (error: any) {
    const errorMsg = error.response?.data?.message || 'Lỗi khi khóa thương hiệu!';
    Swal.fire({ icon: 'error', title: 'Không thể khóa', text: errorMsg });
  }
};
</script>