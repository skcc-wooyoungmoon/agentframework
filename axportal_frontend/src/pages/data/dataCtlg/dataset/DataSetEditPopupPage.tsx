import React, { useState, useEffect } from 'react';

import { UIButton2, UITypography } from '@/components/UI/atoms';
import { UIArticle, UIFormField, UIPopupBody, UIPopupFooter, UIPopupHeader } from '@/components/UI/molecules';
import { UIInput, UITextArea2 } from '@/components/UI/molecules/input';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { UIUnitGroup } from '@/components/UI/molecules';
import { useCommonPopup } from '@/hooks/common/popup/useCommonPopup';
import type { LayerPopupProps } from '@/hooks/common/layer';
import { useGetDatasetById, useUpdateDataset, useUpdateDatasetTags, useDeleteDatasetTags } from '@/services/data/dataCtlgDataSet.services';

// LayerPopupProps를 받도록 수정
interface DataSetEditPopupPageProps extends LayerPopupProps {
  mode: 'ApiCall' | 'ValueBind'; //
  datasetId?: string; //
  initialData?: {
    // 초기 데이터 (상세 페이지에서 전달)
    name: string;
    description: string;
    type: string;
    tags: string[];
    projectId: string;
    datasetId: string;
  };
  onDatasetListRefresh?: () => void; // DatasetList 페이지 새로고침 콜백
  onDatasetDetailRefresh?: () => void; // DatasetDetail 페이지 새로고침 콜백
}

