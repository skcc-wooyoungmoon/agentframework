import React, { useCallback, useEffect, useState } from 'react';

import { Button } from '@/components/common/auth';
import { UIDataCnt, UIPagination } from '@/components/UI';
import { UIArticle, UIInput, UIPopupBody, UIPopupFooter, UIPopupHeader, UIStepper, UIUnitGroup, type UIStepperItem } from '@/components/UI/molecules';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { AUTH_KEY } from '@/constants/auth/auth.constants';
import { useGetAgentBuilders, useGetAgentDeployInfo } from '@/services/agent/builder/agentBuilder.services';
import type { AgentBuilderRes } from '@/services/agent/builder/types';
import { useUser } from '@/stores/auth';
import { useModal } from '@/stores/common/modal';
import { useDeployAgent } from '@/stores/deploy/useDeployAgent';
import { dateUtils } from '@/utils/common';
import type { ColDef } from 'ag-grid-community';

interface DeployAgentStep1AgentSelectPopupPageProps {
  isOpen: boolean;
  stepperItems: UIStepperItem[];
  onClose: () => void;
  onNextStep: () => void;
}

interface SearchValues {
  page: number;
  size: number;
  searchKeyword: string;
}

export function DeployAgentStep1AgentSelectPopupPage({ isOpen, stepperItems = [], onClose, onNextStep }: DeployAgentStep1AgentSelectPopupPageProps) {
  const { openConfirm } = useModal();
  const { user } = useUser();

  const { deployData, updateDeployData, resetDeployData } = useDeployAgent();
  const [selectedAgentBuilder, setSelectedAgentBuilder] = useState<string>('');
  const { data: graphAppInfo, refetch: refetchGraphAppInfo } = useGetAgentDeployInfo(selectedAgentBuilder, { enabled: isOpen && !!selectedAgentBuilder });

  // 체크박스 상태 관리
  const [selectedDataList, setSelectedDataList] = useState<AgentBuilderRes[]>([]);
  const handleSelect = useCallback((datas: AgentBuilderRes[]) => {
    setSelectedDataList(datas);
    if (datas.length > 0) {
      setSelectedAgentBuilder(datas[0].id);
    } else {
      setSelectedAgentBuilder('');
    }
  }, []);

  // 팝업이 열릴 때 deployData에서 선택 값 복원
  useEffect(() => {
    if (isOpen && deployData.targetId) {
      setSelectedAgentBuilder(deployData.targetId);
    } else if (isOpen && !deployData.targetId) {
      // targetId가 없을 때만 초기화
      setSelectedAgentBuilder('');
      setSelectedDataList([]);
    }
  }, [isOpen, deployData.targetId]);

  const [page, setPage] = useState(1);

  const [searchValues, setSearchValues] = useState<SearchValues>({
    page: 1,
    size: 10,
    searchKeyword: '',
  });

  const [appliedSearchValues, setAppliedSearchValues] = useState<SearchValues>({
    page: page,
    size: 10,
    searchKeyword: searchValues.searchKeyword,
  });

  const {
    data: agentBuilders,
    isSuccess,
    refetch: refetchAgentBuilders,
  } = useGetAgentBuilders({
    project_id: user?.adxpProject?.prjUuid,
    page: appliedSearchValues.page,
    size: appliedSearchValues.size,
    sort: 'createdAt,desc',
    search: appliedSearchValues.searchKeyword || undefined,
  });

  // agentBuilders 데이터가 로드되고 deployData.targetId가 있으면 선택 값 복원
  useEffect(() => {
    if (isOpen && agentBuilders?.content && deployData.targetId && selectedDataList.length === 0) {
      const matchedBuilder = agentBuilders.content.find((builder: AgentBuilderRes) => builder.id === deployData.targetId);
      if (matchedBuilder) {
        setSelectedDataList([matchedBuilder]);
      }
    }
  }, [isOpen, agentBuilders?.content, deployData.targetId, selectedDataList.length]);

  useEffect(() => {
    setAppliedSearchValues(prev => ({
      ...prev,
      page: page,
      searchKeyword: searchValues.searchKeyword,
    }));
  }, [page, searchValues.searchKeyword]);

  // 총 페이지 (API 기준)
  const totalPages = isSuccess ? agentBuilders?.totalPages || 1 : 1;

  const handleSearch = () => {
    setAppliedSearchValues(searchValues);
    setPage(1);
  };

  // 페이지네이션 핸들러 수정
  const handlePageChange = (newPage: number) => {
    setPage(newPage);
    setAppliedSearchValues(prev => ({
      ...prev,
      page: newPage,
    }));
    refetchAgentBuilders();
  };

  /**
   * 검색 키 다운
   */
  const handleSearchKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter') {
      handleSearch();
    }
  };

  /**
   * 닫기 버튼 클릭
   */
  const handleClose = () => {
    // 선택 값 초기화
    setSelectedAgentBuilder('');
    setSelectedDataList([]);
    // deployData도 초기화 (팝업을 완전히 닫을 때만)
    resetDeployData();
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
      onCancel: () => {},
    });
  };

  const columnDefs: any = React.useMemo(
    () => [
      {
        headerName: 'NO',
        field: 'no' as const,
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
        },
        sortable: false,
        suppressHeaderMenuButton: true,
        suppressSizeToFit: true,
        valueGetter: (params: any) => (appliedSearchValues.page - 1) * appliedSearchValues.size + params.node.rowIndex + 1,
      },
      {
        headerName: '이름',
        field: 'name' as const,
        width: 240,
        cellStyle: { paddingLeft: '16px' },
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
        field: 'description' as const,
        flex: 1,
        cellStyle: { paddingLeft: '16px' },
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
        headerName: '최초 생성일',
        field: 'createdAt',
        width: 180,
        valueGetter: (params: any) => {
          return dateUtils.formatDate(params.data.createdAt, 'datetime');
        },
      },
      {
        headerName: '최종 수정일',
        field: 'updatedAt',
        width: 180,
        valueGetter: (params: any) => {
          return dateUtils.formatDate(params.data.updatedAt, 'datetime') || dateUtils.formatDate(params.data.createdAt, 'datetime');
        },
      },
    ],
    [appliedSearchValues.page, appliedSearchValues.size, selectedAgentBuilder]
  );

  const handleGetGraphAppInfo = async () => {
    const result = await refetchGraphAppInfo();
    if (result.data) {
      handleNextStep();
    }
  };

  /**
   * 다음 버튼 클릭 핸들러
   */
  const handleNextStep = () => {
    if (selectedAgentBuilder) {
      const updateData = {
        targetId: selectedAgentBuilder,
        targetType: 'agent_graph' as const,
        name: graphAppInfo?.name || '',
        description: graphAppInfo?.description || '',
      };

      updateDeployData(updateData);

      onNextStep();
    }
  };

  return (
    <>
      <UILayerPopup
        isOpen={isOpen}
        onClose={handleClose}
        size='fullscreen'
        showOverlay={true}
        leftContent={
          /* 좌측 Step 영역 콘텐츠 */
          <UIPopupAside>
            {/* 레이어 팝업 헤더 */}
            <UIPopupHeader title='에이전트 배포하기' position='left' />
            {/* 레이어 팝업 바디 */}
            <UIPopupBody>
              <UIArticle>
                <UIStepper currentStep={1} items={stepperItems} direction='vertical' />
              </UIArticle>
            </UIPopupBody>
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <Button className='btn-tertiary-gray' style={{ width: 80 }} onClick={handleCancel}>
                    취소
                  </Button>
                  <Button auth={AUTH_KEY.DEPLOY.AGENT_DEPLOY_CREATE} className='btn-tertiary-blue' style={{ width: 80 }} disabled={true}>
                    배포하기
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
          <UIPopupHeader title='에이전트 선택' description='배포할 에이전트를 선택해 주세요.' position='right' />

          {/* 레이어 팝업 바디 */}
          <UIPopupBody>
            <UIArticle className='article-grid'>
              <UIListContainer>
                <UIListContentBox.Header>
                  <div className='flex-shrink-0'>
                    <div style={{ width: '168px', paddingRight: '8px' }}>
                      <UIDataCnt count={agentBuilders?.totalElements || 0} prefix='총' unit='건' />
                    </div>
                  </div>
                  <div className='flex-shrink-0'>
                    <div className='w-[360px]'>
                      <UIInput.Search
                        value={searchValues.searchKeyword}
                        onChange={e => setSearchValues(prev => ({ ...prev, searchKeyword: e.target.value }))}
                        placeholder='이름 입력'
                        onKeyDown={handleSearchKeyDown}
                      />
                    </div>
                  </div>
                </UIListContentBox.Header>
                <UIListContentBox.Body>
                  <UIGrid<any>
                    type='single-select'
                    rowData={agentBuilders?.content || []}
                    columnDefs={columnDefs as ColDef<AgentBuilderRes, any>[]}
                    selectedDataList={selectedDataList}
                    onCheck={handleSelect}
                  />
                </UIListContentBox.Body>
                <UIListContentBox.Footer>
                  <UIPagination currentPage={page} totalPages={totalPages} onPageChange={handlePageChange} className='flex justify-center' />
                </UIListContentBox.Footer>
              </UIListContainer>
            </UIArticle>
          </UIPopupBody>
          {/* 레이어 팝업 footer */}
          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <Button className='btn-secondary-blue' onClick={handleGetGraphAppInfo} disabled={!selectedAgentBuilder}>
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
