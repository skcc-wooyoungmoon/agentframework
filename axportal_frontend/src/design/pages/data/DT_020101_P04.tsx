import React, { useState } from 'react';

import { UIDataCnt, UIPagination } from '@/components/UI';
import { UITypography, UIButton2 } from '@/components/UI/atoms';
import { UIImage } from '@/components/UI/atoms/UIImage';
import { UIPopupHeader, UIPopupFooter, UIArticle, UIPopupBody, UIFormField, UIStepper } from '@/components/UI/molecules';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';

import { UIGrid } from '../../../components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '../../../components/UI/molecules/list';
import { DesignLayout } from '../../components/DesignLayout';
import { UIUnitGroup } from '@/components/UI/molecules';

export const DT_020101_P04: React.FC = () => {
  const [isPopupOpen, setIsPopupOpen] = useState(true); // 팝업이므로 기본적으로 열려있음
  const [toolType, setToolType] = useState<string>('');

  // 스테퍼 데이터
  const stepperItems = [
    { step: 1, label: '데이터 정보입력' },
    { step: 2, label: '데이터 가져오기' },
    { step: 3, label: '선택 데이터 확인' },
    { step: 4, label: '프로세서 선택' },
  ];

  const handleClose = () => {
    setIsPopupOpen(false);
  };

  const handleToolTypeChange = (_value: string) => {
    setToolType(_value);
  };

  // 데이터 테이블 데이터
  const datasetData = [
    {
      id: '1',
      no: 1,
      name: '대출 상품 설명서 전문 파일 묶음',
      modelName: 'GPT-4 Turbo',
      description: '정기예금 등 기본 수신상품에 대한 고객 질문과 상담사 답변 데이터',
    },
    {
      id: '2',
      no: 2,
      name: '주택담보대출 상담 모델',
      modelName: 'Claude-3.5 Sonnet',
      description: '주택담보대출, 신용대출 등 대출 상품 상담 내용',
    },
    {
      id: '3',
      no: 3,
      name: '카드 상품 추천 모델',
      modelName: 'Gemini Pro',
      description: '고객 프로필별 카드 상품 추천 이력 데이터',
    },
    {
      id: '4',
      no: 4,
      name: '보험 청구 처리 모델',
      modelName: 'LLaMA 2',
      description: '보험 청구 접수부터 처리까지의 프로세스 데이터',
    },
    {
      id: '5',
      no: 5,
      name: '투자상품 분석 모델',
      modelName: 'GPT-3.5 Turbo',
      description: '펀드, 주식 등 투자상품의 과거 성과 및 위험도 데이터',
    },
    {
      id: '6',
      no: 6,
      name: '투자상품 분석 모델',
      modelName: 'GPT-3.5 Turbo',
      description: '펀드, 주식 등 투자상품의 과거 성과 및 위험도 데이터',
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
        minWidth: 392,
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
    ],
    []
  );

  const radioOptions = [
    // [ Case : 1 ]
    {
      value: 'quick_start',
      label: 'No',
      image: '/assets/images/data/ico-radio-data-number.svg',
      alt: 'No',
    },
    {
      value: 'System',
      label: 'System',
      image: '/assets/images/data/ico-radio-visual08.svg',
      alt: 'System',
    },
    {
      value: 'User',
      label: 'User',
      image: '/assets/images/data/ico-radio-visual06.svg',
      alt: 'User',
    },
    {
      value: 'Assistant',
      label: 'Assistant',
      image: '/assets/images/data/ico-radio-visual07.svg',
      alt: 'Assistant',
    },
  ];

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
            <UIPopupHeader title='학습 데이터세트 생성' position='left' />
            {/* 레이어 팝업 바디 */}
            <UIPopupBody>
              <UIArticle>
                <UIStepper currentStep={4} items={stepperItems} direction='vertical' />
              </UIArticle>
            </UIPopupBody>
            {/* 레이어 팝업 footer */}
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <UIButton2 className='btn-tertiary-gray' style={{ width: '80px' }}>
                    취소
                  </UIButton2>
                  <UIButton2 className='btn-tertiary-blue' style={{ width: '80px' }}>
                    만들기
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
          <UIPopupHeader title='프로세서 선택' description='' position='right' />
          {/* 레이어 팝업 바디 */}
          <UIPopupBody>
            <UIArticle className='article-grid'>
              <UIListContainer>
                <UIListContentBox.Header>
                  <div className='flex items-center'>
                    <div style={{ width: '182px', paddingRight: '8px' }}>
                      <UIDataCnt count={datasetData.length} prefix='프로세서 총' unit='건' />
                    </div>
                  </div>
                </UIListContentBox.Header>
                <UIListContentBox.Body>
                  <UIGrid
                    type='multi-select'
                    rowData={datasetData}
                    columnDefs={columnDefs}
                    onClickRow={(_params: any) => {}}
                  />
                </UIListContentBox.Body>
                <UIListContentBox.Footer>
                  <UIPagination currentPage={1} totalPages={1} onPageChange={() => {}} className='flex justify-center' />
                </UIListContentBox.Footer>
              </UIListContainer>
            </UIArticle>
            <UIArticle>
              {/* [251120_퍼블수정] 간격 수정 */}
              <UIFormField gap={16} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                  중복제거할 항목
                </UITypography>
                <div className='flex gap-6'>
                  {radioOptions.slice(0, 4).map(option => (
                    <div key={option.value} className='flex flex-col space-y-3'>
                      {/* 라디오 카드 */}
                      {/* [251104_퍼블수정] bg-gray-50 클래스를 bg-col-gray-100 이렇게 변경 */}
                      <div
                        className={`w-[298px] h-[200px] rounded-[20px] p-6 cursor-pointer transition-all duration-200 flex items-center justify-center ${
                          toolType === option.value ? 'bg-[#f3f6fb] border-2 border-[#005df9]' : 'bg-col-gray-100 border-2 border-transparent hover:bg-gray-100'
                        }`}
                        onClick={() => handleToolTypeChange(option.value)}
                      >
                        <UIImage src={option.image} alt={option.alt} className='max-w-full max-h-full' />
                      </div>
                      {/* 텍스트 영역 - 카드 밑으로 분리 */}
                      <div className='space-y-1'>
                        <div className='flex items-center gap-2'>
                          <UITypography variant='body-1' className='secondary-neutral-800'>
                            {option.label}
                          </UITypography>
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              </UIFormField>
            </UIArticle>
          </UIPopupBody>
          {/* 레이어 팝업 footer */}
          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <UIButton2 className='btn-secondary-gray'>이전</UIButton2>
              </UIUnitGroup>
            </UIArticle>
          </UIPopupFooter>
        </section>
      </UILayerPopup>
    </>
  );
};
