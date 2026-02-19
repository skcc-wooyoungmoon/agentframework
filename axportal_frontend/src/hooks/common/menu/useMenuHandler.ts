import { useCallback, useEffect, useMemo, useState } from 'react';

import { useLocation, useNavigate } from 'react-router';

import type { UILnbMenuItemType } from '@/components/UI/organisms/navigation/UILnb';
import { STORAGE_KEYS } from '@/constants/common/storage.constants';
import { useAuthMode } from '@/hooks/common/auth';
import { useGetMenuCheck, useGetMenuList } from '@/services/common/menu.service';
import { useUser } from '@/stores';
import { useMenu } from '@/stores/common/menu';
import { useModal } from '@/stores/common/modal';

// 빈 메뉴 아이템 (로딩 중 사용)
const EMPTY_MENU_ITEM: UILnbMenuItemType = {
  id: '',
  label: '',
  icon: '',
  path: '',
  auth: '',
  children: [],
};

export const useMenuHandler = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { openAlert } = useModal();
  const { user } = useUser();
  const { menuList: storedMenuList, isLoaded, setMenuList, setLoading } = useMenu();
  const { isVisibleMode } = useAuthMode();
  const { refetch } = useGetMenuCheck({
    enabled: false,
  });

  // 메뉴 API 조회
  const { data: apiMenuList, isLoading: isApiLoading } = useGetMenuList({
    enabled: !isLoaded, // 이미 로드된 경우 재조회 X
  });

  // API 응답을 스토어에 저장
  useEffect(() => {
    if (apiMenuList && apiMenuList.length > 0) {
      setMenuList(apiMenuList);
    }
  }, [apiMenuList]);

  // 로딩 상태 동기화
  useEffect(() => {
    setLoading(isApiLoading);
  }, [isApiLoading, setLoading]);

  // 메뉴 리스트 (API 응답 or config)
  const baseMenuList = useMemo(() => {
    // return dontLoadMenuList ? menuConfigs : storedMenuList;
    return storedMenuList;
  }, [storedMenuList]);

  // 현재 경로
  const currentPaths = useMemo(() => {
    return location.pathname.split('/').filter(Boolean);
  }, [location.pathname]);

  // 세션스토리지에 최근 메뉴 저장하기
  const recentStorageHandler = useMemo(() => {
    return {
      set: (menuItems: UILnbMenuItemType[]) => {
        sessionStorage.setItem(STORAGE_KEYS.RECENT_MENU_ITEMS, JSON.stringify(menuItems));
      },
      get: (): UILnbMenuItemType[] => {
        const stored = sessionStorage.getItem(STORAGE_KEYS.RECENT_MENU_ITEMS);
        return stored ? JSON.parse(stored) : [];
      },
      reset: () => {
        sessionStorage.removeItem(STORAGE_KEYS.RECENT_MENU_ITEMS);
      },
    };
  }, []);

  // 최근 메뉴 상태 관리
  const [recentList, setRecentList] = useState<UILnbMenuItemType[]>([]);

  // 메뉴 로드 후 최근 메뉴 초기화
  useEffect(() => {
    if (!isLoaded || baseMenuList.length === 0) return;

    const storedItems = recentStorageHandler.get();
    if (storedItems.length === 0) {
      // 최신 이용 메뉴 default : 홈/대시보드
      const defaultMenuItem = baseMenuList[0]?.children?.[0];
      if (defaultMenuItem) {
        const initialRecentItems = [defaultMenuItem];
        recentStorageHandler.set(initialRecentItems);
        setRecentList(initialRecentItems);
      }
    } else {
      setRecentList(storedItems);
    }
  }, [isLoaded, baseMenuList, recentStorageHandler]);

  // 최근 이용 메뉴 초기화
  const resetRecentList = useCallback(() => {
    recentStorageHandler.reset();
    setRecentList([]);
  }, [recentStorageHandler]);

  // 로그아웃 시 최근 메뉴 초기화 감지
  useEffect(() => {
    const handleStorageChange = (e: StorageEvent) => {
      // 다른 탭에서 ACCESS_TOKEN이 삭제되면 로그아웃으로 간주하고 최근 메뉴 초기화
      if (e.key === STORAGE_KEYS.ACCESS_TOKEN && e.newValue === null) {
        resetRecentList();
      }
    };

    const handleLogout = () => {
      resetRecentList();
    };

    // 다른 탭에서의 변경 감지
    window.addEventListener('storage', handleStorageChange);
    // 같은 탭에서의 로그아웃 감지 (clearTokens에서 발생시키는 이벤트)
    window.addEventListener('lnb-logout', handleLogout);

    return () => {
      window.removeEventListener('storage', handleStorageChange);
      window.removeEventListener('lnb-logout', handleLogout);
    };
  }, [resetRecentList]);

  // 활성 메뉴 관리
  const [active, setActive] = useState<{
    menu: UILnbMenuItemType;
    subMenu?: UILnbMenuItemType;
  }>({
    menu: EMPTY_MENU_ITEM,
    subMenu: undefined,
  });

  // 메뉴 로드 후 활성 메뉴 초기화
  useEffect(() => {
    if (!isLoaded || baseMenuList.length === 0) return;

    const [d1Path, d2Path] = currentPaths;
    const menu = baseMenuList.find(item => item.path === d1Path) ?? baseMenuList[0];
    const subMenu = menu?.children?.find(item => item.path === d2Path) ?? menu?.children?.[0];

    if (menu) {
      setActive({
        menu,
        subMenu,
      });
    }
  }, [isLoaded, baseMenuList, currentPaths]);

  // 현재 경로에 맞춘 이동
  useEffect(() => {
    if (!isLoaded || baseMenuList.length === 0) return;

    const [d1Path, d2Path] = currentPaths;
    const menu = baseMenuList.find(item => item.path === d1Path);
    const subMenu = menu?.children?.find(item => item.path === d2Path) || menu?.children?.[0];

    if (menu && subMenu && (active.menu?.id !== menu?.id || active.subMenu?.id !== subMenu?.id)) {
      setActive({
        menu,
        subMenu,
      });
    }
  }, [currentPaths, baseMenuList, isLoaded]);

  // 서브메뉴바 열림/닫힘 상태 관리
  const [isSubMenuBarOpen, setIsSubMenuBarOpen] = useState<boolean>(true);

  const filterRoutes = useCallback(
    (routes: UILnbMenuItemType[]): UILnbMenuItemType[] => {
      // 숨김 모드 처리 여부
      if (isVisibleMode) return routes;

      const checkAuth = (item: UILnbMenuItemType) => item.auth == 'ALL' || user.menuAuthList.indexOf(item.auth) > -1;
      return routes.filter(checkAuth).map(item => ({
        ...item,
        children: item.children?.filter(checkAuth),
      }));
    },
    [user.menuAuthList]
  );

  // 1depth 메뉴
  const menuList = filterRoutes(baseMenuList);

  // 현재 활성 메뉴의 경로를 가져오는 함수
  const getHeadPath = useCallback(
    (menuItem?: UILnbMenuItemType) => {
      return (menuItem || active.menu)?.path || '';
    },
    [active.menu]
  );

  // 최근 메뉴 관리 로직
  const updateRecentList = useCallback(
    (newItem: UILnbMenuItemType) => {
      // 현재 최신 리스트
      const currentItems = recentStorageHandler.get();

      // 중복 클릭일 경우 신규 X
      if (currentItems.find(item => item.id === newItem.id)) {
        return;
      }

      // 최대 5개 제한 + 신규순
      const newRecentItems = [newItem, ...currentItems].slice(0, 5);
      recentStorageHandler.set(newRecentItems); // 스토리지 저장
      setRecentList(newRecentItems); // 최신 리스트 관리
    },
    [recentStorageHandler]
  );

  // 공통 메뉴 클릭 핸들러
  const handleCommonMenuClick = useCallback(
    async (item: UILnbMenuItemType, parentItem?: UILnbMenuItemType) => {
      // 메뉴 클릭
      const parent = parentItem || active.menu;

      // 링크 메뉴일 경우 새창 열기
      if (item.href) {
        window.open(item.href, '_blank');
        return;
      }

      // 권한 체크
      const isAuthorized = item.auth == 'ALL' || user.menuAuthList.indexOf(item?.auth || '') > -1;
      if (!isAuthorized) {
        openAlert({
          message: item.label + ' 진입 권한이 없습니다.',
          title: '알림',
          confirmText: '확인',
        });

        return;
      }

      // CSS
      setActive({
        menu: parent,
        subMenu: item,
      });

      // 이동 - 빈 path일 때는 슬래시를 추가하지 않음
      const targetPath = item.path ? `/${getHeadPath(parent)}/${item.path}` : `/${getHeadPath(parent)}`;
      // 이전과 같은 url 로 진입한 경우 - 컴포넌트만 다시 마운트
      if (location.pathname === targetPath) {
        return;
      }

      // 이동
      await navigate(targetPath);
      // 최근 메뉴 업데이트 @deprecated
      updateRecentList(item);
    },
    [active.menu, user.menuAuthList, getHeadPath, location.pathname, navigate, updateRecentList, openAlert]
  );

  // 1depth 클릭 핸들러
  const handleMenuClick = useCallback((item: UILnbMenuItemType) => {
    // 1depth 메뉴 클릭시 활성화만
    setActive({
      menu: item,
    });
  }, []);

  // 2depth 메뉴 상태 설정 (네비게이션 포함)
  const handleSubMenuClick = useCallback(
    async (item: UILnbMenuItemType) => {
      await handleCommonMenuClick(item).then(() => {
        // 이동 완료 후 menu-check 호출
        refetch();
      });
    },
    [handleCommonMenuClick, refetch]
  );

  // 최근 메뉴 클릭 핸들러
  const handleRecentMenuClick = useCallback(
    async (recentItem: UILnbMenuItemType) => {
      // 해당 메뉴 아이템을 찾아서 활성화하고 네비게이션
      for (const menuItem of menuList) {
        const childItem = menuItem.children?.find(child => child.id === recentItem.id);
        if (childItem) {
          // 올바른 1depth 메뉴와 2depth 메뉴로 직접 이동
          setActive({
            menu: menuItem,
            subMenu: childItem,
          });

          await handleCommonMenuClick(childItem, menuItem).then(() => {
            // 이동 완료 후 menu-check 호출
            refetch();
          });
          break;
        }
      }
    },
    [menuList, handleCommonMenuClick]
  );

  return {
    menuList, // 1depth 메뉴 리스트
    active,
    isMenuLoading: isApiLoading || !isLoaded, // 메뉴 로딩 상태
    isMenuLoaded: isLoaded, // 메뉴 로드 완료 상태
    // 메뉴 핸들러들
    setActive: {
      menu: handleMenuClick, // 1depth
      subMenu: handleSubMenuClick, // 2depth
    },
    // 최근 메뉴 관련
    recentList,
    setRecentMenu: handleRecentMenuClick,
    resetRecentList,

    // 메뉴바 토글 관련
    isSubMenuBarOpen,
    setIsSubMenuBarOpen,
  };
};
