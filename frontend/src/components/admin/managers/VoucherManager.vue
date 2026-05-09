<template>
  <div class="bg-white h-100 d-flex flex-column overflow-hidden rounded-4 shadow-sm border border-slate-100">
    <div class="p-4 border-bottom border-slate-100 d-flex justify-content-between align-items-center z-2">
      <h5 class="fw-bolder text-slate-800 mb-0 tracking-tight text-capitalize">Quản lý Mã Giảm Giá (Voucher)</h5>

      <div class="d-flex gap-3">
        <div class="position-relative" style="width: 300px">
          <i class="bi bi-search position-absolute top-50 translate-middle-y text-slate-400 ms-3"></i>
          <input v-model="searchQuery" type="text" class="form-control bg-slate-50 border border-slate-200 text-slate-700 ps-5 py-2 shadow-none rounded-pill fw-medium focus-ring-peel" placeholder="Tìm mã voucher..." />
        </div>
        <button @click="openModal('add')" class="btn btn-dark-peel fw-semibold px-4 py-2 rounded-pill shadow-peel hover-lift d-flex align-items-center gap-2">
          <i class="bi bi-plus-lg"></i> Thêm Voucher
        </button>
      </div>
    </div>

    <div class="p-0 flex-grow-1 overflow-auto custom-scrollbar">
      <table class="table align-middle mb-0 modern-table">
        <thead class="bg-white text-slate-400 small fw-bold tracking-wider text-uppercase" style="position: sticky; top: 0; z-index: 1">
          <tr>
            <th class="ps-4 py-3 border-bottom border-slate-200">Mã Voucher</th>
            <th class="py-3 border-bottom border-slate-200">Giá Trị Giảm</th>
            <th class="py-3 border-bottom border-slate-200">Bắt Đầu Lúc</th>
            <th class="py-3 border-bottom border-slate-200">Kết Thúc Lúc</th>
            <th class="py-3 border-bottom border-slate-200 text-center">Trạng Thái</th>
            <th class="py-3 border-bottom border-slate-200 text-center">Lượt Dùng</th>
            <th class="text-end pe-4 py-3 border-bottom border-slate-200">Thao tác</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="paginatedData.length === 0">
            <td colspan="7" class="text-center py-5 text-slate-400 fw-medium">Không có dữ liệu voucher.</td>
          </tr>
          <tr v-for="promo in paginatedData" :key="promo.id" class="transition-all hover-bg-slate">
            <td class="ps-4 py-3 border-bottom border-slate-50">
              <div class="text-slate-800 fw-bolder">{{ promo.code }}</div>
              <div class="small text-slate-500 mt-1">Đơn từ: <span class="fw-bold">{{ formatVND(promo.minOrderValue) }}</span></div>
            </td>
            
            <td class="py-3 border-bottom border-slate-50">
              <span class="fw-bold text-success">{{ formatCurrency(promo.value, promo.discountType) }}</span>
              <div v-if="promo.discountType === 'PERCENTAGE'" class="small text-slate-500 mt-1">Tối đa: {{ formatVND(promo.maxDiscountAmount) }}</div>
            </td>
            
            <td class="py-3 border-bottom border-slate-50 text-slate-600 fw-medium">
              {{ formatTime(promo.startDate) }}
            </td>

            <td class="py-3 border-bottom border-slate-50 text-slate-600 fw-medium">
              {{ formatTime(promo.expiredAt) }}
            </td>

            <td class="py-3 border-bottom border-slate-50 text-center">
              <span class="badge rounded-pill px-3 py-2 fw-bold" :class="getVoucherStatusBadge(promo)">
                {{ getVoucherStatusText(promo) }}
              </span>
            </td>

            <td class="py-3 border-bottom border-slate-50 text-center fw-bold text-slate-700">
              {{ promo.usageLimit }}
            </td>

            <td class="text-end pe-4 py-3 border-bottom border-slate-50">
              <button @click="openModal('edit', promo)" class="btn btn-sm btn-white text-dark-peel border border-slate-200 shadow-sm rounded-circle me-2 btn-action"><i class="bi bi-pencil-fill"></i></button>
              <button @click="deleteItem(promo.id)" class="btn btn-sm btn-white text-danger border border-slate-200 shadow-sm rounded-circle btn-action"><i class="bi bi-trash3-fill"></i></button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="p-3 border-top border-slate-100 bg-white d-flex justify-content-between align-items-center">
      <span class="text-slate-500 small fw-medium">Hiển thị {{ paginatedData.length }} / {{ filteredData.length }} bản ghi</span>
      <div class="d-flex gap-2 align-items-center">
        <button class="btn btn-sm btn-white border-slate-200 px-3 rounded-pill hover-bg-slate" :disabled="currentPage === 1" @click="currentPage--">Trước</button>
        <span class="fw-bold text-slate-700 mx-2 small">Trang {{ currentPage }} / {{ totalPages }}</span>
        <button class="btn btn-sm btn-white border-slate-200 px-3 rounded-pill hover-bg-slate" :disabled="currentPage === totalPages || totalPages === 0" @click="currentPage++">Sau</button>
      </div>
    </div>

    <Teleport to="body">
      <transition name="fade-scale">
        <div v-if="showModal" class="modal-overlay d-flex align-items-center justify-content-center" @mousedown.self="closeModal">
          <div class="bg-white rounded-4 shadow-lg modal-content-custom d-flex flex-column overflow-hidden" style="width: 600px; max-width: 95vw;">
            <div class="p-4 border-bottom border-slate-100 d-flex justify-content-between align-items-center bg-slate-50">
              <h5 class="mb-0 fw-bolder text-slate-800">{{ modalMode === "add" ? "Thiết Lập Voucher Mới" : "Cập Nhật Voucher" }}</h5>
              <button @click="closeModal" class="btn-close shadow-none"></button>
            </div>
            
            <div class="p-4 custom-scrollbar overflow-y-auto" style="max-height: 75vh;">
              <form @submit.prevent="saveData">
                
                <div class="mb-3">
                  <label class="form-label small fw-semibold text-slate-500 mb-1">Mã Voucher (Code) <span class="text-danger">*</span></label>
                  <input v-model="formData.code" type="text" class="form-control shadow-none border-slate-200 bg-white fw-bold text-slate-800 rounded-3 focus-ring-peel text-uppercase" required placeholder="VD: SIEUSALE1111" />
                </div>

                <div class="row bg-slate-50 p-3 rounded-3 mb-3 mx-0 border border-slate-100">
                  <h6 class="fw-bold text-slate-700 mb-3 px-0"><i class="bi bi-tag-fill me-2 text-warning"></i>Cấu hình Mức giảm</h6>
                  
                  <div class="col-md-6 px-1 mb-2">
                    <label class="form-label small fw-semibold text-slate-500 mb-1">Loại Giảm Giá</label>
                    <select v-model="formData.discountType" class="form-select shadow-none border-slate-200 fw-medium">
                      <option value="PERCENTAGE">Giảm theo Phần trăm (%)</option>
                      <option value="FIXED_AMOUNT">Giảm Tiền mặt (VNĐ)</option>
                    </select>
                  </div>
                  
                  <div class="col-md-6 px-1 mb-2">
                    <label class="form-label small fw-semibold text-slate-500 mb-1">Giá trị giảm <span class="text-danger">*</span></label>
                    <input v-model.number="formData.value" type="number" min="1" class="form-control shadow-none border-slate-200 fw-bold" required />
                  </div>

                  <div class="col-md-6 px-1 mt-2">
                    <label class="form-label small fw-semibold text-slate-500 mb-1">Đơn tối thiểu (VNĐ) <span class="text-danger">*</span></label>
                    <input v-model.number="formData.minOrderValue" type="number" min="0" class="form-control shadow-none border-slate-200 fw-bold" required />
                  </div>

                  <div class="col-md-6 px-1 mt-2" v-if="formData.discountType === 'PERCENTAGE'">
                    <label class="form-label small fw-semibold text-slate-500 mb-1">Giảm Tối đa (VNĐ) <span class="text-danger">*</span></label>
                    <input v-model.number="formData.maxDiscountAmount" type="number" min="1" class="form-control shadow-none border-slate-200 fw-bold" required placeholder="VD: 50000" />
                  </div>
                </div>

                <div class="row">
                  <div class="col-md-6 mb-3">
                    <label class="form-label small fw-semibold text-slate-500 mb-1">Bắt đầu lúc <span class="text-danger">*</span></label>
                    <input v-model="formData.startDate" type="datetime-local" class="form-control shadow-none border-slate-200 fw-medium" required />
                  </div>
                  <div class="col-md-6 mb-3">
                    <label class="form-label small fw-semibold text-slate-500 mb-1">Kết thúc lúc <span class="text-danger">*</span></label>
                    <input v-model="formData.expiredAt" type="datetime-local" class="form-control shadow-none border-slate-200 fw-medium" required />
                  </div>
                </div>

                <div class="mb-2">
                  <label class="form-label small fw-semibold text-slate-500 mb-1">Tổng Số Lượt Dùng Mã <span class="text-danger">*</span></label>
                  <input v-model.number="formData.usageLimit" type="number" min="1" class="form-control shadow-none border-slate-200 fw-bold" required placeholder="VD: 100" />
                  <div class="form-text small">Khi đạt giới hạn, mã sẽ tự động bị khóa.</div>
                </div>

                <div class="d-flex justify-content-end gap-2 pt-3 mt-4 border-top border-slate-100">
                  <button type="button" @click="closeModal" class="btn btn-white border border-slate-200 px-4 py-2 rounded-pill fw-semibold text-slate-600 hover-bg-slate">Hủy</button>
                  <button type="submit" class="btn btn-dark-peel px-4 py-2 rounded-pill fw-bold shadow-peel hover-lift">Lưu Voucher</button>
                </div>
              </form>
            </div>
          </div>
        </div>
      </transition>
    </Teleport>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from "vue";
