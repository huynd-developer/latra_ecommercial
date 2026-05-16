// frontend/src/modules/admin/feature/inventory/stores/imeiStore.ts
import { defineStore } from 'pinia';
import { ref } from 'vue';
import { imeiService } from '../services/imeiService';
import { useAppStore } from '@/common/store/app.store';
import type { Imei } from '../types/imei.type';

export const useImeiStore = defineStore('imeiStore', () => {
  const imeis = ref<Imei[]>([]);
  const isLoading = ref(false);
  const appStore = useAppStore();

  const fetchImeis = async () => {
    isLoading.value = true;
    try {
      const data = await imeiService.getAllImeis();
      // Đảo ngược mảng để máy mới nhập nằm trên cùng
      imeis.value = data.reverse(); 
    } catch (err: any) {
      appStore.notifyError(err.response?.data?.message || 'Không thể tải danh sách IMEI');
    } finally {
      isLoading.value = false;
    }
  };

  return { imeis, isLoading, fetchImeis };
});