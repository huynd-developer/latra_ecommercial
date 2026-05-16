<script setup lang="ts">
import { ref, watch } from 'vue';
import { useCatalogStore } from '../stores/catalogStore';

const store = useCatalogStore();

// Trạng thái mở/đóng Accordion
const open = ref({ category: true, brand: true });

// Local state cho các checkbox
const selectedCats = ref<number[]>([]);
const selectedBrands = ref<number[]>([]);

// Lắng nghe thay đổi và báo cho store
watch([selectedCats, selectedBrands], () => {
  store.updateFilters({
    categoryIds: selectedCats.value,
    brandIds: selectedBrands.value
  });
});
</script>

<template>
  <div class="filter-sidebar pe-lg-4" style="font-family: 'Inter', sans-serif;">
    
    <!-- LỌC THEO DANH MỤC -->
    <div class="filter-group mb-4 pb-4 border-bottom">
      <div class="d-flex justify-content-between align-items-center mb-3 cursor-pointer" @click="open.category = !open.category">
        <h5 class="fw-bold mb-0 text-dark fs-5">Danh Mục</h5>
        <i class="bi text-dark transition-all" :class="open.category ? 'bi-dash' : 'bi-plus'"></i>
      </div>
      <div v-show="open.category" class="filter-options">
        <div class="form-check mb-2" v-for="cat in store.categories" :key="cat.id">
          <input class="form-check-input shadow-none rounded-1" type="checkbox" :id="'cat-'+cat.id" :value="cat.id" v-model="selectedCats">
          <label class="form-check-label text-dark" :for="'cat-'+cat.id">
            {{ cat.name }}
          </label>
        </div>
      </div>
    </div>

    <!-- LỌC THEO THƯƠNG HIỆU -->
    <div class="filter-group mb-4 pb-4 border-bottom">
      <div class="d-flex justify-content-between align-items-center mb-3 cursor-pointer" @click="open.brand = !open.brand">
        <h5 class="fw-bold mb-0 text-dark fs-5">Thương Hiệu</h5>
        <i class="bi text-dark transition-all" :class="open.brand ? 'bi-dash' : 'bi-plus'"></i>
      </div>
      <div v-show="open.brand" class="filter-options">
        <div class="form-check mb-2" v-for="brand in store.brands" :key="brand.id">
          <input class="form-check-input shadow-none rounded-1" type="checkbox" :id="'brand-'+brand.id" :value="brand.id" v-model="selectedBrands">
          <label class="form-check-label text-dark" :for="'brand-'+brand.id">
            {{ brand.name }}
          </label>
        </div>
      </div>
    </div>

  </div>
</template>

<style scoped>
.cursor-pointer { cursor: pointer; }
.transition-all { transition: transform 0.3s ease; }
</style>