// frontend/src/modules/admin/feature/inventory/services/imeiService.ts
import request from '@/common/services/request';
import type { Imei, ImeiImportRequestDTO, ImeiStatus } from '../types/imei.type';
import type { ApiResponse } from '@/common/types/api.type';

export const imeiService = {
  // 1. Lấy danh sách IMEI
  getAllImeis: async (): Promise<Imei[]> => {
    const res = await request.get<any, ApiResponse<Imei[]>>('/imeis');
    return res.data;
  },

  // 2. Import Excel/Danh sách IMEI
  importImeis: async (payload: ImeiImportRequestDTO): Promise<string> => {
    const res = await request.post<any, ApiResponse<string>>('/imeis/import', payload);
    return res.message;
  },

  // 3. Xóa IMEI
  deleteImei: async (id: number): Promise<string> => {
    const res = await request.delete<any, ApiResponse<string>>(`/imeis/${id}`);
    return res.message;
  },

  // 4. Thay đổi trạng thái IMEI
  // ĐÃ FIX: Thêm `{}` làm data body để đúng cú pháp request.put(url, data) của Axios
  changeImeiStatus: async (id: number, status: ImeiStatus): Promise<string> => {
    const res = await request.put<any, ApiResponse<string>>(
      `/imeis/${id}/status?status=${status}`, 
      {} // <--- Bắt buộc phải có body rỗng ở đây để tránh lỗi cấu trúc Axios
    );
    return res.message;
  }
};