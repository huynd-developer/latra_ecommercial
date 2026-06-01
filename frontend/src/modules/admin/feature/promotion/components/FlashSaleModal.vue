<script setup lang="ts">
import { ref, onMounted, computed } from "vue";
import { useAppStore } from "@/common/store/app.store";
import { promotionService } from "../services/promotionService";
import type { FlashSale } from "../types/promotion.type";
import { useProductStore } from "../../product/stores/productStore";
import { ROLES } from "@/common/constants/permissions";

const props = defineProps<{ flashSaleSelected: FlashSale | null }>();
const emit = defineEmits<{ (e: "close"): void; (e: "refresh"): void }>();
const appStore = useAppStore();
const productStore = useProductStore();
const isEdit = ref(false);

const formData = ref<any>({
  name: "",
  startDate: "",
  startTimeStr: "",
  endDate: "",
  endTimeStr: "",
  items: [],
});

// Lấy ngày hiện tại để chặn chọn ngày quá khứ
const todayDate = computed(() => {
  const d = new Date();
  // Điều chỉnh múi giờ cho chuẩn local
  d.setMinutes(d.getMinutes() - d.getTimezoneOffset());
  return d.toISOString().substring(0, 10);
});

onMounted(async () => {
  if (productStore.products.length === 0) await productStore.fetchProducts();

  if (props.flashSaleSelected) {
    isEdit.value = true;
    const st = props.flashSaleSelected.startTime;
    const et = props.flashSaleSelected.endTime;
    formData.value = {
      id: props.flashSaleSelected.id,
      name: props.flashSaleSelected.name,
      startDate: st.substring(0, 10),
      startTimeStr: st.substring(11, 16),
      endDate: et.substring(0, 10),
      endTimeStr: et.substring(11, 16),
      items: props.flashSaleSelected.items.map((i) => ({
        variantId: i.productVariant?.id || i.variantId,
        salePrice: i.salePrice,
        quantityLimit: i.quantityLimit,
      })),
    };
  } else {
    formData.value = {
      name: "",
      startDate: todayDate.value,
      startTimeStr: "00:00",
      endDate: todayDate.value,
      endTimeStr: "23:59",
      items: [],
    };
  }
});

const addItem = () =>
  formData.value.items.push({ variantId: "", salePrice: 0, quantityLimit: 1 });
// Fix lỗi TS Argument of type 'string | number'
const removeItem = (idx: any) => formData.value.items.splice(Number(idx), 1);

const saveData = async () => {
  const cleanName = formData.value.name.trim();
  if (!cleanName)
    return appStore.notifyError("Tên chương trình không được để trống!");

  if (
    !formData.value.startDate ||
    !formData.value.startTimeStr ||
    !formData.value.endDate ||
    !formData.value.endTimeStr
  ) {
    return appStore.notifyError(
      "Vui lòng chọn đầy đủ ngày giờ bắt đầu và kết thúc!",
    );
  }

  if (formData.value.items.length === 0)
    return appStore.notifyError(
      "Chương trình phải có ít nhất 1 sản phẩm tham gia!",
    );

  const start = new Date(
    `${formData.value.startDate}T${formData.value.startTimeStr}:00`,
  );
  const end = new Date(
    `${formData.value.endDate}T${formData.value.endTimeStr}:00`,
  );
  const now = new Date();

  if (start < now && !isEdit.value)
    return appStore.notifyError(
      "Thời gian bắt đầu không được nằm trong quá khứ!",
    );
  if (end <= start)
    return appStore.notifyError(
      "Thời gian kết thúc phải diễn ra SAU thời gian bắt đầu!",
    );

  const uniqueVariants = new Set();
  for (let i = 0; i < formData.value.items.length; i++) {
    const item = formData.value.items[i];
    if (!item.variantId)
      return appStore.notifyError(
        `Dòng ${i + 1}: Vui lòng chọn phiên bản sản phẩm!`,
      );
    if (item.salePrice <= 0)
      return appStore.notifyError(`Dòng ${i + 1}: Giá Sale phải lớn hơn 0!`);
    if (item.quantityLimit < 1)
      return appStore.notifyError(
        `Dòng ${i + 1}: Số lượng giới hạn tối thiểu là 1!`,
      );
    if (uniqueVariants.has(item.variantId))
      return appStore.notifyError(
        `Dòng ${i + 1}: Sản phẩm này đã bị trùng lặp!`,
      );
    uniqueVariants.add(item.variantId);
  }

  const payload: FlashSale = {
    id: formData.value.id,
    name: cleanName,
    startTime: `${formData.value.startDate}T${formData.value.startTimeStr}:00`,
    endTime: `${formData.value.endDate}T${formData.value.endTimeStr}:00`,
    status: "UPCOMING",
    items: formData.value.items.map((i: any) => ({
      variantId: Number(i.variantId),
      salePrice: Number(i.salePrice),
      quantityLimit: Number(i.quantityLimit),
    })),
  };

  try {
    appStore.startLoading();
    if (isEdit.value && payload.id)
      await promotionService.updateFlashSale(payload.id, payload);
    else await promotionService.createFlashSale(payload);

    appStore.notifySuccess("Lưu chiến dịch thành công!");
    emit("refresh");
    emit("close");
  } catch (error: any) {
    appStore.notifyError(
      error.response?.data?.message ||
        "Lỗi khung giờ hoặc sản phẩm đã tham gia đợt Sale khác!",
    );
  } finally {
    appStore.stopLoading();
  }
};
</script>

