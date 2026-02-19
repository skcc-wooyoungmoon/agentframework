import React, { useState } from 'react';

import { UIButton2, UITypography } from '@/components/UI/atoms';
import { UIImage } from '@/components/UI/atoms/UIImage';
import { UIStepper, type UIStepperItem } from '@/components/UI/molecules';
import { UIArticle, UIPopupBody, UIPopupFooter, UIPopupHeader, UIUnitGroup } from '@/components/UI/molecules';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';

import { DesignLayout } from '../../components/DesignLayout';

export const AG_010101_P02: React.FC = () => {
  const [isPopupOpen, setIsPopupOpen] = useState(true);
  const [toolType, setToolType] = useState('');

  const handleClose = () => {
    setIsPopupOpen(false);
  };

  const handleNext = () => {
    // 다음 단계 로직
  };

  const handleToolTypeChange = (_value: string) => {
    setToolType(_value);
  };

  // 스테퍼 데이터
  const stepperItems: UIStepperItem[] = [
    {
      id: 'step1',
      label: '템플릿 선택',
      step: 1,
    },
    {
      id: 'step2',
      label: '기본 정보 입력',
      step: 2,
    },
  ];

  return (
    <>
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
              에이전트
            </UITypography>
            <UITypography variant='body-1' className='secondary-neutral-600'>
              에이전트 등록하기 진행 중...
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
          /* 좌측 Step 영역 콘텐츠 */
          <UIPopupAside>
            {/* 레이어 팝업 헤더 */}
            <UIPopupHeader title='에이전트 등록하기' description='' position='left' />

            {/* 레이어 팝업 바디 */}
            <UIPopupBody>
              <UIArticle>
                <UIStepper currentStep={1} items={stepperItems} direction='vertical' />
              </UIArticle>
            </UIPopupBody>

            {/* 레이어 팝업 푸터 */}
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <UIButton2 className='btn-tertiary-gray' style={{ width: '80px' }}>
                    취소
                  </UIButton2>
                  <UIButton2 className='btn-tertiary-blue' style={{ width: '80px' }} disabled={true}>
                    저장
                  </UIButton2>
                </UIUnitGroup>
              </UIArticle>
            </UIPopupFooter>
          </UIPopupAside>
        }
      >
        <section className='section-popup-content'>
          <UIPopupHeader
            title='템플릿 선택'
            description='에이전트를 만들기 위한 빌더 템플릿을 선택해 주세요. 일부 템플릿에 포함된 기본 프롬프트는 배포가 제한될 수 있습니다.'
            position='right'
          />
          <UIPopupBody>
            <UIArticle>
              {/* 첫번째 줄: 빠른 시작, 새로운 템플릿 */}
              <div className='grid grid-cols-3 gap-6'>
                <div className='flex flex-col space-y-5'>
                  <div
                    className={`relative w-[405px] h-[240px] rounded-[20px] p-6 cursor-pointer transition-all duration-200 flex items-center justify-center ${
                      toolType === 'empty_template' ? 'bg-[#f3f6fb] border-2 border-[#005df9]' : 'bg-col-gray-100 border-2 border-transparent hover:bg-gray-100'
                    }`}
                    onClick={() => handleToolTypeChange('empty_template')}
                  >
                    <UIImage src='/assets/images/agent/ico-radio-ag-visual02.svg' alt='빈 템플릿' className='max-w-full max-h-full' />
                  </div>
                  <UIUnitGroup gap={8} direction='column' align='start'>
                    <UITypography variant='body-1' className='secondary-neutral-800 text-sb'>
                      빈 템플릿
                    </UITypography>

                    <UITypography variant='body-2' className='secondary-neutral-600'>
                      비어있는 템플릿에서 에이전트를 생성할 수 있습니다.
                    </UITypography>
                  </UIUnitGroup>
                </div>

                {/* [251105_퍼블수정] : 위치이동 [S] */}
                <div className='flex flex-col space-y-5'>
                  {/* [251104_퍼블수정] bg-gray-50 클래스를 bg-col-gray-100 이렇게 변경 */}
                  <div
                    className={`relative w-[405px] h-[240px] rounded-[20px] p-6 cursor-pointer transition-all duration-200 flex items-center justify-center ${
                      toolType === 'rag' ? 'bg-[#f3f6fb] border-2 border-[#005df9]' : 'bg-col-gray-100 border-2 border-transparent hover:bg-gray-100'
                    }`}
                    onClick={() => handleToolTypeChange('rag')}
                  >
                    <div className='absolute top-4 left-4 px-1.5 py-0.5 text-xs leading-5 bg-[#C7CEDC] rounded-full'>
                      <UITypography variant='caption-2' className=' secondary-neutral-f text-sb'>
                        기존 예제
                      </UITypography>
                    </div>
                    <UIImage src='/assets/images/agent/ico-radio-ag-visual03.svg' alt='RAG' className='max-w-full max-h-full' />
                  </div>
                  <UIUnitGroup gap={8} direction='column' align='start'>
                    <UITypography variant='body-1' className='secondary-neutral-800 text-sb'>
                      RAG
                    </UITypography>
                    <UITypography variant='body-2' className='secondary-neutral-600'>
                      사용자의 질의를 AI가 한 번 더 해석해서 검색합니다.
                    </UITypography>
                  </UIUnitGroup>
                </div>

                <div className='flex flex-col space-y-5'>
                  {/* [251104_퍼블수정] bg-gray-50 클래스를 bg-col-gray-100 이렇게 변경 */}
                  <div
                    className={`relative w-[405px] h-[240px] rounded-[20px] p-6 cursor-pointer transition-all duration-200 flex items-center justify-center ${
                      toolType === 'chatbot' ? 'bg-[#f3f6fb] border-2 border-[#005df9]' : 'bg-col-gray-100 border-2 border-transparent hover:bg-gray-100'
                    }`}
                    onClick={() => handleToolTypeChange('chatbot')}
                  >
                    <div className='absolute top-4 left-4 px-1.5 py-0.5 text-xs leading-5 bg-[#C7CEDC] rounded-full'>
                      <UITypography variant='caption-2' className=' secondary-neutral-f text-sb'>
                        기존 예제
                      </UITypography>
                    </div>
                    <UIImage src='/assets/images/agent/ico-radio-ag-visual04.svg' alt='챗봇' className='max-w-full max-h-full' />
                  </div>
                  <UIUnitGroup gap={8} direction='column' align='start'>
                    <UITypography variant='body-1' className='secondary-neutral-800 text-sb'>
                      챗봇
                    </UITypography>
                    <UITypography variant='body-2' className='secondary-neutral-600'>
                      챗봇 예제입니다.
                    </UITypography>
                  </UIUnitGroup>
                </div>

                {/* // [251105_퍼블수정] : 위치이동 [E] */}
              </div>
            </UIArticle>
            <UIArticle>
              {/* 두번째 줄: 기존 예제 3개 */}
              <div className='grid grid-cols-3 gap-6 mb-8'>
                <div className='flex flex-col space-y-5'>
                  {/* [251104_퍼블수정] bg-gray-50 클래스를 bg-col-gray-100 이렇게 변경 */}
                  <div
                    className={`relative w-[405px] h-[240px] rounded-[20px] p-6 cursor-pointer transition-all duration-200 flex items-center justify-center ${
                      toolType === 'translator' ? 'bg-[#f3f6fb] border-2 border-[#005df9]' : 'bg-col-gray-100 border-2 border-transparent hover:bg-gray-100'
                    }`}
                    onClick={() => handleToolTypeChange('translator')}
                  >
                    <div className='absolute top-4 left-4 px-1.5 py-0.5 text-xs leading-5 bg-[#C7CEDC] rounded-full'>
                      <UITypography variant='caption-2' className=' secondary-neutral-f text-sb'>
                        기존 예제
                      </UITypography>
                    </div>
                    <UIImage src='/assets/images/agent/ico-radio-ag-visual05.svg' alt='번역기' className='max-w-full max-h-full' />
                  </div>

                  <UIUnitGroup gap={8} direction='column' align='start'>
                    <UITypography variant='body-1' className='secondary-neutral-800 text-sb'>
                      번역기
                    </UITypography>
                    <UITypography variant='body-2' className='secondary-neutral-600'>
                      한-영, 영-한 번역기 예제입니다.
                    </UITypography>
                  </UIUnitGroup>
                </div>

                {/* [251105_퍼블수정] : 영역추가 [S] */}
                <div className='flex flex-col space-y-5'>
                  {/* [251104_퍼블수정] bg-gray-50 클래스를 bg-col-gray-100 이렇게 변경 */}
                  <div
                    className={`relative w-[405px] h-[240px] rounded-[20px] p-6 cursor-pointer transition-all duration-200 flex items-center justify-center ${
                      toolType === 'translator' ? 'bg-[#f3f6fb] border-2 border-[#005df9]' : 'bg-col-gray-100 border-2 border-transparent hover:bg-gray-100'
                    }`}
                    onClick={() => handleToolTypeChange('translator')}
                  >
                    <div className='absolute top-4 left-4 px-1.5 py-0.5 text-xs leading-5 bg-[#C7CEDC] rounded-full'>
                      <UITypography variant='caption-2' className=' secondary-neutral-f text-sb'>
                        기존 예제
                      </UITypography>
                    </div>
                    <UIImage src='/assets/images/agent/ico-radio-ag-visual09.svg' alt='Plan And Execute' className='max-w-full max-h-full' />
                  </div>

                  <UIUnitGroup gap={8} direction='column' align='start'>
                    <UITypography variant='body-1' className='secondary-neutral-800 text-sb'>
                      Plan And Execute
                    </UITypography>
                    <UITypography variant='body-2' className='secondary-neutral-600'>
                      계획 및 실행 구조의 그래프 (재귀 방식)
                    </UITypography>
                  </UIUnitGroup>
                </div>
                {/*  // [251105_퍼블수정] : 영역추가 [E] */}
              </div>
            </UIArticle>
          </UIPopupBody>
          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <UIButton2 className='btn-secondary-blue' style={{ width: '80px' }} onClick={handleNext}>
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
