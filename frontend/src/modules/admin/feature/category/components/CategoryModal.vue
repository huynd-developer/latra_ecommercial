<template>
  <div class="modal-overlay d-flex align-items-center justify-content-center" @mousedown.self="emit('close')">
    <div class="bg-white rounded-4 shadow-lg w-100 overflow-hidden animate__animated animate__fadeInUp animate__faster" style="max-width: 500px;">
      <div class="p-4 border-bottom bg-slate-50 d-flex justify-content-between align-items-center">
        <h5 class="mb-0 fw-bold text-slate-800">
          {{ isEdit ? "Cập Nhật Danh Mục" : "Tạo Danh Mục Mới" }}
        </h5>
        <button @click="emit('close')" class="btn-close shadow-none"></button>
      </div>
      
      <div class="p-4">
        <form @submit.prevent="saveData">
          <div class="mb-4">
            <label class="form-label small fw-bold text-slate-700">Tên Danh Mục <span class="text-danger">*</span></label>
            <input 
              v-model="formData.name" 
              type="text" 
              class="form-control shadow-none rounded-3 fw-medium py-2" 
              required 
              placeholder="VD: Điện thoại di động, Laptop..." 
            />
          </div>

          <div class="mb-4">
            <label class="form-label small fw-bold text-slate-700">Trực thuộc (Danh mục cha)</label>
            <select v-model="formData.parentId" class="form-select shadow-none rounded-3 fw-medium py-2">
              <option :value="null">--- Trở thành Danh mục Gốc (Root) ---</option>
              <option v-for="cat in validParents" :key="cat.id" :value="cat.id">
                {{ cat.name }}
              </option>
            </select>
            <div v-if="isEdit" class="form-text small text-info mt-2 d-flex gap-1">
              <i class="bi bi-info-circle-fill"></i> 
              <span>Hệ thống đã tự động ẩn danh mục hiện tại và các danh mục con để chống lỗi vòng lặp.</span>
            </div>
          </div>

          <div class="d-flex justify-content-end gap-2 pt-3 border-top">
            <button type="button" @click="emit('close')" class="btn btn-light rounded-pill px-4 fw-semibold text-slate-600">Hủy</button>
            <button v-permission="['OWNER', 'MANAGER']" type="submit" class="btn btn-dark-peel px-4 py-2 rounded-pill fw-bold d-flex align-items-center">
              <i class="bi bi-floppy-fill me-2"></i> Lưu Danh Mục
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from "vue";
import { useAppStore } from "@/common/store/app.store";
import { categoryService } from "../services/categoryService";
import type { Category, CategoryRequestDTO } from "../types/category.type";
import Swal from "sweetalert2";

const props = defineProps<{ 
  categorySelected: Category | null;
  allCategories: Category[]; 
}>();

const emit = defineEmits<{ (e: "close"): void; (e: "refresh"): void; }>();
const appStore = useAppStore();

// TỐI ƯU: Tự động nhận diện trạng thái Edit qua Computed
const isEdit = computed(() => !!props.categorySelected);

const formData = ref<CategoryRequestDTO>({
  name: "",
  parentId: null
});

// LOGIC UI CHỐNG VÒNG LẶP (CIRCULAR REFERENCE) - GIỮ NGUYÊN VÌ ĐANG RẤT TỐT
const getDescendantIds = (parentId: number, categories: Category[]): number[] => {
  let children = categories.filter(c => c.parentId === parentId).map(c => c.id);
  let descendants = [...children];
  children.forEach(childId => {
    descendants = descendants.concat(getDescendantIds(childId, categories));
  });
  return descendants;
};

// Lọc Dropdown danh mục cha an toàn
const validParents = computed(() => {
  if (!isEdit.value || !props.categorySelected) return props.allCategories;
  
  const editingId = props.categorySelected.id;
  const descendantIds = getDescendantIds(editingId, props.allCategories);
  
  return props.allCategories.filter(c => 
    c.id !== editingId && !descendantIds.includes(c.id)
  );
});

onMounted(() => {
  if (props.categorySelected) {
    formData.value = { 
      name: props.categorySelected.name,
      parentId: props.categorySelected.parentId
    };
  }
});

const saveData = async () => {
  if (!formData.value.name.trim()) {
    return Swal.fire({ icon: 'warning', title: 'Cảnh báo', text: 'Tên danh mục không được để trống!' });
  }

  try {
    appStore.startLoading();
    if (isEdit.value && props.categorySelected?.id) {
      const msg = await categoryService.updateCategory(props.categorySelected.id, formData.value);
      Swal.fire({ icon: 'success', title: 'Thành công', text: msg || 'Cập nhật thành công', timer: 1500, showConfirmButton: false });
    } else {
      const msg = await categoryService.createCategory(formData.value);
      Swal.fire({ icon: 'success', title: 'Thành công', text: msg || 'Tạo mới thành công', timer: 1500, showConfirmButton: false });
    }
    emit("refresh");
    emit("close");
  } catch (error: any) {
    Swal.fire({ icon: 'error', title: 'Lỗi', text: error.response?.data?.message || "Có lỗi xảy ra khi lưu danh mục!" });
  } finally {
    appStore.stopLoading();
  }
};
</script>

<style scoped>
.modal-overlay { position: fixed; top: 0; left: 0; width: 100vw; height: 100vh; background-color: rgba(15, 23, 42, 0.5); z-index: 1050; backdrop-filter: blur(4px); }
.btn-dark-peel { background-color: #111111; color: white; border: 1px solid #111111; transition: all 0.2s; }
.btn-dark-peel:hover { background-color: #000000; color: white; transform: translateY(-1px); box-shadow: 0 4px 6px rgba(0,0,0,0.1); }
.bg-slate-50 { background-color: #f8fafc !important; }
.text-slate-700 { color: #334155 !important; }
.text-slate-800 { color: #1e293b !important; }
.form-control:focus, .form-select:focus { border-color: #94a3b8; box-shadow: 0 0 0 0.2rem rgba(148, 163, 184, 0.25); }
.animate__faster { animation-duration: 0.3s; }
</style>