import { useRouter, useRoute } from "vue-router";
import axios from "axios";
import Swal from "sweetalert2";

interface VoucherForm {
  id?: number | null;
  code: string;
  discountType: string;
  value: number;
  minOrderValue: number;
  maxDiscountAmount: number | null;
  usageLimit: number;
  startDate: string;
  expiredAt: string;
}

const router = useRouter();
const route = useRoute();
const searchQuery = ref("");
const showModal = ref(false);
const modalMode = ref<"add" | "edit">("add");
const tableData = ref<any[]>([]);

const formData = ref<VoucherForm>({
  code: "", discountType: "PERCENTAGE", value: 0, 
  minOrderValue: 0, maxDiscountAmount: null, usageLimit: 100, 
  startDate: "", expiredAt: "" 
});

const currentPage = ref(Number(route.query.page) || 1);
const pageSize = ref(10);

const filteredData = computed(() => {
  if (!searchQuery.value) return tableData.value;
  return tableData.value.filter(item => item.code?.toLowerCase().includes(searchQuery.value.toLowerCase()));
});

const totalPages = computed(() => Math.ceil(filteredData.value.length / pageSize.value) || 1);
const paginatedData = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  return filteredData.value.slice(start, start + pageSize.value);
});

watch(currentPage, (newPage) => { router.replace({ query: { ...route.query, page: newPage } }); });
watch(searchQuery, () => { currentPage.value = 1; });

