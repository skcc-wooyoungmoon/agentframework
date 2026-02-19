import { agentAtom, edgesAtom, keyTableAtom, nodesAtom } from '@/components/builder/atoms/AgentAtom';
import { messagesAtom } from '@/components/builder/atoms/messagesAtom';
import { Button } from '@/components/common/auth';
import { UIBox, UIDataCnt, UIPagination, UIToggle, UITypography } from '@/components/UI/atoms';
import type { UIStepperItem } from '@/components/UI/molecules';
import { UIArticle, UIDropdown, UIGroup, UIInput, UIPageBody, UIPageHeader, UIUnitGroup } from '@/components/UI/molecules';
import { UICardList } from '@/components/UI/molecules/card/UICardList';
import { UIGridCard } from '@/components/UI/molecules/card/UIGridCard';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { AUTH_KEY } from '@/constants/auth/auth.constants';
import { STORAGE_KEYS } from '@/constants/common/storage.constants';
import { useLayerPopup } from '@/hooks/common/layer';
import { useBackRestoredState } from '@/hooks/common/navigation';
import { AgentBuilderProvider } from '@/providers/agent/AgentBuilderProvider';
import { useDeleteAgentBuilder, useGetAgentBuilders } from '@/services/agent/builder2/agentBuilder.services';
import type { AgentBuilderRes } from '@/services/agent/builder2/types';
import { useModal } from '@/stores/common/modal';
import dateUtils from '@/utils/common/date.utils';
import { useSetAtom } from 'jotai';
import { memo, useCallback, useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';

import { env } from '@/constants/common/env.constants';
import { AgentBuilderEditPopupPage, AgentStep1TmplSelectPopupPage, AgentStep2BaseInfoInputPopupPage } from '.';

interface SearchValues {
  page: number;
  size: number;
  searchKeyword: string;
  view: string;
}

export function AgentBuilderListPage() {
  const navigate = useNavigate();
  const layerEditPopup = useLayerPopup();
  const { openAlert, openConfirm } = useModal();

  // Atoms 초기화를 위한 setter
  const setNodes = useSetAtom(nodesAtom);
  const setEdges = useSetAtom(edgesAtom);
  const setAgent = useSetAtom(agentAtom);
  const setKeyTable = useSetAtom(keyTableAtom);
  const setMessages = useSetAtom(messagesAtom);

  const deleteAgentBuilderMutation = useDeleteAgentBuilder();

  // 팝업 step 상태 (로컬 관리)
  const [currentStep, setCurrentStep] = useState<number>(0);

  // 팝업 제어 함수들
  const handleOpenCreateAgent = async () => {
    // 새 에이전트 등록 시 이전 캔버스 상태 완전 초기화
    setNodes([]);
    setEdges([]);
    setAgent(undefined);
    setKeyTable([]);
    setMessages([]);

    setCurrentStep(1);
  };

  const handleCloseCreateAgent = () => {
    setCurrentStep(0);
  };

  const handleNextStep = () => {
    setCurrentStep(prev => prev + 1);
  };

  const handlePreviousStep = () => {
    setCurrentStep(prev => prev - 1);
  };

  // 검색 조건 (입력용)
  const { filters: searchValues, updateFilters: setSearchValues } = useBackRestoredState<SearchValues>(STORAGE_KEYS.SEARCH_VALUES.AGENT_BUILDER_LIST, {
    page: 1,
    size: 12,
    searchKeyword: '',
    view: 'grid',
  });

  const { data, isSuccess, refetch, isLoading, isFetching } = useGetAgentBuilders(
    {
      page: searchValues.page,
      size: searchValues.size,
      sort: 'created_at,desc',
      search: searchValues.searchKeyword,
    },
    {
      enabled: !env.VITE_NO_PRESSURE_MODE, // 조회 중에도 기존 데이터 유지
      placeholderData: previousData => previousData, // 조회 중에도 기존 데이터 유지
    }
  );

  const updatePageSizeAndRefetch = (patch: Partial<Pick<SearchValues, 'page' | 'size'>>) => {
    setSearchValues(prev => ({ ...prev, ...patch }));
    setTimeout(() => refetch(), 0);
  };

  const [dataList, setDataList] = useState<any[]>([]);

  // 수정 팝업 상태 관리
  const [selectedEditData, setSelectedEditData] = useState<{ appId: string; name: string; description: string } | null>(null);

  // 총 페이지 (API 기준)
  const totalPages = isSuccess ? data?.totalPages || 1 : 1;

  useEffect(() => {
    if (isSuccess && data) {
      setDataList(
        data.content?.map((item: AgentBuilderRes, index: number) => {
          return {
            ...item,
            graphUuid: item.id,
            no: (searchValues.page - 1) * searchValues.size + index + 1,
            description: item.description || '',
            createdAt: item.createdAt ? dateUtils.formatDate(item.createdAt, 'datetime') : '',
            updatedAt: item.updatedAt ? dateUtils.formatDate(item.updatedAt, 'datetime') : '',
          };
        }) || []
      );
    }
  }, [data, isSuccess, searchValues.page, searchValues.size]);

  const handlePageChange = (newPage: number) => {
    updatePageSizeAndRefetch({ page: newPage });
  };

  // 조회 버튼
  const handleSearch = () => {
    setSearchValues(prev => ({ ...prev, page: 1 }));
    refetch();
  };

  // 체크박스 선택 처리
  const [selectedDataList, setSelectedDataList] = useState<AgentBuilderRes[]>([]);
  const handleSelect = useCallback((datas: AgentBuilderRes[]) => {
    setSelectedDataList(datas);
  }, []);

  const handleDeleteConfirm = async (ids: string[]) => {
    if (ids.length === 0) {
      openAlert({
        title: '안내',
        message: '삭제할 항목을 선택해주세요.',
      });
      return;
    }

    // 선택한 에이전트 중 배포된 에이전트가 있는지 확인
    // deploymentStatus가 '개발배포'인 항목이 있는지 확인
    const deployedAgents = ids.filter(agentId => {
      const item = selectedDataList.find(d => d.id === agentId);
      return item?.deploymentStatus === '개발배포';
    });

    if (deployedAgents.length > 0) {
      // 배포된 에이전트가 포함되어 있으면 삭제 불가
      await openAlert({
        title: '안내',
        message: `선택한 항목 중 ${deployedAgents.length}개의 배포된 에이전트가 포함되어 있습니다.\n배포된 에이전트는 삭제할 수 없습니다.`,
      });
      return;
    }

    // 모두 배포되지 않은 에이전트인 경우에만 삭제 확인
    const isConfirmed = await openConfirm({
      title: '안내',
      message: '삭제하시겠어요?\n삭제한 정보는 복구할 수 없습니다.',
      confirmText: '예',
      cancelText: '아니요',
    });

    if (!isConfirmed) return;

    try {
      // 선택된 각 에이전트를 개별적으로 삭제
      const deletePromises = selectedDataList.map(agentId => deleteAgentBuilderMutation.mutateAsync({ graphUuid: agentId.id }));

      await Promise.all(deletePromises);

      await openAlert({
        title: '완료',
        message: '빌더가 삭제되었습니다.',
        confirmText: '확인',
        onConfirm: () => {
          refetch();
          setSelectedDataList([]);
        },
      });
    } catch (error: any) {
      const errorMessage = error?.response?.data?.message || error?.message || '일괄삭제에 실패했습니다.';
      await openAlert({
        title: '오류',
        message: errorMessage,
      });
    }
  };

  const handleEdit = (rowData: any) => {
    layerEditPopup.onOpen();
    setSelectedEditData({
      appId: rowData.id,
      name: rowData.name || '',
      description: rowData.description || '',
    });
  };

  const handleDelete = async (rowData: any) => {
    // 배포된 에이전트인지 확인 - deploymentStatus가 '개발배포'인지 확인
    if (rowData.deploymentStatus === '개발배포') {
      // 배포된 에이전트는 삭제 불가
      await openAlert({
        title: '안내',
        message: '배포된 에이전트는 삭제할 수 없습니다.',
      });
      return;
    }

    // 배포되지 않은 에이전트만 삭제 확인
    const isConfirmed = await openConfirm({
      title: '안내',
      message: '삭제하시겠어요?\n삭제한 정보는 복구할 수 없습니다.',
      confirmText: '예',
      cancelText: '아니요',
    });

    if (!isConfirmed) return;

    try {
      const response = await deleteAgentBuilderMutation.mutateAsync({ graphUuid: rowData.id });
      const isSuccess = response?.success ?? (response === undefined || response === null);
      if (!isSuccess) {
        throw new Error((response as any)?.message || '삭제에 실패했습니다.');
      }

      await openAlert({
        title: '완료',
        message: '빌더가 삭제되었습니다.',
        confirmText: '확인',
      });

      refetch();
      setSelectedDataList(prev => prev.filter(item => item.id !== rowData.id));
    } catch (error: any) {
      const errorMessage = error?.response?.data?.message || error?.message || '삭제에 실패했습니다.';
      await openAlert({
        title: '오류',
        message: errorMessage,
      });
    }
  };

  // 더보기 메뉴 설정
  const moreMenuConfig = useMemo(
    () => ({
      items: [
        {
          label: '수정',
          action: 'modify',
          auth: AUTH_KEY.AGENT.BUILDER_UPDATE,
          onClick: (rowData: any) => {
            handleEdit(rowData);
          },
        },
        {
          label: '삭제',
          action: 'delete',
          auth: AUTH_KEY.AGENT.BUILDER_DELETE,
          onClick: (rowData: any) => {
            handleDelete(rowData);
          },
        },
      ],
    }),
    []
  );

  // 그리드 컬럼 정의 - AG_010101 디자인
  const columnDefs: any = useMemo(
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
        },
        sortable: false,
        suppressHeaderMenuButton: true,
        suppressSizeToFit: true,
      },
      {
        headerName: '이름',
        field: 'name',
        width: 272,
        showTooltip: true,
        cellRenderer: memo((params: any) => {
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
        flex: 1,
        showTooltip: true,
        cellRenderer: memo((params: any) => {
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
        headerName: '배포여부',
        field: 'deploymentStatus',
        width: 120,
        valueGetter: (params: any) => params.data?.deploymentStatus ?? '',
      },
      {
        headerName: '공개범위',
        field: 'publicStatus',
        width: 120,
        valueGetter: (params: any) => {
          return params.data?.publicStatus ?? '';
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
      {
        headerName: '',
        field: 'more',
        width: 60,
        sortable: false,
        suppressHeaderMenuButton: true,
      },
    ],
    []
  );

  /**
   * 스테퍼 데이터
   */
  const stepperItems: UIStepperItem[] = [
    {
      id: 'step1',
      label: '템플릿 선택',
      step: 1,
    },
    {
      id: 'step2',
      label: '기본 정보 입력',
      step: 2,
    },
  ];

  return (
    <>
      <section className='section-page'>
        <UIPageHeader
          title='빌더'
          description={[
            '생성형 AI 모델을 활용한 나만의 AI Agent를 개발하고 개발망에 배포할 수 있습니다.',
            '개발한 AI Agent를 조회해 어떤 모델과 지식이 사용되었는지 확인해 보세요.',
          ]}
          actions={
            <Button
              auth={AUTH_KEY.AGENT.AGENT_CREATE}
              className='btn-text-18-semibold-point'
              leftIcon={{ className: 'ic-system-24-add', children: '' }}
              onClick={handleOpenCreateAgent}
            >
              에이전트 등록
            </Button>
          }
        />

        {/* 페이지 바디 */}
        <UIPageBody>
          {/* 아티클 필터 : 검색 영역 - AG_010101 디자인 */}
          <UIArticle className='article-filter'>
            <UIBox className='box-filter'>
              <UIGroup gap={40} direction='row'>
                <div style={{ width: 'calc(100% - 168px)' }}>
                  <table className='tbl_type_b'>
                    <tbody>
                      <tr>
                        <th>
                          <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                            검색
                          </UITypography>
                        </th>
                        <td colSpan={3}>
                          <UIUnitGroup gap={0} direction='row'>
                            <div style={{ width: '100%' }}>
                              <UIInput.Search
                                value={searchValues.searchKeyword}
                                placeholder='이름 입력'
                                maxLength={50}
                                onChange={e => setSearchValues(prev => ({ ...prev, searchKeyword: e.target.value }))}
                                onKeyDown={e => {
                                  if (e.key === 'Enter') {
                                    handleSearch();
                                  }
                                }}
                              />
                            </div>
                          </UIUnitGroup>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
                <div style={{ width: '128px' }}>
                  <Button className='btn-secondary-blue' onClick={handleSearch} style={{ width: '100%' }}>
                    조회
                  </Button>
                </div>
              </UIGroup>
            </UIBox>
          </UIArticle>

          {/* 아티클 그리드 - AG_010101 디자인 */}
          <UIArticle className='article-grid'>
            <UIListContainer>
              <UIListContentBox.Header>
                <div className='w-full'>
                  <UIUnitGroup gap={16} direction='column'>
                    <div className='flex justify-between w-full items-center'>
                      <div className='flex-shrink-0'>
                        <div style={{ width: '168px', paddingRight: '8px' }}>
                          <UIDataCnt count={data?.totalElements ?? 0} prefix='총' />
                        </div>
                      </div>
                      <div className='flex items-center gap-[8px]'>
                        <div style={{ width: '180px', flexShrink: 0 }}>
                          <UIDropdown
                            value={`${searchValues.size}개씩 보기`}
                            disabled={data?.totalElements === 0}
                            options={[
                              { value: '12', label: '12개씩 보기' },
                              { value: '36', label: '36개씩 보기' },
                              { value: '60', label: '60개씩 보기' },
                            ]}
                            onSelect={(value: string) => updatePageSizeAndRefetch({ size: Number(value), page: 1 })}
                            height={40}
                            variant='dataGroup'
                          />
                        </div>
                        <UIToggle
                          variant='dataView'
                          checked={searchValues.view === 'card'}
                          disabled={data?.totalElements === 0}
                          onChange={checked => setSearchValues(prev => ({ ...prev, view: checked ? 'card' : 'grid' }))}
                        />
                      </div>
                    </div>
                  </UIUnitGroup>
                </div>
              </UIListContentBox.Header>
              <UIListContentBox.Body>
                {searchValues.view === 'grid' ? (
                  <UIGrid<AgentBuilderRes>
                    type='multi-select'
                    loading={isLoading || isFetching}
                    selectedDataList={selectedDataList}
                    rowData={dataList}
                    columnDefs={columnDefs}
                    moreMenuConfig={moreMenuConfig}
                    onClickRow={(params: any) => {
                      if (params.data?.id) navigate(`${params.data.id}`);
                    }}
                    onCheck={handleSelect}
                  />
                ) : (
                  <UICardList
                    rowData={dataList || []}
                    flexType='grow'
                    loading={isLoading || isFetching}
                    card={(item: AgentBuilderRes) => {
                      return (
                        <UIGridCard
                          id={item.id}
                          title={item.name}
                          caption={item.description || ''}
                          data={item}
                          moreMenuConfig={moreMenuConfig}
                          checkbox={{
                            checked: selectedDataList.some(data => data.id === item.id),
                            onChange: (checked: boolean, value: string) => {
                              if (checked) {
                                const newItem = dataList.find((data: AgentBuilderRes) => data.id === value);
                                if (newItem) {
                                  setSelectedDataList([...selectedDataList, newItem]);
                                }
                              } else {
                                setSelectedDataList(selectedDataList.filter(data => data.id !== value));
                              }
                            },
                          }}
                          rows={[
                            {
                              label: '배포여부',
                              value: item.deploymentStatus ?? '',
                            },
                            {
                              label: '공개범위',
                              value: (item as any)?.publicStatus ?? '',
                            },
                            {
                              label: '생성일시',
                              value: item.createdAt,
                            },
                            {
                              label: '최종 수정일시',
                              value: item.updatedAt,
                            },
                          ]}
                          onClick={() => {
                            if (item.id) navigate(`${item.id}`);
                          }}
                        />
                      );
                    }}
                  />
                )}
              </UIListContentBox.Body>
              <UIListContentBox.Footer className='ui-data-has-btn'>
                <Button
                  auth={AUTH_KEY.AGENT.BUILDER_DELETE}
                  className='btn-option-outlined'
                  style={{ width: '40px' }}
                  disabled={(data?.totalElements ?? 0) === 0}
                  onClick={() => {
                    handleDeleteConfirm(selectedDataList.map(item => item.id));
                  }}
                >
                  삭제
                </Button>
                <UIPagination currentPage={searchValues.page} totalPages={totalPages} onPageChange={handlePageChange} hasNext={data?.hasNext} className='flex justify-center' />
              </UIListContentBox.Footer>
            </UIListContainer>
          </UIArticle>
        </UIPageBody>
      </section>
      <AgentBuilderEditPopupPage
        isOpen={layerEditPopup.currentStep > 0}
        agentDescription={selectedEditData?.description || ''}
        agentName={selectedEditData?.name || ''}
        agentId={selectedEditData?.appId || ''}
        onClose={layerEditPopup.onClose}
        onUpdateSuccess={() => {
          layerEditPopup.onClose();
          refetch();
          setSelectedEditData(null);
        }}
      />
      <AgentBuilderProvider>
        <AgentStep1TmplSelectPopupPage
          key={`step1-${currentStep}`}
          isOpen={currentStep === 1}
          stepperItems={stepperItems}
          onClose={handleCloseCreateAgent}
          onNextStep={handleNextStep}
        />
        <AgentStep2BaseInfoInputPopupPage
          key={`step2-${currentStep}`}
          isOpen={currentStep === 2}
          stepperItems={stepperItems}
          onClose={handleCloseCreateAgent}
          onPreviousStep={handlePreviousStep}
        />
      </AgentBuilderProvider>
    </>
  );
}
