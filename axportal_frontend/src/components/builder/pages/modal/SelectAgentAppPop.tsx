import { UIDataCnt, UIPagination, UITextLabel } from '@/components/UI';
import { UIInput } from '@/components/UI/molecules';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { useGetAgentAppList } from '@/services/deploy/agent/agentDeploy.services';
import React, { type FC, memo, useEffect, useMemo, useState } from 'react';
// 검색 조건
interface SearchValues {
  page: number;
  size: number;
  searchKeyword: string;
}

type Props = {
  selectedAgentAppId: string | null;
  readOnly?: boolean;
  onRowClick?: (id: string, name: string) => void;
  selectedRowId?: string;
  nodeId: string;
  onAgentAppSelect?: (selectedAgentApp: any) => void;
};

export const SelectAgentAppPop: FC<Props> = ({ selectedAgentAppId, readOnly = false, onAgentAppSelect }) => {
  // 체크된 항목 저장 (그리드용)
  const [selectedRows, setSelectedRows] = useState<any[]>([]);
  // 검색 조건
  const [searchValues, setSearchValues] = useState<SearchValues>({
    page: 1,
    size: 6,
    searchKeyword: '',
  });

  // 모달이 열릴 때만 API 호출 - 에러 처리 개선
  const { data, refetch, isLoading } = useGetAgentAppList(
    {
      page: searchValues.page,
      size: searchValues.size,
      targetType: 'agent_graph',
      sort: '',
      filter: 'deployment_status:Available',
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

  // 그리드용 데이터 변환 (NO 필드 추가)
  const gridData = useMemo(() => {
    if (!data?.content) return [];

    return data?.content.map((item: any, index: number) => ({
      id: item.id || '',
      no: (searchValues.page - 1) * searchValues.size + index + 1,
      name: item.name || '',
      builderName: item.builderName || '',
      description: item.description || '',
      deploymentVersion: item.deploymentVersion || '',
    }));
  }, [data?.content]);

  // rowData가 생성된 후 selectedRepoId와 비교하여 selectedRows 자동 설정
  useEffect(() => {
    if (!gridData || gridData.length === 0 || !selectedAgentAppId) {
      return;
    }

    const matchedRow = gridData.find(row => {
      return row.id === selectedAgentAppId;
    });

    if (matchedRow) {
      setSelectedRows([matchedRow]);
    }
  }, [gridData, selectedAgentAppId]);

  // 페이지네이션 핸들러
  const handlePageChange = (page: number) => {
    setSearchValues(prev => ({ ...prev, page }));
  };

  const handleSearch = () => {
    setSearchValues(prev => ({ ...prev, page: 1 }));
    refetch();
  };

  // 그리드 컬럼 정의 (이름, 설명, 버전만)
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
        headerName: '배포명',
        field: 'name' as const,
        width: 220,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '빌더명',
        field: 'builderName' as const,
        width: 220,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '설명',
        field: 'description' as const,
        minWidth: 264,
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
        field: 'deploymentVersion' as const,
        width: 100,
        cellStyle: { paddingLeft: '16px' },
        cellRenderer: memo((params: any) => {
          const version = params.value;
          if (!version || version === '' || version === null || version === undefined) {
            return <UITextLabel intent='gray'>-</UITextLabel>;
          }
          return (
            <div className='flex items-center gap-[8px]'>
              <UITextLabel intent='blue'>ver. {version}</UITextLabel>
            </div>
          );
        }),
      },
    ],
    []
  );

  return (
    <>
      <section className='section-modal'>
        <UIArticle className='article-grid'>
          <UIListContainer>
            <UIListContentBox.Header>
              <div className='flex justify-between items-center w-full'>
                <div className='flex-shrink-0'>
                  <div style={{ width: '168px', paddingRight: '8px' }}>
                    <UIDataCnt count={data?.totalElements || 0} prefix='총' />
                  </div>
                </div>
                {!readOnly && (
                  <div>
                    <div className='w-[360px]'>
                      <UIInput.Search
                        value={searchValues.searchKeyword}
                        placeholder='배포명 입력'
                        onChange={e => setSearchValues(prev => ({ ...prev, searchKeyword: e.target.value }))}
                        onKeyDown={e => {
                          if (e.key === 'Enter') {
                            handleSearch();
                          }
                        }}
                      />
                    </div>
                  </div>
                )}
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
                  onAgentAppSelect?.(checkedRows[0]);
                }}
              />
            </UIListContentBox.Body>
            <UIListContentBox.Footer>
              <UIPagination currentPage={searchValues.page || 1} hasNext={data?.hasNext} totalPages={data?.totalPages || 1} onPageChange={handlePageChange} className='flex justify-center' />
            </UIListContentBox.Footer>
          </UIListContainer>
        </UIArticle>
      </section>
    </>
  );
};
