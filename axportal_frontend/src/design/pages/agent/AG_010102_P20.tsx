import React, { useState } from 'react';

import { UIDataCnt, UIPagination } from '@/components/UI';
import { UIInput } from '@/components/UI/molecules';
import { UIArticle } from '@/components/UI/molecules/UIArticle';

import { UIGrid } from '../../../components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '../../../components/UI/molecules/list';

// AG_010102_P20 페이지
export const AG_010102_P20: React.FC = () => {
  const [searchValue, setSearchValue] = useState('');

  // 데이터
  const projectData = [
    {
      id: '1',
      no: 1,
      deployType: '콜센터 응대 특화 모델',
      nodeType: 'GPT-4-Callcenter-Tuned',
      nodeName: '콜센터 응대 특화 모델 설명',
      type: 'serverless, language',
      applicable: 'Y',
    },
    {
      id: '2',
      no: 2,
      deployType: '콜센터 응대 특화 모델 설명',
      nodeType: 'GPT-NeoX-Finance-QA',
      nodeName: '금융 Q&A 응답 모델 설명',
      type: 'serverless, language',
      applicable: 'Y',
    },
    {
      id: '3',
      no: 3,
      deployType: '준법 감수 문장 필터',
      nodeType: 'LLaMA-3-Legal-Checker',
      nodeName: '준법 감수 문장 필터 설명',
      type: 'serverless, language',
      applicable: 'Y',
    },
    {
      id: '4',
      no: 4,
      deployType: '업무 보고서 요약기',
      nodeType: 'Mistral-7B-DocSummary',
      nodeName: '업무 보고서 요약기 설명',
      type: 'serverless, language',
      applicable: 'Y',
    },
    {
      id: '5',
      no: 5,
      deployType: '상품 설명 생성기',
      nodeType: 'Phi-2-CardPromo-Writer',
      nodeName: '상품 설명 생성기 설명',
      type: 'serverless, language',
      applicable: 'Y',
    },
    {
      id: '6',
      no: 6,
      deployType: '업무 보고서 요약기2',
      nodeType: 'Mistral-7B-DocSummary',
      nodeName: '업무 보고서 요약기 설명',
      type: 'serverless, language',
      applicable: 'Y',
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
        field: 'deployType' as const,
        width: 300,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '모델명',
        field: 'nodeType' as const,
        width: 230,
        cellStyle: { paddingLeft: '16px' },
      },
      // [251113_퍼블수정] 그리드 컬럼 속성 수정
      {
        headerName: '설명',
        field: 'nodeName' as const,
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
      // [251113_퍼블수정] 그리드 컬럼 텍스트 수정
      {
        headerName: '배포 유형',
        field: 'type' as const,
        width: 180,
        cellStyle: { paddingLeft: '16px' },
      },
      // [251113_퍼블수정] 그리드 컬럼 속성 수정
      {
        headerName: '가드레일 적용 여부',
        field: 'applicable' as const,
        width: 179,
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
            <UIGrid
              type='single-select'
              rowData={projectData}
              columnDefs={columnDefs}
              onClickRow={(_params: any) => {}}
            />
          </UIListContentBox.Body>
          <UIListContentBox.Footer>
            <UIPagination currentPage={1} totalPages={3} onPageChange={() => {}} className='flex justify-center' />
          </UIListContentBox.Footer>
        </UIListContainer>
      </UIArticle>
    </section>
  );
};
