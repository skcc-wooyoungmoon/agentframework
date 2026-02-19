import { UIDataCnt, UIPagination, UITextLabel, UITypography } from '@/components/UI';
import { UILabel } from '@/components/UI/atoms/UILabel';
import { UIInput } from '@/components/UI/molecules';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { useGetAgentMcpList } from '@/services/agent/mcp/agentMcp.services';
import type { generateMCPCatalog } from '@/services/agent/mcp/types';
import { useModal } from '@/stores/common/modal/useModal';
import React, { memo, useEffect, useState } from 'react';
// 검색 조건
interface SearchValues {
  page: number;
  size: number;
  searchKeyword: string;
}
interface SelectMCPsPopProps {
  selectedMCPs: generateMCPCatalog[];
  onSelectMCP: (mcpSelection: generateMCPCatalog[]) => void;
}

// SelectToolsPop과 동일한 구조로 MCP 선택 컴포넌트
export const SelectMCPsPop: React.FC<SelectMCPsPopProps> = ({ onSelectMCP, selectedMCPs }) => {
  // 체크된 항목 저장 (그리드용)
  const [selectedRows, setSelectedRows] = useState<any[]>(selectedMCPs);
  // 검색 조건
  const [searchValues, setSearchValues] = useState<SearchValues>({
    page: 1,
    size: 6,
    searchKeyword: '',
  });

  const { openAlert } = useModal();

  // 실제 MCP 목록 API 호출
  const {
    data: mcpList,
    refetch,
    isLoading,
  } = useGetAgentMcpList(
    {
      page: searchValues.page,
      size: searchValues.size,
      sort: 'created_at,desc',
      filter: 'enabled:true', // 이용가능 상태만 조회
      search: searchValues.searchKeyword,
    },
    {
      placeholderData: previousData => previousData, // 조회 중에도 기존 데이터 유지
      enabled: false, // 자동 호출 활성화
    }
  );

  // 검색어 변경 시 API 재호출
  useEffect(() => {
    refetch();
  }, [searchValues.page]);

  // API 데이터를 그리드용 데이터로 변환
  const projectData = React.useMemo(() => {
    if (!mcpList?.content) return [];

    return mcpList.content.map((item: any, index: number) => {
      return {
        id: item.id,
        no: (searchValues.page - 1) * searchValues.size + index + 1,
        name: item.name,
        status: item.enabled ? '이용가능' : '이용불가',
        description: item.description,
        tag: item.tags,
        tools: item.tools,
      };
    });
  }, [mcpList?.content]);

  // projectData가 생성된 후 selectedMCPs와 비교하여 selectedRows 자동 설정
  useEffect(() => {
    if (!projectData || projectData.length === 0 || !selectedMCPs || selectedMCPs.length === 0) {
      return;
    }

    const matchedRows = projectData.filter(row => selectedMCPs.includes(row.id));

    if (matchedRows.length > 0) {
      setSelectedRows(prev => {
        // prev 유지하면서 새로운 선택 추가 (중복 제거)
        const existingIds = new Set(prev.map(row => row.id));
        const newRows = matchedRows.filter(row => !existingIds.has(row.id));
        return [...prev, ...newRows];
      });
    }
  }, [projectData, selectedMCPs]);

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
        headerName: '서버명',
        field: 'name' as const,
        width: 272,
        cellStyle: { paddingLeft: '16px' },
        cellRenderer: React.memo((params: any) => {
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
        headerName: '상태',
        field: 'status' as const,
        width: 120,
        cellStyle: { paddingLeft: '16px' },
        cellRenderer: React.memo((params: any) => {
          const statusValue = params.value;
          const getStatusIntent = (status: string) => {
            switch (status) {
              case '이용불가':
                return 'error';
              case '이용가능':
                return 'complete';
              default:
                return 'complete';
            }
          };
          return (
            <UILabel variant='badge' intent={getStatusIntent(statusValue)}>
              {statusValue}
            </UILabel>
          );
        }),
      },
      {
        headerName: '설명',
        field: 'description',
        minWidth: 272,
        flex: 1,
        cellRenderer: React.memo((params: any) => {
          const descriptionValue = params.value;
          return (
            <div
              style={{
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                whiteSpace: 'nowrap',
              }}
            >
              {descriptionValue}
            </div>
          );
        }),
      },
      {
        headerName: '태그',
        field: 'tag' as const,
        width: 230,
        cellStyle: { paddingLeft: '16px' },
        cellRenderer: memo((params: any) => {
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
                {tags.slice(0, 2).map((item: string | { name: string }, index: number) => (
                  <UITextLabel key={index} intent='tag'>
                    {isStringArray ? (item as string) : (item as { name: string }).name}
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
        }),
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
                  <UIDataCnt count={mcpList?.totalElements || 0} prefix='총' unit='건' />
                </div>
              </div>
              <div>
                <div className='w-[360px]'>
                  <UIInput.Search
                    value={searchValues.searchKeyword}
                    placeholder='서버명 입력'
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
              type='multi-select'
              loading={isLoading}
              rowData={projectData}
              columnDefs={columnDefs}
              selectedDataList={selectedRows}
              checkKeyName='id'
              onCheck={(checkedRows: any[]) => {
                // console.log('🔍 checkedRows :: ', checkedRows);

                if (checkedRows.length > 10) {
                  openAlert({
                    title: '안내',
                    message: '최대 10개의 MCP 서버를 선택해주세요.',
                  });
                  return;
                }

                setSelectedRows(checkedRows);
                onSelectMCP?.(checkedRows);
              }}
            />
          </UIListContentBox.Body>
          <UIListContentBox.Footer>
            <UIPagination currentPage={searchValues.page || 1} hasNext={mcpList?.hasNext} totalPages={mcpList?.totalPages || 1} onPageChange={handlePageChange} className='flex justify-center' />
          </UIListContentBox.Footer>
        </UIListContainer>
      </UIArticle>
    </section>
  );
};
