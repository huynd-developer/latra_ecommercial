<template>
  <header class="navbar navbar-expand-lg border-bottom py-3 custom-navbar sticky-top">
    <div class="container-fluid px-lg-5">
      
      <a class="navbar-brand logo-link order-1 me-0" href="#" @click.prevent="goHome">
        <h2 class="fw-bold mb-0 tracking-wide" style="letter-spacing: 0.2em;">LATRA</h2>
      </a>

      <div class="collapse navbar-collapse justify-content-center order-3" id="navbarNav">
        <ul class="navbar-nav gap-lg-4">
          
          <li class="nav-item mega-menu-wrapper" @click.stop="toggleMenu('products')">
            <a class="nav-link custom-link" href="#" @click.prevent>
              Điện thoại 
              <i class="bi bi-chevron-down ms-1 arrow-icon" :class="{ 'rotate-180': activeMenu === 'products' }"></i>
            </a>

            <div class="mega-menu shadow-sm" :class="{ 'show-mega': activeMenu === 'products' }" @click.stop>
              <div class="container py-5">
                <div class="row justify-content-center">
                  
                  <div class="col-lg-6 pe-lg-5">
                    <div class="row">
                      <div class="col-4">
                        <h6 class="menu-heading">Apple iPhone</h6>
                        <ul class="menu-list mb-5">
                          <li v-for="phone in applePhones" :key="phone">
                            <a href="#" @click.prevent="selectPhone(phone)">{{ phone }}</a>
                          </li>
                          <li class="mt-3"><a href="#" class="shop-all-link">Shop All &rarr;</a></li>
                        </ul>

                        <h6 class="menu-heading">Apple iPad</h6>
                        <ul class="menu-list">
                          <li><a href="#" class="shop-all-link">Shop All &rarr;</a></li>
                        </ul>
                      </div>

                      <div class="col-4">
                        <h6 class="menu-heading">Samsung Galaxy</h6>
                        <ul class="menu-list">
                          <li v-for="phone in samsungPhones" :key="phone.name">
                            <a href="#" @click.prevent="selectPhone(phone.name)" :class="{'text-primary': phone.isNew}">
                              {{ phone.name }}
                              <span v-if="phone.isNew" class="badge bg-primary rounded-pill new-badge ms-1">NEW</span>
                            </a>
                          </li>
                          <li class="mt-3"><a href="#" class="shop-all-link">Shop All &rarr;</a></li>
                        </ul>
                      </div>

                      <div class="col-4">
                        <h6 class="menu-heading">Google Pixel</h6>
                        <ul class="menu-list">
                          <li v-for="phone in googlePhones" :key="phone">
                            <a href="#" @click.prevent="selectPhone(phone)">{{ phone }}</a>
                          </li>
                          <li class="mt-3"><a href="#" class="shop-all-link">Shop All &rarr;</a></li>
                        </ul>
                      </div>
                    </div>
                  </div>
                  
                  <div class="col-lg-6 ps-lg-5 border-start">
                    <div class="mega-product-grid">
                      <div class="mega-card" v-for="item in megaProducts" :key="item.name" @click="selectPhone(item.name)">
                        <div class="mega-card-img">
                          <img :src="item.img" :alt="item.name">
                        </div>
                        
                        <div class="mega-card-body">
                          <div class="mega-card-info">
                            <h6 class="mega-card-title" v-html="item.formattedName"></h6>
                            <p class="mega-card-desc">{{ item.desc }}</p>
                          </div>
                          <div class="mega-arrow-btn">
                            <i class="bi bi-arrow-up-right"></i>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>

                </div>
              </div>
            </div>
          </li>

          <li class="nav-item mega-menu-wrapper" @click.stop="toggleMenu('screen')">
            <a class="nav-link custom-link" href="#" @click.prevent>
              Máy cũ
              <i class="bi bi-chevron-down ms-1 arrow-icon" :class="{ 'rotate-180': activeMenu === 'screen' }"></i>
            </a>

            <div class="mega-menu shadow-sm" :class="{ 'show-mega': activeMenu === 'screen' }" @click.stop>
              <div class="container py-5">
                <div class="row justify-content-center">
                  
                  <div class="col-lg-5 pe-lg-5">
                    <div class="row">
                      <div class="col-6">
                        <h6 class="menu-heading">Glass Screen Protectors</h6>
                        <ul class="menu-list mb-5">
                          <li><a href="#">Apple iPhone</a></li>
                          <li>
                            <a href="#" class="text-primary">
                              Samsung Galaxy
                              <span class="badge bg-primary rounded-pill new-badge ms-1">NEW</span>
                            </a>
                          </li>
                          <li><a href="#">Google Pixel</a></li>
                          <li class="mt-3"><a href="#" class="shop-all-link">Shop All &rarr;</a></li>
                        </ul>
                      </div>

                      <div class="col-6">
                        <h6 class="menu-heading">Privacy Glass Screen Protectors</h6>
                        <ul class="menu-list mb-5">
                          <li><a href="#">Apple iPhone</a></li>
                          <li>
                            <a href="#" class="text-primary">
                              Samsung Galaxy
                              <span class="badge bg-primary rounded-pill new-badge ms-1">NEW</span>
                            </a>
                          </li>
                          <li class="mt-3"><a href="#" class="shop-all-link">Shop All &rarr;</a></li>
                        </ul>
                      </div>
                    </div>
                  </div>
                  
                  <div class="col-lg-7 ps-lg-5 border-start">
                    <div class="mega-screen-grid">
                      <div class="mega-card" @click="selectPhone('Glass Screen Protectors')">
                        <div class="mega-card-img screen-img-wrapper">
                          <img src="https://placehold.co/400x300/f0f0f0/333333?text=Glass+Screen" alt="Glass Screen Protectors">
                        </div>
                        <div class="mega-card-body align-items-center">
                          <div class="mega-card-info">
                            <h6 class="mega-card-title mb-0">Glass Screen Protectors</h6>
                          </div>
                          <div class="mega-arrow-btn">
                            <i class="bi bi-arrow-up-right"></i>
                          </div>
                        </div>
                      </div>

                      <div class="mega-card" @click="selectPhone('Privacy Glass Screen Protectors')">
                        <div class="mega-card-img screen-img-wrapper">
                          <img src="https://placehold.co/400x300/f0f0f0/333333?text=Privacy+Glass" alt="Privacy Glass Screen Protectors">
                        </div>
                        <div class="mega-card-body align-items-center">
                          <div class="mega-card-info">
                            <h6 class="mega-card-title mb-0">Privacy Glass Screen <br> Protectors</h6>
                          </div>
                          <div class="mega-arrow-btn">
                            <i class="bi bi-arrow-up-right"></i>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>

                </div>
              </div>
            </div>
          </li>

          <li class="nav-item mega-menu-wrapper" @click.stop="toggleMenu('charging')">
            <a class="nav-link custom-link" href="#" @click.prevent>
              Phụ kiện
              <i class="bi bi-chevron-down ms-1 arrow-icon" :class="{ 'rotate-180': activeMenu === 'charging' }"></i>
            </a>

            <div class="mega-menu shadow-sm" :class="{ 'show-mega': activeMenu === 'charging' }" @click.stop>
              <div class="container py-5">
                <div class="row justify-content-center">
                  
                  <div class="col-lg-5 pe-lg-5">
                    <div class="row">
                      <div class="col-6 mb-5"><a href="#" class="menu-heading-link">Wireless Chargers</a></div>
                      <div class="col-6 mb-5"><a href="#" class="menu-heading-link">Portable Power Banks</a></div>
                      <div class="col-6 mb-5"><a href="#" class="menu-heading-link">Charging Cables</a></div>
                      <div class="col-6 mb-5"><a href="#" class="menu-heading-link">Wall Chargers</a></div>
                      <div class="col-6 mb-5"><a href="#" class="menu-heading-link">Car Chargers</a></div>
                      <div class="col-6 mb-5"><a href="#" class="menu-heading-link">Watch Chargers</a></div>
                    </div>
                  </div>
                  
                  <div class="col-lg-7 ps-lg-5 border-start">
                    <div class="row">
                      <div class="col-6">
                        <a href="#" class="featured-product-link text-decoration-none">
                          <div class="featured-img-box mb-3">
                            <img src="https://placehold.co/300x300/e6e6e6/333333?text=Wireless+Stand" alt="Qi2 3-in-1">
                          </div>
                          <div class="featured-info">
                            <p class="text-muted mb-1" style="font-size: 11px;">Wireless Charger</p>
                            <h6 class="fw-bold text-dark mb-2" style="font-size: 14px; line-height: 1.4;">
                              Qi2 3-in-1 Folding <br> Wireless Charging Stand
                            </h6>
                            <div>
                              <span class="text-danger fw-bold" style="font-size: 13px;">$79.99</span>
                              <span class="text-muted text-decoration-line-through ms-2" style="font-size: 13px;">$90.00</span>
                            </div>
                          </div>
                        </a>
                      </div>

                      <div class="col-6">
                        <a href="#" class="featured-product-link text-decoration-none">
                          <div class="featured-img-box mb-3">
                            <img src="https://placehold.co/300x300/e6e6e6/333333?text=Car+Mount" alt="Magnetic Car Mount">
                          </div>
                          <div class="featured-info">
                            <p class="text-muted mb-1" style="font-size: 11px;">Car Charger</p>
                            <h6 class="fw-bold text-dark mb-2" style="font-size: 14px; line-height: 1.4;">
                              Magnetic Wireless <br> Car Charging Mount
                            </h6>
                            <div>
                              <span class="text-danger fw-bold" style="font-size: 13px;">$44.00</span>
                              <span class="text-muted text-decoration-line-through ms-2" style="font-size: 13px;">$49.00</span>
                            </div>
                          </div>
                        </a>
                      </div>
                    </div>
                  </div>

                </div>
              </div>
            </div>
          </li>

          <li class="nav-item mega-menu-wrapper" @click.stop="toggleMenu('accessories')">
            <a class="nav-link custom-link" href="#" @click.prevent>
              Giới thiệu
              <i class="bi bi-chevron-down ms-1 arrow-icon" :class="{ 'rotate-180': activeMenu === 'accessories' }"></i>
            </a>

            <div class="mega-menu shadow-sm" :class="{ 'show-mega': activeMenu === 'accessories' }" @click.stop>
              <div class="container py-5">
                <div class="row justify-content-center">
                  
                  <div class="col-lg-6 pe-lg-4">
                    <div class="row">
                      <div class="col-4 mb-5"><a href="#" class="accessory-link">Airpods Accessories</a></div>
                      <div class="col-4 mb-5"><a href="#" class="accessory-link">Backpacks</a></div>
                      <div class="col-4 mb-5"><a href="#" class="accessory-link">Screen Cleaner</a></div>
                      
                      <div class="col-4"><a href="#" class="accessory-link">Portable Monitors</a></div>
                      <div class="col-4"><a href="#" class="accessory-link">Docking Stations</a></div>
                      <div class="col-4"><a href="#" class="accessory-link">Webcams</a></div>
                    </div>
                  </div>
                  
                  <div class="col-lg-6 ps-lg-4 border-start">
                    <div class="row">
                      
                      <div class="col-4">
                        <a href="#" class="featured-product-link text-decoration-none">
                          <div class="featured-img-clear mb-3">
                            <img src="https://placehold.co/300x300/ffffff/333333?text=Monitor" alt="Portable Monitor">
                          </div>
                          <div class="featured-info">
                            <p class="text-muted mb-1" style="font-size: 11px;">Monitor</p>
                            <h6 class="fw-bold text-dark mb-2" style="font-size: 14px; line-height: 1.4;">
                              16" Portable <br> Monitor
                            </h6>
                            <div>
                              <span class="text-danger fw-bold" style="font-size: 13px;">$275.00</span>
                              <span class="text-muted text-decoration-line-through ms-2" style="font-size: 13px;">$375.00</span>
                            </div>
                          </div>
                        </a>
                      </div>

                      <div class="col-4">
                        <a href="#" class="featured-product-link text-decoration-none">
                          <div class="featured-img-clear mb-3">
                            <img src="https://placehold.co/300x300/ffffff/333333?text=Backpack" alt="Laptop Backpack">
                          </div>
                          <div class="featured-info">
                            <p class="text-muted mb-1" style="font-size: 11px;">Backpack</p>
                            <h6 class="fw-bold text-dark mb-2" style="font-size: 14px; line-height: 1.4;">
                              Terra Recycled 16" <br> Laptop Backpack
                            </h6>
                            <div>
                              <span class="text-danger fw-bold" style="font-size: 13px;">$45.00</span>
                              <span class="text-muted text-decoration-line-through ms-2" style="font-size: 13px;">$63.99</span>
                            </div>
                          </div>
                        </a>
                      </div>

                      <div class="col-4">
                        <a href="#" class="featured-product-link text-decoration-none">
                          <div class="featured-img-clear mb-3">
                            <img src="https://placehold.co/300x300/ffffff/333333?text=Cleaner" alt="Screen Cleaner">
                          </div>
                          <div class="featured-info">
                            <p class="text-muted mb-1" style="font-size: 11px;">Screen Cleaner</p>
                            <h6 class="fw-bold text-dark mb-2" style="font-size: 14px; line-height: 1.4;">
                              Antimicrobial <br> Screen Cleaner Kit
                            </h6>
                            <div>
                              <span class="text-danger fw-bold" style="font-size: 13px;">$7.50</span>
                              <span class="text-muted text-decoration-line-through ms-2" style="font-size: 13px;">$11.00</span>
                            </div>
                          </div>
                        </a>
                      </div>

                    </div>
                  </div>

                </div>
              </div>
            </div>
          </li>

          <li class="nav-item simple-dropdown-wrapper" @click.stop="toggleMenu('about')">
            <a class="nav-link custom-link" href="#" @click.prevent>
              Hỗ trợ 
              <i class="bi bi-chevron-down ms-1 arrow-icon" :class="{ 'rotate-180': activeMenu === 'about' }"></i>
            </a>
            
            <div class="simple-dropdown shadow-sm" :class="{ 'show-dropdown': activeMenu === 'about' }" @click.stop>
              <ul class="simple-dropdown-list">
                <li><a href="#">About Us</a></li>
                <li><a href="#">Why Peel?</a></li>
                <li><a href="#">Help Center</a></li>
              </ul>
            </div>
          </li>

        </ul>
      </div>

      <div class="d-flex align-items-center gap-4 header-icons order-4">
        <a href="#" class="text-dark fs-5"><i class="bi bi-search"></i></a>
        
        <template v-if="isLoggedIn">
          <div 
            class="profile-wrapper position-relative"
            @mouseenter="showProfileMenu = true"
            @mouseleave="showProfileMenu = false"
          >
            <a href="#" class="text-dark fs-5 d-block"><i class="bi bi-person"></i></a>
            
            <div class="profile-dropdown shadow-sm" :class="{ 'show': showProfileMenu }">
              <div class="profile-header border-bottom py-2">
                <h6 class="mb-0 fw-bold text-dark">{{ currentUser?.fullName || 'Khách hàng' }}</h6>
              </div>

              <ul class="profile-dropdown-list list-unstyled mb-0">
                <li>
                  <a href="#" class="d-flex align-items-center text-decoration-none menu-item">
                    <i class="bi bi-person-circle fs-5 me-3"></i>
                    <span>Trang cá nhân</span>
                  </a>
                </li>
                <li>
                  <a href="#" class="d-flex align-items-center text-decoration-none menu-item">
                    <i class="bi bi-box-seam fs-5 me-3"></i>
                    <span>Đơn hàng của tôi</span>
                  </a>
                </li>
                <li>
                  <a href="#" class="d-flex align-items-center text-decoration-none menu-item">
                    <i class="bi bi-heart fs-5 me-3"></i>
                    <span>Sản phẩm yêu thích</span>
                  </a>
                </li>
                <li><hr class="dropdown-divider my-1"></li>
                <li>
                  <a href="#" @click.prevent="handleLogout" class="d-flex align-items-center text-decoration-none menu-item text-danger">
                    <i class="bi bi-box-arrow-right fs-5 me-3"></i>
                    <span>Đăng xuất</span>
                  </a>
                </li>
              </ul>
            </div>
          </div>
        </template>

        <template v-else>
          <div class="d-flex align-items-center gap-3">
            <a href="#" @click.prevent="goToLogin" class="text-dark text-decoration-none fw-bold" style="font-size: 14px;">Đăng nhập</a>
            <span class="text-muted">|</span>
            <a href="#" @click.prevent="goToRegister" class="text-dark text-decoration-none fw-bold" style="font-size: 14px;">Đăng ký</a>
          </div>
        </template>

        <a href="#" @click.prevent="handleCartClick" class="text-dark fs-5">
          <i class="bi bi-cart3"></i>
        </a>
      </div>
    </div>
  </header>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue';
