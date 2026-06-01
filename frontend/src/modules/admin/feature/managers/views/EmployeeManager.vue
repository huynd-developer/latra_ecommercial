<template>
  <div
    class="bg-white h-100 d-flex flex-column overflow-hidden rounded-4 shadow-sm border border-slate-100"
  >
    <div
      class="p-4 border-bottom border-slate-100 d-flex justify-content-between align-items-center"
    >
      <h5 class="fw-bolder text-slate-800 mb-0">Quản lý Nhân Viên</h5>
      <div class="d-flex gap-3 align-items-center">
        <div class="position-relative">
          <i
            class="bi bi-search position-absolute top-50 start-0 translate-middle-y ms-3 text-muted"
          ></i>
          <input
            v-model="searchQuery"
            type="text"
            class="form-control rounded-pill ps-5 shadow-none border-slate-200"
            placeholder="Tìm mã, tên, SĐT..."
            style="width: 280px; font-size: 0.9rem"
          />
        </div>
        <button
          v-permission="['OWNER']"
          @click="openModal('add')"
          class="btn btn-dark rounded-pill px-4 d-flex align-items-center gap-2"
          style="background-color: #1e293b; color: white"
        >
          <i class="bi bi-plus-lg"></i> Thêm nhân viên
        </button>
      </div>
    </div>

    <div class="flex-grow-1 overflow-auto">
      <table class="table align-middle modern-table mb-0">
        <thead class="bg-white sticky-top">
          <tr>
            <th class="ps-4">Mã NV</th>
            <th>Tên nhân viên</th>
            <th>Liên hệ</th>
            <th>Vai trò</th>
            <th>Địa chỉ thường trú</th>
            <th>Trạng thái</th>
            <th class="text-end pe-4">Thao tác</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="paginatedEmployees.length === 0">
            <td colspan="7" class="text-center py-5 text-muted">
              Không tìm thấy dữ liệu nhân viên...
            </td>
          </tr>
          <tr v-for="emp in paginatedEmployees" :key="emp.id">
            <td class="ps-4 fw-medium text-slate-700">
              {{ emp.username }}
              <span
                v-if="emp.id === 1"
                class="badge bg-danger ms-2"
                style="font-size: 0.65rem"
                >ROOT</span
              >
            </td>
            <td class="fw-semibold text-slate-800">{{ emp.fullName }}</td>
            <td>
              <div class="small text-slate-800 fw-medium">{{ emp.phone }}</div>
              <div class="small text-muted">{{ emp.email }}</div>
            </td>
            <td>
              <span
                class="badge text-dark border px-2 py-1 fw-bold"
                :class="{
                  'bg-warning-subtle text-warning-dark': emp.roleId == 1,
                  'bg-info-subtle text-info-dark': emp.roleId == 2,
                  'bg-light text-secondary': emp.roleId == 3 || !emp.roleId,
                }"
                style="font-size: 0.75rem"
              >
                {{
                  emp.roleId == 1
                    ? "OWNER"
                    : emp.roleId == 2
                    ? "MANAGER"
                    : "CASHIER"
                }}
              </span>
            </td>
            <td>
              <div
                class="text-truncate small text-slate-700 fw-medium"
                style="max-width: 220px"
                :title="emp.address"
              >
                {{ emp.address || "---" }}
              </div>
            </td>
            <td>
              <span v-if="emp.status == 1" class="status-active"
                >Hoạt động</span
              >
              <span v-else class="status-locked">Bị khóa</span>
            </td>
            <td class="text-end pe-4">
              <template v-if="emp.id !== 1">
                <button
                  v-permission="['OWNER']"
                  @click="openModal('edit', emp)"
                  class="btn-action me-2"
                  title="Sửa thông tin"
                >
                  <i class="bi bi-pencil-fill"></i>
                </button>
                <button
                  v-permission="['OWNER']"
                  @click="handleDelete(emp.id)"
                  class="btn-action text-danger"
                  title="Xóa nhân viên"
                >
                  <i class="bi bi-trash3-fill"></i>
                </button>
              </template>
              <template v-else>
                <span class="text-muted small fst-italic"
                  >Bất khả xâm phạm</span
                >
              </template>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div
      class="p-3 border-top border-slate-100 bg-white d-flex justify-content-between align-items-center"
    >
      <span class="text-slate-500 small fw-medium">
        Hiển thị {{ paginatedEmployees.length }} /
        {{ filteredEmployees.length }} bản ghi
      </span>
      <div class="d-flex gap-2 align-items-center">
        <button
          class="btn btn-sm btn-light border-slate-200 px-3 rounded-pill"
          :disabled="currentPage === 1"
          @click="currentPage--"
        >
          Trước
        </button>
        <span class="fw-bold text-slate-700 mx-2 small"
          >Trang {{ currentPage }} / {{ totalPages }}</span
        >
        <button
          class="btn btn-sm btn-light border-slate-200 px-3 rounded-pill"
          :disabled="currentPage === totalPages || totalPages === 0"
          @click="currentPage++"
        >
          Sau
        </button>
      </div>
    </div>

    <Teleport to="body">
      <div
        v-if="showModal"
        class="modal-overlay d-flex align-items-center justify-content-center"
        @mousedown.self="closeModal"
      >
        <div
          class="bg-white rounded-5 shadow-lg overflow-hidden animate__animated animate__zoomIn animate__faster"
          style="width: 650px; border: none"
        >
          <div
            class="p-4 border-bottom border-slate-100 d-flex justify-content-between align-items-center bg-light"
          >
            <h5 class="mb-0 fw-bold">
              {{
                modalMode === "add" ? "Thêm Nhân Viên" : "Cập Nhật Nhân Viên"
              }}
            </h5>
            <button @click="closeModal" class="btn-close shadow-none"></button>
          </div>
          <div class="p-4 px-5">
            <form @submit.prevent="handleSave">
              <div class="row g-3 mb-3">
                <div class="col-md-6">
                  <label class="form-label small fw-bold"
                    >Mã NV (Username)</label
                  >
                  <input
                    v-model="formData.username"
                    class="form-control rounded-3"
                    placeholder="VD: NV001"
                    required
                    :disabled="modalMode === 'edit'"
                  />
                </div>
                <div class="col-md-6">
                  <label class="form-label small fw-bold">Họ tên</label>
                  <input
                    v-model="formData.fullName"
                    class="form-control rounded-3"
                    placeholder="VD: Nguyễn Văn A"
                    required
                  />
                </div>
              </div>

              <div class="row g-3 mb-3">
                <div class="col-md-6">
                  <label class="form-label small fw-bold">Số điện thoại</label>
                  <input
                    v-model="formData.phone"
                    class="form-control rounded-3"
                    placeholder="VD: 0912..."
                    required
                  />
                </div>
                <div class="col-md-6">
                  <label class="form-label small fw-bold">Email</label>
                  <input
                    type="email"
                    v-model="formData.email"
                    class="form-control rounded-3"
                    placeholder="VD: nhanvien@latra.com"
                    required
                  />
                </div>
              </div>

              <div class="p-3 bg-light rounded-3 mb-3 border">
                <label class="form-label small fw-bold mb-2"
                  ><i class="bi bi-geo-alt-fill text-danger"></i> Địa chỉ thường
                  trú</label
                >
                <div class="row g-2">
                  <div class="col-md-6">
                    <select
                      v-model="addressData.province"
                      @change="onProvinceChange"
                      class="form-select form-select-sm rounded-3"
                      required
                    >
                      <option value="">-- Tỉnh/Thành --</option>
                      <option v-for="p in provinces" :key="p.id" :value="p.id">
                        {{ p.name }}
                      </option>
                    </select>
                  </div>
                  <div class="col-md-6">
                    <select
                      v-model="addressData.district"
                      @change="onDistrictChange"
                      class="form-select form-select-sm rounded-3"
                      :disabled="!addressData.province"
                      required
                    >
                      <option value="">-- Quận/Huyện --</option>
                      <option v-for="d in districts" :key="d.id" :value="d.id">
                        {{ d.name }}
                      </option>
                    </select>
                  </div>
                  <div class="col-md-6">
                    <select
                      v-model="addressData.ward"
                      class="form-select form-select-sm rounded-3"
                      :disabled="!addressData.district"
                      required
                    >
                      <option value="">-- Phường/Xã --</option>
                      <option v-for="w in wards" :key="w.id" :value="w.id">
                        {{ w.name }}
                      </option>
                    </select>
                  </div>
                  <div class="col-md-6">
                    <input
                      v-model="addressData.street"
                      class="form-control form-control-sm rounded-3"
                      placeholder="Số nhà, tên đường..."
                      required
                    />
                  </div>
                </div>
                <small class="text-muted mt-1 d-block" style="font-size: 0.7rem"
                  >* Tất cả thông tin địa chỉ là bắt buộc để đồng bộ hệ thống dữ
                  liệu.</small
                >
              </div>

              <div class="row g-3 mb-4">
                <div class="col-md-6">
                  <label class="form-label small fw-bold"
                    >Vai trò hệ thống</label
                  >
                  <select
                    v-model="formData.roleId"
                    class="form-select rounded-3"
                    required
                  >
                    <option value="" disabled>-- Chọn vai trò --</option>
                    <option :value="1">OWNER (Chủ hệ thống)</option>
                    <option :value="2">MANAGER (Quản lý)</option>
                    <option :value="3">CASHIER (Thu ngân)</option>
                  </select>
                </div>
                <div class="col-md-6">
                  <label class="form-label small fw-bold">Trạng thái</label>
                  <select
                    v-model="formData.status"
                    class="form-select rounded-3"
                    required
                  >
                    <option :value="1">Hoạt động</option>
                    <option :value="0">Bị khóa</option>
                  </select>
                </div>
              </div>

              <div class="d-flex justify-content-end gap-2 border-top pt-3">
                <button
                  type="button"
                  @click="closeModal"
                  class="btn btn-light rounded-pill px-4"
                >
                  Hủy
                </button>
                <button
                  v-permission="['OWNER']"
                  type="submit"
                  class="btn btn-dark rounded-pill px-4 fw-bold"
                >
                  Lưu Thông Tin
                </button>
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
import Swal from "sweetalert2";
import request from "@/common/services/request";

