import React, { useEffect, useMemo, useState } from 'react';

import { UIButton2, UIDataCnt, UIIcon2, UILabel, UIRadio2, UISlider, UITextLabel, UIToggle, UITooltip, UITypography } from '@/components/UI/atoms';
import { UIPagination } from '@/components/UI/atoms/UIPagination';
import { UIArticle, UIDropdown, UIFormField, UIGroup, UIPopupBody, UIPopupFooter, UIPopupHeader, UIUnitGroup } from '@/components/UI/molecules';
import { UIInput } from '@/components/UI/molecules/input';
import { UITextArea2 } from '@/components/UI/molecules/input/UITextArea2';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';

import { UINotice } from '@/components/UI/atoms/UINotice';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { useDatasetSelectModal } from '@/hooks/model/useDatasetSelectModal.ts';
import type { GetDatasetsResponse } from '@/services/data/types';
import { useGetFineTuningTrainingById, useUpdateFineTuningTraining } from '@/services/model/fineTuning/modelFineTuning.service.ts';
import { useModal } from '@/stores/common/modal';
import { datasetSelectPopupAtom } from '@/stores/model/fineTuning/fineTuning.atoms.ts';
import { useAtom } from 'jotai';
import dateUtils from '@/utils/common/date.utils.ts';

interface ModelFineTuningEditPopupPageProps {
  isPopupOpen: boolean;
  onClose: () => void;
  onSuccess?: () => void;
  trainingId: string;
}

// Status 정의
const STATUS_CONFIG = {
  completed: {
    label: '이용 가능',
    intent: 'complete' as const,
  },
  processing: {
    label: '진행중',
    intent: 'progress' as const,
  },
  failed: {
    label: '실패',
    intent: 'error' as const,
  },
  canceled: {
    label: '취소',
    intent: 'stop' as const,
  },
} as const;

// params 파라미터 파싱 함수
const parseParamsString = (paramsString: string) => {
  if (!paramsString) return {};

  try {
    const trimmedString = paramsString.trim();
    if (!trimmedString) return {};

    // [TrainingConfig] 헤더 제거 및 줄 단위로 분리
    const lines = trimmedString.split('\n').filter(line => line.trim());

    // 첫 번째 줄이 [TrainingConfig]인 경우 제거
    const dataLines = lines[0]?.startsWith('[') && lines[0]?.endsWith(']') ? lines.slice(1) : lines;

    const config: Record<string, any> = {};

    // 각 줄을 파싱하여 key = value 형식으로 변환
    dataLines.forEach(line => {
      const trimmedLine = line.trim();
      if (!trimmedLine) return;

      const equalIndex = trimmedLine.indexOf('=');
      if (equalIndex === -1) return;

      const key = trimmedLine.substring(0, equalIndex).trim();
      let value: any = trimmedLine.substring(equalIndex + 1).trim();

      // 값 타입 변환
      if (value === 'true') {
        value = true;
      } else if (value === 'false') {
        value = false;
      } else if (!isNaN(Number(value)) && value !== '') {
        // 숫자로 변환 가능한 경우
        value = Number(value);
      }
      // 문자열은 그대로 유지

      config[key] = value;
    });

    return config;
  } catch (error) {
    // console.error('TrainingConfig 파라미터 파싱 오류:', error);
    return {};
  }
};

