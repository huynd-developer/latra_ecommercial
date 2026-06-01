<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'; // 🛠️ Đã gom gọn import computed lên đầu
import { useAppStore } from '@/common/store/app.store';
import { orderAdminService } from '../services/orderAdminService';
import type { Shipment, UpdateShipmentRequestDTO } from '../types/order.type';

const props = defineProps<{ shipment: Shipment; orderId: number }>();
const emit = defineEmits<{ (e: 'updated'): void }>();
const appStore = useAppStore();

// 🛠️ Hàm check quyền lấy trực tiếp từ localStorage đồng bộ với Directive
const isManagerOrOwner = computed(() => {
  const userRawData = localStorage.getItem('currentUser');
  if (!userRawData) return false;
  try {
    const parsedUser = JSON.parse(userRawData);
    let role = parsedUser.role || parsedUser.roles?.[0] || parsedUser.authorities?.[0]?.authority || parsedUser.authorities?.[0] || '';
    role = typeof role === 'string' ? role.toUpperCase().replace('ROLE_', '') : '';
    return ['MANAGER', 'OWNER'].includes(role);
  } catch (e) {
    return false;
  }
});

const formData = ref<UpdateShipmentRequestDTO>({
  carrier: '', trackingCode: '', status: 'PREPARING'
});

const addressData = ref({ name: '', phone: '', address: '' });
const isEditAddress = ref(false);

onMounted(() => {
  formData.value = { 
    carrier: props.shipment.carrier || '', 
    trackingCode: props.shipment.trackingCode || '', 
    status: props.shipment.status 
  };
  addressData.value = { 
    name: props.shipment.recipientName, 
    phone: props.shipment.recipientPhone, 
    address: props.shipment.fullAddress 
  };
});

const saveShipment = async () => {
  try {
    appStore.startLoading();
    await orderAdminService.updateShipment(props.shipment.id, formData.value);
    appStore.notifySuccess('Cập nhật vận đơn thành công!');
    emit('updated'); 
  } catch (err: any) {
    appStore.notifyError(err.response?.data?.message || 'Lỗi cập nhật vận chuyển');
  } finally {
    appStore.stopLoading();
  }
};

const saveAddress = async () => {
  try {
    appStore.startLoading();
    await orderAdminService.updateShippingAddress(props.orderId, addressData.value.name, addressData.value.phone, addressData.value.address);
    appStore.notifySuccess('Đổi địa chỉ thành công!');
    isEditAddress.value = false;
    emit('updated');
  } catch (err: any) {
    appStore.notifyError(err.response?.data?.message || 'Không thể đổi địa chỉ lúc này!');
  } finally {
    appStore.stopLoading();
  }
};
</script>

<template>
  <div class="card border-slate-200 shadow-sm mt-4">
    <div class="card-header bg-slate-50 fw-bold">
      <i class="bi bi-truck text-primary me-2"></i> Thông tin Vận chuyển
    </div>
    <div class="card-body">
      <div class="row mb-4">
        <div class="col-md-4">
          <label class="form-label small fw-bold">Trạng thái Giao hàng</label>
          <select 
            v-model="formData.status" 
            :disabled="!isManagerOrOwner" 
            class="form-select form-select-sm shadow-none font-bold text-primary"
          >
            <option value="PREPARING">Đang chuẩn bị hàng</option>
            <option value="PICKING">Shipper đang lấy hàng</option>
            <option value="DELIVERING">Đang giao hàng</option>
            <option value="DELIVERED">Đã giao thành công</option>
            <option value="RETURNED">Bom hàng (Hoàn về)</option>
            <option value="CANCELLED">Hủy giao</option>
          </select>
        </div>
        <div class="col-md-4">
          <label class="form-label small fw-bold">Đơn vị vận chuyển</label>
          <select 
            v-model="formData.carrier" 
            :disabled="!isManagerOrOwner" 
            class="form-select form-select-sm shadow-none"
          >
            <option value="">-- Chọn Hãng --</option>
            <option value="GHTK">Giao Hàng Tiết Kiệm</option>
            <option value="GHN">Giao Hàng Nhanh</option>
            <option value="VIETTEL_POST">Viettel Post</option>
            <option value="IN_HOUSE">Giao Nội Bộ</option>
          </select>
        </div>
        <div class="col-md-4">
          <label class="form-label small fw-bold">Mã Vận Đơn (Tracking)</label>
          <div class="d-flex gap-2">
            <input 
              v-model="formData.trackingCode" 
              :disabled="!isManagerOrOwner" 
              type="text" 
              class="form-control form-control-sm font-monospace" 
              placeholder="Nhập mã..."
            >
            <button 
              v-permission="['MANAGER', 'OWNER']" 
              @click="saveShipment" 
              class="btn btn-sm btn-dark text-nowrap"
            >
              Lưu Vận Đơn
            </button>
          </div>
        </div>
      </div>

      <div class="border-top pt-3">
        <div class="d-flex justify-content-between align-items-center mb-2">
          <label class="form-label small fw-bold mb-0">Địa chỉ nhận hàng (Của Khách)</label>
          <button 
            v-permission="['MANAGER', 'OWNER']" 
            v-if="!isEditAddress" 
            @click="isEditAddress = true" 
            class="btn btn-sm btn-link text-decoration-none p-0" 
            :disabled="shipment.status !== 'PREPARING'"
          >
            <i class="bi bi-pencil-square"></i> Đổi địa chỉ
          </button>
        </div>
        
        <div v-if="!isEditAddress" class="bg-light p-2 rounded text-sm text-slate-700">
          <span class="fw-bold">{{ addressData.name }}</span> | {{ addressData.phone }} <br>
          {{ addressData.address }}
        </div>

        <div v-else class="row g-2 p-3 bg-light border rounded">
          <div class="col-md-6"><input v-model="addressData.name" class="form-control form-control-sm" placeholder="Tên người nhận"></div>
          <div class="col-md-6"><input v-model="addressData.phone" class="form-control form-control-sm" placeholder="Số điện thoại"></div>
          <div class="col-12"><input v-model="addressData.address" class="form-control form-control-sm" placeholder="Địa chỉ chi tiết"></div>
          <div class="col-12 text-end mt-2">
            <button @click="isEditAddress = false" class="btn btn-sm btn-light me-2">Hủy</button>
            <button @click="saveAddress" class="btn btn-sm btn-primary">Xác nhận đổi</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>