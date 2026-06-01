<script setup lang="ts">
import { ref, onMounted } from "vue";
import Swal from "sweetalert2";
import type { Product, ProductRequestDTO, Variant } from "../types/product.type";
import { productService } from "../services/productService";
import { useAppStore } from "@/common/store/app.store";

const props = defineProps<{ productSelected: Product | null; brandList: any[]; categoryList: any[]; }>();
const emit = defineEmits<{ (e: "close"): void; (e: "refresh"): void; }>();

const appStore = useAppStore();
const isEdit = ref(false);

const dynamicColors = ref(["Đen", "Trắng", "Xám", "Vàng", "Titan", "Xanh", "Bạc", "Đỏ"]);
const dynamicStorages = ref(["64GB", "128GB", "256GB", "512GB", "1TB", "2TB"]);
const defaultConditions = ref(["New", "Like New 99%", "Cũ 97%"]);

const formData = ref<ProductRequestDTO>({
  name: "", brandId: "" as any, categoryId: "" as any, description: "", status: 1, variants: [],
});

onMounted(() => {
  if (props.productSelected) {
    isEdit.value = true;
    props.productSelected.variants?.forEach((v: any) => {
      if (v.color && !dynamicColors.value.includes(v.color)) dynamicColors.value.push(v.color);
      if (v.storage && !dynamicStorages.value.includes(v.storage)) dynamicStorages.value.push(v.storage);
    });

    formData.value = {
      name: props.productSelected.name, brandId: props.productSelected.brandId, categoryId: props.productSelected.categoryId,
      description: props.productSelected.description, status: props.productSelected.status !== undefined ? props.productSelected.status : 1,
      variants: props.productSelected.variants ? props.productSelected.variants.map((v: any) => ({
        id: v.id || v.variantId, color: v.color, storage: v.storage, condition: v.condition || "New", price: v.price, stock: v.stock, sku: v.sku,
      })) : [],
    };
  } else { addVariant(); }
});

const handleDynamicInput = async (event: Event, variant: Variant, type: 'color' | 'storage') => {
  const target = event.target as HTMLSelectElement;
  if (target.value === "ADD_NEW") {
    const isColor = type === 'color';
    const { value: newValue } = await Swal.fire({
      title: isColor ? 'Nhập màu sắc mới' : 'Nhập dung lượng mới', input: 'text', showCancelButton: true, confirmButtonText: 'Thêm'
    });
    if (newValue && newValue.trim() !== '') {
      const formattedValue = newValue.trim();
      if (isColor && !dynamicColors.value.includes(formattedValue)) dynamicColors.value.push(formattedValue);
      else if (!isColor && !dynamicStorages.value.includes(formattedValue)) dynamicStorages.value.push(formattedValue);
      variant[type] = formattedValue;
    } else { variant[type] = ""; }
  }
};

const addVariant = () => formData.value.variants.push({ color: "", storage: "", condition: "New", price: 0, stock: 0, sku: "" });
const removeVariant = (index: number) => formData.value.variants.splice(index, 1);

const saveData = async () => {
  const payload: ProductRequestDTO = {
    ...formData.value, brandId: Number(formData.value.brandId), categoryId: Number(formData.value.categoryId), status: Number(formData.value.status),
    variants: formData.value.variants.map((v) => ({ ...v, price: Number(v.price), stock: Number(v.stock), sku: v.sku.toUpperCase() }))
  };
  try {
    appStore.startLoading();
    if (isEdit.value && props.productSelected) await productService.updateProduct(props.productSelected.id, payload);
    else await productService.createProduct(payload);
    appStore.notifySuccess("Lưu hồ sơ thành công!"); emit("refresh"); emit("close");
  } catch (err: any) { appStore.notifyError(err.response?.data?.message || "Lỗi lưu dữ liệu!"); } 
  finally { appStore.stopLoading(); }
};
</script>

