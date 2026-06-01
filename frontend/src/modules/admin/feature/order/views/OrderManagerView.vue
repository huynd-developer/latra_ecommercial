<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import { useOrderAdminStore } from '../stores/orderAdminStore';
import OrderDetailModal from '../components/OrderDetailModal.vue';
import { formatCurrency, formatDate } from '@/common/utils/format';

const store = useOrderAdminStore();
const searchQuery = ref('');
const isModalOpen = ref(false);

onMounted(() => { store.fetchOrders(); });

const filteredOrders = computed(() => {
  if (!searchQuery.value) return store.orders;
  const q = searchQuery.value.toLowerCase();
  return store.orders.filter(o => 
    o.id.toString().includes(q) || 
    o.customerName.toLowerCase().includes(q) || 
    o.customerPhone.includes(q)
  );
});

const openDetail = async (id: number) => {
  await store.fetchOrderDetail(id);
  isModalOpen.value = true;
};

const handleRefreshDetail = async () => {
  if (store.selectedOrder) {
    await store.fetchOrderDetail(store.selectedOrder.id);
  }
  store.fetchOrders(); // Refresh table
};

// 🎁 Mẹo thực tế: Hàm tô màu trạng thái cho dễ nhìn
const getStatusBadge = (status: string) => {
  switch (status) {
    case 'PENDING': return 'bg-warning text-dark';
    case 'PREPARING': return 'bg-info text-white';
    case 'DELIVERING': return 'bg-primary text-white';
    case 'COMPLETED': case 'DELIVERED': return 'bg-success text-white';
    case 'CANCELLED': case 'RETURNED': return 'bg-danger text-white';
    case 'REFUNDED': return 'bg-secondary text-white';
    default: return 'bg-slate-200 text-slate-800';
  }
};
</script>

<template>
  <div class="h-100 d-flex flex-column g-4 p-4 bg-slate-50">
    <div class="d-flex justify-content-between align-items-center mb-4 border-b pb-3">
      <div>
        <h4 class="fw-bolder text-slate-800 mb-1">Quản lý Hóa Đơn & Vận Chuyển</h4>
        <div class="text-slate-500 small">Đồng bộ tự động với kho IMEI và trạng thái Giao hàng</div>
      </div>
      <div class="position-relative" style="width: 320px;">
        <input v-model="searchQuery" type="text" class="form-control rounded-pill ps-4" placeholder="Tìm mã đơn, tên, sđt...">
      </div>
    </div>

    <div class="bg-white rounded-xl shadow-sm border overflow-auto flex-grow-1">
      <table class="table table-hover align-middle mb-0">
        <thead class="bg-light text-muted small text-uppercase sticky-top">
          <tr>
            <th class="py-3 ps-4">Mã Đơn</th>
            <th>Khách Hàng</th>
            <th>Ngày Tạo</th>
            <th>Tổng Tiền</th>
            <th>Thanh Toán</th>
            <th>Trạng Thái</th>
            <th class="pe-4 text-end">Thao tác</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="order in filteredOrders" :key="order.id" @click="openDetail(order.id)" class="cursor-pointer">
            <td class="ps-4 fw-bold">#{{ order.id }}</td>
            <td>
              <div class="fw-bold">{{ order.customerName }}</div>
              <div class="small text-muted">{{ order.customerPhone }}</div>
            </td>
            <td class="small">{{ formatDate(order.createdAt) }}</td>
            <td class="fw-bold text-red-600">{{ formatCurrency(order.totalAmount) }}</td>
            <td>
              <span class="badge" :class="order.paymentMethod === 'VNPAY' ? 'bg-blue-100 text-blue-700' : 'bg-green-100 text-green-700'">
                {{ order.paymentMethod }}
              </span>
            </td>
            <td><span class="badge" :class="getStatusBadge(order.status)">{{ order.status }}</span></td>
            <td class="pe-4 text-end"><button class="btn btn-sm btn-outline-dark rounded-pill">Chi tiết</button></td>
          </tr>
        </tbody>
      </table>
    </div>

    <OrderDetailModal 
      v-if="isModalOpen" 
      :order="store.selectedOrder" 
      @close="isModalOpen = false" 
      @refresh="handleRefreshDetail" 
    />
  </div>
</template>