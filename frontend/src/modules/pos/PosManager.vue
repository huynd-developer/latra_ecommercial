<script setup>
import { onMounted, onUnmounted } from "vue";
import { usePos } from "./composables/usePos";

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
/* Chỉ giữ lại các class bổ trợ giao diện màn hình bên dưới, XÓA HẲN ĐOẠN @media print Ở FILE NÀY */
.bg-slate-50 {
  background-color: #f8fafc !important;
}
.bg-slate-100 {
  background-color: #f1f5f9 !important;
}
.bg-slate-800 {
  background-color: #1e293b !important;
}
.bg-slate-900 {
  background-color: #0f172a !important;
}
.text-slate-900 {
  color: #0f172a !important;
}
.text-slate-800 {
  color: #1e293b !important;
}
.text-slate-700 {
  color: #334155 !important;
}
.text-slate-600 {
  color: #475569 !important;
}
.text-slate-500 {
  color: #64748b !important;
}
.text-slate-400 {
  color: #94a3b8 !important;
}
.border-slate-50 {
  border-color: #f8fafc !important;
}
.border-slate-100 {
  border-color: #f1f5f9 !important;
}
.border-slate-200 {
  border-color: #e2e8f0 !important;
}
.border-slate-300 {
  border-color: #cbd5e1 !important;
}
.btn-dark-peel {
  background-color: #1e293b;
  color: white;
  border: none;
  transition: all 0.2s;
}
.btn-dark-peel:hover:not(:disabled) {
  background-color: #0f172a;
  transform: translateY(-1px);
}
.btn-outline-dark {
  color: #1e293b;
  border: 1px solid #cbd5e1;
  background: white;
  transition: 0.2s;
}
.btn-outline-dark:hover {
  background: #f8fafc;
  border-color: #1e293b;
}
.focus-ring-peel:focus,
.focus-ring-peel:focus-within {
  border-color: #1e293b !important;
  box-shadow: 0 0 0 3px rgba(30, 41, 59, 0.1) !important;
}
.product-card {
  border: 1px solid #f1f5f9;
  cursor: pointer;
}
.hover-lift:hover {
  transform: translateY(-4px);
  border-color: #cbd5e1;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1) !important;
}
.product-img {
  transition: transform 0.3s ease;
}
.hover-lift:hover .product-img {
  transform: scale(1.05);
}
.grayscale {
  filter: grayscale(100%);
}
.btn-light-peel {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: #f1f5f9;
  color: #64748b;
  transition: all 0.2s;
  border: none;
}
.product-card:hover .btn-light-peel {
  background: #1e293b;
  color: white;
}
.btn-remove {
  position: absolute;
  right: 8px;
  top: 8px;
  border: none;
  background: none;
  color: #cbd5e1;
  font-size: 1.2rem;
  transition: 0.2s;
  padding: 0;
}
.btn-remove:hover {
  color: #ef4444;
  transform: scale(1.1);
}
.fw-black {
  font-weight: 900 !important;
}
.tracking-tight {
  letter-spacing: -0.025em;
}
.tracking-wider {
  letter-spacing: 0.05em;
}
.section-label {
  font-size: 0.75rem;
  font-weight: 800;
  color: #64748b;
  text-transform: uppercase;
  letter-spacing: 0.05rem;
}
.text-nowrap {
  white-space: nowrap !important;
}
.border-dashed {
  border-style: dashed !important;
}
.pay-method-card {
  flex: 1;
  border: 2px solid #e2e8f0;
  padding: 12px;
  border-radius: 12px;
  cursor: pointer;
  text-align: center;
  transition: 0.2s;
  background: white;
  color: #64748b;
  font-weight: 600;
}
.pay-method-card i {
  font-size: 1.8rem;
  color: #94a3b8;
  transition: 0.2s;
}
.pay-method-card.active {
  border-color: #1e293b;
  background: #f8fafc;
  color: #1e293b;
  box-shadow: 0 4px 6px -1px rgba(30, 41, 59, 0.1);
}
.pay-method-card.active i {
  color: #1e293b;
}
.qr-img {
  width: 200px;
  height: 200px;
  object-fit: contain;
}
.custom-scrollbar::-webkit-scrollbar {
  width: 6px;
}
.custom-scrollbar::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 10px;
}
.modal-overlay {
  backdrop-filter: blur(8px);
  animation: fadeIn 0.25s ease-out;
}
@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}
.animate-pop-in {
  animation: popIn 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}
@keyframes popIn {
  from {
    transform: scale(0.95);
    opacity: 0;
  }
  to {
    transform: scale(1);
    opacity: 1;
  }
}
</style>
