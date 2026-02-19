import React, { useState } from 'react';

import { UIIcon2, UIButton2, UITypography } from '@/components/UI/atoms';
import { UIStepper, UIDropdown, UIInput, type UIStepperItem } from '@/components/UI/molecules';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';

import { DesignLayout } from '../components/DesignLayout';

export const P_Sample: React.FC = () => {
  const [isPopupOpen, setIsPopupOpen] = useState(true); // 팝업이므로 기본적으로 열려있음
  const [selectedType, setSelectedType] = useState('VLMOCR');
  const [isDropdownOpen, setIsDropdownOpen] = useState(false);
  const [modelId, setModelId] = useState('');
  const [prompt, setPrompt] = useState('');
  const [maxTokens, setMaxTokens] = useState('');
  const [numWorkers, setNumWorkers] = useState('');
  const [dpi, setDpi] = useState('');
  const [timeout, setTimeout] = useState('');
  const [maxRetries, setMaxRetries] = useState('');

  const handleClose = () => {
    setIsPopupOpen(false);
  };

  const handlePrevious = () => {
    // 이전 단계로 이동 로직
  };

  // 스테퍼 데이터
  const stepperItems: UIStepperItem[] = [
    {
      id: 'step1',
      label: '데이터도구 선택',
      step: 1,
    },
    {
      id: 'step2',
      label: '상세정보 입력',
      step: 2,
    },
  ];

  // 드롭다운 옵션 데이터
  const typeOptions = [
    { value: 'VLMOCR', label: 'VLMOCR' },
    { value: 'Docling', label: 'Docling' },
    { value: 'Other', label: 'Other' },
    { value: 'Custom', label: 'Custom' },
  ];

  const handleDropdownClick = () => {
    setIsDropdownOpen(!isDropdownOpen);
  };

  const handleDropdownSelect = (value: string) => {
    setSelectedType(value);
    setIsDropdownOpen(false);
  };

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
            {/* 데이터 도구 만들기 제목 */}
            <UITypography variant='title-1' className='secondary-neutral-900 text-sb'>
              데이터 도구 만들기
            </UITypography>

            {/* 스테퍼 영역 */}
            <div>
              <UIStepper currentStep={2} items={stepperItems} direction='vertical' className='w-full' />
            </div>

            {/* 버튼 그룹  */}
            <div className='flex justify-start gap-2'></div>
          </UIPopupAside>
        }
      >
        {/* 우측 Contents 영역 콘텐츠 */}
        <div className='h-full flex flex-col'>
          {/* 콘텐츠 영역 */}
          <div className='flex-1 px-10 pt-10 pb-8'>
            <div className='max-w-2xl'>
              {/* 서브 타이틀 */}
              <UITypography variant='title-1' className='secondary-neutral-800 text-sb'>
                Ingestion Tool 만들기
              </UITypography>

              <div className='space-y-8'>
                {/* 유형 선택 필드 - UIDropdown 사용 */}
                <div>
                  <label className='block text-base leading-6 font-semibold tracking-[-0.005em] text-gray-900 mb-2'>
                    <span className='inline-flex items-center'>
                      <span>유형 선택</span>
                      <span className='ml-0.5 w-1 h-4 relative'>
                        <UIIcon2 className='ico-system-16-dot-1 absolute top-px text-negative-red !w-1 !h-1' />
                      </span>
                    </span>
                  </label>
                  <UIDropdown
                    value={selectedType}
                    options={typeOptions}
                    placeholder='유형을 선택하세요'
                    isOpen={isDropdownOpen}
                    onClick={handleDropdownClick}
                    onSelect={handleDropdownSelect}
                    state='inactive'
                  />
                </div>

                {/* Model ID 입력 필드 */}
                <UIInput.Text value={modelId} placeholder='Model ID 입력' onChange={e => setModelId(e.target.value)} />

                {/* Prompt 입력 필드 */}
                <UIInput.Text value={prompt} placeholder='Prompt 입력' onChange={e => setPrompt(e.target.value)} />

                {/* Max Tokens 입력 필드 */}
                <UIInput.Text value={maxTokens} placeholder='Max Tokens 입력' onChange={e => setMaxTokens(e.target.value)} />

                {/* Num of Workers 입력 필드 */}
                <UIInput.Text value={numWorkers} placeholder='Num of Workers 입력' onChange={e => setNumWorkers(e.target.value)} />

                {/* Dpi 입력 필드 */}
                <UIInput.Text value={dpi} placeholder='Dpi 입력' onChange={e => setDpi(e.target.value)} />

                {/* Timeout 입력 필드 */}
                <UIInput.Text value={timeout} placeholder='Timeout 입력' onChange={e => setTimeout(e.target.value)} />

                {/* Max Retries 입력 필드 */}
                <UIInput.Text value={maxRetries} placeholder='Max Retries 입력' onChange={e => setMaxRetries(e.target.value)} />

                {/* 이전 버튼 - 드랍다운 아래 32px 간격 */}
                <div className='flex justify-start'>
                  <UIButton2 onClick={handlePrevious} className='btn-secondary-gray' style={{ width: '80px' }}>
                    이전
                  </UIButton2>
                </div>
              </div>
            </div>
          </div>
        </div>
      </UILayerPopup>
    </>
  );
};
