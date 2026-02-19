import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { UITabs } from '../../../components/UI/organisms';

import { GuardRailListPage } from './GuardRailListPage.tsx';
import { GuardRailPromptPage } from './GuardRailPromptPage';
import { useBackRestoredState } from '@/hooks/common/navigation/useBackRestoredState.ts';
import { STORAGE_KEYS } from '@/constants/common/storage.constants.ts';

// ================================
// 상수 정의
// ================================

const GUARD_RAIL_PROMPT_TAB = 'guardrail-prompt';
const GUARD_RAIL_TAB = 'guardrail';

const tabOptions = [
  { id: GUARD_RAIL_PROMPT_TAB, label: '가드레일 프롬프트 관리' },
  { id: GUARD_RAIL_TAB, label: '가드레일 관리' },
];

export const GuardRailMainPage = () => {
  // const navigate = useNavigate();
  // const { pathname, search } = useLocation();

  const { filters: activeTab, updateFilters: setActiveTab } = useBackRestoredState<{ id: string }>(STORAGE_KEYS.SEARCH_VALUES.GUARDRAIL_MAIN_TAB, {
    id: GUARD_RAIL_PROMPT_TAB,
  });

  // URL의 쿼리 파라미터('search')가 변경될 때마다 탭 상태를 업데이트
  // useEffect(() => {
  //   const searchParams = new URLSearchParams(search);
  //   const tab = searchParams.get('tab') || GUARD_RAIL_PROMPT_TAB;
  //   setActiveTab({ id: tab });
  // }, [search]);

  // const handleTabChange = (tabId: string) => {
  //   navigate(`${pathname}?tab=${tabId}`);
  // };

  return (
    <>
      {/* 섹션 페이지 */}
      <section className='section-page'>
        <UIPageHeader title='가드레일' description='서비스 응답의 품질과 안전성을 보장하기 위해 가드레일을 설정하고 관리합니다.' />

        {/* 페이지 바디 */}
        <UIPageBody>
          {/* 탭 영역 */}
          <UIArticle className='article-tabs'>
            <div className='flex'>
              <UITabs items={tabOptions} activeId={activeTab.id} onChange={tabId => setActiveTab({ id: tabId })} size='large' />
            </div>
          </UIArticle>

          {/* 탭 콘텐츠 */}

          {/* 가드레일 프롬프트 관리  탭  */}
          {activeTab.id === GUARD_RAIL_PROMPT_TAB && <GuardRailPromptPage />}

          {/* 가드레일 관리 탭  */}
          {activeTab.id === GUARD_RAIL_TAB && <GuardRailListPage />}
        </UIPageBody>
      </section>
    </>
  );
};
