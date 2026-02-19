import { useState } from 'react';

/**
 * @description 레이어 팝업의 단계별 상태를 관리하는 커스텀 훅
 * @returns {Object} 팝업 상태와 제어 메소드들을 포함한 객체
 */
export function useLayerPopup() {
  const [step, setStep] = useState(0);

  /**
   * 다음 단계로 이동하는 메소드
   */
  const handleNextStep = () => {
    setStep(step + 1);
  };

  /**
   * 이전 단계로 이동하는 메소드
   */
  const handlePreviousStep = () => {
    setStep(step - 1);
  };

  /**
   * 팝업을 열고 첫 번째 단계로 설정하는 메소드
   * @description step을 1로 설정하여 팝업을 활성화합니다
   */
  const handlePopupOpen = () => {
    setStep(1);
  };

  /**
   * 팝업을 닫고 초기 상태로 되돌리는 메소드
   * @description step을 0으로 설정하여 팝업을 비활성화합니다
   */
  const handlePopupClose = () => {
    setStep(0);
  };

  const setCustomStep = (step: number) => {
    // console.log('?');
    setStep(step);
  };

  return {
    currentStep: step,
    onNextStep: handleNextStep,
    onPreviousStep: handlePreviousStep,
    onOpen: handlePopupOpen,
    onClose: handlePopupClose,
    setCustomStep,
  };
}
