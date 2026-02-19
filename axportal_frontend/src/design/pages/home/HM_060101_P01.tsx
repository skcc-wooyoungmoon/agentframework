import React, { useState } from 'react';

import { UIDataCnt, UIPagination } from '@/components/UI';
import { UIButton2, UITypography, UILabel, UIIcon2 } from '@/components/UI/atoms';
import { UIInput } from '@/components/UI/molecules';
import { UIArticle, UIPopupBody, UIPopupFooter, UIPopupHeader, UIUnitGroup } from '@/components/UI/molecules';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { DesignLayout } from '@/design/components/DesignLayout';

const modelData = [
  {
    id: '1',
    no: 1,
    toolName: 'Jupyter Notebook',
    imageName: 'jupyter-notebook-image-v1-notebook-image-v1',
    status: '사용 가능',
    dwAccount: 'DW_USER01',
    remainingTime: '23시간 59분 (2026.01.10 18:23:43)',
  },
  {
    id: '2',
    no: 2,
    toolName: 'VS Code',
    imageName: 'vscode-python-v2',
    status: '생성중',
    dwAccount: 'DW_USER01',
    remainingTime: '23시간 59분 (2026.01.10 18:23:43)',
  },
  {
    id: '3',
    no: 3,
    toolName: 'Jupyter Lab',
    imageName: 'jupyter-lab-v3',
    status: '사용 가능',
    dwAccount: 'DW_USER01',
    remainingTime: '23시간 59분 (2026.01.10 18:23:43)',
  },
  {
    id: '4',
    no: 4,
    toolName: 'VS Code',
    imageName: 'vscode-default-v1',
    status: '생성실패',
    dwAccount: 'DW_USER01',
    remainingTime: '23시간 59분 (2026.01.10 18:23:43)',
  },
  {
    id: '5',
    no: 5,
    toolName: 'Jupyter Notebook',
    imageName: 'jupyter-notebook-image-v2',
    status: '사용 가능',
    dwAccount: 'DW_USER01',
    remainingTime: '23시간 59분 (2026.01.10 18:23:43)',
  },
  {
    id: '6',
    no: 6,
    toolName: 'VS Code',
    imageName: 'vscode-python-v3',
    status: '생성중',
    dwAccount: 'DW_USER01',
    remainingTime: '23시간 59분 (2026.01.10 18:23:43)',
  },
];

