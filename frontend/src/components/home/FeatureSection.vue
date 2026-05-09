<template>
  <div class="feature-section py-5 my-4 bg-white" style="font-family: 'Inter', sans-serif;">
    <div class="container-fluid px-4 px-xl-5">
      
      <div class="d-flex flex-nowrap overflow-x-auto pb-4 gap-3 gap-md-4 scroll-container justify-content-xl-center">
        
        <router-link :to="feat.link" 
                     class="feature-card flex-shrink-0 text-decoration-none text-center group" 
                     v-for="(feat, index) in features" :key="index">
          
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

<script setup lang="ts">
import { ref } from 'vue'

// Dữ liệu chuẩn xác 100% theo ảnh giao diện mẫu của bạn
const features = ref([
  {
    image: 'https://int.buypeel.com/cdn/shop/files/peel_home_hero_desktop_1.jpg?v=1725916053&width=400', 
    title: 'Super Thin Case',
    description: '0.02in Thin<br>Branding-Free<br>Everyday Protection',
    link: '/shop-all'
  },
  {
    image: 'https://int.buypeel.com/cdn/shop/files/Collection_Apple_WatchCase_83907797-29be-410a-b850-93a830239276.jpg?v=1734633765&width=400', 
    title: 'Magnetic Case',
    description: '0.07in Thin<br>Embedded Magnets<br>Drop-Tested',
    link: '/shop-all'
  },
  {
    image: 'https://placehold.co/400x440/e9e9e9/333?text=Active+Case', 
    title: 'Active Case',
    description: '0.08in Thin<br>Reinforced Bumper<br>10ft Drop-Tested',
    footnote: '*previously named the Bumper Case',
    link: '/shop-all'
  },
  {
    image: 'https://placehold.co/400x440/e9e9e9/333?text=Flex+Case', 
    title: 'Flex Case',
    description: '0.05in Thin<br>Flexible Material<br>8ft Drop-Tested',
    link: '/shop-all'
  },
  {
    image: 'https://placehold.co/400x440/e9e9e9/333?text=RePeel+Case', 
    title: 'RePeel Case',
    description: 'Faux Leather<br>Made from Recycled Apple Peels',
    link: '/shop-all'
  },
  {
    image: 'https://int.buypeel.com/cdn/shop/files/i16_Glass_Features.png?v=1743691200&width=400', 
    title: 'Screen Protection',
    description: 'Protect Your Screen with HD Clarity',
    link: '/iphone-protectors'
  },
  {
    image: 'https://int.buypeel.com/cdn/shop/files/Base_Station_Hero_2_2x_8eb14032-17a4-44df-b4d6-84e1fc3f59fa.jpg?v=1730999516&width=400', 
    title: 'Charging Solutions',
    description: 'Wireless Power Banks, Charging Stands, Watch Chargers, & More',
    link: '/accessories'
  }
])
</script>

<style scoped>
/* TRỌNG TÂM: Ẩn thanh cuộn mặc định của trình duyệt để nhìn UI sạch sẽ như App */
.scroll-container {
  -ms-overflow-style: none;  /* Cho IE và Edge */
  scrollbar-width: none;  /* Cho Firefox */
  scroll-behavior: smooth;
}
.scroll-container::-webkit-scrollbar {
  display: none; /* Cho Chrome, Safari và Opera */
}

/* Định dạng kích thước thẻ để khi thu nhỏ màn hình nó không bị bóp méo, mà cho phép vuốt ngang */
.feature-card {
  flex: 1 1 0; /* Cho phép giãn đều trên màn hình lớn */
  min-width: 150px; /* Giữ kích thước tối thiểu trên điện thoại để có thể vuốt */
  max-width: 220px; /* Khống chế không cho quá to trên màn hình 4K */
}

/* Hiệu ứng zoom nhẹ hình ảnh khi hover chuột */
.feature-card:hover img {
  transform: scale(1.05);
}

.transition-transform {
  transition-property: transform;
}
.duration-500 {
  transition-duration: 500ms;
}

/* Tinh chỉnh riêng cho Mobile */
@media (max-width: 768px) {
  .feature-card {
    min-width: 140px; /* Trên điện thoại nhỏ lại một chút xíu */
  }
}
</style>