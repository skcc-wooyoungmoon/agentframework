import { useAtom } from 'jotai';
import React, { useState } from 'react';

import { UIButton2, UIRadio2, UITypography } from '@/components/UI/atoms';
import { UIArticle, UIFormField, UIInput, UIPopupBody, UIPopupFooter, UIPopupHeader, UIStepper, type UIStepperItem, UITextArea2, UIUnitGroup } from '@/components/UI/molecules';
import { UIDropdown } from '@/components/UI/molecules/dropdown/UIDropdown';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';

import { useCommonPopup } from '@/hooks/common/popup/useCommonPopup';
import {
  fineTuningAdjustmentTechAtom,
  fineTuningDescriptionAtom,
  fineTuningLearningTypeAtom,
  fineTuningNameAtom,
  fineTuningPftTypeAtom,
  resetAllFineTuningDataAtom,
} from '@/stores/model/fineTuning/fineTuning.atoms';

interface LayerPopupProps {
  currentStep: number;
  stepperItems: UIStepperItem[];
  onNextStep: () => void;
  onPreviousStep: () => void;
  onClose: () => void;
}

export const ModelFineTuningCreate02StepPopupPage: React.FC<LayerPopupProps> = ({ currentStep, stepperItems, onClose, onNextStep, onPreviousStep }) => {
  // useModal 훅
  const { showCancelConfirm } = useCommonPopup();

  // Jotai 상태 관리
  const [finetuningName, setFinetuningName] = useAtom(fineTuningNameAtom);
  const [description, setDescription] = useAtom(fineTuningDescriptionAtom);
  const [learningType, setLearningType] = useAtom(fineTuningLearningTypeAtom);
  const [pftType, setPftType] = useAtom(fineTuningPftTypeAtom);
  const [adjustmentTech, setAdjustmentTech] = useAtom(fineTuningAdjustmentTechAtom);
  const [, resetAllData] = useAtom(resetAllFineTuningDataAtom);

  const handleNext = () => {
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

  // 드롭다운 상태
  const [isDatasetTypeDropdownOpen, setIsDatasetTypeDropdownOpen] = useState(false);
  const [isPeftDropdownOpen, setIsPeftDropdownOpen] = useState(false);

  return (
    <>
      {/* DesignLayout 위에 높은 z-index로 뜨는 UILayerPopup */}
      <UILayerPopup
        isOpen={currentStep === 2}
        onClose={onClose}
        size='fullscreen'
        showOverlay={true}
        leftContent={
          /* 좌측 Step 영역 콘텐츠 */
          <UIPopupAside>
            {/* 레이어 팝업 헤더 */}
            <UIPopupHeader title='파인튜닝 등록' description='' position='left' />
            {/* 레이어 팝업 바디 */}
            <UIPopupBody>
              <UIArticle>
                <UIStepper currentStep={currentStep} items={stepperItems} direction='vertical' />
              </UIArticle>
            </UIPopupBody>
            {/* 레이어 팝업 footer */}
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <UIButton2 className='btn-tertiary-gray' style={{ width: '80px' }} onClick={handleCancel}>
                    취소
                  </UIButton2>
                  <UIButton2 className='btn-tertiary-blue' style={{ width: '80px' }} disabled={true}>
                    저장
                  </UIButton2>
                </UIUnitGroup>
              </UIArticle>
            </UIPopupFooter>
          </UIPopupAside>
        }
      >
        {/* 우측 Contents 영역 콘텐츠 */}
        <section className='section-popup-content'>
          <UIPopupHeader title='기본 정보 입력' description='' position='right' />
          <UIPopupBody>
            {/* 파인튜닝 이름 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb' required={true}>
                  파인튜닝 이름
                </UITypography>
                <UIInput.Text value={finetuningName} placeholder='이름 입력' onChange={e => setFinetuningName(e.target.value)} maxLength={50} />
              </UIFormField>
            </UIArticle>

            {/* 설명 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb'>
                  설명
                </UITypography>
                <UITextArea2 value={description} placeholder='설명 입력' onChange={e => setDescription(e.target.value)} maxLength={100} />
              </UIFormField>
            </UIArticle>

            {/* 데이터세트 유형 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb' required={true}>
                  데이터세트 유형
                </UITypography>
                <UIDropdown
                  value={learningType || '지도학습'}
                  placeholder='선택'
                  isOpen={isDatasetTypeDropdownOpen}
                  disabled={false}
                  onClick={() => setIsDatasetTypeDropdownOpen(!isDatasetTypeDropdownOpen)}
                  onSelect={(value: string) => {
                    setLearningType(value);
                    setIsDatasetTypeDropdownOpen(false);
                  }}
                  options={[
                    { value: 'supervised', label: '지도학습' },
                    { value: 'unsupervised', label: '비지도학습' },
                    { value: 'dpo', label: 'DPO' },
                  ]}
                  height={48}
                />
              </UIFormField>
            </UIArticle>

            {/* Efficiency Configuration (PEFT) */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb' required={true}>
                  Efficiency Configuration (PEFT)
                </UITypography>
                <UIDropdown
                  value={pftType || 'LoRA'}
                  placeholder='선택'
                  isOpen={isPeftDropdownOpen}
                  disabled={false}
                  onClick={() => setIsPeftDropdownOpen(!isPeftDropdownOpen)}
                  onSelect={(value: string) => {
                    setPftType(value);
                    setIsPeftDropdownOpen(false);
                  }}
                  options={[
                    { value: 'lora', label: 'LoRA' },
                    { value: 'full', label: 'Full Fine-tuning' },
                  ]}
                  height={48}
                />
              </UIFormField>
            </UIArticle>

            {/* Fine Tuning Techniques */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb' required={true}>
                  Fine Tuning Techniques
                </UITypography>
                <UIUnitGroup gap={12} direction='column' align='start'>
                  <UIRadio2 name='basic1' value='option1' label='BASIC' checked={adjustmentTech === 'basic'} onChange={() => setAdjustmentTech('basic')} />
                </UIUnitGroup>
                <UITypography variant='body-2' className='secondary-neutral-600'>
                  초기 실험이나 작은 데이터에셋에 적합하며, 복잡한 튜닝 없이 바로 결과를 확인할 수 있습니다.
                </UITypography>
              </UIFormField>
            </UIArticle>
          </UIPopupBody>

          {/* 레이어 팝업 footer */}
          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <UIButton2 className='btn-secondary-gray' style={{ width: '80px' }} onClick={onPreviousStep}>
                  이전
                </UIButton2>
                <UIButton2 className='btn-secondary-blue' style={{ width: '80px' }} onClick={handleNext} disabled={!finetuningName.trim() || finetuningName.length > 50}>
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
