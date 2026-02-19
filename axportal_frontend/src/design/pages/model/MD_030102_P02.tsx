import React, { useState } from 'react';

import { UIDataCnt, UIPagination, UILabel, UITextLabel } from '@/components/UI';
import { UIInput } from '@/components/UI/molecules';
import { UIArticle } from '@/components/UI/molecules/UIArticle';

import { UIGrid } from '../../../components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '../../../components/UI/molecules/list';

export const MD_030102_P02: React.FC = () => {
  const [searchValue, setSearchValue] = useState('');

  // 데이터
  const projectData = [
    {
      id: '1',
      no: 1,
      name: '예적금 상품 Q&A 세트',
      status: '이용가능',
      description: '정기예금 등 기본 수신상품에 대한 고객 질문과 상담',
      publicRange: '전체공유',
      tags: ['가나다라마바사아자', '가나다라마바사아자', '가나다라마바사아자', '가나다라마바사아자', '가나다라마바사아자'],
      type: '지도학습',
      createdDate: '2025.03.24 18:23:43',
      modifiedDate: '2025.03.24 18:23:43',
    },
    {
      id: '2',
      no: 2,
      name: '금융 대출 승인 데이터',
      status: '이용가능',
      description: '고객 신용평가 및 대출 승인 관련 데이터',
      publicRange: '전체공유',
      tags: ['가나다라마바사아', '가나다라마바사아'],
      type: '지도학습',
      createdDate: '2025.03.23 14:15:32',
      modifiedDate: '2025.03.23 14:15:32',
    },
    {
      id: '3',
      no: 3,
      name: '민원 분류 데이터',
      status: '이용가능',
      description: '고객 민원 분류 및 처리 데이터',
      publicRange: '전체공유',
      tags: ['test'],
      type: '비지도학습',
      createdDate: '2025.03.22 09:45:21',
      modifiedDate: '2025.03.22 09:45:21',
    },
    {
      id: '4',
      no: 4,
      name: '금융서류 분류 데이터',
      status: '실패',
      description: '각종 금융서류 분류 및 정리',
      publicRange: '전체공유',
      tags: ['Tag1', 'Tag2'],
      type: '지도학습',
      createdDate: '2025.03.24 16:30:15',
      modifiedDate: '2025.03.24 16:30:15',
    },
    {
      id: '5',
      no: 5,
      name: 'Transformer 번역',
      status: '이용가능',
      description: '다국어 번역을 위한 Transformer 기반 신경망 모델',
      publicRange: '전체공유',
      tags: ['NLP', '번역', '다국어'],
      type: '지도학습',
      createdDate: '2025.03.21 12:00:00',
      modifiedDate: '2025.03.21 12:00:00',
    },
    {
      id: '6',
      no: 6,
      name: 'AutoEncoder 이상탐지',
      status: '이용가능',
      description: '데이터 이상 패턴 탐지를 위한 오토인코더 모델',
      publicRange: '전체공유',
      tags: ['이상탐지'],
      type: '비지도학습',
      createdDate: '2025.03.20 15:30:00',
      modifiedDate: '2025.03.20 15:30:00',
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
        headerName: '이름',
        field: 'name' as const,
        width: 240,
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
        field: 'description' as const,
        flex: 1,
        minWidth: 392,
        showTooltip: true,
        cellRenderer: React.memo((params: any) => {
          return (
            <div
              style={{
                paddingLeft: '16px',
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
        headerName: '공개범위',
        field: 'publicRange',
        width: 120,
      },
      // 251107_퍼블수정 그리드 컬럼 속성 '태그' 영역 수정 S
      {
        headerName: '태그',
        field: 'tags' as const,
        width: 230,
        cellStyle: { paddingLeft: '16px' },
        cellRenderer: (params: any) => {
          if (!params.value || !Array.isArray(params.value) || params.value.length === 0) {
            return null;
          }
          const tagText = params.value.join(', ');
          return (
            <div title={tagText}>
              <div className='flex gap-1'>
                {params.value.slice(0, 2).map((tag: string, index: number) => (
                  <UITextLabel key={index} intent='tag' className='nowrap'>
                    {tag}
                  </UITextLabel>
                ))}
              </div>
            </div>
          );
        },
      },
      // 251107_퍼블수정 그리드 컬럼 속성 '태그' 영역 수정 E
      {
        headerName: '유형',
        field: 'type' as const,
        width: 120,
        cellStyle: { paddingLeft: '16px' },
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
            <div className='flex justify-between items-center w-full'>
              <div className='flex-shrink-0'>
                <div style={{ width: '168px', paddingRight: '8px' }}>
                  <UIDataCnt count={projectData.length} prefix='총' unit='건' />
                </div>
              </div>
              <div className='flex gap-3'>
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
      </UIArticle>
    </section>
  );
};
