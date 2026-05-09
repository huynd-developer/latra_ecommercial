<template>
  <div class="bg-white h-100 d-flex flex-column overflow-hidden rounded-4 shadow-sm border border-slate-100">
    <div class="p-4 border-bottom border-slate-100 d-flex justify-content-between align-items-center">
      <h5 class="fw-bolder text-slate-800 mb-0">Quản lý Khách hàng</h5>
      
      <div class="d-flex gap-3 align-items-center">
        <div class="position-relative">
          <i class="bi bi-search position-absolute top-50 start-0 translate-middle-y ms-3 text-muted"></i>
          <input 
            v-model="searchQuery" 
            type="text" 
            class="form-control rounded-pill ps-5 shadow-none border-slate-200" 
            placeholder="Tìm tên, SĐT, email..."
            style="width: 280px; font-size: 0.9rem;"
          />
        </div>

        <button @click="openModal('add')" class="btn btn-dark rounded-pill px-4 d-flex align-items-center gap-2" style="background-color: #1e293b; color: white;">
          <i class="bi bi-plus-lg"></i> 
          <span>Thêm khách hàng</span>
        </button>
      </div>
    </div>

    <div class="flex-grow-1 overflow-auto">
      <table class="table align-middle modern-table mb-0">
        <thead class="bg-white sticky-top">
          <tr>
            <th class="ps-4">Mã KH</th>
            <th>Username</th> 
            <th>Tên khách hàng</th>
            <th>Email</th>
            <th>Số điện thoại</th>
            <th>Địa chỉ</th> 
            <th class="text-end pe-4">Thao tác</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="customer in paginatedCustomers" :key="customer.id">
            <td class="ps-4 text-muted" style="font-size: 0.85rem;">{{ customer.id }}</td>
            <td class="fw-medium text-slate-700">{{ customer.username }}</td> 
            <td class="fw-semibold text-slate-800">{{ customer.name }}</td>
            <td class="text-slate-700">{{ customer.email }}</td>
            <td class="text-slate-700">{{ customer.phone }}</td>
            <td class="text-slate-700 text-truncate" style="max-width: 200px;" :title="customer.address">
              {{ customer.address || '---' }}
            </td> 
            <td class="text-end pe-4">
              <button @click="openModal('edit', customer)" class="btn-action me-2" title="Sửa"><i class="bi bi-pencil-fill"></i></button>
              <button @click="deleteCustomer(customer.id)" class="btn-action text-danger" title="Xóa"><i class="bi bi-trash3-fill"></i></button>
            </td>
          </tr>
          <tr v-if="paginatedCustomers.length === 0">
            <td colspan="7" class="text-center py-5 text-muted">Không tìm thấy khách hàng phù hợp</td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="p-3 border-top border-slate-100 bg-white d-flex justify-content-between align-items-center">
      <span class="text-slate-500 small fw-medium">
        Hiển thị {{ paginatedCustomers.length }} / {{ filteredCustomers.length }} bản ghi
      </span>
      <div class="d-flex gap-2 align-items-center">
        <button class="btn btn-sm btn-light border-slate-200 px-3 rounded-pill" :disabled="currentPage === 1" @click="currentPage--">Trước</button>
        <span class="fw-bold text-slate-700 mx-2 small">Trang {{ currentPage }} / {{ totalPages }}</span>
        <button class="btn btn-sm btn-light border-slate-200 px-3 rounded-pill" :disabled="currentPage === totalPages || totalPages === 0" @click="currentPage++">Sau</button>
      </div>
    </div>

    <Teleport to="body">
      <div v-if="showModal" class="modal-overlay d-flex align-items-center justify-content-center" @mousedown.self="closeModal">
        <div class="bg-white rounded-5 shadow-lg overflow-hidden" style="width: 650px; border: none;">
          <div class="p-4 d-flex justify-content-between align-items-center">
            <h5 class="mb-0 fw-bold">{{ modalMode === 'add' ? 'Thêm Khách Hàng' : 'Cập Nhật Khách Hàng' }}</h5>
            <button @click="closeModal" class="btn-close shadow-none"></button>
          </div>
          <div class="px-4 pb-4">
            <form @submit.prevent="handleSave">
              <div class="row">
                <div class="col-md-6 mb-3">
                  <label class="form-label small fw-bold text-dark">Tên đăng nhập</label>
                  <input v-model="formData.username" class="form-control rounded-3 shadow-none" placeholder="VD: nguyenvana_123" required :disabled="modalMode === 'edit'" />
                </div>
                <div class="col-md-6 mb-3">
                  <label class="form-label small fw-bold text-dark">Họ tên</label>
                  <input v-model="formData.name" class="form-control rounded-3 shadow-none" placeholder="VD: Nguyễn Văn A" required />
                </div>
              </div>
              <div class="row">
                <div class="col-md-6 mb-3">
                  <label class="form-label small fw-bold text-dark">Số điện thoại</label>
                  <input v-model="formData.phone" class="form-control rounded-3 shadow-none" placeholder="VD: 0912345678" required />
                </div>
                <div class="col-md-6 mb-3">
                  <label class="form-label small fw-bold text-dark">Email</label>
                  <input type="email" v-model="formData.email" class="form-control rounded-3 shadow-none" placeholder="VD: vana@gmail.com" required />
                </div>
              </div>

              <div class="p-3 bg-light rounded-3 mb-4 border border-slate-100">
                <label class="form-label small fw-bold text-dark mb-2"><i class="bi bi-geo-alt-fill text-danger"></i> Thông tin địa chỉ</label>
                <div class="row g-2">
                  <div class="col-md-6">
                    <select v-model="addressData.province" @change="onProvinceChange" class="form-select form-select-sm rounded-3 shadow-none">
                      <option value="">-- Tỉnh/Thành phố --</option>
                      <option v-for="p in provinces" :key="p.code" :value="p.code">{{ p.name }}</option>
                    </select>
                  </div>
                  <div class="col-md-6">
                    <select v-model="addressData.district" @change="onDistrictChange" class="form-select form-select-sm rounded-3 shadow-none" :disabled="!addressData.province">
                      <option value="">-- Quận/Huyện --</option>
                      <option v-for="d in districts" :key="d.code" :value="d.code">{{ d.name }}</option>
                    </select>
                  </div>
                  <div class="col-md-6 mt-2">
                    <select v-model="addressData.ward" class="form-select form-select-sm rounded-3 shadow-none" :disabled="!addressData.district">
                      <option value="">-- Phường/Xã --</option>
                      <option v-for="w in wards" :key="w.code" :value="w.code">{{ w.name }}</option>
                    </select>
                  </div>
                  <div class="col-md-6 mt-2">
                    <input v-model="addressData.street" class="form-control form-control-sm rounded-3 shadow-none" placeholder="Số nhà, tên đường, ngõ ngách..." />
                  </div>
                </div>
              </div>

              <div class="d-flex justify-content-end gap-2 pt-2">
                <button type="button" @click="closeModal" class="btn btn-light rounded-pill px-4 fw-medium">Hủy</button>
                <button type="submit" class="btn rounded-pill px-4 fw-medium" style="background-color: #1e293b; color: white;">Lưu lại</button>
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

