import { useAtom } from 'jotai';
import React from 'react';

import { UIButton2, UIIcon2, UISlider, UIToggle, UITooltip, UITypography } from '@/components/UI/atoms';
import { UIArticle, UIFormField, UIInput, UIPopupBody, UIPopupFooter, UIPopupHeader, UIStepper, UIUnitGroup, type UIStepperItem } from '@/components/UI/molecules';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';

import { useCommonPopup } from '@/hooks/common/popup/useCommonPopup';
import {
  fineTuningBatchSizeAtom,
  fineTuningEarlyStopAtom,
  fineTuningLearningEpochsAtom,
  fineTuningLearningRateAtom,
  fineTuningPatienceAtom,
  fineTuningValidationRatioAtom,
  fineTuningValidationRatioTextAtom,
  resetAllFineTuningDataAtom,
} from '@/stores/model/fineTuning/fineTuning.atoms';

interface LayerPopupProps {
  currentStep: number;
  stepperItems?: UIStepperItem[];
  onNextStep: () => void;
  onPreviousStep: () => void;
  onClose: () => void;
}

export const ModelFineTuningCreate05StepPopupPage: React.FC<LayerPopupProps> = ({ currentStep, stepperItems = [], onClose, onNextStep, onPreviousStep }) => {
  // useModal 훅
  const { showCancelConfirm } = useCommonPopup();

  // Jotai 상태 관리
  const [learningEpochs, setLearningEpochs] = useAtom(fineTuningLearningEpochsAtom);
  const [validationRatio, setValidationRatio] = useAtom(fineTuningValidationRatioAtom);
  const [validationRatioText, setValidationRatioText] = useAtom(fineTuningValidationRatioTextAtom);
  const [learningRate, setLearningRate] = useAtom(fineTuningLearningRateAtom);
  const [batchSize, setBatchSize] = useAtom(fineTuningBatchSizeAtom);
  const [earlyStop, setEarlyStop] = useAtom(fineTuningEarlyStopAtom);
  const [patience, setPatience] = useAtom(fineTuningPatienceAtom);
  const [, resetAllData] = useAtom(resetAllFineTuningDataAtom);

  // Validation Split 슬라이더 변경 핸들러
  const handleValidationRatioChange = (value: number) => {
    // 슬라이더 값(0~10)을 실제 값(0~1)으로 변환
    const flooredValue = Math.floor(value * 10) / 10;
    const clampedValue = Math.max(0, Math.min(1, flooredValue));
    setValidationRatio(clampedValue);
    setValidationRatioText(clampedValue.toString());
  };

  // 정수만 입력 가능한 핸들러 (Epochs, Batch Size, Patience) - 최소값 1
  const handleIntegerInput = (value: string, setter: (value: string) => void) => {
    // 숫자만 허용 (빈 문자열도 허용)
    if (value === '' || /^\d+$/.test(value)) {
      setter(value);
    }
  };

  // 정수 입력 필드의 blur 핸들러 - 최소값 1 검증
  const handleIntegerBlur = (value: string, setter: (value: string) => void) => {
    const numValue = parseInt(value, 10);
    if (isNaN(numValue) || numValue < 1) {
      setter('1');
    }
  };

  // 소수점 4자리까지 입력 가능한 핸들러 (Learning Rate) - 최소값 0
  const handleDecimalInput = (value: string, setter: (value: string) => void) => {
    // 소수점 4자리까지 허용
    if (value === '' || /^\d*\.?\d{0,4}$/.test(value)) {
      setter(value);
    }
  };

  // 소수점 입력 필드의 blur 핸들러 - 최소값 0 검증
  const handleDecimalBlur = (value: string, setter: (value: string) => void) => {
    const numValue = parseFloat(value);
    if (isNaN(numValue) || numValue < 0) {
      setter('0');
    }
  };

  const handleNext = () => {
    // inputbox 값을 최종적으로 validationRatio에 저장
    const numValue = parseFloat(validationRatioText);
    if (!isNaN(numValue) && numValue >= 0 && numValue <= 1) {
      setValidationRatio(numValue);
    }

    // 최소값 검증 및 조정
    const epochsValue = parseInt(learningEpochs, 10);
    if (isNaN(epochsValue) || epochsValue < 1) {
      setLearningEpochs('1');
    }

    const rateValue = parseFloat(learningRate);
    if (isNaN(rateValue) || rateValue < 0) {
      setLearningRate('0');
    }

    const batchValue = parseInt(batchSize, 10);
    if (isNaN(batchValue) || batchValue < 1) {
      setBatchSize('1');
    }

    if (earlyStop) {
      const patienceValue = parseInt(patience, 10);
      if (isNaN(patienceValue) || patienceValue < 1) {
        setPatience('1');
      }
    }

    onNextStep();
  };

  // 취소 핸들러 - 확인 알러트 후 상태 초기화
  const handleCancel = () => {
    showCancelConfirm({
      onConfirm: () => {
        // 모든 상태값 초기화 (Jotai 공통 함수 사용)
        resetAllData();
      },
    });
  };

  // 스테퍼 데이터 (props에서 받거나 기본값 사용)
  const defaultStepperItems: UIStepperItem[] = [
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
      label: '데이터세트 선택',
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
      {/* DesignLayout 위에 높은 z-index로 뜨는 UILayerPopup */}
      <UILayerPopup
        isOpen={currentStep === 5}
        onClose={onClose}
        size='fullscreen'
        showOverlay={true}
        leftContent={
          /* 좌측 Step 영역 콘텐츠 */
          <UIPopupAside>
            {/* 파인튜닝 등록 제목 */}
            <UIPopupHeader title='파인튜닝 등록' description='' position='left' />
            <UIPopupBody>
              <UIArticle>
                <UIStepper currentStep={currentStep} items={stepperItems.length > 0 ? stepperItems : defaultStepperItems} direction='vertical' />
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
                <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb' required={true}>
                  Epochs
                </UITypography>
                <UIInput.Text
                  value={learningEpochs}
                  placeholder='1'
                  onChange={e => handleIntegerInput(e.target.value, setLearningEpochs)}
                  onBlur={() => handleIntegerBlur(learningEpochs, setLearningEpochs)}
                />
              </UIFormField>
            </UIArticle>

            {/* 검증비율 */}
            <UIArticle>
              <UISlider
                label='Validation Split'
                required={true}
                value={validationRatio}
                min={0}
                max={1}
                onChange={handleValidationRatioChange}
                startLabel='0'
                endLabel='1'
                width='100%'
                showTextField={true}
                textFieldWidth='w-32'
                decimalPlaces={1}
                step={0.1}
              />
            </UIArticle>

            {/* 학습률 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb' required={true}>
                  Learning Rate
                </UITypography>
                <UIInput.Text
                  value={learningRate}
                  placeholder='0.0001'
                  onChange={e => handleDecimalInput(e.target.value, setLearningRate)}
                  onBlur={() => handleDecimalBlur(learningRate, setLearningRate)}
                />
              </UIFormField>
            </UIArticle>

            {/* 배치 사이즈 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb' required={true}>
                  Batch Size
                </UITypography>
                <UIInput.Text
                  value={batchSize}
                  placeholder='1'
                  onChange={e => handleIntegerInput(e.target.value, setBatchSize)}
                  onBlur={() => handleIntegerBlur(batchSize, setBatchSize)}
                />
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
                    <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb'>
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
                  <UIInput.Text
                    value={patience}
                    placeholder='3'
                    onChange={e => handleIntegerInput(e.target.value, setPatience)}
                    onBlur={() => handleIntegerBlur(patience, setPatience)}
                  />
                </UIFormField>
              </UIArticle>
            )}
          </UIPopupBody>

          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <UIButton2 className='btn-secondary-gray' style={{ width: '80px' }} onClick={onPreviousStep}>
                  이전
                </UIButton2>
                <UIButton2
                  className='btn-secondary-blue'
                  style={{ width: '80px' }}
                  onClick={handleNext}
                  disabled={!learningEpochs.trim() || !learningRate.trim() || !batchSize.trim() || (earlyStop && !patience.trim())}
                >
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
