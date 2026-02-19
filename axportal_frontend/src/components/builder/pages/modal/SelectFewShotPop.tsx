import { UIDataCnt, UIPagination, UITextLabel, UITypography } from '@/components/UI';
import { UIInput } from '@/components/UI/molecules';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { useGetFewShotList } from '@/services/prompt/fewshot/fewShotPrompts.services';
import React, { type FC, useEffect, useMemo, useState } from 'react';

type Props = {
  selectedFewShotId: string | null;
  nodeId: string;
  onFewShowSelect?: (selectedFewShot: any) => void;
};

export const SelectFewShotPop: FC<Props> = ({ selectedFewShotId, onFewShowSelect }) => {
  // 체크된 항목 저장 (그리드용)
  const [selectedRows, setSelectedRows] = useState<any[]>([]);
  const [searchValue, setSearchValue] = useState('');
  const [currentPage, setCurrentPage] = useState(1);
  const size = 6;

  // API 호출 - 전체 데이터를 가져와서 필터링 후 로컬 페이지네이션
  const { data, refetch, isLoading } = useGetFewShotList(
    {
      page: currentPage,
      size,
      sort: 'created_at,desc',
      projectId: '',
      release_only: true, // releaseVersion 필터링
      filter: '',
      search: searchValue,
    },
    {
      placeholderData: previousData => previousData, // 조회 중에도 기존 데이터 유지
      enabled: false, // 자동 호출 활성화
    }
  );

  // 페이지 변경 시 refetch
  useEffect(() => {
    refetch();
  }, [currentPage]);

  // 그리드 데이터 변환
  const gridData = useMemo(() => {
    if (!data?.content) return [];

    return data.content.map((item: any, index: number) => ({
      id: item.uuid,
      no: (currentPage - 1) * size + index + 1,
      name: item.name,
      description: item.description || '',
      latestVersion: item.latestVersion,
      releaseVersion: item.releaseVersion,
      tag: item.tags || [],
      messages: item.messages || [],
      variables: item.variables || [],
    }));
  }, [data?.content]);

  // rowData가 생성된 후 selectedRepoId와 비교하여 selectedRows 자동 설정
  useEffect(() => {
    if (!gridData || gridData.length === 0 || !selectedFewShotId) {
      return;
    }

    const matchedRow = gridData.find(row => {
      return row.id === selectedFewShotId;
    });

    if (matchedRow) {
      setSelectedRows([matchedRow]);
    }
  }, [gridData, selectedFewShotId]);

  // 페이지네이션 핸들러
  const handlePageChange = (page: number) => {
    setCurrentPage(page);
  };

  const handleSearch = () => {
    setCurrentPage(1);
    refetch();
  };

  // 그리드 컬럼 정의
  const columnDefs: any = React.useMemo(
    () => [
      {
        headerName: 'NO',
        field: 'no' as const,
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
        headerName: '이름',
        field: 'name' as const,
        minWidth: 442,
        flex: 1,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '버전',
        field: 'version' as const,
        width: 238,
        cellStyle: { paddingLeft: '16px' },
        cellRenderer: React.memo((params: any) => {
          return (
            <div className='flex items-center gap-[8px]'>
              {params.data?.releaseVersion && <UITextLabel intent='blue'>Release Ver.{params.data?.releaseVersion}</UITextLabel>}
              {params.data?.latestVersion && <UITextLabel intent='gray'>Lastest Ver.{params.data?.latestVersion}</UITextLabel>}
            </div>
          );
        }),
      },
      {
        headerName: '태그',
        field: 'tag' as const,
        width: 230,
        cellStyle: { paddingLeft: '16px' },
        cellRenderer: (params: any) => {
          const tags = params.value || [];
          if (!tags || tags.length === 0) {
            return null;
          }
          // tags가 문자열 배열인지 객체 배열인지 확인
          const isStringArray = typeof tags[0] === 'string';
          return (
            <div
              style={{
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                whiteSpace: 'nowrap',
              }}
            >
              <div className='flex gap-1 flex-wrap'>
                {tags.slice(0, 2).map((item: string | { tag: string }, index: number) => (
                  <UITextLabel key={index} intent='tag'>
                    {isStringArray ? (item as string) : (item as { tag: string }).tag}
                  </UITextLabel>
                ))}
                {/* 2개 이상일 경우 ... 처리 */}
                {tags.length > 2 && (
                  <UITypography variant='caption-2' className='secondary-neutral-550'>
                    {'...'}
                  </UITypography>
                )}
              </div>
            </div>
          );
        },
      },
    ],
    []
  );

  return (
    <section className='section-modal'>
      <UIArticle className='article-grid'>
        <UIListContainer>
          <UIListContentBox.Header>
            <div className='flex justify-between items-center w-full'>
              <div className='flex-shrink-0'>
                <div style={{ width: '168px', paddingRight: '8px' }}>
                  <UIDataCnt count={data?.totalElements || 0} prefix='총' unit='건' />
                </div>
              </div>
              <div>
                <div className='w-[360px]'>
                  <UIInput.Search
                    value={searchValue}
                    placeholder='이름 입력'
                    onChange={e => setSearchValue(e.target.value)}
                    onKeyDown={e => {
                      if (e.key === 'Enter') {
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
              type='single-select'
              loading={isLoading}
              rowData={gridData}
              columnDefs={columnDefs}
              selectedDataList={selectedRows}
              checkKeyName='id'
              onCheck={(checkedRows: any[]) => {
                // console.log('🔍 checkedRows :: ', checkedRows);

                setSelectedRows(checkedRows);
                onFewShowSelect?.(checkedRows[0]);
              }}
            />
          </UIListContentBox.Body>
          <UIListContentBox.Footer>
            <UIPagination currentPage={currentPage} hasNext={data?.hasNext} totalPages={data?.totalPages || 1} onPageChange={handlePageChange} className='flex justify-center' />
          </UIListContentBox.Footer>
        </UIListContainer>
      </UIArticle>
    </section>
  );
};
