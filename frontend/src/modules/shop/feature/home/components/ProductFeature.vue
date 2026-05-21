<script setup lang="ts">
// Đã sửa lại nội dung mặc định để đánh mạnh vào thiết kế và sự sang trọng
defineProps({
  title: {
    type: String,
    default: 'Thiết kế độc bản.<br><span class="accent-line-light">Đẳng cấp thượng lưu.</span>'
  },
  subtitle: {
    type: String,
    default: 'Từng đường nét được trau chuốt tỉ mỉ, kết hợp với vật liệu cao cấp và màu sắc tinh tế, tạo nên một tuyệt tác công nghệ không chỉ mạnh mẽ mà còn vô cùng sang trọng, khẳng định vị thế của người sở hữu.'
  },
  btnText: {
    type: String,
    default: 'KHÁM PHÁ THIẾT KẾ'
  },
  link: {
    type: String,
    default: '/products/latra-x-pro' // Link đến trang chi tiết sản phẩm
  },
  imageSrc: {
    type: String,
    // Ảnh điện thoại màu đen trên nền tối để tạo sự huyền bí, sang trọng
    default: 'https://www.didongmy.com/vnt_upload/news/08_2024/iphone-17-didongmy.jpg' 
  }
})
</script>

<template>
  <section class="black-feature-section">
    <div class="feature-container">
      
      <div class="content-col-black">
        <div class="text-wrapper">
          <h2 class="feature-title-light" v-html="title"></h2>
          <p class="feature-subtitle-light">{{ subtitle }}</p>
          
          <router-link :to="link" class="peel-btn-light text-uppercase">
            {{ btnText }} &rarr;
          </router-link>
        </div>
      </div>

      <div class="image-col">
        <div class="bleed-image" :style="{ backgroundImage: `url(${imageSrc})` }"></div>
      </div>

    </div>
  </section>
</template>

<style scoped>
.black-feature-section {
  width: 100%;
  background-color: #000000; /* Nền đen hoàn toàn */
  overflow: hidden;
  border-top: 1px solid #1a1a1a; /* Đường line mỏng cực tối */
  border-bottom: 1px solid #1a1a1a;
}

.feature-container {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  min-height: 650px;
  height: 80vh;
  max-height: 800px;
}

/* Xử lý cột chữ nền đen */
.content-col-black {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px 12%; /* Tăng padding để chữ gọn hơn */
  background-color: #000000;
}

.text-wrapper {
  max-width: 480px;
}

.feature-title-light {
  color: #fafafa; /* Chữ gần như trắng */
  font-size: 2.8rem;
  font-weight: 700;
  line-height: 1.2;
  letter-spacing: -1px;
  margin-bottom: 24px;
}

/* Custom class cho v-html trên nền tối */
:deep(.accent-line-light) {
  color: #bbbbbb; /* Màu xám trung tính cho dòng phụ */
  font-weight: 400;
}

.feature-subtitle-light {
  color: #cccccc; /* Màu chữ subtitle hơi xám để dịu mắt */
  font-size: 1.05rem;
  line-height: 1.6;
  margin-bottom: 35px;
  font-weight: 400;
}

/* Button trắng chữ đen cực kỳ nổi bật kiểu Minimalist */
.peel-btn-light {
  display: inline-block;
  background-color: #ffffff; /* Nền trắng */
  color: #000000; /* Chữ đen */
  text-decoration: none;
  padding: 14px 36px;
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 2px;
  box-shadow: 0 4px 14px rgba(255, 255, 255, 0.1); /* Bóng mờ màu trắng nhẹ */
  transition: all 0.2s ease-in-out;
}

.peel-btn-light:hover {
  background-color: #eeeeee;
  transform: translateX(3px);
  box-shadow: 0 6px 20px rgba(255, 255, 255, 0.15);
}

/* Xử lý cột ảnh tràn viền */
.image-col {
  width: 100%;
  height: 100%;
}

.bleed-image {
  width: 100%;
  height: 100%;
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
}

/* Responsive cho điện thoại (Xếp chồng thành 1 cột) */
@media (max-width: 991px) {
  .feature-container {
    grid-template-columns: 1fr;
    height: auto;
    max-height: none;
  }
  
  .content-col-black {
    padding: 60px 24px;
    order: 2; /* Chữ xuống dưới */
  }
  
  .image-col {
    height: 400px;
    order: 1; /* Ảnh lên trên */
  }

  .feature-title-light {
    font-size: 2.1rem;
  }
}
</style>