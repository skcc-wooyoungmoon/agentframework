import type { ReactNode } from 'react';

import { useNavigate } from 'react-router';

import { UIArticle, UIPageBody, UIPageHeader } from '@/components/UI/molecules';
import { UITabs } from '@/components/UI/organisms/UITabs';

type TabType = 'basic' | 'project';

interface UserDetailLayoutProps {
  userId: string;
  activeTab: TabType;
  children: ReactNode;
  footer?: ReactNode;
  title?: string;
}

// 탭 아이템 정의
const tabItems = [
  { id: 'basic', label: '기본 정보' },
  { id: 'project', label: '프로젝트 정보' },
];

/**
 * 사용자 상세 페이지 공통 레이아웃
 * 탭 UI와 네비게이션 로직을 캡슐화하여 재사용 가능하도록 함
 */
export const UserDetailLayout = ({ userId, activeTab, children, footer, title = '사용자 조회' }: UserDetailLayoutProps) => {
  const navigate = useNavigate();

  // 탭 변경 처리
  const handleTabChange = (tabId: string) => {
    if (tabId === 'basic' || tabId === 'project') {
      navigate(`/admin/user-mgmt/${userId}?tab=${tabId}`, { replace: true });
    }
  };

  return (
    <section className='section-page'>
      <UIPageHeader title={title} />

      {/* 탭 영역 */}
      <UIPageBody>
        <UIArticle>
          <UITabs items={tabItems} activeId={activeTab} size='large' onChange={handleTabChange} />
        </UIArticle>

        {/* 탭 내용 영역 */}
        {children}
      </UIPageBody>

      {/* 하단 버튼 영역 */}
      {footer && footer}
    </section>
  );
};
