import { useMemo, useState } from 'react';

import { useNavigate } from 'react-router-dom';

import { UIBox, UIButton2, UIDataCnt, UILabel, UIPagination, UITypography } from '@/components/UI';
import { UIArticle, UIDropdown, UIGroup, UIInput, UIPageBody, UIPageHeader } from '@/components/UI/molecules';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { STORAGE_KEYS } from '@/constants/common/storage.constants';
import { API_KEY_TYPE_OPTIONS } from '@/constants/deploy/apikey.constants';
import { useBackRestoredState } from '@/hooks/common/navigation';
import { useGetApiKeyList } from '@/services/deploy/apikey/apikey.services';
import { useUser } from '@/stores/auth/useUser';

import type { ColDef } from 'ag-grid-community';

interface SearchValues {
  page: number;
  size: number;
  dateType: string;
  dateRange: { startDate?: string; endDate?: string };
  searchKeyword: string;
  projectName: string;
}

export const DeployApiKeyListPage = () => {
  const navigate = useNavigate();
  const { user } = useUser();

  const { data: apiKeyList, refetch, isFetching } = useGetApiKeyList({});

  const { filters: searchValues, updateFilters: setSearchValues } = useBackRestoredState<SearchValues>(STORAGE_KEYS.SEARCH_VALUES.DEPLOY_API_KEY_LIST, {
    page: 1,
    size: 12,
    dateType: '생성일시',
    dateRange: { startDate: '2025.06.30', endDate: '2025.07.30' },
    searchKeyword: '',
    projectName: 'all',
  });

  const [appliedFilters, setAppliedFilters] = useState<{
    searchKeyword: string;
    projectName: string;
  }>({
    searchKeyword: searchValues.searchKeyword,
    projectName: searchValues.projectName,
  });

  const projectNameOptions = useMemo(() => {
    const projectList = user?.projectList ?? [];

    const options = projectList.map(project => ({ value: project.prjNm, label: project.prjNm }));
    return [{ value: 'all', label: '전체' }, ...options];
  }, [user?.projectList]);

  // 클라이언트 사이드 필터링 및 페이징 처리
  const paginatedData = useMemo(() => {
    if (!apiKeyList?.content) return { rowData: [], totalPages: 0, totalElements: 0 };

    let filteredData = apiKeyList.content;

    // searchKeyword로 필터링 (이름, 연결 대상 필드 검색)
    if (appliedFilters.searchKeyword.trim()) {
      const keyword = appliedFilters.searchKeyword.trim().toLowerCase();
      filteredData = filteredData.filter(item => item.name?.toLowerCase().includes(keyword) || item.permission?.toLowerCase().includes(keyword));
    }

    // projectName으로 필터링
    if (appliedFilters.projectName !== 'all') {
      filteredData = filteredData.filter(item => item.projectName === appliedFilters.projectName);
    }

    // 페이징 처리
    const startIndex = (searchValues.page - 1) * searchValues.size;
    const endIndex = startIndex + searchValues.size;
    const paginatedContent = filteredData.slice(startIndex, endIndex);

    const rowData = paginatedContent.map((apiKey, index) => ({
      ...apiKey,
      no: startIndex + index + 1,
    }));

    const totalElements = filteredData.length;
    const totalPages = Math.ceil(totalElements / searchValues.size) || 1;

    return { rowData, totalPages, totalElements };
  }, [apiKeyList?.content, appliedFilters, searchValues.page, searchValues.size]);

  const handlePageChange = (page: number) => {
    setSearchValues(prev => ({ ...prev, page: page }));
  };

  const handlePageSizeChange = (newPageSize: string) => {
    setSearchValues(prev => ({ ...prev, size: Number(newPageSize), page: 1 }));
  };

  const handleSearch = () => {
    setAppliedFilters({
      searchKeyword: searchValues.searchKeyword,
      projectName: searchValues.projectName,
    });
    setSearchValues(prev => ({ ...prev, page: 1 }));
    if (searchValues.searchKeyword === '' && searchValues.projectName === '전체') {
      refetch();
    }
  };

  // 그리드 컬럼 정의
  const columnDefs: ColDef[] = useMemo(
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
      },
      {
        headerName: '이름',
        field: 'name',
        width: 300,
      },
      {
        headerName: '상태',
        field: 'expired',
        width: 120,
        cellRenderer: (params: any) => {
          const isExpired = params.value === true;
          return isExpired ? (
            <UILabel variant='badge' intent='error'>
              사용차단
            </UILabel>
          ) : (
            <UILabel variant='badge' intent='complete'>
              사용가능
            </UILabel>
          );
        },
      },
      {
        headerName: '프로젝트명',
        field: 'projectName',
        flex: 1,
      },
      {
        headerName: '구분',
        field: 'type',
        flex: 1,
        cellRenderer: (params: any) => {
          const type = API_KEY_TYPE_OPTIONS.find(option => option.value === params.value.toUpperCase());
          return <div>{type?.label}</div>;
        },
      },
      {
        headerName: '연결 대상',
        field: 'permission',
        width: 440,
      },
    ],
    []
  );

  return (
    <section className='section-page'>
      <UIPageHeader title='API Key' description='모델 및 에이전트 배포화면에서 발급한 API Key를 조회할 수 있습니다.' />
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
                            value={searchValues.searchKeyword}
                            onChange={e => {
                              setSearchValues(prev => ({ ...prev, searchKeyword: e.target.value }));
                            }}
                            placeholder='이름, 연결 대상 입력'
                          />
                        </div>
                      </td>
                      <th>
                        <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                          프로젝트명
                        </UITypography>
                      </th>
                      <td>
                        <div className='flex-1'>
                          <UIDropdown
                            value={searchValues.projectName}
                            placeholder='프로젝트명 선택'
                            options={projectNameOptions}
                            onSelect={value => {
                              setSearchValues(prev => ({ ...prev, projectName: value }));
                            }}
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
              <div className='flex-shrink-0'>
                <UIGroup gap={8} direction='row' align='start'>
                  <div style={{ width: '168px', paddingRight: '8px' }}>
                    <UIDataCnt count={paginatedData.totalElements} prefix='총' />
                  </div>
                </UIGroup>
              </div>
              <div className='flex items-center gap-2'>
                <div style={{ width: '180px', flexShrink: 0 }}>
                  <UIDropdown
                    value={`${String(searchValues.size)}개씩 보기`}
                    disabled={paginatedData.totalElements === 0}
                    options={[
                      { value: '12', label: '12개씩 보기' },
                      { value: '36', label: '36개씩 보기' },
                      { value: '60', label: '60개씩 보기' },
                    ]}
                    onSelect={handlePageSizeChange}
                    height={40}
                    variant='dataGroup'
                    width='w-40'
                  />
                </div>
              </div>
            </UIListContentBox.Header>
            <UIListContentBox.Body className='article-body'>
              <UIGrid
                loading={isFetching}
                rowData={paginatedData.rowData}
                columnDefs={columnDefs}
                onClickRow={params => {
                  navigate(`${params.data.id}`);
                }}
              />
            </UIListContentBox.Body>
            <UIListContentBox.Footer>
              <UIPagination currentPage={searchValues.page} totalPages={paginatedData.totalPages} onPageChange={handlePageChange} className='flex justify-center' />
            </UIListContentBox.Footer>
          </UIListContainer>
        </UIArticle>
      </UIPageBody>
    </section>
  );
};
