import React, { useCallback, useEffect, useMemo, useState } from 'react';

import { useQueryClient } from '@tanstack/react-query';
import type { CellClickedEvent, ColDef } from 'ag-grid-community';
import { useSetAtom } from 'jotai';
import { useNavigate } from 'react-router';

import { Button } from '@/components/common/auth';
import { UIBox, UIButton2, UIDataCnt, UIPagination, UITypography } from '@/components/UI';
import { UIArticle, UIDropdown, UIGroup, UIInput, UIUnitGroup } from '@/components/UI/molecules';
import { UIGrid, type UIMoreMenuConfig } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { AUTH_KEY } from '@/constants/auth';
import { env, RUN_MODE_TYPES } from '@/constants/common/env.constants.ts';
import { STORAGE_KEYS } from '@/constants/common/storage.constants';
import { useBackRestoredState } from '@/hooks/common/navigation';
import { ProjRoleCreateStep1Popup, ProjRoleCreateStep2Popup, ProjRoleCreateStep3Popup } from '@/pages/admin/projMgmt';
import {
  type CreateProjectRoleRequest,
  type GetProjectRolesRequest,
  type GetProjectRolesResponse,
  type ProjectDetailType,
  RoleType,
  useCreateProjectRole,
  useDeleteProjectRoles,
  useGetProjectRoles,
} from '@/services/admin/projMgmt';
import { getProjectRoleTypeLabel } from '@/services/admin/projMgmt/projMgmt.mappers';
import { projRoleListRefetchAtom } from '@/stores/admin/projMgmt/roles';
import { useModal } from '@/stores/common/modal';

// ================================
// 상수 정의
// ================================

// 드롭다운 키들
const DROPDOWN_KEYS = {
  TYPE: 'roleType',
  FILTER_TYPE: 'filterType',
} as const;

// 드롭다운 초기 상태
const INITIAL_DROPDOWN_STATES = {
  [DROPDOWN_KEYS.TYPE]: false,
  [DROPDOWN_KEYS.FILTER_TYPE]: false,
};

const PAGE_SIZE_OPTIONS = [
  { value: '12', label: '12개씩 보기' },
  { value: '36', label: '36개씩 보기' },
  { value: '60', label: '60개씩 보기' },
];

// 기본 역할 체크 함수
const isDefaultRole = (roleName: string) => {
  return ['프로젝트 관리자', '개발자', '테스터'].includes(roleName);
};

// 드롭다운 옵션들
const typeOptions = [
  { label: '전체', value: 'all' },
  { label: '기본', value: RoleType.DEFAULT },
  { label: '사용자 정의', value: RoleType.CUSTOM },
];

const filterOptions = [
  { label: '역할명', value: 'roleNm' },
  { label: '설명', value: 'dtlCtnt' },
];

/**
 * 프로젝트 관리 > 프로젝트 상세 > (TAB) 역할 정보
 */
