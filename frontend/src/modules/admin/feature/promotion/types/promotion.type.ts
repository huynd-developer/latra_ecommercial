// frontend/src/modules/admin/feature/promotion/types/promotion.type.ts
export interface FlashSaleItem {
  variantId: number;
  salePrice: number;
  quantityLimit: number;
  productVariant?: any; // Dùng khi BE trả về
}

export interface FlashSale {
  id?: number | null;
  name: string;
  startTime: string;
  endTime: string;
  status?: string;
  items: FlashSaleItem[];
}

export interface Voucher {
  id?: number | null;
  code: string;
  discountType: 'PERCENTAGE' | 'FIXED_AMOUNT';
  value: number;
  minOrderValue: number;
  maxDiscountAmount: number | null;
  usageLimit: number;
  startDate: string;
  expiredAt: string;
  category?: string;
  status?: number;
}
export interface VoucherAnalytics {
  voucherCode: string;
  totalUsage: number;
  totalDiscountGiven: number;
  totalRevenue: number;
}