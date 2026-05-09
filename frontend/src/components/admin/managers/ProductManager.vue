<template>
  <div class="bg-white h-100 d-flex flex-column overflow-hidden rounded-4 shadow-sm border border-slate-100">
    <div class="p-4 border-bottom border-slate-100 d-flex justify-content-between align-items-center z-2">
      <h5 class="fw-bolder text-slate-800 mb-0 tracking-tight text-capitalize">
        Quản lý Sản Phẩm
      </h5>
      <div class="d-flex gap-3">
        <div class="position-relative" style="width: 300px">
          <i class="bi bi-search position-absolute top-50 translate-middle-y text-slate-400 ms-3"></i>
          <input
            v-model="searchQuery"
            type="text"
            class="form-control bg-slate-50 border border-slate-200 text-slate-700 ps-5 py-2 shadow-none rounded-pill fw-medium focus-ring-peel"
            placeholder="Tìm tên sản phẩm..."
          />
        </div>
        <button
          @click="openModal('add')"
          class="btn btn-dark-peel fw-semibold px-4 py-2 rounded-pill shadow-peel hover-lift d-flex align-items-center gap-2"
        >
          <i class="bi bi-phone-fill"></i> Thêm Sản Phẩm
        </button>
      </div>
    </div>

    <div class="p-0 flex-grow-1 overflow-auto custom-scrollbar">
      <table class="table align-middle mb-0 modern-table">
        <thead class="bg-white text-slate-400 small fw-bold tracking-wider text-uppercase" style="position: sticky; top: 0; z-index: 1">
          <tr>
            <th class="ps-4 py-3 border-bottom border-slate-200" width="80">Hình Ảnh</th>
            <th class="py-3 border-bottom border-slate-200">Mã SP</th>
            <th class="py-3 border-bottom border-slate-200">Tên Sản Phẩm</th>
            <th class="py-3 border-bottom border-slate-200">Thương Hiệu</th>
            <th class="py-3 border-bottom border-slate-200 text-center">Tồn Kho Tổng</th>
            <th class="py-3 border-bottom border-slate-200">Trạng Thái</th>
            <th class="text-end pe-4 py-3 border-bottom border-slate-200">Thao tác</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="paginatedData.length === 0">
            <td colspan="7" class="text-center py-5 text-slate-400 fw-medium">
              Chưa có sản phẩm nào.
            </td>
          </tr>
          <tr v-for="product in paginatedData" :key="product.id" class="transition-all hover-bg-slate">
            <td class="ps-4 py-3 border-bottom border-slate-50">
              <img
                v-if="product.mainImageUrl"
                :src="`http://localhost:8080/uploads/${product.mainImageUrl}`"
                alt="img"
                style="width: 50px; height: 50px; object-fit: cover; border-radius: 8px; border: 1px solid #e2e8f0;"
              />
              <div v-else class="bg-light text-slate-400 d-flex align-items-center justify-content-center" style="width: 50px; height: 50px; border-radius: 8px; font-size: 10px; border: 1px dashed #cbd5e1;">
                Trống
              </div>
            </td>
            <td class="py-3 border-bottom border-slate-50 text-slate-500 fw-semibold small">#{{ product.id }}</td>
            <td class="py-3 border-bottom border-slate-50 text-slate-800 fw-bold">{{ product.name }}</td>
            <td class="py-3 border-bottom border-slate-50 text-slate-700 fw-medium">
              <span class="badge bg-light text-dark border px-2 py-1">{{ getBrandName(product.brandId) }}</span>
            </td>
            <td class="py-3 border-bottom border-slate-50 text-primary fw-bold text-center">
              {{ calculateTotalStock(product.variants) }} máy
            </td>
            <td class="py-3 border-bottom border-slate-50">
              <span v-if="product.status === 1" class="badge bg-success bg-opacity-10 text-success px-2 py-1">Đang bán</span>
              <span v-else class="badge bg-danger bg-opacity-10 text-danger px-2 py-1">Ngừng bán</span>
            </td>
            <td class="text-end pe-4 py-3 border-bottom border-slate-50">
              <button @click="openImageModal(product)" class="btn btn-sm btn-white text-info border border-slate-200 shadow-sm rounded-circle me-2 btn-action" title="Tải ảnh lên">
                <i class="bi bi-image-fill"></i>
              </button>
              <button @click="openModal('edit', product)" class="btn btn-sm btn-white text-dark-peel border border-slate-200 shadow-sm rounded-circle me-2 btn-action" title="Sửa">
                <i class="bi bi-pencil-fill"></i>
              </button>
              
              <button 
                v-if="product.status === 1"
                @click="stopSelling(product.id)" 
                class="btn btn-sm btn-white text-warning border border-slate-200 shadow-sm rounded-circle btn-action" 
                title="Tạm ngừng kinh doanh"
              >
                <i class="bi bi-pause-circle-fill"></i>
              </button>
              <button 
                v-else
                disabled
                class="btn btn-sm btn-light text-slate-300 border border-slate-200 shadow-sm rounded-circle btn-action" 
                title="Sản phẩm đã ngừng bán"
              >
                <i class="bi bi-pause-circle"></i>
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="p-3 border-top border-slate-100 bg-white d-flex justify-content-between align-items-center">
      <span class="text-slate-500 small fw-medium">Hiển thị {{ paginatedData.length }} / {{ filteredData.length }} bản ghi</span>
      <div class="d-flex gap-2 align-items-center">
        <button class="btn btn-sm btn-white border-slate-200 px-3 rounded-pill hover-bg-slate" :disabled="currentPage === 1" @click="currentPage--">
          <i class="bi bi-chevron-left"></i> Trước
        </button>
        <span class="fw-bold text-slate-700 mx-2 small">Trang {{ currentPage }} / {{ totalPages }}</span>
        <button class="btn btn-sm btn-white border-slate-200 px-3 rounded-pill hover-bg-slate" :disabled="currentPage === totalPages || totalPages === 0" @click="currentPage++">
          Sau <i class="bi bi-chevron-right"></i>
        </button>
      </div>
    </div>

    <Teleport to="body">
      <transition name="fade-scale">
        <div v-if="showModal" class="modal-overlay d-flex align-items-center justify-content-center" @mousedown.self="closeModal">
          <div class="bg-white rounded-4 shadow-lg modal-content-custom d-flex flex-column overflow-hidden" style="width: 950px; max-width: 95vw; height: 85vh">
            <div class="p-4 border-bottom border-slate-100 d-flex justify-content-between align-items-center bg-slate-50">
              <h5 class="mb-0 fw-bolder text-slate-800">
                {{ modalMode === "add" ? "Thêm Sản Phẩm Mới (Tạo Hồ Sơ)" : "Cập Nhật Sản Phẩm" }}
              </h5>
              <button @click="closeModal" class="btn-close shadow-none"></button>
            </div>

            <div class="p-4 custom-scrollbar overflow-y-auto flex-grow-1">
              <form @submit.prevent="saveData">
                <h6 class="fw-bold text-slate-700 mb-3"><i class="bi bi-info-circle-fill me-2 text-primary"></i>Thông tin chung</h6>
                <div class="row mb-4">
                  <div class="col-md-12 mb-3">
                    <label class="form-label small fw-semibold text-slate-500 mb-1">Tên Sản Phẩm <span class="text-danger">*</span></label>
                    <input v-model="formData.name" type="text" class="form-control shadow-none border-slate-200 fw-bold text-slate-800 rounded-3" required placeholder="VD: iPhone 15 Pro Max"/>
                  </div>
                  <div class="col-md-4 mb-3">
                    <label class="form-label small fw-semibold text-slate-500 mb-1">Thương Hiệu <span class="text-danger">*</span></label>
                    <select v-model="formData.brandId" class="form-select shadow-none border-slate-200 fw-medium rounded-3" required>
                      <option value="" disabled>-- Chọn hãng --</option>
                      <option v-for="brand in brandList" :key="brand.id" :value="brand.id">{{ brand.name }}</option>
                    </select>
                  </div>
                  <div class="col-md-4 mb-3">
                    <label class="form-label small fw-semibold text-slate-500 mb-1">Danh Mục <span class="text-danger">*</span></label>
                    <select v-model="formData.categoryId" class="form-select shadow-none border-slate-200 fw-medium rounded-3" required>
                      <option value="" disabled>-- Chọn danh mục --</option>
                      <option v-for="cat in categoryList" :key="cat.id" :value="cat.id">{{ cat.name }}</option>
                    </select>
                  </div>
                  <div class="col-md-4 mb-3">
                    <label class="form-label small fw-semibold text-slate-500 mb-1">Trạng thái <span class="text-danger">*</span></label>
                    <select v-model="formData.status" class="form-select shadow-none border-slate-200 fw-medium rounded-3" required>
                      <option :value="1">Đang bán</option>
                      <option :value="0">Ngừng kinh doanh</option>
                    </select>
                  </div>
                  <div class="col-md-12">
                    <label class="form-label small fw-semibold text-slate-500 mb-1">Mô tả sản phẩm</label>
                    <textarea v-model="formData.description" class="form-control shadow-none border-slate-200 fw-medium rounded-3" rows="3" placeholder="Nhập mô tả chi tiết..."></textarea>
                  </div>
                </div>

                <div class="d-flex justify-content-between align-items-center mb-3 pt-3 border-top border-slate-100">
                  <h6 class="fw-bold text-slate-700 mb-0">
                    <i class="bi bi-box-seam-fill me-2 text-warning"></i>Cấu hình Phiên Bản (Variants)
                  </h6>
                  <button type="button" @click="addVariant" class="btn btn-sm btn-outline-dark rounded-pill fw-medium px-3">
                    <i class="bi bi-plus-lg"></i> Thêm phiên bản
                  </button>
                </div>
                
                <div class="alert alert-info py-2 small border-0 bg-info bg-opacity-10 d-flex align-items-center mb-3">
                  <i class="bi bi-lightbulb-fill text-info me-2 fs-5"></i>
                  <span><b>Quy tắc Quản lý Kho:</b> Tồn kho tại đây bị khóa (chỉ hiển thị). Bạn cần sang chức năng <b>Quản lý IMEI</b> để nhập hàng thật vào kho.</span>
                </div>

                <div class="table-responsive border border-slate-200 rounded-3 mb-4">
                  <table class="table table-borderless table-sm align-middle mb-0">
                    <thead class="bg-slate-50 text-slate-500 small border-bottom border-slate-200">
                      <tr>
                        <th class="ps-3 py-2" width="18%">Màu Sắc <span class="text-danger">*</span></th>
                        <th class="py-2" width="18%">Dung lượng <span class="text-danger">*</span></th>
                        <th class="py-2" width="25%">Mã SKU <span class="text-danger">*</span></th>
                        <th class="py-2" width="20%">Giá Bán (VNĐ) <span class="text-danger">*</span></th>
                        <th class="py-2" width="12%">Tồn kho</th>
                        <th class="text-center py-2" width="7%">Loại bỏ</th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr v-if="formData.variants.length === 0">
                        <td colspan="6" class="text-center py-4 text-slate-400 small">Vui lòng bấm "Thêm phiên bản".</td>
                      </tr>
                      <tr v-for="(variant, index) in formData.variants" :key="index" class="border-bottom border-slate-100">
                        <td class="ps-3">
                          <select v-model="variant.color" @change="handleDynamicInput($event, variant, 'color')" class="form-select form-select-sm shadow-none rounded-3 fw-medium text-capitalize" required>
                            <option value="" disabled>Chọn màu</option>
                            <option v-for="c in dynamicColors" :key="c" :value="c">{{ c }}</option>
                            <option value="ADD_NEW" class="fw-bold text-primary bg-primary bg-opacity-10">+ Thêm màu mới...</option>
                          </select>
                        </td>
                        <td>
                          <select v-model="variant.storage" @change="handleDynamicInput($event, variant, 'storage')" class="form-select form-select-sm shadow-none rounded-3 fw-medium text-uppercase" required>
                            <option value="" disabled>Dung lượng</option>
                            <option v-for="s in dynamicStorages" :key="s" :value="s">{{ s }}</option>
                            <option value="ADD_NEW" class="fw-bold text-primary bg-primary bg-opacity-10">+ Thêm DL mới...</option>
                          </select>
                        </td>
                        <td>
                          <input v-model="variant.sku" type="text" class="form-control form-control-sm shadow-none rounded-3 fw-bold text-uppercase" required placeholder="Tự điền hoặc nhập..."/>
                        </td>
                        <td>
                          <input v-model.number="variant.price" type="number" min="0" class="form-control form-control-sm shadow-none rounded-3 fw-bold text-success" required placeholder="0"/>
                        </td>
                        <td>
                          <input v-model.number="variant.stock" type="number" class="form-control form-control-sm shadow-none rounded-3 fw-bold text-center bg-slate-100 text-slate-500 border-0" readonly title="Tồn kho chỉ được cộng khi nhập mã IMEI"/>
                        </td>
                        <td class="text-center">
                          <button type="button" @click="removeVariant(index)" class="btn btn-sm btn-light text-danger rounded-circle p-1 btn-action" title="Loại bỏ phiên bản này">
                            <i class="bi bi-trash3-fill"></i>
                          </button>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>

                <div class="d-flex justify-content-end gap-2 pt-3 border-top border-slate-100 bg-white" style="position: sticky; bottom: -1.5rem; padding-bottom: 1rem;">
                  <button type="button" @click="closeModal" class="btn btn-white border border-slate-200 px-4 py-2 rounded-pill fw-semibold text-slate-600 hover-bg-slate">Hủy</button>
                  <button type="submit" class="btn btn-dark-peel px-4 py-2 rounded-pill fw-bold shadow-peel hover-lift">Lưu Hồ Sơ</button>
                </div>
              </form>
            </div>
          </div>
        </div>
      </transition>
    </Teleport>

    <Teleport to="body">
      <transition name="fade-scale">
        <div v-if="showImageModal" class="modal-overlay d-flex align-items-center justify-content-center" @mousedown.self="closeImageModal">
          <div class="bg-white rounded-4 shadow-lg modal-content-custom d-flex flex-column overflow-hidden" style="width: 400px">
            <div class="p-4 border-bottom border-slate-100 d-flex justify-content-between align-items-center bg-slate-50">
              <h5 class="mb-0 fw-bolder text-slate-800">Tải Ảnh Lên</h5>
              <button @click="closeImageModal" class="btn-close shadow-none"></button>
            </div>
            <div class="p-4">
              <div class="mb-3">
                <label class="form-label small fw-semibold text-slate-500 mb-1">Sản phẩm: <span class="fw-bold text-primary">{{ selectedProductName }}</span></label>
                <input type="file" @change="handleFileChange" class="form-control shadow-none border-slate-200 fw-medium rounded-3" accept="image/png, image/jpeg, image/jpg"/>
              </div>
              <div class="d-flex justify-content-end gap-2 pt-3 mt-3 border-top border-slate-100">
                <button type="button" @click="closeImageModal" class="btn btn-white border border-slate-200 px-4 py-2 rounded-pill fw-semibold text-slate-600 hover-bg-slate">Hủy</button>
                <button type="button" @click="uploadImage" class="btn btn-info px-4 py-2 rounded-pill fw-bold text-white shadow-sm hover-lift" :disabled="!selectedFile">Tải lên ngay</button>
              </div>
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

