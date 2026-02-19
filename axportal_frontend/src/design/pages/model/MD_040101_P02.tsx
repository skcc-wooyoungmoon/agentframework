import React, { useState } from 'react';

import { UIDataCnt, UIPagination } from '@/components/UI';
import { UIInput } from '@/components/UI/molecules';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIGrid } from '../../../components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '../../../components/UI/molecules/list';
import { UITextLabel } from '@/components/UI/atoms/UITextLabel';

// MD_040101_P02 페이지
export const MD_040101_P02: React.FC = () => {
  const [searchValue, setSearchValue] = useState('');

  // 데이터
  const projectData = [
    {
      id: '1',
      no: 1,
      name: '부정',
      version: 'Release Ver.1',
      type: '채팅',
      publicRange: '전체공유',
      tags: ['가나다라마바사아자', '가나다라마바사아자', '가나다라마바사아자', '가나다라마바사아자', '가나다라마바사아자'],
      connectedAgent: '0',
      createdDate: '2025.03.24 18:23:43',
    },
    {
      id: '2',
      no: 2,
      name: '긍정',
      version: 'Release Ver.1',
      type: '채팅',
      publicRange: '전체공유',
      tags: ['가나다라마바사아', '가나다라마바사아'],
      connectedAgent: '0',
      createdDate: '2025.03.24 18:23:43',
    },
    {
      id: '3',
      no: 3,
      name: 'IR, 반기보고서 등 증권문서 요약 및 항목별',
      version: 'Release Ver.1',
      type: '채팅',
      publicRange: '전체공유',
      tags: ['상품설명'],
      connectedAgent: '0',
      createdDate: '2025.03.24 18:23:43',
    },
    {
      id: '4',
      no: 3,
      name: 'IR, 반기보고서 등 증권문서 요약 및 항목별',
      version: 'Release Ver.1',
      type: '채팅',
      publicRange: '전체공유',
      tags: ['상품설명'],
      connectedAgent: '0',
      createdDate: '2025.03.24 18:23:43',
    },
    {
      id: '5',
      no: 3,
      name: 'IR, 반기보고서 등 증권문서 요약 및 항목별',
      version: 'Release Ver.1',
      type: '채팅',
      publicRange: '전체공유',
      tags: ['상품설명'],
      connectedAgent: '0',
      createdDate: '2025.03.24 18:23:43',
    },
    {
      id: '6',
      no: 3,
      name: 'IR, 반기보고서 등 증권문서 요약 및 항목별',
      version: 'Release Ver.1',
      type: '채팅',
      publicRange: '전체공유',
      tags: ['상품설명'],
      connectedAgent: '0',
      createdDate: '2025.03.24 18:23:43',
    },
    {
      id: '7',
      no: 3,
      name: 'IR, 반기보고서 등 증권문서 요약 및 항목별',
      version: 'Release Ver.1',
      type: '채팅',
      publicRange: '전체공유',
      tags: ['상품설명'],
      connectedAgent: '0',
      createdDate: '2025.03.24 18:23:43',
    },
    {
      id: '8',
      no: 3,
      name: 'IR, 반기보고서 등 증권문서 요약 및 항목별',
      version: 'Release Ver.1',
      type: '채팅',
      publicRange: '전체공유',
      tags: ['상품설명'],
      connectedAgent: '0',
      createdDate: '2025.03.24 18:23:43',
    },
    {
      id: '9',
      no: 3,
      name: 'IR, 반기보고서 등 증권문서 요약 및 항목별',
      version: 'Release Ver.1',
      type: '채팅',
      publicRange: '전체공유',
      tags: ['상품설명'],
      connectedAgent: '0',
      createdDate: '2025.03.24 18:23:43',
    },
    {
      id: '10',
      no: 3,
      name: 'IR, 반기보고서 등 증권문서 요약 및 항목별',
      version: 'Release Ver.1',
      type: '채팅',
      publicRange: '전체공유',
      tags: ['상품설명'],
      connectedAgent: '0',
      createdDate: '2025.03.24 18:23:43',
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
      {
        headerName: '버전',
        field: 'version' as const,
        width: 204,
        cellStyle: { paddingLeft: '16px' },
        cellRenderer: React.memo((params: any) => {
          return (
            <div className='flex items-center gap-1'>
              <UITextLabel intent='blue'>{params.value}</UITextLabel>
              <UITextLabel intent='gray'>Lastest Ver.1</UITextLabel>
            </div>
          );
        }),
      },
      {
        headerName: '유형',
        field: 'type' as const,
        width: 120,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '공개범위',
        field: 'publicRange' as const,
        width: 120,
        cellStyle: {
          paddingLeft: '16px',
        },
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
        headerName: '연결 에이전트',
        field: 'connectedAgent' as const,
        width: 120,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '생성일시',
        field: 'createdDate' as const,
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
              <div>
                <div className='w-[360px] h-[40px]'>
                  <UIInput.Search
                    value={searchValue}
                    placeholder='이름 입력'
                    onChange={e => {
                      setSearchValue(e.target.value);
                    }}
                  />
                </div>
              </div>
            </div>
          </UIListContentBox.Header>
          <UIListContentBox.Body>
            <UIGrid type='multi-select' rowData={projectData} columnDefs={columnDefs} onClickRow={(_params: any) => {}} onCheck={(_selectedIds: any[]) => {}} />
          </UIListContentBox.Body>
          <UIListContentBox.Footer>
            <UIPagination currentPage={1} totalPages={3} onPageChange={() => {}} className='flex justify-center' />
          </UIListContentBox.Footer>
        </UIListContainer>
      </UIArticle>
    </section>
  );
};
