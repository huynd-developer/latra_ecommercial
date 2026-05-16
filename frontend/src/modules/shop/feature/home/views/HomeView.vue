<!-- frontend/src/modules/shop/feature/home/views/HomeView.vue -->
<template>
  <div class="home-view" style="min-height: 100vh;">
    <!-- Loading Overlay lúc mới vào Web -->
    <div v-if="homeStore.isLoading" class="d-flex justify-content-center align-items-center vh-100 bg-white">
      <div class="spinner-grow text-dark" style="width: 3rem; height: 3rem;" role="status"></div>
    </div>

    <!-- Nội dung chính -->
    <div v-else>
      <HeroBanner />
      
      <FeatureSection />
      
      <!-- Hiện Flash Sale nếu có chương trình -->
      <HomeFlashSale v-if="homeStore.activeFlashSale" :sale="homeStore.activeFlashSale" />

      <!-- Hiện danh sách Sản phẩm mới -->
      <HomeProductList v-if="homeStore.featuredProducts.length > 0" :products="homeStore.featuredProducts" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue';
import { useHomeStore } from '../stores/homeStore';

import HeroBanner from '../components/HeroBanner.vue';
import FeatureSection from '../components/FeatureSection.vue';
import HomeFlashSale from '../components/HomeFlashSale.vue';
import HomeProductList from '../components/HomeProductList.vue';

const homeStore = useHomeStore();

onMounted(() => {
  // Tự động load dữ liệu từ API khi vào trang
  homeStore.fetchHomeData();
});
</script>