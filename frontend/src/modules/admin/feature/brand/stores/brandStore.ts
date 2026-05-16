// frontend/src/modules/admin/feature/brand/store/brandStore.ts
import { defineStore } from 'pinia';
import { ref } from 'vue';
import { brandService } from '../services/brandService';
import type { Brand } from '../types/brand.type';

export const useBrandStore = defineStore('brandStore', () => {
  const brands = ref<Brand[]>([]);
  const isLoading = ref(false);
  const error = ref<string | null>(null);

  const fetchBrands = async () => {
    isLoading.value = true;
    error.value = null;
    try {
      brands.value = await brandService.getAllBrands();
    } catch (err: any) {
      error.value = err.response?.data?.message || 'Không thể tải danh sách thương hiệu';
      console.error(err);
    } finally {
      isLoading.value = false;
    }
  };

  return {
    brands,
    isLoading,
    error,
    fetchBrands
  };
});