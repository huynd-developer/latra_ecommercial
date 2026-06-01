import { createRouter, createWebHistory, RouterView } from "vue-router";

// --- IMPORT ROUTER CỦA TỪNG MODULE (SHOP) ---
import { homeRoutes } from "@/modules/shop/feature/home/router";
import { catalogRoutes } from "@/modules/shop/feature/catalog/router";
import { cartRoutes } from "@/modules/shop/feature/cart/router";
import { checkoutRoutes } from "@/modules/shop/feature/checkout/router";

// --- IMPORT ROUTER CỦA TỪNG MODULE (ADMIN) ---
import { brandRoutes } from "@/modules/admin/feature/brand/router";
import { categoryRoutes } from "@/modules/admin/feature/category/router";
import { productRoutes } from "@/modules/admin/feature/product/router";
import { inventoryRoutes } from "@/modules/admin/feature/inventory/router";
import { promotionRoutes } from "@/modules/admin/feature/promotion/router";
import { orderAdminRoutes } from "@/modules/admin/feature/order/router";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/login",
      name: "Login",
      component: () => import("@/modules/auth/views/LoginView.vue"),
      meta: { requiresGuest: true },
    },
    {
      path: "/register",
      name: "Register",
      component: () => import("@/modules/auth/views/RegisterView.vue"),
      meta: { requiresGuest: true },
    },
    {
      path: "/",
      name: "ShopLayout",
      component: RouterView, 
      children: [
        ...checkoutRoutes, 
        ...homeRoutes,
        ...catalogRoutes,
        ...cartRoutes,
        {
          path: "products/:slug",
          name: "ProductDetailBySlug", 
          component: () => import("@/modules/shop/feature/catalog/views/ProductDetailView.vue"),
        },
        {
          path: "product/:id",
          name: "ProductDetailById", 
          component: () => import("@/modules/shop/feature/catalog/views/ProductDetailView.vue"),
        },
      ],
    },
    {
      path: "/admin",
      name: "AdminLayout",
      component: () => import("@/modules/admin/layout/AdminView.vue"),
      children: [
        {
          path: "pos",
          name: "AdminPOS",
          component: () => import("@/modules/pos/PosManager.vue"),
          meta: { title: "Bán hàng tại quầy (POS)" },
        },
        {
          path: "customers",
          alias: ["khach-hang"], 
          name: "AdminCustomer",
          component: () => import("@/modules/admin/feature/managers/views/CustomerManager.vue"),
          meta: { title: "Quản lý Khách hàng" },
        },
        {
          path: "employees",
          alias: ["nhan-vien"], 
          name: "AdminEmployee",
          component: () => import("@/modules/admin/feature/managers/views/EmployeeManager.vue"),
          meta: { title: "Quản lý Nhân viên" },
        },
        {
          path: "settings",
          name: "AdminSettings",
          component: () => import("@/modules/admin/feature/settings/GenericManager.vue"),
          meta: { title: "Cài đặt hệ thống" },
        },
        {
          path: "dashboard",
          name: "admin-dashboard-overview",
          component: () => import("@/modules/admin/feature/dashboard/DashboardOverview.vue"),
        },
        ...brandRoutes,
        ...categoryRoutes,
        ...productRoutes,
        ...inventoryRoutes,
        ...promotionRoutes,
        ...orderAdminRoutes,
      ],
    },
  ],
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) return savedPosition;
    return { top: 0, behavior: "smooth" };
  },
});

router.onError((error, to) => {
  const isChunkError = 
    error.message.includes("Failed to fetch dynamically imported module") ||
    error.message.includes("Importing a module script failed");

  if (isChunkError) {
    const hasReloaded = sessionStorage.getItem("chunk-error-reloaded");
    if (!hasReloaded) {
      sessionStorage.setItem("chunk-error-reloaded", "true");
      window.location.href = to.fullPath;
    } else {
      sessionStorage.removeItem("chunk-error-reloaded"); 
    }
  }
});

router.afterEach(() => {
  sessionStorage.removeItem("chunk-error-reloaded");
});

// =========================================================================
// ROUTER NAVIGATION GUARD
// =========================================================================
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('accessToken');
  const userRawData = localStorage.getItem('currentUser');

  const isAuthenticated = !!token && token !== 'null' && token !== 'undefined' && !!userRawData;
  let userRole = 'USER'; 
  
  if (isAuthenticated && userRawData) {
    try {
      const parsedUser = JSON.parse(userRawData);
      let rawRole = parsedUser.role || 
                    (Array.isArray(parsedUser.roles) ? parsedUser.roles[0] : '') || 
                    (Array.isArray(parsedUser.authorities) ? (parsedUser.authorities[0].authority || parsedUser.authorities[0]) : '');

      if (rawRole && typeof rawRole === 'string') {
        userRole = rawRole.toUpperCase().replace('ROLE_', '');
      }
    } catch (e) {
      console.error("Lỗi parse dữ liệu User:", e);
    }
  }

  const isInternalTeam = ['OWNER', 'MANAGER', 'CASHIER'].includes(userRole);

  if (to.path.startsWith('/admin')) {
    if (!isAuthenticated) return next({ path: '/login', replace: true });
    if (!isInternalTeam) return next({ path: '/', replace: true });
    
    if (to.path === '/admin' || to.path === '/admin/') {
      if (userRole === 'CASHIER') return next({ path: '/admin/pos', replace: true });
      return next({ path: '/admin/dashboard', replace: true });
    }

    if (userRole === 'OWNER') return next(); 

    const pathSegments = to.path.split('/');
    let basePath = pathSegments.length >= 3 ? `/${pathSegments[1]}/${pathSegments[2]}` : to.path;
    basePath = basePath.replace(/\/$/, ""); 

    if (basePath === '/admin/khach-hang') basePath = '/admin/customers';
    if (basePath === '/admin/nhan-vien') basePath = '/admin/employees';

    // 🛠️ ĐÃ FIX LOGIC THỰC TẾ: Bổ sung bọc đầu bọc đuôi cho cả route /inventory và /imeis
    const routePermissions: Record<string, string[]> = {
      '/admin/dashboard': ['MANAGER'],
      '/admin/products': ['MANAGER'],
      '/admin/categories': ['MANAGER'],
      '/admin/brands': ['MANAGER'],
      '/admin/inventory': ['MANAGER', 'CASHIER'], // Bẫy thêm trường hợp đặt path theo module tên file
      '/admin/imeis': ['MANAGER', 'CASHIER'],     // Giữ nguyên theo logic cũ của mày
      '/admin/promotions': ['MANAGER', 'CASHIER'],
      '/admin/flash-sale': ['MANAGER'],
      '/admin/pos': ['MANAGER', 'CASHIER'],
      '/admin/orders': ['MANAGER', 'CASHIER'],
      '/admin/customers': ['MANAGER', 'CASHIER'],
      '/admin/employees': [], 
      '/admin/settings': []   
    };

    const allowedRoles = routePermissions[basePath];

    if (allowedRoles && allowedRoles.includes(userRole)) {
      return next(); 
    } else {
      if (userRole === 'CASHIER') return next({ path: '/admin/pos', replace: true });
      if (userRole === 'MANAGER') return next({ path: '/admin/dashboard', replace: true });
      return next({ path: '/', replace: true });
    }
  } 
  
  if (to.meta.requiresGuest && isAuthenticated) {
    if (isInternalTeam) {
      if (userRole === 'CASHIER') return next({ path: '/admin/pos', replace: true });
      return next({ path: '/admin/dashboard', replace: true });
    }
    return next({ path: '/', replace: true });
  } 
  
  return next(); 
});

export default router;