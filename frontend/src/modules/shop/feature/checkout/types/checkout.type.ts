export interface CheckoutFormState {
  customerName: string;
  customerPhone: string;
  customerEmail: string;
  province: string;
  district: string;
  ward: string;
  street: string;
  paymentMethod: 'COD' | 'VNPAY';
  voucherCode: string | null;
  note: string;
  isRequestVat: boolean;
  taxId: string;
  companyName: string;
  companyEmail: string;
  companyAddress: string;
}

export interface CheckoutRequest {
  userId: number; // BẮT BUỘC PHẢI CÓ ĐỂ FIX LỖI "ID MUST NOT BE NULL"
  paymentMethod: 'COD' | 'VNPAY';
  shippingAddress: string;
  voucherCode: string | null;
  note: string;
}

export interface CheckoutResponse {
  orderId: number;
  paymentUrl?: string;
  status: string;
}

export interface VoucherCheckResponse {
  voucherCode: string;
  discountAmount: number;
}