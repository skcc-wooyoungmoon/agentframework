import React from 'react';

import { UIDataCnt, UIPagination } from '@/components/UI';
import { UIArticle } from '@/components/UI/molecules/UIArticle';

import { UIGrid } from '../../../components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '../../../components/UI/molecules/list';
import { UIList } from '@/components/UI/molecules/UIList';
import { UITypography } from '@/components/UI/atoms';
import { UIUnitGroup } from '@/components/UI/molecules';

// HM_060101_P06 페이지
export const HM_060101_P06: React.FC = () => {
  // 데이터
  const projectData = [
    {
      id: '1',
      no: 1,
      accountId: 'DW_USER01',
    },
    {
      id: '2',
      no: 2,
      accountId: 'DW_USER02',
    },
    {
      id: '3',
      no: 3,
      accountId: 'DW_USER03',
    },
    {
      id: '4',
      no: 4,
      accountId: 'DW_USER04',
    },
    {
      id: '5',
      no: 5,
      accountId: 'DW_USER05',
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
        headerName: '계정 ID',
        field: 'accountId' as const,
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
    ],
    []
  );

  return (
    <section className='section-modal'>
      <UIArticle>
        <div className='box-fill'>
          <UIUnitGroup gap={8} direction='column' align='start'>
            <UIList
              gap={4}
              direction='column'
              className='ui-list_bullet'
              data={[
                {
                  dataItem: (
                    <UITypography variant='body-2' className='secondary-neutral-600'>
                      {`현재 이용 가능한 계정이 없습니다. 포탈에서 제공하는 계정 목록을 확인한 후, 행내 ITSM 시스템에서 권한 신청을 진행해 주세요.`}
                    </UITypography>
                  ),
                },
              ]}
            />
            <UIList
              gap={4}
              direction='column'
              className='ui-list_bullet'
              data={[
                {
                  dataItem: (
                    <UITypography variant='body-2' className='secondary-neutral-600'>
                      {`TADWDB에 대한 DB 접속 권한은 행내 ITSM 시스템을 통해 신청하실 수 있습니다. 해당 시스템에서 ‘DB 접속 권한 신청서’를 작성 후 제출해주세요.`}
                    </UITypography>
                  ),
                },
              ]}
            />
          </UIUnitGroup>
        </div>
      </UIArticle>
      <UIArticle className='article-grid'>
        <UIListContainer>
          <UIListContentBox.Header>
            <div className='flex justify-between items-center w-full'>
              <div className='flex-shrink-0'>
                <div style={{ width: '168px', paddingRight: '8px' }}>
                  <UIDataCnt count={projectData.length} prefix='총' unit='건' />
                </div>
              </div>
            </div>
          </UIListContentBox.Header>
          <UIListContentBox.Body>
            <UIGrid type='default' rowData={projectData} columnDefs={columnDefs} onClickRow={(_params: any) => {}} onCheck={(_selectedIds: any[]) => {}} />
          </UIListContentBox.Body>
          <UIListContentBox.Footer>
            <UIPagination currentPage={1} totalPages={3} onPageChange={() => {}} className='flex justify-center' />
          </UIListContentBox.Footer>
        </UIListContainer>
      </UIArticle>
    </section>
  );
};
