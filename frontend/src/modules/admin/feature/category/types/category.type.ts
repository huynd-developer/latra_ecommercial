// frontend/src/modules/admin/feature/category/types/category.type.ts
export interface Category {
  id: number;
  name: string;
  parentId: number | null;
  status: number;
}

export interface CategoryRequestDTO {
  name: string;
  parentId: number | null;
  status?: number; 
}