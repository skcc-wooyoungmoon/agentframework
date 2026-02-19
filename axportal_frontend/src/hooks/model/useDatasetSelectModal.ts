import { createElement, useCallback, useRef } from 'react';
import { useModal } from '@/stores/common/modal';
import { DatasetSelectPopup } from '@/components/model/fineTuning/DatasetSelectPopup';
import { MODAL_ID } from '@/constants/modal/modalId.constants.ts';
import type { GetDatasetsResponse } from '@/services/data/types.ts';

type UseDatasetSelectModalProps = {
  onConfirm?: () => void;
  onCancel?: () => void;
  learningType: string;
  selectedDatasets: GetDatasetsResponse[];
};

export const useDatasetSelectModal = (props?: UseDatasetSelectModalProps) => {
  const { openModal, closeModal } = useModal();

  // props를 ref로 저장하여 최신 값을 참조할 수 있도록 함
  const propsRef = useRef(props);
  propsRef.current = props;

  // learningType을 별도 변수로 추출하여 의존성 추적을 명확하게 함
  const learningType = props?.learningType || '';
  const selectedDatasets = props?.selectedDatasets || [];

  const closeDatasetSelectModal = () => {
    closeModal(MODAL_ID.MODEL_FINETUNING_DATASET_SELECTION);
  };

  const openDatasetSelectModal = useCallback(async () => {
    // console.log('learningType', propsRef.current?.learningType);
    await openModal(
      {
        type: 'large',
        title: '데이터셋 선택',
        body: createElement(DatasetSelectPopup, {
          learningType: propsRef.current?.learningType || '',
          selectedDatasets: propsRef.current?.selectedDatasets || [],
        }),
        onConfirm: () => {
          if (propsRef.current?.onConfirm) {
            propsRef.current.onConfirm();
          }
        },
        onCancel: () => {
          propsRef.current?.onCancel?.();
        },
        onClose: () => {
          propsRef.current?.onCancel?.();
        },
      },
      {
        modalId: MODAL_ID.MODEL_FINETUNING_DATASET_SELECTION,
        confirm: true,
      }
    );
  }, [openModal, learningType, selectedDatasets]);

  return {
    openDatasetSelectModal,
    closeDatasetSelectModal,
  };
};
