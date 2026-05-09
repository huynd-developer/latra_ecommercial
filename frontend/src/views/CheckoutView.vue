<!-- <template>
  <div class="checkout-page py-5" style="font-family: 'Inter', sans-serif; background-color: #f8f9fa; min-height: 100vh;">
    <div class="container px-4 px-lg-5">
      <div class="text-center mb-5">
        <h1 class="display-5 fw-bold text-dark" style="letter-spacing: -1px;">Thanh Toán</h1>
        <p class="text-secondary">Hoàn tất đơn hàng của bạn một cách an toàn.</p>
      </div>

      <div class="row gx-5">
        <div class="col-lg-7 mb-5 mb-lg-0">
          <div class="bg-white p-4 p-lg-5 rounded-4 shadow-sm mb-4">
            <h4 class="fw-bold mb-4">Thông tin giao hàng</h4>
            <form @submit.prevent="handleCheckout">
              <div class="row g-3">
                <div class="col-md-6">
                  <label class="form-label small fw-medium text-secondary">Họ và tên</label>
                  <input type="text" class="form-control py-2" placeholder="Nguyễn Văn A" required>
                </div>
                <div class="col-md-6">
                  <label class="form-label small fw-medium text-secondary">Số điện thoại</label>
                  <input type="tel" class="form-control py-2" placeholder="090 123 4567" required>
                </div>
                <div class="col-12">
                  <label class="form-label small fw-medium text-secondary">Địa chỉ nhận hàng</label>
                  <input type="text" class="form-control py-2" placeholder="Số nhà, Tên đường, Phường/Xã..." required>
                </div>
              </div>

              <hr class="my-5 opacity-25">

              <h4 class="fw-bold mb-4">Phương thức thanh toán</h4>
              <div class="form-check mb-3 p-3 border rounded-3 bg-light">
                <input class="form-check-input ms-1" type="radio" name="payment" id="cod" checked>
                <label class="form-check-label ms-3 fw-medium" for="cod">
                  Thanh toán khi nhận hàng (COD)
                </label>
              </div>
              <div class="form-check p-3 border rounded-3">
                <input class="form-check-input ms-1" type="radio" name="payment" id="card">
                <label class="form-check-label ms-3 fw-medium text-muted" for="card">
                  Thẻ tín dụng / Ghi nợ (Đang bảo trì)
                </label>
              </div>

              <button type="submit" class="btn btn-dark w-100 py-3 mt-5 rounded-pill fw-bold fs-5 shadow-lg">
                XÁC NHẬN ĐẶT HÀNG
              </button>
            </form>
          </div>
        </div>

        <div class="col-lg-5">
          <div class="bg-white p-4 p-lg-5 rounded-4 shadow-sm sticky-top" style="top: 100px;">
            <h4 class="fw-bold mb-4">Tóm tắt đơn hàng</h4>
            
            <div v-if="cartStore.items.length === 0" class="text-center py-4">
              <p class="text-danger mb-0">Giỏ hàng của bạn đang trống!</p>
            </div>

            <div v-else>
              <div v-for="item in cartStore.items" :key="item.product.id" class="d-flex justify-content-between align-items-center mb-3 pb-3 border-bottom">
                <div class="d-flex align-items-center gap-3">
                  <div class="position-relative">
                    <img :src="item.product.image" class="rounded-3 bg-light object-fit-contain p-1" style="width: 60px; height: 60px;">
                    <span class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-dark">
                      {{ item.quantity }}
                    </span>
                  </div>
                  <div class="small fw-medium" style="max-width: 150px;">{{ item.product.name }}</div>
                </div>
                <div class="fw-bold text-dark">${{ (item.product.price * item.quantity).toFixed(2) }}</div>
              </div>

              <div class="d-flex justify-content-between text-secondary mb-2 mt-4">
                <span>Tạm tính</span>
                <span>${{ cartTotal.toFixed(2) }}</span>
              </div>
              <div class="d-flex justify-content-between text-secondary mb-3 pb-3 border-bottom">
                <span>Phí vận chuyển</span>
                <span class="text-success fw-medium">Miễn phí</span>
              </div>
              <div class="d-flex justify-content-between align-items-center">
                <span class="fw-bold fs-5 text-dark">Tổng cộng</span>
                <span class="fw-bold fs-3 text-danger">${{ cartTotal.toFixed(2) }}</span>
              </div>
            </div>

          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { cartStore, cartTotal } from '../store/cartStore'

const router = useRouter()

const handleCheckout = () => {
  if (cartStore.items.length === 0) {
    alert('Giỏ hàng trống! Vui lòng chọn sản phẩm trước khi thanh toán.')
    return
  }
  
  alert('🎉 Đặt hàng thành công! Cảm ơn bạn đã mua sắm tại LATRA.')
  
  // Xóa giỏ hàng và quay về trang chủ
  cartStore.items = []
  router.push('/')
}
</script> -->