// frontend/src/modules/admin/feature/brand/services/brandService.ts
import axios from 'axios';
import type { Brand, BrandRequestDTO, ApiResponse } from '../types/brand.type';

const API_URL = 'http://localhost:8080/api/v1/brands';

export const brandService = {
  getAllBrands: async () => {
    const response = await axios.get<ApiResponse<Brand[]>>(API_URL);
    return response.data.data;
  },

  createBrand: async (payload: BrandRequestDTO) => {
    // 🛠️ ĐÃ SỬA: Xóa /admin, gọi thẳng gốc API_URL
    const response = await axios.post<ApiResponse<Brand>>(API_URL, payload);
    return response.data.data;
  },

  updateBrand: async (id: number, payload: BrandRequestDTO) => {
    // 🛠️ ĐÃ SỬA: Xóa /admin
    const response = await axios.put<ApiResponse<Brand>>(`${API_URL}/${id}`, payload);
    return response.data.data;
  },

  deleteBrand: async (id: number) => {
    // 🛠️ ĐÃ SỬA: Xóa /admin
    const response = await axios.delete<ApiResponse<void>>(`${API_URL}/${id}`);
    return response.data;
  },

  uploadLogo: async (id: number, file: File) => {
    const formData = new FormData();
    formData.append('file', file);
    // 🛠️ ĐÃ SỬA: Xóa /admin
    const response = await axios.post<ApiResponse<string>>(`${API_URL}/${id}/logo`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    });
    return response.data.data;
  }
};