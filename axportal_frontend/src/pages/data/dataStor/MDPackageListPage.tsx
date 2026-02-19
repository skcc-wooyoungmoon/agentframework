import { useMemo, useEffect, useState } from 'react';

import { UIInput, UIGroup } from '@/components/UI/molecules';
import { UIBox, UIButton2, UIDataCnt, UITypography } from '@/components/UI/atoms';
import { UIPagination } from '@/components/UI/atoms/UIPagination';
import { UIUnitGroup } from '@/components/UI/molecules';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { useNavigate } from 'react-router-dom';
import { UIDropdown } from '@/components/UI/molecules/dropdown/UIDropdown';
import { STORAGE_KEYS } from '@/constants/common/storage.constants';
import { useBackRestoredState } from '@/hooks/common/navigation';
import { useGetMDPackageList, useGetOriginSystems } from '@/services/data/storage/dataStorage.services';
import type { GetMDPackageListRequest } from '@/services/data/storage/types';

interface MDPackageListPageProps {
  isActiveTab?: boolean;
}

interface SearchValues {
  page: number;
  countPerPage: number;
  searchKeyword: string;
  category: string;
}

type SearchParams = Required<Pick<GetMDPackageListRequest, 'page' | 'countPerPage'>> & Omit<GetMDPackageListRequest, 'page' | 'countPerPage'>;

// ================================
// 컴포넌트
// ================================

/**
 * MD 패키지 목록 페이지
 */
