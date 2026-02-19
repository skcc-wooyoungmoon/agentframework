import React, { useState } from 'react';
import { UITypography, UIButton2 } from '@/components/UI/atoms';
import { UIPopupHeader, UIPopupFooter, UIArticle, UIPopupBody, UIFormField, UIInput } from '@/components/UI/molecules';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { DesignLayout } from '../../components/DesignLayout';
import { UIUnitGroup } from '@/components/UI/molecules';
import { UICode } from '@/components/UI/atoms/UICode';

export const DT_030402_P01: React.FC = () => {
  const [isPopupOpen] = useState(true); // 팝업이므로 항상 열려있음

  // 폼 데이터 상태
  const [formData, setFormData] = useState({
    typeValue: '',
    description: '',
  });

  const handleInputChange = (field: string, value: string) => {
    setFormData(prev => ({
      ...prev,
      [field]: value,
    }));
  };

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
              <UIPopupHeader title={'Custom Script 수정'} description='' position='left' />
              {/* 레이어 팝업 바디 */}
              <UIPopupBody>
                <UIArticle>{/* 바디 영역 */}</UIArticle>
              </UIPopupBody>
              {/* 레이어 팝업 바디 : [참고] 이 페이지에는 왼쪽 body 영역 없음. */}
              <UIPopupFooter>
                <UIArticle>
                  <UIUnitGroup gap={8} direction='row' align='start'>
                    <UIButton2 className='btn-tertiary-gray' style={{ width: '80px' }}>
                      취소
                    </UIButton2>
                    <UIButton2 className='btn-tertiary-blue' style={{ width: '80px' }}>
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
          {/* 레이어 팝업 바디 */}
          <UIPopupBody>
            {/* 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                  이름
                </UITypography>
                <UIInput.Text
                  value='Genera CSV Loader'
                  placeholder='이름 입력'
                  onChange={e => {
                    handleInputChange('typeValue', e.target.value);
                  }}
                />
              </UIFormField>
            </UIArticle>

            {/* 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                  설명
                </UITypography>
                <UIInput.Text
                  value='Genera CSV Loader for Quick Builder'
                  placeholder='설명 입력'
                  onChange={e => {
                    handleInputChange('description', e.target.value);
                  }}
                />
              </UIFormField>
            </UIArticle>

            {/* 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                  유형
                </UITypography>
                <UIInput.Text
                  value={formData.description}
                  placeholder='Loader'
                  disabled={true}
                  onChange={e => {
                    handleInputChange('description', e.target.value);
                  }}
                />
              </UIFormField>
            </UIArticle>

            {/* 입력 필드 */}
            <UIArticle>
              <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                Script
              </UITypography>

              {/* 에디터 화면(샘플 이미지) */}
              <div className='mt-[16px]'>
                {/* 실제 에디트 코드 영역 */}
                <UICode value={'여기는 에디터 화면입니다. 테스트 testtesttesttest'} language='python' theme='dark' width='100%' minHeight='460px' height='460px' readOnly={false} />
              </div>
            </UIArticle>
          </UIPopupBody>
          {/* 레이어 팝업 footer */}
          {/*
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <UIButton2 className='btn-secondary-gray'>이전</UIButton2>
                </UIUnitGroup>
              </UIArticle>
            </UIPopupFooter> 
            */}
        </section>
      </UILayerPopup>
    </>
  );
};
