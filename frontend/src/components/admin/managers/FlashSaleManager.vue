<template>
  <div class="bg-white h-100 d-flex flex-column overflow-hidden rounded-4 shadow-sm border border-slate-100">
    <div class="p-4 border-bottom border-slate-100 d-flex justify-content-between align-items-center">
      <h5 class="fw-bolder text-slate-800 mb-0">Quản lý Flash Sale</h5>
      <div class="d-flex gap-3 align-items-center">
        <div class="position-relative">
          <i class="bi bi-search position-absolute top-50 start-0 translate-middle-y ms-3 text-muted"></i>
          <input
            v-model="searchQuery"
            type="text"
            class="form-control rounded-pill ps-5 shadow-none border-slate-200"
            placeholder="Tìm tên chương trình..."
            style="width: 280px; font-size: 0.9rem;"
          />
        </div>
        <button @click="openModal('add')" class="btn btn-dark rounded-pill px-4 d-flex align-items-center gap-2" style="background-color: #1e293b; color: white;">
          <i class="bi bi-lightning-charge-fill text-warning"></i> Thêm Flash Sale
        </button>
      </div>
    </div>

    <div class="flex-grow-1 overflow-auto">
      <table class="table align-middle modern-table mb-0">
        <thead class="bg-white sticky-top">
          <tr>
            <th class="ps-4">ID</th>
            <th>Tên Chương Trình</th>
            <th>Bắt đầu lúc</th>
            <th>Kết thúc lúc</th>
            <th>Số Phiên bản Sale</th>
            <th>Trạng thái</th>
            <th class="text-end pe-4">Thao tác</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="paginatedData.length === 0">
            <td colspan="7" class="text-center py-5 text-muted">Chưa có chương trình Flash Sale nào.</td>
          </tr>
          <tr v-for="fs in paginatedData" :key="fs.id">
            <td class="ps-4 text-muted fw-medium small">#{{ fs.id }}</td>
            <td class="fw-semibold text-slate-800">{{ fs.name }}</td>
            <td class="text-slate-700 small">{{ formatTime(fs.startTime) }}</td>
            <td class="text-slate-700 small">{{ formatTime(fs.endTime) }}</td>
            <td class="text-slate-700 fw-medium">
              <span class="badge bg-light text-dark border px-2 py-1">
                {{ fs.items ? fs.items.length : 0 }} phiên bản
              </span>
            </td>
            <td>
              <span class="badge border-0" :class="getStatusClass(fs.status)">
                {{ getStatusText(fs.status) }}
              </span>
            </td>
            <td class="text-end pe-4">
              <button @click="openModal('edit', fs)" class="btn-action me-2" title="Sửa"><i class="bi bi-pencil-fill"></i></button>
              <button @click="deleteItem(fs.id)" class="btn-action text-danger" title="Xóa"><i class="bi bi-trash3-fill"></i></button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="p-3 border-top border-slate-100 bg-white d-flex justify-content-between align-items-center">
      <span class="text-slate-500 small fw-medium">Hiển thị {{ paginatedData.length }} / {{ filteredData.length }} bản ghi</span>
      <div class="d-flex gap-2 align-items-center">
        <button class="btn btn-sm btn-light border-slate-200 px-3 rounded-pill" :disabled="currentPage === 1" @click="currentPage--">Trước</button>
        <span class="fw-bold text-slate-700 mx-2 small">Trang {{ currentPage }} / {{ totalPages }}</span>
        <button class="btn btn-sm btn-light border-slate-200 px-3 rounded-pill" :disabled="currentPage === totalPages || totalPages === 0" @click="currentPage++">Sau</button>
      </div>
    </div>

    <Teleport to="body">
      <div v-if="showModal" class="modal-overlay d-flex align-items-center justify-content-center" @mousedown.self="closeModal">
        <div class="bg-white rounded-5 shadow-lg d-flex flex-column overflow-hidden" style="width: 800px; max-width: 95vw; max-height: 90vh; border: none;">
          
          <div class="p-4 border-bottom border-slate-100 d-flex justify-content-between align-items-center bg-light">
            <h5 class="mb-0 fw-bold"><i class="bi bi-lightning-charge-fill text-warning me-2"></i>{{ modalMode === "add" ? "Tạo Flash Sale" : "Cập Nhật Flash Sale" }}</h5>
            <button @click="closeModal" class="btn-close shadow-none"></button>
          </div>
            
          <div class="p-4 px-5 custom-scrollbar overflow-y-auto">
            <form @submit.prevent="saveData">
              
              <div class="mb-4">
                <h6 class="fw-bold text-slate-700 mb-3"><i class="bi bi-info-circle me-1"></i> Thông tin chương trình</h6>
                <div class="mb-3">
                  <label class="form-label small fw-bold">Tên Chương Trình <span class="text-danger">*</span></label>
                  <input v-model="formData.name" type="text" class="form-control rounded-3 shadow-none" required placeholder="VD: Giờ Vàng Giá Sốc" />
                </div>
                <div class="row g-3">
                  <div class="col-md-6">
                    <label class="form-label small fw-bold">Bắt đầu lúc <span class="text-danger">*</span></label>
                    <div class="d-flex gap-2">
                      <input v-model="formData.startDate" type="date" class="form-control rounded-3 shadow-none fw-medium" required />
                      <input v-model="formData.startTimeStr" type="time" class="form-control rounded-3 shadow-none fw-medium" required />
                    </div>
                  </div>
                  <div class="col-md-6">
                    <label class="form-label small fw-bold">Kết thúc lúc <span class="text-danger">*</span></label>
                    <div class="d-flex gap-2">
                      <input v-model="formData.endDate" type="date" class="form-control rounded-3 shadow-none fw-medium" required />
                      <input v-model="formData.endTimeStr" type="time" class="form-control rounded-3 shadow-none fw-medium" required />
                    </div>
                  </div>
                </div>
              </div>

              <div class="mb-2 d-flex justify-content-between align-items-center border-top pt-4">
                <h6 class="fw-bold text-slate-700 mb-0"><i class="bi bi-box-seam me-1"></i> Phiên bản áp dụng Sale</h6>
                <button type="button" @click="addItem" class="btn btn-sm btn-outline-dark rounded-pill fw-medium px-3">
                  <i class="bi bi-plus-lg"></i> Thêm phiên bản
                </button>
              </div>

              <div class="table-responsive border rounded-3 mb-4">
                <table class="table table-borderless table-sm align-middle mb-0">
                  <thead class="bg-light text-muted small border-bottom">
                    <tr>
                      <th class="ps-3 py-2">Chọn Phiên bản (Variant) <span class="text-danger">*</span></th>
                      <th width="150" class="py-2">Giá Sale (VNĐ) <span class="text-danger">*</span></th>
                      <th width="120" class="py-2">SL Giới hạn <span class="text-danger">*</span></th>
                      <th width="50" class="text-center py-2">Xóa</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-if="formData.items.length === 0">
                      <td colspan="4" class="text-center py-4 text-muted small">Vui lòng bấm "Thêm phiên bản" để chọn sản phẩm.</td>
                    </tr>
                    <tr v-for="(item, index) in formData.items" :key="index" class="border-bottom">
                      <td class="ps-3">
                        <select v-model="item.variantId" class="form-select form-select-sm shadow-none rounded-3" required>
                          <option value="" disabled>-- Chọn --</option>
                          <option :value="1">iPhone 15 Pro Max (Titan Đen - 256GB)</option>
                          <option :value="2">iPhone 15 Pro Max (Titan Trắng - 512GB)</option>
                        </select>
                      </td>
                      <td>
                        <input v-model.number="item.salePrice" type="number" min="0" class="form-control form-control-sm shadow-none rounded-3 fw-medium" placeholder="VD: 25000000" required />
                      </td>
                      <td>
                        <input v-model.number="item.quantityLimit" type="number" min="1" class="form-control form-control-sm shadow-none rounded-3 fw-medium text-center" placeholder="VD: 50" required />
                      </td>
                      <td class="text-center">
                        <button type="button" @click="removeItem(index)" class="btn btn-sm btn-light text-danger rounded-circle p-1" style="width: 28px; height: 28px;">
                          <i class="bi bi-trash3-fill"></i>
                        </button>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>

              <div class="d-flex justify-content-end gap-2 pt-2 border-top">
                <button type="button" @click="closeModal" class="btn btn-light rounded-pill px-4 fw-medium">Hủy</button>
                <button type="submit" class="btn btn-dark rounded-pill px-4 fw-medium" style="background-color: #1e293b; color: white;">Lưu Chương Trình</button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from "vue";
