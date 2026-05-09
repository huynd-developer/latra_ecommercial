<!-- <template>
  <div>
    <Header />

    <main>
      <router-view />
    </main>
  </div>
</template>

<script setup lang="ts">
import Header from './components/layout/Header.vue';
</script> -->

<template>
  <div class="app-wrapper">
    <Header v-if="!isAdminPage" />

    <main>
      <router-view />
    </main>

    <Footer v-if="!isAdminPage" />
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
// Đảm bảo bạn đã import Header và Footer đúng đường dẫn của bạn nhé
import Header from './components/layout/Header.vue' 
import Footer from './components/layout/Footer.vue' 

const route = useRoute()

// Biến kiểm tra xem đường dẫn hiện tại có bắt đầu bằng '/admin' không
const isAdminPage = computed(() => {
  return route.path.toLowerCase().startsWith('/admin')
})
</script>

<style>
/* Các style cũ của bạn giữ nguyên */
</style>