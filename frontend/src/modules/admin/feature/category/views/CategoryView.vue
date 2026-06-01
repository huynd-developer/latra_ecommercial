<template>
  <div class="bg-white h-100 d-flex flex-column overflow-hidden rounded-4 shadow-sm border border-slate-100 p-4 min-vh-100">
    <div class="d-flex flex-column flex-md-row justify-content-between align-items-md-center mb-4 gap-3 border-bottom pb-3">
      <div>
        <h5 class="fw-bolder text-slate-800 mb-1"><i class="bi bi-folder-fill text-primary me-2"></i>Quản Lý Danh Mục</h5>
        <span class="small text-muted">Tổ chức cây thư mục cho Sản phẩm</span>
      </div>
      <div class="d-flex gap-2">
        <div class="position-relative" style="min-width: 250px;">
          <i class="bi bi-search position-absolute top-50 start-0 translate-middle-y ms-3 text-muted"></i>
          <input v-model="searchQuery" type="text" class="form-control rounded-pill ps-5 py-2 shadow-none border-slate-200" placeholder="Tìm tên danh mục..." />
        </div>
        
        <button v-permission="['OWNER', 'MANAGER']" @click="openAddModal" class="btn btn-dark-peel rounded-pill px-4 fw-bold text-nowrap d-flex align-items-center gap-2">
          <i class="bi bi-plus-lg"></i> Thêm Mới
        </button>
      </div>
    </div>

    <div class="table-responsive flex-grow-1 custom-scrollbar border rounded-3">
      <table class="table align-middle table-hover mb-0">
        <thead class="bg-slate-50 text-muted small text-uppercase sticky-top" style="z-index: 1;">
          <tr>
            <th class="ps-4 py-3 border-bottom-0">ID</th>
            <th class="border-bottom-0">Tên Danh Mục</th>
            <th class="border-bottom-0">Thuộc Cấp (Danh mục cha)</th>
            <th class="text-center border-bottom-0">Trạng Thái</th>
            <th class="text-end pe-4 border-bottom-0">Thao tác</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="filteredCategories.length === 0">
            <td colspan="5" class="text-center py-5 text-muted">
              <i class="bi bi-inbox fs-2 opacity-50 mb-2 d-block"></i>
              Không tìm thấy danh mục nào.
            </td>
          </tr>
          <tr v-for="cat in filteredCategories" :key="cat.id" class="transition-all" :class="{ 'opacity-50 bg-light': cat.status !== 1 }">
            <td class="ps-4 text-muted fw-bold font-monospace small">#{{ cat.id }}</td>
            <td class="fw-bold text-slate-800 fs-6">{{ cat.name }}</td>
            <td class="text-slate-600 fw-medium">
              <span v-if="cat.parentId === null" class="badge bg-slate-200 text-slate-700 px-2 py-1"><i class="bi bi-diagram-3-fill me-1"></i> Danh Mục Gốc</span>
              <span v-else class="text-primary"><i class="bi bi-arrow-return-right me-1"></i> {{ store.getParentName(cat.parentId) }}</span>
            </td>
            <td class="text-center">
              <span v-if="cat.status === 1" class="badge bg-success bg-opacity-10 text-success px-2 py-1">Hoạt động</span>
              <span v-else class="badge bg-danger bg-opacity-10 text-danger px-2 py-1">Đã khóa</span>
            </td>
            <td class="text-end pe-4">
              <button v-permission="['OWNER', 'MANAGER']" @click="openEditModal(cat)" class="btn btn-sm btn-light text-primary rounded-circle me-1 shadow-sm" title="Chỉnh sửa">
                <i class="bi bi-pencil-fill"></i>
              </button>
              
              <button 
                v-permission="['OWNER']"
                @click="deleteCategory(cat.id)" 
                class="btn btn-sm btn-light rounded-circle shadow-sm"
                :class="cat.status === 1 ? 'text-danger' : 'text-muted'"
                :disabled="cat.status !== 1"
                title="Khóa/Xóa"
              >
                <i class="bi bi-trash3-fill"></i>
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <Teleport to="body">
      <transition name="fade">
        <CategoryModal 
          v-if="showModal" 
          :category-selected="selectedCategory" 
          :all-categories="store.activeCategories"
          @close="showModal = false" 
          @refresh="store.fetchCategories" 
        />
      </transition>
    </Teleport>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from "vue";
import { useCategoryStore } from "../stores/categoryStore";
import { categoryService } from "../services/categoryService";
import { useAppStore } from "@/common/store/app.store";
import CategoryModal from "../components/CategoryModal.vue";
import Swal from "sweetalert2";

const store = useCategoryStore();
const appStore = useAppStore();

const searchQuery = ref("");
const showModal = ref(false);
const selectedCategory = ref<any>(null);

onMounted(() => {
  store.fetchCategories();
});

const filteredCategories = computed(() => {
  if (!searchQuery.value) return store.categories;
  return store.categories.filter(c => c.name.toLowerCase().includes(searchQuery.value.toLowerCase()));
});

const openAddModal = () => { 
  selectedCategory.value = null; 
  showModal.value = true; 
};

const openEditModal = (item: any) => { 
  selectedCategory.value = item; 
  showModal.value = true; 
};

const deleteCategory = async (id: number) => {
  const result = await Swal.fire({
    title: 'Xác nhận khóa?',
    text: "Bạn có chắc chắn muốn khóa/xóa danh mục này?",
    icon: 'warning',
    showCancelButton: true,
    confirmButtonColor: '#d33',
    cancelButtonColor: '#1e293b',
    confirmButtonText: 'Đồng ý',
    cancelButtonText: 'Hủy'
  });

  if (!result.isConfirmed) return;

  try {
    appStore.startLoading();
    const msg = await categoryService.deleteCategory(id);
    Swal.fire({ icon: 'success', title: 'Thành công', text: msg || 'Tác vụ thành công', timer: 1500, showConfirmButton: false });
    store.fetchCategories();
  } catch (error: any) {
    Swal.fire({ icon: 'error', title: 'Lỗi', text: error.response?.data?.message || 'Không thể thực thi thao tác!' });
  } finally {
    appStore.stopLoading();
  }
};
</script>

<style scoped>
.custom-scrollbar::-webkit-scrollbar { width: 6px; height: 6px; }
.custom-scrollbar::-webkit-scrollbar-track { background: #f1f5f9; }
.custom-scrollbar::-webkit-scrollbar-thumb { background: #cbd5e1; border-radius: 10px; }
.custom-scrollbar::-webkit-scrollbar-thumb:hover { background: #94a3b8; }
.fade-enter-active, .fade-leave-active { transition: opacity 0.25s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; }
.bg-slate-50 { background-color: #f8fafc !important; }
.bg-slate-200 { background-color: #e2e8f0 !important; }
.btn-dark-peel { background-color: #111111; color: white; border: 1px solid #111111; transition: all 0.2s; }
.btn-dark-peel:hover { background-color: #000000; color: white; transform: translateY(-1px); box-shadow: 0 4px 6px rgba(0,0,0,0.1); }
.transition-all { transition: all 0.2s ease-in-out; }
</style>