import { useRouter } from 'vue-router'; 

const router = useRouter(); 
const activeMenu = ref<string | null>(null);
const showProfileMenu = ref(false); 

// State quản lý User
const isLoggedIn = ref(false); 
const currentUser = ref<{ fullName: string } | null>(null);

// --- DỮ LIỆU MENU ---
const applePhones = [
  'iPhone 17 Pro Max', 'iPhone 17 Pro', 'iPhone 17', 'iPhone 17 Air',
  'iPhone 16 Pro Max', 'iPhone 16 Pro', 'iPhone 16 Plus', 'iPhone 16e', 'iPhone 16',
  'iPhone 15 Pro Max', 'iPhone 15 Pro', 'iPhone 15 Plus', 'iPhone 15',
  'iPhone 14 Pro Max', 'iPhone 14 Pro', 'iPhone 14 Plus', 'iPhone 14'
];

const samsungPhones = [
  { name: 'Galaxy S26 Ultra', isNew: true }, { name: 'Galaxy S26 Plus', isNew: true }, { name: 'Galaxy S26', isNew: true },
  { name: 'Galaxy S25 Ultra' }, { name: 'Galaxy S25+' }, { name: 'Galaxy S25' },
  { name: 'Galaxy S24 Ultra' }, { name: 'Galaxy S24+' }, { name: 'Galaxy S24' },
  { name: 'Galaxy S23 Ultra' }, { name: 'Galaxy S23+' }, { name: 'Galaxy S23' }
];

