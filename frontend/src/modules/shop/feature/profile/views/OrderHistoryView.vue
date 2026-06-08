<template>
  <div class="py-5" style="background-color: #f8f9fa; min-height: 100vh;">
    <div class="container max-w-5xl mx-auto">
      <h2 class="fw-bold mb-4">Lịch sử đơn hàng</h2>
      
      <div class="card border-0 shadow-sm rounded-4 p-5 text-center">
        <i class="bi bi-box-seam text-secondary mb-3" style="font-size: 4rem;"></i>
        <h5 class="fw-bold">Bạn chưa có đơn hàng nào hoặc dữ liệu đang tải...</h5>
        <p class="text-muted">Tính năng hiển thị chi tiết đơn hàng đang được cập nhật.</p>
        <router-link to="/" class="btn btn-dark rounded-pill px-4 mt-3">Tiếp tục mua sắm</router-link>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
// Chỗ này sau này mày sẽ gọi API: api.get('/orders/my-orders') để lấy danh sách đổ ra UI
</script>