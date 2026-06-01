<script setup lang="ts">
import { ref, onMounted } from "vue";
import { usePromotionStore } from "../stores/promotionStore";
import { promotionService } from "../services/promotionService";
import { useAppStore } from "@/common/store/app.store";
import VoucherModal from "../components/VoucherModal.vue";
import { ROLES } from "@/common/constants/permissions"; // 🔑 Import ROLES

const store = usePromotionStore();
const appStore = useAppStore();

const activeTab = ref<'LIST' | 'ANALYTICS'>('LIST');
const showModal = ref(false);
const selectedVoucher = ref<any>(null);

onMounted(() => { 
  // 1. Tất cả các quyền (bao gồm cả CASHIER) đều được lấy danh sách Voucher
  store.fetchVouchers(); 

  // 2. 🛡️ Kiểm tra nếu KHÔNG PHẢI CASHIER thì mới gọi API thống kê để tránh lỗi alert 403
  const currentUser = localStorage.getItem("currentUser") || "";
  if (!currentUser.includes("CASHIER")) {
    store.fetchVoucherAnalytics(); 
  }
});

const openAddModal = () => { selectedVoucher.value = null; showModal.value = true; };
const openEditModal = (item: any) => { selectedVoucher.value = item; showModal.value = true; };

const deleteVoucher = async (id: number) => {
  if (!confirm('Hủy mã Voucher này? Mã sẽ không thể sử dụng được nữa!')) return;
  try {
    appStore.startLoading();
    await promotionService.deleteVoucher(id);
    appStore.notifySuccess('Đã hủy mã thành công!');
    store.fetchVouchers();
  } catch (err: any) {
    appStore.notifyError(err.response?.data?.message || 'Lỗi không thể xóa!');
  } finally { 
    appStore.stopLoading();
  }
};

const formatMoney = (val: number | null | undefined) => {
  if (val == null) return "0đ";
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(val);
};

const formatDiscount = (val: number | null | undefined, type: string | null | undefined) => {
  if (val == null) return "0đ";
  return type === 'PERCENTAGE' ? `Giảm ${val}%` : `Giảm ${formatMoney(val)}`;
};

const formatDate = (dateStr: string) => {
  if (!dateStr) return "";
  const d = new Date(dateStr);
  return `${d.getHours().toString().padStart(2, '0')}:${d.getMinutes().toString().padStart(2, '0')} - ${d.getDate().toString().padStart(2, '0')}/${(d.getMonth() + 1).toString().padStart(2, '0')}/${d.getFullYear()}`;
};

const getStatusBadge = (promo: any) => {
  if (promo.status === 0) return { text: 'Đã hủy', class: 'bg-danger bg-opacity-10 text-danger' };
  if (promo.usageLimit <= 0) return { text: 'Hết lượt', class: 'bg-secondary bg-opacity-10 text-secondary' };
  
  const now = new Date().getTime();
  const start = new Date(promo.startDate).getTime();
  const end = new Date(promo.expiredAt).getTime();

  if (now < start) return { text: 'Sắp diễn ra', class: 'bg-info bg-opacity-10 text-info' };
  if (now >= start && now <= end) return { text: 'Đang diễn ra', class: 'bg-success bg-opacity-10 text-success' };
  return { text: 'Đã kết thúc', class: 'bg-secondary bg-opacity-10 text-secondary' };
};
</script>