interface VariantForm {
  id?: number | null;
  color: string;
  storage: string;
  price: number;
  stock: number;
  sku: string;
}

interface ProductForm {
  id?: number | null;
  name: string;
  brandId: number | string;
  categoryId: number | string;
  description: string;
  status: number;
  variants: VariantForm[];
}

const router = useRouter();
const route = useRoute();
const searchQuery = ref("");
const showModal = ref(false);
const modalMode = ref<"add" | "edit">("add");
const tableData = ref<any[]>([]);

const showImageModal = ref(false);
const selectedProductId = ref<number | null>(null);
const selectedProductName = ref("");
const selectedFile = ref<File | null>(null);

const brandList = ref<any[]>([]);
const categoryList = ref<any[]>([]);

const dynamicColors = ref(["Đen", "Trắng", "Xám", "Vàng", "Titan", "Xanh", "Bạc", "Đỏ"]);
const dynamicStorages = ref(["64GB", "128GB", "256GB", "512GB", "1TB", "2TB"]);

const handleDynamicInput = async (event: Event, variant: any, type: 'color' | 'storage') => {
  const target = event.target as HTMLSelectElement;
  if (target.value === "ADD_NEW") {
    const isColor = type === 'color';
    const { value: newValue } = await Swal.fire({
      title: isColor ? 'Nhập màu sắc mới' : 'Nhập dung lượng mới',
      input: 'text',
      inputPlaceholder: isColor ? 'VD: Titan Sa Mạc' : 'VD: 1.5TB',
      showCancelButton: true,
      confirmButtonText: 'Thêm',
      cancelButtonText: 'Hủy'
    });

    if (newValue && newValue.trim() !== '') {
      const formattedValue = newValue.trim();
      if (isColor && !dynamicColors.value.includes(formattedValue)) {
        dynamicColors.value.push(formattedValue);
      } else if (!isColor && !dynamicStorages.value.includes(formattedValue)) {
        dynamicStorages.value.push(formattedValue);
      }
      variant[type] = formattedValue;
    } else {
      variant[type] = ""; 
    }
  }
};

