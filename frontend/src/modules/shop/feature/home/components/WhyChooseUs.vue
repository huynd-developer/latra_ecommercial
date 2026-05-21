<script setup lang="ts">
// Dữ liệu tĩnh cho 3 tiêu chí bán điện thoại, chuẩn chỉnh như ảnh mẫu
const features = [
  {
    id: 1,
    title: 'Bảo mật tuyệt đối',
    description: 'Dữ liệu của bạn luôn được mã hóa và bảo vệ an toàn với chip bảo mật chuyên biệt.',
    // Icon Khiên (Shield)
    svgPath: 'M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z'
  },
  {
    id: 2,
    title: 'Sạc siêu tốc 120W',
    description: 'Nạp đầy 50% pin chỉ trong vòng 10 phút. Luôn sẵn sàng cho ngày dài năng động.',
    // Icon Tia sét (Lightning)
    svgPath: 'M13 2L3 14h9l-1 8 10-12h-9l1-8z'
  },
  {
    id: 3,
    title: 'Bảo hành 2 năm',
    description: 'Yên tâm sử dụng với chính sách bảo hành chính hãng 24 tháng và 1 đổi 1 trong 30 ngày.',
    // Icon Chữ i / Thông tin bảo hành
    svgPath: 'M12 22c5.523 0 10-4.477 10-10S17.523 2 12 2 2 6.477 2 12s4.477 10 10 10zm-1-11h2v6h-2v-6zm0-4h2v2h-2V7z'
  }
]
</script>

<template>
  <section class="why-choose-us py-5">
    <div class="container custom-container">
      <h2 class="section-title text-center">Tại sao chọn LATRA?</h2>
      
      <div class="features-grid">
        <div 
          v-for="feature in features" 
          :key="feature.id" 
          class="feature-item"
        >
          <div class="icon-wrapper">
            <svg 
              xmlns="http://www.w3.org/2000/svg" 
              width="24" 
              height="24" 
              viewBox="0 0 24 24" 
              fill="none" 
              stroke="currentColor" 
              stroke-width="2" 
              stroke-linecap="round" 
              stroke-linejoin="round"
            >
              <path :d="feature.svgPath" />
            </svg>
          </div>
          <h3 class="feature-title">{{ feature.title }}</h3>
          <p class="feature-desc">{{ feature.description }}</p>
        </div>
      </div>
    </div>
  </section>
</template>

<style scoped>
.why-choose-us {
  background-color: #ffffff;
  padding-top: 80px;
  padding-bottom: 80px;
  border-bottom: 1px solid #eaeaea; /* Đường line mỏng chia cách các section */
}

.custom-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.section-title {
  font-size: 2rem;
  font-weight: 700;
  color: #111111;
  margin-bottom: 60px;
  letter-spacing: -0.5px;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 40px;
}

.feature-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.icon-wrapper {
  width: 64px;
  height: 64px;
  background-color: #f5f5f7; /* Màu xám nhạt cực sang */
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 24px;
  color: #111111;
}

.feature-title {
  font-size: 1.15rem;
  font-weight: 700;
  color: #111111;
  margin-bottom: 12px;
}

.feature-desc {
  font-size: 0.95rem;
  color: #666666;
  line-height: 1.6;
  max-width: 300px;
  font-weight: 400;
}

/* Responsive cho màn hình nhỏ */
@media (max-width: 991px) {
  .features-grid {
    grid-template-columns: repeat(1, 1fr);
    gap: 50px;
  }
  .section-title {
    margin-bottom: 40px;
  }
}
</style>