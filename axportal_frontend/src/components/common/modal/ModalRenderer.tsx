import React from 'react';

import UIModalRoot from '@/components/UI/molecules/modal/UIModalRoot/component';
import { UIModal, UIPopup, type UIModalProps, type UIPopupProps } from '@/components/UI/organisms/modal';
import type { ModalInstance } from '@/stores/common/modal';

import type { ModalRendererProps } from './types';

export const ModalRenderer: React.FC<ModalRendererProps> = ({ modals, closeModal }) => {
  const renderModal = (modal: ModalInstance) => {
    const zIndex = 10000 + modals.findIndex(m => m.id === modal.id);
    return (
      <UIModalRoot
        key={modal.id}
        zIndex={zIndex}
        onClose={() => closeModal(modal.id)}
        backdropClosable={modal.props?.backdropClosable ?? true}
        trapFocus={modal.props?.trapFocus ?? true}
      >
        {modal.type === 'alert' && <UIPopup {...(modal.props as UIPopupProps)} type='alert' />}
        {modal.type === 'confirm' && <UIPopup {...(modal.props as UIPopupProps)} type='confirm' />}
        {modal.type === '2xsmall' && <UIModal {...(modal.props as UIModalProps)} type='2xsmall' />}
        {modal.type === 'xsmall' && <UIModal {...(modal.props as UIModalProps)} type='xsmall' />}
        {modal.type === 'small' && <UIModal {...(modal.props as UIModalProps)} type='small' />}
        {modal.type === 'medium' && <UIModal {...(modal.props as UIModalProps)} type='medium' />}
        {modal.type === 'large' && <UIModal {...(modal.props as UIModalProps)} type='large' />}
        {modal.type === 'xlarge' && <UIModal {...(modal.props as UIModalProps)} type='large' />}
      </UIModalRoot>
    );
  };

  // 모달이 하나라도 열려있으면 렌더링
  if (modals.length === 0) return null;

  return <>{modals.map(renderModal)}</>;
};
