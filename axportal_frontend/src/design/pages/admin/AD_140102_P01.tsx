import React, { useState } from 'react';

import { UITypography, UIIcon2, UITooltip } from '@/components/UI/atoms';
import { UIButton2 } from '@/components/UI/atoms/UIButton2';
import { UIPopupHeader, UIPopupFooter, UIArticle, UIPopupBody, UIUnitGroup, UIFormField } from '@/components/UI/molecules';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { UIInput, UITextArea2 } from '@/components/UI/molecules/input';

import { DesignLayout } from '../../components/DesignLayout';

export const AD_140102_P01: React.FC = () => {
  const [isPopupOpen] = useState(true); // 팝업이므로 항상 열려있음

  const handleClose = () => {
    // 팝업 닫기 동작 제거 (디자인 페이지이므로 항상 열려있음)
    //
  };

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
            <UIPopupHeader title='이미지 수정' description='' position='left' />
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
          <UIPopupHeader title='이미지 수정' description='IDE 사용을 위해 필요한 개발 환경 이미지를 수정해주세요.' position='right' />

          {/* 레이어 팝업 바디 */}
          <UIPopupBody>
            {/* 도구명 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb' required={true}>
                  도구명
                </UITypography>
                <UIInput.Text value={'Jupyter Notebook'} onChange={() => {}} placeholder='도구명 입력' readOnly={true} />
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
                <UIInput.Text value={''} onChange={() => {}} placeholder='이미지명 입력' />
              </UIFormField>
            </UIArticle>

            {/* 설명 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb' required={true}>
                  설명
                </UITypography>
                <UITextArea2 value={''} onChange={() => {}} placeholder='설명 입력' maxLength={100} />
              </UIFormField>
            </UIArticle>
          </UIPopupBody>
        </section>
      </UILayerPopup>
    </>
  );
};
