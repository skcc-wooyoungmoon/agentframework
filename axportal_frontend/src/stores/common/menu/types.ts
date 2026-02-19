import type { UILnbMenuItemType } from '@/components/UI/organisms/navigation/UILnb';

export interface MenuState {
  menuList: UILnbMenuItemType[];
  isLoading: boolean;
  isLoaded: boolean;
}