import { useRouter, useRoute } from "vue-router";
import axios from "axios";
import Swal from "sweetalert2";

interface FlashSaleItemForm {
  variantId: number | string;
  salePrice: number;
  quantityLimit: number;
}

interface FlashSaleForm {
  id?: number | null;
  name: string;
  startDate: string;
  startTimeStr: string;
  endDate: string;
  endTimeStr: string;
  status?: string; // ĐÃ SỬA: Đổi từ boolean sang string
  items: FlashSaleItemForm[];
}

const router = useRouter();
const route = useRoute();

const searchQuery = ref("");
const showModal = ref(false);
const modalMode = ref<"add" | "edit">("add");
const tableData = ref<any[]>([]);

const formData = ref<FlashSaleForm>({
  name: "", 
  startDate: "", 
  startTimeStr: "", 
  endDate: "", 
  endTimeStr: "", 
  status: "UPCOMING",
  items: []
});

const currentPage = ref(Number(route.query.page) || 1);
const pageSize = ref(10);

const filteredData = computed(() => {
  if (!searchQuery.value) return tableData.value;
  const lowerQuery = searchQuery.value.toLowerCase();
  return tableData.value.filter(item => item.name?.toLowerCase().includes(lowerQuery));
});

const totalPages = computed(() => Math.ceil(filteredData.value.length / pageSize.value) || 1);
const paginatedData = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  return filteredData.value.slice(start, start + pageSize.value);
});

