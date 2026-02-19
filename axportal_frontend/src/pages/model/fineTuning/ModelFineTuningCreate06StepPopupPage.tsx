import { useAtom } from 'jotai';
import React, { useMemo, useState } from 'react';

import { UIButton2, UIDataCnt, UITextLabel, UITypography } from '@/components/UI/atoms';
import { UIArticle, UIFormField, UIInput, UIPopupBody, UIPopupFooter, UIPopupHeader, UIStepper, type UIStepperItem, UIUnitGroup } from '@/components/UI/molecules';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { useCommonPopup } from '@/hooks/common/popup/useCommonPopup';
import { useCreateFineTuningTraining } from '@/services/model/fineTuning/modelFineTuning.service';
import type { CreateFineTuningRequest } from '@/services/model/fineTuning/types';
import {
  fineTuningAdjustmentTechAtom,
  fineTuningBatchSizeAtom,
  fineTuningCpuValueAtom,
  fineTuningDescriptionAtom,
  fineTuningEarlyStopAtom,
  fineTuningGpuValueAtom,
  fineTuningLearningEpochsAtom,
  fineTuningLearningRateAtom,
  fineTuningLearningTypeAtom,
  fineTuningMemoryValueAtom,
  fineTuningNameAtom,
  fineTuningPatienceAtom,
  fineTuningPftTypeAtom,
  fineTuningScalingGroupAtom,
  fineTuningSelectedDatasetIdsAtom,
  fineTuningSelectedModelAtom,
  fineTuningSelectedModelIdAtom,
  fineTuningValidationRatioAtom,
  fineTuningValidationRatioTextAtom,
  fineTuningWizardCurrentStepAtom,
  fineTuningWizardIsOpenAtom,
  resetAllFineTuningDataAtom,
} from '@/stores/model/fineTuning/fineTuning.atoms';
import dateUtils from '@/utils/common/date.utils.ts';
import { useNavigate } from 'react-router';

interface LayerPopupProps {
  stepperItems: UIStepperItem[];
  onPreviousStep: () => void;
  onClose?: () => void;
}

