import type { RouteRecordRaw } from 'vue-router';

export const categoryRoutes: RouteRecordRaw[] = [
  {
    path: 'categories',
    name: 'AdminCategoryManagement',
    component: () => import('./views/CategoryView.vue'),
    meta: { title: 'Quản lý Danh mục', requiresAuth: true }
  }
];