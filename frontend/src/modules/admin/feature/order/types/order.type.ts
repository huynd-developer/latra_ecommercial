// frontend/src/modules/admin/feature/order/types/order.type.ts

export type OrderStatus = 'PENDING' | 'PAID' | 'SHIPPED' | 'COMPLETED' | 'CANCELLED' | 'REFUNDED';
export type PaymentMethod = 'COD' | 'VNPAY' | 'TRANSFER' | 'CASH';
export type PaymentStatus = 'PENDING' | 'SUCCESS' | 'FAILED' | 'REFUNDED';
export type ShipmentStatus = 'PREPARING' | 'PICKING' | 'DELIVERING' | 'DELIVERED' | 'RETURNED' | 'CANCELLED';
export type ShippingCarrier = 'GHN' | 'GHTK' | 'VIETTEL_POST' | 'IN_HOUSE';

export interface OrderItem {
  id: number;
  productName: string;
  variantName: string; // color + storage
  imeiCode: string;
  price: number;
}

export interface Shipment {
  id: number;
  carrier: ShippingCarrier | null;
  trackingCode: string | null;
  status: ShipmentStatus;
  recipientName: string;
  recipientPhone: string;
  fullAddress: string;
}

export interface PaymentTransaction {
  id: number;
  provider: PaymentMethod;
  gatewayTransactionNo: string;
  amount: number;
  status: PaymentStatus;
  payDate: string | null;
}

export interface Order {
  id: number;
  customerName: string;
  customerPhone: string;
  employeeName: string | null;
  paymentMethod: PaymentMethod;
  totalAmount: number;
  status: OrderStatus;
  createdAt: string;
  items: OrderItem[];
  shipment: Shipment | null;
  paymentTransaction: PaymentTransaction | null;
}

export interface UpdateShipmentRequestDTO {
  carrier: string;
  trackingCode: string;
  status: ShipmentStatus;
}