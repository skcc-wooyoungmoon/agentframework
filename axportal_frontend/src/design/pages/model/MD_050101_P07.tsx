import React, { useState } from 'react';

import { UIButton2, UITypography } from '@/components/UI/atoms';
import { UIPopupHeader, UIPopupBody, UIArticle, UIPopupFooter, UIStepper, UIUnitGroup, UIInput, UITextArea2, UIDropdown } from '@/components/UI/molecules';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';

import { DesignLayout } from '../../components/DesignLayout';

// 스테퍼 데이터
const stepperItems = [
  {
    id: 'step1',
    label: '반입 모델 선택',
    step: 1,
  },
  {
    id: 'step2',
    label: '모델 정보 입력',
    step: 2,
  },
];

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

export const MD_050101_P07: React.FC = () => {
  const [isPopupOpen, setIsPopupOpen] = useState(true);
  const [selectedLoader, setSelectedLoader] = useState('Language');
  const [selectedLoader2, setSelectedLoader2] = useState('Huggingface');
  const [isLoaderDropdownOpen, setIsLoaderDropdownOpen] = useState(false);
  const [isLoader2DropdownOpen, setIsLoader2DropdownOpen] = useState(false);

  // 각 입력 필드별 개별 state
  const [nameValue, setNameValue] = useState('GIP/gpt-4o-mini 설명');
  const [textareaValue, setTextareaValue] = useState('');
  const [modelSizeValue, setModelSizeValue] = useState('7');
  const [licenseValue, setLicenseValue] = useState('');
  const [parameterValue, setParameterValue] = useState('');

  // tags 타입
  const [tags, setTags] = useState<string[]>([]);

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

            {/* 레이어 팝업 바디 */}
            <UIPopupBody>
              <UIArticle>
                <UIStepper currentStep={2} items={stepperItems} direction='vertical' />
              </UIArticle>
            </UIPopupBody>

            {/* 레이어 팝업 footer */}
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <UIButton2 className='btn-aside-gray'>취소</UIButton2>
                  <UIButton2 className='btn-aside-blue' disabled={true}>
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
          <UIPopupHeader title='모델 정보 입력' description='입력한 정보는 모델 관리 등록 시 자동 입력됩니다.' position='right' />
          {/* [251111_퍼블수정] 타이틀명칭 변경 : 모델 카탈로그 > 모델 관리 */}

          {/* 레이어 팝업 바디 */}
          <UIPopupBody>
            <UIArticle>
              {/* 251107_퍼블 텍스트값 수정 */}
              <UIUnitGroup gap={8} direction='column'>
                <UITypography variant='title-4' required={true} className='secondary-neutral-800 text-sb'>
                  모델명
                </UITypography>
                <UIInput.Text
                  value={nameValue}
                  onChange={e => {
                    setNameValue(e.target.value);
                  }}
                  placeholder='모델명 입력'
                  readOnly={true}
                />
              </UIUnitGroup>
            </UIArticle>

            {/* 설명 입력 필드 */}
            <UIArticle>
              <UIUnitGroup gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800'>
                  설명
                </UITypography>
                <UITextArea2 value={textareaValue} onChange={e => setTextareaValue(e.target.value)} maxLength={10} />
              </UIUnitGroup>
            </UIArticle>

            <UIArticle>
              <UIUnitGroup gap={8} direction='column'>
                <UITypography variant='title-4' required={false} className='secondary-neutral-800 text-sb'>
                  모델 크기(GB)
                </UITypography>
                <UIInput.Text
                  value={modelSizeValue}
                  onChange={e => {
                    setModelSizeValue(e.target.value);
                  }}
                  placeholder='모델 크기 입력'
                  readOnly={true}
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
                  isOpen={isLoader2DropdownOpen}
                  onClick={() => setIsLoader2DropdownOpen(!isLoader2DropdownOpen)}
                  onSelect={value => {
                    setSelectedLoader2(value);
                    setIsLoader2DropdownOpen(false);
                  }}
                  placeholder='공급사 선택'
                />
              </UIUnitGroup>
            </UIArticle>

            <UIArticle>
              <UIUnitGroup gap={8} direction='column'>
                <UITypography variant='title-4' required={true} className='secondary-neutral-800 text-sb'>
                  모델 유형
                </UITypography>
                {/* 251107_퍼블 텍스트값 수정 */}
                <UIDropdown
                  required={true}
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
                  라이센스
                </UITypography>{' '}
                <UIInput.Text
                  value={licenseValue}
                  onChange={e => {
                    setLicenseValue(e.target.value);
                  }}
                  placeholder='라이센스 입력'
                />
              </UIUnitGroup>
            </UIArticle>

            <UIArticle>
              <UIUnitGroup gap={8} direction='column'>
                <UITypography variant='title-4' required={false} className='secondary-neutral-800 text-sb'>
                  파라미터 수(B)
                </UITypography>{' '}
                <UIInput.Text
                  value={parameterValue}
                  onChange={e => {
                    setParameterValue(e.target.value);
                  }}
                  placeholder='파라미터 수 입력'
                />
              </UIUnitGroup>
            </UIArticle>

            {/* 태그 입력 필드 */}
            <UIArticle>
              <UIInput.Tags tags={tags} onChange={setTags} placeholder='태그 입력' label='태그' />
            </UIArticle>

            {/* 지원 언어 입력 필드 */}
            <UIArticle>
              <UIInput.Tags tags={tags} onChange={setTags} placeholder='지원 언어 입력' label='지원 언어' />
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
