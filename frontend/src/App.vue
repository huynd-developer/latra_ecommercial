<template>
  <div class="app-wrapper">
    <Header v-if="shouldShowHeaderAndFooter" />

    <main>
      <router-view />
    </main>
    <GlobalToast />

    <Footer v-if="shouldShowHeaderAndFooter" />
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'

import Header from './modules/shop/layout/Header.vue' 
import Footer from './modules/shop/layout/Footer.vue' 
import GlobalToast from './common/components/GlobalToast.vue'

const route = useRoute()

// Định nghĩa logic mới để kiểm tra xem có nên hiện Header/Footer hay không
const shouldShowHeaderAndFooter = computed(() => {
  const path = route.path.toLowerCase()
  
  // 1. Nếu là trang admin (bắt đầu bằng /admin) -> ẨN
  if (path.startsWith('/admin')) {
    return false
  }
  
  // 2. Nếu là trang đăng nhập hoặc đăng ký -> ẨN
  if (path === '/login' || path === '/register') {
    return false
  }
  
  // 3. Các trang mua sắm bình thường khác của khách -> HIỆN
  return true
})
</script>

<style>
/* Các style cũ của bạn giữ nguyên */
</style>