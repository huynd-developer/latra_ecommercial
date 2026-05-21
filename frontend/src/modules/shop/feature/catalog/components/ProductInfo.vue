<template>
  <div class="peel-exact-ui py-2">
    <h1 class="peel-title mb-1">{{ product?.name || 'Active iPhone 17 Pro Max Case' }}</h1>
    
    <div class="peel-price-row mb-1">
      <span class="price-current">{{ formatMoney(activePrice) }}</span>
      <span class="price-old ms-2" v-if="product?.originalPrice || activePrice < 450000">{{ formatMoney(product?.originalPrice || 450000) }}</span>
    </div>

    <div class="peel-sub-text mb-3 mt-2">
      <a href="#" class="peel-link">Shipping</a> calculated at checkout.
    </div>
    <div class="peel-sub-text mb-4">
      Pay in 2 interest-free installments of $19.50 with <span class="shop-pay-logo">shop</span> <a href="#" class="peel-link ms-1">Learn more</a>
    </div>

    <div class="peel-description mb-4">
      <p class="mb-3">{{ product?.description || "Protection that respects your phone's design - Peel's Active Case shields your device without stealing the spotlight." }}</p>
      <p class="small text-muted" style="font-size: 13px; font-style: italic;">*previously known as the Bumper Case</p>
    </div>

    <div class="peel-option-block mb-3" v-if="availableStorages.length > 0">
      <div class="peel-option-label mb-2">Storage: <strong>{{ selectedStorage }}</strong></div>
      <div class="d-flex flex-wrap gap-2">
        <button 
          v-for="st in availableStorages" :key="st"
          @click="selectedStorage = st"
          class="peel-text-swatch"
          :class="{ 'active': selectedStorage === st }"
        >
          {{ st }}
        </button>
      </div>
    </div>

    <div class="peel-option-block mb-4" v-if="availableColors.length > 0">
      <div class="peel-option-label mb-2">Color: <strong>{{ selectedColor || 'Black' }}</strong></div>
      <div class="d-flex flex-wrap gap-2 mb-2 align-items-center">
        <button 
          v-for="col in availableColors" :key="col"
          @click="selectedColor = col"
          class="peel-color-swatch-wrapper"
          :class="{ 'active': selectedColor === col }"
          :title="col"
        >
          <span class="peel-color-swatch-inner" :style="{ backgroundColor: getColorHex(col) }"></span>
        </button>
      </div>
      <a href="#" class="peel-link text-decoration-underline" style="font-size: 13px;">View Color Guide</a>
    </div>

    <div class="peel-actions">
      <div class="d-flex gap-2 mb-2">
        <div class="peel-qty-box d-flex align-items-center justify-content-between">
          <button class="qty-btn" @click="quantity > 1 ? quantity-- : null">—</button>
          <span class="qty-val">{{ quantity }}</span>
          <button class="qty-btn" @click="quantity < 5 ? quantity++ : null">+</button>
        </div>
        
        <button 
          class="peel-btn peel-btn-add flex-grow-1 d-flex justify-content-center align-items-center gap-2"
          :disabled="!isValidConfig"
          @click="handleAddToCart"
        >
          <i class="bi bi-bag-plus" style="font-size: 16px;"></i> THÊM VÀO GIỎ HÀNG
        </button>
      </div>

      <button 
        class="peel-btn peel-btn-buy w-100 mb-2"
        :disabled="!isValidConfig"
        @click="handleBuyNow"
      >
        MUA NGAY VỚI SHOP
      </button>

      <div class="text-center mt-2">
        <a href="#" class="peel-link" style="font-size: 13px; color: #767676; text-decoration: none;">More payment options</a>
      </div>
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

// Trích xuất mã màu cơ bản để render vòng tròn màu
const getColorHex = (colorName: string) => {
  const lower = colorName?.toLowerCase() || '';
  if (lower.includes('trắng') || lower.includes('white')) return '#ffffff';
  if (lower.includes('đen') || lower.includes('black') || lower.includes('matte')) return '#222222';
  if (lower.includes('xanh dương') || lower.includes('blue')) return '#1a508b';
  return '#b3b3b3'; // Màu xám nhạt mặc định như ảnh nếu ko nhận diện được
};

