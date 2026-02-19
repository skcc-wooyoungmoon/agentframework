import React from 'react';
import { UIArticle, UIUnitGroup } from '@/components/UI/molecules';
import { UIGrid } from '../../../components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '../../../components/UI/molecules/list';
import { UIDataCnt, UIPagination, UILabel } from '@/components/UI';
import { UIButton2 } from '@/components/UI/atoms/UIButton2';
import { useToast } from '@/hooks/common/toast/useToast';

export const HM_010101_P04 = () => {
  // 데이터
  const projectData = [
    {
      no: 1,
      name: 'Jupyter Notebook',
      status: '사용중',
    },
    {
      no: 2,
      name: 'VS Code',
      status: '미사용',
    },
  ];

  const { toast } = useToast();

  const handleCopy = (message: string) => {
    toast(message);
    toast.success(message);
    toast.error(message);
  };

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
        headerName: '도구명',
        field: 'name' as const,
        width: 340,
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
        headerName: '상태',
        field: 'status',
        flex: 1,
        cellRenderer: React.memo((params: any) => {
          const getStatusIntent = (status: string) => {
            switch (status) {
              case '사용중':
                return 'complete';
              case '미사용':
                return 'progress';
              case '실패':
                return 'error';
              case '취소':
                return 'stop';
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
    ],
    []
  );

  return (
    <div className='flex h-full'>
      <UIArticle className='article-grid w-full'>
        <UIListContainer>
          <UIListContentBox.Header>
            <div className='flex justify-between items-center w-full'>
              <div className='flex-shrink-0'>
                <div style={{ width: '168px', paddingRight: '8px' }}>
                  <UIDataCnt count={projectData.length} prefix='총' unit='건' />
                </div>
              </div>
              <div className='flex justify-end w-full'>
                <UIUnitGroup gap={8} direction='row' align='end'>
                  <UIButton2 className='btn-tertiary-outline' onClick={() => handleCopy('토스트 팝업입니다. 최대 글자수는 20글자')}>
                    Access Token 복사
                  </UIButton2>
                  <UIButton2 className='btn-tertiary-outline'>Refresh Token 복사</UIButton2>
                </UIUnitGroup>
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
    </div>
  );
};
