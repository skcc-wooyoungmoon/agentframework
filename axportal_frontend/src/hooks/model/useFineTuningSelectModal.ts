import { createElement, useCallback, useRef } from 'react';
import { useModal } from '@/stores/common/modal';
import { MODAL_ID } from '@/constants/modal/modalId.constants.ts';
import { FineTuningSelectPopup } from '@/components/model/fineTuning/FineTuningSelectPopup/component';

type UseFineTuningSelectModalProps = {
  onConfirm?: () => void;
  onCancel?: () => void;
};

export const useFineTuningSelectModal = (props?: UseFineTuningSelectModalProps) => {
  const { openModal, closeModal } = useModal();

  // props를 ref로 저장하여 최신 값을 참조할 수 있도록 함
  const propsRef = useRef(props);
  propsRef.current = props;

  const closeFineTuningSelectModal = () => {
    closeModal(MODAL_ID.MODEL_FINETUNING_METRICS_SELECTION);
  };

  const openFineTuningSelectModal = useCallback(async () => {
    await openModal(
      {
        type: 'large',
        title: '파인튜닝 선택',
        body: createElement(FineTuningSelectPopup),
        onConfirm: () => {
          if (propsRef.current?.onConfirm) {
            propsRef.current.onConfirm();
          }
          closeFineTuningSelectModal()
        },
        onCancel: () => {
          propsRef.current?.onCancel?.();
          closeFineTuningSelectModal()
        },
      },
      {
        modalId: MODAL_ID.MODEL_FINETUNING_METRICS_SELECTION,
        confirm: true,
        cancel: true,
      }
    );
  }, [openModal, closeFineTuningSelectModal]);

  return {
    openFineTuningSelectModal,
    closeFineTuningSelectModal,
  };
};
