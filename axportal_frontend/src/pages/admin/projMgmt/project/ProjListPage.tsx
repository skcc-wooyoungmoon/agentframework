import React, { useMemo, useState } from 'react';

import { useNavigate } from 'react-router-dom';

import { UIBox, UIButton2, UIDataCnt, UITypography } from '@/components/UI/atoms';
import { UIPagination } from '@/components/UI/atoms/UIPagination';
import { UIArticle, UIGroup, UIInput, UIPageBody, UIPageHeader, UIUnitGroup } from '@/components/UI/molecules';
import { UIDropdown } from '@/components/UI/molecules/dropdown/UIDropdown';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { STORAGE_KEYS } from '@/constants/common/storage.constants';
import { useBackRestoredState } from '@/hooks/common/navigation';
import { type GetProjectsRequest, useGetProjects } from '@/services/admin/projMgmt';

// ================================
// 타입 정의
// ================================

type ProjectSearchValues = {
  filterType: 'prjNm' | 'dtlCtnt';
  keyword: string;
};

// ================================
// 상수 정의
// ================================

// 드롭다운 키들
const DROPDOWN_KEYS = {
  FILTER_TYPE: 'filterType',
} as const;

// 드롭다운 초기 상태
const INITIAL_DROPDOWN_STATES = {
  [DROPDOWN_KEYS.FILTER_TYPE]: false,
};

// 드롭다운 옵션들
const filterTypeOptions = [
  { label: '프로젝트명', value: 'prjNm' },
  { label: '설명', value: 'dtlCtnt' },
];

// ================================
// 컴포넌트
// ================================

/**
 * 프로젝트 관리
 */
export const ProjListPage = () => {
  // ================================
  // Hooks
  // ================================

  const navigate = useNavigate();

  // ================================
  // State 관리
  // ================================
  const [dropdownStates, setDropdownStates] = useState(INITIAL_DROPDOWN_STATES);

  // 검색 폼 상태를 sessionStorage에 저장하여 뒤로가기 시 복원
  const { filters: searchForm, updateFilters: setSearchForm } = useBackRestoredState<ProjectSearchValues>(STORAGE_KEYS.SEARCH_VALUES.PROJ_LIST_FORM, {
    filterType: 'prjNm',
    keyword: '',
  });

  // 검색 파라미터를 sessionStorage에 저장하여 뒤로가기 시 복원
  const { filters: searchParams, updateFilters: setSearchParams } = useBackRestoredState<GetProjectsRequest>(STORAGE_KEYS.SEARCH_VALUES.PROJ_LIST_PARAMS, {
    page: 1,
    size: 12,
    filterType: 'prjNm',
    keyword: '',
  });

  // ================================
  // API 호출
  // ================================

  const { data } = useGetProjects(searchParams);

  // ================================
  // 이벤트 핸들러
  // ================================

  const handleSearch = () => {
    const params: GetProjectsRequest = {
      page: 1,
      size: searchParams.size,
      filterType: searchForm.filterType,
      keyword: searchForm.keyword || undefined,
    };

    setSearchParams(params);
  };

  const handleDropdownToggle = (key: keyof typeof dropdownStates) => {
    const isCurrentOpen = dropdownStates[key];

    setDropdownStates({
      ...INITIAL_DROPDOWN_STATES,
      [key]: !isCurrentOpen,
    });
  };

  const handleDropdownSelect = (key: keyof ProjectSearchValues, value: string) => {
    setSearchForm({ ...searchForm, [key]: value });
    setDropdownStates(INITIAL_DROPDOWN_STATES);
  };

  const handleProjectRowClick = (params: any) => {
    const projectInfo = params.data;
    if (projectInfo && projectInfo.uuid) {
      navigate(`/admin/project-mgmt/${projectInfo.uuid}`);
    }
  };

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
        sortable: true,
        suppressHeaderMenuButton: false,
        suppressSizeToFit: true,
        valueGetter: (params: { node: { rowIndex: number } }) => {
          return ((searchParams.page || 1) - 1) * (searchParams.size || 12) + params.node.rowIndex + 1;
        },
      },
      {
        headerName: '프로젝트명',
        field: 'prjNm',
        width: 272,
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
        field: 'dtlCtnt',
        flex: 1,
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
        headerName: '생성일시',
        field: 'fstCreatedAt',
        width: 180,
      },
      {
        headerName: '최종 수정일시',
        field: 'lstUpdatedAt',
        width: 180,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
    ],
    [searchParams.page, searchParams.size]
  );

  // ================================
  // 렌더링
  // ================================

  return (
    <section className='section-page'>
      {/* 페이지 헤더 */}
      <UIPageHeader
          title='프로젝트 관리'
          description={[
            '포탈 내 프로젝트를 확인하고 관리할 수 있습니다.',
            '프로젝트를 선택하여 구성원들을 초대하고 역할을 부여해 보세요.',
          ]}
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
                          조회 조건
                        </UITypography>
                      </th>
                      <td colSpan={3}>
                        <UIUnitGroup gap={32} direction='row'>
                          <div className='flex-1'>
                            <UIDropdown
                              value={searchForm.filterType}
                              options={filterTypeOptions}
                              isOpen={dropdownStates.filterType}
                              onClick={() => handleDropdownToggle(DROPDOWN_KEYS.FILTER_TYPE)}
                              onSelect={value => handleDropdownSelect(DROPDOWN_KEYS.FILTER_TYPE, value)}
                            />
                          </div>
                          <div className='flex-1'>
                            <UIInput.Search
                              value={searchForm.keyword}
                              onChange={e => setSearchForm({ ...searchForm, keyword: e.target.value })}
                              onKeyDown={e => {
                                if (e.key === 'Enter') {
                                  handleSearch();
                                }
                              }}
                              placeholder='검색어 입력'
                            />
                          </div>
                        </UIUnitGroup>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
              <div style={{ width: '128px' }}>
                <UIButton2 onClick={handleSearch} className='btn-secondary-blue' style={{ width: '100%' }}>
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
              <UIUnitGroup gap={16} direction='column'>
                <div className='flex justify-between w-full items-center'>
                  <div className='flex-shrink-0'>
                    <div style={{ width: '168px', paddingRight: '8px' }}>
                      <UIDataCnt count={data?.totalElements || 0} prefix='총' />
                    </div>
                  </div>
                  <div className='flex'>
                    <div style={{ width: '180px', flexShrink: 0 }}>
                      <UIDropdown
                        value={String(searchParams.size || 12)}
                        options={[
                          { value: '12', label: '12개씩 보기' },
                          { value: '36', label: '36개씩 보기' },
                          { value: '60', label: '60개씩 보기' },
                        ]}
                        onSelect={(value: string) => {
                          setSearchParams({ ...searchParams, page: 1, size: Number(value) });
                        }}
                        height={40}
                        variant='dataGroup'
                        disabled={data?.content.length === 0}
                      />
                    </div>
                  </div>
                </div>
              </UIUnitGroup>
            </UIListContentBox.Header>
            <UIListContentBox.Body>
              <UIGrid type='default' rowData={data?.content || []} columnDefs={columnDefs} onClickRow={handleProjectRowClick} />
            </UIListContentBox.Body>
            <UIListContentBox.Footer>
              <UIPagination
                currentPage={searchParams.page || 1}
                totalPages={data?.totalPages || 1}
                onPageChange={(page: number) => {
                  setSearchParams({ ...searchParams, page });
                }}
                className='flex justify-center'
              />
            </UIListContentBox.Footer>
          </UIListContainer>
        </UIArticle>
      </UIPageBody>
    </section>
  );
};
