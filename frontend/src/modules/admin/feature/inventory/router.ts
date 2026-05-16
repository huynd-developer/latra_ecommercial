// frontend/src/modules/admin/feature/inventory/router.ts
import type { RouteRecordRaw } from 'vue-router';

export const inventoryRoutes: RouteRecordRaw[] = [
  {
    path: 'imeis',
    name: "AdminImeiManagement",
    component: () => import("@/modules/admin/feature/inventory/view/ImeiView.vue"),
    meta: { 
      title: "Quản lý Kho (WMS)", 
      requiresAuth: true 
    }
  }
];