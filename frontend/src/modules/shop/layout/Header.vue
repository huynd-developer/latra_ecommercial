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
              Cases 
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
              Screen Protection 
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
              Charging 
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
              Accessories 
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
              About 
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
        <a href="#" class="text-dark fs-5"><i class="bi bi-person"></i></a>
        <a href="#" class="text-dark fs-5"><i class="bi bi-cart3"></i></a>
      </div>
    </div>
  </header>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue';
import { useRouter } from 'vue-router'; // [MỚI] Import useRouter

const router = useRouter(); // [MỚI] Khởi tạo router
const activeMenu = ref<string | null>(null);

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

const toggleMenu = (menuName: string) => { 
  activeMenu.value = activeMenu.value === menuName ? null : menuName; 
};

const closeMenu = () => { activeMenu.value = null; };

// [MỚI] Sửa hàm selectPhone để dùng Vue Router thay vì emit
const selectPhone = (name: string) => {
  activeMenu.value = null; 
  
  // Biến tên điện thoại thành slug (VD: "iPhone 17 Pro Max" -> "iphone-17-pro-max")
  const slug = name.toLowerCase().replace(/\s+/g, '-');
  
  // Push qua Router (yêu cầu cấu hình route có name là 'Category')
  router.push({ name: 'Category', params: { slug: slug } });
  
  // Nếu bạn chưa cấu hình name route, bạn có thể dùng thẳng path:
  // router.push(`/category/${slug}`);
};

// [MỚI] Sửa hàm goHome để chuyển về trang chủ bằng Router
const goHome = () => {
  activeMenu.value = null;
  router.push('/');
};

onMounted(() => { window.addEventListener('click', closeMenu); });
onUnmounted(() => { window.removeEventListener('click', closeMenu); });
</script>

<style scoped>
.custom-navbar { 
  background: #fff; 
  z-index: 1020; 
  position: sticky; 
  top: 0; 
  transition: all 0.3s ease; 
}
.custom-link { color: #000 !important; font-weight: 500; font-size: 15px; padding: 10px 15px; }

/* MEGA MENU CHUNG */
.mega-menu {
  position: absolute; top: 100%; left: 0; width: 100vw; background: white;
  border-top: 1px solid #f0f0f0; z-index: 999;
  visibility: hidden; opacity: 0; pointer-events: none;
  transform: translateY(10px);
  transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1);
  max-height: 85vh; overflow-y: auto;
}
.show-mega { visibility: visible; opacity: 1; pointer-events: auto; transform: translateY(0); }
.arrow-icon { display: inline-block; transition: transform 0.3s ease; font-size: 12px; }
.rotate-180 { transform: rotate(180deg); }

/* SIMPLE DROPDOWN CHO ABOUT */
.simple-dropdown-wrapper { position: relative; }
.simple-dropdown {
  position: absolute;
  top: 100%;
  left: 50%;
  transform: translateX(-50%) translateY(10px);
  background: white;
  min-width: 180px;
  border-top: 1px solid #f0f0f0;
  border-bottom-left-radius: 12px;
  border-bottom-right-radius: 12px;
  padding: 15px 0;
  visibility: hidden;
  opacity: 0;
  pointer-events: none;
  transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1);
  z-index: 1000;
}
.simple-dropdown.show-dropdown {
  visibility: visible;
  opacity: 1;
  pointer-events: auto;
  transform: translateX(-50%) translateY(0);
}
.simple-dropdown-list { list-style: none; padding: 0; margin: 0; }
.simple-dropdown-list li { margin-bottom: 5px; }
.simple-dropdown-list li:last-child { margin-bottom: 0; }
.simple-dropdown-list a {
  display: block;
  padding: 8px 24px;
  color: #000;
  text-decoration: none;
  font-size: 15px;
  font-weight: 500;
  transition: background-color 0.2s, color 0.2s;
}
.simple-dropdown-list a:hover {
  background-color: #f9f9f9;
  color: #555;
}

