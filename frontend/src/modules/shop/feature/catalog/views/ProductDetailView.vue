<!-- frontend/src/modules/shop/feature/catalog/views/ProductDetailView.vue -->
<template>
  <div class="bg-white min-h-screen py-5">
    <div class="container" v-if="isLoading">
      <div class="text-center py-5"><span class="spinner-border text-dark"></span></div>
    </div>

    <div class="container" v-else-if="product">
      <!-- Breadcrumb -->
      <nav aria-label="breadcrumb" class="mb-4">
        <ol class="breadcrumb small">
          <li class="breadcrumb-item"><router-link to="/" class="text-decoration-none text-muted">Trang chủ</router-link></li>
          <li class="breadcrumb-item"><router-link to="/shop" class="text-decoration-none text-muted">Cửa hàng</router-link></li>
          <li class="breadcrumb-item active fw-bold text-dark">{{ product.name }}</li>
        </ol>
      </nav>

      <!-- Layout Sticky -->
      <div class="row gx-lg-5 position-relative">
        
        <!-- Cột Trái: Ảnh (Dính chặt khi cuộn) -->
        <div class="col-lg-6 mb-5 mb-lg-0">
          <div class="position-sticky" style="top: 100px;">
            <ImageGallery :images="allImages" />
          </div>
        </div>

        <!-- Cột Phải: Thông tin & Chốt đơn (Cuộn được) -->
        <div class="col-lg-6">
          <ProductInfo :product="product" :brandList="catalogStore.brands" />
          
          <hr class="my-5 opacity-25">
          <!-- Đưa phần Review lên đây để khách đọc xong là thấy nút Mua ngay bên trên -->
          <ReviewSection />
        </div>

      </div>
    </div>

    <div v-else class="text-center py-5 my-5">
      <i class="bi bi-box-seam display-1 text-muted mb-4 d-block"></i>
      <h2 class="fw-bold text-dark">Sản phẩm không tồn tại!</h2>
      <router-link to="/shop" class="btn btn-dark rounded-pill px-5 py-2 mt-3">Quay lại Cửa Hàng</router-link>
    </div>
  </div>
</template>

<script setup lang="ts">
// (Phần Script của ProductDetailView GIỮ NGUYÊN như bản trước mình gửi bạn nhé)
import { ref, computed, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import request from '@/common/services/request';
import ImageGallery from '../components/ImageGallery.vue';
import ProductInfo from '../components/ProductInfo.vue';
import ReviewSection from '../components/ReviewSection.vue';
import { useCatalogStore } from '../stores/catalogStore';

const route = useRoute();
const catalogStore = useCatalogStore();
const productId = Number(route.params.id);
const product = ref<any>(null);
const isLoading = ref(true);

onMounted(async () => {
  if (catalogStore.brands.length === 0) catalogStore.fetchDropdowns();
  try {
    const res = await request.get(`/products/${productId}`);
    product.value = res.data; 
  } catch (error) {
    console.error("Lỗi lấy chi tiết:", error);
  } finally {
    isLoading.value = false;
  }
});

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