watch(currentPage, (newPage) => { router.replace({ query: { ...route.query, page: newPage } }); });
watch(searchQuery, () => { currentPage.value = 1; });

const API_URL = "http://localhost:8080/api/v1/flash-sales/admin";

const fetchData = async () => {
  try {
    const res = await axios.get(API_URL);
    tableData.value = res.data;
  } catch (error) {
    console.error("Lỗi tải dữ liệu Flash Sale:", error);
  }
};

const getLocalDate = () => {
  const now = new Date();
  now.setMinutes(now.getMinutes() - now.getTimezoneOffset());
  return now.toISOString().substring(0, 10); 
};

// --- HÀM MỚI: XỬ LÝ MÀU SẮC & CHỮ CHO TRẠNG THÁI ---
const getStatusText = (status: string) => {
  if (status === 'ACTIVE') return 'Đang diễn ra';
  if (status === 'UPCOMING') return 'Sắp diễn ra';
  if (status === 'ENDED') return 'Đã Đóng';
  return 'Không xác định';
};

const getStatusClass = (status: string) => {
  if (status === 'ACTIVE') return 'bg-success text-white px-3 py-1 rounded-pill status-active';
  if (status === 'UPCOMING') return 'bg-info text-dark px-3 py-1 rounded-pill status-upcoming';
  if (status === 'ENDED') return 'bg-secondary text-white px-3 py-1 rounded-pill status-locked';
  return 'bg-warning text-dark px-3 py-1 rounded-pill';
};

const openModal = (mode: "add" | "edit", item: any = null) => {
  modalMode.value = mode;
  if (mode === "edit" && item) {
    const st = item.startTime || "";
    const et = item.endTime || "";
    
    formData.value = { 
      id: item.id,
      name: item.name,
      startDate: st ? st.substring(0, 10) : "",
      startTimeStr: st ? st.substring(11, 16) : "",
      endDate: et ? et.substring(0, 10) : "",
      endTimeStr: et ? et.substring(11, 16) : "",
      status: item.status !== undefined ? item.status : "UPCOMING",
      items: item.items ? item.items.map((i: any) => ({
        variantId: i.productVariant?.id || i.variantId || "", 
        salePrice: i.salePrice,
        quantityLimit: i.quantityLimit
      })) : []
    };
  } else {
    const today = getLocalDate();
    formData.value = { 
        name: "", 
        startDate: today, 
        startTimeStr: "00:00", 
        endDate: today, 
        endTimeStr: "23:59", 
        status: "UPCOMING",
        items: [] 
    };
  }
  showModal.value = true;
};

const closeModal = () => showModal.value = false;

const addItem = () => {
  formData.value.items.push({ variantId: "", salePrice: 0, quantityLimit: 1 });
};

const removeItem = (index: number) => {
  formData.value.items.splice(index, 1);
};

