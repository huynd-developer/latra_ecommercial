export const checkoutRoutes = [
  {
    path: "checkout", 
    name: "ShopCheckoutPage", // 🌟 ĐỔI THÀNH TÊN NÀY ĐỂ TRÁNH BỊ TRÙNG
    component: () => import("@/modules/shop/feature/checkout/views/CheckoutView.vue"),
    meta: { 
      requiresAuth: true,
      title: "Thanh toán đơn hàng" 
    }
  },
  // 🌟 THÊM ROUTE NÀY ĐỂ HỨNG KẾT QUẢ TỪ VNPAY ĐÁ VỀ
  {
    path: "payment/result", 
    name: "PaymentResultPage",
    component: () => import("@/modules/shop/feature/checkout/views/PaymentResultView.vue"),
    meta: { 
      title: "Kết quả thanh toán" 
    }
  }
];