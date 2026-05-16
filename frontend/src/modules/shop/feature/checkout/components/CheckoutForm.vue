<!-- frontend/src/modules/shop/feature/checkout/components/CheckoutForm.vue -->
<script setup lang="ts">
import { useCheckoutStore } from '../stores/checkoutStore';
const checkoutStore = useCheckoutStore();

// Mock Data Tỉnh/Thành phố (Sau này gọi API thực tế)
const mockProvinces = ['Hà Nội', 'TP. Hồ Chí Minh', 'Đà Nẵng'];
const mockDistricts = ['Quận 1', 'Quận 3', 'Quận Cầu Giấy', 'Quận Đống Đa'];
const mockWards = ['Phường 1', 'Phường 2', 'Phường Bến Nghé', 'Phường Dịch Vọng'];
</script>

<template>
  <div class="checkout-form-container">
    
    <!-- SECTION 1: THÔNG TIN KHÁCH HÀNG -->
    <div class="bg-white p-4 p-lg-5 rounded-4 shadow-sm mb-4">
      <div class="d-flex justify-content-between align-items-center mb-4">
        <h5 class="fw-bold mb-0">1. Thông tin người nhận</h5>
        <span class="badge bg-success bg-opacity-10 text-success px-3 py-2"><i class="bi bi-shield-check"></i> Đã bảo mật</span>
      </div>

      <div class="row g-3">
        <div class="col-md-6">
          <label class="form-label small fw-bold text-secondary">Họ và tên <span class="text-danger">*</span></label>
          <input v-model="checkoutStore.formState.customerName" type="text" class="form-control py-2 shadow-none border-slate-300" placeholder="VD: Nguyễn Văn A">
        </div>
        <div class="col-md-6">
          <label class="form-label small fw-bold text-secondary">Số điện thoại <span class="text-danger">*</span></label>
          <input v-model="checkoutStore.formState.customerPhone" type="tel" class="form-control py-2 shadow-none border-slate-300" placeholder="VD: 0901234567">
        </div>
        <div class="col-12">
          <label class="form-label small fw-bold text-secondary">Email (Nhận hóa đơn điện tử) <span class="text-danger">*</span></label>
          <input v-model="checkoutStore.formState.customerEmail" type="email" class="form-control py-2 shadow-none border-slate-300" placeholder="VD: email@gmail.com">
        </div>
      </div>
    </div>

    <!-- SECTION 2: ĐỊA CHỈ NHẬN HÀNG -->
    <div class="bg-white p-4 p-lg-5 rounded-4 shadow-sm mb-4">
      <div class="d-flex align-items-center gap-2 mb-4">
        <h5 class="fw-bold mb-0">2. Địa chỉ nhận hàng</h5>
        <span class="badge bg-warning text-dark px-2 py-1" style="font-size: 0.65rem;">[STUB: Dữ liệu mẫu]</span>
      </div>

      <div class="row g-3">
        <div class="col-md-4">
          <label class="form-label small fw-bold text-secondary">Tỉnh / Thành phố <span class="text-danger">*</span></label>
          <select v-model="checkoutStore.formState.province" class="form-select py-2 shadow-none border-slate-300">
            <option value="" disabled>-- Chọn Tỉnh/Thành --</option>
            <option v-for="p in mockProvinces" :key="p" :value="p">{{ p }}</option>
          </select>
        </div>
        <div class="col-md-4">
          <label class="form-label small fw-bold text-secondary">Quận / Huyện <span class="text-danger">*</span></label>
          <select v-model="checkoutStore.formState.district" class="form-select py-2 shadow-none border-slate-300" :disabled="!checkoutStore.formState.province">
            <option value="" disabled>-- Chọn Quận/Huyện --</option>
            <option v-for="d in mockDistricts" :key="d" :value="d">{{ d }}</option>
          </select>
        </div>
        <div class="col-md-4">
          <label class="form-label small fw-bold text-secondary">Phường / Xã <span class="text-danger">*</span></label>
          <select v-model="checkoutStore.formState.ward" class="form-select py-2 shadow-none border-slate-300" :disabled="!checkoutStore.formState.district">
            <option value="" disabled>-- Chọn Phường/Xã --</option>
            <option v-for="w in mockWards" :key="w" :value="w">{{ w }}</option>
          </select>
        </div>
        <div class="col-12">
          <label class="form-label small fw-bold text-secondary">Số nhà, Tên đường <span class="text-danger">*</span></label>
          <input v-model="checkoutStore.formState.street" type="text" class="form-control py-2 shadow-none border-slate-300" placeholder="VD: 123 Đường Lê Lợi, Tòa nhà ABC...">
        </div>
        <div class="col-12">
          <label class="form-label small fw-bold text-secondary">Ghi chú cho Shipper (Không bắt buộc)</label>
          <input v-model="checkoutStore.formState.note" type="text" class="form-control py-2 shadow-none border-slate-300" placeholder="VD: Gọi trước khi giao, Giao trong giờ hành chính...">
        </div>
      </div>
    </div>

    <!-- SECTION 3: HÓA ĐƠN VAT -->
    <div class="bg-white p-4 p-lg-5 rounded-4 shadow-sm mb-4">
      <div class="form-check form-switch d-flex align-items-center mb-0 cursor-pointer">
        <input v-model="checkoutStore.formState.isRequestVat" class="form-check-input fs-4 me-3 cursor-pointer shadow-none" type="checkbox" role="switch" id="vatSwitch">
        <label class="form-check-label fw-bold cursor-pointer pt-1 text-dark" for="vatSwitch">Yêu cầu xuất hóa đơn đỏ (VAT) cho Công ty</label>
      </div>

      <!-- Khung nhập VAT sẽ sổ ra khi bật công tắc -->
      <div v-if="checkoutStore.formState.isRequestVat" class="mt-4 pt-4 border-top">
        <div class="row g-3">
          <div class="col-md-4">
            <label class="form-label small fw-bold text-secondary">Mã số thuế <span class="text-danger">*</span></label>
            <input v-model="checkoutStore.formState.taxId" type="text" class="form-control py-2 shadow-none" placeholder="Nhập MST">
          </div>
          <div class="col-md-8">
            <label class="form-label small fw-bold text-secondary">Tên Công ty <span class="text-danger">*</span></label>
            <input v-model="checkoutStore.formState.companyName" type="text" class="form-control py-2 shadow-none" placeholder="Tên công ty trên GPKD">
          </div>
          <div class="col-md-12">
            <label class="form-label small fw-bold text-secondary">Email nhận hóa đơn <span class="text-danger">*</span></label>
            <input v-model="checkoutStore.formState.companyEmail" type="email" class="form-control py-2 shadow-none" placeholder="Kế toán công ty sẽ nhận email này">
          </div>
          <div class="col-md-12">
            <label class="form-label small fw-bold text-secondary">Địa chỉ Công ty</label>
            <input v-model="checkoutStore.formState.companyAddress" type="text" class="form-control py-2 shadow-none" placeholder="Địa chỉ đăng ký GPKD">
          </div>
        </div>
      </div>
    </div>

  </div>
</template>

<style scoped>
.border-slate-300 { border-color: #cbd5e1; }
.form-control:focus, .form-select:focus { border-color: #0f172a; box-shadow: 0 0 0 0.25rem rgba(15, 23, 42, 0.1); }
.cursor-pointer { cursor: pointer; }
</style>