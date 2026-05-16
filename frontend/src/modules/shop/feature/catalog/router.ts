import type { RouteRecordRaw } from 'vue-router';

export const catalogRoutes: RouteRecordRaw[] = [
  {
    path: 'shop', // Hoặc /collections/all tùy bạn
    name: 'ShopCatalog',
    component: () => import('./views/CatalogView.vue'),
    meta: { title: 'Tất cả sản phẩm' }
  },
  {
    path: 'product/:id',
    name: 'ProductDetail',
    component: () => import('./views/ProductDetailView.vue'),
    meta: { title: 'Chi tiết sản phẩm' }
  }
];