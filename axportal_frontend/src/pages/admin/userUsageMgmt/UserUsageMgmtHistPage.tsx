import React, { useCallback, useEffect, useMemo, useRef, useState } from 'react';

import { useQueryClient } from '@tanstack/react-query';
import { useAtom, useSetAtom } from 'jotai';
import { useLocation, useNavigate, useNavigationType } from 'react-router-dom';

import { UIDataCnt } from '@/components/UI';
import { UIBox, UIButton2, UILabel, UITypography } from '@/components/UI/atoms';
import { UIPagination } from '@/components/UI/atoms/UIPagination';
import { UIGroup, UIInput, UIUnitGroup } from '@/components/UI/molecules';
import { UIDropdown } from '@/components/UI/molecules/dropdown/UIDropdown';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UIArticle } from '@/components/UI/molecules/UIArticle';

import { useGetCommonProjects } from '@/services/admin/UserUsageMgmt/UserUsageMgmt.service';
import { useModal } from '@/stores/common/modal';

import { exportUserUsageMgmts, selectedUserActivityAtom, useGetUseUsageMgmtList, userUsageHistPaginationAtom, userUsageSelectedRowsAtom, type UserActivity, type UserUsageHistFilter } from './index';
import { routeConfig } from '@/routes/route.config';
import { generateBreadcrumb } from '@/utils/common/breadcrumb.utils';

import { Button } from '@/components/common/auth';
import { AUTH_KEY } from '@/constants/auth/auth.constants';
import { STORAGE_KEYS } from '@/constants/common/storage.constants';
import { useBackRestoredState } from '@/hooks/common/navigation';