const googlePhones = [
  'Pixel 10 Pro XL', 'Pixel 10 Pro', 'Pixel 10', 
  'Pixel 9 Pro XL', 'Pixel 9 Pro', 'Pixel 9', 
  'Pixel 8 Pro', 'Pixel 8', 
  'Pixel 7 Pro', 'Pixel 7', 'Pixel 7a', 
  'Pixel 6 Pro', 'Pixel 6'
];

const megaProducts = [
  { name: 'Super Thin Case', formattedName: 'Super<br>Thin Case', desc: '0.02in Thin', img: 'https://placehold.co/300x300/a8b9d3/ffffff?text=Super+Thin' },
  { name: 'Magnetic Case', formattedName: 'Magnetic<br>Case', desc: 'Embedded Magnets', img: 'https://placehold.co/300x300/4c6b8c/ffffff?text=Magnetic' },
  { name: 'Active Case', formattedName: 'Active<br>Case', desc: '10ft Drop-Tested', img: 'https://placehold.co/300x300/e6e6e6/333333?text=Active' },
  { name: 'Flex Case', formattedName: 'Flex<br>Case', desc: 'Added Grip', img: 'https://placehold.co/300x300/98c5d1/333333?text=Flex' },
  { name: 'RePeel Case', formattedName: 'RePeel<br>Case', desc: 'Recycled Material', img: 'https://placehold.co/300x300/d9d9d9/333333?text=RePeel' }
];

