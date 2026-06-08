// frontend/src/modules/admin/feature/category/services/categoryService.ts
import request from '@/common/services/request';
import type { Category, CategoryRequestDTO } from '../types/category.type';
import type { ApiResponse } from '@/common/types/api.type';

export const categoryService = {
  getAllCategories: async (): Promise<Category[]> => {
    const res = await request.get<any, ApiResponse<Category[]>>('/categories');
    return res.data;
  },

  createCategory: async (payload: CategoryRequestDTO): Promise<string> => {
    // 🛠️ ĐÃ SỬA: Bỏ /admin, khớp với @PostMapping ở Backend
    const res = await request.post<any, ApiResponse<Category>>('/categories', payload);
    return res.message;
  },

  updateCategory: async (id: number, payload: CategoryRequestDTO): Promise<string> => {
    // 🛠️ ĐÃ SỬA: Bỏ /admin, khớp với @PutMapping("/{id}") ở Backend
    const res = await request.put<any, ApiResponse<Category>>(`/categories/${id}`, payload);
    return res.message;
  },

  deleteCategory: async (id: number): Promise<string> => {
    // 🛠️ ĐÃ SỬA: Bỏ /admin, khớp với @DeleteMapping("/{id}") ở Backend
    const res = await request.delete<any, ApiResponse<string>>(`/categories/${id}`);
    return res.message;
  }
};