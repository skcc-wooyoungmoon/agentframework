import { UIDataCnt, UILabel, UIToggle } from '@/components/UI';
import { UIBox, UITypography } from '@/components/UI/atoms';
import { UIPagination } from '@/components/UI/atoms/UIPagination';
import { UITextLabel } from '@/components/UI/atoms/UITextLabel';
import { UIGroup } from '@/components/UI/molecules';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { UICardList } from '@/components/UI/molecules/card/UICardList';
import { UIGridCard } from '@/components/UI/molecules/card/UIGridCard';
import { UIDropdown } from '@/components/UI/molecules/dropdown/UIDropdown';
import { UIGrid } from '@/components/UI/molecules/grid/UIGrid/component';
import { UIInput } from '@/components/UI/molecules/input';
import { UIListContainer } from '@/components/UI/molecules/list/UIListContainer/component';
import { UIListContentBox } from '@/components/UI/molecules/list/UIListContentBox';
import { Button } from '@/components/common/auth';
import { AUTH_KEY } from '@/constants/auth/auth.constants';
import { STORAGE_KEYS } from '@/constants/common/storage.constants';
import { useLayerPopup } from '@/hooks/common/layer';
import { useBackRestoredState } from '@/hooks/common/navigation';
import { useDeleteAgentMcpCtlgById, useGetAgentMcpById, useGetAgentMcpList } from '@/services/agent/mcp/agentMcp.services';
import { useModal } from '@/stores/common/modal';
import { dateUtils } from '@/utils/common';

