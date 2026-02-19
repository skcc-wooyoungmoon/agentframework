import type { UIDefaultModalProps, UIModalType } from '@/components/UI/organisms/modal';
import { MODAL_ID } from '@/constants/modal/modalId.constants';

export interface ModalInstance {
  id: string;
  type: UIModalType;
  props: UIDefaultModalProps;
  resolve?: (value: any) => void;
  reject?: (reason?: any) => void;
}

export type ModalControlAction = {
  modalId: keyof typeof MODAL_ID;
  confirm?: boolean;
  cancel?: boolean;
};
