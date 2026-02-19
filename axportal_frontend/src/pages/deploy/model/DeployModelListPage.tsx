import React, { useEffect, useMemo, useState } from 'react';

import { Button } from '@/components/common/auth';
import { UIDataCnt, UILabel, UIToggle } from '@/components/UI';
import { UIBox, UITypography } from '@/components/UI/atoms';
import { type UILabelIntent } from '@/components/UI/atoms/UILabel';
import { UIPagination } from '@/components/UI/atoms/UIPagination';
import { UIDropdown, UIGroup, UIInput } from '@/components/UI/molecules';
import { UICardList } from '@/components/UI/molecules/card/UICardList';
import { UIGridCard } from '@/components/UI/molecules/card/UIGridCard';
import type { UIMoreMenuConfig } from '@/components/UI/molecules/grid';
import { UIGrid } from '@/components/UI/molecules/grid/UIGrid/component';
import { UIListContainer } from '@/components/UI/molecules/list/UIListContainer/component';
import { UIListContentBox } from '@/components/UI/molecules/list/UIListContentBox';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { AUTH_KEY } from '@/constants/auth/auth.constants';
import { env, RUN_MODE_TYPES } from '@/constants/common/env.constants';
import { STORAGE_KEYS } from '@/constants/common/storage.constants';
import { MODEL_DEPLOY_STATUS } from '@/constants/deploy/modelDeploy.constants';
import { useLayerPopup } from '@/hooks/common/layer';
import { useBackRestoredState } from '@/hooks/common/navigation';
import { useCommonPopup } from '@/hooks/common/popup/useCommonPopup';
import { useChangeBackendAiModelDeployStatus, useChangeModelDeployStatus, useDeleteModelDeployBulk, useGetModelDeployList } from '@/services/deploy/model/modelDeploy.services';
import type { GetModelDeployResponse } from '@/services/deploy/model/types';
import { useModal } from '@/stores/common/modal';
import { dateUtils } from '@/utils/common';
import { useLocation, useNavigate } from 'react-router';

import { DeployModelEdit } from './DeployModelEdit';

interface SearchValues {
  page: number;
  size: number;
  searchKeyword: string;
  status: string;
  view: string;
}

