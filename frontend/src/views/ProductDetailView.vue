<template>
  <div class="product-detail-page py-5" style="font-family: 'Inter', sans-serif;">
    <div class="container px-4 px-lg-5" v-if="product">
      
      <nav aria-label="breadcrumb" class="mb-4">
        <ol class="breadcrumb small">
          <li class="breadcrumb-item"><router-link to="/" class="text-dark text-decoration-none">Trang chủ</router-link></li>
          <li class="breadcrumb-item"><router-link to="/shop-all" class="text-dark text-decoration-none">Sản phẩm</router-link></li>
          <li class="breadcrumb-item active" aria-current="page">{{ product.name }}</li>
        </ol>
      </nav>

      <div class="row gx-5">
        <div class="col-lg-6 mb-5 mb-lg-0">
          <ImageGallery :images="allImages" />
        </div>
        
        <div class="col-lg-6 ps-lg-5">
          <ProductInfo :product="product" @add-to-cart="handleAddToCart" />
        </div>
      </div>

      <hr class="my-5 opacity-25">
      <ReviewSection />
    </div>

    <div v-else class="text-center py-5 my-5">
      <i class="bi bi-emoji-frown display-1 text-muted mb-4 d-block"></i>
      <h2 class="fw-bold text-dark">Sản phẩm không tồn tại!</h2>
      <p class="text-secondary mb-4">Có vẻ như sản phẩm bạn tìm đã bị xóa hoặc đường dẫn bị sai.</p>
      <router-link to="/shop-all" class="btn btn-dark rounded-pill px-4 py-2">Quay lại Cửa Hàng</router-link>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'

// Nhập dữ liệu từ kho chung src/data/db.ts (Đúng chuẩn src.docx)
import { productsDB, type Product } from '../data/db'

// Lấy 3 linh kiện của Người số 3 từ src/components/product/ (Đúng chuẩn src.docx)
import ImageGallery from '../components/product/ImageGallery.vue'
import ProductInfo from '../components/product/ProductInfo.vue'
import ReviewSection from '../components/product/ReviewSection.vue'

const route = useRoute()

// 1. Tự động lấy ID từ URL (VD: /product/101 thì ID = 101)
const productId = Number(route.params.id)

// 2. Lục trong Data xem có sản phẩm nào khớp ID không
const product = computed(() => productsDB.find(p => p.id === productId) as unknown as Product)

// 3. Gom tất cả ảnh (ảnh chính + ảnh phụ) thành 1 cục để ném vào ImageGallery
const allImages = computed(() => {
  if (!product.value) return []
  const images = [product.value.image] // Luôn có ảnh gốc đầu tiên
  if (product.value.gallery && product.value.gallery.length > 0) {
    images.push(...product.value.gallery) // Nhét thêm ảnh phụ vào
  }
  return images
})

// 4. Xử lý khi Người dùng bấm Thêm Giỏ Hàng
const handleAddToCart = (payload: { product: Product, quantity: number }) => {
  console.log('Thêm giỏ hàng từ trang Chi Tiết:', payload.product.name, 'Số lượng:', payload.quantity)
  // Sau này Người 1 (Kỹ sư nền tảng) sẽ kết nối hàm từ store vào đây
}
</script>