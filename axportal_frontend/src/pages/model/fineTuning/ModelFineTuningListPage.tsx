import { useAtom } from 'jotai';
import React, { memo, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';

import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';

import { UIDataCnt, UIToggle } from '@/components/UI';
import { UIBox, UILabel, type UILabelIntent, UIProgress, UITypography } from '@/components/UI/atoms';
import { UIPagination } from '@/components/UI/atoms/UIPagination';
import { UIDropdown, UIGroup, UIInput } from '@/components/UI/molecules';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { UICardList } from '@/components/UI/molecules/card/UICardList';
import { UIGridCard } from '@/components/UI/molecules/card/UIGridCard';
import { UIGrid } from '@/components/UI/molecules/grid/UIGrid/component';
import { UIListContainer } from '@/components/UI/molecules/list/UIListContainer/component';
import { UIListContentBox } from '@/components/UI/molecules/list/UIListContentBox';
import { useLayerPopup } from '@/hooks/common/layer';
import { useModal } from '@/stores/common/modal';

import { Button } from '@/components/common/auth';
import { AUTH_KEY } from '@/constants/auth';
import { env } from '@/constants/common/env.constants';
import { STORAGE_KEYS } from '@/constants/common/storage.constants.ts';
import { FINE_TUNING_STATUS_MAP } from '@/constants/model/fineTuningStatus.constants';
import { useBackRestoredState } from '@/hooks/common/navigation';
import { ModelFineTuningEditPopupPage } from '@/pages/model/fineTuning/ModelFineTuningEditPopupPage.tsx';
import { ModelFineTuningLogPopupPage } from '@/pages/model/fineTuning/ModelFineTuningLogPopupPage.tsx';
import { useDeleteFineTuningTraining, useGetFineTuningTrainings, useUpdateFineTuningStatus } from '@/services/model/fineTuning/modelFineTuning.service.ts';
import { FINE_TUNING_STATUS_LABELS, fineTuningSelectedIdsAtom, fineTuningWizardIsOpenAtom } from '@/stores/model/fineTuning/fineTuning.atoms.ts';
import { dateUtils } from '@/utils/common';
import { ModelFineTuningCreateWizard } from './ModelFineTuningCreateWizard';

type SearchValues = {
  page: number;
  size: number;
  sort: string;
  queryKey: string;
  filter: string | undefined;
  search: string | undefined;
  viewType: string;
  searchKeyword: string;
  status: string;
};

export const FineTuningListPage = () => {
  const navigate = useNavigate();
  const { openAlert, openConfirm, openModal } = useModal();
  const layerPopupOne = useLayerPopup();

  // Jotai atoms
  const [selectedIds, setSelectedIds] = useAtom(fineTuningSelectedIdsAtom);
  // const [pagination, setPagination] = useAtom(fineTuningPaginationAtom); // 서버 사이드 페이지네이션으로 대체됨
  const [, setIsWizardOpen] = useAtom(fineTuningWizardIsOpenAtom);

  const { filters: searchValues, updateFilters: setSearchValues } = useBackRestoredState<SearchValues>(STORAGE_KEYS.SEARCH_VALUES.MODEL_FINETUNING_LIST, {
    page: 1,
    size: 12,
    sort: 'created_at,desc',
    queryKey: 'finetuningList',
    filter: undefined,
    search: undefined,
    viewType: 'grid',
    searchKeyword: '',
    status: '전체',
  });

  // 수정 파인튜닝 ID
  const [editTrainingId, setEditTrainingId] = useState('');

  // useApiQuery를 사용한 데이터 가져오기 (서버 사이드 검색)
  const {
    data: fineTuningData,
    refetch: refetchFineTuningList,
    isFetching,
  } = useGetFineTuningTrainings(
    {
      page: searchValues.page,
      size: searchValues.size,
      sort: searchValues.sort,
      queryKey: searchValues.queryKey,
      filter: searchValues.filter,
      search: searchValues.search,
    },
    {
      enabled: !env.VITE_NO_PRESSURE_MODE,
    }
  );

  const updatePageSizeAndRefetch = (patch: Partial<Pick<SearchValues, 'page' | 'size'>>) => {
    setSearchValues(prev => ({ ...prev, ...patch }));
    setTimeout(() => refetchFineTuningList(), 0);
  };

  const gridData = useMemo(() => {
    if (!fineTuningData?.content) {
      return [];
    }

    return fineTuningData.content.map((item, index) => ({ ...item, no: (searchValues.page - 1) * searchValues.size + index + 1 }));
  }, [fineTuningData]);

  const handleTuningRegister = () => {
    setIsWizardOpen(true); // wizard 열기
  };

  const handleItemClick = (itemId: string) => {
    navigate(`${itemId}`);
  };

  const handleSearch = () => {
    if (
      searchValues.page !== 1 ||
      searchValues.filter !== (searchValues.status && searchValues.status !== '전체' ? `status:${searchValues.status}` : undefined) ||
      searchValues.search !== (searchValues.searchKeyword ? searchValues.searchKeyword : undefined)
    ) {
      setSearchValues(prev => ({
        ...prev,
        page: 1,
        filter: searchValues.status && searchValues.status !== '전체' ? `status:${searchValues.status}` : undefined,
        search: searchValues.searchKeyword ? searchValues.searchKeyword : undefined,
      }));

      if (env.VITE_NO_PRESSURE_MODE) {
        // NO_PRESSURE_MODE에서는 조회 버튼에서만 조회되도록 refetch를 여기서 수행
        setTimeout(() => refetchFineTuningList(), 0);
      }
    } else {
      refetchFineTuningList();
    }
  };

  const handleMetricView = () => {
    // 메트릭 뷰 기능 구현 예정
    if (selectedIds.length === 0) {
      openAlert({
        message: '파인튜닝 목록을 선택해 주세요.',
      });
      return;
    } else if (selectedIds.length > 10) {
      openAlert({
        message: '최대 10개까지 선택할 수 있습니다.',
      });
      return;
    }
    navigate(`/model/finetuning/metrics`);
  };

  const handlePageChange = (page: number) => {
    updatePageSizeAndRefetch({ page });
  };

  const handlePageSizeChange = (newPageSize: number) => {
    updatePageSizeAndRefetch({ page: 1, size: newPageSize });
  };

  // 드롭다운 상태 관리 (로컬 상태 유지)
  const [dropdownStates, setDropdownStates] = useState({
    dateType: false,
    searchType: false,
    tuningType: false,
    modelType: false,
  });

  // 날짜 포맷팅 함수
  // const formatDateTime = (dateValue: string) => {
  //   if (!dateValue) return '-';
  //
  //   const date = new Date(dateValue);
  //   const year = date.getFullYear();
  //   const month = String(date.getMonth() + 1).padStart(2, '0');
  //   const day = String(date.getDate()).padStart(2, '0');
  //   const hours = String(date.getHours()).padStart(2, '0');
  //   const minutes = String(date.getMinutes()).padStart(2, '0');
  //   const seconds = String(date.getSeconds()).padStart(2, '0');
  //
  //   return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
  // };

  // React Query 훅 제거됨 - 직접 API 호출로 대체
  // 클라이언트 사이드 필터링 제거 - 서버 사이드 필터링으로 변경

  // 기존 useState 제거됨 - Jotai atoms 사용

  // 초기 데이터 로드는 useGetFineTuningTrainings Hook이 자동으로 처리

  // 자동 필터링 제거 - 조회 버튼 클릭 시에만 필터 적용

  // 드롭다운 핸들러
  const handleDropdownToggle = (key: keyof typeof dropdownStates) => {
    setDropdownStates(prev => ({
      ...Object.keys(prev).reduce((acc, k) => ({ ...acc, [k]: false }), {} as typeof prev),
      [key]: !prev[key],
    }));
  };

  const handleDropdownSelect = (key: string, value: string) => {
    switch (key) {
      // case 'dateType':
      //   setDateType(value);
      //   break;
      // case 'searchType': // UI에서 사용하지 않음
      //   setSearchType(value);
      //   break;
      case 'status':
        setSearchValues(prev => ({ ...prev, status: value }));
        break;
    }
    setDropdownStates(prev => ({ ...prev, [key]: false }));
  };

  // 옵션 정의
  // const dateTypeOptions = [
  //   { value: '생성일시', label: '생성일시' },
  //   { value: '최종 수정일시', label: '최종 수정일시' },
  // ];

  // const searchTypeOptions = [
  //   { value: '이름', label: '이름' },
  //   { value: '설명', label: '설명' },
  // ];

  const statusOptions = [
    { value: '전체', label: '전체' },
    ...Object.entries(FINE_TUNING_STATUS_LABELS).map(([key, label]) => ({
      value: key,
      label: label,
    })),
  ];

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
        headerName: '이름',
        field: 'name',
        width: 272,
        minWidth: 272,
        maxWidth: 272,
        suppressSizeToFit: true,
      },
      {
        headerName: '상태',
        field: 'status',
        width: 120,
        cellRenderer: React.memo((params: any) => {
          return (
            <UILabel variant='badge' intent={FINE_TUNING_STATUS_MAP[params.value as keyof typeof FINE_TUNING_STATUS_MAP]?.intent as UILabelIntent}>
              {FINE_TUNING_STATUS_MAP[params.value as keyof typeof FINE_TUNING_STATUS_MAP]?.label}
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
        headerName: '진행율',
        field: 'progress',
        width: 370,
        cellRenderer: memo((params: any) => {
          const status = params.data.status;
          const percentage = params.value?.percentage || 0;
          return <UIProgress value={percentage} status={status === 'error' ? 'error' : 'normal'} showPercent={true} />;
        }),
      },
      {
        headerName: '공개범위',
        field: 'publicStatus',
        width: 180,
      },
      {
        headerName: '생성일시',
        field: 'createdAt',
        width: 180,
        cellStyle: {
          paddingLeft: 16,
        },
        valueGetter: (params: any) => {
          if (!params.data.createdAt) return '-';
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
          if (!params.data.updatedAt) return '-';
          return dateUtils.formatDate(params.data.updatedAt, 'datetime');
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

  // 파인튜닝 status 변경 훅
  const { mutate: updateFineTuningStatus, isPending: isUpdating } = useUpdateFineTuningStatus({
    onSuccess: data => {
      // console.log('파인튜닝 상태 변경 성공:', data);
      openAlert({
        title: '성공',
        message: `"${data.data?.name || '파인튜닝'}" 상태가 변경되었습니다.`,
      });
      // 목록 새로고침
      refetchFineTuningList();
    },
    onError: error => {
      // console.error('파인튜닝 상태 변경 실패:', error);
      openAlert({
        title: '오류',
        message: `상태 변경에 실패했습니다: ${error.message || '알 수 없는 오류'}`,
      });
    },
  });

  // 각 액션별 핸들러 함수들
  const handleStart = (rowData: any) => {
    // console.log('시작:', rowData);
    // status만 변경하여 전송
    const updateData = {
      id: rowData.id,
      status: 'starting',
      // scalingGroup: trainingConfig?.scaling_group || undefined,
      scalingGroup: rowData?.resource?.scaling_group,
    };
    updateFineTuningStatus(updateData);
  };

  const handleModify = (rowData: any) => {
    setEditTrainingId(rowData?.id);
    // 수정 팝업 열기
    layerPopupOne.onOpen();
  };

  const handleStop = (rowData: any) => {
    // console.log('정지:', rowData);

    // status만 변경하여 전송
    const updateData = {
      id: rowData.id,
      status: 'stopping',
    };

    // console.log('정지 updateData', updateData);
    // PUT /trainings/{trainingId} API 호출
    updateFineTuningStatus(updateData);
  };

  const handleLogs = (rowData: any) => {
    openModal({
      title: '콘솔 로그',
      type: 'large',
      body: <ModelFineTuningLogPopupPage trainingId={rowData.id} />,
      showFooter: false,
    });
  };

  // 삭제 mutation
  const { mutateAsync: deleteTrainingAsync } = useDeleteFineTuningTraining({
    onSuccess: () => { },
    onError: /* err */ () => {
      // console.error('파인튜닝 삭제 실패:', err);
      openAlert({
        title: '안내',
        message: '삭제에 실패했습니다.',
      });
    },
  });

  const handleDelete = (rowData: any) => {
    // console.log('삭제:', rowData);

    if (!rowData?.id) {
      openAlert({
        title: '안내',
        message: '삭제할 항목을 선택해주세요.',
        confirmText: '확인',
      });
      return;
    }

    openConfirm({
      title: '안내',
      message: `"${rowData.name}"을(를) 삭제하시겠어요?\n삭제한 정보는 복구할 수 없습니다.`,
      confirmText: '예',
      cancelText: '아니요',
      onConfirm: async () => {
        await deleteTrainingAsync({ id: rowData.id });

        openAlert({
          title: '완료',
          message: '파인튜닝이 삭제되었습니다.',
          onConfirm: () => {
            // 목록 새로고침
            refetchFineTuningList();
          },
        });
      },
      onCancel: () => {
        // console.log('취소됨');
      },
    });
  };

  const handleDeleteMultiple = () => {
    // console.log('handleDeleteMultiple', selectedIds);

    if (!selectedIds || selectedIds.length === 0) {
      openAlert({
        title: '안내',
        message: '삭제할 항목을 선택해주세요.',
        confirmText: '확인',
      });
      return;
    }

    openConfirm({
      title: '안내',
      message: '삭제하시겠어요?\n삭제한 정보는 복구할 수 없습니다.',
      cancelText: '취소',
      confirmText: '삭제',
      onConfirm: async () => {
        // console.log('handleDeleteMultiple', selectedIds);
        // 삭제 시작 - 병렬 처리
        try {
          await Promise.all(selectedIds.map(selectedId => deleteTrainingAsync({ id: selectedId })));
          // 모든 삭제가 성공한 경우
          openAlert({
            title: '안내',
            message: `${selectedIds.length}개의 항목이 삭제되었습니다.`,
            onConfirm: () => {
              // 목록 새로고침
              refetchFineTuningList();
            },
          });
          // 선택 항목 초기화
          setSelectedIds([]);
        } catch (error) {
          // console.error('파인튜닝 다중 삭제 실패:', error);
          openAlert({
            title: '안내',
            message: '일부 항목 삭제에 실패했습니다.',
          });
          // 일부 실패해도 목록 새로고침
          refetchFineTuningList();
        }
      },
      onCancel: () => {
        // console.log('취소됨');
      },
    });
  };

  const handleInitialize = (rowData: any) => {
    // console.log('초기화 진행:', rowData);

    // status만 변경하여 전송
    const updateData = {
      id: rowData.id,
      status: 'initialized',
    };

    // console.log('초기화 updateData', updateData);
    // PUT /trainings/{trainingId} API 호출
    updateFineTuningStatus(updateData);
  };

  const handleResume = (rowData: any) => {
    // console.log('학습 이어하기:', rowData);

    // status만 변경하여 전송
    const updateData = {
      id: rowData.id,
      status: 'training',
    };

    // console.log('학습 이어하기 updateData', updateData);
    // PUT /trainings/{trainingId} API 호출
    updateFineTuningStatus(updateData);
  };

  // 더보기 메뉴 설정 (동적)
  const moreMenuConfig = useMemo(
    () => ({
      items: [
        // 시작 - Initialized, error 상태에서만 표시
        {
          label: '시작',
          action: 'start',
          auth: AUTH_KEY.MODEL.FINE_TUNING_UPDATE,
          onClick: (rowData: any) => handleStart(rowData),
          disabled: isUpdating,
          visible: (rowData: any) => ['initialized', 'error'].includes(rowData.status),
        },
        // 수정 - Initialized, error 상태에서만 표시
        {
          label: '수정',
          action: 'modify',
          auth: AUTH_KEY.MODEL.FINE_TUNING_UPDATE,
          onClick: (rowData: any) => handleModify(rowData),
          visible: (rowData: any) => ['initialized', 'error'].includes(rowData.status),
        },
        // 정지 - Starting, Resource-allocating, Resource-allocated, Training 상태에서만 표시
        {
          label: '정지',
          action: 'stop',
          auth: AUTH_KEY.MODEL.FINE_TUNING_UPDATE,
          onClick: (rowData: any) => handleStop(rowData),
          visible: (rowData: any) => ['starting', 'resource-allocating', 'resource-allocated', 'training'].includes(rowData.status),
        },
        // 초기화 진행 - Stopped 상태에서만 표시
        {
          label: '초기화 진행',
          action: 'initialize',
          auth: AUTH_KEY.MODEL.FINE_TUNING_UPDATE,
          onClick: (rowData: any) => handleInitialize(rowData),
          visible: (rowData: any) => rowData.status === 'stopped',
        },
        // 학습 이어하기 - Stopped 상태에서만 표시
        {
          label: '학습 이어하기',
          action: 'resume',
          auth: AUTH_KEY.MODEL.FINE_TUNING_UPDATE,
          onClick: (rowData: any) => handleResume(rowData),
          visible: (rowData: any) => rowData.status === 'stopped',
        },
        // 로그 조회 - 모든 상태에서 표시
        {
          label: '로그 조회',
          action: 'logs',
          onClick: (rowData: any) => handleLogs(rowData),
          visible: () => true,
        },
        // 삭제 - 모든 상태에서 표시
        {
          label: '삭제',
          action: 'delete',
          auth: AUTH_KEY.MODEL.FINE_TUNING_DELETE,
          onClick: (rowData: any) => handleDelete(rowData),
          visible: () => true,
        },
      ],
    }),
    []
  );

  return (
    <>
      <section className='section-page'>
        <UIPageHeader
          title='파인튜닝'
          description={[
            '모델의 파인튜닝 작업을 실행하고 각 작업의 상세 내용을 조회할 수 있습니다.',
            '파인튜닝 작업을 선택하여 학습에 사용한 데이터세트와 학습 로그를 확인해 보세요.',
          ]}
          actions={
            <>
              <Button
                auth={AUTH_KEY.MODEL.FINE_TUNING_CREATE}
                className='btn-text-18-semibold-point'
                leftIcon={{ className: 'ic-system-24-add', children: '' }}
                onClick={handleTuningRegister}
              >
                파인튜닝 등록
              </Button>
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
                          <UIInput.Search
                            value={searchValues.searchKeyword}
                            onChange={e => {
                              setSearchValues(prev => ({ ...prev, searchKeyword: e.target.value }));
                            }}
                            onKeyDown={e => {
                              if (e.key === 'Enter') {
                                handleSearch();
                              }
                            }}
                            placeholder='검색어 입력'
                          />
                        </td>
                        <th>
                          <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                            상태
                          </UITypography>
                        </th>
                        <td>
                          <div>
                            <UIDropdown
                              value={searchValues.status || '전체'}
                              placeholder='조회 조건 선택'
                              options={statusOptions}
                              isOpen={dropdownStates.tuningType}
                              onClick={() => handleDropdownToggle('tuningType')}
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
                      <UIDataCnt count={fineTuningData?.totalElements || 0} prefix='총' unit='건' />
                    </div>
                  </UIGroup>
                </div>
                <div className='flex items-center gap-2'>
                  <Button className='btn-tertiary-outline' disabled={!(gridData.length > 0)} onClick={handleMetricView}>
                    매트릭 뷰
                  </Button>
                  <div style={{ width: '180px', flexShrink: 0 }}>
                    <UIDropdown
                      value={searchValues.size.toString()}
                      options={[
                        { value: '12', label: '12개씩 보기' },
                        { value: '36', label: '36개씩 보기' },
                        { value: '60', label: '60개씩 보기' },
                      ]}
                      onSelect={(value: string) => {
                        handlePageSizeChange(Number(value));
                      }}
                      onClick={() => console.log('onClick')}
                      height={40}
                      variant='dataGroup'
                      disabled={!(gridData.length > 0)}
                    />
                  </div>

                  <UIToggle
                    variant='dataView'
                    checked={searchValues.viewType === 'card'}
                    onChange={checked => {
                      setSearchValues(prev => ({ ...prev, viewType: checked ? 'card' : 'grid' }));
                    }}
                    disabled={!(gridData.length > 0)}
                  />
                </div>
              </UIListContentBox.Header>
              <UIListContentBox.Body>
                {searchValues.viewType === 'grid' ? (
                  <UIGrid<any>
                    type='multi-select'
                    loading={isFetching}
                    rowData={gridData}
                    columnDefs={columnDefs}
                    moreMenuConfig={moreMenuConfig}
                    onClickRow={(params: any) => {
                      // console.log('다중 onClickRow', params);
                      handleItemClick(params.data?.id);
                    }}
                    onCheck={(selectedItems: any[]) => {
                      // console.log('다중 onSelect', selectedIds);
                      setSelectedIds(selectedItems.map(item => item.id));
                    }}
                  />
                ) : (
                  <UICardList
                    rowData={gridData}
                    loading={isFetching}
                    flexType='none'
                    card={(item: any) => {
                      return (
                        <UIGridCard<any>
                          id={item.id}
                          title={item.name}
                          caption={item.description}
                          progressValue={item.progress?.percentage || 0}
                          data={item} // 카드형 더보기 추가시
                          moreMenuConfig={moreMenuConfig} // 카드형 더보기 추가시
                          statusArea={
                            <UILabel variant='badge' intent={FINE_TUNING_STATUS_MAP[item.status as keyof typeof FINE_TUNING_STATUS_MAP]?.intent as UILabelIntent}>
                              {FINE_TUNING_STATUS_MAP[item.status as keyof typeof FINE_TUNING_STATUS_MAP]?.label}
                            </UILabel>
                          }
                          checkbox={{
                            checked: selectedIds.includes(item.id),
                            onChange: (checked: boolean /* , value: string */) => {
                              // console.log('checked', checked, value);
                              if (checked) {
                                setSelectedIds([...selectedIds, item.id]);
                              } else {
                                setSelectedIds(selectedIds.filter(id => id !== item.id));
                              }
                            },
                          }}
                          rows={[
                            { label: '생성일시', value: dateUtils.formatDate(item.createdAt, 'datetime') },
                            { label: '최종 수정일시', value: dateUtils.formatDate(item.updatedAt, 'datetime') },
                          ]}
                          onClick={() => handleItemClick(item.id)}
                        />
                      );
                    }}
                  />
                )}
              </UIListContentBox.Body>
              <UIListContentBox.Footer className='ui-data-has-btn'>
                <Button
                  auth={AUTH_KEY.MODEL.FINE_TUNING_DELETE}
                  className='btn-option-outlined'
                  style={{ width: '40px' }}
                  disabled={!(gridData.length > 0)}
                  onClick={handleDeleteMultiple}
                >
                  삭제
                </Button>
                <UIPagination hasNext={fineTuningData?.hasNext} currentPage={searchValues.page} totalPages={fineTuningData?.totalPages || 1} onPageChange={handlePageChange} className='flex justify-center' />
              </UIListContentBox.Footer>
            </UIListContainer>
          </UIArticle>
        </UIPageBody>
      </section>

      {/* 파인튜닝 생성 Wizard */}
      <ModelFineTuningCreateWizard
        onClose={() => {
          refetchFineTuningList();
        }}
      />

      {/* 파인튜닝 수정 팝업 */}
      {layerPopupOne.currentStep === 1 && (
        <ModelFineTuningEditPopupPage
          isPopupOpen={layerPopupOne.currentStep === 1}
          onClose={() => {
            layerPopupOne.onClose();
          }}
          onSuccess={() => {
            refetchFineTuningList();
          }}
          trainingId={editTrainingId}
        />
      )}
    </>
  );
};
