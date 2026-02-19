import React, { useEffect, useMemo, useState } from 'react';

import { useNavigate } from 'react-router-dom';

import { ApiKeyList } from '@/components/common/apikey/ApiKeyList';
import { Button } from '@/components/common/auth';
import { ManagerInfoBox } from '@/components/common/manager';
import { ProjectInfoBox } from '@/components/common/project/ProjectInfoBox';
import { env, RUN_MODE_TYPES } from '@/constants/common/env.constants.ts';

import { UIDataCnt, UILabel, UITypography, type UILabelIntent } from '@/components/UI/atoms';
import { UIIcon2 } from '@/components/UI/atoms/UIIcon2';
import { UIPagination } from '@/components/UI/atoms/UIPagination';
import { UIGroup, UIUnitGroup } from '@/components/UI/molecules';
import { UICircleChart } from '@/components/UI/molecules/chart';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { api } from '@/configs/axios.config';
import { AUTH_KEY } from '@/constants/auth/auth.constants';
import { AGENT_DEPLOY_STATUS } from '@/constants/deploy/agentDeploy.constants';
import { COMMON_DEPLOY_API_GW_STATUS } from '@/constants/deploy/commonDeploy.constants';
import { useLayerPopup } from '@/hooks/common/layer';
import { useCopyHandler } from '@/hooks/common/util';
import { useGetAgentPods } from '@/services/admin/resrcMgmt';
import { useGetAgentLineages } from '@/services/agent/builder/agentBuilder.services';
import type { AgentBuilderDetailRes } from '@/services/agent/builder/types';
import {
  useDeleteAgentApp,
  useDeleteAgentAppDeploy,
  useGetAgentAppApiKeyListById,
  useGetAgentAppDeployListById,
  useGetAgentDeployInfo,
  useRegenerateAgentAppApiKey,
  useRestartAgentDeploy,
  useStopAgentDeploy,
} from '@/services/deploy/agent/agentDeploy.services';
import type { Deployment, GetAgentAppResponse } from '@/services/deploy/agent/types';
import { useCheckApiEndpoint, usePostRetryApiEndpoint } from '@/services/deploy/apigw/apigw.services';
import type { GetCheckApiEndpointResponse } from '@/services/deploy/apigw/types';
import { useModal } from '@/stores/common/modal';
import { dateUtils } from '@/utils/common';
import { DeployAgentCodeReviewPopupPage, DeployAgentEditPopupPage } from '../../../pages/deploy/agent';

/**
 * 노드 타입에 따라 표시명을 반환하는 함수
 * @param nodeType - 노드의 타입 문자열
 * @returns 노드 타입에 해당하는 표시명
 */
function getNodeType(nodeType: string | undefined): string {
  if (nodeType === 'agent__generator') {
    return 'Generator';
  } else if (nodeType === 'retriever__rewriter_hyde') {
    return 'Rewriter HyDE';
  } else if (nodeType === 'retriever__rewriter_multiquery') {
    return 'Rewriter MultiQuery';
  } else if (nodeType === 'retriever__doc_compressor') {
    return 'Compressor';
  } else if (nodeType === 'retriever__doc_filter') {
    return 'Doc Filter';
  } else if (nodeType === 'retriever__doc_reranker') {
    return 'Doc ReRanker';
  } else if (nodeType === 'agent__reviewer') {
    return 'Reviewer';
  } else if (nodeType === 'agent__categorizer') {
    return 'Categorizer';
  } else if (nodeType === 'retriever__knowledge') {
    return 'Retriever'; // 지식
  } else if (nodeType) {
    return nodeType;
  }
  return 'Serving Model';
}

interface DeployAgentInfomationProps {
  data: GetAgentAppResponse;
  agentBuilder?: AgentBuilderDetailRes;
  appId: string;
  refetch: () => void;
  onDropdownOptionsChange?: (options: Array<{ value: string; label: string }>) => void;
  onAuthorizationChange?: (authorization: string) => void;
}

export type DeployApiGwStatus = {
  apigwStatus: GetCheckApiEndpointResponse;
};

