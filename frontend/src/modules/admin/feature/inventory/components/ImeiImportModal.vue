<!-- frontend/src/modules/admin/feature/inventory/components/ImeiImport.vue -->
<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useAppStore } from '@/common/store/app.store';
import { imeiService } from '../services/imeiService';
import { useProductStore } from '../../product/stores/productStore'; // Tái sử dụng Product Store để lấy Variant

const emit = defineEmits<{ (e: 'imported'): void }>();
const appStore = useAppStore();
const productStore = useProductStore();

const selectedProductId = ref('');
const selectedVariantId = ref('');
const rawImeiInput = ref('');

// Lấy danh sách variants phẳng (flat) từ products
const flatVariants = computed(() => {
  const result: any[] = [];
  productStore.products.forEach(p => {
    p.variants.forEach(v => {
      result.push({ ...v, productId: p.id, productName: p.name });
    });
  });
  return result;
});

// Lọc Variant theo Product được chọn
const availableVariants = computed(() => {
  if (!selectedProductId.value) return [];
  return flatVariants.value.filter(v => v.productId === Number(selectedProductId.value));
});

// LỌC RÁC: Loại bỏ dòng trống, khoảng trắng thừa và mã trùng lặp ngay khi đang gõ
const parsedImeis = computed(() => {
  if (!rawImeiInput.value) return [];
  const lines = rawImeiInput.value.split('\n').map(l => l.trim().toUpperCase()).filter(l => l !== '');
  return [...new Set(lines)]; // Khử trùng
});
const duplicateCount = computed(() => {
  const rawCount = rawImeiInput.value.split('\n').filter(l => l.trim() !== '').length;
  return rawCount - parsedImeis.value.length;
});

const submitImport = async () => {
  if (!selectedVariantId.value || parsedImeis.value.length === 0) return;
  try {
    appStore.startLoading();
    await imeiService.importImeis({
      variantId: Number(selectedVariantId.value),
      imeis: parsedImeis.value
    });
    appStore.notifySuccess(`Nhập kho thành công ${parsedImeis.value.length} thiết bị!`);
    
    // Reset form
    rawImeiInput.value = '';
    selectedVariantId.value = '';
    emit('imported'); // Báo cho View biết để load lại Data
  } catch (error: any) {
    appStore.notifyError(error.response?.data?.message || 'Có lỗi xảy ra khi nhập kho!');
  } finally {
    appStore.stopLoading();
  }
};

onMounted(() => {
  if (productStore.products.length === 0) productStore.fetchProducts();
});
</script>

<template>
  <div class="row h-100">
    <!-- Cột trái: Form chọn thông tin -->
    <div class="col-md-5 border-end pe-4">
      <h6 class="fw-bold mb-4">1. Thông tin sản phẩm</h6>
      
      <div class="mb-3">
        <label class="form-label small fw-bold">Chọn Sản Phẩm</label>
        <select v-model="selectedProductId" class="form-select shadow-none" @change="selectedVariantId = ''">
          <option value="" disabled>-- Chọn dòng máy --</option>
          <option v-for="p in productStore.products" :key="p.id" :value="p.id">{{ p.name }}</option>
        </select>
      </div>

      <div class="mb-3">
        <label class="form-label small fw-bold">Chọn Phiên Bản (Màu - DL - Tình trạng)</label>
        <select v-model="selectedVariantId" class="form-select shadow-none" :disabled="!selectedProductId">
          <option value="" disabled>-- Chọn phiên bản --</option>
          <option v-for="v in availableVariants" :key="v.variantId || v.id" :value="v.variantId || v.id">
            {{ v.color }} - {{ v.storage }} ({{ v.condition }})
          </option>
        </select>
      </div>

      <div v-if="selectedVariantId" class="alert alert-success mt-4 border-0 bg-success bg-opacity-10">
        <i class="bi bi-check-circle-fill me-2"></i> Đã chọn phiên bản hợp lệ. Vui lòng quét mã vạch ở cột bên cạnh.
      </div>
    </div>

    <!-- Cột phải: Textarea quét súng -->
    <div class="col-md-7 ps-4 d-flex flex-column">
      <div class="d-flex justify-content-between align-items-center mb-3">
        <h6 class="fw-bold mb-0">2. Quét mã IMEI (Bắn Barcode)</h6>
        <span class="badge bg-primary rounded-pill px-3 py-2 fs-6 shadow-sm">{{ parsedImeis.length }} mã hợp lệ</span>
      </div>

      <div class="alert alert-info py-2 small border-0 bg-info bg-opacity-10">
        <i class="bi bi-info-circle-fill me-2"></i> Click chuột vào ô dưới và dùng súng bắn mã vạch. Mỗi lần tít súng hệ thống tự xuống dòng.
      </div>

      <textarea 
        v-model="rawImeiInput" 
        :disabled="!selectedVariantId"
        class="form-control flex-grow-1 shadow-none font-monospace fw-bold fs-5 bg-light" 
        placeholder="Chỉ có thể quét khi đã chọn phiên bản..."
        style="resize: none;"
      ></textarea>

      <div v-if="duplicateCount > 0" class="text-danger small mt-2 fw-bold">
        <i class="bi bi-exclamation-triangle-fill"></i> Đã tự động loại bỏ {{ duplicateCount }} mã bị quét đúp/trùng lặp.
      </div>

      <div class="text-end mt-4">
        <button 
          @click="submitImport" 
          class="btn btn-dark px-5 py-2 fw-bold rounded-pill shadow-sm"
          :disabled="parsedImeis.length === 0 || !selectedVariantId"
          style="background-color: #111;"
        >
          Xác nhận Nhập {{ parsedImeis.length }} Máy
        </button>
      </div>
    </div>
  </div>
</template>