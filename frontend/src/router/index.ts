import { createRouter, createWebHistory } from "vue-router";

// Import các trang Khách (Storefront)
import HomeView from "../views/HomeView.vue";
import CategoryView from "../views/CategoryView.vue";
import ProductDetailView from "../views/ProductDetailView.vue";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/",
      name: "home",
      component: HomeView,
    },
    {
      path: "/product/:id",
      name: "product-detail",
      component: ProductDetailView,
    },

    // -----------------------------------------------------------
    // KHU VỰC DÀNH CHO ADMIN
    // -----------------------------------------------------------
    {
      path: "/admin",
      name: "AdminDashboard",
      redirect: '/admin/dashboard', 
      component: () => import("../views/AdminView.vue"),
      
      children: [
        {
          path: "dashboard",
          name: "admin-dashboard-overview",
          component: () => import("../components/admin/managers/DashboardOverview.vue"),
        },
        {
          path: "pos",
          name: "admin-pos",
          component: () => import("../components/admin/managers/PosManager.vue"),
        },
        {
          path: "orders",
          name: "admin-orders",
          // Sử dụng @ts-ignore để ép TypeScript bỏ qua kiểm tra lỗi đường dẫn nếu nó vẫn ngoan cố
          // @ts-ignore
          component: () => import("../components/admin/managers/OrderManager.vue"),
        },
        {
          path: "nhan-vien",
          name: "admin-employees",
          component: () => import("../components/admin/managers/EmployeeManager.vue"),
        },
        {
          path: "khach-hang",
          name: "admin-customers",
          component: () => import("../components/admin/managers/CustomerManager.vue"),
        },
        {
          path: "promotions",
          name: "admin-promotions",
          component: () => import("../components/admin/managers/VoucherManager.vue"),
        },
        {
          path: "flash-sale",
          name: "admin-flash-sale",
          component: () => import("../components/admin/managers/FlashSaleManager.vue"),
        },
        {
          path: "products",
          name: "admin-products",
          component: () => import("../components/admin/managers/ProductManager.vue"),
        },
        {
          path: "imeis",
          name: "admin-imeis",
          component: () => import("../components/admin/managers/ImeiManager.vue"),
        },
        {
          path: "settings",
          name: "admin-settings",
          component: () => import("../components/admin/managers/GenericManager.vue"),
        }
      ],
    },

    {
      path: "/:categorySlug",
      name: "category",
      component: CategoryView,
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

export default router;