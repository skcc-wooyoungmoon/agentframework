import React, { useState } from 'react';

import { UIButton2, UITypography } from '@/components/UI/atoms';
import { UIImage } from '@/components/UI/atoms/UIImage';
import { UIArticle, UIPopupBody, UIPopupFooter, UIPopupHeader, UIUnitGroup } from '@/components/UI/molecules';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';

import { DesignLayout } from '../../components/DesignLayout';

export const DT_030101_P01: React.FC = () => {
  const [isPopupOpen, setIsPopupOpen] = useState(true);
  const [toolType, setToolType] = useState('ingestion');

  const handleClose = () => {
    setIsPopupOpen(false);
  };

  const handleToolTypeChange = (_value: string) => {
    setToolType(_value);
  };

  const radioOptions = [
    // [251104_퍼블수정] : 2개 컬럼 삭제
    // {
    //   value: 'ingestion',
    //   label: 'Ingestion Tool',
    //   image: '/assets/images/data/ico-radio-visual03.svg',
    //   alt: 'Ingestion Tool',
    // },
    // {
    //   value: 'custom',
    //   label: 'Custom Script',
    //   image: '/assets/images/data/ico-radio-visual04.svg',
    //   alt: 'Custom Script',
    // },
    {
      value: 'vector',
      label: '백터DB',
      image: '/assets/images/data/ico-radio-visual05.svg',
      alt: '백터DB',
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
              데이터 도구
            </UITypography>
            <UITypography variant='body-1' className='secondary-neutral-600'>
              데이터 도구 만들기 진행 중...
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
            <UIPopupHeader title='데이터 도구 만들기' position='left' />
            {/* 레이어 팝업 바디 */}
            <UIPopupBody></UIPopupBody>
            {/* 레이어 팝업 footer */}
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <UIButton2 className='btn-tertiary-gray' style={{ width: '80px' }}>
                    취소
                  </UIButton2>
                  <UIButton2 className='btn-tertiary-blue' style={{ width: '80px' }} disabled>
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
          <UIPopupBody>
            <div className='grid grid-cols-2 gap-6 max-w-[1240px]'>
              {radioOptions.map(option => (
                <div key={option.value} className='flex flex-col space-y-5'>
                  {/* [251104_퍼블수정] bg-gray-50 클래스를 bg-col-gray-100 이렇게 변경 */}
                  <div
                    className={`w-[600px] h-[286px] rounded-[20px] p-6 cursor-pointer transition-all duration-200 flex items-center justify-center ${
                      toolType === option.value ? 'bg-[#f3f6fb] border-2 border-[#005df9]' : 'bg-col-gray-100 border-2 border-transparent hover:bg-gray-100'
                    }`}
                    onClick={() => handleToolTypeChange(option.value)}
                  >
                    <UIImage src={option.image} alt={option.alt} className='max-w-full max-h-full' />
                  </div>
                  <UITypography variant='body-1' className='secondary-neutral-800 text-sb'>
                    {option.label}
                  </UITypography>
                </div>
              ))}
            </div>
          </UIPopupBody>
          <UIPopupFooter>
            <UIArticle className='btn-group direction-row align-center'>
              <UIButton2 className='btn-secondary-blue'>다음</UIButton2>
            </UIArticle>
          </UIPopupFooter>
        </section>
      </UILayerPopup>
    </>
  );
};
