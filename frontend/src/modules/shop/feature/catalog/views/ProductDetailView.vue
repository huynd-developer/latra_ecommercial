<template>
  <div class="bg-white min-h-screen mb-5 pb-lg-5">
    
    <div class="container py-5" v-if="isLoading">
      <div class="text-center py-5"><span class="spinner-border text-dark"></span></div>
    </div>

    <template v-else-if="product">
      
      <div class="container pt-5">
        <nav aria-label="breadcrumb" class="mb-4">
          <ol class="breadcrumb small">
            <li class="breadcrumb-item"><router-link to="/" class="text-decoration-none text-muted">Trang chủ</router-link></li>
            <li class="breadcrumb-item"><router-link to="/shop" class="text-decoration-none text-muted">Cửa hàng</router-link></li>
            <li class="breadcrumb-item active fw-bold text-dark">{{ product.name }}</li>
          </ol>
        </nav>

        <div class="row gx-lg-5 position-relative mb-5">
          <div class="col-lg-6 mb-5 mb-lg-0">
            <div class="position-sticky" style="top: 100px;">
              <ImageGallery :images="allImages" />
            </div>
          </div>

          <div class="col-lg-6">
            <div id="main-buy-section">
              <ProductInfo :product="product" :brandList="catalogStore.brands" />
            </div>
          </div>
        </div>
      </div>

      <ProductFeatures :image="allImages[0]" />

      <ProductVideos />
      <PhoneIntroSection :image="allImages[0]" />
      
      <div class="container">
        <div class="row mt-5 pt-5 border-top">
          <div class="col-12 col-md-10 mx-auto">
            <ReviewSection />
          </div>
        </div>
      </div>
      
      <ProductFaq />
      
    </template>

    <div v-else class="container text-center py-5 my-5">
      <i class="bi bi-box-seam display-1 text-muted mb-4 d-block"></i>
      <h2 class="fw-bold text-dark">Sản phẩm không tồn tại!</h2>
      <router-link to="/shop" class="btn btn-dark rounded-pill px-5 py-2 mt-3">Quay lại Cửa Hàng</router-link>
    </div>

    <button 
      class="peel-scroll-top" 
      :class="{ 'show': showStickyElements }" 
      @click="scrollToTop"
    >
      <i class="bi bi-arrow-up"></i>
    </button>

    <div class="peel-sticky-bar" :class="{ 'show': showStickyElements }">
      <div class="container h-100">
        <div class="d-flex align-items-center justify-content-between h-100">
          
          <div class="d-none d-md-flex align-items-center gap-3">
            <div class="sticky-img-wrapper">
              <img :src="allImages[0]" alt="Product">
            </div>
            <div class="d-flex flex-column justify-content-center">
              <span class="sticky-title">{{ product?.name }}</span>
              <div class="sticky-price-row">
                <span class="sticky-price-current">{{ formatMoney(product?.price || 390000) }}</span>
                <span class="sticky-price-old ms-2" v-if="product?.originalPrice || 450000">{{ formatMoney(product?.originalPrice || 450000) }}</span>
              </div>
            </div>
          </div>

          <div class="d-flex align-items-center gap-2 gap-md-3 ms-auto">
            
            <div 
              ref="dropupRef"
              class="custom-dropup d-none d-sm-block" 
              v-if="product?.variants && product.variants.length > 0"
            >
              <div class="dropup-selected" @click="isVariantMenuOpen = !isVariantMenuOpen">
                {{ selectedVariantName }}
                <i class="bi bi-chevron-up ms-2" style="font-size: 12px; color: #666;"></i>
              </div>
              
              <ul class="dropup-list" :class="{ 'show': isVariantMenuOpen }">
                <li 
                  v-for="variant in product.variants" 
                  :key="variant.variantId || variant.id"
                  @click="selectVariant(variant)"
                  :class="{ 'active': selectedVariantId === (variant.variantId || variant.id) }"
                >
                  {{ variant.color || variant.colorName || variant.name || 'Mặc định' }}
                </li>
              </ul>
            </div>
            
            <div class="peel-sticky-qty d-flex align-items-center justify-content-between">
              <button class="sticky-qty-btn" @click="stickyQty > 1 ? stickyQty-- : null">—</button>
              <span class="sticky-qty-val">{{ stickyQty }}</span>
              <button class="sticky-qty-btn" @click="stickyQty++">+</button>
            </div>
            
            <button class="btn peel-sticky-add-btn" @click="addFromSticky">
              Thêm Vào Giỏ Hàng
            </button>
          </div>

        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue';
