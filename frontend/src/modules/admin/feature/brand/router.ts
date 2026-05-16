import type { RouteRecordRaw } from 'vue-router';

export const brandRoutes: RouteRecordRaw[] = [
  {
    path: 'brands',
    name: 'AdminBrandManagement',
    component: () => import('./views/BrandView.vue'),
    meta: { title: 'Quản lý thương hiệu'}
  }
];