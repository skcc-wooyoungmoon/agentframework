import React, { useEffect, useMemo, useState } from 'react';
import { UIDataCnt, UILabel, type UILabelIntent, UIPagination } from '@/components/UI';
import { UIProgress } from '@/components/UI/atoms/UIProgress/component';
import { UIInput } from '@/components/UI/molecules';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import type { GetFineTuningTrainingsRequest } from '@/services/model/fineTuning/types.ts';
import { useGetFineTuningTrainings } from '@/services/model/fineTuning/modelFineTuning.service.ts';
import { fineTuningSelectPopupAtom } from '@/stores/model/fineTuning/fineTuning.atoms.ts';
import { useAtom } from 'jotai';
import dateUtils from '@/utils/common/date.utils.ts';
import { FINE_TUNING_STATUS_MAP } from '@/constants/model/fineTuningStatus.constants.ts';

export const FineTuningSelectPopup = () => {
  const [, setLocalSelectedIds] = useAtom(fineTuningSelectPopupAtom);

  const [searchText, setSearchText] = useState('');

  // 상태 필터 상태
  // const [statusFilter, setStatusFilter] = useState('all');
  // const [isStatusDropdownOpen, setIsStatusDropdownOpen] = useState(false);

  const [searchValues, setSearchValues] = useState<GetFineTuningTrainingsRequest>({
    page: 1,
    size: 6,
    sort: 'updated_at,desc',
    search: '',
    queryKey: 'metric',
  });

  const { data, refetch } = useGetFineTuningTrainings(searchValues, { enabled: !!searchValues.page && !!searchValues.size });

  useEffect(() => {
    setLocalSelectedIds([]);
  }, [data?.content]);

  useEffect(() => {
    refetch();
  }, [searchValues]);

  const rowData = useMemo(() => {
    if (!data?.content) {
      return [];
    }

    return data.content.map((item, index) => ({ ...item, no: (searchValues.page - 1) * searchValues.size + index + 1 }));
  }, [data]);

  // 컬럼 정의
  const columnDefs: any = useMemo(
    () => [
      {
        headerName: 'NO',
        field: 'no',
        width: 56,
        minWidth: 56,
        maxWidth: 56,
        cellClass: 'text-center',
        headerClass: 'text-center',
        cellStyle: {
          textAlign: 'center' as const,
          display: 'flex' as const,
          alignItems: 'center' as const,
          justifyContent: 'center' as const,
        },
        sortable: false,
        suppressHeaderMenuButton: true,
        suppressSizeToFit: true,
      },
      {
        headerName: '제목',
        field: 'name',
        width: 272,
      },
      {
        headerName: '상태',
        field: 'status',
        width: 120,
        cellRenderer: React.memo((params: { value: string }) => {
          return (
            <UILabel variant='badge' intent={FINE_TUNING_STATUS_MAP[params.value as keyof typeof FINE_TUNING_STATUS_MAP]?.intent as UILabelIntent}>
              {FINE_TUNING_STATUS_MAP[params.value as keyof typeof FINE_TUNING_STATUS_MAP]?.label}
            </UILabel>
          );
        }),
      },
      {
        headerName: '설명',
        field: 'description',
        minWidth: 392,
        flex: 1,
      },
      {
        headerName: '진행률',
        field: 'progress',
        width: 347,
        cellRenderer: React.memo((params: any) => {
          const rowData = params.data;
          const status = rowData.status;
          const percentage = rowData.progress?.percentage || 0;
          let progressStatus: 'normal' | 'error' = 'normal';

          if (status === 'error') {
            progressStatus = 'error';
          }

          return <UIProgress value={percentage} status={progressStatus} showPercent={true} className='w-[100%]' />;
        }),
      },
      {
        headerName: '생성 일시',
        field: 'createdAt',
        width: 120,
        valueGetter: (params: any) => {
          // console.log(params);
          const createdAt = params.data.createdAt;
          return createdAt ? dateUtils.formatDate(new Date(createdAt), 'datetime') : '';
        },
      },
      {
        headerName: '최종 수정일시',
        field: 'updatedAt',
        width: 120,
        valueGetter: (params: any) => {
          const updatedAt = params.data.updatedAt;
          return updatedAt ? dateUtils.formatDate(new Date(updatedAt), 'datetime') : '';
        },
      },
    ],
    []
  );

  const handleSearch = () => {
    setSearchValues(prev => ({ ...prev, search: searchText }));
  };

  return (
    <>
      <section className='section-modal'>
        <UIArticle className='article-grid'>
          <UIListContainer>
            <UIListContentBox.Header>
              <div className='flex items-center w-full mb-2'>
                <div className='flex-shrink-0'>
                  <div style={{ width: '102px', paddingRight: '12px' }}>
                    <UIDataCnt count={data?.totalElements} />
                  </div>
                </div>
                <div className='w-[360px] h-[40px] ml-auto'>
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
            </UIListContentBox.Header>
            <UIListContentBox.Body>
              <UIGrid
                type='multi-select'
                rowData={rowData}
                columnDefs={columnDefs}
                onCheck={(selectedItem: any[]) => {
                  setLocalSelectedIds(selectedItem.map(item => item.id));
                }}
              />
            </UIListContentBox.Body>
            <UIListContentBox.Footer>
              <UIPagination
                currentPage={searchValues.page || 1}
                totalPages={data?.totalPages || 0}
                onPageChange={(page: number) => {
                  setSearchValues(prev => ({ ...prev, page }));
                }}
                className='flex justify-center'
                hasNext={data?.hasNext}
              />
            </UIListContentBox.Footer>
          </UIListContainer>
        </UIArticle>
      </section>
    </>
  );
};
