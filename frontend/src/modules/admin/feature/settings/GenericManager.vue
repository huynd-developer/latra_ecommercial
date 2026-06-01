<script setup lang="ts">
import { ref, computed, watch, onMounted } from "vue";
import axios from "axios";
import Swal from "sweetalert2";

// Định nghĩa Interface cấu trúc cột (Schema) rõ ràng để ép kiểu dữ liệu chuẩn
interface SchemaCol {
  key: string; 
  label: string; 
  type?: string; 
  options?: any[]; 
  hideInFormAdd?: boolean; 
  hideInFormEdit?: boolean; 
  readonlyInEdit?: boolean;
}

const props = defineProps<{ activeTab: string }>();

const searchQuery = ref("");
const showModal = ref(false);
const modalMode = ref<"add" | "edit">("add");

// Sử dụng kiểu dữ liệu Record<string, any> để loại bỏ hoàn toàn các lỗi cảnh báo object trùng lặp key
const formData = ref<Record<string, any>>({});
const tableData = ref<Record<string, any>[]>([]);

// BỘ ĐỊNH TUYẾN API (API MAPPER)
const apiEndpoints: Record<string, string> = {
  products: "http://localhost:8080/api/v1/products",
  promotions: "http://localhost:8080/api/v1/promotions",
  orders: "http://localhost:8080/api/v1/orders",
  employees: "http://localhost:8080/api/v1/employees",
  settings: "http://localhost:8080/api/v1/settings",
};

// LOGIC PHÂN TRANG (PAGINATION)
const currentPage = ref(1);
const pageSize = ref(10);

const filteredData = computed(() => {
  if (!searchQuery.value) return tableData.value;
  const lowerQuery = searchQuery.value.toLowerCase();
  return tableData.value.filter((item) =>
    Object.values(item).some((val) => String(val).toLowerCase().includes(lowerQuery))
  );
});

const totalPages = computed(() => Math.ceil(filteredData.value.length / pageSize.value) || 1);

const paginatedData = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return filteredData.value.slice(start, end);
});

watch(searchQuery, () => {
  currentPage.value = 1;
});

// GỌI API THEO TAB
const fetchData = async () => {
  const endpoint = apiEndpoints[props.activeTab];
  if (!endpoint) return;
  
  try {
    const res = await axios.get(endpoint);
    tableData.value = res.data;
    currentPage.value = 1;
  } catch (error) {
    console.error(`Lỗi tải dữ liệu tab ${props.activeTab}:`, error);
    tableData.value = []; 
  }
};

watch(() => props.activeTab, () => {
  fetchData();
  searchQuery.value = "";
});

// KHAI BÁO CẤU TRÚC CỘT (SCHEMAS)
const schemas: Record<string, SchemaCol[]> = {
  products: [
    { key: "id", label: "SKU" }, 
    { key: "name", label: "Tên Sản phẩm" },
    { key: "itemType", label: "Loại", options: ["Điện thoại", "Phụ kiện"] },
    { key: "stock", label: "Tồn", type: "number" }, 
    { key: "price", label: "Đơn Giá", type: "money" },
    { key: "status", label: "Trạng thái", type: "status" },
  ],
  promotions: [
    { key: "id", label: "Mã Voucher" }, 
    { key: "name", label: "Tên CT" },
    { key: "discount", label: "Giảm (%)", type: "percent" },
    { key: "usageLimit", label: "Lượt còn", type: "number" }, 
    { key: "status", label: "Trạng thái", type: "status" },
  ],
  employees: [
    { key: "id", label: "ID", readonlyInEdit: true, hideInFormAdd: true },
    { key: "username", label: "Mã NV (Tên ĐN)", readonlyInEdit: true },
    { key: "fullName", label: "Họ tên" },
    { key: "phone", label: "SĐT" },
    { key: "email", label: "Email" },
    { key: "roleId", label: "Vai trò", options: [{ label: 'ADMIN', value: 1 }, { label: 'STAFF', value: 2 }] },
    { key: "status", label: "Trạng thái", type: "status", options: [{ label: 'Hoạt động', value: 1 }, { label: 'Bị khóa', value: 0 }] },
  ],
};

const currentSchema = computed<SchemaCol[]>(() => schemas[props.activeTab] || [{ key: "id", label: "ID" }]);

const tabNames: Record<string, string> = {
  orders: "Hóa đơn", products: "Sản phẩm", promotions: "Khuyến mãi", employees: "Nhân viên", settings: "Cài đặt",
};
const pageTitle = computed(() => tabNames[props.activeTab] || "Quản trị");

