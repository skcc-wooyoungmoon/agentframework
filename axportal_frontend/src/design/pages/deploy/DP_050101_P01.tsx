import React, { useState } from 'react';

import { UIButton2, UIIcon2, UIRadio2, UITypography } from '@/components/UI/atoms';

import { UIArticle, UIFormField, UIList, UIPopupBody, UIPopupFooter, UIPopupHeader, UIStepper, UIUnitGroup } from '@/components/UI/molecules';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';

import { DesignLayout } from '../../components/DesignLayout';

export const DP_050101_P01: React.FC = () => {
  const [isPopupOpen, setIsPopupOpen] = useState(true);

  // 스테퍼 데이터
  const stepperItems = [
    { step: 1, label: '분류 선택' },
    { step: 2, label: '배포 대상 선택' },
    { step: 3, label: '운영용 정보 입력' },
    { step: 4, label: '최종 정보 확인' },
  ];

  const handleClose = () => {
    setIsPopupOpen(false);
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
            {/* 레이어 팝업 헤더 */}
            <UIPopupHeader title='운영 배포' position='left' />
            {/* 레이어 팝업 바디 */}
            <UIPopupBody>
              {' '}
              <UIArticle>
                <UIStepper currentStep={1} items={stepperItems} direction='vertical' />
              </UIArticle>
            </UIPopupBody>
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <UIButton2 className='btn-tertiary-gray' style={{ width: 80 }}>
                    취소
                  </UIButton2>
                  <UIButton2 className='btn-tertiary-blue' style={{ width: 80 }} disabled={true}>
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
          {/* 레이어 팝업 헤더 */}
          <UIPopupHeader title='분류 선택' description='운영 이행을 원하는 분류를 선택해주세요.' position='right' />

          {/* 레이어 팝업 바디 */}
          <UIPopupBody>
            <UIArticle>
              <UIFormField gap={12} direction='column'>
                <div className='inline-flex items-center'>
                  <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                    분류
                  </UITypography>
                </div>
                <div>
                  <UIUnitGroup gap={12} direction='column' align='start'>
                    <UIRadio2 name='basic1' value='option1' label='세이프티 필터' />
                    <UIRadio2 name='basic1' value='option2' label='백터 DB' />
                    <UIRadio2 name='basic1' value='option3' label='가드레일' />
                    <UIRadio2 name='basic1' value='option4' label='모델' />
                    <UIRadio2 name='basic1' value='option5' label='지식' />
                    <UIRadio2 name='basic1' value='option6' label='에이전트' />
                  </UIUnitGroup>
                </div>
              </UIFormField>
            </UIArticle>

            <UIArticle>
              <div className='box-fill'>
                <UIUnitGroup gap={8} direction='column' align='start'>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '0 6px' }}>
                    <UIIcon2 className='ic-system-16-info-gray' />
                    <UITypography variant='body-2' className='secondary-neutral-600 text-sb'>
                      운영 환경에서 정상 작동을 위해 아래 항목을 반드시 사전에 운영 배포해 주세요.
                    </UITypography>
                  </div>
                  <div style={{ paddingLeft: '22px' }}>
                    <UIUnitGroup gap={8} direction='column' align='start'>
                      <UIList
                        gap={4}
                        direction='column'
                        className='ui-list_dash'
                        data={[
                          {
                            dataItem: (
                              <UITypography variant='body-2' className='secondary-neutral-600'>
                                {'모델 경우 : 참조 세이프티 필터'}
                              </UITypography>
                            ),
                          },
                        ]}
                      />
                      <UIList
                        gap={4}
                        direction='column'
                        className='ui-list_dash'
                        data={[
                          {
                            dataItem: (
                              <UITypography variant='body-2' className='secondary-neutral-600'>
                                {'가드레일 경우 : 참조 모델'}
                              </UITypography>
                            ),
                          },
                        ]}
                      />
                      <UIList
                        gap={4}
                        direction='column'
                        className='ui-list_dash'
                        data={[
                          {
                            dataItem: (
                              <UITypography variant='body-2' className='secondary-neutral-600'>
                                {'지식의 경우 : 참조 벡터 DB, 참조 임베딩 모델'}
                              </UITypography>
                            ),
                          },
                        ]}
                      />
                      <UIList
                        gap={4}
                        direction='column'
                        className='ui-list_dash'
                        data={[
                          {
                            dataItem: (
                              <UITypography variant='body-2' className='secondary-neutral-600'>
                                {'에이전트의 경우 : 참조 세이프티 필터, 참조 가드레일, 참조 지식, 참조 LLM'}
                              </UITypography>
                            ),
                          },
                        ]}
                      />
                    </UIUnitGroup>
                  </div>
                </UIUnitGroup>
              </div>
            </UIArticle>
          </UIPopupBody>
          {/* 레이어 팝업 footer */}
          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                {/* <UIButton2 className='btn-secondary-gray'>이전</UIButton2> */}
                <UIButton2 className='btn-secondary-blue'>다음</UIButton2>
              </UIUnitGroup>
            </UIArticle>
          </UIPopupFooter>
        </section>
      </UILayerPopup>
    </>
  );
};
