import { useEffect, useMemo, useState } from 'react';

import { useLocation, useNavigate } from 'react-router';

import { UIMainLayout } from '@/components/UI/templates/layout/UIMainLayout';
import { env, RUN_MODE_TYPES } from '@/constants/common/env.constants';
import { useMenuHandler } from '@/hooks/common/menu';
import { AlarmInfo, IdeStep1ToolPickPopupPage, ProjCreWizard, ProjJoinWizard, ProjQuitStep } from '@/pages/home';
import { AuthProvider } from '@/providers/common';
import { AppHistoryProvider, useAppHistory } from '@/providers/common/HistoryProvider';
import { routeConfig } from '@/routes/route.config';
import { authServices } from '@/services/auth/auth.non.services.ts';
import { usePostExchangeGroup, usePostLogout } from '@/services/auth/auth.services';
import { useGetProjectList } from '@/services/home/proj/projBaseInfo.service';
import { useUser } from '@/stores/auth';
import { useMenu } from '@/stores/common/menu';
import { useModal } from '@/stores/common/modal';
import { stringUtils } from '@/utils/common';
import { generateBreadcrumb } from '@/utils/common/breadcrumb.utils';

function MainLayoutContent() {
  const { openModal, openConfirm } = useModal();
  const { isLoading: isMenuLoading } = useMenu();
  const navigate = useNavigate();
  const location = useLocation();

  /////////////// Header 처리
  const { canGoBack, canGoForward, goBack, goForward, resetHistory } = useAppHistory();
  useEffect(() => {
    resetHistory();

    // 페이지 새로고침 시 서버로부터 user 정보 갱신
    const refreshUserInfo = async () => {
      const user = await authServices.getMe();
      if (user) {
        updateUser(user);
      }
    };

    refreshUserInfo();
  }, []);

  const handleLogoClick = () => {
    navigate('/');
  };

  // 사용자
  const { user, updateUser } = useUser();
  const formattedUserName = useMemo(() => {
    return stringUtils.getProfileIconString(user?.userInfo?.jkwNm ?? '');
  }, [user]);

  // projcet 처리
  const { data: projectList, isSuccess } = useGetProjectList();
  const preProjectList = useMemo(() => {
    if (!isSuccess) return [];
    return user.projectList.map(item => ({
      id: item.prjSeq,
      name: item.prjNm,
      selected: item.active === true,
      count: projectList?.find(proj => proj.prjSeq === item.prjSeq)?.memberCnt ?? 0,
    }));
  }, [user.projectList, projectList, isSuccess]);

  const [headerPopupOpen, setHeaderPopupOpen] = useState<'NONE' | 'USER' | 'CREATE_PROJECT' | 'JOIN_PROJECT' | 'QUIT_PROJECT' | 'ALARM'>('NONE');
  const handleHeaderPopupClose = () => {
    setHeaderPopupOpen('NONE');
  };

  // IDE 이동 팝업 오픈 이벤트 핸들러
  const handleOpenIDEPopup = () => {
    openModal({
      title: 'IDE 선택',
      type: 'medium',
      body: <IdeStep1ToolPickPopupPage />,
      useCustomFooter: true,
      showFooter: true,
    });
  };

  /////////////// User Popup 처리
  const handleProfileClick = () => {
    if (headerPopupOpen === 'NONE') setHeaderPopupOpen('USER');
    else setHeaderPopupOpen('NONE');
  };
  const { mutate: postLogout } = usePostLogout();
  const { mutateAsync: exchangeGroup } = usePostExchangeGroup();
  const handleLogout = async () => {
    const confirmed = await openConfirm({
      bodyType: 'text',
      title: '안내',
      message: '로그아웃 하시겠어요?',
      cancelText: '아니요',
      confirmText: '예',
    });

    if (confirmed) {
      postLogout(
        {},
        {
          onSuccess: () => {
            navigate('/');
          },
        }
      );
    }
  };

  /////////////// Menu 처리
  const { menuList, active, recentList, setActive, setRecentMenu } = useMenuHandler();

  /////////////// Breadcrumb 처리
  const breadcrumb = useMemo(() => {
    return generateBreadcrumb(location.pathname, routeConfig);
  }, [location.pathname]);

  // 관리 메뉴 진입 여부 확인
  const isManagementPage = useMemo(() => {
    // TODO 하드코딩..
    return location.pathname.startsWith('/admin');
  }, [location.pathname]);

  return (
    !isMenuLoading && (
      <>
        <UIMainLayout
          headerProps={{
            title: env.VITE_RUN_MODE !== RUN_MODE_TYPES.PROD ? env.VITE_RUN_MODE : '',
            onLogoClick: handleLogoClick,
            onProfileClick: handleProfileClick,
            name: formattedUserName,
            navigateActions: {
              left: {
                available: canGoBack,
                onClick: () => {
                  goBack();
                },
              },
              right: {
                available: canGoForward,
                onClick: () => {
                  goForward();
                },
              },
            },
            locations: breadcrumb,
            projectBoxProps: {
              projectList: preProjectList,
              onCreateProject: () => {
                setHeaderPopupOpen('CREATE_PROJECT');
              },
              onJoinProject: () => {
                setHeaderPopupOpen('JOIN_PROJECT');
              },
              onQuitProject: () => {
                setHeaderPopupOpen('QUIT_PROJECT');
              },
              onProjectSelect: async (_, id) => {
                try {
                  if (!id || user.activeProject.prjSeq === id) return;
                  await exchangeGroup({ prjSeq: id });

                  navigate('/home', { replace: true });
                  resetHistory();

                  const newUser = await authServices.getMe();
                  if (newUser) {
                    updateUser(newUser);
                  }
                } catch {
                  // console.error('Failed to select project from dropdown:', err);
                }
              },
              disabled: isManagementPage,
            },
            onAlarmClick: () => {
              setHeaderPopupOpen('ALARM');
            },
            unreadAlarmCount: user?.unreadAlarmCount ?? 0,
            userPopupProps: {
              isOpen: headerPopupOpen === 'USER',
              onClose: handleHeaderPopupClose,
              onLogout: handleLogout,
              userName: user?.userInfo?.jkwNm,
              userTeam: user?.userInfo?.deptNm,
              userInitial: formattedUserName,
            },
            onOpenIDEPopup: handleOpenIDEPopup,
          }}
          lnbProps={{
            active,
            menuItems: menuList,
            recentMenuItems: recentList,
            setActive,
            setRecentMenuItem: setRecentMenu,
          }}
        />
        {headerPopupOpen === 'CREATE_PROJECT' && <ProjCreWizard onClose={handleHeaderPopupClose} />}
        {headerPopupOpen === 'JOIN_PROJECT' && <ProjJoinWizard onClose={handleHeaderPopupClose} />}
        {headerPopupOpen === 'QUIT_PROJECT' && <ProjQuitStep onClose={handleHeaderPopupClose} />}
        {headerPopupOpen === 'ALARM' && <AlarmInfo onClose={handleHeaderPopupClose} />}
      </>
    )
  );
}

export function MainLayout() {
  return (
    <AppHistoryProvider>
      <AuthProvider>
        <MainLayoutContent />
      </AuthProvider>
    </AppHistoryProvider>
  );
}
