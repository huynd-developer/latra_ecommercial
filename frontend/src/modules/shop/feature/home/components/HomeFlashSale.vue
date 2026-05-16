<!-- frontend/src/modules/shop/feature/home/components/HomeFlashSale.vue -->
<template>
  <div v-if="sale" class="bg-dark py-5 my-5 position-relative overflow-hidden">
    <!-- Hiệu ứng tia chớp nền -->
    <div class="position-absolute top-50 start-50 translate-middle text-white opacity-10" style="font-size: 20rem; z-index: 0;">
      <i class="bi bi-lightning-charge-fill"></i>
    </div>

    <div class="container px-4 px-lg-5 position-relative z-1">
      <div class="row align-items-center mb-5">
        <div class="col-md-6 text-center text-md-start mb-4 mb-md-0">
          <span class="badge bg-danger text-white rounded-pill px-3 py-2 mb-3 fw-bold fs-6">
            <i class="bi bi-fire me-1"></i> {{ sale.status === 'UPCOMING' ? 'SẮP DIỄN RA' : 'ĐANG DIỄN RA' }}
          </span>
          <h2 class="display-5 fw-black text-white mb-2" style="letter-spacing: -1px;">{{ sale.name }}</h2>
          <p class="text-white-50 fs-5 mb-0">Nhanh tay! Số lượng giới hạn.</p>
        </div>
        
        <div class="col-md-6 d-flex justify-content-center justify-content-md-end">
          <!-- Đồng hồ đếm ngược -->
          <div class="countdown-box d-flex gap-3">
            <div class="time-block text-center">
              <div class="num bg-white text-dark rounded-3 fw-black display-6 p-3">{{ hours }}</div>
              <div class="label text-white-50 small mt-1 text-uppercase fw-bold">Giờ</div>
            </div>
            <div class="text-white fs-1 fw-bold mt-2">:</div>
            <div class="time-block text-center">
              <div class="num bg-white text-dark rounded-3 fw-black display-6 p-3">{{ minutes }}</div>
              <div class="label text-white-50 small mt-1 text-uppercase fw-bold">Phút</div>
            </div>
            <div class="text-white fs-1 fw-bold mt-2">:</div>
            <div class="time-block text-center">
              <div class="num bg-danger text-white rounded-3 fw-black display-6 p-3">{{ seconds }}</div>
              <div class="label text-white-50 small mt-1 text-uppercase fw-bold">Giây</div>
            </div>
          </div>
        </div>
      </div>

      <!-- Không cần gọi ProductCard, nút Mua Ngay sẽ link sang trang danh mục -->
      <div class="text-center mt-4">
        <router-link to="/shop" class="btn btn-light rounded-pill px-5 py-3 fw-bold fs-5 shadow hover-scale transition-all">
          XEM TẤT CẢ DEALS <i class="bi bi-arrow-right ms-2"></i>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed } from 'vue';
import type { FlashSale } from '@/modules/admin/feature/promotion/types/promotion.type';

const props = defineProps<{ sale: FlashSale | null }>();

const hours = ref('00');
const minutes = ref('00');
const seconds = ref('00');
let timer: ReturnType<typeof setInterval>;

const targetTime = computed(() => {
  if (!props.sale) return 0;
  return new Date(props.sale.status === 'UPCOMING' ? props.sale.startTime : props.sale.endTime).getTime();
});

const updateCountdown = () => {
  const now = new Date().getTime();
  const distance = targetTime.value - now;

  if (distance < 0) {
    clearInterval(timer);
    return;
  }

  hours.value = Math.floor((distance % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60)).toString().padStart(2, '0');
  minutes.value = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60)).toString().padStart(2, '0');
  seconds.value = Math.floor((distance % (1000 * 60)) / 1000).toString().padStart(2, '0');
};

onMounted(() => {
  updateCountdown();
  timer = setInterval(updateCountdown, 1000);
});

onUnmounted(() => { clearInterval(timer); });
</script>

<style scoped>
.fw-black { font-weight: 900 !important; }
.time-block .num { width: 80px; box-shadow: 0 10px 20px rgba(0,0,0,0.3); }
.hover-scale:hover { transform: scale(1.05); }
.transition-all { transition: all 0.3s ease; }
</style>