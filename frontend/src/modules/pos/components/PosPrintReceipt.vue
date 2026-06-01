<script setup lang="ts">
import { usePos } from '../composables/usePos'

const { printData, formatMoney } = usePos()
</script>

<template>
  <div id="print-receipt" class="d-none">
    <div class="receipt-header">
      <h1 class="store-name">LATRA STORE</h1>
      <p>Hệ Thống Bán Lẻ Điện Thoại Cao Cấp</p>
      <p>Địa chỉ: 123 Đường Cầu Giấy, Hà Nội</p>
      <p>Hotline: 090.100.0001</p>
      <h2 class="receipt-title">HÓA ĐƠN BÁN HÀNG</h2>
      <p class="barcode-fake">|||| | |||||| || |||| |||</p>
    </div>

    <div class="divider"></div>

    <div class="customer-info">
      <p>Khách hàng: {{ printData.customerName || 'Khách vãng lai' }}</p>
      <p>Điện thoại: {{ printData.customerPhone }}</p>
      <p>Ngày mua: {{ new Date().toLocaleString('vi-VN') }}</p>
    </div>

    <div class="divider"></div>

    <table class="receipt-table">
      <thead>
        <tr>
          <th class="text-start">Sản phẩm / IMEI</th>
          <th class="text-end">Thành tiền</th>
        </tr>
      </thead>
      <tbody>
        <template v-for="item in printData.items" :key="item.imeiCode">
          <tr>
            <td class="text-start item-name">
              {{ item.name }} ({{ item.storage }} - {{ item.color }})
            </td>
            <td class="text-end fw-bold" style="padding-top: 8px;">
              {{ formatMoney(item.price) }}
            </td>
          </tr>
          <tr>
            <td colspan="2" class="text-start item-desc font-monospace" style="padding-bottom: 4px;">
              ⚡ S/N IMEI: {{ item.imeiCode }}
            </td>
          </tr>
          <tr v-if="item.warrantyFee > 0">
            <td class="text-start item-desc" style="padding-left: 8px; font-style: italic;">
              + {{ item.warrantyName }}
            </td>
            <td class="text-end item-desc" style="font-style: italic;">
              {{ formatMoney(item.warrantyFee) }}
            </td>
          </tr>
        </template>
      </tbody>
    </table>

    <div class="divider"></div>

    <div class="receipt-summary">
      <div class="summary-line">
        <span>Tạm tính hệ thống:</span>
        <span>{{ formatMoney(printData.subtotal) }}</span>
      </div>
      <div v-if="printData.discount > 0" class="summary-line fw-bold">
        <span>Khuyến mãi Voucher:</span>
        <span>-{{ formatMoney(printData.discount) }}</span>
      </div>
      
      <div class="total-line summary-line">
        <span>TỔNG TIỀN THANH TOÁN:</span>
        <span>{{ formatMoney(printData.total) }}</span>
      </div>

      <div class="summary-line">
        <span>Phương thức:</span>
        <span class="fw-bold">
          {{ printData.method === 'CASH' ? 'Tiền mặt' : (printData.method === 'TRANSFER' ? 'Chuyển khoản' : 'Hỗn hợp') }}
        </span>
      </div>
      <div v-if="printData.customerGiven > 0" class="summary-line">
        <span>Tiền mặt đã nhận:</span>
        <span>{{ formatMoney(printData.customerGiven) }}</span>
      </div>
      <div v-if="printData.transferAmount > 0" class="summary-line">
        <span>Chuyển khoản đã nhận:</span>
        <span>{{ formatMoney(printData.transferAmount) }}</span>
      </div>
    </div>

    <div class="divider"></div>

    <div class="receipt-footer text-center">
      <p class="fw-bold">CẢM ƠN QUÝ KHÁCH ĐÃ ỦY THÁC MUA SẮM!</p>
      <p class="policy">Lưu ý: Quý khách vui lòng giữ hóa đơn này kết hợp mã IMEI máy để đối chiếu kích hoạt hệ thống bảo hành điện tử.</p>
      <p style="margin-top: 15px; font-size: 10px;">Powered by LATRA POS v2.0</p>
    </div>
  </div>
</template>