<script setup lang="ts">
import { usePos } from '../composables/usePos'

const { 
  cart, customer, voucherCode, appliedDiscount, cartTotal, finalTotal,
  removeScannedImei, clearCart, updateWarrantyName, handleApplyVoucher, removeVoucher,
  openPaymentModal, formatMoney 
} = usePos()
</script>

<template>
  <div class="card border-0 shadow-sm rounded-4 bg-white h-100 d-flex flex-column overflow-hidden">
    <div class="p-3 border-b border-slate-100 d-flex align-items-center justify-content-between bg-white z-1">
      <div class="d-flex align-items-center gap-2">
        <i class="bi bi-cart3 text-slate-800 fs-5"></i>
        <span class="text-slate-800 fw-black tracking-tight fs-5">Giỏ máy đã quét</span>
        <span class="badge bg-slate-800 text-white rounded-pill px-2 py-1 text-xs fw-bold">{{ cart.length }}</span>
      </div>
      <button v-if="cart.length > 0" class="btn btn-link text-danger text-decoration-none p-0 text-sm fw-bold" @click="clearCart">
        <i class="bi bi-trash3 me-1"></i>Xóa hết
      </button>
    </div>

    <div class="flex-grow-1 overflow-y-auto px-3 py-2 custom-scrollbar bg-slate-50">
      <div v-if="cart.length === 0" class="h-100 d-flex flex-column align-items-center justify-content-center text-center py-5">
        <i class="bi bi-qr-code text-slate-300 fs-1 mb-3"></i>
        <p class="text-slate-400 text-sm fw-medium px-4">Chưa có máy nào được quét.<br>Hãy bắn IMEI hoặc click chọn sản phẩm kho.</p>
      </div>

      <div v-else class="d-flex flex-column gap-3 py-1">
        <div v-for="(item, idx) in cart" :key="item.imeiCode" class="card border-slate-200 p-3 rounded-4 shadow-sm position-relative bg-white">
          <button class="btn-remove" @click="removeScannedImei(idx)" title="Xóa máy này">
            <i class="bi bi-x-circle-fill"></i>
          </button>
          
          <div class="d-flex gap-3">
            <img :src="item.image" class="rounded-3 object-fit-contain bg-slate-50 p-1 border" style="width: 56px; height: 56px;" alt="phone"/>
            <div class="flex-grow-1 min-w-0 pe-3">
              <h6 class="text-slate-900 fw-bold mb-0 text-truncate">{{ item.name }}</h6>
              <p class="text-slate-500 text-xs mb-1 fw-medium">{{ item.storage }} • {{ item.color }}</p>
              <div class="font-monospace text-slate-400 text-xs fw-bold tracking-wider">IMEI: {{ item.imeiCode }}</div>
            </div>
          </div>

          <div class="mt-3 pt-2 border-top border-slate-100 row g-2 align-items-center">
            <div class="col-5">
              <span class="section-label d-block text-nowrap">🛡️ Bảo hành mở rộng:</span>
            </div>
            <div class="col-7">
              <select v-model="item.warrantyFee" @change="updateWarrantyName(item)" class="form-select form-select-sm text-xs border-slate-200 rounded-3 fw-semibold text-slate-700">
                <option :value="0">6 Tháng Tiêu Chuẩn (+0đ)</option>
                <option :value="500000">VIP 12 Tháng Gold (+500k)</option>
                <option :value="800000">Rơi vỡ, Vào nước (+800k)</option>
              </select>
            </div>
          </div>

          <div class="mt-2 text-end">
            <span class="text-slate-900 fw-black text-sm">{{ formatMoney(item.price + Number(item.warrantyFee)) }}</span>
          </div>
        </div>
      </div>
    </div>

    <div class="p-3 border-top border-slate-100 bg-white mt-auto">
      <div class="mb-3">
        <span class="section-label d-block mb-2">👤 Thông tin khách mua</span>
        <div class="row g-2">
          <div class="col-7">
            <input v-model="customer.phone" type="text" class="form-control form-control-sm border-slate-200 rounded-3" placeholder="Số điện thoại (*)" required />
          </div>
          <div class="col-5">
            <input v-model="customer.name" type="text" class="form-control form-control-sm border-slate-200 rounded-3" placeholder="Tên khách" />
          </div>
        </div>
      </div>

      <div class="mb-3 pt-2 border-top border-slate-100">
        <span class="section-label d-block mb-2">🎟️ Mã giảm giá / Voucher</span>
        <div class="input-group input-group-sm rounded-3 overflow-hidden border">
          <input 
            v-model="voucherCode" 
            type="text" 
            class="form-control border-0 bg-slate-50 text-slate-800 fw-bold uppercase px-2" 
            placeholder="NHẬP MÃ GIẢM GIÁ..." 
            :disabled="appliedDiscount > 0"
            @keydown.enter="handleApplyVoucher"
          />
          <button v-if="appliedDiscount === 0" class="btn btn-dark-peel px-3 text-xs fw-bold" type="button" @click="handleApplyVoucher">Áp dụng</button>
          <button v-else class="btn btn-danger px-3 text-xs fw-bold" type="button" @click="removeVoucher"><i class="bi bi-x-lg"></i> Hủy</button>
        </div>
      </div>

      <div class="bg-slate-50 rounded-4 p-3 border border-slate-100 text-sm mb-3">
        <div class="d-flex justify-content-between mb-1 text-slate-600 fw-medium">
          <span>Tạm tính máy + BH:</span>
          <span>{{ formatMoney(cartTotal) }}</span>
        </div>
        <div v-if="appliedDiscount > 0" class="d-flex justify-content-between mb-1 text-danger fw-bold">
          <span>Giảm giá Voucher:</span>
          <span>-{{ formatMoney(appliedDiscount) }}</span>
        </div>
        <hr class="my-2 border-slate-200 border-dashed">
        <div class="d-flex justify-content-between align-items-center">
          <span class="text-slate-800 fw-black text-base">Tổng cần thu:</span>
          <span class="text-danger fw-black fs-4 tracking-tight">{{ formatMoney(finalTotal) }}</span>
        </div>
      </div>

      <button 
        class="btn btn-dark-peel w-100 py-2.5 rounded-4 fw-black tracking-tight d-flex align-items-center justify-content-center gap-2 shadow"
        :disabled="cart.length === 0"
        @click="openPaymentModal"
      >
        <span>TIẾP TỤC THANH TOÁN (F9)</span>
        <i class="bi bi-arrow-right-short fs-5"></i>
      </button>
    </div>
  </div>
</template>