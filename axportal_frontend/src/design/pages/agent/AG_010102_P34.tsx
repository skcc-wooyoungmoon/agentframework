import React, { useState } from 'react';

import { UIDataCnt, UIPagination } from '@/components/UI';
import { UIInput } from '@/components/UI/molecules';
import { UIArticle } from '@/components/UI/molecules/UIArticle';

import { UIGrid } from '../../../components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '../../../components/UI/molecules/list';

// AG_010102_P34 페이지
export const AG_010102_P34: React.FC = () => {
  const [searchValue, setSearchValue] = useState('');

  // 데이터
  const projectData = [
    {
      id: '1',
      no: 1,
      name: '문서요약에이전트',
      builderName: '문서요약 빌더',
      description: '문서요약에이전트 설명',
      version: 'ver. 1',
    },
    {
      id: '2',
      no: 2,
      name: '금융 Q&A 응답 에이전트',
      builderName: '문서요약 빌더',
      description: '금융 Q&A 응답 에이전트 설명',
      version: 'ver. 1',
    },
    {
      id: '3',
      no: 3,
      name: '금융 Q&A 응답 에이전트',
      builderName: '문서요약 빌더',
      description: '금융 Q&A 응답 에이전트 설명',
      version: 'ver. 1',
    },
    {
      id: '4',
      no: 4,
      name: '업무 보고서 요약기',
      builderName: '문서요약 빌더',
      description: '업무 보고서 요약기 설명',
      version: 'ver. 1',
    },
    {
      id: '5',
      no: 5,
      name: '상품 설명 생성기',
      builderName: '문서요약 빌더',
      description: '상품 설명 생성기 설명',
      version: 'ver. 1',
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
      // 251113_퍼블수정 그리드 컬럼 속성 '이름' 영역 수정 S
      {
        headerName: '배포명',
        field: 'name' as const,
        width: 272,
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
      // 251113_퍼블수정 그리드 컬럼 속성 '이름' 영역 수정 E
      // 251201_퍼블수정 그리드 컬럼 속성 '빌더명' 영역 추가 S
      {
        headerName: '빌더명',
        field: 'builderName' as const,
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
      // 251201_퍼블수정 그리드 컬럼 속성 '빌더명' 영역 추가 E
      {
        headerName: '설명',
        field: 'description',
        width: 280,
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
        headerName: '버전',
        field: 'version',
        width: 100,
        cellStyle: {
          paddingLeft: '16px',
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
                  <UIDataCnt count={projectData.length} prefix='총' unit='건' />
                </div>
              </div>
              <div>
                <div className='w-[360px]'>
                  <UIInput.Search
                    value={searchValue}
                    placeholder='배포명 입력'
                    onChange={e => {
                      setSearchValue(e.target.value);
                    }}
                  />
                </div>
              </div>
            </div>
          </UIListContentBox.Header>
          <UIListContentBox.Body>
            <UIGrid type='single-select' rowData={projectData} columnDefs={columnDefs} onClickRow={(_params: any) => {}} />
          </UIListContentBox.Body>
          <UIListContentBox.Footer>
            <UIPagination currentPage={1} totalPages={3} onPageChange={() => {}} className='flex justify-center' />
          </UIListContentBox.Footer>
        </UIListContainer>
      </UIArticle>
    </section>
  );
};
