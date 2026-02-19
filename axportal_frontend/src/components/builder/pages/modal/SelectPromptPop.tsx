import { UIDataCnt, UIPagination, UITextLabel, UITypography } from '@/components/UI';
import { UIInput } from '@/components/UI/molecules';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { useGetInfPromptList } from '@/services/prompt/inference/inferencePrompts.services';
import React, { type FC, memo, useEffect, useMemo, useState } from 'react';
import { NodeType } from '../../types/Agents';
// 검색 조건
interface SearchValues {
  page: number;
  size: number;
  searchKeyword: string;
}

type Prompt = {
  // selectedRowId?: string;
  // nodeId: string;
};

type PromptModalParamProps = {
  nodeType?: string;
  selectedPromptId: string | null;
  onPromptSelect?: (selectedPrompt: Prompt) => void;
};

export const SelectPromptPop: FC<PromptModalParamProps> = ({ nodeType, selectedPromptId, onPromptSelect }) => {
  const filter = useMemo(() => {
    if (!nodeType) return undefined;

    const filterMap: Record<string, string> = {
      [NodeType.RewriterHyDE.name]: 'tags:retriever,tags:hyde',
      [NodeType.RewriterMultiQuery.name]: 'tags:retriever,tags:multi_query',
      [NodeType.RetrieverCompressor.name]: 'tags:retriever,tags:doc_compressor',
      [NodeType.RetrieverFilter.name]: 'tags:retriever,tags:doc_filter',
    };

    return filterMap[nodeType] ?? undefined;
  }, [nodeType]);

  // 체크된 항목 저장 (그리드용)
  const [selectedRows, setSelectedRows] = useState<any[]>([]);
  // 검색 조건
  const [searchValues, setSearchValues] = useState<SearchValues>({
    page: 1,
    size: 6,
    searchKeyword: '',
  });

  // API 호출 - 모달이 열릴 때 자동으로 호출
  const { data, refetch, isLoading } = useGetInfPromptList(
    {
      page: searchValues.page,
      size: searchValues.size,
      search: searchValues.searchKeyword,
      sort: 'created_at,desc',
      release_only: true, // releaseVersion이 있는 항목만 필터링
      filter: filter,
    },
    {
      placeholderData: previousData => previousData, // 조회 중에도 기존 데이터 유지
      enabled: false, // 자동 호출 활성화
    }
  );

  // searchValues 변경 시 refetch
  useEffect(() => {
    refetch();
  }, [searchValues.page]);

  // 그리드용 데이터 변환
  const gridData = React.useMemo(() => {
    if (!data?.content) return [];

    return data.content.map((item: any, index: number) => ({
      id: item.uuid,
      no: (searchValues.page - 1) * searchValues.size + index + 1,
      name: item.name,
      latestVersion: item.latestVersion,
      releaseVersion: item.releaseVersion,
      type: item.ptype === 1 ? '채팅' : '기타',
      tags: item.tags || [],
      connectedAgent: '0', // API에서 제공되지 않는 필드
      createdAt: item.createdAt,
      updatedAt: item.updatedAt,
      publicStatus: item.publicStatus,
    }));
  }, [data?.content]);

  // rowData가 생성된 후 selectedRepoId와 비교하여 selectedRows 자동 설정
  useEffect(() => {
    if (!gridData || gridData.length === 0 || !selectedPromptId) {
      return;
    }

    const matchedRow = gridData.find(row => {
      return row.id === selectedPromptId;
    });

    if (matchedRow) {
      setSelectedRows([matchedRow]);
    }
  }, [gridData, selectedPromptId]);

  // 페이지네이션 핸들러
  const handlePageChange = (page: number) => {
    setSearchValues(prev => ({ ...prev, page }));
  };

  const handleSearch = () => {
    setSearchValues(prev => ({ ...prev, page: 1 }));
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
        cellRenderer: memo((params: any) => {
          const nameValue = params.value;
          return (
            <div
              style={{
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                whiteSpace: 'nowrap',
              }}
            >
              {nameValue}
            </div>
          );
        }),
      },
      {
        headerName: '버전',
        field: 'release_version' as const,
        width: 238,
        cellStyle: { paddingLeft: '16px' },
        cellRenderer: memo((params: any) => {
          return (
            <div className='flex items-center gap-1'>
              {params.data?.releaseVersion && <UITextLabel intent='blue'>Release Ver.{params.data?.releaseVersion}</UITextLabel>}
              {params.data?.latestVersion && <UITextLabel intent='gray'>Lastest Ver.{params.data?.latestVersion}</UITextLabel>}
            </div>
          );
        }),
      },
      {
        headerName: '태그',
        field: 'tags' as const,
        width: 230,
        cellStyle: { paddingLeft: '16px' },
        cellRenderer: (params: any) => {
          const tags = params.value || [];
          if (!tags || tags.length === 0) {
            return null;
          }
          return (
            <div
              style={{
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                whiteSpace: 'nowrap',
              }}
            >
              <div className='flex gap-1 flex-wrap'>
                {tags.slice(0, 2).map((item: { tag: string }, index: number) => (
                  <UITextLabel key={index} intent='tag'>
                    {item.tag}
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
                    value={searchValues.searchKeyword}
                    placeholder='이름 입력'
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
              rowData={gridData}
              columnDefs={columnDefs}
              selectedDataList={selectedRows}
              checkKeyName={'id'}
              onCheck={(checkedRows: any[]) => {
                // console.log('🔍 checkedRows :: ', checkedRows);

                setSelectedRows(checkedRows);
                onPromptSelect?.(checkedRows[0]);
              }}
            />
          </UIListContentBox.Body>
          <UIListContentBox.Footer>
            <UIPagination currentPage={searchValues.page || 1} hasNext={data?.hasNext} totalPages={data?.totalPages || 1} onPageChange={handlePageChange} className='flex justify-center' />
          </UIListContentBox.Footer>
        </UIListContainer>
      </UIArticle>
    </section>
  );
};
