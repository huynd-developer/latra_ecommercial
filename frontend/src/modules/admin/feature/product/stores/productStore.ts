import { defineStore } from 'pinia';
import { ref } from 'vue';
import { productService } from '../services/productService';
import { useAppStore } from '@/common/store/app.store';
import type { Product } from '../types/product.type';

export const useProductStore = defineStore('productStore', () => {
  const products = ref<Product[]>([]);
  const brandList = ref<any[]>([]);
  const categoryList = ref<any[]>([]);
  const isLoading = ref(false);
  const appStore = useAppStore();

  const fetchProducts = async () => {
    isLoading.value = true;
    try {
      products.value = await productService.getProducts();
    } catch (err: any) {
      appStore.notifyError(err.response?.data?.message || 'Không thể tải danh sách sản phẩm');
    } finally {
      isLoading.value = false;
    }
  };

  const fetchDropdowns = async () => {
    try {
      const [brands, categories] = await Promise.all([
        productService.getBrands(),
        productService.getCategories()
      ]);
      brandList.value = brands;
      categoryList.value = categories;
    } catch (err: any) {
      console.error('Lỗi tải Dropdown:', err);
    }
  };

  return {
    products,
    brandList,
    categoryList,
    isLoading,
    fetchProducts,
    fetchDropdowns
  };
});