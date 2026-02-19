import React, { useState } from 'react';
// import { UIStepper } from '@/components/UI/molecules';
import { UITypography, UIButton2 } from '@/components/UI/atoms';
import { UIPopupHeader, UIPopupFooter, UIArticle, UIPopupBody, UIFormField } from '@/components/UI/molecules';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { UIDropdown } from '@/components/UI/molecules/dropdown/UIDropdown';
import { DesignLayout } from '../../components/DesignLayout';
import { UIUnitGroup } from '@/components/UI/molecules';
import { UIInput } from '@/components/UI/molecules/input';

export const DT_030302_P01: React.FC = () => {
  const [isPopupOpen] = useState(true); // 팝업이므로 항상 열려있음
  const [selectedLoader, setSelectedLoader] = useState('RecursiveCharacter');
  const [isLoaderDropdownOpen, setIsLoaderDropdownOpen] = useState(false);

  const handleClose = () => {
    // 팝업 닫기 동작 제거 (디자인 페이지이므로 항상 열려있음)
    // 
  };

  const loaderOptions = [
    { value: 'RecursiveCharacter', label: 'RecursiveCharacter' },
    { value: 'RecursiveCharacter2', label: 'RecursiveCharacter2' },
    { value: 'RecursiveCharacter3', label: 'RecursiveCharacter3' },
  ];

  // text 타입
  const [textValue, setTextValue] = useState('');

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
              <UIPopupHeader title='Ingestion Tools 수정' description='' position='left' />
              {/* 레이어 팝업 바디 */}
              <UIPopupBody></UIPopupBody>
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
            {/* 이름 입력 필드 */}
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

            {/* 유형  입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={false}>
                  유형
                </UITypography>
                <div>
                  <UIInput.Text
                    value={textValue}
                    onChange={e => {
                      setTextValue(e.target.value);
                    }}
                    placeholder='유형 입력'
                    disabled={true}
                  />
                </div>
              </UIFormField>
            </UIArticle>

            {/* Model ID 입력 필드 */}
            <UIArticle>
              <UIUnitGroup gap={8} direction='column'>
                <UITypography variant='title-4' required={true} className='secondary-neutral-800 text-sb'>
                  Model ID
                </UITypography>
                <UIDropdown
                  value={selectedLoader}
                  options={loaderOptions}
                  isOpen={isLoaderDropdownOpen}
                  onClick={() => setIsLoaderDropdownOpen(!isLoaderDropdownOpen)}
                  onSelect={value => {
                    setSelectedLoader(value);
                    setIsLoaderDropdownOpen(false);
                  }}
                />
              </UIUnitGroup>
            </UIArticle>

            {/* Prompt 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={false}>
                  Prompt
                </UITypography>
                <div>
                  <UIInput.Text
                    value={textValue}
                    onChange={e => {
                      setTextValue(e.target.value);
                    }}
                    placeholder='Prompt 입력'
                  />
                </div>
              </UIFormField>
            </UIArticle>

            {/* Max Tokens 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={false}>
                  Max Tokens
                </UITypography>
                <div>
                  <UIInput.Text
                    value={textValue}
                    onChange={e => {
                      setTextValue(e.target.value);
                    }}
                    placeholder='Max Tokens 입력'
                  />
                </div>
              </UIFormField>
            </UIArticle>

            {/* Num of Workers 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={false}>
                  Num of Workers
                </UITypography>
                <div>
                  <UIInput.Text
                    value={textValue}
                    onChange={e => {
                      setTextValue(e.target.value);
                    }}
                    placeholder='Num of Workers 입력'
                  />
                </div>
              </UIFormField>
            </UIArticle>

            {/* Dpi 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={false}>
                  Dpi
                </UITypography>
                <div>
                  <UIInput.Text
                    value={textValue}
                    onChange={e => {
                      setTextValue(e.target.value);
                    }}
                    placeholder='Dpi 입력'
                  />
                </div>
              </UIFormField>
            </UIArticle>

            {/* Timeout 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={false}>
                  Timeout
                </UITypography>
                <div>
                  <UIInput.Text
                    value={textValue}
                    onChange={e => {
                      setTextValue(e.target.value);
                    }}
                    placeholder='Timeout 입력'
                  />
                </div>
              </UIFormField>
            </UIArticle>

            {/* Mas Retries 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={false}>
                  Mas Retries
                </UITypography>
                <div>
                  <UIInput.Text
                    value={textValue}
                    onChange={e => {
                      setTextValue(e.target.value);
                    }}
                    placeholder='Mas Retries 입력'
                  />
                </div>
              </UIFormField>
            </UIArticle>
          </UIPopupBody>
          {/* 레이어 팝업 footer */}
          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <UIButton2 className='btn-secondary-gray'>이전</UIButton2>
                {/* <UIButton2 className='btn-secondary-blue'>다음</UIButton2> */}
              </UIUnitGroup>
            </UIArticle>
          </UIPopupFooter>
        </section>
      </UILayerPopup>
    </>
  );
};
