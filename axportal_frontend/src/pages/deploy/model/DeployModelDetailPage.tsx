import { useMemo, useState } from 'react';

import { useNavigate, useParams } from 'react-router';

import { Button } from '@/components/common/auth';
import { DeployModelInformation, DeployModelLog, DeployModelMonitoring } from '@/components/model/deploy';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { UITabs } from '@/components/UI/organisms/UITabs';
import { useGetModelDeployDetail } from '@/services/deploy/model/modelDeploy.services';

export const DeployModelDetailPage = () => {
  const { servingId } = useParams();
  const navigate = useNavigate();

  // 모델 배포 상세 조회
  const { data: modelDeployDetail, refetch } = useGetModelDeployDetail(servingId ?? '');

  // TAB 설정
  const tabOptions = useMemo(() => {
    if (modelDeployDetail?.servingType === 'self_hosting') {
      return [
        { id: 'tab1', label: '기본 정보' },
        { id: 'tab2', label: '시스템로그' },
        { id: 'tab3', label: '모니터링' },
      ];
    } else {
      return [
        { id: 'tab1', label: '기본 정보' },
        { id: 'tab3', label: '모니터링' },
      ];
    }
  }, [modelDeployDetail]);
  const [activeTab, setActiveTab] = useState(tabOptions[0].id);

  return (
    servingId &&
    modelDeployDetail && (
      <section className='section-page'>
        {/* 페이지 헤더 */}
        <UIPageHeader
          title='모델 배포 조회'
          description=''
          actions={
            <>
              <Button
                className='btn-tertiary-outline line-only-blue'
                onClick={() => {
                  navigate(`/model/modelCtlg/${modelDeployDetail?.modelId}`);
                }}
              >
                모델 상세
              </Button>
            </>
          }
        />
        {/* 페이지 바디 */}
        <UIPageBody>
          <UIArticle className='article-tabs'>
            {/* 아티클 탭 */}
            <UITabs items={tabOptions} activeId={activeTab} size='large' onChange={setActiveTab} />
          </UIArticle>
          {activeTab === 'tab1' && <DeployModelInformation data={modelDeployDetail} servingId={servingId} refetch={refetch} />}
          {activeTab === 'tab2' && <DeployModelLog servingId={servingId} />}
          {activeTab === 'tab3' && <DeployModelMonitoring servingId={servingId} name={modelDeployDetail.name} isVllm={modelDeployDetail.runtime === 'vllm'} />}
        </UIPageBody>
      </section>
    )
  );
};