<template>
  <div class="modal fade show d-block custom-backdrop" tabindex="-1" @click.self="emit('close')">
    <div class="modal-dialog modal-dialog-centered modal-dialog-scrollable" style="max-width: 900px;">
      
      <form @submit.prevent="saveData" novalidate class="modal-content shadow-lg border-0 rounded-4 overflow-hidden">
        
        <div class="modal-header border-bottom px-4 py-3 bg-light bg-opacity-75">
          <h5 class="modal-title fw-semibold text-dark">
            {{
              isEdit
                ? "Cập Nhật Chiến Dịch Flash Sale"
                : "Tạo Chiến Dịch Flash Sale Mới"
            }}
          </h5>
          <button type="button" class="btn-close shadow-none" @click="emit('close')"></button>
        </div>

        <div class="modal-body p-4 bg-light bg-opacity-25 custom-scrollbar">
          
          <div class="bg-white rounded-4 border p-4 mb-4 shadow-sm">
            <h6 class="fw-semibold text-dark mb-4">Cài đặt cơ bản</h6>
            <div class="row g-3">
              <div class="col-md-12">
                <label class="form-label small fw-medium text-muted mb-1">
                  Tên Chương Trình <span class="text-danger">*</span>
                </label>
                <input
                  v-model="formData.name"
                  class="form-control bg-light border-0 py-2 custom-focus"
                  placeholder="VD: Sale giữa tháng 15/05..."
                />
              </div>

              <div class="col-md-6">
                <div class="p-3 border rounded-3 bg-light bg-opacity-50">
                  <label class="form-label small fw-bold text-success mb-2">
                    <i class="bi bi-play-circle-fill me-1"></i>BẮT ĐẦU
                  </label>
                  <div class="d-flex gap-2">
                    <input
                      v-model="formData.startDate"
                      type="date"
                      :min="todayDate"
                      class="form-control border-0 custom-focus"
                    />
                    <input
                      v-model="formData.startTimeStr"
                      type="time"
                      class="form-control border-0 custom-focus"
                    />
                  </div>
                </div>
              </div>

              <div class="col-md-6">
                <div class="p-3 border rounded-3 bg-light bg-opacity-50">
                  <label class="form-label small fw-bold text-danger mb-2">
                    <i class="bi bi-stop-circle-fill me-1"></i>KẾT THÚC
                  </label>
                  <div class="d-flex gap-2">
                    <input
                      v-model="formData.endDate"
                      type="date"
                      :min="formData.startDate || todayDate"
                      class="form-control border-0 custom-focus"
                    />
                    <input
                      v-model="formData.endTimeStr"
                      type="time"
                      class="form-control border-0 custom-focus"
                    />
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div class="bg-white rounded-4 border p-4 shadow-sm">
            <div class="d-flex justify-content-between align-items-center mb-3">
              <div>
                <h6 class="fw-semibold text-dark mb-1">
                  Sản phẩm áp dụng Sale
                </h6>
                <p class="small text-muted mb-0">
                  Thiết lập giá sốc và giới hạn số lượng bán ra.
                </p>
              </div>
              <button
                type="button"
                @click="addItem"
                v-permission="[ROLES.OWNER, ROLES.MANAGER]"
                class="btn btn-sm btn-dark rounded-pill px-3 fw-medium transition-all"
              >
                + Thêm Sản Phẩm
              </button>
            </div>

            <div class="table-responsive">
              <table class="table align-middle mb-0">
                <thead class="text-muted small">
                  <tr>
                    <th class="border-bottom-0 pb-2">Phiên bản Variant</th>
                    <th class="border-bottom-0 pb-2">Giá Sale (VNĐ)</th>
                    <th class="border-bottom-0 pb-2" width="120">
                      SL Giới hạn
                    </th>
                    <th class="border-bottom-0 pb-2"></th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-if="formData.items.length === 0">
                    <td
                      colspan="4"
                      class="text-center py-4 text-muted small border-bottom-0"
                    >
                      Chưa có sản phẩm nào được chọn.
                    </td>
                  </tr>
                  <tr v-for="(item, idx) in formData.items" :key="idx">
                    <td class="ps-0 py-2 border-bottom-0">
                      <select
                        v-model="item.variantId"
                        class="form-select form-select-sm bg-light border-0 custom-focus"
                      >
                        <option value="" disabled>-- Chọn sản phẩm --</option>
                        <optgroup
                          v-for="p in productStore.products"
                          :key="p.id!"
                          :label="p.name"
                        >
                          <option
                            v-for="v in p.variants"
                            :key="v.variantId!"
                            :value="v.variantId"
                          >
                            {{ v.color }} - {{ v.storage }} (Kho:
                            {{ v.stock }})
                          </option>
                        </optgroup>
                      </select>
                    </td>
                    <td class="py-2 border-bottom-0">
                      <input
                        v-model.number="item.salePrice"
                        type="number"
                        min="0"
                        class="form-control form-control-sm bg-light border-0 text-success fw-bold custom-focus"
                      />
                    </td>
                    <td class="py-2 border-bottom-0">
                      <input
                        v-model.number="item.quantityLimit"
                        type="number"
                        min="1"
                        class="form-control form-control-sm bg-light border-0 text-center custom-focus"
                      />
                    </td>
                    <td class="text-end pe-0 py-2 border-bottom-0">
                      <button
                        type="button"
                        @click="removeItem(idx)"
                        v-permission="[ROLES.OWNER, ROLES.MANAGER]"
                        class="btn btn-sm btn-light text-danger rounded-circle hover-danger"
                      >
                        <svg
                          width="14"
                          height="14"
                          viewBox="0 0 24 24"
                          fill="none"
                          stroke="currentColor"
                          stroke-width="2"
                          stroke-linecap="round"
                          stroke-linejoin="round"
                        >
                          <path d="M3 6h18"></path>
                          <path
                            d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"
                          ></path>
                        </svg>
                      </button>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
          
        </div>

        <div class="modal-footer bg-white border-top px-4 py-3 d-flex justify-content-end gap-2">
          <button
            type="button"
            @click="emit('close')"
            class="btn btn-light px-4 py-2 rounded-pill fw-medium border"
          >
            Hủy bỏ
          </button>
          <button
            type="submit"
            v-permission="[ROLES.OWNER, ROLES.MANAGER]"
            class="btn btn-dark px-5 py-2 rounded-pill fw-semibold transition-all"
          >
            Lưu Chiến Dịch
          </button>
        </div>

      </form>
    </div>
  </div>
</template>

<style scoped>
/* Lớp phủ đen bán trong suốt giống như Backdrop của Modal chuẩn */
.custom-backdrop {
  background-color: rgba(15, 23, 42, 0.4);
  backdrop-filter: blur(3px);
}

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

/* Tùy chỉnh hiệu ứng khi click vào các trường nhập liệu */
.custom-focus:focus {
  box-shadow: 0 0 0 3px rgba(15, 23, 42, 0.1) !important;
  border-color: #cbd5e1 !important;
  outline: none;
}

/* Các hiệu ứng Hover */
.transition-all {
  transition: all 0.2s ease-in-out;
}
.transition-all:hover {
  transform: translateY(-1px);
}

.hover-danger {
  transition: all 0.2s ease;
}
.hover-danger:hover {
  background-color: #fee2e2 !important;
  color: #dc2626 !important;
}
</style>