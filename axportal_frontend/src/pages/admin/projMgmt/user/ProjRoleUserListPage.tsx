import React, { useMemo, useState } from 'react';

import { UIBox, UIButton2, UIDataCnt, UILabel, UIPagination, UITextLabel, UITypography } from '@/components/UI';
import { UIArticle, UIDropdown, UIGroup, UIInput, UIUnitGroup } from '@/components/UI/molecules';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { STORAGE_KEYS } from '@/constants/common/storage.constants';
import { useBackRestoredState } from '@/hooks/common/navigation';
import { useGetProjectRoles, useGetProjectRoleUsers } from '@/services/admin/projMgmt/projMgmt.services';
import type { GetProjectRoleUsersRequest, ProjectRoleUserType } from '@/services/admin/projMgmt/projMgmt.types';
import { useModal } from '@/stores/common/modal';

import { ProjRoleUserUpdatePopup } from './ProjRoleUserUpdatePopup';
import { Button } from '@/components/common/auth';
import { AUTH_KEY } from '@/constants/auth'; // ================================

// ================================
// 타입 정의
// ================================

type RoleSummary = {
  id: string;
  name: string;
  description?: string | null;
};

type ProjRoleUserListPageProps = {
  projectId: string;
  role: RoleSummary;
};

type RoleUserSearchForm = {
  filterType: 'jkwNm' | 'deptNm';
  keyword: string;
  dmcStatus: 'all' | 'ACTIVE' | 'DORMANT' | 'WITHDRAW';
  retrJkwYn: 'all' | '0' | '1';
};

type RoleUserSearchParams = GetProjectRoleUsersRequest & {
  page: number;
  size: number;
  filterType: 'jkwNm' | 'deptNm';
};

// UIGrid에서 체크박스 선택을 위해 id 필드가 필요
type GridRowData = ProjectRoleUserType & { id: string; no: number };

// ================================
// 상수 정의
// ================================

const DROPDOWN_KEYS = {
  FILTER_TYPE: 'filterType',
  DMC_STATUS: 'dmcStatus',
  RETR_STATUS: 'retrJkwYn',
  PAGE_SIZE: 'pageSize',
} as const;

type DropdownKey = (typeof DROPDOWN_KEYS)[keyof typeof DROPDOWN_KEYS];

const INITIAL_DROPDOWN_STATES: Record<DropdownKey, boolean> = {
  [DROPDOWN_KEYS.FILTER_TYPE]: false,
  [DROPDOWN_KEYS.DMC_STATUS]: false,
  [DROPDOWN_KEYS.RETR_STATUS]: false,
  [DROPDOWN_KEYS.PAGE_SIZE]: false,
};

const FILTER_TYPE_OPTIONS = [
  { label: '이름', value: 'jkwNm' },
  { label: '부서', value: 'deptNm' },
];

const DMC_STATUS_OPTIONS = [
  { label: '전체', value: 'all' },
  { label: '활성화', value: 'ACTIVE' },
  { label: '비활성화', value: 'DORMANT' },
  { label: '탈퇴', value: 'WITHDRAW' },
];

const RETR_STATUS_OPTIONS = [
  { label: '전체', value: 'all' },
  { label: '재직', value: '0' },
  { label: '퇴사', value: '1' },
];

const PAGE_SIZE_OPTIONS = [
  { label: '12개씩 보기', value: '12' },
  { label: '36개씩 보기', value: '36' },
  { label: '60개씩 보기', value: '60' },
];

const STATUS_BADGE_MAP: Record<string, { label: string; intent: 'complete' | 'error' | 'gray' }> = {
  '0': { label: '재직', intent: 'complete' },
  '1': { label: '퇴사', intent: 'error' },
};

const ACCOUNT_STATUS_LABEL: Record<string, string> = {
  ACTIVE: '활성화',
  DORMANT: '비활성화',
  WITHDRAW: '탈퇴',
};

// ================================
// 컴포넌트
// ================================

/**
 * 프로젝트 관리 > 프로젝트 상세 > 역할 정보(TAB) > 역할 상세 > 구성원 정보(TAB)
 */
