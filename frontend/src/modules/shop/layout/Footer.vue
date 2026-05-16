<template>
  <footer class="footer-section text-white">
    <div class="container-fluid px-lg-5 pb-5 pt-5 mt-4">
      
      <div class="row justify-content-between mb-4">
        
        <div class="col-lg-5 pe-lg-5 mb-5 mb-lg-0">
          <h2 class="footer-title">Đăng ký nhận bản tin</h2>
          <p class="footer-subtitle">Hãy là người đầu tiên biết về các bộ sưu tập mới và ưu đãi độc quyền.</p>
          
          <div class="email-input-group">
            <input type="email" placeholder="Email" class="form-control rounded-pill email-input">
            <button class="btn email-submit-btn"><i class="bi bi-chevron-right"></i></button>
          </div>
          
          <p class="terms-text">
            Bằng việc đăng ký, bạn đồng ý với <a href="#">Điều khoản sử dụng</a> & <a href="#">Chính sách bảo mật</a>.
          </p>
        </div>

        <div class="col-lg-3 ms-auto">
          <h6 class="footer-title-sm">Liên kết nhanh</h6>
          <ul class="list-unstyled footer-links mb-0">
            <li><a href="#">Về chúng tôi</a></li>
            <li><a href="#">Tại sao chọn Peel?</a></li>
            <li><a href="#">Đăng ký giao hàng miễn phí PEEL+</a></li>
            <li><a href="#">Trợ giúp / Câu hỏi thường gặp</a></li>
            <li><a href="#">Chính sách bảo hành</a></li>
            <li><a href="#">Điều khoản dịch vụ</a></li>
            <li><a href="#">Chính sách bảo mật</a></li>
          </ul>
        </div>
      </div>

      <hr class="footer-divider">

      <div class="row align-items-end pt-3">
        <div class="col-lg-6 mb-4 mb-lg-0">
          <div class="country-selector">
            <span class="cursor-pointer">
              <span class="text-danger star-icon">★</span> Việt Nam (USD $) <i class="bi bi-chevron-down ms-1 icon-sm"></i>
            </span>
          </div>
          <p class="copyright-text">
            © 2026 Peel. <a href="#">Nền tảng thương mại điện tử bởi Shopify</a>
          </p>
          <button class="btn rounded-pill d-inline-flex align-items-center follow-shop-btn">
             <i class="bi bi-heart-fill me-2"></i> Theo dõi trên shop
          </button>
        </div>
        
        <div class="col-lg-6 text-lg-end d-flex flex-column align-items-lg-end justify-content-end">
          <div class="social-icons">
            <a href="#"><i class="bi bi-facebook"></i></a>
            <a href="#"><i class="bi bi-instagram"></i></a>
          </div>
          <div class="payment-icons d-flex gap-1 justify-content-lg-end flex-wrap">
            <span class="pay-badge">AMEX</span>
            <span class="pay-badge">Pay</span>
            <span class="pay-badge">Diners</span>
            <span class="pay-badge">Discover</span>
            <span class="pay-badge">G Pay</span>
            <span class="pay-badge">Master</span>
            <span class="pay-badge">PayPal</span>
            <span class="pay-badge bg-shop text-white">shop</span>
            <span class="pay-badge">VISA</span>
          </div>
        </div>
      </div>

    </div>
  </footer>
</template>

<script setup lang="ts">
// Không cần viết gì ở đây nếu Footer chỉ là giao diện tĩnh
</script>

<style scoped>
.footer-section { 
  background-color: #000; 
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
  padding-top: 60px;
}

.footer-title {
  font-weight: 700;
  font-size: 32px;
  letter-spacing: -0.5px;
  margin-bottom: 16px;
  color: #fff;
}
.footer-subtitle {
  font-size: 14.5px;
  font-weight: 400;
  margin-bottom: 24px;
  color: #fff;
}
.footer-title-sm {
  font-weight: 700;
  font-size: 15px;
  margin-bottom: 24px;
  color: #fff;
}

.email-input-group { 
  position: relative; 
  max-width: 360px; 
  margin-bottom: 18px;
}
.email-input { 
  height: 48px;
  padding: 0 20px; 
  font-size: 14.5px; 
  border: none; 
}
.email-input:focus { 
  box-shadow: 0 0 0 2px #fff; 
  outline: none; 
}
.email-submit-btn { 
  position: absolute; 
  right: 4px; 
  top: 50%; 
  transform: translateY(-50%); 
  border: none; 
  background: transparent; 
  color: #000; 
  font-size: 15px; 
  padding: 0 12px;
}

.terms-text {
  font-size: 12px;
  color: #888; 
}
.terms-text a {
  color: #888;
  text-decoration: underline;
  text-underline-offset: 2px;
}
.terms-text a:hover {
  color: #fff;
}

.footer-links {
  display: flex;
  flex-direction: column;
  gap: 16px; 
}
.footer-links a { 
  color: #fff; 
  text-decoration: none; 
  font-size: 13.5px;
  transition: opacity 0.2s; 
}
.footer-links a:hover { 
  text-decoration: underline; 
  text-underline-offset: 3px;
}

.footer-divider {
  border-color: #222; 
  opacity: 1;
  margin: 40px 0;
}

.country-selector {
  font-size: 13.5px;
  font-weight: 600;
  color: #fff;
}
.star-icon {
  font-size: 14px;
  margin-right: 4px;
}
.icon-sm {
  font-size: 10px;
}

.copyright-text {
  font-size: 12px;
  color: #888; 
  margin: 24px 0;
}
.copyright-text a {
  color: #888;
  text-decoration: none;
}
.copyright-text a:hover {
  text-decoration: underline;
  color: #fff;
}

.follow-shop-btn {
  background: #5a31f4; 
  color: white;
  font-size: 13.5px;
  font-weight: 600;
  padding: 6px 16px;
}
.follow-shop-btn i {
  font-size: 13px;
}
.follow-shop-btn:hover {
  background: #4b26d3;
  color: white;
}

.social-icons {
  margin-bottom: 24px;
}
.social-icons a {
  color: #fff;
  font-size: 20px;
  margin-left: 20px;
  transition: opacity 0.2s;
}
.social-icons a:hover {
  opacity: 0.7;
}

.pay-badge { 
  background: #fff;
  color: #000;
  font-size: 9px; 
  font-weight: 700; 
  padding: 4px 6px; 
  border-radius: 3px; 
  letter-spacing: 0.5px;
}
.bg-shop {
  background-color: #5a31f4;
}
</style>

