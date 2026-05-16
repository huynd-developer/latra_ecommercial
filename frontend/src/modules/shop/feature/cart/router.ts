import type { RouteRecordRaw } from 'vue-router';

export const cartRoutes: RouteRecordRaw[] = [
  {
    path: 'cart',
    name: 'ShopCart',
    component: () => import('./views/CartView.vue'),
    meta: { 
      title: 'Giỏ hàng của bạn', 
      requiresAuth: false 
    }
  }
];