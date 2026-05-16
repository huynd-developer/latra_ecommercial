import type { RouteRecordRaw } from 'vue-router';

export const productRoutes: RouteRecordRaw[] = [
  {
    path: 'products',
    name: 'AdminProductManagement',
    component: () => import('./views/ProductView.vue'),
    meta: { title: 'Quản lý Sản phẩm', requiresAuth: true }
  }
];