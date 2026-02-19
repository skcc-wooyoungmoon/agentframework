import React, { useEffect, useMemo, useState } from 'react';

import { UIBox, UIButton2, UIDataCnt, UITypography } from '@/components/UI/atoms';
import { UIPagination } from '@/components/UI/atoms/UIPagination';
import { UIDropdown, UIGroup, UIInput, UIUnitGroup } from '@/components/UI/molecules';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { STORAGE_KEYS } from '@/constants/common/storage.constants';
import { useBackRestoredState } from '@/hooks/common/navigation';
import { ProjRoleUpdatePermissionStep1Popup, ProjRoleUpdatePermissionStep2Popup } from '@/pages/admin/projMgmt';
import type { GetProjectRoleAuthoritiesRequest } from '@/services/admin/projMgmt';
import { useGetProjectRoleAuthorities } from '@/services/admin/projMgmt';
import { useModal } from '@/stores/common/modal';
import { Button } from '@/components/common/auth';
import { AUTH_KEY } from '@/constants/auth';

type RoleSummary = {
  id: string;
  name: string;
  description?: string | null;
  type?: 'DEFAULT' | 'CUSTOM' | string;
};

type ProjRolePermissionListPageProps = {
  projectId: string;
  role: RoleSummary;
};

type PermissionSearchForm = {
  filterType: 'authorityNm' | 'dtlCtnt';
  keyword: string;
  menu: string;
};

type AppliedFilters = {
  filterType: 'authorityNm' | 'dtlCtnt';
  keyword?: string;
  twoDepthMenu?: string;
};

type PermissionRow = {
  no: number;
  authorityId: string;
  menuName: string;
  authorityName: string;
  authorityDetail: string;
};

const FILTER_TYPE_OPTIONS = [
  { value: 'authorityNm', label: '권한명' },
  { value: 'dtlCtnt', label: '상세 권한' },
];

const MENU_OPTIONS = [
  { label: '전체', value: 'all' },
  { label: 'IDE', value: 'IDE' },
  { label: '프로젝트', value: '프로젝트' },
  { label: '데이터 탐색', value: '데이터 탐색' },
  { label: '지식/학습 데이터 관리', value: '지식/학습 데이터 관리' },
  { label: '데이터 도구', value: '데이터 도구' },
  { label: '모델 탐색', value: '모델 탐색' },
  { label: '모델 관리', value: '모델 관리' },
  { label: '파인튜닝', value: '파인튜닝' },
  { label: '플레이그라운드', value: '플레이그라운드' },
  { label: '추론 프롬프트', value: '추론 프롬프트' },
  { label: '퓨샷', value: '퓨샷' },
  { label: '가드레일', value: '가드레일' },
  { label: '워크플로우', value: '워크플로우' },
  { label: '빌더', value: '빌더' },
  { label: 'Tools', value: 'Tools' },
  { label: 'MCP 서버', value: 'MCP 서버' },
  { label: '평가', value: '평가' },
  { label: '모델 배포', value: '모델 배포' },
  { label: '에이전트 배포', value: '에이전트 배포' },
  { label: 'API Key', value: 'API Key' },
  { label: '세이프티 필터', value: '세이프티 필터' },
  { label: '운영 배포', value: '운영 배포' },
  { label: '사용자 관리', value: '사용자 관리' },
  { label: '프로젝트 관리', value: '프로젝트 관리' },
  { label: '자원 관리', value: '자원 관리' },
  { label: '사용자 이용 현황', value: '사용자 이용 현황' },
  { label: '공지사항 관리', value: '공지사항 관리' },
  { label: 'API Key 관리', value: 'API Key 관리' },
];

const PAGE_SIZE_OPTIONS = [
  { value: '12', label: '12개씩 보기' },
  { value: '36', label: '36개씩 보기' },
  { value: '60', label: '60개씩 보기' },
];

const DROPDOWN_KEYS = {
  FILTER_TYPE: 'filterType',
  MENU: 'menu',
} as const;

const INITIAL_DROPDOWN_STATES = {
  [DROPDOWN_KEYS.FILTER_TYPE]: false,
  [DROPDOWN_KEYS.MENU]: false,
};

/**
 * 프로젝트 상세 > 역할 상세 > 권한 목록 페이지
 */