export const HM_060101_P01: React.FC = () => {
  const [isPopupOpen, setIsPopupOpen] = useState(true); // 팝업이므로 기본적으로 열려있음
  // search 타입
  const [searchValue, setSearchValue] = useState('');

  // 그리드 선택 상태 (라디오는 단일 선택)
  const [selectedId, _] = useState<string>('');

  const handleClose = () => {
    setIsPopupOpen(false);
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
        field: 'toolName' as const,
        width: 160,
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
        headerName: '이미지명',
        field: 'imageName' as const,
        width: 200,
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
        field: 'status' as const,
        width: 120,
        cellRenderer: React.memo((params: any) => {
          const getStatusIntent = (status: string) => {
            switch (status) {
              case '사용 가능':
                return 'complete';
              case '생성중':
                return 'progress';
              case '생성실패':
                return 'error';
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
        headerName: 'DW 계정',
        field: 'dwAccount' as const,
        minWidth: 120,
        flex: 1,
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
        headerName: '남은 시간',
        field: 'remainingTime' as const,
        width: 260,
      },
      {
        headerName: '사용 기간 연장',
        field: 'usageExtension' as const,
        width: 140,
        cellRenderer: React.memo((params: any) => {
          return (
            <UIButton2 className='btn-text-14-underline-point' disabled={params.data.status !== '사용 가능'} onClick={() => console.log('사용 기간 연장:', params.data.id)}>
              사용 기간 연장
            </UIButton2>
          );
        }),
      },
      {
        headerName: '사용 종료',
        field: 'usageEnd' as const,
        width: 140,
        cellRenderer: React.memo((params: any) => {
          return (
            <UIButton2 className='btn-text-14-underline-point' disabled={params.data.status !== '사용 가능'} onClick={() => console.log('사용 종료:', params.data.id)}>
              사용 종료
            </UIButton2>
          );
        }),
      },
    ],
    [selectedId]
  );

  return (
    <>
      {/* DesignLayout 기본 구조 */}
      <DesignLayout
        initialMenu={{ id: 'data', label: '데이터' }}
        initialSubMenu={{
          id: 'data-tools',
          label: '데이터도구',
          icon: 'ico-lnb-menu-20-data-storage',
        }}
      >
        <div className='flex items-center justify-center h-full'>
          <div className='text-center'>
            <UITypography variant='title-1' className='secondary-neutral-800 text-sb'>
              IDE 이동
            </UITypography>
            <UITypography variant='body-1' className='secondary-neutral-600'>
              Ingestion Tool 만들기 진행 중...
            </UITypography>
          </div>
        </div>
      </DesignLayout>

      {/* DesignLayout 위에 높은 z-index로 뜨는 UILayerPopup */}
      <UILayerPopup
        isOpen={isPopupOpen}
        onClose={handleClose}
        size='fullscreen'
        showOverlay={true}
        leftContent={
          /* 좌측 Step 영역 콘텐츠 */
          <UIPopupAside>
            {/* 레이어 팝업 헤더 */}
            <UIPopupHeader title='IDE 이동' position='left' />
            {/* 레이어 팝업 바디 */}
            <UIPopupBody></UIPopupBody>
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <UIButton2 className='btn-tertiary-gray' style={{ width: 80 }}>
                    취소
                  </UIButton2>
                  <UIButton2 className='btn-tertiary-blue' style={{ width: 80 }}>
                    이동
                  </UIButton2>
                </UIUnitGroup>
              </UIArticle>
            </UIPopupFooter>
          </UIPopupAside>
        }
      >
        {/* 우측 Contents 영역 콘텐츠 - 기존 컴포넌트 사용 */}
        <section className='section-popup-content'>
          {/* 레이어 팝업 헤더 */}
          <UIPopupHeader title='IDE 이동' description='기존에 생성된 IDE를 선택하여 이동하거나, [IDE 추가] 버튼을 통해 새로운 IDE를 생성해보세요.' position='right' />

          {/* 레이어 팝업 바디 */}
          <UIPopupBody>
            <UIArticle className='article-grid'>
              <UIListContainer>
                <UIListContentBox.Header>
                  <div className='flex flex-shrink-0 gap-1 items-center'>
                    <div style={{ width: 'auto', paddingRight: '8px' }}>
                      <UIDataCnt count={modelData.length} prefix='보유한 IDE 총' unit='건' />
                    </div>
                    <UITypography variant='body-2' className='secondary-neutral-600'>
                      Jupyter Notebook 최대 $N$개, VS Code 최대 $N$개까지 생성할 수 있습니다.
                    </UITypography>
                  </div>
                  <div className='flex gap-3 flex-shrink-0'>
                    <div>
                      <UIButton2 className='btn-tertiary-outline line-only-blue' onClick={() => {}}>
                        IDE 추가
                      </UIButton2>
                    </div>
                    <div className='w-[360px]'>
                      <UIInput.Search
                        value={searchValue}
                        onChange={e => {
                          setSearchValue(e.target.value);
                        }}
                        placeholder='이미지명, DW계정 입력'
                      />
                    </div>
                  </div>
                </UIListContentBox.Header>
                <UIListContentBox.Body>
                  <UIGrid type='single-select' rowData={modelData} columnDefs={columnDefs} onClickRow={(_params: any) => {}} />
                </UIListContentBox.Body>
                <UIListContentBox.Footer>
                  <UIPagination currentPage={1} totalPages={3} onPageChange={() => {}} className='flex justify-center' />
                </UIListContentBox.Footer>
              </UIListContainer>
            </UIArticle>
            <UIArticle>
              <div className='box-fill'>
                <div style={{ display: 'flex', alignItems: 'center', gap: '0 6px' }}>
                  <UIIcon2 className='ic-system-16-info-gray' />
                  <UITypography variant='body-2' className='secondary-neutral-600'>
                    생성에 실패한 IDE의 경우, 사용 종료 버튼을 클릭하여 삭제하실 수 있습니다.
                  </UITypography>
                </div>
              </div>
            </UIArticle>
          </UIPopupBody>
        </section>
      </UILayerPopup>
    </>
  );
};
