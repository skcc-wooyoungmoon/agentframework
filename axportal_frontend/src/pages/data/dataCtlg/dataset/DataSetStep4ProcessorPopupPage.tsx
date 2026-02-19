import React, { useState, useMemo, useEffect } from 'react';

import { UIButton2, UIDataCnt, UIPagination, UITypography } from '@/components/UI/atoms';
import { UIImage } from '@/components/UI/atoms/UIImage';
import { UIArticle, UIFormField, UIPopupBody, UIPopupFooter, UIPopupHeader, UIStepper, UIUnitGroup } from '@/components/UI/molecules';
import { UILayerPopup, UIGrid, UIPopupAside } from '@/components/UI/organisms';
import { type UIStepperItem } from '@/components/UI/molecules';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { useGetProcList } from '@/services/data/tool/dataToolProc.services';
import type { GetToolRequest } from '@/services/data/tool/types';

import { useCreateS3TrainingDataset, useUploadDatasetFile } from '@/services/data/dataCtlgDataSet.services';
import type { CreateS3TrainingDatasetRequest, UploadDatasetFileRequest } from '@/services/data/types';
import { useModal } from '@/stores/common/modal';
import { useAtom } from 'jotai';
import { DataAtom } from '@/stores/data/dataStore';
import { useNavigate } from 'react-router-dom';

interface DataSetStep4ProcessorPopupPageProps {
  isOpen: boolean;
  stepperItems: UIStepperItem[];
  onClose: () => void;
  onPreviousStep: () => void;
  handlePopupClose: () => void;
  // selectedStorageData, uploadedFileInfos는 useAtom(DataAtom)으로 직접 접근
}