export const ModelFineTuningCreate06StepPopupPage: React.FC<LayerPopupProps> = ({ stepperItems, onPreviousStep }) => {
  // useModal 훅
  const { showFailure, showComplete, showCancelConfirm } = useCommonPopup();
  const navigate = useNavigate();
  // Jotai 상태 관리 (읽기 전용) - 01~05step의 모든 입력정보
  const [finetuningName] = useAtom(fineTuningNameAtom);
  const [finetuningDescription] = useAtom(fineTuningDescriptionAtom);
  const [learningType] = useAtom(fineTuningLearningTypeAtom);
  const [rftType] = useAtom(fineTuningPftTypeAtom);
  const [adjustmentTech] = useAtom(fineTuningAdjustmentTechAtom);
  const [learningEpochs] = useAtom(fineTuningLearningEpochsAtom);
  const [validationRatio] = useAtom(fineTuningValidationRatioAtom);
  const [validationRatioText] = useAtom(fineTuningValidationRatioTextAtom);
  const [learningRate] = useAtom(fineTuningLearningRateAtom);
  const [batchSize] = useAtom(fineTuningBatchSizeAtom);
  const [earlyStop] = useAtom(fineTuningEarlyStopAtom);
  const [patience] = useAtom(fineTuningPatienceAtom);
  const [cpuValue] = useAtom(fineTuningCpuValueAtom);
  const [memoryValue] = useAtom(fineTuningMemoryValueAtom);
  const [gpuValue] = useAtom(fineTuningGpuValueAtom);
  const [selectedDatasetIds] = useAtom(fineTuningSelectedDatasetIdsAtom);
  const [selectedModelId] = useAtom(fineTuningSelectedModelIdAtom);
  const [selectedModel] = useAtom(fineTuningSelectedModelAtom);
  const [resourceGroup] = useAtom(fineTuningScalingGroupAtom);

  // 01step에서 Jotai에 저장된 모델명 사용
  const getModelName = () => {
    if (selectedModel) {
      return selectedModel.displayName || selectedModel.name;
    }

    // selectedModel이 null인 경우 selectedModelId만 표시
    return selectedModelId || '모델을 선택해주세요';
  };

  const [, setCurrentStep] = useAtom(fineTuningWizardCurrentStepAtom);
  const [, setIsWizardOpen] = useAtom(fineTuningWizardIsOpenAtom);
  const [, resetAllData] = useAtom(resetAllFineTuningDataAtom);

  // 파인튜닝 생성 mutation
  const { mutate: createFineTuning, isPending: isCreating } = useCreateFineTuningTraining({
    onSuccess: ({ data: { id } }) => {
      // 성공 알림 표시 - 확인 버튼 클릭 시 상태 초기화 및 팝업 닫기
      showComplete({
        itemName: '파인튜닝을',
        onConfirm: () => {
          // 상태 초기화 및 팝업 닫기
          resetAllData();
          setIsWizardOpen(false);
          setCurrentStep(1);
          // 리스트 갱신을 위한 이벤트 발생
          // onClose?.();

          navigate(`/model/finetuning/${id}`);
        },
      });
    },
    onError: /* error */ () => {
      showFailure({
        itemName: '파인튜닝',
      });
    },
  });

  // 로컬 상태
  const [isPopupOpen, setIsPopupOpen] = useState(true); // 팝업이므로 기본적으로 열려있음

  // 그리드 컬럼 정의 (MD_030101_P06 기준)
  const columnDefs: any = useMemo(
    () => [
      {
        headerName: 'NO',
        field: 'id' as any,
        width: 56,
        minWidth: 56,
        maxWidth: 56,
        cellClass: 'text-center',
        headerClass: 'text-center',
        cellStyle: {
          textAlign: 'center',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
        } as any,
        sortable: false,
        suppressHeaderMenuButton: true,
        suppressSizeToFit: true,
        valueGetter: (params: any) => (params.node?.rowIndex != null ? params.node.rowIndex + 1 : ''),
      },
      {
        headerName: '이름',
        field: 'name',
        width: 272,
      },
      {
        headerName: '설명',
        field: 'description',
        minWidth: 392,
        flex: 1,
        cellRenderer: React.memo((params: any) => {
          return (
            <div
              style={{
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                whiteSpace: 'nowrap',
              }}
              title={params.value}
            >
              {params.value}
            </div>
          );
        }),
      },
      {
        headerName: '태그',
        field: 'tags' as const,
        width: 230,
        cellStyle: { paddingLeft: '16px' },
        cellRenderer: (params: any) => {
          const tags = params.value; // 태그 배열

          if (!Array.isArray(tags) || tags.length === 0) {
            return null;
          }

          const tagText = tags.map((tag: any) => tag.name).join(', ');
          const tagTextArray = tags.map((tag: any) => tag.name);

          return (
            <div title={tagText}>
              <div className='flex gap-1'>
                {tagTextArray.slice(0, 2).map((tag: string, index: number) => (
                  <UITextLabel key={index} intent='tag' className='nowrap'>
                    {tag}
                  </UITextLabel>
                ))}
              </div>
            </div>
          );
        },
      },
      {
        headerName: '공개범위',
        field: 'publicStatus',
        width: 120,
      },
      {
        headerName: '유형',
        field: 'type',
        width: 120,
        cellRenderer: React.memo((params: any) => {
          // supervised_finetuning → 지도학습 등으로 변환
          const convertType = (type: string): string => {
            switch (type) {
              case 'supervised_finetuning':
                return '지도학습';
              case 'unsupervised_finetuning':
                return '비지도학습';
              case '지도학습':
              case '비지도학습':
                return type;
              default:
                return type || '-';
            }
          };
          return <span>{convertType(params.value)}</span>;
        }),
      },
      {
        headerName: '생성일시',
        field: 'createdDate',
        width: 180,
        cellStyle: { paddingLeft: '16px' },
        valueGetter: (params: any) => {
          return params.data.createdAt ? dateUtils.formatDate(params.data.createdAt, 'datetime') : '';
        },
      },
      {
        headerName: '최종 수정일시',
        field: 'updatedDate' as const,
        width: 180,
        cellStyle: { paddingLeft: '16px' },
        valueGetter: (params: any) => {
          return params.data.updatedAt ? dateUtils.formatDate(params.data.updatedAt, 'datetime') : '';
        },
      },
    ],
    []
  );

  const handleClose = () => {
    setIsPopupOpen(false);
  };

  // 파인튜닝 생성 함수
  const handleCreateFineTuning = async () => {
    const params = [
      '[TrainingConfig]',
      `use_lora = ${rftType === 'lora'}`,
      `num_train_epochs = ${learningEpochs}`,
      `validation_split = ${validationRatio}`,
      `learning_rate = ${learningRate}`,
      `batch_size = ${batchSize}`,
      // `early_stopping = ${earlyStop}`,
      // `early_stopping_patience = ${patience}`,
      // `scaling_group = ${resourceGroup}`,
    ];

    if (earlyStop) {
      params.push(`early_stopping = ${earlyStop}`)
      params.push(`early_stopping_patience = ${patience}`)
    }

    const createRequest: CreateFineTuningRequest = {
      name: finetuningName,
      status: 'initialized',
      prev_status: 'initialized',
      progress: {},
      resource: {
        cpu_quota: cpuValue.toString(),
        mem_quota: memoryValue.toString(),
        gpu_quota: gpuValue.toString(),
        scaling_group: resourceGroup,
      },
      dataset_ids: selectedDatasetIds.map(item => item.id.toString()),
      base_model_id: selectedModelId,
      // params: JSON.stringify({
      //   learning_epochs: parseInt(learningEpochs.toString()),
      //   validation_ratio: parseFloat(validationRatio.toString()),
      //   learning_rate: parseFloat(learningRate.toString()),
      //   batch_size: parseInt(batchSize.toString()),
      //   early_stop: earlyStop,
      //   patience: parseInt(patience.toString()),
      //   learning_type: learningType,
      //   pft_type: rftType,
      //   adjustment_tech: adjustmentTech,
      // }),
      params: params.join('\n'),
      envs: {},
      description: finetuningDescription,
      // project_id: projectId,
      trainer_id: '77a85f64-5717-4562-b3fc-2c963f66afa6',

      // scalingGroup: ['E-LOCAL', 'E-DEV'].includes(env.VITE_RUN_MODE) ? 'default' : 'FT-IDE',
      scalingGroup: resourceGroup,
    };

    createFineTuning(createRequest);
  };

  const handleCancel = () => {
    showCancelConfirm({
      onConfirm: () => {
        // 모든 상태값 초기화 (Jotai 공통 함수 사용)
        resetAllData();
      },
    });
  };

  const handleNext = () => {
    /* console.log('튜닝시작 버튼 클릭', {
      learningEpochs,
      validationRatio,
      learningRate,
      batchSize,
      earlyStop,
      patience: earlyStop ? patience : null,
    }); */
    // 파인튜닝 생성 API 호출 (성공 시 onSuccess 콜백에서 모달 닫기 처리)
    handleCreateFineTuning();
  };

  return (
    <>
      {/* DesignLayout 위에 높은 z-index로 뜨는 UILayerPopup */}
      <UILayerPopup
        isOpen={isPopupOpen}
        onClose={handleClose}
        size='fullscreen'
        showOverlay={true}
        leftContent={
          /* 좌측 Step 영역 콘텐츠 */
          <UIPopupAside>
            <UIPopupHeader title='파인튜닝 만들기' description='' position='left' />
            <UIPopupBody>
              <UIArticle>
                <UIStepper currentStep={6} items={stepperItems} direction='vertical' />
              </UIArticle>
            </UIPopupBody>
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <UIButton2 className='btn-tertiary-gray' style={{ width: '80px' }} onClick={handleCancel} disabled={isCreating}>
                    취소
                  </UIButton2>
                  <UIButton2 className='btn-tertiary-blue' style={{ width: '80px' }} onClick={handleNext} disabled={isCreating}>
                    튜닝시작
                  </UIButton2>
                </UIUnitGroup>
              </UIArticle>
            </UIPopupFooter>
          </UIPopupAside>
        }
      >
        {/* 우측 Contents 영역 콘텐츠 */}
        <section className='section-popup-content'>
          <UIPopupHeader title='입력정보 확인' description='' position='right' />
          <UIPopupBody>
            {/* 모델 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb' required={false}>
                  모델
                </UITypography>
                <UIInput.Text value={getModelName()} placeholder='모델 이름' onChange={() => { }} readOnly={true} />
              </UIFormField>
            </UIArticle>

            {/* 파인튜닝 이름 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb' required={false}>
                  파인튜닝 이름
                </UITypography>
                <UIInput.Text value={finetuningName} placeholder='파인튜닝 이름' onChange={() => { }} readOnly={true} />
              </UIFormField>
            </UIArticle>

            {/* 데이터세트 유형 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb' required={false}>
                  데이터세트 유형
                </UITypography>
                <UIInput.Text value={learningType} placeholder='학습 유형' onChange={() => { }} readOnly={true} />
              </UIFormField>
            </UIArticle>

            {/* 효율성 구성(PEFT) */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb' required={false}>
                  Efficiency Configuration (PEFT)
                </UITypography>
                <UIInput.Text value={rftType} placeholder='효율성 구성' onChange={() => { }} readOnly={true} />
              </UIFormField>
            </UIArticle>

            {/* 미세 조정 기술 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb' required={false}>
                  Fine Tuning Techniques
                </UITypography>
                <UIInput.Text value={adjustmentTech} placeholder='미세 조정 기술' onChange={() => { }} readOnly={true} />
              </UIFormField>
            </UIArticle>

            {/* CPU, Memory, GPU */}
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <UIFormField gap={8} direction='column'>
                  <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb' required={false}>
                    CPU
                  </UITypography>
                  <UIInput.Text value={`${cpuValue}`} placeholder='CPU' onChange={() => { }} readOnly={true} />
                </UIFormField>
                <UIFormField gap={8} direction='column'>
                  <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb' required={false}>
                    Memory
                  </UITypography>
                  <UIInput.Text value={`${memoryValue}`} placeholder='Memory' onChange={() => { }} readOnly={true} />
                </UIFormField>
                <UIFormField gap={8} direction='column'>
                  <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb' required={false}>
                    GPU
                  </UITypography>
                  <UIInput.Text value={`${gpuValue}`} placeholder='GPU' onChange={() => { }} readOnly={true} />
                </UIFormField>
              </UIUnitGroup>
            </UIArticle>

            {/* 데이터세트 그리드 */}
            <UIArticle className='article-grid'>
              <UIListContainer>
                <UIListContentBox.Header>
                  <UIUnitGroup gap={16} direction='column'>
                    <div className='flex justify-between w-full items-center'>
                      <div className='flex-shrink-0'>
                        <div style={{ width: '168px', paddingRight: '8px' }}>
                          <UIDataCnt count={selectedDatasetIds.length || 0} prefix='학습 데이터세트 총' />
                        </div>
                      </div>
                    </div>
                  </UIUnitGroup>
                </UIListContentBox.Header>
                <UIListContentBox.Body>
                  <UIGrid
                    type='default'
                    rowData={selectedDatasetIds || []}
                    columnDefs={columnDefs}
                  /* onClickRow={(params: any) => {
                    console.log('데이터셋 클릭', params);
                  }}
                  onCheck={(selectedIds: any[]) => {
                    console.log('데이터셋 선택', selectedIds);
                  }} */
                  />
                </UIListContentBox.Body>
              </UIListContainer>
            </UIArticle>

            {/* 학습 횟수 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb' required={false}>
                  Epochs
                </UITypography>
                <UIInput.Text value={learningEpochs} placeholder='1' onChange={() => { }} readOnly={true} />
              </UIFormField>
            </UIArticle>

            {/* 검증 비율 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb' required={false}>
                  Validation Split
                </UITypography>
                <UIInput.Text value={validationRatioText} placeholder='0.2' onChange={() => { }} readOnly={true} />
              </UIFormField>
            </UIArticle>

            {/* 학습률 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb' required={false}>
                  Learning Rate
                </UITypography>
                <UIInput.Text value={learningRate.toString()} placeholder='0.0001' onChange={() => { }} readOnly={true} />
              </UIFormField>
            </UIArticle>

            {/* 배치 사이즈 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb' required={false}>
                  Batch Size
                </UITypography>
                <UIInput.Text value={batchSize.toString()} placeholder='1' onChange={() => { }} readOnly={true} />
              </UIFormField>
            </UIArticle>

            {/* 조기 종료 */}
            {earlyStop && (
              <UIArticle>
                <UIFormField gap={8} direction='column'>
                  <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb' required={false}>
                    Early Stopping
                  </UITypography>
                  <UIInput.Text value={patience} placeholder='3' onChange={() => { }} readOnly={true} />
                </UIFormField>
              </UIArticle>
            )}
          </UIPopupBody>
          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <UIButton2 className='btn-secondary-gray' style={{ width: '80px' }} onClick={onPreviousStep}>
                  이전
                </UIButton2>
              </UIUnitGroup>
            </UIArticle>
          </UIPopupFooter>
        </section>
      </UILayerPopup>
    </>
  );
};