const customers = ref<any[]>([]);
const searchQuery = ref("");
const showModal = ref(false);
const modalMode = ref<"add" | "edit">("add");
const formData = ref({ id: null, username: "", name: "", phone: "", email: "", address: "", status: 1 });

const currentPage = ref(Number(route.query.page) || 1);
const pageSize = ref(10);

const provinces = ref<any[]>([]);
const districts = ref<any[]>([]);
const wards = ref<any[]>([]);
const addressData = ref({ province: "", district: "", ward: "", street: "" });

const filteredCustomers = computed(() => {
  const query = searchQuery.value.toLowerCase().trim();
  if (!query) return customers.value;
  return customers.value.filter(c => 
    c.name?.toLowerCase().includes(query) || 
    c.email?.toLowerCase().includes(query) || 
    c.phone?.includes(query) ||
    c.username?.toLowerCase().includes(query)
  );
});

const totalPages = computed(() => Math.ceil(filteredCustomers.value.length / pageSize.value) || 1);
const paginatedCustomers = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  return filteredCustomers.value.slice(start, start + pageSize.value);
});

watch(currentPage, (newPage) => {
  router.replace({ query: { ...route.query, page: newPage } });
});

watch(searchQuery, () => {
  currentPage.value = 1;
});

const fetchProvinces = async () => {
  try {
    const res = await axios.get("https://provinces.open-api.vn/api/p/");
    provinces.value = res.data;
  } catch (error) { console.error("Lỗi API tỉnh"); }
};

const onProvinceChange = async () => {
  addressData.value.district = ""; addressData.value.ward = "";
  districts.value = []; wards.value = [];
  if (!addressData.value.province) return;
  try {
    const res = await axios.get(`https://provinces.open-api.vn/api/p/${addressData.value.province}?depth=2`);
    districts.value = res.data.districts;
  } catch (error) { console.error("Lỗi API huyện"); }
};

const onDistrictChange = async () => {
  addressData.value.ward = ""; wards.value = [];
  if (!addressData.value.district) return;
  try {
    const res = await axios.get(`https://provinces.open-api.vn/api/d/${addressData.value.district}?depth=2`);
    wards.value = res.data.wards;
  } catch (error) { console.error("Lỗi API phường"); }
};

const fetchCustomers = async () => {
  try {
    const res = await axios.get("http://localhost:8080/api/v1/khachHangs");
    customers.value = res.data;
  } catch (error) { console.error("Lỗi tải khách hàng"); }
};

