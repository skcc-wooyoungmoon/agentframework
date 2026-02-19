import React, { useState } from 'react';

import { UIButton2 } from '@/components/UI/atoms';
import { UIPopupHeader, UIPopupFooter, UIArticle, UIPopupBody, UIFormField } from '@/components/UI/molecules';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { UIInput, UITextArea2 } from '@/components/UI/molecules/input';
import { DesignLayout } from '../../components/DesignLayout';
import { UIUnitGroup } from '@/components/UI/molecules';
import { UITypography } from '@/components/UI/atoms';

export const DP_020102_P01: React.FC = () => {
  const [isPopupOpen] = useState(true); // 팝업이므로 항상 열려있음

  // textarea 타입
  const [textareaValue, setTextareaValue] = useState('');

  const [textValue, setTextValue] = useState('');

  const handleClose = () => {
    // 팝업 닫기 동작 제거 (디자인 페이지이므로 항상 열려있음)
    // 
  };

  return (
    <>
      {/* DesignLayout 기본 구조 */}
      <DesignLayout
        initialMenu={{ id: 'deploy', label: '배포' }}
        initialSubMenu={{
          id: 'deploy-tools',
          label: '배포도구',
          icon: 'ico-lnb-menu-20-deploy',
        }}
      >
        <div className='flex items-center justify-center h-full'>
          <div className='text-center'>
            <UITypography variant='title-1' className='secondary-neutral-800 text-sb'>기본 정보</UITypography>
            <UITypography variant='body-1' className='secondary-neutral-600'>에이전트 배포하기 진행 중...</UITypography>
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
              <UIPopupHeader
                title={
                  <>
                    에이전트 배포
                    <br />
                    정보 수정
                  </>
                }
                description=''
                position='left'
              />
              {/* <UIPopupBody></UIPopupBody> */}
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
          {/* <UIPopupHeader title='' description='' position='right' /> */}

          {/* 레이어 팝업 바디 */}
          <UIPopupBody>
            {/* 역할명 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                  배포명
                </UITypography>
                <UIInput.Text
                  value={textValue}
                  onChange={e => {
                    setTextValue(e.target.value);
                  }}
                  placeholder='배포명 입력'
                  readOnly={false}
                />
              </UIFormField>
            </UIArticle>

            {/* 설명 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb'>
                  설명
                </UITypography>
                <UITextArea2 value={textareaValue} placeholder='설명 입력' onChange={e => setTextareaValue(e.target.value)} maxLength={100} />
              </UIFormField>
            </UIArticle>
          </UIPopupBody>
          {/* 레이어 팝업 footer */}
          <UIPopupFooter>
            {/* <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <UIButton2 className='btn-secondary-gray'>이전</UIButton2>
                  <UIButton2 className='btn-secondary-blue'>다음</UIButton2>
                </UIUnitGroup>
              </UIArticle> */}
          </UIPopupFooter>
        </section>
      </UILayerPopup>
    </>
  );
};
