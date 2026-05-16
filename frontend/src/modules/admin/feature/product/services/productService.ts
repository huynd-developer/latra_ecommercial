// frontend/src/modules/admin/feature/product/services/productService.ts
import request from '@/common/services/request';
import type { ProductRequestDTO } from '../types/product.type';

export const productService = {
  getProducts: async () => {
    const response = await request.get<any, any>('/products');
    
    // Đã fix: Trỏ thẳng vào response.data.products theo đúng cấu trúc JSON trả về
    if (response.data && response.data.products) {
      return response.data.products;
    }
    // Dự phòng trường hợp cấu trúc khác
    return response.products || [];
  },

  createProduct: async (payload: ProductRequestDTO) => {
    return await request.post<any, any>('/products/admin', payload);
  },

  updateProduct: async (id: number, payload: ProductRequestDTO) => {
    return await request.put<any, any>(`/products/admin/${id}`, payload);
  },

  deleteProduct: async (id: number) => {
    await request.delete<any, any>(`/products/admin/${id}`);
  },

  uploadImage: async (id: number, file: File) => {
    const formData = new FormData();
    formData.append('file', file);
    return await request.post<any, any>(`/products/admin/${id}/images`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    });
  },

  getBrands: async () => {
    const response = await request.get<any, any>('/brands');
    return response.data || response;
  },
  
  getCategories: async () => {
    const response = await request.get<any, any>('/categories');
    return response.data || response;
  }
};