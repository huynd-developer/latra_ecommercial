<template>
  <div class="image-gallery">
    <div class="main-image bg-light rounded-4 overflow-hidden mb-3 d-flex justify-content-center align-items-center" style="height: 500px; background-color: #f1f1f1 !important;">
      <img :src="activeImage" class="img-fluid mix-blend-multiply transition-all" style="max-height: 90%; object-fit: contain;" alt="Product Main Image">
    </div>
    
    <div class="thumbnail-list d-flex gap-2 overflow-x-auto pb-2">
      <div v-for="(img, idx) in images" :key="idx" 
           class="thumbnail-item bg-light rounded-3 overflow-hidden cursor-pointer flex-shrink-0"
           :class="{'border border-2 border-dark': activeImage === img}"
           style="width: 80px; height: 80px; background-color: #f8f9fa;"
           @click="activeImage = img">
        <img :src="img" class="img-fluid w-100 h-100 object-fit-contain mix-blend-multiply p-2">
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'

const props = defineProps<{ images: string[] }>()
const activeImage = ref(props.images[0] || '')

// Lắng nghe khi người dùng chuyển sản phẩm khác thì reset ảnh chính
watch(() => props.images, (newImages) => {
  if (newImages && newImages.length > 0) {
    // ĐÃ FIX: Thêm "|| ''" để đảm bảo luôn luôn là chuỗi (string)
    activeImage.value = newImages[0] || ''
  }
}, { immediate: true })
</script>