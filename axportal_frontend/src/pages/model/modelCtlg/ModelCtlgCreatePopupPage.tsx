import type { UIStepperItem } from '@/components/UI/molecules';
import { useCommonPopup } from '@/hooks/common/popup/useCommonPopup';
import { ModelCtlgStep1PickPopup } from '@/pages/model/modelCtlg/ModelCtlgStep1PickPopup.tsx';
import { ModelCtlgStep2InfoChk } from '@/pages/model/modelCtlg/ModelCtlgStep2InfoChk.tsx';
import { ModelCtlgStep3Extrain } from '@/pages/model/modelCtlg/ModelCtlgStep3Extrain.tsx';
import { useCreateModelCtlg } from '@/services/model/ctlg/modelCtlg.services';
import type { CreateModelCtlgRequest } from '@/services/model/ctlg/types.ts';
import type { ModelGardenInfo } from '@/services/model/garden/types.ts';
import React, { useState } from 'react';
import { useNavigate } from 'react-router';

interface ModelCtlgCreatePopupPageProps {
  currentStep: number;
  onNextStep: () => void;
  onPreviousStep: () => void;
  onClose: () => void;
  onSuccess?: () => void;
}

export const ModelCtlgCreatePopupPage: React.FC<ModelCtlgCreatePopupPageProps> = ({ currentStep, onNextStep, onPreviousStep, onClose }) => {
  const { showComplete, showFailure, showCancelConfirm } = useCommonPopup();
  const navigate = useNavigate();

  // 모델 카탈로그 생성 mutation
  const createModelCtlgMutation = useCreateModelCtlg({
    onSuccess: ({ data: { id } }) => {
      // console.log('모델 카탈로그 생성 성공:', data);
      showComplete({
        itemName: '모델 카탈로그 만들기를',
        onConfirm: () => {
          navigate(`/model/modelCtlg/${id}`);
        },
      });
    },
    onError: /* error */ () => {
      // console.error('모델 카탈로그 생성 실패:', error);
      showFailure({
        itemName: '모델 카탈로그 만들기',
      });
    },
  });

  const [selectedModelGarden, setSelectedModelGarden] = useState<ModelGardenInfo | undefined>(undefined);

  // 모델 카탈로그 저장 함수
  const handleSaveModelCtlg = async (displayName: string, apiKey: string, tags: string[]) => {
    // 필수 필드 검증
    if (!selectedModelGarden || !apiKey) {
      // console.error('필수 필드가 누락되었습니다:');
      return;
    }

    const createModelCtlgRequest: CreateModelCtlgRequest = {
      // 필수 필드
      displayName: displayName,
      name: selectedModelGarden.name,
      providerId: selectedModelGarden.providerId,

      // 선택적 필드
      type: selectedModelGarden?.type ? selectedModelGarden?.type : undefined,
      description: selectedModelGarden?.description ? selectedModelGarden?.description : undefined,
      servingType: selectedModelGarden?.serving_type ? selectedModelGarden?.serving_type : undefined,
      license: selectedModelGarden?.license ? selectedModelGarden?.license : undefined,
      readme: selectedModelGarden?.readme ? selectedModelGarden?.readme : undefined,
      languages: selectedModelGarden?.langauges ? [{ name: selectedModelGarden?.langauges }] : [],
      tags: tags.map(tag => ({ name: tag })),
      tasks: [],
      endpoint: {
        url: selectedModelGarden?.url,
        identifier: selectedModelGarden?.identifier,
        key: apiKey,
      },
      size: selectedModelGarden?.param_size,
      modelGardenId: selectedModelGarden.id,
    };

    createModelCtlgMutation.mutate(createModelCtlgRequest);
  };

  // 스테퍼 데이터
  const stepperItems: UIStepperItem[] = [
    {
      id: 'step1',
      label: '모델 선택',
      step: 1,
    },
    {
      id: 'step2',
      label: '모델 정보 확인',
      step: 2,
    },
    {
      id: 'step3',
      label: '추가 정보 입력',
      step: 3,
    },
  ];

  const handleClose = async () => {
    showCancelConfirm({
      onConfirm: () => {
        onClose();
      },
    });
  };

  return (
    <>
      {currentStep === 1 && (
        <ModelCtlgStep1PickPopup
          isOpen={currentStep === 1}
          onNextStep={onNextStep}
          onClose={handleClose}
          stepperItems={stepperItems}
          selectedModelGarden={selectedModelGarden}
          setSelectedModelGarden={setSelectedModelGarden}
        />
      )}
      {currentStep === 2 && (
        <ModelCtlgStep2InfoChk
          isOpen={currentStep === 2}
          onNextStep={onNextStep}
          onPreviousStep={onPreviousStep}
          onClose={handleClose}
          stepperItems={stepperItems}
          selectedModelGarden={selectedModelGarden}
        />
      )}
      {currentStep === 3 && (
        <ModelCtlgStep3Extrain
          isOpen={currentStep === 3}
          onPreviousStep={onPreviousStep}
          onClose={handleClose}
          stepperItems={stepperItems}
          selectedModelGarden={selectedModelGarden}
          onSave={handleSaveModelCtlg}
        />
      )}
    </>
  );
};