const router = useRouter();
const route = useRoute();

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
const formData = ref({
  id: null,
  username: "",
  fullName: "",
  phone: "",
  email: "",
  address: "",
  roleId: "" as any,
  status: 1,
});

const Toast = Swal.mixin({
  toast: true,
  position: "top-end",
  showConfirmButton: false,
  timer: 2500,
  timerProgressBar: true,
  didOpen: (toast) => {
    toast.addEventListener("mouseenter", Swal.stopTimer);
    toast.addEventListener("mouseleave", Swal.resumeTimer);
  },
});

const fetchEmployees = async () => {
  try {
    const res = await request.get("/nhanViens");
    employees.value = (res as any) || [];
  } catch (error) {
    console.error("Lỗi tải danh sách:", error);
  }
};

const normalizeStr = (str: string): string => {
  if (!str) return "";
  return str
    .toLowerCase()
    .normalize("NFD")
    .replace(/[\u0300-\u036f]/g, "")
    .replace(/đ/g, "d")
    .replace(/[^a-z0-9\s]/g, "")
    .replace(/\s+/g, " ")
    .trim();
};

const openModal = async (mode: "add" | "edit", item: any = null) => {
  modalMode.value = mode;
  districts.value = [];
  wards.value = [];

  if (mode === "edit" && item) {
    formData.value = { ...item };
    formData.value.roleId = item.roleId ? Number(item.roleId) : "";
    formData.value.status = item.status !== undefined ? Number(item.status) : 1;

    if (item.address && item.address.includes(",")) {
      const parts = item.address.split(",").map((p: string) => p.trim());
      if (parts.length >= 4) {
        const pStr = parts[parts.length - 1] || "";
        const dStr = parts[parts.length - 2] || "";
        const wStr = parts[parts.length - 3] || "";
        addressData.value.street = parts.slice(0, parts.length - 3).join(", ");

        const foundProv = provinces.value.find(
          (p) =>
            normalizeStr(p.name).includes(normalizeStr(pStr)) ||
            normalizeStr(pStr).includes(normalizeStr(p.name))
        );

        if (foundProv) {
          addressData.value.province = foundProv.id;
          try {
            const resD = await fetch(
              `https://esgoo.net/api-tinhthanh/2/${foundProv.id}.htm`
            ).then((r) => r.json());
            if (resD.error === 0) {
              districts.value = resD.data;
              const foundDist = districts.value.find(
                (d) =>
                  normalizeStr(d.name).includes(normalizeStr(dStr)) ||
                  normalizeStr(dStr).includes(normalizeStr(d.name))
              );
              if (foundDist) {
                addressData.value.district = foundDist.id;
                const resW = await fetch(
                  `https://esgoo.net/api-tinhthanh/3/${foundDist.id}.htm`
                ).then((r) => r.json());
                if (resW.error === 0) {
                  wards.value = resW.data;
                  const foundWard = wards.value.find(
                    (w) =>
                      normalizeStr(w.name).includes(normalizeStr(wStr)) ||
                      normalizeStr(wStr).includes(normalizeStr(w.name))
                  );
                  if (foundWard) addressData.value.ward = foundWard.id;
                }
              }
            }
          } catch (e) {
            console.error("Lỗi dữ liệu địa chỉ cũ không khớp API:", e);
          }
        }
      } else {
        addressData.value = {
          province: "",
          district: "",
          ward: "",
          street: item.address,
        };
      }
    } else {
      addressData.value = {
        province: "",
        district: "",
        ward: "",
        street: item.address || "",
      };
    }
  } else {
    formData.value = {
      id: null,
      username: "",
      fullName: "",
      phone: "",
      email: "",
      address: "",
      roleId: "",
      status: 1,
    };
    addressData.value = { province: "", district: "", ward: "", street: "" };
  }
  showModal.value = true;
};

