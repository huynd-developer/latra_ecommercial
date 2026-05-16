// frontend/src/modules/shop/feature/home/services/homeService.ts
import request from '@/common/services/request';
import type { PaginatedResponse } from '../../catalog/types/catalog.type';
import type { Product } from '@/modules/admin/feature/product/types/product.type';
import type { FlashSale } from '@/modules/admin/feature/promotion/types/promotion.type';
import type { ApiResponse } from '@/common/types/api.type';

export const homeService = {
  // 1. Lấy 8 sản phẩm mới nhất lên Trang chủ
  getFeaturedProducts: async (): Promise<Product[]> => {
    // Gọi API products với size=8, sort mới nhất
    const res = await request.get<any, ApiResponse<PaginatedResponse<Product>>>('/products?page=0&size=8');
    return res.data?.products || [];
  },

  // 2. Lấy Chương trình Flash Sale đang ACTIVE
  getActiveFlashSale: async (): Promise<FlashSale | null> => {
    const res = await request.get<any, ApiResponse<FlashSale[]>>('/flash-sales/admin');
    const allSales = res.data || [];
    // Tìm cái đang chạy
    const activeSale = allSales.find(sale => sale.status === 'ACTIVE');
    // Hoặc tìm cái sắp chạy gần nhất nếu không có cái nào đang chạy
    const upcomingSale = allSales.find(sale => sale.status === 'UPCOMING');
    return activeSale || upcomingSale || null;
  }
};