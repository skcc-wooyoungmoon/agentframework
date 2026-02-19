import React, { useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';

import { Button } from '@/components/common/auth';
import { UIBox, UIDataCnt, UITypography } from '@/components/UI/atoms';
import { UIPagination } from '@/components/UI/atoms/UIPagination';
import { UIGroup, UIUnitGroup } from '@/components/UI/molecules';
import { UIDropdown } from '@/components/UI/molecules/dropdown/UIDropdown';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIInput } from '@/components/UI/molecules/input';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { AUTH_KEY } from '@/constants/auth';
import { env } from '@/constants/common/env.constants';
import { STORAGE_KEYS } from '@/constants/common/storage.constants';
import { useBackRestoredState } from '@/hooks/common/navigation';
import { useCommonPopup } from '@/hooks/common/popup/useCommonPopup';
import { SafetyFilterCreatePopup, SafetyFilterUpdatePopup } from '@/pages/deploy/safetyFilter';
import {
  useDeleteSafetyFilter,
  useGetSafetyFilterDetail,
  useGetSafetyFilterList
} from '@/services/deploy/safetyFilter';
import { useModal } from '@/stores/common/modal';

const buildDeleteResultAlert = (requestedCount: number, deletedCount: number) => {
  const failCount = Math.max(requestedCount - deletedCount, 0);

  if (failCount === 0) {
    return {
      title: '완료',
      message: '세이프티 필터가 삭제되었습니다.',
    } as const;
  }

  return {
    title: '안내',
    message: `세이프티 필터 삭제가 완료되었습니다.\n${deletedCount}건 성공, ${failCount}건 실패\n\n실패한 항목은 확인 후 다시 시도해주세요.`,
  } as const;
};

/**
 * 금지어 객체 배열을 쉼표로 구분된 문자열로 변환
 *
 * AG Grid에서 객체 배열을 직접 렌더링하면 React 에러가 발생하므로,
 * 미리 문자열로 변환하여 안전하게 표시
 *
 * @param stopWords - 금지어 객체 배열 (예: [{ id: 'uuid', stopWord: '@GMAIL.COM' }])
 * @returns 쉼표로 구분된 금지어 문자열 (예: '@GMAIL.COM, @NAVER.COM')
 *
 * @example
 * stringifyStopWords([{ id: '1', stopWord: 'A' }, { id: '2', stopWord: 'B' }]) // 'A, B'
 * stringifyStopWords(undefined) // ''
 * stringifyStopWords([]) // ''
 */
const stringifyStopWords = (stopWords?: { id?: string; stopWord?: string }[]) => {
  if (!Array.isArray(stopWords)) return '';

  return stopWords
    .map(item => (typeof item?.stopWord === 'string' ? item.stopWord : ''))
    .filter(Boolean)
    .join(', ');
};

interface SearchValues {
  page: number;
  size: number;
  searchKeyword: string;
}

/**
 * 배포 > 세이프티 필터 > 세이프티 목록
 */
