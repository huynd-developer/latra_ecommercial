<script setup>
import { onMounted, onUnmounted } from "vue";
import { usePos } from "./composables/usePos";

// Import các linh kiện
import PosHeaderScanner from "./components/PosHeaderScanner.vue";
import PosProductGrid from "./components/PosProductGrid.vue";
import PosCartSidebar from "./components/PosCartSidebar.vue";
import PosPaymentModal from "./components/PosPaymentModal.vue";
import PosPrintReceipt from "./components/PosPrintReceipt.vue";

const { fetchWarehouseData, openPaymentModal } = usePos();

const handleF9 = (e) => {
  if (e.key === "F9") {
    e.preventDefault();
    openPaymentModal();
  }
};

onMounted(() => {
  fetchWarehouseData();
  window.addEventListener("keydown", handleF9);
});

onUnmounted(() => {
  window.removeEventListener("keydown", handleF9);
});
</script>

<template>
  <div class="pos-container">
    <div class="h-100 row g-4 p-3 bg-slate-50 d-print-none">
      <div class="col-xl-8 col-lg-7 d-flex flex-column h-100">
        <PosHeaderScanner />
        <PosProductGrid />
      </div>

      <div class="col-xl-4 col-lg-5 h-100 d-flex flex-column">
        <PosCartSidebar />
      </div>

      <PosPaymentModal />
    </div>

    <PosPrintReceipt />
  </div>
