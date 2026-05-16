<!-- frontend/src/modules/admin/feature/promotion/components/VoucherModal.vue -->
<script setup lang="ts">
import { ref, onMounted, computed } from "vue";
import { useAppStore } from "@/common/store/app.store";
import { promotionService } from "../services/promotionService";
import type { Voucher } from "../types/promotion.type";

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
  <div>
    <div class="offcanvas-backdrop fade show" @click="emit('close')"></div>
    <div class="offcanvas offcanvas-end show shadow-lg border-0" tabindex="-1" style="visibility: visible; width: 600px; max-width: 100vw;">
      <div class="offcanvas-header border-bottom px-4 py-3 bg-light bg-opacity-50">
        <h5 class="offcanvas-title fw-semibold">{{ isEdit ? "Cập Nhật Voucher" : "Tạo Mã Voucher Mới" }}</h5>
        <button type="button" class="btn-close" @click="emit('close')"></button>
      </div>

      <div class="offcanvas-body px-4 p-0 custom-scrollbar d-flex flex-column bg-light bg-opacity-25">
        <!-- Đã thêm novalidate -->
        <form @submit.prevent="saveData" novalidate class="d-flex flex-column h-100">
          
          <div class="flex-grow-1 pb-5">
            <div class="bg-white rounded-4 border p-4 mb-4 mt-3 shadow-sm">
              <h6 class="fw-semibold text-dark mb-3">Định dạng mã</h6>
              <div class="mb-3">
                <label class="form-label small fw-medium text-muted mb-1">Mã Voucher (Code) <span class="text-danger">*</span></label>
                <input v-model="formData.code" class="form-control bg-light border-0 py-2 text-uppercase fw-bold text-primary" placeholder="VD: TET2026, FREESHIP..." />
              </div>
            </div>

            <div class="bg-white rounded-4 border p-4 mb-4 shadow-sm">
              <h6 class="fw-semibold text-dark mb-3">Thiết lập giảm giá</h6>
              <div class="row g-3">
                <!-- (Giữ nguyên form nhập giá tiền) -->
                <div class="col-md-6">
                  <label class="form-label small fw-medium text-muted mb-1">Loại Giảm</label>
                  <select v-model="formData.discountType" class="form-select bg-light border-0 py-2">
                    <option value="PERCENTAGE">Giảm theo phần trăm (%)</option>
                    <option value="FIXED_AMOUNT">Giảm tiền mặt trực tiếp</option>
                  </select>
                </div>
                <div class="col-md-6">
                  <label class="form-label small fw-medium text-muted mb-1">Giá trị giảm <span class="text-danger">*</span></label>
                  <input v-model.number="formData.value" type="number" min="1" class="form-control bg-light border-0 py-2 fw-bold text-success" />
                </div>
                
                <div class="col-md-6">
                  <label class="form-label small fw-medium text-muted mb-1">Giá trị đơn tối thiểu <span class="text-danger">*</span></label>
                  <input v-model.number="formData.minOrderValue" type="number" min="0" class="form-control bg-light border-0 py-2" placeholder="0 = Áp dụng mọi đơn" />
                </div>
                <div class="col-md-6" v-if="formData.discountType === 'PERCENTAGE'">
                  <label class="form-label small fw-medium text-muted mb-1">Mức giảm tối đa <span class="text-danger">*</span></label>
                  <input v-model.number="formData.maxDiscountAmount" type="number" min="1" class="form-control bg-light border-0 py-2" />
                </div>
              </div>
            </div>

            <div class="bg-white rounded-4 border p-4 shadow-sm">
              <h6 class="fw-semibold text-dark mb-3">Điều kiện & Thời gian áp dụng</h6>
              <div class="row g-3">
                <div class="col-md-12">
                  <label class="form-label small fw-medium text-muted mb-1">Tổng lượt sử dụng tối đa <span class="text-danger">*</span></label>
                  <input v-model.number="formData.usageLimit" type="number" min="1" class="form-control bg-light border-0 py-2" />
                </div>
                <div class="col-md-6">
                  <label class="form-label small fw-medium text-muted mb-1">Thời gian bắt đầu <span class="text-danger">*</span></label>
                  <!-- Chặn chọn ngày giờ quá khứ -->
                  <input v-model="formData.startDate" type="datetime-local" :min="nowDateTime" class="form-control bg-light border-0 py-2" />
                </div>
                <div class="col-md-6">
                  <label class="form-label small fw-medium text-muted mb-1">Thời gian kết thúc <span class="text-danger">*</span></label>
                  <input v-model="formData.expiredAt" type="datetime-local" :min="formData.startDate || nowDateTime" class="form-control bg-light border-0 py-2" />
                </div>
              </div>
            </div>
          </div>

          <div class="mt-auto py-3 border-top bg-white d-flex justify-content-end gap-2 sticky-bottom px-4 mx-[-1.5rem]">
            <button type="button" @click="emit('close')" class="btn btn-light px-4 py-2 rounded-pill fw-medium">Hủy bỏ</button>
            <button type="submit" class="btn btn-dark px-5 py-2 rounded-pill fw-semibold">Lưu Voucher</button>
          </div>
          
        </form>
      </div>
    </div>
  </div>
</template>