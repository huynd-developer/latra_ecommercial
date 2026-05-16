<script setup lang="ts">
import { useHomeStore } from '../stores/homeStore';
const homeStore = useHomeStore();
</script>

<template>
  <!-- SỬ DỤNG: v-for="feat in homeStore.features". HTML và CSS giữ Y NGUYÊN BẢN CŨ -->
  <div class="feature-section py-5 my-4 bg-white">
    <div class="container-fluid px-4 px-xl-5">
      <div class="d-flex flex-nowrap overflow-x-auto pb-4 gap-3 gap-md-4 scroll-container justify-content-xl-center">
        <router-link :to="feat.link"
                     class="feature-card flex-shrink-0 text-decoration-none text-center group"
                     v-for="(feat, index) in homeStore.features" :key="index">

          <div class="img-wrapper mb-3 rounded-4 overflow-hidden position-relative" style="background-color: #f4f4f4; aspect-ratio: 1 / 1.1;">
            <img :src="feat.image" :alt="feat.title" class="img-fluid w-100 h-100 object-fit-cover transition-transform duration-500">
          </div>

          <h6 class="fw-bold text-dark mb-2" style="font-size: 0.9rem;">{{ feat.title }}</h6>

          <p class="text-secondary mb-1 lh-sm mx-auto" style="font-size: 0.8rem; max-width: 90%;" v-html="feat.description"></p>

          <p v-if="feat.footnote" class="text-muted mt-2 mb-0" style="font-size: 0.65rem; font-style: italic;">{{ feat.footnote }}</p>

        </router-link>
      </div>
    </div>
  </div>
</template>