</template>
<style>
/* Đã bỏ 'scoped' để CSS ăn thẳng vào tất cả component con */
.bg-slate-50 { background-color: #f8fafc !important; }
.bg-slate-100 { background-color: #f1f5f9 !important; }
.bg-slate-800 { background-color: #1e293b !important; }
.bg-slate-900 { background-color: #0f172a !important; }
.text-slate-900 { color: #0f172a !important; }
.text-slate-800 { color: #1e293b !important; }
.text-slate-700 { color: #334155 !important; }
.text-slate-600 { color: #475569 !important; }
.text-slate-500 { color: #64748b !important; }
.text-slate-400 { color: #94a3b8 !important; }
.border-slate-50 { border-color: #f8fafc !important; }
.border-slate-100 { border-color: #f1f5f9 !important; }
.border-slate-200 { border-color: #e2e8f0 !important; }
.border-slate-300 { border-color: #cbd5e1 !important; }
.btn-dark-peel { background-color: #1e293b; color: white; border: none; transition: all 0.2s; }
.btn-dark-peel:hover:not(:disabled) { background-color: #0f172a; transform: translateY(-1px); }
.btn-outline-dark { color: #1e293b; border: 1px solid #cbd5e1; background: white; transition: 0.2s; }
.btn-outline-dark:hover { background: #f8fafc; border-color: #1e293b; }
.focus-ring-peel:focus, .focus-ring-peel:focus-within { border-color: #1e293b !important; box-shadow: 0 0 0 3px rgba(30, 41, 59, 0.1) !important; }
.product-card { border: 1px solid #f1f5f9; cursor: pointer; }
.hover-lift:hover { transform: translateY(-4px); border-color: #cbd5e1; box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1) !important; }
.product-img { transition: transform 0.3s ease; }
.hover-lift:hover .product-img { transform: scale(1.05); }
.grayscale { filter: grayscale(100%); }
.btn-light-peel { width: 32px; height: 32px; border-radius: 50%; background: #f1f5f9; color: #64748b; transition: all 0.2s; border: none; }
.product-card:hover .btn-light-peel { background: #1e293b; color: white; }
.btn-remove { position: absolute; right: 8px; top: 8px; border: none; background: none; color: #cbd5e1; font-size: 1.2rem; transition: 0.2s; padding: 0; }
.btn-remove:hover { color: #ef4444; transform: scale(1.1); }
.fw-black { font-weight: 900 !important; }
.tracking-tight { letter-spacing: -0.025em; }
.tracking-wider { letter-spacing: 0.05em; }
.section-label { font-size: 0.75rem; font-weight: 800; color: #64748b; text-transform: uppercase; letter-spacing: 0.05rem; }
.text-nowrap { white-space: nowrap !important; }
.border-dashed { border-style: dashed !important; }
.pay-method-card { flex: 1; border: 2px solid #e2e8f0; padding: 12px; border-radius: 12px; cursor: pointer; text-align: center; transition: 0.2s; background: white; color: #64748b; font-weight: 600; }
.pay-method-card i { font-size: 1.8rem; color: #94a3b8; transition: 0.2s; }
.pay-method-card.active { border-color: #1e293b; background: #f8fafc; color: #1e293b; box-shadow: 0 4px 6px -1px rgba(30, 41, 59, 0.1); }
.pay-method-card.active i { color: #1e293b; }
.qr-img { width: 200px; height: 200px; object-fit: contain; }
.custom-scrollbar::-webkit-scrollbar { width: 6px; }
.custom-scrollbar::-webkit-scrollbar-thumb { background: #cbd5e1; border-radius: 10px; }
.modal-overlay { backdrop-filter: blur(8px); animation: fadeIn 0.25s ease-out; }
@keyframes fadeIn { from { opacity: 0; } to { opacity: 1; } }
.animate-pop-in { animation: popIn 0.3s cubic-bezier(0.34, 1.56, 0.64, 1); }
@keyframes popIn { from { transform: scale(0.95); opacity: 0; } to { transform: scale(1); opacity: 1; } }
</style>

<style>
/* CSS HÓA ĐƠN IN */
@media print {
  body { background-color: white !important; margin: 0 !important; padding: 0 !important; }
  body > :not(#print-receipt) { display: none !important; }
  #print-receipt { display: block !important; width: 80mm !important; margin: 0 auto !important; padding: 5mm !important; position: relative; font-family: Arial, Helvetica, sans-serif; color: #000; }
  @page { margin: 0; size: 80mm auto; }
  #print-receipt .receipt-header { text-align: center; margin-bottom: 10px; }
  #print-receipt .store-name { font-size: 22px; font-weight: 900; margin: 0 0 5px 0; letter-spacing: 1px; }
  #print-receipt .receipt-title { font-size: 18px; font-weight: bold; margin: 10px 0; }
  #print-receipt p { margin: 3px 0; font-size: 12px; }
  #print-receipt .customer-info { text-align: left; margin: 10px 0; }
  #print-receipt .customer-info p { margin: 3px 0; font-size: 12px; font-weight: bold; }
  #print-receipt .receipt-table { width: 100%; border-collapse: collapse; margin-bottom: 10px; font-size: 12px; }
  #print-receipt .receipt-table th { border-bottom: 1px dashed #000; padding: 5px 0; }
  #print-receipt .receipt-table td { padding: 4px 0; }
  #print-receipt .item-name { font-weight: bold; padding-top: 8px !important; }
  #print-receipt .item-desc { font-size: 11px; color: #333; }
  #print-receipt .text-start { text-align: left; }
  #print-receipt .text-end { text-align: right; }
  #print-receipt .divider { border-bottom: 1px dashed #000; margin: 10px 0; }
  #print-receipt .receipt-summary { font-size: 12px; }
  #print-receipt .summary-line { display: flex; justify-content: space-between; margin-bottom: 4px; }
  #print-receipt .total-line { font-size: 16px; font-weight: 900; margin: 8px 0; border-top: 1px dashed #000; border-bottom: 1px dashed #000; padding: 8px 0; }
  #print-receipt .receipt-footer { margin-top: 20px; }
  #print-receipt .policy { font-size: 11px; font-style: italic; margin-top: 5px; }
  #print-receipt .barcode-fake { font-family: 'Courier New', Courier, monospace; font-size: 14px; margin-top: 15px; font-weight: bold; }
}
</style>