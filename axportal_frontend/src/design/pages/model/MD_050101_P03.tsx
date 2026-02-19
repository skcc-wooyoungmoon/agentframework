import React, { useState } from 'react';

import { UIButton2, UITypography } from '@/components/UI/atoms';
import { UIPopupHeader, UIPopupBody, UIArticle, UIPopupFooter, UIUnitGroup } from '@/components/UI/molecules';
import { UIDropdown } from '@/components/UI/molecules/dropdown/UIDropdown';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { UIInput, UITextArea2 } from '@/components/UI/molecules/input';

import { DesignLayout } from '../../components/DesignLayout';

const loaderOptions = [
  { value: 'Language', label: 'Language' },
  { value: 'Language2', label: 'Language2' },
  { value: 'Language3', label: 'Language3' },
];
const loaderOptions2 = [
  { value: 'Huggingface', label: 'Huggingface' },
  { value: 'Huggingface2', label: 'Huggingface2' },
  { value: 'Huggingface3', label: 'Huggingface3' },
];

export const MD_050101_P03: React.FC = () => {
  const [isPopupOpen, setIsPopupOpen] = useState(true);
  const [selectedLoader, setSelectedLoader] = useState('Language');
  const [selectedLoader2, setSelectedLoader2] = useState('Huggingface');
  const [isLoaderDropdownOpen, setIsLoaderDropdownOpen] = useState(false);

  // text 타입
  const [textValue, setTextValue] = useState('');

  // textarea 타입
  const [, setTextareaValue] = useState('');

  const handleClose = () => {
    setIsPopupOpen(false);
  };

  return (
    <>
      <DesignLayout
        initialMenu={{ id: 'home', label: '홈' }}
        initialSubMenu={{
          id: 'home-ide',
          label: 'IDE',
          icon: 'ico-lnb-menu-20-home',
        }}
      >
        <div className='flex items-center justify-center h-full'>
          <div className='text-center'>
            <UITypography variant='title-1' className='secondary-neutral-800 text-sb'>
              홈
            </UITypography>
            <UITypography variant='body-1' className='secondary-neutral-600'>
              IDE 생성...
            </UITypography>
          </div>
        </div>
      </DesignLayout>

      <UILayerPopup
        isOpen={isPopupOpen}
        onClose={handleClose}
        size='fullscreen'
        showOverlay={true}
        leftContent={
          <UIPopupAside>
            {/* 레이어 팝업 헤더 */}
            <UIPopupHeader title='모델 반입' description='' position='left' />

            {/* 레이어 팝업 footer */}
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <UIButton2 className='btn-aside-gray'>취소</UIButton2>
                  <UIButton2 className='btn-aside-blue' disabled={false}>
                    반입요청
                  </UIButton2>
                </UIUnitGroup>
              </UIArticle>
            </UIPopupFooter>
          </UIPopupAside>
        }
      >
        <section className='section-popup-content'>
          {/* 레이어 팝업 헤더 */}
          <UIPopupHeader title='모델 정보 입력' description='' position='right' />

          {/* 레이어 팝업 바디 */}
          <UIPopupBody>
            <UIArticle>
              <UIUnitGroup gap={8} direction='column'>
                <UITypography variant='title-4' required={true} className='secondary-neutral-800 text-sb'>
                  모델명
                </UITypography>
                {/* 251107_퍼블 텍스트값 수정 */}
                <UIInput.Text
                  value={textValue}
                  onChange={e => {
                    setTextValue(e.target.value);
                  }}
                  placeholder='모델명 입력'
                />
              </UIUnitGroup>
            </UIArticle>

            {/* 설명 입력 필드 */}
            <UIArticle>
              <UIUnitGroup gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800'>
                  설명
                </UITypography>
                <UITextArea2 value={'GIP/gpt-4o-mini 설명'} placeholder='설명 입력' onChange={e => setTextareaValue(e.target.value)} maxLength={100} />
              </UIUnitGroup>
            </UIArticle>

            <UIArticle>
              <UIUnitGroup gap={8} direction='column'>
                <UITypography variant='title-4' required={true} className='secondary-neutral-800 text-sb'>
                  모델 유형
                </UITypography>
                {/* 251107_퍼블 텍스트값 수정 */}
                <UIDropdown
                  value={selectedLoader}
                  options={loaderOptions}
                  isOpen={isLoaderDropdownOpen}
                  onClick={() => setIsLoaderDropdownOpen(!isLoaderDropdownOpen)}
                  onSelect={value => {
                    setSelectedLoader(value);
                    setIsLoaderDropdownOpen(false);
                  }}
                  placeholder='모델 유형 선택'
                />
              </UIUnitGroup>
            </UIArticle>

            <UIArticle>
              <UIUnitGroup gap={8} direction='column'>
                <UITypography variant='title-4' required={true} className='secondary-neutral-800 text-sb'>
                  공급사
                </UITypography>
                {/* 251107_퍼블 텍스트값 수정 */}
                <UIDropdown
                  value={selectedLoader2}
                  options={loaderOptions2}
                  isOpen={isLoaderDropdownOpen}
                  onClick={() => setIsLoaderDropdownOpen(!isLoaderDropdownOpen)}
                  onSelect={value => {
                    setSelectedLoader2(value);
                    setIsLoaderDropdownOpen(false);
                  }}
                  placeholder='공급사 선택'
                />
              </UIUnitGroup>
            </UIArticle>

            <UIArticle>
              <UIUnitGroup gap={8} direction='column'>
                <UITypography variant='title-4' required={false} className='secondary-neutral-800 text-sb'>
                  파라미터 수(B)
                </UITypography>
                <UIInput.Text
                  value={'7'}
                  onChange={e => {
                    setTextValue(e.target.value);
                  }}
                  placeholder='파라미터 수 입력'
                />
              </UIUnitGroup>
            </UIArticle>
            <UIArticle>
              <UIUnitGroup gap={8} direction='column'>
                <UITypography variant='title-4' required={true} className='secondary-neutral-800 text-sb'>
                  URL
                </UITypography>
                <UIInput.Text
                  value={'https://api.platform.a49.com/v1.1'}
                  onChange={e => {
                    setTextValue(e.target.value);
                  }}
                  placeholder='URL 입력'
                />
              </UIUnitGroup>
            </UIArticle>
            <UIArticle>
              <UIUnitGroup gap={8} direction='column'>
                <UITypography variant='title-4' required={true} className='secondary-neutral-800 text-sb'>
                  identifier
                </UITypography>
                {/* 251107_퍼블 속성값 수정 */}
                <UIInput.Text
                  value={'azure/openai/gpt-4o-mini-2024-07-18'}
                  onChange={e => {
                    setTextValue(e.target.value);
                  }}
                  placeholder='identifier 입력'
                  readOnly={true}
                />
              </UIUnitGroup>
            </UIArticle>
          </UIPopupBody>

          {/* 레이어 팝업 footer */}
          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} align='start'>
                <UIButton2 className='btn-secondary-gray'>이전</UIButton2>
              </UIUnitGroup>
            </UIArticle>
          </UIPopupFooter>
        </section>
      </UILayerPopup>
    </>
  );
};
