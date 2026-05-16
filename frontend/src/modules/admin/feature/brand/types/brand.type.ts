export interface Brand {
  id: number;
  name: string;
  country: string;
  logo: string | null;
  status: number;
  createdAt?: string;
  updatedAt?: string;
}

export interface BrandRequestDTO {
  name: string;
  country: string;
  status: number;
}

export interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
}