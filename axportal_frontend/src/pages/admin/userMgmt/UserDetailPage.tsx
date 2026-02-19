import { UIArticle, UIPageBody, UIPageHeader } from '@/components/UI/molecules';
import { UITabs } from '@/components/UI/organisms';
import { useLocation, useParams } from 'react-router';
import { UserBasicDetailPage } from '@/pages/admin/userMgmt/UserBasicDetailPage.tsx';
import { UserProjListPage } from '@/pages/admin/userMgmt/UserProjListPage.tsx';
import { useGetUserById } from '@/services/admin/userMgmt';
import { useState } from 'react';

// 탭 아이템 정의
const tabItems = [
  { id: 'basic', label: '기본 정보' },
  { id: 'project', label: '프로젝트 정보' },
];

/**
 * 사용자 상세 페이지 (탭 상태 관리)
 * 기본 정보 탭과 프로젝트 정보 탭을 관리합니다
 */
export const UserDetailPage = () => {
  const { userId } = useParams();
  const location = useLocation();

  // location.state에서 activeTab을 받거나, 기본값 'basic' 사용
  const initialTab = (location.state as any)?.activeTab || 'basic';
  const [activeTab, setActiveTab] = useState(initialTab);

  // location.state에서 userInfo를 받거나, userId로 API 조회
  const locationUserInfo = (location.state as any)?.userInfo;
  const { data: queriedUserInfo } = useGetUserById({ userId: userId! });

  // API 데이터 우선, locationUserInfo는 초기 로딩 시 fallback으로 사용
  const userInfo = queriedUserInfo || locationUserInfo;

  return (
    <section className='section-page'>
      <UIPageHeader title='사용자 관리' />

      <UIPageBody>
        <UIArticle>
          <UITabs items={tabItems} activeId={activeTab} size='large' onChange={setActiveTab} />
        </UIArticle>

        {/* 탭 콘텐츠 */}
        {activeTab === 'basic' && userInfo && <UserBasicDetailPage userInfo={userInfo} />}

        {activeTab === 'project' && userInfo && <UserProjListPage userInfo={userInfo} />}
      </UIPageBody>
    </section>
  );
};
