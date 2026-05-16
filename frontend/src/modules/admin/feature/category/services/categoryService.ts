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
    const res = await request.post<any, ApiResponse<Category>>('/categories/admin', payload);
    return res.message;
  },

  updateCategory: async (id: number, payload: CategoryRequestDTO): Promise<string> => {
    const res = await request.put<any, ApiResponse<Category>>(`/categories/admin/${id}`, payload);
    return res.message;
  },

  deleteCategory: async (id: number): Promise<string> => {
    const res = await request.delete<any, ApiResponse<string>>(`/categories/admin/${id}`);
    return res.message;
  }
};