// API kết nối với Spring Boot Controller của ông
const API_URL = "http://localhost:8080/api/v1/vouchers/admin";

const fetchData = async () => {
  try {
    const res = await axios.get(API_URL);
    tableData.value = res.data;
  } catch (error) { tableData.value = []; }
};

const openModal = (mode: "add" | "edit", item: any = null) => {
  modalMode.value = mode;
  if (mode === "edit" && item) {
    formData.value = { 
        id: item.id, code: item.code, discountType: item.discountType,
        value: item.value, minOrderValue: item.minOrderValue, 
        maxDiscountAmount: item.maxDiscountAmount, usageLimit: item.usageLimit,
        startDate: item.startDate ? item.startDate.substring(0, 16) : "",
        expiredAt: item.expiredAt ? item.expiredAt.substring(0, 16) : ""
    };
  } else {
    formData.value = { code: "", discountType: "PERCENTAGE", value: 0, minOrderValue: 0, maxDiscountAmount: null, usageLimit: 100, startDate: "", expiredAt: "" };
  }
  showModal.value = true;
};

const closeModal = () => showModal.value = false;

const saveData = async () => {
  const start = new Date(formData.value.startDate);
  const end = new Date(formData.value.expiredAt);
  const now = new Date();

  // Validate Frontend
  if (start >= end) {
      Swal.fire({ icon: 'warning', title: 'Sai thời gian!', text: 'Ngày Bắt đầu phải xảy ra trước ngày Kết thúc.' });
      return;
  }
  if (modalMode.value === "add" && end <= now) {
      Swal.fire({ icon: 'warning', title: 'Sai thời gian!', text: 'Mã mới tạo thì Ngày hết hạn phải ở tương lai.' });
      return;
  }

  const fixSeconds = (timeStr: string) => timeStr && timeStr.length === 16 ? `${timeStr}:00` : timeStr;

  const payload: any = {
      code: formData.value.code,
      discountType: formData.value.discountType,
      value: Number(formData.value.value),
      usageLimit: Number(formData.value.usageLimit),
      minOrderValue: Number(formData.value.minOrderValue),
      maxDiscountAmount: formData.value.discountType === 'PERCENTAGE' ? Number(formData.value.maxDiscountAmount) : null,
      startDate: fixSeconds(formData.value.startDate),
      expiredAt: fixSeconds(formData.value.expiredAt)
  };

  try {
    if (modalMode.value === "add") {
      await axios.post(API_URL, payload);
      Swal.fire({ icon: 'success', title: 'Tạo mã thành công!', timer: 1500, showConfirmButton: false });
    } else {
      await axios.put(`${API_URL}/${formData.value.id}`, payload);
      Swal.fire({ icon: 'success', title: 'Cập nhật thành công!', timer: 1500, showConfirmButton: false });
    }
    closeModal();
    await fetchData(); 
  } catch (error: any) {
    let errorMsg = 'Lỗi hệ thống! Vui lòng kiểm tra lại.';
    if (error.response?.data?.message) {
        errorMsg = error.response.data.message;
    }
    Swal.fire({ icon: 'error', title: 'Không thể lưu!', text: errorMsg });
  }
};

