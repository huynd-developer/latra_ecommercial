<template>
  <div class="bg-white h-100 d-flex flex-column overflow-hidden rounded-4 shadow-sm border border-slate-100">
    <!-- HEADER -->
    <div class="p-4 border-bottom border-slate-100 d-flex justify-content-between align-items-center">
      <h5 class="fw-bolder text-slate-800 mb-0">Quản lý Nhân Viên</h5>
      <div class="d-flex gap-3 align-items-center">
        <div class="position-relative">
          <i class="bi bi-search position-absolute top-50 start-0 translate-middle-y ms-3 text-muted"></i>
          <input v-model="searchQuery" type="text" class="form-control rounded-pill ps-5 shadow-none border-slate-200" placeholder="Tìm mã, tên, SĐT..." style="width: 280px; font-size: 0.9rem;" />
        </div>
        <button @click="openModal('add')" class="btn btn-dark rounded-pill px-4 d-flex align-items-center gap-2" style="background-color: #1e293b; color: white;">
          <i class="bi bi-plus-lg"></i> Thêm nhân viên
        </button>
      </div>
    </div>

    <!-- TABLE AREA -->
    <div class="flex-grow-1 overflow-auto">
      <table class="table align-middle modern-table mb-0">
        <thead class="bg-white sticky-top">
          <tr>
            <th class="ps-4">Mã NV</th>
            <th>Tên nhân viên</th>
            <th>Liên hệ</th>
            <th>Vai trò</th>
            <th>Trạng thái</th>
            <th class="text-end pe-4">Thao tác</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="paginatedEmployees.length === 0">
            <td colspan="6" class="text-center py-5 text-muted">Không tìm thấy dữ liệu nhân viên...</td>
          </tr>
          <tr v-for="emp in paginatedEmployees" :key="emp.id">
            <td class="ps-4 fw-medium text-slate-700">
              {{ emp.username }}
              <span v-if="emp.id === 1" class="badge bg-danger ms-2" style="font-size: 0.65rem;">ROOT</span>
            </td>
            <td class="fw-semibold text-slate-800">{{ emp.fullName }}</td>
            <td>
              <div class="small text-slate-800 fw-medium">{{ emp.phone }}</div>
              <div class="small text-muted">{{ emp.email }}</div>
            </td>
            <td>
              <span class="badge bg-light text-dark border px-2 py-1" style="font-size: 0.75rem;">
                {{ emp.roleId == 1 ? 'ADMIN' : 'STAFF' }}
              </span>
            </td>
            <td>
              <span v-if="emp.status == 1" class="status-active">Hoạt động</span>
              <span v-else class="status-locked">Bị khóa</span>
            </td>
            <td class="text-end pe-4">
              <button @click="openModal('edit', emp)" class="btn-action me-2" title="Sửa thông tin"><i class="bi bi-pencil-fill"></i></button>
              <button @click="handleDelete(emp.id)" class="btn-action text-danger" title="Xóa nhân viên"><i class="bi bi-trash3-fill"></i></button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- PAGINATION -->
    <div class="p-3 border-top border-slate-100 bg-white d-flex justify-content-between align-items-center">
      <span class="text-slate-500 small fw-medium">
        Hiển thị {{ paginatedEmployees.length }} / {{ filteredEmployees.length }} bản ghi
      </span>
      <div class="d-flex gap-2 align-items-center">
        <button class="btn btn-sm btn-light border-slate-200 px-3 rounded-pill" :disabled="currentPage === 1" @click="currentPage--">Trước</button>
        <span class="fw-bold text-slate-700 mx-2 small">Trang {{ currentPage }} / {{ totalPages }}</span>
        <button class="btn btn-sm btn-light border-slate-200 px-3 rounded-pill" :disabled="currentPage === totalPages || totalPages === 0" @click="currentPage++">Sau</button>
      </div>
    </div>

    <!-- MODAL TELEPORT -->
    <Teleport to="body">
      <div v-if="showModal" class="modal-overlay d-flex align-items-center justify-content-center" @mousedown.self="closeModal">
        <div class="bg-white rounded-5 shadow-lg overflow-hidden animate__animated animate__zoomIn animate__faster" style="width: 650px; border: none;">
          <div class="p-4 border-bottom border-slate-100 d-flex justify-content-between align-items-center bg-light">
            <h5 class="mb-0 fw-bold">{{ modalMode === 'add' ? 'Thêm Nhân Viên' : 'Cập Nhật Nhân Viên' }}</h5>
            <button @click="closeModal" class="btn-close shadow-none"></button>
          </div>
          <div class="p-4 px-5">
            <form @submit.prevent="handleSave">
              <div class="row g-3 mb-3">
                <div class="col-md-6">
                  <label class="form-label small fw-bold">Mã NV (Username)</label>
                  <input v-model="formData.username" class="form-control rounded-3" placeholder="VD: NV001" required :disabled="modalMode === 'edit'" />
                </div>
                <div class="col-md-6">
                  <label class="form-label small fw-bold">Họ tên</label>
                  <input v-model="formData.fullName" class="form-control rounded-3" placeholder="VD: Nguyễn Văn A" required />
                </div>
              </div>

              <div class="row g-3 mb-3">
                <div class="col-md-6">
                  <label class="form-label small fw-bold">Số điện thoại</label>
                  <input v-model="formData.phone" class="form-control rounded-3" placeholder="VD: 0912..." required />
                </div>
                <div class="col-md-6">
                  <label class="form-label small fw-bold">Email</label>
                  <input type="email" v-model="formData.email" class="form-control rounded-3" placeholder="VD: nhanvien@latra.com" required />
                </div>
              </div>

              <div class="p-3 bg-light rounded-3 mb-3 border">
                <label class="form-label small fw-bold mb-2"><i class="bi bi-geo-alt-fill text-danger"></i> Địa chỉ thường trú</label>
                <div class="row g-2">
                  <div class="col-md-6">
                    <select v-model="addressData.province" @change="onProvinceChange" class="form-select form-select-sm rounded-3">
                      <option value="">-- Tỉnh/Thành --</option>
                      <option v-for="p in provinces" :key="p.code" :value="p.code">{{ p.name }}</option>
                    </select>
                  </div>
                  <div class="col-md-6">
                    <select v-model="addressData.district" @change="onDistrictChange" class="form-select form-select-sm rounded-3" :disabled="!addressData.province">
                      <option value="">-- Quận/Huyện --</option>
                      <option v-for="d in districts" :key="d.code" :value="d.code">{{ d.name }}</option>
                    </select>
                  </div>
                  <div class="col-md-6">
                    <select v-model="addressData.ward" class="form-select form-select-sm rounded-3" :disabled="!addressData.district">
                      <option value="">-- Phường/Xã --</option>
                      <option v-for="w in wards" :key="w.code" :value="w.code">{{ w.name }}</option>
                    </select>
                  </div>
                  <div class="col-md-6">
                    <input v-model="addressData.street" class="form-control form-control-sm rounded-3" placeholder="Số nhà, tên đường..." />
                  </div>
                </div>
                <small class="text-muted mt-1 d-block" style="font-size: 0.7rem;">* Chọn đầy đủ Tỉnh/Huyện/Xã để cập nhật địa chỉ mới.</small>
              </div>

              <div class="row g-3 mb-4">
                <div class="col-md-6">
                  <label class="form-label small fw-bold">Vai trò hệ thống</label>
                  <select v-model="formData.roleId" class="form-select rounded-3" required :disabled="formData.id === 1">
                    <option value="" disabled>-- Chọn vai trò --</option>
                    <option :value="1">ADMIN</option>
                    <option :value="2">STAFF</option>
                  </select>
                </div>
                <div class="col-md-6">
                  <label class="form-label small fw-bold">Trạng thái</label>
                  <select v-model="formData.status" class="form-select rounded-3" required :disabled="formData.id === 1">
                    <option :value="1">Hoạt động</option>
                    <option :value="0">Bị khóa</option>
                  </select>
                </div>
              </div>

              <div class="d-flex justify-content-end gap-2 border-top pt-3">
                <button type="button" @click="closeModal" class="btn btn-light rounded-pill px-4">Hủy</button>
                <button type="submit" class="btn btn-dark rounded-pill px-4 fw-bold">Lưu Thông Tin</button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from "vue";
