import React, { useState } from 'react';

import { UIIcon2, UISlider, UIToggle, UITooltip, UIButton2, UITypography } from '@/components/UI/atoms';
import { UIStepper, UIUnitGroup, UIPopupFooter, UIPopupHeader, UIPopupBody, UIFormField, UIInput, type UIStepperItem } from '@/components/UI/molecules';
import { UIArticle } from '@/components/UI/molecules';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';

import { DesignLayout } from '../../components/DesignLayout';

export const MD_030101_P05: React.FC = () => {
  const [isPopupOpen, setIsPopupOpen] = useState(true); // 팝업이므로 기본적으로 열려있음

  // 폼 상태
  const [learningEpochs, setLearningEpochs] = useState('1');
  const [validationRatio, setValidationRatio] = useState(0.2);
  const [learningRate, setLearningRate] = useState('0.0001');
  const [batchSize, setBatchSize] = useState('1');
  const [earlyStop, setEarlyStop] = useState(true);
  const [patience, setPatience] = useState('3');

  const handleClose = () => {
    setIsPopupOpen(false);
  };

  const handleCancel = () => {
    handleClose();
  };

  const handleNext = () => {
    // 다음 단계로 이동 로직
  };

  // 스테퍼 데이터
  const stepperItems: UIStepperItem[] = [
    {
      id: 'step1',
      label: '모델 선택',
      step: 1,
    },
    {
      id: 'step2',
      label: '기본 정보 입력',
      step: 2,
    },
    {
      id: 'step3',
      label: '자원 할당',
      step: 3,
    },
    {
      id: 'step4',
      label: '학습 데이터세트 선택',
      step: 4,
    },
    {
      id: 'step5',
      label: '파라미터 설정',
      step: 5,
    },
    {
      id: 'step6',
      label: '입력정보 확인',
      step: 6,
    },
  ];

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
              데이터 도구
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
            {/* 파인튜닝 등록 제목 */}
            <UIPopupHeader title='파인튜닝 등록' description='' position='left' />
            <UIPopupBody>
              <UIArticle>
                <UIStepper currentStep={5} items={stepperItems} direction='vertical' />
              </UIArticle>
            </UIPopupBody>
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <UIButton2 className='btn-tertiary-gray' style={{ width: '80px' }} onClick={handleCancel}>
                    취소
                  </UIButton2>
                  <UIButton2 className='btn-tertiary-blue' style={{ width: '80px' }} onClick={handleNext} disabled={true}>
                    튜닝시작
                  </UIButton2>
                </UIUnitGroup>
              </UIArticle>
            </UIPopupFooter>
          </UIPopupAside>
        }
      >
        {/* 우측 Contents 영역 콘텐츠 */}
        <section className='section-popup-content'>
          {/* 헤더 영역 */}
          <UIPopupHeader title='파라미터 설정' description='' position='right' />

          {/* 폼 컨테이너 */}
          <UIPopupBody>
            {/* 학습 횟수 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                  Epochs
                </UITypography>
                <UIInput.Text value={learningEpochs} placeholder='1' onChange={e => setLearningEpochs(e.target.value)} />
              </UIFormField>
            </UIArticle>

            {/* 검증비율 */}
            {/* [251110_퍼블수정] 슬라이더 속성값 재정의 */}
            <UIArticle>
              <UISlider
                label='Validation Split'
                required={true}
                value={validationRatio}
                min={0}
                max={10}
                onChange={setValidationRatio}
                startLabel='0'
                endLabel='1'
                width='100%'
                showTextField={true}
                textFieldWidth='w-32'
                decimalPlaces={1}
                step={0.1} // [251128_퍼블수정] : 소수점일 경우 step 값 추가 하여 0.1 단위로 증가
              />
            </UIArticle>

            {/* 학습률 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                  Learning Rate
                </UITypography>
                <UIInput.Text value={learningRate} placeholder='0.0001' onChange={e => setLearningRate(e.target.value)} />
              </UIFormField>
            </UIArticle>

            {/* 배치 사이즈 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                  Batch Size
                </UITypography>
                <UIInput.Text value={batchSize} placeholder='1' onChange={e => setBatchSize(e.target.value)} />
              </UIFormField>
            </UIArticle>

            {/* 조기 종료 */}
            <UIArticle>
              <UIToggle label='Early Stopping' labelPosition='top' checked={earlyStop} onChange={setEarlyStop} variant='basic' size='medium' />
            </UIArticle>

            {/* 조기 종료 인내도 (조건부 표시) */}
            {earlyStop && (
              <UIArticle>
                <UIFormField gap={8} direction='column'>
                  <div className='flex items-start'>
                    <UITypography variant='title-4' className='secondary-neutral-800 text-sb'>
                      Early Stopping Patience
                    </UITypography>
                    <UITooltip
                      trigger='click'
                      position='bottom-start'
                      type='notice'
                      title=''
                      items={['조기 종료 인내도는 입력한 학습횟수까지만 적용됩니다.']}
                      bulletType='default'
                      showArrow={false}
                      showCloseButton={true}
                      className='ml-1'
                    >
                      <UIButton2>
                        <UIIcon2 className='ic-system-20-info' />
                      </UIButton2>
                    </UITooltip>
                  </div>
                  <UIInput.Text value={patience} placeholder='3' onChange={e => setPatience(e.target.value)} />
                </UIFormField>
              </UIArticle>
            )}
          </UIPopupBody>

          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <UIButton2 className='btn-secondary-gray' style={{ width: '80px' }} onClick={handleCancel}>
                  이전
                </UIButton2>
                <UIButton2 className='btn-secondary-blue' style={{ width: '80px' }} onClick={handleNext}>
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
