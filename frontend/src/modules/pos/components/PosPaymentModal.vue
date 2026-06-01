<script setup lang="ts">
import { usePos }from '../composables/usePos'

const { 
  showPaymentModal, paymentForm, finalTotal, changeAmount, customerTransferAmount,
  supportedBanks, selectedBankId, qrCodeUrl, cashSuggestions, displayCustomerGiven,
  onInputCash, setQuickCash, handleFocusInput, simulateBankTransfer, handleCheckout, formatMoney 
} = usePos()
</script>

<template>
  <div v-if="showPaymentModal" class="modal-overlay position-fixed top-0 start-0 w-100 h-100 bg-black bg-opacity-50 d-flex align-items-center justify-content-center z-3 p-3">
    <div class="card border-0 shadow-lg rounded-4 overflow-hidden animate-pop-in bg-white w-100" style="max-width: 720px;">
      
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
        <div class="d-flex gap-3 mb-4">
          <div class="pay-method-card" :class="{ active: paymentForm.method === 'CASH' }" @click="paymentForm.method = 'CASH'">
            <i class="bi bi-cash-coin d-block mb-1"></i> Tiền mặt
          </div>
          <div class="pay-method-card" :class="{ active: paymentForm.method === 'TRANSFER' }" @click="paymentForm.method = 'TRANSFER'">
            <i class="bi bi-bank d-block mb-1"></i> Chuyển khoản
          </div>
          <div class="pay-method-card" :class="{ active: paymentForm.method === 'SPLIT' }" @click="paymentForm.method = 'SPLIT'">
            <i class="bi bi-layers-half d-block mb-1"></i> Hỗn hợp (Tiền + CK)
          </div>
        </div>

        <div class="row g-4">
          <div v-if="paymentForm.method === 'CASH' || paymentForm.method === 'SPLIT'" class="col-sm-6">
            <label class="form-label text-slate-700 fw-bold text-sm">
              {{ paymentForm.method === 'SPLIT' ? '👉 Tiền mặt khách đưa trước:' : '👉 Tiền mặt khách đưa:' }}
            </label>
            <div class="input-group focus-ring-peel rounded-3 border overflow-hidden mb-2">
              <input 
                :value="displayCustomerGiven"
                @input="onInputCash"
                @focus="handleFocusInput"
                type="text" 
                class="form-control border-0 fw-bold fs-5 text-slate-900 font-monospace text-end py-2 pe-3 bg-light" 
                placeholder="0"
              />
              <span class="input-group-text border-0 bg-light fw-bold text-slate-600">₫</span>
            </div>

            <div class="d-flex flex-wrap gap-1.5 mb-3">
              <button 
                v-for="amt in cashSuggestions" 
                :key="amt" 
                class="btn btn-outline-dark text-xs py-1 px-2 rounded-pill fw-semibold"
                @click="setQuickCash(amt)"
              >
                +{{ (amt / 1000).toLocaleString() }}k
              </button>
            </div>

            <div v-if="paymentForm.method === 'CASH'" class="p-3 rounded-4" :class="changeAmount >= 0 ? 'bg-success-subtle text-success' : 'bg-warning-subtle text-warning'">
              <div class="d-flex justify-content-between align-items-center text-sm fw-bold">
                <span>{{ changeAmount >= 0 ? 'Tiền thừa trả khách:' : 'Còn thiếu:' }}</span>
                <span class="fs-5 fw-black font-monospace">{{ formatMoney(Math.abs(changeAmount)) }}</span>
              </div>
            </div>

            <div v-if="paymentForm.method === 'SPLIT'" class="p-3 bg-primary-subtle text-primary rounded-4">
              <div class="d-flex justify-content-between align-items-center text-sm fw-bold">
                <span>Số tiền còn lại cần CK:</span>
                <span class="fs-5 fw-black font-monospace">{{ formatMoney(customerTransferAmount) }}</span>
              </div>
            </div>
          </div>

          <div v-if="paymentForm.method === 'TRANSFER' || paymentForm.method === 'SPLIT'" class="col-md" :class="{ 'col-sm-6 border-start border-slate-200': paymentForm.method === 'SPLIT' }">
            <div class="d-flex flex-column align-items-center text-center">
              <label class="form-label text-slate-700 fw-bold text-sm w-100 text-start mb-2">🏦 Tài khoản & QR nhận tiền</label>
              
              <select v-model="selectedBankId" class="form-select form-select-sm border-slate-200 rounded-3 text-xs fw-bold mb-3 text-slate-700">
                <option v-for="b in supportedBanks" :key="b.id" :value="b.id">{{ b.name }} ({{ b.accNo }})</option>
              </select>

              <div class="p-2 border border-slate-200 rounded-4 bg-white shadow-sm position-relative mb-2">
                <img :src="qrCodeUrl" class="qr-img img-fluid" alt="VietQR Viet Nam" />
                <button 
                  class="btn btn-light bg-white border shadow-sm position-absolute start-50 top-50 translate-middle rounded-circle p-2" 
                  title="Kiểm tra tiền ting ting"
                  @click="simulateBankTransfer"
                >
                  <i class="bi bi-arrow-clockwise text-slate-800 animate-spin"></i>
                </button>
              </div>
              <span class="text-slate-400 text-xs fw-medium"><i class="bi bi-info-circle me-1"></i>Quét để điền tự động số tiền & nội dung</span>
            </div>
          </div>
        </div>

        <div class="mt-4 pt-3 border-top border-slate-100">
          <label class="form-label text-slate-700 fw-bold text-sm">📝 Ghi chú lưu đơn hàng (nếu có)</label>
          <textarea v-model="paymentForm.note" class="form-control border-slate-200 rounded-3 text-sm" rows="2" placeholder="Ví dụ: Khách hẹn lấy máy sau, tặng thêm cường lực bao da..."></textarea>
        </div>

      </div>

      <div class="p-3 bg-slate-50 border-top border-slate-100 d-flex gap-3 justify-content-end">
        <button class="btn btn-outline-secondary px-4 py-2 rounded-3 text-sm fw-bold" @click="showPaymentModal = false">Quay lại giỏ</button>
        <button 
          class="btn btn-dark-peel px-5 py-2 rounded-3 text-sm fw-black tracking-tight"
          :disabled="paymentForm.method === 'CASH' && changeAmount < 0"
          @click="handleCheckout"
        >
          <i class="bi bi-check2-circle me-1.5"></i>HOÀN THÀNH XUẤT ĐƠN
        </button>
      </div>

    </div>
  </div>
</template>