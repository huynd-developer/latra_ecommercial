<template>
  <section class="product-videos-section">
    <div class="container">
      <h2 class="section-title text-center">Trải Nghiệm Thực Tế Từ Khách Hàng</h2>
    </div>
      
    <div class="carousel-full-width position-relative">
      
      <button class="nav-btn prev-btn" @click="scrollTrack('left')" aria-label="Previous videos">
        <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" fill="currentColor" viewBox="0 0 16 16">
          <path fill-rule="evenodd" d="M11.354 1.646a.5.5 0 0 1 0 .708L5.707 8l5.647 5.646a.5.5 0 0 1-.708.708l-6-6a.5.5 0 0 1 0-.708l6-6a.5.5 0 0 1 .708 0z"/>
        </svg>
      </button>
      
      <div 
        class="carousel-viewport"
        @mousedown="handleDragStart"
        @mouseup="handleDragEnd"
        @touchstart="handleDragStart"
        @touchend="handleDragEnd"
      >
        <div class="video-track" :style="trackStyle">
          <div 
            v-for="item in extendedVideos" 
            :key="item.uniqueId" 
            class="video-card"
          >
            <div class="video-wrapper">
              <video 
                :ref="(el) => setVideoRef(el, item.uniqueId)"
                :src="item.videoUrl" 
                :poster="item.posterUrl"
                loop 
                playsinline
                preload="metadata"
                :muted="item.isMuted"
                @click="togglePlay(item)"
              ></video>
              
              <div class="video-top-controls">
                <button class="control-icon-btn" @click.stop="toggleMute(item)" :title="item.isMuted ? 'Bật âm' : 'Tắt âm'">
                  <svg v-if="item.isMuted" xmlns="http://www.w3.org/2000/svg" width="14" height="14" fill="currentColor" viewBox="0 0 16 16">
                    <path d="M6.717 3.55A.5.5 0 0 1 7 4v8a.5.5 0 0 1-.812.39L3.825 10.5H1.5A.5.5 0 0 1 1 10V6a.5.5 0 0 1 .5-.5h2.325l2.363-1.89a.5.5 0 0 1 .529-.06zm7.137 2.096a.5.5 0 0 1 0 .708L12.207 8l1.647 1.646a.5.5 0 0 1-.708.708L11.5 8.707l-1.646 1.647a.5.5 0 0 1-.708-.708L10.793 8 9.146 6.354a.5.5 0 1 1 .708-.708L11.5 7.293l1.646-1.647a.5.5 0 0 1 .708 0z"/>
                  </svg>
                  <svg v-else xmlns="http://www.w3.org/2000/svg" width="14" height="14" fill="currentColor" viewBox="0 0 16 16">
                    <path d="M11.536 14.01A8.473 8.473 0 0 0 14 8c0-2.25-.864-4.31-2.285-5.855a.5.5 0 1 1 .756-.656A9.473 9.473 0 0 1 15 8c0 2.541-.952 4.859-2.516 6.584a.5.5 0 0 1-.748-.688zm-2.86-1.457A5.473 5.473 0 0 0 11 8c0-1.54-.532-2.953-1.424-4.072a.5.5 0 0 1 .782-.628A6.473 6.473 0 0 1 12 8c0 1.833-.674 3.51-1.782 4.894a.5.5 0 0 1-.782-.627zm-2.81-1.921A3.473 3.473 0 0 0 8 8c0-.903-.311-1.734-.827-2.39a.5.5 0 1 1 .784-.62C8.547 5.86 9 6.877 9 8c0 1.123-.453 2.14-1.043 2.81a.5.5 0 1 1-.784-.62zM6.717 3.55A.5.5 0 0 1 7 4v8a.5.5 0 0 1-.812.39L3.825 10.5H1.5A.5.5 0 0 1 1 10V6a.5.5 0 0 1 .5-.5h2.325l2.363-1.89a.5.5 0 0 1 .529-.06z"/>
                  </svg>
                </button>

                <button class="control-icon-btn" @click.stop="togglePlay(item)">
                  <svg v-if="item.isPlaying" xmlns="http://www.w3.org/2000/svg" width="14" height="14" fill="currentColor" viewBox="0 0 16 16">
                    <path d="M5.5 3.5A1.5 1.5 0 0 1 7 5v6a1.5 1.5 0 0 1-3 0V5a1.5 1.5 0 0 1 1.5-1.5zm5 0A1.5 1.5 0 0 1 12 5v6a1.5 1.5 0 0 1-3 0V5a1.5 1.5 0 0 1 1.5-1.5z"/>
                  </svg>
                  <svg v-else xmlns="http://www.w3.org/2000/svg" width="14" height="14" fill="currentColor" viewBox="0 0 16 16">
                    <path d="m11.596 8.697-6.363 3.692c-.54.313-1.233-.066-1.233-.697V4.308c0-.63.692-1.01 1.233-.696l6.363 3.692a.802.802 0 0 1 0 1.393z"/>
                  </svg>
                </button>
              </div>

              <div class="video-username">
                {{ item.username }}
              </div>
            </div>
          </div>
        </div>
      </div>

      <button class="nav-btn next-btn" @click="scrollTrack('right')" aria-label="Next videos">
        <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" fill="currentColor" viewBox="0 0 16 16">
          <path fill-rule="evenodd" d="M4.646 1.646a.5.5 0 0 1 .708 0l6 6a.5.5 0 0 1 0 .708l-6 6a.5.5 0 0 1-.708-.708L10.293 8 4.646 2.354a.5.5 0 0 1 0-.708z"/>
        </svg>
      </button>

    </div>
  </section>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';