const handleSave = async () => {
  if (document.activeElement instanceof HTMLElement) {
    document.activeElement.blur();
  }

  const phone = formData.value.phone?.trim() || "";
  const phoneRegex = /^0[35789][0-9]{8}$/;
  if (!phoneRegex.test(phone)) {
    Swal.fire({
      icon: "warning",
      title: "Số điện thoại không hợp lệ",
      text: "SĐT nhân viên phải có 10 chữ số và bắt đầu bằng các đầu số di động chuẩn (03, 05, 07, 08, 09).",
      confirmButtonColor: "#1e293b",
    });
    return;
  }

  if (
    !addressData.value.province ||
    !addressData.value.district ||
    !addressData.value.ward ||
    !addressData.value.street.trim()
  ) {
    Swal.fire({
      icon: "warning",
      title: "Thiếu địa chỉ",
      text: "Vui lòng chọn đầy đủ Tỉnh/Thành, Quận/Huyện, Phường/Xã và Số nhà cụ thể.",
      confirmButtonColor: "#1e293b",
    });
    return;
  }

  const p = provinces.value.find(
    (x) => x.id === addressData.value.province
  )?.name;
  const d = districts.value.find(
    (x) => x.id === addressData.value.district
  )?.name;
  const w = wards.value.find((x) => x.id === addressData.value.ward)?.name;
  formData.value.address = `${addressData.value.street.trim()}, ${w}, ${d}, ${p}`;

  const roleName =
    formData.value.roleId === 1
      ? "OWNER (Chủ hệ thống)"
      : formData.value.roleId === 2
      ? "MANAGER (Quản lý)"
      : "CASHIER (Thu ngân)";

  const confirmResult = await Swal.fire({
    title: "Xác nhận lưu thông tin?",
    html: `
      <div class="text-start border p-3 rounded-3 bg-light" style="font-size: 0.9rem;">
        <p class="mb-1"><b>Họ tên:</b> ${formData.value.fullName}</p>
        <p class="mb-1"><b>Tài khoản:</b> ${formData.value.username}</p>
        <p class="mb-1"><b>Vai trò:</b> <span class="text-primary fw-bold">${roleName}</span></p>
        <p class="mb-0 text-truncate"><b>Địa chỉ:</b> ${formData.value.address}</p>
      </div>
      <p class="mt-3 mb-0 text-center text-muted small">Bạn có chắc chắn muốn thực hiện hành động này?</p>
    `,
    icon: "question",
    showCancelButton: true,
    confirmButtonColor: "#1e293b",
    cancelButtonColor: "#94a3b8",
    confirmButtonText: "Đồng ý",
    cancelButtonText: "Hủy",
  });

  if (!confirmResult.isConfirmed) return;

  try {
    if (modalMode.value === "add") {
      await request.post("/nhanViens", formData.value);
    } else {
      await request.put(`/nhanViens/${formData.value.id}`, formData.value);
    }

    closeModal();

    Toast.fire({
      icon: "success",
      title:
        modalMode.value === "add"
          ? "Thêm mới thành công!"
          : "Đã cập nhật thông tin!",
    });

    await fetchEmployees();
  } catch (error: any) {
    Swal.fire({
      icon: "error",
      title: "Lỗi",
      text: error.response?.data?.message || "Thao tác thất bại!",
    });
  }
};

