<template>
  <div class="category-page py-5" style="font-family: 'Inter', sans-serif;">
    <div class="container-fluid px-4 px-xl-5">
      <div class="row">
        <div class="col-lg-3">
          <FilterSidebar 
            :types="dynamicTypes" 
            :models="dynamicPhoneModels"
            v-model:selectedTypes="selectedTypes"
            v-model:selectedModels="selectedModels"
            v-model:selectedFeatures="selectedFeatures"
            v-model:selectedColors="selectedColors"
          />
        </div>

        <div class="col-lg-9">
          <div class="d-flex justify-content-between align-items-center mb-4 border-bottom pb-3">
            <span class="text-secondary fw-medium">{{ sortedProducts.length }} sản phẩm</span>
            
            <div class="d-flex align-items-center gap-2">
              <span class="small fw-bold text-uppercase text-dark">Sort by:</span>
              <select v-model="sortBy" class="form-select form-select-sm border-0 shadow-none w-auto fw-medium bg-transparent cursor-pointer">
                <option value="featured">Featured</option>
                <option value="price-asc">Price, low to high</option>
                <option value="price-desc">Price, high to low</option>
                <option value="alpha-asc">Alphabetically, A-Z</option>
                <option value="alpha-desc">Alphabetically, Z-A</option>
              </select>
            </div>
          </div>

          <div class="row g-4">
            <div v-if="sortedProducts.length === 0" class="col-12 text-center py-5">
              <p class="text-muted">Không tìm thấy sản phẩm nào phù hợp với bộ lọc của bạn.</p>
              <button class="btn btn-outline-dark mt-2" @click="clearFilters">Xóa bộ lọc</button>
            </div>

            <div class="col-6 col-md-4 col-xl-3" v-for="product in sortedProducts" :key="product.id">
              <ProductCard :product="product" />
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { useRoute } from 'vue-router'
import { type Product, productsDB } from '../data/db'
import FilterSidebar from '../components/category/FilterSidebar.vue'
import ProductCard from '../components/category/ProductCard.vue'

const route = useRoute()

// 1. Phân tích Link
const pageInfo = computed(() => {
  const path = route.path.toLowerCase()
  return {
    isCharging: path.includes('charging'),
    isAccessories: path.includes('accessories'),
    isScreen: path.includes('screen'),
    isPrivacy: path.includes('privacy'),
    brand: path.includes('iphone') ? 'Apple' : path.includes('samsung') ? 'Samsung' : 'All'
  }
})

// 2. Lấy danh sách sản phẩm theo Trang
const baseProducts = computed<Product[]>(() => {
  const { isCharging, isAccessories, isScreen, brand } = pageInfo.value
  const filtered = productsDB.filter(p => {
    if (isCharging && p.category !== 'Charging') return false
    if (isAccessories && p.category !== 'Accessories') return false
    if (isScreen && (!p.type || !p.type.includes('Screen'))) return false
    if (!isCharging && !isAccessories && brand !== 'All' && p.category !== brand) return false
    return true
  })
  return filtered as unknown as Product[]
})

// 3. Quản lý trạng thái bộ lọc
const selectedTypes = ref<string[]>([])
const selectedModels = ref<string[]>([])
const selectedFeatures = ref<string[]>([])
const selectedColors = ref<string[]>([])
const sortBy = ref('featured')

// Tạo danh sách Model và Type cho Sidebar
function createFilter(key: keyof Product) {
  const counts: Record<string, number> = {}
  baseProducts.value.forEach(p => {
    const val = p[key]
    if (!val) return
    const name = String(val)
    counts[name] = (counts[name] || 0) + 1
  })
  
  // ĐÃ FIX LỖI ÉP KIỂU: Thêm "|| 0" để đảm bảo count luôn là number
  return Object.keys(counts).map(name => ({ 
    name, 
    count: counts[name] || 0 
  }))
}

const dynamicTypes = computed(() => createFilter('type'))
const dynamicPhoneModels = computed(() => createFilter('category'))

const clearFilters = () => {
  selectedTypes.value = []; selectedModels.value = []; selectedFeatures.value = []; selectedColors.value = [];
}

// 4. LOGIC LỌC TỔNG HỢP (Đã Fix case-sensitive cho Color)
const sortedProducts = computed<Product[]>(() => {
  let res = [...baseProducts.value]

  // Lọc theo Type
  if (selectedTypes.value.length > 0) {
    res = res.filter(p => p.type && selectedTypes.value.some(t => t.trim().toLowerCase() === p.type.trim().toLowerCase()))
  }

  // Lọc theo Model
  if (selectedModels.value.length > 0) {
    res = res.filter(p => p.category && selectedModels.value.some(m => m.trim().toLowerCase() === p.category.trim().toLowerCase()))
  }

  // LỌC THEO TÍNH NĂNG (Case & Screen Features)
  if (selectedFeatures.value.length > 0) {
    res = res.filter(p => p.features && p.features.some(f => selectedFeatures.value.includes(f)))
  }

  // LỌC THEO MÀU SẮC (Color) - Chống lỗi chữ hoa/chữ thường
  if (selectedColors.value.length > 0) {
    res = res.filter(p => 
      p.colors && p.colors.some(c => 
        selectedColors.value.some(sc => sc.toLowerCase() === c.toLowerCase())
      )
    )
  }

  // Sắp xếp
  switch (sortBy.value) {
    case 'price-asc': res.sort((a, b) => a.price - b.price); break;
    case 'price-desc': res.sort((a, b) => b.price - a.price); break;
    case 'alpha-asc': res.sort((a, b) => a.name.localeCompare(b.name)); break;
    case 'alpha-desc': res.sort((a, b) => b.name.localeCompare(a.name)); break;
    default: res.sort((a, b) => b.id - a.id); break;
  }

  return res as unknown as Product[]
})

// Reset khi đổi trang
watch(pageInfo, () => {
  clearFilters()
  sortBy.value = 'featured'
})
</script>