// --- LOGIC MENU & ROUTER ---
const toggleMenu = (menuName: string) => { 
  activeMenu.value = activeMenu.value === menuName ? null : menuName; 
};
const closeMenu = () => { activeMenu.value = null; };

const selectPhone = (name: string) => {
  activeMenu.value = null; 
  const slug = name.toLowerCase().replace(/\s+/g, '-');
  router.push({ name: 'ProductDetailBySlug', params: { slug: slug } });
};

const goHome = () => {
  activeMenu.value = null;
  router.push('/');
};

const goToLogin = () => { router.push('/login'); };
const goToRegister = () => { router.push('/register'); };

// Xử lý khi bấm nút Giỏ Hàng
const handleCartClick = () => {
  if (!isLoggedIn.value) {
    // Nếu chưa đăng nhập -> Đẩy sang login kèm theo query redirect về lại giỏ hàng
    router.push({ path: '/login', query: { redirect: '/cart' } });
  } else {
    // Nếu đã đăng nhập -> Vào thẳng trang giỏ hàng của hệ thống
    router.push('/cart'); 
  }
};

// --- LOGIC AUTHENTICATION ---
const checkAuthStatus = () => {
  const token = localStorage.getItem('accessToken');
  const userRawData = localStorage.getItem('currentUser');

  if (token && userRawData) {
    try {
      const parsedUser = JSON.parse(userRawData);
      isLoggedIn.value = true;
      currentUser.value = parsedUser; 
    } catch (error) {
      console.error("Lỗi parse thông tin user, xóa dữ liệu rác:", error);
      handleLogout(); 
    }
  } else {
    isLoggedIn.value = false;
    currentUser.value = null;
  }
};

