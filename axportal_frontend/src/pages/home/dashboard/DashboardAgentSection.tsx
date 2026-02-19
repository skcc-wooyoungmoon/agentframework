import { useMemo } from 'react';
import { useNavigate } from 'react-router-dom';

import { UIButton2, UITextLabel, UITypography } from '@/components/UI/atoms';
import { UIImage } from '@/components/UI/atoms/UIImage';
import { UIGroup } from '@/components/UI/molecules';
import { useGetAgentAppList } from '@/services/deploy/agent/agentDeploy.services.ts';
import type { GetAgentAppResponse } from '@/services/deploy/agent/types.ts';
import dateUtils from '@/utils/common/date.utils.ts';

export const DashboardAgentSection = () => {
  const navigate = useNavigate();

  const { data: agentDeployData } = useGetAgentAppList({
    page: 1,
    size: 5,
    targetType: 'all',
    sort: 'created_at,desc',
    filter: 'deployment_status:Available',
    search: '',
  });

  const agentList = useMemo(() => {
    const tempDeployList: GetAgentAppResponse[] = (agentDeployData?.content as unknown as GetAgentAppResponse[]) || [];

    return [
      ...tempDeployList.map(item => ({
        id: item.id,
        name: item.name,
        version: item.deploymentVersion,
        status: item.deploymentStatus,
        displayAgentType: '에이전트 배포',
        displayCreateDate: dateUtils.formatDate(item.createdAt, 'custom', {
          pattern: 'yyyy.MM.dd HH:mm',
          useKoreanLocale: true,
        }),
        createdAt: dateUtils.toKoreanTime(item.createdAt),
        type: 'deploy',
      })),
    ];
  }, [agentDeployData]);

  const handleAgentClick = (agent: { id: string }) => {
    navigate(`/deploy/agentDeploy/${agent.id}`);
  };

  return (
    <div className='box-group-item'>
      <div className='item-header'>
        <div className='left'>
          <UITypography variant='title-4' className='secondary-neutral-900'>
            사용 가능한 에이전트
          </UITypography>
        </div>
      </div>
      <div className='item-cont mt-4'>
        {agentList.length > 0 ? (
          <ul className='recent-data-list'>
            {agentList.map(agent => (
              <li key={agent.id} className='item'>
                <div className='item-left'>
                  <UIGroup direction='column' gap={4}>
                    {agent.version ? (
                      <UITypography variant='title-4' className='secondary-neutral-700 text-sb'>
                        <div className='flex gap-2'>
                          <UIButton2 onClick={() => handleAgentClick(agent)} className='cursor-pointer'>
                            <UITypography variant='title-4' className='secondary-neutral-700 text-sb'>
                              {agent.name}
                            </UITypography>
                          </UIButton2>
                          <UITextLabel intent='blue' className='!border-0'>
                            {`Ver.${agent.version}`}
                          </UITextLabel>
                        </div>
                      </UITypography>
                    ) : (
                      <UIButton2 onClick={() => handleAgentClick(agent)} className='cursor-pointer'>
                        <UITypography variant='title-4' className='secondary-neutral-700 text-sb'>
                          {agent.name}
                        </UITypography>
                      </UIButton2>
                    )}
                    <UIGroup direction='row' gap={4} vAlign='center' className='justify-between'>
                      <UITypography variant='body-2' className='text-gray-500'>
                        {agent.displayAgentType}
                      </UITypography>
                      <UITypography variant='body-2' className='text-gray-500'>
                        {agent.displayCreateDate}
                      </UITypography>
                    </UIGroup>
                  </UIGroup>
                </div>
              </li>
            ))}
          </ul>
        ) : (
          <div className='no-date'>
            <UIImage src='/assets/images/system/ico-system-80-default-nodata.svg' alt='No data' className='w-20 h-20' />
            <UITypography variant='body-1' className='text-gray-500'>
              조회 가능한 에이전트가 없습니다.
            </UITypography>
          </div>
        )}
      </div>
    </div>
  );
};
