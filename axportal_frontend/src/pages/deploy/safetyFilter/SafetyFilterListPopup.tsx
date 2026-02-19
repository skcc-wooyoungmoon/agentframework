import React, { useEffect, useMemo, useState } from 'react';

import { UIDataCnt, UIPagination } from '@/components/UI';
import { UIInput } from '@/components/UI/molecules';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { useGetSafetyFilterList } from '@/services/deploy/safetyFilter';

interface SafetyFilterListPopupProps {
  selectedList?: any[];
  onConfirm?: (selectedCategories: any[]) => void;
}

const PAGE_SIZE = 6;

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

export const SafetyFilterListPopup: React.FC<SafetyFilterListPopupProps> = ({ onConfirm, selectedList }) => {
  const [searchValue, setSearchValue] = useState('');
  const [searchQuery, setSearchQuery] = useState('');
  const [currentPage, setCurrentPage] = useState(1);
  const [item, setItem] = useState<any[]>(selectedList || []);

  // API 호출: 세이프티 필터 목록 조회
  const {
    data: filterData,
    refetch,
    isLoading,
  } = useGetSafetyFilterList({
    page: currentPage,
    size: PAGE_SIZE,
    search: searchQuery,
    sort: 'created_at,desc',
  });

  // 검색 버튼 클릭 핸들러
  const handleSearch = () => {
    setSearchQuery(searchValue);
    setCurrentPage(1);
    refetch();
  };

  // searchQuery 또는 currentPage 변경 시 자동 refetch
  useEffect(() => {
    refetch();
  }, [searchQuery, currentPage, refetch]);

  // API 데이터를 그리드 형식으로 변환 (원본 데이터 유지)
  const safetyFilterData = useMemo(() => {
    if (!filterData?.content) return [];

    return filterData.content.map((item, index) => ({
      ...item,
      stopWordsText: stringifyStopWords(item.stopWords),
      rowNumber: (currentPage - 1) * PAGE_SIZE + index + 1,
    }));
  }, [filterData, currentPage]);

  // 그리드 컬럼 정의
  const columnDefs: any = React.useMemo(
    () => [
      {
        headerName: 'NO',
        field: 'rowNumber' as const,
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
        headerName: '분류',
        field: 'filterGroupName' as const,
        width: 272,
        cellStyle: { paddingLeft: '16px' },
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
        field: 'createdAt',
        width: 180,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '최종 수정일시',
        field: 'updatedAt' as any,
        width: 180,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
    ],
    []
  );

  return (
    <section className='section-modal'>
      <UIArticle className='article-grid'>
        <div className='article-body'>
          <UIListContainer>
            <UIListContentBox.Header>
              <div className='flex justify-between items-center w-full'>
                <div className='flex-shrink-0'>
                  <div style={{ width: '168px', paddingRight: '8px' }}>
                    <UIDataCnt count={filterData?.totalElements || 0} />
                  </div>
                </div>
                <div>
                  <div className='w-[360px]'>
                    <UIInput.Search
                      value={searchValue}
                      placeholder='분류, 금지어 입력'
                      onChange={e => {
                        setSearchValue(e.target.value);
                      }}
                      onKeyDown={e => {
                        if (e.key === 'Enter') {
                          e.preventDefault();
                          handleSearch();
                        }
                      }}
                    />
                  </div>
                </div>
              </div>
            </UIListContentBox.Header>
            <UIListContentBox.Body>
              <UIGrid
                type='multi-select'
                rowData={safetyFilterData}
                columnDefs={columnDefs}
                selectedDataList={item}
                checkKeyName={'filterGroupId'}
                loading={isLoading}
                onCheck={(selectedRows: any[]) => {
                  setItem(selectedRows);
                  // 전체 객체 배열을 onConfirm으로 전달
                  if (onConfirm) {
                    onConfirm(selectedRows);
                  }
                }}
              />
            </UIListContentBox.Body>
            <UIListContentBox.Footer>
              <UIPagination
                currentPage={currentPage}
                hasNext={filterData?.hasNext}
                totalPages={filterData?.totalPages || 1}
                onPageChange={page => setCurrentPage(page)}
                className='flex justify-center'
              />
            </UIListContentBox.Footer>
          </UIListContainer>
        </div>
      </UIArticle>
    </section>
  );
};
