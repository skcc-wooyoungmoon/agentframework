import { useRef, useCallback } from 'react';
import { useModal } from '@/stores/common/modal';
import { createElement } from 'react';
import { MODAL_ID } from '@/constants/modal/modalId.constants';
import { PromptSelectPopup } from '@/components/model/playground/PromptSelectPopup';

interface UsePromptSelectModalProps {
  onPromptSelect: (promptUuids: string[]) => void;
}

export const usePromptSelectModal = ({ onPromptSelect }: UsePromptSelectModalProps) => {
  const { openModal, closeModal } = useModal();
  const tempSelectedPromptUuidsRef = useRef<string[]>([]);

  // tempSelectedPromptUuids가 변경될 때마다 ref 업데이트
  const updateTempSelectedPromptUuids = useCallback((newValue: string[]) => {
    tempSelectedPromptUuidsRef.current = newValue;
  }, []);

  const openPromptSelectModal = useCallback(() => {
    // 모달이 열릴 때 현재 선택된 프롬프트로 초기화
    updateTempSelectedPromptUuids([]);

    openModal(
      {
        type: 'large',
        title: '추론 프롬프트 선택',
        body: createElement(PromptSelectPopup, {
          onPromptSelect: (promptUuids: string[]) => {
            // console.log('onPromptSelect', promptUuids);
            updateTempSelectedPromptUuids(promptUuids);
          },
        }),
        onConfirm: () => {
          // ref를 사용하여 최신 상태 참조
          const currentSelectedUuids = tempSelectedPromptUuidsRef.current;
          // console.log('onConfirm', currentSelectedUuids);
          if (currentSelectedUuids && currentSelectedUuids.length > 0) {
            onPromptSelect(currentSelectedUuids);
          }
          closeModal(MODAL_ID.MODEL_PLAYGROUND_PROMPT_SELECTION);
        },
        onCancel: () => {
          updateTempSelectedPromptUuids([]);
          closeModal(MODAL_ID.MODEL_PLAYGROUND_PROMPT_SELECTION);
        },
      },
      {
        modalId: MODAL_ID.MODEL_PLAYGROUND_PROMPT_SELECTION,
        confirm: true,
      }
    );
  }, [openModal, onPromptSelect, updateTempSelectedPromptUuids]);

  return {
    openPromptSelectModal,
  };
};
