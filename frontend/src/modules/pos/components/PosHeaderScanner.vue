<script setup lang="ts">
import { usePos } from '../composables/usePos';

const { scannedCode, searchQuery, filterStock, handleScanImei } = usePos()
</script>

<template>
  <div class="card p-3 border-0 shadow-sm rounded-4 mb-3 bg-white">
    <div class="row g-3 align-items-center">
      <div class="col-md-5">
        <div class="input-group focus-ring-peel rounded-3 overflow-hidden border">
          <span class="input-group-text bg-slate-100 border-0 text-slate-500">
            <i class="bi bi-qr-code-scan"></i>
          </span>
          <input 
            v-model="scannedCode"
            type="text" 
            class="form-control border-0 bg-slate-100 py-2 ps-2 text-slate-800" 
            placeholder="Quét hoặc nhập mã IMEI máy tại đây... (Enter)"
            @keydown.enter="handleScanImei"
          />
        </div>
      </div>

      <div class="col-md-4">
        <div class="input-group focus-ring-peel rounded-3 overflow-hidden border">
          <span class="input-group-text bg-white border-0 text-slate-400">
            <i class="bi bi-search"></i>
          </span>
          <input 
            v-model="searchQuery"
            type="text" 
            class="form-control border-0 py-2 ps-1 text-slate-800" 
            placeholder="Tìm tên, màu, dung lượng..." 
          />
        </div>
      </div>

      <div class="col-md-3">
        <select v-model="filterStock" class="form-select border-slate-200 py-2 rounded-3 text-slate-700 text-sm fw-semibold">
          <option value="ALL">📦 Tất cả hàng kho</option>
          <option value="IN_STOCK">✅ Chỉ máy có sẵn</option>
        </select>
      </div>
    </div>
  </div>
</template>