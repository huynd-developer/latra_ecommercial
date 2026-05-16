// frontend/src/modules/shop/feature/catalog/services/catalogService.ts
import request from '@/common/services/request';
import type { CatalogFilter, PaginatedResponse, Brand, Category } from '../types/catalog.type';
import type { ApiResponse } from '@/common/types/api.type';
import type { Product } from '@/modules/admin/feature/product/types/product.type';

export const catalogService = {
  // Lấy danh sách sản phẩm có phân trang và lọc (Tùy biến query string)
  getProducts: async (filters: CatalogFilter): Promise<PaginatedResponse<Product>> => {
    // Chuyển mảng thành chuỗi phân cách bằng dấu phẩy (nếu có)
    const brandParam = filters.brandIds.length ? `&brandIds=${filters.brandIds.join(',')}` : '';
    const catParam = filters.categoryIds.length ? `&categoryIds=${filters.categoryIds.join(',')}` : '';
    const keywordParam = filters.keyword ? `&keyword=${encodeURIComponent(filters.keyword)}` : '';
    
    const url = `/products?page=${filters.page - 1}&size=${filters.size}${brandParam}${catParam}${keywordParam}&sort=${filters.sortBy}`;
    const res = await request.get<any, ApiResponse<PaginatedResponse<Product>>>(url);
    
    // Nếu BE chưa hỗ trợ lọc nâng cao, tạm thời lấy toàn bộ res.data rồi lọc dưới FE (Khuyến nghị nâng cấp BE sau)
    return res.data; 
  },

  getBrands: async (): Promise<Brand[]> => {
    const res = await request.get<any, ApiResponse<Brand[]>>('/brands');
    return res.data;
  },

  getCategories: async (): Promise<Category[]> => {
    const res = await request.get<any, ApiResponse<Category[]>>('/categories');
    return res.data;
  }
};