const normalizeMenuPath = (menuPath: string): string => {
  let normalized = menuPath?.trim() ?? '';
  if (!normalized) return '';

  if (/^https?:\/\//i.test(normalized)) {
    try {
      normalized = new URL(normalized).pathname;
    } catch (_error) {
      // URL 파싱이 실패하면 원본 경로 문자열을 그대로 사용합니다.
      normalized = menuPath.trim();
    }
  }

  normalized = normalized.split(/[?#]/)[0];

  if (!normalized.startsWith('/')) {
    normalized = `/${normalized.replace(/^\/+/, '')}`;
  }

  normalized = normalized.replace(/\/{2,}/g, '/');

  return normalized;
};

const formatMenuPathLabel = (menuPath?: string | null): string => {
  if (!menuPath) return '-';
  if (menuPath.includes('>')) {
    const parts = menuPath.split('>').map(p => p.trim());
    return parts.slice(0, 2).join(' > ');
  }

  const normalizedPath = normalizeMenuPath(menuPath);
  if (!normalizedPath) return menuPath;

  if (normalizedPath === '/login') {
    return '로그인';
  }

  if (!normalizedPath) return menuPath;

  const breadcrumb = generateBreadcrumb(normalizedPath, routeConfig);
  if (!breadcrumb.length) return menuPath;

  return breadcrumb.slice(0, 2).join(' > ');
};

const getActionByMethod = (action: string): string => {
  const actionMap: { [key: string]: string } = {
    POST: '생성',
    GET: '조회',
    GET_DETAIL: '상세조회',
    PUT: '수정',
    DELETE: '삭제',
  };
  return actionMap[action?.toUpperCase()] || action;
};

const getUsageDetails = (menuPath: string | null | undefined, action: string): string => {
  if (!menuPath) {
    const actionText = getActionByMethod(action);
    return actionText || '-';
  }

  const pathParts = menuPath.split('>');
  const lastPart = pathParts[pathParts.length - 1]?.trim() || '';

  if (lastPart === '로그인' || lastPart === '로그아웃') {
    return lastPart;
  }

  const actionText = getActionByMethod(action);

  return `${lastPart} ${actionText}`;
};

const UserUsageMgmtHistPageComponent = () => {
  const queryClient = useQueryClient();

  const { openAlert, openConfirm } = useModal();
  const showAlert = (message: string) => {
    openAlert({
      message: message,
      title: '안내',
      confirmText: '확인',
    });
  };

  const { data: projectsData } = useGetCommonProjects({
    enabled: true,
  });

  const [paginationState, setPaginationState] = useAtom(userUsageHistPaginationAtom);
  const [currentPage, setCurrentPage] = useState(() => paginationState.page);
  const [pageSize, setPageSize] = useState(() => paginationState.pageSize);

  const setSelectedUserActivity = useSetAtom(selectedUserActivityAtom);
  const navigate = useNavigate();
  const location = useLocation();
  const navigationType = useNavigationType();

  const [dateInputKey, setDateInputKey] = React.useState(0);
  const prevPathnameRef = React.useRef<string>(sessionStorage.getItem('userUsageHistPrevPath') || '');
  const isSearchClickRef = React.useRef<boolean>(false);

  const [gridKey, setGridKey] = React.useState(0);

  const getDefaultFilter = (): UserUsageHistFilter => ({
    dateType: 'created',
    projectName: '전체',
    result: '전체',
    searchType: 'userName',
    searchValue: '',
    fromDate: (() => {
      const today = new Date();
      const oneWeekAgo = new Date(today.getTime() - 7 * 24 * 60 * 60 * 1000);
      const isoDate = oneWeekAgo.toISOString().split('T')[0];
      return isoDate.replace(/-/g, '.');
    })(),
    toDate: (() => {
      const today = new Date();
      const isoDate = today.toISOString().split('T')[0];
      return isoDate.replace(/-/g, '.');
    })(),
  });

  // 검색 조건 (입력용) - DeployAgentListPage 방식으로 변경
  const { filters: pendingFilter, updateFilters: setPendingFilter } = useBackRestoredState<UserUsageHistFilter>(
    STORAGE_KEYS.SEARCH_VALUES.USER_USAGE_HIST_LIST,
    getDefaultFilter()
  );

  // 실제 검색에 사용할 값 (조회 버튼 클릭 시 업데이트)
  const [activeFilter, setActiveFilter] = useState<typeof pendingFilter | null>(null);

  const [hasInitialLoad, setHasInitialLoad] = useState(false);

  const extractGridHeaders = () => {
    const headers = columnDefs
      .filter(col => col.headerName)
      .map(col => ({
        field: col.field || 'no',
        headerName: col.headerName,
      }));

    return headers;
  };

  const exportToExcel = async (selectedRows: any[]) => {
    try {
      if (selectedRows.length === 0) {
        showAlert('다운로드할 항목을 선택 후 다시 시도해주세요.');
        return;
      }

      const headers = extractGridHeaders();

      const response = await exportUserUsageMgmts(selectedRows, headers);

      const blob = new Blob([response.data], {
        type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
      });

      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = `사용자_이용_현황_${new Date().toISOString().split('T')[0]}.xlsx`;

      document.body.appendChild(link);
      link.click();

      document.body.removeChild(link);
      window.URL.revokeObjectURL(url);

      selectedRowsRef.current = [];
      setSelectedRowsMap({});
    } catch (error) {
      showAlert('엑셀 다운로드에 실패했습니다.');
    }
  };

  const onConditionChange = useCallback(
    (newFilter: Partial<typeof pendingFilter>) => {
      setPendingFilter(newFilter);
    },
    [setPendingFilter]
  );

  const selectedRowsRef = useRef<any[]>([]);
  const [selectedRowsMap, setSelectedRowsMap] = useAtom(userUsageSelectedRowsAtom);

  const [selectedDataList, setSelectedDataList] = useState<any[]>([]);

  const dummyData: any[] = [];

  const formatDate = (dateStr: string) => {
    if (!dateStr) return undefined;
    return dateStr.replace(/\./g, '-');
  };

  const transformSearchValue = (searchType: string, searchValue: string): string | undefined => {
    if (!searchValue || !searchValue.trim()) return undefined;

    // 조회 조건이 'apiEndpoint' (요청 경로)일 경우
    if (searchType === 'apiEndpoint') {
      const lowerValue = searchValue.toLowerCase().trim();
      
      // "portal"로 시작하는 경우 (p, po, por, port, porta, portal 등)
      if (lowerValue === 'p' || lowerValue === 'po' || lowerValue === 'por' || 
          lowerValue === 'port' || lowerValue === 'porta' || lowerValue.startsWith('portal')) {
        return 'controller';
      }
      // "adxp"로 시작하는 경우 (a, ad, adx, adxp 등)
      if (lowerValue === 'a' || lowerValue === 'ad' || lowerValue === 'adx' || 
          lowerValue.startsWith('adxp')) {
        return '/api';
      }
    }

    return searchValue.trim();
  };

  const { data, refetch, isFetching } = useGetUseUsageMgmtList(
    activeFilter
      ? {
          page: currentPage - 1,
          size: pageSize,
          dateType: activeFilter.dateType !== 'created' ? activeFilter.dateType : 'created',
          projectName: activeFilter.projectName !== '전체' ? activeFilter.projectName : undefined,
          result: activeFilter.result !== '전체' ? activeFilter.result : undefined,
          searchType: activeFilter.searchType !== '전체' ? activeFilter.searchType : undefined,
          searchValue: transformSearchValue(activeFilter.searchType, activeFilter.searchValue || ''),
          fromDate: formatDate(activeFilter.fromDate),
          toDate: formatDate(activeFilter.toDate),
        }
      : undefined,
    {
      enabled: false,
    }
  );

  const rowData = useMemo(() => {
    return (
      data?.content?.map((item: UserActivity) => ({
        id: item.id,
        userName: item.userName,
        projectName: item.projectName,
        roleName: item.roleName,
        menuPath: item.menuPath,
        menuPathDisplay: formatMenuPathLabel(item.menuPath),
        usageDetails: getUsageDetails(item.menuPath, item.action),
        action: item.action,
        targetAsset: item.targetAsset,
        resourceType: item.resourceType,
        apiEndpoint: item.apiEndpoint,
        result: item.result,
        errCode: item.errCode,
        createdAt: item.createdAt,
        userAgent: item.userAgent,
        clientIp: item.clientIp,
        requestContent: item.requestContent,
        responseContent: item.responseContent,
      })) || dummyData
    );
  }, [data]);

  // 페이지 변경 시에만 refetch (조회 클릭은 onSearchClick에서 직접 처리)
  useEffect(() => {
    if (activeFilter && hasInitialLoad && !isSearchClickRef.current) {
      refetch();
    }
    // 조회 클릭 플래그 리셋
    isSearchClickRef.current = false;
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [pageSize, currentPage]);

  const handlePageChange = (page: number) => {
    setCurrentPage(page);
    setPaginationState(prev => ({ ...prev, page }));
  };

  const handleCheck = useCallback(
    (selectedRowsData: any[]) => {
      setSelectedRowsMap(prev => {
        const updated = { ...prev };

        rowData.forEach((row: any) => {
          if (updated[row.id]) {
            delete updated[row.id];
          }
        });

        selectedRowsData.forEach(row => {
          const rowIndex = rowData.findIndex((dataRow: any) => dataRow.id === row.id);
          const sequenceNumber = (currentPage - 1) * pageSize + rowIndex + 1;

          updated[row.id] = {
            ...row,
            no: sequenceNumber,
          };
        });

        return updated;
      });
    },
    [currentPage, pageSize, rowData]
  );

  useEffect(() => {
    const currentPageSelections = rowData.filter((row: any) => selectedRowsMap[row.id]);
    setSelectedDataList(currentPageSelections);
    selectedRowsRef.current = Object.values(selectedRowsMap);
  }, [rowData, selectedRowsMap]);

  const handleExcelDownload = async () => {
    const currentSelectedRows = selectedRowsRef.current;

    if (currentSelectedRows.length === 0) {
      showAlert('다운로드할 항목을 선택 후 다시 시도해주세요.');
      return;
    }

    openConfirm({
      title: '안내',
      message: '현재 선택한 항목을 엑셀 파일로 다운로드 하시겠습니까?',
      confirmText: '예',
      cancelText: '아니요',
      onConfirm: async () => {
        await exportToExcel(currentSelectedRows);
      },
    });
  };

  const columnDefs = useMemo(
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
        cellRenderer: (params: any) => {
          const rowIndex = params.node.rowIndex;
          const sequenceNumber = (currentPage - 1) * pageSize + rowIndex + 1;
          return sequenceNumber;
        },
      },
      {
        headerName: '이름',
        field: 'userName',
        width: 170,
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
        headerName: '프로젝트명',
        field: 'projectName',
        minWidth: 200,
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
        headerName: '역할명',
        field: 'roleName',
        width: 180,
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
        headerName: '메뉴 경로',
        field: 'menuPathDisplay',
        width: 200,
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
        headerName: '요청 경로',
        field: 'apiEndpoint',
        width: 200,
        cellStyle: {
          paddingLeft: '16px',
        },
        cellRenderer: (params: any) => {
          const apiEndpoint = params.data.apiEndpoint;
          if (!apiEndpoint) return '-';

          if (apiEndpoint.startsWith('Controller')) {
            return 'Portal';
          } else if (apiEndpoint.startsWith('/api')) {
            return 'ADXP';
          }
          return '-';
        },
      },
      {
        headerName: 'API URL',
        field: 'apiEndpoint',
        width: 400,
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
        headerName: '응답 결과',
        field: 'errCode',
        width: 200,
        cellRenderer: (params: any) => {
          const errCode = params.data.errCode;
          const isSuccess = ['200', '201', '204'].includes(errCode);
          const displayValue = isSuccess ? '성공' : '실패';
          const colorIntent = isSuccess ? 'complete' : 'error';

          return (
            <UILabel variant='badge' intent={colorIntent as any}>
              {displayValue}
            </UILabel>
          );
        },
      },
      {
        headerName: '요청 일시',
        field: 'createdAt',
        width: 180,
        cellStyle: {
          paddingLeft: '16px',
        },
        cellRenderer: (params: any) => {
          if (params.value) {
            const date = new Date(params.value);
            if (!isNaN(date.getTime())) {
              const year = date.getFullYear();
              const month = String(date.getMonth() + 1).padStart(2, '0');
              const day = String(date.getDate()).padStart(2, '0');
              const hours = String(date.getHours()).padStart(2, '0');
              const minutes = String(date.getMinutes()).padStart(2, '0');
              const seconds = String(date.getSeconds()).padStart(2, '0');
              return `${year}.${month}.${day} ${hours}:${minutes}:${seconds}`;
            }
          }
          return params.value || '';
        },
      },
    ],
    [currentPage, pageSize]
  );

  const handleDropdownSelect = useCallback(
    (key: string, value: string) => {
      if (key === 'dateType') {
        onConditionChange({ dateType: value });
      } else if (key === 'projectName') {
        onConditionChange({ projectName: value });
      } else if (key === 'result') {
        onConditionChange({ result: value });
      } else if (key === 'searchType') {
        onConditionChange({ searchType: value });
      } else if (key === 'framDate') {
        onConditionChange({ fromDate: value });
      } else if (key === 'toDate') {
        onConditionChange({ toDate: value });
      }
    },
    [onConditionChange]
  );

  const handlePageSizeChange = useCallback(
    (size: string) => {
      const parsedSize = parseInt(size);
      const safeSize = Number.isNaN(parsedSize) ? paginationState.pageSize : parsedSize;
      setPageSize(safeSize);
      setCurrentPage(1);
      setPaginationState({ page: 1, pageSize: safeSize });
    },
    [paginationState.pageSize, setPaginationState]
  );

  useEffect(() => {
    if (!hasInitialLoad) {
      setActiveFilter(pendingFilter);
      setHasInitialLoad(true);
      // 초기 로드 시 refetch
      setTimeout(() => {
        refetch();
      }, 0);
    }
  }, [hasInitialLoad, pendingFilter, refetch]);

  // paginationState atom 변경 시 currentPage와 pageSize 동기화 (값이 다를 때만)
  useEffect(() => {
    if (currentPage !== paginationState.page) {
      setCurrentPage(paginationState.page);
    }
    if (pageSize !== paginationState.pageSize) {
      setPageSize(paginationState.pageSize);
    }
  }, [paginationState.page, paginationState.pageSize, currentPage, pageSize]);

  useEffect(() => {
    const currentPath = location.pathname;



    if (navigationType === 'PUSH' && currentPath === '/admin/usage-hist-mgmt') {
      setDateInputKey(prev => prev + 1);
      // useBackRestoredState가 자동으로 필터 정보를 복원하므로 activeFilter만 업데이트
      setActiveFilter(pendingFilter);
      // 페이지네이션 초기화 (atom 먼저 업데이트 후 state 업데이트)
      setPaginationState({ page: 1, pageSize: 12 });
      setCurrentPage(1);
      setPageSize(12);
    }

    // 현재 경로를 다음을 위한 이전 경로로 저장
    prevPathnameRef.current = currentPath;
  }, [location.pathname, navigationType]);

  const onSearchClick = useCallback(async () => {
    await queryClient.invalidateQueries({
      queryKey: ['user-usage-mgmt-list'],
      exact: false,
    });

    setGridKey(prev => prev + 1);

    // 조회 클릭 플래그 설정 (useEffect에서 refetch하지 않도록)
    isSearchClickRef.current = true;

    // 실제 검색에 사용할 값 업데이트
    setPaginationState({ page: 1, pageSize: pageSize });
    setCurrentPage(1);
    setSelectedRowsMap({});
    selectedRowsRef.current = [];
    
    // activeFilter 업데이트 후 직접 refetch 호출 (한 번만 호출)
    setActiveFilter(pendingFilter);
    
    // activeFilter가 업데이트된 후 refetch
    setTimeout(() => {
      refetch();
    }, 0);
  }, [pendingFilter, refetch, queryClient, setPaginationState, pageSize]);

  const handleRowClick = useCallback(
    (params: any) => {
      const rowData = params.data;
      // localStorage에 row 데이터 저장
      if (rowData) {
        localStorage.setItem((STORAGE_KEYS.SEARCH_VALUES as any).USER_USAGE_HIST_DETAIL || 'USER_USAGE_HIST_DETAIL', JSON.stringify(rowData));
      }
      setSelectedUserActivity(rowData);
      navigate('detail');
    },
    [setSelectedUserActivity, navigate]
  );

  const GridComponent = useMemo(
    () => (
      <UIGrid
        key={`grid-${gridKey}`}
        type='multi-select'
        rowData={rowData}
        loading={isFetching}
        columnDefs={columnDefs as any}
        onClickRow={handleRowClick}
        onCheck={handleCheck}
        selectedDataList={selectedDataList}
      />
    ),
    [rowData, gridKey, columnDefs, handleRowClick, handleCheck, selectedDataList, isFetching]
  );

  const projectNameOptions = useMemo(() => {
    const defaultOption = { value: '전체', label: '전체' };

    if (!projectsData || projectsData.length === 0) {
      return [defaultOption];
    }

    const projectList = projectsData.map(project => ({
      value: project.prjNm,
      label: project.prjNm,
    }));

    return [defaultOption, ...projectList];
  }, [projectsData]);

  const DateTypeDropdown = (
    <UIDropdown
      placeholder='조회 기간 선택'
      value={pendingFilter.dateType}
      options={[{ value: 'created', label: '요청 일시' }]}
      onSelect={value => handleDropdownSelect('dateType', value)}
    />
  );

  const ProjectNameDropdown = (
    <UIDropdown value={pendingFilter.projectName} placeholder='프로젝트 선택' options={projectNameOptions} onSelect={value => handleDropdownSelect('projectName', value)} />
  );

  const ResultDropdown = (
    <UIDropdown
      value={pendingFilter.result}
      placeholder='결과 선택'
      options={[
        { value: '전체', label: '전체' },
        { value: 'success', label: '성공' },
        { value: 'fail', label: '실패' },
      ]}
      onSelect={value => handleDropdownSelect('result', value)}
    />
  );

  const SearchTypeDropdown = (
    <UIDropdown
      value={pendingFilter.searchType}
      placeholder='조회 조건 선택'
      options={[
        { value: 'userName', label: '이름' },
        { value: 'roleName', label: '역할명' },
        { value: 'apiUrl', label: 'API URL' },
        { value: 'apiEndpoint', label: '요청 경로' },
      ]}
      onSelect={value => handleDropdownSelect('searchType', value)}
    />
  );

  const SearchInput = (
    <UIInput.Search
      value={pendingFilter.searchValue}
      onChange={e => {
        const value = e.target.value;
        if (value.length <= 50) {
          onConditionChange({ searchValue: value });
        }
      }}
      onKeyDown={e => {
        if (e.key === 'Enter') {
          onSearchClick();
        }
      }}
      placeholder='검색어 입력'
      maxLength={50}
    />
  );

  const DateFields = (
    <UIUnitGroup gap={4} direction='row' vAlign='center'>
      <div className='flex-1'>
        <UIInput.Date
          key={`start-date-${location.pathname}-${dateInputKey}`}
          value={pendingFilter.fromDate}
          onChange={e => {
            const value = e.target.value.replace(/-/g, '.');

            setPendingFilter(prev => {
              const currentEndDate = prev.toDate;

              if (!currentEndDate) {
                return {
                  ...prev,
                  fromDate: value,
                };
              }

              const startStr = value.replace(/\./g, '');
              const endStr = currentEndDate.replace(/\./g, '');

              if (startStr > endStr) {
                // fromDate가 toDate보다 클 경우, 두 날짜를 모두 입력값으로 설정
                return {
                  ...prev,
                  fromDate: value,
                  toDate: value,
                };
              }

              return {
                ...prev,
                fromDate: value,
              };
            });
          }}
          placeholder='시작 날짜'
        />
      </div>

      <UITypography variant='body-1' className='secondary-neutral-p'>
        ~
      </UITypography>
      <div className='flex-1'>
        <UIInput.Date
          key={`end-date-${location.pathname}-${dateInputKey}`}
          value={pendingFilter.toDate}
          onChange={e => {
            const value = e.target.value.replace(/-/g, '.');

            setPendingFilter(prev => {
              const currentStartDate = prev.fromDate;

              if (!currentStartDate) {
                return {
                  ...prev,
                  toDate: value,
                };
              }

              const startStr = currentStartDate.replace(/\./g, '');
              const endStr = value.replace(/\./g, '');

              if (endStr < startStr) {
                // endDate가 fromDate보다 작을 경우, 두 날짜를 모두 입력값으로 설정
                return {
                  ...prev,
                  fromDate: value,
                  toDate: value,
                };
              }

              return {
                ...prev,
                toDate: value,
              };
            });
          }}
          placeholder='종료 날짜'
        />
      </div>
    </UIUnitGroup>
  );

  const FilterSection = (
    <UIArticle className='article-filter'>
      <UIBox className='box-filter'>
        <UIGroup gap={40} direction='row'>
          <div style={{ width: 'calc(100% - 168px)' }}>
            <table className='tbl_type_b'>
              <tbody>
                <tr>
                  <th>
                    <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                      조회 기간
                    </UITypography>
                  </th>
                  <td colSpan={3}>
                    <UIUnitGroup gap={32} direction='row'>
                      <div className='flex-1' style={{ maxWidth: '540px' }}>
                        {DateTypeDropdown}
                      </div>
                      <div className='flex-1' style={{ zIndex: 10 }}>
                        {DateFields}
                      </div>
                    </UIUnitGroup>
                  </td>
                </tr>
                <tr>
                  <th>
                    <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                      조회 조건
                    </UITypography>
                  </th>
                  <td colSpan={3}>
                    <UIUnitGroup gap={32} direction='row'>
                      <div className='flex-1' style={{ maxWidth: '540px' }}>
                        {SearchTypeDropdown}
                      </div>
                      <div className='flex-1'>{SearchInput}</div>
                    </UIUnitGroup>
                  </td>
                </tr>
                <tr>
                  <th>
                    <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                      프로젝트명
                    </UITypography>
                  </th>
                  <td colSpan={3}>
                    <UIUnitGroup gap={32} direction='row' className='items-center'>
                      <div className='flex-1' style={{ maxWidth: '540px' }}>
                        {ProjectNameDropdown}
                      </div>
                      <div className='flex flex-1 items-center gap-4'>
                        <UITypography variant='body-1' className='!w-[80px] secondary-neutral-800 text-body-1-sb'>
                          응답 결과
                        </UITypography>
                        <div className='flex-1'>{ResultDropdown}</div>
                      </div>
                    </UIUnitGroup>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
          <div style={{ width: '128px' }}>
            <UIButton2 className='btn-secondary-blue' style={{ width: '100%' }} onClick={onSearchClick}>
              조회
            </UIButton2>
          </div>
        </UIGroup>
      </UIBox>
    </UIArticle>
  );


  return (
    <>
      {FilterSection}
      {/* {GridSection} */}
          <UIArticle className='article-grid'>
      <UIListContainer>
        <UIListContentBox.Header>
          <div className='w-full'>
            <UIUnitGroup gap={16} direction='column'>
              <div className='flex justify-between w-full items-center'>
                <div className='flex-shrink-0'>
                  <UIDataCnt count={data?.totalElements || 0} prefix='총' unit='건' />
                </div>
                <div className='flex items-center' style={{ gap: '12px' }}>
                  <Button 
                  auth={AUTH_KEY.ADMIN.USAGE_STATISTICS_DOWNLOAD}
                  className='btn-tertiary-outline' onClick={handleExcelDownload} disabled={(data?.totalElements || 0) === 0}>
                    엑셀 다운로드
                  </Button>
                  <div style={{ width: '160px', flexShrink: 0 }}>
                    <UIDropdown
                      value={String(pageSize)}
                      options={[
                        { value: '12', label: '12개씩 보기' },
                        { value: '36', label: '36개씩 보기' },
                        { value: '60', label: '60개씩 보기' },
                      ]}
                      onSelect={handlePageSizeChange}
                      height={40}
                      variant='dataGroup'
                      disabled={(data?.totalElements || 0) === 0}
                    />
                  </div>
                </div>
              </div>
            </UIUnitGroup>
          </div>
        </UIListContentBox.Header>
        <UIListContentBox.Body>{GridComponent}</UIListContentBox.Body>
        <UIListContentBox.Footer>
          <UIPagination currentPage={currentPage} totalPages={data?.totalPages || 1} onPageChange={handlePageChange} className='flex justify-center' />
        </UIListContentBox.Footer>
      </UIListContainer>
    </UIArticle>
    </>
  );
};

export const UserUsageMgmtHistPage = React.memo(UserUsageMgmtHistPageComponent);
