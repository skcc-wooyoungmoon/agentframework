import React, { useState } from 'react';

import { UIButton2, UITypography } from '@/components/UI/atoms';
import { UIStepper, UIDropdown, type UIStepperItem, UIGroup } from '@/components/UI/molecules';
import { UIArticle, UIPopupBody, UIPopupFooter, UIPopupHeader, UIUnitGroup, UIFormField } from '@/components/UI/molecules';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';

import { DesignLayout } from '../../components/DesignLayout';

export const AD_120401_P02: React.FC = () => {
  const [isPopupOpen, setIsPopupOpen] = useState(true); // 팝업이므로 기본적으로 열려있음

  // 데이터 세트 유형 상태
  const [dataset, setdataset] = useState('1');
  const [isDatasetDropdownOpen, setIsDatasetDropdownOpen] = useState(false);

  const handleClose = () => {
    setIsPopupOpen(false);
  };

  const handleCancel = () => {
    handleClose();
  };

  // 스테퍼 데이터
  const stepperItems: UIStepperItem[] = [
    {
      id: 'step1',
      label: '사용자 선택',
      step: 1,
    },
    {
      id: 'step2',
      label: '역할 할당',
      step: 2,
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
            {/* 레이어 팝업 헤더 */}
            <UIPopupHeader title='구성원 초대하기' description='' position='left' />
            <UIPopupBody>
              <UIArticle>
                <UIStepper currentStep={2} items={stepperItems} direction='vertical' />
              </UIArticle>
            </UIPopupBody>
            {/* 레이어 팝업 바디 : [참고] 이 페이지에는 왼쪽 body 영역 없음. */}
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <UIButton2 className='btn-tertiary-gray' style={{ width: '80px' }}>
                    취소
                  </UIButton2>
                  <UIButton2 className='btn-tertiary-blue' style={{ width: '80px' }} disabled={false}>
                    완료
                  </UIButton2>
                </UIUnitGroup>
              </UIArticle>
            </UIPopupFooter>
          </UIPopupAside>
        }
      >
        {/* 우측 Contents 영역 콘텐츠 - 2단계: 역할 할당 */}
        <section className='section-popup-content'>
          <UIPopupHeader title='역할 할당' description='사용자 역할을 수정하고 싶은 경우 변경할 수 있습니다. 기본 역할은 테스터로 할당됩니다.' position='right' />
          <UIPopupBody>
            <UIArticle>
              {/* 데이터 세트 유형 섹션 */}
              <UIFormField gap={8} direction='column'>
                <UIGroup gap={0} direction='row'>
                  {/* [251106_퍼블수정] 마크업 수정 */}
                  <UITypography variant='title-4' className='secondary-neutral-800 text-sb'>
                    장정현
                  </UITypography>
                  <UITypography variant='title-4' className='secondary-neutral-800 text-sb' divider={true} required={true}>
                    Data기획Unit
                  </UITypography>
                </UIGroup>

                <UIDropdown
                  value={dataset}
                  options={[
                    { value: '1', label: '테스터' },
                    { value: '2', label: '관리자' },
                    { value: '3', label: '편집자' },
                    { value: '4', label: '엔지니어' },
                    { value: '5', label: '디자이너' },
                    { value: '6', label: '퍼블리셔' },
                  ]}
                  isOpen={isDatasetDropdownOpen}
                  onClick={() => setIsDatasetDropdownOpen(!isDatasetDropdownOpen)}
                  onSelect={(value: string) => {
                    setdataset(value);
                    setIsDatasetDropdownOpen(false);
                  }}
                  placeholder='데이터 세트 유형 선택'
                />
              </UIFormField>
            </UIArticle>
            <UIArticle>
              {/* 데이터 세트 유형 섹션 */}
              <UIFormField gap={8} direction='column'>
                <UIGroup gap={0} direction='row'>
                  {/* [251106_퍼블수정] 마크업 수정 */}
                  <UITypography variant='title-4' className='secondary-neutral-800 text-sb'>
                    장정현
                  </UITypography>
                  <UITypography variant='title-4' className='secondary-neutral-800 text-sb' divider={true} required={true}>
                    Data기획Unit
                  </UITypography>
                </UIGroup>

                <UIDropdown
                  value={dataset}
                  options={[
                    { value: '1', label: '테스터' },
                    { value: '2', label: '관리자' },
                    { value: '3', label: '편집자' },
                    { value: '4', label: '엔지니어' },
                    { value: '5', label: '디자이너' },
                    { value: '6', label: '퍼블리셔' },
                  ]}
                  isOpen={isDatasetDropdownOpen}
                  onClick={() => setIsDatasetDropdownOpen(!isDatasetDropdownOpen)}
                  onSelect={(value: string) => {
                    setdataset(value);
                    setIsDatasetDropdownOpen(false);
                  }}
                  placeholder='데이터 세트 유형 선택'
                />
              </UIFormField>
            </UIArticle>
            <UIArticle>
              {/* 데이터 세트 유형 섹션 */}
              <UIFormField gap={8} direction='column'>
                <UIGroup gap={0} direction='row'>
                  {/* [251106_퍼블수정] 마크업 수정 */}
                  <UITypography variant='title-4' className='secondary-neutral-800 text-sb'>
                    장정현
                  </UITypography>
                  <UITypography variant='title-4' className='secondary-neutral-800 text-sb' divider={true} required={true}>
                    Data기획Unit
                  </UITypography>
                </UIGroup>

                <UIDropdown
                  value={dataset}
                  options={[
                    { value: '1', label: '테스터' },
                    { value: '2', label: '관리자' },
                    { value: '3', label: '편집자' },
                    { value: '4', label: '엔지니어' },
                    { value: '5', label: '디자이너' },
                    { value: '6', label: '퍼블리셔' },
                  ]}
                  isOpen={isDatasetDropdownOpen}
                  onClick={() => setIsDatasetDropdownOpen(!isDatasetDropdownOpen)}
                  onSelect={(value: string) => {
                    setdataset(value);
                    setIsDatasetDropdownOpen(false);
                  }}
                  placeholder='데이터 세트 유형 선택'
                />
              </UIFormField>
            </UIArticle>
            <UIArticle>
              {/* 데이터 세트 유형 섹션 */}
              <UIFormField gap={8} direction='column'>
                <UIGroup gap={0} direction='row'>
                  {/* [251106_퍼블수정] 마크업 수정 */}
                  <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                    장정현
                  </UITypography>
                  <UITypography variant='title-4' className='secondary-neutral-800 text-sb' divider={true}>
                    Data기획Unit
                  </UITypography>
                </UIGroup>

                <UIDropdown
                  value={dataset}
                  options={[
                    { value: '1', label: '테스터' },
                    { value: '2', label: '관리자' },
                    { value: '3', label: '편집자' },
                    { value: '4', label: '엔지니어' },
                    { value: '5', label: '디자이너' },
                    { value: '6', label: '퍼블리셔' },
                  ]}
                  isOpen={isDatasetDropdownOpen}
                  onClick={() => setIsDatasetDropdownOpen(!isDatasetDropdownOpen)}
                  onSelect={(value: string) => {
                    setdataset(value);
                    setIsDatasetDropdownOpen(false);
                  }}
                  placeholder='데이터 세트 유형 선택'
                />
              </UIFormField>
            </UIArticle>
            <UIArticle>
              {/* 데이터 세트 유형 섹션 */}
              <UIFormField gap={8} direction='column'>
                <UIGroup gap={0} direction='row'>
                  {/* [251106_퍼블수정] 마크업 수정 */}
                  <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                    장정현
                  </UITypography>
                  <UITypography variant='title-4' className='secondary-neutral-800 text-sb' divider={true}>
                    Data기획Unit
                  </UITypography>
                </UIGroup>

                <UIDropdown
                  value={dataset}
                  options={[
                    { value: '1', label: '테스터' },
                    { value: '2', label: '관리자' },
                    { value: '3', label: '편집자' },
                    { value: '4', label: '엔지니어' },
                    { value: '5', label: '디자이너' },
                    { value: '6', label: '퍼블리셔' },
                  ]}
                  isOpen={isDatasetDropdownOpen}
                  onClick={() => setIsDatasetDropdownOpen(!isDatasetDropdownOpen)}
                  onSelect={(value: string) => {
                    setdataset(value);
                    setIsDatasetDropdownOpen(false);
                  }}
                  placeholder='데이터 세트 유형 선택'
                />
              </UIFormField>
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
