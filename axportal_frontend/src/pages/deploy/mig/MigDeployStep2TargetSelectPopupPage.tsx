import { Button } from '@/components/common/auth';
import { UIDataCnt, UIIcon2, UILabel, UITextLabel, UITypography, type UILabelIntent } from '@/components/UI/atoms';
import { UIPagination } from '@/components/UI/atoms/UIPagination';
import { UIArticle, UIGroup, UIInput, UIList, UIPopupBody, UIPopupFooter, UIPopupHeader, UIStepper, UIUnitGroup, type UIStepperItem } from '@/components/UI/molecules';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { AGENT_DEPLOY_STATUS } from '@/constants/deploy/agentDeploy.constants';
import { MODEL_DEPLOY_STATUS } from '@/constants/deploy/modelDeploy.constants';
import { useGetVectorDBList } from '@/services/data/tool/dataToolVectorDB.services';
import { useGetAgentAppList } from '@/services/deploy/agent/agentDeploy.services';
import { useAssetValidation } from '@/services/deploy/mig/mig.services';
import { useGetModelDeployList } from '@/services/deploy/model/modelDeploy.services';
import { useGetSafetyFilterList } from '@/services/deploy/safetyFilter/safetyFilter.service';
import { useGetExternalRepos } from '@/services/knowledge/knowledge.services';
import { useGetGuardRailList } from '@/services/prompt/guardRail/guardRail.services';
import { useUser } from '@/stores/auth/useUser';
import { useModal } from '@/stores/common/modal/useModal';
import { MIG_DEPLOY_CATEGORY_MAP } from '@/stores/deploy/types';
import { useMigDeploy } from '@/stores/deploy/useMigDeploy';
import dateUtils from '@/utils/common/date.utils';
import React, { useEffect, useMemo, useState } from 'react';

/**
 * 금지어 객체 배열을 쉼표로 구분된 문자열로 변환
 *
 * AG Grid에서 객체 배열을 직접 렌더링하면 React 에러가 발생하므로,
 * 미리 문자열로 변환하여 안전하게 표시
 *
 * @param stopWords - 금지어 객체 배열 (예: [{ id: 'uuid', stopWord: '@GMAIL.COM' }])
 * @returns 쉼표로 구분된 금지어 문자열 (예: '@GMAIL.COM, @NAVER.COM')
 */
const stringifyStopWords = (stopWords?: { id?: string; stopWord?: string }[]) => {
  if (!Array.isArray(stopWords)) return '';

  return stopWords
    .map(item => (typeof item?.stopWord === 'string' ? item.stopWord : ''))
    .filter(Boolean)
    .join(', ');
};

interface MigDeployStep2TargetSelectPopupPageProps {
  isOpen: boolean;
  stepperItems: UIStepperItem[];
  onClose: () => void;
  onPreviousStep: () => void;
  onNextStep: () => void;
}

interface MigDeployValidationBodyProps {
  iconClassName?: string;
  message: string;
}

function MigDeployValidationBody({ iconClassName = 'ic-system-56-check', message }: MigDeployValidationBodyProps) {
  return (
    <section className='section-modal'>
      <UIArticle>
        <UIGroup gap={16} direction='column' vAlign='center'>
          <UIIcon2 className={iconClassName} />
          <UITypography variant='body-1' className='secondary-neutral-600 text-center'>
            {message}
          </UITypography>
        </UIGroup>
      </UIArticle>
    </section>
  );
}

