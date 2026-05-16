import { defineStore } from 'pinia';
import { ref } from 'vue';
import { catalogService } from '../services/catalogService';
import { useAppStore } from '@/common/store/app.store';
import type { Brand, Category, CatalogFilter } from '../types/catalog.type';
import type { Product } from '@/modules/admin/feature/product/types/product.type';

export const useCatalogStore = defineStore('catalogStore', () => {
  const products = ref<Product[]>([]);
  const brands = ref<Brand[]>([]);
  const categories = ref<Category[]>([]);
  
  const totalItems = ref(0);
  const totalPages = ref(1);
  const isLoading = ref(false);
  const appStore = useAppStore();

  const filters = ref<CatalogFilter>({
    page: 1,
    size: 12,
    keyword: '',
    brandIds: [],
    categoryIds: [],
    sortBy: 'newest'
  });

  const fetchData = async () => {
    isLoading.value = true;
    try {
      const res = await catalogService.getProducts(filters.value);
      products.value = res.products || [];
      totalItems.value = res.totalItems || res.products?.length || 0;
      totalPages.value = res.totalPages || 1;
    } catch (err) {
      appStore.notifyError('Lỗi tải danh sách sản phẩm');
    } finally {
      isLoading.value = false;
    }
  };

  const fetchDropdowns = async () => {
    try {
      const [b, c] = await Promise.all([
        catalogService.getBrands(),
        catalogService.getCategories()
      ]);
      brands.value = b;
      categories.value = c;
    } catch (err) {
      console.error('Lỗi tải Bộ lọc:', err);
    }
  };

  const updateFilters = (newFilters: Partial<CatalogFilter>) => {
    filters.value = { ...filters.value, ...newFilters, page: 1 }; // Reset về trang 1 khi đổi bộ lọc
    fetchData();
  };

  const changePage = (page: number) => {
    filters.value.page = page;
    fetchData();
  };

  return { 
    products, brands, categories, filters, totalItems, totalPages, isLoading, 
    fetchData, fetchDropdowns, updateFilters, changePage 
  };
});