<template>
  <div>
    <div
      class="offcanvas-backdrop fade show"
      @click="closeModal"
      style="z-index: 1040"
    ></div>

    <div
      class="offcanvas offcanvas-end show shadow-lg border-0"
      tabindex="-1"
      style="visibility: visible; z-index: 1045"
    >
      <div class="offcanvas-header border-bottom px-4 py-3 bg-light">
        <h5 class="offcanvas-title fw-bold">
          {{ isEdit ? "Cập nhật thương hiệu" : "Thêm thương hiệu" }}
        </h5>
        <button
          type="button"
          class="btn-close shadow-none"
          @click="closeModal"
          aria-label="Close"
        ></button>
      </div>

      <div class="offcanvas-body px-4 py-4">
        <form
          @submit.prevent="handleSubmit"
          novalidate
          class="d-flex flex-column h-100"
        >
          <div class="flex-grow-1">
            <div class="mb-4">
              <label class="form-label fw-medium"
                >Tên thương hiệu <span class="text-danger">*</span></label
              >
              <input
                v-model="formData.name"
                @blur="validateField('name')"
                @input="validateField('name')"
                type="text"
                class="form-control shadow-none py-2"
                :class="{ 'is-invalid': errors.name }"
                placeholder="Ví dụ: Apple, Nike..."
              />
              <div class="invalid-feedback">{{ errors.name }}</div>
            </div>

            <div class="mb-4">
              <label class="form-label fw-medium"
                >Quốc gia <span class="text-danger">*</span></label
              >
              <input
                v-model="formData.country"
                list="country-list"
                @blur="validateField('country')"
                @input="validateField('country')"
                type="text"
                class="form-control shadow-none py-2"
                :class="{ 'is-invalid': errors.country }"
                placeholder="Gõ hoặc chọn quốc gia..."
              />
              <datalist id="country-list">
                <option
                  v-for="(country, index) in countryList"
                  :key="index"
                  :value="country"
                ></option>
              </datalist>
              <div class="invalid-feedback">{{ errors.country }}</div>
            </div>

            <div class="mb-4">
              <label class="form-label fw-medium">Trạng thái</label>
              <select
                v-model="formData.status"
                :disabled="!isOwner"
                class="form-select shadow-none py-2"
              >
                <option :value="1">Đang hoạt động</option>
                <option :value="0">Ngừng hoạt động (Khóa)</option>
              </select>
              <div v-if="!isOwner" class="form-text text-muted small mt-1">
                * Chỉ Chủ hệ thống (Owner) mới được phép thay đổi trạng thái
                hoạt động.
              </div>
            </div>

            <div class="mb-4">
              <label class="form-label fw-medium mb-2">Logo thương hiệu</label>
              <div style="width: 130px">
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
              Hủy bỏ
            </button>
            <button
              v-permission="['OWNER', 'MANAGER']"
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
import ImageUploader from "@/common/components/ImageUploader.vue";
import Swal from "sweetalert2";

const props = defineProps<{ brandSelected: Brand | null }>();
const emit = defineEmits<{ (e: "close"): void; (e: "refresh"): void }>();

const isEdit = computed(() => !!props.brandSelected);
const isSubmitting = ref(false);
const selectedFile = ref<File | null>(null);
const previewUrl = ref<string | null>(null);

const formData = ref<BrandRequestDTO>({ name: "", country: "", status: 1 });
const errors = reactive({ name: "", country: "" });

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
];

// 🛠️ ĐÃ FIX: Logic lấy role để kiểm tra quyền khóa status (Chỉ OWNER mới được sửa)
const currentUser = JSON.parse(localStorage.getItem("currentUser") || "{}");
const isOwner = computed(() => {
  const role = currentUser.role || currentUser.roles?.[0] || "";
  return role.toUpperCase().replace("ROLE_", "") === "OWNER";
});

// Hàm hỗ trợ lấy base url để render ảnh
const getBaseUrl = () =>
  import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

onMounted(() => {
  if (props.brandSelected) {
    formData.value = {
      name: props.brandSelected.name,
      country: props.brandSelected.country,
      status: props.brandSelected.status,
    };

    if (props.brandSelected.logo) {
      const logoUrl = props.brandSelected.logo;
      previewUrl.value = logoUrl.startsWith("http")
        ? logoUrl
        : `${getBaseUrl()}/uploads/brands/${logoUrl}`;
    }
  }
});

const closeModal = () => emit("close");

const validateField = (field: "name" | "country") => {
  if (field === "name") {
    errors.name = !formData.value.name.trim()
      ? "Vui lòng nhập tên thương hiệu."
      : "";
  }
  if (field === "country") {
    errors.country = !formData.value.country.trim()
      ? "Vui lòng nhập quốc gia."
      : "";
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
      const response = await brandService.createBrand(formData.value);
      targetBrandId = response.id; // Chỉ cần thế này là xong!
    }

    // Nếu người dùng có chọn upload file logo mới
    if (selectedFile.value && targetBrandId) {
      await brandService.uploadLogo(targetBrandId, selectedFile.value);
    }

    Swal.fire({
      icon: "success",
      title: "Thành công",
      text: isEdit.value
        ? "Cập nhật thông tin thành công!"
        : "Thêm mới thương hiệu thành công!",
      timer: 1500,
      showConfirmButton: false,
    });

    emit("refresh");
    closeModal();
  } catch (error: any) {
    Swal.fire({
      icon: "error",
      title: "Thất bại",
      text: error.response?.data?.message || "Có lỗi xảy ra khi lưu dữ liệu!",
    });
  } finally {
    isSubmitting.value = false;
  }
};
</script>
