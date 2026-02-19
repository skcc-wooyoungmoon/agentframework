import React, { useState } from 'react';

import { UIDataCnt, UIPagination } from '@/components/UI';
import { UIButton2, UITypography } from '@/components/UI/atoms';
import { UIPopupBody, UIPopupFooter, UIPopupHeader, UIStepper, UIUnitGroup, UIInput } from '@/components/UI/molecules';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';

import { UIGrid } from '../../../components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '../../../components/UI/molecules/list';
import { DesignLayout } from '../../components/DesignLayout';

export const MD_050101_P05: React.FC = () => {
  const [isPopupOpen, setIsPopupOpen] = useState(true); // 팝업이므로 기본적으로 열려있음

  const [searchValue, setSearchValue] = useState('');

  const handleClose = () => {
    setIsPopupOpen(false);
  };

  // 프로젝트 테이블 데이터
  /* [251120_퍼블수정] 속성값 수정 */
  const projectData = [
    {
      id: '1',
      no: 1,
      projectName: 'LiquidAI/LFM2-VL-1.6B',
      size: '14GB',
    },
    {
      id: '2',
      no: 2,
      projectName: 'LiquidAI/LFM2-VL-1.6B',
      size: '14GB',
    },
    {
      id: '3',
      no: 3,
      projectName: 'LiquidAI/LFM2-VL-1.6B',
      size: '14GB',
    },
    {
      id: '4',
      no: 4,
      projectName: 'LiquidAI/LFM2-VL-1.6B',
      size: '14GB',
    },
    {
      id: '5',
      no: 5,
      projectName: 'LiquidAI/LFM2-VL-1.6B',
      size: '14GB',
    },
    {
      id: '6',
      no: 6,
      projectName: 'LiquidAI/LFM2-VL-1.6B',
      size: '14GB',
    },
    {
      id: '7',
      no: 7,
      projectName: 'LiquidAI/LFM2-VL-1.6B',
      size: '14GB',
    },
    {
      id: '8',
      no: 8,
      projectName: 'LiquidAI/LFM2-VL-1.6B',
      size: '14GB',
    },
    {
      id: '9',
      no: 9,
      projectName: 'LiquidAI/LFM2-VL-1.6B',
      size: '14GB',
    },
    {
      id: '10',
      no: 10,
      projectName: 'LiquidAI/LFM2-VL-1.6B',
      size: '14GB',
    },
    {
      id: '11',
      no: 11,
      projectName: 'LiquidAI/LFM2-VL-1.6B',
      size: '14GB',
    },
    {
      id: '12',
      no: 12,
      projectName: 'LiquidAI/LFM2-VL-1.6B',
      size: '14GB',
    },
  ];

  // 그리드 컬럼 정의
  /* 251107_퍼블 컬럼 속성값 수정 */
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
      // [251120_퍼블수정] 그리드 컬럼 속성 수정
      {
        headerName: '모델명',
        field: 'projectName' as const,
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
      {
        headerName: '크기',
        field: 'size' as const,
        width: 150,
        cellStyle: { paddingLeft: '16px' },
      },
    ],
    []
  );

  return (
    <>
      {/* DesignLayout 기본 구조 */}
      <DesignLayout
        initialMenu={{ id: 'model', label: '모델' }}
        initialSubMenu={{
          id: 'model-catalog',
          label: '모델 카탈로그',
          icon: 'ico-lnb-menu-20-model-catalog',
        }}
      >
        <div className='flex items-center justify-center h-full'>
          <div className='text-center'>
            <UITypography variant='title-1' className='secondary-neutral-800 text-sb'>
              모델 카탈로그
            </UITypography>
            <UITypography variant='body-1' className='secondary-neutral-600'>
              파인튜닝 등록 진행 중...
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
            <UIPopupHeader title='모델 검색' position='left' />
            {/* 레이어 팝업 바디 */}
            <UIPopupBody>
              {/* 스테퍼 영역 */}
              <UIStepper
                items={[
                  { id: 'step1', step: 1, label: '모델 검색 및 선택' },
                  { id: 'step2', step: 2, label: '모델 정보 확인' },
                ]}
                currentStep={1}
                direction='vertical'
              />
            </UIPopupBody>
            {/* 레이어 팝업 footer */}
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <UIButton2 className='btn-tertiary-gray' style={{ width: '80px' }}>
                    취소
                  </UIButton2>
                  <UIButton2 className='btn-tertiary-blue' style={{ width: '80px' }} disabled>
                    추가
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
          <UIPopupHeader title='모델 검색 및 선택' description='Reservoir의 self-hosting 모델 목록 중에서 모델 탐색에 추가할 모델을 검색하여 선택해주세요.' position='right' />
          {/* [251111_퍼블수정] 타이틀명칭 변경 : 모델 가든 > 모델 탐색 */}
          {/* 레이어 팝업 바디 */}
          <UIPopupBody>
            <UIArticle className='article-grid'>
              <UIListContainer>
                <UIListContentBox.Header>
                  <div className='flex-shrink-0'>
                    <div style={{ width: '168px', paddingRight: '8px' }}>
                      <UIDataCnt count={projectData.length} prefix='총' unit='건' />
                    </div>
                  </div>

                  <div style={{ display: 'flex', marginLeft: 'auto', gap: '0 12px' }}>
                    <div style={{ width: '360px', flexShrink: 0 }}>
                      {/* 251107_퍼블 속성값 수정 */}
                      <UIInput.Search
                        value={searchValue}
                        placeholder='모델명 입력'
                        onChange={e => {
                          setSearchValue(e.target.value);
                        }}
                      />
                    </div>
                  </div>
                </UIListContentBox.Header>
                <UIListContentBox.Body>
                  <UIGrid type='single-select' rowData={projectData} columnDefs={columnDefs} onClickRow={(_params: any) => {}} />
                </UIListContentBox.Body>
                <UIListContentBox.Footer>
                  <UIPagination currentPage={1} totalPages={1} onPageChange={() => {}} className='flex justify-center' />
                </UIListContentBox.Footer>
              </UIListContainer>
            </UIArticle>
          </UIPopupBody>
          {/* 레이어 팝업 footer */}
          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <UIButton2 className='btn-secondary-blue' style={{ width: '80px' }}>
                  다음
                </UIButton2>
              </UIUnitGroup>
            </UIArticle>
          </UIPopupFooter>
        </section>
      </UILayerPopup>
    </>
  );
};
