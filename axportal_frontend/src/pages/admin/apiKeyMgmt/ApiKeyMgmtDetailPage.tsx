import { useState } from 'react';

import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIPageHeader, UIPageBody } from '@/components/UI/molecules';
import { UITabs } from '../../../components/UI/organisms';
import { ApiKeyMgmtBasicPage } from './ApiKeyMgmtBasicPage';
import { ApiKeyMgmtMoniterPage } from './ApiKeyMgmtMoniterPage';

export const ApiKeyMgmtDetailPage = () => {
  const [activeTab, setActiveTab] = useState('Tab1');

  const tabOptions = [
    { id: 'Tab1', label: '기본 정보' },
    { id: 'Tab2', label: '모니터링' },
  ];

  return (
    <div>
      <section className='section-page'>
        <UIPageHeader title='API Key 조회' description='' />

        <UIPageBody>
          <UIArticle className='article-tabs'>
            <div className='flex'>
              <UITabs items={tabOptions} activeId={activeTab} onChange={setActiveTab} size='large' />
            </div>
          </UIArticle>

          {activeTab === 'Tab1' && <ApiKeyMgmtBasicPage />}
          {activeTab === 'Tab2' && <ApiKeyMgmtMoniterPage />}
        </UIPageBody>
      </section>
    </div>
  );
};

