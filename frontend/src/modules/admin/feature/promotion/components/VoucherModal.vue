<script setup lang="ts">
import { ref, onMounted, computed } from "vue";
import { useAppStore } from "@/common/store/app.store";
import { promotionService } from "../services/promotionService";
import type { Voucher } from "../types/promotion.type";
import { ROLES } from "@/common/constants/permissions"; // 🔑 Import cấu hình Role

const props = defineProps<{ voucherSelected: Voucher | null }>();
const emit = defineEmits<{ (e: "close"): void; (e: "refresh"): void; }>();
const appStore = useAppStore();
const isEdit = ref(false);

const formData = ref<Voucher>({
  code: "", discountType: "PERCENTAGE", value: 0, minOrderValue: 0, 
  maxDiscountAmount: null, usageLimit: 100, startDate: "", expiredAt: "", category: "DISCOUNT"
});

const nowDateTime = computed(() => {
  const d = new Date();
  d.setMinutes(d.getMinutes() - d.getTimezoneOffset());
  return d.toISOString().substring(0, 16);
});

onMounted(() => {
  if (props.voucherSelected) {
    isEdit.value = true;
    formData.value = { 
      ...props.voucherSelected,
      startDate: props.voucherSelected.startDate.substring(0, 16),
      expiredAt: props.voucherSelected.expiredAt.substring(0, 16)
    };
  } else {
    formData.value.startDate = nowDateTime.value;
  }
});

const saveData = async () => {
  const cleanCode = formData.value.code.trim().toUpperCase();
  if (!cleanCode) return appStore.notifyError("Vui lòng nhập Mã Voucher!");
  if (!formData.value.startDate || !formData.value.expiredAt) return appStore.notifyError("Vui lòng chọn thời gian áp dụng!");
  
  const start = new Date(formData.value.startDate);
  const end = new Date(formData.value.expiredAt);
  const now = new Date();
  
  if (start < now && !isEdit.value) return appStore.notifyError("Thời gian bắt đầu không được trong quá khứ!");
  if (end <= start) return appStore.notifyError("Thời gian kết thúc phải diễn ra SAU thời gian bắt đầu!");
  
  if (formData.value.discountType === 'PERCENTAGE') {
    if (formData.value.value <= 0 || formData.value.value > 100) return appStore.notifyError("Mức giảm phần trăm phải từ 1 đến 100!");
    if (!formData.value.maxDiscountAmount || formData.value.maxDiscountAmount <= 0) return appStore.notifyError("Vui lòng nhập mức giảm tối đa cho mã phần trăm!");
  } else {
    if (formData.value.value <= 0) return appStore.notifyError("Số tiền giảm phải lớn hơn 0!");
  }

  const fixSeconds = (t: string) => t.length === 16 ? `${t}:00` : t;
  const payload: Voucher = {
    ...formData.value,
    code: cleanCode,
    startDate: fixSeconds(formData.value.startDate),
    expiredAt: fixSeconds(formData.value.expiredAt)
  };

  try {
    appStore.startLoading();
    if (isEdit.value && payload.id) await promotionService.updateVoucher(payload.id, payload);
    else await promotionService.createVoucher(payload);
    
    appStore.notifySuccess("Đã lưu mã Voucher thành công!");
    emit("refresh"); emit("close");
  } catch (error: any) {
    appStore.notifyError(error.response?.data?.message || "Mã Voucher đã tồn tại hoặc dữ liệu không hợp lệ!");
  } finally {
    appStore.stopLoading();
  }
};
</script>

