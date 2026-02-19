import type { UIModalProps, UIPopupProps } from '.';

// src/types/modal.ts
export type UIModalType = 'alert' | 'confirm' | '2xsmall' | 'xsmall' | 'small' | 'medium' | 'large' | 'xlarge';

export type UIBaseModalProps = {
  onClose: () => void;
  backdropClosable?: boolean;
  trapFocus?: boolean;
  zIndex?: number;
};

export type UIDefaultModalProps = UIPopupProps | UIModalProps;