export function DataSetStep4ProcessorPopupPage({ isOpen, stepperItems = [], onClose, onPreviousStep, handlePopupClose }: DataSetStep4ProcessorPopupPageProps) {
  const navigate = useNavigate();
  const { openConfirm, openAlert } = useModal();
  const [toolType, setToolType] = useState<string[]>([]);
  const [selectOneOption, setSelectOneOption] = useState<Boolean>(false);
  const [selectedProcessors, setSelectedProcessors] = useState<any[]>([]);
  const [currentPage, setCurrentPage] = useState(1);

  // DataAtom에서 직접 가져오기 (prop 전달 불필요)
  const [dataForm] = useAtom(DataAtom);

  // dataForm에서 필요한 값들 추출
  const dataType = dataForm.dataType;
  const dataSet = dataForm.dataset;
  const importType = dataForm.importType;
  const datasetName = dataForm.name;
  const datasetDescription = dataForm.description;
  const datasetType = dataForm.dataset;
  const tags = dataForm.tags;
  const uploadedFiles = dataForm.uploadedFiles;
  const selectedStorageData = dataForm.selectedStorageData;
  const uploadedFileInfos = dataForm.uploadedFileInfos;

  // 팝업이 열릴 때마다 프로세서 관련 상태 초기화
  useEffect(() => {
    if (isOpen) {
      // console.log('=== Step4 팝업 열림 - 프로세서 상태 초기화 ===');
      setToolType([]);
      setSelectOneOption(false);
      setSelectedProcessors([]);
      setCurrentPage(1);
    }
  }, [isOpen]);

  // API 호출 파라미터
  const searchParams: GetToolRequest = useMemo(
    () => ({
      page: currentPage,
      size: 12,
      sort: 'created_at,desc',
    }),
    [currentPage]
  );

  // 프로세서 목록 API 호출
  const { data: processorData, isLoading } = useGetProcList(searchParams, { enabled: isOpen });

  // API 응답 데이터를 useMemo로 저장
  const processorList = useMemo(() => {
    if (!processorData?.content) {
      return [];
    }
    return processorData.content.map((item: any, index: number) => ({
      no: (currentPage - 1) * 12 + (index || 0) + 1,
      id: item.id,
      name: item.name,
      description: item.description,
    }));
  }, [processorData]);

  // 성공 시 공통 처리 함수
  const handleSuccess = (data: any) => {
    const handleComplete = () => {
      onClose();

      // 학습데이터셋 새로고침을 위한 이벤트 발생 
      // window.dispatchEvent(
      //   new CustomEvent('dataset-created', {
      //     detail: {
      //       datasetId: (data as any).data?.id,
      //       datasetName: (data as any).data?.name,
      //       message: data.message,
      //     },
      //   })
      // );

      // 상세 페이지로 이동 
      navigate(`/data/dataCtlg/dataset/${data.data?.datasetId}?datasourceId=${data.data?.datasourceId}`);
    };
    if (data.data.status === 'failed') {
      openAlert({
        title: '안내',
        message: `학습 데이터세트가 생성되었으나, 설정값 오류로 상태가 '실패'로 처리되었습니다. \n\n[사유 : ${data.data.errorMessage}]`,

        confirmText: '확인',
        onConfirm: handleComplete,
      });
    } else {
      openConfirm({
        title: '완료',
        message: '데이터세트만들기를 완료하였습니다.',
        confirmText: '확인',
        cancelText: '',
        onConfirm: handleComplete,
        onCancel: handleComplete,
      });
    }
  };

  // S3 훈련 데이터셋 생성 API hook
  const createS3TrainingDataset = useCreateS3TrainingDataset({
    onSuccess: (data: any) => {
      handleSuccess(data);
    },
    onError: (error: any) => {
      // 타임아웃 에러인 경우 특별한 메시지 표시
      if (error.error?.code === 'ECONNABORTED' || error.error?.hscode === 'timeout') {
        // console.log('요청 시간이 초과되었습니다. 백엔드에서 처리가 진행 중일 수 있습니다. 잠시 후 데이터셋 목록을 확인해주세요.');
      } else {
        // console.log('데이터셋 생성 중 오류가 발생했습니다: ' + (error.error?.message || error.message));
      }
    },
  });

  // Custom 학습 데이터 파일 업로드 API hook
  const uploadCustomDatasetFile = useUploadDatasetFile({
    onSuccess: (data: any) => {
      handleSuccess(data);
    },
    onError: (error: any) => {
      // 타임아웃 에러인 경우 특별한 메시지 표시
      if (error.error?.code === 'ECONNABORTED' || error.error?.hscode === 'timeout') {
        // console.log('요청 시간이 초과되었습니다. 백엔드에서 처리가 진행 중일 수 있습니다. 잠시 후 데이터셋 목록을 확인해주세요.');
      } else {
        // console.log('데이터셋 생성 중 오류가 발생했습니다: ' + (error.error?.message || error.message));
      }
    },
  });

  // 그리드 컬럼 정의
  const columnDefs: any = useMemo(
    () =>
      [
        {
          headerName: 'NO',
          field: 'no' as const,
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
          },
          sortable: false,
          suppressHeaderMenuButton: true,
          suppressSizeToFit: true,
          // valueGetter: (params: any) => {
          //   return (currentPage - 1) * 12 + (params.node?.rowIndex || 0) + 1;
          // },
        },
        {
          headerName: '이름',
          field: 'name' as const,
          width: 272,
          cellStyle: { paddingLeft: '16px' },
        },
        {
          headerName: '설명',
          field: 'description' as const,
          flex: 1,
          showTooltip: true,
          cellStyle: { paddingLeft: '16px' },
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
      ] as any,
    []
  );

  const radioOptions = [
    {
      value: 'no',
      label: 'No',
      image: '/assets/images/data/ico-radio-data-number.svg',
      alt: 'No',
    },
    {
      value: 'system',
      label: 'System',
      image: '/assets/images/data/ico-radio-visual08.svg',
      alt: 'System',
    },
    {
      value: 'user',
      label: 'User',
      image: '/assets/images/data/ico-radio-visual06.svg',
      alt: 'User',
    },
    {
      value: 'assistant',
      label: 'Assistant',
      image: '/assets/images/data/ico-radio-visual07.svg',
      alt: 'Assistant',
    },
    {
      value: 'text',
      label: 'Text',
      image: '/assets/images/data/ico-radio-visual11.svg',
      alt: 'Text',
    },
    {
      value: 'chosen',
      label: 'Chosen',
      image: '/assets/images/data/ico-radio-visual09.svg',
      alt: 'Chosen',
    },
    {
      value: 'rejected',
      label: 'Rejected',
      image: '/assets/images/data/ico-radio-visual10.svg',
      alt: 'Rejected',
    },
  ];

  // 중복제거할 항목 표시 함수
  const displayRadioItems = () => {
    switch (dataSet) {
      // No, System, User, Assistant
      case '지도학습':
        return radioOptions.slice(0, 4);
      // No, Text
      case '비지도학습':
        return [radioOptions[0], radioOptions[4]];
      // No, chosen, rejected
      case 'DPO':
        return [radioOptions[2], radioOptions[5], radioOptions[6]];
      case 'Custom':
        return radioOptions.slice(0, 0);
      default:
        return radioOptions.slice(0, 4);
    }
  };

  // 중복제거할 항목에서 첫 번째 항목을 기본 선택
  useEffect(() => {
    const items = displayRadioItems();
    if (items.length > 0 && toolType.length === 0) {
      setToolType([items[0].value]);
    }
  }, [dataSet, toolType.length]);

  // 프로세서 정보 구성 함수
  const buildProcessorData = () => {
    if (selectedProcessors.length === 0) {
      return null; // 프로세서가 선택되지 않은 경우
    }

    // 중복제거할 항목 매핑
    const duplicateColumnMapping: { [key: string]: string } = {
      no: 'no',
      System: 'system',
      User: 'user',
      Assistant: 'assistant',
    };

    // 선택된 중복제거할 항목들을 컬럼명으로 변환
    const duplicateSubsetColumns = toolType.map(value => duplicateColumnMapping[value] || value);

    return {
      ids: selectedProcessors.map(processor => processor.id),
      duplicate_subset_columns: duplicateSubsetColumns,
      regular_expression: [], // 현재는 빈 배열로 설정
    };
  };

  const handleCreate = () => {
    if (datasetType === 'Custom' && importType === 'local' && uploadedFiles && uploadedFiles.length > 0) {
      // console.log('=== Custom 학습 데이터 + 파일 업로드 - 새로운 API 호출 ===');

      // 각 파일에 대해 개별적으로 API 호출
      uploadedFiles.forEach((file /* , index */) => {
        // 태그 배열을 JSON 문자열로 변환: [{"name":"tag1"}, {"name":"tag2"}]
        const tagsJsonString = tags && tags.length > 0 ? JSON.stringify(tags.map(tag => ({ name: tag }))) : '';

        // Custom 학습 데이터인 경우 항상 'custom' 타입으로 설정
        const finalType = 'custom'; // Custom 학습 데이터는 항상 'custom' 타입

        const requestData: UploadDatasetFileRequest = {
          name: datasetName || file.name.replace(/\.[^/.]+$/, ''), // Step1에서 입력한 이름 또는 파일명
          type: finalType, // Custom 학습 데이터는 항상 'custom' 타입
          status: null as any, // null로 전송
          description: datasetDescription || `Custom 학습 데이터 - ${file.name}`, // Step1에서 입력한 설명
          tags: tagsJsonString, // Step1에서 입력한 태그들 (JSON 문자열)
          projectId: null as any, // null로 전송
          createdBy: null as any, // null로 전송
          updatedBy: null as any, // null로 전송
          payload: null as any, // null로 전송
        };

        // console.log(`Custom 데이터셋 파일 업로드 API 호출 시작... (${index + 1}/${uploadedFiles.length})`, file.name);
        uploadCustomDatasetFile.mutate({ file, requestData });
      });

      return; // 새로운 API 호출 후 함수 종료
    }

    let fileNames: string | null = null;
    let tempFiles: any[] | null = null;

    if (importType === 'local') {
      // 로컬 업로드인 경우
      // file_names는 null로 전송
      fileNames = null;

      // temp_files는 업로드 API 응답 데이터를 그대로 사용
      if (uploadedFileInfos && uploadedFileInfos.length > 0) {
        // console.log('업로드된 파일 정보로 temp_files 구성 중...');
        tempFiles = uploadedFileInfos.map(fileInfo => ({
          file_name: fileInfo.fileName,
          temp_file_path: fileInfo.tempFilePath,
          file_metadata: fileInfo.fileMetadata || {},
          knowledge_config: fileInfo.knowledgeConfig || {},
        }));
        // console.log('구성된 temp_files:', tempFiles);
      } else {
        // console.warn('uploadedFileInfos가 비어있거나 없습니다!');
      }
    } else {
      // 데이터 저장소인 경우
      // file_names에 파일명들을 쉼표로 구분하여 전송
      fileNames = selectedStorageData.map(item => item.title || item.fileName || item.name || 'unknown').join(',');
      // temp_files는 null
      tempFiles = null;
    }

    // importType에 따라 type 결정
    const requestType = importType === 'local' ? 'file' : 's3';

    // 데이터셋 유형 매핑 함수
    const getDatasetType = (datasetType: string): string => {
      const typeMapping: { [key: string]: string } = {
        지도학습: 'supervised_finetuning',
        비지도학습: 'unsupervised_finetuning',
        DPO: 'dpo_finetuning',
        Custom: 'custom',
      };
      return typeMapping[datasetType] || '';
    };

    // 태그 배열을 { name: string }[] 형태로 변환
    const formattedTags = tags.map(tag => ({ name: tag }));

    // 프로세서 정보 구성
    const processorData = buildProcessorData();

    const requestData: CreateS3TrainingDatasetRequest = {
      source_bucket_name: null, // null로 전송
      file_names: fileNames, // 데이터 저장소: 파일명들, 로컬 업로드: null
      name: datasetName, // Step 1에서 입력한 이름
      type: requestType, // 's3' 또는 'file'
      description: datasetDescription, // Step 1에서 입력한 설명
      project_id: null, // null로 전송
      created_by: null, // null로 전송
      updated_by: null, // null로 전송
      is_deleted: false,
      scope: null, // null로 전송
      temp_files: tempFiles, // 로컬 업로드: temp_files, 데이터 저장소: null
      policy: null, // null로 전송
      dataset_type: getDatasetType(datasetType), // Step 1에서 입력한 데이터셋 유형 (매핑된 값)
      tags: formattedTags, // Step 1에서 입력한 태그들
      processor: processorData, // 프로세서 정보 추가
    };

    createS3TrainingDataset.mutate(requestData);
  };

  const handleToolTypeChange = (value: string) => {
    setToolType(prev => {
      if (prev.includes(value)) {
        // 이미 선택된 항목이면 제거
        return prev.filter(item => item !== value);
      } else {
        // 선택되지 않은 항목이면 추가
        return [...prev, value];
      }
    });
  };

  // selectedProcessors 변경 시 '중복데이터 제거' 프로세서 확인
  useEffect(() => {
    const findId = selectedProcessors.find((item: any) => item.name === '중복데이터 제거');
    if (findId) {
      setSelectOneOption(true);
    } else {
      setSelectOneOption(false);
    }
  }, [selectedProcessors]);

  return (
    <UILayerPopup
      isOpen={isOpen}
      onClose={onClose}
      size='fullscreen'
      showOverlay={true}
      leftContent={
        /* 좌측 Step 영역 콘텐츠 */
        <UIPopupAside>
          <UIPopupHeader title='학습 데이터세트 생성' position='left' />
          <UIPopupBody>
            <UIArticle>
              <UIStepper currentStep={4} items={dataType === 'basicDataSet' ? stepperItems : stepperItems.slice(0, 3)} direction='vertical' />
            </UIArticle>
          </UIPopupBody>
          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <UIButton2 className='btn-tertiary-gray' style={{ width: '80px' }} onClick={handlePopupClose}>
                  취소
                </UIButton2>
                <UIButton2 className='btn-tertiary-blue' style={{ width: 80 }} onClick={handleCreate} disabled={selectOneOption && toolType.length === 0}>
                  만들기
                </UIButton2>
              </UIUnitGroup>
            </UIArticle>
          </UIPopupFooter>
        </UIPopupAside>
      }
    >
      <section className='section-popup-content'>
        <UIPopupHeader title='프로세서 선택' description='' position='right' />
        <UIPopupBody>
          <UIArticle className='article-grid'>
            <UIListContainer>
              <UIListContentBox.Header>
                <div className='flex items-center'>
                  <div style={{ width: '182px', paddingRight: '8px' }}>
                    <UIDataCnt count={processorData?.totalElements || 0} prefix='프로세서 총' />
                  </div>
                </div>
              </UIListContentBox.Header>
              <UIListContentBox.Body>
                <UIGrid
                  type='multi-select'
                  loading={isLoading}
                  rowData={processorList || []}
                  columnDefs={columnDefs}
                  onCheck={(selectedIds: any[]) => {
                    setSelectedProcessors(selectedIds);
                    // API 데이터에서 '중복데이터 제거' 프로세서 찾기
                    // const findId = selectedIds.find((item: any) => item.name === '중복데이터 제거');
                    // if (findId) {
                    //   setSelectOneOption(true);
                    //   // console.log(selectOneOption);
                    // } else {
                    //   setSelectOneOption(false);
                    // }
                  }}
                />
              </UIListContentBox.Body>
            </UIListContainer>
            <UIListContentBox.Footer>
              <UIPagination
                currentPage={currentPage}
                hasNext={processorData?.hasNext}
                totalPages={processorData?.totalPages || 1}
                onPageChange={(page: number) => {
                  setCurrentPage(page);
                }}
                className='flex justify-center'
              />
            </UIListContentBox.Footer>
          </UIArticle>
          <UIArticle>
            {selectOneOption && displayRadioItems().length > 0 && (
              <UIFormField gap={16} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb' required={true}>
                  중복제거할 항목
                </UITypography>
                <div className='flex gap-6'>
                  {displayRadioItems().map(option => (
                    <div key={option.value} className='flex flex-col space-y-3'>
                      <div
                        className={`w-[298px] h-[200px] rounded-[20px] p-6 cursor-pointer transition-all duration-200 flex items-center justify-center ${toolType.includes(option.value) ? 'bg-[#f3f6fb] border-2 border-[#005df9]' : 'bg-col-gray-100 border-2 border-transparent hover:bg-gray-100'
                          }`}
                        onClick={() => handleToolTypeChange(option.value)}
                      >
                        <UIImage src={option.image} alt={option.alt} className='max-w-full max-h-full' />
                        {/* 선택 표시 */}
                        {/* {toolType.includes(option.value) && (
                          <div className='absolute top-2 right-2 w-6 h-6 bg-[#005df9] rounded-full flex items-center justify-center'>
                            <svg className='w-4 h-4 text-white' fill='currentColor' viewBox='0 0 20 20'>
                              <path
                                fillRule='evenodd'
                                d='M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z'
                                clipRule='evenodd'
                              />
                            </svg>
                          </div>
                        )} */}
                      </div>
                      <div className='space-y-1'>
                        <div className='flex items-center gap-2'>
                          <UITypography variant='body-1' className='secondary-neutral-800'>
                            {option.label}
                          </UITypography>
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              </UIFormField>
            )}
          </UIArticle>
        </UIPopupBody>
        <UIPopupFooter>
          <UIArticle>
            <UIUnitGroup gap={8} direction='row' align='start'>
              <UIButton2 className='btn-secondary-gray' onClick={onPreviousStep}>
                이전
              </UIButton2>
            </UIUnitGroup>
          </UIArticle>
        </UIPopupFooter>
      </section>
    </UILayerPopup>
  );
}
