// frontend/src/modules/shop/feature/cart/stores/cartStore.ts
import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { cartService } from '../services/cartService';
import { useAppStore } from '@/common/store/app.store';
import type { CartResponse, CartItemResponse, CartItemRequest } from '../types/cart.type';
const isOpen = ref(false); // Thêm dòng này

const toggleCart = () => { isOpen.value = !isOpen.value; };
export const useCartStore = defineStore('cartStore', () => {
  const cartData = ref<CartResponse | null>(null);
  const localCart = ref<CartItemRequest[]>(JSON.parse(localStorage.getItem('guest_cart') || '[]'));
  const isLoading = ref(false);
  const appStore = useAppStore();
  
  const getUserId = () => 1; 

  const saveLocalCart = () => {
    localStorage.setItem('guest_cart', JSON.stringify(localCart.value));
  };

  const totalItemsCount = computed(() => {
    if (getUserId()) return cartData.value?.items.reduce((sum, i) => sum + i.quantity, 0) || 0;
    return localCart.value.reduce((sum, i) => sum + i.quantity, 0);
  });

  const hasInvalidItems = computed(() => {
    // Đã bọc an toàn tuyệt đối, chỉ true khi item đó thực sự báo lỗi từ backend
    return cartData.value?.items.some(i => i.isAvailable === false) || false;
  });

  // ==========================================
  // HÀM CHUẨN HÓA DỮ LIỆU JSON TỪ BACKEND
  // (Fix triệt để lỗi Jackson tự đổi tên boolean)
  // ==========================================
  const normalizeCartData = (data: CartResponse) => {
    if (data && data.items) {
      data.items = data.items.map((item: any) => ({
        ...item,
        // Nếu API trả về 'available' thay vì 'isAvailable', ta tự gán lại cho đúng Model Frontend
        isAvailable: item.isAvailable !== undefined ? item.isAvailable : item.available
      }));
    }
    return data;
  };

  const fetchCart = async () => {
    const userId = getUserId();
    if (!userId) return; 
    
    try {
      isLoading.value = true;
      const rawData = await cartService.getCart(userId);
      cartData.value = normalizeCartData(rawData); // Áp dụng chuẩn hóa
    } catch (err: any) {
      appStore.notifyError('Không thể tải giỏ hàng');
    } finally {
      isLoading.value = false;
    }
  };

  const addItem = async (variantId: number, quantity: number = 1) => {
    const userId = getUserId();
    if (!userId) {
      const existing = localCart.value.find(i => i.variantId === variantId);
      if (existing) {
        if (existing.quantity + quantity > 5) return appStore.notifyError("Tối đa 5 sản phẩm!");
        existing.quantity += quantity;
      } else {
        localCart.value.push({ variantId, quantity });
      }
      saveLocalCart();
      appStore.notifySuccess("Đã thêm vào giỏ");
      return;
    }

    try {
      appStore.startLoading();
      const rawData = await cartService.addToCart(userId, { variantId, quantity });
      cartData.value = normalizeCartData(rawData);
      appStore.notifySuccess("Đã thêm vào giỏ");
    } catch (err: any) {
      appStore.notifyError(err.response?.data?.message || 'Lỗi thêm vào giỏ');
    } finally {
      appStore.stopLoading();
    }
  };

  const updateItemQty = async (cartItemId: number, quantity: number) => {
    const userId = getUserId();
    if (!userId) return;
    try {
      appStore.startLoading();
      const rawData = await cartService.updateQuantity(userId, cartItemId, quantity);
      cartData.value = normalizeCartData(rawData);
    } catch (err: any) {
      appStore.notifyError(err.response?.data?.message || 'Lỗi cập nhật số lượng');
      await fetchCart(); 
    } finally {
      appStore.stopLoading();
    }
  };

  const removeItem = async (cartItemId: number) => {
    const userId = getUserId();
    if (!userId) return;
    try {
      appStore.startLoading();
      const rawData = await cartService.removeItem(userId, cartItemId);
      cartData.value = normalizeCartData(rawData);
    } catch (err: any) {
      appStore.notifyError('Lỗi xóa sản phẩm');
    } finally {
      appStore.stopLoading();
    }
  };

  const syncGuestCart = async () => {
    const userId = getUserId();
    if (userId && localCart.value.length > 0) {
      try {
        const rawData = await cartService.mergeCart(userId, localCart.value);
        cartData.value = normalizeCartData(rawData);
        localCart.value = [];
        saveLocalCart(); 
      } catch (err) {
        console.error("Lỗi đồng bộ giỏ hàng", err);
      }
    }
  };

  return { 
    cartData, localCart, isLoading, totalItemsCount, hasInvalidItems, isOpen,
    fetchCart, addItem, updateItemQty, removeItem, syncGuestCart, toggleCart 
  };
});