import { useRoute } from 'vue-router';
import request from '@/common/services/request';
import ImageGallery from '../components/ImageGallery.vue';
import ProductInfo from '../components/ProductInfo.vue';
import ReviewSection from '../components/ReviewSection.vue';
import ProductFeatures from '../components/ProductFeatures.vue';
import ProductVideos from '../components/ProductVideos.vue'; 
import PhoneIntroSection from '../components/PhoneIntroSection.vue';
import ProductFaq from '../components/ProductFaq.vue'; // ĐN THÊM: Import component FAQ vào đây
import { useCatalogStore } from '../stores/catalogStore';
import { useAppStore } from '@/common/store/app.store'; 
import { useCartStore } from '../../cart/stores/cartStore';

const route = useRoute();
const catalogStore = useCatalogStore();
const appStore = useAppStore();
const cartStore = useCartStore();

const productId = Number(route.params.id);
const product = ref<any>(null);
const isLoading = ref(true);

const showStickyElements = ref(false);
const stickyQty = ref(1);

const selectedVariantId = ref<number | null>(null);
const isVariantMenuOpen = ref(false);
const dropupRef = ref<HTMLElement | null>(null);

const selectedVariantName = computed(() => {
  if (!product.value?.variants) return 'Chọn cấu hình';
  const selected = product.value.variants.find(
    (v: any) => (v.variantId || v.id) === selectedVariantId.value
  );
  return selected ? (selected.color || selected.colorName || selected.name || 'Mặc định') : 'Chọn cấu hình';
});

onMounted(async () => {
  if (catalogStore.brands.length === 0) catalogStore.fetchDropdowns();
  try {
    const res = await request.get(`/products/${productId}`);
    product.value = res.data; 

    if (product.value?.variants && product.value.variants.length > 0) {
      const firstVariant = product.value.variants[0];
      selectedVariantId.value = firstVariant.variantId || firstVariant.id;
    }
  } catch (error) {
    console.error("Lỗi lấy chi tiết:", error);
  } finally {
    isLoading.value = false;
  }
  window.addEventListener('scroll', handleScroll);
  window.addEventListener('click', handleClickOutside); 
});

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll);
  window.removeEventListener('click', handleClickOutside);
});

const handleScroll = () => {
  const scrollPosition = window.scrollY;
  if (scrollPosition > 600) {
    showStickyElements.value = true;
  } else {
    showStickyElements.value = false;
    isVariantMenuOpen.value = false; 
  }
};

const handleClickOutside = (event: MouseEvent) => {
  if (dropupRef.value && !dropupRef.value.contains(event.target as Node)) {
    isVariantMenuOpen.value = false;
  }
};

const selectVariant = (variant: any) => {
  selectedVariantId.value = variant.variantId || variant.id;
  isVariantMenuOpen.value = false;
};

const scrollToTop = () => {
  window.scrollTo({
    top: 0,
    behavior: 'smooth'
  });
};

const formatMoney = (val: number) => {
  if (val < 1000) return `$${val.toFixed(2)}`; 
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(val);
};

const addFromSticky = async () => {
  if (selectedVariantId.value) {
    await cartStore.addItem(selectedVariantId.value, stickyQty.value);
    appStore.notifySuccess("Đã thêm vào giỏ hàng từ thanh công cụ!");
  } else {
    appStore.notifyError("Vui lòng chọn cấu hình sản phẩm!");
  }
};

