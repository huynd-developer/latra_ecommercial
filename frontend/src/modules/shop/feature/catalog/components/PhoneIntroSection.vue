<template>
  <div class="phone-features-container container my-5 py-5">
    <div
      v-for="(item, index) in featureItems"
      :key="item.title"
      class="row align-items-center mb-5"
    >
      <div
        :class="['col-md-6', index % 2 === 0 ? 'text-start text-md-start pr-md-5' : 'text-start text-md-end pl-md-5 order-md-2']"
      >
        <h3 class="feature-title fw-bold mb-3">{{ item.title }}</h3>
        <p class="feature-desc text-muted">{{ item.description }}</p>
      </div>

      <div
        :class="['col-md-6', index % 2 === 0 ? 'pl-md-5 text-md-end text-center' : 'pr-md-5 text-md-start text-center order-md-1']"
      >
        <div class="image-box p-4 rounded-lg bg-light d-flex align-items-center justify-content-center">
          <img :src="image" :alt="item.title" class="feature-image img-fluid" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';

// Nhận prop image cho hình ảnh điện thoại chính (ví dụ từ image_5.png)
defineProps<{
  image: string;
}>();

const featureItems = ref([
  {
    title: 'Hiệu suất đỉnh cao, chơi game mượt mà.',
    description: 'Được trang bị vi xử lý thế hệ mới nhất và RAM dung lượng lớn, điện thoại xử lý mọi tác vụ nặng một cách dễ dàng, từ đa nhiệm phức tạp đến chơi game đồ họa cao, đảm bảo trải nghiệm không độ trễ.',
  },
  {
    title: 'Hệ thống camera chuyên nghiệp, chụp ảnh như nhiếp ảnh gia.',
    description: 'Với bộ ba camera độ phân giải cao và công nghệ xử lý ảnh AI tiên tiến, bạn có thể ghi lại mọi khoảnh khắc với độ chi tiết kinh ngạc, từ phong cảnh hùng vĩ đến chân dung sắc nét, ngay cả trong điều kiện ánh sáng yếu.',
  },
  {
    title: 'Pin dung lượng lớn, sạc nhanh cấp tốc.',
    description: 'Sở hữu viên pin bền bỉ cho cả ngày dài sử dụng và công nghệ sạc nhanh thông minh, bạn sẽ không bao giờ phải lo lắng về việc hết pin giữa chừng, giúp bạn luôn kết nối và làm việc hiệu quả.',
  },
]);
</script>

<style scoped>
.phone-features-container {
  font-family: Arial, sans-serif; /* Phông chữ tối giản PEEL */
}

.feature-title {
  font-size: 1.5rem;
  line-height: 1.2;
  color: #1a1a1a;
  font-weight: 700;
}

.feature-desc {
  font-size: 1rem;
  line-height: 1.6;
}

.image-box {
  background-color: #f5f5f5; /* Màu nền xám nhạt từ PEEL */
  border-radius: 8px; /* Bo góc nhẹ PEEL */
  box-shadow: 0 4px 6px rgba(0,0,0,0.05); /* Bóng nhẹ */
  min-height: 300px;
}

.feature-image {
  max-height: 250px;
  object-fit: contain;
  /* Giữ góc nghiêng của hình ảnh điện thoại để duy trì sự năng động của bố cục gốc */
  /* Nếu hình ảnh điện thoại của bạn đã nghiêng sẵn, hãy bỏ transform này */
  /* transform: rotate(10deg); */
}

/* responsive tinh chỉnh cho col order */
@media (max-width: 767px) {
  .feature-items .col-md-6 {
    text-align: center !important;
    order: unset !important;
    padding-left: 15px !important;
    padding-right: 15px !important;
  }
}
</style>