import { useRouter, useRoute } from "vue-router";
import axios from "axios";
import Swal from "sweetalert2";

const router = useRouter();
const route = useRoute();

// --- STATE ---
const employees = ref<any[]>([]);
const searchQuery = ref("");
const showModal = ref(false);
const modalMode = ref<"add" | "edit">("add");
const provinces = ref<any[]>([]);
const districts = ref<any[]>([]);
const wards = ref<any[]>([]);
const currentPage = ref(Number(route.query.page) || 1);
const pageSize = ref(10);

const addressData = ref({ province: "", district: "", ward: "", street: "" });
const formData = ref({ id: null, username: "", fullName: "", phone: "", email: "", address: "", roleId: "" as any, status: 1 });

// Cấu hình Toast chuẩn (Sửa lỗi thông báo bị đè)
const Toast = Swal.mixin({
  toast: true,
  position: 'top-end',
  showConfirmButton: false,
  timer: 2500,
  timerProgressBar: true,
  didOpen: (toast) => {
    toast.addEventListener('mouseenter', Swal.stopTimer)
    toast.addEventListener('mouseleave', Swal.resumeTimer)
  }
});

// --- METHODS ---
const fetchEmployees = async () => {
  try {
    const res = await axios.get("http://localhost:8080/api/v1/nhanViens");
    employees.value = res.data;
  } catch (error) { console.error("Lỗi tải danh sách:", error); }
};

