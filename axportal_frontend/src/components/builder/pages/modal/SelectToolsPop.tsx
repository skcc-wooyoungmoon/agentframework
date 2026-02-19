import { UIDataCnt, UIPagination } from '@/components/UI';
import { UIInput } from '@/components/UI/molecules';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { useGetAgentToolList } from '@/services/agent/tool/agentTool.services';
import React, { type FC, memo, useEffect, useMemo, useState } from 'react';
// 검색 조건
interface SearchValues {
  page: number;
  size: number;
  searchKeyword: string;
}
type Props = {
  // modalId: string;
  toolIds: string[];
  nodeId: string;
  isSingle?: boolean; // 단일 선택 모드 여부
  onToolsSelect?: (selectedTools: any) => void;
};

export const SelectToolsPop: FC<Props> = ({ toolIds, isSingle = false, onToolsSelect }) => {
  // console.log('🔍 SelectToolsPop toolIds :: ', toolIds);

  // 체크된 항목 저장 (그리드용)
  const [selectedRows, setSelectedRows] = useState<any[]>([]);
  // 검색 조건
  const [searchValues, setSearchValues] = useState<SearchValues>({
    page: 1,
    size: 6,
    searchKeyword: '',
  });

  // API 호출 - 모달이 열릴 때 자동으로 호출
  const { data, isLoading, refetch } = useGetAgentToolList(
    {
      page: searchValues.page,
      size: searchValues.size,
      sort: 'created_at,desc',
      search: searchValues.searchKeyword,
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

  // API 응답 데이터를 Tool 타입으로 변환
  const toolsData = useMemo(() => {
    if (!data?.content) return [];

    return data.content.map((item: any, index: number) => ({
      id: item.id,
      no: (searchValues.page - 1) * searchValues.size + index + 1,
      name: item.name,
      description: item.description || '',
      toolType: item.toolType || '',
      displayName: item.displayName || '',
      code: item.code || '',
      createdAt: item.createdAt || '',
      updatedAt: item.updatedAt || '',
      createdBy: item.createdBy || '',
      updatedBy: item.updatedBy || '',
      project_id: item.projectId || '',
      inputKeys: item.inputKeys,
      serverUrl: item.serverUrl,
      method: item.method,
    }));
  }, [data?.content]);

  // rowData가 생성된 후 toolIds와 비교하여 selectedRows 자동 설정
  useEffect(() => {
    if (!toolsData || toolsData.length === 0 || !toolIds || toolIds.length === 0) {
      return;
    }

    const matchedRows = toolsData.filter(row => toolIds.includes(row.id));

    if (matchedRows.length > 0) {
      if (isSingle) {
        setSelectedRows([matchedRows[0]]);
      } else {
        setSelectedRows(prev => {
          // prev 유지하면서 새로운 선택 추가 (중복 제거)
          const existingIds = new Set(prev.map(row => row.id));
          const newRows = matchedRows.filter(row => !existingIds.has(row.id));
          return [...prev, ...newRows];
        });
      }
    }
  }, [toolsData, toolIds]);

  // 페이지네이션 핸들러
  const handlePageChange = (page: number) => {
    setSearchValues(prev => ({ ...prev, page }));
  };

  const handleSearch = () => {
    setSearchValues(prev => ({ ...prev, page: 1 }));
    refetch();
  };

  // 그리드 컬럼 정의 (AG_010102_P23 디자인 적용)
  const columnDefs: any = React.useMemo(
    () => [
      {
        headerName: 'NO',
        field: 'no' as const,
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
        },
        sortable: false,
        suppressHeaderMenuButton: true,
        suppressSizeToFit: true,
      },
      {
        headerName: '이름',
        field: 'name' as const,
        width: 200,
        minWidth: 150,
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
        headerName: '설명',
        field: 'description' as const,
        flex: 1,
        minWidth: 300,
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
        headerName: '도구 유형',
        field: 'toolType' as const,
        width: 150,
        minWidth: 120,
        cellStyle: { paddingLeft: '16px' },
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
                  <UIDataCnt count={data?.totalElements || toolsData.length} prefix='총' />
                </div>
              </div>
              <div>
                <div className='w-[360px]'>
                  <UIInput.Search
                    value={searchValues.searchKeyword}
                    placeholder='이름, 설명 입력'
                    onChange={e => setSearchValues(prev => ({ ...prev, searchKeyword: e.target.value }))}
                    onKeyDown={e => e.key === 'Enter' && handleSearch()}
                  />
                </div>
              </div>
            </div>
          </UIListContentBox.Header>
          <UIListContentBox.Body>
            <UIGrid
              type={isSingle ? 'single-select' : 'multi-select'}
              loading={isLoading}
              rowData={toolsData}
              columnDefs={columnDefs}
              selectedDataList={selectedRows}
              checkKeyName='id'
              onCheck={(checkedRows: any[]) => {
                setSelectedRows(checkedRows);
                onToolsSelect?.(checkedRows);
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
