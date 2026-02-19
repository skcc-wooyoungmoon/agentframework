import { Button } from '@/components/common/auth';
import { ManagerInfoBox } from '@/components/common/manager';
import { UILabel, UITypography, type UILabelIntent } from '@/components/UI/atoms';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { UIUnitGroup } from '@/components/UI/molecules/UIUnitGroup';
import { AGENT_DEPLOY_STATUS } from '@/constants/deploy/agentDeploy.constants';
import { useGetAgentBuilderById } from '@/services/agent/builder/agentBuilder.services';
import { useGetAgentServing } from '@/services/deploy/agent/agentDeploy.services';
import { useLocation, useNavigate } from 'react-router-dom';

export function DeployAgentVerInfoPage() {
  const navigate = useNavigate();

  const location = useLocation();
  const { servingId, deployName, description, builderName, targetId } = location.state;

  /**
   * 에이전트 서빙 상세 데이터 조회
   */
  const { data: servingData } = useGetAgentServing({
    servingId: servingId || '',
  });

  const { data: agentBuilder } = useGetAgentBuilderById(targetId || '');

  const handleBuilderClick = () => {
    if (!agentBuilder) {
      return;
    }

    navigate(`/agent/builder/graph`, {
      state: {
        agentId: agentBuilder.id,
        isReadOnly: true,
        data: {
          id: agentBuilder.id,
          name: agentBuilder.name,
          description: agentBuilder.description,
          project_id: agentBuilder.id,
          nodes: agentBuilder.nodes || [],
          edges: agentBuilder.edges || [],
        },
      },
    });
  };

  return (
    <section className='section-page'>
      {/* 페이지 헤더 */}
      <UIPageHeader title='에이전트 배포 버전 조회' description='' />

      {/* 페이지 바디 */}
      <UIPageBody>
        {/* 테이블 */}
        <UIArticle>
          <div className='article-header'>
            <UITypography variant='title-4' className='secondary-neutral-900'>
              버전 배포 정보
            </UITypography>
          </div>
          <div className='article-body'>
            <div className='border-t border-black'>
              <table className='tbl-v'>
                <colgroup>
                  <col style={{ width: '10%' }} />
                  <col style={{ width: '40%' }} />
                  <col style={{ width: '10%' }} />
                  <col style={{ width: '40%' }} />
                </colgroup>
                <tbody>
                  <tr>
                    <th>
                      <UITypography variant='body-2' className='secondary-neutral-900'>
                        배포명
                      </UITypography>
                    </th>
                    <td>
                      <UITypography variant='body-2' className='secondary-neutral-600'>
                        {deployName || servingData?.agentServingName || ''}
                      </UITypography>
                    </td>
                    <th>
                      <UITypography variant='body-2' className='secondary-neutral-900'>
                        설명
                      </UITypography>
                    </th>
                    <td>
                      <UITypography variant='body-2' className='secondary-neutral-600'>
                        {description || servingData?.description || ''}
                      </UITypography>
                    </td>
                  </tr>
                  {builderName.length > 0 && (
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          빌더
                        </UITypography>
                      </th>
                      <td colSpan={3}>
                        <UIUnitGroup gap={16} direction='row' vAlign='center'>
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            {builderName}
                          </UITypography>
                          <Button className='btn-text-14-point ml-4' rightIcon={{ className: 'ic-system-12-arrow-right-blue', children: '' }} onClick={() => handleBuilderClick()}>
                            빌더 바로가기
                          </Button>
                        </UIUnitGroup>
                      </td>
                    </tr>
                  )}
                  <tr>
                    <th>
                      <UITypography variant='body-2' className='secondary-neutral-900'>
                        운영 배포 여부
                      </UITypography>
                    </th>
                    <td>
                      <UITypography variant='body-2' className='secondary-neutral-600'>
                        {servingData?.isMigration ? '배포' : '미배포'}
                      </UITypography>
                    </td>
                    <th>
                      <UITypography variant='body-2' className='secondary-neutral-900'>
                        버전
                      </UITypography>
                    </th>
                    <td>
                      <UITypography variant='body-2' className='secondary-neutral-600'>
                        Ver.{servingData?.appVersion}
                      </UITypography>
                    </td>
                  </tr>

                  <tr style={{ height: '68px' }}>
                    <th>
                      <UITypography variant='body-2' className='secondary-neutral-900'>
                        상태
                      </UITypography>
                    </th>
                    <td>
                      <UILabel variant='badge' intent={(AGENT_DEPLOY_STATUS[servingData?.status as keyof typeof AGENT_DEPLOY_STATUS]?.intent as UILabelIntent) || 'gray'}>
                        {AGENT_DEPLOY_STATUS[servingData?.status as keyof typeof AGENT_DEPLOY_STATUS]?.label || servingData?.status || ''}
                      </UILabel>
                    </td>
                    <th>
                      <UITypography variant='body-2' className='secondary-neutral-900'>
                        할당된 자원
                        <br />
                        (CPU/Memory)
                      </UITypography>
                    </th>
                    <td>
                      <UITypography variant='body-2' className='secondary-neutral-600'>
                        {servingData?.cpuRequest}Cores / {servingData?.memRequest}GB
                      </UITypography>
                    </td>
                  </tr>
                  <tr>
                    <th>
                      <UITypography variant='body-2' className='secondary-neutral-900'>
                        복제 인스턴스 수
                      </UITypography>
                    </th>
                    <td colSpan={3}>
                      <UITypography variant='body-2' className='secondary-neutral-600'>
                        MIN{servingData?.minReplicas}-MAX{servingData?.maxReplicas}
                      </UITypography>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </UIArticle>

        {/* 테이블 */}
        <ManagerInfoBox
          type={(servingData?.createdBy ?? '').length === 36 ? 'uuid' : 'memberId'}
          rowInfo={[{ personLabel: '생성자', dateLabel: '생성일시' }]}
          people={[{ userId: servingData?.createdBy ?? '', datetime: servingData?.createdAt ?? '' }]}
        />
      </UIPageBody>
    </section>
  );
}
