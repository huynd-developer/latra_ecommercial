<!-- frontend/src/modules/shop/feature/catalog/components/QuickViewModal.vue -->
<template>
  <div class="fixed inset-0 z-[1060] flex items-center justify-center bg-black bg-opacity-50 backdrop-blur-sm" @mousedown.self="emit('close')">
    <div class="bg-white rounded-4 shadow-lg w-full max-w-3xl overflow-hidden d-flex flex-column flex-md-row position-relative animation-zoom-in">
      
      <!-- Nút Đóng -->
      <button @click="emit('close')" class="btn-close position-absolute top-0 end-0 m-3 z-3 shadow-none"></button>

      <!-- Cột Trái -->
      <div class="col-md-5 bg-light d-flex align-items-center justify-content-center p-4">
        <img :src="getImageUrl(product?.mainImageUrl)" class="img-fluid mix-blend-multiply" style="max-height: 300px; object-fit: contain;" alt="Product">
      </div>

      <!-- Cột Phải -->
      <div class="col-md-7 p-4 p-lg-5 d-flex flex-column">
        <div class="mb-1 text-primary small fw-bold text-uppercase tracking-wider">{{ getBrandName(product?.brandId) }}</div>
        <h4 class="fw-bold text-dark mb-2">{{ product?.name || 'Đang cập nhật...' }}</h4>
        
        <div class="d-flex align-items-center gap-3 mb-4">
          <span class="fw-black fs-3 text-danger">{{ formatMoney(activePrice) }}</span>
          <span v-if="isFlashSale" class="badge bg-danger rounded-pill px-3 py-2 animate-pulse"><i class="bi bi-lightning-charge-fill"></i> Flash Sale</span>
        </div>

        <div class="flex-grow-1 overflow-auto custom-scrollbar pe-2">
          <!-- Chọn Dung Lượng -->
          <div class="mb-3" v-if="availableStorages.length > 0">
            <div class="fw-bold text-slate-700 mb-2 small">Dung lượng: <span class="text-dark">{{ selectedStorage }}</span></div>
            <div class="d-flex flex-wrap gap-2">
              <button 
                v-for="st in availableStorages" :key="st"
                @click="selectedStorage = st"
                class="btn btn-sm btn-outline-dark fw-medium rounded-3"
                :class="selectedStorage === st ? 'active bg-dark text-white border-dark' : ''"
              >
                {{ st }}
              </button>
            </div>
          </div>

          <!-- Chọn Màu Sắc -->
          <div class="mb-3" v-if="availableColors.length > 0">
            <div class="fw-bold text-slate-700 mb-2 small">Màu sắc: <span class="text-dark">{{ selectedColor }}</span></div>
            <div class="d-flex flex-wrap gap-2">
              <button 
                v-for="col in availableColors" :key="col"
                @click="selectedColor = col"
                class="btn btn-sm btn-outline-dark fw-medium rounded-pill px-3"
                :class="selectedColor === col ? 'active bg-dark text-white border-dark' : ''"
              >
                {{ col }}
              </button>
            </div>
          </div>

          <!-- Tình trạng & Tồn kho -->
          <div class="mb-3 p-3 bg-slate-50 rounded-3 border">
            <div class="d-flex justify-content-between align-items-center mb-2">
              <span class="small fw-medium text-muted">Tình trạng máy:</span>
              <span class="fw-bold text-dark">{{ activeVariant?.condition || 'Mới nguyên seal' }}</span>
            </div>
            <div class="d-flex justify-content-between align-items-center">
              <span class="small fw-medium text-muted">Kho hàng:</span>
              <span v-if="currentStock > 0" class="fw-bold text-success">{{ currentStock }} sản phẩm sẵn có</span>
              <span v-else class="fw-bold text-warning">Đặt trước (Pre-order)</span>
            </div>
          </div>
        </div>

        <!-- Hành Động -->
        <div class="mt-4 pt-3 border-top">
          <div class="d-flex gap-2 mb-3">
            <div class="d-flex align-items-center justify-content-between border rounded-pill px-3 py-2 bg-white" style="width: 120px;">
              <i class="bi bi-dash fs-5 cursor-pointer text-muted" @click="quantity > 1 ? quantity-- : null"></i>
              <span class="fw-bold">{{ quantity }}</span>
              <i class="bi bi-plus fs-5 cursor-pointer text-dark" @click="quantity < 5 ? quantity++ : null"></i>
            </div>
            <button 
              class="btn btn-outline-dark flex-grow-1 rounded-pill fw-bold"
              :disabled="!isValidConfig"
              @click="handleAddToCart"
            >
              <i class="bi bi-cart-plus me-1"></i> Thêm Giỏ
            </button>
          </div>
          
          <button 
            class="btn w-100 py-3 rounded-pill fw-bold fs-6 shadow-sm transition-all"
            :class="!isValidConfig ? 'btn-secondary disabled' : (currentStock <= 0 ? 'btn-warning text-dark' : 'btn-danger text-white')"
            @click="handleBuyNow"
          >
            {{ !isValidConfig ? 'CHƯA CHỌN CẤU HÌNH' : (currentStock <= 0 ? 'MUA TRƯỚC (PRE-ORDER)' : 'MUA NGAY - THANH TOÁN') }}
          </button>
        </div>

      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useCartStore } from '../../cart/stores/cartStore';
