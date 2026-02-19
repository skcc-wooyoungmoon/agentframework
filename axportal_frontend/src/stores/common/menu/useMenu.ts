import { useCallback } from 'react';

import { atom, useAtom } from 'jotai';

import type { UILnbMenuItemType } from '@/components/UI/organisms/navigation/UILnb';
import type { MenuItemResponse } from '@/services/common/menu.service';

import type { MenuState } from './types';

/**
 * 메뉴 초기값 정의
 */
const INITIAL_MENU: MenuState = {
  menuList: [],
  isLoading: false,
  isLoaded: false,
};

/**
 * 메뉴 정보를 관리하는 전역 상태
 */
const menuAtom = atom<MenuState>(INITIAL_MENU);

/**
 * API 응답을 UILnbMenuItemType으로 변환하는 함수
 */
const transformMenuItem = (item: MenuItemResponse): UILnbMenuItemType => ({
  id: item.id,
  label: item.label,
  icon: item.icon,
  path: item.path,
  href: item.href,
  auth: item.auth,
  children: item.children?.map(transformMenuItem),
});

/**
 * 메뉴 정보를 관리하는 커스텀 훅
 */
export const useMenu = () => {
  const [menuState, setMenuState] = useAtom(menuAtom);

  /**
   * 메뉴 리스트 설정
   */
  const setMenuList = useCallback(
    (menuList: MenuItemResponse[]) => {
      const transformedMenuList = menuList.map(transformMenuItem);
      setMenuState(prev => ({
        ...prev,
        menuList: transformedMenuList,
        isLoaded: true,
        isLoading: false,
      }));
    },
    [setMenuState]
  );

  /**
   * 로딩 상태 설정
   */
  const setLoading = useCallback(
    (isLoading: boolean) => {
      setMenuState(prev => ({ ...prev, isLoading }));
    },
    [setMenuState]
  );

  /**
   * 메뉴 초기화
   */
  const clearMenu = useCallback(() => {
    setMenuState(INITIAL_MENU);
  }, [setMenuState]);

  return {
    menuList: menuState.menuList,
    isLoading: menuState.isLoading,
    isLoaded: menuState.isLoaded,
    setMenuList,
    setLoading,
    clearMenu,
  };
};
