import React, { useEffect, useState } from 'react';

import { UIDataCnt, UILabel, type UILabelIntent, UIPagination } from '@/components/UI';
import { UIInput } from '@/components/UI/molecules';
import { UIArticle } from '@/components/UI/molecules/UIArticle';

import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { useGetModelDeployList } from '@/services/deploy/model/modelDeploy.services';
import dateUtils from '@/utils/common/date.utils';

import { MODEL_DEPLOY_STATUS } from '@/constants/deploy/modelDeploy.constants.ts';
import type { GetModelDeployResponse } from '@/services/deploy/model/types';

// 그리드용 확장 타입
type GridModelDeployResponse = GetModelDeployResponse & {
  no: number;
};

interface ModelSelectPopupProps {
  onModelSelect?: (selectedModels: GetModelDeployResponse[]) => void;
}

export const ModelSelectPopup: React.FC<ModelSelectPopupProps> = ({ onModelSelect }) => {
  const [searchText, setSearchText] = useState<string>('');
  const [selectedItems, setSelectedItems] = useState<string[]>([]);

  const [searchValues, setSearchValues] = useState({
    page: 0,
    size: 6,
    search: '',
    filter: 'status:Available,type:language',
    queryKey: 'playground',
  });

  const { data, refetch, isFetching } = useGetModelDeployList(searchValues);

  useEffect(() => {
    refetch();
  }, [searchValues]);

  // 체크박스 선택 이벤트 핸들러
  const handleCheckboxChange = (selectedIds: string[]) => {
    setSelectedItems(selectedIds);

    // 선택된 모델 객체를 즉시 전달
    if (onModelSelect) {
      const selectedModels = projectData.filter(item => selectedIds.includes(item.servingId));
      onModelSelect(selectedModels);
    }
  };

  // API 데이터를 GetModelDeployResponse 타입으로 변환
  const projectData: GetModelDeployResponse[] = React.useMemo(() => {
    if (!data?.content) return [];
    return data.content;
  }, [data?.content]);

  // 그리드용 데이터 (GridModelDeployResponse[] 타입)
  const gridData: GridModelDeployResponse[] = React.useMemo(() => {
    return projectData.map((item, index) => ({
      ...item,
      no: searchValues.page * searchValues.size + index + 1,
    }));
  }, [projectData, searchValues.page, searchValues.size]);

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
        headerName: '배포명',
        field: 'name' as const,
        width: 272,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '모델명',
        field: 'modelName' as const,
        width: 272,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '상태',
        field: 'status' as const,
        width: 120,
        cellStyle: { paddingLeft: '16px' },
        cellRenderer: React.memo((params: any) => {
          return (
            <UILabel variant='badge' intent={(MODEL_DEPLOY_STATUS[params.value as keyof typeof MODEL_DEPLOY_STATUS]?.intent as UILabelIntent) || 'neutral'}>
              {MODEL_DEPLOY_STATUS[params.value as keyof typeof MODEL_DEPLOY_STATUS]?.label || params.value}
            </UILabel>
          );
        }),
      },
      {
        headerName: '설명',
        field: 'description' as const,
        flex: 1,
        minWidth: 392,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '모델유형',
        field: 'type' as const,
        width: 120,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '배포유형',
        field: 'servingType' as const,
        width: 120,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '운영배포 여부',
        field: 'isDeployed' as const,
        width: 120,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '공개범위',
        field: 'isPrivate' as const,
        width: 120,
        cellStyle: { paddingLeft: '16px' },
        cellRenderer: (params: any) => {
          return params.value ? '내부공유' : '전체공유';
        },
      },
      {
        headerName: '생성일시',
        field: 'createdAt' as const,
        width: 180,
        cellStyle: { paddingLeft: '16px' },
        cellRenderer: (params: any) => {
          return dateUtils.formatDate(params.value, 'datetime');
        },
      },
      {
        headerName: '최종 수정일시',
        field: 'updatedAt' as const,
        width: 180,
        cellStyle: { paddingLeft: '16px' },
        cellRenderer: (params: any) => {
          return dateUtils.formatDate(params.value, 'datetime');
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
                  <UIDataCnt count={data?.totalElements} />
                </div>
              </div>
              <div>
                <div className='w-[360px] h-[40px]'>
                  <UIInput.Search
                    value={searchText}
                    placeholder='검색어 입력'
                    onChange={e => {
                      setSearchText(e.target.value);
                    }}
                    onKeyDown={e => {
                      if (e.key === 'Enter') {
                        setSearchValues(prev => ({ ...prev, search: searchText }));
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
              loading={isFetching}
              rowData={gridData}
              columnDefs={columnDefs}
              selectedDataList={(selectedItems && selectedItems.length > 0) ? selectedItems.map((id) => ({ servingId: id })) : []}
              checkKeyName={'servingId'}
              onCheck={(selectedRows: any[]) => {
                const selectedIds = selectedRows.map(row => row.servingId);
                handleCheckboxChange(selectedIds);
              }}
            />
          </UIListContentBox.Body>
          <UIListContentBox.Footer>
            <UIPagination
              currentPage={searchValues.page + 1 || 1}
              hasNext={data?.hasNext}
              totalPages={data?.totalPages || 1}
              onPageChange={(page: number) => {
                setSearchValues(prev => ({
                  ...prev,
                  page: page - 1,
                }));
              }}
              className='flex justify-center'
            />
          </UIListContentBox.Footer>
        </UIListContainer>
      </UIArticle>
    </section>
  );
};
