import React, { useState } from 'react';

import { UIDataCnt, UIPagination } from '@/components/UI';
import { UIInput } from '@/components/UI/molecules';
import { UIArticle } from '@/components/UI/molecules/UIArticle';

import { UIGrid } from '../../../components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '../../../components/UI/molecules/list';

export const DP_040102_P02: React.FC = () => {
  const [searchValue, setSearchValue] = useState('');

  // 데이터
  const projectData = [
    {
      id: '1',
      no: 1,
      deployName: '예적금 상품 Q&A 세트',
      modelName: 'GPT/text-embedding-3-large',
      modelType: 'language',
      publicStatus: '전체공유',
      createdDate: '2025.03.25 10:15:20',
      modifiedDate: '2024-01-20 14:25:00',
    },
    {
      id: '2',
      no: 2,
      deployName: '모바일뱅킹 이용 가이드',
      modelName: 'Cohere/embed-multilingual',
      modelType: 'language',
      publicStatus: '전체공유',
      createdDate: '2025.03.25 10:15:20',
      modifiedDate: '2024-01-20 14:25:00',
    },
    {
      id: '3',
      no: 3,
      deployName: 'ATM/창구 업무 안내 문서',
      modelName: 'Google/text-bison-001',
      modelType: 'language',
      publicStatus: '전체공유',
      createdDate: '2025.03.25 10:15:20',
      modifiedDate: '2024-01-20 14:25:00',
    },
    {
      id: '4',
      no: 4,
      deployName: '외화 송금 및 환율 상담 로그',
      modelName: 'HuggingFace/bert-base-korean',
      modelType: 'language',
      publicStatus: '전체공유',
      createdDate: '2025.03.25 10:15:20',
      modifiedDate: '2024-01-20 14:25:00',
    },
    {
      id: '5',
      no: 5,
      deployName: '상품 비교형 답변 데이터',
      modelName: 'OpenAI/gpt-4-turbo',
      modelType: 'language',
      publicStatus: '전체공유',
      createdDate: '2025.03.25 10:15:20',
      modifiedDate: '2024-01-20 14:25:00',
    },
  ];

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
        headerName: '분류',
        field: 'deployName' as const,
        width: 272,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '금지어',
        field: 'modelName' as const,
        // [251112_퍼블수정] 컬럼속성 스타일 추가
        minWidth: 392,
        flex: 1,
        cellStyle: { paddingLeft: '16px' },
        // [251110_퍼블수정] 컬럼속성 말줄임 추가
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
      // 251112_퍼블수정 그리드 컬럼 속성영역 수정 S
      {
        headerName: '공개범위',
        field: 'publicStatus',
        width: 120,
      },
      {
        headerName: '생성일시',
        field: 'createdDate',
        width: 180,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '최종 수정일시',
        field: 'modifiedDate' as any,
        width: 180,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      // 251112_퍼블수정 그리드 컬럼 속성영역 수정 E
    ],
    []
  );

  return (
    <section className='section-modal'>
      <UIArticle className='article-grid'>
        <div className='article-body'>
          <UIListContainer>
            <UIListContentBox.Header>
              <div className='flex justify-between items-center w-full'>
                <div className='flex-shrink-0'>
                  <div style={{ width: '168px', paddingRight: '8px' }}>
                    <UIDataCnt count={projectData.length} prefix='총' unit='건' />
                  </div>
                </div>
                <div>
                  <div className='w-[360px]'>
                    <UIInput.Search
                      value={searchValue}
                      placeholder='검색어 입력'
                      onChange={e => {
                        setSearchValue(e.target.value);
                      }}
                    />
                  </div>
                </div>
              </div>
            </UIListContentBox.Header>
            <UIListContentBox.Body>
              <UIGrid
                type='multi-select'
                rowData={projectData}
                columnDefs={columnDefs}
                onClickRow={(_params: any) => {}}
                onCheck={(_selectedIds: any[]) => {
                }}
              />
            </UIListContentBox.Body>
            <UIListContentBox.Footer>
              <UIPagination currentPage={1} totalPages={3} onPageChange={() => {}} className='flex justify-center' />
            </UIListContentBox.Footer>
          </UIListContainer>
        </div>
      </UIArticle>
    </section>
  );
};
