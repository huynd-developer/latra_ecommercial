<template>
  <div>
    <div class="offcanvas-backdrop fade show" @click="closeModal"></div>

    <div
      class="offcanvas offcanvas-end show shadow-lg border-0"
      tabindex="-1"
      style="visibility: visible"
    >
      <div class="offcanvas-header border-bottom px-4 py-3">
        <h5 class="offcanvas-title fw-semibold">
          {{ isEdit ? "Cập nhật thương hiệu" : "Thêm thương hiệu" }}
        </h5>
        <button
          type="button"
          class="btn-close"
          @click="closeModal"
          aria-label="Close"
        ></button>
      </div>

      <div class="offcanvas-body px-4">
        <form
          @submit.prevent="handleSubmit"
          novalidate
          class="d-flex flex-column h-100"
        >
          <div class="flex-grow-1">
            <div class="mb-4">
              <div class="d-flex justify-content-between mb-1">
                <label class="form-label fw-medium mb-0">Tên thương hiệu</label>
                <small class="text-muted opacity-75">Bắt buộc</small>
              </div>
              <input
                v-model="formData.name"
                @blur="validateField('name')"
                @input="validateField('name')"
                type="text"
                class="form-control bg-light border-0 py-2"
                :class="{ 'is-invalid': errors.name }"
                placeholder="Ví dụ: Apple, Nike..."
              />
              <div class="invalid-feedback">{{ errors.name }}</div>
            </div>

            <div class="mb-4">
              <div class="d-flex justify-content-between mb-1">
                <label class="form-label fw-medium mb-0">Quốc gia</label>
                <small class="text-muted opacity-75">Bắt buộc</small>
              </div>

              <input
                v-model="formData.country"
                list="country-list"
                @blur="validateField('country')"
                @input="validateField('country')"
                type="text"
                class="form-control bg-light border-0 py-2"
                :class="{ 'is-invalid': errors.country }"
                placeholder="Gõ hoặc chọn quốc gia..."
              />

              <datalist id="country-list">
                <option v-for="(country, index) in countryList" :key="index" :value="country"></option>
              </datalist>

              <div class="invalid-feedback">{{ errors.country }}</div>
            </div>
            
            <div class="mb-4">
              <label class="form-label fw-medium mb-1">Trạng thái</label>
              <select
                v-model="formData.status"
                class="form-select bg-light border-0 py-2"
              >
                <option :value="1">Đang hoạt động</option>
                <option :value="0">Ngừng hoạt động</option>
              </select>
            </div>

            <div class="mb-4">
              <label class="form-label fw-medium mb-2">Logo thương hiệu</label>
              <div style="width: 120px">
                <ImageUploader
                  :initial-url="previewUrl"
                  ratio="1/1"
                  @update:file="(file) => (selectedFile = file)"
                />
              </div>
            </div>
          </div>

          <div class="mt-4 pt-3 border-top d-flex gap-2">
            <button
              type="button"
              @click="closeModal"
              class="btn btn-light w-50 py-2 fw-medium"
            >
              Hủy
            </button>
            <button
              type="submit"
              :disabled="isSubmitting || hasErrors"
              class="btn btn-dark w-50 py-2 fw-medium d-flex justify-content-center align-items-center"
            >
              <span
                v-if="isSubmitting"
                class="spinner-border spinner-border-sm me-2"
                role="status"
              ></span>
              {{ isSubmitting ? "Đang lưu..." : "Lưu thông tin" }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from "vue";
import type { Brand, BrandRequestDTO } from "../types/brand.type";
import { brandService } from "../services/brandService";
import { useAppStore } from "@/common/store/app.store";
import ImageUploader from "@/common/components/ImageUploader.vue"; 

const appStore = useAppStore();
const props = defineProps<{ brandSelected: Brand | null }>();
const emit = defineEmits<{ (e: "close"): void; (e: "refresh"): void }>();

const isEdit = computed(() => !!props.brandSelected);
const isSubmitting = ref(false);
const selectedFile = ref<File | null>(null);
const previewUrl = ref<string | null>(null);

const formData = ref<BrandRequestDTO>({ name: "", country: "", status: 1 });
const errors = reactive({ name: "", country: "" });

// Đã chuyển danh sách quốc gia xuống đây
const countryList = [
  "Việt Nam",
  "Hoa Kỳ (Mỹ)",
  "Nhật Bản",
  "Hàn Quốc",
  "Trung Quốc",
  "Đài Loan",
  "Đức",
  "Pháp",
  "Ý",
  "Anh",
  "Thụy Sĩ",
  "Thái Lan",
  "Singapore"
];

onMounted(() => {
  if (props.brandSelected) {
    formData.value = {
      name: props.brandSelected.name,
      country: props.brandSelected.country,
      status: props.brandSelected.status,
    };
    
    // Đã cập nhật: Xử lý Cloudinary URL cho ảnh preview
    if (props.brandSelected.logo) {
      const logoUrl = props.brandSelected.logo;
      previewUrl.value = logoUrl.startsWith('http') ? logoUrl : `http://localhost:8080/uploads/brands/${logoUrl}`;
    }
  }
});

const closeModal = () => emit("close");

const validateField = (field: "name" | "country") => {
  if (field === "name") {
    const val = formData.value.name.trim();
    if (!val) errors.name = "Vui lòng nhập tên thương hiệu.";
    else errors.name = "";
  }
  if (field === "country") {
    const val = formData.value.country.trim();
    if (!val) errors.country = "Vui lòng nhập quốc gia.";
    else errors.country = "";
  }
};

const validateAll = () => {
  validateField("name");
  validateField("country");
};
const hasErrors = computed(() => !!errors.name || !!errors.country);

const handleSubmit = async () => {
  validateAll();
  if (hasErrors.value) return;

  formData.value.name = formData.value.name.trim();
  formData.value.country = formData.value.country.trim();

  try {
    isSubmitting.value = true;
    let targetBrandId: number;

    if (isEdit.value && props.brandSelected) {
      targetBrandId = props.brandSelected.id;
      await brandService.updateBrand(targetBrandId, formData.value);
    } else {
      const newBrand = await brandService.createBrand(formData.value);
      targetBrandId = newBrand.id;
    }

    if (selectedFile.value && targetBrandId) {
      await brandService.uploadLogo(targetBrandId, selectedFile.value);
    }

    appStore.notifySuccess(isEdit.value ? "Đã cập nhật" : "Đã thêm mới");
    emit("refresh");
    closeModal();
  } catch (error: any) {
    appStore.notifyError(error.response?.data?.message || "Có lỗi xảy ra!");
  } finally {
    isSubmitting.value = false;
  }
};
</script>

<style scoped>
.form-control:focus,
.form-select:focus {
  box-shadow: 0 0 0 0.25rem rgba(0, 0, 0, 0.05);
}
</style>