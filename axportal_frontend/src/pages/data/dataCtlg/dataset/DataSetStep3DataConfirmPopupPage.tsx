import { UIButton2, UIFileBox, UITypography } from '@/components/UI/atoms';
import { UIArticle, UIGroup, UIList, UIPopupBody, UIPopupFooter, UIPopupHeader, UIStepper, UIUnitGroup } from '@/components/UI/molecules';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { type UIStepperItem } from '@/components/UI/molecules';
import { UIDataCnt } from '@/components/UI/';
import { useCreateS3TrainingDataset, useUploadDatasetFile, useCreateCustomDatasetFromStorage } from '@/services/data/dataCtlgDataSet.services';
import type { CreateS3TrainingDatasetRequest, UploadDatasetFileRequest } from '@/services/data/types';
import { useModal } from '@/stores/common/modal';
import { useAtom } from 'jotai';
import { DataAtom } from '@/stores/data/dataStore';
import { useNavigate } from 'react-router-dom';

interface DataSetStep3DataConfirmPopupPageProps {
  isOpen: boolean;
  stepperItems: UIStepperItem[];
  onClose: () => void;
  onNextStep: () => void;
  onPreviousStep: () => void;
  handlePopupClose: () => void;
}

export function DataSetStep3DataConfirmPopupPage({ isOpen, stepperItems = [], onClose, onNextStep, onPreviousStep, handlePopupClose }: DataSetStep3DataConfirmPopupPageProps) {
  const { openAlert } = useModal();
  const navigate = useNavigate();
  // DataAtom에서 직접 가져오기 (prop 전달 불필요)
  const [dataForm, setDataForm] = useAtom(DataAtom);

  // console.log('3333333333333333... dataForm: ', dataForm);

  // dataForm에서 필요한 값들 추출
  const files = dataForm.files;
  const uploadedFiles = dataForm.uploadedFiles;
  const dataType = dataForm.dataType;
  const importType = dataForm.importType;
  const datasetName = dataForm.name;
  const datasetDescription = dataForm.description;
  const datasetType = dataForm.dataset;
  const tags = dataForm.tags;
  const selectedStorageData = dataForm.selectedStorageData;
  const uploadedFileInfos = dataForm.uploadedFileInfos;

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
      navigate(`/data/dataCtlg/dataset/${data.data.id}`);
    };

    openAlert({
      title: '완료',
      message: '데이터세트만들기를 완료하였습니다.',
      confirmText: '확인',
      onConfirm: handleComplete,
    });
  };

  // S3 훈련 데이터셋 생성 API hook
  const createS3TrainingDataset = useCreateS3TrainingDataset({
    onSuccess: (data: any) => {
      // console.log('=== API 응답 데이터 ===');
      // console.log(JSON.stringify(data, null, 2));
      // console.log('S3 훈련 데이터셋 생성 성공');
      handleSuccess(data);
    },
  });

  // Custom 학습 데이터 파일 업로드 API hook
  const uploadCustomDatasetFile = useUploadDatasetFile({
    onSuccess: (data: any) => {
      // console.log('=== Custom 데이터셋 파일 업로드 API 응답 데이터 ===');
      // console.log(JSON.stringify(data, null, 2));
      // console.log('Custom 데이터셋 파일 업로드 성공');
      handleSuccess(data);
    },
  });

  // Custom 학습 데이터 데이터저장소 API hook
  const createCustomDatasetFromStorage = useCreateCustomDatasetFromStorage({
    onSuccess: (data: any) => {
      handleSuccess(data);
    },
  });

  // 파일 제거 핸들러
  const handleFileRemove = (index: number) => {
    setDataForm((prev: any) => ({
      ...prev,
      files: prev.files.filter((_: any, i: number) => i !== index),
      uploadedFiles: prev.uploadedFiles.filter((_: any, i: number) => i !== index),
      uploadedFileInfos: prev.uploadedFileInfos.filter((_: any, i: number) => i !== index),
    }));
  };

  // 저장소 데이터 제거 핸들러
  const handleStorageDataRemove = (index: number) => {
    setDataForm((prev: any) => ({
      ...prev,
      selectedStorageData: prev.selectedStorageData.filter((_: any, i: number) => i !== index),
    }));
  };

  const handleCreate = () => {
    // Custom 학습 데이터세트인 경우 프로세서 선택 단계를 건너뛰고 바로 데이터셋 생성
    if (dataType === 'customDataSet') {
      // console.log('=== Custom 학습 데이터세트 - 프로세서 선택 단계 건너뛰고 바로 생성 ===');


      // Custom 학습 데이터 + 파일 업로드인 경우 새로운 API 호출
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

      // Custom + 데이터저장소인 경우 custom API 호출
      if (datasetType === 'Custom' && importType === 'storage' && selectedStorageData && selectedStorageData.length > 0) {
        // 각 선택된 데이터에 대해 개별적으로 API 호출
        selectedStorageData.forEach(item => {
          // 태그 배열을 JSON 문자열로 변환: [{"name":"tag1"}, {"name":"tag2"}]
          const tagsJsonString = tags && tags.length > 0 ? JSON.stringify(tags.map(tag => ({ name: tag }))) : '';

          // Custom 학습 데이터인 경우 항상 'custom' 타입으로 설정
          const finalType = 'custom'; // Custom 학습 데이터는 항상 'custom' 타입

          const fileName = item.title || item.fileName || item.name || 'unknown';

          const requestData: UploadDatasetFileRequest = {
            name: datasetName || fileName, // Step1에서 입력한 이름 또는 파일명
            type: finalType, // Custom 학습 데이터는 항상 'custom' 타입
            status: null as any, // null로 전송
            description: datasetDescription || `Custom 학습 데이터 - ${fileName}`, // Step1에서 입력한 설명
            tags: tagsJsonString, // Step1에서 입력한 태그들 (JSON 문자열)
            projectId: null as any, // null로 전송
            createdBy: null as any, // null로 전송
            updatedBy: null as any, // null로 전송
            payload: null as any, // null로 전송
          };

          createCustomDatasetFromStorage.mutate({ fileName: fileName, sourceType: 's3', requestData });
        });

        return; // custom API 호출 후 함수 종료
      }

      // Custom이지만 다른 조건인 경우 (예: 로컬 업로드인데 파일이 없는 경우 등)
      // 기본 S3 훈련 데이터셋 생성 API 사용
      // console.log('=== Custom 학습 데이터 - 기본 S3 API 사용 ===');

      let fileNames: string | null = null;
      let tempFiles: any[] | null = null;

      if (importType === 'local') {
        // 로컬 업로드인 경우
        fileNames = null;

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
        fileNames = selectedStorageData.map(item => item.title || item.fileName || item.name || 'unknown').join(',');
        tempFiles = null;
      }

      const requestType = importType === 'local' ? 'file' : 's3';

      // 데이터셋 유형 매핑
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

      const requestData: CreateS3TrainingDatasetRequest = {
        source_bucket_name: null,
        file_names: fileNames,
        name: datasetName,
        type: requestType,
        description: datasetDescription,
        project_id: null,
        created_by: null,
        updated_by: null,
        is_deleted: false,
        scope: null,
        temp_files: tempFiles,
        policy: null,
        dataset_type: getDatasetType(datasetType),
        tags: formattedTags,
        processor: null,
      };

      // console.log('=== API 요청 데이터 ===');
      // console.log(JSON.stringify(requestData, null, 2));
      createS3TrainingDataset.mutate(requestData);

      return;
    }

    // 기본 학습 데이터세트인 경우에만 Step 4로 이동 (프로세서 선택)
    // console.log('기본 학습 데이터세트 - Step 4로 이동');
    onNextStep();
  };

  return (
    <UILayerPopup
      isOpen={isOpen}
      onClose={onClose}
      size='fullscreen'
      showOverlay={true}
      leftContent={
        <UIPopupAside>
          <UIPopupHeader title='학습 데이터세트 생성' position='left' />
          <UIPopupBody>
            <UIArticle>
              <UIStepper currentStep={3} items={dataType === 'basicDataSet' ? stepperItems : stepperItems.slice(0, 3)} direction='vertical' />
            </UIArticle>
          </UIPopupBody>
          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <UIButton2 className='btn-tertiary-gray' style={{ width: 80 }} onClick={handlePopupClose}>
                  취소
                </UIButton2>
                <UIButton2
                  className='btn-tertiary-blue'
                  style={{ width: 80 }}
                  onClick={handleCreate}
                  disabled={
                    dataType === 'basicDataSet' || (importType === 'storage' && selectedStorageData?.length === 0) || (importType === 'local' && uploadedFiles?.length === 0)
                  }
                >
                  만들기
                </UIButton2>
              </UIUnitGroup>
            </UIArticle>
          </UIPopupFooter>
        </UIPopupAside>
      }
    >
      <section className='section-popup-content'>
        <UIPopupHeader title='선택 데이터 확인' position='right' />
        <UIPopupBody>
          <UIArticle>
            <div className='filebox-item bg-gray-100 rounded-xl px-6 py-5 flex items-center justify-between'>
              <UIList
                gap={4}
                direction='column'
                className='ui-list_important'
                data={[
                  {
                    dataItem: (
                      <UITypography variant='body-2' className='secondary-neutral-600 text-sb'>
                        한 번 생성한 데이터세트의 파일 구성은 변경할 수 없습니다. 추가한 데이터를 다시 한 번 확인 해주세요.
                      </UITypography>
                    ),
                  },
                ]}
              />
            </div>
          </UIArticle>
          <UIArticle>
            <UIUnitGroup gap={16} direction='column'>
              <UIDataCnt count={importType === 'storage' ? selectedStorageData?.length || 0 : files?.length || 0} prefix='선택된 데이터 총' />
              <UIGroup gap={16} direction='column'>
                <div>
                  {/* 데이터 저장소에서 선택된 데이터 표시 */}
                  {importType === 'storage' && selectedStorageData && selectedStorageData.length > 0 && (
                    <div className='space-y-3'>
                      {selectedStorageData.map((item, index) => (
                        <UIFileBox
                          key={index}
                          variant='default'
                          size='full'
                          fileName={item.title || '제목 없음'}
                          className='w-full'
                          onFileRemove={() => handleStorageDataRemove(index)}
                        />
                      ))}
                    </div>
                  )}

                  {/* 로컬 파일 업로드 데이터 표시 */}
                  {importType === 'local' && files && files.length > 0 && (
                    <div className='space-y-3'>
                      {files.map((fileName, index) => {
                        const matchedFile = uploadedFiles?.find(file => file.name === fileName);
                        const fileSize = matchedFile ? Math.round(matchedFile.size / 1024) : 0;
                        //const fileSize = uploadedFiles && uploadedFiles[index] ? Math.round(uploadedFiles[index].size / 1024) : 99;
                        return (
                          <UIFileBox
                            key={index}
                            variant='default'
                            size='full'
                            fileName={fileName}
                            fileSize={fileSize}
                            onFileRemove={() => handleFileRemove(index)}
                            className='w-full'
                          />
                        );
                      })}
                    </div>
                  )}
                </div>
                <div>
                  <UIList
                    gap={4}
                    direction='column'
                    className='ui-list_bullet'
                    data={[
                      {
                        dataItem: (
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            {datasetType === 'Custom'
                              ? 'Custom 유형의 학습 데이터세트는 업로드 파일 크기 제한이 없습니다.'
                              : '단일 파일 최대 업로드 크기 : 100MB, 전체 파일 최대 업로드 크기 : 200MB'}
                          </UITypography>
                        ),
                      },
                    ]}
                  />
                </div>
              </UIGroup>
            </UIUnitGroup>
          </UIArticle>
        </UIPopupBody>
        <UIPopupFooter>
          <UIArticle>
            <UIUnitGroup gap={8} direction='row' align='start'>
              <UIButton2 className='btn-secondary-gray' style={{ width: 80 }} onClick={onPreviousStep}>
                이전
              </UIButton2>
              {dataType !== 'customDataSet' && (
                <UIButton2
                  className='btn-secondary-blue'
                  style={{ width: 80 }}
                  onClick={onNextStep}
                  disabled={(importType === 'storage' && selectedStorageData?.length === 0) || (importType === 'local' && files?.length === 0)}
                >
                  다음
                </UIButton2>
              )}
            </UIUnitGroup>
          </UIArticle>
        </UIPopupFooter>
      </section>
    </UILayerPopup>
  );
}
