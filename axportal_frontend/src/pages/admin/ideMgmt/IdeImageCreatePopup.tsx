import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router';

import { UIIcon2, UITooltip, UITypography } from '@/components/UI/atoms';
import { UIButton2 } from '@/components/UI/atoms/UIButton2';
import { UIArticle, UIFormField, UIPopupBody, UIPopupFooter, UIPopupHeader, UIUnitGroup } from '@/components/UI/molecules';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { UIInput, UITextArea2 } from '@/components/UI/molecules/input';
import { UIDropdown } from '@/components/UI/molecules/dropdown/UIDropdown';
import { useCommonPopup } from '@/hooks/common/popup/useCommonPopup';
import { useModal } from '@/stores/common/modal';
import { UI_LABEL_TO_IMAGE_TYPE, useCreateImage } from '@/services/admin/ideMgmt';

interface IdeImageCreatePopupProps {
  isOpen: boolean;
  onClose: () => void;
  onSave?: () => void;
  initialToolName?: string;
}

/**
 * 관리 > IDE 관리 > 이미지 등록 팝업
 */
export const IdeImageCreatePopup: React.FC<IdeImageCreatePopupProps> = ({ isOpen, onClose, onSave: _, initialToolName = 'Jupyter Notebook' }) => {
  const { showCancelConfirm } = useCommonPopup();
  const { openAlert } = useModal();
  const navigate = useNavigate();

  // 폼 상태 관리
  const [formData, setFormData] = useState({
    toolName: initialToolName,
    imageName: '',
    imageUrl: '',
    description: '',
  });

  // 드롭다운 상태
  const [isToolNameDropdownOpen, setIsToolNameDropdownOpen] = useState(false);

  // 이미지 생성 mutation
  const { mutate: createImage, isPending } = useCreateImage({
    onSuccess: response => {
      const uuid = response.data.uuid;
      openAlert({
        title: '완료',
        message: '이미지 등록이 완료되었습니다.',
        onConfirm: () => {
          // 폼 초기화
          setFormData({
            toolName: initialToolName,
            imageName: '',
            imageUrl: '',
            description: '',
          });
          // 팝업 닫기
          onClose();
          // 상세 페이지로 이동
          navigate(`image/${uuid}`);
        },
      });
    },
  });

  // initialToolName 변경 시 formData 업데이트
  useEffect(() => {
    setFormData(prev => ({ ...prev, toolName: initialToolName }));
  }, [initialToolName]);

  // 폼 유효성 검사 (모든 필수 필드가 채워졌는지 확인)
  const isFormValid = formData.toolName.trim() !== '' && formData.imageName.trim() !== '' && formData.imageUrl.trim() !== '' && formData.description.trim() !== '';

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
    // UI 라벨을 백엔드 ImageType으로 변환
    const imageType = UI_LABEL_TO_IMAGE_TYPE[formData.toolName];

    // API 요청
    createImage({
      imgG: imageType,
      imgNm: formData.imageName.trim(),
      imgUrl: formData.imageUrl.trim(),
      dtlCtnt: formData.description.trim(),
    });
  };

  return (
    <UILayerPopup
      isOpen={isOpen}
      onClose={onClose}
      size='fullscreen'
      showOverlay={true}
      leftContent={
        <UIPopupAside>
          <UIPopupHeader title='이미지 등록' description='' position='left' />
          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <UIButton2 className='btn-tertiary-gray w-[80px]' onClick={handleCancel}>
                  취소
                </UIButton2>
                <UIButton2 className='btn-tertiary-blue w-[80px]' onClick={handleSave} disabled={!isFormValid || isPending}>
                  저장
                </UIButton2>
              </UIUnitGroup>
            </UIArticle>
          </UIPopupFooter>
        </UIPopupAside>
      }
    >
      <section className='section-popup-content'>
        <UIPopupHeader title='이미지 등록' description='IDE 사용을 위해 필요한 개발 환경 이미지를 등록해주세요.' position='right' />

        <UIPopupBody>
          {/* 도구명 드롭다운 */}
          <UIArticle>
            <UIFormField gap={8} direction='column'>
              <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb' required={true}>
                도구명
              </UITypography>
              <UIDropdown
                value={formData.toolName}
                placeholder='도구명 선택'
                options={[
                  { value: 'Jupyter Notebook', label: 'Jupyter Notebook' },
                  { value: 'VS Code', label: 'VS Code' },
                ]}
                isOpen={isToolNameDropdownOpen}
                onClick={() => setIsToolNameDropdownOpen(prev => !prev)}
                onSelect={value => {
                  setFormData(prev => ({ ...prev, toolName: value }));
                  setIsToolNameDropdownOpen(false);
                }}
              />
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
              <UIInput.Text maxLength={100} value={formData.imageUrl} onChange={e => setFormData(prev => ({ ...prev, imageUrl: e.target.value }))} placeholder='이미지 URL 입력' />
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
