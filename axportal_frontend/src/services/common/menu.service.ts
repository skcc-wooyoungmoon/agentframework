import type { ApiQueryOptions } from '@/hooks/common/api';
import { useApiQuery } from '@/hooks/common/api';

export interface MenuItemResponse {
  id: string;
  label: string;
  icon: string;
  path: string;
  auth: string;
  href?: string;
  children?: MenuItemResponse[];
}

export interface MenuListResponse {
  success: boolean;
  message: string;
  data: MenuItemResponse[];
  timestamp: string;
  path: string;
}

export const useGetMenuList = (options?: ApiQueryOptions<MenuItemResponse[]>) => {
  return useApiQuery<MenuItemResponse[]>({
    url: `/common/menus`,
    ...options,
    disableCache: true,
  });
};

export const useGetMenuCheck = (options?: ApiQueryOptions<string>) => {
  return useApiQuery<string, void>({
    url: `/common/menu-check`,
    ...options,
    disableCache: true,
  });
};
