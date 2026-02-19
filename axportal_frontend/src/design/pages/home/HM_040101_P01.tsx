import React, { useState } from 'react';

import { UITypography, UIButton2, UIRadio2 } from '@/components/UI/atoms';
import { UIStepper, UIUnitGroup, UIPopupHeader, UIPopupBody, UIPopupFooter, UIInput, UITextArea2 } from '@/components/UI/molecules';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';

import { DesignLayout } from '../../components/DesignLayout';

export const HM_040101_P01: React.FC = () => {
  const [isPopupOpen] = useState(true); // 팝업이므로 항상 열려있음
  const [selectedValue1, setSelectedValue1] = useState<string>('');

  const handleClose = () => {
    // 팝업 닫기 동작 제거 (디자인 페이지이므로 항상 열려있음)
    // 
  };

  // textarea 타입
  const [maxLengthTextareaValue, setMaxLengthTextareaValue] = useState('');
  const [maxLengthTextareaValue2, setMaxLengthTextareaValue2] = useState('');

  return (
    <>
      {/* DesignLayout 기본 구조 */}
      <DesignLayout
        initialMenu={{ id: 'home', label: '홈' }}
        initialSubMenu={{
          id: 'home-dashboard',
          label: '대시보드',
          icon: 'ico-lnb-menu-20-home-dashboard',
        }}
      >
        <div className='flex items-center justify-center h-full'>
          <div className='text-center'>
            <UITypography variant='title-1' className='secondary-neutral-800 text-sb'>
              프로젝트 추가
            </UITypography>
            <UITypography variant='body-1' className='secondary-neutral-600'>
              프로젝트 추가 진행 중...
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
            <UIPopupHeader title='프로젝트 생성' position='left' />
            {/* 레이어 팝업 바디 */}
            <UIPopupBody>
              {/* 스테퍼 컴포넌트 */}
              <UIStepper
                items={[
                  { id: 'step1', step: 1, label: '기본 정보 입력' },
                  { id: 'step2', step: 2, label: '구성원 선택' },
                ]}
                currentStep={1}
                direction='vertical'
              />
            </UIPopupBody>
            {/* 레이어 팝업 footer */}
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <UIButton2 className='btn-tertiary-gray' style={{ width: '80px' }}>
                    취소
                  </UIButton2>
                  <UIButton2 className='btn-tertiary-blue' style={{ width: '80px' }} disabled>
                    생성
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
          <UIPopupHeader title='기본 정보 입력' description='생성할 프로젝트의 이름, 설명 등 기본 정보를 입력해주세요.' position='right' />
          {/* 레이어 팝업 바디 */}
          <UIPopupBody>
            {/* 역할명 입력 필드 */}
            <UIArticle>
              <UIUnitGroup gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800' required={true}>
                  프로젝트명
                </UITypography>
                <UIInput.Text value={''} onChange={() => {}} placeholder='프로젝트명 입력' />
              </UIUnitGroup>
            </UIArticle>

            {/* 설명 입력 필드 */}
            <UIArticle>
              <UIUnitGroup gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800'>
                  설명
                </UITypography>
                <UITextArea2 value={maxLengthTextareaValue} maxLength={100} onChange={e => setMaxLengthTextareaValue(e.target.value)} placeholder='설명 입력' />
              </UIUnitGroup>
            </UIArticle>

            {/* 개인정보 포함 여부 필드 */}
            <UIArticle>
              <UIUnitGroup gap={12} direction='column' style={{ marginBottom: '8px' }}>
                <UITypography variant='title-4' className='secondary-neutral-800' required={true}>
                  개인정보 포함 여부
                </UITypography>
                <div>
                  <UIUnitGroup gap={16} direction='column'>
                    <UIRadio2
                      name='basic1'
                      value='option1'
                      label='미포함'
                      checked={selectedValue1 === 'option1'}
                      onChange={(checked, value) => {
                        if (checked) setSelectedValue1(value);
                      }}
                    />
                    <UIRadio2
                      name='basic1'
                      value='option2'
                      label='포함'
                      checked={selectedValue1 === 'option2'}
                      onChange={(checked, value) => {
                        if (checked) setSelectedValue1(value);
                      }}
                    />
                  </UIUnitGroup>
                </div>
              </UIUnitGroup>

              <UITextArea2 value={maxLengthTextareaValue2} maxLength={100} onChange={e => setMaxLengthTextareaValue2(e.target.value)} placeholder='개인정보 포함 사유 입력' />
            </UIArticle>
          </UIPopupBody>
          {/* 레이어 팝업 footer */}
          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <UIButton2 className='btn-secondary-blue' style={{ width: '80px' }} disabled>
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