const openModal = async (mode: any, item: any = null) => {
  modalMode.value = mode;
  formData.value = item ? { ...item } : { id: null, username: "", name: "", phone: "", email: "", address: "", status: 1 };
  
  addressData.value = { province: "", district: "", ward: "", street: "" };
  districts.value = []; wards.value = [];
  showModal.value = true;

  if (mode === 'edit' && item?.address) {
    const parts = item.address.split(',').map((p: string) => p.trim());
    if (parts.length >= 4) {
      const pStr = parts[parts.length - 1]; 
      const dStr = parts[parts.length - 2]; 
      const wStr = parts[parts.length - 3]; 
      addressData.value.street = parts.slice(0, parts.length - 3).join(', ');

      const foundProv = provinces.value.find(p => p.name === pStr);
      if (foundProv) {
        addressData.value.province = foundProv.code;
        const resD = await axios.get(`https://provinces.open-api.vn/api/p/${foundProv.code}?depth=2`);
        districts.value = resD.data.districts;

        const foundDist = districts.value.find((d: any) => d.name === dStr);
        if (foundDist) {
          addressData.value.district = foundDist.code;
          const resW = await axios.get(`https://provinces.open-api.vn/api/d/${foundDist.code}?depth=2`);
          wards.value = resW.data.wards;

          const foundWard = wards.value.find((w: any) => w.name === wStr);
          if (foundWard) addressData.value.ward = foundWard.code;
        }
      }
    }
  }
};

const closeModal = () => (showModal.value = false);

const handleSave = async () => {
  const username = formData.value.username?.trim();
  const name = formData.value.name?.trim();
  const email = formData.value.email?.trim();
  const phone = formData.value.phone?.trim();

  let finalAddress = formData.value.address; 
  if (addressData.value.province) {
    const pName = provinces.value.find(p => p.code === addressData.value.province)?.name;
    const dName = districts.value.find(d => d.code === addressData.value.district)?.name;
    const wName = wards.value.find(w => w.code === addressData.value.ward)?.name;
    finalAddress = `${addressData.value.street.trim()}, ${wName}, ${dName}, ${pName}`;
  }

  const actionText = modalMode.value === 'add' ? 'thêm khách hàng mới' : 'cập nhật thông tin';
  const confirmResult = await Swal.fire({
    title: 'Xác nhận lưu?',
    text: `Bạn có chắc chắn muốn ${actionText}?`,
    icon: 'question',
    showCancelButton: true,
    confirmButtonColor: '#1e293b',
    cancelButtonColor: '#94a3b8',
    confirmButtonText: 'Đồng ý',
    cancelButtonText: 'Hủy'
  });

  if (!confirmResult.isConfirmed) return;

  try {
    const payload = { ...formData.value, username, name, email, phone, address: finalAddress, status: Number(formData.value.status) };
    if (modalMode.value === 'add') await axios.post("http://localhost:8080/api/v1/khachHangs", payload);
    else await axios.put(`http://localhost:8080/api/v1/khachHangs/${payload.id}`, payload);
    
    closeModal();
    await fetchCustomers();
    Swal.fire({ icon: 'success', title: 'Thành công', timer: 1500, showConfirmButton: false });
  } catch (error: any) {
    Swal.fire({ icon: 'error', title: 'Lỗi', text: error.response?.data?.message || 'Lỗi hệ thống' });
  }
};

const deleteCustomer = async (id: any) => {
  const result = await Swal.fire({ 
    title: 'Xác nhận xóa?', 
    text: "Dữ liệu sẽ được ẩn khỏi hệ thống!", 
    icon: 'warning', 
    showCancelButton: true, 
    confirmButtonColor: '#d33', 
    cancelButtonColor: '#1e293b', 
    confirmButtonText: 'Đồng ý xóa', 
    cancelButtonText: 'Hủy' 
  });
  if (result.isConfirmed) {
    try {
      await axios.delete(`http://localhost:8080/api/v1/khachHangs/${id}`);
      await fetchCustomers();
      Swal.fire({ icon: 'success', title: 'Đã xóa', timer: 1000, showConfirmButton: false });

      if (paginatedCustomers.value.length === 0 && currentPage.value > 1) {
        currentPage.value--;
      }
    } catch (error: any) {
      Swal.fire({ icon: 'error', title: 'Lỗi', text: error.response?.data?.message || 'Lỗi xóa' });
    }
  }
};

onMounted(() => { fetchCustomers(); fetchProvinces(); });
</script>

<style>
.modern-table thead th { 
  background-color: #f8fafc; color: #64748b; text-transform: uppercase; 
  font-size: 0.75rem; font-weight: 600; padding: 1.25rem 0.75rem; letter-spacing: 0.5px;
}
.text-slate-800 { color: #1e293b !important; }
.text-slate-700 { color: #334155 !important; }
.modern-table tbody tr td { padding: 1rem 0.75rem; border-bottom: 1px solid #f1f5f9; font-size: 0.875rem; vertical-align: middle; }
.btn-action { width: 32px; height: 32px; border-radius: 8px; border: 1px solid #e2e8f0; background: white; color: #64748b; display: inline-flex; align-items: center; justify-content: center; transition: all 0.2s; }
.btn-action:hover { background: #f8fafc; color: #1e293b; border-color: #cbd5e1; }
.btn-action.text-danger:hover { background: #fef2f2; color: #ef4444 !important; border-color: #fca5a5; }
.modal-overlay { position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.4); z-index: 2000; backdrop-filter: blur(2px); }
.swal2-container { z-index: 99999 !important; }
</style>