const openModal = (mode: "add" | "edit", item: any = null) => {
  modalMode.value = mode;
  if (mode === "edit" && item) {
    formData.value = { ...item };
  } else {
    formData.value = {};
    currentSchema.value.forEach((col) => {
      if (col.type === "number" || col.type === "money" || col.type === "percent") formData.value[col.key] = 0;
      else if (col.options) formData.value[col.key] = col.options[0]?.value || col.options[0];
      else formData.value[col.key] = "";
    });
    formData.value.status = 1;
  }
  showModal.value = true;
};

const closeModal = () => showModal.value = false;

// LƯU DỮ LIỆU (POST / PUT)
const saveData = async () => {
  const endpoint = apiEndpoints[props.activeTab];
  if (!endpoint) return closeModal();

  try {
    if (modalMode.value === "add") {
      await axios.post(endpoint, formData.value);
      Swal.fire({ icon: 'success', title: 'Thêm mới thành công!', timer: 1500, showConfirmButton: false });
    } else {
      await axios.put(`${endpoint}/${formData.value.id}`, formData.value);
      Swal.fire({ icon: 'success', title: 'Cập nhật thành công!', timer: 1500, showConfirmButton: false });
    }
    closeModal();
    await fetchData(); 
  } catch (error: any) {
    const msg = error.response?.data?.message || 'Lỗi hệ thống!';
    Swal.fire({ icon: 'error', title: 'Lỗi', text: msg });
  }
};

// XÓA DỮ LIỆU (DELETE)
const deleteItem = async (id: string | number) => {
  const endpoint = apiEndpoints[props.activeTab];
  if (!endpoint) return;

  const result = await Swal.fire({
    title: 'Xác nhận xóa/khóa?',
    icon: 'warning',
    showCancelButton: true,
    confirmButtonColor: '#1e293b',
    confirmButtonText: 'Đồng ý'
  });

  if (result.isConfirmed) {
    try {
      await axios.delete(`${endpoint}/${id}`);
      Swal.fire({ icon: 'success', title: 'Đã thực hiện!', timer: 1000, showConfirmButton: false });
      fetchData();
    } catch (error: any) {
      Swal.fire({ icon: 'error', title: 'Lỗi', text: error.response?.data?.message || 'Không thể thực hiện!' });
    }
  }
};

const formatMoney = (val: any) => `${(Number(val) || 0).toLocaleString("vi-VN")} ₫`;
const formatVNTime = (isoString: string) => {
  if (!isoString) return "";
  const date = new Date(isoString);
  const pad = (n: number) => n.toString().padStart(2, "0");
  return `${pad(date.getHours())}:${pad(date.getMinutes())} ${pad(date.getDate())}/${pad(date.getMonth() + 1)}/${date.getFullYear()}`;
};

onMounted(() => {
  fetchData();
});
</script>

