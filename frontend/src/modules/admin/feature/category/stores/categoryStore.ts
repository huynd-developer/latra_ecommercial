// frontend/src/modules/admin/feature/category/stores/categoryStore.ts
import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { categoryService } from '../services/categoryService';
import { useAppStore } from '@/common/store/app.store';
import type { Category } from '../types/category.type';

export const useCategoryStore = defineStore('categoryStore', () => {
  const categories = ref<Category[]>([]);
  const isLoading = ref(false);
  const appStore = useAppStore();

  const fetchCategories = async () => {
    isLoading.value = true;
    try {
      categories.value = await categoryService.getAllCategories();
    } catch (err: any) {
      appStore.notifyError('Lỗi tải danh sách Danh mục');
    } finally {
      isLoading.value = false;
    }
  };

  // Helper: Lọc danh mục đang hoạt động (để hiển thị lên dropdown)
  const activeCategories = computed(() => {
    return categories.value.filter(c => c.status === 1);
  });

  // Helper: Lấy tên danh mục cha để hiển thị lên bảng
  const getParentName = (parentId: number | null) => {
    if (!parentId) return '--- (Danh mục gốc)';
    const parent = categories.value.find(c => c.id === parentId);
    return parent ? parent.name : 'Không xác định';
  };

  return { categories, activeCategories, isLoading, fetchCategories, getParentName };
});