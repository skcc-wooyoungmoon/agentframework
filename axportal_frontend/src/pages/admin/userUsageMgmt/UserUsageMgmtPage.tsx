import { useState } from 'react';

import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { UITabs } from '@/components/UI/organisms';

import { UserUsageMgmtHistPage } from './UserUsageMgmtHistPage';
import { UserUsageMgmtStatsPage } from './UserUsageMgmtStatsPage';

export const UserUsageMgmtListPage = () => {
  const [activeTab, setActiveTab] = useState('usage-history');

  const tabOptions = [
    { id: 'usage-history', label: '사용 이력' },
    { id: 'usage-statistics', label: '사용 통계' },
  ];

  return (
    <div>
      <section className='section-page'>
        <UIPageHeader title='사용자 이용 현황' description='포탈 전체 사용자의 사용 이력 및 통계를 확인하고 관리할 수 있습니다.' />

        <UIPageBody>
          <UIArticle className='article-tabs'>
            <div className='flex'>
              <UITabs items={tabOptions} activeId={activeTab} onChange={setActiveTab} size='large' />
            </div>
          </UIArticle>

          {activeTab === 'usage-history' && <UserUsageMgmtHistPage />}
          {activeTab === 'usage-statistics' && <UserUsageMgmtStatsPage />}
        </UIPageBody>
      </section>
    </div>
  );
};
