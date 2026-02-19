import React, { useState } from 'react';
import { UITypography, UIButton2, UIRadio2 } from '@/components/UI/atoms';
import { UIPopupHeader, UIPopupFooter, UIArticle, UIPopupBody, UIUnitGroup, UIFormField } from '@/components/UI/molecules';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { UIInput, UITextArea2 } from '@/components/UI/molecules/input';

import { DesignLayout } from '../../components/DesignLayout';

export const AD_120201_P01: React.FC = () => {
  const [isPopupOpen] = useState(true); // 팝업이므로 항상 열려있음

  const handleClose = () => {
    // 팝업 닫기 동작 제거 (디자인 페이지이므로 항상 열려있음)
    // 
  };

  // text 타입
  const [, setTextValue] = useState('기준금리');

  return (
    <>
      {/* DesignLayout 기본 구조 */}
      <DesignLayout
        initialMenu={{ id: 'admin', label: '관리' }}
        initialSubMenu={{
          id: 'admin-roles',
          label: '역할 관리',
          icon: 'ico-lnb-menu-20-admin-role',
        }}
      >
        <div className='flex items-center justify-center h-full'>
          <div className='text-center'>
            <UITypography variant='title-1' className='secondary-neutral-800 text-sb'>
              기본 정보
            </UITypography>
            <UITypography variant='body-1' className='secondary-neutral-600'>
              기본 정보 수정 진행 중...
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
            <UIPopupHeader title='기본 정보 수정' description='' position='left' />
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <UIButton2 className='btn-tertiary-gray w-[80px]'>취소</UIButton2>
                  <UIButton2 className='btn-tertiary-blue w-[80px]'>저장</UIButton2>
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
          {/* [251105_퍼블수정] 텍스트 수정 */}
          <UIPopupHeader title='기본 정보 수정' description='프로젝트명 및 설명을 수정해주세요.' position='right' />

          {/* 레이어 팝업 바디 */}
          <UIPopupBody>
            {/* 프로젝트명 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb' required={true}>
                  프로젝트명
                </UITypography>
                <div>
                  <UIInput.Text
                    value={'슈퍼SOL 챗봇 개발'}
                    onChange={e => {
                      setTextValue(e.target.value);
                    }}
                    placeholder='프로젝트명 입력'
                  />
                </div>
              </UIFormField>
            </UIArticle>

            {/* 설명 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                  설명
                </UITypography>
                <UITextArea2 value={'슈퍼SOL에서 사용할 챗봇을 개발'} onChange={() => {}} maxLength={100} />
              </UIFormField>
            </UIArticle>

            {/* 라디오 필드 */}
            <UIArticle>
              <UIFormField gap={12} direction='column'>
                {/* [251106_퍼블수정] 텍스트 수정 */}
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                  개인정보 포함 여부
                </UITypography>
                <UIUnitGroup gap={12} direction='column' align='start'>
                  <UIRadio2 name='basic1' value='option1' label='미포함' />
                  <UIRadio2 name='basic1' value='option2' label='포함' />
                </UIUnitGroup>
                <div>
                  <UITextArea2 value={'고객 본인 확인을 위해 주민등록번호가 포함됩니다.'} onChange={() => {}} maxLength={100} />
                </div>
              </UIFormField>
            </UIArticle>
          </UIPopupBody>
        </section>
      </UILayerPopup>
    </>
  );
};
