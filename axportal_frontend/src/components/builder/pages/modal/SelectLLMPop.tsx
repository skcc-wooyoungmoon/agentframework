import React, { useEffect, useMemo, useState } from 'react';

import { UIDataCnt, UIPagination } from '@/components/UI';
import { UIInput } from '@/components/UI/molecules';
import { UIArticle } from '@/components/UI/molecules/UIArticle';

import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { useGetModelDeployList } from '@/services/deploy/model/modelDeploy.services';
import type { GetModelDeployResponse } from '@/services/deploy/model/types';

interface SelectLLMPopProps {
  modalId?: string;
  selectedServingModel?: string;
  isReRanker?: boolean;
  onSelectLLM?: (llm: GetModelDeployResponse) => void;
  onConfirm?: () => void;
}

// SelectLLMPop
export const SelectLLMPop: React.FC<SelectLLMPopProps> = ({ selectedServingModel, isReRanker = false, onSelectLLM }) => {
  const [searchValue, setSearchValue] = useState('');
  const [currentPage, setCurrentPage] = useState(1);
  const [selectedRows, setSelectedRows] = useState<any[]>([]); // 체크된 항목 저장 (그리드용)
  const size = 6;

  const {
    data: modelList,
    refetch,
    isLoading,
  } = useGetModelDeployList(
    {
      page: currentPage - 1,
      size: size,
      filter: `status:Available,type:${isReRanker ? 'reranker' : 'language'}`,
      search: searchValue,
    },
    {
      placeholderData: previousData => previousData, // 조회 중에도 기존 데이터 유지
      enabled: false, // 자동 호출 활성화
    }
  );

  // 페이지 변경 시 데이터 다시 조회
  useEffect(() => {
    refetch();
  }, [currentPage]);

  const handleSearch = () => {
    setCurrentPage(1);
    refetch();
  };

  // 데이터 변환
  const projectData = useMemo(() => {
    if (!modelList?.content) return [];

    return (
      modelList?.content.map((item, index) => ({
        id: item.servingId || '',
        no: (currentPage - 1) * size + index + 1,
        name: item.name || '',
        modelName: item.modelName || '',
        description: item.modelDescription || '',
        deployType: item.servingType === 'self_hosting' ? 'Self Hosting' : item.servingType === 'serverless' ? 'Serverless' : item.servingType,
        guardrailApplied: item.safetyFilterInput || item.safetyFilterOutput ? '적용' : '미적용',
      })) ?? []
    );
  }, [modelList?.content]);

  // rowData가 생성된 후 selectedRepoId와 비교하여 selectedRows 자동 설정
  useEffect(() => {
    if (!projectData || projectData.length === 0 || !selectedServingModel) {
      return;
    }

    const matchedRow = projectData.find(row => {
      return row.id === selectedServingModel;
    });

    if (matchedRow) {
      setSelectedRows([matchedRow]);
    }
  }, [projectData, selectedServingModel]);

  // 그리드 컬럼 정의: 배포명, 모델명, 설명, 배포유형, 가드레일 적용여부
  const columnDefs: any = React.useMemo(() => {
    const baseColumns: any[] = [
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
        width: 200,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '모델명',
        field: 'modelName' as const,
        width: 200,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '설명',
        field: 'description' as const,
        flex: 1,
        cellStyle: { paddingLeft: '16px' },
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
    ];

    // isReRanker가 false일 때만 배포유형, 가드레일 적용여부 컬럼 추가
    if (!isReRanker) {
      baseColumns.push(
        {
          headerName: '배포유형',
          field: 'deployType' as const,
          width: 150,
          cellStyle: { paddingLeft: '16px' },
        },
        {
          headerName: '가드레일 적용여부',
          field: 'guardrailApplied' as const,
          width: 150,
          cellClass: 'text-center',
          headerClass: 'text-center',
          cellStyle: {
            paddingLeft: '16px',
            textAlign: 'center',
          },
        }
      );
    }

    return baseColumns;
  }, [isReRanker]);

  return (
    <section className='section-modal'>
      <UIArticle className='article-grid'>
        <UIListContainer>
          <UIListContentBox.Header>
            <div className='flex justify-between items-center w-full'>
              <div className='flex-shrink-0'>
                <div style={{ width: '168px', paddingRight: '8px' }}>
                  <UIDataCnt count={modelList?.totalElements ?? 0} prefix='총' unit='건' />
                </div>
              </div>
              <div>
                <div className='w-[360px]'>
                  <UIInput.Search
                    value={searchValue}
                    placeholder='배포명, 모델명 입력'
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
              rowData={projectData}
              columnDefs={columnDefs}
              selectedDataList={selectedRows}
              checkKeyName={'id'}
              onCheck={(checkedRows: any[]) => {
                setSelectedRows(checkedRows);
                onSelectLLM?.(checkedRows[0]);
              }}
            />
          </UIListContentBox.Body>
          <UIListContentBox.Footer>
            <UIPagination currentPage={currentPage} hasNext={modelList?.hasNext} totalPages={modelList?.totalPages ?? 1} onPageChange={(page: number) => setCurrentPage(page)} className='flex justify-center' />
          </UIListContentBox.Footer>
        </UIListContainer>
      </UIArticle>
    </section>
  );
};
