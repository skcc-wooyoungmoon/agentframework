import { useRef } from 'react';
import { useModal } from '@/stores/common/modal';
import { createElement } from 'react';
import { MODAL_ID } from '@/constants/modal/modalId.constants';
import { SetModelParam, type SetModelParamRef } from '@/components/model/playground/SetModelParam';
import type { ModelParameters, PlaygroundModel } from '@/services/model/playground/types';

interface UseSetModelParamProps {
  onUpdateParameters?: (modelId: string, parameters: ModelParameters) => void;
}

export const useSetModelParam = ({ onUpdateParameters }: UseSetModelParamProps) => {
  const { openModal, closeModal } = useModal();
  const setModelParamRef = useRef<SetModelParamRef>(null);

  const handleParameterUpdate = (modelId: string, parameters: ModelParameters) => {
    onUpdateParameters?.(modelId, parameters);
  };

  const openSetModelParamModal = (model: PlaygroundModel) => {
    openModal(
      {
        type: 'medium',
        title: `파라미터 설정`,
        body: createElement(SetModelParam, {
          ref: setModelParamRef,
          initialParameters: model.parameters,
          onConfirm: (parameters: ModelParameters) => handleParameterUpdate(model.instanceId, parameters),
        }),
        onConfirm: () => {
          // SetModelParam의 handleConfirm을 호출하여 파라미터를 가져와서 업데이트
          if (setModelParamRef.current) {
            setModelParamRef.current.handleConfirm();
          }
          closeModal(MODAL_ID.SET_MODEL_PARAM);
        },
        onCancel: () => {
          closeModal(MODAL_ID.SET_MODEL_PARAM);
        },
      },
      {
        modalId: MODAL_ID.SET_MODEL_PARAM,
        confirm: true,
      }
    );
  };

  return {
    openSetModelParamModal,
  };
};
