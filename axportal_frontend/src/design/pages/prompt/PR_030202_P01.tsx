import React, { useState } from 'react';

import { UIButton2, UITypography } from '@/components/UI/atoms';
import { UIUnitGroup, UIPopupFooter, UIPopupHeader, UIPopupBody, UIFormField, UIInput, UITextArea2, UIArticle } from '@/components/UI/molecules';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';

import { DesignLayout } from '../../components/DesignLayout';

export const PR_030202_P01: React.FC = () => {
  const [isPopupOpen, setIsPopupOpen] = useState(true); // 팝업이므로 기본적으로 열려있음

  // 폼 상태 - 프롬프트 가드레일 관련으로 변경
  const [description, setDescription] = useState(
    '계좌번호, 카드번호, 비밀번호, OTP 번호, 보안카드 일련번호, CVV 코드 등 금융 보안 정보는 어떤 경우에도 입력되거나 출력되지 않는다.'
  );

  const [tags, setTags] = useState<string[]>([]);

  const handleClose = () => {
    setIsPopupOpen(false);
  };

  const handleCancel = () => {
    handleClose();
  };

  const handleNext = () => {
  };

  return (
    <>
      {/* DesignLayout 기본 구조 */}
      <DesignLayout
        initialMenu={{ id: 'prompt', label: '프롬프트' }}
        initialSubMenu={{
          id: 'prompt-guardrails',
          label: '가드레일',
          icon: 'ico-lnb-menu-20-shield',
        }}
      >
        <div className='flex items-center justify-center h-full'>
          <div className='text-center'>
            <UITypography variant='title-1' className='secondary-neutral-800 text-sb'>
              프롬프트 가드레일
            </UITypography>
            <UITypography variant='body-1' className='secondary-neutral-600'>
              프롬프트 가드레일 생성 진행 중...
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
            <UIPopupHeader
              title={
                <>
                  가드레일 프롬프트
                  <br />
                  수정
                </>
              }
              description=''
              position='left'
            />
            {/* <UIPopupBody></UIPopupBody> */}
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <UIButton2 className='btn-tertiary-gray' style={{ width: '80px' }} onClick={handleCancel}>
                    취소
                  </UIButton2>
                  <UIButton2 className='btn-tertiary-blue' style={{ width: '80px' }} onClick={handleNext}>
                    만들기
                  </UIButton2>
                </UIUnitGroup>
              </UIArticle>
            </UIPopupFooter>
          </UIPopupAside>
        }
      >
        {/* 우측 Contents 영역 콘텐츠 */}
        <section className='section-popup-content'>
          <UIPopupHeader title='가드레일 프롬프트 생성' description='가드레일에 적용할 시스템 프롬프트를 만들 수 있습니다.' position='right' />
          <UIPopupBody>
            {/* 이름 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                  이름
                </UITypography>
                <UIInput.Text value={''} placeholder='이름 입력' onChange={_e => {}} />
              </UIFormField>
            </UIArticle>

            {/* 프롬프트 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                  가드레일 프롬프트
                </UITypography>
                <UITextArea2 value={description} placeholder='가드레일 프롬프트 입력' onChange={e => setDescription(e.target.value)} maxLength={4000} />
              </UIFormField>
            </UIArticle>

            {/* 태그 */}
            <UIArticle>
              <UIInput.Tags tags={tags} onChange={setTags} placeholder='태그 입력' label='태그' required={true} />
            </UIArticle>
          </UIPopupBody>
        </section>
      </UILayerPopup>
    </>
  );
};
