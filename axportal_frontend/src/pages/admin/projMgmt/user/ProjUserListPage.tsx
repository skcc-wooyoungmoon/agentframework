import React, { useCallback, useMemo, useState } from 'react';

import { useNavigate } from 'react-router-dom';

import { UILabel, UITextLabel, UITypography } from '@/components/UI';
import { UIBox, UIButton2, UIDataCnt, UIPagination } from '@/components/UI/atoms';
import { UIArticle, UIDropdown, UIGroup, UIInput, UIUnitGroup } from '@/components/UI/molecules';
import { UIGrid } from '@/components/UI/molecules/grid';
import type { UIMoreMenuConfig } from '@/components/UI/molecules/grid/UIGrid/types';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { STORAGE_KEYS } from '@/constants/common/storage.constants';
import { useBackRestoredState } from '@/hooks/common/navigation';
import type {
  GetProjectUsersRequest,
  GetProjectUsersResponse,
  ProjectDetailType,
  ProjectUserAssignRequest,
  ProjectUserDeleteRequest,
  ProjectUserType,
} from '@/services/admin/projMgmt';
import { useAssignProjectUsers, useDeleteProjectUsers, useGetProjectRoles, useGetProjectUsers } from '@/services/admin/projMgmt';
import { useModal } from '@/stores/common/modal';

import { useUser } from '@/stores';
import { Button } from '@/components/common/auth';
import { AUTH_KEY } from '@/constants/auth';
import { ProjUserInviteStep1Popup, ProjUserInviteStep2Popup } from '@/pages/admin/projMgmt';
import { ProjRoleUserUpdatePopup } from './ProjRoleUserUpdatePopup';
import { env, RUN_MODE_TYPES } from '@/constants/common/env.constants.ts';

type RetrJkwYnFilterValue = 'all' | '0' | '1';

type DmcStatusFilterValue = 'all' | 'ACTIVE' | 'DORMANT';

type FilterTypeValue = 'jkwNm' | 'deptNm';

type UserSearchValues = {
  filterType: FilterTypeValue;
  keyword: string;
  dmcStatus: DmcStatusFilterValue;
  retrJkwYn: RetrJkwYnFilterValue;
};

type ProjectUserInfoPageProps = {
  projectInfo: ProjectDetailType;
};

type ProjectUserSearchParams = GetProjectUsersRequest & {
  page: number;
  size: number;
};

// Popup으로 넘길 때 필요한 필드(roleUuid, roleNm, portalAdmin 등)를 모두 포함한 사용자 타입
type RoleUpdatableUser = GetProjectUsersResponse;
type GridRowData = RoleUpdatableUser & { id: string };

// ================================
// 상수 정의
// ================================

// 드롭다운 키들
const DROPDOWN_KEYS = {
  FILTER_TYPE: 'filterType',
  DMC_STATUS: 'dmcStatus',
  RETR_JKW_YN: 'retrJkwYn',
} as const;

// 드롭다운 초기 상태
const INITIAL_DROPDOWN_STATES = {
  [DROPDOWN_KEYS.FILTER_TYPE]: false,
  [DROPDOWN_KEYS.DMC_STATUS]: false,
  [DROPDOWN_KEYS.RETR_JKW_YN]: false,
};

// 인사 상태 뱃지 매핑
const RETR_JKW_YN_BADGE_MAP: Record<string, { label: string; intent: 'complete' | 'error' }> = {
  '0': { label: '재직', intent: 'complete' },
  '1': { label: '퇴사', intent: 'error' },
};

const DMC_STATUS_LABEL: Record<string, string> = {
  ACTIVE: '활성화',
  DORMANT: '비활성화',
  WITHDRAW: '탈퇴',
};

// 드롭다운 옵션들
const filterTypeOptions = [
  { label: '이름', value: 'jkwNm' },
  { label: '부서', value: 'deptNm' },
];

const dmcStatusOptions = [
  { label: '전체', value: 'all' },
  { label: '활성화', value: 'ACTIVE' },
  { label: '비활성화', value: 'DORMANT' },
  { label: '탈퇴', value: 'WITHDRAW' },
];

const retrJkwYnOptions = [
  { label: '전체', value: 'all' },
  { label: '재직', value: '0' },
  { label: '퇴사', value: '1' },
];

/**
 * 프로젝트 관리 > 프로젝트 상세 > 구성원 정보(TAB)
 */