const handleDelete = async (id: number) => {
  if (id === 1) {
    Toast.fire({ icon: "error", title: "Không thể xóa tài khoản ROOT!" });
    return;
  }

  const result = await Swal.fire({
    title: "Xác nhận ngừng hoạt động?",
    // 🔥 FIX LOGIC 2: Sửa lại nội dung text vì hành động này ở Backend là set status = 2 (xóa khỏi danh sách SQL)
    text: "Nhân viên sẽ bị xóa khỏi danh sách hoạt động!",
    icon: "warning",
    showCancelButton: true,
    confirmButtonColor: "#ef4444",
    cancelButtonColor: "#1e293b",
    confirmButtonText: "Xóa ngay",
    cancelButtonText: "Hủy",
  });

  if (result.isConfirmed) {
    try {
      await request.delete(`/nhanViens/${id}`);
      Toast.fire({ icon: "success", title: "Đã xóa nhân viên khỏi hệ thống!" });

      await fetchEmployees();
      if (paginatedEmployees.value.length === 0 && currentPage.value > 1) {
        currentPage.value--;
      }
    } catch (error: any) {
      Swal.fire({
        icon: "error",
        title: "Lỗi",
        text: error.response?.data?.message || "Không thể xóa nhân viên này!",
      });
    }
  }
};