<template>
  <div class="bg-white h-100 d-flex flex-column overflow-hidden rounded-4 shadow-sm border border-slate-100">
    <div class="p-4 border-bottom border-slate-100 d-flex justify-content-between align-items-center z-2">
      <h5 class="fw-bolder text-slate-800 mb-0 tracking-tight text-capitalize">
        {{ pageTitle }}
      </h5>

      <div class="d-flex gap-3">
        <div class="position-relative" style="width: 300px">
          <i class="bi bi-search position-absolute top-50 translate-middle-y text-slate-400 ms-3"></i>
          <input
            v-model="searchQuery"
            type="text"
            class="form-control bg-slate-50 border border-slate-200 text-slate-700 ps-5 py-2 shadow-none rounded-pill fw-medium focus-ring-peel"
            placeholder="Tìm kiếm..."
          />
        </div>
        <button
          @click="openModal('add')"
          class="btn btn-dark-peel fw-semibold px-4 py-2 rounded-pill shadow-peel hover-lift d-flex align-items-center gap-2"
        >
          <i class="bi bi-plus-lg"></i> Thêm mới
        </button>
      </div>
    </div>

    <div class="p-0 flex-grow-1 overflow-auto custom-scrollbar">
      <table class="table align-middle mb-0 modern-table">
        <thead class="bg-white text-slate-400 small fw-bold tracking-wider text-uppercase" style="position: sticky; top: 0; z-index: 1">
          <tr>
            <th class="ps-4 py-3 border-bottom border-slate-200" v-for="col in currentSchema" :key="col.key">
              {{ col.label }}
            </th>
            <th class="text-end pe-4 py-3 border-bottom border-slate-200">
              Thao tác
            </th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="paginatedData.length === 0">
            <td :colspan="currentSchema.length + 1" class="text-center py-5 text-slate-400 fw-medium">
              Không có dữ liệu hiển thị.
            </td>
          </tr>
          <tr v-for="row in paginatedData" :key="row.id" class="transition-all hover-bg-slate">
            <td class="ps-4 py-3 border-bottom border-slate-50" v-for="col in currentSchema" :key="col.key">
              <span
                v-if="col.type === 'status'"
                class="badge fw-medium px-3 py-2 rounded-pill"
                :class="row[col.key] == 1 || row[col.key] === 'Hoạt động' ? 'bg-slate-800 text-white' : 'bg-slate-100 text-slate-500'"
              >
                {{ row[col.key] == 1 ? 'Hoạt động' : (row[col.key] == 0 ? 'Bị khóa' : row[col.key]) }}
              </span>
              <span v-else-if="col.type === 'money'" class="fw-bold text-slate-800">{{ formatMoney(row[col.key]) }}</span>
              <span v-else-if="col.type === 'percent'" class="fw-bold text-dark-peel">{{ row[col.key] }}%</span>
              <span v-else-if="col.type === 'datetime'" class="text-slate-500 small fw-medium">{{ formatVNTime(row[col.key]) }}</span>
              <span v-else-if="col.key === 'id'" class="text-slate-400 fw-semibold small">#{{ row[col.key] }}</span>
              <span v-else class="text-slate-700 fw-medium">{{ row[col.key] }}</span>
            </td>
            <td class="text-end pe-4 py-3 border-bottom border-slate-50">
              <button @click="openModal('edit', row)" class="btn btn-sm btn-white text-dark-peel border border-slate-200 shadow-sm rounded-circle me-2 btn-action">
                <i class="bi bi-pencil-fill"></i>
              </button>
              <button @click="deleteItem(row.id)" class="btn btn-sm btn-white text-danger border border-slate-200 shadow-sm rounded-circle btn-action">
                <i class="bi bi-trash3-fill"></i>
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="p-3 border-top border-slate-100 bg-white d-flex justify-content-between align-items-center">
      <span class="text-slate-500 small fw-medium">
        Hiển thị {{ paginatedData.length }} / {{ filteredData.length }} bản ghi
      </span>
      <div class="d-flex gap-2 align-items-center">
        <button 
          class="btn btn-sm btn-white border-slate-200 px-3 rounded-pill hover-bg-slate" 
          :disabled="currentPage === 1" 
          @click="currentPage--"
        >
          <i class="bi bi-chevron-left"></i> Trước
        </button>
        <span class="fw-bold text-slate-700 mx-2 small">Trang {{ currentPage }} / {{ totalPages }}</span>
        <button 
          class="btn btn-sm btn-white border-slate-200 px-3 rounded-pill hover-bg-slate" 
          :disabled="currentPage === totalPages || totalPages === 0" 
          @click="currentPage++"
        >
          Sau <i class="bi bi-chevron-right"></i>
        </button>
      </div>
    </div>

    <Teleport to="body">
      <transition name="fade-scale">
        <div v-if="showModal" class="modal-overlay d-flex align-items-center justify-content-center" @mousedown.self="closeModal">
          <div class="bg-white rounded-4 shadow-lg modal-content-custom d-flex flex-column overflow-hidden">
            <div class="p-4 border-bottom border-slate-100 d-flex justify-content-between align-items-center bg-slate-50">
              <h5 class="mb-0 fw-bolder text-slate-800">
                {{ modalMode === "add" ? "Thêm Dữ Liệu" : "Cập Nhật" }}
              </h5>
              <button @click="closeModal" class="btn-close shadow-none"></button>
            </div>
            <div class="p-4 custom-scrollbar overflow-y-auto">
              <form @submit.prevent="saveData">
                <template v-for="col in currentSchema" :key="col.key">
                  <div class="mb-3" v-if="!(modalMode === 'add' && col.hideInFormAdd) && !(modalMode === 'edit' && col.hideInFormEdit)">
                    <label class="form-label small fw-semibold text-slate-500 mb-1">{{ col.label }}</label>
                    
                    <select v-if="col.options" v-model="formData[col.key]" class="form-select shadow-none border-slate-200 bg-white fw-medium text-slate-800 rounded-3 focus-ring-peel" required>
                      <option v-for="opt in col.options" :key="typeof opt === 'object' ? opt.value : opt" :value="typeof opt === 'object' ? opt.value : opt">
                        {{ typeof opt === 'object' ? opt.label : opt }}
                      </option>
                    </select>
                    
                    <input v-else-if="col.key === 'id' && modalMode === 'edit' && col.readonlyInEdit" v-model="formData[col.key]" type="text" class="form-control shadow-none border-slate-200 bg-slate-100 fw-medium text-slate-500 rounded-3" disabled />
                    <input v-else-if="col.type === 'money' || col.type === 'number' || col.type === 'percent'" v-model.number="formData[col.key]" type="number" class="form-control shadow-none border-slate-200 bg-white fw-bold text-slate-800 rounded-3 focus-ring-peel" required />
                    <input v-else-if="col.type === 'datetime'" v-model="formData[col.key]" type="datetime-local" class="form-control shadow-none border-slate-200 bg-white fw-medium text-slate-800 rounded-3 focus-ring-peel" required />
                    <input v-else v-model="formData[col.key]" type="text" class="form-control shadow-none border-slate-200 bg-white fw-medium text-slate-800 rounded-3 focus-ring-peel" required />
                  </div>
                </template>
                <div class="d-flex justify-content-end gap-2 pt-3 mt-4 border-top border-slate-100">
                  <button type="button" @click="closeModal" class="btn btn-white border border-slate-200 px-4 py-2 rounded-pill fw-semibold text-slate-600 hover-bg-slate">Hủy</button>
                  <button type="submit" class="btn btn-dark-peel px-4 py-2 rounded-pill fw-bold shadow-peel hover-lift">Lưu Thông Tin</button>
                </div>
              </form>
            </div>
          </div>
        </div>
      </transition>
    </Teleport>
  </div>
