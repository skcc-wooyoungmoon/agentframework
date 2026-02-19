import { UIButton2, UITypography } from '@/components/UI/atoms';
import { UIImage } from '@/components/UI/atoms/UIImage';
import { UIArticle, UIPopupBody, UIPopupFooter, UIPopupHeader, UIUnitGroup } from '@/components/UI/molecules';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';

import type { ModelGardenInStepProps } from './ModelGardnIn';

/**
 * @author SGO1032948
 * @description Step1. 반입 모델 유형 선택
 *
 * MD_050101_P04
 */
export function MdGdnImpStep1TypePickPopup({ currentStep, onClose, onNextStep, info, onSetInfo }: ModelGardenInStepProps) {
  const handleClose = () => {
    onClose();
  };

  const handleNext = () => {
    onNextStep();
  };

  const handleToolTypeChange = (value: string) => {
    onSetInfo({ serving_type: value });
  };

  const radioOptions = [
    {
      value: 'self-hosting',
      label: 'self-hosting 모델',
      description: '',
      image: '/assets/images/model/ico-radio-model-visual01.svg',
      alt: '모델',
      imageSize: 'w-[456px] h-[248px]',
    },
    {
      value: 'serverless',
      label: 'severless 모델',
      description: '',
      image: '/assets/images/model/ico-radio-model-visual02.svg',
      alt: '모델',
      imageSize: 'w-[456px] h-[248px]',
    },
  ];

  return (
    <>
      <UILayerPopup
        isOpen={currentStep === 1}
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
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <UIButton2 className='btn-aside-gray' onClick={handleClose}>
                    취소
                  </UIButton2>
                  <UIButton2 className='btn-aside-blue' disabled>
                    반입요청
                  </UIButton2>
                </UIUnitGroup>
              </UIArticle>
            </UIPopupFooter>
          </UIPopupAside>
        }
      >
        <section className='section-popup-content'>
          {/* 라디오 옵션들 - 2행 레이아웃 */}

          {/* 레이어 팝업 헤더 */}
          {/* 헤더 내용 - 없음 주석처리 */}

          {/* 레이어 팝업 바디 */}
          <UIPopupBody>
            <UIArticle>
              {/* 첫번째 줄: 도구, 행내 API */}
              <div className='grid grid-cols-2 gap-6'>
                {radioOptions.slice(0, 2).map(option => (
                  <div key={option.value} className='flex flex-col space-y-5'>
                    {/* 라디오 카드 */}
                    <div
                      className={`w-[620px] h-[286px] rounded-[20px] p-6 cursor-pointer transition-all duration-200 flex items-center justify-center ${
                        info.serving_type === option.value ? 'bg-[#f3f6fb] border-2 border-[#005df9]' : 'bg-col-gray-100 border-2 border-transparent hover:bg-gray-100'
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
                <UIButton2 className='btn-secondary-blue' onClick={handleNext}>
                  다음
                </UIButton2>
              </UIUnitGroup>
            </UIArticle>
          </UIPopupFooter>
        </section>
      </UILayerPopup>
    </>
  );
}
