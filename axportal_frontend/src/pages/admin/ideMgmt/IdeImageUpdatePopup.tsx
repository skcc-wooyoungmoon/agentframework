import React, { useEffect, useState } from 'react';

import { UIIcon2, UITooltip, UITypography } from '@/components/UI/atoms';
import { UIButton2 } from '@/components/UI/atoms/UIButton2';
import { UIArticle, UIFormField, UIPopupBody, UIPopupFooter, UIPopupHeader, UIUnitGroup } from '@/components/UI/molecules';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { UIInput, UITextArea2 } from '@/components/UI/molecules/input';
import { useCommonPopup } from '@/hooks/common/popup/useCommonPopup';
import { UI_LABEL_TO_IMAGE_TYPE, useGetImageDetail, useUpdateImage } from '@/services/admin/ideMgmt';

interface IdeImageData {
  toolName: string;
  imageName: string;
  imageUrl?: string;
  description: string;
}

interface IdeImageUpdatePopupProps {
  isOpen: boolean;
  onClose: () => void;
  onSave?: (data: IdeImageData) => void;
  initialData?: IdeImageData;
  imageUuid?: string;
}

/**
 * 관리 > IDE 관리 > 이미지 수정 팝업
 */
export const IdeImageUpdatePopup: React.FC<IdeImageUpdatePopupProps> = ({ isOpen, onClose, onSave, initialData, imageUuid }) => {
  const { showCancelConfirm, showEditComplete } = useCommonPopup();
  const { mutate: updateImage, isPending: isUpdating } = useUpdateImage(imageUuid || '');
  const { data: imageDetail } = useGetImageDetail(imageUuid || '', { enabled: !!imageUuid && !initialData?.imageUrl });
  // 폼 상태 관리
  const [formData, setFormData] = useState<IdeImageData>({
    toolName: 'Jupyter Notebook',
    imageName: '',
    imageUrl: '',
    description: '',
  });

  // 폼 초기화 여부 추적 (리렌더링 시 formData 덮어쓰기 방지)
  const [isInitialized, setIsInitialized] = useState(false);

  // 팝업이 열릴 때만 폼 데이터 초기화 (리렌더링 시 사용자 입력값 유지)
  useEffect(() => {
    // 팝업이 닫히면 초기화 상태 리셋
    if (!isOpen) {
      setIsInitialized(false);
      return;
    }

    // 이미 초기화되었으면 건너뛰기
    if (isInitialized) return;

    if (initialData) {
      // imageDetail이 필요하지만 아직 로드되지 않았으면 대기
      if (!initialData.imageUrl && imageUuid && !imageDetail) return;

      setFormData({
        ...initialData,
        imageUrl: initialData.imageUrl || imageDetail?.imgUrl || '',
      });
      setIsInitialized(true);
    }
  }, [isOpen, initialData, imageDetail, imageUuid, isInitialized]);

  // 취소 핸들러
  const handleCancel = () => {
    showCancelConfirm({
      onConfirm: () => {
        onClose();
      },
    });
  };

  // 저장 핸들러
  const handleSave = () => {
    if (!imageUuid) return;

    // toolName을 ImageType으로 변환
    const imgG = UI_LABEL_TO_IMAGE_TYPE[formData.toolName];

    if (!imgG) {
      console.error('유효하지 않은 도구명:', formData.toolName);
      return;
    }

    updateImage(
      {
        imgG,
        imgNm: formData.imageName,
        imgUrl: formData.imageUrl || '',
        dtlCtnt: formData.description,
      },
      {
        onSuccess: () => {
          showEditComplete({
            onConfirm: () => {
              onSave?.(formData);
              onClose();
            },
          });
        },
        onError: error => {
          console.error('이미지 수정 실패:', error);
          // 에러 처리 (옵션: 에러 메시지 표시)
        },
      }
    );
  };

  return (
    <UILayerPopup
      isOpen={isOpen}
      onClose={onClose}
      size='fullscreen'
      showOverlay={true}
      leftContent={
        <UIPopupAside>
          <UIPopupHeader title='이미지 수정' description='' position='left' />
          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <UIButton2 className='btn-tertiary-gray w-[80px]' onClick={handleCancel}>
                  취소
                </UIButton2>
                <UIButton2 className='btn-tertiary-blue w-[80px]' onClick={handleSave} disabled={isUpdating}>
                  저장
                </UIButton2>
              </UIUnitGroup>
            </UIArticle>
          </UIPopupFooter>
        </UIPopupAside>
      }
    >
      <section className='section-popup-content'>
        <UIPopupHeader title='이미지 수정' description='IDE 사용을 위해 필요한 개발 환경 이미지를 수정해주세요.' position='right' />

        <UIPopupBody>
          {/* 도구명 입력 필드 */}
          <UIArticle>
            <UIFormField gap={8} direction='column'>
              <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb' required={true}>
                도구명
              </UITypography>
              <UIInput.Text value={formData.toolName} onChange={e => setFormData(prev => ({ ...prev, toolName: e.target.value }))} placeholder='도구명 입력' readOnly={true} />
            </UIFormField>
          </UIArticle>

          {/* 이미지명 입력 필드 */}
          <UIArticle>
            <UIFormField gap={8} direction='column'>
              <div className='inline-flex items-center'>
                <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb' required={true}>
                  이미지명
                </UITypography>
                <UITooltip
                  trigger='click'
                  position='bottom-start'
                  type='notice'
                  title=''
                  items={['이미지명은 이미지 식별에 활용됩니다. 이미지명이 부정확할 경우 IDE 접속 또는 실행 과정에서 오류가 발생할 수 있으니 정확히 입력해 주세요.']}
                  bulletType='default'
                  showArrow={false}
                  showCloseButton={true}
                  className='tooltip-wrap ml-1'
                >
                  <UIButton2 className='btn-text-only-16 p-0'>
                    <UIIcon2 className='ic-system-20-info' />
                  </UIButton2>
                </UITooltip>
              </div>
              <UIInput.Text value={formData.imageName} onChange={e => setFormData(prev => ({ ...prev, imageName: e.target.value }))} placeholder='이미지명 입력' maxLength={50} />
            </UIFormField>
          </UIArticle>

          {/* 이미지 URL 입력 필드 */}
          <UIArticle>
            <UIFormField gap={8} direction='column'>
              <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb' required={true}>
                이미지 URL
              </UITypography>
              <UIInput.Text
                value={formData.imageUrl || ''}
                onChange={e => setFormData(prev => ({ ...prev, imageUrl: e.target.value }))}
                placeholder='이미지 URL 입력'
                maxLength={100}
              />
            </UIFormField>
          </UIArticle>

          {/* 설명 입력 필드 */}
          <UIArticle>
            <UIFormField gap={8} direction='column'>
              <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb' required={true}>
                설명
              </UITypography>
              <UITextArea2 value={formData.description} onChange={e => setFormData(prev => ({ ...prev, description: e.target.value }))} placeholder='설명 입력' maxLength={100} />
            </UIFormField>
          </UIArticle>
        </UIPopupBody>
      </section>
    </UILayerPopup>
  );
};
