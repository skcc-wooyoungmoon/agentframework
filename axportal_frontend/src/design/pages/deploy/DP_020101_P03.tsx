import React, { useState } from 'react';

import { UIButton2, UITypography, UIIcon2, UISlider } from '@/components/UI/atoms';
import { UIStepper, UIUnitGroup, UIPopupFooter, UIPopupHeader, UIPopupBody, type UIStepperItem } from '@/components/UI/molecules';
import { UIArticle, UIFormField, UIInput, UIList } from '@/components/UI/molecules';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { UICircleChart } from '@/components/UI/molecules/chart';
import { DesignLayout } from '../../components/DesignLayout';

export const DP_020101_P03: React.FC = () => {
  const [isPopupOpen, setIsPopupOpen] = useState(true); // 팝업이므로 기본적으로 열려있음

  // 슬라이더 상태 관리
  const [cpuValue, setCpuValue] = useState(41);
  const [memoryValue, setMemoryValue] = useState(166);

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
      label: '에이전트 선택',
      step: 1,
    },
    {
      id: 'step2',
      label: '배포 정보 입력',
      step: 2,
    },
    {
      id: 'step3',
      label: '자원 할당',
      step: 3,
    },
  ];

  // 슬라이더 핸들러
  const handleCpuChange = (value: number) => {
    setCpuValue(value);
  };

  const handleMemoryChange = (value: number) => {
    setMemoryValue(value);
  };

  const [textValue, setTextValue] = useState('0');

  return (
    <>
      {/* DesignLayout 기본 구조 */}
      <DesignLayout
        initialMenu={{ id: 'model', label: '모델' }}
        initialSubMenu={{
          id: 'model-finetuning',
          label: '에이전트 배포하기',
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
            <UIPopupHeader title='에이전트 배포하기' description='' position='left' />
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
                  <UIButton2 className='btn-tertiary-blue' style={{ width: '80px' }} disabled={false}>
                    배포
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
            {/* [251106_퍼블수정] 마크업 수정 */}
            <UIArticle>
              {/* 새로운 디자인: 리소스 정보 + 리소스 차트 */}
              <div className='flex items-center'>
                {/* 왼쪽: 리소스 차트 */}
                <div className='flex-1 flex ml-[60px] justify-center'>
                  <div className='flex chart-graph h-[300px] gap-x-20 justify-center'>
                    <div className='w-[240px] flex items-center justify-center'>
                      <UICircleChart.Half type='Memory' value={20} total={100} showLabel={false} /> {/* [참고] showLabel : 그래프 하단의 라벨(사용중인 자원) 숨김 처리 */}
                      {/* #### 기획 확인후 수정필요 ####  [251219_퍼블수정필요] : type='CPU' >  type='CPU(Core)' 로 수정해야하는데 공통으로 모든 반원 차트가 바뀌는건지? type 명이 개별적으로 바뀌는건지? (체크 필요) */}
                    </div>
                    <div className='w-[240px] flex items-center justify-center'>
                      <UICircleChart.Half type='GPU' value={70.78} total={100} showLabel={false} />
                      {/* #### 기획 확인후 수정필요 ####  [251219_퍼블수정필요] : type='CPU' >  type='CPU(Core)' 로 수정해야하는데 공통으로 모든 반원 차트가 바뀌는건지? type 명이 개별적으로 바뀌는건지? (체크 필요) */}
                    </div>
                  </div>
                </div>

                {/* 오른쪽: 리소스 정보 */}
                <div className='flex-shrink-0'>
                  <div className='resource-info-container w-36 opacity-100 gap-4 p-4 rounded-xl border border-gray-300 flex flex-col justify-between'>
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
                {/* [참고] 차트 라이브러리 사용필요 */}
                <UISlider label='CPU' value={cpuValue} min={0} max={92} required={true} showTextField={true} onChange={handleCpuChange} unit='' color='#2670FF' />
                <UISlider label='Memory' value={memoryValue} min={0} max={380} required={true} showTextField={true} onChange={handleMemoryChange} unit='' color='#37D8D0' />{' '}
              </div>
            </UIArticle>
            {/* 입력 필터 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                  에이전트 복제 인스턴스 수
                </UITypography>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <div className='flex-1'>
                    <UIInput.Text
                      value={textValue}
                      onChange={e => {
                        setTextValue(e.target.value);
                      }}
                      placeholder='입력 필터 입력'
                    />
                  </div>
                  <div>
                    <UIButton2 className='btn-secondary-minus'>{''}</UIButton2>
                  </div>
                  <div>
                    <UIButton2 className='btn-secondary-plus'>{''}</UIButton2>
                  </div>
                </UIUnitGroup>
              </UIFormField>
              <UIList
                gap={4}
                direction='column'
                className='ui-list_bullet'
                data={[
                  {
                    dataItem: (
                      <UITypography variant='body-2' className='secondary-neutral-600'>
                        선택한 리소스의 크기를 고려하여 설정할 수 있는 최대 복제본 수는 104개입니다.
                      </UITypography>
                    ),
                  },
                ]}
              />
            </UIArticle>
          </UIPopupBody>

          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <UIButton2 className='btn-secondary-gray' style={{ width: '80px' }} onClick={handleCancel}>
                  이전
                </UIButton2>
                {/* <UIButton2 className='btn-secondary-blue' style={{ width: '80px' }}>
                  다음
                </UIButton2> */}
              </UIUnitGroup>
            </UIArticle>
          </UIPopupFooter>
        </section>
      </UILayerPopup>
    </>
  );
};
