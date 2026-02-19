import type { MenuItemResponse } from '@/services/common/menu.service';

export type UILnbMenuItemType = MenuItemResponse;

export interface UILnbProps {
  menuItems: UILnbMenuItemType[];
  recentMenuItems?: UILnbMenuItemType[];
  active: {
    menu: UILnbMenuItemType | null;
    subMenu?: UILnbMenuItemType | null;
  };
  setActive: {
    menu: (item: UILnbMenuItemType) => void;
    subMenu: (item: UILnbMenuItemType) => void;
  };
  className?: string;
  is2DepthOpen?: boolean;
  onToggle2Depth?: () => void;
  onMenuClick?: () => void;
  onSubMenuClick?: () => void;
  onRecentMenuClick?: () => void;
  setRecentMenuItem?: (item: UILnbMenuItemType) => void;
}
