<!-- frontend/src/modules/shop/feature/catalog/components/ProductInfo.vue -->
<template>
  <div class="product-info py-lg-3">
    <!-- Tên & Thương hiệu -->
    <div class="text-primary small mb-2 text-uppercase fw-bold tracking-wider">{{ getBrandName(product?.brandId) }}</div>
    <h1 class="fw-bold text-slate-800 display-6 mb-3" style="letter-spacing: -1px;">{{ product?.name || 'Đang cập nhật...' }}</h1>
    
    <!-- Giá hiển thị động theo cấu hình -->
    <div class="price-wrap d-flex align-items-center gap-3 mb-4 bg-light bg-opacity-50 p-4 rounded-4 border">
      <span class="fw-black display-5 text-danger">{{ formatMoney(activePrice) }}</span>
      <span v-if="isFlashSale" class="badge bg-danger rounded-pill px-3 py-2 animate-pulse">
        <i class="bi bi-lightning-charge-fill"></i> Flash Sale
      </span>
    </div>

    <!-- Tùy chọn Cấu hình (Variants) -->
    <div class="mb-4" v-if="hasVariants">
      
      <!-- Chọn Dung lượng -->
      <div class="mb-4" v-if="availableStorages.length > 0">
        <div class="fw-bold text-slate-700 mb-2">Dung lượng: <span class="text-dark fw-black">{{ selectedStorage }}</span></div>
        <div class="d-flex flex-wrap gap-2">
          <button 
            v-for="st in availableStorages" :key="st"
            @click="selectedStorage = st"
            class="btn btn-outline-dark fw-bold rounded-3 px-4 py-2"
            :class="selectedStorage === st ? 'active bg-dark text-white border-dark shadow-sm' : 'border-slate-200'"
          >
            {{ st }}
          </button>
        </div>
      </div>

      <!-- Chọn Màu sắc -->
      <div class="mb-4" v-if="availableColors.length > 0">
        <div class="fw-bold text-slate-700 mb-2">Màu sắc: <span class="text-dark fw-black">{{ selectedColor }}</span></div>
        <div class="d-flex flex-wrap gap-2">
          <button 
            v-for="col in availableColors" :key="col"
            @click="selectedColor = col"
            class="btn btn-outline-dark fw-medium rounded-pill px-4 py-2"
            :class="selectedColor === col ? 'active bg-dark text-white border-dark shadow-sm' : 'border-slate-200'"
          >
            {{ col }}
          </button>
        </div>
      </div>
      
    </div>

    <!-- Tình trạng & Kho -->
    <div class="mb-4 p-3 bg-slate-50 border rounded-3 text-slate-600 fw-medium d-flex flex-column gap-2">
      <div class="d-flex justify-content-between">
        <span>Tình trạng máy:</span>
        <span class="text-dark fw-bold">{{ activeVariant?.condition || 'Mới nguyên seal' }}</span>
      </div>
      <div class="d-flex justify-content-between">
        <span>Kho hàng:</span>
        <span v-if="currentStock > 0" class="text-success fw-bold">Còn {{ currentStock }} sản phẩm</span>
        <span v-else class="text-warning fw-bold">Cho phép đặt trước (Pre-order)</span>
      </div>
    </div>

    <p class="text-secondary mb-4 lh-lg">{{ product?.description }}</p>

    <!-- Khu vực Hành động -->
    <div class="action-area border-top pt-4">
      <div class="d-flex flex-column flex-sm-row gap-3 mb-3">
        <!-- Bộ chọn số lượng -->
        <div class="quantity-selector d-flex align-items-center justify-content-between border rounded-pill px-3 bg-white shadow-sm" style="width: 140px; height: 54px;">
          <button class="btn btn-link text-dark p-0 text-decoration-none shadow-none" @click="quantity > 1 ? quantity-- : null"><i class="bi bi-dash fs-4"></i></button>
          <span class="fw-bold fs-5">{{ quantity }}</span>
          <button class="btn btn-link text-dark p-0 text-decoration-none shadow-none" @click="quantity < 5 ? quantity++ : null"><i class="bi bi-plus fs-4"></i></button>
        </div>
        
        <!-- Nút Thêm Giỏ Hàng -->
        <button 
          class="btn btn-outline-dark flex-grow-1 rounded-pill fw-bold fs-6 shadow-sm d-flex justify-content-center align-items-center gap-2"
          style="height: 54px;"
          :disabled="!isValidConfig"
          @click="handleAddToCart"
        >
          <i class="bi bi-cart-plus fs-5"></i> Thêm Giỏ Hàng
        </button>
      </div>

      <!-- Nút MUA NGAY to bản -->
      <button 
        class="btn w-100 rounded-pill fw-bold shadow d-flex flex-column justify-content-center align-items-center transition-all"
        style="height: 64px;"
        :class="!isValidConfig ? 'btn-secondary disabled' : (currentStock <= 0 ? 'btn-warning text-dark' : 'btn-danger text-white')"
        @click="handleBuyNow"
      >
        <span class="fs-5">{{ !isValidConfig ? 'CHƯA CHỌN CẤU HÌNH' : (currentStock <= 0 ? 'MUA TRƯỚC (PRE-ORDER)' : 'MUA NGAY') }}</span>
        <span class="small fw-normal" v-if="isValidConfig">Giao tận nơi hoặc nhận tại cửa hàng</span>
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useCartStore } from '../../cart/stores/cartStore';
import { useAppStore } from '@/common/store/app.store';

