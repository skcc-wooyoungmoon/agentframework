import React, { useState } from 'react';

import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';

import { DesignLayout } from '../../components/DesignLayout';

import { UIButton2, UITypography } from '@/components/UI/atoms';
import { UIImage } from '@/components/UI/atoms/UIImage';
import { UIPopupHeader, UIPopupBody, UIArticle, UIPopupFooter, UIUnitGroup } from '@/components/UI/molecules';

export const MD_050101_P04: React.FC = () => {
  const [isPopupOpen, setIsPopupOpen] = useState(true);
  const [toolType, setToolType] = useState('opt1');

  const handleClose = () => {
    setIsPopupOpen(false);
  };

  // const handleCancel = () => {
  //   
  //   handleClose();
  // };

  // const handleNext = () => {
  //   
  //   // 다음 단계 로직
  // };

  const handleToolTypeChange = (value: string) => {
    setToolType(value);
  };

  const radioOptions = [
    {
      value: 'opt1',
      label: 'self-hosting 모델',
      description: '',
      image: '/assets/images/model/ico-radio-model-visual01.svg',
      alt: '모델',
      imageSize: 'w-[456px] h-[248px]',
    },
    {
      value: 'opt2',
      label: 'severless 모델',
      description: '',
      image: '/assets/images/model/ico-radio-model-visual02.svg',
      alt: '모델',
      imageSize: 'w-[456px] h-[248px]',
    },
  ];

  return (
    <>
      <DesignLayout
        initialMenu={{ id: 'agent', label: '에이전트' }}
        initialSubMenu={{
          id: 'agent-tools',
          label: '에이전트도구',
          icon: 'ico-lnb-menu-20-agent',
        }}
      >
        <div className='flex items-center justify-center h-full'>
          <div className='text-center'>
            <UITypography variant='title-1' className='secondary-neutral-800 text-sb'>
              에이전트
            </UITypography>
            <UITypography variant='body-1' className='secondary-neutral-600'>
              도구·API 등록...
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
              <UIArticle>{/* 빈 공간 추가시 사용 */}</UIArticle>
            </UIPopupBody>

            {/* 레이어 팝업 footer */}
            {/* [251120_퍼블수정] 버튼 영역 수정 */}
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
            <div></div>
          </UIPopupAside>
        }
      >
        <section className='section-popup-content'>
          {/* 라디오 옵션들 - 2행 레이아웃 */}

          {/* 레이어 팝업 헤더 */}
          {/* 헤더 내용 - 없음 주석처리 */}
          {/* <UIPopupHeader title='도구 선택' description='IDE 환경에서 사용할 도구를 선택해주세요.' position='right' /> */}

          {/* 레이어 팝업 바디 */}
          <UIPopupBody>
            <UIArticle>
              {/* 첫번째 줄: 도구, 행내 API */}
              <div className='grid grid-cols-2 gap-6'>
                {radioOptions.slice(0, 2).map(option => (
                  <div key={option.value} className='flex flex-col space-y-5'>
                    {/* 라디오 카드 */}
                    {/* [251104_퍼블수정] bg-gray-50 클래스를 bg-col-gray-100 이렇게 변경 */}
                    <div
                      className={`w-[620px] h-[286px] rounded-[20px] p-6 cursor-pointer transition-all duration-200 flex items-center justify-center ${
                        toolType === option.value ? 'bg-[#f3f6fb] border-2 border-[#005df9]' : 'bg-col-gray-100 border-2 border-transparent hover:bg-gray-100'
                      }`}
                      onClick={() => handleToolTypeChange(option.value)}
                    >
                      <UIImage src={option.image} alt={option.alt} className={option.imageSize} />
                    </div>

                    {/* 텍스트 영역 - 카드 밑으로 분리 */}
                    <div className='space-y-1'>
                      <div className='flex items-center gap-2'>
                        <UITypography variant='body-1' className='secondary-neutral-800 text-sb'>
                          {option.label}
                        </UITypography>
                      </div>
                      <UITypography variant='body-2' className='secondary-neutral-600'>
                        {option.description}
                      </UITypography>
                    </div>
                  </div>
                ))}
              </div>
            </UIArticle>
          </UIPopupBody>

          {/* 레이어 팝업 footer */}
          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} align='start'>
                <UIButton2 className='btn-secondary-blue'>다음</UIButton2>
              </UIUnitGroup>
            </UIArticle>
          </UIPopupFooter>
        </section>
      </UILayerPopup>
    </>
  );
};
