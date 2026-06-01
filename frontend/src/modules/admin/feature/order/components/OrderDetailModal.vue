<script setup lang="ts">
import { computed } from "vue";
import { useAppStore } from "@/common/store/app.store";
import { orderAdminService } from "../services/orderAdminService";
import ShipmentUpdateCard from "./ShipmentUpdateCard.vue";
import type { Order } from "../types/order.type";
import { formatCurrency, formatDate } from "@/common/utils/format";

const props = defineProps<{ order: Order | null }>();
const emit = defineEmits<{ (e: "close"): void; (e: "refresh"): void }>();
const appStore = useAppStore();

const processRefund = async () => {
  if (!props.order) return;
  if (
    !confirm(
      "Bạn có chắc chắn muốn Hủy và Hoàn tiền (Refund) đơn hàng này qua VNPAY? Tiền sẽ được hoàn lại thẻ của khách."
    )
  )
    return;

  try {
    appStore.startLoading();
    await orderAdminService.refundOrder(props.order.id);
    appStore.notifySuccess("Đã gọi lệnh hoàn tiền VNPAY thành công!");
    emit("refresh"); // Reload đơn
  } catch (error: any) {
    appStore.notifyError(
      error.response?.data?.message || "Lỗi từ chối hoàn tiền VNPAY"
    );
  } finally {
    appStore.stopLoading();
  }
};
</script>

<template>
  <div
    class="fixed inset-0 z-[1050] flex items-center justify-center bg-black bg-opacity-50 backdrop-blur-sm"
    @mousedown.self="emit('close')"
  >
    <div
      class="bg-white rounded-xl shadow-lg w-full max-w-4xl max-h-[90vh] flex flex-col overflow-hidden"
    >
      <div
        class="bg-slate-900 text-white p-4 flex justify-between items-center"
      >
        <h5 class="mb-0 font-bold flex items-center gap-2">
          <i class="bi bi-receipt-cutoff"></i> Hóa Đơn
          <span class="text-slate-300">#{{ order?.id }}</span>
        </h5>
        <button
          @click="emit('close')"
          class="text-white hover:text-gray-300 text-2xl"
        >
          &times;
        </button>
      </div>

      <div class="p-0 overflow-y-auto flex-grow custom-scrollbar">
        <div class="row g-0 border-b bg-gray-50">
          <div class="col-md-4 p-4 border-r">
            <div class="text-gray-500 text-xs font-bold uppercase mb-1">
              Khách Hàng
            </div>
            <div class="font-bold text-gray-800">
              {{ order?.customerName || "Khách vãng lai" }}
            </div>
            <div class="text-gray-600 text-sm mt-1">
              {{ order?.customerPhone || "Không có SĐT" }}
            </div>
          </div>
          <div class="col-md-4 p-4 border-r">
            <div class="text-gray-500 text-xs font-bold uppercase mb-1">
              Giao Dịch
            </div>
            <div class="font-bold text-gray-800">
              {{ formatDate(order?.createdAt) }}
            </div>
            <div class="text-gray-600 text-sm mt-1">
              Trạng thái:
              <span class="text-primary font-bold">{{ order?.status }}</span>
            </div>
          </div>
          <div class="col-md-4 p-4">
            <div class="text-gray-500 text-xs font-bold uppercase mb-1">
              Thanh Toán
            </div>
            <div class="font-bold text-gray-800">
              <span
                v-if="order?.paymentMethod === 'VNPAY'"
                class="text-blue-600"
                ><i class="bi bi-qr-code-scan"></i> VNPAY ({{
                  order?.paymentTransaction?.status
                }})</span
              >
              <span v-else class="text-green-600"
                ><i class="bi bi-cash-stack"></i> Tiền mặt (COD)</span
              >
            </div>
            <button
              v-permission="['MANAGER', 'OWNER']"
              v-if="
                order?.paymentMethod === 'VNPAY' &&
                order?.paymentTransaction?.status === 'SUCCESS' &&
                order?.status !== 'REFUNDED'
              "
              @click="processRefund"
              class="btn btn-sm btn-outline-danger mt-2 fw-bold"
            >
              <i class="bi bi-arrow-counterclockwise"></i> Hoàn Tiền (Refund)
            </button>
          </div>
        </div>

        <div class="p-4" v-if="order?.shipment">
          <ShipmentUpdateCard
            :shipment="order.shipment"
            :order-id="order.id"
            @updated="emit('refresh')"
          />
        </div>

        <div class="p-4">
          <h6 class="font-bold text-gray-800 mb-3">
            <i class="bi bi-box-seam me-2"></i>Danh sách thiết bị
          </h6>
          <table class="table table-sm border rounded">
            <thead class="bg-light text-xs uppercase text-gray-500">
              <tr>
                <th class="ps-3 py-2">Sản phẩm</th>
                <th>Mã IMEI</th>
                <th class="text-end pe-3">Giá bán</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="item in order?.items" :key="item.id">
                <td class="ps-3">
                  <div class="font-bold">{{ item.productName }}</div>
                  <div class="text-xs text-gray-500">
                    {{ item.variantName }}
                  </div>
                </td>
                <td class="font-mono text-sm font-bold">{{ item.imeiCode }}</td>
                <td class="text-end pe-3 font-bold text-gray-800">
                  {{ formatCurrency(item.price) }}
                </td>
              </tr>
            </tbody>
          </table>
          <div class="text-end pt-2">
            <h4 class="font-black text-red-600">
              Tổng cộng: {{ formatCurrency(order?.totalAmount) }}
            </h4>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
