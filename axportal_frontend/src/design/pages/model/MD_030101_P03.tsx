import React, { useState } from 'react';

import { UIButton2, UITypography, UIIcon2, UISlider } from '@/components/UI/atoms';
import { UIStepper, UIUnitGroup, UIPopupFooter, UIPopupHeader, UIPopupBody, type UIStepperItem, UIDropdown } from '@/components/UI/molecules';
import { UIArticle } from '@/components/UI/molecules';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { UICircleChart } from '@/components/UI/molecules/chart';
import { DesignLayout } from '../../components/DesignLayout';

export const MD_030101_P03: React.FC = () => {
  const [isPopupOpen, setIsPopupOpen] = useState(true); // 팝업이므로 기본적으로 열려있음

  // 슬라이더 상태 관리
  const [cpuValue, setCpuValue] = useState(0);
  const [memoryValue, setMemoryValue] = useState(0);
  const [gpuValue, setGpuValue] = useState(0);

  const handleClose = () => {
    setIsPopupOpen(false);
  };

  const handleCancel = () => {
    handleClose();
  };

  // 스테퍼 데이터
  const stepperItems: UIStepperItem[] = [
    {
      id: 'step1',
      label: '모델 선택',
      step: 1,
    },
    {
      id: 'step2',
      label: '기본 정보 입력',
      step: 2,
    },
    {
      id: 'step3',
      label: '자원 할당',
      step: 3,
    },
    {
      id: 'step4',
      label: '학습 데이터세트 선택',
      step: 4,
    },
    {
      id: 'step5',
      label: '파라미터 설정',
      step: 5,
    },
    {
      id: 'step6',
      label: '입력정보 확인',
      step: 6,
    },
  ];

  // 슬라이더 핸들러
  const handleCpuChange = (value: number) => {
    setCpuValue(value);
  };

  const handleMemoryChange = (value: number) => {
    setMemoryValue(value);
  };

  const handleGpuChange = (value: number) => {
    setGpuValue(value);
  };

  // 데이터 세트 유형 상태
  const [dataset, setdataset] = useState('1');
  const [isDatasetDropdownOpen, setIsDatasetDropdownOpen] = useState(false);

  return (
    <>
      {/* DesignLayout 기본 구조 */}
      <DesignLayout
        initialMenu={{ id: 'model', label: '모델' }}
        initialSubMenu={{
          id: 'model-finetuning',
          label: '파인튜닝',
          icon: 'ico-lnb-menu-20-model',
        }}
      >
        <div className='flex items-center justify-center h-full'>
          <div className='text-center'>
            <UITypography variant='title-1' className='secondary-neutral-800 text-sb'>
              모델 파인튜닝
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
            <UIPopupHeader title='파인튜닝 등록' description='' position='left' />
            <UIPopupBody>
              <UIArticle>
                <UIStepper currentStep={3} items={stepperItems} direction='vertical' />
              </UIArticle>
            </UIPopupBody>
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <UIButton2 className='btn-tertiary-gray' style={{ width: '80px' }} onClick={handleCancel}>
                    취소
                  </UIButton2>
                  <UIButton2 className='btn-tertiary-blue' style={{ width: '80px' }} disabled={true}>
                    튜닝시작
                  </UIButton2>
                </UIUnitGroup>
              </UIArticle>
            </UIPopupFooter>
          </UIPopupAside>
        }
      >
        {/* 우측 Contents 영역 콘텐츠 - 기존 컴포넌트 사용 */}
        <section className='section-popup-content'>
          <UIPopupHeader title='자원 할당' description='' position='right' />
          <UIPopupBody>
            <UIArticle>
              <UIUnitGroup gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-900' required={true}>
                  리소스 그룹 선택
                </UITypography>
                <UIDropdown
                  value={dataset}
                  options={[
                    { value: '1', label: '리소스 그룹 선택' },
                    { value: '2', label: 'Default2' },
                  ]}
                  isOpen={isDatasetDropdownOpen}
                  onClick={() => setIsDatasetDropdownOpen(!isDatasetDropdownOpen)}
                  onSelect={(value: string) => {
                    setdataset(value);
                    setIsDatasetDropdownOpen(false);
                  }}
                  placeholder=' 리소스 그룹 선택'
                />
              </UIUnitGroup>
            </UIArticle>
            <UIArticle>
              {/* 새로운 디자인: 리소스 정보 + 리소스 차트 */}
              <div className='flex items-center gap-[80px]'>
                {/* 왼쪽: 리소스 차트 */}
                <div className='flex-1 flex ml-[60px] justify-center'>
                  <div className='flex chart-graph h-[300px] gap-x-10 justify-center'>
                    <div className='w-[240px] flex items-center justify-center'>
                      <UICircleChart.Half type='CPU' value={50} total={50.25} showLabel={false} /> {/* [참고] showLabel : 그래프 하단의 라벨(사용중인 자원) 숨김 처리 */}
                    </div>
                    <div className='w-[240px] flex items-center justify-center'>
                      <UICircleChart.Half type='Memory' value={20} total={100} showLabel={false} />
                    </div>
                    <div className='w-[240px] flex items-center justify-center'>
                      <UICircleChart.Half type='GPU' value={128.72} total={20} showLabel={false} />
                    </div>
                  </div>
                  {/* Asis 소스
                  <div className='flex justify-between items-center'>
                    <div className='graphs gap-x-10'>
                      <div className='item'>
                        <div className='item-cont'>그래프영역</div>
                      </div>
                      <div className='item'>
                        <div className='item-cont'>그래프영역</div>
                      </div>
                      <div className='item'>
                        <div className='item-cont'>그래프영역</div>
                      </div>
                    </div>
                  </div> */}
                </div>

                {/* 오른쪽: 리소스 정보 */}
                <div className='flex-shrink-0'>
                  <div className='resource-info-container w-36 h-[218px] opacity-100 gap-4 p-4 rounded-xl border border-gray-300 flex flex-col justify-between'>
                    {/* 할당 가능한 자원 섹션 */}
                    <div className='resource-section'>
                      <UITypography variant='title-4' className='secondary-neutral-900'>
                        할당 가능한 자원
                      </UITypography>

                      <div className='flex flex-col gap-2 pt-3'>
                        <div className='flex items-center gap-2'>
                          <UIIcon2 className='ic-system-8-dot-1' />
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            CPU
                          </UITypography>
                        </div>

                        <div className='flex items-center gap-2'>
                          <UIIcon2 className='ic-system-8-dot-2' />
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            Memory
                          </UITypography>
                        </div>

                        <div className='flex items-center gap-2'>
                          <UIIcon2 className='ic-system-8-dot-3' />
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            GPU
                          </UITypography>
                        </div>
                      </div>
                    </div>

                    {/* 할당된 자원 섹션 */}
                    <div className='resource-section'>
                      <UITypography variant='title-4' className='secondary-neutral-900'>
                        할당된 자원
                      </UITypography>

                      <div className='flex items-center gap-2 pt-3'>
                        <UIIcon2 className='ic-system-8-dot-4' />
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          사용중인 자원
                        </UITypography>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </UIArticle>
            <UIArticle>
              {/* [251110_퍼블수정] 슬라이더 속성값 재정의 */}
              <div className='w-full flex flex-col space-y-[8px]'>
                <UISlider label='CPU' value={cpuValue} min={0} max={0} required={true} showTextField={true} onChange={handleCpuChange} unit='' color='#2670FF' />
                <UISlider label='Memory' value={memoryValue} min={0} max={0} required={true} showTextField={true} onChange={handleMemoryChange} unit='' color='#37D8D0' />
                <UISlider label='GPU' value={gpuValue} min={0} max={0} required={true} showTextField={true} onChange={handleGpuChange} unit='' color='#8166D2' />
              </div>
            </UIArticle>
          </UIPopupBody>

          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <UIButton2 className='btn-secondary-gray' style={{ width: '80px' }} onClick={handleCancel}>
                  이전
                </UIButton2>
                <UIButton2 className='btn-secondary-blue' style={{ width: '80px' }}>
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
