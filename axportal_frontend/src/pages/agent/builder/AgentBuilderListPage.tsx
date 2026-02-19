import { agentAtom, edgesAtom, keyTableAtom, nodesAtom } from '@/components/agents/builder/atoms/AgentAtom';
import { messagesAtom } from '@/components/agents/builder/atoms/messagesAtom';
import { Button } from '@/components/common/auth';
import { UIBox, UIDataCnt, UIPagination, UIToggle, UITypography } from '@/components/UI/atoms';
import type { UIStepperItem } from '@/components/UI/molecules';
import { UIArticle, UIDropdown, UIGroup, UIInput, UILoading, UIPageBody, UIPageHeader, UIUnitGroup } from '@/components/UI/molecules';
import { UICardList } from '@/components/UI/molecules/card/UICardList';
import { UIGridCard } from '@/components/UI/molecules/card/UIGridCard';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { AUTH_KEY } from '@/constants/auth/auth.constants';
import { STORAGE_KEYS } from '@/constants/common/storage.constants';
import { useLayerPopup } from '@/hooks/common/layer';
import { useBackRestoredState } from '@/hooks/common/navigation';
import { AgentBuilderProvider } from '@/providers/agent/AgentBuilderProvider';
import { useDeleteAgentBuilder, useGetAgentBuilderById, useGetAgentBuilders } from '@/services/agent/builder/agentBuilder.services';
import type { AgentBuilderRes } from '@/services/agent/builder/types';
import { useGetAgentAppList } from '@/services/deploy/agent/agentDeploy.services';
import { useUser } from '@/stores/auth';
import { useModal } from '@/stores/common/modal';
import dateUtils from '@/utils/common/date.utils';
import { useQueryClient } from '@tanstack/react-query';
import type { ICellRendererParams } from 'ag-grid-community';
import { useSetAtom } from 'jotai';
import { useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';

import { AgentBuilderEditPopupPage, AgentStep1TmplSelectPopupPage, AgentStep2BaseInfoInputPopupPage } from './';

interface SearchValues {
  page: number;
  size: number;
  searchKeyword: string;
  view: string;
}

export const AgentBuilderListPage = () => {
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const layerEditPopup = useLayerPopup();
  const { openAlert, openConfirm } = useModal();

  const normalizeDescription = (description?: string | null) => {
    if (!description) return '';
    const trimmed = description.trim();
    if (trimmed === '' || trimmed === '설명 없음') {
      return '';
    }
    return trimmed;
  };

  const setNodes = useSetAtom(nodesAtom);
  const setEdges = useSetAtom(edgesAtom);
  const setAgent = useSetAtom(agentAtom);
  const setKeyTable = useSetAtom(keyTableAtom);
  const setMessages = useSetAtom(messagesAtom);

  const [currentStep, setCurrentStep] = useState<number>(0);

  const handleOpenCreateAgent = async () => {
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

  const { user } = useUser();

  const { filters: searchValues, updateFilters: setSearchValues } = useBackRestoredState<SearchValues>(STORAGE_KEYS.SEARCH_VALUES.AGENT_BUILDER_LIST, {
    page: 1,
    size: 12,
    searchKeyword: '',
    view: 'grid',
  });

  const [appliedSearchValues, setAppliedSearchValues] = useState<SearchValues>({
    page: searchValues.page,
    size: searchValues.size,
    searchKeyword: searchValues.searchKeyword,
    view: searchValues.view,
  });

  const [selectedIds, setSelectedIds] = useState<string[]>([]);
  const [selectedDataList, setSelectedDataList] = useState<any[]>([]);

  const [editingAgentId, setEditingAgentId] = useState<string | null>(null);
  const [editName, setEditName] = useState('');
  const [editDescription, setEditDescription] = useState('');
  const [isProcessing, setIsProcessing] = useState(false);

  const {
    data: agentBuilderList,
    isSuccess,
    refetch,
    isLoading,
  } = useGetAgentBuilders({
    project_id: user?.adxpProject?.prjUuid,
    page: appliedSearchValues.page,
    size: appliedSearchValues.size,
    sort: 'createdAt,desc',
    search: appliedSearchValues.searchKeyword,
  });

  const totalPages = isSuccess ? agentBuilderList?.totalPages || 1 : 1;

  const { data: editingAgent } = useGetAgentBuilderById(editingAgentId || '');

  const gridRowData = useMemo(() => {
    if (!agentBuilderList?.content) return [];
    return agentBuilderList.content.map((item: AgentBuilderRes) => ({
      ...item,
      description: normalizeDescription(item.description),
    }));
  }, [agentBuilderList?.content]);

  const [dataList, setDataList] = useState<AgentBuilderRes[]>([]);

  useEffect(() => {
    if (isSuccess && agentBuilderList) {
      const processedContent = (agentBuilderList?.content || []).map((item: AgentBuilderRes) => ({
        ...item,
        description: normalizeDescription(item.description),
      }));
      setDataList(processedContent);
    }
  }, [agentBuilderList, isSuccess]);

  const rowData = useMemo(() => {
    return gridRowData.map((item: AgentBuilderRes) => ({
      graphUuid: item.id,
      ...item,
    }));
  }, [gridRowData]);

  useEffect(() => {
    refetch();
  }, [appliedSearchValues.page, appliedSearchValues.size]);

  useEffect(() => {
    if (editingAgent) {
      setEditName(editingAgent.name || '');
      setEditDescription(editingAgent.description || '');
    }
  }, [editingAgent]);

  const deleteAgentBuilderMutation = useDeleteAgentBuilder();

  const handlePageChange = (newPage: number) => {
    setAppliedSearchValues(prev => ({
      ...prev,
      page: newPage,
    }));
    setSearchValues(prev => ({
      ...prev,
      page: newPage,
    }));
  };

  const handleSizeChange = (newSize: number) => {
    setAppliedSearchValues(prev => ({
      ...prev,
      size: newSize,
      page: 1,
    }));
    setSearchValues(prev => ({
      ...prev,
      size: newSize,
      page: 1,
    }));
  };

  const handleSearch = () => {
    setAppliedSearchValues({
      page: 1,
      size: searchValues.size,
      searchKeyword: searchValues.searchKeyword,
      view: searchValues.view,
    });
    setSearchValues(prev => ({ ...prev, page: 1 }));
  };

  const handleSelect = (selectedDatas: any[]) => {
    setSelectedDataList(selectedDatas);
    setSelectedIds(selectedDatas.map(d => d.graphUuid || d.id));
  };

  const handleBulkDelete = async () => {
    if (selectedIds.length === 0) {
      openAlert({
        title: '안내',
        message: '삭제할 항목을 선택해주세요.',
      });
      return;
    }

    if (isLoadingDeployedAgents) {
      await openAlert({
        title: '안내',
        message: '배포 정보를 확인하는 중입니다. 잠시 후 다시 시도해주세요.',
      });
      return;
    }

    const deployedAgents = selectedIds.filter(agentId => deployedAgentIds.has(agentId));

    if (deployedAgents.length > 0) {
      await openAlert({
        title: '안내',
        message: `선택한 항목 중 ${deployedAgents.length}개의 배포된 에이전트가 포함되어 있습니다.\n배포된 에이전트는 삭제할 수 없습니다.`,
      });
      return;
    }

    const isConfirmed = await openConfirm({
      title: '안내',
      message: '삭제하시겠어요?\n삭제한 정보는 복구할 수 없습니다.',
      confirmText: '예',
      cancelText: '아니요',
    });

    if (!isConfirmed) return;

    setIsProcessing(true);

    try {
      const deletePromises = selectedIds.map(agentId =>
        deleteAgentBuilderMutation.mutateAsync({ graphUuid: agentId })
      );

      const responses = await Promise.all(deletePromises);

      const allSuccess = responses.every(response =>
        response?.success ?? (response === undefined || response === null)
      );

      if (!allSuccess) {
        throw new Error('일부 에이전트 삭제에 실패했습니다.');
      }

      await queryClient.invalidateQueries({ queryKey: ['agent-builder-list'] });
      await queryClient.invalidateQueries({ queryKey: ['GET', '/agent/builder'] });
      await refetch();

      setIsProcessing(false);

      await openAlert({
        title: '완료',
        message: '빌더가 삭제되었습니다.',
        confirmText: '확인',
      });
      setSelectedIds([]);
      setSelectedDataList([]);
    } catch (error: any) {
      setIsProcessing(false);
      const errorMessage = error?.response?.data?.message || error?.message || '일괄삭제에 실패했습니다.';
      await openAlert({
        title: '오류',
        message: errorMessage,
      });
    }
  };

  const handleEdit = (agentId: string, agentName: string, agentDescription: string) => {
    if (!agentId) return;

    setEditingAgentId(agentId);
    setEditName(agentName);
    setEditDescription(agentDescription);
    layerEditPopup.onOpen();
  };

  const currentPageAgentIds = useMemo(() => {
    if (!agentBuilderList?.content) return [];
    return agentBuilderList.content.map((item: AgentBuilderRes) => item.id).filter(Boolean);
  }, [agentBuilderList?.content]);

  const { data: deployedAgentsData, isLoading: isLoadingDeployedAgents } = useGetAgentAppList({
    page: 1,
    size: 100,
    targetType: 'all',
    sort: 'created_at,desc',
    filter: '',
    search: '',
  });

  const deployedAgentIds = useMemo(() => {
    if (!deployedAgentsData?.content || currentPageAgentIds.length === 0) return new Set<string>();

    const ids = new Set<string>();
    const currentPageIdsSet = new Set(currentPageAgentIds);

    deployedAgentsData.content.forEach((app: any) => {
      if (app.deployments && app.deployments.length > 0) {
        app.deployments.forEach((deployment: any) => {
          if (deployment.deploymentConfigPath) {
            const pathParts = deployment.deploymentConfigPath.split('/');
            const extractedId = pathParts[pathParts.length - 2];
            if (extractedId && currentPageIdsSet.has(extractedId)) {
              ids.add(extractedId);
            }
          }
          if (deployment.targetId && currentPageIdsSet.has(deployment.targetId)) {
            ids.add(deployment.targetId);
          }
        });
      }
      if (app.targetId && currentPageIdsSet.has(app.targetId)) {
        ids.add(app.targetId);
      }
    });

    return ids;
  }, [deployedAgentsData, currentPageAgentIds]);

  const handleDelete = async (agentId: string) => {
    if (!agentId) return;


    if (isLoadingDeployedAgents) {
      await openAlert({
        title: '안내',
        message: '배포 정보를 확인하는 중입니다. 잠시 후 다시 시도해주세요.',
      });
      return;
    }

    const isDeployed = deployedAgentIds.has(agentId);

    if (isDeployed) {
      await openAlert({
        title: '안내',
        message: '배포된 에이전트는 삭제할 수 없습니다.',
      });
      return;
    }

    const isConfirmed = await openConfirm({
      title: '안내',
      message: '삭제하시겠어요?\n삭제한 정보는 복구할 수 없습니다.',
      confirmText: '예',
      cancelText: '아니요',
    });

    if (!isConfirmed) return;

    setIsProcessing(true);

    try {
      const response = await deleteAgentBuilderMutation.mutateAsync({ graphUuid: agentId });
      const isSuccess = response?.success ?? (response === undefined || response === null);
      if (!isSuccess) {
        throw new Error((response as any)?.message || '삭제에 실패했습니다.');
      }

      await queryClient.invalidateQueries({ queryKey: ['agent-builder-list'] });
      await queryClient.invalidateQueries({ queryKey: ['GET', '/agent/builder'] });
      await refetch();

      setIsProcessing(false);

      await openAlert({
        title: '완료',
        message: '빌더가 삭제되었습니다.',
        confirmText: '확인',
      });
      setSelectedIds([]);
      setSelectedDataList(prev => prev.filter(item => item.graphUuid !== agentId));
    } catch (error: any) {
      setIsProcessing(false);
      const errorMessage = error?.response?.data?.message || error?.message || '삭제에 실패했습니다.';
      await openAlert({
        title: '오류',
        message: errorMessage,
      });
    }
  };

  const moreMenuConfig = useMemo(
    () => ({
      items: [
        {
          label: '수정',
          action: 'modify',
          auth: AUTH_KEY.AGENT.BUILDER_UPDATE,
          onClick: (rowData: any) => {
            handleEdit(rowData.id, rowData.name, rowData.description);
          },
        },
        {
          label: '삭제',
          action: 'delete',
          auth: AUTH_KEY.AGENT.BUILDER_DELETE,
          onClick: (rowData: any) => {
            handleDelete(rowData.id);
          },
        },
      ],
      isActive: (rowData: any) => {
        const agentId = rowData?.id || rowData?.id;
        if (agentId && deployedAgentIds.has(agentId)) {
          return false;
        }

        return true;
      },
    }),
    [deployedAgentIds]
  );

  const renderEllipsisCell = ({ value }: ICellRendererParams<string>) => {
    if (value === null || value === undefined) {
      return <span className='agent-grid-ellipsis'></span>;
    }
    return <span className='agent-grid-ellipsis'>{value}</span>;
  };

  const columnDefs: any = useMemo(
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
        valueGetter: (params: any) => (appliedSearchValues.page - 1) * appliedSearchValues.size + params.node.rowIndex + 1,
      },
      {
        headerName: '이름',
        field: 'name',
        width: 272,
        tooltipField: 'name',
        cellStyle: {
          paddingLeft: '16px',
        },
        cellRenderer: renderEllipsisCell,
      },
      {
        headerName: '설명',
        field: 'description',
        flex: 1,
        showTooltip: true,
        tooltipField: 'description',
        cellStyle: {
          paddingLeft: '16px',
        },
        cellRenderer: renderEllipsisCell,
      },
      {
        headerName: '배포여부',
        field: 'deploymentStatus',
        width: 120,
        valueGetter: (params: any) => {
          const agentId = params.data?.id || params.data?.id;

          if (isLoadingDeployedAgents) {
            return '확인 중...';
          }

          if (deployedAgentIds.has(agentId)) {
            return '개발배포';
          }

          return '미배포';
        },
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
        valueGetter: (params: any) => {
          return dateUtils.formatDate(params.data.createdAt, 'datetime');
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
          if (!params.data.updatedAt) return '';
          return dateUtils.formatDate(params.data.updatedAt, 'datetime');
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
    [deployedAgentIds, isLoadingDeployedAgents, appliedSearchValues.page, appliedSearchValues.size]
  );

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
      {isProcessing && <UILoading />}
      <section className='section-page'>
        <style>
          {`
            .agent-grid-ellipsis {
              display: block;
              max-width: 100%;
              overflow: hidden;
              white-space: nowrap;
              text-overflow: ellipsis;
            }
          `}
        </style>
        <UIPageHeader
          title={
            <>
              <UITypography variant='headline-2-product' className='secondary-neutral-900'>
                빌더
              </UITypography>

              <Button
                className='w-10 h-5'
                onClick={() => {
                  console.log('숨김');
                }}
              />
            </>
          }
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
        <UIPageBody>
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
                            <div className='flex-1'>
                              <UIInput.Search
                                value={searchValues.searchKeyword}
                                placeholder='이름 입력'
                                maxLength={50}
                                onChange={e => setSearchValues(prev => ({ ...prev, searchKeyword: e.target.value.slice(0, 50) }))}
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
          <UIArticle className='article-grid'>
            <UIListContainer>
              <UIListContentBox.Header>
                <div className='w-full'>
                  <UIUnitGroup gap={16} direction='column'>
                    <div className='flex justify-between w-full items-center'>
                      <div className='flex-shrink-0'>
                        <div style={{ width: '168px', paddingRight: '8px' }}>
                          <UIDataCnt count={agentBuilderList?.totalElements || 0} prefix='총' />
                        </div>
                      </div>
                      <div className='flex items-center gap-[8px]'>
                        <div style={{ width: '180px', flexShrink: 0 }}>
                          <UIDropdown
                            value={String(searchValues.size || 12)}
                            disabled={(agentBuilderList?.totalElements ?? 0) === 0}
                            options={[
                              { value: '12', label: '12개씩 보기' },
                              { value: '36', label: '36개씩 보기' },
                              { value: '60', label: '60개씩 보기' },
                            ]}
                            onSelect={(value: string) => {
                              handleSizeChange(Number(value));
                            }}
                            height={40}
                            variant='dataGroup'
                          />
                        </div>
                        <UIToggle
                          variant='dataView'
                          checked={searchValues.view === 'card'}
                          disabled={(agentBuilderList?.totalElements ?? 0) === 0}
                          onChange={checked => setSearchValues(prev => ({ ...prev, view: checked ? 'card' : 'grid' }))}
                        />
                      </div>
                    </div>
                  </UIUnitGroup>
                </div>
              </UIListContentBox.Header>
              <UIListContentBox.Body>
                {searchValues.view === 'grid' ? (
                  <UIGrid
                    type='multi-select'
                    rowData={rowData}
                    loading={isLoading}
                    columnDefs={columnDefs}
                    moreMenuConfig={moreMenuConfig}
                    onClickRow={(params: any) => {
                      if (params.data?.graphUuid) navigate(`${params.data.graphUuid}`);
                    }}
                    onCheck={handleSelect}
                  />
                ) : (
                  <UICardList
                    rowData={dataList}
                    flexType='grow'
                    loading={isLoading}
                    card={(item: AgentBuilderRes) => {
                      return (
                        <UIGridCard
                          id={item.id}
                          title={item.name}
                          caption={normalizeDescription(item.description)}
                          data={item}
                          moreMenuConfig={moreMenuConfig}
                          checkbox={{
                            checked: selectedDataList.some(data => data.id === item.id),
                            onChange: (checked: boolean, value: string) => {
                              if (checked) {
                                const newItem = dataList.find((data: AgentBuilderRes) => data.id === value);
                                if (newItem) {
                                  setSelectedDataList([...selectedDataList, newItem]);
                                  setSelectedIds([...selectedIds, value]);
                                }
                              } else {
                                setSelectedDataList(selectedDataList.filter(data => data.id !== value));
                                setSelectedIds(selectedIds.filter(id => id !== value));
                              }
                            },
                          }}
                          rows={[
                            {
                              label: '배포여부',
                              value: deployedAgentIds.has((item as any)?.id || item.id) ? '개발배포' : '미배포',
                            },
                            {
                              label: '공개범위',
                              value: (item as any)?.publicStatus ?? '',
                            },
                            {
                              label: '생성일시',
                              value: item.createdAt ? dateUtils.formatDate(item.createdAt, 'datetime') : '',
                            },
                            {
                              label: '최종 수정일시',
                              value: item.updatedAt ? dateUtils.formatDate(item.updatedAt, 'datetime') : '',
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
                  onClick={handleBulkDelete}
                  disabled={(agentBuilderList?.totalElements ?? 0) === 0}
                >
                  삭제
                </Button>
                <UIPagination currentPage={appliedSearchValues.page} totalPages={totalPages} onPageChange={handlePageChange} hasNext={agentBuilderList?.hasNext} className='flex justify-center' />
              </UIListContentBox.Footer>
            </UIListContainer>
          </UIArticle>
        </UIPageBody>
      </section>
      <AgentBuilderEditPopupPage
        isOpen={layerEditPopup.currentStep > 0}
        agentDescription={editDescription}
        agentName={editName}
        agentId={editingAgentId || ''}
        onClose={layerEditPopup.onClose}
        onUpdateSuccess={() => {
          refetch();
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
