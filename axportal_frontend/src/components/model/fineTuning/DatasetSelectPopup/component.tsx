import React, { useEffect, useMemo, useState } from 'react';

import { UIDataCnt, UILabel, UIPagination, UITextLabel, UITypography } from '@/components/UI';
import { UIInput } from '@/components/UI/molecules';
import { UIArticle } from '@/components/UI/molecules/UIArticle';

import { useGetDatasets } from '@/services/data/dataCtlgDataSet.services';
import { useAtom } from 'jotai';
import { datasetSelectPopupAtom } from '@/stores/model/fineTuning/fineTuning.atoms';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UIGrid } from '@/components/UI/molecules/grid';
import { dateUtils } from '@/utils/common';
import type { GetDatasetsResponse } from '@/services/data/types.ts';

const STATUS_CONFIG = {
  completed: {
    label: '이용 가능',
    intent: 'complete' as const,
  },
  processing: {
    label: '진행중',
    intent: 'progress' as const,
  },
  failed: {
    label: '실패',
    intent: 'error' as const,
  },
  canceled: {
    label: '취소',
    intent: 'stop' as const,
  },
} as const;

export const DatasetSelectPopup = ({ learningType, selectedDatasets }: { learningType: string; selectedDatasets: GetDatasetsResponse[] }) => {
  const [localSelectedDatasets, setLocalSelectedDatasets] = useAtom(datasetSelectPopupAtom);
  const [searchText, setSearchText] = useState('');

  // requestParams 설정
  const [searchValues, setSearchValues] = useState({
    page: 1,
    size: 6,
    sort: 'created_at,desc',
    search: searchText,
    filter: `status:completed,type:` + (learningType === 'dpo' ? 'dpo_finetuning' : learningType === 'supervised' ? 'supervised_finetuning' : 'unsupervised_finetuning'),
  });

  // API 호출
  const { data, refetch } = useGetDatasets(searchValues, { enabled: !!learningType });

  // 페이지/사이즈 변경 시 파라미터 업데이트
  useEffect(() => {
    refetch();
  }, [searchValues]);

  const rowData = useMemo(() => {
    if (!data?.content || data.content.length === 0) return [];
    return data.content.map((item: any, index: number) => ({
      ...item,
      no: (searchValues.page - 1) * searchValues.size + index + 1,
    }));
  }, [data]);

  useEffect(() => {
    if (selectedDatasets && selectedDatasets.length > 0) {
      setLocalSelectedDatasets(JSON.parse(JSON.stringify(selectedDatasets)));
    }
  }, [selectedDatasets]);

  const handleSearch = () => {
    setSearchValues(prev => ({
      ...prev,
      search: searchText,
      page: 1, // 검색 시 첫 페이지로 이동
    }));
    setLocalSelectedDatasets([]);
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
        width: 272,
      },
      {
        headerName: '상태',
        field: 'status' as const,
        width: 120,
        cellStyle: { paddingLeft: '16px' },
        cellRenderer: React.memo((params: any) => {
          const status = params.value as keyof typeof STATUS_CONFIG;
          const config = STATUS_CONFIG[status] || {
            label: status,
            intent: 'complete' as const,
          };
          return (
            <UILabel variant='badge' intent={config.intent}>
              {config.label}
            </UILabel>
          );
        }),
      },
      {
        headerName: '설명',
        field: 'description' as const,
        flex: 1,
        minWidth: 392,
        cellRenderer: React.memo((params: any) => {
          return (
            <div
              style={{
                paddingLeft: '16px',
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                whiteSpace: 'nowrap',
              }}
              title={params.value}
            >
              {params.value}
            </div>
          );
        }),
      },
      {
        headerName: '태그',
        field: 'tags' as const,
        width: 120,
        cellRenderer: (params: any) => {
          const tags = params.value || [];
          const tagText = tags.map((tag: { name: string }) => tag.name).join(', ');
          return (
            <div
              style={{
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                whiteSpace: 'nowrap',
              }}
              title={tagText}
            >
              <div className='flex gap-1 flex-wrap'>
                {tags.slice(0, 2).map((tag: { name: string }, index: number) => (
                  <UITextLabel key={index} intent='tag'>
                    {tag.name}
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
      {
        headerName: '공개범위',
        field: 'scope' as const,
        width: 160,
        cellStyle: { paddingLeft: '16px' },
        cellRenderer: (params: any) => {
          // scope 필드가 없거나 undefined인 경우 기본값 표시
          return params.value || '';
        },
      },
      {
        headerName: '유형',
        field: 'type' as const,
        width: 120,
        cellStyle: { paddingLeft: '16px' },
        cellRenderer: (params: any) => {
          return params.value === 'supervised_finetuning' ? '지도학습' : params.value === 'unsupervised_finetuning' ? '비지도학습' : params.value || '';
        },
      },
      {
        headerName: '생성일시',
        field: 'createdAt' as const,
        width: 240,
        cellStyle: { paddingLeft: '16px' },
        cellRenderer: (params: any) => {
          return params.value ? dateUtils.formatDate(new Date(params.value), 'datetime') : '-';
        },
      },
      {
        headerName: '최종 수정일시',
        field: 'updatedAt' as const,
        width: 180,
        cellStyle: { paddingLeft: '16px' },
        cellRenderer: (params: any) => {
          return params.value ? dateUtils.formatDate(new Date(params.value), 'datetime') : '-';
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
              <div className='flex gap-3'>
                <div className='w-[360px]'>
                  <UIInput.Search
                    value={searchText}
                    placeholder='검색어 입력'
                    onChange={e => {
                      setSearchText(e.target.value);
                    }}
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
              rowData={rowData}
              columnDefs={columnDefs}
              selectedDataList={localSelectedDatasets}
              checkKeyName={'id'}
              onCheck={(selectedIds: any[]) => {
                // console.log('다중 선택:', selectedIds);
                // id 값 기준으로 중복 제거 (첫 번째 항목만 유지)
                // const uniqueSelectedIds = Array.from(new Map(selectedIds.map((item: any) => [item.id, item])).values());
                // console.log('다중 선택:', uniqueSelectedIds);
                setLocalSelectedDatasets(selectedIds);
              }}
            />
          </UIListContentBox.Body>
          <UIListContentBox.Footer>
            <UIPagination
              currentPage={searchValues.page}
              totalPages={data?.totalPages || 1}
              onPageChange={(newPage: number) => {
                setSearchValues(prev => ({
                  ...prev,
                  page: newPage, // 검색 시 첫 페이지로 이동
                }));
              }}
              className='flex justify-center'
              hasNext={data?.hasNext}
            />
          </UIListContentBox.Footer>
        </UIListContainer>
      </UIArticle>
    </section>
  );
};
