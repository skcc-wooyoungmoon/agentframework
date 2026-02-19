import { Button } from '@/components/common/auth';
import { DeployAgentInfomation, DeployAgentLog, DeployAgentMonitoring } from '@/components/deploy/agent';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { UITabs } from '@/components/UI/organisms';
import { AUTH_KEY } from '@/constants/auth/auth.constants';
import { env } from '@/constants/common/env.constants';
import { useLayerPopup } from '@/hooks/common/layer';
import { useGetAgentBuilderById } from '@/services/agent/builder/agentBuilder.services';
import { useGetAgentAppById } from '@/services/deploy/agent/agentDeploy.services';
import { useState } from 'react';
import { useLocation, useParams } from 'react-router-dom';
import { DeployAgentChatTestPopupPage } from './';

export function DeployAgentDetailPage() {
  const { appId } = useParams<{ appId: string }>();
  const location = useLocation();
  const targetType = location.state?.targetType || 'agent_graph'; // external_graph  : custom app 배포, agent_graph : agent 빌더 app 배포
  const [activeTab, setActiveTab] = useState('agentTab1');
  const layerPopupChatTest = useLayerPopup(); // 채팅 테스트 팝업용
  const [dropdownOptions, setDropdownOptions] = useState<Array<{ value: string; label: string }>>([]);
  const [endpoint, setEndpoint] = useState<string>('');
  const [authorization, setAuthorization] = useState<string>('');

  // 에이전트 앱 데이터 조회
  const { data: agentAppData, refetch: refetchAgentAppData } = useGetAgentAppById({ appId: appId || '' });

  const { data: agentBuilder } = useGetAgentBuilderById(agentAppData?.targetId || '', {
    enabled: Boolean(agentAppData?.targetId),
  });
  // 탭 옵션 정의
  const tabOptions = [
    { id: 'agentTab1', label: '기본 정보' },
    { id: 'agentTab2', label: '시스템로그' },
    { id: 'agentTab3', label: '모니터링' },
  ];

  return (
    <>
      {/* 섹션 페이지 */}
      <section className='section-page'>
        {/* 페이지 헤더 */}
        <UIPageHeader
          title='에이전트 배포 조회'
          description=''
          actions={
            <>
              {activeTab === 'agentTab1' ? (
                <Button
                  auth={AUTH_KEY.DEPLOY.AGENT_DEPLOY_UPDATE}
                  className='btn-tertiary-outline line-only-blue'
                  onClick={() => {
                    layerPopupChatTest.onOpen();
                  }}
                >
                  채팅 테스트
                </Button>
              ) : (
                <></>
              )}
            </>
          }
        />

        {/* 페이지 바디 */}
        <UIPageBody>
          <UIArticle className='article-tabs'>
            {/* 아티클 탭 */}
            <UITabs items={tabOptions} activeId={activeTab} size='large' onChange={setActiveTab} />
          </UIArticle>
          {activeTab === 'agentTab1' && agentAppData && (
            <DeployAgentInfomation
              data={agentAppData}
              agentBuilder={agentBuilder}
              appId={appId || ''}
              refetch={refetchAgentAppData}
              onDropdownOptionsChange={(options: Array<{ value: string; label: string }>) => {
                setDropdownOptions(options);
                // endpoint도 함께 설정 (agentAppData에서 가져온 id 사용)
                if (options.length > 0) {
                  setEndpoint(`${env.VITE_GATEWAY_URL}/agent/${options[0].value}`);
                }
              }}
              onAuthorizationChange={(auth: string) => {
                setAuthorization(auth);
              }}
            />
          )}
          {activeTab === 'agentTab2' && appId && <DeployAgentLog appId={appId} />}
          {activeTab === 'agentTab3' && appId && <DeployAgentMonitoring appId={appId} targetType={targetType} agentBuilder={agentBuilder} />}
        </UIPageBody>
      </section>
      <DeployAgentChatTestPopupPage
        isOpen={layerPopupChatTest.currentStep > 0}
        onClose={layerPopupChatTest.onClose}
        targetType={targetType}
        endPoint={endpoint}
        dropdownOptions={dropdownOptions}
        authorization={authorization}
      />
    </>
  );
}
