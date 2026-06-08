<script setup lang="ts">
import { onMounted } from 'vue';
import { useCheckoutStore } from '../stores/checkoutStore';
// 🌟 CHUẨN BÀI: Import userService vào để bốc data, không gọi request trực tiếp nữa
import { userService } from '../services/userService';

const checkoutStore = useCheckoutStore();

onMounted(async () => {
  try {
    // 1. Gọi xuống tầng Service để lấy thông tin User
    const res = await userService.getCurrentUser();
    
    // 2. Bóc tách data từ format chuẩn ApiResponse của Spring Boot (res.data)
    const userData = res?.data; 
    console.log("=== DỮ LIỆU USER ĐỒNG BỘ THÀNH CÔNG ===", userData);

    if (userData) {
  // Điền thông tin cơ bản
  if (!checkoutStore.formState.customerName) checkoutStore.formState.customerName = userData.fullName || ''; 
  if (!checkoutStore.formState.customerPhone) checkoutStore.formState.customerPhone = userData.phone || '';
  if (!checkoutStore.formState.customerEmail) checkoutStore.formState.customerEmail = userData.email || '';
  
  // 🌟 TRICK CHẺ CHUỖI ĐỊA CHỈ:
  if (userData.address) {
    // Tách chuỗi dựa vào dấu phẩy (,) và xóa khoảng trắng thừa ở 2 đầu
    const addressParts = userData.address.split(',').map((item: string) => item.trim());
    
    // Nếu chuỗi có đủ 4 phần trở lên (Ví dụ: [0]="sadas", [1]="Đại Thành", [2]="Quốc Oai", [3]="Hà Nội")
    if (addressParts.length >= 4) {
      // Đảo ngược lại thứ tự để điền từ Tỉnh -> Quận -> Phường -> Số nhà
      // Dùng .pop() để lấy từ cuối mảng (Hà Nội) lên dần
      if (!checkoutStore.formState.province) checkoutStore.formState.province = addressParts.pop() || '';
      if (!checkoutStore.formState.district) checkoutStore.formState.district = addressParts.pop() || '';
      if (!checkoutStore.formState.ward) checkoutStore.formState.ward = addressParts.pop() || '';
      
      // Phần còn lại gộp hết vào Số nhà (phòng trường hợp số nhà có chứa dấu phẩy)
      if (!checkoutStore.formState.street) checkoutStore.formState.street = addressParts.join(', ') || '';
    } else {
      // Rủi ro: Nếu khách nhập địa chỉ quá ngắn (không đủ dấu phẩy), nhét tất cả vào ô Số nhà như cũ
      if (!checkoutStore.formState.street) checkoutStore.formState.street = userData.address || '';
    }
  }
}
  } catch (error) {
    // Nếu có lỗi (hết hạn token hoặc backend sập), in ra lỗi để dev dễ debug
    console.error("🚨 Lỗi tự động điền thông tin khách hàng:", error);
  }
});
</script>

