import { useMemo } from 'react';

import { useNavigate } from 'react-router-dom';

import { ManagerInfoBox } from '@/components/common';
import { ApiKeyList } from '@/components/common/apikey/ApiKeyList';
import { ProjectInfoBox } from '@/components/common/project/ProjectInfoBox/component';
import { UICode, UIDataCnt, UIPagination, UITextLabel, UITypography } from '@/components/UI/atoms';
import { UIArticle, UIUnitGroup } from '@/components/UI/molecules';
import { UICircleChart } from '@/components/UI/molecules/chart';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { env } from '@/constants/common/env.constants';
import { useLayerPopup } from '@/hooks/common/layer';
import { useCommonPopup } from '@/hooks/common/popup/useCommonPopup';
import { DeployModelEdit } from '@/pages/deploy/model/DeployModelEdit.tsx';
import { useCheckApiEndpoint, usePostRetryApiEndpoint } from '@/services/deploy/apigw/apigw.services';
import type { GetCheckApiEndpointResponse } from '@/services/deploy/apigw/types';
import { useDeleteModelDeployBulk, useGetModelDeployResourceInfo } from '@/services/deploy/model/modelDeploy.services';
import type { GetModelDeployResponse } from '@/services/deploy/model/types';
import { useGetSafetyFilterList } from '@/services/deploy/safetyFilter/safetyFilter.service';
import type { SafetyFilter } from '@/services/deploy/safetyFilter/types';
import { useGetFineTuningTrainingById } from '@/services/model/fineTuning/modelFineTuning.service.ts';
import { useModal } from '@/stores/common/modal';

import { SelfHostingDeployInfoTable } from './SelfHostingDeployInfoTable';
import { SelfHostingModelInfoTable } from './SelfHostingModelInfoTable';
import { ServerlessDeployInfoTable } from './ServerlessDeployInfoTable';
import { ServerlessModelInfoTable } from './ServerlessModelInfoTable';

import { Button } from '@/components/common/auth';
import { AUTH_KEY } from '@/constants/auth';
import type { ColDef } from 'ag-grid-community';

export type DeployModelSafetyFilterList = {
  inputFilters?: SafetyFilter[];
  outputFilters?: SafetyFilter[];
};

export type DeployApiGwStatus = {
  apigwStatus: GetCheckApiEndpointResponse;
};
/**
 *
 * @author SGO1032948
 * @description 모델 정보 페이지
 *
 * DP_010102
 * @returns
 */