<template>
  <div>
    <div class="offcanvas-backdrop fade show" @click="emit('close')"></div>
    <div class="offcanvas offcanvas-end show shadow-lg border-0" tabindex="-1" style="visibility: visible; width: 1000px; max-width: 100vw;">
      <div class="offcanvas-header border-bottom px-4 py-3 bg-light bg-opacity-50">
        <h5 class="offcanvas-title fw-semibold">{{ isEdit ? "Cập Nhật Sản Phẩm" : "Thêm Sản Phẩm Mới" }}</h5>
        <button type="button" class="btn-close" @click="emit('close')"></button>
      </div>

      <div class="offcanvas-body px-4 p-0 custom-scrollbar d-flex flex-column">
        <form @submit.prevent="saveData" class="d-flex flex-column h-100">
          
          <div class="flex-grow-1 pb-5">
            <div class="bg-white rounded-4 border p-4 mb-4 mt-3 shadow-sm">
              <h6 class="fw-semibold text-dark mb-4">Thông tin cơ bản</h6>
              <div class="row g-3">
                <div class="col-md-12">
                  <label class="form-label small fw-medium text-muted mb-1">Tên Sản Phẩm <span class="text-danger">*</span></label>
                  <input v-model="formData.name" type="text" class="form-control bg-light border-0 py-2" required placeholder="Nhập tên sản phẩm..." />
                </div>
                <div class="col-md-4">
                  <label class="form-label small fw-medium text-muted mb-1">Thương Hiệu</label>
                  <select v-model="formData.brandId" class="form-select bg-light border-0 py-2" required>
                    <option value="" disabled>-- Chọn hãng --</option>
                    <option v-for="b in brandList" :key="b.id" :value="b.id">{{ b.name }}</option>
                  </select>
                </div>
                <div class="col-md-4">
                  <label class="form-label small fw-medium text-muted mb-1">Danh Mục</label>
                  <select v-model="formData.categoryId" class="form-select bg-light border-0 py-2" required>
                    <option value="" disabled>-- Chọn danh mục --</option>
                    <option v-for="c in categoryList" :key="c.id" :value="c.id">{{ c.name }}</option>
                  </select>
                </div>
                <div class="col-md-4">
                  <label class="form-label small fw-medium text-muted mb-1">Trạng thái</label>
                  <select v-model="formData.status" class="form-select bg-light border-0 py-2" required>
                    <option :value="1">Đang bán</option>
                    <option :value="0">Ngừng kinh doanh</option>
                  </select>
                </div>
                <div class="col-md-12">
                  <label class="form-label small fw-medium text-muted mb-1">Mô tả sản phẩm</label>
                  <textarea v-model="formData.description" class="form-control bg-light border-0 py-2" rows="2" placeholder="Ghi chú thêm..."></textarea>
                </div>
              </div>
            </div>

            <div class="bg-white rounded-4 border p-4 shadow-sm">
              <div class="d-flex justify-content-between align-items-center mb-3">
                <h6 class="fw-semibold text-dark mb-0">Cấu hình Phiên Bản (Variants)</h6>
                <button type="button" @click="addVariant" class="btn btn-sm btn-dark rounded-pill px-3 fw-medium">+ Thêm Phiên Bản</button>
              </div>
              
              <div class="table-responsive">
                 <table class="table align-middle mb-0">
                   <thead class="text-muted small">
                     <tr>
                       <th class="border-bottom-0 pb-2">Màu sắc</th>
                       <th class="border-bottom-0 pb-2">Dung lượng</th>
                       <th class="border-bottom-0 pb-2">Tình trạng</th>
                       <th class="border-bottom-0 pb-2">SKU</th>
                       <th class="border-bottom-0 pb-2">Giá bán (VNĐ)</th>
                       <th class="border-bottom-0 pb-2" width="100">Kho</th>
                       <th class="border-bottom-0 pb-2"></th>
                     </tr>
                   </thead>
                   <tbody>
                     <tr v-for="(variant, index) in formData.variants" :key="index">
                        <td class="ps-0 py-2 border-bottom-0">
                          <select v-model="variant.color" @change="handleDynamicInput($event, variant, 'color')" class="form-select form-select-sm bg-light border-0" required>
                            <option value="" disabled>Chọn màu</option>
                            <option v-for="c in dynamicColors" :key="c" :value="c">{{ c }}</option>
                            <option value="ADD_NEW" class="fw-bold text-primary">+ Thêm màu...</option>
                          </select>
                        </td>
                        <td class="py-2 border-bottom-0">
                          <select v-model="variant.storage" @change="handleDynamicInput($event, variant, 'storage')" class="form-select form-select-sm bg-light border-0" required>
                            <option value="" disabled>Dung lượng</option>
                            <option v-for="s in dynamicStorages" :key="s" :value="s">{{ s }}</option>
                            <option value="ADD_NEW" class="fw-bold text-primary">+ Thêm DL...</option>
                          </select>
                        </td>
                        <td class="py-2 border-bottom-0">
                          <select v-model="variant.condition" class="form-select form-select-sm bg-light border-0" required>
                            <option v-for="cond in defaultConditions" :key="cond" :value="cond">{{ cond }}</option>
                          </select>
                        </td>
                        <td class="py-2 border-bottom-0"><input v-model="variant.sku" type="text" class="form-control form-control-sm bg-light border-0" placeholder="VD: IPH15-TITAN..." required /></td>
                        <td class="py-2 border-bottom-0"><input v-model.number="variant.price" type="number" class="form-control form-control-sm bg-light border-0 text-success fw-medium" required /></td>
                        <td class="py-2 border-bottom-0"><input v-model.number="variant.stock" type="number" class="form-control form-control-sm bg-secondary bg-opacity-10 border-0" readonly title="Tồn kho sinh ra từ module IMEI" /></td>
                        <td class="text-end pe-0 py-2 border-bottom-0">
                          <button type="button" @click="removeVariant(index)" class="btn btn-sm btn-light text-danger rounded-circle"><svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M3 6h18"></path><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path></svg></button>
                        </td>
                     </tr>
                   </tbody>
                 </table>
              </div>
            </div>
          </div>

          <div class="mt-auto py-3 border-top bg-white d-flex justify-content-end gap-2 sticky-bottom">
            <button type="button" @click="emit('close')" class="btn btn-light px-4 py-2 rounded-pill fw-medium">Hủy bỏ</button>
            <button v-permission="['OWNER', 'MANAGER']" type="submit" class="btn btn-dark px-5 py-2 rounded-pill fw-semibold">Lưu Hồ Sơ</button>
          </div>
          
        </form>
      </div>
    </div>
  </div>
</template>

<style scoped>
.custom-scrollbar::-webkit-scrollbar { width: 6px; }
.custom-scrollbar::-webkit-scrollbar-thumb { background: #e2e8f0; border-radius: 10px; }
.form-control:focus, .form-select:focus { box-shadow: none; border: 1px solid #cbd5e1 !important; }
</style>