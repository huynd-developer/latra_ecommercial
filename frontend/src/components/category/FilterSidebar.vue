<template>
  <div class="filter-sidebar pe-lg-4" style="font-family: 'Inter', sans-serif;">
    
    <div class="filter-group mb-4 pb-4 border-bottom" v-if="types && types.length > 0">
      <div class="d-flex justify-content-between align-items-center mb-3 cursor-pointer" @click="toggle('type')">
        <h5 class="fw-bold mb-0 text-dark fs-5">Type</h5>
        <i class="bi text-dark icon-spin" :class="[open.type ? 'bi-dash' : 'bi-plus', { 'is-open': open.type }]"></i>
      </div>
      <transition name="slide-fade">
        <div v-show="open.type" class="filter-options">
          <div class="form-check mb-2" v-for="item in types" :key="item.name">
            <input class="form-check-input shadow-none rounded-1" type="checkbox" :id="'type-'+item.name" :value="item.name" v-model="selectedTypes">
            <label class="form-check-label text-dark" :for="'type-'+item.name">
              {{ item.name }} <span class="text-secondary small">({{ item.count }})</span>
            </label>
          </div>
        </div>
      </transition>
    </div>

    <div class="filter-group mb-4 pb-4 border-bottom" v-if="models && models.length > 0">
      <div class="d-flex justify-content-between align-items-center mb-3 cursor-pointer" @click="toggle('model')">
        <h5 class="fw-bold mb-0 text-dark fs-5">Phone Model</h5>
        <i class="bi text-dark icon-spin" :class="[open.model ? 'bi-dash' : 'bi-plus', { 'is-open': open.model }]"></i>
      </div>
      <transition name="slide-fade">
        <div v-show="open.model" class="filter-options">
          <div class="form-check mb-2" v-for="item in models" :key="item.name">
            <input class="form-check-input shadow-none rounded-1" type="checkbox" :id="'model-'+item.name" :value="item.name" v-model="selectedModels">
            <label class="form-check-label text-dark" :for="'model-'+item.name">
              {{ item.name }} <span class="text-secondary small">({{ item.count }})</span>
            </label>
          </div>
        </div>
      </transition>
    </div>

    <div class="filter-group mb-4 pb-4 border-bottom">
      <div class="d-flex justify-content-between align-items-center mb-3 cursor-pointer" @click="toggle('color')">
        <h5 class="fw-bold mb-0 text-dark fs-5">Color</h5>
        <i class="bi text-dark icon-spin" :class="[open.color ? 'bi-dash' : 'bi-plus', { 'is-open': open.color }]"></i>
      </div>
      <transition name="slide-fade">
        <div v-show="open.color" class="overflow-hidden">
          <div class="d-flex flex-wrap gap-2">
            <div v-for="color in staticData.colors" :key="color"
                 class="color-swatch rounded-circle cursor-pointer border"
                 :style="{ background: color }"
                 :class="{ 'border-dark border-2 scale-up': selectedColors.includes(color) }"
                 @click="toggleColor(color)">
            </div>
            <div class="w-100 mt-2">
              <span class="text-decoration-underline small cursor-pointer text-dark fw-medium">+ Show More</span>
            </div>
          </div>
        </div>
      </transition>
    </div>

    <div class="filter-group mb-4 pb-4 border-bottom">
      <div class="d-flex justify-content-between align-items-center mb-3 cursor-pointer" @click="toggle('caseFeature')">
        <h5 class="fw-bold mb-0 text-dark fs-5">Case Features</h5>
        <i class="bi text-dark icon-spin" :class="[open.caseFeature ? 'bi-dash' : 'bi-plus', { 'is-open': open.caseFeature }]"></i>
      </div>
      <transition name="slide-fade">
        <div v-show="open.caseFeature" class="filter-options">
          <div class="form-check mb-2" v-for="item in staticData.caseFeatures" :key="item.name">
            <input class="form-check-input shadow-none rounded-1" type="checkbox" :id="'cf-'+item.name" :value="item.name" v-model="selectedFeatures">
            <label class="form-check-label text-dark" :for="'cf-'+item.name">
              {{ item.name }}
            </label>
          </div>
        </div>
      </transition>
    </div>

    <div class="filter-group mb-4 pb-4">
      <div class="d-flex justify-content-between align-items-center mb-3 cursor-pointer" @click="toggle('screenFeature')">
        <h5 class="fw-bold mb-0 text-dark fs-5">Screen Protector Features</h5>
        <i class="bi text-dark icon-spin" :class="[open.screenFeature ? 'bi-dash' : 'bi-plus', { 'is-open': open.screenFeature }]"></i>
      </div>
      <transition name="slide-fade">
        <div v-show="open.screenFeature" class="filter-options">
          <div class="form-check mb-2" v-for="item in staticData.screenFeatures" :key="item.name">
            <input class="form-check-input shadow-none rounded-1" type="checkbox" :id="'sf-'+item.name" :value="item.name" v-model="selectedFeatures">
            <label class="form-check-label text-dark" :for="'sf-'+item.name">
              {{ item.name }}
            </label>
          </div>
        </div>
      </transition>
    </div>

  </div>
