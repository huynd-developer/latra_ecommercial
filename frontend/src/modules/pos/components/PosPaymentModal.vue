<script setup lang="ts">
import { usePos } from '../composables/usePos'

const { 
  showPaymentModal, paymentForm, finalTotal, changeAmount, customerTransferAmount,
  cashSuggestions, displayCustomerGiven,
  showVnPayQrModal, vnPayQrUrl, confirmVnPayPaymentDone, // Các biến & hàm VNPay
  onInputCash, setQuickCash, handleFocusInput, handleCheckout, formatMoney 
} = usePos()
</script>

<template>
  <div v-if="showPaymentModal" class="modal-overlay position-fixed top-0 start-0 w-100 h-100 bg-black bg-opacity-50 d-flex align-items-center justify-content-center z-3 p-3">
    <div class="card border-0 shadow-lg rounded-4 overflow-hidden animate-pop-in bg-white w-100" style="max-width: 650px;">
      
      <div class="p-3 bg-slate-900 text-white d-flex align-items-center justify-content-between">
        <div class="d-flex align-items-center gap-2">
          <i class="bi bi-wallet2 fs-5"></i>
          <span class="fw-black tracking-tight fs-5">Hành trình thanh toán đơn hàng</span>
        </div>
        <button class="btn btn-link text-white text-decoration-none p-0 opacity-70 hover-opacity-100" @click="showPaymentModal = false">
          <i class="bi bi-x-lg fs-5"></i>
        </button>
      </div>

      <div class="card-body p-4 overflow-y-auto custom-scrollbar" style="max-height: calc(85vh - 100px);">
        
        <div class="bg-danger-subtle border border-danger-subtle rounded-4 p-3 text-center mb-4">
          <span class="text-slate-600 text-xs fw-bold tracking-wider text-uppercase d-block mb-1">Số tiền khách hàng cần trả</span>
          <span class="text-danger fw-black fs-2 tracking-tight">{{ formatMoney(finalTotal) }}</span>
        </div>

        <span class="section-label d-block mb-2">💵 Hình thức thanh toán</span>
        <div class="row g-3 mb-4 justify-content-center">
          <div class="col-4">
            <div class="pay-method-card h-100" :class="{ active: paymentForm.method === 'CASH' }" @click="paymentForm.method = 'CASH'">
              <i class="bi bi-cash-coin d-block mb-1 fs-3"></i> Tiền mặt
            </div>
          </div>
          <div class="col-4">
            <div class="pay-method-card h-100 position-relative border-primary" :class="{ active: paymentForm.method === 'VNPAY' }" @click="paymentForm.method = 'VNPAY'">
              <span class="position-absolute top-0 start-50 translate-middle badge rounded-pill bg-danger text-xxs px-2 py-1" style="transform: translate(-50%, -40%) !important;">TỰ ĐỘNG</span>
              <i class="bi bi-qr-code-scan d-block mb-1 fs-3 text-primary"></i> Cổng VNPay
            </div>
          </div>
          <div class="col-4">
            <div class="pay-method-card h-100" :class="{ active: paymentForm.method === 'SPLIT' }" @click="paymentForm.method = 'SPLIT'">
              <i class="bi bi-layers-half d-block mb-1 fs-3"></i> Hỗn hợp
            </div>
          </div>
        </div>

        <div class="row g-4">
          <div v-if="paymentForm.method === 'CASH' || paymentForm.method === 'SPLIT'" class="col-12">
            <label class="form-label text-slate-700 fw-bold text-sm">
              {{ paymentForm.method === 'SPLIT' ? '👉 Tiền mặt khách đưa trước:' : '👉 Số tiền mặt khách đưa:' }}
            </label>
            <div class="input-group focus-ring-peel rounded-3 border overflow-hidden mb-2">
              <input :value="displayCustomerGiven" @input="onInputCash" @focus="handleFocusInput" type="text" class="form-control border-0 fw-bold fs-4 text-slate-900 font-monospace text-center py-2 bg-light" placeholder="0" />
              <span class="input-group-text border-0 bg-light fw-bold text-slate-600 px-4">₫</span>
            </div>

            <div class="d-flex justify-content-center flex-wrap gap-2 mb-3">
              <button v-for="amt in cashSuggestions" :key="amt" class="btn btn-outline-dark py-1 px-3 rounded-pill fw-semibold" @click="setQuickCash(amt)">
                +{{ (amt / 1000).toLocaleString() }}k
              </button>
            </div>

            <div v-if="paymentForm.method === 'CASH'" class="p-3 rounded-4 text-center" :class="changeAmount >= 0 ? 'bg-success-subtle text-success' : 'bg-warning-subtle text-warning'">
              <span class="text-sm fw-bold d-block mb-1">{{ changeAmount >= 0 ? 'Tiền thừa trả khách:' : 'Khách đưa còn thiếu:' }}</span>
              <span class="fs-4 fw-black font-monospace">{{ formatMoney(Math.abs(changeAmount)) }}</span>
            </div>

            <div v-if="paymentForm.method === 'SPLIT'" class="p-3 bg-primary-subtle text-primary rounded-4 text-center mt-2">
              <span class="text-sm fw-bold d-block mb-1">Số tiền còn thiếu (Hệ thống sẽ tạo mã VNPay):</span>
              <span class="fs-4 fw-black font-monospace">{{ formatMoney(customerTransferAmount) }}</span>
            </div>
          </div>

          <div v-if="paymentForm.method === 'VNPAY'" class="col-12">
            <div class="p-4 rounded-4 bg-primary-subtle text-primary border border-primary border-opacity-25 d-flex align-items-center gap-3">
              <div class="fs-1"><i class="bi bi-shield-check"></i></div>
              <div>
                <h6 class="fw-bold text-slate-900 mb-1">Thanh toán tự động qua cổng VNPay</h6>
                <p class="text-slate-600 text-sm mb-0">Hệ thống sẽ kết nối với máy chủ để sinh mã QR đúng số tiền <strong class="text-danger font-monospace">{{ formatMoney(finalTotal) }}</strong>. Bấm <b>Hoàn Thành Xuất Đơn</b> để lấy mã cho khách quét!</p>
              </div>
            </div>
          </div>
        </div>

        <div class="mt-4 pt-3 border-top border-slate-100">
          <label class="form-label text-slate-700 fw-bold text-sm">📝 Ghi chú lưu đơn hàng (nếu có)</label>
          <textarea v-model="paymentForm.note" class="form-control border-slate-200 rounded-3 text-sm" rows="2" placeholder="Ví dụ: Khách hẹn lấy máy sau..."></textarea>
        </div>

      </div>

      <div class="p-3 bg-slate-50 border-top border-slate-100 d-flex gap-3 justify-content-end">
        <button class="btn btn-outline-secondary px-4 py-2 rounded-3 text-sm fw-bold" @click="showPaymentModal = false">Quay lại giỏ</button>
        <button class="btn btn-dark-peel px-5 py-2 rounded-3 text-sm fw-black tracking-tight" :disabled="paymentForm.method === 'CASH' && changeAmount < 0" @click="handleCheckout">
          <i class="bi bi-check2-circle me-1.5"></i>HOÀN THÀNH XUẤT ĐƠN
        </button>
      </div>

    </div>
  </div>

  <div v-if="showVnPayQrModal" class="modal-overlay position-fixed top-0 start-0 w-100 h-100 bg-black bg-opacity-80 d-flex align-items-center justify-content-center z-3 p-3">
    <div class="card border-0 shadow-lg rounded-4 overflow-hidden bg-white w-100 text-center p-4 animate-pop-in" style="max-width: 440px;">
      
      <div class="mb-3 text-primary">
        <img src="https://pay.vnpay.vn/images/brands/logo-vnpay.svg" alt="VNPay Logo" style="height: 32px;" class="mb-2" />
        <h5 class="fw-black text-slate-900 tracking-tight mb-1">MÃ QUÉT TỰ ĐỘNG KHÁCH HÀNG</h5>
        <p class="text-slate-500 text-xs mb-0">Vui lòng hướng dẫn khách bật Camera / App ngân hàng quét mã</p>
      </div>

      <div class="bg-danger-subtle border border-danger-subtle rounded-4 py-2 px-3 text-center mb-3">
        <span class="text-slate-500 text-xxs fw-bold text-uppercase tracking-wider d-block mb-0.5">Giá trị cần thanh toán</span>
        <span class="text-danger fw-black fs-3 font-monospace tracking-tight">
          {{ formatMoney(paymentForm.method === 'SPLIT' ? customerTransferAmount : finalTotal) }}
        </span>
      </div>

      <div class="d-inline-block p-3 border border-slate-200 rounded-4 bg-white shadow-sm mb-3 mx-auto" style="width: fit-content;">
        <img :src="`https://api.qrserver.com/v1/create-qr-code/?size=250x250&data=${encodeURIComponent(vnPayQrUrl)}`" class="img-fluid" style="width: 250px; height: 250px;" alt="VNPay QR Link" />
      </div>

      <div class="text-xs text-slate-500 mb-4 animate-pulse-custom">
        <i class="bi bi-hourglass-split text-warning me-1"></i> Kho đã giữ hàng, đang đợi luồng tiền báo về...
      </div>

      <div class="d-flex gap-2.5 w-100">
        <button class="btn btn-outline-secondary px-3 py-2 rounded-3 text-sm fw-bold flex-grow-1" @click="showVnPayQrModal = false">Đóng ẩn</button>
        <button class="btn btn-success px-4 py-2 rounded-3 text-sm fw-black flex-grow-2" @click="confirmVnPayPaymentDone">
          <i class="bi bi-check-circle me-1"></i>XÁC NHẬN ĐÃ THU TIỀN
        </button>
      </div>

    </div>
  </div>
</template>

<style scoped>
.pay-method-card { border: 2px solid #e2e8f0; padding: 1rem 0.5rem; text-align: center; border-radius: 12px; cursor: pointer; font-size: 14px; font-weight: 700; color: #475569; transition: all 0.2s ease; }
.pay-method-card:hover { background-color: #f8fafc; border-color: #cbd5e1; }
.pay-method-card.active { border-color: #0f172a; background-color: #f1f5f9; color: #0f172a; }
.animate-pulse-custom { animation: pulse 1.8s cubic-bezier(0.4, 0, 0.6, 1) infinite; }
@keyframes pulse { 0%, 100% { opacity: 1; } 50% { opacity: .4; } }
.text-xxs { font-size: 10px; }
.flex-grow-2 { flex-grow: 2; }
</style>