const props = defineProps<{ product: any; brandList?: any[]; }>();

const cartStore = useCartStore();
const appStore = useAppStore();
const router = useRouter();

const quantity = ref(1);
const selectedStorage = ref<string>('');
const selectedColor = ref<string>('');
const isFlashSale = ref(false);

// 1. Kiểm tra an toàn dữ liệu mảng variants
const safeVariants = computed<any[]>(() => {
  return Array.isArray(props.product?.variants) ? props.product.variants : [];
});
const hasVariants = computed(() => safeVariants.value.length > 0);

// 2. Trích xuất Storage an toàn
const availableStorages = computed<string[]>(() => {
  const storages = safeVariants.value.map(v => v?.storage || '').filter(Boolean);
  return Array.from(new Set(storages));
});

// 3. Trích xuất Color an toàn
const availableColors = computed<string[]>(() => {
  const colors = safeVariants.value
    .filter(v => !selectedStorage.value || (v?.storage || '') === selectedStorage.value)
    .map(v => v?.color || '')
    .filter(Boolean);
  return Array.from(new Set(colors));
});

// 4. Set mặc định khi load
onMounted(() => {
  if (availableStorages.value.length > 0) selectedStorage.value = availableStorages.value[0] ?? '';
  if (availableColors.value.length > 0 && !selectedColor.value) selectedColor.value = availableColors.value[0] ?? '';
});

// 5. Watcher: Đổi Storage tự động khớp Color
watch(selectedStorage, () => {
  if (availableColors.value.length > 0 && !availableColors.value.includes(selectedColor.value)) {
    selectedColor.value = availableColors.value[0] ?? '';
  }
});

// 6. Tìm Variant chuẩn xác
const activeVariant = computed<any>(() => {
  if (!hasVariants.value) return null;
  // Trúng TH chỉ có 1 variant không tên tuổi (Sản phẩm nguyên khối)
  if (safeVariants.value.length === 1 && availableStorages.value.length === 0 && availableColors.value.length === 0) {
    return safeVariants.value[0];
  }
  return safeVariants.value.find(v => 
    (v?.storage || '') === selectedStorage.value && 
    (v?.color || '') === selectedColor.value
  );
});

// 7. Các biến tính toán UI
const activePrice = computed(() => activeVariant.value?.price || safeVariants.value[0]?.price || 0);
const currentStock = computed(() => activeVariant.value?.stock || 0);

// Bắt ID an toàn (Tránh đứt gãy DTO Backend - Frontend)
const activeVariantId = computed<number | null>(() => {
  if (!activeVariant.value) return null;
  return activeVariant.value.variantId || activeVariant.value.id || null;
});

const isValidConfig = computed(() => activeVariantId.value !== null);

const getBrandName = (id?: number) => {
  if (!id || !props.brandList) return '';
  const b = props.brandList.find(x => x.id === id);
  return b ? b.name : '';
};

const formatMoney = (val: number) => new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(val);

// 8. Handlers
const handleAddToCart = async () => {
  if (!isValidConfig.value) {
    appStore.notifyError("Vui lòng chọn đầy đủ cấu hình sản phẩm!");
    return;
  }
  await cartStore.addItem(activeVariantId.value!, quantity.value);
  appStore.notifySuccess("Đã thêm vào giỏ hàng!");
};

const handleBuyNow = async () => {
  if (!isValidConfig.value) {
    appStore.notifyError("Vui lòng chọn đầy đủ cấu hình sản phẩm!");
    return;
  }
  await cartStore.addItem(activeVariantId.value!, quantity.value);
  router.push('/checkout');
};
</script>