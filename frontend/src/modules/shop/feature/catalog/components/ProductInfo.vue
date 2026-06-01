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

    <div class="peel-option-block mb-3" v-if="availableColors.length > 0">
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

    <div class="peel-stock-status mb-4" v-if="activeVariant">
      <div v-if="activeVariant.stock > 0" class="stock-available">
        <i class="bi bi-box-seam me-1"></i> Còn lại: <strong>{{ activeVariant.stock }}</strong> sản phẩm trong kho
      </div>
      <div v-else class="stock-empty">
        <i class="bi bi-exclamation-triangle me-1"></i> Rất tiếc, cấu hình này đã hết hàng!
      </div>
    </div>

    <div class="peel-actions">
      <div class="d-flex gap-2 mb-2">
        <div class="peel-qty-box d-flex align-items-center justify-content-between" :class="{ 'disabled-box': isOutOfStock }">
          <button class="qty-btn" :disabled="isOutOfStock" @click="quantity > 1 ? quantity-- : null">—</button>
          <span class="qty-val">{{ isOutOfStock ? 0 : quantity }}</span>
          <button class="qty-btn" :disabled="isOutOfStock" @click="quantity < maxStock ? quantity++ : null">+</button>
        </div>
        
        <button 
          class="peel-btn peel-btn-add flex-grow-1 d-flex justify-content-center align-items-center gap-2"
          :disabled="!isValidConfig || isOutOfStock"
          @click="handleAddToCart"
        >
          <i class="bi bi-bag-plus" style="font-size: 16px;"></i> 
          {{ isOutOfStock ? 'HẾT HÀNG' : 'THÊM VÀO GIỎ HÀNG' }}
        </button>
      </div>

      <button 
        class="peel-btn peel-btn-buy w-100 mb-2"
        :disabled="!isValidConfig || isOutOfStock"
        @click="handleBuyNow"
      >
        {{ isOutOfStock ? 'TẠM HẾT HÀNG' : 'MUA NGAY VỚI SHOP' }}
      </button>

      <div class="text-center mt-2">
        <a href="#" class="peel-link" style="font-size: 13px; color: #767676; text-decoration: none;">More payment options</a>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useCartStore } from '../../cart/stores/cartStore';
import { useAppStore } from '@/common/store/app.store';
import { useAuthStore } from '@/modules/auth/stores/authStore'; 

const props = defineProps<{ product: any; brandList?: any[]; }>();

const cartStore = useCartStore();
const appStore = useAppStore();
const authStore = useAuthStore(); 
const router = useRouter();
const route = useRoute(); 

const quantity = ref(1);
const selectedStorage = ref<string>('');
const selectedColor = ref<string>('');

const getColorHex = (colorName: string) => {
  const lower = colorName?.toLowerCase() || '';
  if (lower.includes('trắng') || lower.includes('white')) return '#ffffff';
  if (lower.includes('đen') || lower.includes('black') || lower.includes('matte')) return '#222222';
  if (lower.includes('xanh dương') || lower.includes('blue')) return '#1a508b';
  return '#b3b3b3'; 
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

const activeVariant = computed<any>(() => {
  if (!hasVariants.value) return null;
  if (safeVariants.value.length === 1 && availableStorages.value.length === 0 && availableColors.value.length === 0) return safeVariants.value[0];
  return safeVariants.value.find(v => (v?.storage || '') === selectedStorage.value && (v?.color || '') === selectedColor.value);
});

const maxStock = computed(() => activeVariant.value?.stock ?? 0);
const isOutOfStock = computed(() => maxStock.value <= 0);

watch(activeVariant, (newVariant) => {
  if (availableColors.value.length > 0 && !availableColors.value.includes(selectedColor.value)) {
    selectedColor.value = availableColors.value[0] ?? '';
  }
  
  if (newVariant) {
    if (newVariant.stock <= 0) {
      quantity.value = 1; 
    } else if (quantity.value > newVariant.stock) {
      quantity.value = newVariant.stock; 
    }
  }
});

const activePrice = computed(() => activeVariant.value?.price || safeVariants.value[0]?.price || 0);
const activeVariantId = computed<number | null>(() => activeVariant.value?.variantId || activeVariant.value?.id || null);
const isValidConfig = computed(() => activeVariantId.value !== null);

const formatMoney = (val: number) => {
  if (val < 1000) return `$${val.toFixed(2)}`; 
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(val);
};

// --------------------------------------------------------
// LOGIC ĐÃ SỬA: Bảo mật luồng gọi API Add to Cart
// --------------------------------------------------------
const handleAddToCart = async () => {
  if (!isValidConfig.value || isOutOfStock.value) {
    appStore.notifyError("Sản phẩm tạm thời hết hàng hoặc chưa chọn đúng cấu hình!");
    return;
  }

  // 1. Chặn request API 500: Kiểm tra đăng nhập
  if (!authStore.isLoggedIn) {
    appStore.notifyError("Vui lòng đăng nhập để thêm vào giỏ hàng!");
    // Đẩy sang trang login kèm "dấu vết" là đường dẫn sản phẩm hiện tại
    router.push({ path: '/login', query: { redirect: route.fullPath } });
    return;
  }

  // 2. Nếu đã đăng nhập thì mới cho gọi API giỏ hàng
  await cartStore.addItem(activeVariantId.value!, quantity.value);
  appStore.notifySuccess("Đã thêm vào giỏ hàng!");
};

// --------------------------------------------------------
// LOGIC ĐÃ SỬA: Mua Ngay chuyển sang trang Giỏ Hàng
// --------------------------------------------------------
const handleBuyNow = async () => {
  if (!isValidConfig.value || isOutOfStock.value) {
    appStore.notifyError("Sản phẩm tạm thời hết hàng hoặc chưa chọn đúng cấu hình!");
    return;
  }

  // 1. Kiểm tra đăng nhập
  if (!authStore.isLoggedIn) {
    appStore.notifyError("Vui lòng đăng nhập để mua hàng!");
    router.push({ path: '/login', query: { redirect: route.fullPath } });
    return;
  }

  // 2. Thêm vào giỏ hàng và chuyển hướng sang trang giỏ hàng
  await cartStore.addItem(activeVariantId.value!, quantity.value);
  router.push('/cart'); 
};
</script>

<style scoped>
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

.peel-stock-status {
  font-size: 13px;
}
.stock-available {
  color: #2e7d32;
  background-color: #edf7ed;
  padding: 6px 12px;
  border-radius: 6px;
  display: inline-block;
}
.stock-empty {
  color: #c62828;
  background-color: #fdecea;
  padding: 6px 12px;
  border-radius: 6px;
  display: inline-block;
  font-weight: 500;
}
.disabled-box {
  opacity: 0.4;
  pointer-events: none;
}

.peel-color-swatch-wrapper {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  border: 1px solid transparent; 
  background: none;
  padding: 3px; 
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.1s ease;
}
.peel-color-swatch-wrapper.active {
  border-color: #1a1a1a; 
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
.qty-btn:disabled {
  cursor: not-allowed;
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
  background-color: #cccccc !important;
  color: #666666 !important;
  cursor: not-allowed;
  opacity: 0.7;
}
.peel-btn:hover:not(:disabled) {
  opacity: 0.85;
}

.peel-btn-add {
  background-color: #ebebeb;
  color: #1a1a1a;
}

.peel-btn-buy {
  background-color: #5a31f4; 
  color: #ffffff;
}
</style>