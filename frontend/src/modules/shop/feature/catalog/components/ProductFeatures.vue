<template>
  <section class="peel-features-section">
    <div class="container">
      <h2 class="features-main-title text-center">Vì sao nên chọn LATRA?</h2>

      <div class="row align-items-center mt-5 pt-3">
        
        <div class="col-lg-4 d-flex flex-column gap-5 text-center px-4 mb-5 mb-lg-0">
          
          <div class="feature-item">
            <div class="feature-icon">
              <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" fill="currentColor" viewBox="0 0 16 16">
                <path fill-rule="evenodd" d="M11.5 8a.5.5 0 0 1-.5.5H8.5V10a.5.5 0 0 1-1 0V8.5H5a.5.5 0 0 1 0-1h2.5V6a.5.5 0 0 1 1 0v1.5H11a.5.5 0 0 1 .5.5z"/>
                <path d="M8 15A7 7 0 1 1 8 1a7 7 0 0 1 0 14zm0 1A8 8 0 1 0 8 0a8 8 0 0 0 0 16z"/>
                <path d="M4.5 8a.5.5 0 0 0 .5-.5v-3a.5.5 0 0 0-1 0v3a.5.5 0 0 0 .5.5zm7 0a.5.5 0 0 1-.5-.5v-3a.5.5 0 0 1 1 0v3a.5.5 0 0 1-.5.5z"/>
              </svg>
            </div>
            <h3 class="feature-title">THIẾT KẾ SIÊU MỎNG, CAO CẤP</h3>
            <p class="feature-desc">Chế tác tinh xảo từ khung viền kim loại nguyên khối và mặt kính cường lực, mang lại vẻ đẹp sang trọng và đẳng cấp.</p>
          </div>

          <div class="feature-item">
            <div class="feature-icon">
              <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" fill="currentColor" viewBox="0 0 16 16">
                <path d="M3 2a2 2 0 0 1 2-2h6a2 2 0 0 1 2 2v12a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V2zm6 11a1 1 0 1 0-2 0 1 1 0 0 0 2 0z"/>
              </svg>
            </div>
            <h3 class="feature-title">MÀN HÌNH SIÊU TRÀN VIỀN</h3>
            <p class="feature-desc">Trải nghiệm thị giác sống động với tấm nền OLED sắc nét, độ phân giải cao cùng tần số quét 120Hz mượt mà.</p>
          </div>

          <div class="feature-item">
            <div class="feature-icon">
              <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" fill="currentColor" viewBox="0 0 16 16">
                <path d="M4 0h8a4 4 0 0 1 4 4v8a4 4 0 0 1-4 4H4a4 4 0 0 1-4-4V4a4 4 0 0 1 4-4zm8 1.5H4A2.5 2.5 0 0 0 1.5 4v8A2.5 2.5 0 0 0 4 14.5h8a2.5 2.5 0 0 0 2.5-2.5V4A2.5 2.5 0 0 0 12 1.5z"/>
                <path d="M5.5 4a1.5 1.5 0 0 0-1.5 1.5v5A1.5 1.5 0 0 0 5.5 12h5a1.5 1.5 0 0 0 1.5-1.5v-5A1.5 1.5 0 0 0 10.5 4h-5zM5 5.5A.5.5 0 0 1 5.5 5h5a.5.5 0 0 1 .5.5v5a.5.5 0 0 1-.5.5h-5a.5.5 0 0 1-.5-.5v-5z"/>
              </svg>
            </div>
            <h3 class="feature-title">HIỆU NĂNG VƯỢT TRỘI</h3>
            <p class="feature-desc">Được trang bị vi xử lý thế hệ mới nhất, dễ dàng xử lý mọi tác vụ nặng và chiến mượt các tựa game đồ họa cao.</p>
          </div>

        </div>

        <div class="col-lg-4 d-flex justify-content-center mb-5 mb-lg-0">
          <div class="central-image-box">
            <img :src="image || 'https://via.placeholder.com/400x500?text=Smartphone'" alt="Smartphone Features" class="img-fluid tilted-phone" />
          </div>
        </div>

        <div class="col-lg-4 d-flex flex-column gap-5 text-center px-4">
          
          <div class="feature-item mt-lg-4">
            <div class="feature-icon">
              <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" fill="currentColor" viewBox="0 0 16 16">
                <path d="M15 12a1 1 0 0 1-1 1H2a1 1 0 0 1-1-1V6a1 1 0 0 1 1-1h1.172a3 3 0 0 0 2.12-.879l.83-.828A1 1 0 0 1 6.827 3h2.344a1 1 0 0 1 .707.293l.828.828A3 3 0 0 0 12.828 5H14a1 1 0 0 1 1 1v6zM2 4a2 2 0 0 0-2 2v6a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V6a2 2 0 0 0-2-2h-1.172a2 2 0 0 1-1.414-.586l-.828-.828A2 2 0 0 0 9.172 2H6.828a2 2 0 0 0-1.414.586l-.828.828A2 2 0 0 1 3.172 4H2z"/>
                <path d="M8 11a2.5 2.5 0 1 1 0-5 2.5 2.5 0 0 1 0 5zm0 1a3.5 3.5 0 1 0 0-7 3.5 3.5 0 0 0 0 7z"/>
              </svg>
            </div>
            <h3 class="feature-title">CAMERA CHUẨN STUDIO</h3>
            <p class="feature-desc">Hệ thống camera AI tiên tiến với cảm biến lớn, hỗ trợ chụp đêm xuất sắc và quay video 4K sắc nét chuẩn điện ảnh.</p>
          </div>

          <div class="feature-item">
            <div class="feature-icon">
              <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" fill="currentColor" viewBox="0 0 16 16">
                <path d="M2 6h10v4H2V6z"/>
                <path d="M2 4a2 2 0 0 0-2 2v4a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2V6a2 2 0 0 0-2-2H2zm10 1a1 1 0 0 1 1 1v4a1 1 0 0 1-1 1H2a1 1 0 0 1-1-1V6a1 1 0 0 1 1-1h10zm4 3a1.5 1.5 0 0 1-1.5 1.5v-3A1.5 1.5 0 0 1 16 8z"/>
              </svg>
            </div>
            <h3 class="feature-title">PIN KHỦNG & SẠC SIÊU NHANH</h3>
            <p class="feature-desc">Dung lượng pin lớn đồng hành cùng bạn cả ngày dài. Công nghệ sạc nhanh thông minh giúp nạp đầy năng lượng trong tích tắc.</p>
          </div>

        </div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
