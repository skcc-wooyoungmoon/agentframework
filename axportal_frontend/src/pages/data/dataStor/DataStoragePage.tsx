import { useState } from 'react';

import { UIPageHeader } from '@/components/UI/molecules';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UITabs } from '@/components/UI/organisms';
import { useEnvCheck } from '@/hooks/common/util';
import { EvaluationDataListPage } from './EvaluationDataListPage';
import { MDPackageListPage } from './MDPackageListPage';
import { TrainingDataListPage } from './TrainingDataListPage';

export function DataStoragePage() {
  const [activeTab, setActiveTab] = useState('md-package');

  // 숨김 모드 처리 여부
  const { isProd } = useEnvCheck();

  const tabItems = !isProd
    ? [
        { id: 'md-package', label: '지식 데이터' },
        { id: 'training-data', label: '학습 데이터' },
        { id: 'evaluation-data', label: '평가 데이터' },
      ]
    : [
        { id: 'md-package', label: '지식 데이터' },
        { id: 'evaluation-data', label: '평가 데이터' },
      ];

  const renderTabContent = () => {
    switch (activeTab) {
      case 'md-package':
        return <MDPackageListPage isActiveTab={activeTab === 'md-package'} />;
      case 'training-data':
        return <TrainingDataListPage isActiveTab={activeTab === 'training-data'} />;
      case 'evaluation-data':
        return <EvaluationDataListPage isActiveTab={activeTab === 'evaluation-data'} />;
      default:
        return <MDPackageListPage isActiveTab={true} />;
    }
  };

  return (
    <section className='section-page'>
      <UIPageHeader
        title='데이터 탐색'
        description={['비정형데이터플랫폼 내의 데이터를 검색을 통해 탐색해 볼 수 있습니다.', '목록을 클릭하여 구성항목과 메타데이터를 상세하게 살펴보세요.']}
      />

      <UIPageBody>
        <UIArticle className='article-tabs mb-6'>
          <div className='flex'>
            <UITabs items={tabItems} activeId={activeTab} size='large' onChange={setActiveTab} />
          </div>
        </UIArticle>
        {renderTabContent()}
      </UIPageBody>
    </section>
  );
}
