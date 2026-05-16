<script setup lang="ts">
import { ref } from 'vue';
import { useAppStore } from '@/common/store/app.store';
import { productService } from '../services/productService';
import type { Product } from '../types/product.type';
import ImageUploader from '@/common/components/ImageUploader.vue'; // TÁI SỬ DỤNG

const props = defineProps<{ productSelected: Product | null; }>();
const emit = defineEmits<{ (e: 'close'): void; (e: 'refresh'): void; }>();

const appStore = useAppStore();
const selectedFile = ref<File | null>(null);
const isUploading = ref(false);

const closeImageModal = () => {
  selectedFile.value = null;
  emit('close');
};

const uploadImage = async () => {
  if (!selectedFile.value || !props.productSelected) return;
  try {
    isUploading.value = true;
    appStore.startLoading();
    await productService.uploadImage(props.productSelected.id, selectedFile.value);
    appStore.notifySuccess('Đã tải ảnh lên thành công!');
    emit('refresh');
    closeImageModal();
  } catch (error: any) {
    appStore.notifyError(error.response?.data?.message || 'Lỗi tải ảnh!');
  } finally {
    isUploading.value = false;
    appStore.stopLoading();
  }
};
</script>

<template>
  <div>
    <div class="offcanvas-backdrop fade show" @click="closeImageModal"></div>
    <div class="offcanvas offcanvas-end show shadow-lg border-0" tabindex="-1" style="visibility: visible; width: 400px;">
      <div class="offcanvas-header border-bottom px-4 py-3 bg-light bg-opacity-50">
        <h6 class="offcanvas-title fw-semibold">Cập nhật ảnh đại diện</h6>
        <button type="button" class="btn-close" @click="closeImageModal"></button>
      </div>
      
      <div class="offcanvas-body px-4 d-flex flex-column">
        <div class="flex-grow-1">
          <p class="text-muted small mb-4">
            Đang tải ảnh cho sản phẩm:<br/>
            <span class="fw-bold text-dark fs-6">{{ productSelected?.name }}</span>
          </p>

          <label class="form-label fw-medium mb-2">Chọn hình ảnh</label>
          <ImageUploader ratio="1/1" @update:file="(file) => selectedFile = file" />
        </div>

        <div class="mt-4 pt-3 border-top d-flex gap-2">
          <button type="button" @click="closeImageModal" class="btn btn-light w-50 py-2 fw-medium">Hủy</button>
          <button type="button" @click="uploadImage" :disabled="!selectedFile || isUploading" class="btn btn-dark w-50 py-2 fw-medium d-flex justify-content-center align-items-center">
            <span v-if="isUploading" class="spinner-border spinner-border-sm me-2"></span>
            Lưu hình ảnh
          </button>
        </div>
      </div>
    </div>
  </div>
</template>