export const ProjRoleListPage = ({ projectInfo }: { projectInfo: ProjectDetailType }) => {
  const isPublicProject = projectInfo.prjSeq === -999;

  // ================================
  // State 관리
  // ================================

  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const { openAlert, openConfirm } = useModal();

  const [dropdownStates, setDropdownStates] = useState(INITIAL_DROPDOWN_STATES);
  const [selectedIds, setSelectedIds] = useState<string[]>([]);

  // 검색 폼 상태를 sessionStorage에 저장하여 뒤로가기 시 복원
  const { filters: searchForm, updateFilters: setSearchForm } = useBackRestoredState(STORAGE_KEYS.SEARCH_VALUES.PROJ_ROLE_LIST_FORM, {
    roleType: 'all',
    filterType: 'roleNm',
    keyword: '',
  });

  const resetSelection = useCallback(() => {
    setSelectedIds([]);
  }, []);

  // 검색 파라미터를 sessionStorage에 저장하여 뒤로가기 시 복원
  const { filters: searchParams, updateFilters: setSearchParams } = useBackRestoredState<GetProjectRolesRequest>(STORAGE_KEYS.SEARCH_VALUES.PROJ_ROLE_LIST_PARAMS, {
    page: 1,
    size: 12,
    roleType: undefined,
    filterType: 'roleNm',
    keyword: '',
  });

  const [gridKey, setGridKey] = useState(0);

  const [isCreatePopupOpen, setIsCreatePopupOpen] = useState(false);
  const [currentStep, setCurrentStep] = useState(1);

  const [step1FormData, setStep1FormData] = useState({
    roleNm: '',
    dtlCtnt: '',
  });

  const [step2FormData, setStep2FormData] = useState({
    selectedMenuIds: [] as string[],
  });

  const [step3FormData, setStep3FormData] = useState({
    selectedDetailIds: [] as string[],
  });

  // ================================
  // 팝업 상태 제어
  // ================================

  /**
   * 역할 생성 팝업 상태 초기화 및 닫기
   */
  const resetAndCloseCreateRolePopup = () => {
    setIsCreatePopupOpen(false);
    setCurrentStep(1);
    setStep1FormData({ roleNm: '', dtlCtnt: '' });
    setStep2FormData({ selectedMenuIds: [] });
    setStep3FormData({ selectedDetailIds: [] });
  };

  /**
   * 역할 생성 팝업 열기 (초기 상태로)
   */
  const openCreateRolePopup = () => {
    setCurrentStep(1);
    setStep1FormData({ roleNm: '', dtlCtnt: '' });
    setStep2FormData({ selectedMenuIds: [] });
    setStep3FormData({ selectedDetailIds: [] });
    setIsCreatePopupOpen(true);
  };

  // ================================
  // API 호출
  // ================================
  const { data, refetch } = useGetProjectRoles(projectInfo.uuid, searchParams);
  const setProjRoleListRefetch = useSetAtom(projRoleListRefetchAtom);

  useEffect(() => {
    setProjRoleListRefetch(() => refetch);
  }, [refetch, setProjRoleListRefetch]);

  const createProjectRoleMutation = useCreateProjectRole(projectInfo.uuid, {
    onSuccess: async response => {
      const uuid = response.data.uuid;
      await queryClient.invalidateQueries({
        queryKey: ['GET', `/admin/projects/${projectInfo.uuid}/roles`],
      });

      openAlert({
        bodyType: 'text',
        title: '완료',
        message: '새 역할 만들기를 완료했습니다.',
        confirmText: '확인',
        onConfirm: () => {
          resetAndCloseCreateRolePopup();
          navigate(`/admin/project-mgmt/${projectInfo.uuid}/roles/${uuid}`);
        },
      });
    },
  });

  const { mutateAsync: deleteProjectRoles, isPending: isDeletingRoles } = useDeleteProjectRoles(projectInfo.uuid);

  // ================================
  // 이벤트 핸들러
  // ================================

  /**
   * 역할 생성
   */
  const handleCreateRole = useCallback(
    (payload: CreateProjectRoleRequest) => {
      createProjectRoleMutation.mutate(payload);
    },
    [createProjectRoleMutation]
  );

  /**
   * 검색 조건에 따라 역할 목록 조회
   */
  const handleSearch = () => {
    const params: GetProjectRolesRequest = {
      page: 1,
      size: searchParams.size,
      roleType: searchForm.roleType === 'all' ? undefined : (searchForm.roleType as RoleType),
      filterType: searchForm.filterType as 'roleNm' | 'dtlCtnt',
      keyword: searchForm.keyword ?? '',
    };

    setSearchParams(params);
    resetSelection();
  };

  /**
   * 드롭다운 열림/닫힘 토글
   */
  const handleDropdownToggle = (key: keyof typeof dropdownStates) => {
    const isCurrentOpen = dropdownStates[key];

    setDropdownStates({
      ...INITIAL_DROPDOWN_STATES,
      [key]: !isCurrentOpen,
    });
  };

  /**
   * 드롭다운에서 옵션 선택 시 검색 폼 업데이트
   */
  const handleDropdownSelect = (key: keyof typeof searchForm, value: string) => {
    setSearchForm({ ...searchForm, [key]: value });
    setDropdownStates(INITIAL_DROPDOWN_STATES);
  };

  /**
   * 선택된 역할 삭제 (기본 역할 삭제 불가, 사용자 확인 필요)
   */
  const handleDeleteRoles = useCallback(
    async (roleIds: string[]) => {
      if (isDeletingRoles) {
        return;
      }

      if (roleIds.length === 0) {
        openAlert({
          message: '삭제할 항목을 선택해 주세요.',
          title: '안내',
        });
        return;
      }

      const defaultRoleIds = new Set((data?.content || []).filter(role => isDefaultRole(role.roleNm)).map(role => role.uuid));

      if (roleIds.some(roleId => defaultRoleIds.has(roleId))) {
        openAlert({
          title: '안내',
          message: '사전 정의된 역할의 경우 역할을 삭제할 수 없습니다.\n 해당 역할 제외 후 다시 시도해주세요.',
          confirmText: '확인',
        });
        return;
      }

      // 삭제 확인 컨펌
      const confirmed = await openConfirm({
        title: '안내',
        message: '삭제 하시겠어요?\n삭제한 정보는 복구할 수 없습니다.',
        confirmText: '예',
        cancelText: '아니요',
      });

      if (!confirmed) {
        return;
      }

      const response = await deleteProjectRoles({ roleUuids: roleIds });
      const { successCount, failureCount, errorMessage } = response.data;
      const hasDeletedRoles = (successCount ?? 0) > 0;

      let resultMessage = '';
      let alertTitle = '완료';

      if (errorMessage) {
        alertTitle = '안내';
        resultMessage = errorMessage;
      } else if (failureCount > 0) {
        alertTitle = '실패';
        resultMessage = `총 ${roleIds.length}개 중 ${successCount}개 삭제, ${failureCount}개 실패했습니다.`;
      } else {
        resultMessage = '역할이 삭제되었습니다.';
      }

      openAlert({
        title: alertTitle,
        message: resultMessage,
        confirmText: '확인',
      });

      if (hasDeletedRoles) {
        await refetch();
        resetSelection();
        setGridKey(prev => prev + 1);
      }
    },
    [data?.content, deleteProjectRoles, isDeletingRoles, openAlert, openConfirm, refetch, resetSelection]
  );

  const rowData = data?.content ?? [];
  const selectedRows = useMemo(() => {
    if (!selectedIds.length || !rowData.length) {
      return [];
    }

    const selectedIdSet = new Set(selectedIds);

    return rowData.filter(row => selectedIdSet.has(row.uuid));
  }, [rowData, selectedIds]);

  const totalCount = data?.totalElements ?? 0;
  const totalPages = data?.totalPages ?? 1;

  // 더보기 메뉴 설정
  const moreMenuConfig = useMemo<UIMoreMenuConfig<GetProjectRolesResponse>>(
    () => ({
      shouldShowMenu: row => !isDefaultRole(row.roleNm),
      items: [
        {
          label: '삭제',
          action: 'delete',
          onClick: row => {
            void handleDeleteRoles([row.uuid]);
          },
        },
      ],
    }),
    [handleDeleteRoles]
  );

  // ================================
  // 그리드 컬럼 정의
  // ================================

  const roleColumnDefs = useMemo<ColDef<GetProjectRolesResponse>[]>(() => {
    return [
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
          cursor: 'pointer',
        },
        sortable: true,
        suppressHeaderMenuButton: false,
        suppressSizeToFit: true,
        valueGetter: params => {
          return ((searchParams.page || 1) - 1) * (searchParams.size || 12) + (params.node?.rowIndex || 0) + 1;
        },
      },
      {
        headerName: '역할명',
        field: 'roleNm',
        width: 200,
        cellClass: 'cursor-pointer',
      },
      {
        headerName: '유형',
        field: 'roleType',
        width: 120,
        cellClass: 'cursor-pointer',
        cellRenderer: React.memo((params: { value: string }) => {
          return getProjectRoleTypeLabel(params.value);
        }),
      },
      {
        headerName: '설명',
        field: 'dtlCtnt',
        flex: 1,
        minWidth: 250,
        cellClass: 'cursor-pointer',
      },
      {
        headerName: '생성일시',
        field: 'fstCreatedAt',
        width: 180,
        cellClass: 'cursor-pointer',
      },
      {
        headerName: '최종 수정일시',
        field: 'lstUpdatedAt',
        width: 180,
        cellClass: 'cursor-pointer',
      },
      {
        headerName: '',
        field: 'more' as any,
        width: 56,
        minWidth: 56,
        maxWidth: 56,
        sortable: false,
        suppressHeaderMenuButton: true,
        resizable: false,
      },
    ];
  }, [searchParams.page, searchParams.size]);

  /**
   * 그리드 체크박스 선택 시 선택된 역할 ID 목록 업데이트
   */
  const handleGridCheck = useCallback((selectedRows: GetProjectRolesResponse[]) => {
    const newSelectedIds = selectedRows.map(row => row.uuid);

    setSelectedIds(prevIds => {
      // 실제로 변경이 있을 때만 업데이트하여 무한 루프 방지
      if (prevIds.length !== newSelectedIds.length || !prevIds.every(id => newSelectedIds.includes(id))) {
        return newSelectedIds;
      }

      return prevIds; // 변경이 없으면 이전 값 유지
    });
  }, []);

  /**
   * 그리드 행 클릭 시 역할 상세 페이지로 이동 (체크박스 및 더보기 메뉴 클릭 제외)
   */
  const handleGridRowClick = useCallback(
    (event: CellClickedEvent<GetProjectRolesResponse>) => {
      const columnId = event.column?.getColId?.();
      const targetElement = event.event?.target as HTMLElement | null;

      if (targetElement?.closest('input[type="checkbox"]') || columnId === 'more') {
        return;
      }

      const roleId = event.data?.uuid ?? '';

      if (roleId) {
        navigate(`/admin/project-mgmt/${projectInfo.uuid}/roles/${roleId}`);
      }
    },
    [navigate, projectInfo.uuid]
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
                        조회 조건
                      </UITypography>
                    </th>
                    <td colSpan={3}>
                      <UIUnitGroup gap={32} direction='row'>
                        <div className='flex-1'>
                          <UIDropdown
                            value={searchForm.filterType}
                            placeholder='조회 조건 선택'
                            options={filterOptions}
                            isOpen={dropdownStates.filterType}
                            onClick={() => handleDropdownToggle(DROPDOWN_KEYS.FILTER_TYPE)}
                            onSelect={value => handleDropdownSelect(DROPDOWN_KEYS.FILTER_TYPE, value)}
                          />
                        </div>
                        <div className='flex-1'>
                          <UIInput.Search
                            value={searchForm.keyword}
                            onChange={e => {
                              setSearchForm({ ...searchForm, keyword: e.target.value });
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
                        유형
                      </UITypography>
                    </th>
                    <td colSpan={3}>
                      <UIUnitGroup gap={32} direction='row'>
                        <div style={{ width: 'calc(50% - 14px)' }}>
                          <UIDropdown
                            value={searchForm.roleType}
                            placeholder='조회 조건 선택'
                            options={typeOptions}
                            isOpen={dropdownStates.roleType}
                            onClick={() => handleDropdownToggle(DROPDOWN_KEYS.TYPE)}
                            onSelect={value => handleDropdownSelect(DROPDOWN_KEYS.TYPE, value)}
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

      {/* 역할 목록 */}
      <UIArticle className='article-grid'>
        <UIListContainer>
          <UIListContentBox.Header>
            <UIUnitGroup gap={16} direction='column'>
              <div className='flex items-center justify-between w-full'>
                <div className='flex-shrink-0'>
                  <div style={{ width: '168px', paddingRight: '8px' }}>
                    <UIDataCnt count={totalCount} prefix='총' />
                  </div>
                </div>
                <div className='flex items-center gap-2'>
                  {/* 운영환경은 다음 영역 보이지 않음 */}
                  {env.VITE_RUN_MODE !== RUN_MODE_TYPES.PROD && (
                    <Button auth={AUTH_KEY.ADMIN.ROLE_CREATE} className='btn-tertiary-outline' onClick={openCreateRolePopup} disabled={isPublicProject}>
                      새 역할 만들기
                    </Button>
                  )}
                  <div style={{ width: '180px', flexShrink: 0 }}>
                    <UIDropdown
                      value={String(searchParams.size ?? 12)}
                      options={PAGE_SIZE_OPTIONS}
                      onSelect={(value: string) => {
                        const size = Number(value);
                        setSearchParams({ ...searchParams, page: 1, size });
                        resetSelection();
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
            <UIGrid
              key={gridKey}
              type={env.VITE_RUN_MODE !== RUN_MODE_TYPES.PROD ? 'multi-select' : 'default'}
              rowData={rowData}
              columnDefs={roleColumnDefs}
              moreMenuConfig={isPublicProject || env.VITE_RUN_MODE === RUN_MODE_TYPES.PROD ? null : moreMenuConfig}
              onClickRow={handleGridRowClick}
              onCheck={handleGridCheck}
              selectedDataList={selectedRows}
              checkKeyName='uuid'
            />
          </UIListContentBox.Body>
          <UIListContentBox.Footer className='ui-data-has-btn'>
            {/* 운영환경은 다음 영역 보이지 않음 */}
            {env.VITE_RUN_MODE !== RUN_MODE_TYPES.PROD && (
              <Button
                auth={AUTH_KEY.ADMIN.ROLE_DELETE}
                className='btn-option-outlined'
                style={{ width: '40px' }}
                onClick={() => handleDeleteRoles(selectedIds)}
                disabled={data?.content.length === 0 || isPublicProject}
              >
                삭제
              </Button>
            )}
            <UIPagination
              currentPage={searchParams.page ?? 1}
              totalPages={totalPages || 1}
              onPageChange={(page: number) => {
                setSearchParams({ ...searchParams, page });
                resetSelection();
              }}
              className='flex justify-center'
            />
          </UIListContentBox.Footer>
        </UIListContainer>
      </UIArticle>

      {/* 새역할 만들기 팝업 - Step 1 */}
      <ProjRoleCreateStep1Popup
        isOpen={isCreatePopupOpen && currentStep === 1}
        formData={step1FormData}
        onChange={setStep1FormData}
        onClose={resetAndCloseCreateRolePopup}
        onNext={() => setCurrentStep(2)}
        projectInfo={projectInfo}
      />

      {/* 새역할 만들기 팝업 - Step 2 */}
      <ProjRoleCreateStep2Popup
        isOpen={isCreatePopupOpen && currentStep === 2}
        initialSelectedMenuIds={step2FormData.selectedMenuIds}
        onClose={resetAndCloseCreateRolePopup}
        onPrevious={() => setCurrentStep(1)}
        onNext={selectedMenuIds => {
          setStep2FormData({ selectedMenuIds });
          setCurrentStep(3);
        }}
      />

      {/* 새역할 만들기 팝업 - Step 3 */}
      <ProjRoleCreateStep3Popup
        isOpen={isCreatePopupOpen && currentStep === 3}
        onClose={resetAndCloseCreateRolePopup}
        onPrevious={selectedDetailIds => {
          setStep3FormData({ selectedDetailIds });
          setCurrentStep(2);
        }}
        onComplete={payload => {
          setStep3FormData({ selectedDetailIds: payload.authorityIds });
          handleCreateRole(payload);
        }}
        isSubmitting={createProjectRoleMutation.isPending}
        step1Data={step1FormData}
        step2Data={step2FormData}
        initialSelectedDetailIds={step3FormData.selectedDetailIds}
      />
    </>
  );
};
