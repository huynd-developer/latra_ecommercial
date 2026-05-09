<template>
  <header class="top-nav-glass sticky-top d-flex align-items-center justify-content-between px-4 z-3">
    
    <!-- KHU VỰC LOGO -->
    <div class="d-flex align-items-center gap-2">
      <div class="bg-dark-peel text-white rounded-3 d-flex align-items-center justify-content-center shadow-peel" style="width: 36px; height: 36px;">
        <i class="bi bi-layers-fill fs-5"></i>
      </div>
      <h4 class="fw-black mb-0 text-slate-800 tracking-tight" style="letter-spacing: 0.5px;">LATRA</h4>
    </div>

    <!-- KHU VỰC MENU -->
    <div class="d-flex align-items-center gap-1 bg-slate-100 p-1 rounded-pill border border-slate-200 shadow-sm">
      <router-link to="/admin/pos" class="btn nav-pill transition-all fw-semibold text-decoration-none" :class="activeTab === 'pos' ? 'active bg-dark-peel text-white shadow-peel' : 'text-slate-500 hover-text-dark'">
        <i class="bi bi-cart3 me-1"></i> Bán hàng
      </router-link>
      <router-link to="/admin/dashboard" class="btn nav-pill transition-all fw-semibold text-decoration-none" :class="activeTab === 'dashboard' ? 'active bg-dark-peel text-white shadow-peel' : 'text-slate-500 hover-text-dark'">
        <i class="bi bi-grid-1x2 me-1"></i> Tổng quan
      </router-link>
      <router-link 
        v-for="item in menuItems" 
        :key="item.id" 
        :to="'/admin/' + item.id" 
        class="btn nav-pill transition-all fw-semibold text-decoration-none" 
        :class="activeTab === item.id ? 'active bg-dark-peel text-white shadow-peel' : 'text-slate-500 hover-text-dark'"
      >
        <i :class="item.icon" class="me-1"></i> {{ item.name }}
      </router-link>
    </div>

    <!-- KHU VỰC TIỆN ÍCH -->
    <div class="d-flex align-items-center gap-3">
      <!-- Đồng hồ -->
      <div class="text-slate-500 fw-medium small d-flex align-items-center gap-2 bg-white px-3 py-2 rounded-pill shadow-sm border border-slate-100">
        <i class="bi bi-clock text-dark-peel"></i> {{ currentTime }}
      </div>
      
      <!-- Nút Avatar User -->
      <div class="position-relative">
        <div @click.stop="toggleProfile" class="bg-dark-peel text-white rounded-circle fw-bold d-flex align-items-center justify-content-center shadow-peel cursor-pointer hover-lift" style="width: 40px; height: 40px; font-size: 0.85rem;">
          AD
        </div>

        <!-- Dropdown Menu -->
        <div v-if="showProfile" @click.stop class="dropdown-menu-custom position-absolute end-0 mt-2 bg-white rounded-4 shadow-lg border border-slate-100 overflow-hidden animate__animated animate__fadeIn animate__faster z-3" style="width: 260px;">
          
          <div class="p-3 border-bottom border-slate-100 bg-slate-50 d-flex gap-3 align-items-center">
            <div class="bg-dark-peel text-white rounded-circle fw-bold d-flex align-items-center justify-content-center flex-shrink-0" style="width: 45px; height: 45px;">AD</div>
            <div>
              <h6 class="mb-0 fw-bold text-slate-800" style="font-size: 0.9rem;">Quản Trị Viên</h6>
              <div class="text-slate-500" style="font-size: 0.75rem;">admin@latra.com</div>
            </div>
          </div>
          
          <div class="p-2">
            <button class="dropdown-item-custom w-100 text-start bg-transparent border-0 p-2 rounded-3 text-slate-700 fw-medium mb-1">
              <i class="bi bi-person me-2"></i> Hồ sơ cá nhân
            </button>
            <button class="dropdown-item-custom w-100 text-start bg-transparent border-0 p-2 rounded-3 text-slate-700 fw-medium mb-1">
              <i class="bi bi-shield-lock me-2"></i> Đổi mật khẩu
            </button>
            
            <hr class="my-1 border-slate-200">
            
            <!-- NÚT BÀN GIAO CA MỚI THÊM -->
            <button @click="handleShiftHandover" class="dropdown-item-custom w-100 text-start bg-transparent border-0 p-2 rounded-3 text-slate-700 fw-bold mb-1" style="color: #0284c7 !important;">
              <i class="bi bi-clock-history me-2"></i> Bàn giao ca làm việc
            </button>
          </div>
          
          <div class="p-2 border-top border-slate-100">
            <button @click="handleLogout" class="dropdown-item-custom w-100 text-start bg-transparent border-0 p-2 rounded-3 text-danger fw-bold">
              <i class="bi bi-box-arrow-right me-2"></i> Đăng xuất
            </button>
          </div>

        </div>
      </div>

    </div>
  </header>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router' // 1. Khai báo thư viện chuyển trang
import axios from 'axios' // 2. Đã mở khóa (bỏ comment) Axios để gọi API
import Swal from 'sweetalert2'

defineProps<{ activeTab: string }>()

const router = useRouter() // 3. Khởi tạo biến router
const currentTime = ref('')

const menuItems = [
  { id: 'orders', name: 'Hóa đơn', icon: 'bi-receipt' },
  { id: 'products', name: 'Sản phẩm', icon: 'bi-box-seam' },
  { id: 'imeis', name: 'Kho IMEI', icon: 'bi-upc-scan' },
  { id: 'promotions', name: 'Voucher', icon: 'bi-ticket-perforated' }, 
  { id: 'flash-sale', name: 'Flash Sale', icon: 'bi-lightning-charge' }, 
  { id: 'khach-hang', name: 'Khách hàng', icon: 'bi-people' }, 
  { id: 'nhan-vien', name: 'Nhân viên', icon: 'bi-person-badge' }, 
  { id: 'settings', name: 'Cài đặt', icon: 'bi-gear' },
]

