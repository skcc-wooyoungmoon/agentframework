import { ApiKeyDetailInfo, ApiKeyMonitoring } from '@/components/deploy/apikey';
import { UIArticle, UIPageBody, UIPageHeader } from '@/components/UI/molecules';
import { UITabs } from '@/components/UI/organisms';
import { useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';

/**
 * @author SGO1032948
 * @description API Key 상세
 *
 * DP_030102
 * DP_030103
 */
export const DeployApiKeyDetailPage = () => {
  const { apiKeyId } = useParams<{ apiKeyId: string }>();
  const navigate = useNavigate();

  if (apiKeyId === undefined || apiKeyId === '') {
    navigate('/not-found');
  }

  const [tab, setActiveTab] = useState('tab1');
  // 탭 아이템 정의
  const tabItems = [
    { id: 'tab1', label: '기본 정보' },
    { id: 'tab2', label: '모니터링' },
  ];

  return (
    <section className='section-page'>
      {/* 페이지 헤더 */}
      <UIPageHeader title='API Key 조회' description='' />

      {/* 페이지 바디 */}
      <UIPageBody>
        <UIArticle className='article-tabs'>
          {/* 아티클 탭 */}
          <UITabs items={tabItems} activeId={tab} size='large' onChange={setActiveTab} />
        </UIArticle>
        {tab === 'tab1' && <ApiKeyDetailInfo apiKeyId={apiKeyId || ''} />}
        {tab === 'tab2' && <ApiKeyMonitoring apiKeyId={apiKeyId || ''} />}
      </UIPageBody>
    </section>
  );
};