<template>
  <div class="modal fade show d-block custom-backdrop" tabindex="-1" @click.self="emit('close')">
    <div class="modal-dialog modal-dialog-centered modal-dialog-scrollable" style="max-width: 700px;">
      
      <form @submit.prevent="saveData" novalidate class="modal-content shadow-lg border-0 rounded-4 overflow-hidden">
        
        <div class="modal-header border-bottom px-4 py-3 bg-light bg-opacity-75">
          <h5 class="modal-title fw-semibold text-dark">
            {{ isEdit ? "Cập Nhật Voucher" : "Tạo Mã Voucher Mới" }}
          </h5>
          <button type="button" class="btn-close shadow-none" @click="emit('close')"></button>
        </div>

        <div class="modal-body p-4 bg-light bg-opacity-25 custom-scrollbar">
          
          <div class="bg-white rounded-4 border p-4 mb-4 shadow-sm">
            <h6 class="fw-semibold text-dark mb-3">Định dạng mã</h6>
            <div class="mb-2">
              <label class="form-label small fw-medium text-muted mb-1">Mã Voucher (Code) <span class="text-danger">*</span></label>
              <input v-model="formData.code" class="form-control bg-light border-0 py-2 text-uppercase fw-bold text-primary custom-focus" placeholder="VD: TET2026, FREESHIP..." />
            </div>
          </div>

          <div class="bg-white rounded-4 border p-4 mb-4 shadow-sm">
            <h6 class="fw-semibold text-dark mb-3">Thiết lập giảm giá</h6>
            <div class="row g-3">
              <div class="col-md-6">
                <label class="form-label small fw-medium text-muted mb-1">Loại Giảm</label>
                <select v-model="formData.discountType" class="form-select bg-light border-0 py-2 custom-focus">
                  <option value="PERCENTAGE">Giảm theo phần trăm (%)</option>
                  <option value="FIXED_AMOUNT">Giảm tiền mặt trực tiếp</option>
                </select>
              </div>
              <div class="col-md-6">
                <label class="form-label small fw-medium text-muted mb-1">Giá trị giảm <span class="text-danger">*</span></label>
                <input v-model.number="formData.value" type="number" min="1" class="form-control bg-light border-0 py-2 fw-bold text-success custom-focus" />
              </div>
              
              <div class="col-md-6">
                <label class="form-label small fw-medium text-muted mb-1">Giá trị đơn tối thiểu <span class="text-danger">*</span></label>
                <input v-model.number="formData.minOrderValue" type="number" min="0" class="form-control bg-light border-0 py-2 custom-focus" placeholder="0 = Áp dụng mọi đơn" />
              </div>
              <div class="col-md-6" v-if="formData.discountType === 'PERCENTAGE'">
                <label class="form-label small fw-medium text-muted mb-1">Mức giảm tối đa <span class="text-danger">*</span></label>
                <input v-model.number="formData.maxDiscountAmount" type="number" min="1" class="form-control bg-light border-0 py-2 custom-focus" />
              </div>
            </div>
          </div>

          <div class="bg-white rounded-4 border p-4 shadow-sm">
            <h6 class="fw-semibold text-dark mb-3">Điều kiện & Thời gian áp dụng</h6>
            <div class="row g-3">
              <div class="col-md-12">
                <label class="form-label small fw-medium text-muted mb-1">Tổng lượt sử dụng tối đa <span class="text-danger">*</span></label>
                <input v-model.number="formData.usageLimit" type="number" min="1" class="form-control bg-light border-0 py-2 custom-focus" />
              </div>
              <div class="col-md-6">
                <label class="form-label small fw-medium text-muted mb-1">Thời gian bắt đầu <span class="text-danger">*</span></label>
                <input v-model="formData.startDate" type="datetime-local" :min="nowDateTime" class="form-control bg-light border-0 py-2 custom-focus" />
              </div>
              <div class="col-md-6">
                <label class="form-label small fw-medium text-muted mb-1">Thời gian kết thúc <span class="text-danger">*</span></label>
                <input v-model="formData.expiredAt" type="datetime-local" :min="formData.startDate || nowDateTime" class="form-control bg-light border-0 py-2 custom-focus" />
              </div>
            </div>
          </div>
          
        </div>

        <div class="modal-footer bg-white border-top px-4 py-3 d-flex justify-content-end gap-2">
          <button type="button" @click="emit('close')" class="btn btn-light px-4 py-2 rounded-pill fw-medium border">
            Hủy bỏ
          </button>
          <button 
            type="submit" 
            v-permission="[ROLES.OWNER, ROLES.MANAGER]"
            class="btn btn-dark px-5 py-2 rounded-pill fw-semibold transition-all"
          >
            Lưu Voucher
          </button>
        </div>
        
      </form>
    </div>
  </div>
</template>

<style scoped>
/* Lớp phủ đen làm mờ nền giống file FlashSaleModal */
.custom-backdrop {
  background-color: rgba(15, 23, 42, 0.4);
  backdrop-filter: blur(3px);
}

/* Custom Scrollbar */
.custom-scrollbar::-webkit-scrollbar {
  width: 6px;
}
.custom-scrollbar::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 10px;
}
.custom-scrollbar::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}

/* Hiệu ứng focus đồng bộ */
.custom-focus:focus {
  box-shadow: 0 0 0 3px rgba(15, 23, 42, 0.1) !important;
  border-color: #cbd5e1 !important;
  outline: none;
}

/* Hiệu ứng nút bấm */
.transition-all {
  transition: all 0.2s ease-in-out;
}
.transition-all:hover {
  transform: translateY(-1px);
}
</style>