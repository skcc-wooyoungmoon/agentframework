import { useCallback, useEffect } from 'react';

import { atom, useAtom } from 'jotai';

import type { UIDefaultModalProps, UIModalProps, UIPopupProps } from '@/components/UI/organisms/modal';
import { stringUtils } from '@/utils/common';

import type { ModalControlAction, ModalInstance } from './types';

// ======================================== 원자 ========================================
/**
 * 모달 인스턴스들을 관리하는 전역 상태
 */
export const modalsAtom = atom<ModalInstance[]>([]);

/**
 * 모달 관련 에러들을 관리하는 전역 상태
 */
export const modalErrorsAtom = atom<string[]>([]);

// // 성능 최적화를 위한 ref
// const modalTimeoutRef = useRef<Map<string, NodeJS.Timeout>>(new Map());

// ======================================== 메소드 ========================================
/**
 * Body 스크롤을 잠그거나 해제하는 함수
 * @param lock - true: 스크롤 잠금, false: 스크롤 해제
 */
function lockBodyScroll(lock: boolean) {
  if (lock) {
    document.body.style.overflow = 'hidden';
    document.body.style.paddingRight = 'var(--scrollbar-width)';
  } else {
    document.body.style.overflow = '';
    document.body.style.paddingRight = '';
  }
}

// ESC 키 처리 개선
// function setupEscapeKeyHandler(
//   modals: ModalInstance[],
//   closeModal: (id: string) => void
// ) {
//   const handleKeyDown = (e: KeyboardEvent) => {
//     if (e.key === 'Escape' && modals.length > 0) {
//       const topModal = modals[modals.length - 1];
//       closeModal(topModal.id);
//       }
//   };

//   window.addEventListener('keydown', handleKeyDown);
//   return () => window.removeEventListener('keydown', handleKeyDown);
// }

/**
 * 모달 상태를 관리하는 커스텀 훅
 * @returns {Object} 모달 상태와 제어 메소드들을 포함한 객체
 */