const getBrandName = (id: number) => {
  const brand = brandList.value.find((b) => b.id === id);
  return brand ? brand.name : `Hãng #${id}`;
};

const calculateTotalStock = (variants: any[]) => {
  if (!variants || variants.length === 0) return 0;
  return variants.reduce((total, v) => total + (v.stock || 0), 0);
};

const fetchDropdownData = async () => {
  try {
    const brandRes = await axios.get("http://localhost:8080/api/v1/brands");
    brandList.value = brandRes.data;

    const catRes = await axios.get("http://localhost:8080/api/v1/categories");
    categoryList.value = catRes.data;
  } catch (error) {
    console.error("Lỗi tải Dropdown:", error);
  }
};

onMounted(() => { 
  fetchData(); 
  fetchDropdownData(); 
});

const formData = ref<ProductForm>({
  name: "", brandId: "", categoryId: "", description: "", status: 1, variants: [],
});

const currentPage = ref(Number(route.query.page) || 1);
const pageSize = ref(10);

const filteredData = computed(() => {
  if (!searchQuery.value) return tableData.value;
  return tableData.value.filter((item) =>
    item.name?.toLowerCase().includes(searchQuery.value.toLowerCase())
  );
});

const totalPages = computed(() => Math.ceil(filteredData.value.length / pageSize.value) || 1);
const paginatedData = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  return filteredData.value.slice(start, start + pageSize.value);
});

