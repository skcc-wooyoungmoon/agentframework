import React, { useMemo, useState } from 'react';

import { useNavigate } from 'react-router';

import { UIBox, UIButton2, UIDataCnt, UILabel, UITypography } from '@/components/UI/atoms';
import { UIPagination } from '@/components/UI/atoms/UIPagination';
import { UITextLabel } from '@/components/UI/atoms/UITextLabel';
import { UIGroup, UIInput, UIUnitGroup } from '@/components/UI/molecules';
import { UIDropdown } from '@/components/UI/molecules/dropdown/UIDropdown';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { STORAGE_KEYS } from '@/constants/common/storage.constants';
import { useBackRestoredState } from '@/hooks/common/navigation';
import type { GetUsersRequest, UserType } from '@/services/admin/userMgmt';
import { useGetUsers } from '@/services/admin/userMgmt/userMgmt.services';

// ================================
// 타입 정의
// ================================

type UserSearchValues = {
  filterType: string;
  keyword: string;
  workStatus: string; // 인사 상태 (retrJkwYn)
  accountStatus: string; // 계정 상태 (dmcStatus)
};

type UserSearchParams = Required<Pick<GetUsersRequest, 'page' | 'size'>> & Omit<GetUsersRequest, 'page' | 'size'>;

// ================================
// 상수 정의
// ================================

const PAGE_OPTIONS = [
  { value: '12', label: '12개씩 보기' },
  { value: '36', label: '36개씩 보기' },
  { value: '60', label: '60개씩 보기' },
];

const DROPDOWN_KEYS = {
  FILTER_TYPE: 'filterType',
  WORK_STATUS: 'workStatus',
  ACCOUNT_STATUS: 'accountStatus',
} as const;

const INITIAL_DROPDOWN_STATES = {
  [DROPDOWN_KEYS.FILTER_TYPE]: false,
  [DROPDOWN_KEYS.WORK_STATUS]: false,
  [DROPDOWN_KEYS.ACCOUNT_STATUS]: false,
};

const STATUS_MAP: {
  [key: string]: { label: string; intent: 'complete' | 'error' };
} = {
  '0': { label: '재직', intent: 'complete' },
  '1': { label: '퇴사', intent: 'error' },
};

const filterTypeOptions = [
  { label: '이름', value: 'jkwNm' },
  { label: '부서', value: 'deptNm' },
  { label: '직급', value: 'jkgpNm' },
  { label: '행번', value: 'memberId' },
];

const WORK_STATUS_VALUES = {
  EMPLOYED: '0',
  RESIGNED: '1',
} as const;

const workStatusOptions = [
  { label: '전체', value: 'all' },
  { label: '재직', value: WORK_STATUS_VALUES.EMPLOYED },
  { label: '퇴사', value: WORK_STATUS_VALUES.RESIGNED },
];

const ACCOUNT_STATUS_MAP: {
  [key: string]: { label: string; intent: 'blue' | 'gray' | 'red' };
} = {
  ACTIVE: { label: '활성화', intent: 'blue' },
  DORMANT: { label: '비활성화', intent: 'gray' },
  WITHDRAW: { label: '탈퇴', intent: 'red' },
};

const accountStatusOptions = [
  { label: '전체', value: 'all' },
  { label: '활성화', value: 'ACTIVE' },
  { label: '비활성화', value: 'DORMANT' },
  { label: '탈퇴', value: 'WITHDRAW' },
];

/**
 * 관리 > 사용자 관리
 */