// Nhận link ảnh sản phẩm từ bên ngoài truyền vào
defineProps<{ image?: string }>();
</script>

<style scoped>
/* Reset font chuẩn chung của khối */
.peel-features-section {
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
  background-color: #f2f7f8; /* Nền xanh mint siêu nhạt y hệt ảnh */
  padding: 100px 0;
  color: #1a1a1a;
}

/* Tiêu đề chính lớn ở giữa */
.features-main-title {
  font-size: 38px;
  font-weight: 600;
  letter-spacing: -0.5px;
  color: #1a1a1a;
}

/* Khối từng tính năng */
.feature-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

/* Biểu tượng Icon (Màu xanh Teal đặc trưng) */
.feature-icon {
  color: #5a807c; /* Mã màu xanh rêu nhạt */
  margin-bottom: 16px;
}

/* Tiêu đề của từng tính năng */
.feature-title {
  font-size: 14px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  margin-bottom: 12px;
  color: #1a1a1a;
  line-height: 1.4;
}

/* Đoạn text mô tả */
.feature-desc {
  font-size: 14px;
  line-height: 1.6;
  color: #444444;
  margin: 0;
  max-width: 280px; /* Bóp chiều rộng lại để chữ tự rớt dòng đẹp */
}

/* Khối chứa ảnh ở giữa */
.central-image-box {
  background-color: #e5e5e5; /* Hộp nền xám nhạt */
  width: 100%;
  max-width: 360px;
  aspect-ratio: 4/5;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px; /* Hơi bo góc nhẹ */
  overflow: hidden;
}

/* Hiệu ứng nghiêng ảnh 3D để giống phối cảnh */
.tilted-phone {
  max-width: 80%;
  max-height: 80%;
  object-fit: contain;
  transform: rotate(-12deg) scale(1.1); /* Xoay nghiêng và phóng to nhẹ */
  filter: drop-shadow(-10px 15px 20px rgba(0,0,0,0.15)); /* Đổ bóng tạo khối 3D */
  transition: transform 0.5s ease;
}

.central-image-box:hover .tilted-phone {
  transform: rotate(0deg) scale(1); /* Hover vào thì đt đứng thẳng lại */
}

/* Responsive cho Mobile */
@media (max-width: 991px) {
  .peel-features-section {
    padding: 60px 0;
  }
  .features-main-title {
    font-size: 30px;
  }
  .central-image-box {
    margin: 40px auto;
  }
  .tilted-phone {
    transform: rotate(0deg) scale(1); /* Trên mobile cứ để đứng thẳng cho rõ ràng */
  }
}
</style>