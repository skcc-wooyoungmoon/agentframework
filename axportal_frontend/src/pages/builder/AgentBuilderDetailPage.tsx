import { agentAtom, edgesAtom, keyTableAtom, nodesAtom } from '@/components/builder/atoms/AgentAtom';
import { messagesAtom } from '@/components/builder/atoms/messagesAtom';
import { Button } from '@/components/common/auth';
import { ManagerInfoBox } from '@/components/common/manager/ManagerInfoBox/component';
import { ProjectInfoBox } from '@/components/common/project/ProjectInfoBox/component';
import { UIDataCnt, UITypography } from '@/components/UI/atoms';
import { UIPagination } from '@/components/UI/atoms/UIPagination';
import { UIArticle, UIGroup, UIUnitGroup } from '@/components/UI/molecules';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIPageFooter } from '@/components/UI/molecules/UIPageFooter';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { api } from '@/configs/axios.config';
import { AUTH_KEY } from '@/constants/auth/auth.constants';
import { env, RUN_MODE_TYPES } from '@/constants/common/env.constants';
import { AGENT_BUILDER_DEPLOY_STATUS, type AgentBuilderDeployStatus } from '@/constants/deploy/agentDeploy.constants';
import { useLayerPopup } from '@/hooks/common/layer';
import { getPhoenixProjectId, useDeleteAgentBuilder, useGetAgentBuilderById, useGetAgentDeployInfo, useGetAgentLineages } from '@/services/agent/builder2/agentBuilder.services';
import { useGetAgentAppById } from '@/services/deploy/agent/agentDeploy.services';
import { useUser } from '@/stores/auth/useUser';
import { useModal } from '@/stores/common/modal';
import { useSetAtom } from 'jotai';
import { useEffect, useMemo, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { AgentBuilderEditPopupPage } from './AgentBuilderEditPopupPage';
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
    _servingId = node.data?.context_refiner?.rerank_cnf?.model_info?.api_key || '';
    _modelName = node.data?.context_refiner?.rerank_cnf?.model_info?.serving_name || '';
  }

  return {
    servingId: _servingId,
    modelName: _modelName,
  };
}