export const DeployModelListPage = () => {
  const { openAlert } = useModal();
  const { showDeleteConfirm, showDeleteItemNotSelected, showDeleteComplete } = useCommonPopup();
  const navigate = useNavigate();
  const location = useLocation();
  // const deployModelLayerPopup = useLayerPopup();
  const editModelLayerPopup = useLayerPopup();

  // https://aip-stg.sktai.io/api/v1/servings?page=1&size=12&filter=status:Scaling&sort=created_at,desc
  // 검색 조건
  const { filters: searchValues, updateFilters: setSearchValues } = useBackRestoredState<SearchValues>(STORAGE_KEYS.SEARCH_VALUES.MODEL_CTLG_LIST, {
    page: 1,
    size: 12,
    searchKeyword: '',
    status: 'all',
    view: 'grid',
  });

  // 목록 조회
  const {
    data: modelList,
    refetch,
    isLoading,
    isFetching,
  } = useGetModelDeployList(
    {
      page: searchValues.page - 1,
      size: searchValues.size,
      filter: searchValues.status === 'all' ? '' : `status:${searchValues.status}`,
      search: searchValues.searchKeyword,
    },
    {
      enabled: !env.VITE_NO_PRESSURE_MODE,
    }
  );

  // 배포 완료 후 새로고침 처리
  useEffect(() => {
    if (location.state?.shouldRefresh) {
      if (!env.VITE_NO_PRESSURE_MODE) {
        refetch();
      }
      // 상태 초기화
      navigate(location.pathname, { replace: true, state: {} });
    }
  }, [location.state, refetch, navigate, location.pathname]);

  const rowData = useMemo(() => {
    return (
      modelList?.content.map((item, index: number) => ({
        id: item.servingId,
        ...item,
        modelName: item.displayName !== null && item.displayName !== '' ? item.displayName : item.modelName,
        no: (searchValues.page - 1) * searchValues.size + index + 1,
        createdAt: item.createdAt ? dateUtils.formatDate(item.createdAt, 'datetime') : '',
        updatedAt: item.updatedAt ? dateUtils.formatDate(item.updatedAt, 'datetime') : dateUtils.formatDate(item.createdAt, 'datetime'),
      })) ?? []
    );
  }, [modelList]);

  const updatePageSizeAndRefetch = (patch: Partial<Pick<SearchValues, 'page' | 'size'>>) => {
    setSearchValues(prev => ({ ...prev, ...patch }));
    setTimeout(() => refetch(), 0);
  };

  // 상태 변경
  const { mutate: changeModelDeployStatus } = useChangeModelDeployStatus();
  const { mutate: changeBackendAiModelDeployStatus } = useChangeBackendAiModelDeployStatus();
  const handleChangeModelDeployStatus = async (type: GetModelDeployResponse['servingType'], servingId: string, status: 'start' | 'stop') => {
    const isOk = await openAlert({
      title: '안내',
      message: status === 'start' ? '배포를 시작합니다.' : '배포를 중지합니다.',
    });
    if (isOk) {
      if (type === 'serverless') {
        changeModelDeployStatus(
          { id: servingId, status },
          {
            onSuccess: () => {
              refetch();
            },
          }
        );
      } else {
        changeBackendAiModelDeployStatus(
          { id: servingId, status },
          {
            onSuccess: () => {
              refetch();
            },
          }
        );
      }
    }
  };

  // 조회 버튼
  const handleSearch = () => {
    setSearchValues(prev => ({ ...prev, page: 1 }));
    refetch();
  };

  // checkbox
  const [selectedDataList, setSelectedDataList] = useState<GetModelDeployResponse[]>([]);
  const handleSelect = (datas: GetModelDeployResponse[]) => {
    setSelectedDataList(datas);
  };

  // 상세 이동
  const handleRowClick = (id: string) => {
    navigate(`${id}`);
  };

  const handleDropdownSelect = (key: keyof SearchValues, value: string) => {
    setSearchValues(prev => ({ ...prev, [key]: value }));
  };

  // 옵션 정의
  const statusOptions = [
    { value: 'all', label: '전체' },
    { value: 'Deploying', label: '배포중' },
    { value: 'Available', label: '이용 가능' },
    { value: 'Failed', label: '실패' },
    // { value: 'Error', label: '에러' },
    { value: 'Stopped', label: '중지' },
    // { value: 'Scaling', label: 'Scaling' },
    // { value: 'Updating', label: 'Updating' },
    // { value: 'Deleting', label: '삭제' },
    { value: 'Terminated', label: '종료' },
    // { value: 'Unknown', label: '알 수 없음' },
  ];

  // 삭제
  const { mutate: deleteModelDeployBulk } = useDeleteModelDeployBulk();
  const handleDeleteModelCtlgBulk = async (type: 'single' | 'bulk', id?: string) => {
    // 내부 메소드
    const handleSuccess = () => {
      setSelectedDataList([]);
      refetch();
    };

    // 삭제할 항목이 없으면 안내 표시
    if (type === 'bulk' && selectedDataList.length === 0) {
      showDeleteItemNotSelected();
      return;
    }

    // 운영 배포 모델 삭제 불가
    if (
      (type === 'bulk' && selectedDataList.some(item => item.production)) ||
      (type === 'single' && id !== undefined && modelList?.content.find(item => item.servingId === id)?.production)
    ) {
      openAlert({
        title: '안내',
        message: '운영계에 배포된 모델은 삭제할 수 없습니다.',
      });
      return;
    }

    // 삭제 확인 팝업 표시
    showDeleteConfirm({
      onConfirm: () => {
        if (type === 'bulk') {
          deleteModelDeployBulk(
            selectedDataList.map(item => ({
              servingId: item.servingId,
              servingType: item.servingType,
            })),
            {
              onSuccess: () => {
                showDeleteComplete({
                  itemName: '모델 배포가',
                  onConfirm: () => {
                    handleSuccess();
                  },
                });
              },
            }
          );
        } else {
          // 개별 삭제의 경우 해당 아이템을 찾아서 servingType을 가져옴
          const targetItem = modelList?.content.find((item: GetModelDeployResponse) => item.servingId === id);
          if (targetItem) {
            deleteModelDeployBulk(
              [
                {
                  servingId: id ?? '',
                  servingType: targetItem.servingType,
                },
              ],
              {
                onSuccess: () => {
                  showDeleteComplete({
                    itemName: '모델 배포가',
                    onConfirm: () => {
                      handleSuccess();
                    },
                  });
                },
              }
            );
          }
        }
      },
    });
  };

  // 수정
  const [editData, setEditData] = useState<GetModelDeployResponse | null>(null);
  const handleEditClick = (data: GetModelDeployResponse) => {
    if (data.status !== 'Stopped') {
      openAlert({
        title: '안내',
        message: "배포 상태가 '중지'일 때만 수정이 가능합니다.",
      });
      return;
    }
    setEditData(data);
    editModelLayerPopup.onOpen();
  };
  const handleEditPageClose = () => {
    editModelLayerPopup.onClose();
    refetch();
  };

  const moreMenuConfig: UIMoreMenuConfig<GetModelDeployResponse> = {
    items: [
      {
        label: '시작하기',
        action: 'start',
        auth: AUTH_KEY.DEPLOY.MODEL_DEPLOY_UPDATE,
        onClick: (params: GetModelDeployResponse) => {
          handleChangeModelDeployStatus(params.servingType, params.servingId, 'start');
        },
        visible: (rowData: GetModelDeployResponse) => {
          return rowData.status === 'Stopped' || rowData.status === 'Failed';
        },
      },
      {
        label: '중지하기',
        action: 'stop',
        auth: AUTH_KEY.DEPLOY.MODEL_DEPLOY_UPDATE,
        onClick: (params: GetModelDeployResponse) => {
          handleChangeModelDeployStatus(params.servingType, params.servingId, 'stop');
        },
        visible: (rowData: GetModelDeployResponse) => {
          return rowData.status === 'Available' || rowData.status === 'Deploying' || rowData.status === 'Unknown' || rowData.status === 'Failed';
        },
      },
      {
        label: '수정하기',
        action: 'edit',
        auth: AUTH_KEY.DEPLOY.MODEL_DEPLOY_UPDATE,
        onClick: (rowData: GetModelDeployResponse) => {
          handleEditClick(rowData);
        },
        visible: (rowData: GetModelDeployResponse) => {
          return rowData.status === 'Stopped';
        },
      },
      {
        label: '삭제하기',
        action: 'delete',
        auth: AUTH_KEY.DEPLOY.MODEL_DEPLOY_UPDATE,
        onClick: (rowData: any) => {
          handleDeleteModelCtlgBulk('single', rowData.servingId);
        },
      },
    ],
  };
  // 그리드 컬럼 정의
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
          textAlign: 'center' as const,
          display: 'flex' as const,
          alignItems: 'center' as const,
          justifyContent: 'center' as const,
        },
        sortable: false,
        suppressHeaderMenuButton: true,
        suppressSizeToFit: true,
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
        headerName: '운영 배포 여부',
        field: 'production',
        width: 120,
        cellRenderer: (params: any) => {
          return env.VITE_RUN_MODE !== RUN_MODE_TYPES.PROD ? (params.value ? '배포' : '미배포') : '배포';
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
        field: 'more', // 더보기 컬럼 필드명 (고정)
        width: 56,
      },
    ],
    []
  );

  return (
    <>
      <section className='section-page'>
        <UIPageHeader
          title='모델 배포'
          description={['배포한 모델의 정보를 확인하고 관리할 수 있습니다.', '배포 모델을 선택하여 간단한 사용방법과 시스템 로그를 확인해보세요.']}
          actions={
            <>
              {/* <Button
                auth={AUTH_KEY.DEPLOY.MODEL_DEPLOY_CREATE}
                className='btn-text-14-semibold-point'
                leftIcon={{ className: 'ic-system-24-add', children: '' }}
                onClick={() => deployModelLayerPopup.onOpen()}
              >
                모델 배포
              </Button> */}
            </>
          }
        />
        {/* 페이지 바디 */}
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
                        <td>
                          <div className='flex-1'>
                            <UIInput.Search
                              value={searchValues.searchKeyword}
                              onChange={e => {
                                setSearchValues(prev => ({ ...prev, searchKeyword: e.target.value }));
                              }}
                              placeholder='배포명, 모델명, 설명 입력'
                            />
                          </div>
                        </td>
                        <th>
                          <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                            상태
                          </UITypography>
                        </th>
                        <td>
                          <div className='flex-1'>
                            <UIDropdown
                              value={searchValues.status}
                              placeholder='조회 조건 선택'
                              options={statusOptions}
                              onSelect={value => handleDropdownSelect('status', value)}
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
            {/* 다중 선택 그리드 */}
            <UIListContainer>
              <UIListContentBox.Header>
                <div className='flex-shrink-0'>
                  <UIGroup gap={8} direction='row' align='start'>
                    <div style={{ width: '168px', paddingRight: '8px' }}>
                      <UIDataCnt count={modelList?.totalElements ?? 0} />
                    </div>
                  </UIGroup>
                </div>
                <div className='flex items-center gap-2'>
                  <div style={{ width: '180px', flexShrink: 0 }}>
                    <UIDropdown
                      value={String(searchValues.size)}
                      disabled={(modelList?.totalElements ?? 0) === 0}
                      options={[
                        { value: '12', label: '12개씩 보기' },
                        { value: '36', label: '36개씩 보기' },
                        { value: '60', label: '60개씩 보기' },
                      ]}
                      onSelect={(value: string) => updatePageSizeAndRefetch({ page: 1, size: Number(value) })}
                      height={40}
                      variant='dataGroup'
                    />
                  </div>
                  <UIToggle
                    variant='dataView'
                    disabled={(modelList?.totalElements ?? 0) === 0}
                    checked={searchValues.view === 'card'}
                    onChange={checked => setSearchValues(prev => ({ ...prev, view: checked ? 'card' : 'grid' }))}
                  />
                </div>
              </UIListContentBox.Header>
              <UIListContentBox.Body>
                {searchValues.view === 'grid' ? (
                  <UIGrid<GetModelDeployResponse>
                    type='multi-select'
                    loading={isLoading || isFetching}
                    // checkKeyName={'servingId'}
                    selectedDataList={selectedDataList}
                    rowData={rowData}
                    columnDefs={columnDefs}
                    onCheck={handleSelect}
                    onClickRow={(params: any) => {
                      handleRowClick(params.data.servingId);
                    }}
                    moreMenuConfig={moreMenuConfig}
                  />
                ) : (
                  <UICardList
                    loading={isLoading}
                    rowData={rowData}
                    flexType='none'
                    card={(item: any) => {
                      return (
                        <UIGridCard<GetModelDeployResponse>
                          id={item.servingId}
                          data={item}
                          onClick={() => {
                            handleRowClick(item.servingId);
                          }}
                          moreMenuConfig={moreMenuConfig}
                          title={item.name}
                          caption={item.description}
                          statusArea={
                            <UILabel variant='badge' intent={MODEL_DEPLOY_STATUS[item.status as keyof typeof MODEL_DEPLOY_STATUS].intent as UILabelIntent}>
                              {MODEL_DEPLOY_STATUS[item.status as keyof typeof MODEL_DEPLOY_STATUS].label}
                            </UILabel>
                          }
                          checkbox={{
                            checked: selectedDataList.includes(item),
                            onChange: (checked: boolean, value: string) => {
                              if (checked) setSelectedDataList([...selectedDataList, rowData[rowData.findIndex(data => data.servingId === value)]]);
                              else setSelectedDataList(selectedDataList.filter(data => data.servingId !== value));
                            },
                          }}
                          rows={[
                            { label: '모델명', value: item.modelName },
                            { label: '모델유형', value: item.type },
                            { label: '배포유형', value: item.servingType },
                            { label: '공개범위', value: item.publicStatus },
                          ]}
                        />
                      );
                    }}
                  />
                )}
              </UIListContentBox.Body>
              <UIListContentBox.Footer className='ui-data-has-btn'>
                <Button
                  auth={AUTH_KEY.DEPLOY.MODEL_DEPLOY_UPDATE}
                  className='btn-option-outlined'
                  disabled={(modelList?.totalElements ?? 0) === 0}
                  style={{ width: '40px' }}
                  onClick={() => {
                    handleDeleteModelCtlgBulk('bulk');
                  }}
                >
                  삭제
                </Button>

                <UIPagination
                  className='flex justify-center'
                  currentPage={searchValues.page}
                  hasNext={modelList?.hasNext}
                  totalPages={modelList?.totalPages ?? 1}
                  onPageChange={(page: number) => updatePageSizeAndRefetch({ page })}
                />
              </UIListContentBox.Footer>
            </UIListContainer>
          </UIArticle>
        </UIPageBody>
      </section>
      {/* {deployModelLayerPopup.currentStep > 0 && (
        <DeployModelCreatePopupPage
          {...deployModelLayerPopup}
          onClose={() => {
            refetch();
            deployModelLayerPopup.onClose();
          }}
        />
      )} */}
      {editData && editModelLayerPopup.currentStep > 0 && <DeployModelEdit {...editModelLayerPopup} data={editData} onClose={handleEditPageClose} />}
    </>
  );
};