const deleteItem = async (id: string | number) => {
  const result = await Swal.fire({ title: 'Xóa mã Voucher?', text: 'Mã này sẽ bị xóa vĩnh viễn!', icon: 'warning', showCancelButton: true, confirmButtonColor: '#d33', confirmButtonText: 'Xóa' });
  if (result.isConfirmed) {
    try {
      await axios.delete(`${API_URL}/${id}`);
      Swal.fire({ icon: 'success', title: 'Đã xóa!', timer: 1000, showConfirmButton: false });
      await fetchData();
      if (paginatedData.value.length === 0 && currentPage.value > 1) currentPage.value--;
    } catch (error: any) {
      Swal.fire({ icon: 'error', title: 'Lỗi', text: 'Không thể xóa mã này!' });
    }
  }
};

// --- FORMAT VÀ TÍNH TOÁN TRẠNG THÁI ---
const formatTime = (timeStr: string) => {
  if(!timeStr) return "";
  const d = new Date(timeStr);
  return `${d.getHours().toString().padStart(2, '0')}:${d.getMinutes().toString().padStart(2, '0')} - ${d.getDate().toString().padStart(2, '0')}/${(d.getMonth()+1).toString().padStart(2, '0')}/${d.getFullYear()}`;
};

const getVoucherStatusText = (promo: any) => {
  if (promo.usageLimit <= 0) return 'Đã hết lượt';
  
  const now = new Date().getTime();
  const start = new Date(promo.startDate).getTime();
  const end = new Date(promo.expiredAt).getTime();

  if (now < start) return 'Sắp diễn ra';
  if (now >= start && now <= end) return 'Đang diễn ra';
  return 'Đã kết thúc';
};

