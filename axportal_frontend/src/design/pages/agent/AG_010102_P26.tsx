import React, { useState } from 'react';

import { UIDataCnt, UIPagination } from '@/components/UI';
import { UIInput } from '@/components/UI/molecules';
import { UIArticle } from '@/components/UI/molecules/UIArticle';

import { UIGrid } from '../../../components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '../../../components/UI/molecules/list';

// AG_010102_P26 페이지
export const AG_010102_P26: React.FC = () => {
  const [searchValue, setSearchValue] = useState('');

  // 데이터
  const projectData = [
    {
      id: '1',
      no: 1,
      deployName: '콜센터 응대 특화 모델',
      modelType: 'GPT-4-Callcenter-Tuned',
      description: '콜센터 응대 특화 모델 설명',
    },
    {
      id: '2',
      no: 2,
      deployName: '금융 Q&A 응답 모델',
      modelType: 'GPT-NeoX-Finance-QA',
      description: '금융 Q&A 응답 모델 설명',
    },
    {
      id: '3',
      no: 3,
      deployName: '준법 감수 문장 필터',
      modelType: 'LLaMA-3-Legal-Checker',
      description: '준법 감수 문장 필터 설명',
    },
    {
      id: '4',
      no: 4,
      deployName: '업무 보고서 요약기',
      modelType: 'Mistral-7B-DocSummary',
      description: '업무 보고서 요약기 설명',
    },
    {
      id: '5',
      no: 5,
      deployName: '상품 설명 생성기',
      modelType: 'Phi-2-CardPromo-Writer',
      description: '상품 설명 생성기 설명',
    },
    {
      id: '6',
      no: 6,
      deployName: '업무 보고서 요약기2',
      modelType: 'Mistral-7B-DocSummary',
      description: '업무 보고서 요약기 설명',
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
      // [251113_퍼블수정] 그리드 컬럼 속성 수정
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
                  <UIDataCnt count={projectData.length} prefix='총' unit='건' />
                </div>
              </div>
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
            </div>
          </UIListContentBox.Header>
          <UIListContentBox.Body>
            <UIGrid type='single-select' rowData={projectData} columnDefs={columnDefs} onClickRow={(_params: any) => {}} onCheck={(_selectedIds: any[]) => {}} />
          </UIListContentBox.Body>
          <UIListContentBox.Footer>
            <UIPagination currentPage={1} totalPages={3} onPageChange={() => {}} className='flex justify-center' />
          </UIListContentBox.Footer>
        </UIListContainer>
      </UIArticle>
    </section>
  );
};