</template>

<script setup lang="ts">
import { reactive } from 'vue'

defineProps<{
  types: { name: string, count: number }[],
  models: { name: string, count: number }[]
}>()

const selectedTypes = defineModel<string[]>('selectedTypes', { default: () => [] })
const selectedModels = defineModel<string[]>('selectedModels', { default: () => [] })
const selectedFeatures = defineModel<string[]>('selectedFeatures', { default: () => [] })
const selectedColors = defineModel<string[]>('selectedColors', { default: () => [] })

const open = reactive({
  type: true, model: true, color: true, caseFeature: true, screenFeature: true
})
const toggle = (section: keyof typeof open) => { open[section] = !open[section] }

const toggleColor = (hex: string) => {
  const index = selectedColors.value.indexOf(hex)
  if (index > -1) selectedColors.value.splice(index, 1)
  else selectedColors.value.push(hex)
}

const staticData = {
  colors: ["#414141", "#000000", "#E4DED1", "#FFFFFF", "#FF924E", "#4A5368", "#645D71", "#F3F3F3", "linear-gradient(180deg, #000 0%, #555 100%)", "#232323", "#1F473A"],
  caseFeatures: [
    { name: 'Drop-Tested' }, { name: 'Easy Grip' }, { name: 'Magsafe-Compatible' },
    { name: 'Pocket friendly' }, { name: 'Shockproof' }, { name: 'Snug fit' }, { name: 'Thin Design' }
  ],
  screenFeatures: [
    { name: '9H Hardness Rating' }, { name: '28° Privacy Filtering' }, { name: 'Reinforced Silicone Edges' },
    { name: '3D Design' }, { name: 'HD Clarity' }, { name: 'Oleophobic Coating' }, { name: 'Anti-Fingerprint' }
  ]
}
</script>

<style scoped>
.cursor-pointer { cursor: pointer; }

/* 1. HIỆU ỨNG THU GỌN / MỞ RA (ACCORDION) */
.slide-fade-enter-active,
.slide-fade-leave-active {
  transition: max-height 0.4s ease-in-out, opacity 0.3s ease, transform 0.4s ease;
  max-height: 500px; /* Chiều cao tối đa khi mở */
  opacity: 1;
  transform: translateY(0);
  overflow: hidden;
}

.slide-fade-enter-from,
.slide-fade-leave-to {
  max-height: 0;
  opacity: 0;
  transform: translateY(-10px); /* Trượt nhẹ lên trên khi đóng */
}

/* 2. HIỆU ỨNG XOAY ICON (+ / -) */
.bi {
  font-size: 1.3rem; /* Làm icon mỏng và thanh lịch hơn theo mẫu */
}
.icon-spin {
  transition: transform 0.3s ease;
}
.icon-spin.is-open {
  transform: rotate(180deg);
}

/* Các style cũ giữ nguyên */
.form-check-input { width: 1.1em; height: 1.1em; margin-top: 0.25em; border-color: #adb5bd; }
.form-check-input:checked { background-color: #000; border-color: #000; }
.color-swatch { width: 32px; height: 32px; border-color: #dee2e6 !important; transition: all 0.2s ease; }
.color-swatch:hover { transform: scale(1.1); }
.color-swatch.scale-up { transform: scale(1.15); box-shadow: 0 0 0 2px #fff, 0 0 0 4px #000; }
</style>