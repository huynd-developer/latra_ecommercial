export interface Variant {
  id?: number | null;
  variantId?: number; 
  color: string;
  storage: string;
  condition: string; // <-- THÊM DÒNG NÀY (VD: "New", "99%")
  price: number;
  stock: number;
  sku: string;
}

export interface Product {
  id: number;
  name: string;
  brandId: number;
  categoryId: number;
  description: string;
  status: number;
  mainImageUrl?: string;
  variants: Variant[];
}

export interface ProductRequestDTO {
  name: string;
  brandId: number;
  categoryId: number;
  description: string;
  status: number;
  variants: Variant[];
}

export interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
}