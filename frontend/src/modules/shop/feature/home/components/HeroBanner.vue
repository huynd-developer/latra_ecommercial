<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue';
import { useHomeStore } from '../stores/homeStore';

const homeStore = useHomeStore();
const currentIndex = ref(0);
const intervalId = ref<ReturnType<typeof setInterval> | null>(null);

const nextSlide = () => { currentIndex.value = (currentIndex.value + 1) % homeStore.banners.length; };
const goToSlide = (index: number) => { currentIndex.value = index; resetInterval(); };
const resetInterval = () => {
  if (intervalId.value) clearInterval(intervalId.value);
  intervalId.value = setInterval(nextSlide, 5000);
};

onMounted(() => { resetInterval(); });
onUnmounted(() => { if (intervalId.value) clearInterval(intervalId.value); });
</script>

<template>
  <!-- SỬ DỤNG: v-for="slide in homeStore.banners" thay vì slides. HTML và CSS giữ Y NGUYÊN BẢN CŨ CỦA BẠN -->
  <div class="hero-carousel-wrapper">
    <div class="carousel-inner-box position-relative overflow-hidden w-100 vh-100" style="max-height: 850px; min-height: 500px;">
      <div v-for="(slide, index) in homeStore.banners" :key="index"
           class="carousel-item-custom position-absolute w-100 h-100"
           :class="{ 'active': currentIndex === index }">
        <div class="bg-image w-100 h-100" :style="{ backgroundImage: `url(${slide.image})` }"></div>
        <div class="overlay position-absolute top-0 start-0 w-100 h-100 bg-dark" style="opacity: 0.35;"></div>

        <div class="content-box position-absolute top-50 start-50 translate-middle text-center w-100 px-4 z-2">
          <h1 class="text-white fw-bold mb-3 display-3 title-shadow" v-html="slide.title" style="letter-spacing: -2px; line-height: 1.1;"></h1>
          <p class="text-white fs-5 mb-5 mx-auto desc-shadow fw-light" style="max-width: 600px;">{{ slide.subtitle }}</p>

          <router-link :to="slide.link" class="btn bg-white text-dark px-5 py-3 fw-bold rounded-pill shadow-sm custom-btn">
            {{ slide.btnText }}
          </router-link>
        </div>
      </div>

      <div class="indicators-wrapper position-absolute bottom-0 start-50 translate-middle-x mb-5 d-flex gap-3 z-3">
        <button v-for="(slide, index) in homeStore.banners" :key="'ind-'+index"
                class="indicator-line border-0"
                :class="{ 'active': currentIndex === index }"
                @click="goToSlide(index)"
                aria-label="Chuyển slide">
        </button>
      </div>

    </div>
  </div>
</template>