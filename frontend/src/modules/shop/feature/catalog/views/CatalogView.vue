<!-- frontend/src/modules/shop/feature/catalog/views/CatalogView.vue -->
<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useCatalogStore } from '../stores/catalogStore';
import FilterSidebar from '../components/FilterSidebar.vue';
import ProductCard from '../components/ProductCard.vue';
import QuickViewModal from '../components/QuickViewModal.vue'; // IMPORT MỚI

const store = useCatalogStore();
const selectedProduct = ref<any>(null); // State quản lý popup

onMounted(() => {
  store.fetchDropdowns();
  store.fetchData();
});

const handleSortChange = (e: Event) => {
  const target = e.target as HTMLSelectElement;
  store.updateFilters({ sortBy: target.value });
};

const clearFilters = () => {
  window.location.reload(); 
};
</script>

<template>
  <div class="category-page py-5 bg-gray-50 min-h-screen relative">
    <div class="container-fluid px-4 px-xl-5 max-w-7xl mx-auto">
      <div class="row">
        <!-- SIDEBAR -->
        <div class="col-lg-3">
          <FilterSidebar />
        </div>

        <!-- MAIN CONTENT -->
        <div class="col-lg-9">
          <!-- (Giữ nguyên phần top bar và phân trang) -->
          <div class="d-flex justify-content-between align-items-center mb-4 border-bottom pb-3">
            <span class="text-secondary fw-medium">Tìm thấy {{ store.totalItems }} sản phẩm</span>
            <div class="d-flex align-items-center gap-2">
              <span class="small fw-bold text-uppercase text-dark">Sắp xếp:</span>
              <select @change="handleSortChange" class="form-select form-select-sm border-0 shadow-none w-auto fw-medium bg-transparent cursor-pointer">
                <option value="newest">Mới nhất</option>
                <option value="price-asc">Giá: Thấp đến Cao</option>
                <option value="price-desc">Giá: Cao đến Thấp</option>
              </select>
            </div>
          </div>

          <div v-if="store.isLoading" class="text-center py-5">
            <span class="spinner-border text-dark"></span>
          </div>

          <div class="row g-4" v-else>
            <div v-if="store.products.length === 0" class="col-12 text-center py-5">
              <i class="bi bi-box-seam display-1 text-muted mb-3 d-block"></i>
              <p class="text-muted">Không tìm thấy sản phẩm nào phù hợp.</p>
              <button class="btn btn-outline-dark mt-2 rounded-pill px-4" @click="clearFilters">Xóa bộ lọc</button>
            </div>

            <div class="col-6 col-md-4 col-xl-3" v-for="product in store.products" :key="product.id">
              <!-- Hứng sự kiện @quick-view -->
              <ProductCard 
                :product="product" 
                :brandList="store.brands" 
                @quick-view="selectedProduct = $event" 
              />
            </div>
          </div>
          
        </div>
      </div>
    </div>

    <!-- GẮN POPUP VÀO ĐÂY -->
    <Teleport to="body">
      <transition name="fade">
        <QuickViewModal 
          v-if="selectedProduct" 
          :product="selectedProduct" 
          :brandList="store.brands"
          @close="selectedProduct = null" 
        />
      </transition>
    </Teleport>
  </div>
</template>

<style scoped>
.fade-enter-active, .fade-leave-active { transition: opacity 0.2s; }
.fade-enter-from, .fade-leave-to { opacity: 0; }
</style>