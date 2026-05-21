<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue';
import { useHomeStore } from '../stores/homeStore';

const homeStore = useHomeStore();
const currentIndex = ref(0);
const intervalId = ref<ReturnType<typeof setInterval> | null>(null);

// Đặt thời gian chạy slide cố định là 5000ms (5 giây) để đồng bộ với CSS Animation của thanh tiến trình
const slideDuration = 5000; 

const nextSlide = () => { currentIndex.value = (currentIndex.value + 1) % homeStore.banners.length; };
const goToSlide = (index: number) => { currentIndex.value = index; resetInterval(); };
const resetInterval = () => {
  if (intervalId.value) clearInterval(intervalId.value);
  intervalId.value = setInterval(nextSlide, slideDuration);
};

onMounted(() => { resetInterval(); });
onUnmounted(() => { if (intervalId.value) clearInterval(intervalId.value); });
</script>

<template>
  <div class="hero-carousel-wrapper">
    <div class="carousel-inner-box position-relative overflow-hidden w-100 vh-100" style="max-height: 800px; min-height: 500px;">
      
      <div v-for="(slide, index) in homeStore.banners" :key="index"
           class="carousel-item-custom position-absolute w-100 h-100"
           :class="{ 'active': currentIndex === index }">
        
        <div class="bg-image w-100 h-100" :style="{ backgroundImage: `url(${slide.image})` }"></div>

        <div class="content-box position-absolute top-50 start-50 translate-middle text-center w-100 px-4 z-2">
          <h1 class="hero-title mb-3" v-html="slide.title"></h1>
          <p class="hero-subtitle mb-4 mx-auto">{{ slide.subtitle }}</p>

          <router-link :to="slide.link" class="btn peel-btn text-uppercase">
            {{ slide.btnText || 'SHOP NOW' }}
          </router-link>
        </div>
      </div>

      <div class="indicators-wrapper position-absolute bottom-0 start-50 translate-middle-x mb-4 d-flex align-items-center gap-2 z-3">
        <button v-for="(slide, index) in homeStore.banners" :key="'ind-'+index"
                class="indicator-dot border-0 p-0"
                :class="{ 'active': currentIndex === index }"
                @click="goToSlide(index)"
                aria-label="Chuyển slide">
          
          <div v-if="currentIndex === index" class="progress-fill"></div>
          
        </button>
      </div>

    </div>
  </div>
</template>

<style scoped>
.carousel-item-custom {
  opacity: 0;
  transition: opacity 0.8s ease-in-out;
  pointer-events: none;
}
.carousel-item-custom.active {
  opacity: 1;
  pointer-events: auto;
}

/* Cấu hình ảnh nền + LỚP PHỦ ĐEN MỜ */
.bg-image {
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  position: relative;
}

.bg-image::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.35); /* Đen mờ 35% giúp chữ nổi bật */
  z-index: 1;
}

/* Typography phong cách cao cấp - MÀU TRẮNG */
.hero-title {
  color: #ffffff; /* Chữ trắng */
  font-weight: 700;
  font-size: 3.5rem;
  line-height: 1.15;
  letter-spacing: -1px;
  position: relative; /* Quan trọng để nằm trên lớp phủ .bg-image::after */
}

:deep(.accent-line) {
  color: #e0e0e0; /* Xám nhạt cho dòng chữ nhấn nếu có */
}

.hero-subtitle {
  color: #f0f0f0; /* Trắng xám nhẹ */
  font-size: 1.15rem;
  max-width: 550px;
  font-weight: 400;
  line-height: 1.5;
  position: relative;
}

/* Button NỀN TRẮNG, CHỮ ĐEN */
.peel-btn {
  background-color: #ffffff !important;
  color: #000000 !important;
  border: none !important;
  border-radius: 0px !important;
  padding: 14px 40px !important;
  font-size: 13px !important;
  font-weight: 600 !important;
  letter-spacing: 2px !important;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2) !important;
  transition: all 0.2s ease-in-out !important;
  position: relative;
}

.peel-btn:hover {
  background-color: #eeeeee !important; 
  transform: translateY(-2px);
}

/* =========================================
   HIỆU ỨNG CHẠY THANH MÀU ĐEN (Viên thuốc)
   ========================================= */

.indicators-wrapper {
  gap: 12px !important; 
}

/* Trạng thái chấm xám tĩnh khi không được chọn */
.indicator-dot {
  width: 10px;
  height: 10px;
  background-color: rgba(255, 255, 255, 0.5); /* Chấm mờ trên nền ảnh */
  border-radius: 50%;
  position: relative;
  overflow: hidden;
  transition: all 0.4s ease;
  cursor: pointer;
}

/* Trạng thái active: Biến hình tròn thành viên thuốc dài */
.indicator-dot.active {
  width: 45px;
  border-radius: 10px; 
  background-color: rgba(255, 255, 255, 0.5);
}

/* Thanh tiến trình chạy trong viên thuốc */
.progress-fill {
  position: absolute;
  top: 0;
  left: 0;
  height: 100%;
  background-color: #000000; /* MÀU ĐEN chạy dần như mẫu của ông */
  border-radius: 10px;
  width: 0%;
  animation: fillBlack 5s linear forwards; 
}

@keyframes fillBlack {
  0% { width: 0%; }
  100% { width: 100%; }
}

@media (max-width: 768px) {
  .hero-title { font-size: 2.2rem; }
  .hero-subtitle { font-size: 1rem; }
  .peel-btn { padding: 12px 30px !important; font-size: 11px !important; }
}
</style>