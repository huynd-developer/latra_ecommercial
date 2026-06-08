<script setup lang="ts">
import { usePos } from "../composables/usePos";

const { printData, formatMoney } = usePos();
</script>

<template>
  <div id="print-receipt" class="receipt-zone d-none d-print-block">
    <div class="receipt-header">
      <h1 class="store-name">LATRA</h1>
      <p>Hệ Thống Bán Lẻ Điện Thoại Cao Cấp</p>
      <p>Địa chỉ: 123 Đường Cầu Giấy, Hà Nội</p>
      <p>Hotline: 090.100.0001</p>
      <h2 class="receipt-title">HÓA ĐƠN BÁN HÀNG</h2>
      <p class="barcode-fake">|||| | |||||| || |||| |||</p>
    </div>

    <div class="divider"></div>

    <div class="customer-info">
      <p>Khách hàng: {{ printData.customerName || "Khách vãng lai" }}</p>
      <p>Điện thoại: {{ printData.customerPhone || "N/A" }}</p>
      <p>Ngày mua: {{ new Date().toLocaleString("vi-VN") }}</p>
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
            <td class="text-end fw-bold" style="padding-top: 8px">
              {{ formatMoney(item.price) }}
            </td>
          </tr>
          <tr>
            <td
              colspan="2"
              class="text-start item-desc font-monospace"
              style="padding-bottom: 4px"
            >
               S/N IMEI: {{ item.imeiCode }}
            </td>
          </tr>
          <tr v-if="item.warrantyFee > 0">
            <td
              class="text-start item-desc"
              style="padding-left: 8px; font-style: italic"
            >
              + {{ item.warrantyName }}
            </td>
            <td class="text-end item-desc" style="font-style: italic">
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
          {{
            printData.method === "CASH"
              ? "Tiền mặt"
              : printData.method === "TRANSFER"
              ? "Chuyển khoản"
              : "Hỗn hợp"
          }}
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
      <p class="policy">
        Lưu ý: Quý khách vui lòng giữ hóa đơn này kết hợp mã IMEI máy để đối
        chiếu kích hoạt hệ thống bảo hành điện tử.
      </p>
      <p style="margin-top: 15px; font-size: 10px">Powered by LATRA POS v2.0</p>
    </div>
  </div>
</template>

<style>
/* Định dạng cấu hình giấy in nhiệt cuộn K80 tiêu chuẩn */
@page {
  size: 80mm auto;
  margin: 0 !important;
}

@media print {
  /* 1. TÀNG HÌNH TOÀN BỘ WEBSITE (Giấu luôn cả Header ở component cha) */
  body * {
    visibility: hidden !important;
  }

  /* 2. CHỈ CHO PHÉP HÓA ĐƠN VÀ CON CỦA NÓ HIỂN THỊ */
  #print-receipt,
  #print-receipt * {
    visibility: visible !important;
  }

  /* 3. BỐC HÓA ĐƠN LÊN TRÊN CÙNG & CĂN GIỮA TUYỆT ĐỐI */
  #print-receipt {
    position: absolute !important;
    top: 0 !important;
    left: 0 !important;
    right: 0 !important;
    margin: 0 auto !important; /* Lệnh này giúp hóa đơn luôn nằm chính giữa */
    width: 80mm !important;
    padding: 6mm 4mm !important;
    background-color: #ffffff !important;
    color: #000000 !important;
    font-family: Arial, Helvetica, sans-serif !important;
  }

  /* --- CÁC STYLE CHI TIẾT BÊN TRONG HÓA ĐƠN LÀM NÉT CHỮ --- */
  .receipt-header {
    text-align: center;
    margin-bottom: 12px;
  }
  .store-name {
    font-size: 18px;
    font-weight: 900;
    margin: 0 0 4px 0;
    text-transform: uppercase;
  }
  .receipt-title {
    font-size: 14px;
    font-weight: bold;
    margin: 12px 0 6px 0;
  }
  .receipt-zone p {
    margin: 4px 0;
    font-size: 12px;
    line-height: 1.4;
  }
  .customer-info {
    margin: 10px 0;
    text-align: left;
  }

  .divider {
    border-top: 1px dashed #000000 !important;
    border-bottom: none !important;
    margin: 8px 0 !important;
    height: 0 !important;
  }

  .receipt-table {
    width: 100% !important;
    border-collapse: collapse !important;
    font-size: 12px;
    margin: 8px 0;
  }
  .receipt-table th {
    border-bottom: 1px dashed #000000 !important;
    padding: 6px 0 !important;
    font-weight: bold;
  }
  .receipt-table td {
    padding: 5px 0 !important;
    vertical-align: top;
  }
  .item-name {
    font-weight: bold;
    font-size: 12px;
  }
  .item-desc {
    font-size: 11px;
    color: #111111;
    padding-top: 2px !important;
  }

  .text-start {
    text-align: left !important;
  }
  .text-end {
    text-align: right !important;
  }

  .receipt-summary {
    font-size: 12px;
  }
  .summary-line {
    display: flex !important;
    justify-content: space-between !important;
    margin: 5px 0 !important;
  }
  .total-line {
    font-size: 14px;
    font-weight: 900;
    border-top: 1px dashed #000000 !important;
    border-bottom: 1px dashed #000000 !important;
    padding: 6px 0 !important;
    margin-top: 8px !important;
  }

  .receipt-footer {
    text-align: center;
    margin-top: 15px;
  }
  .policy {
    font-size: 11px;
    font-style: italic;
    line-height: 1.3;
  }
  .barcode-fake {
    font-family: "Courier New", Courier, monospace;
    font-size: 12px;
    letter-spacing: 2px;
    font-weight: bold;
    margin-top: 8px;
    text-align: center;
  }
}
</style>
