export const checkoutRoutes = [
  {
    path: "checkout", 
    name: "ShopCheckoutPage", // 🌟 ĐỔI THÀNH TÊN NÀY ĐỂ TRÁNH BỊ TRÙNG
    component: () => import("@/modules/shop/feature/checkout/views/CheckoutView.vue"),
    meta: { 
      requiresAuth: true,
      title: "Thanh toán đơn hàng" 
    }
  }
];