import { useAppStore } from '@/common/store/app.store';

const props = defineProps<{ product: any; brandList?: any[] }>();
const emit = defineEmits<{ (e: 'close'): void }>();

const cartStore = useCartStore();
const appStore = useAppStore();
const router = useRouter();

const quantity = ref(1);
const selectedStorage = ref<string>('');
const selectedColor = ref<string>('');
const isFlashSale = ref(false);

const safeVariants = computed<any[]>(() => {
  return Array.isArray(props.product?.variants) ? props.product.variants : [];
});
const hasVariants = computed(() => safeVariants.value.length > 0);

const availableStorages = computed<string[]>(() => {
  const storages = safeVariants.value.map(v => v?.storage || '').filter(Boolean);
  return Array.from(new Set(storages));
});

const availableColors = computed<string[]>(() => {
  const colors = safeVariants.value
    .filter(v => !selectedStorage.value || (v?.storage || '') === selectedStorage.value)
    .map(v => v?.color || '')
    .filter(Boolean);
  return Array.from(new Set(colors));
});

onMounted(() => {
  if (availableStorages.value.length > 0) selectedStorage.value = availableStorages.value[0] ?? '';
  if (availableColors.value.length > 0 && !selectedColor.value) selectedColor.value = availableColors.value[0] ?? '';
});

watch(selectedStorage, () => {
  if (availableColors.value.length > 0 && !availableColors.value.includes(selectedColor.value)) {
    selectedColor.value = availableColors.value[0] ?? '';
  }
});

const activeVariant = computed<any>(() => {
  if (!hasVariants.value) return null;
  if (safeVariants.value.length === 1 && availableStorages.value.length === 0 && availableColors.value.length === 0) {
    return safeVariants.value[0];
  }
  return safeVariants.value.find(v => 
    (v?.storage || '') === selectedStorage.value && 
    (v?.color || '') === selectedColor.value
  );
});

const activePrice = computed(() => activeVariant.value?.price || safeVariants.value[0]?.price || 0);
const currentStock = computed(() => activeVariant.value?.stock || 0);

const activeVariantId = computed<number | null>(() => {
  if (!activeVariant.value) return null;
  return activeVariant.value.variantId || activeVariant.value.id || null;
});

const isValidConfig = computed(() => activeVariantId.value !== null);

const getImageUrl = (url?: string) => {
  if (!url) return 'https://via.placeholder.com/300?text=No+Image';
  return url.startsWith('http') ? url : `http://localhost:8080/uploads/${url}`;
};

const getBrandName = (id?: number) => {
  if (!id || !props.brandList) return '';
  const b = props.brandList.find(x => x.id === id);
  return b ? b.name : '';
};

const formatMoney = (val: number) => new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(val);

const handleAddToCart = async () => {
  if (!isValidConfig.value) {
    appStore.notifyError("Vui lòng chọn cấu hình sản phẩm!");
    return;
  }
  await cartStore.addItem(activeVariantId.value!, quantity.value);
  appStore.notifySuccess("Đã thêm vào giỏ hàng!");
  emit('close'); 
};

const handleBuyNow = async () => {
  if (!isValidConfig.value) {
    appStore.notifyError("Vui lòng chọn cấu hình sản phẩm!");
    return;
  }
  await cartStore.addItem(activeVariantId.value!, quantity.value);
  emit('close');
  router.push('/checkout'); 
};
</script>

<style scoped>
.animation-zoom-in { animation: zoomIn 0.2s cubic-bezier(0.175, 0.885, 0.32, 1.275); }
@keyframes zoomIn { from { transform: scale(0.9); opacity: 0; } to { transform: scale(1); opacity: 1; } }
.mix-blend-multiply { mix-blend-mode: multiply; }
.cursor-pointer { cursor: pointer; }
.custom-scrollbar::-webkit-scrollbar { width: 4px; }
.custom-scrollbar::-webkit-scrollbar-thumb { background: #cbd5e1; border-radius: 10px; }
.transition-all { transition: all 0.2s ease; }
</style>