export const ProjRoleUserListPage: React.FC<ProjRoleUserListPageProps> = ({ projectId, role }) => {
  const { id: roleId } = role;
  const { openAlert } = useModal();

  // ================================
  // State 관리
  // ================================

  const [dropdownStates, setDropdownStates] = useState(INITIAL_DROPDOWN_STATES);
  const [isRoleUpdatePopupOpen, setIsRoleUpdatePopupOpen] = useState(false);
  const [selectedUsers, setSelectedUsers] = useState<GridRowData[]>([]);

  // 검색 폼 상태를 sessionStorage에 저장하여 뒤로가기 시 복원
  const { filters: searchForm, updateFilters: setSearchForm } = useBackRestoredState<RoleUserSearchForm>(STORAGE_KEYS.SEARCH_VALUES.PROJ_ROLE_USER_LIST_FORM, {
    filterType: 'jkwNm',
    keyword: '',
    dmcStatus: 'all',
    retrJkwYn: 'all',
  });

  // 검색 파라미터를 sessionStorage에 저장하여 뒤로가기 시 복원
  const { filters: searchParams, updateFilters: setSearchParams } = useBackRestoredState<RoleUserSearchParams>(STORAGE_KEYS.SEARCH_VALUES.PROJ_ROLE_USER_LIST_PARAMS, {
    page: 1,
    size: 12,
    filterType: 'jkwNm',
    keyword: '',
    dmcStatus: undefined,
    retrJkwYn: undefined,
  });

  // ================================
  // API 호출
  // ================================

  const { data, refetch } = useGetProjectRoleUsers(projectId, roleId, searchParams);
  const { data: projectRolesData } = useGetProjectRoles(projectId, { page: 1, size: 1000 });

  // ================================
  // 이벤트 핸들러
  // ================================

  const handleDropdownToggle = (key: DropdownKey) => {
    setDropdownStates(prev => ({
      ...INITIAL_DROPDOWN_STATES,
      [key]: !prev[key],
    }));
  };

  const handleDropdownSelect = (key: DropdownKey, value: string) => {
    setDropdownStates(INITIAL_DROPDOWN_STATES);

    switch (key) {
      case DROPDOWN_KEYS.FILTER_TYPE:
        setSearchForm({ ...searchForm, filterType: value as RoleUserSearchForm['filterType'] });
        break;
      case DROPDOWN_KEYS.DMC_STATUS:
        setSearchForm({ ...searchForm, dmcStatus: value as RoleUserSearchForm['dmcStatus'] });
        break;
      case DROPDOWN_KEYS.RETR_STATUS:
        setSearchForm({ ...searchForm, retrJkwYn: value as RoleUserSearchForm['retrJkwYn'] });
        break;
      case DROPDOWN_KEYS.PAGE_SIZE:
        setSearchParams({ ...searchParams, page: 1, size: Number(value) });
        break;
      default:
        break;
    }
  };

  const handleSearch = () => {
    const nextDmcStatus = searchForm.dmcStatus === 'all' ? undefined : (searchForm.dmcStatus as 'ACTIVE' | 'DORMANT');
    const nextRetrStatus = searchForm.retrJkwYn === 'all' ? undefined : searchForm.retrJkwYn === '0' ? 0 : 1;

    setSearchParams({
      ...searchParams,
      page: 1,
      filterType: searchForm.filterType,
      keyword: searchForm.keyword.trim(),
      dmcStatus: nextDmcStatus,
      retrJkwYn: nextRetrStatus,
    });
  };

  const handleRoleUpdateSuccess = async () => {
    setSelectedUsers([]);
    await refetch();
  };

  const handlePageChange = (page: number) => {
    setSearchParams({ ...searchParams, page });
  };

  // ================================
  // 파생 데이터
  // ================================

  const totalCount = data?.totalElements ?? 0;
  const totalPages = data?.totalPages ?? 1;

  const rowData = useMemo<GridRowData[]>(() => {
    if (!data?.content) {
      return [];
    }

    const baseIndex = (searchParams.page - 1) * searchParams.size;

    return data.content.map((user, index) => ({
      ...user,
      id: user.uuid ?? '',
      no: baseIndex + index + 1,
    }));
  }, [data, searchParams.page, searchParams.size]);

  const roleOptions = useMemo(() => {
    if (!projectRolesData?.content) {
      return [] as Array<{ value: string; label: string }>;
    }

    return projectRolesData.content.map(role => ({
      value: role.uuid,
      label: role.roleNm,
    }));
  }, [projectRolesData]);

  // 역할 변경 팝업에 전달할 선택된 사용자 목록
  const selectedUsersForRoleUpdate = useMemo<ProjectRoleUserType[]>(
    () => selectedUsers,
    [selectedUsers]
  );

  // ================================
  // 그리드 컬럼 정의
  // ================================

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
      },
      {
        headerName: '계정 상태',
        field: 'dmcStatus',
        width: 120,
        cellRenderer: React.memo((params: { value: string }) => {
          return <UITextLabel intent={params.value === 'WITHDRAW' ? 'red' : params.value === 'ACTIVE' ? 'blue' : 'gray'}>{ACCOUNT_STATUS_LABEL[params.value] ?? '-'}</UITextLabel>;
        }),
      },
      {
        headerName: '이름',
        field: 'jkwNm',
        minWidth: 450,
      },
      {
        headerName: '부서',
        field: 'deptNm',
        minWidth: 450,
        flex: 1,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: '인사 상태',
        field: 'retrJkwYn',
        width: 150,
        cellRenderer: React.memo((params: { value: string }) => {
          const status = STATUS_BADGE_MAP[params.value] ?? { label: '-', intent: 'gray' as const };

          return (
            <UILabel variant='badge' intent={status.intent}>
              {status.label}
            </UILabel>
          );
        }),
      },
      {
        headerName: '마지막 접속 일시',
        field: 'lstLoginAt',
        width: 180,
        minWidth: 190,
        maxWidth: 190,
        // suppressSizeToFit: true,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
    ],
    []
  );

  // ================================
  // 렌더링
  // ================================

  return (
    <>
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
                        <div style={{ width: '540px' }}>
                          <UIDropdown
                            value={searchForm.filterType}
                            placeholder='조회 조건 선택'
                            options={FILTER_TYPE_OPTIONS}
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
                  <tr>
                    <th>
                      <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                        계정 상태
                      </UITypography>
                    </th>
                    <td>
                      <div style={{ width: '540px' }}>
                        <UIDropdown
                          value={searchForm.dmcStatus}
                          placeholder='조회조건 선택'
                          options={DMC_STATUS_OPTIONS}
                          isOpen={dropdownStates.dmcStatus}
                          onClick={() => handleDropdownToggle(DROPDOWN_KEYS.DMC_STATUS)}
                          onSelect={value => handleDropdownSelect(DROPDOWN_KEYS.DMC_STATUS, value)}
                        />
                      </div>
                    </td>
                    <th>
                      <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                        인사 상태
                      </UITypography>
                    </th>
                    <td colSpan={3}>
                      <div className='flex-1'>
                        <UIDropdown
                          value={searchForm.retrJkwYn}
                          placeholder='조회조건 선택'
                          options={RETR_STATUS_OPTIONS}
                          isOpen={dropdownStates.retrJkwYn}
                          onClick={() => handleDropdownToggle(DROPDOWN_KEYS.RETR_STATUS)}
                          onSelect={value => handleDropdownSelect(DROPDOWN_KEYS.RETR_STATUS, value)}
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
                <div className='flex justify-between items-center w-full'>
                  <div className='flex-shrink-0 pr-2'>
                    <div style={{ width: '168px', paddingRight: '8px' }}>
                      <UIDataCnt count={totalCount} prefix='총' />
                    </div>
                  </div>
                  <div className='flex' style={{ gap: '12px' }}>
                    <div className=''>
                      <Button
                        auth={AUTH_KEY.ADMIN.ROLE_MEMBER_ADD}
                        className='btn-tertiary-outline'
                        onClick={() => {
                          if (selectedUsersForRoleUpdate.length === 0) {
                            return;
                          }

                          // 비활성화(DORMANT) 상태인 사용자가 있는지 확인
                          const hasDormantUser = selectedUsersForRoleUpdate.some(user => user.dmcStatus === 'DORMANT');
                          if (hasDormantUser) {
                            openAlert({
                              title: '안내',
                              message: '비활성화 상태의 사용자는 역할 변경을 할 수 없습니다.',
                            });
                            return;
                          }

                          setIsRoleUpdatePopupOpen(true);
                        }}
                        disabled={selectedUsersForRoleUpdate.length === 0}
                      >
                        구성원 역할 변경
                      </Button>
                    </div>
                    <div style={{ width: '180px', flexShrink: 0 }}>
                      <UIDropdown
                        value={String(searchParams.size)}
                        options={PAGE_SIZE_OPTIONS}
                        isOpen={dropdownStates.pageSize}
                        onClick={() => handleDropdownToggle(DROPDOWN_KEYS.PAGE_SIZE)}
                        onSelect={value => handleDropdownSelect(DROPDOWN_KEYS.PAGE_SIZE, value)}
                        height={40}
                        variant='dataGroup'
                        disabled={data?.content?.length === 0}
                      />
                    </div>
                  </div>
                </div>
              </UIUnitGroup>
            </div>
          </UIListContentBox.Header>

          <UIListContentBox.Body>
            <UIGrid
              type='multi-select'
              rowData={rowData}
              selectedDataList={selectedUsers}
              checkKeyName='uuid'
              columnDefs={columnDefs}
              onCheck={(rows: GridRowData[]) => {
                setSelectedUsers(rows);
              }}
            />
          </UIListContentBox.Body>

          <UIListContentBox.Footer>
            <UIPagination currentPage={searchParams.page} totalPages={totalPages || 1} onPageChange={handlePageChange} className='flex justify-center' />
          </UIListContentBox.Footer>
        </UIListContainer>
      </UIArticle>

      {/* 구성원 역할 변경 팝업 */}
      <ProjRoleUserUpdatePopup
        projectId={projectId}
        isOpen={isRoleUpdatePopupOpen}
        onClose={() => setIsRoleUpdatePopupOpen(false)}
        onSuccess={handleRoleUpdateSuccess}
        users={selectedUsersForRoleUpdate}
        roleOptions={roleOptions}
        roleId={roleId}
      />
    </>
  );
};