const allImages = computed(() => {
  if (!product.value) return [];
  const imgs = [];
  if (product.value.mainImageUrl) {
    const url = product.value.mainImageUrl;
    imgs.push(url.startsWith('http') ? url : `http://localhost:8080/uploads/${url}`);
  }
  return imgs.length > 0 ? imgs : ['https://via.placeholder.com/500'];
});
</script>

<style scoped>
/* ========================================= */
/* CSS CHO CUSTOM DROPUP (MỞ LÊN TRÊN)       */
/* ========================================= */
.custom-dropup {
  position: relative;
  min-width: 140px;
}
.dropup-selected {
  border: 1px solid #e1e1e1;
  border-radius: 50px;
  padding: 0 15px 0 20px;
  height: 48px;
  font-size: 14px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  cursor: pointer;
  background: #fff;
  user-select: none;
  transition: border-color 0.2s;
}
.dropup-selected:hover {
  border-color: #1a1a1a;
}
.dropup-list {
  position: absolute;
  bottom: calc(100% + 8px); 
  left: 0;
  width: 100%;
  background: #fff;
  border: 1px solid #e1e1e1;
  border-radius: 12px;
  padding: 8px 0;
  margin: 0;
  list-style: none;
  box-shadow: 0 -10px 25px rgba(0, 0, 0, 0.08);
  opacity: 0;
  visibility: hidden;
  transform: translateY(10px);
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
  z-index: 1060;
}
.dropup-list.show {
  opacity: 1;
  visibility: visible;
  transform: translateY(0);
}
.dropup-list li {
  padding: 10px 20px;
  font-size: 14px;
  cursor: pointer;
  transition: background 0.2s;
}
.dropup-list li:hover {
  background-color: #f8f9fa;
}
.dropup-list li.active {
  font-weight: 600;
  background-color: #f1f1f1;
  color: #000;
}

/* ========================================= */
/* CSS THÀNH PHẦN NỔI (GIỮ NGUYÊN GIAO DIỆN) */
/* ========================================= */
.peel-scroll-top {
  position: fixed;
  bottom: 115px;
  right: 30px;
  width: 45px;
  height: 45px;
  background-color: #000000;
  color: #ffffff;
  border: none;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  opacity: 0;
  visibility: hidden;
  transform: translateY(20px);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  z-index: 1045;
  cursor: pointer;
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
}
.peel-scroll-top:hover {
  background-color: #333333;
}
.peel-scroll-top.show {
  opacity: 1;
  visibility: visible;
  transform: translateY(0);
}

.peel-sticky-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 90px;
  background-color: #ffffff;
  border-top: 1px solid #e1e1e1;
  box-shadow: 0 -4px 15px rgba(0, 0, 0, 0.03);
  transform: translateY(100%);
  transition: transform 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  z-index: 1050;
}
.peel-sticky-bar.show {
  transform: translateY(0);
}

.sticky-img-wrapper {
  width: 56px;
  height: 56px;
  background-color: #f5f5f5;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 4px;
}
.sticky-img-wrapper img {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
}
.sticky-title {
  font-size: 15px;
  font-weight: 500;
  color: #1a1a1a;
  margin-bottom: 2px;
}
.sticky-price-current {
  color: #d13227;
  font-size: 14px;
  font-weight: 700;
}
.sticky-price-old {
  color: #767676;
  font-size: 13px;
  text-decoration: line-through;
}

.peel-sticky-qty {
  border: 1px solid #e1e1e1;
  border-radius: 50px;
  height: 48px;
  width: 110px;
  padding: 0 8px;
  background: #fff;
}
.sticky-qty-btn {
  background: none;
  border: none;
  font-size: 16px;
  color: #1a1a1a;
  width: 30px;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
}
.sticky-qty-val {
  font-size: 15px;
  font-weight: 400;
}

.peel-sticky-add-btn {
  background-color: #000000;
  color: #ffffff;
  border-radius: 50px;
  height: 48px;
  padding: 0 35px;
  font-size: 15px;
  font-weight: 500;
  transition: opacity 0.2s;
}
.peel-sticky-add-btn:hover {
  background-color: #000000;
  color: #ffffff;
  opacity: 0.85;
}
</style>