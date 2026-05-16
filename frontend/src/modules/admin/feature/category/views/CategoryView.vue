<!-- frontend/src/modules/admin/feature/category/views/CategoryView.vue -->
<script setup lang="ts">
import { ref, onMounted, computed } from "vue";
import { useCategoryStore } from "../stores/categoryStore";
import { categoryService } from "../services/categoryService";
import { useAppStore } from "@/common/store/app.store";
import CategoryModal from "../components/CategoryModal.vue";

const store = useCategoryStore();
const appStore = useAppStore();

const searchQuery = ref("");
const showModal = ref(false);
const selectedCategory = ref<any>(null);

onMounted(() => {
  store.fetchCategories();
});

// Lọc tìm kiếm
const filteredCategories = computed(() => {
  if (!searchQuery.value) return store.categories;
  return store.categories.filter(c => c.name.toLowerCase().includes(searchQuery.value.toLowerCase()));
});

const openAddModal = () => { selectedCategory.value = null; showModal.value = true; };
const openEditModal = (item: any) => { selectedCategory.value = item; showModal.value = true; };

const deleteCategory = async (id: number) => {
  if (!confirm('Bạn có chắc chắn muốn xóa danh mục này?')) return;
  try {
    appStore.startLoading();
    const msg = await categoryService.deleteCategory(id);
    appStore.notifySuccess(msg);
    store.fetchCategories();
  } catch (error: any) {
    // Hiển thị lỗi từ backend (VD: Lỗi vẫn còn danh mục con, vẫn còn sản phẩm)
    appStore.notifyError(error.response?.data?.message || 'Không thể xóa danh mục!');
  } finally {
    appStore.stopLoading();
  }
};
</script>

<template>
  <div class="bg-white h-100 d-flex flex-column overflow-hidden rounded-4 shadow-sm border border-slate-100 p-4">
    <!-- Header -->
    <div class="d-flex justify-content-between align-items-center mb-4 border-bottom pb-3">
      <div>
        <h5 class="fw-bolder text-slate-800 mb-1"><i class="bi bi-folder-fill text-primary me-2"></i>Quản Lý Danh Mục</h5>
        <span class="small text-muted">Tổ chức cây thư mục cho Sản phẩm</span>
      </div>
      <div class="d-flex gap-3">
        <div class="position-relative">
          <i class="bi bi-search position-absolute top-50 start-0 translate-middle-y ms-3 text-muted"></i>
          <input v-model="searchQuery" type="text" class="form-control rounded-pill ps-5 shadow-none border-slate-200" placeholder="Tìm tên danh mục..." />
        </div>
        <button @click="openAddModal" class="btn btn-dark-peel rounded-pill px-4 fw-bold">
          + Thêm Danh Mục
        </button>
      </div>
    </div>

    <!-- Table -->
    <div class="flex-grow-1 overflow-auto custom-scrollbar border rounded-3">
      <table class="table align-middle mb-0">
        <thead class="bg-light text-muted small text-uppercase sticky-top">
          <tr>
            <th class="ps-4 py-3">ID</th>
            <th>Tên Danh Mục</th>
            <th>Thuộc Cấp (Danh mục cha)</th>
            <th class="text-center">Trạng Thái</th>
            <th class="text-end pe-4">Thao tác</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="filteredCategories.length === 0">
            <td colspan="5" class="text-center py-5 text-muted">Không tìm thấy danh mục nào.</td>
          </tr>
          <tr v-for="cat in filteredCategories" :key="cat.id" class="hover-bg-slate">
            <td class="ps-4 text-muted fw-bold font-monospace small">#{{ cat.id }}</td>
            <td class="fw-bold text-slate-800 fs-6">{{ cat.name }}</td>
            <td class="text-slate-600 fw-medium">
              <span v-if="cat.parentId === null" class="badge bg-dark bg-opacity-10 text-dark">Danh Mục Gốc</span>
              <span v-else class="text-primary"><i class="bi bi-arrow-return-right me-1"></i> {{ store.getParentName(cat.parentId) }}</span>
            </td>
            <td class="text-center">
              <span v-if="cat.status === 1" class="badge bg-success bg-opacity-10 text-success">Hoạt động</span>
              <span v-else class="badge bg-danger bg-opacity-10 text-danger">Đã khóa</span>
            </td>
            <td class="text-end pe-4">
              <button @click="openEditModal(cat)" class="btn btn-sm btn-light text-primary rounded-circle me-2"><i class="bi bi-pencil-fill"></i></button>
              <button 
                @click="deleteCategory(cat.id)" 
                class="btn btn-sm btn-light rounded-circle"
                :class="cat.status === 1 ? 'text-danger' : 'text-muted opacity-50'"
                :disabled="cat.status !== 1"
              ><i class="bi bi-trash3-fill"></i></button>
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

<style scoped>
.custom-scrollbar::-webkit-scrollbar { width: 6px; }
.custom-scrollbar::-webkit-scrollbar-thumb { background: #cbd5e1; border-radius: 10px; }
.fade-enter-active, .fade-leave-active { transition: opacity 0.2s; }
.fade-enter-from, .fade-leave-to { opacity: 0; }
.hover-bg-slate:hover { background-color: #f8fafc; }
.btn-dark-peel { background-color: #111111; color: white; border: 1px solid #111111; }
.btn-dark-peel:hover { background-color: #000000; color: white; }
</style>