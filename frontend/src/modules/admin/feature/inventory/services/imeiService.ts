// frontend/src/modules/admin/feature/inventory/services/imeiService.ts
import request from '@/common/services/request';
import type { Imei, ImeiImportRequestDTO, ImeiStatus } from '../types/imei.type';
import type { ApiResponse } from '@/common/types/api.type';

export const imeiService = {
  getAllImeis: async (): Promise<Imei[]> => {
    const res = await request.get<any, ApiResponse<Imei[]>>('/imeis/admin');
    return res.data;
  },

  importImeis: async (payload: ImeiImportRequestDTO): Promise<string> => {
    const res = await request.post<any, ApiResponse<string>>('/imeis/admin/import', payload);
    return res.message;
  },

  deleteImei: async (id: number): Promise<string> => {
    const res = await request.delete<any, ApiResponse<string>>(`/imeis/admin/${id}`);
    return res.message;
  },

  changeStatus: async (id: number, status: ImeiStatus): Promise<string> => {
    const res = await request.put<any, ApiResponse<string>>(`/imeis/admin/${id}/status?status=${status}`);
    return res.message;
  }
};