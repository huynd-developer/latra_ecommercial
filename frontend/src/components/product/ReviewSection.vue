<template>
  <div class="review-section">
    <div class="text-center mb-5">
      <h2 class="fw-bold text-dark display-6" style="letter-spacing: -1px;">Đánh Giá Từ Khách Hàng</h2>
      <p class="text-secondary">Hàng ngàn khách hàng đã tin dùng sản phẩm của LATRA.</p>
    </div>
    
    <div class="row justify-content-center">
      <div class="col-md-10 col-lg-8">
        <div class="review-card p-4 bg-light rounded-4 mb-4 shadow-sm" v-for="i in 3" :key="i">
          <div class="d-flex justify-content-between align-items-start mb-3">
            <div>
              <div class="fw-bold text-dark fs-5">Khách hàng ẩn danh {{ i }}</div>
              <div class="small text-muted">Đã mua hàng</div>
            </div>
            <div class="text-warning fs-5">
              <i class="bi bi-star-fill" v-for="s in 5" :key="s"></i>
            </div>
          </div>
          <p class="text-secondary mb-0 lh-lg" style="font-size: 1.05rem;">
            Sản phẩm rất đẹp, đóng gói cẩn thận. Mình xài qua rất nhiều loại rồi nhưng loại này cầm trên tay cực kỳ thích, mỏng nhẹ đúng như quảng cáo! Đáng đồng tiền bát gạo!
          </p>
        </div>
      </div>
    </div>
  </div>
</template>