export const UserListPage = () => {
  const navigate = useNavigate();

  const [_, setDropdownStates] = useState(INITIAL_DROPDOWN_STATES);

  // 검색 폼 상태를 sessionStorage에 저장하여 뒤로가기 시 복원
  const { filters: searchForm, updateFilters: setSearchForm } = useBackRestoredState<UserSearchValues>(STORAGE_KEYS.SEARCH_VALUES.USER_LIST_FORM, {
    filterType: 'jkwNm',
    keyword: '',
    workStatus: 'all',
    accountStatus: 'all',
  });

  // 검색 파라미터(API 요청값)를 sessionStorage에 저장하여 뒤로가기 시 복원
  const { filters: searchParams, updateFilters: setSearchParams } = useBackRestoredState<UserSearchParams>(STORAGE_KEYS.SEARCH_VALUES.USER_LIST_PARAMS, {
    page: 1,
    size: 12,
    filterType: '',
    keyword: '',
    retrJkwYn: undefined,
    dmcStatus: undefined,
  });

  const { data } = useGetUsers(searchParams, {
    placeholderData: previousData => previousData,
  });

  const handleSearch = () => {
    const nextRetrJkwYn = searchForm.workStatus === 'all' ? undefined : searchForm.workStatus;
    const nextDmcStatus = searchForm.accountStatus === 'all' ? undefined : searchForm.accountStatus;

    setSearchParams({
      page: 1,
      size: searchParams.size,
      filterType: searchForm.filterType ?? '',
      keyword: searchForm.keyword ?? '',
      retrJkwYn: nextRetrJkwYn,
      dmcStatus: nextDmcStatus,
    });
  };

  const handleDropdownSelect = (key: keyof UserSearchValues, value: string) => {
    setSearchForm(prev => ({ ...prev, [key]: value }));
    setDropdownStates(INITIAL_DROPDOWN_STATES);
  };

  const handleUserRowClick = (userInfo: UserType) => {
    navigate(`${userInfo.memberId}`, { state: { userInfo } });
  };

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
        headerName: '계정 상태',
        field: 'dmcStatus',
        width: 120,
        cellRenderer: React.memo((params: any) => {
          const status = params.data?.dmcStatus;
          const mapped = ACCOUNT_STATUS_MAP[status];
          return <UITextLabel intent={mapped.intent}>{mapped.label}</UITextLabel>;
        }),
      },
      {
        headerName: '이름',
        field: 'jkwNm',
        minWidth: 272,
      },
      {
        headerName: '부서',
        field: 'deptNm',
        flex: 1,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: '직급',
        field: 'jkgpNm',
        width: 268,
      },
      {
        headerName: '인사 상태',
        field: 'retrJkwYn',
        width: 130,
        cellRenderer: React.memo((params: { value: string }) => {
          const status = STATUS_MAP[params.value] || {
            label: params.value,
            intent: 'complete' as const,
          };
          return (
            <UILabel variant='badge' intent={status.intent}>
              {status.label}
            </UILabel>
          );
        }),
      },
      {
        headerName: '행번',
        field: 'memberId',
        width: 268,
      },
      {
        headerName: '마지막 접속 일시',
        field: 'lstLoginAt',
        width: 180,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
    ],
    [searchParams.page, searchParams.size]
  );

  return (
    <section className='section-page'>
      {/* 페이지 헤더 */}
      <UIPageHeader title='사용자 관리' description='포탈을 이용하는 모든 사용자를 확인하고 관리할 수 있습니다.' />

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
                          조회 조건
                        </UITypography>
                      </th>
                      <td colSpan={3}>
                        <UIUnitGroup gap={32} direction='row'>
                          <div className='flex-1'>
                            <UIDropdown
                              value={searchForm.filterType}
                              placeholder='조회 조건 선택'
                              options={filterTypeOptions}
                              onSelect={value => handleDropdownSelect(DROPDOWN_KEYS.FILTER_TYPE, value)}
                            />
                          </div>
                          <div className='flex-1'>
                            <UIInput.Search
                              value={searchForm.keyword}
                              onChange={e => {
                                setSearchForm(prev => ({ ...prev, keyword: e.target.value }));
                              }}
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
                    <tr>
                      <th>
                        <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                          계정 상태
                        </UITypography>
                      </th>
                      <td colSpan={3}>
                        <UIUnitGroup gap={32} direction='row'>
                          <div className='flex-1'>
                            <UIDropdown
                              value={searchForm.accountStatus}
                              placeholder='조회 조건 선택'
                              options={accountStatusOptions}
                              onSelect={value => handleDropdownSelect(DROPDOWN_KEYS.ACCOUNT_STATUS, value)}
                            />
                          </div>
                          <div className='flex flex-1 items-center'>
                            <div className='w-[100px]'>
                              <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                                인사 상태
                              </UITypography>
                            </div>
                            <UIDropdown
                              value={searchForm.workStatus}
                              placeholder='조회 조건 선택'
                              options={workStatusOptions}
                              onSelect={value => handleDropdownSelect(DROPDOWN_KEYS.WORK_STATUS, value)}
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

        {/* 그리드 영역 */}
        <UIArticle className='article-grid'>
          <UIListContainer>
            <UIListContentBox.Header>
              <UIUnitGroup gap={16} direction='column'>
                <div className='flex justify-between w-full items-center'>
                  <div className='flex-shrink-0'>
                    <div style={{ width: '168px', paddingRight: '8px' }}>
                      <UIDataCnt count={data?.totalElements || 0} prefix='총' unit='건' />
                    </div>
                  </div>
                  <div className='flex'>
                    <div style={{ width: '180px', flexShrink: 0 }}>
                      <UIDropdown
                        value={String(searchParams.size)}
                        options={PAGE_OPTIONS}
                        disabled={(data?.totalElements || 0) === 0}
                        onSelect={(value: string) => {
                          setSearchParams({ ...searchParams, page: 1, size: Number(value) });
                        }}
                        height={40}
                        variant='dataGroup'
                      />
                    </div>
                  </div>
                </div>
              </UIUnitGroup>
            </UIListContentBox.Header>
            <UIListContentBox.Body>
              <UIGrid
                type='default'
                rowData={data?.content || []}
                columnDefs={columnDefs}
                onClickRow={(params: any) => {
                  handleUserRowClick(params.data);
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
      </UIPageBody>
    </section>
  );
};
