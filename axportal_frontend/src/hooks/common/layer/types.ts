import type { UIStepperItem } from '@/components/UI/molecules';

export type LayerPopupProps = {
  currentStep: number;
  stepperItems?: UIStepperItem[];
  onNextStep: () => void;
  onPreviousStep: () => void;
  onClose: () => void;
};
