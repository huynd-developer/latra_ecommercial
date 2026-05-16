export interface CatalogFilter {
  page: number;
  size: number;
  keyword: string;
  brandIds: number[];
  categoryIds: number[];
  sortBy: string; // VD: 'price-asc', 'price-desc', 'newest'
}

export interface PaginatedResponse<T> {
  products: T[];
  currentPage: number;
  totalItems: number;
  totalPages: number;
}

export interface Brand {
  id: number;
  name: string;
}

export interface Category {
  id: number;
  name: string;
  parentId: number | null;
}