export function AgentBuilderDetailPage() {
  const { agentId } = useParams<{ agentId: string }>();
  const { openAlert, openConfirm } = useModal();
  const navigate = useNavigate();
  const layerPopupOne = useLayerPopup();
  const deleteAgentBuilderMutation = useDeleteAgentBuilder();

  const { user } = useUser();

  // Atoms 초기화를 위한 setter
  const setNodes = useSetAtom(nodesAtom);
  const setEdges = useSetAtom(edgesAtom);
  const setAgent = useSetAtom(agentAtom);
  const setKeyTable = useSetAtom(keyTableAtom);
  const setMessages = useSetAtom(messagesAtom);
  const [knowledgeRepoIds, setKnowledgeRepoIds] = useState<string[]>([]);
  const [knowledgeRowData, setKnowledgeRowData] = useState<{ all: any[]; paginated: any[] }>({ all: [], paginated: [] });
  const [isDeployed, setIsDeployed] = useState<AgentBuilderDeployStatus>(AGENT_BUILDER_DEPLOY_STATUS.LOADING);
  const [modelCurrentPage, setModelCurrentPage] = useState(1);
  const [knowledgeCurrentPage, setKnowledgeCurrentPage] = useState(1);
  const PAGE_SIZE = 6;

  const { data: agentBuilder, refetch: refetchAgentBuilder } = useGetAgentBuilderById(agentId!, {
    enabled: false,
  });
  const { data: lineagesData, refetch: refetchLineages } = useGetAgentLineages(
    { agentId: agentId! },
    {
      enabled: Boolean(agentId),
    }
  );
  // 그래프 id ==> 개발 배포여부 조회
  const { data: agentAppData } = useGetAgentDeployInfo(agentId || '', {
    enabled: !!agentId,
  });
  // 배포 App id ==> 운영 배포여부 조회 (isMigration: true)
  const { data: agentDeployInfo, refetch: refetchAgentDeployInfo } = useGetAgentAppById(
    { appId: agentAppData?.id || '' },
    {
      enabled: false,
    }
  );

  // 페이지 변경 시 데이터 다시 조회
  useEffect(() => {
    refetchAgentBuilder();
  }, []);

  useEffect(() => {
    if (agentAppData?.id) {
      refetchAgentDeployInfo();
    }
  }, [agentAppData?.id, refetchAgentDeployInfo]);

  // 배포 상태 결정
  useEffect(() => {
    // agentAppData가 undefined이면 아직 API 조회 중이므로 LOADING 상태 유지
    if (agentAppData === undefined) {
      return;
    }

    const hasDevDeploy = !!agentAppData?.id;
    const hasProdDeploy = agentDeployInfo?.isMigration === true;

    if (hasDevDeploy && hasProdDeploy) {
      setIsDeployed(AGENT_BUILDER_DEPLOY_STATUS.BOTH_DEPLOYED);
    } else if (hasDevDeploy) {
      setIsDeployed(AGENT_BUILDER_DEPLOY_STATUS.DEV_DEPLOYED);
    } else {
      setIsDeployed(AGENT_BUILDER_DEPLOY_STATUS.NOT_DEPLOYED);
    }
  }, [agentAppData, agentDeployInfo?.isMigration]);

  // 사용 모델 그리드 컬럼 설정
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
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '노드 종류',
        field: 'nodeType' as any,
        width: 392,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '모델명',
        field: 'modelName' as any,
        flex: 1,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '',
        field: 'more',
        width: 56,
      },
    ],
    []
  );

  // 사용 지식 그리드 컬럼 설정
  const knowledgeColumnDefs: any = useMemo(
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
      },
      {
        headerName: '노드명',
        field: 'nodeName' as any,
        width: 392,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '노드 종류',
        field: 'nodeType' as any,
        width: 392,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '지식명',
        field: 'knowledgeName' as any,
        flex: 1,
        cellStyle: { paddingLeft: '16px', cursor: 'pointer', color: '#1B84FF', textDecoration: 'underline' },
      },
    ],
    []
  );

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

  // ----------------------------------------------------------------------------------------- 지식 관련 코드 -----------------------------------------------------------------------------------------

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

  // -----------------------------------------------------------------------------------------

  // 페이지 진입 시 Lineage 데이터도 새로고침
  useEffect(() => {
    if (agentId) {
      refetchLineages();
    }
  }, [agentId, refetchLineages]);

  // ProjectInfoBox에 전달할 assets 배열 생성 (lineage 기반)
  const assets = useMemo(() => {
    const assetList: Array<{ type: string; id: string }> = [
      // 1. 에이전트 그래프 자체
      { type: 'graph', id: agentBuilder?.id || '' },
    ];

    // 2. lineage에서 사용된 에셋들 추가
    if (lineagesData && Array.isArray(lineagesData)) {
      lineagesData.forEach((lineage: any) => {
        let assetType = '';

        // targetType에 따라 type 매핑
        if (lineage.target_type === 'FEW_SHOT') {
          assetType = 'few-shot';
        } else if (lineage.target_type === 'TOOL') {
          assetType = 'tool';
        } else if (lineage.target_type === 'MCP') {
          assetType = 'mcp';
        } else if (lineage.target_type === 'PROMPT') {
          assetType = 'infer-prompts';
        }
        // else if (lineage.target_type === 'SERVING_AGENT') {
        //   assetType = 'agent-serving';
        // }

        // 유효한 타입이고 targetKey가 있으면 추가
        if (assetType && lineage.target_key) {
          // 중복 제거
          const exists = assetList.some(a => a.type === assetType && a.id === lineage.target_key);
          if (!exists) {
            assetList.push({ type: assetType, id: lineage.target_key });
          }
        }
      });
    }

    return assetList;
  }, [agentBuilder, lineagesData]);

  /**
   * 빌더 편집 권한 체크
   * @param isReadOnly - 읽기 전용 모드 여부
   * @returns 권한이 없으면 true, 있으면 false
   */
  const checkBuilderEditPermission = (isReadOnly: boolean): boolean => {
    if (isReadOnly) {
      return false; // 읽기 전용 모드는 권한 체크 불필요
    }

    const raw = agentBuilder as any;
    if (Number(raw?.lstPrjSeq) === -999 && Number(user.activeProject.prjSeq) !== -999 && Number(user.activeProject.prjSeq) !== Number(raw?.fstPrjSeq)) {
      openAlert({
        title: '안내',
        message: '빌더 편집에 대한 권한이 없습니다.',
        confirmText: '확인',
      });
      return true; // 권한 없음
    }

    return false; // 권한 있음
  };

  const handleBuilderCanvas = (isReadOnly: boolean) => {
    // 프론트 버튼 권한으로 안막히는 케이스 처리
    if (checkBuilderEditPermission(isReadOnly)) {
      return;
    }

    // 빌더 캔버스 진입 전 데이터 초기화 ★ 제거 시 노드 누적됨
    setNodes([]);
    setEdges([]);
    setAgent(undefined);
    setKeyTable([]);
    setMessages([]);

    if (agentBuilder?.id) {
      navigate(`/agent/builder/graph`, {
        state: {
          isReadOnly: isReadOnly, // 조회/편집 모드 상태 전달
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
    }
  };

  const handleDelete = async () => {
    if (isDeployed !== AGENT_BUILDER_DEPLOY_STATUS.NOT_DEPLOYED) {
      // 배포된 에이전트는 삭제 불가
      await openAlert({
        title: '안내',
        message: '배포된 에이전트는 삭제할 수 없습니다.',
      });
      return;
    }

    if (checkBuilderEditPermission(false)) {
      return;
    }

    // 배포되지 않은 에이전트 삭제 확인
    const isConfirmed = await openConfirm({
      title: '안내',
      message: '삭제하시겠어요?\n삭제한 정보는 복구할 수 없습니다.',
      confirmText: '예',
      cancelText: '아니요',
    });

    if (!isConfirmed) return;

    await deleteAgentBuilderMutation.mutateAsync({ graphUuid: agentId! });

    await openAlert({
      title: '완료',
      message: `빌더가 삭제되었습니다.`,
      confirmText: '확인',
      onConfirm: () => {
        navigate('/agent/builder');
      },
    });
  };

  const handleAgentBuilderEditPopup = () => {
    // 프론트 버튼 권한으로 안막히는 케이스 처리
    if (checkBuilderEditPermission(false)) {
      return;
    }
    layerPopupOne.onOpen();
  };

  const handlePhoenix = async () => {
    if (!agentBuilder?.id) {
      openAlert({
        title: '안내',
        message: '에이전트 ID가 없습니다.',
        confirmText: '확인',
      });
      return;
    }

    try {
      const phoenixProjectInfo = await getPhoenixProjectId(agentBuilder.id);
      const phoenixProjectId = phoenixProjectInfo?.projectId || (agentBuilder as any)?.phoenixProjectId;

      let phoenixUrl: string = phoenixProjectInfo?.phoenixUrl || '';

      if (!phoenixUrl) {
        const phoenixBaseUrl = env.VITE_PHOENIX_BASE_URL;
        const baseUrlWithoutProjects = phoenixBaseUrl.replace(/\/projects\/?$/, '').replace(/\/$/, '');

        if (phoenixProjectId && typeof phoenixProjectId === 'string') {
          phoenixUrl = `${baseUrlWithoutProjects}/projects/${phoenixProjectId}/spans`;
        } else {
          phoenixUrl = `${baseUrlWithoutProjects}/projects`;
        }
      }

      const newWindow = window.open(phoenixUrl, '_blank', 'noopener,noreferrer');
      if (newWindow) {
        newWindow.focus();
      } else {
        openAlert({
          title: '안내',
          message: '팝업이 차단되었습니다. 브라우저 설정에서 팝업을 허용해주세요.',
          confirmText: '확인',
        });
      }
    } catch (error) {
      openAlert({
        title: '오류',
        message: 'Phoenix를 열 수 없습니다.',
        confirmText: '확인',
      });
    }
  };

  return (
    <>
      {/* 섹션 페이지 */}
      <section className='section-page'>
        <UIPageHeader
          title='빌더 조회'
          description=''
          actions={
            <UIGroup gap={8} direction='row' align='start'>
              {env.VITE_RUN_MODE !== RUN_MODE_TYPES.PROD ? (
                <Button auth={AUTH_KEY.AGENT.BUILDER_UPDATE} className='btn-tertiary-outline line-only-blue' onClick={() => handleBuilderCanvas(false)}>
                  빌더캔버스 편집
                </Button>
              ) : (
                <Button className='btn-tertiary-outline line-only-blue' onClick={() => handlePhoenix()}>
                  <span className='text-gray-500'>Phoenix</span>
                </Button>
              )}
              <Button className='btn-tertiary-outline line-only-blue' onClick={() => handleBuilderCanvas(true)}>
                빌더캔버스 조회
              </Button>
            </UIGroup>
          }
        />

        {/* 페이지 바디 */}
        <UIPageBody>
          <UIArticle>
            <div className='article-header'>
              <UIUnitGroup direction='row' align='space-between' gap={0}>
                <UITypography variant='title-4' className='secondary-neutral-900 text-sb'>
                  에이전트 정보
                </UITypography>
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
                  <tbody>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          이름
                        </UITypography>
                      </th>
                      <td colSpan={3}>
                        <UITypography variant='body-2' className='secondary-neutral-600 break-words'>
                          {agentBuilder?.name || ''}
                        </UITypography>
                      </td>
                    </tr>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          설명
                        </UITypography>
                      </th>
                      <td colSpan={3}>
                        <UITypography variant='body-2' className='secondary-neutral-600 whitespace-pre-wrap break-words'>
                          {agentBuilder?.description || ''}
                        </UITypography>
                      </td>
                    </tr>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          배포여부
                        </UITypography>
                      </th>
                      <td colSpan={3}>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {isDeployed}
                        </UITypography>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
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
                            <UIDataCnt count={modelRowData.all.length} prefix='사용 모델 총' />
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
                            <UIDataCnt count={knowledgeRowData.all.length} prefix='사용 지식 총' />
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

          {/* 담당자 정보 섹션 */}
          <ManagerInfoBox
            type='uuid'
            people={[
              { userId: agentBuilder?.createdBy || '', datetime: agentBuilder?.createdAt || '' },
              { userId: agentBuilder?.updatedBy || '', datetime: agentBuilder?.updatedAt || '' },
            ]}
          />

          {agentBuilder?.id && assets.length > 0 && <ProjectInfoBox assets={assets} auth={AUTH_KEY.AGENT.BUILDER_CHANGE_PUBLIC} />}
        </UIPageBody>

        {/* 페이지 footer */}
        <UIPageFooter>
          <UIArticle>
            <UIUnitGroup gap={8} direction='row' align='center'>
              <Button auth={AUTH_KEY.AGENT.BUILDER_DELETE} className='btn-primary-gray' onClick={handleDelete} disabled={isDeployed !== AGENT_BUILDER_DEPLOY_STATUS.NOT_DEPLOYED}>
                삭제
              </Button>
              <Button auth={AUTH_KEY.AGENT.BUILDER_UPDATE} className='btn-primary-blue' onClick={handleAgentBuilderEditPopup}>
                수정
              </Button>
            </UIUnitGroup>
          </UIArticle>
        </UIPageFooter>
      </section>
      <AgentBuilderEditPopupPage
        agentId={agentBuilder?.id || ''}
        agentName={agentBuilder?.name || ''}
        agentDescription={agentBuilder?.description || ''}
        isOpen={layerPopupOne.currentStep > 0}
        onClose={layerPopupOne.onClose}
        onUpdateSuccess={() => {
          refetchAgentBuilder();
        }}
      />
    </>
  );
}