const fetchProvinces = async () => {
  try {
    const response = await fetch("https://esgoo.net/api-tinhthanh/1/0.htm");
    const res = await response.json();
    if (res.error === 0) {
      provinces.value = res.data;
    }
  } catch (e) {
    console.error("Lỗi lấy Tỉnh:", e);
  }
};

const onProvinceChange = async () => {
  if (!addressData.value.province) {
    districts.value = [];
    wards.value = [];
    return;
  }
  try {
    const response = await fetch(
      `https://esgoo.net/api-tinhthanh/2/${addressData.value.province}.htm`
    );
    const res = await response.json();
    if (res.error === 0) {
      districts.value = res.data;
    }
    addressData.value.district = "";
    addressData.value.ward = "";
    wards.value = [];
  } catch (e) {
    console.error("Lỗi lấy Huyện:", e);
  }
};

const onDistrictChange = async () => {
  if (!addressData.value.district) {
    wards.value = [];
    return;
  }
  try {
    const response = await fetch(
      `https://esgoo.net/api-tinhthanh/3/${addressData.value.district}.htm`
    );
    const res = await response.json();
    if (res.error === 0) {
      wards.value = res.data;
    }
    addressData.value.ward = "";
  } catch (e) {
    console.error("Lỗi lấy Xã:", e);
  }
};

const closeModal = () => {
  showModal.value = false;
};

const filteredEmployees = computed(() => {
  const q = searchQuery.value.toLowerCase().trim();
  return employees.value.filter(
    (e) =>
      e.fullName?.toLowerCase().includes(q) ||
      e.username?.toLowerCase().includes(q) ||
      (e.phone && e.phone.includes(q))
  );
});

const totalPages = computed(
  () => Math.ceil(filteredEmployees.value.length / pageSize.value) || 1
);

const paginatedEmployees = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  return filteredEmployees.value.slice(start, start + pageSize.value);
});

watch(currentPage, (newPage) => {
  router.replace({ query: { ...route.query, page: newPage } });
});

watch(searchQuery, () => {
  currentPage.value = 1;
});

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
.modern-table tbody tr {
  transition: background-color 0.2s;
}
.modern-table tbody tr:hover {
  background-color: #f8fafc;
}

/* STATUS BADGE */
.status-active {
  color: #059669;
  background: #d1fae5;
  padding: 4px 10px;
  border-radius: 6px;
  font-size: 0.8rem;
  font-weight: 500;
}
.status-locked {
  color: #64748b;
  background: #f1f5f9;
  padding: 4px 10px;
  border-radius: 6px;
  font-size: 0.8rem;
  font-weight: 500;
}

/* COLOR EXTRA FOR ROLES */
.bg-warning-subtle {
  background-color: #fef3c7 !important;
  color: #b45309 !important;
}
.bg-info-subtle {
  background-color: #e0f2fe !important;
  color: #0369a1 !important;
}

/* ACTION BUTTONS */
.btn-action {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
  background: white;
  color: #64748b;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}
.btn-action:hover {
  background: #f1f5f9;
  color: #1e293b;
  transform: translateY(-1px);
}
.btn-action.text-danger:hover {
  background: #fef2f2;
  color: #ef4444 !important;
}

/* MODAL OVERLAY */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(15, 23, 42, 0.4);
  z-index: 2000;
  backdrop-filter: blur(4px);
}

/* CUSTOM SCROLLBAR */
.custom-scrollbar::-webkit-scrollbar {
  width: 6px;
}
.custom-scrollbar::-webkit-scrollbar-track {
  background: transparent;
}
.custom-scrollbar::-webkit-scrollbar-thumb {
  background: #e2e8f0;
  border-radius: 10px;
}

/* GLOBAL SWAL Z-INDEX */
:deep(.swal2-container) {
  z-index: 3000 !important;
}
</style>
