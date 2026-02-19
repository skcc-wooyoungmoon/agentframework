import React, { memo, useMemo } from 'react';
import { useNavigate, useParams } from 'react-router-dom';

import { UIDataCnt, UILabel, type UILabelIntent, UITypography } from '@/components/UI/atoms';
import { UIPagination } from '@/components/UI/atoms/UIPagination';
import { UITextLabel } from '@/components/UI/atoms/UITextLabel';
import { UIGroup, UIUnitGroup } from '@/components/UI/molecules';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIPageFooter } from '@/components/UI/molecules/UIPageFooter';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';

import { useDeleteFineTuningTraining, useGetFineTuningTrainingById } from '@/services/model/fineTuning/modelFineTuning.service.ts';
import { type FineTuningStatus } from '@/stores/model/fineTuning/fineTuning.atoms.ts';

import { ManagerInfoBox } from '@/components/common';
import { Button } from '@/components/common/auth';
import { ProjectInfoBox } from '@/components/common/project/ProjectInfoBox/component';
import { AUTH_KEY } from '@/constants/auth';
import { FINE_TUNING_STATUS_MAP } from '@/constants/model/fineTuningStatus.constants';
import { useLayerPopup } from '@/hooks/common/layer';
import { ModelFineTuningEditPopupPage } from '@/pages/model/fineTuning/ModelFineTuningEditPopupPage.tsx';
import { ModelFineTuningLogPopupPage } from '@/pages/model/fineTuning/ModelFineTuningLogPopupPage.tsx';
import { useModal } from '@/stores/common/modal';
import { dateUtils } from '@/utils/common';

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
const parseTrainingConfigParams = (paramsString: string) => {
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

export const ModelFineTuningDetailPage = () => {
  const navigate = useNavigate();
  const { id } = useParams();
  const { openAlert, openConfirm, openModal } = useModal();
  const layerPopupOne = useLayerPopup();
  // const [selectedFineTuning] = useAtom(selectedFineTuningAtom);
  // const [showDeleteModal, setShowDeleteModal] = useState(false);

  // React Query hooks
  const { data: fineTuningData, refetch } = useGetFineTuningTrainingById({ id: id || '', isDataSet: true }, { enabled: !!id, staleTime: 0, refetchOnMount: 'always' });

  const {
    mutate: deleteTraining,
    // , isPending: isDeleting
  } = useDeleteFineTuningTraining({
    onSuccess: () => {
      openAlert({
        title: '완료',
        message: '파인튜닝이 삭제되었습니다.',
        onConfirm: () => {
          navigate('/model/finetuning');
        },
      });
    },
    onError: /* err */ () => {
      // console.error('파인튜닝 삭제 실패:', err);
    },
  });

  const projectInfoAssets = useMemo(() => {
    const assets = [{ type: 'finetuning', id: id || '' }];
    if (fineTuningData?.datasetIds) {
      fineTuningData?.datasetIds.forEach((datasetId: string) => {
        assets.push({ type: 'dataset', id: datasetId });
      });
    }
    return assets;
  }, [fineTuningData]);

  const handleLogView = () => {
    // 로그 조회 기능 구현 예정
    openModal({
      title: '콘솔 로그',
      type: 'large',
      body: <ModelFineTuningLogPopupPage trainingId={id} />,
      showFooter: false,
    });
  };

  // 삭제 함수
  const handleDelete = () => {
    if (!id) {
      openAlert({
        title: '안내',
        message: '삭제할 항목을 선택해주세요.',
      });
      return;
    }

    openConfirm({
      title: '안내',
      message: '삭제하시겠어요? \n삭제한 정보는 복구할 수 없습니다.',
      confirmText: '예',
      cancelText: '아니요',
      onConfirm: () => {
        deleteTraining({ id: id });
      },
      onCancel: () => {
        // console.log('취소됨');
      },
    });
  };

  // TrainingConfig 파라미터 파싱 결과를 메모이제이션
  const trainingConfig = useMemo(() => {
    return parseTrainingConfigParams(fineTuningData?.params || '');
  }, [fineTuningData?.params]);

  // 데이터세트 데이터 - fineTuningData에서 datasetDetails 사용
  const datasetData = useMemo(() => {
    if (!fineTuningData?.datasetDetails || !Array.isArray(fineTuningData.datasetDetails)) {
      return [];
    }

    return fineTuningData.datasetDetails.map((dataset: any, index: number) => {
      // tags 배열에서 name 속성 추출
      const tagNames = dataset.tags && Array.isArray(dataset.tags) ? dataset.tags.map((tag: any) => tag.name || tag).filter((tag: string) => tag && tag.trim() !== '') : [];

      const status = dataset.status as keyof typeof STATUS_CONFIG;
      const config = STATUS_CONFIG[status] || {
        label: status,
        intent: 'complete' as const,
      };

      // 날짜 포맷 변환 (ISO 형식을 yyyy-mm-dd hh:mm:ss 형식으로)
      const formatDate = (dateString: string) => {
        if (!dateString) return '날짜 없음';
        try {
          const date = new Date(dateString);
          const year = date.getFullYear();
          const month = String(date.getMonth() + 1).padStart(2, '0');
          const day = String(date.getDate()).padStart(2, '0');
          const hours = String(date.getHours()).padStart(2, '0');
          const minutes = String(date.getMinutes()).padStart(2, '0');
          const seconds = String(date.getSeconds()).padStart(2, '0');
          return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
        } catch {
          return '날짜 없음';
        }
      };

      return {
        no: index + 1,
        modelName: dataset.name || `데이터세트 ${index + 1}`,
        description: dataset.description || '설명 없음',
        tags: tagNames, // 배열로 저장 (04StepPopupPage와 동일한 방식)
        modelType: dataset.type || '유형 없음',
        status: config.label,
        statusValue: config.intent,
        createdDate: formatDate(dataset.createdAt || dataset.updatedAt),
        datasourceId: dataset.datasourceId,
        id: dataset.id,
        publicStatus: dataset.publicStatus,
      };
    });
  }, [fineTuningData?.datasetDetails]);

  // 그리드 컬럼 정의
  const columnDefs: any = useMemo(
    () => [
      {
        headerName: 'NO',
        field: 'no' as any,
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
        field: 'modelName',
        width: 272,
      },
      {
        headerName: '상태',
        field: 'status',
        width: 120,
        cellRenderer: memo((params: any) => {
          const statusValue = params.data.statusValue as FineTuningStatus;
          return (
            <UILabel variant='badge' intent={statusValue as any}>
              {params.value}
            </UILabel>
          );
        }),
      },
      {
        headerName: '설명',
        field: 'description',
        minWidth: 452,
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
              title={params.value}
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
      },
      {
        headerName: '태그',
        field: 'tags' as const,
        width: 230,
        cellStyle: { paddingLeft: '16px' },
        cellRenderer: (params: any) => {
          if (!params.value || !Array.isArray(params.value) || params.value.length === 0) {
            return null;
          }
          const tagText = params.value.join(', ');
          return (
            <div title={tagText}>
              <div className='flex gap-1'>
                {params.value.slice(0, 2).map((tag: string, index: number) => (
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
        field: 'modelType',
        width: 120,
        valueGetter: (params: any) => {
          const type = params.data.modelType;
          return type === 'dpo_finetuning' ? 'DPO' : type === 'supervised_finetuning' ? '지도학습' : type === 'unsupervised_finetuning' ? '비지도학습' : type || '';
        },
      },
      {
        headerName: '생성일시',
        field: 'createdDate' as any,
        width: 180,
        valueGetter: (params: any) => {
          return dateUtils.formatDate(params.data.createdDate, 'datetime');
        },
      },
    ],
    [datasetData]
  );

  return (
    <>
      <section className='section-page'>
        {/* 페이지 헤더 */}
        <UIPageHeader
          title='파인튜닝 조회'
          description=''
          actions={
            <>
              <Button className='btn-tertiary-outline line-only-blue' onClick={handleLogView}>
                로그 조회
              </Button>
            </>
          }
        />
        {/* 페이지 바디 */}
        <UIPageBody>
          {/* 테이블 */}
          <UIArticle>
            <div className='article-header'>
              <UITypography variant='title-4' className='secondary-neutral-900'>
                모델
              </UITypography>
            </div>
            <div className='article-body'>
              <div className='border-t border-black'>
                <table className='tbl-v'>
                  <colgroup>
                    <col style={{ width: '10%' }} />
                    <col style={{ width: '40%' }} />
                    <col style={{ width: '10%' }} />
                    <col style={{ width: '40%' }} />
                  </colgroup>
                  <tbody>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          공급자
                        </UITypography>
                      </th>
                      <td colSpan={3}>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {fineTuningData?.baseModelDetail?.provider_name || '공급자 정보 없음'}
                          <Button
                            className='btn-text-14-point ml-4'
                            rightIcon={{ className: 'ic-system-12-arrow-right-blue', children: '' }}
                            onClick={() => {
                              if (fineTuningData?.baseModelDetail?.id) {
                                navigate(`/model/modelCtlg/${fineTuningData.baseModelDetail.id}`);
                              }
                            }}
                          >
                            모델 바로가기
                          </Button>
                        </UITypography>
                      </td>
                    </tr>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          이름
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {fineTuningData?.baseModelDetail?.name || ''}
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          설명
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {fineTuningData?.baseModelDetail?.description || ''}
                        </UITypography>
                      </td>
                    </tr>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          유형
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {fineTuningData?.baseModelDetail?.type || ''}
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          배포 유형
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {fineTuningData?.baseModelDetail?.serving_type || ''}
                        </UITypography>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </UIArticle>

          {/* 테이블 */}
          <UIArticle>
            <div className='article-header'>
              <UITypography variant='title-4' className='secondary-neutral-900'>
                파인튜닝
              </UITypography>
            </div>
            <div className='article-body'>
              <div className='border-t border-black'>
                <table className='tbl-v'>
                  <colgroup>
                    <col style={{ width: '10%' }} />
                    <col style={{ width: '40%' }} />
                    <col style={{ width: '10%' }} />
                    <col style={{ width: '40%' }} />
                  </colgroup>
                  <tbody>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          제목
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {fineTuningData?.name || '데이터 없음'}
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          상태
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          <UILabel variant='badge' intent={FINE_TUNING_STATUS_MAP[fineTuningData?.status as keyof typeof FINE_TUNING_STATUS_MAP]?.intent as UILabelIntent}>
                            {FINE_TUNING_STATUS_MAP[fineTuningData?.status as keyof typeof FINE_TUNING_STATUS_MAP]?.label}
                          </UILabel>
                        </UITypography>
                      </td>
                    </tr>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          설명
                        </UITypography>
                      </th>
                      <td colSpan={3}>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {fineTuningData?.description || '설명 없음'}
                        </UITypography>
                      </td>
                    </tr>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          할당된 리소스 (CPU/Memory/GPU)
                        </UITypography>
                      </th>
                      <td colSpan={3}>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {fineTuningData?.resource?.cpu_quota || '0'}Core / {fineTuningData?.resource?.mem_quota || '0'}GiB / {fineTuningData?.resource?.gpu_quota || '0'}fGPU
                        </UITypography>
                      </td>
                    </tr>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          학습 유형
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {fineTuningData?.datasetDetails?.[0]?.type === 'dpo_finetuning'
                            ? 'DPO'
                            : fineTuningData?.datasetDetails?.[0]?.type === 'supervised_finetuning'
                              ? '지도학습'
                              : fineTuningData?.datasetDetails?.[0]?.type === 'unsupervised_finetuning'
                                ? '비지도학습'
                                : fineTuningData?.datasetDetails?.[0]?.type || ''}
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          Efficiency
                          <br />
                          Configuration(PEFT)
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {trainingConfig.use_lora === true ? 'LoRA' : trainingConfig.use_lora === false ? 'Full Fine-tuning' : ''}
                        </UITypography>
                      </td>
                    </tr>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          Fine Tuning
                          <br />
                          Techniques
                        </UITypography>
                      </th>
                      <td colSpan={3}>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          BASIC
                        </UITypography>
                      </td>
                    </tr>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          Epochs
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral혈00'>
                          {trainingConfig.num_train_epochs}
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          Validation Split
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {trainingConfig.validation_split}
                        </UITypography>
                      </td>
                    </tr>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          Learning Rate
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {trainingConfig.learning_rate}
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          Batch Size
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {trainingConfig.batch_size}
                        </UITypography>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </UIArticle>

          {/* 그리드 */}
          <UIArticle className='article-grid'>
            <UIListContainer>
              <UIListContentBox.Header>
                <div className='w-full'>
                  <UIUnitGroup gap={16} direction='column'>
                    <div className='flex justify-between w-full items-center'>
                      <div className='w-full'>
                        <UIGroup gap={12} direction='row' align='start'>
                          <div style={{ width: '200px', display: 'flex', alignItems: 'center' }}>
                            <UIDataCnt count={datasetData.length} prefix='학습 데이터세트 총' />
                          </div>
                        </UIGroup>
                      </div>
                    </div>
                  </UIUnitGroup>
                </div>
              </UIListContentBox.Header>
              <UIListContentBox.Body>
                <UIGrid
                  rowData={datasetData}
                  columnDefs={columnDefs}
                  onClickRow={(params: any) => {
                    const data = params.data;
                    navigate(`/data/dataCtlg/dataset/${data.id}?datasourceId=${data?.datasourceId}`);
                  }}
                />
              </UIListContentBox.Body>
              <UIListContentBox.Footer>
                <UIPagination currentPage={1} totalPages={1} onPageChange={() => {}} className='flex justify-center' />
              </UIListContentBox.Footer>
            </UIListContainer>
          </UIArticle>

          {/* 테이블 */}
          <ManagerInfoBox
            type='uuid'
            people={[
              { userId: fineTuningData?.createdBy || '', datetime: fineTuningData?.createdAt || '' },
              { userId: fineTuningData?.updatedBy || '', datetime: fineTuningData?.updatedAt || '' },
            ]}
          />
          <ProjectInfoBox assets={projectInfoAssets} auth={AUTH_KEY.MODEL.FINE_TUNING_CHANGE_PUBLIC} />
        </UIPageBody>

        {/* 페이지 footer */}
        <UIPageFooter>
          <UIArticle>
            <UIUnitGroup gap={8} direction='row' align='center'>
              <Button
                auth={AUTH_KEY.MODEL.FINE_TUNING_DELETE}
                className='btn-primary-gray'
                onClick={() => {
                  // setShowDeleteModal(true);
                  handleDelete();
                }}
              >
                삭제
              </Button>
              {['initialized', 'error'].includes(fineTuningData?.status || '') && (
                <Button
                  auth={AUTH_KEY.MODEL.FINE_TUNING_UPDATE}
                  className='btn-primary-blue'
                  onClick={() => {
                    layerPopupOne.onOpen();
                  }}
                >
                  수정
                </Button>
              )}
            </UIUnitGroup>
          </UIArticle>
        </UIPageFooter>
      </section>

      {layerPopupOne.currentStep === 1 && (
        <ModelFineTuningEditPopupPage
          isPopupOpen={layerPopupOne.currentStep === 1}
          onClose={() => {
            layerPopupOne.onClose();
          }}
          onSuccess={() => {
            refetch();
          }}
          trainingId={id || ''}
        />
      )}
    </>
  );
};
