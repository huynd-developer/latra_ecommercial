<script setup lang="ts">
import { ref, onMounted } from "vue";
import { useImeiStore } from "../stores/imei.store";
import ImeiList from "../components/ImeiList.vue";
import ImeiImportModal from "../components/ImeiImportModal.vue"; // Đã import tên này...

const store = useImeiStore();
const activeTab = ref<"LIST" | "IMPORT">("LIST");

onMounted(() => {
  store.fetchImeis();
});

const handleImported = () => {
  store.fetchImeis();
  activeTab.value = "LIST"; // Tự động chuyển về tab danh sách khi nhập xong
};
</script>

<template>
  <div
    class="bg-white h-100 d-flex flex-column overflow-hidden rounded-4 shadow-sm border border-slate-100 p-4"
  >
    <div
      class="d-flex justify-content-between align-items-end mb-4 border-bottom pb-3"
    >
      <div>
        <h5 class="fw-bolder text-slate-800 mb-3">Hệ thống Kho (WMS)</h5>
        <div class="btn-group bg-light p-1 rounded-pill">
          <button
            @click="activeTab = 'LIST'"
            class="btn rounded-pill px-4 fw-bold"
            :class="
              activeTab === 'LIST'
                ? 'btn-white shadow-sm text-dark border'
                : 'btn-light text-muted border-0'
            "
          >
            <i class="bi bi-box-seam me-2"></i>Tồn Kho
          </button>

          <button
            v-permission="['MANAGER', 'OWNER']"
            @click="activeTab = 'IMPORT'"
            class="btn rounded-pill px-4 fw-bold"
            :class="
              activeTab === 'IMPORT'
                ? 'btn-white shadow-sm text-dark border'
                : 'btn-light text-muted border-0'
            "
          >
            <i class="bi bi-upc-scan me-2"></i>Nhập Hàng
          </button>
        </div>
      </div>

      <div class="text-end">
        <p class="mb-0 text-muted small">Tổng thiết bị hệ thống</p>
        <h3 class="mb-0 fw-bold text-primary">
          {{ store.imeis?.length || 0 }}
        </h3>
      </div>
    </div>

    <div class="flex-grow-1 overflow-hidden">
      <transition name="fade" mode="out-in">
        <ImeiList
          v-if="activeTab === 'LIST'"
          :imeis="store.imeis"
          @refresh="store.fetchImeis"
        />

        <ImeiImportModal v-else @imported="handleImported" />
      </transition>
    </div>
  </div>
</template>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