export function MDPackageListPage({ isActiveTab }: MDPackageListPageProps) {
  // ================================
  // Hooks
  // ================================

  const navigate = useNavigate();

  // ================================
  // State 관리
  // ================================

  // 검색 조건
  const { filters: searchForm, updateFilters: setSearchForm } = useBackRestoredState<SearchValues>(STORAGE_KEYS.SEARCH_VALUES.MD_PACKAGE_LIST, {
    page: 1,
    countPerPage: 12,
    searchKeyword: '',
    category: '전체',
  });

  // 실제 API 호출에 사용할 검색 조건 (검색 버튼을 눌렀을 때만 업데이트)
  const [appliedSearchParams, setAppliedSearchParams] = useState<Pick<SearchParams, 'searchWord' | 'originSystemCd'>>({
    searchWord: searchForm.searchKeyword ?? '',
    originSystemCd: searchForm.category === '전체' ? '' : searchForm.category,
  });

  // searchParams 설정 (페이지네이션과 페이지당 개수 변경 시에만 업데이트)
  const searchParams = useMemo<SearchParams>(
    () => ({
      page: searchForm.page,
      countPerPage: searchForm.countPerPage,
      searchWord: appliedSearchParams.searchWord,
      originSystemCd: appliedSearchParams.originSystemCd,
    }),
    [searchForm.page, searchForm.countPerPage, appliedSearchParams.searchWord, appliedSearchParams.originSystemCd]
  );

  // ================================
  // API 호출
  // ================================

  const { data, refetch, isLoading } = useGetMDPackageList(searchParams, { enabled: isActiveTab });

  useEffect(() => {
    refetch();
  }, [searchParams.page, searchParams.countPerPage]);
  const originSystemsQuery = useGetOriginSystems({ enabled: true });

  // ================================
  // 이벤트 핸들러
  // ================================

  const handleSearch = () => {
    // 검색 버튼을 눌렀을 때만 검색 조건을 적용하고 API 호출
    setAppliedSearchParams({
      searchWord: searchForm.searchKeyword ?? '',
      originSystemCd: searchForm.category === '전체' ? '' : searchForm.category,
    });
    setSearchForm(prev => ({ ...prev, page: 1 }));
  };

  const handleDropdownSelect = (key: keyof SearchValues, value: string) => {
    setSearchForm(prev => ({ ...prev, [key]: value }));
  };

  // ================================
  // 원천 시스템 드롭다운 옵션 생성
  // ================================

  const originSystemOptions = useMemo(() => {
    const defaultOptions = [{ value: '전체', label: '전체' }];

    // API 응답이 있고 데이터가 있는 경우
    if (originSystemsQuery.data?.datasetReferList && originSystemsQuery.data.datasetReferList.length > 0) {
      const apiOptions = originSystemsQuery.data.datasetReferList.map(item => ({
        value: item.datasetcardReferCd,
        label: item.datasetcardReferNm,
      }));
      // console.log('[API 데이터로 옵션 생성]', apiOptions);
      return [...defaultOptions, ...apiOptions];
    }

    // API 에러 또는 데이터가 없는 경우 '전체'만 노출
    return defaultOptions;
  }, [originSystemsQuery.data]);

  // ================================
  // 그리드 컬럼 정의
  // ================================

  const columnDefs: any = useMemo(
    () => [
      {
        headerName: 'NO',
        field: 'no',
        width: 80,
        minWidth: 80,
        maxWidth: 80,
        cellClass: 'text-center',
        headerClass: 'text-center',
        cellStyle: {
          textAlign: 'center',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
        },
        sortable: false,
        suppressHeaderMenuButton: false,
        suppressSizeToFit: true,
        valueGetter: (params: { node: { rowIndex: number } }) => {
          return (searchForm.page - 1) * searchForm.countPerPage + params.node.rowIndex + 1;
        },
      },
      {
        headerName: '이름',
        field: 'datasetName',
        width: 400,
        cellStyle: {
          paddingLeft: '16px',
        },
        valueGetter: (params: any) => {
          return params.data?.datasetName || '';
        },
        sortable: false,
      },
      {
        headerName: '요약',
        field: 'datasetCardSummary',
        width: 800,
        valueGetter: (params: any) => {
          return params.data?.datasetCardSummary || '';
        },
        sortable: false,
      },
      {
        headerName: '원천 시스템',
        field: 'originSystemName',
        flex: 1,
        valueGetter: (params: any) => {
          return params.data?.originSystemName || '';
        },
        sortable: false,
      },
    ],
    [searchForm.page, searchForm.countPerPage]
  );

  // ================================
  // 렌더링
  // ================================

  return (
    <>
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
                      <div>
                        <UIInput.Search
                          value={searchForm.searchKeyword}
                          placeholder='이름 입력'
                          onChange={e => {
                            setSearchForm(prev => ({ ...prev, searchKeyword: e.target.value }));
                          }}
                        />
                      </div>
                    </td>
                    <th>
                      <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                        원천시스템
                      </UITypography>
                    </th>
                    <td>
                      <div>
                        <UIDropdown value={searchForm.category} placeholder='전체' options={originSystemOptions} onSelect={value => handleDropdownSelect('category', value)} />
                      </div>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
            <div style={{ width: '128px' }}>
              <UIButton2 className='btn-secondary-blue' style={{ width: '100%' }} onClick={handleSearch}>
                조회
              </UIButton2>
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
                      <UIDataCnt count={data?.totalElements || 0} prefix='총' />
                    </div>
                  </div>
                  <div style={{ width: '180px', flexShrink: 0 }}>
                    <UIDropdown
                      value={String(searchForm.countPerPage)}
                      disabled={(data?.totalElements || 0) === 0}
                      options={[
                        { value: '12', label: '12개씩 보기' },
                        { value: '36', label: '36개씩 보기' },
                        { value: '60', label: '60개씩 보기' },
                      ]}
                      onSelect={(value: string) => {
                        setSearchForm(prev => ({ ...prev, page: 1, countPerPage: Number(value) }));
                      }}
                      height={40}
                      variant='dataGroup'
                    />
                  </div>
                </div>
              </UIUnitGroup>
            </div>
          </UIListContentBox.Header>
          <UIListContentBox.Body>
            <UIGrid
              type='default'
              loading={isLoading}
              rowData={data?.content || []}
              columnDefs={columnDefs}
              onClickRow={(params: any) => {
                const datasetCd = params?.data?.datasetCd;
                if (datasetCd && datasetCd !== null) {
                  navigate(`/data/dataStor/md-package/${datasetCd}`, {
                    state: {
                      packageInfo: {
                        name: params?.data?.datasetName,
                        summary: params?.data?.datasetCardSummary,
                        sourceSystem: params?.data?.originSystemName,
                      },
                    },
                  });
                }
              }}
            />
          </UIListContentBox.Body>
          <UIListContentBox.Footer>
            <UIPagination
              currentPage={searchForm.page}
              totalPages={data?.totalPages || 1}
              onPageChange={(page: number) => {
                setSearchForm(prev => ({ ...prev, page }));
              }}
              className='flex justify-center'
            />
          </UIListContentBox.Footer>
        </UIListContainer>
      </UIArticle>
    </>
  );
}
