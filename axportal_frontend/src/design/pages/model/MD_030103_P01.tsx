import React, { useMemo, useState } from 'react';

import { UIDataCnt, UILabel, UIPagination } from '@/components/UI';
import { UIProgress } from '@/components/UI/atoms/UIProgress/component';
import { UIInput } from '@/components/UI/molecules';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UIArticle } from '@/components/UI/molecules/UIArticle';

export const MD_030103_P01 = () => {
  const [searchValue, setSearchValue] = useState('');

  // 그리드 샘플 데이터
  const sampleData = [
    {
      id: '1',
      modelName: 'FT_TEST_20250605',
      status: '완료',
      description: '파인튜닝 테스트',
      publicRange: '전체공유',
      progress: 100,
      createdDate: '2025.03.24 18:23:43',
      modifiedDate: '2025.03.24 18:23:43',
    },
    {
      id: '2',
      modelName: 'FT_PRODUCTION_20250520',
      status: '완료',
      description: '파인튜닝 테스트',
      publicRange: '전체공유',
      progress: 75,
      createdDate: '2025.03.24 18:23:43',
      modifiedDate: '2025.03.24 18:23:43',
    },
    {
      id: '3',
      modelName: 'FT_EXPERIMENTAL_20250515',
      status: '학습중',
      description: '파인튜닝 테스트',
      publicRange: '전체공유',
      progress: 0,
      createdDate: '2025.03.24 18:23:43',
      modifiedDate: '2025.03.24 18:23:43',
    },
    {
      id: '4',
      modelName: 'FT_OPTIMIZE_20250510',
      status: '완료',
      description: '파인튜닝 테스트',
      publicRange: '전체공유',
      progress: 45,
      createdDate: '2025.03.24 18:23:43',
      modifiedDate: '2025.03.24 18:23:43',
    },
    {
      id: '5',
      modelName: 'FT_BASELINE_20250501',
      status: '오류',
      description: '파인튜닝 테스트',
      publicRange: '전체공유',
      progress: 100,
      createdDate: '2025.03.24 18:23:43',
      modifiedDate: '2025.03.24 18:23:43',
    },
  ];

  // 그리드 컬럼 정의
  const columnDefs: any = useMemo(
    () => [
      {
        headerName: 'NO',
        field: 'id',
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
        field: 'modelName',
        width: 272,
      },
      {
        headerName: '상태',
        field: 'status' as const,
        width: 120,
        cellStyle: { paddingLeft: '16px' },
        cellRenderer: React.memo((params: any) => {
          const getStatusIntent = (status: string) => {
            switch (status) {
              case '실패':
                return 'error';
              case '이용가능':
                return 'complete';
              default:
                return 'complete';
            }
          };
          return (
            <UILabel variant='badge' intent={getStatusIntent(params.value)}>
              {params.value}
            </UILabel>
          );
        }),
      },
      {
        headerName: '설명',
        field: 'description',
        minWidth: 392,
        flex: 1,
        showTooltip: true,
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
        headerName: '모델',
        field: 'progress',
        width: 347,
        cellRenderer: React.memo((params: any) => {
          const rowData = params.data;
          const status = rowData.status;
          let progressStatus: 'normal' | 'error' = 'normal';

          if (status === '오류') {
            progressStatus = 'error';
          } else if (status === '학습중' || status === '완료') {
            progressStatus = 'normal';
          } else {
            progressStatus = 'normal';
          }

          return <UIProgress value={params.value} status={progressStatus} showPercent={true} className='w-[100%]' />;
        }),
      },
      {
        headerName: '공개범위',
        field: 'publicRange',
        width: 120,
      },
      {
        headerName: '생성일시',
        field: 'createdDate' as const,
        width: 180,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '최종 수정일시',
        field: 'modifiedDate' as const,
        width: 180,
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
            <div className='flex items-center w-full mb-2'>
              <div className='flex-shrink-0'>
                <div style={{ width: '102px', paddingRight: '12px' }}>
                  <UIDataCnt count={sampleData.length} prefix='총' unit='건' />
                </div>
              </div>
              <div className='w-[360px] h-[40px] ml-auto'>
                <UIInput.Search
                  value={searchValue}
                  placeholder='검색어 입력'
                  onChange={e => {
                    setSearchValue(e.target.value);
                  }}
                />
              </div>
            </div>
          </UIListContentBox.Header>
          <UIListContentBox.Body>
            <UIGrid type='multi-select' rowData={sampleData} columnDefs={columnDefs} />
          </UIListContentBox.Body>
          <UIListContentBox.Footer>
            <UIPagination currentPage={1} totalPages={10} onPageChange={() => {}} className='flex justify-center' />
          </UIListContentBox.Footer>
        </UIListContainer>
      </UIArticle>
    </section>
  );
};
