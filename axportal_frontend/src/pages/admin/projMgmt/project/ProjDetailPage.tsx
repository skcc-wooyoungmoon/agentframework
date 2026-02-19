import { useEffect } from 'react';

import { useLocation, useParams } from 'react-router-dom';

import { UIArticle, UIPageBody, UIPageHeader } from '@/components/UI/molecules';
import { UITabs } from '@/components/UI/organisms/UITabs';
import { STORAGE_KEYS } from '@/constants/common/storage.constants';
import { useBackRestoredState } from '@/hooks/common/navigation';
import { ProjBasicDetailPage } from '@/pages/admin/projMgmt/project/ProjBasicDetailPage';
import { ProjSummaryCard } from '@/pages/admin/projMgmt/project/ProjSummaryCard';
import { ProjRoleListPage } from '@/pages/admin/projMgmt/role/ProjRoleListPage';
import { ProjUserListPage } from '@/pages/admin/projMgmt/user/ProjUserListPage';
import { useGetProjectById } from '@/services/admin/projMgmt';

// 탭 아이템 정의
const tabItems = [
  { id: 'basic', label: '기본 정보' },
  { id: 'role', label: '역할 정보' },
  { id: 'users', label: '구성원 정보' },
];

// ================================
// 컴포넌트
// ================================

/**
 * 프로젝트 관리 > 프로젝트 상세 - (탭 상태 관리)
 */
export const ProjDetailPage = () => {
  const { projectId } = useParams();
  const location = useLocation();

  // 탭 상태를 sessionStorage에 저장하여 뒤로가기 시 복원
  const { filters: tabState, updateFilters: setTabState } = useBackRestoredState<{ activeTab: string }>(
    STORAGE_KEYS.SEARCH_VALUES.PROJ_DETAIL_TAB,
    { activeTab: 'basic' }
  );

  const activeTab = tabState.activeTab;
  const setActiveTab = (newTab: string) => setTabState({ activeTab: newTab });

  // navigate state로 탭이 전달되면 해당 탭으로 설정
  useEffect(() => {
    const stateTab = (location.state as { tab?: string })?.tab;
    if (stateTab) {
      setActiveTab(stateTab);
    }
  }, [location.state]);

  // 프로젝트 기본 상세 정보 조회
  const { data: projectData, refetch } = useGetProjectById(projectId!);

  return (
    <section className='section-page'>
      <UIPageHeader title='프로젝트 조회' />

      {/* 탭 영역 */}
      <UIPageBody>
        {/* 프로젝트 요약 카드 */}
        {projectData?.project && <ProjSummaryCard projectInfo={projectData.project} />}

        <UIArticle>
          <UITabs items={tabItems} activeId={activeTab} size='large' onChange={setActiveTab} />
        </UIArticle>

        {/* 탭 내용 영역 */}

        {/* 프로젝트 기본 정보 */}
        {activeTab === 'basic' && projectData?.project && <ProjBasicDetailPage projectInfo={projectData.project} onProjectUpdated={refetch} />}

        {/* 프로젝트 역할 정보 */}
        {activeTab === 'role' && projectData?.project && <ProjRoleListPage projectInfo={projectData.project} />}

        {/* 프로젝트 구성원 정보 */}
        {activeTab === 'users' && projectData?.project && <ProjUserListPage projectInfo={projectData.project} />}
      </UIPageBody>
    </section>
  );
};
