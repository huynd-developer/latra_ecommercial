import type { RouteRecordRaw } from 'vue-router';

export const orderAdminRoutes: RouteRecordRaw[] = [
  {
    path: 'orders',
    name: 'AdminOrderManagement',
    component: () => import('./views/OrderManagerView.vue'),
    meta: { title: 'Quản lý Hóa Đơn', requiresAuth: true }
  }
];