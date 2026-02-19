import React, { useState } from 'react';

import { UIButton2, UITypography } from '@/components/UI/atoms';
import { UIStepper, UIUnitGroup, UIPopupFooter, UIPopupHeader, UIPopupBody, UIFormField, UIInput, UIDropdown, type UIStepperItem } from '@/components/UI/molecules';
import { UIArticle } from '@/components/UI/molecules';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';

import { DesignLayout } from '../../components/DesignLayout';

export const HM_060101_P07: React.FC = () => {
  const [isPopupOpen, setIsPopupOpen] = useState(true); // 팝업이므로 기본적으로 열려있음

  // 폼 상태
  const [batchSize, setBatchSize] = useState('1');
  const [dataset, setDataset] = useState('Small (기본)');
  const [isDatasetDropdownOpen, setIsDatasetDropdownOpen] = useState(false);

  const handleClose = () => {
    setIsPopupOpen(false);
  };

  const handleCancel = () => {
    handleClose();
  };

  const handleNext = () => {
    // 다음 단계로 이동 로직
  };

  // 스테퍼 데이터
  const stepperItems: UIStepperItem[] = [
    {
      id: 'step1',
      label: '프로젝트 선택',
      step: 1,
    },
    {
      id: 'step2',
      label: '도구 및 이미지 선택',
      step: 2,
    },
    {
      id: 'step3',
      label: 'DW 계정 선택',
      step: 3,
    },
    {
      id: 'step4',
      label: '자원 선택',
      step: 4,
    },
  ];

  return (
    <>
      {/* DesignLayout 기본 구조 */}
      <DesignLayout
        initialMenu={{ id: 'data', label: '데이터' }}
        initialSubMenu={{
          id: 'data-tools',
          label: '데이터도구',
          icon: 'ico-lnb-menu-20-data-storage',
        }}
      >
        <div className='flex items-center justify-center h-full'>
          <div className='text-center'>
            <UITypography variant='title-1' className='secondary-neutral-800 text-sb'>
              데이터 도구
            </UITypography>
            <UITypography variant='body-1' className='secondary-neutral-600'>
              Ingestion Tool 만들기 진행 중...
            </UITypography>
          </div>
        </div>
      </DesignLayout>

      {/* DesignLayout 위에 높은 z-index로 뜨는 UILayerPopup */}
      <UILayerPopup
        isOpen={isPopupOpen}
        onClose={handleClose}
        size='fullscreen'
        showOverlay={true}
        leftContent={
          /* 좌측 Step 영역 콘텐츠 */
          <UIPopupAside>
            <UIPopupHeader title='IDE 생성' description='' position='left' />
            <UIPopupBody>
              <UIArticle>
                <UIStepper currentStep={4} items={stepperItems} direction='vertical' />
              </UIArticle>
            </UIPopupBody>
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <UIButton2 className='btn-tertiary-gray' style={{ width: '80px' }} onClick={handleCancel}>
                    취소
                  </UIButton2>
                  <UIButton2 className='btn-tertiary-blue' style={{ width: '80px' }} onClick={handleNext}>
                    생성
                  </UIButton2>
                </UIUnitGroup>
              </UIArticle>
            </UIPopupFooter>
          </UIPopupAside>
        }
      >
        {/* 우측 Contents 영역 콘텐츠 */}
        <section className='section-popup-content'>
          <UIPopupHeader
            title='자원 선택'
            description='디폴트로 설정된 자원량으로 IDE가 생성됩니다. 기본 프리셋을 제외한 프리셋을 사용하여 IDE 생성을 원할 경우, 간편 결재 요청이 발송됩니다.'
            position='right'
          />
          <UIPopupBody>
            {/* 모델 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                  자원 프리셋
                </UITypography>
                <UIDropdown
                  value={dataset}
                  options={[
                    { value: '1', label: 'Small (기본)' },
                    { value: '2', label: 'Medium' },
                    { value: '3', label: 'Large' },
                  ]}
                  isOpen={isDatasetDropdownOpen}
                  onClick={() => setIsDatasetDropdownOpen(!isDatasetDropdownOpen)}
                  onSelect={(value: string) => {
                    setDataset(value);
                    setIsDatasetDropdownOpen(false);
                  }}
                  placeholder='자원 프리셋 선택'
                />
              </UIFormField>
            </UIArticle>

            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <UIFormField gap={8} direction='column'>
                  <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                    CPU(Core)
                  </UITypography>
                  <UIInput.Text value={batchSize} placeholder='1' onChange={e => setBatchSize(e.target.value)} readOnly={true} />
                </UIFormField>
                <UIFormField gap={8} direction='column'>
                  <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                    Memory(GiB)
                  </UITypography>
                  <UIInput.Text value={batchSize} placeholder='1' onChange={e => setBatchSize(e.target.value)} readOnly={true} />
                </UIFormField>
              </UIUnitGroup>
            </UIArticle>
          </UIPopupBody>
          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <UIButton2 className='btn-secondary-gray' style={{ width: '80px' }} onClick={handleCancel}>
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
