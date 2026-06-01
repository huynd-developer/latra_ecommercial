<template>
  <div class="container-fluid py-4 bg-light min-vh-100">
    <div class="d-flex flex-column flex-md-row justify-content-between align-items-md-center mb-4 gap-3">
      <div>
        <h3 class="fw-bold text-dark mb-0">Quản lý Thương hiệu</h3>
        <span class="text-muted small">Quản lý danh sách các hãng sản xuất</span>
      </div>
      
      <div class="d-flex gap-2">
        <div class="input-group" style="max-width: 300px;">
          <span class="input-group-text bg-white border-end-0"><i class="bi bi-search text-muted"></i></span>
          <input 
            v-model="searchQuery" 
            type="text" 
            class="form-control border-start-0 ps-0 shadow-none" 
            placeholder="Tìm tên thương hiệu..."
          >
        </div>
        
        <button v-permission="['OWNER', 'MANAGER']" @click="openModalForCreate" class="btn btn-dark d-flex align-items-center gap-2 shadow-sm text-nowrap">
          <i class="bi bi-plus-lg"></i> Thêm mới
        </button>
      </div>
    </div>

    <div v-if="brandStore.error" class="alert alert-danger border-start border-danger border-4 d-flex align-items-center mb-4" role="alert">
      <i class="bi bi-exclamation-triangle-fill me-3 fs-5"></i>
      <div>{{ brandStore.error }}</div>
    </div>

    <BrandList 
      :brands="filteredBrands" 
      :is-loading="brandStore.isLoading"
      @edit="openModalForEdit"
      @delete="handleDeleteBrand"
    />

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
import { useAppStore } from "@/common/store/app.store";
import type { Brand } from '../types/brand.type';
import Swal from 'sweetalert2'; 

import BrandList from '../components/BrandList.vue';
import BrandModal from '../components/BrandModal.vue';

const brandStore = useBrandStore();
const appStore = useAppStore();

const isModalOpen = ref(false);
const selectedBrand = ref<Brand | null>(null);
const searchQuery = ref('');

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

const handleDeleteBrand = async (brand: Brand) => {
  const result = await Swal.fire({
    title: 'Xác nhận khóa?',
    text: `Bạn có chắc chắn muốn khóa thương hiệu "${brand.name}" không?`,
    icon: 'warning',
    showCancelButton: true,
    confirmButtonColor: '#d33',
    cancelButtonColor: '#1e293b',
    confirmButtonText: 'Đồng ý khóa',
    cancelButtonText: 'Hủy'
  });

  if (!result.isConfirmed) return;

  try {
    appStore.startLoading();
    await brandService.deleteBrand(brand.id);
    Swal.fire({ icon: 'success', title: 'Đã khóa!', text: 'Thương hiệu đã được khóa thành công.', timer: 1500, showConfirmButton: false });
    await refreshData();
  } catch (error: any) {
    const errorMsg = error.response?.data?.message || 'Lỗi khi khóa thương hiệu!';
    Swal.fire({ icon: 'error', title: 'Không thể khóa', text: errorMsg });
  } finally {
    appStore.stopLoading();
  }
};
</script>