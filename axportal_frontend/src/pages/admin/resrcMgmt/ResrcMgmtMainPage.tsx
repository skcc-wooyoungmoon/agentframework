import { useAtom } from 'jotai';

import { UITabs } from '../../../components/UI/organisms';

import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIArticle } from '@/components/UI/molecules/UIArticle';

import { ResrcMgmtPortalPage } from './ResrcMgmtPortalPage.tsx';
import { ResrcMgmtGpuNodePage } from './ResrcMgmtGpuNodePage.tsx';
import { ResrcMgmtSolutionPage } from './ResrcMgmtSolutionPage.tsx';
import { resrcMgmtActiveTabAtom } from '@/stores/admin/resrcMgmt';

export const ResrcMgmtMainPage = () => {
  const [activeTab, setActiveTab] = useAtom(resrcMgmtActiveTabAtom);

  const tabOptions = [
    { id: 'Tab1', label: '포탈 자원 현황' },
    { id: 'Tab2', label: 'GPU 노드별 자원 현황' },
    { id: 'Tab3', label: '솔루션 자원 현황' },
  ];

  return (
    <div>
      <section className='section-page'>
      <UIPageHeader title='자원 관리' description='포탈, GPU 노드, 솔루션 네임스페이스별 자원 할당량과 사용률을 한눈에 확인할 수 있습니다.' />

        <UIPageBody>
          <UIArticle className='article-tabs'>
            <div className='flex'>
              <UITabs items={tabOptions} activeId={activeTab} onChange={setActiveTab} size='large' />
            </div>
          </UIArticle>

          {activeTab === 'Tab1' && <ResrcMgmtPortalPage />}
          {activeTab === 'Tab2' && <ResrcMgmtGpuNodePage />}
          {activeTab === 'Tab3' && <ResrcMgmtSolutionPage />}
        </UIPageBody>
      </section>
    </div>
  );
};