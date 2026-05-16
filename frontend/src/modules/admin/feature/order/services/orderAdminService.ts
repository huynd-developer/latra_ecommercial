// frontend/src/modules/admin/feature/order/services/orderAdminService.ts
import request from '@/common/services/request';
import type { Order, UpdateShipmentRequestDTO } from '../types/order.type';
import type { ApiResponse } from '@/common/types/api.type';

export const orderAdminService = {
  // Quản lý Đơn
  getOrders: async (): Promise<Order[]> => {
    const res = await request.get<any, ApiResponse<any>>('/orders/admin');
    
    // 🔥 SỬA 3: Trích xuất mảng từ thuộc tính .content của Spring Boot Page Object
    return res.data.content || res.data || [];
  },
  
  getOrderDetail: async (id: number): Promise<Order> => {
    const res = await request.get<any, ApiResponse<Order>>(`/orders/admin/${id}`);
    return res.data;
  },

  // Quản lý Vận chuyển (Shipment)
  updateShipment: async (shipmentId: number, payload: UpdateShipmentRequestDTO): Promise<string> => {
    const res = await request.put<any, ApiResponse<any>>(`/shipments/admin/${shipmentId}`, payload);
    return res.message;
  },
  
  updateShippingAddress: async (orderId: number, name: string, phone: string, address: string): Promise<string> => {
    const payload = { recipientName: name, recipientPhone: phone, fullAddress: address };
    const res = await request.put<any, ApiResponse<any>>(`/shipments/admin/order/${orderId}/address`, payload);
    return res.message;
  },

  // Quản lý Hoàn tiền (Payment)
  refundOrder: async (orderId: number): Promise<string> => {
    const res = await request.post<any, ApiResponse<any>>(`/payments/admin/refund/${orderId}`);
    return res.message;
  }
};