const openModal = (mode: 'add' | 'edit', item: any = null) => {
  modalMode.value = mode;
  if (mode === 'edit' && item) {
    formData.value = { ...item };
    formData.value.roleId = item.roleId ? Number(item.roleId) : "";
    formData.value.status = item.status !== undefined ? Number(item.status) : 1;
    addressData.value = { province: "", district: "", ward: "", street: item.address || "" };
  } else {
    formData.value = { id: null, username: "", fullName: "", phone: "", email: "", address: "", roleId: "", status: 1 };
    addressData.value = { province: "", district: "", ward: "", street: "" };
  }
  showModal.value = true;
};

const handleSave = async () => {
  // Logic ghép địa chỉ
  if (addressData.value.province && addressData.value.district && addressData.value.ward) {
     const p = provinces.value.find(x => x.code === addressData.value.province)?.name;
     const d = districts.value.find(x => x.code === addressData.value.district)?.name;
     const w = wards.value.find(x => x.code === addressData.value.ward)?.name;
     formData.value.address = `${addressData.value.street}, ${w}, ${d}, ${p}`;
  } else {
     formData.value.address = addressData.value.street;
  }

  const confirmResult = await Swal.fire({
    title: 'Xác nhận?',
    text: modalMode.value === 'add' ? 'Bạn muốn thêm nhân viên mới?' : 'Bạn muốn cập nhật thông tin?',
    icon: 'question',
    showCancelButton: true,
    confirmButtonColor: '#1e293b',
    cancelButtonColor: '#94a3b8',
    confirmButtonText: 'Đồng ý',
    cancelButtonText: 'Hủy'
  });

  if (!confirmResult.isConfirmed) return;

  try {
    if (modalMode.value === 'add') {
      await axios.post("http://localhost:8080/api/v1/nhanViens", formData.value);
    } else {
      await axios.put(`http://localhost:8080/api/v1/nhanViens/${formData.value.id}`, formData.value);
    }
    
    // Đóng Modal trước khi hiện Toast để không bị đè
    closeModal();
    
    Toast.fire({
      icon: 'success',
      title: modalMode.value === 'add' ? 'Thêm mới thành công!' : 'Đã cập nhật thông tin!'
    });

    await fetchEmployees();
  } catch (error: any) {
    Swal.fire({ icon: 'error', title: 'Lỗi', text: error.response?.data?.message || 'Thao tác thất bại!' });
  }
};

