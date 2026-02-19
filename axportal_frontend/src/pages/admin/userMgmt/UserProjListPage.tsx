import React, { useState } from 'react';

import { useNavigate } from 'react-router';

import { UIButton2, UITypography } from '@/components/UI/atoms';
import { UIBox } from '@/components/UI/atoms/UIBox';
import { UIDataCnt } from '@/components/UI/atoms/UIDataCnt';
import { UIPagination } from '@/components/UI/atoms/UIPagination';
import { UIDropdown, UIGroup, UIInput, UIUnitGroup } from '@/components/UI/molecules';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { STORAGE_KEYS } from '@/constants/common/storage.constants';
import { useBackRestoredState } from '@/hooks/common/navigation';
import { type GetUserProjectsRequest, type ProjectType, useGetUserProjects, type UserType } from '@/services/admin/userMgmt'; // ================================

// ================================
// 타입 정의
// ================================

type ProjectSearchValues = {
  filterType: string;
  keyword: string;
};

type ProjectSearchParams = Required<Pick<GetUserProjectsRequest, 'page' | 'size'>> & Omit<GetUserProjectsRequest, 'page' | 'size'>;

// ================================
// 상수 정의
// ================================

const DROPDOWN_KEYS = {
  FILTER_TYPE: 'filterType',
} as const;

const INITIAL_DROPDOWN_STATES = {
  [DROPDOWN_KEYS.FILTER_TYPE]: false,
};

const searchTypeOptions = [
  { label: '프로젝트명', value: 'prjNm' },
  { label: '설명', value: 'dtlCtnt' },
];

// ================================
// 컴포넌트
// ================================

/**
 * 관리 > 사용자 관리 > 사용자 상세 >  (TAB) 프로젝트 정보
 */
export const UserProjListPage = ({ userInfo }: { userInfo: UserType }) => {
  const navigate = useNavigate();

  // ================================
  // State 관리
  // ================================

  const [dropdownStates, setDropdownStates] = useState(INITIAL_DROPDOWN_STATES);

  // 검색 폼 상태를 sessionStorage에 저장하여 뒤로가기 시 복원
  const { filters: searchForm, updateFilters: setSearchForm } = useBackRestoredState<ProjectSearchValues>(STORAGE_KEYS.SEARCH_VALUES.USER_PROJ_LIST_FORM, {
    filterType: 'prjNm',
    keyword: '',
  });

  // 검색 파라미터를 sessionStorage에 저장하여 뒤로가기 시 복원
  const { filters: searchParams, updateFilters: setSearchParams } = useBackRestoredState<ProjectSearchParams>(STORAGE_KEYS.SEARCH_VALUES.USER_PROJ_LIST_PARAMS, {
    page: 1,
    size: 12,
    filterType: '',
    keyword: '',
  });

  // ================================
  // API 호출
  // ================================

  const { data } = useGetUserProjects({ userId: userInfo.memberId }, searchParams);

  // ================================
  // 이벤트 핸들러
  // ================================

  const handleSearch = () => {
    setSearchParams({
      page: 1,
      size: searchParams.size,
      filterType: searchForm.filterType,
      keyword: searchForm.keyword ?? '',
    });
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

  const handleProjectRowClick = (projectInfo: ProjectType) => {
    // 프로젝트 상세 페이지로 절대 경로로 이동
    navigate(`projects/${projectInfo.uuid}`);
  };

  // ================================
  // 그리드 컬럼 정의
  // ================================

  const columnDefs: any = [
    {
      headerName: 'NO',
      field: 'no' as any,
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
      suppressHeaderMenuButton: true,
      suppressSizeToFit: true,
      valueGetter: (params: { node: { rowIndex: number } }) => {
        return (searchParams.page - 1) * searchParams.size + params.node.rowIndex + 1;
      },
    },
    {
      headerName: '프로젝트명',
      field: 'prjNm',
      width: 272,
      cellStyle: {
        paddingLeft: '16px',
      },
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
      minWidth: 250,
      cellStyle: {
        paddingLeft: '16px',
      },
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
      cellStyle: {
        paddingLeft: '16px',
      },
    },
    {
      headerName: '최종 수정일시',
      field: 'lstUpdatedAt',
      width: 180,
      cellStyle: {
        paddingLeft: '16px',
      },
    },
  ];

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
                        조회 조건
                      </UITypography>
                    </th>
                    <td colSpan={3}>
                      <UIUnitGroup gap={32} direction='row'>
                        <div className='flex-1'>
                          <UIDropdown
                            value={searchForm.filterType}
                            placeholder='조회 조건 선택'
                            options={searchTypeOptions}
                            isOpen={dropdownStates.filterType}
                            onClick={() => handleDropdownToggle(DROPDOWN_KEYS.FILTER_TYPE)}
                            onSelect={value => handleDropdownSelect(DROPDOWN_KEYS.FILTER_TYPE, value)}
                          />
                        </div>
                        <div className='flex-1'>
                          <UIInput.Search
                            value={searchForm.keyword}
                            placeholder='검색어 입력'
                            onChange={e => setSearchForm({ ...searchForm, keyword: e.target.value })}
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
              <UIButton2 className='btn-secondary-blue' style={{ width: '100%' }} onClick={handleSearch}>
                조회
              </UIButton2>
            </div>
          </UIGroup>
        </UIBox>
      </UIArticle>

      {/* 데이터 그리드 컴포넌트 */}
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
                      value={String(searchParams.size)}
                      options={[
                        { value: '12', label: '12개씩 보기' },
                        { value: '36', label: '36개씩 보기' },
                        { value: '60', label: '60개씩 보기' },
                      ]}
                      disabled={(data?.totalElements || 0) === 0}
                      onSelect={(value: string) => {
                        setSearchParams({ ...searchParams, page: 1, size: Number(value) });
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
              rowData={data?.content || []}
              columnDefs={columnDefs}
              onClickRow={(params: any) => {
                handleProjectRowClick(params.data);
              }}
            />
          </UIListContentBox.Body>
          <UIListContentBox.Footer>
            <UIPagination
              currentPage={searchParams.page}
              totalPages={data?.totalPages || 1}
              onPageChange={(page: number) => {
                setSearchParams({ ...searchParams, page });
              }}
              className='flex justify-center'
            />
          </UIListContentBox.Footer>
        </UIListContainer>
      </UIArticle>
    </>
  );
};
