<!-- <template>
  <div class="container mt-5 text-center">
    <h1 class="fw-bold">Trang Chủ LATRA</h1>
    <p class="text-muted">Xin chào! Hãy chọn "Cases > Apple iPhone 17 Pro Max" trên menu để xem thử.</p>
    
    <div class="mt-4">
      <img src="https://placehold.co/800x400/f8f9fa/333333?text=Banner+Trang+Chủ" alt="Banner" class="img-fluid rounded shadow-sm">
    </div>
  </div>
</template>

<script setup lang="ts">
// Logic trang chủ ở đây
</script> -->

<template>
  <div class="home-view">
    <HeroBanner />
    <FeatureSection />
  </div>
</template>

<script setup lang="ts">
import HeroBanner from '../components/home/HeroBanner.vue'
import FeatureSection from '../components/home/FeatureSection.vue'
</script>