const handleDelete = async (id: number) => {
  if (id === 1) {
    Toast.fire({ icon: 'error', title: 'Không thể xóa tài khoản ROOT!' });
    return;
  }

  const result = await Swal.fire({
    title: 'Xác nhận xóa?',
    text: "Nhân viên sẽ được chuyển sang trạng thái Bị khóa!",
    icon: 'warning', 
    showCancelButton: true,
    confirmButtonColor: '#ef4444',
    cancelButtonColor: '#1e293b',
    confirmButtonText: 'Xóa ngay',
    cancelButtonText: 'Hủy'
  });

  if (result.isConfirmed) {
    try {
      await axios.delete(`http://localhost:8080/api/v1/nhanViens/${id}`);
      Toast.fire({ icon: 'success', title: 'Đã cập nhật trạng thái nhân viên!' });
      await fetchEmployees();
    } catch (error: any) {
      Swal.fire({ icon: 'error', title: 'Lỗi', text: 'Không thể xóa nhân viên này!' });
    }
  }
};

const fetchProvinces = async () => { 
  try { const res = await axios.get("https://provinces.open-api.vn/api/p/"); provinces.value = res.data; } catch (e) { console.error(e); }
};
const onProvinceChange = async () => { 
  if(!addressData.value.province) return;
  const res = await axios.get(`https://provinces.open-api.vn/api/p/${addressData.value.province}?depth=2`); 
  districts.value = res.data.districts; 
  addressData.value.district = ""; addressData.value.ward = "";
};
const onDistrictChange = async () => { 
  if(!addressData.value.district) return;
  const res = await axios.get(`https://provinces.open-api.vn/api/d/${addressData.value.district}?depth=2`); 
  wards.value = res.data.wards; 
  addressData.value.ward = "";
};

const closeModal = () => showModal.value = false;

// --- COMPUTED ---
const filteredEmployees = computed(() => {
  const q = searchQuery.value.toLowerCase().trim();
  return employees.value.filter(e => 
    e.fullName.toLowerCase().includes(q) || 
    e.username.toLowerCase().includes(q) || 
    (e.phone && e.phone.includes(q))
  );
});

const totalPages = computed(() => Math.ceil(filteredEmployees.value.length / pageSize.value) || 1);

const paginatedEmployees = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  return filteredEmployees.value.slice(start, start + pageSize.value);
});

// --- WATCHERS ---
watch(currentPage, (newPage) => {
  router.replace({ query: { ...route.query, page: newPage } });
});

watch(searchQuery, () => { currentPage.value = 1; });

onMounted(() => { 
  fetchEmployees(); 
  fetchProvinces(); 
});
</script>

<style scoped>
/* TABLE STYLE */
.modern-table thead th { 
  background-color: #f8fafc; 
  color: #64748b; 
  text-transform: uppercase; 
  font-size: 0.75rem; 
  font-weight: 600; 
  padding: 1.25rem 0.75rem; 
  border-bottom: 2px solid #e2e8f0; 
}
.modern-table tbody tr { transition: background-color 0.2s; }
.modern-table tbody tr:hover { background-color: #f8fafc; }

/* STATUS BADGE */
.status-active { color: #059669; background: #d1fae5; padding: 4px 10px; border-radius: 6px; font-size: 0.8rem; font-weight: 500; }
.status-locked { color: #64748b; background: #f1f5f9; padding: 4px 10px; border-radius: 6px; font-size: 0.8rem; font-weight: 500; }

/* ACTION BUTTONS */
.btn-action { 
  width: 32px; height: 32px; border-radius: 8px; border: 1px solid #e2e8f0; 
  background: white; color: #64748b; display: inline-flex; align-items: center; 
  justify-content: center; transition: all 0.2s; 
}
.btn-action:hover { background: #f1f5f9; color: #1e293b; transform: translateY(-1px); }
.btn-action.text-danger:hover { background: #fef2f2; color: #ef4444 !important; }

/* MODAL OVERLAY */
.modal-overlay { 
  position: fixed; top: 0; left: 0; width: 100%; height: 100%; 
  background: rgba(15, 23, 42, 0.4); z-index: 2000; backdrop-filter: blur(4px); 
}

/* CUSTOM SCROLLBAR */
.custom-scrollbar::-webkit-scrollbar { width: 6px; }
.custom-scrollbar::-webkit-scrollbar-track { background: transparent; }
.custom-scrollbar::-webkit-scrollbar-thumb { background: #e2e8f0; border-radius: 10px; }

/* GLOBAL SWAL Z-INDEX (Sửa lỗi bị đè) */
:global(.swal2-container) {
  z-index: 3000 !important;
}
</style>