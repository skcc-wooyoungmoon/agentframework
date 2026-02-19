import React, { useState, useMemo } from 'react';

import { UIDataCnt } from '@/components/UI';
import { UIDataList, UIUnitGroup } from '@/components/UI/molecules';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIPagination } from '@/components/UI/atoms';
import { UIInput } from '@/components/UI/molecules';
import { UIDropdown } from '@/components/UI/molecules/dropdown/UIDropdown';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import type { ColDef } from 'ag-grid-community';

const rowData = [
  {
    no: 1,
    id: '1',
    name: '신한은행_대출심사_안내서_v1.md',
    title: '신한은행 대출 심사 안내서',
    attachmentName: '대출심사안내서.pdf',
    uuid: 'ss23ac10b-58cc-4372',
    createdDate: '2025.03.24 18:23:43',
    modifiedDate: '2025.03.24 18:23:43',
  },
  {
    no: 2,
    id: '2',
    name: '신한은행_대출심사_프로세스_v2.md',
    title: '신한은행 대출 심사 프로세스',
    attachmentName: '대출심사프로세스.pdf',
    uuid: 'ss23ac10b-58cc-4372',
    createdDate: '2025.03.24 18:23:43',
    modifiedDate: '2025.03.24 18:23:43',
  },
  {
    no: 3,
    id: '3',
    name: '신한은행_대출심사_기준_설명.md',
    title: '신한은행 대출 심사 기준 설명',
    attachmentName: '대출심사기준.pdf',
    uuid: 'ss23ac10b-58cc-4372',
    createdDate: '2025.03.24 18:23:43',
    modifiedDate: '2025.03.24 18:23:43',
  },
  {
    no: 4,
    id: '4',
    name: '신한은행_대출심사_절차_안내.md',
    title: '신한은행 대출 심사 절차 안내',
    attachmentName: '대출심사절차.pdf',
    uuid: 'ss23ac10b-58cc-4372',
    createdDate: '2025.03.24 18:23:43',
    modifiedDate: '2025.03.24 18:23:43',
  },
  {
    no: 5,
    id: '5',
    name: '신한은행_대출심사_체크리스트.md',
    title: '신한은행 대출 심사 체크리스트',
    attachmentName: '대출심사체크리스트.pdf',
    uuid: 'ss23ac10b-58cc-4372',
    createdDate: '2025.03.24 18:23:43',
    modifiedDate: '2025.03.24 18:23:43',
  },
  {
    no: 6,
    id: '6',
    name: '신한은행_대출심사_FAQ_v1.md',
    title: '신한은행 대출 심사 FAQ',
    attachmentName: '대출심사FAQ.pdf',
    uuid: 'ss23ac10b-58cc-4372',
    createdDate: '2025.03.24 18:23:43',
    modifiedDate: '2025.03.24 18:23:43',
  },
  {
    no: 7,
    id: '7',
    name: '신한은행_대출심사_FAQ_v2.md',
    title: '신한은행 대출 심사 FAQ',
    attachmentName: '대출심사FAQ_v2.pdf',
    uuid: 'ss23ac10b-58cc-4372',
    createdDate: '2025.03.24 18:23:43',
    modifiedDate: '2025.03.24 18:23:43',
  },
  {
    no: 8,
    id: '8',
    name: '신한은행_대출심사_FAQ_v3.md',
    title: '신한은행 대출 심사 FAQ',
    attachmentName: '대출심사FAQ_v3.pdf',
    uuid: 'ss23ac10b-58cc-4372',
    createdDate: '2025.03.24 18:23:43',
    modifiedDate: '2025.03.24 18:23:43',
  },
  {
    no: 9,
    id: '9',
    name: '신한은행_대출심사_FAQ_v4.md',
    title: '신한은행 대출 심사 FAQ',
    attachmentName: '대출심사FAQ_v4.pdf',
    uuid: 'ss23ac10b-58cc-4372',
    createdDate: '2025.03.24 18:23:43',
    modifiedDate: '2025.03.24 18:23:43',
  },
  {
    no: 10,
    id: '10',
    name: '신한은행_대출심사_FAQ_v5.md',
    title: '신한은행 대출 심사 FAQ',
    attachmentName: '대출심사FAQ_v5.pdf',
    uuid: 'ss23ac10b-58cc-4372',
    createdDate: '2025.03.24 18:23:43',
    modifiedDate: '2025.03.24 18:23:43',
  },
];