const handleLogout = () => {
  showProfileMenu.value = false;
  localStorage.removeItem('accessToken'); 
  localStorage.removeItem('currentUser'); 
  
  isLoggedIn.value = false;
  currentUser.value = null;

  window.dispatchEvent(new Event('storage'));
  router.push('/');
};

// --- LIFECYCLE HOOKS ---
onMounted(() => { 
  window.addEventListener('click', closeMenu); 
  checkAuthStatus(); 
  window.addEventListener('user-logged-in', checkAuthStatus);
  window.addEventListener('storage', checkAuthStatus);
});

onUnmounted(() => { 
  window.removeEventListener('click', closeMenu); 
  window.removeEventListener('user-logged-in', checkAuthStatus);
  window.removeEventListener('storage', checkAuthStatus);
});
</script>

<style scoped>
/* Toàn bộ phần Style cũ giữ nguyên để giữ vững giao diện */
.custom-navbar {
  background-color: #ffffff;
  z-index: 1030;
}
.logo-link {
  text-decoration: none;
  color: #000000;
}
.custom-link {
  color: #111827;
  font-weight: 500;
  font-size: 15px;
  display: flex;
  align-items: center;
}
.custom-link:hover {
  color: #6b7280;
}
.arrow-icon {
  font-size: 11px;
  transition: transform 0.2s ease;
  display: inline-block;
}
.rotate-180 {
  transform: rotate(180deg);
}
.mega-menu-wrapper {
  position: static;
}
.mega-menu {
  position: absolute;
  top: 100%;
  left: 0;
  width: 100%;
  background-color: #ffffff;
  visibility: hidden;
  opacity: 0;
  transform: translateY(10px);
  transition: all 0.25s ease;
  z-index: 1000;
  border-top: 1px solid #e5e7eb;
}
.show-mega {
  visibility: visible;
  opacity: 1;
  transform: translateY(0);
}
.menu-heading {
  font-size: 13px;
  font-weight: 700;
  text-transform: uppercase;
  color: #111827;
  letter-spacing: 0.05em;
  margin-bottom: 15px;
}
.menu-heading-link {
  font-size: 14px;
  font-weight: 600;
  color: #111827;
  text-decoration: none;
}
.menu-heading-link:hover {
  text-decoration: underline;
}
.menu-list {
  list-style: none;
  padding-left: 0;
}
.menu-list li {
  margin-bottom: 8px;
}
.menu-list li a {
  color: #4b5563;
  text-decoration: none;
  font-size: 14px;
}
.menu-list li a:hover {
  color: #000000;
}
.shop-all-link {
  font-weight: 600;
  color: #111827 !important;
}
.new-badge {
  font-size: 8px;
  padding: 3px 6px;
  vertical-align: middle;
}
.mega-product-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 15px;
}
.mega-card {
  background-color: #f9fafb;
  border-radius: 6px;
  overflow: hidden;
  cursor: pointer;
  transition: background-color 0.2s;
  display: flex;
  flex-direction: column;
}
.mega-card:hover {
  background-color: #f3f4f6;
}
.mega-card-img img {
  width: 100%;
  height: auto;
  display: block;
}
.mega-card-body {
  padding: 12px;
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  flex-grow: 1;
}
.mega-card-title {
  font-size: 13px;
  font-weight: 700;
  color: #111827;
  line-height: 1.3;
  margin-bottom: 2px;
}
.mega-card-desc {
  font-size: 11px;
  color: #6b7280;
  margin-bottom: 0;
}
.mega-arrow-btn {
  width: 24px;
  height: 24px;
  background-color: #ffffff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 11px;
  color: #111827;
  border: 1px solid #e5e7eb;
}
.mega-screen-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}
.screen-img-wrapper {
  max-height: 160px;
  overflow: hidden;
}
.accessory-link {
  font-size: 14px;
  color: #4b5563;
  text-decoration: none;
  font-weight: 500;
}
.accessory-link:hover {
  color: #000000;
  text-decoration: underline;
}
.featured-product-link:hover .featured-img-box img,
.featured-product-link:hover .featured-img-clear img {
  transform: scale(1.03);
}
.featured-img-box {
  background-color: #f4f4f4;
  border-radius: 6px;
  overflow: hidden;
}
.featured-img-box img,
.featured-img-clear img {
  width: 100%;
  height: auto;
  transition: transform 0.3s ease;
}
.simple-dropdown-wrapper {
  position: relative;
}
.simple-dropdown {
  position: absolute;
  top: 100%;
  left: 0;
  min-width: 160px;
  background-color: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 4px;
  visibility: hidden;
  opacity: 0;
  transform: translateY(10px);
  transition: all 0.2s ease;
  z-index: 1000;
}
.show-dropdown {
  visibility: visible;
  opacity: 1;
  transform: translateY(0);
}
.simple-dropdown-list {
  list-style: none;
  padding: 8px 0;
  margin-bottom: 0;
}
.simple-dropdown-list li a {
  display: block;
  padding: 8px 16px;
  color: #4b5563;
  text-decoration: none;
  font-size: 14px;
}
.simple-dropdown-list li a:hover {
  background-color: #f9fafb;
  color: #000000;
}
.header-icons a {
  transition: color 0.2s;
}
.header-icons a:hover {
  color: #6b7280 !important;
}

/* PROFILE DROPDOWN MENU */
.profile-dropdown {
  position: absolute;
  top: 130%;
  right: 0;
  width: 240px;
  background: #ffffff;
  border-radius: 8px;
  padding: 10px;
  z-index: 2000;
  display: block;
  visibility: hidden;
  opacity: 0;
  transform: translateY(10px);
  transition: all 0.25s ease;
  border: 1px solid #f0f0f0;
}
.profile-dropdown.show {
  visibility: visible;
  opacity: 1;
  transform: translateY(0);
}
.profile-header {
  padding: 4px 12px 10px 12px;
}
.profile-dropdown-list {
  padding-top: 6px;
}
.menu-item {
  padding: 10px 12px;
  color: #374151;
  font-size: 14px;
  border-radius: 6px;
  transition: background 0.15s;
}
.menu-item:hover {
  background: #f3f4f6;
  color: #000000;
}
.menu-item.text-danger:hover {
  background: #fef2f2;
  color: #dc2626 !important;
}
</style>