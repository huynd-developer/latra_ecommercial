import { reactive, computed } from 'vue'
import type { Product } from '../data/db'

// Tạo một kho dữ liệu dùng chung cho toàn bộ app
export const cartStore = reactive({
  items: [] as Array<{ product: Product, quantity: number }>,
  isOpen: false, // Trạng thái mở/đóng của Cart Drawer

  // Mở / Đóng giỏ hàng
  toggleCart() {
    this.isOpen = !this.isOpen
  },

  // Thêm vào giỏ
  addToCart(product: Product, quantity: number) {
    const existingItem = this.items.find(item => item.product.id === product.id)
    if (existingItem) {
      existingItem.quantity += quantity
    } else {
      this.items.push({ product, quantity })
    }
    this.isOpen = true // Thêm xong tự động mở giỏ hàng
  },

  // Xóa khỏi giỏ
  removeItem(productId: number) {
    this.items = this.items.filter(item => item.product.id !== productId)
  }
})

// Tính tổng tiền
export const cartTotal = computed(() => {
  return cartStore.items.reduce((total, item) => total + (item.product.price * item.quantity), 0)
})

// Tính tổng số lượng món
export const cartCount = computed(() => {
  return cartStore.items.reduce((count, item) => count + item.quantity, 0)
})