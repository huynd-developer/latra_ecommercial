// frontend/src/router/index.ts
import { createRouter, createWebHistory } from "vue-router";

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
    // 1. GIAO DIỆN KHÁCH HÀNG (SHOP)
    {
      path: "/",
      name: "ShopLayout",
      // component: () => import("@/layout/ShopLayout.vue"), 
      children: [
        ...homeRoutes,      
        ...catalogRoutes,
        ...cartRoutes,
        ...checkoutRoutes,
      ]
    },

    // 2. GIAO DIỆN QUẢN TRỊ (ADMIN)
    {
      path: "/admin",
      name: "AdminLayout",
      redirect: '/admin/dashboard', 
      component: () => import("@/modules/admin/layout/AdminView.vue"),
      
      children: [
        // --- CÁC ROUTE ĐÃ ĐƯỢC CHIA MODULE ---
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

        // --- CÁC ROUTE CHƯA CÓ FILE ROUTER.TS (THÊM MỚI ĐỂ FIX LỖI WARNING) ---
        {
          path: "pos",
          name: "AdminPOS",
          component: () => import("@/modules/pos/PosManager.vue"),
          meta: { title: "Bán hàng tại quầy (POS)" }
        },
        {
          path: "khach-hang",
          name: "AdminCustomer",
          component: () => import("@/modules/admin/feature/managers/views/CustomerManager.vue"),
          meta: { title: "Quản lý Khách hàng" }
        },
        {
          path: "nhan-vien",
          name: "AdminEmployee",
          component: () => import("@/modules/admin/feature/managers/views/EmployeeManager.vue"),
          meta: { title: "Quản lý Nhân viên" }
        },
        {
          path: "settings",
          name: "AdminSettings",
          component: () => import("@/modules/admin/feature/settings/GenericManager.vue"),
          meta: { title: "Cài đặt hệ thống" }
        }
      ],
    },
  ],

  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition;
    } else {
      return { top: 0, behavior: "smooth" };
    }
  },
});
router.onError((error, to) => {
  if (error.message.includes('Failed to fetch dynamically imported module') || error.message.includes('Importing a module script failed')) {
    // Nếu lỗi do mạng/load file không kịp, ép trình duyệt tải lại trang đó luôn
    window.location.href = to.fullPath;
  } else {
    console.error('Lỗi Router Navigation:', error);
  }
});
export default router;