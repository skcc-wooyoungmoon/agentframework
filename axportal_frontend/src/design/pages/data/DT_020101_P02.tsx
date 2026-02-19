import { useState } from 'react';

import { UIButton2, UITypography } from '@/components/UI/atoms';
import { UIStepper, UIUnitGroup, UIPopupHeader, UIPopupBody, UIPopupFooter, UIArticle, UIFormField, UIInput } from '@/components/UI/molecules';
import { UIDropdown } from '@/components/UI/molecules/dropdown/UIDropdown';
import { UITextArea2 } from '@/components/UI/molecules/input/UITextArea2';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { UIRadio2 } from '@/components/UI/atoms/UIRadio2';

import { DesignLayout } from '../../components/DesignLayout';

export const DT_020101_P02: React.FC = () => {
  const [isPopupOpen, setIsPopupOpen] = useState(true); // 팝업이므로 기본적으로 열려있음
  const [name, setname] = useState('');

  const [datasetDescription, setDatasetDescription] = useState('');

  const [tags, setTags] = useState<string[]>([]);

  // 폼 상태
  const [dataset, setdataset] = useState('1');
  const [isDatasetDropdownOpen, setIsDatasetDropdownOpen] = useState(false);

  // 스테퍼 데이터
  const stepperItems = [
    { step: 1, label: '데이터 정보입력' },
    { step: 2, label: '데이터 가져오기' },
    { step: 3, label: '선택 데이터 확인' },
    { step: 4, label: '프로세서 선택' },
  ];
  const [currentStep] = useState(1); // 현재 1단계 진행 중

  const handleClose = () => {
    setIsPopupOpen(false);
  };

  const handleCancel = () => {
    handleClose();
  };

  const handleSave = () => {
    handleClose();
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
            <UIPopupHeader title='학습 데이터세트 생성' description='' position='left' />
            <UIPopupBody>
              <UIArticle>
                <UIStepper items={stepperItems} currentStep={currentStep} direction='vertical' />
              </UIArticle>
            </UIPopupBody>
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <UIButton2 className='btn-tertiary-gray' style={{ width: '80px' }} onClick={handleCancel}>
                    취소
                  </UIButton2>
                  <UIButton2 className='btn-tertiary-blue' style={{ width: '80px' }} disabled={true} onClick={handleSave}>
                    만들기
                  </UIButton2>
                </UIUnitGroup>
              </UIArticle>
            </UIPopupFooter>
          </UIPopupAside>
        }
      >
        {/* 우측 Contents 영역 콘텐츠 */}
        <section className='section-popup-content'>
          <UIPopupHeader title='데이터 정보입력' description='' position='right' />
          <UIPopupBody>
            {/* 학습 데이터세트 생성 방식 유형 라디오 버튼 영역 */}
            <UIArticle>
              <UIFormField gap={12} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                  학습 데이터세트 생성 방식
                </UITypography>
                <UIUnitGroup gap={12} direction='column' align='start'>
                  <UIRadio2 name='basic1' value='option1' label='기본 학습 데이터세트' />
                  <UIRadio2 name='basic1' value='option2' label='Custom 학습 데이터세트' />
                </UIUnitGroup>
              </UIFormField>
            </UIArticle>
            {/* 데이터 세트 유형 섹션 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                  학습 데이터세트 유형
                </UITypography>
                <UIDropdown
                  value={dataset}
                  options={[
                    { value: '1', label: '지도학습' },
                    { value: '2', label: '비지도학습' },
                    { value: '3', label: 'DPO' },
                    { value: '4', label: 'Custom' },
                  ]}
                  isOpen={isDatasetDropdownOpen}
                  onClick={() => setIsDatasetDropdownOpen(!isDatasetDropdownOpen)}
                  onSelect={(value: string) => {
                    setdataset(value);
                    setIsDatasetDropdownOpen(false);
                  }}
                  placeholder='데이터 세트 유형 선택'
                />
              </UIFormField>
            </UIArticle>

            {/* 이름 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                  이름
                </UITypography>
                <UIInput.Text value={name} placeholder='이름 입력' onChange={e => setname(e.target.value)} />
              </UIFormField>
            </UIArticle>

            {/* 설명 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={false}>
                  설명
                </UITypography>
                <UITextArea2 value={datasetDescription} onChange={e => setDatasetDescription(e.target.value)} placeholder='설명 입력' maxLength={100} />
              </UIFormField>
            </UIArticle>

            {/* 태그 섹션 */}
            <UIArticle>
              <UIInput.Tags tags={tags} onChange={setTags} placeholder='태그 입력' label='태그' />
            </UIArticle>
          </UIPopupBody>
          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <UIButton2 className='btn-secondary-gray' style={{ width: '80px' }} onClick={handleCancel}>
                  이전
                </UIButton2>
                <UIButton2 className='btn-secondary-blue' style={{ width: '80px' }} onClick={handleSave}>
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
