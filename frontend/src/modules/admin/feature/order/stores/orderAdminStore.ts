import { defineStore } from 'pinia';
import { ref } from 'vue';
import { orderAdminService } from '../services/orderAdminService';
import { useAppStore } from '@/common/store/app.store';
import type { Order } from '../types/order.type';

export const useOrderAdminStore = defineStore('orderAdminStore', () => {
  const orders = ref<Order[]>([]);
  const selectedOrder = ref<Order | null>(null);
  const isLoading = ref(false);
  const appStore = useAppStore();

  const fetchOrders = async () => {
    isLoading.value = true;
    try {
      orders.value = await orderAdminService.getOrders();
    } catch (err) {
      appStore.notifyError('Lỗi tải danh sách đơn hàng');
    } finally {
      isLoading.value = false;
    }
  };

  const fetchOrderDetail = async (id: number) => {
    try {
      selectedOrder.value = await orderAdminService.getOrderDetail(id);
    } catch (err) {
      appStore.notifyError('Lỗi tải chi tiết đơn hàng');
    }
  };

  return { orders, selectedOrder, isLoading, fetchOrders, fetchOrderDetail };
});