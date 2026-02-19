import React, { useState } from 'react';

import { UITypography, UIButton2 } from '@/components/UI/atoms';
import { UIStepper, UIUnitGroup, UIPopupHeader, UIPopupBody, UIPopupFooter, UIFormField } from '@/components/UI/molecules';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { UIInput } from '@/components/UI/molecules';
import { DesignLayout } from '../../components/DesignLayout';

export const MD_010101_P03: React.FC = () => {
  const [isPopupOpen] = useState(true); // 팝업이므로 항상 열려있음

  const handleClose = () => {
    // 팝업 닫기 동작 제거 (디자인 페이지이므로 항상 열려있음)
  };

  // text 타입
  const [nameValue, setNameValue] = useState('');
  const [apiValue, setApiValue] = useState('');

  const [tags, setTags] = useState<string[]>([]);
  return (
    <>
      {/* DesignLayout 기본 구조 */}
      <DesignLayout
        initialMenu={{ id: 'model', label: '모델' }}
        initialSubMenu={{
          id: 'model-catalog',
          label: '모델카탈로그 조회',
          icon: 'ico-lnb-menu-20-model-catalog',
        }}
      >
        <div className='flex items-center justify-center h-full'>
          <div className='text-center'>
            <UITypography variant='title-1' className='secondary-neutral-800 text-sb'>
              모델카탈로그 조회
            </UITypography>
            <UITypography variant='body-1' className='secondary-neutral-600'>
              모델 수정 진행 중...
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
            <UIPopupHeader title='모델 등록' position='left' />
            {/* 레이어 팝업 바디 */}
            <UIPopupBody>
              {/* 스테퍼 영역 */}
              <UIStepper
                items={[
                  { id: 'step1', step: 1, label: '모델 선택' },
                  { id: 'step2', step: 2, label: '모델 정보 확인' },
                  { id: 'step3', step: 3, label: '추가 정보 입력' },
                ]}
                currentStep={3}
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
                    등록
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
          <UIPopupHeader
            title='추가 정보 입력'
            description='Serverless 모델의 API Key 정보와 프로젝트 내에서 모델의 용도 및 식별을 위한 추가 정보를 입력해주세요.'
            position='right'
          />
          {/* 레이어 팝업 바디 */}
          <UIPopupBody>
            {/* 모델 이름 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb'>
                  표시 이름
                </UITypography>
                <UIInput.Text
                  value={nameValue}
                  onChange={e => {
                    setNameValue(e.target.value);
                  }}
                  placeholder='표시 이름 입력'
                />
              </UIFormField>
            </UIArticle>

            {/* 라이센스 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb' required={true}>
                  API Key
                </UITypography>
                <UIInput.Text
                  value={apiValue}
                  onChange={e => {
                    setApiValue(e.target.value);
                  }}
                  placeholder='API Key 입력'
                />
              </UIFormField>
            </UIArticle>

            {/* 태그 입력 필드 */}
            <UIArticle>
              <UIInput.Tags tags={tags} onChange={setTags} placeholder='태그 입력' label='태그' />
            </UIArticle>
          </UIPopupBody>
          {/* 레이어 팝업 footer */}
          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <UIButton2 className='btn-secondary-gray' style={{ width: '80px' }}>
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