// --- QUẢN LÝ ẨN HIỆN MENU ---
const showProfile = ref(false)
const toggleProfile = () => { showProfile.value = !showProfile.value }
const closeAllMenus = () => { showProfile.value = false }

// --- TÍNH NĂNG CHỐT CA / BÀN GIAO CA ---
const handleShiftHandover = async () => {
  showProfile.value = false // Tắt menu
  
  try {
    // Gọi API lấy dữ liệu chốt ca 
    const res = await axios.get('http://localhost:8080/api/v1/shifts/summary?userId=1')
    const data = res.data

    // Format tiền tệ VNĐ
    const formattedRevenue = new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(data.totalRevenue)

    // Hiển thị Popup
    Swal.fire({
      title: 'Tổng kết ca làm việc',
      html: `
        <div class="text-start mt-3" style="font-size: 0.95rem;">
          <div class="d-flex justify-content-between mb-2">
            <span class="text-muted">Nhân viên ca:</span>
            <span class="fw-bold text-slate-800">${data.staffName}</span>
          </div>
          <div class="d-flex justify-content-between mb-2">
            <span class="text-muted">Giờ mở ca:</span>
            <span class="fw-bold text-slate-800">${data.startTime}</span>
          </div>
          <div class="d-flex justify-content-between mb-2">
            <span class="text-muted">Đơn hàng đã bán:</span>
            <span class="fw-bold text-slate-800">${data.totalOrders} Đơn</span>
          </div>
          <div class="p-3 bg-light rounded-3 mt-3 border">
            <div class="d-flex justify-content-between align-items-center">
              <span class="fw-semibold">Tiền mặt thu được:</span>
              <span class="fw-black fs-5 text-success">${formattedRevenue}</span>
            </div>
          </div>
          <div class="text-center mt-3 small text-danger fw-medium">
            <i class="bi bi-exclamation-circle me-1"></i> Sau khi chốt ca, hệ thống sẽ tự động đăng xuất!
          </div>
        </div>
      `,
      icon: 'info',
      showCancelButton: true,
      confirmButtonColor: '#111111',
      cancelButtonColor: '#94a3b8',
      confirmButtonText: '<i class="bi bi-printer me-1"></i> Chốt ca & In phiếu',
      cancelButtonText: 'Đóng'
    }).then((result) => {
      if (result.isConfirmed) {
        Swal.fire({ icon: 'success', title: 'Chốt ca thành công!', text: 'Phiếu bàn giao đang được in...', timer: 2000, showConfirmButton: false })
        setTimeout(() => {
          // 4. Mở khóa luôn tính năng văng ra màn hình Đăng xuất
          router.push('/login')
        }, 2000)
      }
    })

  } catch (error) {
    Swal.fire({ icon: 'error', title: 'Lỗi', text: 'Không thể kết nối đến máy chủ! Vui lòng kiểm tra lại Spring Boot.' })
  }
}

// --- ĐĂNG XUẤT ---
const handleLogout = () => {
  showProfile.value = false
  Swal.fire({
    title: 'Đăng xuất',
    text: 'Bạn có chắc chắn muốn đăng xuất?',
    icon: 'question',
    showCancelButton: true,
    confirmButtonColor: '#ef4444',
    cancelButtonColor: '#1e293b',
    confirmButtonText: 'Đăng xuất',
    cancelButtonText: 'Hủy'
  }).then((result) => {
    if (result.isConfirmed) {
      Swal.fire({ icon: 'success', title: 'Đã đăng xuất', timer: 1500, showConfirmButton: false })
      router.push('/login') // 5. Mở khóa tính năng văng ra màn hình Đăng xuất
    }
  })
}

// --- LIFECYCLE ---
let timer: ReturnType<typeof setInterval>;

onMounted(() => {
  const updateClock = () => {
    const now = new Date()
    const p = (n: number) => n.toString().padStart(2, '0')
    currentTime.value = `${p(now.getHours())}:${p(now.getMinutes())}:${p(now.getSeconds())} - ${p(now.getDate())}/${p(now.getMonth()+1)}/${now.getFullYear()}`
  }
  updateClock()
  timer = setInterval(updateClock, 1000)

  window.addEventListener('click', closeAllMenus)
})

onUnmounted(() => {
  clearInterval(timer)
  window.removeEventListener('click', closeAllMenus)
})
</script>

<style scoped>
/* NAV CHÍNH */
.top-nav-glass {
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.3);
  height: 70px;
}
.nav-pill { color: #64748b; border-radius: 50px; padding: 6px 16px; font-size: 0.85rem; border: none; background: transparent; }
.nav-pill.active { background-color: #111111 !important; color: #ffffff !important; }
.hover-text-dark:hover { color: #1e293b !important; }
.shadow-peel { box-shadow: 0 4px 12px 0 rgba(0, 0, 0, 0.1); }
.hover-lift { transition: transform 0.2s, box-shadow 0.2s; }
.hover-lift:hover { transform: translateY(-2px); }
.bg-dark-peel { background-color: #111111 !important; }
.text-dark-peel { color: #111111 !important; }
.cursor-pointer { cursor: pointer; }

/* MENU DROPDOWN */
.dropdown-menu-custom {
  transform-origin: top right;
  z-index: 1050;
}
.dropdown-item-custom { transition: background-color 0.2s; }
.dropdown-item-custom:hover { background-color: #f1f5f9 !important; }
.animate__faster { animation-duration: 0.2s; }
</style>