const safeVariants = computed<any[]>(() => Array.isArray(props.product?.variants) ? props.product.variants : []);
const hasVariants = computed(() => safeVariants.value.length > 0);

const availableStorages = computed<string[]>(() => {
  return Array.from(new Set(safeVariants.value.map(v => v?.storage || '').filter(Boolean)));
});

const availableColors = computed<string[]>(() => {
  return Array.from(new Set(safeVariants.value
    .filter(v => !selectedStorage.value || (v?.storage || '') === selectedStorage.value)
    .map(v => v?.color || '')
    .filter(Boolean)));
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
  if (safeVariants.value.length === 1 && availableStorages.value.length === 0 && availableColors.value.length === 0) return safeVariants.value[0];
  return safeVariants.value.find(v => (v?.storage || '') === selectedStorage.value && (v?.color || '') === selectedColor.value);
});

const activePrice = computed(() => activeVariant.value?.price || safeVariants.value[0]?.price || 0);

const activeVariantId = computed<number | null>(() => activeVariant.value?.variantId || activeVariant.value?.id || null);
const isValidConfig = computed(() => activeVariantId.value !== null);

// Định dạng tiền giả định (nếu data gốc là USD thì ông sửa lại hàm format này nhé)
const formatMoney = (val: number) => {
  if (val < 1000) return `$${val.toFixed(2)}`; // Render USD nếu giá trị nhỏ
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(val);
};

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

<style scoped>
/* Reset font giống ảnh */
.peel-exact-ui {
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
  color: #1a1a1a;
  max-width: 450px;
}

.peel-title {
  font-size: 26px;
  font-weight: 600;
  letter-spacing: -0.5px;
  line-height: 1.2;
}

.price-current {
  color: #d13227;
  font-size: 16px;
  font-weight: 700;
}

.price-old {
  color: #767676;
  font-size: 15px;
  text-decoration: line-through;
}

.peel-sub-text {
  font-size: 13px;
  color: #1a1a1a;
}
.peel-link {
  color: #1a1a1a;
  text-decoration: underline;
  text-underline-offset: 3px;
}

.shop-pay-logo {
  font-weight: 800;
  color: #5a31f4;
}

.peel-description p {
  font-size: 14px;
  line-height: 1.5;
  color: #1a1a1a;
}

.peel-option-label {
  font-size: 14px;
  color: #1a1a1a;
}

/* Vòng tròn màu y hệt bản gốc (image_d6bd93.png) */
.peel-color-swatch-wrapper {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  border: 1px solid transparent; 
  background: none;
  padding: 3px; /* Tạo viền trắng ở giữa */
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.1s ease;
}
.peel-color-swatch-wrapper.active {
  border-color: #1a1a1a; /* Viền ngoài màu đen khi active */
}
.peel-color-swatch-inner {
  display: block;
  width: 100%;
  height: 100%;
  border-radius: 50%;
}

.peel-text-swatch {
  background: #fff;
  border: 1px solid #e1e1e1;
  border-radius: 4px;
  padding: 6px 14px;
  font-size: 14px;
}
.peel-text-swatch.active {
  border-color: #1a1a1a;
  border-width: 2px;
  padding: 5px 13px;
}

/* Buttons (image_f3ca0d.png) */
.peel-qty-box {
  width: 105px;
  height: 48px;
  background-color: #ebebeb;
  border-radius: 50px;
  padding: 0 10px;
}
.qty-btn {
  background: none;
  border: none;
  font-size: 16px;
  color: #1a1a1a;
  cursor: pointer;
  width: 30px;
  display: flex;
  justify-content: center;
  align-items: center;
}
.qty-val {
  font-size: 15px;
  font-weight: 400;
}

.peel-btn {
  height: 48px;
  border: none;
  border-radius: 50px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: opacity 0.2s;
}
.peel-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
.peel-btn:hover:not(:disabled) {
  opacity: 0.85;
}

.peel-btn-add {
  background-color: #ebebeb;
  color: #1a1a1a;
}

.peel-btn-buy {
  background-color: #5a31f4; /* Tím Shop Pay */
  color: #ffffff;
}
</style>