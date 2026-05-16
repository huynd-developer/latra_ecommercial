// frontend/src/modules/admin/feature/inventory/types/imei.type.ts

export type ImeiStatus = 'AVAILABLE' | 'SOLD' | 'DEFECTIVE' | 'WARRANTY' | 'RETURNING';

export interface Imei {
  id: number;
  imeiCode: string;
  status: ImeiStatus;
  createdAt: string;
  productName: string;
  variantName: string;
  condition: string;
  price: number;
}

export interface ImeiImportRequestDTO {
  variantId: number;
  imeis: string[];
}