watch(currentPage, (newPage) => router.replace({ query: { ...route.query, page: newPage } }));
watch(searchQuery, () => currentPage.value = 1);

const API_URL = "http://localhost:8080/api/v1/products";

const fetchData = async () => {
  try {
    const res = await axios.get(API_URL);
    tableData.value = res.data.products || [];
  } catch (error) {
    tableData.value = [];
  }
};

const addVariant = () =>
  formData.value.variants.push({
    color: "", storage: "", price: 0, stock: 0, sku: "",
  });

const removeVariant = (index: number) => formData.value.variants.splice(index, 1);

const openModal = (mode: "add" | "edit", item: any = null) => {
  modalMode.value = mode;
  if (mode === "edit" && item) {
    item.variants?.forEach((v: any) => {
      if (v.color && !dynamicColors.value.includes(v.color)) dynamicColors.value.push(v.color);
      if (v.storage && !dynamicStorages.value.includes(v.storage)) dynamicStorages.value.push(v.storage);
    });

    formData.value = {
      id: item.id,
      name: item.name,
      brandId: item.brandId,
      categoryId: item.categoryId,
      description: item.description,
      status: item.status !== undefined ? item.status : 1,
      variants: item.variants ? item.variants.map((v: any) => ({
            id: v.variantId, color: v.color, storage: v.storage, price: v.price, stock: v.stock, sku: v.sku,
          })) : [],
    };
  } else {
    formData.value = { name: "", brandId: "", categoryId: "", description: "", status: 1, variants: [] };
    addVariant();
  }
  showModal.value = true;
};

