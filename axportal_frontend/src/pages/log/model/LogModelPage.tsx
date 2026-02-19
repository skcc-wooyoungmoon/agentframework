import React, { useMemo, useState } from 'react';

import { UIBox, UIButton2, UIDataCnt, UILabel, UIToggle, UITypography } from '@/components/UI';
import { UIPagination } from '@/components/UI/atoms/UIPagination';
import { UIDropdown, UIGroup } from '@/components/UI/molecules';
import { UICardList } from '@/components/UI/molecules/card/UICardList';
import { UIGridCard } from '@/components/UI/molecules/card/UIGridCard';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIInput } from '@/components/UI/molecules/input';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { env } from '@/constants/common/env.constants';
import { STORAGE_KEYS } from '@/constants/common/storage.constants.ts';
import { MODEL_DEPLOY_STATUS } from '@/constants/deploy/modelDeploy.constants';
import { useBackRestoredState } from '@/hooks/common/navigation';
import { useGetModelDeployList } from '@/services/deploy/model/modelDeploy.services';
import { dateUtils } from '@/utils/common';
import { useNavigate } from 'react-router-dom';

interface SearchValues {
  page: number;
  size: number;
  searchKeyword: string;
  status: string;
}

export const LogModelPage = () => {
  const navigate = useNavigate();

  // 드롭다운 상태 관리
  const [dropdownStates, setDropdownStates] = useState({
    status: false,
  });

  // search 타입
  const [searchText, setSearchText] = useState('');
  const [searchStatus, setSearchStatus] = useState('all');

  const [view, setView] = useState('grid');

  // 검색 조건
  const { filters: searchValues, updateFilters: setSearchValues } = useBackRestoredState<SearchValues>(STORAGE_KEYS.SEARCH_VALUES.MODEL_DEPLOY_LOG_LIST, {
    page: 1,
    size: 12,
    searchKeyword: '',
    status: 'all',
  });

  // 목록 조회
  const {
    data: modelList,
    refetch,
    isFetching,
  } = useGetModelDeployList(
    {
      page: searchValues.page - 1,
      size: searchValues.size,
      filter: searchValues.status === 'all' ? '' : `status:${searchValues.status}`,
      search: searchValues.searchKeyword,
      queryKey: 'log-model',
    },
    {
      enabled: !env.VITE_NO_PRESSURE_MODE,
    }
  );

  const updatePageSizeAndRefetch = (patch: Partial<Pick<SearchValues, 'page' | 'size'>>) => {
    setSearchValues(prev => ({ ...prev, ...patch }));
    setTimeout(() => refetch(), 0);
  };

  // 실제 데이터 변환
  const rowData = useMemo(() => {
    return (
      modelList?.content.map((item, index) => ({
        id: item.servingId,
        no: (searchValues.page - 1) * searchValues.size + index + 1,
        ...item,
        createdAt: item.createdAt ? dateUtils.formatDate(item.createdAt, 'datetime') : '',
        updatedAt: item.updatedAt ? dateUtils.formatDate(item.updatedAt, 'datetime') : '',
      })) ?? []
    );
  }, [modelList, searchValues.page, searchValues.size]);

  const handleSearch = () => {
    setSearchValues(prev => ({ ...prev, searchKeyword: searchText, status: searchStatus }));

    setTimeout(() => refetch(), 0);
  };

  // 드롭다운 핸들러
  const handleDropdownToggle = (key: keyof typeof dropdownStates) => {
    setDropdownStates(prev => ({
      ...Object.keys(prev).reduce((acc, k) => ({ ...acc, [k]: false }), {} as typeof prev),
      [key]: !prev[key],
    }));
  };

  const handleDropdownSelect = (key: keyof SearchValues, value: string) => {
    setDropdownStates(prev => ({ ...prev, [key]: false }));
    setSearchStatus(value);
  };

  // 검색 조건 변경 시 자동 refetch
  // useEffect(() => {
  //   refetch();
  // }, [searchValues.page, searchValues.size, searchValues.status, searchValues.searchKeyword, refetch]);

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

  // 그리드 컬럼 정의
  const columnDefs = useMemo(
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
      },
      {
        headerName: '배포명',
        field: 'name',
        width: 272,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: '모델명',
        field: 'modelName',
        width: 272,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: '상태',
        field: 'status',
        width: 120,
        cellRenderer: React.memo((params: any) => {
          const statusConfig = MODEL_DEPLOY_STATUS[params.value as keyof typeof MODEL_DEPLOY_STATUS];
          return (
            <UILabel variant='badge' intent={(statusConfig?.intent as any) || 'complete'}>
              {statusConfig?.label || params.value}
            </UILabel>
          );
        }),
      },
      {
        headerName: '설명',
        field: 'description',
        minWidth: 400,
        flex: 1,
      },
      {
        headerName: '모델유형',
        field: 'type',
        width: 120,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: '배포유형',
        field: 'servingType',
        width: 120,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: '운영 배포 여부',
        field: 'production',
        width: 120,
        cellStyle: {
          paddingLeft: '16px',
        },
        cellRenderer: React.memo((params: any) => {
          return params.value ? '배포' : '미배포';
        }),
      },
      {
        headerName: '공개범위',
        field: 'publicStatus',
        width: 120,
        cellStyle: {
          paddingLeft: '16px',
        },
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
    []
  );

  return (
    <>
      {/* 섹션 페이지 */}
      <section className='section-page'>
        <UIPageHeader title='모델 사용 로그' description='배포된 모델의 사용 로그를 확인할 수 있습니다.' />

        {/* 페이지 바디 */}
        <UIPageBody>
          {/* 검색 영역 */}
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
                              value={searchText}
                              placeholder={'배포명, 모델명, 설명 입력'}
                              onChange={e => {
                                setSearchText(e.target.value);
                              }}
                              onKeyDown={e => {
                                if (e.key === 'Enter') {
                                  handleSearch();
                                }
                              }}
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
                              value={searchStatus}
                              placeholder='조회 조건 선택'
                              options={statusOptions}
                              isOpen={dropdownStates.status}
                              onClick={() => handleDropdownToggle('status')}
                              onSelect={value => handleDropdownSelect('status', value)}
                            />
                          </div>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
                <div style={{ width: '128px' }}>
                  <UIButton2 className='btn-secondary-blue' onClick={handleSearch} style={{ width: '100%' }}>
                    조회
                  </UIButton2>
                </div>
              </UIGroup>
            </UIBox>
          </UIArticle>

          {/* 그리드 */}
          <UIArticle className='article-grid'>
            <UIListContainer>
              <UIListContentBox.Header>
                <div className='article-header'>
                  <div className='grid-header-left'>
                    <UIDataCnt count={modelList?.totalElements} />
                  </div>
                </div>
                <div className='flex items-center gap-2'>
                  <div style={{ width: '160px', flexShrink: 0 }}>
                    <UIDropdown
                      value={String(searchValues.size)}
                      options={[
                        { value: '12', label: '12개씩 보기' },
                        { value: '36', label: '36개씩 보기' },
                        { value: '60', label: '60개씩 보기' },
                      ]}
                      onSelect={(value: string) => updatePageSizeAndRefetch({ size: Number(value) })}
                      height={40}
                      variant='dataGroup'
                      width='w-40'
                      disabled={!(rowData?.length > 0)}
                    />
                  </div>
                  <UIToggle variant='dataView' checked={view === 'card'} onChange={checked => setView(checked ? 'card' : 'grid')} disabled={!(rowData?.length > 0)} />
                </div>
              </UIListContentBox.Header>
              <UIListContentBox.Body>
                {view === 'grid' ? (
                  <UIGrid
                    // type='multi-select'
                    loading={isFetching}
                    rowData={rowData}
                    columnDefs={columnDefs as any}
                    onClickRow={(params: any) => {
                      // console.log('다중 onClickRow', params);

                      if (params?.data?.id) {
                        navigate(params?.data?.id);
                      }
                    }}
                  // onCheck={(selectedIds: any[]) => {
                  //   console.log('다중 onSelect', selectedIds);
                  // }}
                  />
                ) : (
                  <UICardList
                    loading={isFetching}
                    rowData={rowData}
                    flexType='none'
                    card={(item: any) => {
                      // console.log(item);
                      const statusConfig = MODEL_DEPLOY_STATUS[item.status as keyof typeof MODEL_DEPLOY_STATUS];

                      return (
                        <UIGridCard
                          id={item.id}
                          title={item.name}
                          caption={item.description}
                          statusArea={
                            <UILabel variant='badge' intent={(statusConfig?.intent as any) || 'complete'}>
                              {statusConfig?.label || item.status}
                            </UILabel>
                          }
                          rows={[
                            { label: '모델명', value: item.name },
                            { label: '모델유형', value: item.type },
                            { label: '배포유형', value: item.servingType },
                            { label: '공개범위', value: item.isPrivate ? '비공개' : '공개' },
                          ]}
                          onClick={() => {
                            if (item?.servingId) {
                              navigate(item.servingId);
                            }
                          }}
                        />
                      );
                    }}
                  />
                )}
              </UIListContentBox.Body>
              <UIListContentBox.Footer>
                <UIPagination
                  currentPage={searchValues.page}
                  totalPages={modelList?.totalPages || 1}
                  onPageChange={(page: number) => updatePageSizeAndRefetch({ page })}
                  className='flex justify-center'
                  hasNext={modelList?.hasNext}
                />
              </UIListContentBox.Footer>
            </UIListContainer>
          </UIArticle>
        </UIPageBody>
      </section>
    </>
  );
};
