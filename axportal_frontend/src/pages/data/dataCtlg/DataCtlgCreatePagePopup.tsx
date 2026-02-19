import { useState, useEffect } from 'react';

import { UIArticle, UIPopupBody, UIPopupFooter, UIPopupHeader, UIUnitGroup, type UIStepperItem } from '@/components/UI/molecules';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { UIButton2, UITypography } from '@/components/UI';
import { UIImage } from '@/components/UI/atoms/UIImage';
import { useAtom } from 'jotai';
import { DataAtom } from '@/stores/data/dataStore';
import { DataSetStep1DataDetailPopupPage } from './dataset/DataSetStep1DataDetailPopupPage';
import { KnowledgeCreatePopup } from './knowledge';
import { DataSetStep2DataImportPopupPage } from './dataset/DataSetStep2DataImportPopupPage';
import { DataSetStep3DataConfirmPopupPage } from './dataset/DataSetStep3DataConfirmPopupPage';
import { DataSetStep4ProcessorPopupPage } from './dataset/DataSetStep4ProcessorPopupPage';
import { useCommonPopup } from '@/hooks/common/popup/useCommonPopup';

interface DataCtlgCreatePagePopupProps {
  isOpen: boolean;
  onClose: () => void;
}

export const DataCtlgCreatePagePopup = ({ isOpen, onClose }: DataCtlgCreatePagePopupProps) => {
  const [accessType, setAccessType] = useState<string>('knowledge');
  const [, setDataForm] = useAtom(DataAtom);

  const [currentStep, setCurrentStep] = useState(0);
  const [popupStep, setPopupStep] = useState<'dataset' | 'knowledge' | ''>('');

  // 공통 팝업 훅
  const { showCancelConfirm } = useCommonPopup();

  // 팝업이 열릴 때 popupStep 초기화
  useEffect(() => {
    if (isOpen) {
      setAccessType('knowledge');
    }
  }, [isOpen]);

  // 스테퍼 데이터
  const stepperItems: UIStepperItem[] = [
    {
      id: 'step1',
      label: '데이터 정보입력',
      step: 1,
    },
    {
      id: 'step2',
      label: '데이터 가져오기',
      step: 2,
    },
    {
      id: 'step3',
      label: '선택 데이터 확인',
      step: 3,
    },
    {
      id: 'step4',
      label: '프로세서 선택',
      step: 4,
    },
  ];

  // accessType에 따라 적절한 팝업 열기
  const onNext = (accessType: string) => {
    // DataCtlgCreatePagePopup에서 처음 dataset으로 넘어올 때만 데이터 초기화
    if (accessType === 'dataset') {
      setDataForm({
        dataType: 'basicDataSet',
        dataset: '지도학습',
        name: '',
        description: '',
        tags: [],
        importType: 'none',
        files: [],
        uploadedFiles: [],
        uploadedFileInfos: [],
        selectedStorageData: [],
      });
    }

    setPopupStep(accessType as 'dataset' | 'knowledge');
    setCurrentStep(1);
  };

  const handlePreviousStep = () => {
    if (currentStep === 1) {
      setPopupStep('');
      setCurrentStep(0);
    } else {
      // 다른 Step에서는 이전 스텝으로 이동
      setCurrentStep(prev => prev - 1);
    }
  };

  const handlePopupClose = () => {
    showCancelConfirm({
      onConfirm: () => {
        setCurrentStep(0);
        setDataForm({
          dataType: 'basicDataSet',
          dataset: '지도학습',
          name: '',
          description: '',
          tags: [],
          importType: 'none',
          files: [],
          uploadedFiles: [],
          uploadedFileInfos: [],
          selectedStorageData: [],
        });
        setPopupStep('');
        setAccessType('knowledge');
        onClose();
      },
    });
  };

  // 완료 시 팝업 닫기 (확인 팝업 없이 바로 닫기)
  const handleCompletePopup = () => {
    setCurrentStep(0);
    setDataForm({
      dataType: 'basicDataSet',
      dataset: '지도학습',
      name: '',
      description: '',
      tags: [],
      importType: 'none',
      files: [],
      uploadedFiles: [],
      uploadedFileInfos: [],
      selectedStorageData: [],
    });
    setPopupStep('');
    setAccessType('knowledge');
    onClose();
  };

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
      {/* 학습 데이터 세트 - Step 1 컴포넌트 */}
      <DataSetStep1DataDetailPopupPage
        isOpen={popupStep === 'dataset' && currentStep === 1}
        stepperItems={stepperItems}
        handlePopupClose={handlePopupClose}
        onNextStep={() => setCurrentStep(2)}
        onPreviousStep={handlePreviousStep}
      />

      {/* Step 2 컴포넌트 */}
      <DataSetStep2DataImportPopupPage
        isOpen={popupStep === 'dataset' && currentStep === 2}
        stepperItems={stepperItems}
        handlePopupClose={handlePopupClose}
        onNextStep={() => setCurrentStep(3)}
        onPreviousStep={handlePreviousStep}
      />

      {/* Step 3 컴포넌트 */}
      <DataSetStep3DataConfirmPopupPage
        isOpen={popupStep === 'dataset' && currentStep === 3}
        stepperItems={stepperItems}
        handlePopupClose={handlePopupClose}
        onClose={handleCompletePopup}
        onNextStep={() => setCurrentStep(4)}
        onPreviousStep={handlePreviousStep}
      />

      {/* Step 4 컴포넌트 */}
      <DataSetStep4ProcessorPopupPage
        isOpen={popupStep === 'dataset' && currentStep === 4}
        stepperItems={stepperItems}
        handlePopupClose={handlePopupClose}
        onClose={handleCompletePopup}
        onPreviousStep={handlePreviousStep}
      />

      {/* 지식 생성 팝업 */}
      <KnowledgeCreatePopup isOpen={popupStep === 'knowledge'} onClose={handleCompletePopup} onPreviousStep={handlePreviousStep} onComplete={handleCompletePopup} />

      {/* 데이터 만들기 팝업 */}
      <UILayerPopup
        isOpen={isOpen}
        onClose={onClose}
        size='fullscreen'
        showOverlay={true}
        leftContent={
          <UIPopupAside>
            <UIPopupHeader title='데이터 만들기' description='' position='left' />

            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <UIButton2 className='btn-tertiary-gray' style={{ width: '80px' }} onClick={handlePopupClose}>
                    취소
                  </UIButton2>
                  <UIButton2 className='btn-tertiary-blue' style={{ width: '80px' }} disabled={true}>
                    만들기
                  </UIButton2>
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
                    <div
                      className={`w-[620px] h-[286px] rounded-[20px] p-6 cursor-pointer transition-all duration-200 flex items-center justify-center ${
                        accessType === option.value ? 'bg-[#f3f6fb] border-2 border-[#005df9]' : 'bg-col-gray-100 border-2 border-transparent hover:bg-gray-100'
                      }`}
                      onClick={() => setAccessType(option.value)}
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
                <UIButton2 className='btn-secondary-blue' style={{ width: '80px' }} onClick={() => onNext(accessType)}>
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
