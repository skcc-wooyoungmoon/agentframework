import React from 'react';

import { ModalRenderer } from '@/components/common';
import { useModal } from '@/stores/common/modal';
// TODO-0727: 모달 시스템 구현 완료 후 render 코드 위치 변경 예정
export const ModalProvider: React.FC<{ children: React.ReactNode }> = ({
  children,
}) => {
  const { modals, closeModal } = useModal();

  return (
    <>
      {children}
      {/* 실제 모달 렌더링 */}
      <ModalRenderer modals={modals} closeModal={closeModal} />
    </>
  );
};