interface ShortVideo {
  id: number;
  videoUrl: string;
  posterUrl: string; 
  username: string; 
  isPlaying: boolean;
  isMuted: boolean;
}

const videoRefs = ref(new Map<string, HTMLVideoElement>());
const setVideoRef = (el: any, uniqueId: string) => {
  if (el) videoRefs.value.set(uniqueId, el as HTMLVideoElement);
};

const listVideos = ref<ShortVideo[]>([
  { id: 1, videoUrl: 'https://assets.mixkit.co/videos/preview/mixkit-holding-a-smartphone-with-a-blank-screen-40141-large.mp4', posterUrl: 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=400&q=80', username: '@tylerbamberger', isPlaying: false, isMuted: true },
  { id: 2, videoUrl: 'https://assets.mixkit.co/videos/preview/mixkit-unboxing-a-new-brand-smartphone-40142-large.mp4', posterUrl: 'https://images.unsplash.com/photo-1598327105666-5b89351aff97?w=400&q=80', username: '@zenjosh', isPlaying: false, isMuted: true },
  { id: 3, videoUrl: 'https://assets.mixkit.co/videos/preview/mixkit-man-hands-holding-a-smartphone-close-up-40143-large.mp4', posterUrl: 'https://images.unsplash.com/photo-1546054454-aa26e2b734c7?w=400&q=80', username: '@boc', isPlaying: false, isMuted: true },
  { id: 4, videoUrl: 'https://assets.mixkit.co/videos/preview/mixkit-girl-taking-a-selfie-with-her-smartphone-40147-large.mp4', posterUrl: 'https://images.unsplash.com/photo-1533228891704-7f6c8b7466c5?w=400&q=80', username: '@therealsona', isPlaying: false, isMuted: true },
  { id: 5, videoUrl: 'https://assets.mixkit.co/videos/preview/mixkit-using-a-smartphone-with-a-green-screen-40137-large.mp4', posterUrl: 'https://images.unsplash.com/photo-1565849328610-4861b0dd9ad8?w=400&q=80', username: '@latra_store', isPlaying: false, isMuted: true },
  { id: 6, videoUrl: 'https://assets.mixkit.co/videos/preview/mixkit-woman-using-a-smartphone-in-the-street-40139-large.mp4', posterUrl: 'https://images.unsplash.com/photo-1512428559087-560fa5ceab42?w=400&q=80', username: '@tech_review', isPlaying: false, isMuted: true },
  { id: 7, videoUrl: 'https://assets.mixkit.co/videos/preview/mixkit-taking-photos-of-a-beautiful-girl-on-a-smartphone-40134-large.mp4', posterUrl: 'https://images.unsplash.com/photo-1525547719571-a2d4ac8945e2?w=400&q=80', username: '@phone_vibes', isPlaying: false, isMuted: true }
]);

// ----------------------------------------------------
// CẤU HÌNH VÒNG LẶP VÔ TẬN KHÔNG GIẬT LÙI (INFINITE BUFFER)
// ----------------------------------------------------
const bufferCount = 4; // Số lượng video copy thêm sang hai đầu để che mắt người dùng
const currentIndex = ref(bufferCount); // Điểm khởi đầu mặc định nhảy qua phần đệm đầu
const itemWidth = 304; 
const isTransitionEnabled = ref(true);
const isResetting = ref(false); // Khóa bấm nút liên tục khi đang thực hiện snap ngầm

// Tạo mảng mở rộng ảo từ list gốc để render slider
const extendedVideos = computed(() => {
  const items = listVideos.value;
  if (items.length === 0) return [];

  // Clone vài phần tử cuối đưa lên đầu danh sách
  const prepend = items.slice(-bufferCount).map((item, index) => ({
    ...item,
    uniqueId: `prepend-${item.id}-${index}`,
    originalIndex: items.length - bufferCount + index
  }));

  // Giữ nguyên danh sách gốc ở giữa
  const core = items.map((item, index) => ({
    ...item,
    uniqueId: `core-${item.id}`,
    originalIndex: index
  }));

  // Clone vài phần tử đầu đưa xuống cuối danh sách
  const append = items.slice(0, bufferCount).map((item, index) => ({
    ...item,
    uniqueId: `append-${item.id}-${index}`,
    originalIndex: index
  }));

  return [...prepend, ...core, ...append];
});

const trackStyle = computed(() => ({
  transform: `translateX(-${currentIndex.value * itemWidth}px)`,
  transition: isTransitionEnabled.value ? 'transform 0.5s cubic-bezier(0.25, 1, 0.5, 1)' : 'none'
}));

const pauseAllVideos = () => {
  videoRefs.value.forEach((v) => { if (!v.paused) v.pause(); });
  listVideos.value.forEach(v => v.isPlaying = false);
};

const scrollTrack = (direction: 'left' | 'right') => {
  if (isResetting.value) return; // Nếu đang snap ngầm thì chặn không cho click tiếp
  
  pauseAllVideos(); 
  isTransitionEnabled.value = true;
  const totalCoreItems = listVideos.value.length;

  if (direction === 'right') {
    currentIndex.value++;
    // Khi trượt hết video gốc cuối cùng và chạm hẳn vào danh sách bản sao phía sau
    if (currentIndex.value === bufferCount + totalCoreItems) {
      isResetting.value = true;
      setTimeout(() => {
        isTransitionEnabled.value = false; // Tắt animation
        currentIndex.value = bufferCount;   // Dịch chuyển tức thời về video gốc đầu tiên
        setTimeout(() => {
          isTransitionEnabled.value = true; // Bật lại animation cho các lần bấm sau
          isResetting.value = false;
        }, 20);
      }, 500); // 500ms trùng với thời gian chạy transition CSS
    }
  } else {
    currentIndex.value--;
    // Khi trượt lùi vượt qua video gốc đầu tiên về phía bản sao bên trái
    if (currentIndex.value === bufferCount - 1) {
      isResetting.value = true;
      setTimeout(() => {
        isTransitionEnabled.value = false;
        currentIndex.value = bufferCount + totalCoreItems - 1; // Snap ngầm về video gốc cuối cùng
        setTimeout(() => {
          isTransitionEnabled.value = true;
          isResetting.value = false;
        }, 20);
      }, 500);
    }
  }
};

// ----------------------------------------------------
// HỖ TRỢ VUỐT TRÊN ĐIỆN THOẠI & KÉO CHUỘT
// ----------------------------------------------------
let dragStartX = 0;

const handleDragStart = (e: MouseEvent | TouchEvent) => {
  const event = e as any;
  dragStartX = event.touches ? event.touches[0].screenX : event.pageX;
};

const handleDragEnd = (e: MouseEvent | TouchEvent) => {
  if (dragStartX === 0) return;
  
  const event = e as any;
  const dragEndX = event.changedTouches ? event.changedTouches[0].screenX : event.pageX;
    
  const distance = dragStartX - dragEndX;
  
  if (distance > 40) scrollTrack('right');
  else if (distance < -40) scrollTrack('left');
  
  dragStartX = 0;
};

// ----------------------------------------------------
// XỬ LÝ PHÁT VIDEO THÔNG QUA INDEX GỐC (ĐỒNG BỘ ĐƯỢC CẢ CÁC BẢN CLONE)
// ----------------------------------------------------
const togglePlay = (extendedItem: any) => {
  const originalItem = listVideos.value[extendedItem.originalIndex];
  if (!originalItem) return;

  const videoEl = videoRefs.value.get(extendedItem.uniqueId);
  if (!videoEl) return;

  if (originalItem.isPlaying) {
    videoEl.pause();
    originalItem.isPlaying = false;
  } else {
    pauseAllVideos();
    videoEl.play().catch(err => console.log("Lỗi tự động phát:", err));
    originalItem.isPlaying = true;
  }
};

const toggleMute = (extendedItem: any) => {
  const originalItem = listVideos.value[extendedItem.originalIndex];
  if (originalItem) {
    originalItem.isMuted = !originalItem.isMuted;
  }
};
</script>

<style scoped>
/* Giữ nguyên CSS cũ của bạn */
.product-videos-section {
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
  background-color: #ffffff;
  padding: 60px 0;
  width: 100%;
  overflow: hidden; 
}

.section-title {
  font-size: 30px;
  font-weight: 500;
  color: #1a1a1a;
  margin-bottom: 40px;
}

.carousel-full-width {
  width: 100%;
}

.carousel-viewport {
  width: 100%;
  overflow: hidden;
  cursor: grab;
}
.carousel-viewport:active {
  cursor: grabbing;
}

.video-track {
  display: flex;
  gap: 24px;
  width: max-content;
  padding-left: 4vw; 
  padding-right: 4vw;
}

.video-card {
  flex: 0 0 280px; 
}

.video-wrapper {
  position: relative;
  width: 100%;
  aspect-ratio: 9 / 16;
  background-color: #000000;
  border-radius: 14px;
  overflow: hidden;
  box-shadow: 0 4px 10px rgba(0,0,0,0.06);
}

.video-wrapper video {
  width: 100%;
  height: 100%;
  object-fit: cover;
  cursor: pointer;
}

.video-top-controls {
  position: absolute;
  top: 14px;
  right: 14px;
  display: flex;
  gap: 8px;
  z-index: 2;
}

.control-icon-btn {
  background: rgba(255, 255, 255, 0.35);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  border: none;
  color: #ffffff;
  width: 34px;
  height: 34px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: background 0.2s ease, transform 0.2s ease;
}
.control-icon-btn:hover {
  background: rgba(255, 255, 255, 0.5);
  transform: scale(1.05);
}

.video-username {
  position: absolute;
  bottom: 16px;
  left: 0;
  width: 100%;
  text-align: center;
  color: #ffffff;
  font-size: 14px;
  font-weight: 500;
  text-shadow: 0 1px 4px rgba(0, 0, 0, 0.8);
  z-index: 2;
  pointer-events: none;
}

.nav-btn {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  background: rgba(30, 30, 30, 0.7);
  backdrop-filter: blur(4px);
  border: none;
  color: #ffffff;
  width: 48px;
  height: 48px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  z-index: 10;
  transition: all 0.2s ease;
}
.nav-btn:hover {
  background: rgba(0, 0, 0, 0.9);
}

.prev-btn { left: 2vw; }
.next-btn { right: 2vw; }

@media (max-width: 991px) {
  .nav-btn {
    display: none;
  }
}
</style>