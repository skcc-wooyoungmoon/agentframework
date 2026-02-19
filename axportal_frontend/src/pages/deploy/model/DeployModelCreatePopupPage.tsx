import { useMemo, useState } from 'react';

import type { UIStepperItem } from '@/components/UI/molecules';
import { useLayerPopup } from '@/hooks/common/layer';
import { useCommonPopup } from '@/hooks/common/popup/useCommonPopup';
import { PayReqWizard, type PayReqWizardProps } from '@/pages/common/PayReqWizrad';
import { DeployModelStep1ModelSelect } from '@/pages/deploy/model/DeployModelStep1ModelSelect.tsx';
import { DeployModelStep2InfoInput } from '@/pages/deploy/model/DeployModelStep2InfoInput.tsx';
import { DeployModelStep3ResAlloc } from '@/pages/deploy/model/DeployModelStep3ResAlloc.tsx';
import { useCheckApprovalStatus } from '@/services/common/payReq.service';
import { useCreateBackendAiModelDeploy, useCreateModelDeploy } from '@/services/deploy/model/modelDeploy.services';
import type { CreateBackendAiModelDeployRequest, CreateModelDeployRequest, InfoInputDataType } from '@/services/deploy/model/types';
import type { ModelCtlgType } from '@/services/model/ctlg/types.ts';
import { useUser } from '@/stores';
import { useModal } from '@/stores/common/modal';
import { useNavigate } from 'react-router';

type DeployModelCreatePopupPageProps = {
  currentStep: number;
  stepperItems?: UIStepperItem[];
  onNextStep: () => void;
  onPreviousStep: () => void;
  onClose: () => void;
  defaultModel?: ModelCtlgType | undefined;
};

const advancedValueInit = {
  inflight_quantization: null,
  quantization: null,
  dtype: null,
  gpu_memory_utilization: null,
  load_format: null,
  tensor_parallel_size: null,
  pipeline_parallel_size: null,
  cpu_offload_gb: null,
  enforce_eager: null,
  max_model_len: null,
  vllm_use_v1: null,
  max_num_seqs: null,
  limit_mm_per_prompt: null,
  tokenizer_mode: null,
  config_format: null,
  trust_remote_code: null,
  hf_overrides: null,
  mm_processor_kwargs: null,
  disable_mm_preprocessor_cache: null,
  enable_auto_tool_choice: null,
  tool_call_parser: null,
  tool_parser_plugin: null,
  chat_template: null,
  guided_decoding_backend: null,
  enable_reasoning: null,
  reasoning_parser: null,
  device: null,
  shm_size: null,
  custom_serving: null,
  model_definition_path: null,
};

const envsInit = {};

const infoInputDataInit: InfoInputDataType = {
  name: '',
  description: '',
  selectedFrame: 'vLLM',
  selectedFrameVer: '',

  advancedChecked: false,
  advancedValue: advancedValueInit,
  envs: envsInit,
  inputFilter: [],
  outputFilter: [],
};

const resourceAllocDataInit = {
  selectedResource: 'none',
  cpuValue: 0,
  memoryValue: 0,
  gpuValue: 0,
  instanceCount: 1,
};

