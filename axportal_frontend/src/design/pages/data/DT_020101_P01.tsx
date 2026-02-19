import React, { useState } from 'react';

import { UIButton2, UITypography } from '@/components/UI/atoms';
import { UIImage } from '@/components/UI/atoms/UIImage';
import { UIArticle, UIPopupFooter, UIPopupHeader, UIPopupBody, UIUnitGroup } from '@/components/UI/molecules';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';

import { DesignLayout } from '../../components/DesignLayout';

export const DT_020101_P01: React.FC = () => {
  const [isPopupOpen, setIsPopupOpen] = useState(true);
  const [datasetType, setDatasetType] = useState('dataset');

  const handleClose = () => {
    setIsPopupOpen(false);
  };

  const handleNext = () => {
    // 다음 단계 로직
  };

  const handleDatasetTypeChange = (_value: string) => {
    setDatasetType(_value);
  };

  /* 25110_퍼블 속성값 수정 */
  const radioOptions = [
    {
      value: 'knowledge',
      label: '지식',
      image: '/assets/images/data/ico-radio-visual02.svg',
      alt: '지식',
    },
    {
      value: 'dataset',
      label: '학습 데이터세트',
      image: '/assets/images/data/ico-radio-visual01.svg',
      alt: '학습 데이터세트',
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
              Ingestion Tool 만들기 진행 중...
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
            <UIPopupHeader title='데이터 만들기' description='' position='left' />
            <UIPopupBody>
              <UIArticle>{/* 바디 영역 */}</UIArticle>
            </UIPopupBody>
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <UIButton2 className='btn-tertiary-gray' style={{ width: '80px' }}>
                    취소
                  </UIButton2>
                  {/* <UIButton2 className='btn-tertiary-blue' style={{ width: '80px' }} disabled={true}></UIButton2> */}
                </UIUnitGroup>
              </UIArticle>
            </UIPopupFooter>
          </UIPopupAside>
        }
      >
        <section className='section-popup-content'>
          <UIPopupBody>
            <UIArticle>
              <div className='flex gap-6'>
                {radioOptions.map(option => (
                  <div key={option.value} className='flex flex-col space-y-5'>
                    {/* [251104_퍼블수정] bg-gray-50 클래스를 bg-col-gray-100 이렇게 변경 */}
                    <div
                      className={`w-[620px] h-[286px] rounded-[20px] p-6 cursor-pointer transition-all duration-200 flex items-center justify-center ${
                        datasetType === option.value ? 'bg-[#f3f6fb] border-2 border-[#005df9]' : 'bg-col-gray-100 border-2 border-transparent hover:bg-gray-100'
                      }`}
                      onClick={() => handleDatasetTypeChange(option.value)}
                    >
                      <UIImage src={option.image} alt={option.alt} className='max-w-full max-h-full' />
                    </div>
                    <UITypography variant='body-1' className='secondary-neutral-800 text-sb'>
                      {option.label}
                    </UITypography>
                  </div>
                ))}
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
