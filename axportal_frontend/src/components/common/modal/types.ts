import type { ModalInstance } from '@/stores/common/modal';

export interface ModalRendererProps {
  modals: ModalInstance[];
  closeModal: (id: string) => void;
}
