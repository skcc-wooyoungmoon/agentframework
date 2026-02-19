import React, { useMemo, useState } from 'react';

import { UIDataCnt, UIPagination } from '@/components/UI';
import { UITypography } from '@/components/UI/atoms';
import { UIArticle, UIUnitGroup } from '@/components/UI/molecules';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIList } from '@/components/UI/molecules/UIList';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { useDwAllAccounts } from '@/services/home/webide/ide.services';

/** IdeDwAccountListPopup Props 타입 */
interface IdeDwAccountListPopupProps {
  /** 팝업 내용만 렌더링 (UIModal 내부에서 사용 시) */
  asContent?: boolean;
}

/**
 * DW 계정 목록 팝업 컨텐츠
 * - 포탈 관리자 계정 목록 표시
 * - ITSM 시스템 권한 신청 안내
 */
export const IdeDwAccountListPopup: React.FC<IdeDwAccountListPopupProps> = ({ asContent = true }) => {
  // 현재 페이지
  const [currentPage, setCurrentPage] = useState(1);

  // 서비스 호출
  const { data: accountsData } = useDwAllAccounts();

  // 페이지당 아이템 수
  const pageSize = 12;

  // 데이터 가공
  const dwAccounts = useMemo(() => {
    if (!accountsData) return [];
    return accountsData.map((accountId, index) => ({
      no: index + 1,
      accountId,
    }));
  }, [accountsData]);

  // 현재 페이지의 아이템 목록
  const pagedItems = useMemo(() => {
    const startIndex = (currentPage - 1) * pageSize;
    return dwAccounts.slice(startIndex, startIndex + pageSize);
  }, [dwAccounts, currentPage, pageSize]);

  // 전체 페이지 수
  const totalPages = Math.ceil(dwAccounts.length / pageSize) || 1;

  // 그리드 컬럼 정의
  const columnDefs: any = useMemo(
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

  /**
   * 행 클릭 핸들러
   */
  const handleRowClick = (params: any) => {
    // 행 클릭 시 처리 로직
    console.log('Selected account:', params.data);
  };

  const content = (
    <>
      {/* 안내 메시지 */}
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
                      {`TADWDB에 대한 DB 접속 권한은 행내 ITSM 시스템을 통해 신청하실 수 있습니다. 해당 시스템에서 'DB 접속 권한 신청서'를 작성 후 제출해주세요.`}
                    </UITypography>
                  ),
                },
              ]}
            />
          </UIUnitGroup>
        </div>
      </UIArticle>

      {/* 계정 목록 그리드 */}
      <UIArticle className='article-grid'>
        <UIListContainer>
          <UIListContentBox.Header>
            <div className='flex justify-between items-center w-full'>
              <div className='flex-shrink-0'>
                <div style={{ width: '168px', paddingRight: '8px' }}>
                  <UIDataCnt count={dwAccounts.length} prefix='총' unit='건' />
                </div>
              </div>
            </div>
          </UIListContentBox.Header>
          <UIListContentBox.Body>
            <UIGrid type='default' rowData={pagedItems} columnDefs={columnDefs} onClickRow={handleRowClick} />
          </UIListContentBox.Body>
          <UIListContentBox.Footer>
            <UIPagination currentPage={currentPage} totalPages={totalPages} onPageChange={setCurrentPage} className='flex justify-center' />
          </UIListContentBox.Footer>
        </UIListContainer>
      </UIArticle>
    </>
  );

  // asContent가 true이면 section 없이 내용만 반환 (UIModal 내부 사용 시)
  if (asContent) {
    return <>{content}</>;
  }

  // 전체 섹션으로 감싸서 반환
  return <section className='section-modal'>{content}</section>;
};
