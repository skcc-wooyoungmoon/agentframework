import React, { useState } from 'react';

import { UIButton2, UITypography } from '@/components/UI/atoms';
import { UIArticle, UIFormField, UIInput, UIPopupBody, UIPopupFooter, UIPopupHeader, UIUnitGroup, UIStepper, UITextArea2 } from '@/components/UI/molecules';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';

import { DesignLayout } from '../../components/DesignLayout';

export const PR_030101_P01: React.FC = () => {
  const [isPopupOpen, setIsPopupOpen] = useState(true);
  const [guardrailName, setGuardrailName] = useState('');
  const [tagValue, setTagValue] = useState('');

  const handleClose = () => {
    setIsPopupOpen(false);
  };

  // textarea 타입
  const [maxLengthTextareaValue, setMaxLengthTextareaValue] = useState('');

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
            <UIPopupHeader title='가드레일 생성' position='left' />
            {/* 레이어 팝업 바디 */}
            <UIPopupBody>
              {/* 스테퍼 영역 */}
              <UIStepper
                items={[
                  { id: 'step1', step: 1, label: '기본 정보 입력' },
                  { id: 'step2', step: 2, label: '배포 모델 선택' },
                ]}
                currentStep={1}
                direction='vertical'
              />
            </UIPopupBody>
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <UIButton2 className='btn-tertiary-gray' style={{ width: 80 }}>
                    취소
                  </UIButton2>
                  <UIButton2 className='btn-tertiary-blue' style={{ width: 80 }} disabled={true}>
                    만들기
                  </UIButton2>
                </UIUnitGroup>
              </UIArticle>
            </UIPopupFooter>
          </UIPopupAside>
        }
      >
        {/* 우측 Contents 영역 콘텐츠 - 기존 컴포넌트 사용 */}
        <section className='section-popup-content'>
          {/* 레이어 팝업 헤더 */}
          <UIPopupHeader title='가드레일 생성' description='분류와 가드레일을 입력해 새로운 필터링을 만들어보세요.' position='right' />

          {/* 레이어 팝업 바디 */}
          <UIPopupBody>
            <UIArticle>
              <UIUnitGroup gap={8} direction='column'>
                <UITypography variant='title-4' required={true} className='secondary-neutral-800 text-sb'>
                  이름
                </UITypography>
                <div>
                  {' '}
                  <UIInput.Text
                    value={guardrailName}
                    onChange={e => {
                      setGuardrailName(e.target.value);
                    }}
                    placeholder='이름 입력' // [251104_퍼블수정] : '가드레일명 입력' > '이름 입력'
                  />
                </div>
              </UIUnitGroup>
            </UIArticle>

            {/* 직접 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={false}>
                  설명
                </UITypography>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <div className='flex-1'>
                    {' '}
                    <UITextArea2 value={maxLengthTextareaValue} maxLength={4000} onChange={e => setMaxLengthTextareaValue(e.target.value)} placeholder='설명 입력' />
                  </div>
                </UIUnitGroup>
              </UIFormField>
            </UIArticle>
            {/* 가드레일 프롬프트 선택 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                  가드레일 프롬프트
                </UITypography>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <div className='flex-1'>
                    <UIInput.Text
                      value={tagValue}
                      onChange={e => {
                        setTagValue(e.target.value);
                      }}
                      placeholder='가드레일 프롬프트 선택'
                    />
                  </div>
                  <div>
                    <UIButton2 className='btn-secondary-outline !min-w-[64px]'>선택</UIButton2>
                  </div>
                </UIUnitGroup>
              </UIFormField>
            </UIArticle>
          </UIPopupBody>
          {/* 레이어 팝업 footer */}
          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                {/* <UIButton2 className='btn-secondary-gray'>이전</UIButton2> */}
                <UIButton2 className='btn-secondary-blue' disabled={false}>
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
