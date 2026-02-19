import { useRef, useCallback } from 'react';
import { useModal } from '@/stores/common/modal';
import { createElement } from 'react';
import { MODAL_ID } from '@/constants/modal/modalId.constants';
import { ModelSelectPopup } from '@/components/model/playground/ModelSelectPopup';
import type { PlaygroundModel } from '@/services/model/playground/types';
import { stringUtils } from '@/utils/common';

interface UseModelSelectModalProps {
  onModelSelect: (models: PlaygroundModel[]) => void;
}

export const useModelSelectModal = ({ onModelSelect }: UseModelSelectModalProps) => {
  const { openModal, closeModal } = useModal();
  const tempSelectedModelsRef = useRef<PlaygroundModel[]>([]);

  // tempSelectedModels가 변경될 때마다 ref 업데이트
  const updateTempSelectedModels = useCallback((newValue: PlaygroundModel[]) => {
    tempSelectedModelsRef.current = newValue;
  }, []);

  const openModelSelectModal = useCallback(() => {
    // 모달이 열릴 때 현재 선택된 모델로 초기화
    updateTempSelectedModels([]);

    openModal(
      {
        type: 'large',
        title: '모델 선택',
        body: createElement(ModelSelectPopup, {
          onModelSelect: selectedModels => {
            // console.log('onModelSelect', selectedModels);
            // 선택된 모델들을 PlaygroundModel로 변환하여 저장
            const playgroundModels: PlaygroundModel[] = selectedModels.map(item => ({
              ...item,
              instanceId: stringUtils.generateUuid(), // 고유한 instanceId 생성
              parameters: {
                temperature: 1,
                topP: 1,
                presencePenalty: 0,
                frequencyPenalty: 0,
                maxTokens: 4096,
                temperatureChecked: true,
                topPChecked: true,
                presencePenaltyChecked: true,
                frequencyPenaltyChecked: true,
                maxTokensChecked: true,
              },
            }));
            updateTempSelectedModels(playgroundModels);
          },
        }),
        onConfirm: () => {
          // ref를 사용하여 최신 상태 참조
          const currentSelectedModels = tempSelectedModelsRef.current;
          // console.log('onConfirm', currentSelectedModels);
          if (currentSelectedModels && currentSelectedModels.length > 0) {
            onModelSelect(currentSelectedModels);
          }
          closeModal(MODAL_ID.MODEL_PLAYGROUND_MODEL_SELECTION);
        },
        onCancel: () => {
          updateTempSelectedModels([]);
          closeModal(MODAL_ID.MODEL_PLAYGROUND_MODEL_SELECTION);
        },
      },
      {
        modalId: MODAL_ID.MODEL_PLAYGROUND_MODEL_SELECTION,
        confirm: true,
      }
    );
  }, [openModal, onModelSelect, updateTempSelectedModels]);

  return {
    openModelSelectModal,
  };
};