export function DeployAgentInfomation({
  data: agentAppData,
  agentBuilder,
  appId,
  refetch: refetchAgentAppData,
  onDropdownOptionsChange,
  onAuthorizationChange,
}: DeployAgentInfomationProps) {
  const navigate = useNavigate();
  const { handleCopy } = useCopyHandler();
  // 필터 상태
  const { openAlert, openConfirm } = useModal();
  const layerPopupCodeReview = useLayerPopup(); // cURL/Python 코드 확인용
  const layerPopupEdit = useLayerPopup(); // 수정 팝업용
  const [viewType, setViewType] = useState<'curl' | 'python'>('curl');
  const gatewayUrl = `${env.VITE_GATEWAY_URL}/agent/${appId}`;

  //api 등록 상태
  const { data: apiEndpointStatus } = useCheckApiEndpoint('agent', appId);

  // Lineage 데이터 조회
  const { data: lineagesData } = useGetAgentLineages(
    { agentId: agentAppData.targetId || '' },
    {
      enabled: Boolean(agentAppData.targetId),
    }
  );

  // 배포 버전 리스트 페이지네이션 상태
  const [deployListPage, setDeployListPage] = useState(1);
  const [allDeployListData, setAllDeployListData] = useState<any[]>([]);
  const displaySize = 6; // 화면에 표시할 페이지 크기

  // 전체 데이터 조회
  const { data: agentAppDeployListData, refetch: refetchAgentAppDeployList } = useGetAgentAppDeployListById(
    {
      appId: appId,
      page: 1,
      size: 1000, // 충분히 큰 값으로 설정하여 전체 데이터 조회
      sort: 'deployed_dt,desc',
    },
    {
      enabled: !!appId,
    }
  );

  // 전체 데이터 수집
  useEffect(() => {
    if (agentAppDeployListData?.content) {
      setAllDeployListData(agentAppDeployListData.content || []);
    } else {
      setAllDeployListData([]);
    }
  }, [agentAppDeployListData]);

  // 클라이언트 페이지네이션: 6개씩 나눠서 보여주기
  const paginatedDeployListData = useMemo(() => {
    const startIndex = (deployListPage - 1) * displaySize;
    const endIndex = startIndex + displaySize;
    return allDeployListData.slice(startIndex, endIndex);
  }, [allDeployListData, deployListPage, displaySize]);

  // 클라이언트 페이지네이션 기준 총 페이지 수
  const deployTotalPages = useMemo(() => {
    return allDeployListData.length > 0 ? Math.ceil(allDeployListData.length / displaySize) : 1;
  }, [allDeployListData.length, displaySize]);

  // agentAppDeployListData 중에 Available 상태인 항목이 있는지 확인 (전체 데이터 기준)
  const hasAvailableDeployment = useMemo(() => {
    if (!allDeployListData || allDeployListData.length === 0) return false;
    return allDeployListData.some((deploy: any) => deploy.status === 'Available');
  }, [allDeployListData]);

  /**
   * 에이전트 배포 정보 조회 (Available 상태인 배포가 있을 때만 조회)
   */
  const { data: agentDeployInfo } = useGetAgentDeployInfo(appId || '', {
    enabled: !!appId && allDeployListData.length > 0 && hasAvailableDeployment,
  });

  // 에이전트 Pods 조회
  const { data: agentPods } = useGetAgentPods();

  const podData = Array.isArray(agentPods?.pods)
    ? agentPods.pods.find((pod: any) => {
        return pod.pod_name.substring(0, 12) === agentDeployInfo?.isvcName;
      })
    : null;

  // CPU는 소수점 2자리까지, Memory는 바이트를 기가바이트로 변환
  const cpuUsage = Number(Number(podData?.cpu_usage || 0).toFixed(3));
  const memoryUsage = Number((Number(podData?.memory_usage || 0) / (1024 * 1024 * 1024)).toFixed(2)); // 바이트를 GB로 변환
  const cpuRequest = Number(Number(podData?.cpu_request || 100).toFixed(3));
  const memoryRequest = Number((Number(podData?.memory_request || 100) / (1024 * 1024 * 1024)).toFixed(2)); // 바이트를 GB로 변환

  function getServingInfo(node: any): { servingId: string; modelName: string } {
    let _servingId = '';
    let _modelName = '';

    // Categorizer, Reviewer, Generator 노드
    if (node.type === 'agent__categorizer' || node.type === 'agent__reviewer' || node.type === 'agent__generator') {
      _servingId = node.data?.serving_model || '';
      _modelName = node.data?.serving_name || '';
    }
    // Rewriter HyDE, Rewriter MultiQuery 노드
    else if (node.type === 'retriever__rewriter_hyde' || node.type === 'retriever__rewriter_multiquery') {
      _servingId = node.data?.query_rewriter?.llm_chain?.llm_config?.api_key || '';
      _modelName = node.data?.query_rewriter?.llm_chain?.llm_config?.serving_name || '';
    }
    // Compressor, Doc Filter
    else if (node.type === 'retriever__doc_compressor' || node.type === 'retriever__doc_filter') {
      _servingId = node.data?.context_refiner?.llm_chain?.llm_config?.api_key || '';
      _modelName = node.data?.context_refiner?.llm_chain?.llm_config?.serving_name || '';
    }
    // Doc ReRanker 노드
    else if (node.type === 'retriever__doc_reranker') {
      // 최상위 레벨의 serving_model을 우선 사용, 없으면 model_info 내부의 serving_model 사용
      _servingId = node.data?.context_refiner?.rerank_cnf?.model_info?.serving_model || '';
      _modelName = node.data?.context_refiner?.rerank_cnf?.model_info?.serving_name || '';
    }

    return {
      servingId: _servingId,
      modelName: _modelName,
    };
  }

  /**
   * 배포 버전 상세 페이지 이동
   * @param appId 에이전트 배포 고유 아이디
   */
  const handleDeployDetailClick = (servingId: string) => {
    navigate(`deploy/${servingId}`, {
      state: {
        servingId: servingId,
        deployName: agentAppData.name,
        description: agentAppData.description,
        builderName: agentAppData.builderName,
        targetId: agentAppData.targetId,
      },
    });
  };

  /**
   * 데이터 삭제 (단일 또는 다중 삭제)
   */
  const handleDeleteConfirm = async (appId: string) => {
    if (agentAppData.isMigration === true) {
      openAlert({
        title: '안내',
        message: '운영 배포된 에이전트는 삭제할 수 없습니다.',
      });
      return;
    }

    openConfirm({
      title: '안내',
      message: '삭제하시겠어요? \n삭제한 정보는 복구할 수 없습니다.',
      confirmText: '예',
      cancelText: '아니요',
      onConfirm: async () => {
        // agentAppDeployListData의 모든 배포 버전 삭제
        try {
          const deployDeletePromises: Promise<PromiseSettledResult<any>>[] = [];

          if (allDeployListData && allDeployListData.length > 0) {
            for (const deployItem of allDeployListData) {
              if (deployItem.id) {
                // 타임아웃 추가 (각 삭제 요청당 10초)
                const deleteWithTimeout = Promise.race([
                  deleteAgentAppDeployAsync({ deployId: deployItem.id }),
                  new Promise((_, reject) => setTimeout(() => reject(new Error('타임아웃')), 2000)),
                ]);

                deployDeletePromises.push(
                  deleteWithTimeout
                    .then(result => ({ status: 'fulfilled' as const, value: result }))
                    .catch(error => {
                      console.error(`배포 버전 삭제 실패 (deployId: ${deployItem.id}):`, error);
                      return { status: 'rejected' as const, reason: error };
                    })
                );
              }
            }
          }

          // 모든 배포 버전 삭제 완료 대기 (성공/실패 관계없이 모두 완료될 때까지)
          const results = await Promise.allSettled(deployDeletePromises);

          // 실패한 항목이 있는지 확인
          const failedCount = results.filter(r => r.status === 'rejected').length;
          if (failedCount > 0) {
            console.warn(`${failedCount}개의 배포 버전 삭제가 실패했지만 앱 삭제를 계속 진행합니다.`);
          }
        } catch (error) {
          console.error('배포 버전 삭제 중 오류:', error);
          // 에러가 발생해도 앱 삭제는 계속 진행
        }

        // 배포 버전 삭제 완료 후 앱 삭제
        deleteAgentApp({ appId: appId });
      },
    });
  };

  /**
   * 에이전트 앱 삭제
   */
  const { mutate: deleteAgentApp } = useDeleteAgentApp({
    onSuccess: () => {
      openAlert({
        title: '완료',
        message: '배포된 에이전트가 삭제되었습니다.',
        onConfirm: () => {
          navigate('/deploy/agentDeploy', { replace: true });
        },
      });
    },
    onError: () => {},
  });

  // 페이지네이션 상태
  const [modelCurrentPage, setModelCurrentPage] = useState(1);
  const PAGE_SIZE = 6;

  // 사용 모델 그리드 데이터 - Agent Graph 노드 기반 (모든 serving_model 표시)
  const modelRowData = useMemo(() => {
    if (!agentBuilder?.nodes) return { all: [], paginated: [] };

    // serving_model이 있는 모든 노드 필터링 (중복 제거 없이 모두 표시)
    const allowedTypes = [
      'agent__categorizer',
      'agent__reviewer',
      'agent__generator',
      'retriever__rewriter_hyde',
      'retriever__doc_compressor',
      'retriever__doc_filter',
      'retriever__rewriter_multiquery',
      'retriever__doc_reranker',
    ];

    // 1. 필터링: 허용된 타입의 노드만 선택
    const filteredNodes = agentBuilder.nodes.filter((node: any) => allowedTypes.includes(node.type));

    // 2. 변환: 각 노드를 그리드 데이터 형식으로 변환 (servingId, modelName이 있는 것만)
    const modelNodes = filteredNodes
      .filter((node: any) => {
        const { servingId, modelName } = getServingInfo(node);
        return !!servingId && !!modelName;
      })
      .map((node: any, index: number) => {
        const { servingId, modelName } = getServingInfo(node);

        return {
          id: index + 1,
          nodeName: node.data?.name,
          nodeType: getNodeType(node.type),
          modelName: modelName,
          servingId: servingId,
          node: node,
        };
      });

    return {
      all: modelNodes,
      paginated: modelNodes.slice((modelCurrentPage - 1) * PAGE_SIZE, modelCurrentPage * PAGE_SIZE),
    };
  }, [agentBuilder, modelCurrentPage]);

  // 사용 모델 컬럼 설정 - modelRowData 이후에 정의
  const modelColumnDefs: any = useMemo(
    () => [
      {
        headerName: 'NO',
        field: 'id' as any,
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
      },
      {
        headerName: '노드명',
        field: 'nodeName' as any,
        width: 392,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: '노드 종류',
        field: 'nodeType' as any,
        width: 392,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: '모델명',
        field: 'modelName' as any,
        flex: 1,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
    ],
    [modelRowData] // 의존성 배열에 modelRowData 추가
  );

  // ProjectInfoBox에 전달할 assets 배열 생성 (lineage 기반)
  const assets = useMemo(() => {
    const assetList: Array<{ type: string; id: string }> = [
      // 1. 에이전트 앱 자체
      { type: 'app', id: appId || '' },
    ];

    // 2. allDeployListData에서 servingId와 id 추가 (전체 데이터 기준)
    if (allDeployListData && allDeployListData.length > 0) {
      allDeployListData.forEach((deploy: any) => {
        if (deploy.servingId) {
          const exists = assetList.some(a => a.type === 'agent-serving' && a.id === deploy.servingId);
          if (!exists) {
            assetList.push({ type: 'agent-serving', id: deploy.servingId });
          }
        }
        if (deploy.id) {
          const exists = assetList.some(a => a.type === 'app-deployment' && a.id === deploy.id);
          if (!exists) {
            assetList.push({ type: 'app-deployment', id: deploy.id });
          }
        }
      });
    }

    // 3. lineage에서 사용된 에셋들 추가
    // lineagesData가 배열이거나 lineagesData.data가 배열일 수 있음
    const lineagesArray = Array.isArray(lineagesData) ? lineagesData : lineagesData?.data;
    if (lineagesArray && Array.isArray(lineagesArray)) {
      lineagesArray.forEach((lineage: any) => {
        // camelCase와 snake_case 모두 지원
        const targetType = (lineage as any).targetType ?? (lineage as any).target_type;
        const targetKey = (lineage as any).targetKey ?? (lineage as any).target_key;

        let assetType = '';

        // targetType에 따라 type 매핑
        if (targetType === 'AGENT_GRAPH') {
          assetType = 'graph';
        } else if (targetType === 'FEW_SHOT') {
          assetType = 'few-shot';
        } else if (targetType === 'TOOL') {
          assetType = 'tool';
        } else if (targetType === 'MCP') {
          assetType = 'mcp';
        } else if (targetType === 'PROMPT') {
          assetType = 'infer-prompts';
        }

        // 유효한 타입이고 targetKey가 있으면 추가
        if (assetType && targetKey) {
          // 중복 제거
          const exists = assetList.some(a => a.type === assetType && a.id === targetKey);
          if (!exists) {
            assetList.push({ type: assetType, id: targetKey });
          }
        }
      });
    }

    return assetList;
  }, [appId, allDeployListData, lineagesData]);

  // 페이지네이션 상태
  const [knowledgeCurrentPage, setKnowledgeCurrentPage] = useState(1);
  const [knowledgeRepoIds, setKnowledgeRepoIds] = useState<string[]>([]);
  const [knowledgeRowData, setKnowledgeRowData] = useState<{ all: any[]; paginated: any[] }>({ all: [], paginated: [] });

  useEffect(() => {
    if (!agentBuilder?.nodes) {
      setKnowledgeRepoIds([]);
      setKnowledgeRowData({ all: [], paginated: [] });
      return;
    }

    // 1. type: "retriever__knowledge"인 node만 필터
    const knowledgeNodes = agentBuilder.nodes.filter((node: any) => node.type === 'retriever__knowledge');

    // 2. 필터링된 node의 data?.knowledge_retriever?.repo_id 가져오기
    // 중복 저장 가능하도록 배열로 변경
    const knowledgeRowDataArray: Array<{
      no: number;
      nodeName: string;
      nodeType: string;
      knowledgeName: string;
      knowledgeId: string; // ADXP ID
      knwId: string; // 지식 껍데기 ID
    }> = [];

    const knowledgeRepoIdSet = new Array<string>();

    knowledgeNodes.forEach((node: any) => {
      const repoId = node.data?.knowledge_retriever?.repo_id;
      if (!repoId) {
        return;
      }

      // 중복 저장 가능: 같은 repoId를 사용하는 여러 노드를 모두 저장
      knowledgeRowDataArray.push({
        no: knowledgeRowDataArray.length + 1,
        nodeName: node.data?.name ?? '',
        nodeType: getNodeType(node.type),
        knowledgeName: '', // 따로 서비스 호출하여 가져올거라 일단은 빈칸
        knowledgeId: repoId,
        knwId: '',
      });

      // repoId는 Set에 저장하여 중복 제거
      knowledgeRepoIdSet.push(repoId);
    });

    setKnowledgeRepoIds(knowledgeRepoIdSet);
    setKnowledgeRowData({
      all: knowledgeRowDataArray,
      paginated: knowledgeRowDataArray.slice((knowledgeCurrentPage - 1) * PAGE_SIZE, knowledgeCurrentPage * PAGE_SIZE),
    });
  }, [agentBuilder?.nodes, knowledgeCurrentPage]);

  // 지식 상세 조회하여 지식명 가져오기
  useEffect(() => {
    const fetchKnowledgeRepos = async () => {
      if (knowledgeRowData.all.length === 0) {
        return;
      }

      // knowledgeRowData.all의 복사본 만들기
      const updatedRowData = knowledgeRowData.all.map(row => ({ ...row }));

      await Promise.all(
        knowledgeRowData.all.map(async (rowData, index) => {
          if (!rowData.knowledgeId) return;

          // 🔥 SelectKnowledge와 동일한 API 사용 (external만 시도)
          const response = await api.get(`/dataCtlg/knowledge/repos/external/v2/${rowData.knowledgeId}`); // adxp id
          const knowledgeName = response?.data?.data?.name || '';
          const knwId = response?.data?.data?.knwId || ''; // 지식 껍데기 ID

          // 복사본에 knowledgeName 저장
          updatedRowData[index].knowledgeName = knowledgeName;
          updatedRowData[index].knwId = knwId;
        })
      );

      // Promise.all이 끝나면 복사본으로 setKnowledgeRowData (페이징 처리 포함)
      setKnowledgeRowData({
        all: updatedRowData,
        paginated: updatedRowData.slice((knowledgeCurrentPage - 1) * PAGE_SIZE, knowledgeCurrentPage * PAGE_SIZE),
      });
    };

    fetchKnowledgeRepos();
  }, [knowledgeRepoIds, knowledgeCurrentPage]);

  // 사용 지식 컬럼 설정 - knowledgeRowData 이후에 정의
  const knowledgeColumnDefs: any = useMemo(
    () => [
      {
        headerName: 'NO',
        field: 'id' as any,
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
      },
      {
        headerName: '노드명',
        field: 'nodeName' as any,
        width: 392,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: '노드 종류',
        field: 'nodeType' as any,
        width: 392,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: '지식명',
        field: 'knowledgeName' as any,
        flex: 1,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
    ],
    []
  );

  // 배포 버전 그리드 컬럼 설정
  const agentAppDeployListColumnDefs: any = useMemo(
    () => [
      {
        headerName: 'NO',
        field: 'no',
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
        valueGetter: (params: any) => {
          return (deployListPage - 1) * displaySize + params.node.rowIndex + 1;
        },
      },
      {
        headerName: '버전',
        field: 'version' as any, // API 응답: version (number)
        width: 272,
        cellStyle: {
          paddingLeft: '16px',
        },
        valueGetter: (params: any) => {
          if (!params || !params.data) return '';
          return params.data.version ? `ver.${params.data.version}` : '';
        },
      },
      {
        headerName: '상태',
        field: 'status' as any, // API 응답: status (string)
        width: 130,
        cellStyle: {
          paddingLeft: '16px',
        },
        cellRenderer: React.memo((params: any) => {
          const statusConfig = AGENT_DEPLOY_STATUS[params.value as keyof typeof AGENT_DEPLOY_STATUS];
          return (
            <UILabel variant='badge' intent={(statusConfig?.intent as UILabelIntent) || 'gray'}>
              {statusConfig?.label || params.value}
            </UILabel>
          );
        }),
      },
      {
        headerName: '운영 배포 여부',
        field: 'isMigration' as any,
        width: 272,
        cellStyle: {
          paddingLeft: '16px',
        },
        cellRenderer: React.memo((params: any) => {
          const isMigration = params.data?.isMigration === true;
          return env.VITE_RUN_MODE !== RUN_MODE_TYPES.PROD ? (isMigration ? '배포' : '미배포') : '배포';
        }),
      },
      {
        headerName: '배포일',
        field: 'deployedDt' as any,
        flex: 1,
        cellStyle: {
          paddingLeft: '16px',
        },
        valueGetter: (params: any) => {
          return formatDateSafely(params.data?.deployedDt);
        },
      },
      {
        headerName: '',
        field: 'more', // 더보기 컬럼 필드명 (고정)
        width: 56,
      },
    ],
    []
  );

  const handleStartAlert = (status: string, deployId: string, servingId: string) => {
    if (servingId === '' || servingId === null || servingId === undefined) {
      openAlert({
        message: '배포된 Serving ID가 없습니다. 배포를 진행할 수 없습니다.',
        confirmText: '확인',
        title: '안내',
        onClose: () => {},
      });
      return;
    }

    if (status === 'Available') {
      openAlert({
        message: 'Available 상태에서는 배포를 시작할 수 없습니다.',
        confirmText: '확인',
        title: '안내',
        onClose: () => {},
      });
    } else {
      openAlert({
        message: '배포를 시작합니다.',
        confirmText: '확인',
        title: '안내',
        onConfirm: () => {
          startAgentDeploy({ deployId });
        },
      });
    }
  };

  const { mutate: startAgentDeploy } = useRestartAgentDeploy({
    onSuccess: () => {
      // 배포 리스트만 재조회 (info API는 Available 배포가 있을 때만 자동 조회)
      refetchAgentAppDeployList();
    },
  });

  const handleStopAlert = (deployId: string) => {
    openAlert({
      message: '배포를 중지합니다.',
      confirmText: '확인',
      title: '안내',
      onConfirm: () => {
        stopAgentDeploy({ deployId });
      },
    });
  };

  const { mutate: stopAgentDeploy } = useStopAgentDeploy({
    onSuccess: () => {
      // 배포 리스트만 재조회
      refetchAgentAppDeployList();
    },
  });

  const handleDeleteAlert = (deployId: string) => {
    // if (agentAppData.isMigration === true) {
    //   openAlert({
    //     title: '안내',
    //     message: '운영 배포된 에이전트는 삭제할 수 없습니다.',
    //   });
    //   return;
    // }

    openConfirm({
      title: '안내',
      message: '삭제하시겠어요? \n삭제한 정보는 복구할 수 없습니다.',
      confirmText: '예',
      cancelText: '아니요',
      onConfirm: () => {
        deleteAgentAppDeploy({ deployId });
      },
    });
  };

  const { mutate: deleteAgentAppDeploy, mutateAsync: deleteAgentAppDeployAsync } = useDeleteAgentAppDeploy({
    onSuccess: () => {
      // 배포 리스트만 재조회
      refetchAgentAppDeployList();
    },
  });

  // 더보기 메뉴 설정
  const moreMenuConfig = useMemo(
    () => ({
      items: [
        {
          label: '시작',
          action: 'start',
          auth: AUTH_KEY.DEPLOY.AGENT_DEPLOY_UPDATE,
          visible: (rowData: any) => {
            // Failed 또는 stopped 상태일 때만 표시
            return rowData.status === 'Failed' || rowData.status === 'Stopped';
          },
          onClick: (rowData: any) => {
            handleStartAlert(rowData.status, rowData.id, rowData.servingId);
          },
        },
        {
          label: '중지',
          action: 'stop',
          auth: AUTH_KEY.DEPLOY.AGENT_DEPLOY_UPDATE,
          visible: (rowData: any) => {
            // Available 또는 Deploying 상태일 때만 표시
            return rowData.status === 'Available' || rowData.status === 'Deploying';
          },
          onClick: (rowData: any) => {
            handleStopAlert(rowData.id);
          },
        },
        {
          label: '삭제',
          action: 'delete',
          auth: AUTH_KEY.DEPLOY.AGENT_DEPLOY_DELETE,
          visible: () => true, // 모든 상태에서 표시
          onClick: (rowData: any) => {
            handleDeleteAlert(rowData.id);
          },
        },
      ],
    }),
    [startAgentDeploy] // 의존성 배열에 startAgentDeploy 추가
  );

  const handleBuilderClick = () => {
    if (!agentBuilder) {
      return;
    }

    navigate(`/agent/builder/graph`, {
      state: {
        isReadOnly: env.VITE_RUN_MODE === RUN_MODE_TYPES.PROD ? true : false,
        agentId: agentBuilder.id,
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

  // 안전한 날짜 포맷팅 함수
  const formatDateSafely = (dateString: string | undefined, fallback: string = '-') => {
    if (!dateString || dateString.trim() === '') {
      return fallback;
    }

    try {
      const date = new Date(dateString);
      if (isNaN(date.getTime())) {
        return fallback;
      }
      return dateUtils.formatDate(dateString, 'datetime');
    } catch (error) {
      // console.warn('날짜 포맷팅 오류:', dateString, error);
      return fallback;
    }
  };

  // 드롭다운 옵션 생성
  const dropdownOptions = useMemo(() => {
    const options = [{ value: agentAppData.id, label: 'app' }];

    // deployments 배열에서 status가 'Available'인 것만 필터링하여 servingId를 추출하여 버전 옵션 추가
    if (agentAppData.deployments && Array.isArray(agentAppData.deployments)) {
      agentAppData.deployments
        .filter((deployment: Deployment) => deployment.status === 'Available')
        .forEach((deployment: Deployment) => {
          if (deployment.servingId) {
            options.push({
              value: deployment.servingId,
              label: `ver.${deployment.version}`,
            });
          }
        });
    }

    return options;
  }, [agentAppData]);

  // dropdownOptions가 변경될 때마다 부모 컴포넌트에 전달
  useEffect(() => {
    if (onDropdownOptionsChange && dropdownOptions.length > 0) {
      onDropdownOptionsChange(dropdownOptions);
    }
  }, [dropdownOptions, onDropdownOptionsChange]);

  const { data: keyData, refetch: refetchKeyData } = useGetAgentAppApiKeyListById(
    {
      appId: appId,
    },
    {
      enabled: !!appId,
    }
  );

  // API 키 재생성
  const { mutate: regenerateApiKey } = useRegenerateAgentAppApiKey(
    { appId: appId },
    {
      onSuccess: () => {
        // API 키 생성 성공 후 데이터 다시 조회
        refetchKeyData();
      },
      onError: (/* error: any */) => {
        // console.error('API 키 생성 실패:', error);
      },
    }
  );

  // keyData가 비어있을 때 자동으로 API 키 생성
  useEffect(() => {
    if (appId && agentAppData.deploymentStatus === 'Available' && keyData && (!keyData.apiKeys || keyData.apiKeys.length === 0)) {
      regenerateApiKey({ appId });
    }
  }, [appId, agentAppData.deploymentStatus, keyData, regenerateApiKey]);

  // authorization 값 설정 및 부모에게 전달
  useEffect(() => {
    if (onAuthorizationChange && keyData?.apiKeys && keyData.apiKeys.length > 0) {
      const authorization = keyData.apiKeys[0];
      onAuthorizationChange(authorization);
    }
  }, [keyData, onAuthorizationChange]);

  // 재시도
  const { mutate: retryApiEndpoint } = usePostRetryApiEndpoint(agentAppData.id);
  const handleRetry = () => {
    retryApiEndpoint(agentAppData.id, {
      onSuccess: () => {
        refetchAgentAppData();
      },
    });
  };

  return (
    <>
      <>
        {/* 테이블 */}
        <UIArticle>
          <div className='article-header'>
            <UIGroup direction='column' gap={8}>
              <UIUnitGroup direction='row' align='space-between' gap={0}>
                <UITypography variant='title-4' className='secondary-neutral-900'>
                  에이전트 배포 정보
                </UITypography>
                {apiEndpointStatus?.status === 'FAILED' && (
                  <Button className='btn-option-outlined' onClick={handleRetry}>
                    재시도
                  </Button>
                )}
              </UIUnitGroup>
              <UITypography variant='body-2' className='secondary-neutral-600'>
                A.X 배포 상태와 API Gateway 배포 상태는 APP 버전의 상태값입니다.
              </UITypography>
            </UIGroup>
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
                        {agentAppData.name}
                      </UITypography>
                    </td>
                    <th>
                      <UITypography variant='body-2' className='secondary-neutral-900'>
                        배포 유형
                      </UITypography>
                    </th>
                    <td>
                      <UITypography variant='body-2' className='secondary-neutral-600'>
                        {agentAppData.targetId !== null ? '기본' : '사용자 정의'}
                      </UITypography>
                    </td>
                  </tr>
                  {agentAppData.targetId !== null ? (
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          빌더
                        </UITypography>
                      </th>
                      <td>
                        <UIUnitGroup gap={16} direction='row' vAlign='center'>
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            {agentAppData.builderName}
                          </UITypography>
                          <Button
                            auth={AUTH_KEY.AGENT.BUILDER_UPDATE}
                            className='btn-text-14-point ml-4'
                            rightIcon={{ className: 'ic-system-12-arrow-right-blue', children: '' }}
                            onClick={() => handleBuilderClick()}
                          >
                            빌더 바로가기
                          </Button>
                        </UIUnitGroup>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          설명
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {agentAppData.description}
                        </UITypography>
                      </td>
                    </tr>
                  ) : (
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          설명
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {agentAppData.description}
                        </UITypography>
                      </td>
                    </tr>
                  )}
                  <tr style={{ height: '68px' }}>
                    <th>
                      <UITypography variant='body-2' className='secondary-neutral-900'>
                        A.X 배포 상태
                      </UITypography>
                    </th>
                    <td>
                      <UITypography variant='body-2' className='secondary-neutral-600'>
                        {(() => {
                          const statusValue = agentAppData.deploymentStatus;

                          // 상태 배지 로직 (DeployAgentListPage와 동일)
                          let label = '';
                          let intent: UILabelIntent = 'gray';

                          if (!statusValue) {
                            const fallback = AGENT_DEPLOY_STATUS.Failed;
                            label = fallback.label;
                            intent = fallback.intent as UILabelIntent;
                          } else {
                            const normalized = Object.keys(AGENT_DEPLOY_STATUS).find(key => key.toLowerCase() === String(statusValue).toLowerCase());

                            if (!normalized) {
                              label = statusValue;
                              intent = 'gray';
                            } else {
                              const config = AGENT_DEPLOY_STATUS[normalized as keyof typeof AGENT_DEPLOY_STATUS];
                              label = config.label;
                              intent = (config.intent as UILabelIntent) || 'gray';
                            }
                          }

                          return (
                            <UILabel variant='badge' intent={intent}>
                              {label}
                            </UILabel>
                          );
                        })()}
                      </UITypography>
                    </td>
                    <th>
                      <UITypography variant='body-2' className='secondary-neutral-900'>
                        API Gateway
                        <br />
                        배포 상태
                      </UITypography>
                    </th>
                    <td style={{ borderTop: '1px solid #e7edf6' }}>
                      <UITypography variant='body-2' className='secondary-neutral-600'>
                        <UILabel
                          variant='badge'
                          intent={COMMON_DEPLOY_API_GW_STATUS[apiEndpointStatus?.status as keyof typeof COMMON_DEPLOY_API_GW_STATUS]?.intent as UILabelIntent}
                        >
                          {COMMON_DEPLOY_API_GW_STATUS[apiEndpointStatus?.status as keyof typeof COMMON_DEPLOY_API_GW_STATUS]?.label || apiEndpointStatus?.status}
                        </UILabel>
                      </UITypography>
                    </td>
                  </tr>
                  <tr>
                    <th>
                      <UITypography variant='body-2' className='secondary-neutral-900'>
                        Endpoint
                      </UITypography>
                    </th>
                    <td colSpan={3}>
                      <UITypography variant='body-2' className='secondary-neutral-600'>
                        <div className='flex align-center gap-2'>
                          <span>{`${env.VITE_GATEWAY_URL}/agent/${agentAppData.id}`}</span>
                          <a href='#none' onClick={() => handleCopy(`${env.VITE_GATEWAY_URL}/agent/${agentAppData.id}`)}>
                            <UIIcon2 className='ic-system-20-copy-gray' />
                          </a>
                        </div>
                      </UITypography>
                    </td>
                  </tr>
                  <tr>
                    <th>
                      <UITypography variant='body-2' className='secondary-neutral-900'>
                        cURL 코드
                      </UITypography>
                    </th>
                    <td>
                      <UITypography variant='body-2' className='secondary-neutral-600'>
                        <Button
                          className='btn-text-14-underline-point imp-underline_16'
                          rightIcon={{ className: 'ic-16 ic-system-24-outline-blue-export ipt-16', children: '' }}
                          onClick={() => {
                            setViewType('curl');
                            layerPopupCodeReview.onOpen();
                          }}
                        >
                          코드 확인하기
                        </Button>
                      </UITypography>
                    </td>
                    <th>
                      <UITypography variant='body-2' className='secondary-neutral-900'>
                        Python 코드
                      </UITypography>
                    </th>
                    <td>
                      <UITypography variant='body-2' className='secondary-neutral-600'>
                        <Button
                          className='btn-text-14-underline-point imp-underline_16'
                          rightIcon={{ className: 'ic-16 ic-system-24-outline-blue-export ipt-16', children: '' }}
                          onClick={() => {
                            setViewType('python');
                            layerPopupCodeReview.onOpen();
                          }}
                        >
                          코드 확인하기
                        </Button>
                      </UITypography>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </UIArticle>

        <UIArticle>
          <div className='article-header'>
            <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb'>
              자원 할당량 및 사용률
            </UITypography>
          </div>
          {/*  TODO : 리소스 정보 + 리소스 차트 */}
          <div className='flex items-center gap-[80px]'>
            <div className='flex-1 flex justify-center'>
              <div className='flex justify-between items-center'>
                <div className='chart-item flex-1'>
                  <div className='flex chart-graph h-[218px] gap-x-10 justify-between'>
                    <div className='w-[280px] flex items-center justify-center'>
                      <UICircleChart.Half type='CPU' value={cpuUsage} total={cpuRequest} showLabel={false} />
                    </div>
                    <div className='w-[280px] flex items-center justify-center'>
                      <UICircleChart.Half type='Memory' value={memoryUsage} total={memoryRequest} showLabel={false} />
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </UIArticle>

        {agentAppData.targetId !== null && (
          <>
            <UIArticle className='article-grid'>
              <UIListContainer>
                <UIListContentBox.Header>
                  <div className='w-full'>
                    <UIUnitGroup gap={16} direction='column'>
                      <div className='flex justify-between w-full items-center'>
                        <div className='w-full'>
                          <UIGroup gap={12} direction='row' align='start'>
                            <div style={{ width: '200px', display: 'flex', alignItems: 'center' }}>
                              <UIDataCnt count={modelRowData.all.length} prefix='사용 모델 총' unit='건' />
                            </div>
                          </UIGroup>
                        </div>
                      </div>
                    </UIUnitGroup>
                  </div>
                </UIListContentBox.Header>
                <UIListContentBox.Body>
                  <UIGrid
                    rowData={modelRowData.paginated}
                    columnDefs={modelColumnDefs}
                    onClickRow={(params: any) => {
                      const { servingId } = getServingInfo(params?.data?.node);
                      if (servingId) {
                        // 운영 환경에서는 모델 상세 이동 불가 (모델 서빙 id 가 개발/운영이 달라짐)
                        env.VITE_RUN_MODE === RUN_MODE_TYPES.PROD ? '' : navigate(`/deploy/modelDeploy/${servingId}`);
                      }
                    }}
                  />
                </UIListContentBox.Body>
                <UIListContentBox.Footer>
                  {modelRowData.all.length > 0 && (
                    <UIPagination
                      currentPage={modelCurrentPage}
                      totalPages={Math.ceil(modelRowData.all.length / PAGE_SIZE)}
                      onPageChange={(page: number) => setModelCurrentPage(page)}
                      className='flex justify-center'
                    />
                  )}
                </UIListContentBox.Footer>
              </UIListContainer>
            </UIArticle>

            <UIArticle className='article-grid'>
              <UIListContainer>
                <UIListContentBox.Header>
                  <div className='w-full'>
                    <UIUnitGroup gap={16} direction='column'>
                      <div className='flex justify-between w-full items-center'>
                        <div className='w-full'>
                          <UIGroup gap={12} direction='row' align='start'>
                            <div style={{ width: '200px', display: 'flex', alignItems: 'center' }}>
                              <UIDataCnt count={knowledgeRowData.all.length} prefix='사용 지식 총' unit='건' />
                            </div>
                          </UIGroup>
                        </div>
                      </div>
                    </UIUnitGroup>
                  </div>
                </UIListContentBox.Header>
                <UIListContentBox.Body>
                  <UIGrid
                    rowData={knowledgeRowData.paginated}
                    columnDefs={knowledgeColumnDefs}
                    onClickRow={(params: any) => {
                      const knowledgeDetailId = params.data.knwId || params.data.knowledgeId;
                      if (knowledgeDetailId) {
                        env.VITE_RUN_MODE === RUN_MODE_TYPES.PROD ? '' : navigate(`/data/dataCtlg/knowledge/detail/${knowledgeDetailId}`); // 지식 상세 이동
                      }
                    }}
                  />
                </UIListContentBox.Body>
                <UIListContentBox.Footer>
                  {knowledgeRowData.all.length > 0 && (
                    <UIPagination
                      currentPage={knowledgeCurrentPage}
                      totalPages={Math.ceil(knowledgeRowData.all.length / PAGE_SIZE)}
                      onPageChange={(page: number) => setKnowledgeCurrentPage(page)}
                      className='flex justify-center'
                    />
                  )}
                </UIListContentBox.Footer>
              </UIListContainer>
            </UIArticle>
          </>
        )}

        {/* 그리드 : 3번  - API Key 섹션*/}
        <ApiKeyList scope='agent' id={appId || ''} name={agentAppData.name || ''} apiGwStatus={apiEndpointStatus} />

        {/* 그리드 : 4번 - 배포 목록 섹션 */}
        <UIArticle className='article-grid'>
          <UIListContainer>
            <UIListContentBox.Header>
              <div className='w-full'>
                <UIUnitGroup gap={16} direction='column'>
                  <div className='flex justify-between w-full items-center'>
                    <div className='w-full'>
                      <UIGroup gap={12} direction='row' align='start'>
                        <div style={{ width: '200px', display: 'flex', alignItems: 'center' }}>
                          <UIDataCnt count={allDeployListData.length || 0} prefix='버전 정보 총' unit='건' />
                        </div>
                      </UIGroup>
                    </div>
                  </div>
                </UIUnitGroup>
              </div>
            </UIListContentBox.Header>
            <UIListContentBox.Body>
              <UIGrid
                rowData={paginatedDeployListData}
                columnDefs={agentAppDeployListColumnDefs}
                moreMenuConfig={moreMenuConfig}
                onClickRow={(params: any) => {
                  if (params.data.status !== 'Failed') {
                    handleDeployDetailClick(params.data.servingId);
                  } else {
                    // TODO : 서빙에 실패해서 못간다고 alert ?
                  }
                }}
              />
            </UIListContentBox.Body>
            <UIListContentBox.Footer>
              <UIPagination
                currentPage={deployListPage || 1}
                totalPages={deployTotalPages}
                hasNext={agentAppDeployListData?.hasNext}
                onPageChange={(newPage: number) => {
                  setDeployListPage(newPage);
                }}
                className='flex justify-center'
              />
            </UIListContentBox.Footer>
          </UIListContainer>
        </UIArticle>

        {/* 담당자 정보 테이블 */}
        <ManagerInfoBox
          type='uuid'
          people={[
            { userId: agentAppData?.createdBy || '', datetime: agentAppData?.createdAt || '' },
            { userId: agentAppData?.updatedBy || '', datetime: agentAppData?.updatedAt || '' },
          ]}
        />

        {/* 테이블 */}
        <ProjectInfoBox assets={assets} auth={AUTH_KEY.DEPLOY.AGENT_DEPLOY_CHANGE_PUBLIC} />

        {/* 페이지 footer - renderTabContent에서 표시 하기 위해 변경 */}
        <UIArticle>
          <UIUnitGroup gap={8} direction='row' align='center'>
            <Button
              auth={AUTH_KEY.DEPLOY.AGENT_DEPLOY_DELETE}
              className='btn-primary-gray'
              onClick={() => {
                handleDeleteConfirm(appId);
              }}
            >
              삭제
            </Button>
            <Button auth={AUTH_KEY.DEPLOY.AGENT_DEPLOY_UPDATE} className='btn-primary-blue' onClick={layerPopupEdit.onOpen}>
              수정
            </Button>
          </UIUnitGroup>
        </UIArticle>
      </>

      {/* 팝업들 */}
      <DeployAgentEditPopupPage
        appId={appId}
        name={agentAppData.name || ''}
        description={agentAppData.description || ''}
        isOpen={layerPopupEdit.currentStep > 0}
        onClose={layerPopupEdit.onClose}
        onUpdateSuccess={() => {
          refetchAgentAppData();
        }}
      />

      <DeployAgentCodeReviewPopupPage
        viewType={viewType}
        currentStep={layerPopupCodeReview.currentStep}
        onNextStep={layerPopupCodeReview.onNextStep}
        onPreviousStep={layerPopupCodeReview.onPreviousStep}
        onClose={layerPopupCodeReview.onClose}
        endPoint={gatewayUrl}
      />
    </>
  );
}
