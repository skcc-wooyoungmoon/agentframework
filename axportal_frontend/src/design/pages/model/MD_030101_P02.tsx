import React, { useState } from 'react';

import { UIPopupHeader, UIPopupFooter, UIPopupBody, UIFormField, UIInput, UITextArea2 } from '@/components/UI/molecules';
import { UITypography, UIButton2, UIRadio2 } from '@/components/UI/atoms';
import { UIStepper, type UIStepperItem } from '@/components/UI/molecules';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { UIArticle } from '@/components/UI/molecules';
import { UIDropdown } from '../../../components/UI/molecules/dropdown/UIDropdown';
import { DesignLayout } from '../../components/DesignLayout';
import { UIUnitGroup } from '@/components/UI/molecules';

export const MD_030101_P02: React.FC = () => {
  const [isPopupOpen, setIsPopupOpen] = useState(true); // 팝업이므로 기본적으로 열려있음

  // 폼 상태
  const [nameValue, setNameValue] = useState('');

  const handleClose = () => {
    setIsPopupOpen(false);
  };

  // 스테퍼 데이터
  const stepperItems: UIStepperItem[] = [
    {
      id: 'step1',
      label: '모델 선택',
      step: 1,
    },
    {
      id: 'step2',
      label: '기본 정보 입력',
      step: 2,
    },
    {
      id: 'step3',
      label: '자원 할당',
      step: 3,
    },
    {
      id: 'step4',
      label: '학습 데이터세트 선택', // [251104_퍼블수정] : 데이터세트 선택 > 학습 데이터세트 선택
      step: 4,
    },
    {
      id: 'step5',
      label: '파라미터 설정',
      step: 5,
    },
    {
      id: 'step6',
      label: '입력정보 확인',
      step: 6,
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
            <UIPopupHeader title='파인튜닝 등록' description='' position='left' />
            {/* 레이어 팝업 바디 */}
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
                    튜닝시작
                  </UIButton2>
                </UIUnitGroup>
              </UIArticle>
            </UIPopupFooter>
          </UIPopupAside>
        }
      >
        {/* 우측 Contents 영역 콘텐츠 */}
        <section className='section-popup-content'>
          <UIPopupHeader title='기본 정보 입력' description='' position='right' />
          <UIPopupBody>
            {/* 이름 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                  파인튜닝 이름
                </UITypography>
                <UIInput.Text value={nameValue} placeholder='이름 입력' onChange={e => setNameValue(e.target.value)} />
              </UIFormField>
            </UIArticle>

            {/* 설명 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb'>
                  설명
                </UITypography>
                <UITextArea2 value={''} placeholder='설명 입력' onChange={() => {}} maxLength={100} />
              </UIFormField>
            </UIArticle>

            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                  데이터세트 유형
                </UITypography>
                <UIDropdown
                  value='지도학습'
                  placeholder='선택'
                  isOpen={false}
                  disabled={false}
                  onClick={() => {}}
                  onSelect={() => {}}
                  options={[
                    { value: '지도학습1', label: '지도학습' },
                    { value: '비지도학습', label: '비지도학습' },
                    { value: 'DPO', label: 'DPO' },
                    { value: 'CUSTOM', label: 'CUSTOM' },
                  ]}
                  height={48}
                />
              </UIFormField>
            </UIArticle>

            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                  Efficiency Configuration (PEFT)
                </UITypography>
                <UIDropdown
                  value='LoRA'
                  placeholder='선택'
                  isOpen={false}
                  disabled={false}
                  onClick={() => {}}
                  onSelect={() => {}}
                  options={[
                    { value: 'LoRA1', label: 'LoRA' },
                    { value: 'LoRA2', label: 'LoRA' },
                    { value: 'LoRA3', label: 'LoRA' },
                  ]}
                  height={48}
                />
              </UIFormField>
            </UIArticle>

            {/* 라디오 버튼 영역 */}
            <UIArticle>
              <UIFormField gap={12} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                  Fine Tuning Techniques
                </UITypography>
                <UIUnitGroup gap={8} direction='column' align='start'>
                  <UIRadio2 name='basic1' value='option1' label='BASIC' />
                  <UITypography variant='body-2' className='secondary-neutral-600 pl-8'>
                    초기 실험이나 작은 데이터에셋에 적합하며, 복잡한 튜닝 없이 바로 결과를 확인할 수 있습니다.
                  </UITypography>
                </UIUnitGroup>
              </UIFormField>
            </UIArticle>
          </UIPopupBody>
          {/* 레이어 팝업 footer */}
          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <UIButton2 className='btn-secondary-gray' style={{ width: '80px' }}>
                  이전
                </UIButton2>
                <UIButton2 className='btn-secondary-blue' style={{ width: '80px' }}>
                  다음
                </UIButton2>
              </UIUnitGroup>
            </UIArticle>
          </UIPopupFooter>
        </section>
      </UILayerPopup>
    </>
  );
};
