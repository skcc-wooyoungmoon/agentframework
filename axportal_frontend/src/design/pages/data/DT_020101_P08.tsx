import React, { useState } from 'react';

import { UIDataCnt, UIPagination } from '@/components/UI';
import { UIDataList, UIInput } from '@/components/UI/molecules';
import { UIArticle } from '@/components/UI/molecules/UIArticle';

import { UIGrid } from '../../../components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '../../../components/UI/molecules/list';

export const DT_020101_P08: React.FC = () => {
  const [searchValue, setSearchValue] = useState('');

  // 데이터
  const projectData = [
    {
      id: '1',
      no: 1,
      name: '예적금 상품 Q&A 세트',
      createdDate: '2025.03.24 18:23:43',
      modifiedDate: '2025.03.24 18:23:43',
    },
    {
      id: '2',
      no: 2,
      name: '모바일뱅킹 이용 가이드',
      createdDate: '2025.03.23 14:15:22',
      modifiedDate: '2025.03.24 10:30:15',
    },
    {
      id: '3',
      no: 3,
      name: 'ATM/창구 업무 안내 문서',
      createdDate: '2025.03.22 09:45:33',
      modifiedDate: '2025.03.23 16:20:44',
    },
    {
      id: '4',
      no: 4,
      name: '외화 송금 및 환율 상담 로그',
      createdDate: '2025.03.21 11:30:12',
      modifiedDate: '2025.03.22 08:45:21',
    },
    {
      id: '5',
      no: 5,
      name: '상품 비교형 답변 데이터',
      createdDate: '2025.03.20 15:22:55',
      modifiedDate: '2025.03.21 13:10:33',
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
      // 251113_퍼블수정 그리드 컬럼 속성 '이름' 영역 수정 E
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
      <UIArticle className='article-dataList'>
        <div className='article-body'>
          <UIDataList
            gap={12}
            direction='column'
            datalist={[
              { dataName: '이름', dataValue: '신한 대출상품 약관파일 묶음' },
              { dataName: '유형', dataValue: '대출' },
              { dataName: '구성 파일', dataValue: '120,000개' },
              { dataName: '생성 일시', dataValue: '2025.03.02 20:12:24' },
              { dataName: '최종 수정일', dataValue: '2025.05.02 11:29:23' },
            ]}
          >
            {null}
          </UIDataList>
        </div>
      </UIArticle>

      <UIArticle className='article-grid'>
        <UIListContainer>
          <UIListContentBox.Header>
            <div className='flex justify-between items-center w-full'>
              <div className='flex-shrink-0'>
                <div style={{ width: '168px', paddingRight: '8px' }}>
                  <UIDataCnt count={projectData.length} prefix='구성파일 총' unit='건' />
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
              type='default'
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
