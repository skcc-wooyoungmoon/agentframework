import React, { useCallback, useEffect, useMemo, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { useAtom } from 'jotai';
import { noticeServerDataAtom, setSelectedNoticeDetailAtom, useNoticeMgmt } from '@/stores/admin/noticeMgmt/noticeMgmt.atoms';
import { useBackRestoredState } from '@/hooks/common/navigation';
import { STORAGE_KEYS } from '@/constants/common/storage.constants';

import { UIBox, UIButton2, UIDataCnt, UITypography } from '@/components/UI/atoms';
import { UILabel } from '@/components/UI/atoms/UILabel';
import { UIPagination } from '@/components/UI/atoms/UIPagination';
import { UIDropdown } from '@/components/UI/molecules/dropdown/UIDropdown';
import { UIInput } from '@/components/UI/molecules/input';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UIArticle, UIGroup, UIPageBody, UIPageHeader, UIUnitGroup } from '@/components/UI/molecules';
import { NoticeMgmtCreatePage } from './NoticeMgmtCreatePage';
import { NoticeMgmtUpdatePage } from './NoticeMgmtUpdatePage';
import type { createRequest } from './type';
import { useDeleteNotice, useGetNotices, usePostNotice, usePutNotice } from '@/services/admin/noticeMgmt/noticeMgmt.services';
import { useModal } from '@/stores/common/modal';

import { Button } from '@/components/common/auth';
import { AUTH_KEY } from '@/constants/auth/auth.constants';

interface SearchValues {
  dateType: string;
  dateRange: { startDate?: string; endDate?: string };
  searchType: string;
  searchKeyword: string;
  status: string;
  modelType: string;
}

