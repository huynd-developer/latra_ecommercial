<script setup lang="ts">
import { ref, onMounted } from "vue";
import { usePromotionStore } from "../stores/promotionStore";
import { promotionService } from "../services/promotionService";
import { useAppStore } from "@/common/store/app.store";
import FlashSaleModal from "../components/FlashSaleModal.vue";

const store = usePromotionStore();
const appStore = useAppStore();

const showModal = ref(false);
const selectedFlashSale = ref<any>(null);

onMounted(() => { store.fetchFlashSales(); });

const openAddModal = () => { selectedFlashSale.value = null; showModal.value = true; };
const openEditModal = (item: any) => { selectedFlashSale.value = item; showModal.value = true; };

const deleteFlashSale = async (id: number) => {
  if (!confirm('Hủy chương trình Flash Sale này?')) return;
  try {
    appStore.startLoading();
    await promotionService.deleteFlashSale(id);
    appStore.notifySuccess('Đã hủy thành công!');
    store.fetchFlashSales();
  } catch (err: any) {
    appStore.notifyError(err.response?.data?.message || 'Lỗi không thể xóa!');
  } finally {
    appStore.stopLoading();
  }
};

const formatDate = (timeStr: string) => {
  if(!timeStr) return "";
  const d = new Date(timeStr);
  return `${d.getHours().toString().padStart(2, '0')}:${d.getMinutes().toString().padStart(2, '0')} - ${d.getDate().toString().padStart(2, '0')}/${(d.getMonth()+1).toString().padStart(2, '0')}/${d.getFullYear()}`;
};

const getStatusBadge = (status: string) => {
  if (status === 'ACTIVE') return { text: 'Đang diễn ra', class: 'bg-danger text-white shadow-sm' };
  if (status === 'UPCOMING') return { text: 'Sắp diễn ra', class: 'bg-info bg-opacity-10 text-info' };
  if (status === 'ENDED') return { text: 'Đã kết thúc', class: 'bg-secondary bg-opacity-10 text-secondary' };
  return { text: 'Không xác định', class: 'bg-light text-dark' };
};
</script>
<template>
  <div class="bg-white h-100 d-flex flex-column overflow-hidden rounded-4 shadow-sm border-0">
    <div class="p-4 border-bottom border-light d-flex justify-content-between align-items-center bg-light bg-opacity-50">
      <div>
        <h5 class="fw-semibold mb-1">Quản Lý Flash Sale</h5>
        <span class="small text-muted">Tạo các khung giờ vàng giảm giá chớp nhoáng</span>
      </div>
      <button @click="openAddModal" class="btn btn-dark rounded-pill px-4 fw-medium shadow-sm">
        + Tạo Flash Sale Mới
      </button>
    </div>

    <div class="flex-grow-1 overflow-auto custom-scrollbar">
      <table class="table align-middle mb-0 table-hover border-0">
        <thead class="bg-white text-muted small fw-medium tracking-wider" style="position: sticky; top: 0; z-index: 1">
          <!-- (Giữ nguyên header) -->
        </thead>
        <tbody>
          <tr v-if="store.flashSales.length === 0"><td colspan="6" class="text-center py-5 text-muted fw-medium">Chưa có chương trình Flash Sale nào.</td></tr>
          
          <!-- Đã FIX: Thêm ! sau fs.id -->
          <tr v-for="fs in store.flashSales" :key="fs.id!">
            <td class="ps-4 py-3 border-bottom border-light text-muted fw-medium small">#{{ fs.id }}</td>
            <td class="py-3 border-bottom border-light text-dark fw-semibold">{{ fs.name }}</td>
            <td class="py-3 border-bottom border-light small text-muted">
              <div><span class="fw-medium">Từ:</span> {{ formatDate(fs.startTime) }}</div>
              <div><span class="fw-medium">Đến:</span> {{ formatDate(fs.endTime) }}</div>
            </td>
            <td class="py-3 border-bottom border-light text-center font-bold text-primary">{{ fs.items ? fs.items.length : 0 }} phiên bản</td>
            <td class="py-3 border-bottom border-light text-center">
              <span class="badge rounded-pill px-3 py-2 fw-normal" :class="getStatusBadge(fs.status || 'UPCOMING').class">
                {{ getStatusBadge(fs.status || 'UPCOMING').text }}
              </span>
            </td>
            <td class="text-end pe-4 py-3 border-bottom border-light">
              <button @click="openEditModal(fs)" class="btn btn-sm btn-light rounded-circle p-2 mx-1 text-dark" title="Sửa">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path></svg>
              </button>
              <button @click="deleteFlashSale(fs.id!)" class="btn btn-sm btn-light rounded-circle p-2 mx-1 text-danger" title="Xóa">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><polyline points="3 6 5 6 21 6"></polyline><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path><line x1="10" y1="11" x2="10" y2="17"></line><line x1="14" y1="11" x2="14" y2="17"></line></svg>
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <Teleport to="body">
      <FlashSaleModal v-if="showModal" :flash-sale-selected="selectedFlashSale" @close="showModal = false" @refresh="store.fetchFlashSales" />
    </Teleport>
  </div>
</template>

<style scoped>
.custom-scrollbar::-webkit-scrollbar { width: 6px; height: 6px; }
.custom-scrollbar::-webkit-scrollbar-thumb { background: #e2e8f0; border-radius: 10px; }
.btn-light { background-color: #f8fafc; border: 1px solid transparent; transition: all 0.2s; }
.btn-light:hover { background-color: #fff; border-color: #e2e8f0; box-shadow: 0 2px 4px rgba(0,0,0,0.05); transform: translateY(-1px); }
</style>