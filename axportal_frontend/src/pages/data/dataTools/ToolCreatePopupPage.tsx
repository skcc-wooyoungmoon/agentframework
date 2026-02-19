import { useState } from 'react';

import { UIButton2, UITypography } from '@/components/UI/atoms';
import { UIImage } from '@/components/UI/atoms/UIImage';
import { UIArticle, UIPopupHeader, UIPopupBody, UIPopupFooter, UIUnitGroup } from '@/components/UI/molecules';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { useCommonPopup } from '@/hooks/common/popup/useCommonPopup';

interface ToolCreatePopupPageProps {
  isOpen: boolean;
  onClose: () => void;
  onNext: (accessType: string) => void;
}

export const ToolCreatePopupPage = ({ isOpen, onClose, onNext }: ToolCreatePopupPageProps) => {
  const [accessType, setAccessType] = useState<string>('vectorDB');
  // 공통 팝업 훅
  const { showCancelConfirm } = useCommonPopup();

  const handleClose = () => {
    setAccessType('vectorDB');
    onClose();
  };

  const handleCancel = () => {
    showCancelConfirm({
      onConfirm: () => {
        handleClose();
      },
    });
  };

  const handleNext = () => {
    onNext(accessType);
  };

  const handleAccessTypeChange = (value: string) => {
    setAccessType(value);
  };

  const radioOptions = [
    // {
    //   value: 'ingestionTool',
    //   label: 'Ingestion Tool',
    //   description: '',
    //   image: '/assets/images/data/ico-radio-visual03.svg',
    //   alt: 'Ingestion Tool',
    // },
    // {
    //   value: 'script',
    //   label: 'Custom Script',
    //   description: '',
    //   image: '/assets/images/data/ico-radio-visual04.svg',
    //   alt: 'Custom Script',
    // },
    {
      value: 'vectorDB',
      label: '백터DB',
      description: '',
      image: '/assets/images/data/ico-radio-visual05.svg',
      alt: '백터DB',
    },
  ];

  return (
    <UILayerPopup
      isOpen={isOpen}
      onClose={handleClose}
      size='fullscreen'
      showOverlay={true}
      leftContent={
        <UIPopupAside>
          {/* 레이어 팝업 헤더 */}
          <UIPopupHeader title='데이터 도구 만들기' position='left' />
          {/* 레이어 팝업 바디 */}
          <UIPopupBody></UIPopupBody>
          <UIPopupFooter>
            <UIArticle>
              {/* 버튼 그룹  */}
              <UIUnitGroup gap={8} direction='row' align='start'>
                <UIButton2 className='btn-tertiary-gray' style={{ width: '80px' }} onClick={handleCancel}>
                  취소
                </UIButton2>
                <UIButton2 className='btn-tertiary-blue' style={{ width: '80px' }} onClick={handleNext} disabled={true}>
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
                <div
                  className={`w-[600px] h-[286px] rounded-[20px] p-6 cursor-pointer transition-all duration-200 flex items-center justify-center ${
                    accessType === option.value ? 'bg-[#f3f6fb] border-2 border-[#005df9]' : 'bg-col-gray-100 border-2 border-transparent hover:bg-gray-100'
                  }`}
                  onClick={() => handleAccessTypeChange(option.value)}
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
            <UIButton2 className='btn-secondary-blue' onClick={handleNext} disabled={!accessType}>
              다음
            </UIButton2>
          </UIArticle>
        </UIPopupFooter>
      </section>
    </UILayerPopup>
  );
};