<template>
  <div v-if="checkoutStore?.formState" class="bg-white p-4 p-lg-5 rounded-4 shadow-sm mb-4 border border-slate-200">
    <h4 class="fw-bold mb-4 text-dark">Thông tin giao hàng</h4>
    
    <div class="row g-3">
      <div class="col-12">
        <label for="customerName" class="form-label small fw-bold text-secondary">
          Họ và tên người nhận <span class="text-danger">*</span>
        </label>
        <input 
          v-model="checkoutStore.formState.customerName" 
          type="text" 
          id="customerName" 
          class="form-control p-3 rounded-3 shadow-none border-slate-300 transition-all" 
          placeholder="Nhập đầy đủ họ và tên" 
          required
        >
      </div>

      <div class="col-md-6">
        <label for="customerPhone" class="form-label small fw-bold text-secondary">
          Số điện thoại <span class="text-danger">*</span>
        </label>
        <input 
          v-model="checkoutStore.formState.customerPhone" 
          type="tel" 
          id="customerPhone" 
          class="form-control p-3 rounded-3 shadow-none border-slate-300 transition-all" 
          placeholder="Ví dụ: 0912345678" 
          required
        >
      </div>

      <div class="col-md-6">
        <label for="customerEmail" class="form-label small fw-bold text-secondary">
          Địa chỉ Email <span class="text-danger">*</span>
        </label>
        <input 
          v-model="checkoutStore.formState.customerEmail" 
          type="email" 
          id="customerEmail" 
          class="form-control p-3 rounded-3 shadow-none border-slate-300 transition-all" 
          placeholder="name@example.com" 
          required
        >
      </div>

      <div class="col-md-4">
        <label for="province" class="form-label small fw-bold text-secondary">
          Tỉnh / Thành phố <span class="text-danger">*</span>
        </label>
        <input 
          v-model="checkoutStore.formState.province" 
          type="text" 
          id="province" 
          class="form-control p-3 rounded-3 shadow-none border-slate-300 transition-all" 
          placeholder="Ví dụ: Hà Nội" 
          required
        >
      </div>

      <div class="col-md-4">
        <label for="district" class="form-label small fw-bold text-secondary">
          Quận / Huyện <span class="text-danger">*</span>
        </label>
        <input 
          v-model="checkoutStore.formState.district" 
          type="text" 
          id="district" 
          class="form-control p-3 rounded-3 shadow-none border-slate-300 transition-all" 
          placeholder="Ví dụ: Cầu Giấy" 
          required
        >
      </div>

      <div class="col-md-4">
        <label for="ward" class="form-label small fw-bold text-secondary">
          Phường / Xã <span class="text-danger">*</span>
        </label>
        <input 
          v-model="checkoutStore.formState.ward" 
          type="text" 
          id="ward" 
          class="form-control p-3 rounded-3 shadow-none border-slate-300 transition-all" 
          placeholder="Ví dụ: Dịch Vọng" 
          required
        >
      </div>

      <div class="col-12">
        <label for="street" class="form-label small fw-bold text-secondary">
          Số nhà, tên đường <span class="text-danger">*</span>
        </label>
        <textarea 
          v-model="checkoutStore.formState.street" 
          id="street" 
          rows="2" 
          class="form-control p-3 rounded-3 shadow-none border-slate-300 transition-all" 
          placeholder="Ví dụ: Số 12, Ngõ 34 Phố Nguyễn Văn Huyên" 
          required
        ></textarea>
      </div>

      <div class="col-12">
        <label for="orderNotes" class="form-label small fw-bold text-secondary">
          Ghi chú đơn hàng (Tùy chọn)
        </label>
        <textarea 
          v-model="checkoutStore.formState.note" 
          id="orderNotes" 
          rows="2" 
          class="form-control p-3 rounded-3 shadow-none border-slate-300 transition-all" 
          placeholder="Ví dụ: Giao giờ hành chính, gọi trước khi giao..."
        ></textarea>
      </div>

      <div class="col-12 mt-4">
        <div class="form-check form-switch p-3 bg-light rounded-3 border d-flex align-items-center justify-content-between">
          <div class="ms-2">
            <label class="form-check-label fw-bold text-dark cursor-pointer" for="isRequestVat">
              <i class="bi bi-receipt text-primary me-2"></i> Yêu cầu xuất hóa đơn GTGT (VAT)
            </label>
            <div class="small text-muted mt-1">Hệ thống sẽ gửi hóa đơn điện tử qua email doanh nghiệp của bạn.</div>
          </div>
          <input 
            v-model="checkoutStore.formState.isRequestVat" 
            class="form-check-input me-2 custom-switch-scale shadow-none" 
            type="checkbox" 
            id="isRequestVat"
          >
        </div>
      </div>

      <div v-if="checkoutStore.formState.isRequestVat" class="col-12 mt-2 input-vat-fade">
        <div class="p-4 border border-primary border-opacity-25 bg-primary bg-opacity-10 rounded-4">
          <h5 class="fw-bold text-primary mb-3" style="font-size: 1rem;">Thông tin doanh nghiệp</h5>
          <div class="row g-3">
            
            <div class="col-md-6">
              <label for="taxId" class="form-label small fw-bold text-secondary">Mã số thuế <span class="text-danger">*</span></label>
              <input 
                v-model="checkoutStore.formState.taxId" 
                type="text" 
                id="taxId" 
                class="form-control p-3 rounded-3 shadow-none border-slate-300 bg-white" 
                placeholder="Nhập mã số thuế công ty" 
                :required="checkoutStore.formState.isRequestVat"
              >
            </div>

            <div class="col-md-6">
              <label for="companyEmail" class="form-label small fw-bold text-secondary">Email nhận hóa đơn <span class="text-danger">*</span></label>
              <input 
                v-model="checkoutStore.formState.companyEmail" 
                type="email" 
                id="companyEmail" 
                class="form-control p-3 rounded-3 shadow-none border-slate-300 bg-white" 
                placeholder="accounting@company.com" 
                :required="checkoutStore.formState.isRequestVat"
              >
            </div>

            <div class="col-12">
              <label for="companyName" class="form-label small fw-bold text-secondary">Tên công ty / đơn vị <span class="text-danger">*</span></label>
              <input 
                v-model="checkoutStore.formState.companyName" 
                type="text" 
                id="companyName" 
                class="form-control p-3 rounded-3 shadow-none border-slate-300 bg-white" 
                placeholder="Công ty TNHH Giải Pháp Công Nghệ LATRA" 
                :required="checkoutStore.formState.isRequestVat"
              >
            </div>

            <div class="col-12">
              <label for="companyAddress" class="form-label small fw-bold text-secondary">Địa chỉ công ty (Theo GPKD) <span class="text-danger">*</span></label>
              <input 
                v-model="checkoutStore.formState.companyAddress" 
                type="text" 
                id="companyAddress" 
                class="form-control p-3 rounded-3 shadow-none border-slate-300 bg-white" 
                placeholder="Số khối, tên đường, quận/huyện, tỉnh/thành phố..." 
                :required="checkoutStore.formState.isRequestVat"
              >
            </div>

          </div>
        </div>
      </div>

    </div>
  </div>
</template>

<style scoped>
.transition-all { transition: all 0.2s ease-in-out; }
.form-control:focus {
  border-color: #111;
  box-shadow: 0 0 0 0.25rem rgba(17, 17, 17, 0.1);
}
.border-slate-200 { border-color: #e2e8f0 !important; }
.border-slate-300 { border-color: #cbd5e1 !important; }
.cursor-pointer { cursor: pointer; }
.custom-switch-scale {
  width: 2.5em !important;
  height: 1.25em !important;
}
.input-vat-fade { animation: fadeIn 0.3s ease-out; }
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(-10px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>