const closeModal = () => (showModal.value = false);

const saveData = async () => {
  if (formData.value.variants.length === 0) {
    Swal.fire({ icon: "warning", title: "Thiếu thông tin", text: "Sản phẩm phải có ít nhất 1 phiên bản!" });
    return;
  }

  const payload: any = {
    name: formData.value.name,
    brandId: Number(formData.value.brandId),
    categoryId: Number(formData.value.categoryId),
    description: formData.value.description,
    status: Number(formData.value.status),
    variants: formData.value.variants.map((v) => ({
      id: v.id, color: v.color, storage: v.storage, price: Number(v.price), stock: Number(v.stock), sku: v.sku.toUpperCase(),
    })),
  };

  try {
    if (modalMode.value === "add") {
      await axios.post(`${API_URL}/admin`, payload);
      Swal.fire({ icon: "success", title: "Tạo hồ sơ thành công!", timer: 1500, showConfirmButton: false });
    } else {
      await axios.put(`${API_URL}/admin/${formData.value.id}`, payload);
      Swal.fire({ icon: "success", title: "Cập nhật thành công!", timer: 1500, showConfirmButton: false });
    }
    closeModal();
    await fetchData();
  } catch (error: any) {
    let errorMsg = "Vui lòng kiểm tra lại dữ liệu.";
    if (error.response?.data) {
      if (typeof error.response.data === 'string') errorMsg = error.response.data;
      else if (error.response.data.message) errorMsg = error.response.data.message;
      else if (typeof error.response.data === 'object') {
        const errorValues = Object.values(error.response.data);
        if (errorValues.length > 0) errorMsg = String(errorValues[0]);
      }
    }
    Swal.fire({ icon: "error", title: "Không thể lưu!", text: errorMsg });
  }
};