export function DeployModelInformation({ data, servingId, refetch }: { data: GetModelDeployResponse; servingId: string; refetch: () => void }) {
  const navigate = useNavigate();
  const layerPopupProps = useLayerPopup();
  const { openAlert } = useModal();
  const { showDeleteConfirm, showDeleteComplete } = useCommonPopup();

  const endpoint = `${env.VITE_GATEWAY_URL}/model/${servingId}`;

  // self-hosting 여부
  const isSelfHosting = useMemo(() => {
    return data.servingType === 'self_hosting'; //|| data.servingType === 'self-hosting' ;
  }, [data.servingType]);

  //api 등록 상태
  const { data: apiEndpointStatus } = useCheckApiEndpoint('model', servingId);

  // 모델 배포 자원 현황 조회
  const { data: resourceInfo } = useGetModelDeployResourceInfo(servingId, {
    enabled: isSelfHosting && !!servingId,
  });

  // 파인튜닝 상세 조회 (학습 데이터용)
  const { data: fineTuningDetail } = useGetFineTuningTrainingById({ id: data.fineTuningId || '', isDataSet: true }, { enabled: !!data.fineTuningId && isSelfHosting });

  // 학습 데이터 rowData
  const trainingDataRowData = useMemo(() => {
    if (!fineTuningDetail || !fineTuningDetail?.datasetDetails || fineTuningDetail?.datasetDetails?.length === 0) {
      return [];
    }
    return fineTuningDetail?.datasetDetails;
  }, [fineTuningDetail]);

  // 학습 데이터 그리드 컬럼 정의
  const trainingDataColumnDefs: ColDef[] = useMemo(
    () => [
      {
        headerName: 'NO',
        field: 'no' as any,
        width: 56,
        minWidth: 56,
        maxWidth: 56,
        cellClass: 'text-center',
        headerClass: 'text-center',
        cellStyle: {
          textAlign: 'center',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
        } as any,
        sortable: false,
        suppressHeaderMenuButton: true,
        suppressSizeToFit: true,
        valueGetter: (params: any) => params.node.rowIndex + 1,
      },
      {
        headerName: '이름',
        field: 'name',
        width: 272,
      },
      {
        headerName: '설명',
        field: 'description',
        minWidth: 230,
        flex: 1,
        cellRenderer: (params: any) => {
          return (
            <div
              style={{
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                whiteSpace: 'nowrap',
                width: '100%',
              }}
              title={params.value}
            >
              {params.value}
            </div>
          );
        },
      },
      {
        headerName: '유형',
        field: 'type',
        width: 120,
        valueGetter: (params: any) => {
          const type = params?.data?.type;
          return type === 'supervised_finetuning' ? '지도학습' : type === 'unsupervised_finetuning' ? '비지도학습' : type || '';
        },
      },
      {
        headerName: '태그',
        field: 'tags' as const,
        width: 230,
        cellStyle: { paddingLeft: '16px' },
        cellRenderer: (params: any) => {
          const tags = params.value; // 태그 배열

          if (!Array.isArray(tags) || tags.length === 0) {
            return null;
          }

          const tagText = tags.map((tag: any) => tag.name).join(', ');
          const tagTextArray = tags.map((tag: any) => tag.name);
          return (
            <div title={tagText}>
              <div className='flex gap-1'>
                {tagTextArray.slice(0, 2).map((tag: string, index: number) => (
                  <UITextLabel key={index} intent='tag' className='nowrap'>
                    {tag}
                  </UITextLabel>
                ))}
              </div>
            </div>
          );
        },
      },
    ],
    []
  );

  // 모델 배포 내 세이프티 필터 관련 조회
  const { data: safetyFilterInputList } = useGetSafetyFilterList(
    {
      size: data?.safetyFilterInputGroups?.length ?? 0,
      filter: `group_id:${data?.safetyFilterInputGroups?.join('|')}`,
    },
    {
      enabled: !!data?.safetyFilterInputGroups && data?.safetyFilterInputGroups?.length > 0,
    }
  );
  const { data: safetyFilterOutputList } = useGetSafetyFilterList(
    {
      size: data?.safetyFilterOutputGroups?.length ?? 0,
      filter: `group_id:${data?.safetyFilterOutputGroups?.join('|')}`,
    },
    {
      enabled: !!data?.safetyFilterOutputGroups && data?.safetyFilterOutputGroups?.length > 0,
    }
  );
  // 삭제
  const { mutate: deleteModelCtlgBulk } = useDeleteModelDeployBulk();
  const handleDeleteModelDeploy = async () => {
    if (data.production) {
      openAlert({
        title: '안내',
        message: '운영계에 배포된 모델은 삭제할 수 없습니다.',
      });
      return;
    }
    showDeleteConfirm({
      onConfirm: () => {
        deleteModelCtlgBulk(
          [
            {
              servingId: servingId,
              servingType: data.servingType,
            },
          ],
          {
            onSuccess: () => {
              showDeleteComplete({
                itemName: '모델 배포',
                onConfirm: () => {
                  navigate('/deploy/modelDeploy', { replace: true });
                },
              });
            },
          }
        );
      },
    });
  };

  const handleEditPageClose = () => {
    layerPopupProps.onClose();
    refetch();
  };

  const handleEditButtonClick = () => {
    if (data.status !== 'Stopped') {
      openAlert({
        title: '안내',
        message: '중지 상태에서만 수정할 수 있습니다.',
      });
      return;
    }
    layerPopupProps.onOpen();
  };

  // 재시도
  const { mutate: retryApiEndpoint } = usePostRetryApiEndpoint(servingId);
  const handleRetry = () => {
    retryApiEndpoint(servingId, {
      onSuccess: () => {
        refetch();
      },
    });
  };

  // 모델 타입에 따른 예시 코드
  const exampleCode = useMemo(() => {
    // const isEmbeddingOrReranker = data.type === 'embedding' || data.type === 'reranker';

    if (data.type === 'embedding') {
      return `import ssl
import httpx
import truststore

ssl_context = truststore.SSLContext(ssl.PROTOCOL_TLS_CLIENT)
httpx_client = httpx.Client(verify=ssl_context)
## 사내 인증서를 이용하는 httpx_client 생성 ##

from openai import OpenAI

client = OpenAI(
    base_url="${endpoint}",
    api_key="<api-key>",
    http_client=httpx_client,  ## 생성한 httpx_client 사용
)

embedding_response = client.embeddings.create(model="${data.name}", input=["Embedding Example1", "Embedding Example2"])
`;
    }

    return `import ssl
import httpx
import truststore

ssl_context = truststore.SSLContext(ssl.PROTOCOL_TLS_CLIENT)
httpx_client = httpx.Client(verify=ssl_context)
## 사내 인증서를 이용하는 httpx_client 생성 ##

from openai import OpenAI

### Create Client ###
client = OpenAI(
    base_url = "${endpoint}",
    api_key = "<api-key>",
    http_client=httpx_client,  ## 생성한 httpx_client 사용
)

# Chat example (OpenAI)
chat_completions = client.chat.completions.create(
    model="${data.name}",
    messages=[
        {
            "role": "user",
            "content": "Write a one-sentence bedtime story about a unicorn."
        }
    ]
)

# Completions example (OpenAI)
completions = client.completions.create(
    model="${data.name}",
    prompt="Say this is a test",
    max_tokens=7,
    temperature=0
)
`;
  }, [data.type, data.name, endpoint]);

  return (
    <>
      {/* 테이블 */}
      <UIArticle>
        <div className='article-header'>
          <UITypography variant='title-4' className='secondary-neutral-900'>
            모델 정보
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
              {isSelfHosting ? <SelfHostingModelInfoTable data={data} /> : <ServerlessModelInfoTable data={data} />}
            </table>
          </div>
        </div>
      </UIArticle>

      {/* 배포 정보 테이블 */}
      <UIArticle>
        <div className='article-header'>
          <UIUnitGroup direction='row' align='space-between' gap={0}>
            <UITypography variant='title-4' className='secondary-neutral-900'>
              배포 정보
            </UITypography>
            {apiEndpointStatus?.status === 'FAILED' && (
              <Button className='btn-option-outlined' onClick={handleRetry}>
                재시도
              </Button>
            )}
          </UIUnitGroup>
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
              {isSelfHosting ? (
                <SelfHostingDeployInfoTable
                  data={{ ...data, replicas: resourceInfo?.replicas || '' }}
                  endpoint={endpoint}
                  inputFilters={safetyFilterInputList?.content}
                  outputFilters={safetyFilterOutputList?.content}
                  apigwStatus={apiEndpointStatus ?? { status: 'PROCESSING', message: '', infWorkSeq: '' }}
                />
              ) : (
                <ServerlessDeployInfoTable
                  data={data}
                  endpoint={endpoint}
                  inputFilters={safetyFilterInputList?.content}
                  outputFilters={safetyFilterOutputList?.content}
                  apigwStatus={apiEndpointStatus ?? { status: 'PROCESSING', message: '', infWorkSeq: '' }}
                />
              )}
            </table>
          </div>
        </div>
      </UIArticle>

      {isSelfHosting && (
        <UIArticle>
          <div className='article-header'>
            <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb'>
              자원 할당량 및 사용률
            </UITypography>
          </div>
          {/*  리소스 정보 + 리소스 차트 */}
          <div className='flex items-center gap-[80px]'>
            <div className='flex-1 flex justify-center'>
              <div className='flex justify-between items-center'>
                <div className='chart-item flex-1'>
                  <div className='flex chart-graph  h-[218px] gap-x-10 justify-self-center'>
                    <div className='w-[280px] flex items-center justify-center'>
                      <UICircleChart.Half type='CPU' value={parseFloat((resourceInfo?.cpuUsage || 0).toFixed(2))} total={parseFloat((resourceInfo?.cpuRequest || 0).toFixed(2))} />
                    </div>
                    <div className='w-[280px] flex items-center justify-center'>
                      <UICircleChart.Half
                        type='Memory'
                        value={parseFloat((resourceInfo?.memoryUsage || 0).toFixed(2))}
                        total={parseFloat((resourceInfo?.memoryRequest || 0).toFixed(2))}
                      />
                    </div>
                    <div className='w-[280px] flex items-center justify-center'>
                      <UICircleChart.Half type='GPU' value={parseFloat((resourceInfo?.gpuUsage || 0).toFixed(2))} total={parseFloat((resourceInfo?.gpuRequest || 0).toFixed(2))} />
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </UIArticle>
      )}

      {/* 학습데이터 섹션 */}
      {isSelfHosting && fineTuningDetail && trainingDataRowData.length > 0 && (
        <UIArticle className='article-grid'>
          <UIListContainer>
            <UIListContentBox.Header>
              <div className='w-full'>
                <UIUnitGroup gap={16} direction='column'>
                  <div className='flex justify-between w-full items-center'>
                    <div className='flex-shrink-0'>
                      <div style={{ width: '168px', paddingRight: '8px' }}>
                        <UIDataCnt count={fineTuningDetail?.datasetDetails?.length || 1} prefix='학습데이터 총' />
                      </div>
                    </div>
                  </div>
                </UIUnitGroup>
              </div>
            </UIListContentBox.Header>
            <UIListContentBox.Body>
              <UIGrid
                type='default'
                rowData={trainingDataRowData}
                columnDefs={trainingDataColumnDefs}
                onClickRow={(params: any) => {
                  const rowData = params.data;
                  navigate(`/data/dataCtlg/dataset/${rowData.id}${rowData?.datasourceId ? `?datasourceId=${rowData.datasourceId}` : ''}`);
                }}
              />
            </UIListContentBox.Body>
            <UIListContentBox.Footer>
              <UIPagination currentPage={1} totalPages={1} onPageChange={() => { }} className='flex justify-center' />
            </UIListContentBox.Footer>
          </UIListContainer>
        </UIArticle>
      )}

      {/* API Key 섹션 */}
      <ApiKeyList scope='model' id={servingId} name={data.name || ''} apiGwStatus={apiEndpointStatus} />

      {/* Advanced Setting 섹션 - TODO servingParams? */}
      {isSelfHosting && data.servingParams && (
        <UIArticle>
          <div className='article-header'>
            <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb'>
              Advanced Setting
            </UITypography>
          </div>
          <div className='article-body'>
            {/* 소스코드 영역 */}
            <UICode
              value={JSON.stringify(JSON.parse(data.servingParams), null, 2)}
              language='json'
              theme='dark'
              width='100%'
              minHeight='300px'
              maxHeight='500px'
              readOnly={true}
              wordWrap={true}
            />
          </div>
        </UIArticle>
      )}

      {/* envs 섹션 */}
      {isSelfHosting && data.envs && (
        <UIArticle>
          <div className='article-header'>
            <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb'>
              envs
            </UITypography>
          </div>
          <div className='article-body'>
            {/* 소스코드 영역 */}
            <UICode
              value={JSON.stringify(data.envs, null, 2)}
              language='json'
              theme='dark'
              width='100%'
              minHeight='300px'
              maxHeight='500px'
              readOnly={true}
              wordWrap={true}
            />
          </div>
        </UIArticle>
      )}

      {/* 예시 코드 */}
      <UIArticle>
        <div className='article-header'>
          <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb'>
            예시 코드
          </UITypography>
        </div>
        <div className='article-body'>
          {/* 소스코드 영역 */}
          <UICode value={exampleCode} language='python' theme='dark' width='100%' minHeight='300px' maxHeight='500px' readOnly={true} />
        </div>
      </UIArticle>

      {/* 담당자 정보 */}
      <ManagerInfoBox
        type='uuid'
        people={[
          { userId: data.createdBy, datetime: data.createdAt },
          { userId: data.updatedBy ?? data.createdBy, datetime: data.updatedAt ?? data.createdAt },
        ]}
      />

      {/* 프로젝트 정보 */}
      <ProjectInfoBox assets={[{ type: 'model', id: data.servingId }]} auth={AUTH_KEY.DEPLOY.MODEL_DEPLOY_CHANGE_PUBLIC} />

      {/* 페이지 footer */}
      <UIArticle>
        <UIUnitGroup gap={8} direction='row' align='center'>
          <Button auth={AUTH_KEY.DEPLOY.MODEL_DEPLOY_UPDATE} className='btn-primary-gray' onClick={handleDeleteModelDeploy}>
            삭제
          </Button>
          <Button auth={AUTH_KEY.DEPLOY.MODEL_DEPLOY_UPDATE} className='btn-primary-blue' onClick={handleEditButtonClick}>
            수정
          </Button>
        </UIUnitGroup>
      </UIArticle>
      {data && <DeployModelEdit {...layerPopupProps} data={data} onClose={handleEditPageClose} />}
    </>
  );
}
