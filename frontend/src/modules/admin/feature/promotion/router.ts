import type { RouteRecordRaw } from 'vue-router';

export const promotionRoutes: RouteRecordRaw[] = [
  {
    path: 'flash-sale', // Sửa từ 'flash-sales' thành 'flash-sale'
    name: 'AdminFlashSale',
    component: () => import('./views/FlashSaleView.vue'),
    meta: { title: 'Quản lý Flash Sale', requiresAuth: true }
  },
  {
    path: 'promotions', // Sửa từ 'vouchers' thành 'promotions' cho khớp warning
    name: 'AdminVoucher',
    component: () => import('./views/VoucherViews.vue'),
    meta: { title: 'Quản lý Khuyến mãi', requiresAuth: true }
  }
];