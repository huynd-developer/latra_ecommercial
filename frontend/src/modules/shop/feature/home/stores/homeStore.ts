import { defineStore } from 'pinia';
import { ref } from 'vue';
import { homeService } from '../services/homeService';
import type { Banner, Feature } from '../types/home.type';
import type { Product } from '@/modules/admin/feature/product/types/product.type';
import type { FlashSale } from '@/modules/admin/feature/promotion/types/promotion.type';

export const useHomeStore = defineStore('homeStore', () => {
  // --- STATE TĨNH ---
  // Đã cập nhật link ảnh thật chuẩn studio cực sang cho cả 3 Banner mẫu
  const banners = ref<Banner[]>([
    {
      // Banner 1: Mô phỏng ảnh mẫu chuỗi ốp lưng iPhone phối màu pastel mượt mà
      image: 'https://images.unsplash.com/photo-1603302576837-37561b2e2302?q=80&w=2000&auto=format&fit=cover',
      title: 'Sleek. Thin.<br>Made for the iPhone 17.',
      subtitle: 'Mượt mà. Siêu mỏng. Thiết kế tối giản tinh tế, ôm khít hoàn hảo từng đường nét của dòng iPhone mới nhất.',
      btnText: 'SHOP NOW',
      link: '/shop'
    },
    {
      // Banner 2: Mô phỏng ảnh mẫu dòng ốp lưng mờ cao cấp cho Samsung Galaxy trên nền xám sạch
      image: 'https://images.unsplash.com/photo-1610945265064-0e34e5519bbf?q=80&w=2000&auto=format&fit=cover',
      title: 'DESIGNED FOR<br>Galaxy S26.',
      subtitle: 'Bộ sưu tập ốp lưng nhám mờ chống vân tay độc quyền kết hợp cùng kính cường lực bảo vệ màn hình độ nét HD.',
      btnText: 'SHOP NOW',
      link: '/shop'
    },
    {
      // Banner 3: Mô phỏng ảnh mẫu trạm sạc không dây 3-trong-1 gọn gàng trên bàn làm việc gỗ cao cấp
      image: 'https://images.unsplash.com/photo-1622445262465-2481c4574875?q=80&w=2000&auto=format&fit=cover',
      title: 'One Charger.<br>Zero Clutter.',
      subtitle: 'Trạm sạc không dây 3-trong-1 siêu gọn gàng cho điện thoại, đồng hồ và tai nghe của bạn — loại bỏ hoàn toàn dây cáp lộn xộn.',
      btnText: 'SHOP NOW',
      link: '/shop'
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