export const ModelFineTuningEditPopupPage: React.FC<ModelFineTuningEditPopupPageProps> = ({ isPopupOpen, onClose, onSuccess, trainingId }) => {
  const { openAlert, openConfirm } = useModal();

  // const [selectedFineTuning] = useAtom(selectedFineTuningAtom);
  const [popupSelectedDataset, setPopupSelectedDataset] = useAtom(datasetSelectPopupAtom);

  // 라디오 버튼 상태 (선언을 앞쪽으로 이동: 다른 훅에서 참조)
  const [learningType, setLearningType] = useState('supervised'); // 지도학습/비지도학습

  // 파인튜닝 정보조회
  const { data: fineTuningDetail } = useGetFineTuningTrainingById({ id: trainingId || '', isDataSet: true }, { enabled: !!trainingId });

  const [name, setName] = useState('');
  const [description, setDescription] = useState('');

  const [pftType, setPftType] = useState('lora'); // LoRA/Full Fine-tuning
  const [adjustmentTech, setAdjustmentTech] = useState('basic'); // BASIC

  // 파인튜닝 파라미터 상태
  const [validationRatio, setValidationRatio] = useState(0.2); // 검증비율
  const [learningRate, setLearningRate] = useState(0.0001); // 학습률
  const [batchSize, setBatchSize] = useState(1); // 배치 사이즈
  const [earlyStopping, setEarlyStopping] = useState(true); // 조기 종료
  const [earlyStoppingPatience, setEarlyStoppingPatience] = useState(3); // 조기 종료 인내도
  const [numTrainEpochs, setNumTrainEpochs] = useState(1); // 학습 에포크 수

  // 데이터세트 선택 팝업 상태
  const [selectedDatasets, setSelectedDatasets] = useState<GetDatasetsResponse[]>([]);

  // 드롭다운 상태
  const [isLearningTypeDropdownOpen, setIsLearningTypeDropdownOpen] = useState(false);

  const { openDatasetSelectModal, closeDatasetSelectModal } = useDatasetSelectModal({
    onConfirm: () => {
      setSelectedDatasets(popupSelectedDataset);
      closeDatasetSelectModal();
      setPopupSelectedDataset([]);
    },
    onCancel: () => {
      setPopupSelectedDataset([]);
      closeDatasetSelectModal();
    },
    learningType: learningType,
    selectedDatasets: selectedDatasets,
  });

  // 파인튜닝 정보 업데이트
  const updateFineTuning = useUpdateFineTuningTraining({
    onSuccess: () => {
      openAlert({
        title: '완료',
        message: '수정사항이 저장되었습니다.',
        confirmText: '확인',
        onConfirm: () => {
          onSuccess?.();
          onClose();
        },
      });
    },
    onError: /* error */ () => {
      // console.error('파인튜닝 정보 저장 중 오류가 발생했습니다:', error);
      openAlert({
        message: '저장 중 오류가 발생했습니다. 다시 시도해주세요.',
      });
    },
  });

  // 현재 상태를 params 문자열로 변환하는 함수
  const buildParamsString = () => {
    const params = [
      '[TrainingConfig]',
      `use_lora = ${pftType === 'lora'}`,
      `num_train_epochs = ${numTrainEpochs}`,
      `validation_split = ${validationRatio}`,
      `learning_rate = ${learningRate}`,
      `batch_size = ${batchSize}`,
      `early_stopping = ${earlyStopping}`,
      `early_stopping_patience = ${earlyStoppingPatience}`,
    ];

    return params.join('\n');
  };

  useEffect(() => {
    if (isPopupOpen) {
      setName(fineTuningDetail?.name || '');
      setDescription(fineTuningDetail?.description || '');

      if (fineTuningDetail?.datasetDetails && fineTuningDetail?.datasetDetails.length > 0) {
        const tempSelectedDatasets: GetDatasetsResponse[] = [];
        fineTuningDetail.datasetDetails.forEach((dataset, i) => {
          if (i === 0) {
            switch (dataset.type) {
              case 'supervised_finetuning':
                setLearningType('supervised');
                break;
              case 'unsupervised_finetuning':
                setLearningType('unsupervised');
                break;
              case 'dpo_finetuning':
                setLearningType('dpo');
                break;
            }
          }

          tempSelectedDatasets.push({
            id: dataset.id,
            name: dataset.name || '',
            status: dataset.status || '',
            description: dataset.description || '',
            tags: dataset.tags,
            type: dataset.type || '',
            createdAt: dataset.createdAt || '',
            updatedAt: dataset.updatedAt || '',
            projectId: dataset.projectId || '',
            isDeleted: false,
            datasourceId: '',
            datasourceFiles: [],
            filePath: '',
            processor: '',
            createdBy: '',
            updatedBy: '',
            publicStatus: dataset.publicStatus || '',
            lstPrjSeq: dataset.lstPrjSeq || 0,
            fstPrjSeq: dataset.fstPrjSeq || 0,
          } as GetDatasetsResponse);
        });

        setSelectedDatasets(tempSelectedDatasets);
      }

      if (fineTuningDetail?.params) {
        const parsedParams = parseParamsString(fineTuningDetail?.params || '');
        // console.log('parsedParams', parsedParams);
        if (parsedParams && Object.keys(parsedParams).length > 0) {
          if (parsedParams.validation_split !== undefined) {
            setValidationRatio(parsedParams.validation_split);
          }
          if (parsedParams.learning_rate !== undefined) {
            setLearningRate(parsedParams.learning_rate);
          }
          if (parsedParams.batch_size !== undefined) {
            setBatchSize(parsedParams.batch_size);
          }
          if (parsedParams.early_stopping !== undefined) {
            setEarlyStopping(parsedParams.early_stopping);
          }
          if (parsedParams.early_stopping_patience !== undefined) {
            setEarlyStoppingPatience(parsedParams.early_stopping_patience);
          }
          if (parsedParams.use_lora !== undefined) {
            setPftType(parsedParams.use_lora ? 'lora' : 'full');
          }
          if (parsedParams.num_train_epochs !== undefined) {
            setNumTrainEpochs(parsedParams.num_train_epochs);
          }
        }
      }
    }
  }, [fineTuningDetail, isPopupOpen]);

  // 필수값 유효성 검사 함수
  const validateRequiredFields = () => {
    const errors: string[] = [];

    if (!name || name.length > 50) {
      errors.push('이름이 없거나 50자가 넘습니다.');
    }

    // 검증비율 검증 (0 이상 100 이하의 숫자)
    if (validationRatio === null || validationRatio === undefined) {
      errors.push('검증비율을 입력해주세요.');
    } else if (validationRatio < 0 || validationRatio > 1) {
      errors.push('검증비율은 0 이상 1 이하의 값이어야 합니다.');
    }

    // 학습률 검증 (양수)
    if (learningRate === null || learningRate === undefined || learningRate <= 0) {
      errors.push('학습률을 올바르게 입력해주세요. (0보다 큰 값)');
    }

    // 배치 사이즈 검증 (1 이상의 정수)
    if (batchSize === null || batchSize === undefined || batchSize < 1 || !Number.isInteger(batchSize)) {
      errors.push('배치 사이즈를 올바르게 입력해주세요. (1 이상의 정수)');
    }

    return {
      isValid: errors.length === 0,
      errors: errors,
    };
  };

  // 저장 가능 여부 확인
  const canSave = () => {
    return validateRequiredFields().isValid;
  };

  // 저장 버튼 클릭 핸들러
  const handleSave = async () => {
    if (!fineTuningDetail?.id) {
      return;
    }

    // 저장할 데이터 구성
    const updateData = {
      ...fineTuningDetail,
      name: name,
      description: description,
      params: buildParamsString(), // 업데이트된 파라미터 문자열
      datasetIds: selectedDatasets.map(dataset => dataset.id),
    };

    // console.log('저장할 데이터:', {
    //   learningType,
    //   pftType,
    //   adjustmentTech,
    //   validationRatio,
    //   learningRate,
    //   batchSize,
    //   earlyStopping,
    //   earlyStoppingPatience,
    //   paramsString: buildParamsString(),
    //   selectedDatasets,
    // });

    try {
      // API 호출 - URL 템플릿의 {id}가 request 객체의 id로 자동 치환됨
      await updateFineTuning.mutateAsync(updateData);
    } catch (error) {
      // console.error('저장 중 오류:', error);
      // 에러는 mutation의 onError에서 처리됨
    }
  };

  const handleCancel = () => {
    openConfirm({
      message: '화면을 나가시겠어요?\n입력한 정보가 저장되지 않을 수 있습니다.',
      title: '알림',
      confirmText: '나가기',
      cancelText: '취소',
      onConfirm: () => {
        onClose();
      },
      onCancel: () => {
        // console.log('취소됨');
      },
    });
  };

  const verificationChange = (value: number) => {
    const flooredValue = Math.floor(value * 10) / 10;
    setValidationRatio(flooredValue);
  };

  // 학습 유형 옵션
  const learningTypeOptions = [
    {
      value: 'supervised',
      label: '지도 학습',
    },
    {
      value: 'unsupervised',
      label: '비지도 학습',
    },
    {
      value: 'dpo',
      label: 'DPO',
    },
  ];

  // 드롭다운 옵션들
  const learningTypeDropdownOptions = learningTypeOptions.map(option => ({
    value: option.value,
    label: option.label,
  }));

  // 선택된 옵션의 라벨
  const selectedLearningTypeLabel = learningTypeOptions.find(option => option.value === learningType)?.label || '지도 학습';

  // 그리드 컬럼 정의
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
        valueGetter: (params: any) => params.node.rowIndex + 1,
      },
      {
        headerName: '이름',
        field: 'name',
        width: 262,
        cellRenderer: React.memo((params: any) => {
          return (
            <div
              style={{
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                whiteSpace: 'nowrap',
              }}
            >
              {params.value}
            </div>
          );
        }),
      },
      {
        headerName: '상태',
        field: 'status' as any,
        width: 120,
        cellRenderer: React.memo((params: any) => {
          const status = params.value as keyof typeof STATUS_CONFIG;
          const config = STATUS_CONFIG[status] || {
            label: status,
            intent: 'complete' as const,
          };
          return (
            <UILabel variant='badge' intent={config.intent}>
              {config.label}
            </UILabel>
          );
        }),
      },
      {
        headerName: '설명',
        field: 'description',
        minWidth: 230,
        flex: 1,
        showTooltip: true,
        cellRenderer: React.memo((params: any) => {
          return (
            <div
              style={{
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                whiteSpace: 'nowrap',
              }}
            >
              {params.value}
            </div>
          );
        }),
      },
      {
        headerName: '공개범위',
        field: 'publicStatus',
        width: 120,
        cellStyle: { paddingLeft: '16px' },
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
        headerName: '유형',
        field: 'type',
        width: 120,
        cellStyle: { paddingLeft: '16px' },
        valueGetter: (params: any) => {
          const type = params.data.type;
          return type === 'dpo_finetuning' ? 'DPO' : type === 'supervised_finetuning' ? '지도학습' : type === 'unsupervised_finetuning' ? '비지도학습' : type || '';
        },
      },
      {
        headerName: '생성일시',
        field: 'createdAt',
        width: 180,
        cellStyle: { paddingLeft: '16px' },
        valueFormatter: (params: any) => {
          return params.value ? dateUtils.formatDate(params.value, 'datetime') : '';
        },
      },
      {
        headerName: '최종 수정일시',
        field: 'updatedAt',
        width: 180,
        cellStyle: { paddingLeft: '16px' },
        valueFormatter: (params: any) => {
          return params.value ? dateUtils.formatDate(params.value, 'datetime') : '';
        },
      },
    ],
    []
  );

  return (
    <>
      <UILayerPopup
        isOpen={isPopupOpen}
        // onClose={}
        size='fullscreen'
        showOverlay={true}
        leftContent={
          /* 좌측 Step 영역 콘텐츠 */
          <UIPopupAside>
            <UIPopupHeader title='파인튜닝 수정' description='' position='left' />
            <UIPopupBody>
              <UIArticle>{/* 좌측 콘텐츠 영역 */}</UIArticle>
            </UIPopupBody>
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <UIButton2 className='btn-tertiary-gray' style={{ width: '80px' }} onClick={handleCancel}>
                    취소
                  </UIButton2>
                  <UIButton2 className='btn-tertiary-blue' style={{ width: '80px' }} disabled={!canSave() || updateFineTuning.isPending} onClick={handleSave}>
                    저장
                  </UIButton2>
                </UIUnitGroup>
              </UIArticle>
            </UIPopupFooter>
          </UIPopupAside>
        }
      >
        {/* 우측 Contents 영역 콘텐츠 */}
        <section className='section-popup-content'>
          <UIPopupBody>
            {/* 파인튜닝 이름 입력 섹션 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb' required={true}>
                  파인튜닝 이름
                </UITypography>
                <UIInput.Text value={name} placeholder='파인튜닝 이름을 입력해주세요' onChange={e => setName(e.target.value)} disabled={false} maxLength={50} />
              </UIFormField>
            </UIArticle>

            {/* 파인튜닝 설명 입력 섹션 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                  설명
                </UITypography>
                <UITextArea2 placeholder='파인튜닝 설명을 입력해주세요' maxLength={100} value={description || ''} onChange={e => setDescription(e.target.value)} />
              </UIFormField>
            </UIArticle>

            {/* 학습 유형 섹션 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb' required={true}>
                  데이터세트 유형
                </UITypography>
                <UIDropdown
                  value={selectedLearningTypeLabel}
                  options={learningTypeDropdownOptions}
                  isOpen={isLearningTypeDropdownOpen}
                  onClick={() => setIsLearningTypeDropdownOpen(!isLearningTypeDropdownOpen)}
                  disabled={true}
                  onSelect={(value: string) => {
                    setLearningType(value);
                    setIsLearningTypeDropdownOpen(false);
                  }}
                />
              </UIFormField>
            </UIArticle>

            {/* 미세 조정 기술 섹션 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb' required={true}>
                  Fine Tuning Techniques
                </UITypography>
                <div className='space-y-4'>
                  <div className='flex items-start gap-2'>
                    <UIRadio2 name='adjustmentTech' value='basic' checked={adjustmentTech === 'basic'} onChange={() => setAdjustmentTech('basic')} />
                    <UIGroup direction='column' gap={4}>
                      <UITypography variant='body-1' className='secondary-neutral-800 text-sb'>
                        BASIC
                      </UITypography>
                      <UITypography variant='body-2' className='secondary-neutral-600 '>
                        초기 실험이나 작은 데이터에셋에 적합하며, 복잡한 튜닝 없이 바로 확인할 수 있습니다.
                      </UITypography>
                    </UIGroup>
                  </div>
                </div>
              </UIFormField>
            </UIArticle>

            {/* 자원 할당 섹션 */}
            <UIArticle>
              <div className='article-header'>
                <UITypography variant='title-3' className='secondary-neutral-900'>
                  자원 할당
                </UITypography>
              </div>
              <div className='article-body'>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <UIFormField gap={8} direction='column'>
                    <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={false}>
                      CPU
                    </UITypography>
                    <UIInput.Text value={fineTuningDetail?.resource?.cpu_quota} placeholder='1' readOnly={true} />
                  </UIFormField>
                  <UIFormField gap={8} direction='column'>
                    <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={false}>
                      Memory
                    </UITypography>
                    <UIInput.Text value={fineTuningDetail?.resource?.mem_quota} placeholder='1' readOnly={true} />
                  </UIFormField>
                  <UIFormField gap={8} direction='column'>
                    <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={false}>
                      GPU
                    </UITypography>
                    <UIInput.Text value={fineTuningDetail?.resource?.gpu_quota} placeholder='1' readOnly={true} />
                  </UIFormField>
                </UIUnitGroup>
              </div>
            </UIArticle>

            {/* 학습 횟수 입력 섹션 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb' required={true}>
                  Epochs
                </UITypography>
                <UIInput.Text value={numTrainEpochs.toString()} placeholder='학습 횟수 입력' onChange={e => setNumTrainEpochs(Number(e.target.value))} disabled={false} />
              </UIFormField>
              <div className='mt-2'>
                <UINotice
                  variant='info'
                  message={
                    <>
                      전체 데이터셋을 모델이 한 번 학습하는 주기를 지정합니다. 값이 높을수록 학습이 오래 진행되지만, 과적합 위험이 증가하므로,
                      <br />
                      초기에 3~5회로 설정한 뒤 검증 성능 변화를 보고 조정하세요.
                    </>
                  }
                  bulletType='circle'
                  gapSize='large'
                />
              </div>
            </UIArticle>

            {/* 데이터 그리드 섹션 */}
            <UIArticle className='article-grid'>
              <div className='article-body'>
                <UIListContainer>
                  <UIListContentBox.Header>
                    <UIUnitGroup gap={16} direction='column'>
                      <div className='flex justify-between w-full items-center'>
                        <div className='flex-shrink-0'>
                          <div style={{ width: '168px', paddingRight: '8px' }}>
                            <UIDataCnt count={selectedDatasets?.length || 0} prefix='학습 데이터세트 총' unit='건' />
                          </div>
                        </div>
                      </div>
                    </UIUnitGroup>
                  </UIListContentBox.Header>
                  <UIListContentBox.Body>
                    <UIGrid
                      type='multi-select'
                      rowData={selectedDatasets || []}
                      columnDefs={columnDefs}
                      // onClickRow={(params: any) => {
                      //   console.log('다중 onClickRow', params);
                      // }}
                      // onCheck={(selectedIds: any[]) => {
                      //   console.log('다중 onSelect', selectedIds);
                      // }}
                    />
                  </UIListContentBox.Body>
                  <UIListContentBox.Footer className='ui-data-has-btn'>
                    <UIButton2 className='btn-option-outlined' style={{ width: '40px' }} onClick={openDatasetSelectModal}>
                      추가
                    </UIButton2>
                    <UIPagination currentPage={1} totalPages={1} onPageChange={() => {}} className='flex justify-center' />
                  </UIListContentBox.Footer>
                </UIListContainer>
              </div>
            </UIArticle>

            {/* 슬라이더 섹션 */}
            <UIArticle>
              <div className='w-full'>
                <UISlider
                  label='Validation Split'
                  value={validationRatio}
                  min={0}
                  max={1}
                  required={true}
                  showTextField={true}
                  onChange={verificationChange}
                  color='#2670FF'
                  decimalPlaces={1}
                />
              </div>
            </UIArticle>

            {/* 학습률 섹션 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb' required={true}>
                  Learning Rate
                </UITypography>
                <UIInput.Text value={learningRate.toString()} placeholder='학습률 입력' onChange={e => setLearningRate(Number(e.target.value))} disabled={false} />
              </UIFormField>
            </UIArticle>

            {/* 배치 사이즈 섹션 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb' required={true}>
                  Batch Size
                </UITypography>
                <UIInput.Text value={batchSize.toString()} placeholder='학습 횟수 입력' onChange={e => setBatchSize(Number(e.target.value))} disabled={false} />
              </UIFormField>
            </UIArticle>

            {/* 조기 종료 */}
            <UIArticle>
              <UIToggle label='Early Stopping' labelPosition='top' checked={earlyStopping} variant='basic' size='medium' onChange={setEarlyStopping} />
            </UIArticle>

            {/* 조기 종료 인내도 (조건부 표시) */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <div className='flex items-start'>
                  <UITypography variant='title-4' className='secondary-neutral-800 text-sb'>
                    Early Stopping Patience
                  </UITypography>
                  <UITooltip
                    trigger='click'
                    position='bottom-start'
                    type='notice'
                    title=''
                    items={['조기 종료 인내도는 입력한 학습횟수까지만 적용됩니다.']}
                    bulletType='default'
                    showArrow={false}
                    showCloseButton={true}
                    className='ml-1'
                  >
                    <UIButton2>
                      <UIIcon2 className='ic-system-20-info' />
                    </UIButton2>
                  </UITooltip>
                </div>
                <UIInput.Text value={earlyStoppingPatience.toString()} placeholder='3' onChange={e => setEarlyStoppingPatience(Number(e.target.value))} />
              </UIFormField>
            </UIArticle>
          </UIPopupBody>
        </section>
      </UILayerPopup>
    </>
  );
};
