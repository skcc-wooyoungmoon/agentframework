import { useState } from 'react';

import { UIMainLayout } from '@/components/UI/templates/layout/UIMainLayout/component';

interface DesignLayoutProps {
  /** 페이지 콘텐츠 */
  children: React.ReactNode;
  /** 초기 활성 메뉴 */
  initialMenu?: { id: string; label: string };
  /** 초기 활성 서브메뉴 */
  initialSubMenu?: { id: string; label: string; icon: string };
  /** 콘텐츠 배경색 */
  contentsBgColor?: string;
  /** 가로 스크롤 활성화 여부 */
  enableHorizontalScroll?: boolean;
}

export const DesignLayout: React.FC<DesignLayoutProps> = ({
  children,
  initialMenu = { id: 'home', label: '홈' },
  initialSubMenu = {
    id: 'model-garden',
    label: '모델 탐색', // [251111_퍼블수정] 타이틀명칭 변경 : 모델가든 > 모델 탐색
    icon: 'ico-lnb-menu-20-home-modelgarden',
  },
  contentsBgColor,
  enableHorizontalScroll = false,
}) => {
  const [activeMenu, setActiveMenu] = useState<any>(initialMenu);
  const [activeSubMenu, setActiveSubMenu] = useState<any>(initialSubMenu);
  const [isUserPopupOpen, setIsUserPopupOpen] = useState(false);

  const headerProps = {
    navigateActions: {
      left: {
        available: true,
        onClick: () => {},
      },
      right: { available: true, onClick: () => {} },
    },
    locations: [],
    title: '',
    rightContent: (
      <div className='flex items-center'>
        <button onClick={() => setIsUserPopupOpen(!isUserPopupOpen)} className='p-0 border-0 bg-transparent cursor-pointer'>
          <div
            className='flex items-center justify-center text-white font-medium rounded-full'
            style={{
              width: '38px',
              height: '38px',
              backgroundColor: '#0046FF',
              fontSize: '14px',
              fontWeight: 500,
              lineHeight: '19.6px',
            }}
          >
            신한
          </div>
        </button>
      </div>
    ),

    userPopupProps: {
      isOpen: isUserPopupOpen,
      userName: '김신한 프로',
      userTeam: 'Data 기획 Unit',
      userInitial: '신한',
      onClose: () => setIsUserPopupOpen(false),
      onLogout: () => {
        setIsUserPopupOpen(false);
      },
    },
  };

  const menuItems = [
    {
      id: 'home',
      label: '홈',
      path: '/',
      icon: 'ico-lnb-menu-32-default-home',
      auth: 'ALL',
      children: [{ id: 'dashboard', label: '대시보드', icon: 'ico-lnb-menu-20-home-dashboard', path: '', auth: 'ALL' }],
    },
    {
      id: 'data',
      label: '데이터',
      path: '/data',
      icon: 'ico-lnb-menu-32-default-data-2',
      auth: 'ALL',
      children: [
        {
          id: 'data-storage',
          label: '데이터 탐색', // [251111_퍼블수정] 타이틀명칭 변경 : 데이터 저장소 > 데이터 탐색
          icon: 'ico-lnb-menu-20-data-storage',
          path: '',
          auth: 'ALL',
        },
        {
          id: 'data-catalog',
          label: '데이터 카탈로그',
          icon: 'ico-lnb-menu-20-data-catalog',
          path: '',
          auth: 'ALL',
        },
        {
          id: 'data-tools',
          label: '데이터도구',
          icon: 'ico-lnb-menu-20-tool',
          path: '',
          auth: 'ALL',
        },
      ],
    },
    {
      id: 'model',
      label: '모델',
      path: '/model',
      icon: 'ico-lnb-menu-32-default-model',
      auth: 'ALL',
      children: [
        {
          id: 'model-garden',
          label: '모델 탐색', // [251111_퍼블수정] 타이틀명칭 변경 : 모델가든 > 모델 탐색
          icon: 'ico-lnb-menu-20-home-modelgarden',
          path: '',
          auth: 'ALL',
        },
        {
          id: 'model-catalog',
          label: '모델 관리', // [251111_퍼블수정] 타이틀명칭 변경 : 모델 카탈로그 > 모델 관리
          icon: 'ico-lnb-menu-20-model-catalog',
          path: '',
          auth: 'ALL',
        },
        {
          id: 'fine-tuning',
          label: '파인튜닝',
          icon: 'ico-lnb-menu-20-finetuning',
          path: '',
          auth: 'ALL',
        },
        {
          id: 'playground',
          label: '플레이그라운드',
          icon: 'ico-lnb-menu-20-playground',
          path: '',
          auth: 'ALL',
        },
      ],
    },
    {
      id: 'prompt',
      label: '프롬프트',
      path: '/prompt',
      icon: 'ico-lnb-menu-32-default-prompt',
      auth: 'ALL',
      children: [
        {
          id: 'infer-prompt',
          label: '추론 프롬프트',
          icon: 'ico-lnb-menu-20-prompt-inference',
          path: '',
          auth: 'ALL',
        },
        {
          id: 'few-shot',
          label: '퓨샷',
          icon: 'ico-lnb-menu-20-prompt-fewshot',
          path: '',
          auth: 'ALL',
        },
        {
          id: 'guardrail',
          label: '가드레일',
          icon: 'ico-lnb-menu-20-guardrail',
          path: '',
          auth: 'ALL',
        },
        {
          id: 'workflow',
          label: '워크플로우',
          icon: 'ico-lnb-menu-20-workflow',
          path: '',
          auth: 'ALL',
        },
      ],
    },
    {
      id: 'agent',
      label: '에이전트',
      path: '/agent',
      icon: 'ico-lnb-menu-32-default-agent-2',
      auth: 'ALL',
      children: [
        { id: 'builder', label: '빌더', icon: 'ico-lnb-menu-20-agent-builder', path: '', auth: 'ALL' },
        { id: 'tools', label: '도구', icon: 'ico-lnb-menu-20-tool', path: '', auth: 'ALL' },
        { id: 'mcp', label: 'MCP 서버', icon: 'ic-lnb-menu-20-agent-list', path: '', auth: 'ALL' },
        { id: 'master', label: '명장 AI', icon: 'ic-lnb-menu-20-master', path: '', href: 'https://example.com', auth: 'ALL' },
        // [251110_퍼블수정] : 명장 AI 메뉴추가 / 링크 추가
      ],
    },
    {
      id: 'eval',
      label: '평가',
      path: '/eval',
      icon: 'ico-lnb-menu-32-default-evaluation',
      auth: 'ALL',
      children: [
        {
          id: 'agent-eval',
          label: '평가',
          icon: 'ico-lnb-menu-20-assessment',
          path: '',
          auth: 'ALL',
        },
      ],
    },
    {
      id: 'deploy',
      label: '배포',
      path: '/deploy',
      icon: 'ico-lnb-menu-32-default-develop',
      auth: 'ALL',
      children: [
        {
          id: 'model-deploy',
          label: '모델 배포',
          icon: 'ico-lnb-menu-20-model-deployment',
          path: '',
          auth: 'ALL',
        },
        {
          id: 'agent-deploy',
          label: '에이전트 배포',
          icon: 'ico-lnb-menu-20-agent-deployment',
          path: '',
          auth: 'ALL',
        },
        {
          id: 'safety-filter',
          label: '세이프티 필터',
          icon: 'ico-lnb-menu-20-manage-filter',
          path: '',
          auth: 'ALL',
        },
        {
          id: 'api-key',
          label: 'API KEY',
          icon: 'ico-lnb-menu-20-agent-apikey',
          path: '',
          auth: 'ALL',
        },
        {
          id: 'operate',
          label: '운영 배포',
          icon: 'ic-lnb-menu-20-operate',
          path: '',
          auth: 'ALL',
        },
      ],
    },
    {
      id: 'log',
      label: '로그',
      path: '/log',
      icon: 'ico-lnb-menu-32-default-log',
      auth: 'ALL',
      children: [
        {
          id: 'model-deploy-log',
          label: '모델사용 로그', // [251111_퍼블수정] 타이틀명칭 변경 : 모델배포 로그 > 모델사용 로그
          icon: 'ico-lnb-menu-20-model-deployment',
          path: '',
          auth: 'ALL',
        },
        {
          id: 'agent-deploy-log',
          label: '에이전트사용 로그', // [251111_퍼블수정] 타이틀명칭 변경 : 에이전트배포 로그 > 에이전트사용 로그
          icon: 'ico-lnb-menu-20-agent-deployment',
          path: '',
          auth: 'ALL',
        },
      ],
    },
    {
      id: 'notice',
      label: '공지사항',
      path: '/notice',
      icon: 'ico-lnb-menu-32-default-notice',
      auth: 'ALL',
      children: [
        {
          id: 'notice-menu',
          label: '공지사항',
          icon: 'ico-lnb-menu-20-notice',
          path: '',
          auth: 'ALL',
        },
      ],
    },
    {
      id: 'admin',
      label: '관리',
      path: '/admin',
      icon: 'ico-lnb-menu-32-default-management',
      auth: 'ALL',
      children: [
        {
          id: 'user-mgmt',
          label: '사용자 관리',
          icon: 'ico-lnb-menu-20-manage-user',
          path: '',
          auth: 'ALL',
        },
        {
          id: 'project-mgmt',
          label: '프로젝트 관리',
          icon: 'ico-lnb-menu-20-manage-group',
          path: '',
          auth: 'ALL',
        },
        {
          id: 'resource-mgmt',
          label: '자원 관리',
          icon: 'ico-lnb-menu-20-manage-resource',
          path: '',
          auth: 'ALL',
        },
        {
          id: 'usage-mgmt',
          label: '사용자 이용 현황',
          icon: 'ico-lnb-menu-20-manage-history',
          path: '',
          auth: 'ALL',
        },
        {
          id: 'notice-mgmt',
          label: '공지사항 관리',
          icon: 'ico-lnb-menu-20-notice',
          path: '',
          auth: 'ALL',
        },
        {
          id: 'api-key-mgmt',
          label: 'API Key 관리',
          icon: 'ico-lnb-menu-20-agent-apikey',
          path: '',
          auth: 'ALL',
        },
        {
          id: 'ide-mgmt',
          label: 'IDE 관리',
          icon: 'ico-lnb-menu-20-ide',
          path: '',
          auth: 'ALL',
        },
      ],
    },
  ];

  const lnbProps = {
    menuItems,
    active: {
      menu: activeMenu,
      subMenu: activeSubMenu,
    },
    setActive: {
      menu: (menu: any) => {
        setActiveMenu(menu);
        const selectedMenu = menuItems.find(item => item.id === menu.id);
        if (selectedMenu && selectedMenu.children && selectedMenu.children.length > 0) {
          setActiveSubMenu(selectedMenu.children[0]);
        } else {
          setActiveSubMenu(null);
        }
      },
      subMenu: (subMenu: any) => setActiveSubMenu(subMenu),
    },
    // onMenuClick: () => {},
    // onSubMenuClick: () => {},
  };

  const contentsProps = {
    padding: true,
    backgroundColor: contentsBgColor,
    enableHorizontalScroll: enableHorizontalScroll,
  };

  return (
    <UIMainLayout type='design' lnbProps={lnbProps} contentsProps={contentsProps} headerProps={headerProps}>
      {children}
    </UIMainLayout>
  );
};