export const ProjRolePermissionListPage: React.FC<ProjRolePermissionListPageProps> = ({ projectId, role }) => {
  const { openAlert } = useModal();
  const { type } = role;

  const isDefaultRole = type === 'DEFAULT';

  // 권한 추가 팝업 상태 관리
  const [isAddPermissionStep1Open, setIsAddPermissionStep1Open] = useState(false);
  const [isAddPermissionStep2Open, setIsAddPermissionStep2Open] = useState(false);

  // 드롭다운 상태 관리
  const [dropdownStates, setDropdownStates] = useState(INITIAL_DROPDOWN_STATES);

  // ================================
  // 드롭다운 관련 함수
  // ================================
  const handleDropdownToggle = (key: keyof typeof dropdownStates) => {
    const isCurrentOpen = dropdownStates[key];

    setDropdownStates({
      ...INITIAL_DROPDOWN_STATES,
      [key]: !isCurrentOpen,
    });
  };

  const handleDropdownSelect = (key: keyof PermissionSearchForm, value: string) => {
    setSearchForm({ ...searchForm, [key]: value as any });
    setDropdownStates(INITIAL_DROPDOWN_STATES);
  };

  // step1 선택된 권한 아이디
  const [addPermStep1Ids, setAddPermStep1Ids] = useState<string[]>([]);
  // step2 선택된 권한 아이디 (이전 버튼으로 돌아갈 때 유지)
  const [addPermStep2Ids, setAddPermStep2Ids] = useState<string[]>([]);

  const [searchForm, setSearchForm] = useState<PermissionSearchForm>({
    filterType: 'authorityNm',
    keyword: '',
    menu: 'all',
  });

  const [appliedFilters, setAppliedFilters] = useState<AppliedFilters>({
    filterType: 'authorityNm',
  });

  const { filters: paginationParams, updateFilters: setPaginationParams } = useBackRestoredState<{ page: number; size: number }>(
    STORAGE_KEYS.SEARCH_VALUES.PROJ_ROLE_PERMISSION_LIST_PARAMS,
    {
      page: 1,
      size: 12,
    }
  );

  const requestParams = useMemo<GetProjectRoleAuthoritiesRequest>(() => {
    const params: GetProjectRoleAuthoritiesRequest = {
      page: paginationParams.page,
      size: paginationParams.size,
      filterType: appliedFilters.filterType,
    };

    if (appliedFilters.keyword) params.keyword = appliedFilters.keyword;
    if (appliedFilters.twoDepthMenu) params.twoDepthMenu = appliedFilters.twoDepthMenu;

    return params;
  }, [appliedFilters, paginationParams]);

  const { data, isLoading, refetch } = useGetProjectRoleAuthorities(projectId, role.id, requestParams);

  // 전체 권한 목록 조회 (팝업 초기화용) - 페이징 없이 전체 데이터 가져옴
  const { data: allAuthoritiesData, refetch: refetchAllAuthorities } = useGetProjectRoleAuthorities(projectId, role.id, { size: 9999 }, { enabled: !!projectId && !!role.id });

  // 현재 역할에 할당된 메뉴 ID 목록 (hrnkAuthorityId 기준) - Step1 팝업용
  // 전체 데이터(allAuthoritiesData)에서 추출하여 모든 메뉴가 선택되도록 함
  const currentRoleMenuIds = useMemo(() => {
    if (!allAuthoritiesData?.content) return [];

    // 중복 제거: Set 사용
    const menuIds = new Set<string>();
    allAuthoritiesData.content.forEach(auth => {
      if (auth.hrnkAuthorityId) {
        menuIds.add(auth.hrnkAuthorityId);
      }
    });

    return Array.from(menuIds);
  }, [allAuthoritiesData]);

  // Step2 팝업용: Step1에서 선택한 메뉴의 권한만 (authorityId 기준)
  // 전체 데이터(allAuthoritiesData)에서 추출하여 모든 권한이 포함되도록 함
  const currentRoleAuthoritiesForStep2 = useMemo(() => {
    if (!allAuthoritiesData?.content || addPermStep1Ids.length === 0) return [];

    // Step1에서 선택한 메뉴 ID Set
    const selectedMenuSet = new Set(addPermStep1Ids);

    // 선택된 메뉴에 해당하는 권한만 필터링
    return allAuthoritiesData.content.filter(auth => auth.hrnkAuthorityId && selectedMenuSet.has(auth.hrnkAuthorityId)).map(auth => auth.authorityId);
  }, [allAuthoritiesData, addPermStep1Ids]);

  // Step2 팝업에 전달할 초기 선택 권한 (기존 역할 권한 + 이전에 Step2에서 선택한 권한 병합)
  const step2InitialAuthorityIds = useMemo(() => {
    const combined = new Set([...currentRoleAuthoritiesForStep2, ...addPermStep2Ids]);
    return Array.from(combined);
  }, [currentRoleAuthoritiesForStep2, addPermStep2Ids]);

  const totalCount = data?.totalElements ?? 0;
  const totalPages = data?.totalPages && data.totalPages > 0 ? data.totalPages : 1;

  useEffect(() => {
    if (!isLoading && paginationParams.page > totalPages) {
      setPaginationParams({ ...paginationParams, page: totalPages });
    }
  }, [isLoading, paginationParams, totalPages, setPaginationParams]);

  const rowData = useMemo<PermissionRow[]>(() => {
    const content = data?.content ?? [];
    const baseIndex = (paginationParams.page - 1) * paginationParams.size;

    return content.map((authority, index) => ({
      no: baseIndex + index + 1,
      authorityId: authority.authorityId ?? '',
      menuName: authority.twoDepthMenu ?? authority.oneDepthMenu ?? '',
      authorityName: authority.authorityNm,
      authorityDetail: authority.detailContent ?? '',
    }));
  }, [data, paginationParams]);

  const handlePageChange = (nextPage: number) => {
    setPaginationParams({ ...paginationParams, page: nextPage });
  };

  const handlePageSizeChange = (value: string) => {
    const nextSize = Number(value);
    setPaginationParams({ ...paginationParams, page: 1, size: nextSize });
  };

  const handleSearch = () => {
    setPaginationParams({ ...paginationParams, page: 1 });

    setAppliedFilters({
      filterType: searchForm.filterType,
      keyword: searchForm.keyword.trim() || undefined,
      twoDepthMenu: searchForm.menu !== 'all' ? searchForm.menu : undefined,
    });
  };

  const handleOpenAddPermission = () => {
    if (isDefaultRole) {
      openAlert({
        message: '기본역할은 권한을 변경할 수 없습니다.',
        title: '안내',
        confirmText: '확인',
      });

      return;
    }

    // 현재 역할의 메뉴 ID로 초기화
    setAddPermStep1Ids(currentRoleMenuIds);
    setIsAddPermissionStep1Open(true);
  };

  const handleCloseStep1 = () => {
    setIsAddPermissionStep1Open(false);
  };

  const handleNextFromStep1 = (selectedMenuIds: string[]) => {
    setAddPermStep1Ids(selectedMenuIds);
    setIsAddPermissionStep1Open(false);
    setIsAddPermissionStep2Open(true);
  };

  const handleCloseStep2 = () => {
    setIsAddPermissionStep2Open(false);
  };

  const handleBackToStep1 = (selectedAuthorityIds: string[]) => {
    setAddPermStep2Ids(selectedAuthorityIds);
    setIsAddPermissionStep2Open(false);
    setIsAddPermissionStep1Open(true);
  };

  const handleSavePermissions = () => {
    setIsAddPermissionStep2Open(false);
    void refetch();
    void refetchAllAuthorities(); // 전체 데이터도 새로고침하여 팝업 초기값 동기화
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
        } as any,
        sortable: false,
        suppressHeaderMenuButton: true,
        suppressSizeToFit: true,
      },
      {
        headerName: '메뉴명',
        field: 'menuName' as const,
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
        headerName: '권한명',
        field: 'authorityName' as const,
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
        headerName: '상세 권한',
        field: 'authorityDetail' as const,
        flex: 1,
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
    ],
    []
  );

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
                        메뉴명
                      </UITypography>
                    </th>
                    <td colSpan={3}>
                      <div style={{ width: '540px' }}>
                        <UIDropdown
                          value={searchForm.menu}
                          options={MENU_OPTIONS}
                          isOpen={dropdownStates.menu}
                          onClick={() => handleDropdownToggle(DROPDOWN_KEYS.MENU)}
                          onSelect={value => handleDropdownSelect(DROPDOWN_KEYS.MENU, value)}
                        />
                      </div>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
            <div style={{ width: '128px' }}>
              <UIButton2 className='btn-secondary-blue' style={{ width: '100%' }} onClick={handleSearch} disabled={isLoading}>
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
                      <UIDataCnt count={totalCount} prefix='총' />
                    </div>
                  </div>
                  <div className='flex' style={{ gap: '12px' }}>
                    <div className=''>
                      <Button auth={AUTH_KEY.ADMIN.ROLE_PERMISSION_ADD} className='btn-tertiary-outline' onClick={handleOpenAddPermission} disabled={data?.content?.length === 0}>
                        권한 설정하기
                      </Button>
                    </div>
                    <div style={{ width: '180px', flexShrink: 0 }}>
                      <UIDropdown
                        value={String(paginationParams.size)}
                        options={PAGE_SIZE_OPTIONS}
                        onSelect={handlePageSizeChange}
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
            <UIGrid type='default' rowData={rowData} columnDefs={columnDefs} />
          </UIListContentBox.Body>
          <UIListContentBox.Footer className='ui-data-has-btn'>
            <UIPagination currentPage={paginationParams.page} totalPages={totalPages} onPageChange={handlePageChange} className='flex justify-center' />
          </UIListContentBox.Footer>
        </UIListContainer>
      </UIArticle>

      {/* 권한 추가 1 팝업*/}
      {isAddPermissionStep1Open && (
        <ProjRoleUpdatePermissionStep1Popup isOpen={isAddPermissionStep1Open} onClose={handleCloseStep1} onNext={handleNextFromStep1} initialSelectedMenuIds={addPermStep1Ids} />
      )}

      {/* 권한 추가 2 팝업*/}
      {isAddPermissionStep2Open && (
        <ProjRoleUpdatePermissionStep2Popup
          isOpen={isAddPermissionStep2Open}
          onClose={handleCloseStep2}
          onPrevious={handleBackToStep1}
          onSave={handleSavePermissions}
          selectedMenuIds={addPermStep1Ids}
          initialSelectedAuthorityIds={step2InitialAuthorityIds}
          projectId={projectId}
          roleId={role.id}
        />
      )}
    </>
  );
};
