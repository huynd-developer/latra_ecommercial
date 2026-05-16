import type { RouteRecordRaw } from 'vue-router';

export const checkoutRoutes: RouteRecordRaw[] = [
  {
    path: 'checkout',
    name: 'ShopCheckout',
    // Đã sửa: Đổi từ components -> views và dùng @/
    component: () => import('@/modules/shop/feature/checkout/views/CheckoutView.vue'),
    meta: { 
      title: 'Thanh toán đơn hàng', 
      requiresAuth: true 
    }
  },
  {
    path: 'payment/vnpay-return',
    name: 'VnpayReturn',
    component: () => import('@/modules/shop/feature/checkout/views/VnPayReturnView.vue'),
    meta: { title: 'Kết quả giao dịch', requiresAuth: false }
  }
];