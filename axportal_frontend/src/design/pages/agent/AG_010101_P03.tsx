import React, { useState } from 'react';
import { UIStepper } from '@/components/UI/molecules';
import { UIInput, UITextArea2 } from '@/components/UI/molecules/input';

import { UITypography, UIButton2 } from '@/components/UI/atoms';
import { UIPopupHeader, UIPopupFooter, UIArticle, UIPopupBody, UIFormField } from '@/components/UI/molecules';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';

import { DesignLayout } from '../../components/DesignLayout';
import { UIUnitGroup } from '@/components/UI/molecules';

export const AG_010101_P03: React.FC = () => {
  const [isPopupOpen] = useState(true); // 팝업이므로 항상 열려있음
  // const [selectedLoader, setSelectedLoader] = useState('RecursiveCharacter');
  // const [isLoaderDropdownOpen, setIsLoaderDropdownOpen] = useState(false);

  // 스테퍼 데이터
  const stepperItems = [
    { step: 1, label: '템플릿 선택' },
    { step: 2, label: '기본 정보 입력' },
  ];

  const handleClose = () => {
    // 팝업 닫기 동작 제거 (디자인 페이지이므로 항상 열려있음)
    // 
  };
  // text 타입
  const [textValue, setTextValue] = useState('');

  // textarea 타입
  const [textareaValue, setTextareaValue] = useState('');

  return (
    <>
      {/* DesignLayout 기본 구조 */}
      <DesignLayout
        initialMenu={{ id: 'admin', label: '관리' }}
        initialSubMenu={{
          id: 'admin-roles',
          label: '',
          icon: 'ico-lnb-menu-20-admin-role',
        }}
      >
        <div className='flex items-center justify-center h-full'>
          <div className='text-center'>
            <UITypography variant='title-1' className='secondary-neutral-800 text-sb'>기본 정보</UITypography>
            <UITypography variant='body-1' className='secondary-neutral-600'>지식 만들기 진행 중...</UITypography>
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
              <UIPopupHeader title='에이전트 등록하기' description='' position='left' />
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
          <UIPopupHeader title='기본 정보 입력' description='' position='right' />

          {/* 레이어 팝업 바디 */}
          <UIPopupBody>
            {/* 이름 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                  이름
                </UITypography>
                <div>
                  <UIInput.Text
                    value={textValue}
                    onChange={e => {
                      setTextValue(e.target.value);
                    }}
                    placeholder='이름 입력'
                  />
                </div>
              </UIFormField>
            </UIArticle>

            {/* 설명 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={false}>
                  설명
                </UITypography>
                <div>
                  <UITextArea2 value={textareaValue} onChange={e => setTextareaValue(e.target.value)} placeholder='설명 입력' maxLength={100} />
                </div>
              </UIFormField>
            </UIArticle>
          </UIPopupBody>
          {/* 레이어 팝업 footer */}
          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <UIButton2 className='btn-secondary-gray'>이전</UIButton2>
              </UIUnitGroup>
            </UIArticle>
          </UIPopupFooter>
        </section>
      </UILayerPopup>
    </>
  );
};