export const NoticeMgmtListPage = () => {
  const navigate = useNavigate();
  const location = useLocation();

  // 초기 날짜 범위 계산 (한 달 전 ~ 오늘)
  const getDefaultDateRange = () => {
    const today = new Date();
    const oneMonthAgo = new Date();
    oneMonthAgo.setMonth(today.getMonth() - 1);

    const formatDate = (date: Date) => {
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      return `${year}.${month}.${day}`;
    };

    return {
      startDate: formatDate(oneMonthAgo),
      endDate: formatDate(today),
    };
  };

  const { filters: searchValues, updateFilters: setSearchValues } = useBackRestoredState<SearchValues>(STORAGE_KEYS.SEARCH_VALUES.NOTICE_MGMT_LIST, {
    dateType: '수정일시',
    dateRange: getDefaultDateRange(),
    searchType: '제목',
    searchKeyword: '',
    status: '전체',
    modelType: '전체',
  });

  const [, setServerData] = useAtom(noticeServerDataAtom);
  const [, setSelectedNoticeDetail] = useAtom(setSelectedNoticeDetailAtom);

  // 실제 검색에 사용할 값 (조회 버튼 클릭 시 업데이트)
  const [appliedSearchValues, setAppliedSearchValues] = useState<SearchValues>({
    dateType: searchValues.dateType,
    dateRange: searchValues.dateRange,
    searchType: searchValues.searchType,
    searchKeyword: searchValues.searchKeyword,
    status: searchValues.status,
    modelType: searchValues.modelType,
  });

  const [dropdownStates, setDropdownStates] = useState({
    dateType: false,
    searchType: false,
    status: false,
    modelType: false,
  });
  const { mutate: createNotice } = usePostNotice();
  const { mutate: deleteNotice } = useDeleteNotice();
  const { mutate: updateNotice } = usePutNotice();

  const [open, setOpen] = useState(false);
  const [showUpdateModal, setShowUpdateModal] = useState(false);

  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize, setPageSize] = useState(12);
  const [totalPages, setTotalPages] = useState(1);
  const [totalElements, setTotalElements] = useState(0);

  const [selectedRowData, setSelectedRowData] = useState<any>(null);

  const { openAlert, openConfirm } = useModal();
  const showAlert = (message: string, title: string = '안내') => {
    openAlert({
      message: message,
      title: title,
      confirmText: '확인',
      onConfirm: () => {},
    });
  };

  const handleSubmit = (payload: createRequest | any) => {
    if (payload && payload.data) {
      const notiId = payload.data?.notiId;
      openAlert({
        message: '새 공지 등록을 완료했습니다.',
        title: '안내',
        confirmText: '확인',

        onConfirm: () => {
          setOpen(false);
          if (notiId) {
            navigate(`${notiId}`);
          } else {
            refetch();
          }
        },
      });
      return;
    }

    createNotice(payload, {
      onSuccess: (response: any) => {
        const notiId = response.data?.notiId;
        openAlert({
          message: '새 공지 등록을 완료했습니다.',
          title: '안내',
          confirmText: '확인',
          onConfirm: () => {
            setOpen(false);
            if (notiId) {
              navigate(`${notiId}`);
            } else {
              refetch();
            }
          },
        });
      },
      onError: () => {
        showAlert('공지사항 등록에 실패했습니다.', '실패');
      },
    });
  };

  const handleUpdateSubmit = (payload: any) => {
    payload.id = payload.notiId;

    openConfirm({
      message: '공지사항을 수정하시겠어요?',
      title: '안내',
      confirmText: '예',
      cancelText: '아니요',
      onConfirm: () => {
        updateNotice(payload, {
          onSuccess: () => {
            openAlert({
              message: '공지사항이 수정되었습니다.',
              title: '안내',
              confirmText: '확인',
              onConfirm: () => {
                setShowUpdateModal(false);
                setOpen(false);
              },
            });
          },
          onError: () => {
            showAlert('수정에 실패했습니다.', '실패');
          },
        });
      },
      onCancel: () => {
        // Cancel handling
      },
    });
  };

  const handleNoticeClick = () => {
    setOpen(true);
  };

  const handleRowClick = (data: any) => {
    const itemId = data.notiId || data.id;

    if (!itemId) {
      return;
    }

    // 로컬스토리지에 apiNotiId 저장
    localStorage.setItem(STORAGE_KEYS.SEARCH_VALUES.NOTICE_MGMT_DETAIL_NOTI_ID, String(itemId));

    // GuardRailPromptPage와 동일하게 params.data를 직접 사용
    setSelectedNoticeDetail(data);
    navigate(`/admin/notice-mgmt/${itemId}`);
  };

  // GuardRailPromptPage와 동일하게 rowData를 API 응답에서 직접 생성

  const [selectedItems, setSelectedItems] = useState<any[]>([]);

  const apiParams = useMemo(() => {
    const params: any = {
      page: currentPage - 1,
      size: pageSize,
    };

    if (appliedSearchValues.searchKeyword && appliedSearchValues.searchKeyword.trim()) {
      params.searchKeyword = appliedSearchValues.searchKeyword.trim();
    }

    if (appliedSearchValues.searchType) {
      params.searchType = appliedSearchValues.searchType;
    }

    if (appliedSearchValues.dateType) {
      params.dateType = appliedSearchValues.dateType;
    }

    if (appliedSearchValues.dateRange.startDate) {
      params.startDate = appliedSearchValues.dateRange.startDate.replace(/\./g, '-');
    }

    if (appliedSearchValues.dateRange.endDate) {
      params.endDate = appliedSearchValues.dateRange.endDate.replace(/\./g, '-');
    }

    if (appliedSearchValues.status) {
      params.status = appliedSearchValues.status;
    }

    if (appliedSearchValues.modelType) {
      params.type = appliedSearchValues.modelType;
    }

    return params;
  }, [currentPage, pageSize, appliedSearchValues]);

  const {
    data: noticeData,
    error: apiError,
    refetch,
  } = useGetNotices(apiParams, {
    enabled: true,
    staleTime: 0,
    refetchOnMount: 'always',
  });

  // GuardRailPromptPage와 동일하게 API 응답 데이터를 직접 사용
  useEffect(() => {
    if (noticeData) {
      const data = noticeData as any;
      setTotalPages(data.totalPages || 1);
      setTotalElements(data.totalElements || 0);
    }
  }, [noticeData]);

  useEffect(() => {
    if (apiError) {
      setServerData([]);
    }
  }, [apiError]);

  const handleSearch = () => {
    setAppliedSearchValues(searchValues);
    setCurrentPage(1);

    setTimeout(() => {
      refetch();
    }, 0);
  };

  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter') {
      e.preventDefault();
      handleSearch();
    }
  };

  const calculateNoticeStatus = useCallback((item: any) => {
    const now = new Date();
    const expFrom = item.expFrom ? new Date(item.expFrom) : null;
    const expTo = item.expTo ? new Date(item.expTo) : null;
    const useYn = item.useYn;

    if (useYn === 'N') {
      return '임시저장';
    }

    if (useYn === 'Y') {
      if (!expFrom || !expTo) {
        return '게시';
      }

      const isWithinPeriod = now >= expFrom && now <= expTo;

      if (isWithinPeriod) {
        return '게시';
      } else {
        return '만료';
      }
    }

    return '임시저장';
  }, []);

  const handleDelete = useCallback(
    async (ids: string[]) => {
      const data = noticeData as any;
      let content: any[] = [];
      if (data?.content && Array.isArray(data.content)) {
        content = data.content;
      } else if (Array.isArray(data)) {
        content = data;
      }

      // GuardRailPromptPage와 동일하게 selectedItems에서 ID 추출
      const actualSelectedIds = ids.length > 0 ? ids : selectedItems.map((item: any) => item?.notiId || item?.id);

      if (actualSelectedIds.length > 0) {
        let itemsForValidation: any[];
        if (ids.length > 0) {
          itemsForValidation = content.filter((item: any) => ids.includes(item?.notiId || item?.id));
        } else {
          itemsForValidation = selectedItems;
        }

        const publishedItems = itemsForValidation.filter((item: any) => {
          const status = calculateNoticeStatus(item);
          return status === '게시';
        });

        if (publishedItems.length > 0) {
          showAlert('게시 중인 항목은 삭제할 수 없습니다.\n\n상태 변경 후 삭제해주세요.');
          return;
        }

        openConfirm({
          message: '삭제하시겠어요?\n삭제한 정보는 복구할 수 없습니다.',
          title: '안내',
          confirmText: '예',
          cancelText: '아니요',
          onConfirm: () => {
            // GuardRailPromptPage와 동일하게 각 항목에 대해 삭제 API 호출

            actualSelectedIds.forEach(id => {
              deleteNotice(
                { notiId: id },
                {
                  onSuccess: () => {
                    openAlert({
                      title: '완료',
                      message: '공지사항이 삭제되었습니다.',
                      onConfirm: () => {
                        setSelectedItems([]);
                        refetch();
                      },
                    });
                  },
                  onError: () => {},
                }
              );
            });
          },
          onCancel: () => {
            // 취소 시 아무것도 하지 않음 (선택 상태는 그대로 유지됨)
          },
        });
      } else {
        showAlert('삭제할 항목을 선택해주세요.', '안내');
      }
    },
    [openConfirm, deleteNotice, refetch, showAlert, selectedItems, calculateNoticeStatus, noticeData]
  );

  const handlePageChange = (page: number) => {
    setCurrentPage(page);
  };

  const handlePageSizeChange = (newPageSize: number) => {
    setPageSize(newPageSize);
    setCurrentPage(1);
  };

  // GuardRailPromptPage와 동일하게 rowData를 API 응답에서 직접 생성
  const rowData = useMemo(() => {
    if (!noticeData) return [];

    const data = noticeData as any;
    let content: any[] = [];

    if (data.content && Array.isArray(data.content)) {
      content = data.content;
    } else if (Array.isArray(data)) {
      content = data;
    }

    return content.map((item, index) => ({
      ...item,
      no: (currentPage - 1) * pageSize + index + 1,
    }));
  }, [noticeData, currentPage, pageSize]);

  // useEffect(() => {
  //   if (location.pathname === '/admin/notice-mgmt') {
  //     selectedIdsRef.current = [];
  //     setGridKey(prev => prev + 1);
  //   }
  // }, [location.pathname]);

  const handleGridCheck = useCallback((selectedData: any[]) => {
    // GuardRailPromptPage와 동일하게 단순히 state만 업데이트
    setSelectedItems(selectedData);
  }, []);

  // searchValues 변경 시 appliedSearchValues 초기화 (첫 마운트 시)
  useEffect(() => {
    setAppliedSearchValues(searchValues);
  }, []);

  const prevPathnameRef = React.useRef<string>('');

  const [dateInputKey, setDateInputKey] = React.useState(0);

  React.useEffect(() => {
    const currentPath = location.pathname;
    const prevPath = prevPathnameRef.current;

    if (prevPath.includes('/admin/notice-mgmt/') && currentPath === '/admin/notice-mgmt') {
      refetch();
    }

    if (prevPath && prevPath !== currentPath && currentPath === '/admin/notice-mgmt') {
      setDateInputKey(prev => prev + 1);
      // GuardRailPromptPage와 동일하게 페이지 복귀 시 별도 처리 없음
    }

    prevPathnameRef.current = currentPath;
  }, [location.pathname, refetch]);

  const handleDropdownToggle = (key: keyof typeof dropdownStates) => {
    setDropdownStates(prev => ({
      ...Object.keys(prev).reduce((acc, k) => ({ ...acc, [k]: false }), {} as typeof prev),
      [key]: !prev[key],
    }));
  };

  const handleDropdownSelect = (key: keyof SearchValues, value: string) => {
    setSearchValues(prev => {
      const newValues = { ...prev, [key]: value };

      return newValues;
    });
    setDropdownStates(prev => ({ ...prev, [key]: false }));
  };

  const dateTypeOptions = [{ value: '수정일시', label: '최종 수정일시' }];

  const searchTypeOptions = [{ value: '제목', label: '제목' }];

  const statusOptions = [
    { value: '전체', label: '전체' },
    { value: '게시', label: '게시' },
    { value: '만료', label: '만료' },
    { value: '임시저장', label: '임시저장' },
  ];

  const { noticeTypeOptions } = useNoticeMgmt();

  const modelTypeOptions = [{ value: '전체', label: '전체' }, ...noticeTypeOptions];

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
        suppressSizeToFit: true,
        valueGetter: (params: any) => {
          return (currentPage - 1) * pageSize + (params.node?.rowIndex ?? 0) + 1;
        },
        comparator: (nodeA: any, nodeB: any) => {
          const indexA = nodeA?.rowIndex || 0;
          const indexB = nodeB?.rowIndex || 0;
          return indexA - indexB;
        },
      },
      {
        headerName: '제목',
        field: 'title',
        flex: 1,
        minWidth: 480,
        cellRenderer: React.memo((params: any) => {
          const titleValue = params.value || params.data?.title || '제목 없음';
          return <div className='text-left px-2'>{titleValue}</div>;
        }),
      },
      {
        headerName: '상태',
        field: 'status',
        width: 150,
        valueGetter: (params: any) => {
          return calculateNoticeStatus(params.data);
        },
        comparator: (valueA: string, valueB: string) => {
          const statusOrder: { [key: string]: number } = {
            게시: 1,
            임시저장: 2,
            만료: 3,
          };
          const orderA = statusOrder[valueA] || 999;
          const orderB = statusOrder[valueB] || 999;
          return orderA - orderB;
        },
        cellRenderer: React.memo((params: any) => {
          const status = calculateNoticeStatus(params.data);
          const getStatusIntent = (status: string) => {
            switch (status) {
              case '게시':
                return 'complete';
              case '임시저장':
                return 'progress';
              case '만료':
                return 'error';
              default:
                return 'gray-type1';
            }
          };
          return (
            <UILabel variant='badge' intent={getStatusIntent(status)}>
              {status}
            </UILabel>
          );
        }),
      },
      {
        headerName: '유형',
        field: 'type',
        width: 180,
      },
      {
        headerName: '게시 기간',
        field: 'expFrom',
        width: 320,
        cellRenderer: React.memo((params: any) => {
          const expFrom = params.data.expFrom;
          const expTo = params.data.expTo;
          if (expFrom && expTo) {
            const formatDateTime = (dateTimeStr: string) => {
              if (!dateTimeStr) return '';

              try {
                // ISO 형식이나 Date 객체로 파싱 가능한 경우
                const date = new Date(dateTimeStr);
                if (!isNaN(date.getTime())) {
                  const year = date.getFullYear();
                  const month = String(date.getMonth() + 1).padStart(2, '0');
                  const day = String(date.getDate()).padStart(2, '0');
                  const hours = String(date.getHours()).padStart(2, '0');
                  const minutes = String(date.getMinutes()).padStart(2, '0');
                  return `${year}.${month}.${day} ${hours}:${minutes}`;
                }
              } catch (error) {
                // 파싱 실패 시 기존 로직 사용
              }

              const parts = dateTimeStr.split(' ');
              if (parts.length >= 2) {
                const date = parts[0].replace(/-/g, '.');
                const time = parts[1].substring(0, 5);
                return `${date} ${time}`;
              }
              return dateTimeStr.replace(/-/g, '.');
            };

            const fromFormatted = formatDateTime(expFrom);
            const toFormatted = formatDateTime(expTo);

            return (
              <span>
                {fromFormatted} ~ {toFormatted}
              </span>
            );
          }
          return <span>-</span>;
        }),
      },
      {
        headerName: '최종 수정일시',
        field: 'updateAt',
        width: 200,
        cellRenderer: React.memo((params: any) => {
          const modifiedDate = params.data.updateAt;
          const createDate = params.data.createAt;

          const displayDate = modifiedDate || createDate || '-';

          const formatDateTime = (dateTimeStr: string) => {
            if (!dateTimeStr || dateTimeStr === '-') return dateTimeStr;

            try {
              const date = new Date(dateTimeStr);
              if (isNaN(date.getTime())) {
                // 이미 포맷된 문자열인 경우 처리
                if (dateTimeStr.includes('-')) {
                  const parts = dateTimeStr.split(' ');
                  if (parts.length >= 2) {
                    return `${parts[0].replace(/-/g, '.')} ${parts[1]}`;
                  }
                  return dateTimeStr.replace(/-/g, '.');
                }
                return dateTimeStr;
              }

              const year = date.getFullYear();
              const month = String(date.getMonth() + 1).padStart(2, '0');
              const day = String(date.getDate()).padStart(2, '0');
              const hours = String(date.getHours()).padStart(2, '0');
              const minutes = String(date.getMinutes()).padStart(2, '0');
              const seconds = String(date.getSeconds()).padStart(2, '0');

              return `${year}.${month}.${day} ${hours}:${minutes}:${seconds}`;
            } catch (error) {
              return dateTimeStr;
            }
          };

          return <span>{formatDateTime(displayDate)}</span>;
        }),
      },
      {
        headerName: '',
        field: 'more',
        width: 56,
        sortable: false,
        suppressHeaderMenuButton: true,
      },
    ],
    [currentPage, pageSize, calculateNoticeStatus]
  );

  const moreMenuConfig = useMemo(
    () => ({
      items: [
        {
          label: '수정',
          action: 'modify',
          auth: AUTH_KEY.ADMIN.NOTICE_UPDATE,
          onClick: (rowData: any) => {
            setShowUpdateModal(false);
            setTimeout(() => {
              setSelectedRowData(rowData);
              setShowUpdateModal(true);
            }, 0);
          },
        },
        {
          label: '삭제',
          action: 'delete',
          auth: AUTH_KEY.ADMIN.NOTICE_DELETE,
          onClick: (rowData: any) => {
            handleDelete([rowData.notiId]);
          },
        },
      ],
    }),
    [handleDelete]
  );

  return (
    <section className='section-page'>
      {open && (
        <NoticeMgmtCreatePage
          onSubmit={handleSubmit}
          open={open}
          onClose={() => {
            setOpen(false);
            refetch();
          }}
        />
      )}

      {showUpdateModal && selectedRowData && (
        <NoticeMgmtUpdatePage
          key={selectedRowData.notiId}
          onSubmit={handleUpdateSubmit}
          open={showUpdateModal}
          onClose={() => {
            setShowUpdateModal(false);
            setSelectedRowData(null);
            refetch();
          }}
          selectedRowData={selectedRowData}
        />
      )}

      <UIPageHeader
        title='공지사항 관리'
        description='포탈 내 게시할 공지사항을 등록하고 관리할 수 있습니다.'
        actions={
          <>
            <Button
              auth={AUTH_KEY.ADMIN.NOTICE_CREATE}
              className='btn-text-18-semibold-point'
              leftIcon={{ className: 'ic-system-24-add', children: '' }}
              onClick={handleNoticeClick}
            >
              새 공지 등록하기
            </Button>
          </>
        }
      />

      <UIPageBody>
        <UIArticle className='article-filter'>
          <UIBox className='box-filter'>
            <UIGroup gap={40} direction='row'>
              <div style={{ width: 'calc(100% - 168px)' }}>
                <table className='tbl_type_b'>
                  <tbody>
                    <tr>
                      <th className='!w-[80px] !min-w-[80px] !max-w-[80px]'>
                        <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                          조회 기간
                        </UITypography>
                      </th>
                      <td colSpan={3}>
                        <UIUnitGroup gap={32} direction='row'>
                          <div style={{ width: '540px' }}>
                            <UIDropdown
                              value={searchValues.dateType}
                              placeholder='조회 기간 선택'
                              options={dateTypeOptions}
                              isOpen={dropdownStates.dateType}
                              onClick={() => handleDropdownToggle('dateType')}
                              onSelect={value => handleDropdownSelect('dateType', value)}
                            />
                          </div>
                          <div className='flex-1' style={{ zIndex: '10' }}>
                            <UIUnitGroup gap={4} direction='row' vAlign='center'>
                              <div className='flex-1'>
                                <UIInput.Date
                                  key={`start-date-${location.pathname}-${dateInputKey}`}
                                  value={searchValues.dateRange.startDate || ''}
                                  onChange={e => {
                                    const value = e.target.value.replace(/-/g, '.');

                                    setSearchValues(prev => {
                                      const currentEndDate = prev.dateRange.endDate;

                                      if (!currentEndDate) {
                                        return {
                                          ...prev,
                                          dateRange: { ...prev.dateRange, startDate: value },
                                        };
                                      }

                                      const startStr = value.replace(/\./g, '');
                                      const endStr = currentEndDate.replace(/\./g, '');

                                      if (startStr > endStr) {
                                        return {
                                          ...prev,
                                          dateRange: { startDate: value, endDate: value },
                                        };
                                      }

                                      return {
                                        ...prev,
                                        dateRange: { ...prev.dateRange, startDate: value },
                                      };
                                    });
                                  }}
                                />
                              </div>

                              <UITypography variant='body-1' className='secondary-neutral-p'>
                                ~
                              </UITypography>

                              <div className='flex-1'>
                                <UIInput.Date
                                  key={`end-date-${location.pathname}-${dateInputKey}`}
                                  value={searchValues.dateRange.endDate || ''}
                                  onChange={e => {
                                    const value = e.target.value.replace(/-/g, '.');

                                    setSearchValues(prev => {
                                      const currentStartDate = prev.dateRange.startDate;

                                      if (!currentStartDate) {
                                        return {
                                          ...prev,
                                          dateRange: { ...prev.dateRange, endDate: value },
                                        };
                                      }

                                      const startStr = currentStartDate.replace(/\./g, '');
                                      const endStr = value.replace(/\./g, '');

                                      if (endStr < startStr) {
                                        return {
                                          ...prev,
                                          dateRange: { startDate: value, endDate: value },
                                        };
                                      }

                                      return {
                                        ...prev,
                                        dateRange: { ...prev.dateRange, endDate: value },
                                      };
                                    });
                                  }}
                                />
                              </div>
                            </UIUnitGroup>
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
                          <div style={{ width: '540px' }}>
                            <UIDropdown
                              value={searchValues.searchType}
                              placeholder='조회 조건 선택'
                              options={searchTypeOptions}
                              isOpen={dropdownStates.searchType}
                              onClick={() => handleDropdownToggle('searchType')}
                              onSelect={value => handleDropdownSelect('searchType', value)}
                            />
                          </div>
                          <div className='flex-1'>
                            <UIInput.Search
                              value={searchValues.searchKeyword}
                              onChange={e => {
                                const value = e.target.value;
                                setSearchValues(prev => ({
                                  ...prev,
                                  searchKeyword: value,
                                }));
                              }}
                              onKeyDown={handleKeyDown}
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
                        <UIUnitGroup gap={32} direction='row' className='items-center'>
                          <div style={{ width: '540px' }}>
                            <UIDropdown
                              value={searchValues.modelType}
                              placeholder='유형 선택'
                              options={modelTypeOptions}
                              isOpen={dropdownStates.modelType}
                              onClick={() => handleDropdownToggle('modelType')}
                              onSelect={value => handleDropdownSelect('modelType', value)}
                            />
                          </div>
                          <div className='!w-[80px] !min-w-[80px] !max-w-[80px]'>
                            <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                              상태
                            </UITypography>
                          </div>
                          <div className='flex-1'>
                            <UIDropdown
                              value={searchValues.status}
                              placeholder='상태 선택'
                              options={statusOptions}
                              isOpen={dropdownStates.status}
                              onClick={() => handleDropdownToggle('status')}
                              onSelect={value => handleDropdownSelect('status', value)}
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

        <UIArticle className='article-grid'>
          <UIListContainer>
            <UIListContentBox.Header>
              <UIUnitGroup gap={16} direction='column'>
                <div className='flex justify-between w-full items-center'>
                  <div className='flex-shrink-0'>
                    <div style={{ width: '168px', paddingRight: '8px' }}>
                      <UIDataCnt count={totalElements} prefix='총' />
                    </div>
                  </div>
                  <div className='flex-shrink-0 w-[160px]'>
                    <UIDropdown
                      value={pageSize === 12 ? '12개씩 보기' : pageSize === 36 ? '36개씩 보기' : '60개씩 보기'}
                      options={[
                        { value: '12개씩 보기', label: '12개씩 보기' },
                        { value: '36개씩 보기', label: '36개씩 보기' },
                        { value: '60개씩 보기', label: '60개씩 보기' },
                      ]}
                      onSelect={(value: string) => {
                        const size = value === '12개씩 보기' ? 12 : value === '36개씩 보기' ? 36 : 60;
                        handlePageSizeChange(size);
                      }}
                      height={40}
                      variant='dataGroup'
                      width='w-40'
                      disabled={totalElements === 0}
                    />
                  </div>
                </div>
              </UIUnitGroup>
            </UIListContentBox.Header>
            <UIListContentBox.Body>
              <UIGrid
                type='multi-select'
                rowData={rowData}
                columnDefs={columnDefs}
                moreMenuConfig={moreMenuConfig}
                onClickRow={(params: any) => {
                  const columnId = params?.colDef?.field;
                  if (columnId !== 'checkbox' && columnId !== 'more') {
                    handleRowClick(params.data);
                  }
                }}
                onCheck={handleGridCheck}
              />
            </UIListContentBox.Body>
            <UIListContentBox.Footer className='ui-data-has-btn'>
              <Button auth={AUTH_KEY.ADMIN.NOTICE_DELETE} className='btn-option-outlined' style={{ width: '40px' }} onClick={() => handleDelete([])} disabled={totalElements === 0}>
                삭제
              </Button>
              <UIPagination currentPage={currentPage} totalPages={totalPages} onPageChange={handlePageChange} className='flex justify-center' />
            </UIListContentBox.Footer>
          </UIListContainer>
        </UIArticle>
      </UIPageBody>
    </section>
  );
};