const saveData = async () => {
  if(formData.value.items.length === 0) {
    Swal.fire({ icon: 'warning', title: 'Thiếu thông tin', text: 'Vui lòng thêm ít nhất 1 phiên bản vào chương trình Sale!' });
    return;
  }

  const startDateTime = new Date(`${formData.value.startDate}T${formData.value.startTimeStr}:00`);
  const endDateTime = new Date(`${formData.value.endDate}T${formData.value.endTimeStr}:00`);
  const now = new Date();

  if (endDateTime <= startDateTime) {
      Swal.fire({ icon: 'warning', title: 'Lỗi', text: 'Giờ kết thúc phải LỚN HƠN giờ bắt đầu!' });
      return; 
  }

  if (endDateTime <= now) {
      Swal.fire({ icon: 'warning', title: 'Lỗi Thời Gian', text: 'Bạn phải chọn thời gian kết thúc ở TƯƠNG LAI!' });
      return; 
  }

  const payload: any = {
    name: formData.value.name,
    startTime: `${formData.value.startDate}T${formData.value.startTimeStr}:00`,
    endTime: `${formData.value.endDate}T${formData.value.endTimeStr}:00`,
    items: formData.value.items.map(item => ({
        variantId: Number(item.variantId),      
        salePrice: Number(item.salePrice),      
        quantityLimit: Number(item.quantityLimit) 
    }))
  };

  if (modalMode.value === "edit" && formData.value.id) {
      payload.id = formData.value.id;
  }

  try {
    if (modalMode.value === "add") {
      await axios.post(API_URL, payload);
      Swal.fire({ icon: 'success', title: 'Tạo Flash Sale thành công!', timer: 1500, showConfirmButton: false });
    } else {
      await axios.put(`${API_URL}/${formData.value.id}`, payload);
      Swal.fire({ icon: 'success', title: 'Cập nhật thành công!', timer: 1500, showConfirmButton: false });
    }
    closeModal();
    await fetchData(); 
  } catch (error: any) {
    console.error("🛑 SPRING BOOT TỪ CHỐI DỮ LIỆU. CHI TIẾT LỖI:", error.response?.data);

    let errorMsg = 'Lỗi 400 (Bad Request). Hãy mở F12 -> Console để xem Spring Boot đang báo sai trường nào!';
    
    if (error.response && error.response.data) {
        if (error.response.data.errors) {
            const errorList = Object.values(error.response.data.errors);
            errorMsg = Array.isArray(errorList) ? errorList.join('\n') : "Dữ liệu nhập chưa hợp lệ.";
        } 
        else if (error.response.data.message || error.response.data.trace) {
            let trace = error.response.data.trace || error.response.data.message;
            let match = trace.match(/java\.lang\.RuntimeException:\s*(.*)/);
            if (match && match[1]) {
                errorMsg = match[1].trim();
            } else {
                errorMsg = error.response.data.message;
            }
        }
    }
    
    Swal.fire({ icon: 'error', title: 'Không thể lưu!', text: errorMsg });
  }
};

const deleteItem = async (id: string | number) => {
  const result = await Swal.fire({ title: 'Hủy Flash Sale?', icon: 'warning', showCancelButton: true, confirmButtonColor: '#d33', confirmButtonText: 'Hủy chương trình' });
  if (result.isConfirmed) {
    try {
      await axios.delete(`${API_URL}/${id}`);
      Swal.fire({ icon: 'success', title: 'Đã hủy!', timer: 1000, showConfirmButton: false });
      await fetchData();
      if (paginatedData.value.length === 0 && currentPage.value > 1) currentPage.value--;
    } catch (error: any) {
      Swal.fire({ icon: 'error', title: 'Lỗi', text: error.response?.data?.message || 'Không thể thực hiện!' });
    }
  }
};

const formatTime = (timeStr: string) => {
  if(!timeStr) return "";
  const d = new Date(timeStr);
  return `${d.getHours().toString().padStart(2, '0')}:${d.getMinutes().toString().padStart(2, '0')} - ${d.getDate().toString().padStart(2, '0')}/${(d.getMonth()+1).toString().padStart(2, '0')}/${d.getFullYear()}`;
}

onMounted(() => { fetchData(); });
</script>

<style scoped>
.modern-table thead th { 
  background-color: #f8fafc; color: #64748b; text-transform: uppercase; 
  font-size: 0.75rem; font-weight: 600; padding: 1.25rem 0.75rem; letter-spacing: 0.5px;
}
.text-slate-800 { color: #1e293b !important; }
.text-slate-700 { color: #334155 !important; }
.modern-table tbody tr td { padding: 1rem 0.75rem; border-bottom: 1px solid #f1f5f9; font-size: 0.875rem; vertical-align: middle; }

/* Các style cũ đã được cập nhật trực tiếp vào class của getStatusClass() cho tiện, nhưng tôi vẫn giữ lại một số base style */
.status-active { font-size: 0.8rem; font-weight: 600; font-family: system-ui; }
.status-upcoming { font-size: 0.8rem; font-weight: 600; font-family: system-ui; background-color: #e0f2fe !important; color: #0284c7 !important; border: 1px solid #bae6fd;}
.status-locked { font-size: 0.8rem; font-weight: 600; font-family: system-ui; }

.btn-action { width: 32px; height: 32px; border-radius: 8px; border: 1px solid #e2e8f0; background: white; color: #64748b; display: inline-flex; align-items: center; justify-content: center; transition: all 0.2s; }
.btn-action:hover { background: #f8fafc; color: #1e293b; border-color: #cbd5e1; }
.btn-action.text-danger:hover { background: #fef2f2; color: #ef4444 !important; border-color: #fca5a5; }

.modal-overlay { position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.4); z-index: 1050; backdrop-filter: blur(2px); }
.custom-scrollbar::-webkit-scrollbar { width: 6px; }
.custom-scrollbar::-webkit-scrollbar-thumb { background: #cbd5e1; border-radius: 10px; }
</style>