export const useModal = () => {
  const [modals, setModals] = useAtom(modalsAtom);
  const [errors, setErrors] = useAtom(modalErrorsAtom);

  // Body scroll lock 관리
  useEffect(() => {
    lockBodyScroll(modals.length > 0);
    return () => lockBodyScroll(false);
  }, [modals.length]);

  /**
   * 특정 모달을 닫는 메소드
   * @param id - 닫을 모달의 ID {modalId}
   */
  const closeModal = useCallback(
    (id: string) => {
      setModals(prev => prev.filter(m => m.id !== id));
    },
    [setModals]
  );

  /**
   * 모든 모달을 닫는 메소드
   */
  const closeAllModals = useCallback(() => {
    setModals([]);
  }, [setModals]);

  /**
   * 모달을 여는 메소드
   * @props
   * ```tsx
   * *type: 'small' | 'large' // 모달 타입
   * *title: string // 모달 제목
   * body: React.ReactNode // 모달 내용
   * showFooter: boolean // 하단 버튼 표시 여부
   * cancelText: string // 취소 버튼 텍스트
   * confirmText: string // 확인 버튼 텍스트
   * onCancel: () => void // 취소 버튼 클릭 시 실행되는 콜백 함수
   * onClose: () => void // 모달 닫기 콜백 함수
   * onClickCloseButton: () => void // X 버튼 클릭 시 실행되는 콜백 함수
   * ```
   * @controlAction
   * ```tsx
   * modalId: keyof typeof MODAL_ID; // 제어할 모달ID 필수
   * confirm?: boolean; // 확인 버튼 클릭 시 제어 여부
   * cancel?: boolean; // 취소 버튼 클릭 시 제어 여부
   * ```
   * @returns {Promise<boolean>} 모달 결과를 반환하는 Promise
   */
  const openModal = useCallback(
    (props: Omit<UIModalProps, 'onClose'> & { onClose?: () => void }, controlAction?: ModalControlAction) => {
      return new Promise<boolean>((resolve, reject) => {
        try {
          const id = controlAction?.modalId || stringUtils.generateUuid();
          const modalInstance: ModalInstance = {
            id: id,
            type: props.type,
            props: {
              ...props,
              onClose: () => {
                props.onClose?.();
                closeModal(id);
              },
              onCancel: () => {
                if (controlAction?.cancel) {
                  if (props.onCancel) {
                    props.onCancel();
                  } else {
                    closeModal(id);
                    resolve(false);
                  }
                } else {
                  props.onCancel?.();
                  closeModal(id);
                  resolve(false);
                }
              },
              onConfirm: () => {
                if (controlAction?.confirm) {
                  if (props.onConfirm) {
                    props.onConfirm();
                  } else {
                    closeModal(id);
                    resolve(true);
                  }
                } else {
                  props.onConfirm?.();
                  closeModal(id);
                  resolve(true);
                }
              },
            },
            resolve,
            reject,
          };

          setModals(prev => [...prev, modalInstance]);
        } catch (error) {
          reject(error);
          setErrors(prev => [...prev, `Failed to open modal: ${error}`]);
        }
      });
    },
    [setModals, closeModal, setErrors]
  );

  /**
   * Promise 기반 alert 모달을 여는 메소드
   * @props
   * ```tsx
   * title: string // alert 제목 : 없다면 노출 X
   * body: React.ReactNode // alert 내용
   * showFooter: boolean // 하단 버튼 표시 여부
   * confirmText: string // 확인 버튼 텍스트
   * onConfirm: () => void // 확인 버튼 클릭 시 실행되는 콜백 함수
   * onClose: () => void // alert 닫기 콜백 함수
   * onClickCloseButton: () => void // X 버튼 클릭 시 실행되는 콜백 함수
   * ```
   * @controlAction
   * ```tsx
   * modalId: string; // 제어할 모달ID 필수
   * confirm?: boolean; // 확인 버튼 클릭 시 제어 여부
   * cancel?: boolean; // 취소 버튼 클릭 시 제어 여부
   * ```
   * @returns {Promise<boolean>} 확인 버튼 클릭 시 true를 반환하는 Promise
   */
  const openAlert = useCallback(
    (
      props: Omit<UIPopupProps, 'type' | 'onClose' | 'showHeader'> & {
        onClose?: () => void;
      }
    ) => {
      return new Promise<boolean>((resolve, reject) => {
        try {
          const id = stringUtils.generateUuid();
          const modalInstance: ModalInstance = {
            id: id,
            type: 'alert',
            props: {
              ...props,
              type: 'alert',
              showHeader: !!props.title,
              onClose: () => {
                props.onClose?.();
                closeModal(id);
              },
              onConfirm: () => {
                props.onConfirm?.();
                closeModal(id);
                resolve(true);
              },
            },
            resolve,
            reject,
          };

          setModals(prev => [...prev, modalInstance]);
        } catch (error) {
          reject(error);
          setErrors(prev => [...prev, `Failed to open alert: ${error}`]);
        }
      });
    },
    [setModals, closeModal, setErrors]
  );

  /**
   * Promise 기반 confirm 모달을 여는 메소드
   * @props
   * ```tsx
   * title: string // confirm 제목 : 없다면 노출 X
   * body: React.ReactNode // confirm 내용
   * showFooter: boolean // 하단 버튼 표시 여부
   * cancelText: string // 취소 버튼 텍스트
   * confirmText: string // 확인 버튼 텍스트
   * onCancel: () => void // 취소 버튼 클릭 시 실행되는 콜백 함수
   * onConfirm: () => void // 확인 버튼 클릭 시 실행되는 콜백 함수
   * onClose: () => void // confirm 닫기 콜백 함수
   * onClickCloseButton: () => void // X 버튼 클릭 시 실행되는 콜백 함수
   * ```
   * @returns {Promise<boolean>} 확인 시 true, 취소 시 false를 반환하는 Promise
   */
  const openConfirm = useCallback(
    (
      props: Omit<UIPopupProps, 'type' | 'onClose' | 'showHeader'> & {
        onClose?: () => void;
      },
      controlAction?: ModalControlAction
    ) => {
      return new Promise<boolean>((resolve, reject) => {
        try {
          const id = controlAction?.modalId || stringUtils.generateUuid();
          const modalInstance: ModalInstance = {
            id,
            type: 'confirm',
            props: {
              ...props,
              type: 'confirm',
              showHeader: !!props.title,
              onClose: () => {
                props.onClose?.();
                closeModal(id);
              },
              onConfirm: () => {
                if (controlAction?.confirm) {
                  if (props.onConfirm) {
                    props.onConfirm();
                  } else {
                    closeModal(id);
                    resolve(true);
                  }
                } else {
                  props.onConfirm?.();
                  closeModal(id);
                  resolve(true);
                }
              },
              onCancel: () => {
                if (controlAction?.cancel) {
                  if (props.onCancel) {
                    props.onCancel();
                  } else {
                    closeModal(id);
                    resolve(false);
                  }
                } else {
                  props.onCancel?.();
                  closeModal(id);
                  resolve(false);
                }
              },
            },
            resolve,
            reject,
          };

          setModals(prev => [...prev, modalInstance]);
        } catch (error) {
          reject(error);
          setErrors(prev => [...prev, `Failed to open confirm: ${error}`]);
        }
      });
    },
    [setModals, closeModal, setErrors]
  );

  /**
   * 열려있는 모달의 Props를 업데이트하는 메소드
   * @param id - 업데이트할 모달의 ID
   * @param props - 업데이트할 Props
   */
  const updateModal = useCallback(
    (id: string, props: Partial<UIDefaultModalProps>) => {
      setModals(prev =>
        prev.map(modal => {
          if (modal.id === id) {
            return {
              ...modal,
              props: { ...modal.props, ...props } as UIDefaultModalProps,
            };
          }
          return modal;
        })
      );
    },
    [setModals]
  );

  return {
    modals,
    errors,
    openModal,
    openAlert,
    openConfirm,
    updateModal,
    closeModal,
    closeAllModals,
  };
};