export const SafetyFilterListPage = () => {
  const navigate = useNavigate();
  // const queryClient = useQueryClient();

  // 검색 조건
  const { filters: searchValues, updateFilters: setSearchValues } = useBackRestoredState<SearchValues>(STORAGE_KEYS.SEARCH_VALUES.SAFETY_FILTER_LIST, {
    page: 1,
    size: 12,
    searchKeyword: '',
  });

  // 입력 중인 검색어 (UI 바인딩용, 조회 버튼 클릭 시에만 searchKeyword에 반영)
  const [inputKeyword, setInputKeyword] = useState(searchValues.searchKeyword);

  const [selectedIds, setSelectedIds] = useState<string[]>([]);
  const [isCreatePopupOpen, setIsCreatePopupOpen] = useState(false);
  const [isEditPopupOpen, setIsEditPopupOpen] = useState(false);
  const [selectedFilterGroupId, setSelectedFilterGroupId] = useState<string>('');

  const { openAlert } = useModal();
  const { showDeleteConfirm } = useCommonPopup();

  // 생성 팝업 열기
  const handleCreate = () => {
    setIsCreatePopupOpen(true);
  };

  // 생성 팝업 닫기
  const handlePopupClose = () => {
    setIsCreatePopupOpen(false);
  };

  // 생성 완료 후 처리
  const handleCreateSuccess = () => {
    setIsCreatePopupOpen(false);
  };

  // 수정 팝업 닫기
  const handleEditPopupClose = () => {
    setIsEditPopupOpen(false);
    setSelectedFilterGroupId('');
  };

  // 수정 완료 후 처리 (목록 갱신)
  const handleEditSuccess = async () => {
    // staleTime: 0 설정으로 인해 자동 refetch되지만, 사용자 경험을 위해 명시적으로 호출
    await refetch();
  };

  // API 호출: 세이프티 필터 목록 조회
  const {
    data: safetyFilterData,
    refetch,
    isLoading,
  } = useGetSafetyFilterList({
    page: searchValues.page,
    size: searchValues.size,
    search: searchValues.searchKeyword || undefined,
    sort: 'created_at,desc',
  }, {
    enabled: !env.VITE_NO_PRESSURE_MODE,
  });

  // 수정 대상의 상세 데이터 조회
  const { data: selectedFilterDetail } = useGetSafetyFilterDetail(selectedFilterGroupId, {
    enabled: !!selectedFilterGroupId,
    gcTime: 0, // 사용하지 않는 캐시 즉시 제거
    staleTime: 0, // 항상 최신 데이터 조회
  });

  // queryKey에 page, size, search가 포함되어 있어 React Query가 자동으로 refetch

  // API 호출: 삭제 (단일/복수 통합)
  const { mutate: deleteSafetyFilter } = useDeleteSafetyFilter({
    onSuccess: async (response, _) => {
      const requestedCount = selectedIds.length;
      const deletedCount = response.data?.deletedCount || 0;

      // 선택 초기화
      setSelectedIds([]);

      // 삭제 후 남은 데이터 계산
      const currentTotal = safetyFilterData?.totalElements || 0;
      const remainingItems = Math.max(0, currentTotal - deletedCount);

      // 남은 데이터로 최대 페이지 계산 (1-based)
      const newTotalPages = remainingItems > 0 ? Math.ceil(remainingItems / searchValues.size) : 1;
      const maxPage = newTotalPages;

      // 현재 페이지가 최대 페이지를 초과하면 이동
      if (searchValues.page > maxPage) {
        setSearchValues(prev => ({ ...prev, page: maxPage }));
      }

      const alertPayload = buildDeleteResultAlert(requestedCount, deletedCount);
      openAlert(alertPayload);

      // 목록 갱신
      refetch();
    },
  });

  // 조회 버튼 클릭: 입력된 검색어를 적용하고 1페이지로 이동
  const handleSearch = () => {
    setSearchValues(prev => ({ ...prev, page: 1, searchKeyword: inputKeyword }));

    if (env.VITE_NO_PRESSURE_MODE) {
      setTimeout(() => refetch(), 0);
    }
  };

  // 삭제 버튼 클릭 (다건 삭제)
  const handleDelete = () => {
    if (selectedIds.length === 0) {
      openAlert({
        title: '안내',
        message: '삭제할 항목을 선택해주세요.',
      });
      return;
    }

    showDeleteConfirm({
      onConfirm: () => {
        deleteSafetyFilter({ filterGroupIds: selectedIds });
      },
    });
  };

  const updatePageSizeAndRefetch = (patch: Partial<Pick<SearchValues, 'page' | 'size'>>) => {
    setSearchValues(prev => ({ ...prev, ...patch }));
    setTimeout(() => refetch(), 0);
  };

  const handlePageSizeChange = (value: string) => {
    updatePageSizeAndRefetch({ size: Number(value), page: 1 });
  };

  const handlePageChange = (page: number) => {
    updatePageSizeAndRefetch({ page });
  };

  // 실제 데이터 (API 응답) - more 필드 및 순번 추가
  const rowData = useMemo(() => {
    if (!safetyFilterData?.content) return [];

    return safetyFilterData.content.map((item, index) => ({
      ...item,
      stopWordsText: stringifyStopWords(item.stopWords),
      rowNumber: (searchValues.page - 1) * searchValues.size + index + 1, // 페이지 고려한 순번 (1부터 시작)
      more: 'more', // 더보기 컬럼을 위한 필드
    }));
  }, [safetyFilterData, searchValues.page, searchValues.size]);

  // 수정 팝업 열기
  const handleEdit = (rowData: any) => {
    setSelectedFilterGroupId(rowData.filterGroupId);
    setIsEditPopupOpen(true);
  };

  // 행 클릭 핸들러
  const handleRowClick = (params: any) => {
    // 체크박스 컬럼 또는 더보기 컬럼 클릭은 제외
    if (params.colDef.field === 'more' || params.colDef.headerCheckboxSelection) return;

    const rowData = params.data;
    // filterGroupId만 URL로 전달
    navigate(`${rowData.filterGroupId}`);
  };

  // 더보기 메뉴 설정
  const moreMenuConfig = useMemo(
    () => ({
      items: [
        {
          auth: AUTH_KEY.DEPLOY.SAFETY_FILTER_UPDATE,
          key: 'modify',
          label: '수정',
          action: 'modify',
          onClick: (rowData: any) => {
            handleEdit(rowData);
          },
        },
        {
          auth: AUTH_KEY.DEPLOY.SAFETY_FILTER_DELETE,
          key: 'delete',
          label: '삭제',
          action: 'delete',
          onClick: (rowData: any) => {
            showDeleteConfirm({
              onConfirm: () => {
                deleteSafetyFilter({ filterGroupIds: [rowData.filterGroupId] });
              },
            });
          },
        },
      ],
      isActive: () => true,
    }),
    [showDeleteConfirm, deleteSafetyFilter, handleEdit, openAlert]
  );

  // 그리드 컬럼 정의
  const columnDefs: any = useMemo(
    () => [
      {
        headerName: 'NO',
        field: 'rowNumber' as any,
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
        headerName: '분류',
        field: 'filterGroupName' as any,
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
        headerName: '금지어',
        field: 'stopWordsText',
        flex: 1,
        cellStyle: {
          paddingLeft: '16px',
        },
        cellRenderer: (params: any) => {
          const stopWord = typeof params.value === 'string' ? params.value : stringifyStopWords(params.data?.stopWords);

          return (
            <div
              style={{
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                whiteSpace: 'nowrap',
              }}
            >
              {stopWord}
            </div>
          );
        },
      },
      {
        headerName: '공개범위',
        field: 'scope' as any,
        width: 120,
        cellStyle: {
          paddingLeft: '16px',
        },
        valueFormatter: (params: any) => {
          return params.data.isPublicAsset ? '전체공유' : '내부공유';
        },
      },
      {
        headerName: '생성일시',
        field: 'createdAt' as any,
        width: 180,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: '최종 수정일시',
        field: 'updatedAt' as any,
        width: 180,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: '',
        field: 'more', // 더보기 컬럼 필드명 (고정)
        width: 56,
      },
    ],
    [rowData]
  );

  return (
    <>
      <section className='section-page'>
        {/* 페이지 헤더 */}
        <UIPageHeader
          title='세이프티 필터'
          description={['서비스 응답의 품질과 안정성을 보장하기 위해 금지어를 설정하고 관리합니다.', '세이프티 필터 생성 버튼을 클릭해 생성형 AI의 안정성을 높여 보세요.']}
          actions={
            <>
              <Button
                auth={AUTH_KEY.DEPLOY.SAFETY_FILTER_CREATE}
                className='btn-text-18-semibold-point'
                leftIcon={{ className: 'ic-system-24-add', children: '' }}
                onClick={handleCreate}
              >
                세이프티 필터 생성
              </Button>
            </>
          }
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
                        <th className='!w-[72px]'>
                          <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                            검색
                          </UITypography>
                        </th>
                        <td className='!w-[1200px]'>
                          <div>
                            <UIInput.Search
                              value={inputKeyword}
                              placeholder='분류, 금지어 입력'
                              onChange={e => setInputKeyword(e.target.value)}
                              onKeyDown={e => {
                                if (e.key === 'Enter') {
                                  e.preventDefault();
                                  handleSearch();
                                }
                              }}
                            />
                          </div>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
                <div style={{ width: '128px' }}>
                  <Button auth={AUTH_KEY.DEPLOY.SAFETY_FILTER_LIST_VIEW} className='btn-secondary-blue' style={{ width: '100%' }} onClick={handleSearch}>
                    조회
                  </Button>
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
                          <UIDataCnt count={safetyFilterData?.totalElements || 0} prefix='총' />
                        </div>
                      </div>
                      <div className='flex' style={{ gap: '12px' }}>
                        <div style={{ width: '180px', flexShrink: 0 }}>
                          <UIDropdown
                            value={String(searchValues.size)}
                            disabled={safetyFilterData?.content.length === 0}
                            options={[
                              { value: '12', label: '12개씩 보기' },
                              { value: '36', label: '36개씩 보기' },
                              { value: '60', label: '60개씩 보기' },
                            ]}
                            onSelect={(value: string) => {
                              handlePageSizeChange(value);
                            }}
                            height={40}
                            variant='dataGroup'
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
                  columnDefs={columnDefs}
                  moreMenuConfig={moreMenuConfig}
                  onClickRow={handleRowClick}
                  onCheck={(selectedRows: any[]) => {
                    // 객체 배열에서 filterGroupId만 추출
                    const ids = selectedRows.map(row => row.filterGroupId);
                    setSelectedIds(ids);
                  }}
                  loading={isLoading}
                />
              </UIListContentBox.Body>
              {/* [참고] classname 관련
                  - 그리드 하단 (삭제) 버튼이 있는 경우 classname 지정 (예시) <UIListContentBox.Footer classname="ui-data-has-btn">
                  - 그리드 하단 (버튼) 없는 경우 classname 없이 (예시) <UIListContentBox.Footer>
                */}
              <UIListContentBox.Footer className='ui-data-has-btn'>
                <Button
                  auth={AUTH_KEY.DEPLOY.SAFETY_FILTER_DELETE}
                  className='btn-option-outlined'
                  style={{ width: '40px' }}
                  onClick={handleDelete}
                  disabled={safetyFilterData?.content.length === 0}
                >
                  삭제
                </Button>
                <UIPagination currentPage={searchValues.page} hasNext={safetyFilterData?.hasNext} totalPages={safetyFilterData?.totalPages || 1} onPageChange={handlePageChange} className='flex justify-center' />
              </UIListContentBox.Footer>
            </UIListContainer>
          </UIArticle>
        </UIPageBody>
      </section>

      {/* 세이프티 필터 생성 팝업 */}
      <SafetyFilterCreatePopup isOpen={isCreatePopupOpen} onClose={handlePopupClose} onSuccess={handleCreateSuccess} />

      {/* 세이프티 필터 수정 팝업 */}
      {isEditPopupOpen && selectedFilterGroupId && (
        <SafetyFilterUpdatePopup
          isOpen={isEditPopupOpen}
          onClose={handleEditPopupClose}
          onSave={handleEditSuccess}
          filterGroupId={selectedFilterGroupId}
          safetyFilterData={selectedFilterDetail}
        />
      )}
    </>
  );
};
