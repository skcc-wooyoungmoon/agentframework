import { useMemo, useState } from 'react';

import { UIInput, UIGroup } from '@/components/UI/molecules';
import { UIBox, UIButton2, UIDataCnt, UITypography } from '@/components/UI/atoms';
import { UIPagination } from '@/components/UI/atoms/UIPagination';
import { UIUnitGroup } from '@/components/UI/molecules';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIDropdown } from '@/components/UI/molecules/dropdown/UIDropdown';
import { useGetEvaluationDataList } from '@/services/data/storage/dataStorage.services';
import type { GetEvaluationDataListRequest } from '@/services/data/storage/types';

interface EvaluationDataListPageProps {
  isActiveTab?: boolean;
}

interface SearchValues {
  dateType: string;
  dateRange: { startDate: string; endDate: string };
  searchType: string;
  searchKeyword: string;
  status: string;
  publicRange: string;
  category: string;
}

type SearchParams = Required<Pick<GetEvaluationDataListRequest, 'page' | 'countPerPage'>> & Omit<GetEvaluationDataListRequest, 'page' | 'countPerPage'>;

export function EvaluationDataListPage({ isActiveTab }: EvaluationDataListPageProps) {
  // isActiveTab은 향후 탭 활성화 상태에 따라 사용할 예정
  // console.log('isActiveTab:', isActiveTab);

  // ================================
  // State 관리
  // ================================

  const [searchForm, setSearchForm] = useState<SearchValues>({
    dateType: '생성일시',
    dateRange: { startDate: '2025.06.30', endDate: '2025.07.30' },
    searchType: '이름',
    searchKeyword: '',
    status: '전체',
    publicRange: '전체',
    category: '전체',
  });

  const [searchParams, setSearchParams] = useState<SearchParams>({
    page: 1,
    countPerPage: 12,
    cat01: '평가',
    cat02: '',
    title: '',
  });

  // ================================
  // API 호출
  // ================================

  const { data, isLoading } = useGetEvaluationDataList(searchParams, {
    enabled: isActiveTab,
    // 이전 데이터를 유지하여 로딩 중에도 빈 화면이 보이지 않도록 함
    placeholderData: previousData => previousData,
  });

  // ================================
  // 이벤트 핸들러
  // ================================

  const handleSearch = () => {
    setSearchParams(prev => ({
      ...prev,
      page: 1,
      cat01: '평가',
      cat02: searchForm.category === '전체' ? '' : searchForm.category,
      title: searchForm.searchKeyword ?? '',
    }));
  };

  // 드롭다운 상태 관리
  const [dropdownStates, setDropdownStates] = useState({
    dateType: false,
    searchType: false,
    status: false,
    publicRange: false,
    category: false,
  });

  const handleDropdownSelect = (key: keyof SearchValues, value: string) => {
    setSearchForm(prev => ({ ...prev, [key]: value }));
    setDropdownStates(prev => ({ ...prev, [key]: false }));
  };

  // ================================
  // 그리드 컬럼 정의
  // ================================

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
        valueGetter: (params: { node: { rowIndex: number } }) => {
          return (searchParams.page - 1) * searchParams.countPerPage + params.node.rowIndex + 1;
        },
      },
      {
        headerName: '이름',
        field: 'title',
        width: 300,
        cellStyle: {
          paddingLeft: '16px',
        },
        valueGetter: (params: any) => {
          return params.data?.title || '';
        },
        sortable: false,
      },
      {
        headerName: '설명',
        field: 'descCtnt' as any,
        width: 472,
        sortable: false,
        valueGetter: (params: any) => {
          return params.data?.descCtnt || '';
        },
      },
      {
        headerName: '데이터 유형',
        field: 'datasetCat02' as any,
        flex: 1,
        sortable: false,
        valueGetter: (params: any) => {
          const value = params.data?.datasetCat02;
          if (!value) return '';

          // 드롭다운 옵션과 매핑
          const typeMapping: { [key: string]: string } = {
            CONTEXT_SET: 'CONTEXT_SET',
            QUERY_SET: 'QUERY_SET',
            RESPONSE_SET: 'RESPONSE_SET',
            HUMAN_EVALUATION_RESULT_MANUAL: 'HUMAN_EVALUATION_RESULT_MANUAL',
            HUMAN_EVALUATION_RESULT_INTERACTIVE: 'HUMAN_EVALUATION_RESULT_INTERACTIVE',
          };

          return typeMapping[value] || value;
        },
      },
      {
        headerName: '생성일시',
        field: 'fstCreatedAt' as any,
        width: 180,
        sortable: false,
        cellStyle: {
          paddingLeft: '16px',
        },
        valueFormatter: (params: any) => {
          if (params.value) {
            const dateStr = params.value.toString();
            if (dateStr.length === 17) {
              // "20231017 12:00:00" 형식 파싱
              const year = dateStr.substring(0, 4);
              const month = dateStr.substring(4, 6);
              const day = dateStr.substring(6, 8);
              const time = dateStr.substring(9);

              // UTC -> KST 변환 (UTC + 9시간)
              const utcDateString = `${year}-${month}-${day}T${time}Z`;
              const utcDate = new Date(utcDateString);
              const kstDate = new Date(utcDate.getTime() + 9 * 60 * 60 * 1000);

              // "YYYY.MM.DD HH:mm:ss" 형식으로 포맷팅
              const kstYear = kstDate.getUTCFullYear();
              const kstMonth = String(kstDate.getUTCMonth() + 1).padStart(2, '0');
              const kstDay = String(kstDate.getUTCDate()).padStart(2, '0');
              const kstHours = String(kstDate.getUTCHours()).padStart(2, '0');
              const kstMinutes = String(kstDate.getUTCMinutes()).padStart(2, '0');
              const kstSeconds = String(kstDate.getUTCSeconds()).padStart(2, '0');

              return `${kstYear}.${kstMonth}.${kstDay} ${kstHours}:${kstMinutes}:${kstSeconds}`;
            }
            return params.value;
          }
          return '';
        },
      },
      {
        headerName: '최종수정일시',
        field: 'lstUpdatedAt' as any,
        width: 180,
        sortable: false,
        cellStyle: {
          paddingLeft: '16px',
        },
        valueFormatter: (params: any) => {
          if (params.value) {
            const dateStr = params.value.toString();
            if (dateStr.length === 17) {
              // "20231017 12:00:00" 형식 파싱
              const year = dateStr.substring(0, 4);
              const month = dateStr.substring(4, 6);
              const day = dateStr.substring(6, 8);
              const time = dateStr.substring(9);

              // UTC -> KST 변환 (UTC + 9시간)
              const utcDateString = `${year}-${month}-${day}T${time}Z`;
              const utcDate = new Date(utcDateString);
              const kstDate = new Date(utcDate.getTime() + 9 * 60 * 60 * 1000);

              // "YYYY.MM.DD HH:mm:ss" 형식으로 포맷팅
              const kstYear = kstDate.getUTCFullYear();
              const kstMonth = String(kstDate.getUTCMonth() + 1).padStart(2, '0');
              const kstDay = String(kstDate.getUTCDate()).padStart(2, '0');
              const kstHours = String(kstDate.getUTCHours()).padStart(2, '0');
              const kstMinutes = String(kstDate.getUTCMinutes()).padStart(2, '0');
              const kstSeconds = String(kstDate.getUTCSeconds()).padStart(2, '0');

              return `${kstYear}.${kstMonth}.${kstDay} ${kstHours}:${kstMinutes}:${kstSeconds}`;
            }
            return params.value;
          }
          return '';
        },
      },
    ],
    [searchParams.page, searchParams.countPerPage]
  );

  return (
    <>
      {/* 탭/레이아웃은 상위 컨테이너에서 관리 */}
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
                          value={searchForm.searchKeyword}
                          placeholder='이름 입력'
                          onChange={e => {
                            setSearchForm(prev => ({ ...prev, searchKeyword: e.target.value }));
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
                        데이터 유형
                      </UITypography>
                    </th>
                    <td>
                      <div className='flex-1'>
                        <UIDropdown
                          value={searchForm.category}
                          placeholder='전체'
                          options={[
                            { value: '전체', label: '전체' },
                            { value: 'CONTEXT_SET', label: 'CONTEXT_SET' },
                            { value: 'QUERY_SET', label: 'QUERY_SET' },
                            { value: 'RESPONSE_SET', label: 'RESPONSE_SET' },
                            { value: 'HUMAN_EVALUATION_RESULT_MANUAL', label: 'HUMAN_EVALUATION_RESULT_MANUAL' },
                            { value: 'HUMAN_EVALUATION_RESULT_INTERACTIVE', label: 'HUMAN_EVALUATION_RESULT_INTERACTIVE' },
                          ]}
                          isOpen={dropdownStates.category}
                          onSelect={value => handleDropdownSelect('category', value)}
                        />
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
                      value={String(searchParams.countPerPage)}
                      options={[
                        { value: '12', label: '12개씩 보기' },
                        { value: '36', label: '36개씩 보기' },
                        { value: '60', label: '60개씩 보기' },
                      ]}
                      onSelect={(value: string) => {
                        setSearchParams(prev => ({ ...prev, page: 1, countPerPage: Number(value) }));
                      }}
                      height={40}
                      variant='dataGroup'
                      disabled={data?.totalElements === 0}
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
              /* onClickRow={(params: any) => {
                console.log('다중 onClickRow', params);
              }}
              onCheck={(selectedIds: any[]) => {
                console.log('다중 onSelect', selectedIds);
              }} */
            />
          </UIListContentBox.Body>
          <UIListContentBox.Footer>
            <UIPagination
              currentPage={searchParams.page}
              totalPages={data?.totalPages || 1}
              onPageChange={(page: number) => {
                setSearchParams(prev => ({ ...prev, page }));
              }}
              className='flex justify-center'
            />
          </UIListContentBox.Footer>
        </UIListContainer>
      </UIArticle>
    </>
  );
}
