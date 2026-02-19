import React, { type FC, useEffect, useState, useMemo } from 'react';

import { UIDataCnt, UIPagination, UITextLabel } from '@/components/UI';
import { UIInput } from '@/components/UI/molecules';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { useGetExternalRepos } from '@/services/knowledge/knowledge.services';
// 검색 조건
interface SearchValues {
  page: number;
  size: number;
  searchKeyword: string;
  is_active: string;
}
interface Knowledge {
  id: string;
  knw_id: string;
  name: string;
  description: string;
  embedding_model_serving_name: string;
  repo_kind?: string;
}

interface SelectKnowledgePopProps {
  selectedRepoId: string;
  onKnowledgeSelect?: (selectedKnowledge: Knowledge) => void;
}

export const SelectKnowledgePop: FC<SelectKnowledgePopProps> = ({ selectedRepoId, onKnowledgeSelect }) => {
  // 체크된 항목 저장 (그리드용)
  const [selectedRows, setSelectedRows] = useState<any[]>([]);

  // 검색 조건
  const [searchValues, setSearchValues] = useState<SearchValues>({
    page: 1,
    size: 6,
    searchKeyword: '',
    is_active: 'true',
  });

  // External Knowledge 목록 조회 - Backend API 연동
  const {
    data: externalReposData,
    refetch,
    isLoading,
  } = useGetExternalRepos(
    {
      page: searchValues.page,
      size: searchValues.size,
      search: searchValues.searchKeyword,
      sort: 'updated_at,desc',
      filter: `is_active:${searchValues.is_active}`, // 상태값 조건
    },
    {
      placeholderData: previousData => previousData, // 조회 중에도 기존 데이터 유지
      enabled: false,
    }
  );

  // searchValues 변경 시 refetch
  useEffect(() => {
    refetch();
  }, [searchValues.page]);

  // pagenation 개수
  const totalPages = externalReposData?.payload?.pagination?.last_page || 1;
  const totalCount = externalReposData?.payload?.pagination?.total || 0;

  // API 응답 데이터를 UI에 맞게 변환 - NO 컬럼 순차 번호 추가
  const rowData = useMemo(() => {
    if (!externalReposData?.data) {
      return [];
    }
    return externalReposData.data.map((item: any, index: number) => {
      return {
        // 그리드 표시용 필드
        no: (searchValues.page - 1) * searchValues.size + index + 1,
        knw_id: item.knw_id, // 기본지식 : knw_id
        id: item.id, // 사용자정의 지식 : id (id : ADXP Repository ID)
        name: item.name,
        description: item.description || '',
        embedding_model_serving_name: item.embedding_model_name || '',
        repo_kind: 'repo_ext', // external knowledge
        indexName: item.index_name,
        script: item.script,
      };
    });
  }, [externalReposData]);

  // rowData가 생성된 후 selectedRepoId와 비교하여 selectedRows 자동 설정
  useEffect(() => {
    if (!rowData || rowData.length === 0 || !selectedRepoId) {
      return;
    }

    const matchedRow = rowData.find(row => {
      return row.id === selectedRepoId;
    });

    if (matchedRow) {
      setSelectedRows([matchedRow]);
    }
  }, [rowData, selectedRepoId]);

  // 페이지네이션 핸들러
  const handlePageChange = (page: number) => {
    setSearchValues(prev => ({ ...prev, page }));
  };

  // 검색어 엔터 시 조회 핸들러
  const handleSearch = () => {
    setSearchValues(prev => ({ ...prev, page: 1 }));
    refetch();
  };

  // 그리드 컬럼 정의 (SelectMCPPop과 동일한 구조)
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
        width: 272,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '설명',
        field: 'description' as const,
        minWidth: 392,
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
        headerName: '임베딩 모델',
        field: 'embedding_model_serving_name' as const,
        width: 230,
        cellStyle: { paddingLeft: '16px' },
        cellRenderer: (params: any) => {
          return <UITextLabel intent='blue'>{params.value || ''}</UITextLabel>;
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
                  <UIDataCnt count={totalCount} prefix='총' unit='건' />
                </div>
              </div>
              <div>
                <div className='w-[360px]'>
                  <UIInput.Search
                    value={searchValues.searchKeyword}
                    placeholder='이름, 설명 입력'
                    onChange={e => setSearchValues(prev => ({ ...prev, searchKeyword: e.target.value }))}
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
              rowData={rowData}
              columnDefs={columnDefs}
              selectedDataList={selectedRows}
              checkKeyName={'id'}
              onCheck={(checkedRows: any[]) => {
                setSelectedRows(checkedRows);
                onKnowledgeSelect?.(checkedRows[0]);
              }}
            />
          </UIListContentBox.Body>
          <UIListContentBox.Footer>
            <UIPagination currentPage={searchValues.page} hasNext={externalReposData?.hasNext} totalPages={totalPages} onPageChange={handlePageChange} className='flex justify-center' />
          </UIListContentBox.Footer>
        </UIListContainer>
      </UIArticle>
    </section>
  );
};