const getVoucherStatusBadge = (promo: any) => {
  if (promo.usageLimit <= 0) return 'bg-secondary bg-opacity-10 text-secondary border border-secondary border-opacity-25';
  
  const now = new Date().getTime();
  const start = new Date(promo.startDate).getTime();
  const end = new Date(promo.expiredAt).getTime();

  if (now < start) return 'bg-info bg-opacity-10 text-info border border-info border-opacity-25'; // Màu xanh dương nhạt
  if (now >= start && now <= end) return 'bg-success text-white shadow-sm'; // Màu xanh lá rực rỡ
  return 'bg-secondary bg-opacity-10 text-secondary border border-secondary border-opacity-25'; // Màu xám tắt
};

const formatVND = (val: number | null) => {
  if (val == null) return "0đ";
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(val);
};

const formatCurrency = (val: number, type: string) => {
  if (type === 'PERCENTAGE') return `Giảm ${val}%`;
  return `Giảm ${formatVND(val)}`;
};

onMounted(() => { fetchData(); });
</script>

<style scoped>
.text-slate-400 { color: #94a3b8 !important; }
.text-slate-500 { color: #64748b !important; }
.text-slate-600 { color: #475569 !important; }
.text-slate-700 { color: #334155 !important; }
.text-slate-800 { color: #1e293b !important; }
.bg-slate-50 { background-color: #f8fafc !important; }
.bg-slate-100 { background-color: #f1f5f9 !important; }
.border-slate-50 { border-color: #f8fafc !important; }
.border-slate-100 { border-color: #f1f5f9 !important; }
.border-slate-200 { border-color: #e2e8f0 !important; }
.text-dark-peel { color: #111111 !important; }
.btn-dark-peel { background-color: #111111; color: white; border: 1px solid #111111; }
.btn-dark-peel:hover { background-color: #000000; color: white; }
.btn-white { background-color: #ffffff; border-color: #e2e8f0; color: #475569; }
.btn-white:hover { background-color: #f8fafc; }
.fw-bolder { font-weight: 800 !important; }
.tracking-tight { letter-spacing: -0.025em; }
.tracking-wider { letter-spacing: 0.05em; }
.transition-all { transition: all 0.2s ease-in-out; }
.hover-lift { transition: transform 0.2s; }
.hover-lift:hover { transform: translateY(-2px); }
.hover-bg-slate:hover { background-color: #f8fafc !important; }
.focus-ring-peel:focus { border-color: #111111 !important; box-shadow: 0 0 0 1px #111111 !important; }
.btn-action { width: 34px; height: 34px; padding: 0; display: inline-flex; align-items: center; justify-content: center; }
.custom-scrollbar::-webkit-scrollbar { width: 5px; height: 5px; }
.custom-scrollbar::-webkit-scrollbar-thumb { background: #cbd5e1; border-radius: 10px; }
.modal-overlay { position: fixed; top: 0; left: 0; width: 100vw; height: 100vh; background-color: rgba(15, 23, 42, 0.4); z-index: 1050; backdrop-filter: blur(2px); }
.modal-content-custom { width: 100%; margin: 0 auto; }
.fade-scale-enter-active, .fade-scale-leave-active { transition: opacity 0.2s, transform 0.2s; }
.fade-scale-enter-from, .fade-scale-leave-to { opacity: 0; transform: scale(0.95); }
</style>