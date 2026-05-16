<template>
  <div class="modern-layout d-flex flex-column" style="font-family: 'Inter', system-ui, sans-serif; background-color: #f8fafc; min-height: 100vh;">

    <TopHeader :activeTab="activeTab" @changeTab="switchTab" />

    <main class="flex-grow-1 d-flex flex-column position-relative container-fluid px-4 pb-4 pt-4">
      
      <router-view v-slot="{ Component }">
        <transition name="fade-slide" mode="out-in">
          <component :is="Component" :key="route.path" />
        </transition>
      </router-view>

    </main>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import TopHeader from './TopHeader.vue'

// XÓA SẠCH các import EmployeeManager, CustomerManager... ở đây!
// Vue Router (trong file router/index.ts) đã lo việc tải file rồi.

const router = useRouter()
const route = useRoute()

// Lấy tên tab hiện tại từ thanh URL (Ví dụ: localhost:5173/admin/employees -> activeTab = 'employees')
const activeTab = computed(() => {
  const pathParts = route.path.split('/')
  return pathParts[pathParts.length - 1] || 'dashboard'
})

// Khi người dùng bấm vào Menu trên TopHeader, ta bắt Router đổi đường dẫn
const switchTab = (t: string) => { 
  // Biến t có thể là 'employees', 'customers', 'dashboard'...
  // Dùng router.push để đổi URL, giao diện sẽ tự động chuyển trang!
  router.push(`/admin/${t}`)
}
</script>

<style scoped>
.fade-slide-enter-active, .fade-slide-leave-active { transition: opacity 0.3s, transform 0.3s; }
.fade-slide-enter-from { opacity: 0; transform: translateY(10px); }
.fade-slide-leave-to { opacity: 0; transform: translateY(-10px); }
</style>