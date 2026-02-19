import { UIButton2, UITypography } from '@/components/UI/atoms';
import { UIArticle, UIFormField, UIPopupBody, UIPopupFooter, UIPopupHeader, UIStepper, UIUnitGroup } from '@/components/UI/molecules';
import { UIInput, UITextArea2 } from '@/components/UI/molecules/input';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import type { ProjectDetailType } from '@/services/admin/projMgmt';
import { useModal } from '@/stores/common/modal';
import React from 'react';

interface Step1FormData {
  roleNm: string;
  dtlCtnt: string;
}

interface ProjRoleCreateStep1PopupProps {
  isOpen: boolean;
  onClose: () => void;
  onNext: () => void;
  formData: Step1FormData;
  onChange: (formData: Step1FormData) => void;
  projectInfo: ProjectDetailType;
}

// 스테퍼 아이템
const stepperItems = [
  { step: 1, label: '기본 정보 입력' },
  { step: 2, label: '메뉴 진입 설정' },
  { step: 3, label: '권한 추가하기' },
];

export const ProjRoleCreateStep1Popup: React.FC<ProjRoleCreateStep1PopupProps> = ({ isOpen, onClose, onNext, formData, onChange, projectInfo }) => {
  const { openConfirm } = useModal();

  // 입력값 변경 핸들러
  const handleInputChange = (field: keyof Step1FormData, value: string) => {
    onChange({ ...formData, [field]: value });
  };

  const handleCancel = async () => {
    const confirmed = await openConfirm({
      bodyType: 'text',
      title: '안내',
      message: `화면을 나가시겠습니까?
                입력한 정보가 저장되지 않을 수 있습니다.`,
      cancelText: '아니요',
      confirmText: '예',
    });

    if (confirmed) {
      onClose();
    }
  };

  // 필수 필드 유효성 검사 (역할명이 필수)
  const isFormValid = formData.roleNm.trim() !== '';

  return (
    <UILayerPopup
      isOpen={isOpen}
      onClose={handleCancel}
      size='fullscreen'
      showOverlay={true}
      leftContent={
        /* 좌측 Step 영역 */
        <UIPopupAside>
          {/* 팝업 헤더 */}
          <UIPopupHeader title='새 역할 만들기' description='' position='left' />

          {/* 팝업 바디 */}
          <UIPopupBody>
            <UIArticle>
              <UIStepper currentStep={1} items={stepperItems} direction='vertical' />
            </UIArticle>
          </UIPopupBody>

          {/* 팝업 푸터 */}
          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <UIButton2 className='btn-tertiary-gray' style={{ width: '80px' }} onClick={handleCancel}>
                  취소
                </UIButton2>
                <UIButton2 className='btn-tertiary-blue' style={{ width: '80px' }} disabled={true}>
                  만들기
                </UIButton2>
              </UIUnitGroup>
            </UIArticle>
          </UIPopupFooter>
        </UIPopupAside>
      }
    >
      {/* 우측 Contents 영역 */}
      {/* 콘텐츠 영역 */}
      <section className='section-popup-content'>
        {/* 팝업 헤더 */}
        <UIPopupHeader title='기본 정보 입력' description='사전 정의된 역할 외에, 필요한 역할을 자유롭게 추가할 수 있습니다.' position='right' />

        {/* 팝업 바디 */}
        <UIPopupBody>
          <UIArticle>
            <UIFormField gap={8} direction='column'>
              <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb' required={false}>
                프로젝트명
              </UITypography>
              <UIInput.Text value={projectInfo?.prjNm || ''} onChange={() => {}} placeholder='프로젝트명 입력' readOnly={true} />
            </UIFormField>
          </UIArticle>

          {/* 역할명 입력 */}
          <UIArticle>
            <UIFormField gap={8} direction='column'>
              <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb' required={true}>
                역할명
              </UITypography>
              <UIInput.Text value={formData.roleNm} maxLength={50} onChange={e => handleInputChange('roleNm', e.target.value)} placeholder='역할명 입력' />
            </UIFormField>
          </UIArticle>

          {/* 설명 입력 */}
          <UIArticle>
            <UIFormField gap={8} direction='column'>
              <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb' required={false}>
                설명
              </UITypography>
              <UITextArea2 value={formData.dtlCtnt} onChange={e => handleInputChange('dtlCtnt', e.target.value)} placeholder='설명 입력' maxLength={100} />
            </UIFormField>
          </UIArticle>
        </UIPopupBody>
        {/* 팝업 푸터 */}
        <UIPopupFooter>
          <UIArticle>
            <UIUnitGroup gap={8} direction='row' align='start'>
              <UIButton2 className='btn-secondary-blue' disabled={!isFormValid} onClick={onNext}>
                다음
              </UIButton2>
            </UIUnitGroup>
          </UIArticle>
        </UIPopupFooter>
      </section>
    </UILayerPopup>
  );
};