export function MigDeployStep2TargetSelectPopupPage({ isOpen, stepperItems = [], onClose, onPreviousStep, onNextStep }: MigDeployStep2TargetSelectPopupPageProps) {
  const { openConfirm, openAlert, openModal } = useModal();
  const { user } = useUser();

  const { migDeployData, updateMigDeployData, resetMigDeployData } = useMigDeploy();

  // Asset Validation mutation
  const { mutate: validateAsset } = useAssetValidation({
    onSuccess: response => {
      // response.data === true이면 정합성 일치 (모달 표시)
      if (response.data === true) {
        if (migDeployData.category === 'SAFETY_FILTER' || migDeployData.category === 'GUARDRAILS' || migDeployData.category === 'PROJECT') {
          // FILTER, GUARDRAILS, PROJECT는 validation 성공 시 바로 Step4로 이동 (Step3 건너뛰기)
          openModal({
            type: '2xsmall',
            title: '안내',
            body: <MigDeployValidationBody message='정합성 검증 결과 생성된 파일과 포탈 형상이 일치합니다. 운영 배포를 위한 최종 파일을 생성할게요.' />,
            confirmText: '예',
            onConfirm: () => {
              // Step3을 건너뛰고 Step4로 이동
              onNextStep(); // Step3으로 이동
              onNextStep(); // Step4로 이동
            },
          });
        } else {
          // 다른 카테고리는 Step3으로 이동
          openModal({
            type: '2xsmall',
            title: '안내',
            body: <MigDeployValidationBody message='정합성 검증 결과, 생성된 파일과 포탈 형상이 일치합니다. 이어서 추가정보를 입력해주세요.' />,
            confirmText: '예',
            onConfirm: () => {
              onNextStep();
            },
          });
        }
      } else {
        openModal({
          type: '2xsmall',
          title: '안내',
          body: (
            <MigDeployValidationBody
              iconClassName='ic-system-56-feedback'
              message={`정합성 검증 결과, 생성된 파일과 일부 포탈 형상이 일치하지 않습니다. 포탈에서 파일을 다시 생성 후 재시도해주세요.`}
            />
          ),
          confirmText: '확인',
          onConfirm: () => {
            onClose();
          },
        });
      }
    },
  });

  // 페이지네이션 상태
  const [page, setPage] = useState(1);
  const [size] = useState(10);
  const [searchKeyword, setSearchKeyword] = useState(''); // 입력 중인 검색어
  const [searchQuery, setSearchQuery] = useState(''); // 실제 검색에 사용되는 검색어

  // category에 따라 조건부로 API 호출
  const {
    data: safetyFilterData,
    refetch: refetchSafetyFilter,
    isLoading: isLoadingSafetyFilter,
  } = useGetSafetyFilterList(
    {
      page,
      size,
      search: searchQuery || undefined,
      sort: 'created_at,desc',
    },
    {
      enabled: isOpen && migDeployData.category === 'SAFETY_FILTER',
    }
  );

  const {
    data: knowledgeData,
    refetch: refetchKnowledge,
    isLoading: isLoadingKnowledge,
  } = useGetExternalRepos(
    {
      page,
      size,
      search: searchQuery || undefined,
      sort: 'updated_at,desc' as any,
      filter: 'is_active:true',
    },
    {
      enabled: isOpen && migDeployData.category === 'KNOWLEDGE',
    }
  );

  const {
    data: modelDeployData,
    refetch: refetchModelDeploy,
    isFetching: isLoadingModelDeploy,
  } = useGetModelDeployList(
    {
      page: page - 1,
      size,
      search: searchQuery || undefined,
      sort: 'created_at,desc',
      filter: 'status:Available|status:Stopped',
    },
    {
      enabled: isOpen && migDeployData.category === 'SERVING_MODEL',
    }
  );

  const {
    data: agentData,
    refetch: refetchAgent,
    isLoading: isLoadingAgent,
  } = useGetAgentAppList(
    {
      page,
      size,
      search: searchQuery || undefined,
      sort: 'created_at,desc',
      targetType: 'all',
      filter: 'deployment_status!=Failed',
    } as any,
    {
      enabled: isOpen && migDeployData.category === 'AGENT_APP',
    }
  );

  const {
    data: vectorDBData,
    refetch: refetchVectorDB,
    isLoading: isLoadingVectorDB,
  } = useGetVectorDBList(
    {
      page,
      size,
      search: searchQuery || undefined,
      sort: 'created_at,desc',
    },
    {
      enabled: isOpen && migDeployData.category === 'VECTOR_DB',
    }
  );

  const {
    data: guardRailData,
    refetch: refetchGuardRail,
    isLoading: isLoadingGuardRail,
  } = useGetGuardRailList(
    {
      page,
      size,
      search: searchQuery || undefined,
      sort: 'created_at,desc',
      project_id: '24ba585a-02fc-43d8-b9f1-f7ca9e020fe5', // TODO
    },
    {
      enabled: isOpen && migDeployData.category === 'GUARDRAILS',
    }
  );

  // GRID 로딩 상태
  const isGridLoading = useMemo(() => {
    return isLoadingSafetyFilter || isLoadingKnowledge || isLoadingModelDeploy || isLoadingAgent || isLoadingVectorDB || isLoadingGuardRail;
  }, [isLoadingSafetyFilter, isLoadingKnowledge, isLoadingModelDeploy, isLoadingAgent, isLoadingVectorDB, isLoadingGuardRail]);

  // category 변경 시 페이지 및 검색어 리셋
  useEffect(() => {
    if (isOpen) {
      setPage(1);
      setSearchKeyword('');
      setSearchQuery('');

      // PROJECT 카테고리인 경우 현재 프로젝트를 자동 선택
      if (migDeployData.category === 'PROJECT' && user?.activeProject) {
        updateMigDeployData({
          uuidList: [user.activeProject.prjUuid || ''],
          prjSeq: user.activeProject.prjSeq || '',
          prjNm: user.activeProject.prjNm || '',
          name: user.activeProject.prjNm || '',
        });
      }
    }
  }, [migDeployData.category, isOpen]);

  // page나 searchQuery 변경 시 API refetch
  useEffect(() => {
    if (!isOpen) return;

    if (migDeployData.category === 'SAFETY_FILTER') {
      refetchSafetyFilter();
    } else if (migDeployData.category === 'KNOWLEDGE') {
      refetchKnowledge();
    } else if (migDeployData.category === 'SERVING_MODEL') {
      refetchModelDeploy();
    } else if (migDeployData.category === 'AGENT_APP') {
      refetchAgent();
    } else if (migDeployData.category === 'VECTOR_DB') {
      refetchVectorDB();
    } else if (migDeployData.category === 'GUARDRAILS') {
      refetchGuardRail();
    }
  }, [page, searchQuery, isOpen, migDeployData.category, refetchSafetyFilter, refetchKnowledge, refetchModelDeploy, refetchAgent, refetchVectorDB, refetchGuardRail]);

  // API 응답 데이터를 그리드 형식으로 변환
  const safetyFilterRowData = useMemo(() => {
    if (!safetyFilterData?.content) return [];
    return safetyFilterData.content.map((item: any) => ({
      ...item,
      id: item.filterGroupId || item.id,
      stopWordsText: stringifyStopWords(item.stopWords),
    }));
  }, [safetyFilterData, page, size]);

  const knowledgeRowData = useMemo(() => {
    if (!knowledgeData?.data) return [];
    return knowledgeData.data.map((item: any) => ({
      id: item.id,
      name: item.name || '',
      status: item.is_active ? '활성화' : '비활성화',
      description: item.description || '',
      publicStatus: item.public_status || item.publicStatus || '',
      vectorDb: item.vector_db_name || '',
      modelName: item.embedding_model_name || '',
      indexName: item.index_name || '',
      createdDate: item.created_at ? dateUtils.formatDate(item.created_at, 'datetime') : '',
      modifiedDate: item.updated_at ? dateUtils.formatDate(item.updated_at, 'datetime') : '',
    }));
  }, [knowledgeData, page, size]);

  const modelDeployRowData = useMemo(() => {
    if (!modelDeployData?.content) return [];
    // Stopped 상태 제외 필터링
    return modelDeployData.content
      .filter((item: any) => item.status !== 'Stopped')
      .map((item: any) => ({
        id: item.servingId || item.id,
        name: item.name || '',
        modelName: item.modelName || '',
        status: item.status || '',
        description: item.description || '',
        type: item.type || '',
        servingType: item.servingType || '',
        publicStatus: item.publicStatus || '',
        createdAt: item.createdAt ? dateUtils.formatDate(item.createdAt, 'datetime') : '',
        updatedAt: item.updatedAt ? dateUtils.formatDate(item.updatedAt, 'datetime') : dateUtils.formatDate(item.createdAt, 'datetime'),
      }));
  }, [modelDeployData, page, size]);

  const agentRowData = useMemo(() => {
    if (!agentData?.content) return [];
    return agentData.content.map((item: any) => ({
      id: item.appId || item.id,
      name: item.name || '',
      builderName: item.builderName || '',
      deploymentStatus:
        item.deploymentStatus ||
        (item.deployments && Array.isArray(item.deployments) && item.deployments.length > 0
          ? [...item.deployments].sort((a: any, b: any) => (b.version || 0) - (a.version || 0))[0]?.status
          : ''),
      description: item.description || '',
      deploymentVersion:
        item.deploymentVersion ||
        (item.deployments && Array.isArray(item.deployments) && item.deployments.length > 0
          ? [...item.deployments].sort((a: any, b: any) => (b.version || 0) - (a.version || 0))[0]?.version
          : null),
      isMigration: item.isMigration || false,
      publicStatus: item.publicStatus || '',
      createdAt: item.createdAt || '',
      updatedAt: item.updatedAt || item.createdAt || '',
    }));
  }, [agentData, page, size]);

  const vectorDBRowData = useMemo(() => {
    if (!vectorDBData?.content) return [];
    return vectorDBData.content.map((item: any) => ({
      id: item.id,
      name: item.name || '',
      type: item.type || '',
      createdAt: item.createdAt ? dateUtils.formatDate(item.createdAt, 'datetime') : '',
      updatedAt: item.updatedAt ? dateUtils.formatDate(item.updatedAt, 'datetime') : '',
    }));
  }, [vectorDBData, page, size]);

  const guardRailRowData = useMemo(() => {
    if (!guardRailData?.content) return [];
    return guardRailData.content.map((item: any) => {
      let createdAt = '';
      let updatedAt = '';

      try {
        if (item.createdAt) {
          createdAt = dateUtils.formatDate(item.createdAt, 'datetime');
        }
      } catch (e) {
        createdAt = item.createdAt || '';
      }

      try {
        if (item.updatedAt) {
          updatedAt = dateUtils.formatDate(item.updatedAt, 'datetime');
        } else if (item.createdAt) {
          updatedAt = dateUtils.formatDate(item.createdAt, 'datetime');
        }
      } catch (e) {
        updatedAt = item.updatedAt || item.createdAt || '';
      }

      return {
        id: item.uuid,
        name: item.name || '',
        description: item.description || '',
        isPublicAsset: item.isPublicAsset || false,
        publicStatus: item.isPublicAsset ? '전체공유' : '내부공유',
        createdAt,
        updatedAt,
      };
    });
  }, [guardRailData, page, size]);

  // 총 페이지 수 계산
  const totalPages = useMemo(() => {
    if (migDeployData.category === 'SAFETY_FILTER') {
      return safetyFilterData?.totalPages || 1;
    } else if (migDeployData.category === 'KNOWLEDGE') {
      return knowledgeData?.payload?.pagination?.last_page || 1;
    } else if (migDeployData.category === 'SERVING_MODEL') {
      return modelDeployData?.totalPages || 1;
    } else if (migDeployData.category === 'AGENT_APP') {
      return agentData?.totalPages || 1;
    } else if (migDeployData.category === 'VECTOR_DB') {
      return vectorDBData?.totalPages || 1;
    } else if (migDeployData.category === 'GUARDRAILS') {
      return guardRailData?.totalPages || 1;
    }
    return 1;
  }, [migDeployData.category, safetyFilterData, knowledgeData, modelDeployData, agentData, vectorDBData, guardRailData]);

  // 총 개수 계산
  const totalCount = useMemo(() => {
    if (migDeployData.category === 'SAFETY_FILTER') {
      return safetyFilterData?.totalElements || 0;
    } else if (migDeployData.category === 'KNOWLEDGE') {
      return knowledgeData?.payload?.pagination?.total || 0;
    } else if (migDeployData.category === 'SERVING_MODEL') {
      return modelDeployData?.totalElements || 0;
    } else if (migDeployData.category === 'AGENT_APP') {
      return agentData?.totalElements || 0;
    } else if (migDeployData.category === 'VECTOR_DB') {
      return vectorDBData?.totalElements || 0;
    } else if (migDeployData.category === 'GUARDRAILS') {
      return guardRailData?.totalElements || 0;
    }
    return 0;
  }, [migDeployData.category, safetyFilterData, knowledgeData, modelDeployData, agentData, vectorDBData, guardRailData]);

  // hasNext 계산 - 각 API 응답에서 직접 가져오기 (없으면 page < totalPages로 계산)
  const hasNext = useMemo(() => {
    let apiHasNext: boolean | undefined;
    
    if (migDeployData.category === 'SAFETY_FILTER') {
      apiHasNext = (safetyFilterData as any)?.hasNext;
    } else if (migDeployData.category === 'KNOWLEDGE') {
      apiHasNext = (knowledgeData as any)?.hasNext;
    } else if (migDeployData.category === 'SERVING_MODEL') {
      apiHasNext = (modelDeployData as any)?.hasNext;
    } else if (migDeployData.category === 'AGENT_APP') {
      apiHasNext = (agentData as any)?.hasNext;
    } else if (migDeployData.category === 'VECTOR_DB') {
      apiHasNext = (vectorDBData as any)?.hasNext;
    } else if (migDeployData.category === 'GUARDRAILS') {
      apiHasNext = (guardRailData as any)?.hasNext;
    }
    
    // API 응답에 hasNext가 있으면 사용, 없으면 계산
    return apiHasNext !== undefined ? apiHasNext : page < totalPages;
  }, [migDeployData.category, safetyFilterData, knowledgeData, modelDeployData, agentData, vectorDBData, guardRailData, page, totalPages]);

  const handleClose = () => {
    resetMigDeployData();
    onClose();
  };

  const handleCancel = () => {
    openConfirm({
      title: '안내',
      message: '화면을 나가시겠어요?\n입력한 정보가 저장되지 않을 수 있습니다.',
      confirmText: '예',
      cancelText: '아니요',
      onConfirm: () => {
        handleClose();
      },
      onCancel: () => { },
    });
  };

  const handleNextStep = () => {
    if (!migDeployData.uuidList || migDeployData.uuidList.length === 0) {
      openAlert({
        title: '안내',
        message: '선택된 항목이 없습니다.',
      });
      return;
    }

    if (!migDeployData.prjSeq) {
      openAlert({
        title: '안내',
        message: '프로젝트 ID가 없습니다.',
      });
      return;
    }

    // 선택된 항목을 rowData에서 찾기
    const selectedUuid = migDeployData.uuidList[0];
    const category = migDeployData.category;
    let selectedItem: any = null;

    if (category === 'SAFETY_FILTER') {
      selectedItem = safetyFilterRowData.find((item: any) => item.id === selectedUuid);
    } else if (category === 'KNOWLEDGE') {
      selectedItem = knowledgeRowData.find((item: any) => item.id === selectedUuid);
    } else if (category === 'SERVING_MODEL') {
      selectedItem = modelDeployRowData.find((item: any) => item.id === selectedUuid);
    } else if (category === 'AGENT_APP') {
      selectedItem = agentRowData.find((item: any) => item.id === selectedUuid);
    } else if (category === 'VECTOR_DB') {
      selectedItem = vectorDBRowData.find((item: any) => item.id === selectedUuid);
    } else if (category === 'GUARDRAILS') {
      selectedItem = guardRailRowData.find((item: any) => item.id === selectedUuid);
    }

    // 공개범위 검증: false면 return
    if (selectedItem) {
      const isValid = validatePublicStatus(selectedItem, category);
      if (!isValid) {
        return;
      }
    }

    validateAsset({
      uuid: migDeployData.uuidList[0] as string,
      project_id: migDeployData.prjSeq as string,
      type: migDeployData.category as string,
    });
  };

  const handlePreviousStep = () => {
    onPreviousStep();
  };

  const handleSearch = () => {
    setSearchQuery(searchKeyword); // 실제 검색어 업데이트
    setPage(1); // 첫 페이지로 리셋
  };

  const handlePageChange = (newPage: number) => {
    setPage(newPage);
  };

  // 공개범위 검증 함수
  const validatePublicStatus = (item: any, category: string): boolean => {
    const message = '전체 공유된 항목은 운영 배포 할 수 없습니다. 해당 항목 제외 후 다시 시도해주세요.';
    
    // prjSeq가 음수가 아니면 검증하지 않음
    if (!migDeployData.prjSeq || (typeof migDeployData.prjSeq === 'number' && migDeployData.prjSeq >= 0)) {
      return true;
    }

    // 공개 프로젝트에 있는게 아니면 공개 항목은 운영 배포 할 수 없음
    const isOnPublicProject = user?.activeProject?.prjSeq === '-999';
    if (!isOnPublicProject) {
      // SAFETY_FILTER와 GUARDRAILS는 isPublicAsset 필드 사용
      if (category === 'SAFETY_FILTER' || category === 'GUARDRAILS') {
        if (item.isPublicAsset === true) {
          openAlert({
            title: '안내',
            message: message,
            confirmText: '확인',
          });
          return false;
        }
      } else {
        // 다른 카테고리는 publicStatus 필드 사용
        if (item.publicStatus === '전체공유') {
          openAlert({
            title: '안내',
            message: message,
            confirmText: '확인',
          });
          return false;
        }
      }
    }

    return true;
  };

  // 그리드 컬럼 정의
  const filterColumnDefs: any = useMemo(
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
        valueGetter: (params: any) => (page - 1) * size + params.node.rowIndex + 1,
      },
      {
        headerName: '분류',
        field: 'filterGroupName' as any,
        width: 272,
        cellStyle: {
          paddingLeft: '16px',
        },
        cellRenderer: React.memo((params: any) => {
          return (
            <div
              style={{
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                whiteSpace: 'nowrap',
              }}
            >
              {params.value}
            </div>
          );
        }),
      },
      {
        headerName: '금지어',
        field: 'stopWordsText',
        flex: 1,
        cellStyle: {
          paddingLeft: '16px',
        },
        cellRenderer: React.memo((params: any) => {
          return (
            <div
              style={{
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                whiteSpace: 'nowrap',
              }}
            >
              {params.value}
            </div>
          );
        }),
      },
      {
        headerName: '공개범위',
        field: 'scope' as any,
        width: 120,
        cellStyle: {
          paddingLeft: '16px',
        },
        valueFormatter: (params: any) => {
          return params.data.isPublicAsset ? '전체공유' : '내부공유';
        },
      },
      {
        headerName: '생성일시',
        field: 'createdAt' as any,
        width: 180,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: '최종 수정일시',
        field: 'updatedAt' as any,
        width: 180,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
    ],
    [safetyFilterRowData, page, size]
  );

  // 그리드 컬럼 정의
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
        valueGetter: (params: any) => (page - 1) * size + params.node.rowIndex + 1,
      },
      {
        headerName: '이름',
        field: 'name' as any,
        width: 272,
        cellRenderer: React.memo((params: any) => {
          return (
            <div
              style={{
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                whiteSpace: 'nowrap',
              }}
            >
              {params.value}
            </div>
          );
        }),
      },
      {
        headerName: '상태',
        field: 'status',
        width: 120,
        cellRenderer: (params: any) => {
          const colorMap: { [key: string]: string } = {
            활성화: 'complete',
            비활성화: 'error',
          };
          return (
            <UILabel variant='badge' intent={colorMap[params.value] as any}>
              {params.value}
            </UILabel>
          );
        },
      },
      {
        headerName: '설명',
        field: 'description',
        minWidth: 272,
        flex: 1,
        showTooltip: true,
        cellRenderer: React.memo((params: any) => {
          return (
            <div
              style={{
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                whiteSpace: 'nowrap',
              }}
            >
              {params.value}
            </div>
          );
        }),
      },
      {
        headerName: '공개범위',
        field: 'publicStatus',
        width: 120,
      },
      {
        headerName: '벡터DB',
        field: 'vectorDb',
        width: 272,
        cellRenderer: React.memo((params: any) => {
          return (
            <div
              style={{
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                whiteSpace: 'nowrap',
              }}
            >
              {params.value}
            </div>
          );
        }),
      },
      {
        headerName: '임베딩 모델',
        field: 'modelName',
        width: 172,
        cellRenderer: React.memo((params: any) => {
          return (
            <div
              style={{
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                whiteSpace: 'nowrap',
              }}
            >
              {params.value}
            </div>
          );
        }),
      },
      {
        headerName: '인덱스명',
        field: 'indexName',
        width: 172,
        cellRenderer: React.memo((params: any) => {
          return (
            <div
              style={{
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                whiteSpace: 'nowrap',
              }}
            >
              {params.value}
            </div>
          );
        }),
      },
      {
        headerName: '생성일시',
        field: 'createdDate' as any,
        width: 180,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: '최종 수정일시',
        field: 'modifiedDate' as any,
        width: 180,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
    ],
    [knowledgeRowData, page, size]
  );

  // 그리드 컬럼 정의
  const modelDeployColumnDefs: any = useMemo(
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
          textAlign: 'center' as const,
          display: 'flex' as const,
          alignItems: 'center' as const,
          justifyContent: 'center' as const,
        },
        sortable: false,
        suppressHeaderMenuButton: true,
        suppressSizeToFit: true,
        valueGetter: (params: any) => (page - 1) * size + params.node.rowIndex + 1,
      },
      {
        headerName: '배포명',
        field: 'name',
        width: 272,
        minWidth: 272,
        maxWidth: 272,
        suppressSizeToFit: true,
      },
      {
        headerName: '모델명',
        field: 'modelName',
        width: 272,
      },
      {
        headerName: '상태',
        field: 'status',
        width: 120,
        cellRenderer: React.memo((params: any) => {
          const statusConfig = MODEL_DEPLOY_STATUS[params.value as keyof typeof MODEL_DEPLOY_STATUS];
          return (
            <UILabel variant='badge' intent={(statusConfig?.intent as UILabelIntent) || 'gray'}>
              {statusConfig?.label || params.value}
            </UILabel>
          );
        }),
      },
      {
        headerName: '설명',
        field: 'description',
        minWidth: 472,
        flex: 1,
        showTooltip: true,
      },
      {
        headerName: '모델유형',
        field: 'type',
        width: 120,
      },
      {
        headerName: '배포유형',
        field: 'servingType',
        width: 120,
      },
      {
        headerName: '공개범위',
        field: 'publicStatus',
        width: 120,
      },
      {
        headerName: '생성일시',
        field: 'createdAt',
        width: 180,
        cellStyle: {
          paddingLeft: 16,
        },
      },
      {
        headerName: '최종 수정일시',
        field: 'updatedAt',
        width: 180,
        cellStyle: {
          paddingLeft: 16,
        },
      },
    ],
    [page, size]
  );

  // 그리드 컬럼 정의
  const agentColumnDefs: any = useMemo(
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
          textAlign: 'center' as const,
          display: 'flex' as const,
          alignItems: 'center' as const,
          justifyContent: 'center' as const,
        },
        sortable: false,
        suppressHeaderMenuButton: true,
        suppressSizeToFit: true,
        valueGetter: (params: any) => (page - 1) * size + params.node.rowIndex + 1,
      },
      {
        headerName: '배포명',
        field: 'name',
        width: 272,
        minWidth: 272,
        maxWidth: 272,
        suppressSizeToFit: true,
        cellRenderer: React.memo((params: any) => {
          return (
            <div
              style={{
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                whiteSpace: 'nowrap',
              }}
            >
              {params.value}
            </div>
          );
        }),
      },
      {
        headerName: '빌더명',
        field: 'builderName',
        width: 272,
        cellRenderer: React.memo((params: any) => {
          return (
            <div
              style={{
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                whiteSpace: 'nowrap',
              }}
            >
              {params.value}
            </div>
          );
        }),
      },
      {
        headerName: '상태',
        field: 'deploymentStatus',
        width: 120,
        valueGetter: (params: any) => {
          if (params.data?.deploymentStatus) {
            return params.data.deploymentStatus;
          }
          if (params.data?.deployments && Array.isArray(params.data.deployments) && params.data.deployments.length > 0) {
            const sortedDeployments = [...params.data.deployments].sort((a: any, b: any) => (b.version || 0) - (a.version || 0));
            return sortedDeployments[0]?.status || '';
          }
          return '';
        },
        cellRenderer: React.memo((params: any) => {
          const statusValue = params.value || params.data?.deploymentStatus;
          const statusConfig = statusValue ? AGENT_DEPLOY_STATUS[statusValue as keyof typeof AGENT_DEPLOY_STATUS] : null;
          return (
            <UILabel variant='badge' intent={(statusConfig?.intent as UILabelIntent) || 'gray'}>
              {statusConfig?.label || statusValue || ''}
            </UILabel>
          );
        }),
      },
      {
        headerName: '설명',
        field: 'description',
        minWidth: 392,
        flex: 1,
        showTooltip: true,
        cellRenderer: React.memo((params: any) => {
          return (
            <div
              style={{
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                whiteSpace: 'nowrap',
              }}
            >
              {params.value}
            </div>
          );
        }),
      },
      {
        headerName: '버전',
        field: 'deploymentVersion',
        width: 120,
        valueGetter: (params: any) => {
          return params.data.deploymentVersion ? `ver.${params.data.deploymentVersion}` : '';
        },
      },
      {
        headerName: '운영 배포 여부',
        field: 'isMigration',
        width: 120,
        valueGetter: (params: any) => {
          return params.data.isMigration ? '배포' : '미배포';
        },
      },
      {
        headerName: '공개범위',
        field: 'publicStatus',
        width: 120,
      },
      {
        headerName: '생성일시',
        field: 'createdAt',
        width: 180,
        cellStyle: {
          paddingLeft: 16,
        },
        valueGetter: (params: any) => {
          return params.data.createdAt ? dateUtils.formatDate(params.data.createdAt, 'datetime') : '';
        },
      },
      {
        headerName: '최종 수정일시',
        field: 'updatedAt',
        width: 180,
        cellStyle: {
          paddingLeft: 16,
        },
        valueGetter: (params: any) => {
          return params.data.updatedAt
            ? dateUtils.formatDate(params.data.updatedAt, 'datetime')
            : params.data.createdAt
              ? dateUtils.formatDate(params.data.createdAt, 'datetime')
              : '';
        },
      },
    ],
    [page, size]
  );

  // 그리드 컬럼 정의 - VECTOR_DB
  const vectorDBColumnDefs: any = useMemo(
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
          textAlign: 'center' as const,
          display: 'flex' as const,
          alignItems: 'center' as const,
          justifyContent: 'center' as const,
        },
        sortable: false,
        suppressHeaderMenuButton: true,
        suppressSizeToFit: true,
        valueGetter: (params: any) => (page - 1) * size + params.node.rowIndex + 1,
      },
      {
        headerName: '이름',
        field: 'name',
        flex: 1,
        suppressSizeToFit: true,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: '유형',
        field: 'type',
        width: 180,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: '기본설정',
        field: 'defaultConfig',
        minWidth: 120,
        cellRenderer: (params: any) => {
          return (
            <div className='flex gap-1 flex-wrap'>
              <UITextLabel intent={params.data.isDefault ? 'blue' : 'gray'}>{params.data.isDefault?.toString() || 'false'}</UITextLabel>
            </div>
          );
        },
      },
      {
        headerName: '생성일시',
        field: 'createdAt',
        width: 180,
        cellStyle: {
          paddingLeft: 16,
        },
      },
      {
        headerName: '최종 수정일시',
        field: 'updatedAt',
        width: 180,
        cellStyle: {
          paddingLeft: 16,
        },
      },
    ],
    [page, size]
  );

  // 그리드 컬럼 정의 - GUARDRAIL
  const guardRailColumnDefs: any = useMemo(
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
          textAlign: 'center' as const,
          display: 'flex' as const,
          alignItems: 'center' as const,
          justifyContent: 'center' as const,
        },
        sortable: false,
        suppressHeaderMenuButton: true,
        suppressSizeToFit: true,
        valueGetter: (params: any) => (page - 1) * size + params.node.rowIndex + 1,
      },
      {
        headerName: '이름',
        field: 'name',
        width: 272,
        cellRenderer: React.memo((params: any) => {
          return (
            <div
              style={{
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                whiteSpace: 'nowrap',
              }}
            >
              {params.value}
            </div>
          );
        }),
      },
      {
        headerName: '설명',
        field: 'description',
        minWidth: 392,
        flex: 1,
        showTooltip: true,
        cellRenderer: React.memo((params: any) => {
          return (
            <div
              style={{
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                whiteSpace: 'nowrap',
              }}
            >
              {params.value}
            </div>
          );
        }),
      },
      {
        headerName: '공개범위',
        field: 'publicStatus',
        width: 120,
      },
      {
        headerName: '생성일시',
        field: 'createdAt',
        width: 180,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: '최종 수정일시',
        field: 'updatedAt',
        width: 180,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
    ],
    [page, size]
  );

  return (
    <>
      {/* DesignLayout 위에 높은 z-index로 뜨는 UILayerPopup */}
      <UILayerPopup
        isOpen={isOpen}
        onClose={handleClose}
        size='fullscreen'
        showOverlay={true}
        leftContent={
          /* 좌측 Step 영역 콘텐츠 */
          <UIPopupAside>
            {/* 레이어 팝업 헤더 */}
            <UIPopupHeader title='운영 이행' position='left' />
            {/* 레이어 팝업 바디 */}
            <UIPopupBody>
              <UIArticle>
                <UIArticle>
                  <UIStepper items={migDeployData.category === 'PROJECT' || migDeployData.category === 'SAFETY_FILTER' || migDeployData.category === 'GUARDRAILS' ? [stepperItems[0], stepperItems[1], { ...stepperItems[3], step: 3 }] : stepperItems} currentStep={2} direction='vertical' />
                </UIArticle>
              </UIArticle>
            </UIPopupBody>
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <Button className='btn-tertiary-gray' style={{ width: 80 }} onClick={handleCancel}>
                    취소
                  </Button>
                  <Button className='btn-tertiary-blue' style={{ width: 80 }} disabled={true}>
                    이행
                  </Button>
                </UIUnitGroup>
              </UIArticle>
            </UIPopupFooter>
          </UIPopupAside>
        }
      >
        {/* 우측 Contents 영역 콘텐츠 - 기존 컴포넌트 사용 */}
        <section className='section-popup-content'>
          {/* 레이어 팝업 헤더 */}
          <UIPopupHeader
            title='이행 대상 선택'
            description='대상을 선택 후 다음 버튼을 누르면, 자동으로 이행을 위한 사전 검증 파일 생성 및 정합성 검증이 진행됩니다.'
            position='right'
          />

          {/* 레이어 팝업 바디 */}
          <UIPopupBody>
            <UIArticle>
              <div className='box-fill'>
                <UIUnitGroup gap={8} direction='column' align='start'>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '0 6px' }}>
                    <UIIcon2 className='ic-system-16-info-gray' />
                    <UITypography variant='body-2' className='secondary-neutral-600 text-sb'>
                      정합성 검증은 운영 이행을 위해 생성된 사전 검증 파일이 포탈 형상과 동일한지 확인하는 절차를 의미합니다.
                    </UITypography>
                  </div>
                  <div style={{ paddingLeft: '22px' }}>
                    <UIUnitGroup gap={8} direction='column' align='start'>
                      <UIList
                        gap={4}
                        direction='column'
                        className='ui-list_dash'
                        data={[
                          {
                            dataItem: (
                              <UITypography variant='body-2' className='secondary-neutral-600'>
                                {`검증 결과 ‘일치'인 경우, 해당 파일을 기반으로 이행용 최종 파일을 생성합니다.`}
                              </UITypography>
                            ),
                          },
                        ]}
                      />
                      <UIList
                        gap={4}
                        direction='column'
                        className='ui-list_dash'
                        data={[
                          {
                            dataItem: (
                              <UITypography variant='body-2' className='secondary-neutral-600'>
                                {`검증 결과 ‘불일치'인 경우, 해당 파일로 이행은 불가하며 포탈에서 파일을 다시 생성 후 재시도해주세요.`}
                              </UITypography>
                            ),
                          },
                        ]}
                      />
                    </UIUnitGroup>
                  </div>
                </UIUnitGroup>
              </div>
            </UIArticle>

            <UIArticle>
              <div className='article-header'>
                <UITypography variant='title-4' className='secondary-neutral-900'>
                  이행 정보
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
                            프로젝트명
                          </UITypography>
                        </th>
                        <td colSpan={3}>
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            {migDeployData.prjNm || user.activeProject?.prjNm || ''}
                          </UITypography>
                        </td>
                      </tr>
                      <tr>
                        <th>
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            분류
                          </UITypography>
                        </th>
                        <td colSpan={3}>
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            {MIG_DEPLOY_CATEGORY_MAP[migDeployData.category] || '-'}
                          </UITypography>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>
            </UIArticle>

            {migDeployData.category === 'SAFETY_FILTER' && (
              <UIArticle className='article-grid'>
                <UIListContainer>
                  <UIListContentBox.Header>
                    <div className='flex-shrink-0'>
                      <UIGroup gap={8} direction='row' align='start'>
                        <div style={{ width: '168px', paddingRight: '8px' }}>
                          <UIDataCnt count={totalCount} prefix='세이프티 필터 총' unit='건' />
                        </div>
                      </UIGroup>
                    </div>
                    <div className='flex items-center gap-2'>
                      <div className='w-[360px]'>
                        <UIInput.Search
                          placeholder='분류, 금지어 입력'
                          value={searchKeyword}
                          onChange={e => setSearchKeyword(e.target.value)}
                          onKeyDown={e => {
                            if (e.key === 'Enter') {
                              handleSearch();
                            }
                          }}
                        />
                      </div>
                    </div>
                  </UIListContentBox.Header>
                  <UIListContentBox.Body>
                    <UIGrid
                      type='single-select'
                      loading={isGridLoading}
                      rowData={safetyFilterRowData}
                      columnDefs={filterColumnDefs}
                      selectedDataList={safetyFilterRowData.filter((item: any) => migDeployData.uuidList?.includes(item.id))}
                      onCheck={(selectedItems: any[]) => {
                        if (selectedItems.length === 0) return;

                        const uuidList = selectedItems.map((item: any) => (typeof item === 'string' ? item : item?.id || String(item)));
                        const selectedItem = selectedItems[0];
                        const nameValue = selectedItem?.filterGroupName || selectedItem?.name || selectedItem?.data?.filterGroupName || selectedItem?.data?.name;

                        updateMigDeployData({
                          uuidList,
                          name: typeof nameValue === 'string' ? nameValue : nameValue?.name || String(nameValue || ''),
                        });
                      }}
                    />
                  </UIListContentBox.Body>
                  <UIListContentBox.Footer>
                    <UIPagination currentPage={page} hasNext={hasNext} totalPages={totalPages} onPageChange={handlePageChange} className='flex justify-center' />
                  </UIListContentBox.Footer>
                </UIListContainer>
              </UIArticle>
            )}

            {migDeployData.category === 'KNOWLEDGE' && (
              <UIArticle className='article-grid'>
                <UIListContainer>
                  <UIListContentBox.Header>
                    <div className='flex-shrink-0'>
                      <UIGroup gap={8} direction='row' align='start'>
                        <div style={{ width: '168px', paddingRight: '8px' }}>
                          <UIDataCnt count={totalCount} prefix='지식 총' unit='건' />
                        </div>
                      </UIGroup>
                    </div>
                    <div className='flex items-center gap-2'>
                      <div className='w-[360px]'>
                        <UIInput.Search
                          placeholder='이름, 설명 입력'
                          value={searchKeyword}
                          onChange={e => setSearchKeyword(e.target.value)}
                          onKeyDown={e => {
                            if (e.key === 'Enter') {
                              handleSearch();
                            }
                          }}
                        />
                      </div>
                    </div>
                  </UIListContentBox.Header>
                  <UIListContentBox.Body>
                    <UIGrid
                      type='single-select'
                      loading={isGridLoading}
                      rowData={knowledgeRowData}
                      columnDefs={knowledgeColumnDefs}
                      selectedDataList={knowledgeRowData.filter((item: any) => migDeployData.uuidList?.includes(item.id))}
                      onClickRow={(params: any) => {
                        if (params.data) {

                          const id = typeof params.data.id === 'string' ? params.data.id : params.data.id?.id || String(params.data.id);
                          const nameValue = params.data.name;
                          updateMigDeployData({
                            name: typeof nameValue === 'string' ? nameValue : nameValue?.name || String(nameValue || ''),
                            uuidList: [id],
                          });
                        }
                      }}
                      onCheck={(selectedItems: any[]) => {
                        if (selectedItems.length === 0) return;

                        const selectedItem = selectedItems[0];
                        const id = typeof selectedItem === 'string' ? selectedItem : selectedItem?.id || selectedItem?.data?.id || String(selectedItem);
                        const nameValue = selectedItem?.name || selectedItem?.data?.name;

                        updateMigDeployData({
                          name: typeof nameValue === 'string' ? nameValue : nameValue?.name || String(nameValue || ''),
                          uuidList: [id],
                        });
                      }}
                    />
                  </UIListContentBox.Body>
                  <UIListContentBox.Footer>
                    <UIPagination currentPage={page} hasNext={hasNext} totalPages={totalPages} onPageChange={handlePageChange} className='flex justify-center' />
                  </UIListContentBox.Footer>
                </UIListContainer>
              </UIArticle>
            )}

            {migDeployData.category === 'SERVING_MODEL' && (
              <UIArticle className='article-grid'>
                <UIListContainer>
                  <UIListContentBox.Header>
                    <div className='flex-shrink-0'>
                      <UIGroup gap={8} direction='row' align='start'>
                        <div style={{ width: '168px', paddingRight: '8px' }}>
                          <UIDataCnt count={totalCount} prefix='배포 모델 총' unit='건' />
                        </div>
                      </UIGroup>
                    </div>
                    <div className='flex items-center gap-2'>
                      <div className='w-[360px]'>
                        <UIInput.Search
                          placeholder='배포명 입력'
                          value={searchKeyword}
                          onChange={e => setSearchKeyword(e.target.value)}
                          onKeyDown={e => {
                            if (e.key === 'Enter') {
                              handleSearch();
                            }
                          }}
                        />
                      </div>
                    </div>
                  </UIListContentBox.Header>
                  <UIListContentBox.Body>
                    <UIGrid
                      type='single-select'
                      loading={isGridLoading}
                      rowData={modelDeployRowData}
                      columnDefs={modelDeployColumnDefs}
                      selectedDataList={modelDeployRowData.filter((item: any) => migDeployData.uuidList?.includes(item.id))}
                      onClickRow={(params: any) => {
                        if (params.data) {
                          const id = typeof params.data.id === 'string' ? params.data.id : params.data.id?.id || String(params.data.id);
                          const nameValue = params.data.name;
                          updateMigDeployData({
                            name: typeof nameValue === 'string' ? nameValue : nameValue?.name || String(nameValue || ''),
                            uuidList: [id],
                          });
                        }
                      }}
                      onCheck={(selectedItems: any[]) => {
                        // selectedItems는 전체 row 객체 배열 (single-select는 최대 1개)
                        if (selectedItems && selectedItems.length > 0) {
                          const selectedItem = selectedItems[0];
                          const id = typeof selectedItem === 'string' ? selectedItem : selectedItem?.id || selectedItem?.data?.id || String(selectedItem);
                          const nameValue = selectedItem?.name || selectedItem?.data?.name;
                          updateMigDeployData({
                            name: typeof nameValue === 'string' ? nameValue : nameValue?.name || String(nameValue || ''),
                            uuidList: [id],
                          });
                        }
                      }}
                    />
                  </UIListContentBox.Body>
                  <UIListContentBox.Footer>
                    <UIPagination currentPage={page} hasNext={hasNext} totalPages={totalPages} onPageChange={handlePageChange} className='flex justify-center' />
                  </UIListContentBox.Footer>
                </UIListContainer>
              </UIArticle>
            )}

            {migDeployData.category === 'AGENT_APP' && (
              <UIArticle className='article-grid'>
                <UIListContainer>
                  <UIListContentBox.Header>
                    <div className='flex-shrink-0'>
                      <UIGroup gap={8} direction='row' align='start'>
                        <div style={{ width: '168px', paddingRight: '8px' }}>
                          <UIDataCnt count={totalCount} prefix='배포 에이전트 총' unit='건' />
                        </div>
                      </UIGroup>
                    </div>
                    <div className='flex items-center gap-2'>
                      <div className='w-[360px]'>
                        <UIInput.Search
                          placeholder='배포명 입력'
                          value={searchKeyword}
                          onChange={e => setSearchKeyword(e.target.value)}
                          onKeyDown={e => {
                            if (e.key === 'Enter') {
                              handleSearch();
                            }
                          }}
                        />
                      </div>
                    </div>
                  </UIListContentBox.Header>
                  <UIListContentBox.Body>
                    <UIGrid
                      type='single-select'
                      loading={isGridLoading}
                      rowData={agentRowData}
                      columnDefs={agentColumnDefs}
                      selectedDataList={agentRowData.filter((item: any) => migDeployData.uuidList?.includes(item.id))}
                      onClickRow={(params: any) => {
                        if (params.data) {
                          const id = typeof params.data.id === 'string' ? params.data.id : params.data.id?.id || String(params.data.id);
                          const nameValue = params.data.name;
                          updateMigDeployData({
                            name: typeof nameValue === 'string' ? nameValue : nameValue?.name || String(nameValue || ''),
                            uuidList: [id],
                          });
                        }
                      }}
                      onCheck={(selectedItems: any[]) => {
                        // selectedItems는 전체 row 객체 배열 (single-select는 최대 1개)
                        if (selectedItems && selectedItems.length > 0) {
                          const selectedItem = selectedItems[0];
                          const id = typeof selectedItem === 'string' ? selectedItem : selectedItem?.id || selectedItem?.data?.id || String(selectedItem);
                          const nameValue = selectedItem?.name || selectedItem?.data?.name;
                          updateMigDeployData({
                            name: typeof nameValue === 'string' ? nameValue : nameValue?.name || String(nameValue || ''),
                            uuidList: [id],
                          });
                        }
                      }}
                    />
                  </UIListContentBox.Body>
                  <UIListContentBox.Footer>
                    <UIPagination currentPage={page} hasNext={hasNext} totalPages={totalPages} onPageChange={handlePageChange} className='flex justify-center' />
                  </UIListContentBox.Footer>
                </UIListContainer>
              </UIArticle>
            )}

            {migDeployData.category === 'VECTOR_DB' && (
              <UIArticle className='article-grid'>
                <UIListContainer>
                  <UIListContentBox.Header>
                    <div className='flex-shrink-0'>
                      <UIGroup gap={8} direction='row' align='start'>
                        <div style={{ width: '168px', paddingRight: '8px' }}>
                          <UIDataCnt count={totalCount} prefix='벡터 DB 총' unit='건' />
                        </div>
                      </UIGroup>
                    </div>
                    <div className='flex items-center gap-2'>
                      <div className='w-[360px]'>
                        <UIInput.Search
                          placeholder='이름 입력'
                          value={searchKeyword}
                          onChange={e => setSearchKeyword(e.target.value)}
                          onKeyDown={e => {
                            if (e.key === 'Enter') {
                              handleSearch();
                            }
                          }}
                        />
                      </div>
                    </div>
                  </UIListContentBox.Header>
                  <UIListContentBox.Body>
                    <UIGrid
                      type='single-select'
                      loading={isGridLoading}
                      rowData={vectorDBRowData}
                      columnDefs={vectorDBColumnDefs}
                      selectedDataList={vectorDBRowData.filter((item: any) => migDeployData.uuidList?.includes(item.id))}
                      onClickRow={(params: any) => {
                        // 벡터 DB 권한 체크
                        const isAuthorized = user?.functionAuthList?.includes('A020302');
                        if (!isAuthorized) {
                          openAlert({
                            title: '안내',
                            message: '권한이 없습니다. \n 벡터DB 상세는 포탈관리자만 조회할 수 있습니다.',
                            confirmText: '확인',
                          });
                          return;
                        }

                        if (params.data) {
                          const id = typeof params.data.id === 'string' ? params.data.id : params.data.id?.id || String(params.data.id);
                          const nameValue = params.data.name;
                          updateMigDeployData({
                            name: typeof nameValue === 'string' ? nameValue : nameValue?.name || String(nameValue || ''),
                            uuidList: [id],
                          });
                        }
                      }}
                      onCheck={(selectedItems: any[]) => {
                        // 벡터 DB 권한 체크
                        const isAuthorized = user?.functionAuthList?.includes('A020302');
                        if (!isAuthorized) {
                          openAlert({
                            title: '안내',
                            message: '권한이 없습니다. \n 벡터DB 상세는 포탈관리자만 조회할 수 있습니다.',
                            confirmText: '확인',
                          });
                          return;
                        }

                        // selectedItems는 전체 row 객체 배열 (single-select는 최대 1개)
                        if (selectedItems && selectedItems.length > 0) {
                          const selectedItem = selectedItems[0];
                          const id = typeof selectedItem === 'string' ? selectedItem : selectedItem?.id || selectedItem?.data?.id || String(selectedItem);
                          const nameValue = selectedItem?.name || selectedItem?.data?.name;
                          updateMigDeployData({
                            name: typeof nameValue === 'string' ? nameValue : nameValue?.name || String(nameValue || ''),
                            uuidList: [id],
                          });
                        }
                      }}
                    />
                  </UIListContentBox.Body>
                  <UIListContentBox.Footer>
                    <UIPagination currentPage={page} hasNext={hasNext} totalPages={totalPages} onPageChange={handlePageChange} className='flex justify-center' />
                  </UIListContentBox.Footer>
                </UIListContainer>
              </UIArticle>
            )}

            {migDeployData.category === 'GUARDRAILS' && (
              <UIArticle className='article-grid'>
                <UIListContainer>
                  <UIListContentBox.Header>
                    <div className='flex-shrink-0'>
                      <UIGroup gap={8} direction='row' align='start'>
                        <div style={{ width: '168px', paddingRight: '8px' }}>
                          <UIDataCnt count={totalCount} prefix='가드레일 총' unit='건' />
                        </div>
                      </UIGroup>
                    </div>
                    <div className='flex items-center gap-2'>
                      <div className='w-[360px]'>
                        <UIInput.Search
                          placeholder='이름, 설명 입력'
                          value={searchKeyword}
                          onChange={e => setSearchKeyword(e.target.value)}
                          onKeyDown={e => {
                            if (e.key === 'Enter') {
                              handleSearch();
                            }
                          }}
                        />
                      </div>
                    </div>
                  </UIListContentBox.Header>
                  <UIListContentBox.Body>
                    <UIGrid
                      type='single-select'
                      loading={isGridLoading}
                      rowData={guardRailRowData}
                      columnDefs={guardRailColumnDefs}
                      selectedDataList={guardRailRowData.filter((item: any) => migDeployData.uuidList?.includes(item.id))}
                      onClickRow={(params: any) => {
                        if (params.data) {
                          const id = typeof params.data.id === 'string' ? params.data.id : params.data.id?.id || String(params.data.id);
                          const nameValue = params.data.name;
                          updateMigDeployData({
                            name: typeof nameValue === 'string' ? nameValue : nameValue?.name || String(nameValue || ''),
                            uuidList: [id],
                          });
                        }
                      }}
                      onCheck={(selectedItems: any[]) => {
                        if (selectedItems.length === 0) return;

                        const selectedItem = selectedItems[0];
                        const id = typeof selectedItem === 'string' ? selectedItem : selectedItem?.id || selectedItem?.data?.id || String(selectedItem);
                        const nameValue = selectedItem?.name || selectedItem?.data?.name;

                        updateMigDeployData({
                          name: typeof nameValue === 'string' ? nameValue : nameValue?.name || String(nameValue || ''),
                          uuidList: [id],
                        });
                      }}
                    />
                  </UIListContentBox.Body>
                  <UIListContentBox.Footer>
                    <UIPagination currentPage={page} hasNext={hasNext} totalPages={totalPages} onPageChange={handlePageChange} className='flex justify-center' />
                  </UIListContentBox.Footer>
                </UIListContainer>
              </UIArticle>
            )}

            {migDeployData.category === 'PROJECT' && (
              <UIArticle>
                <div className='article-header'>
                  <UITypography variant='title-4' className='secondary-neutral-900'>
                    이행 대상 프로젝트
                  </UITypography>
                </div>
                <div className='article-body'>
                  <div className='box-fill'>
                    <UIUnitGroup gap={8} direction='column' align='start'>
                      <div style={{ display: 'flex', alignItems: 'center', gap: '0 6px' }}>
                        <UIIcon2 className='ic-system-16-info-gray' />
                        <UITypography variant='body-2' className='secondary-neutral-600 text-sb'>
                          현재 위치한 프로젝트가 자동으로 선택됩니다.
                        </UITypography>
                      </div>
                    </UIUnitGroup>
                  </div>
                  <div className='border-t border-black mt-4'>
                    <table className='tbl-v'>
                      <colgroup>
                        <col style={{ width: '15%' }} />
                        <col style={{ width: '85%' }} />
                      </colgroup>
                      <tbody>
                        <tr>
                          <th>
                            <UITypography variant='body-2' className='secondary-neutral-900'>
                              프로젝트명
                            </UITypography>
                          </th>
                          <td>
                            <UITypography variant='body-2' className='secondary-neutral-600'>
                              {user?.activeProject?.prjNm || '-'}
                            </UITypography>
                          </td>
                        </tr>
                        <tr>
                          <th>
                            <UITypography variant='body-2' className='secondary-neutral-900'>
                              프로젝트 ID
                            </UITypography>
                          </th>
                          <td>
                            <UITypography variant='body-2' className='secondary-neutral-600'>
                              {user?.activeProject?.prjSeq || '-'}
                            </UITypography>
                          </td>
                        </tr>
                      </tbody>
                    </table>
                  </div>
                </div>
              </UIArticle>
            )}
          </UIPopupBody>
          {/* 레이어 팝업 footer */}
          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <Button className='btn-secondary-gray' onClick={handlePreviousStep}>
                  이전
                </Button>
                <Button className='btn-secondary-blue' onClick={handleNextStep}>
                  다음
                </Button>
              </UIUnitGroup>
            </UIArticle>
          </UIPopupFooter>
        </section>
      </UILayerPopup>
    </>
  );
}