export const ProjUserListPage = ({ projectInfo }: ProjectUserInfoPageProps) => {
  const projectId = projectInfo.uuid;
  const isPublicProject = projectInfo.prjSeq === -999;

  // ================================
  // State 관리
  // ================================

  const navigate = useNavigate();
  const { openAlert, openConfirm } = useModal();

  const { user } = useUser();
  const isPortalAdmin = user.activeProject.prjRoleSeq === '-199';

  const [dropdownStates, setDropdownStates] = useState(INITIAL_DROPDOWN_STATES);

  // 검색 폼 상태를 sessionStorage에 저장하여 뒤로가기 시 복원
  const { filters: searchForm, updateFilters: setSearchForm } = useBackRestoredState<UserSearchValues>(STORAGE_KEYS.SEARCH_VALUES.PROJ_USER_LIST_FORM, {
    filterType: 'jkwNm',
    keyword: '',
    dmcStatus: 'all',
    retrJkwYn: 'all',
  });

  // 검색 파라미터를 sessionStorage에 저장하여 뒤로가기 시 복원
  const { filters: searchParams, updateFilters: setSearchParams } = useBackRestoredState<ProjectUserSearchParams>(STORAGE_KEYS.SEARCH_VALUES.PROJ_USER_LIST_PARAMS, {
    page: 1,
    size: 12,
  });

  const [isInvitePopupOpen, setIsInvitePopupOpen] = useState(false);
  const [inviteStep, setInviteStep] = useState<1 | 2>(1);
  const [inviteSelectedUsers, setInviteSelectedUsers] = useState<ProjectUserType[]>([]);
  const [inviteUserRoles, setInviteUserRoles] = useState<Record<string, string>>({});
  const [inviteSearchForm, setInviteSearchForm] = useState<{ filterType: string; keyword: string }>({
    filterType: 'jkwNm',
    keyword: '',
  });
  const [selectedUsers, setSelectedUsers] = useState<GridRowData[]>([]);
  const [isRoleUpdatePopupOpen, setIsRoleUpdatePopupOpen] = useState(false);

  // API 호출
  const { data, refetch: refetchProjectUsers } = useGetProjectUsers(projectId, searchParams);
  const { data: projectRolesData } = useGetProjectRoles(projectId, { page: 1, size: 1000 });
  const { mutate: assignProjectUsers, isPending: isAssigningProjectUsers } = useAssignProjectUsers(projectId);
  const { mutate: deleteProjectUsers, isPending: isDeletingProjectUsers } = useDeleteProjectUsers(projectId);

  const openInviteFlow = useCallback(() => {
    setInviteStep(1);
    setInviteSelectedUsers([]);
    setInviteUserRoles({});
    setInviteSearchForm({ filterType: 'jkwNm', keyword: '' });
    setIsInvitePopupOpen(true);
  }, []);

  const resetInviteFlow = useCallback(() => {
    setIsInvitePopupOpen(false);
    setInviteStep(1);
    setInviteSelectedUsers([]);
    setInviteUserRoles({});
    setInviteSearchForm({ filterType: 'jkwNm', keyword: '' });
  }, []);

  const handleInviteStep1Next = useCallback((users: ProjectUserType[], searchFormData: { filterType: string; keyword: string }) => {
    // Step1에서 선택된 사용자 목록과 검색 폼 상태를 다음 단계로 전달
    setInviteSelectedUsers(users);
    setInviteSearchForm(searchFormData);

    setInviteUserRoles(prev => {
      const next: Record<string, string> = {};

      users.forEach(user => {
        const key = user.uuid || user.memberId;

        if (key && prev[key]) {
          next[key] = prev[key];
        }
      });

      return next;
    });

    setInviteStep(2);
  }, []);

  const handleInviteComplete = useCallback(
    (assignments: { user: ProjectUserType; roleUuid: string }[]) => {
      const payload: ProjectUserAssignRequest = {
        assignments: assignments
          .map(({ user, roleUuid }) => {
            if (!user.uuid) {
              // 사용자 UUID가 없어 역할 할당에서 제외
              return null;
            }

            return {
              userUuid: user.uuid,
              roleUuid,
            };
          })
          .filter((assignment): assignment is ProjectUserAssignRequest['assignments'][number] => assignment !== null),
      };

      if (payload.assignments.length === 0) {
        // 요청할 역할 할당 정보가 없음
        return;
      }

      assignProjectUsers(payload, {
        onSuccess: async response => {
          const successCount = response?.data?.successCount ?? 0;
          const failureCount = response?.data?.failureCount ?? 0;

          if (failureCount === 0) {
            openAlert({
              title: '완료',
              message: '구성원 초대를 완료했습니다.',
              confirmText: '확인',
            });
          } else if (successCount === 0) {
            openAlert({
              title: '실패',
              message: '구성원 초대에 실패했습니다.',
              confirmText: '확인',
            });
          } else {
            openAlert({
              title: '안내',
              message: `구성원 초대가 완료되었습니다.\n${successCount}건 성공, ${failureCount}건 실패\n실패한 항목은 확인 후 다시 시도해주세요.`,
              confirmText: '확인',
            });
          }

          // 페이지를 1페이지로 리셋하여 최신 데이터를 확실히 표시
          setSearchParams({ ...searchParams, page: 1 });
          await refetchProjectUsers();
          resetInviteFlow();
        },
      });
    },
    [assignProjectUsers, openAlert, refetchProjectUsers, resetInviteFlow, searchParams, setSearchParams]
  );

  const handleDeleteUsers = useCallback(
    async (userUuids: string[]) => {
      const filteredUuids = userUuids.filter(id => !!id);

      if (filteredUuids.length === 0 || isDeletingProjectUsers) {
        openAlert({
          message: '삭제할 항목을 선택해 주세요.',
          title: '안내',
        });
        return;
      }

      const confirmed = await openConfirm({
        title: '안내',
        message: '삭제하시겠어요?\n삭제한 정보는 복구할 수 없습니다.',
        cancelText: '아니요',
        confirmText: '예',
      });

      if (!confirmed) {
        return;
      }

      const payload: ProjectUserDeleteRequest = {
        userUuids: filteredUuids,
      };

      deleteProjectUsers(payload, {
        onSuccess: async response => {
          const successCount = response?.data?.successCount ?? 0;
          const failureCount = response?.data?.failureCount ?? 0;

          if (failureCount === 0) {
            await openAlert({
              title: '완료',
              message: '구성원 삭제를 완료했습니다.',
              confirmText: '확인',
            });
          } else {
            await openAlert({
              title: '안내',
              message: `구성원 삭제가 완료되었습니다.\n${successCount}건 성공, ${failureCount}건 실패\n실패한 항목은 확인 후 다시 시도해주세요.`,
              confirmText: '확인',
            });
          }

          setSelectedUsers([]);
          // 페이지를 1페이지로 리셋하여 최신 데이터를 확실히 표시
          setSearchParams({ ...searchParams, page: 1 });
          await refetchProjectUsers();
        },
      });
    },
    [deleteProjectUsers, isDeletingProjectUsers, openAlert, openConfirm, refetchProjectUsers, searchParams, setSearchParams]
  );

  // 역할 변경 완료 시 선택 상태 초기화 + 목록 갱신
  const handleRoleUpdateSuccess = useCallback(async () => {
    setSelectedUsers([]);
    await refetchProjectUsers();
  }, [refetchProjectUsers]);

  // ================================
  // 이벤트 핸들러
  // ================================

  // 전체 선택 여부 계산
  const gridData = useMemo<GridRowData[]>(() => {
    return (data?.content || []).map(user => ({
      id: user.uuid ?? '',
      uuid: user.uuid ?? '',
      memberId: user.memberId,
      jkwNm: user.jkwNm,
      deptNm: user.deptNm,
      roleNm: user.roleNm,
      roleUuid: user.roleUuid,
      dmcStatus: user.dmcStatus,
      retrJkwYn: user.retrJkwYn ?? '',
      lstLoginAt: user.lstLoginAt,
    }));
  }, [data?.content]);

  const selectedIds = useMemo(() => selectedUsers.map(user => user.uuid).filter((uuid): uuid is string => Boolean(uuid)), [selectedUsers]);

  // 구성원 역할 변경 팝업으로 넘길 사용자 목록 (portalAdmin flag 활용 예정)
  const selectedUsersForRoleUpdate = useMemo<RoleUpdatableUser[]>(() => selectedUsers, [selectedUsers]);

  // 역할 변경 팝업 드롭다운에서 사용할 수 있도록 프로젝트 전체 역할 옵션 캐싱
  const roleOptions = useMemo(
    () =>
      projectRolesData?.content?.map(role => ({
        value: role.uuid,
        label: role.roleNm,
      })) ?? [],
    [projectRolesData]
  );

  const totalPages = useMemo(() => {
    const totalElements = data?.totalElements ?? 0;
    const pageSize = searchParams.size || 1;
    const derivedTotalPages = Math.ceil(totalElements / pageSize);

    return Math.max(1, derivedTotalPages || data?.totalPages || 0);
  }, [data?.totalElements, data?.totalPages, searchParams.size]);

  // more 메뉴 구성 (단건 삭제)
  const moreMenuConfig = useMemo<UIMoreMenuConfig<{ uuid?: string }>>(
    () => ({
      items: [
        {
          label: '삭제',
          action: 'delete',
          onClick: row => {
            if (row?.uuid) {
              void handleDeleteUsers([row.uuid]);
            }
          },
        },
      ],
    }),
    [handleDeleteUsers]
  );

  const handleSearch = () => {
    const params: ProjectUserSearchParams = {
      page: 1,
      size: searchParams.size,
      filterType: searchForm.filterType,
      keyword: searchForm.keyword?.trim() || undefined,
      dmcStatus: searchForm.dmcStatus === 'all' ? undefined : searchForm.dmcStatus,
      retrJkwYn: searchForm.retrJkwYn === 'all' ? undefined : searchForm.retrJkwYn,
    };

    setSearchParams(params);
  };

  // 사용자 행 클릭 시 상세 페이지로 이동
  const handleUserRowClick = useCallback(
    (event: any) => {
      const memberId = event.data?.memberId;

      // 포탈 관리자인 경우만 이동 가능
      if (memberId && isPortalAdmin) {
        navigate(`/admin/user-mgmt/${memberId}`);
      } else {
        openAlert({
          title: '안내',
          message: '사용자 조회 권한이 없습니다.',
        });
      }
    },
    [navigate]
  );

  const handleDropdownToggle = (key: keyof typeof dropdownStates) => {
    const isCurrentOpen = dropdownStates[key];

    setDropdownStates({
      ...INITIAL_DROPDOWN_STATES,
      [key]: !isCurrentOpen,
    });
  };

  const handleDropdownSelect = <K extends keyof UserSearchValues>(key: K, value: UserSearchValues[K]) => {
    setSearchForm({ ...searchForm, [key]: value });
    setDropdownStates(INITIAL_DROPDOWN_STATES);
  };

  // ================================
  // 그리드 컬럼 정의
  // ================================

  const columnDefs = useMemo(() => {
    return [
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
        sortable: true,
        suppressHeaderMenuButton: false,
        suppressSizeToFit: true,
        valueGetter: (params: { node: { rowIndex: number } }) => {
          return (searchParams.page - 1) * searchParams.size + params.node.rowIndex + 1;
        },
      },
      {
        headerName: '역할명',
        field: 'roleNm',
        width: 160,
      },
      {
        headerName: '계정 상태',
        field: 'dmcStatus',
        width: 120,
        cellRenderer: React.memo((params: { value: string }) => {
          const rawValue = params.value ?? '';
          const label = DMC_STATUS_LABEL[rawValue] || rawValue || '미확인';
          const intent: 'blue' | 'gray' | 'red' = rawValue === 'DORMANT' ? 'gray' : rawValue === 'WITHDRAW' ? 'red' : 'blue';

          return <UITextLabel intent={intent}>{label}</UITextLabel>;
        }),
      },
      {
        headerName: '이름',
        field: 'jkwNm',
        flex: 1,
      },
      {
        headerName: '인사 상태',
        field: 'retrJkwYn',
        width: 120,
        cellRenderer: React.memo((params: { value: string }) => {
          const rawValue = params.value ?? '';
          const badge = RETR_JKW_YN_BADGE_MAP[rawValue] || {
            label: rawValue || '미확인',
            intent: 'complete' as const,
          };

          return (
            <UILabel variant='badge' intent={badge.intent}>
              {badge.label}
            </UILabel>
          );
        }),
      },
      {
        headerName: '부서',
        field: 'deptNm',
        flex: 1,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '마지막 접속 일시',
        field: 'lstLoginAt',
        width: 180,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: '',
        // eslint-disable-next-line @typescript-eslint/no-explicit-any
        field: 'more' as any,
        width: 56,
      },
    ];
  }, [searchParams.page, searchParams.size]);

  // ================================
  // 렌더링
  // ================================

  return (
    <>
      {/* 검색 영역 - 새 퍼블리싱 구조 */}
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
                            onSelect={value => handleDropdownSelect(DROPDOWN_KEYS.FILTER_TYPE, value as UserSearchValues['filterType'])}
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
                    <td colSpan={3}>
                      <UIUnitGroup gap={32} direction='row'>
                        <div className='flex-1'>
                          <UIDropdown
                            value={searchForm.dmcStatus}
                            options={dmcStatusOptions}
                            isOpen={dropdownStates.dmcStatus}
                            onClick={() => handleDropdownToggle(DROPDOWN_KEYS.DMC_STATUS)}
                            onSelect={value => handleDropdownSelect(DROPDOWN_KEYS.DMC_STATUS, value as UserSearchValues['dmcStatus'])}
                          />
                        </div>
                        <div className='flex-1'>
                          <UIUnitGroup gap={16} direction='row' vAlign='center'>
                            <div style={{ width: '92px', flexShrink: 0 }}>
                              <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                                인사 상태
                              </UITypography>
                            </div>
                            <div className='flex-1'>
                              <UIDropdown
                                value={searchForm.retrJkwYn}
                                options={retrJkwYnOptions}
                                isOpen={dropdownStates.retrJkwYn}
                                onClick={() => handleDropdownToggle(DROPDOWN_KEYS.RETR_JKW_YN)}
                                onSelect={value => handleDropdownSelect(DROPDOWN_KEYS.RETR_JKW_YN, value as UserSearchValues['retrJkwYn'])}
                              />
                            </div>
                          </UIUnitGroup>
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

      {/* 데이터 그룹 - 새 퍼블리싱 컴포넌트 */}
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
                  <div className='flex items-center gap-2'>
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
                    <Button
                      auth={AUTH_KEY.ADMIN.PROJECT_MEMBER_ADD}
                      className='btn-tertiary-outline'
                      onClick={openInviteFlow}
                      disabled={data?.content.length === 0 || isPublicProject}
                    >
                      구성원 초대하기
                    </Button>
                    <div style={{ width: '180px', flexShrink: 0 }}>
                      <UIDropdown
                        value={String(searchParams.size)}
                        options={[
                          { value: '12', label: '12개씩 보기' },
                          { value: '36', label: '36개씩 보기' },
                          { value: '60', label: '60개씩 보기' },
                        ]}
                        onSelect={(value: string) => {
                          const size = Number(value);
                          if (!Number.isNaN(size)) {
                            setSearchParams({ ...searchParams, page: 1, size });
                          }
                        }}
                        height={40}
                        variant='dataGroup'
                        disabled={gridData.length === 0}
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
              rowData={gridData}
              selectedDataList={selectedUsers}
              checkKeyName='uuid'
              moreMenuConfig={isPublicProject || env.VITE_RUN_MODE === RUN_MODE_TYPES.PROD ? null : moreMenuConfig}
              // eslint-disable-next-line @typescript-eslint/no-explicit-any
              columnDefs={columnDefs as any}
              onClickRow={handleUserRowClick}
              onCheck={(rows: GridRowData[]) => {
                setSelectedUsers(rows);
              }}
            />
          </UIListContentBox.Body>
          <UIListContentBox.Footer className='ui-data-has-btn'>
            <Button
              auth={AUTH_KEY.ADMIN.PROJECT_MEMBER_DELETE}
              className='btn-option-outlined'
              style={{ width: '40px' }}
              onClick={() => handleDeleteUsers(selectedIds)}
              disabled={gridData.length === 0 || isPublicProject}
            >
              삭제
            </Button>
            <UIPagination
              currentPage={searchParams.page}
              totalPages={totalPages}
              onPageChange={(page: number) => setSearchParams({ ...searchParams, page })}
              className='flex justify-center'
            />
          </UIListContentBox.Footer>
        </UIListContainer>
      </UIArticle>

      {/* 구성원 초대하기 팝업 */}
      <ProjUserInviteStep1Popup
        isOpen={isInvitePopupOpen && inviteStep === 1}
        onClose={resetInviteFlow}
        onNext={handleInviteStep1Next}
        projectId={projectId}
        initialSelectedUsers={inviteSelectedUsers}
        initialSearchForm={inviteSearchForm}
      />

      <ProjUserInviteStep2Popup
        isOpen={isInvitePopupOpen && inviteStep === 2}
        onClose={resetInviteFlow}
        onPrevious={currentUserRoles => {
          // Step2에서 변경한 역할 상태를 저장하여 다시 돌아올 때 유지되도록 함
          setInviteUserRoles(currentUserRoles);
          setInviteStep(1);
        }}
        onComplete={handleInviteComplete}
        projectId={projectId}
        selectedUsers={inviteSelectedUsers}
        initialUserRoles={inviteUserRoles}
        isSubmitting={isAssigningProjectUsers}
      />

      <ProjRoleUserUpdatePopup
        projectId={projectId}
        isOpen={isRoleUpdatePopupOpen}
        onClose={() => setIsRoleUpdatePopupOpen(false)}
        onSuccess={handleRoleUpdateSuccess}
        users={selectedUsersForRoleUpdate}
        roleOptions={roleOptions}
      />
    </>
  );
};
