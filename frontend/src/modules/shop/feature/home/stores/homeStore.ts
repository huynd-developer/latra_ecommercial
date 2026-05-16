// frontend/src/modules/shop/feature/home/stores/homeStore.ts
import { defineStore } from 'pinia';
import { ref } from 'vue';
import { homeService } from '../services/homeService';
import type { Banner, Feature } from '../types/home.type';
import type { Product } from '@/modules/admin/feature/product/types/product.type';
import type { FlashSale } from '@/modules/admin/feature/promotion/types/promotion.type';

export const useHomeStore = defineStore('homeStore', () => {
  // --- STATE TĨNH ---
  const banners = ref<Banner[]>([
    {
      image: 'https://int.buypeel.com/cdn/shop/files/Base_Station_Hero_2_2x_8eb14032-17a4-44df-b4d6-84e1fc3f59fa.jpg?v=1730999516&width=2000',
      title: 'Một Bộ Sạc.<br>Không Còn Lộn Xộn.',
      subtitle: 'Trạm sạc 3-trong-1 siêu gọn gàng cho điện thoại, đồng hồ và tai nghe của bạn.',
      btnText: 'MUA NGAY', link: '/shop'
    },
    {
      image: 'https://int.buypeel.com/cdn/shop/files/peel_home_hero_desktop_1.jpg?v=1725916053&width=2000',
      title: 'Ốp Lưng Siêu Mỏng.<br>Bảo Vệ Tối Ưu.',
      subtitle: 'Giữ nguyên vẻ đẹp nguyên bản của thiết bị với ốp lưng siêu mỏng LATRA.',
      btnText: 'XEM ỐP LƯNG', link: '/shop'
    }
  ]);

  const features = ref<Feature[]>([
    { image: 'https://int.buypeel.com/cdn/shop/files/peel_home_hero_desktop_1.jpg?v=1725916053&width=400', title: 'Super Thin Case', description: '0.02in Thin<br>Branding-Free<br>Everyday Protection', link: '/shop' },
    { image: 'https://int.buypeel.com/cdn/shop/files/Collection_Apple_WatchCase_83907797-29be-410a-b850-93a830239276.jpg?v=1734633765&width=400', title: 'Magnetic Case', description: '0.07in Thin<br>Embedded Magnets<br>Drop-Tested', link: '/shop' },
    { image: 'https://placehold.co/400x440/e9e9e9/333?text=Active+Case', title: 'Active Case', description: '0.08in Thin<br>Reinforced Bumper<br>10ft Drop-Tested', footnote: '*previously named the Bumper Case', link: '/shop' },
    { image: 'https://int.buypeel.com/cdn/shop/files/i16_Glass_Features.png?v=1743691200&width=400', title: 'Screen Protection', description: 'Protect Your Screen with HD Clarity', link: '/shop' }
  ]);

  // --- STATE ĐỘNG (Từ API) ---
  const featuredProducts = ref<Product[]>([]);
  const activeFlashSale = ref<FlashSale | null>(null);
  const isLoading = ref(false);

  const fetchHomeData = async () => {
    isLoading.value = true;
    try {
      const [products, flashSale] = await Promise.all([
        homeService.getFeaturedProducts(),
        homeService.getActiveFlashSale()
      ]);
      featuredProducts.value = products;
      activeFlashSale.value = flashSale;
    } catch (error) {
      console.error("Lỗi tải dữ liệu trang chủ", error);
    } finally {
      isLoading.value = false;
    }
  };

  return { banners, features, featuredProducts, activeFlashSale, isLoading, fetchHomeData };
});