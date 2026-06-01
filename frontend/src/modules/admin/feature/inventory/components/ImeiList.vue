<script setup lang="ts">
import { ref, computed } from 'vue';
import { useAppStore } from '@/common/store/app.store';
import { imeiService } from '../services/imeiService';
import type { Imei, ImeiStatus } from '../types/imei.type';
import { formatDate } from '@/common/utils/format';

const props = defineProps<{ imeis: Imei[] }>();
const emit = defineEmits<{ (e: 'refresh'): void }>();
const appStore = useAppStore();

const searchQuery = ref('');
const statusFilter = ref<string>('ALL');
const currentPage = ref(1);
const pageSize = ref(15);

// Lọc thông minh dữ liệu danh sách IMEI
const filteredImeis = computed(() => {
  let result = props.imeis || [];
  if (statusFilter.value !== 'ALL') {
    result = result.filter(i => i.status === statusFilter.value);
  }
  if (searchQuery.value) {
    const q = searchQuery.value.toLowerCase();
    result = result.filter(i => 
      i.imeiCode?.toLowerCase().includes(q) || 
      i.productName?.toLowerCase().includes(q)
    );
  }
  return result;
});

// Phân trang Client-side
const paginatedImeis = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  return filteredImeis.value.slice(start, start + pageSize.value);
});

const totalPages = computed(() => Math.ceil(filteredImeis.value.length / pageSize.value) || 1);

// Xử lý Xóa mã định danh (Chỉ khả dụng với máy AVAILABLE)
const deleteImei = async (imei: Imei) => {
  if (imei.status !== 'AVAILABLE') return appStore.notifyError('Chỉ được xóa máy đang ở trạng thái AVAILABLE!');
  if (!confirm(`Xóa mã IMEI [${imei.imeiCode}] khỏi hệ thống?`)) return;
  
  try {
    appStore.startLoading();
    await imeiService.deleteImei(imei.id);
    appStore.notifySuccess('Đã xóa thành công!');
    emit('refresh');
  } catch (error: any) {
    appStore.notifyError(error.response?.data?.message || 'Lỗi khi xóa!');
  } finally {
    appStore.stopLoading();
  }
};

// 🛠️ ĐÃ SỬA: Xử lý Đổi trạng thái bẫy Rollback cực chuẩn (Bỏ qua v-model tránh race condition)
const handleStatusChange = async (imei: Imei, event: Event) => {
  const selectElement = event.target as HTMLSelectElement;
  const newStatus = selectElement.value as ImeiStatus;
  const oldStatus = imei.status; // Giữ lại trạng thái cũ chính xác tuyệt đối

  try {
    appStore.startLoading();
    // Gọi đúng tên hàm thực tế của file service
    await imeiService.changeImeiStatus(imei.id, newStatus);
    
    // API phản hồi OK thành công -> Tiến hành cập nhật State giao diện
    imei.status = newStatus;
    appStore.notifySuccess('Cập nhật trạng thái thành công!');
  } catch (error: any) {
    // Nếu API lỗi (hoặc phân quyền chặn), ép UI Select Box nhảy ngược lại giá trị ban đầu
    selectElement.value = oldStatus; 
    appStore.notifyError(error.response?.data?.message || 'Không thể đổi trạng thái thiết bị!');
  } finally {
    appStore.stopLoading();
  }
};
</script>

<template>
  <div class="h-100 d-flex flex-column">
    <div class="d-flex gap-3 mb-3">
      <input v-model="searchQuery" type="text" class="form-control rounded-pill shadow-none" placeholder="Tìm theo IMEI hoặc Tên máy..." style="max-width: 300px;" @input="currentPage = 1">
      <select v-model="statusFilter" class="form-select rounded-pill shadow-none" style="max-width: 200px;" @change="currentPage = 1">
        <option value="ALL">Tất cả trạng thái</option>
        <option value="AVAILABLE">Trong kho (Sẵn sàng)</option>
        <option value="SOLD">Đã bán</option>
        <option value="DEFECTIVE">Máy lỗi</option>
        <option value="WARRANTY">Bảo hành</option>
        <option value="RETURNING">Đổi trả</option>
      </select>
    </div>

    <div class="table-responsive flex-grow-1 border rounded-3">
      <table class="table align-middle mb-0">
        <thead class="bg-light text-muted small text-uppercase">
          <tr>
            <th class="ps-3 py-3">Mã IMEI</th>
            <th>Sản Phẩm</th>
            <th>Tình trạng</th>
            <th class="text-center">Trạng thái WMS</th>
            <th>Ngày Nhập</th>
            <th class="text-center pe-3">Thao tác</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="paginatedImeis.length === 0">
            <td colspan="6" class="text-center py-5 text-muted">Không tìm thấy mã IMEI nào.</td>
          </tr>
          <tr v-for="imei in paginatedImeis" :key="imei.id">
            <td class="ps-3 font-monospace fw-bold text-dark">{{ imei.imeiCode }}</td>
            <td>
              <div class="fw-bold">{{ imei.productName }}</div>
              <div class="small text-muted">{{ imei.variantName }}</div>
            </td>
            <td><span class="badge bg-primary bg-opacity-10 text-primary">{{ imei.condition }}</span></td>
            
            <td class="text-center">
              <select 
                :value="imei.status" 
                @change="handleStatusChange(imei, $event)"
                class="form-select form-select-sm shadow-none fw-bold border-0"
                :class="{
                  'text-success bg-success bg-opacity-10': imei.status === 'AVAILABLE',
                  'text-danger bg-danger bg-opacity-10': imei.status === 'SOLD',
                  'text-warning bg-warning bg-opacity-10': imei.status === 'DEFECTIVE',
                  'text-info bg-info bg-opacity-10': imei.status === 'WARRANTY' || imei.status === 'RETURNING'
                }"
              >
                <option value="AVAILABLE" :disabled="imei.status === 'DEFECTIVE'">Có sẵn</option>
                <option value="SOLD" disabled>Đã Bán</option>
                <option value="DEFECTIVE">Hàng Lỗi</option>
                <option value="WARRANTY" :disabled="imei.status !== 'SOLD'">Bảo hành</option>
                <option value="RETURNING" :disabled="imei.status !== 'SOLD'">Đổi trả</option>
              </select>
            </td>

            <td class="small text-muted">{{ formatDate(imei.createdAt) }}</td>
            
            <td class="text-center pe-3">
              <button 
                v-permission="['OWNER']"
                @click="deleteImei(imei)" 
                class="btn btn-sm btn-light rounded-circle"
                :class="imei.status === 'AVAILABLE' ? 'text-danger' : 'text-muted opacity-50'"
                :disabled="imei.status !== 'AVAILABLE'"
                :title="imei.status === 'AVAILABLE' ? 'Xóa IMEI' : 'Không thể xóa máy đang giao dịch/lỗi'"
              >
                <i class="bi bi-trash3-fill"></i>
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="d-flex justify-content-between align-items-center pt-3">
      <span class="small text-muted">Hiển thị {{ paginatedImeis.length }} / {{ filteredImeis.length }}</span>
      <div class="btn-group">
        <button class="btn btn-sm btn-outline-secondary" :disabled="currentPage === 1" @click="currentPage--">Trước</button>
        <span class="btn btn-sm btn-light disabled px-3">{{ currentPage }} / {{ totalPages }}</span>
        <button class="btn btn-sm btn-outline-secondary" :disabled="currentPage === totalPages" @click="currentPage++">Sau</button>
      </div>
    </div>
  </div>
</template>