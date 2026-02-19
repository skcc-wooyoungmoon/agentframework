import React, { useMemo, useState } from 'react';

import { UIButton2, UIDataCnt, UITypography } from '@/components/UI/atoms';
import { UIPagination } from '@/components/UI/atoms/UIPagination';
import { UIArticle, UIPopupBody, UIPopupFooter, UIPopupHeader, UIStepper, UIUnitGroup, type UIStepperItem } from '@/components/UI/molecules';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIInput } from '@/components/UI/molecules/input';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';

import { DesignLayout } from '../../components/DesignLayout';

// import { useModal } from '@/stores/common/modal';

// 스테퍼 데이터
const stepperItems: UIStepperItem[] = [
  {
    id: 'step1',
    label: '반입 모델 선택',
    step: 1,
  },
  {
    id: 'step2',
    label: '모델 정보 입력',
    step: 2,
  },
];

export const MD_050101_P02: React.FC = () => {
  const [isPopupOpen, setIsPopupOpen] = useState(true);
  // const { openAlert } = useModal();

  const handleClose = () => {
    setIsPopupOpen(false);
  };

  // search 타입
  const [searchValue1, setSearchValue1] = useState('');

  // UIGrid 컬럼 설정
  /* [251120_퍼블수정] 속성값 수정 */
  const columnDefs: any = useMemo(
    () => [
      {
        headerName: 'NO',
        field: 'no',
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
      },
      /* 251107_퍼블 텍스트값 수정 */
      {
        headerName: '모델명',
        field: 'name',
        width: 272,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      // [251113_퍼블수정] 그리드 컬럼 속성 수정
      {
        headerName: '설명',
        field: 'description',
        minWidth: 392,
        flex: 1,
        showTooltip: true,
        cellStyle: {
          paddingLeft: '16px',
        },
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
        field: 'version',
        width: 140,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
    ],
    []
  );

  // UIGrid 로우 데이터
  /* [251120_퍼블수정] 속성값 수정 */
  const rowData = [
    {
      no: 1,
      id: '1',
      name: 'GPT-4o',
      description: 'OpenAI의 최신 대화형 AI 모델입니다. 자연어 이해와 생성 능력이 뛰어나며, 다양한 업무에 활용 가능합니다.',
      version: '1.12GB',
    },
    {
      no: 2,
      id: '2',
      name: 'Claude-3.5 Sonnet',
      description: 'Anthropic의 고성능 AI 모델로, 창의적 글쓰기와 분석에 특화되어 있으며 안전성이 뛰어납니다.',
      version: '1.12GB',
    },
    {
      no: 3,
      id: '3',
      name: 'LiquidAI/LFM2-VL-1.6B',
      description: 'LiquidAI에서 개발한 비전-언어 멀티모달 모델로, 이미지와 텍스트를 함께 처리할 수 있습니다.',
      version: '1.12GB',
    },
  ];

  return (
    <>
      <DesignLayout
        initialMenu={{ id: 'home', label: '홈' }}
        initialSubMenu={{
          id: 'home-ide',
          label: 'IDE',
          icon: 'ico-lnb-menu-20-home',
        }}
      >
        <div className='flex items-center justify-center h-full'>
          <div className='text-center'>
            <UITypography variant='title-1' className='secondary-neutral-800 text-sb'>
              홈
            </UITypography>
            <UITypography variant='body-1' className='secondary-neutral-600'>
              IDE 생성...
            </UITypography>
          </div>
        </div>
      </DesignLayout>

      <UILayerPopup
        isOpen={isPopupOpen}
        onClose={handleClose}
        size='fullscreen'
        showOverlay={true}
        leftContent={
          <UIPopupAside>
            {/* 레이어 팝업 헤더 */}
            <UIPopupHeader title='모델 반입' description='' position='left' />

            {/* 레이어 팝업 바디 */}
            <UIPopupBody>
              <UIArticle>
                <UIStepper currentStep={1} items={stepperItems} direction='vertical' />
              </UIArticle>
            </UIPopupBody>

            {/* 레이어 팝업 footer */}
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <UIButton2 className='btn-aside-gray'>취소</UIButton2>
                  <UIButton2 className='btn-aside-blue' disabled={true}>
                    반입요청
                  </UIButton2>
                </UIUnitGroup>
              </UIArticle>
            </UIPopupFooter>
          </UIPopupAside>
        }
      >
        <section className='section-popup-content'>
          {/* 레이어 팝업 헤더 */}
          <UIPopupHeader title='반입 모델 선택' description='반입할 모델을 선택해주세요.' position='right' />

          {/* 레이어 팝업 바디 */}
          <UIPopupBody>
            <UIArticle className='article-grid'>
              <UIListContainer>
                <UIListContentBox.Header>
                  <div className='w-full'>
                    <UIUnitGroup gap={16} direction='column'>
                      <div className='flex justify-between w-full items-center'>
                        <div style={{ width: '168px', paddingRight: '8px', display: 'flex', alignItems: 'center' }}>
                          <UIDataCnt count={100} prefix='총' unit='건' />
                        </div>

                        <div style={{ width: '360px' }}>
                          {/* [251120_퍼블수정] 플레이스홀더 수정 */}
                          <UIInput.Search
                            value={searchValue1}
                            placeholder='모델명, 설명 입력'
                            onChange={e => {
                              setSearchValue1(e.target.value);
                            }}
                          />
                        </div>
                      </div>
                    </UIUnitGroup>
                  </div>
                </UIListContentBox.Header>
                <UIListContentBox.Body>
                  <UIGrid
                    type='single-select'
                    rowData={rowData}
                    columnDefs={columnDefs}
                    onClickRow={(_params: any) => {}}
                    onCheck={(_selectedIds: any[]) => {
                    }}
                  />
                </UIListContentBox.Body>
                <UIListContentBox.Footer>
                  <UIPagination currentPage={1} totalPages={5} onPageChange={() => {}} className='flex justify-center' />
                </UIListContentBox.Footer>
              </UIListContainer>
            </UIArticle>
          </UIPopupBody>

          {/* 레이어 팝업 footer */}
          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} align='start'>
                <UIButton2 className='btn-secondary-gray'>이전</UIButton2>
                <UIButton2 className='btn-secondary-blue'>다음</UIButton2>
              </UIUnitGroup>
            </UIArticle>
          </UIPopupFooter>
        </section>
      </UILayerPopup>
    </>
  );
};