</template>

<style scoped>
.text-slate-400 { color: #94a3b8 !important; }
.text-slate-500 { color: #64748b !important; }
.text-slate-700 { color: #334155 !important; }
.text-slate-800 { color: #1e293b !important; }
.bg-slate-50 { background-color: #f8fafc !important; }
.bg-slate-100 { background-color: #f1f5f9 !important; }
.border-slate-50 { border-color: #f8fafc !important; }
.border-slate-100 { border-color: #f1f5f9 !important; }
.border-slate-200 { border-color: #e2e8f0 !important; }
.text-dark-peel { color: #111111 !important; }
.bg-dark-peel { background-color: #111111 !important; }
.btn-dark-peel { background-color: #111111; color: white; border: 1px solid #111111; }
.btn-dark-peel:hover { background-color: #000000; color: white; box-shadow: 0 4px 14px 0 rgba(0, 0, 0, 0.2); }
.btn-white { background-color: #ffffff; border-color: #e2e8f0; color: #475569; }
.btn-white:hover { background-color: #f8fafc; }
.fw-black { font-weight: 900 !important; }
.fw-bolder { font-weight: 800 !important; }
.tracking-tight { letter-spacing: -0.025em; }
.tracking-wider { letter-spacing: 0.05em; }
.transition-all { transition: all 0.2s ease-in-out; }
.hover-lift { transition: transform 0.2s, box-shadow 0.2s; }
.hover-lift:hover { transform: translateY(-2px); }
.hover-bg-slate:hover { background-color: #f8fafc !important; }
.focus-ring-peel:focus-within, .focus-ring-peel:focus { border-color: #111111 !important; box-shadow: 0 0 0 1px #111111 !important; background-color: #ffffff !important; }
.btn-action { width: 34px; height: 34px; padding: 0; display: inline-flex; align-items: center; justify-content: center; }
.modern-table th { border-top: none; }
.modern-table td { border-bottom: none; }
.custom-scrollbar::-webkit-scrollbar { width: 5px; height: 5px; }
.custom-scrollbar::-webkit-scrollbar-thumb { background: #cbd5e1; border-radius: 10px; }
.custom-scrollbar::-webkit-scrollbar-track { background: transparent; }
.modal-overlay { position: fixed; top: 0; left: 0; width: 100vw; height: 100vh; background-color: rgba(15, 23, 42, 0.4); z-index: 1050; backdrop-filter: blur(2px); }
.modal-content-custom { width: 100%; max-width: 480px; max-height: 90vh; }
.fade-scale-enter-active, .fade-scale-leave-active { transition: opacity 0.2s, transform 0.2s; }
.fade-scale-enter-from, .fade-scale-leave-to { opacity: 0; transform: scale(0.95); }
</style>