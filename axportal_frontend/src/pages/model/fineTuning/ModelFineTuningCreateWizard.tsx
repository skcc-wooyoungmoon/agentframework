import { useAtom } from 'jotai';
import { type UIStepperItem } from '@/components/UI/molecules';
import { ModelFineTuningCreate01StepPopupPage } from './ModelFineTuningCreate01StepPopupPage';
import { ModelFineTuningCreate02StepPopupPage } from './ModelFineTuningCreate02StepPopupPage';
import { ModelFineTuningCreate03StepPopupPage } from './ModelFineTuningCreate03StepPopupPage';
import { ModelFineTuningCreate04StepPopupPage } from './ModelFineTuningCreate04StepPopupPage';
import { ModelFineTuningCreate05StepPopupPage } from './ModelFineTuningCreate05StepPopupPage';
import { ModelFineTuningCreate06StepPopupPage } from './ModelFineTuningCreate06StepPopupPage';
import { fineTuningWizardCurrentStepAtom, fineTuningWizardIsOpenAtom } from '@/stores/model/fineTuning/fineTuning.atoms';

type Props = {
  onClose?: () => void;
};

export const ModelFineTuningCreateWizard = ({ onClose }: Props) => {
  // Jotai 상태 관리
  const [currentStep, setCurrentStep] = useAtom(fineTuningWizardCurrentStepAtom);
  const [isWizardOpen, setIsWizardOpen] = useAtom(fineTuningWizardIsOpenAtom);

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

  const handleNextStep = () => {
    if (currentStep < 6) {
      setCurrentStep(currentStep + 1);
    }
  };

  const handlePreviousStep = () => {
    if (currentStep > 1) {
      setCurrentStep(currentStep - 1);
    }
  };

  const handleClose = () => {
    setIsWizardOpen(false);
  };

  if (!isWizardOpen) {
    return null;
  }

  const renderCurrentStep = () => {
    switch (currentStep) {
      case 1:
        return <ModelFineTuningCreate01StepPopupPage currentStep={currentStep} stepperItems={stepperItems} onNextStep={handleNextStep} onClose={handleClose} />;
      case 2:
        return (
          <ModelFineTuningCreate02StepPopupPage
            currentStep={currentStep}
            stepperItems={stepperItems}
            onNextStep={handleNextStep}
            onPreviousStep={handlePreviousStep}
            onClose={handleClose}
          />
        );
      case 3:
        return (
          <ModelFineTuningCreate03StepPopupPage
            currentStep={currentStep}
            stepperItems={stepperItems}
            onNextStep={handleNextStep}
            onPreviousStep={handlePreviousStep}
            onClose={handleClose}
          />
        );
      case 4:
        return (
          <ModelFineTuningCreate04StepPopupPage
            currentStep={currentStep}
            stepperItems={stepperItems}
            onNextStep={handleNextStep}
            onPreviousStep={handlePreviousStep}
            onClose={handleClose}
          />
        );
      case 5:
        return (
          <ModelFineTuningCreate05StepPopupPage
            currentStep={currentStep}
            stepperItems={stepperItems}
            onNextStep={handleNextStep}
            onPreviousStep={handlePreviousStep}
            onClose={handleClose}
          />
        );
      case 6:
        return <ModelFineTuningCreate06StepPopupPage stepperItems={stepperItems} onPreviousStep={handlePreviousStep} onClose={onClose} />;
      default:
        return null;
    }
  };

  return <>{renderCurrentStep()}</>;
};
