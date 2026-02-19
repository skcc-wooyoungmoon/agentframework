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
  isReRanker?: boolean;
  onSelectLLM?: (llm: GetModelDeployResponse) => void;
  onConfirm?: () => void;
  readOnly?: boolean;
}

export const SelectLLMPop: React.FC<SelectLLMPopProps> = ({ isReRanker = false, onSelectLLM, readOnly = false }) => {
  const [searchValue, setSearchValue] = useState('');
  const [currentPage, setCurrentPage] = useState(1);
  const [selectedLLM, setSelectedLLM] = useState<GetModelDeployResponse | null>(null);
  const size = 6;

  const filterValue = isReRanker ? 'status:Available,type:reranker' : 'status:Available,type:language';
  const { data: modelList, isLoading, refetch } = useGetModelDeployList({
    page: currentPage - 1,
    size: size,
    filter: filterValue,
    search: searchValue,
  });

  useEffect(() => {
    refetch();
  }, [currentPage, searchValue, filterValue, refetch]);

  const projectData = useMemo(() => {
    return (
      modelList?.content.map((item, index) => ({
        id: item.servingId || `temp-${index}`,
        no: (currentPage - 1) * size + index + 1,
        deployName: item.name || '',
        modelType: item.modelName || '',
        description: item.description || '',
        type: item.servingType === 'serverless' ? 'serverless, language' : item.servingType === 'self_hosting' ? 'self_hosting, language' : item.servingType || '',
        guardrailApplied: item.guardrailApplied || '',
        originalData: item,
      })) ?? []
    );
  }, [modelList, currentPage, size]);

  const selectedGridData = selectedLLM ? projectData.filter(item => item.originalData?.servingId === selectedLLM.servingId) : [];

  const columnDefs: any = React.useMemo(() => {
    if (isReRanker) {
      return [
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
          field: 'deployName' as const,
          width: 272,
          cellStyle: { paddingLeft: '16px' },
        },
        {
          headerName: '모델명',
          field: 'modelType' as const,
          width: 272,
          cellStyle: { paddingLeft: '16px' },
        },
        {
          headerName: '설명',
          field: 'description' as const,
          minWidth: 272,
          flex: 1,
          showTooltip: true,
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
    }
    return [
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
        field: 'deployName' as const,
        width: 300,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '모델명',
        field: 'modelType' as const,
        width: 230,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '설명',
        field: 'description' as const,
        minWidth: 344,
        flex: 1,
        showTooltip: true,
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
      {
        headerName: '배포 유형',
        field: 'type' as const,
        width: 180,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '가드레일 적용 여부',
        field: 'guardrailApplied' as const,
        width: 179,
        cellStyle: { paddingLeft: '16px' },
        cellRenderer: React.memo((params: any) => {
          // 적용불가 → Y, 미적용 → N
          if (params.value === '적용 불가') {
            return 'Y';
          } else if (params.value === '미적용') {
            return 'N';
          }
          // 그 외의 경우 (적용중 등)는 원본 값 그대로 표시
          return params.value || '';
        }),
      },
    ];
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
              {!readOnly && (
                <div>
                  <div className='w-[360px]'>
                    <UIInput.Search
                      value={searchValue}
                      placeholder='배포명, 모델명, 설명 입력'
                      onChange={e => {
                        setSearchValue(e.target.value);
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
              rowData={projectData}
              columnDefs={columnDefs}
              selectedDataList={selectedGridData}
              loading={isLoading}
              onCheck={(datas: any[]) => {
                if (datas.length > 0) {
                  const selectedLLMData = datas[0]?.originalData;

                  if (selectedLLMData) {
                    setSelectedLLM(selectedLLMData);
                    if (onSelectLLM) {
                      onSelectLLM(selectedLLMData);
                    }
                  }
                } else {
                  setSelectedLLM(null);
                }
              }}
            />
          </UIListContentBox.Body>
          <UIListContentBox.Footer>
            <UIPagination currentPage={currentPage} totalPages={modelList?.totalPages ?? 0} onPageChange={(page: number) => setCurrentPage(page)} className='flex justify-center' />
          </UIListContentBox.Footer>
        </UIListContainer>
      </UIArticle>
    </section>
  );
};
