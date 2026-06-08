import request from '@/common/services/request';
import type { ApiResponse } from '@/common/types/api.type';

export const userService = {
  /**
   * API lấy thông tin profile của người dùng đang đăng nhập
   * Ép Header Authorization chứa Token vào đây để sửa lỗi file request chưa cấu hình tự động
   */
  getCurrentUser: async (): Promise<ApiResponse<any>> => {
    const token = localStorage.getItem('token') || localStorage.getItem('accessToken');
    
    return request.get<any, ApiResponse<any>>('/users/me', {
      headers: {
        Authorization: `Bearer ${token}`
      }
    });
  }
};