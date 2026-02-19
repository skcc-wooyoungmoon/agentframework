import type { UIBaseModalProps } from '../types';

export interface UIModalProps extends UIBaseModalProps {
  type: '2xsmall' | 'xsmall' | 'small' | 'medium' | 'large';
  title: string;
  body?: React.ReactNode;
  useCustomFooter?: boolean;
  showFooter?: boolean;
  cancelText?: string;
  confirmText?: string;
  onCancel?: () => void;
  cancelDisabled?: boolean;
  onConfirm?: () => void;
  confirmDisabled?: boolean;
  onClickCloseButton?: () => void;
}