import { env } from '@/constants/common/env.constants';
import React, { useCallback, useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { AgentMcpCtlgCreatePopupPage } from './AgentMcpCtlgCreatePopupPage';
import { AgentMcpCtlgEditPopupPage } from './AgentMcpCtlgEditPopupPage';

interface SearchValues {
  page: number;
  size: number;
  searchKeyword: string;
  view: string;
}

export function AgentMcpCtlgListPage() {
  const navigate = useNavigate();
  const { openAlert, openConfirm } = useModal();
  const layerCreatePopup = useLayerPopup();
  const layerEditPopup = useLayerPopup();

  const [selectedDataList, setSelectedDataList] = useState<any[]>([]);
  const handleSelect = useCallback((datas: any[]) => {
    setSelectedDataList(datas);
  }, []);

  const [editingMcpId, setEditingMcpId] = useState<string | null>(null);

  const { data: editingMcpData } = useGetAgentMcpById(
    { mcpId: editingMcpId || '' },
    {
      enabled: !!editingMcpId,
    }
  );

  const { filters: searchValues, updateFilters: setSearchValues } = useBackRestoredState<SearchValues>(STORAGE_KEYS.SEARCH_VALUES.AGENT_MCP_CTLG_LIST, {
    page: 1,
    size: 12,
    searchKeyword: '',
    view: 'grid',
  });

  /** VITE_NO_PRESSURE_MODE일 때 진입 시 자동 조회하지 않고, 조회 버튼 클릭 후에만 refetch 허용 */
  // const [hasSearched, setHasSearched] = useState(false);

  const handlePageChange = (newPage: number) => {
    updatePageSizeAndRefetch({ page: newPage });
  };

  const handleSearch = () => {
    // setHasSearched(true);
    setSearchValues(prev => ({ ...prev, page: 1 }));
    refetch();
  };

  const handleEditClick = (mcpId: string) => {
    setEditingMcpId(mcpId);
    layerEditPopup.onOpen();
  };

  const moreMenuConfig = useMemo(
    () => ({
      items: [
        {
          label: '수정',
          action: 'modify',
          auth: AUTH_KEY.AGENT.MCP_SERVER_UPDATE,
          onClick: (rowData: any) => {
            handleEditClick(rowData.id);
          },
        },
        {
          label: '삭제',
          action: 'delete',
          auth: AUTH_KEY.AGENT.MCP_SERVER_DELETE,
          onClick: (rowData: any) => {
            handleDeleteConfirm([rowData.id]);
          },
        },
      ],
      isActive: () => true,
    }),
    []
  );

  const {
    data: mcpCtlgList,
    isSuccess,
    refetch,
    isLoading,
  } = useGetAgentMcpList(
    {
      page: searchValues.page,
      size: searchValues.size,
      sort: 'created_at,desc',
      search: searchValues.searchKeyword,
    },
    {
      enabled: !env.VITE_NO_PRESSURE_MODE,
      // enabled: false, // 조회 중에도 기존 데이터 유지
      placeholderData: previousData => previousData, // 조회 중에도 기존 데이터 유지
    }
  );

  const totalPages = isSuccess ? mcpCtlgList?.totalPages || 1 : 1;

  const updatePageSizeAndRefetch = (patch: Partial<Pick<SearchValues, 'page' | 'size'>>) => {
    setSearchValues(prev => ({ ...prev, ...patch }));
    setTimeout(() => refetch(), 0);
  };

  const gridRowData = useMemo(() => {
    if (!mcpCtlgList?.content) return [];
    return mcpCtlgList.content.map((item: any) => ({
      ...item,
      name: typeof item.name === 'string' ? item.name : item.name?.name || '',
      tags: Array.isArray(item.tags) ? item.tags.map((tag: any) => (typeof tag === 'string' ? tag : tag?.name || '')) : [],
    }));
  }, [mcpCtlgList?.content]);

  const [dataList, setDataList] = useState<any[]>([]);

  useEffect(() => {
    if (isSuccess && mcpCtlgList) {
      const processedContent = (mcpCtlgList?.content || []).map((item: any) => ({
        ...item,
        name: typeof item.name === 'string' ? item.name : item.name?.name || '',
        tags: Array.isArray(item.tags) ? item.tags.map((tag: any) => (typeof tag === 'string' ? tag : tag?.name || '')) : [],
      }));
      setDataList(processedContent);
    }
  }, [mcpCtlgList, isSuccess]);

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
        valueGetter: (params: any) => (searchValues.page - 1) * searchValues.size + params.node.rowIndex + 1,
      },
      {
        headerName: '서버명',
        field: 'name',
        width: 272,
        cellRenderer: React.memo((params: any) => {
          const nameValue = typeof params.value === 'string' ? params.value : params.value?.name || params.data?.name || '';
          return (
            <div
              style={{
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                whiteSpace: 'nowrap',
              }}
            >
              {nameValue}
            </div>
          );
        }),
      },
      {
        headerName: '상태',
        field: 'enabled',
        width: 120,
        cellRenderer: React.memo((params: any) => {
          const getStatusIntent = (status: boolean) => {
            switch (status) {
              case true:
                return 'complete';
              case false:
                return 'error';
              default:
                return 'complete';
            }
          };
          return (
            <UILabel variant='badge' intent={getStatusIntent(params.value)}>
              {params.value ? '이용 가능' : '이용 불가능'}
            </UILabel>
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
        headerName: '인증유형',
        field: 'authType',
        width: 120,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: '태그',
        field: 'tags' as any,
        width: 230,
        cellRenderer: React.memo((params: any) => {
          const tagValue = params.value;

          if (!tagValue || tagValue === '' || tagValue.length === 0) {
            return null;
          }
          if (Array.isArray(tagValue)) {
            const tagNames = tagValue.map((tag: any) => (typeof tag === 'string' ? tag : tag?.name || ''));
            return (
              <div className='flex items-center gap-[2px] flex-wrap'>
                {tagNames.slice(0, 2).map((tagName: string, index: number) => (
                  <UITextLabel key={index} intent='tag'>
                    {tagName}
                  </UITextLabel>
                ))}
                {tagNames.length > 2 && (
                  <UITypography variant='caption-2' className='secondary-neutral-550'>
                    {'...'}
                  </UITypography>
                )}
              </div>
            );
          }

          return null;
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
          return params.data.updatedAt ? dateUtils.formatDate(params.data.updatedAt, 'datetime') : '';
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

  const { mutate: deleteAgentMcpCtlgById } = useDeleteAgentMcpCtlgById({
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
        deleteAgentMcpCtlgById(
          { mcpId: id },
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
          message: '에이전트 MCP 서버가 삭제되었습니다.',
        });
      }
    } else {
      if (failCount == 0) {
        openAlert({
          title: '완료',
          message: '에이전트 MCP 서버 삭제가 완료되었습니다.',
        });
      } else if (failCount > 0 && successCount > 0) {
        openAlert({
          title: '안내',
          message: `에이전트 MCP 서버 삭제가 완료되었습니다.\n${successCount}건 성공, ${failCount}건 실패\n\n실패한 항목은 확인 후 다시 시도해주세요.`,
        });
      }
    }

    if (successCount > 0) {
      setSelectedDataList([]);
      refetch();
    }
  };

  const handleAgentMcpCtlgCreatePopup = () => {
    layerCreatePopup.onOpen();
  };

  const handleDetailClick = (mcpId: string) => {
    navigate(`${mcpId}`);
  };

  return (
    <>
      <section className='section-page'>
        <UIPageHeader
          title='MCP 서버'
          description='MCP 서버를 등록 수정 관리 할 수 있습니다.'
          actions={
            <Button
              auth={AUTH_KEY.AGENT.MCP_SERVER_CREATE}
              className='btn-text-18-semibold-point'
              leftIcon={{ className: 'ic-system-24-add', children: '' }}
              onClick={handleAgentMcpCtlgCreatePopup}
            >
              MCP 서버 등록
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
                          <div>
                            <UIInput.Search
                              value={searchValues.searchKeyword}
                              placeholder='서버명 입력'
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
                <div className='flex-shrink-0'>
                  <UIGroup gap={8} direction='row' align='start'>
                    <div style={{ width: '168px', paddingRight: '8px' }}>
                      <UIDataCnt count={mcpCtlgList?.totalElements || 0} prefix='총' unit='건' />
                    </div>
                  </UIGroup>
                </div>
                <div className='flex items-center gap-2'>
                  <div style={{ width: '180px', flexShrink: 0 }}>
                    <UIDropdown
                      value={`${searchValues.size}개씩 보기`}
                      disabled={(mcpCtlgList?.totalElements ?? 0) === 0}
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
                    disabled={(mcpCtlgList?.totalElements ?? 0) === 0}
                    onChange={checked => setSearchValues(prev => ({ ...prev, view: checked ? 'card' : 'grid' }))}
                  />
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
                      const getStatusIntent = (status: boolean) => {
                        switch (status) {
                          case true:
                            return 'complete';
                          case false:
                            return 'error';
                          default:
                            return 'complete';
                        }
                      };
                      return (
                        <UIGridCard<any>
                          id={item.id}
                          title={typeof item.name === 'string' ? item.name : item.name?.name || ''}
                          caption={item.description}
                          statusArea={
                            <UILabel variant='badge' intent={getStatusIntent(item.enabled)}>
                              {item.enabled ? '이용 가능' : '이용 불가능'}
                            </UILabel>
                          }
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
                            {
                              label: '태그명',
                              value: Array.isArray(item.tags) ? item.tags.map((tag: any) => (typeof tag === 'string' ? tag : tag?.name || '')).join(', ') : '',
                            },
                            { label: '생성일시', value: item.createdAt ? dateUtils.formatDate(item.createdAt, 'datetime') : '' },
                            { label: '최종수정일시', value: item.updatedAt ? dateUtils.formatDate(item.updatedAt, 'datetime') : '' },
                          ]}
                        />
                      );
                    }}
                  />
                )}
              </UIListContentBox.Body>
              <UIListContentBox.Footer className='ui-data-has-btn'>
                <Button
                  auth={AUTH_KEY.AGENT.MCP_SERVER_DELETE}
                  className='btn-option-outlined'
                  style={{ width: '40px' }}
                  disabled={(mcpCtlgList?.totalElements ?? 0) === 0}
                  onClick={() => {
                    handleDeleteConfirm(selectedDataList.map(item => item.id));
                  }}
                >
                  삭제
                </Button>
                <UIPagination currentPage={searchValues.page} totalPages={totalPages} onPageChange={handlePageChange} hasNext={mcpCtlgList?.hasNext} className='flex justify-center' />
              </UIListContentBox.Footer>
            </UIListContainer>
          </UIArticle>
        </UIPageBody>
      </section>
      <AgentMcpCtlgCreatePopupPage
        isOpen={layerCreatePopup.currentStep > 0}
        onClose={layerCreatePopup.onClose}
        onCreateSuccess={() => {
          refetch();
        }}
      />
      <AgentMcpCtlgEditPopupPage
        isOpen={layerEditPopup.currentStep > 0}
        onClose={() => {
          layerEditPopup.onClose();
          setEditingMcpId(null);
        }}
        mcpId={editingMcpId || ''}
        name={editingMcpData ? (typeof (editingMcpData as any)?.name === 'string' ? (editingMcpData as any)?.name : (editingMcpData as any)?.name?.name || '') : undefined}
        description={editingMcpData ? (editingMcpData as any)?.description : undefined}
        serverUrl={editingMcpData ? (editingMcpData as any)?.serverUrl : undefined}
        transportType={editingMcpData ? ((editingMcpData as any)?.transportType as 'streamable-http' | 'sse' | undefined) : undefined}
        authType={editingMcpData ? ((editingMcpData as any)?.authType as 'none' | 'basic' | 'bearer' | 'custom-header' | undefined) : undefined}
        authConfig={editingMcpData ? (editingMcpData as any)?.authConfig : undefined}
        tags={
          editingMcpData && Array.isArray((editingMcpData as any)?.tags)
            ? (editingMcpData as any).tags.map((tag: any) => (typeof tag === 'string' ? tag : tag?.name || ''))
            : undefined
        }
        onUpdateSuccess={() => {
          layerEditPopup.onClose();
          setEditingMcpId(null);
          refetch();
        }}
      />
    </>
  );
}