export const DT_020302_P06 = () => {
  // dropdown 상태
  const [value, setValue] = useState('1');

  // 더보기 메뉴 설정
  const moreMenuConfig = useMemo(
    () => ({
      items: [
        {
          label: '실행',
          action: 'run',
          onClick: (_rowData: any) => {},
        },
        {
          label: '수정',
          action: 'modify',
          onClick: (_rowData: any) => {},
        },
        {
          label: '복사',
          action: 'copy',
          onClick: (_rowData: any) => {},
        },
        {
          label: '삭제',
          action: 'delete',
          onClick: (_rowData: any) => {},
        },
      ],
      isActive: () => true, // 모든 테스트에 대해 활성화
    }),
    []
  );

  const columnDefs: ColDef[] = useMemo(
    () => [
      {
        headerName: 'NO',
        field: 'no',
        width: 56,
        cellStyle: {
          textAlign: 'center' as const,
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
        } as any,
        sortable: false,
        suppressHeaderMenuButton: true,
      },
      {
        headerName: '이름',
        field: 'name',
        width: 243,
        sortable: false,
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
        headerName: '타이틀',
        field: 'title',
        width: 253,
        sortable: false,
        cellStyle: { paddingLeft: 16 },
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
        headerName: '첨부파일 이름',
        field: 'attachmentName',
        width: 253,
        sortable: false,
        cellStyle: { paddingLeft: 16 },
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
        headerName: 'UUID',
        field: 'uuid',
        width: 320,
        sortable: false,
        cellStyle: { paddingLeft: 16 },
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
        headerName: '생성일시',
        field: 'createdDate',
        width: 180,
        sortable: false,
        cellStyle: { paddingLeft: 16 },
      },
      {
        headerName: '최종 수정일시',
        field: 'modifiedDate',
        width: 180,
        sortable: false,
        cellStyle: { paddingLeft: 16 },
      },
    ],
    []
  );

  // search 타입
  const [searchValue, setSearchValue] = useState('');

  return (
    <section className='section-modal'>
      <UIArticle className='article-dataList'>
        <div className='article-body'>
          <UIDataList
            gap={12}
            direction='column'
            datalist={[
              { dataName: '이름', dataValue: '여신상품 약관 MD묶음' },
              { dataName: '구성 MD파일', dataValue: '120,000개' },
            ]}
          >
            {null}
          </UIDataList>
        </div>
      </UIArticle>

      <UIArticle className='article-grid'>
        <UIListContainer>
          <UIListContentBox.Header>
            <div className='w-full'>
              <UIUnitGroup gap={16} direction='column'>
                <div className='flex justify-between w-full items-center'>
                  <div className='flex-shrink-0'>
                    <div style={{ width: '168px', paddingRight: '8px' }}>
                      <UIDataCnt count={0} prefix='MD파일 총' unit='건' />
                    </div>
                  </div>
                  <div className='flex gap-2 flex-shrink-0'>
                    <div style={{ width: '160px', flexShrink: 0 }}>
                      <UIDropdown
                        value={String(value)}
                        options={[
                          { value: '1', label: '파일명' },
                          { value: '2', label: 'UUID' },
                        ]}
                        onSelect={(value: string) => {
                          setValue(value);
                        }}
                        onClick={() => {}}
                        height={40}
                        variant='dataGroup'
                        disabled={false}
                      />
                    </div>
                    <div className='w-[360px]'>
                      <UIInput.Search
                        value={searchValue}
                        onChange={e => {
                          setSearchValue(e.target.value);
                        }}
                        placeholder='검색어 입력'
                      />
                    </div>
                  </div>
                </div>
              </UIUnitGroup>
            </div>
          </UIListContentBox.Header>
          <UIListContentBox.Body>
            <UIGrid
              type='default'
              rowData={rowData}
              columnDefs={columnDefs}
              moreMenuConfig={moreMenuConfig}
              onClickRow={(_params: any) => {}}
              onCheck={(_selectedIds: any[]) => {}}
            />
          </UIListContentBox.Body>
          <UIListContentBox.Footer>
            <UIPagination currentPage={1} totalPages={10} onPageChange={() => {}} className='flex justify-center' />
          </UIListContentBox.Footer>
        </UIListContainer>
      </UIArticle>
    </section>
  );
};