export const DeployModelCreatePopupPage = ({ currentStep, onClose, onNextStep, onPreviousStep, defaultModel }: DeployModelCreatePopupPageProps) => {
  const { openAlert } = useModal();
  const { showCancelConfirm, showComplete } = useCommonPopup();
  const { user } = useUser();
  const navigate = useNavigate();
  const [selectedModel, setSelectedModel] = useState<ModelCtlgType | undefined>(defaultModel);
  const [infoInputData, setInfoInputData] = useState<InfoInputDataType>(infoInputDataInit);
  const [resourceAllocData, setResourceAllocData] = useState(resourceAllocDataInit);

  const createModelDeployMutation = useCreateModelDeploy();
  const createBackendAiModelDeployMutation = useCreateBackendAiModelDeploy();

  //// 결재
  const payReqWizardPopup = useLayerPopup();
  const uniqueKey = useMemo(() => `${user.userInfo.memberId}-${selectedModel?.id}`, [user.userInfo.memberId, selectedModel?.id]);

  // 결재 상태 조회
  const { data: approvalStatus } = useCheckApprovalStatus(
    {
      approvalUniqueKey: uniqueKey,
    },
    {
      enabled: payReqWizardPopup.currentStep === 1,
      refetchOnMount: 'always',
    }
  );
  // 결재 팝업 닫기
  const handlePayReqWizardClose = () => {
    handleClosePopup();
    payReqWizardPopup.onClose();
  };
  // PayReqWizard에 전달할 모델 배포 정보를 별도로 보관
  const [approvalInfo, setApprovalInfo] = useState<PayReqWizardProps['approvalInfo']>({
    memberId: '',
    approvalType: '',
    afterProcessParamString: '',
    approvalItemString: '',
  });

  // 결재 처리 함수
  const handleSubmitApproval = (requestData: CreateBackendAiModelDeployRequest | CreateModelDeployRequest) => {
    payReqWizardPopup.onOpen();
    setApprovalInfo({
      memberId: user.userInfo.memberId,
      approvalType: '04', // 모델 배포 생성 (업무코드는 실제 업무에 맞게 수정 필요)
      approvalUniqueKey: uniqueKey, // 요청 식별자 사용자 id + 모델 id
      approvalItemString: `${infoInputData.name}`, // 요청하는 대상/작업 이름 (알람 표시 목적) <- 배포명
      afterProcessParamString: JSON.stringify(requestData),
      apprivalTableInfo: [
        [{ key: '모델명', value: `${selectedModel?.name}` }],
        [
          { key: '배포명', value: infoInputData.name },
          { key: '배포 유형', value: 'self-hosting' },
        ],
        [
          { key: '리소스 그룹', value: resourceAllocData.selectedResource },
          { key: '할당 자원', value: `CPU : ${resourceAllocData.cpuValue}Core, Memory : ${resourceAllocData.memoryValue}GiB, GPU : ${resourceAllocData.gpuValue}GPU ` },
        ],
      ],
    });
  };

  // 모델 선택 함수
  const handleModelSelect = (model: ModelCtlgType | undefined) => {
    setSelectedModel(model);
  };

  // 모델 배포 생성 성공 함수
  const handleCreateDeployModelSuccess = (servingId: string) => {
    showComplete({
      itemName: '모델 배포를',
      onConfirm: () => {
        navigate(`/deploy/modelDeploy/${servingId}`);
      },
    });
  };

  // 배포 팝업 닫기
  const handleClosePopup = () => {
    setSelectedModel(undefined);
    setInfoInputData(infoInputDataInit);
    setResourceAllocData(resourceAllocDataInit);
    onClose();
  };

  // 취소 버튼 클릭 함수
  const handleClose = async () => {
    showCancelConfirm({
      onConfirm: () => {
        handleClosePopup();
      },
    });
  };

  // 배포 시작 클릭 함수
  const onClickCreateDeployModel = async (isDirectDeploy: boolean = false) => {
    // 결재 중복 체크
    if (!isDirectDeploy && approvalStatus?.inProgress) {
      await openAlert({
        title: '안내',
        message: '동일한 모델 배포 요청이 이미 진행 중입니다. 기존 요청 처리 완료 후 다시 시도해주세요.',
      });
      return;
    }

    // 유효성 검사
    if (!selectedModel) {
      await openAlert({
        title: '오류',
        message: '모델을 선택해주세요.',
      });
      return;
    }

    if (!infoInputData.name) {
      await openAlert({
        title: '오류',
        message: '필수 정보를 모두 입력해주세요.',
      });
      return;
    }

    // 배포명 유효성 검증: 영문, 숫자, '-', '_', '/', '.' 만 허용, 영숫자로 시작/끝나야 함
    const nameValue = infoInputData.name.trim();
    const allowedPattern = /^[a-zA-Z0-9_\-/.]+$/;
    const startsWithAlphanumeric = /^[a-zA-Z0-9]/.test(nameValue);
    const endsWithAlphanumeric = /[a-zA-Z0-9]$/.test(nameValue);

    if (!allowedPattern.test(nameValue)) {
      await openAlert({
        title: '오류',
        message: '배포명은 영문, 숫자, -, _, /, . 만 사용할 수 있습니다.',
      });
      return;
    }

    if (!startsWithAlphanumeric) {
      await openAlert({
        title: '오류',
        message: '배포명은 영문 또는 숫자로 시작해야 합니다.',
      });
      return;
    }

    if (!endsWithAlphanumeric) {
      await openAlert({
        title: '오류',
        message: '배포명은 영문 또는 숫자로 끝나야 합니다.',
      });
      return;
    }

    // servingType에 따라 분기처리
    if (selectedModel.servingType === 'self-hosting') {
      if (!infoInputData.selectedFrame || !infoInputData.selectedFrameVer) {
        await openAlert({
          title: '오류',
          message: '프레임워크를 선택해주세요.',
        });
        return;
      }

      if (!resourceAllocData.selectedResource || resourceAllocData.selectedResource === 'none') {
        await openAlert({
          title: '오류',
          message: '리소스그룹을 선택해주세요.',
        });
        return;
      }

      // Backend.AI 모델 배포 생성 요청 데이터 구성
      const createBackendAiRequest: CreateBackendAiModelDeployRequest = {
        name: infoInputData.name,
        description: infoInputData.description,
        modelId: selectedModel.id,
        runtime: infoInputData.selectedFrame === 'vLLM' ? 'vllm' : 'sglang',
        runtimeImage: infoInputData.selectedFrameVer,
        servingMode: 'SINGLE_NODE',
        servingParams: infoInputData?.advancedValue, // param
        cpuRequest: resourceAllocData.cpuValue || 1,
        gpuRequest: resourceAllocData.gpuValue || 1,
        memRequest: resourceAllocData.memoryValue || 1,
        minReplicas: resourceAllocData.instanceCount || 1,
        safetyFilterInput: infoInputData?.inputFilter ? infoInputData.inputFilter.length > 0 : false,
        safetyFilterOutput: infoInputData?.outputFilter ? infoInputData.outputFilter.length > 0 : false,
        dataMaskingInput: false,
        dataMaskingOutput: false,
        safetyFilterInputGroups: infoInputData?.inputFilter.map(filter => filter.filterGroupId) || [],
        safetyFilterOutputGroups: infoInputData?.outputFilter.map(filter => filter.filterGroupId) || [],
        resourceGroup: resourceAllocData?.selectedResource,
        envs: infoInputData?.envs || {},
      };

      if (isDirectDeploy) {
        // TODO 우회 (삭제 예정)
        createBackendAiModelDeployMutation.mutate(createBackendAiRequest, {
          onSuccess: ({ data: { servingId } }) => {
            handleCreateDeployModelSuccess(servingId);
          },
        });
      } else {
        // 결재
        handleSubmitApproval(createBackendAiRequest);
      }
    } else {
      // 일반 모델 배포 생성 요청 데이터 구성
      const createRequest: CreateModelDeployRequest = {
        name: infoInputData.name,
        description: infoInputData.description,
        modelId: selectedModel.id,

        safetyFilterInput: infoInputData?.inputFilter ? infoInputData.inputFilter.length > 0 : false,
        safetyFilterOutput: infoInputData?.outputFilter ? infoInputData.outputFilter.length > 0 : false,
        dataMaskingInput: false,
        dataMaskingOutput: false,
        safetyFilterInputGroups: infoInputData?.inputFilter.map(filter => filter.filterGroupId) || [],
        safetyFilterOutputGroups: infoInputData?.outputFilter.map(filter => filter.filterGroupId) || [],
        isCustom: false,
      };

      if (isDirectDeploy) {
        // TODO 우회 (삭제 예정)
        createModelDeployMutation.mutate(createRequest, {
          onSuccess: ({ data: { servingId } }) => {
            handleCreateDeployModelSuccess(servingId);
          },
        });
      } else {
        // 결재
        handleSubmitApproval(createRequest);
      }
    }
  };
  const stepperItems = useMemo(() => {
    const tempStepperItems = [
      { step: 1, label: '모델 선택' },
      { step: 2, label: '배포 정보 입력' },
    ];

    if (selectedModel?.servingType !== 'serverless') {
      tempStepperItems.push({ step: 3, label: '자원 할당' });
    }
    return tempStepperItems;
  }, [selectedModel]);

  const advancedSettingInitialize = () => {
    setInfoInputData(prev => ({ ...prev, advancedValue: advancedValueInit }));
  };
  const envsInitialize = () => {
    setInfoInputData(prev => ({ ...prev, envs: envsInit }));
  };
  const onNextStepStep2 = () => {
    setResourceAllocData(resourceAllocDataInit);
    onNextStep();
  };

  return (
    <>
      {currentStep === 1 && (
        <DeployModelStep1ModelSelect
          currentStep={currentStep}
          stepperItems={stepperItems}
          onClose={handleClose}
          onNextStep={onNextStep}
          onPreviousStep={onPreviousStep}
          selectedModel={selectedModel}
          handleModelSelect={handleModelSelect}
        />
      )}
      {currentStep === 2 && (
        <DeployModelStep2InfoInput
          currentStep={currentStep}
          stepperItems={stepperItems}
          onClose={handleClose}
          onNextStep={onNextStepStep2}
          onPreviousStep={!!defaultModel ? handleClose : onPreviousStep}
          selectedModel={selectedModel}
          infoInputData={infoInputData}
          setInfoInputData={setInfoInputData}
          onClickCreateDeployModel={onClickCreateDeployModel}
          advancedSettingInitialize={advancedSettingInitialize}
          envsInitialize={envsInitialize}
        />
      )}
      {currentStep === 3 && (
        <DeployModelStep3ResAlloc
          currentStep={currentStep}
          stepperItems={stepperItems}
          onClose={handleClose}
          onNextStep={onNextStep}
          onPreviousStep={onPreviousStep}
          resourceAllocData={resourceAllocData}
          setResourceAllocData={setResourceAllocData}
          onClickCreateDeployModel={onClickCreateDeployModel}
        />
      )}
      {/* 모델 배포 생성 후 PayReqWizard 표시 */}
      {payReqWizardPopup.currentStep === 1 && (
        <PayReqWizard
          isOpen={payReqWizardPopup.currentStep === 1}
          onClose={handlePayReqWizardClose}
          approvalInfo={approvalInfo} // 저장된 모델 배포 정보 전달
        />
      )}
    </>
  );
};