// ĐỔI TÊN HÀM TỪ deleteItem THÀNH stopSelling
const stopSelling = async (id: string | number) => {
  const result = await Swal.fire({
    title: "Ngừng kinh doanh?", 
    text: "Sản phẩm này sẽ bị ẩn khỏi gian hàng, bạn chắc chắn chứ?", 
    icon: "warning", 
    showCancelButton: true, 
    confirmButtonColor: "#f59e0b", // Màu cam cảnh báo
    confirmButtonText: "Tạm dừng",
    cancelButtonText: "Hủy"
  });
  if (result.isConfirmed) {
    try {
      await axios.delete(`${API_URL}/admin/${id}`);
      Swal.fire({ icon: "success", title: "Đã tạm dừng!", timer: 1000, showConfirmButton: false });
      await fetchData();
    } catch (error: any) { 
      Swal.fire({ icon: "error", title: "Lỗi", text: "Không thể thao tác!" }); 
    }
  }
};

const openImageModal = (product: any) => { selectedProductId.value = product.id; selectedProductName.value = product.name; selectedFile.value = null; showImageModal.value = true; };
const closeImageModal = () => { showImageModal.value = false; selectedFile.value = null; };

// ĐÃ SỬA LỖI ĐỎ TYPESCRIPT
const handleFileChange = (event: Event) => { 
  const target = event.target as HTMLInputElement; 
  if (target && target.files && target.files.length > 0) {
    selectedFile.value = target.files[0]!; 
  }
};

const uploadImage = async () => {
  if (!selectedFile.value || !selectedProductId.value) return;
  const formDataUpload = new FormData(); formDataUpload.append("file", selectedFile.value);
  try {
    await axios.post(`${API_URL}/admin/${selectedProductId.value}/images`, formDataUpload, { headers: { "Content-Type": "multipart/form-data" } });
    Swal.fire({ icon: "success", title: "Đã tải ảnh lên!", timer: 1500, showConfirmButton: false });
    closeImageModal(); await fetchData();
  } catch (error) { Swal.fire({ icon: "error", title: "Lỗi upload!", text: "Không thể tải ảnh lên Server." }); }
};
</script>

<style scoped>
.text-slate-300 { color: #cbd5e1 !important; }
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
.modal-content-custom { margin: 0 auto; }
.fade-scale-enter-active, .fade-scale-leave-active { transition: opacity 0.2s, transform 0.2s; }
.fade-scale-enter-from, .fade-scale-leave-to { opacity: 0; transform: scale(0.95); }
</style>