export const DataSetEditPopupPage: React.FC<DataSetEditPopupPageProps> = ({ currentStep, onClose, datasetId, initialData, mode, onDatasetListRefresh, onDatasetDetailRefresh }) => {
  // API 호출 조건부 처리
  const { data: datasetData, refetch: refetchDataset } = useGetDatasetById(mode === 'ApiCall' && datasetId ? { datasetId } : undefined);

  // 데이터셋 Id
  const paramDatasetId = mode === 'ApiCall' ? datasetId : initialData?.datasetId;

  // 사용할 데이터 결정
  const currentData = mode === 'ApiCall' ? datasetData : initialData;

  //  상태 초기화
  const [datasetName, setDatasetName] = useState('');
  const [datasetDescription, setDatasetDescription] = useState('');
  const [tags, setTags] = useState<string[]>([]);
  const removedTags = (currentData?.tags as { name: string }[] | undefined) || [];

  // 공통 팝업 훅
  const { showEditComplete, showCancelConfirm, showNoEditContent } = useCommonPopup();

  // 데이터셋 업데이트
  const updateDatasetMutation = useUpdateDataset({
    onSuccess: () => {
      // console.log('데이터셋 업데이트 성공');
      showEditComplete({
        onConfirm: () => {
          handleClose();
        },
      });
      //handleClose();
    },
    onError: /* error */ () => {
      // console.error('데이터셋 업데이트 실패:', error);
    },
  });

  // 데이터셋 태그 업데이트
  const updateDatasetTags = useUpdateDatasetTags({
    onSuccess: () => {
      // console.log('데이터셋 태그 업데이트 성공');
    },
    onError: /* error */ () => {
      // console.error('데이터셋 태그 업데이트 실패:', error);
    },
  });

  // 데이터셋 태그 삭제
  const { mutate: deleteDatasetTags } = useDeleteDatasetTags({
    onSuccess: () => {
      // console.log('데이터셋 태그 삭제 성공');
    },
    onError: /* error */ () => {
      // console.error('데이터셋 태그 삭제 실패:', error);
    },
  });

  // 팝업이 열릴 때마다 API 호출 (동일한 datasetid여도, currentStep이 1이 되면)
  useEffect(() => {
    if (currentStep === 1 && mode === 'ApiCall' && datasetId) {
      refetchDataset();
    }
  }, [currentStep, mode, datasetId, refetchDataset]);

  // currentData가 변경되면 상태 업데이트
  useEffect(() => {
    if (currentData) {
      setDatasetName(currentData.name || '');
      setDatasetDescription(currentData.description || '');
      if (currentData?.tags && Array.isArray(currentData.tags)) {
        //console.log('currentData.tags:', currentData.tags);
        //const tagNames = currentData.tags.map(tag => tag.name);
        const tagNames = (currentData.tags as unknown as { name: string }[]).map(tag => tag.name);
        //console.log('tagNames:', tagNames);
        setTags(tagNames);
      }
    }
  }, [currentData, datasetId, mode]);

  //console.log('mode:', mode);
  //console.log('currentData:', currentData);
  //console.log('datasetName:', datasetName);

  const handleClose = () => {
    // 모든 state 초기화
    setDatasetName('');
    setDatasetDescription('');
    setTags([]);

    onClose();
  };

  const handleCancel = () => {
    // console.log('취소 버튼 클릭');
    showCancelConfirm({
      onConfirm: () => {
        handleClose();
      },
    });
  };

  const handleSave = async () => {
    // console.log('저장클릭');

    // 수정 내용이 있는지 확인
    const originalDescription = currentData?.description || '';
    const hasDescriptionChanged = datasetDescription.trim() !== originalDescription.trim();

    // 태그 비교
    const originalTags = currentData?.tags ? (currentData.tags as unknown as { name: string }[]).map(tag => tag.name).sort() : [];
    const currentTags = [...tags].sort();
    const hasTagsChanged = JSON.stringify(currentTags) !== JSON.stringify(originalTags);

    // 변경사항이 없으면 알림 표시
    if (!hasDescriptionChanged && !hasTagsChanged) {
      showNoEditContent({});
      return;
    }

    /* console.log(
      '>>>>>>tags:',
      tags.map(tag => ({ name: tag }))
    ); */
    //console.log('>>>>>>removedTags:', JSON.stringify(removedTags));

    try {
      // 1. 데이터셋 태그 삭제 먼저 실행
      if (removedTags.length > 0) {
        await new Promise<void>((resolve, reject) => {
          deleteDatasetTags(
            {
              datasetId: paramDatasetId || '',
              tags: removedTags,
            },
            {
              onSuccess: () => {
                // console.log('태그 삭제 성공');
                resolve();
              },
              onError: error => {
                // console.error('태그 삭제 실패:', error);
                reject(error);
              },
            }
          );
        });
      }

      // 2. 태그 삭제 완료 후 데이터셋 태그 업데이트
      if (tags.length > 0) {
        await new Promise<void>((resolve, reject) => {
          updateDatasetTags.mutate(
            {
              datasetId: paramDatasetId || '',
              tags: tags.map(tag => ({ name: tag })),
            },
            {
              onSuccess: () => {
                // console.log('태그 업데이트 성공');
                resolve();
              },
              onError: error => {
                // console.error('태그 업데이트 실패:', error);
                reject(error);
              },
            }
          );
        });
      }

      // 3. 데이터셋 업데이트
      await new Promise<void>((resolve, reject) => {
        updateDatasetMutation.mutate(
          {
            datasetId: paramDatasetId || '',
            description: datasetDescription,
            projectId: currentData?.projectId || '',
          },
          {
            onSuccess: () => {
              // console.log('데이터셋 업데이트 성공');
              resolve();
            },
            onError: error => {
              // console.error('데이터셋 업데이트 실패:', error);
              reject(error);
            },
          }
        );
      });
      // 4. 모든 작업 완료 후 콜백 실행
      // mode에 따라 적절한 콜백 호출
      if (mode === 'ApiCall' && onDatasetListRefresh) {
        // console.log('DatasetList 페이지 새로고침');
        onDatasetListRefresh();
      } else if (mode === 'ValueBind' && onDatasetDetailRefresh) {
        // console.log('DatasetDetail 페이지 새로고침');
        onDatasetDetailRefresh();
      }

      // console.log('mode:', mode);
      // console.log('onDatasetListRefresh:', onDatasetListRefresh);
      // console.log('onDatasetDetailRefresh:', onDatasetDetailRefresh);
    } catch (error) {
      // console.error('저장 중 에러 발생:', error);
    }
  };

  return (
    <>
      {/* DesignLayout 위에 높은 z-index로 뜨는 UILayerPopup */}
      <UILayerPopup
        isOpen={currentStep === 1}
        onClose={handleClose}
        size='fullscreen'
        showOverlay={true}
        leftContent={
          /* 좌측 Step 영역 콘텐츠 */
          <UIPopupAside>
            {/* 레이어 팝업 헤더 */}
            <UIPopupHeader title='학습 데이터세트 수정' description='' position='left' />
            {/* <UIPopupBody></UIPopupBody> */}
            {/* 레이어 팝업 바디 : [참고] 이 페이지에는 왼쪽 body 영역 없음. */}
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <UIButton2 className='btn-tertiary-gray' style={{ width: '80px' }} onClick={handleCancel}>
                    취소
                  </UIButton2>
                  <UIButton2 className='btn-tertiary-blue' style={{ width: '80px' }} disabled={false} onClick={handleSave}>
                    저장
                  </UIButton2>
                </UIUnitGroup>
              </UIArticle>
            </UIPopupFooter>
          </UIPopupAside>
        }
      >
        {/* 우측 Contents 영역 콘텐츠 */}
        {/* 콘텐츠 영역 */}
        <section className='section-popup-content'>
          {/* 레이어 팝업 헤더 */}
          {/* <UIPopupHeader title='' description='' position='right' /> */}

          {/* 레이어 팝업 바디 */}
          <UIPopupBody>
            {/* 이름 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                  이름
                </UITypography>
                <UIInput.Text value={datasetName} onChange={() => {}} placeholder='이름 입력' readOnly={true} />
              </UIFormField>
            </UIArticle>

            {/* 설명 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb'>
                  설명
                </UITypography>
                <UITextArea2 value={datasetDescription} placeholder='설명 입력' onChange={e => setDatasetDescription(e.target.value)} maxLength={100} />
              </UIFormField>
            </UIArticle>

            {/* 태그 섹션 */}
            <UIArticle>
              <UIInput.Tags tags={tags} onChange={setTags} label='태그' placeholder='태그 입력' />
            </UIArticle>
          </UIPopupBody>
          {/* 레이어 팝업 footer */}
          {/* <UIPopupFooter></UIPopupFooter> */}
        </section>
      </UILayerPopup>
    </>
  );
};
