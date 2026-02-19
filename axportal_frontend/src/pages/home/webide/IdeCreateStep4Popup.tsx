import React, { useState } from 'react';

import { useAtom } from 'jotai';

import { UIButton2, UITypography } from '@/components/UI/atoms';
import { UIStepper, UIUnitGroup, UIPopupFooter, UIPopupHeader, UIPopupBody, UIFormField, UIInput, UIDropdown } from '@/components/UI/molecules';
import type { UIStepperItem } from '@/components/UI/molecules';
import { UIArticle } from '@/components/UI/molecules';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { useCommonPopup } from '@/hooks/common/popup/useCommonPopup';
import { ideCreWizardAtom } from '@/stores/home/webide/ideCreWizard.atoms';

/** IdeCreateStep4Popup Props 타입 */
interface IdeCreateStep4PopupProps {
  /** 팝업 표시 여부 */
  isOpen: boolean;
  /** 팝업 닫기 핸들러 */
  onClose: () => void;
  /** 이전 버튼 클릭 핸들러 */
  onPrev: () => void;
  /** 생성 버튼 클릭 핸들러 */
  onCreate: () => void;
}

/** 스테퍼 아이템 */
const stepperItems: UIStepperItem[] = [
  { id: 'step1', label: '프로젝트 선택', step: 1 },
  { id: 'step2', label: '도구 및 이미지 선택', step: 2 },
  { id: 'step3', label: 'DW 계정 선택', step: 3 },
  { id: 'step4', label: '자원 선택', step: 4 },
];

/**
 * IDE 생성 Step4 - 자원 선택 팝업
 */
export const IdeCreateStep4Popup: React.FC<IdeCreateStep4PopupProps> = ({
  isOpen,
  onClose,
  onPrev,
  onCreate,
}) => {
  // 공통 팝업 훅
  const { showCancelConfirm } = useCommonPopup();

  // 위자드 데이터 상태
  const [wizardData, setWizardData] = useAtom(ideCreWizardAtom);
  const resourcePreset = wizardData.resourcePreset;
  const cpuValue = wizardData.cpuValue;
  const memoryValue = wizardData.memoryValue;

  const setResourcePreset = (value: string) => {
    setWizardData(prev => ({ ...prev, resourcePreset: value }));
  };
  const setCpuValue = (value: string) => {
    setWizardData(prev => ({ ...prev, cpuValue: value }));
  };
  const setMemoryValue = (value: string) => {
    setWizardData(prev => ({ ...prev, memoryValue: value }));
  };

  // 자원 프리셋 상태
  const [isPresetDropdownOpen, setIsPresetDropdownOpen] = useState(false);

  /**
   * 프리셋 변경 핸들러
   */
  const handlePresetChange = (value: string) => {
    setResourcePreset(value);
    setIsPresetDropdownOpen(false);

    // 프리셋에 따른 CPU/Memory 값 설정
    switch (value) {
      case 'preset-small':
        setCpuValue('1');
        setMemoryValue('2');
        break;
      case 'preset-medium':
        setCpuValue('2');
        setMemoryValue('4');
        break;
      case 'preset-large':
        setCpuValue('4');
        setMemoryValue('8');
        break;
      default:
        setCpuValue('1');
        setMemoryValue('2');
    }
  };

  /**
   * 취소 버튼 클릭 핸들러
   */
  const handleCancel = () => {
    showCancelConfirm({
      onConfirm: onClose,
    });
  };

  /**
   * 이전 버튼 클릭 핸들러
   */
  const handlePrev = () => {
    onPrev();
  };

  /**
   * 생성 버튼 클릭 핸들러
   */
  const handleCreate = () => {
    onCreate();
  };

  return (
    <UILayerPopup
      isOpen={isOpen}
      onClose={onClose}
      size='fullscreen'
      showOverlay={true}
      leftContent={
        <UIPopupAside>
          {/* 좌측 헤더 */}
          <UIPopupHeader title='IDE 생성' position='left' />
          {/* 좌측 바디 - 스테퍼 */}
          <UIPopupBody>
            <UIArticle>
              <UIStepper currentStep={4} items={stepperItems} direction='vertical' />
            </UIArticle>
          </UIPopupBody>
          {/* 좌측 푸터 - Step4에서는 생성 버튼 활성화 */}
          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <UIButton2 className='btn-tertiary-gray' style={{ width: 80 }} onClick={handleCancel}>
                  취소
                </UIButton2>
                <UIButton2 className='btn-tertiary-blue' style={{ width: 80 }} onClick={handleCreate}>
                  생성
                </UIButton2>
              </UIUnitGroup>
            </UIArticle>
          </UIPopupFooter>
        </UIPopupAside>
      }
    >
      {/* 우측 Contents 영역 */}
      <section className='section-popup-content'>
        {/* 우측 헤더 */}
        <UIPopupHeader
          title='자원 선택'
          description='디폴트로 설정된 자원량으로 IDE가 생성됩니다. 기본 프리셋을 제외한 프리셋을 사용하여 IDE 생성을 원할 경우, 간편 결재 요청이 발송됩니다.'
          position='right'
        />

        {/* 우측 바디 */}
        <UIPopupBody>
          {/* 자원 프리셋 */}
          <UIArticle>
            <UIFormField gap={8} direction='column'>
              <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                자원 프리셋
              </UITypography>
              <UIDropdown
                value={resourcePreset}
                options={[
                  { value: 'preset-small', label: 'Small (기본)' },
                  /* todo: 간편결재 개발 후 주석 제거 (26.01.06 pjt) */
                  { value: 'preset-medium', label: 'Medium' },
                  { value: 'preset-large', label: 'Large' },
                ]}
                isOpen={isPresetDropdownOpen}
                onClick={() => setIsPresetDropdownOpen(!isPresetDropdownOpen)}
                onSelect={handlePresetChange}
                placeholder='자원 프리셋 선택'
              />
            </UIFormField>
          </UIArticle>

          {/* CPU / Memory */}
          <UIArticle>
            <UIUnitGroup gap={8} direction='row' align='start'>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                  CPU(Core)
                </UITypography>
                <UIInput.Text value={cpuValue} placeholder='1' readOnly={true} />
              </UIFormField>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                  Memory(GiB)
                </UITypography>
                <UIInput.Text value={memoryValue} placeholder='2' readOnly={true} />
              </UIFormField>
            </UIUnitGroup>
          </UIArticle>
        </UIPopupBody>

        {/* 우측 푸터 */}
        <UIPopupFooter>
          <UIArticle>
            <UIUnitGroup gap={8} direction='row' align='start'>
              <UIButton2 className='btn-secondary-gray' style={{ width: '80px' }} onClick={handlePrev}>
                이전
              </UIButton2>
            </UIUnitGroup>
          </UIArticle>
        </UIPopupFooter>
      </section>
    </UILayerPopup>
  );
};
