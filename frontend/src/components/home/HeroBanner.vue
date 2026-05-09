<template>
  <div class="hero-carousel-wrapper">
    <div class="carousel-inner-box position-relative overflow-hidden w-100 vh-100" style="max-height: 850px; min-height: 500px;">
      
      <div v-for="(slide, index) in slides" :key="index"
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
        <button v-for="(slide, index) in slides" :key="'ind-'+index"
                class="indicator-line border-0"
                :class="{ 'active': currentIndex === index }"
                @click="goToSlide(index)"
                aria-label="Chuyển slide">
        </button>
      </div>

    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'

const currentIndex = ref(0)
const intervalId = ref<ReturnType<typeof setInterval> | null>(null)

// Dữ liệu các Slide
const slides = ref([
  {
    image: 'https://int.buypeel.com/cdn/shop/files/Base_Station_Hero_2_2x_8eb14032-17a4-44df-b4d6-84e1fc3f59fa.jpg?v=1730999516&width=2000',
    title: 'Một Bộ Sạc.<br>Không Còn Lộn Xộn.',
    subtitle: 'Trạm sạc 3-trong-1 siêu gọn gàng cho điện thoại, đồng hồ và tai nghe của bạn.',
    btnText: 'MUA NGAY',
    link: '/accessories'
  },
  {
    image: 'https://int.buypeel.com/cdn/shop/files/peel_home_hero_desktop_1.jpg?v=1725916053&width=2000',
    title: 'Ốp Lưng Siêu Mỏng.<br>Bảo Vệ Tối Ưu.',
    subtitle: 'Giữ nguyên vẻ đẹp nguyên bản của thiết bị với ốp lưng siêu mỏng LATRA.',
    btnText: 'XEM ỐP LƯNG',
    link: '/shop-all'
  },
  {
    image: 'https://int.buypeel.com/cdn/shop/files/i16_Glass_Features.png?v=1743691200&width=2000',
    title: 'Kính Cường Lực<br>Trong Suốt Hoàn Hảo.',
    subtitle: 'Bảo vệ màn hình tràn viền, mang lại cảm giác vuốt chạm như màn hình gốc.',
    btnText: 'MUA KÍNH CƯỜNG LỰC',
    link: '/iphone-protectors'
  }
])

const nextSlide = () => {
  currentIndex.value = (currentIndex.value + 1) % slides.value.length
}

const goToSlide = (index: number) => {
  currentIndex.value = index
  resetInterval() 
}

const resetInterval = () => {
  if (intervalId.value) clearInterval(intervalId.value)
  intervalId.value = setInterval(nextSlide, 5000)
}

onMounted(() => {
  intervalId.value = setInterval(nextSlide, 5000)
})

onUnmounted(() => {
  if (intervalId.value) clearInterval(intervalId.value)
})
</script>

<style scoped>
.hero-carousel-wrapper {
  font-family: 'Inter', -apple-system, sans-serif;
}

.bg-image {
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  transition: transform 7s ease; /* Zoom siêu mượt */
}

/* HIỆU ỨNG FADE CỦA SLIDE */
.carousel-item-custom {
  opacity: 0;
  transition: opacity 1s ease-in-out;
  pointer-events: none;
}

.carousel-item-custom.active {
  opacity: 1;
  pointer-events: auto;
}

/* Hiệu ứng zoom background khi active */
.carousel-item-custom.active .bg-image {
  transform: scale(1.05); 
}

/* TEXT SHADOW ĐỂ CHỮ NỔI BẬT */
.title-shadow { text-shadow: 0 4px 12px rgba(0, 0, 0, 0.3); }
.desc-shadow { text-shadow: 0 2px 8px rgba(0, 0, 0, 0.4); }

/* NÚT BẤM */
.custom-btn {
  font-size: 0.9rem;
  letter-spacing: 1.5px;
  transition: all 0.3s ease;
}
.custom-btn:hover {
  background-color: #f1f1f1 !important;
  transform: translateY(-2px);
}

/* INDICATORS DẠNG GẠCH NGANG */
.indicator-line {
  width: 35px;
  height: 3px;
  background-color: rgba(255, 255, 255, 0.4);
  padding: 0;
  transition: all 0.4s ease;
  cursor: pointer;
}

.indicator-line:hover {
  background-color: rgba(255, 255, 255, 0.8);
}

.indicator-line.active {
  background-color: #ffffff;
  width: 50px; /* Dài ra một chút khi active */
}

/* Responsive Mobile */
@media (max-width: 768px) {
  .carousel-inner-box {
    height: 60vh !important;
    min-height: 400px;
  }
  .display-3 {
    font-size: 2.5rem;
  }
}
</style>