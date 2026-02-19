import React, { useState } from 'react';

import { UIDataCnt, UIPagination } from '@/components/UI';
import { UIInput } from '@/components/UI/molecules';
import { UIArticle } from '@/components/UI/molecules/UIArticle';

import { UIGrid } from '../../../components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '../../../components/UI/molecules/list';

// AG_010102_P23 페이지
export const AG_010102_P23: React.FC = () => {
  const [searchValue, setSearchValue] = useState('');

  // 데이터
  const projectData = [
    {
      id: '1',
      no: 1,
      name: '웹검색',
      description: '실시간 웹 검색을 통해 최신 정보를 검색할 수 있습니다.',
      toolType: '검색',
    },
    {
      id: '2',
      no: 2,
      name: '파일업로드',
      description: 'PDF, 문서 파일을 업로드하여 내용을 분석할 수 있습니다.',
      toolType: '파일처리',
    },
    {
      id: '3',
      no: 3,
      name: '이미지생성',
      description: 'AI를 활용하여 다양한 스타일의 이미지를 생성할 수 있습니다.',
      toolType: '생성',
    },
    {
      id: '4',
      no: 4,
      name: '코드실행',
      description: 'Python 코드를 실행하고 결과를 확인할 수 있습니다.',
      toolType: '개발',
    },
    {
      id: '5',
      no: 5,
      name: '번역',
      description: '다양한 언어간 번역을 수행할 수 있습니다.',
      toolType: '언어',
    },
    {
      id: '6',
      no: 6,
      name: '데이터분석',
      description: 'CSV, Excel 파일의 데이터를 분석하고 시각화할 수 있습니다.',
      toolType: '분석',
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
        headerName: '설명',
        field: 'description' as const,
        minWidth: 464,
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
      {
        headerName: '도구 유형',
        field: 'toolType' as const,
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
                <div className='w-[360px]'>
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