/* LINK CHO PHẦN ACCESSORIES CÓ VIỀN TRÁI */
.accessory-link {
  font-weight: 600;
  color: #000;
  font-size: 14px;
  text-decoration: none;
  display: inline-block;
  border-left: 2px solid #e5e5e5;
  padding-left: 12px;
  transition: border-color 0.2s, color 0.2s;
}
.accessory-link:hover {
  border-left-color: #000;
}

/* LINK MENU DẠNG HEADING (CHARGING) */
.menu-heading-link {
  font-weight: 700; 
  color: #000; 
  font-size: 14px; 
  text-decoration: none;
  display: inline-block;
  transition: color 0.2s;
}
.menu-heading-link:hover {
  text-decoration: underline;
}

/* THỂ HIỆN SẢN PHẨM CHARGING VÀ ACCESSORIES */
.featured-product-link:hover .featured-img-box img,
.featured-product-link:hover .featured-img-clear img {
  transform: scale(1.05);
}

.featured-img-box {
  background-color: #efefef;
  border-radius: 12px;
  overflow: hidden;
  aspect-ratio: 1; 
  display: flex;
  align-items: center;
  justify-content: center;
}

.featured-img-clear {
  background-color: transparent;
  overflow: hidden;
  aspect-ratio: 1; 
  display: flex;
  align-items: center;
  justify-content: center;
}

.featured-img-box img,
.featured-img-clear img {
  width: 90%;
  height: 90%;
  object-fit: contain;
  transition: transform 0.4s ease;
}

/* BÊN TRÁI: DANH SÁCH MENU (IPHONE/SCREEN) */
.menu-heading { font-weight: 700; margin-bottom: 1.2rem; color: #000; font-size: 14px; }
.menu-list { list-style: none; padding: 0; }
.menu-list li { margin-bottom: 12px; }
.menu-list a { text-decoration: none; color: #444; font-size: 13.5px; transition: color 0.2s; }
.menu-list a:hover { color: #000; text-decoration: underline; }
.shop-all-link { color: #000 !important; font-weight: 500; }
.new-badge { font-size: 9px; padding: 3px 6px; letter-spacing: 0.5px; background-color: #2b6cb0 !important; vertical-align: middle;}

/* LƯỚI CARD BÊN PHẢI (CASES & SCREEN) */
.mega-product-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 16px; max-width: 520px; padding-bottom: 20px; }
.mega-screen-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 20px; max-width: 650px; }

/* STYLE THẺ CARD (CASES & SCREEN) */
.mega-card { background: #fff; border: 1px solid #f0f0f0; border-radius: 16px; overflow: hidden; cursor: pointer; transition: all 0.3s ease; display: flex; flex-direction: column; }
.mega-card:hover { border-color: #dcdcdc; box-shadow: 0 10px 25px rgba(0,0,0,0.05); }

.mega-card-img { width: 100%; aspect-ratio: 1; background-color: #f8f8f8; display: flex; align-items: center; justify-content: center; }
.screen-img-wrapper { aspect-ratio: 4 / 3; }
.mega-card-img img { width: 100%; height: 100%; object-fit: cover; transition: transform 0.4s ease; }
.mega-card:hover .mega-card-img img { transform: scale(1.04); }

.mega-card-body { padding: 16px 14px; display: flex; justify-content: space-between; align-items: flex-start; flex-grow: 1; }
.mega-card-info { padding-right: 6px; }
.mega-card-title { font-size: 14px; font-weight: 600; line-height: 1.35; margin-bottom: 4px; color: #000; }
.mega-card-desc { font-size: 11px; color: #888; margin: 0; }

.mega-arrow-btn { width: 32px; height: 32px; border-radius: 50%; background-color: #F1F1F1; display: flex; align-items: center; justify-content: center; color: #000; font-size: 13px; flex-shrink: 0; transition: all 0.2s; }
.mega-card:hover .mega-arrow-btn { background-color: #e4e4e4; }
</style>