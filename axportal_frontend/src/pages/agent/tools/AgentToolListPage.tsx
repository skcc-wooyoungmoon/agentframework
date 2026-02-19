import { Button } from '@/components/common/auth';
import { UIBox, UIDataCnt, UITypography } from '@/components/UI/atoms';
import { UIPagination } from '@/components/UI/atoms/UIPagination';
import { UIToggle } from '@/components/UI/atoms/UIToggle';
import { UIDropdown, UIGroup, UIInput, UIUnitGroup } from '@/components/UI/molecules';
import { UICardList } from '@/components/UI/molecules/card/UICardList';
import { UIGridCard } from '@/components/UI/molecules/card/UIGridCard';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { AUTH_KEY } from '@/constants/auth/auth.constants';
import { STORAGE_KEYS } from '@/constants/common/storage.constants';
import { useLayerPopup } from '@/hooks/common/layer';
import { useBackRestoredState } from '@/hooks/common/navigation';
import { useDeleteAgentToolById, useGetAgentToolById, useGetAgentToolList } from '@/services/agent/tool/agentTool.services';
import { useModal } from '@/stores/common/modal';
import { dateUtils } from '@/utils/common';
import type { ColDef } from 'ag-grid-community';
import React, { useCallback, useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';

import { env } from '@/constants/common/env.constants';
import { AgentToolCreatePopupPage } from './';
import { AgentToolEditPopupPage } from './AgentToolEditPopupPage';

interface SearchValues {
  page: number;
  size: number;
  searchKeyword: string;
  view: string;
}

export function AgentToolListPage() {
  const navigate = useNavigate();
  const { openAlert, openConfirm } = useModal();
  const layerCreatePopup = useLayerPopup();
  const layerEditPopup = useLayerPopup();

  const [editingToolId, setEditingToolId] = useState<string | null>(null);

  const { data: editingToolData } = useGetAgentToolById(
    { toolId: editingToolId || '' },
    {
      enabled: !!editingToolId,
    }
  );

  const { filters: searchValues, updateFilters: setSearchValues } = useBackRestoredState<SearchValues>(STORAGE_KEYS.SEARCH_VALUES.AGENT_TOOL_LIST, {
    page: 1,
    size: 12,
    searchKeyword: '',
    view: 'grid',
  });

  const { data, isSuccess, refetch, isLoading } = useGetAgentToolList(
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

  const totalPages = isSuccess ? data?.totalPages || 1 : 1;

  const gridRowData = useMemo(() => {
    if (!data?.content) return [];
    return data.content.map(item => ({ ...item }));
  }, [data?.content?.length, JSON.stringify(data?.content?.map(item => item.id))]);

  const [dataList, setDataList] = useState<any[]>([]);

  useEffect(() => {
    if (isSuccess && data) {
      setDataList(data.content || []);
    }
  }, [data, isSuccess]);

  const updatePageSizeAndRefetch = (patch: Partial<Pick<SearchValues, 'page' | 'size'>>) => {
    setSearchValues(prev => ({ ...prev, ...patch }));
    setTimeout(() => refetch(), 0);
  };

  const [selectedDataList, setSelectedDataList] = useState<any[]>([]);
  const handleSelect = useCallback((datas: any[]) => {
    setSelectedDataList(datas);
  }, []);

  const columnDefs: ColDef[] = useMemo(
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
        valueGetter: (params: any) => (searchValues.page - 1) * searchValues.size + params.node.rowIndex + 1,
      },
      {
        headerName: '이름',
        field: 'displayName',
        width: 272,
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
        headerName: '설명',
        field: 'description',
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
        headerName: 'Tools 유형',
        field: 'toolType',
        width: 120,
        valueGetter: (params: any) => params.data.toolType || '',
      },
      {
        headerName: '생성일시',
        field: 'createdAt',
        width: 180,
        cellStyle: {
          paddingLeft: '16px',
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
          paddingLeft: '16px',
        },
        valueGetter: (params: any) => {
          if (!params.data.updatedAt) return '';
          return dateUtils.formatDate(params.data.updatedAt, 'datetime');
        },
      },
      {
        headerName: '',
        field: 'more',
        width: 56,
      },
    ],
    [searchValues.page, searchValues.size]
  );

  const { mutate: deleteAgentTool } = useDeleteAgentToolById({
    onSuccess: () => { },
    onError: () => { },
  });

  const handleDeleteConfirm = async (ids: string[]) => {
    if (ids.length === 0) {
      openAlert({
        title: '안내',
        message: '삭제할 항목을 선택해주세요.',
      });
      return;
    } else {
      openConfirm({
        title: '안내',
        message: '삭제하시겠어요? \n삭제한 정보는 복구할 수 없습니다.',
        confirmText: '예',
        cancelText: '아니요',
        onConfirm: () => {
          handleDelete(ids);
        },
        onCancel: () => { },
      });
    }
  };

  const handleDelete = async (ids: string[]) => {
    let successCount = 0;
    let failCount = 0;

    for (const id of ids) {
      await new Promise<void>(resolve => {
        deleteAgentTool(
          { toolId: id },
          {
            onSuccess: () => {
              successCount++;
              resolve();
            },
            onError: () => {
              failCount++;
              resolve();
            },
          }
        );
      });
    }

    if (ids.length === 1) {
      if (successCount > 0) {
        openAlert({
          title: '완료',
          message: '에이전트 도구가 삭제되었습니다.',
        });
      }
    } else {
      if (failCount == 0) {
        openAlert({
          title: '완료',
          message: '에이전트 도구 삭제가 완료되었습니다.',
        });
      } else {
        openAlert({
          title: '안내',
          message: `에이전트 도구 삭제가 완료되었습니다.\n${successCount}건 성공, ${failCount}건 실패\n\n실패한 항목은 확인 후 다시 시도해주세요.`,
        });
      }
    }

    if (successCount > 0) {
      setSelectedDataList([]);
      refetch();
    }
  };

  const handlePageChange = (newPage: number) => {
    updatePageSizeAndRefetch({ page: newPage });
  };

  const handleSearch = () => {
    setSearchValues(prev => ({ ...prev, page: 1 }));
    refetch();
  };

  const handleEditClick = (toolId: string) => {
    setEditingToolId(toolId);
    layerEditPopup.onOpen();
  };

  const moreMenuConfig = useMemo(
    () => ({
      items: [
        {
          label: '수정',
          action: 'modify',
          auth: AUTH_KEY.AGENT.TOOL_UPDATE,
          onClick: (rowData: any) => {
            handleEditClick(rowData.id);
          },
        },
        {
          label: '삭제',
          action: 'delete',
          auth: AUTH_KEY.AGENT.TOOL_DELETE,
          onClick: (rowData: any) => {
            handleDeleteConfirm([rowData.id]);
          },
        },
      ],
      isActive: () => true,
    }),
    []
  );

  const handleDetailClick = (toolId: string) => {
    navigate(`${toolId}`);
  };

  const handleAgentToolCreatePopup = () => {
    layerCreatePopup.onOpen();
  };

  return (
    <>
      <section className='section-page'>
        <UIPageHeader
          title='Tools'
          description={['Agent가 사용할 도구를 등록하고 관리할 수 있습니다.', '행내 다양한 API를 등록하거나 커스텀 코드 개발을 통해 다양한 도구를 만들어보세요.']}
          actions={
            <Button
              auth={AUTH_KEY.AGENT.TOOL_CREATE}
              className='btn-text-18-semibold-point'
              leftIcon={{ className: 'ic-system-24-add', children: '' }}
              onClick={handleAgentToolCreatePopup}
            >
              Tools 등록
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
                          <div className='flex-1'>
                            <UIInput.Search
                              value={searchValues.searchKeyword}
                              placeholder='이름 입력'
                              maxLength={50}
                              onChange={e => setSearchValues(prev => ({ ...prev, searchKeyword: e.target.value.slice(0, 50) }))}
                            />
                          </div>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
                <div style={{ width: '128px' }}>
                  <Button className='btn-secondary-blue' style={{ width: '100%' }} onClick={handleSearch}>
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
                          <UIDataCnt count={data?.totalElements || 0} prefix='총' unit='건' />
                        </div>
                      </div>
                      <div className='flex items-center gap-[8px]'>
                        <div style={{ width: '180px', flexShrink: 0 }}>
                          <UIDropdown
                            value={`${searchValues.size}개씩 보기`}
                            disabled={(data?.totalElements ?? 0) === 0}
                            options={[
                              { value: '12', label: '12개씩 보기' },
                              { value: '36', label: '36개씩 보기' },
                              { value: '60', label: '60개씩 보기' },
                            ]}
                            onSelect={(value: string) => updatePageSizeAndRefetch({ size: Number(value) })}
                            height={40}
                            variant='dataGroup'
                          />
                        </div>
                        <UIToggle
                          variant='dataView'
                          checked={searchValues.view === 'card'}
                          disabled={(data?.totalElements ?? 0) === 0}
                          onChange={checked => setSearchValues(prev => ({ ...prev, view: checked ? 'card' : 'grid' }))}
                        />
                      </div>
                    </div>
                  </UIUnitGroup>
                </div>
              </UIListContentBox.Header>
              <UIListContentBox.Body>
                {searchValues.view === 'grid' ? (
                  <UIGrid<any>
                    type='multi-select'
                    loading={isLoading}
                    selectedDataList={selectedDataList}
                    rowData={gridRowData}
                    columnDefs={columnDefs}
                    moreMenuConfig={moreMenuConfig}
                    onClickRow={(params: any) => {
                      handleDetailClick(params.data.id);
                    }}
                    onCheck={handleSelect}
                  />
                ) : (
                  <UICardList
                    rowData={dataList}
                    flexType='none'
                    loading={isLoading}
                    card={(item: any) => {
                      return (
                        <UIGridCard<any>
                          key={item.id}
                          id={item.id}
                          title={item.name?.length > 20 ? item.name.slice(0, 20) + '...' : item.name || ''}
                          caption={item.description?.length > 20 ? item.description.slice(0, 20) + '...' : item.description || ''}
                          moreMenuConfig={moreMenuConfig}
                          data={item}
                          checkbox={{
                            checked: selectedDataList.some(data => data.id === item.id),
                            onChange: (checked: boolean) => {
                              if (checked) {
                                setSelectedDataList([...selectedDataList, item]);
                              } else {
                                setSelectedDataList(selectedDataList.filter(data => data.id !== item.id));
                              }
                            },
                          }}
                          onClick={() => handleDetailClick(item.id)}
                          moreButton={{
                            onClick: e => {
                              e.stopPropagation();
                              handleDetailClick(item.id);
                            },
                          }}
                          rows={[
                            { label: 'Tools 유형', value: item.toolType || '' },
                            { label: '생성일시', value: item.createdAt ? dateUtils.formatDate(item.createdAt, 'datetime') : '' },
                            { label: '최종 수정일시', value: item.updatedAt ? dateUtils.formatDate(item.updatedAt, 'datetime') : '' },
                          ]}
                        />
                      );
                    }}
                  />
                )}
              </UIListContentBox.Body>
              <UIListContentBox.Footer className='ui-data-has-btn'>
                <Button
                  auth={AUTH_KEY.AGENT.TOOL_DELETE}
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

      <AgentToolCreatePopupPage
        isOpen={layerCreatePopup.currentStep > 0}
        onClose={layerCreatePopup.onClose}
        onCreateSuccess={() => {
          refetch();
        }}
      />
      <AgentToolEditPopupPage
        currentStep={layerEditPopup.currentStep}
        onNextStep={layerEditPopup.onNextStep}
        onPreviousStep={layerEditPopup.onPreviousStep}
        onClose={() => {
          layerEditPopup.onClose();
          setEditingToolId(null);
        }}
        toolId={editingToolId || ''}
        toolName={editingToolData?.name}
        toolDisplayName={editingToolData?.displayName || ''}
        toolDescription={editingToolData?.description}
        toolType={editingToolData?.toolType}
        method={editingToolData?.method}
        serverUrl={editingToolData?.serverUrl}
        headerParams={editingToolData?.apiParam?.header ? Object.entries(editingToolData.apiParam.header).map(([name, value]) => ({ name, value: String(value) })) : []}
        apiParams={editingToolData?.apiParam ? JSON.stringify(editingToolData.apiParam) : ''}
        code={editingToolData?.code}
        onUpdateSuccess={() => {
          layerEditPopup.onClose();
          setEditingToolId(null);
          refetch();
        }}
      />
    </>
  );
}
