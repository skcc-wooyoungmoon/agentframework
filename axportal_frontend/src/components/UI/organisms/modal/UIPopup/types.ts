import type { UIModalBodyProps } from '@/components/UI/molecules/modal';

import type { UIBaseModalProps } from '../types';

export type UIPopupProps = {
  type: 'alert' | 'confirm';
  confirmText?: string;
  onConfirm?: () => void;
  cancelText?: string;
  onCancel?: () => void;
  showHeader?: boolean;
  title?: string;
  bodyType?: 'text' | 'image';
  message?: string;
  image?: {
    url: string;
    alt: string;
    size: number;
  };
} & UIBaseModalProps &
  Omit<UIModalBodyProps, 'type' | 'children'>;