<template>
  <div class="bg-white h-100 d-flex flex-column overflow-hidden rounded-4 shadow-sm border-0">
    <div class="p-4 border-bottom border-light d-flex justify-content-between align-items-center bg-light bg-opacity-50">
      <div>
        <h5 class="fw-semibold mb-3">Trung Tâm Khuyến Mãi (Voucher)</h5>
        <div class="btn-group bg-white p-1 rounded-pill border shadow-sm">
          <button 
            @click="activeTab = 'LIST'" 
            class="btn rounded-pill px-4 fw-medium btn-sm" 
            :class="activeTab === 'LIST' ? 'btn-dark' : 'btn-light text-muted border-0'"
          >
            Danh sách Mã
          </button>
          
          <button 
            v-permission="[ROLES.OWNER, ROLES.MANAGER]"
            @click="activeTab = 'ANALYTICS'" 
            class="btn rounded-pill px-4 fw-medium btn-sm" 
            :class="activeTab === 'ANALYTICS' ? 'btn-dark' : 'btn-light text-muted border-0'"
          >
            Thống Kê Hiệu Quả
          </button>
        </div>
      </div>
      <button 
        v-if="activeTab === 'LIST'" 
        @click="openAddModal" 
        v-permission="[ROLES.OWNER, ROLES.MANAGER]"
        class="btn btn-dark rounded-pill px-4 fw-medium shadow-sm"
      >
        + Tạo Voucher Mới
      </button>
    </div>

    <div v-if="activeTab === 'LIST'" class="flex-grow-1 overflow-auto custom-scrollbar">
      <table class="table align-middle mb-0 table-hover border-0">
        <thead class="bg-white text-muted small fw-medium tracking-wider" style="position: sticky; top: 0; z-index: 1">
          <tr>
            <th class="ps-4 py-3 border-bottom font-weight-normal">Mã Voucher</th>
            <th class="py-3 border-bottom font-weight-normal">Mức Giảm & Điều kiện</th>
            <th class="py-3 border-bottom font-weight-normal">Thời gian áp dụng</th>
            <th class="py-3 border-bottom font-weight-normal text-center">Lượt dùng</th>
            <th class="py-3 border-bottom font-weight-normal text-center">Trạng thái</th>
            <th class="text-end pe-4 py-3 border-bottom font-weight-normal">Thao tác</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="!store.vouchers || store.vouchers.length === 0">
            <td colspan="6" class="text-center py-5 text-muted fw-medium">Chưa có mã khuyến mãi nào.</td>
          </tr>
          <tr v-for="promo in store.vouchers" :key="(promo as any).id">
            <td class="ps-4 py-3 border-bottom border-light font-bold text-lg text-primary">
              {{ (promo as any).code }}
            </td>
            <td class="py-3 border-bottom border-light">
              <div class="fw-semibold text-dark">
                {{ formatDiscount((promo as any).value || (promo as any).discountValue, (promo as any).discountType) }}
              </div>
              <div class="small text-muted">
                Đơn từ: {{ formatMoney((promo as any).minOrderValue) }}
              </div>
            </td>
            <td class="py-3 border-bottom border-light small text-muted">
              <div><span class="fw-medium">Từ:</span> {{ formatDate((promo as any).startDate) }}</div>
              <div><span class="fw-medium">Đến:</span> {{ formatDate((promo as any).expiredAt) }}</div>
            </td>
            <td class="py-3 border-bottom border-light text-center fw-semibold fs-6">
              {{ (promo as any).usageLimit }}
            </td>
            <td class="py-3 border-bottom border-light text-center">
              <span class="badge rounded-pill px-3 py-2 fw-normal" :class="getStatusBadge(promo).class">
                {{ getStatusBadge(promo).text }}
              </span>
            </td>
            <td class="text-end pe-4 py-3 border-bottom border-light">
              <button 
                @click="openEditModal(promo)" 
                v-permission="[ROLES.OWNER, ROLES.MANAGER]"
                class="btn btn-sm btn-light rounded-circle p-2 mx-1 text-dark" 
                title="Sửa"
              >
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path></svg>
              </button>
              <button 
                @click="deleteVoucher((promo as any).id)" 
                v-permission="[ROLES.OWNER]"
                class="btn btn-sm btn-light text-danger rounded-circle p-2" 
                title="Xóa"
              >
                <i class="bi bi-trash3-fill"></i>
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-if="activeTab === 'ANALYTICS'" class="flex-grow-1 overflow-auto custom-scrollbar p-4">
      <div class="alert alert-light border shadow-sm mb-4 d-flex align-items-center">
        <svg class="me-3 text-info" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"></circle><line x1="12" y1="16" x2="12" y2="12"></line><line x1="12" y1="8" x2="12.01" y2="8"></line></svg>
        <span class="small text-muted fw-medium">Bảng thống kê giúp Admin theo dõi doanh thu mang lại và chi phí khuyến mãi (Discount Given) của từng mã Voucher.</span>
      </div>
      
      <div class="border rounded-4 overflow-hidden">
        <table class="table align-middle mb-0">
          <thead class="bg-light text-muted small fw-medium tracking-wider text-uppercase">
            <tr>
              <th class="ps-4 py-3 border-bottom-0">Mã Voucher</th>
              <th class="py-3 border-bottom-0 text-center">Số lượt đã dùng thành công</th>
              <th class="py-3 border-bottom-0 text-end">Tổng tiền đã tài trợ (Chi phí)</th>
              <th class="py-3 border-bottom-0 text-end pe-4">Doanh thu kéo về</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="!store.voucherAnalytics || store.voucherAnalytics.length === 0">
              <td colspan="4" class="text-center py-5 text-muted fw-medium border-top">Chưa có dữ liệu thống kê.</td>
            </tr>
            <tr v-for="stat in store.voucherAnalytics" :key="stat.voucherCode">
              <td class="ps-4 py-3 border-top border-light font-bold text-dark">{{ stat.voucherCode }}</td>
              <td class="text-center py-3 border-top border-light font-bold text-primary fs-6">{{ stat.totalUsage }} lượt</td>
              <td class="text-end py-3 border-top border-light font-bold text-danger">- {{ formatMoney(stat.totalDiscountGiven) }}</td>
              <td class="text-end pe-4 py-3 border-top border-light font-bold text-success fs-6">+ {{ formatMoney(stat.totalRevenue) }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <Teleport to="body">
      <VoucherModal v-if="showModal" :voucher-selected="selectedVoucher" @close="showModal = false" @refresh="store.fetchVouchers" />
    </Teleport>
  </div>
</template>

<style scoped>
.custom-scrollbar::-webkit-scrollbar { width: 6px; height: 6px; }
.custom-scrollbar::-webkit-scrollbar-thumb { background: #e2e8f0; border-radius: 10px; }
.btn-light { background-color: #f8fafc; border: 1px solid transparent; transition: all 0.2s; }
.btn-light:hover { background-color: #fff; border-color: #e2e8f0; box-shadow: 0 2px 4px rgba(0,0,0,0.05); transform: translateY(-1px); }
</style>