import React, { useState } from 'react';

import { UIButton2 } from '@/components/UI/atoms';
import { UIPopupHeader, UIPopupFooter, UIArticle, UIPopupBody, UIFormField, UIList } from '@/components/UI/molecules';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { UIStepper } from '@/components/UI/molecules';
import { UIInput, UITextArea2 } from '@/components/UI/molecules/input';

import { DesignLayout } from '../../components/DesignLayout';
import { UIUnitGroup } from '@/components/UI/molecules';
import { UITypography } from '@/components/UI/atoms';

export const DP_020101_P02: React.FC = () => {
  const [isPopupOpen] = useState(true); // 팝업이므로 항상 열려있음

  // 스테퍼 데이터
  const stepperItems = [
    { step: 1, label: '에이전트 선택' },
    { step: 2, label: '배포 정보 입력' },
    { step: 3, label: '자원 할당' },
  ];

  const handleClose = () => {
    // 팝업 닫기 동작 제거 (디자인 페이지이므로 항상 열려있음)
    //
  };

  // textarea 타입
  const [textareaValue, setTextareaValue] = useState('');

  return (
    <>
      {/* DesignLayout 기본 구조 */}
      <DesignLayout
        initialMenu={{ id: 'admin', label: '관리' }}
        initialSubMenu={{
          id: 'admin-roles',
          label: '역할 관리',
          icon: 'ico-lnb-menu-20-admin-role',
        }}
      >
        <div className='flex items-center justify-center h-full'>
          <div className='text-center'>
            <UITypography variant='title-1' className='secondary-neutral-800 text-sb'>
              기본 정보
            </UITypography>
            <UITypography variant='body-1' className='secondary-neutral-600'>
              에이전트 배포하기 진행 중...
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
            <UIPopupHeader title='에이전트 배포하기' description='' position='left' />
            <UIPopupBody>
              <UIArticle>
                <UIStepper currentStep={2} items={stepperItems} direction='vertical' />
              </UIArticle>
            </UIPopupBody>
            {/* 레이어 팝업 바디 : [참고] 이 페이지에는 왼쪽 body 영역 없음. */}
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <UIButton2 className='btn-tertiary-gray' style={{ width: '80px' }}>
                    취소
                  </UIButton2>
                  <UIButton2 className='btn-tertiary-blue' style={{ width: '80px' }} disabled={true}>
                    배포
                  </UIButton2>
                </UIUnitGroup>
              </UIArticle>
            </UIPopupFooter>
          </UIPopupAside>
        }
      >
        {/* 우측 Contents 영역 콘텐츠 */}
        {/* 콘텐츠 영역 */}
        <section className='section-popup-content'>
          {/* 레이어 팝업 헤더 */}
          <UIPopupHeader title='배포 정보 입력' description='' position='right' />

          {/* 레이어 팝업 바디 */}
          <UIPopupBody>
            {/* 역할명 입력 필드 */}

            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' required={true} className='secondary-neutral-800 text-sb'>
                  배포명
                </UITypography>
                <UIInput.Text value={''} placeholder='배포명 입력' onChange={() => {}} readOnly={false} />
              </UIFormField>
            </UIArticle>

            {/* 설명 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb'>
                  설명
                </UITypography>
                <UITextArea2 value={textareaValue} placeholder='설명 입력' maxLength={100} onChange={e => setTextareaValue(e.target.value)} />
              </UIFormField>
            </UIArticle>

            <UIArticle>
              <UIUnitGroup gap={8} direction='column' align='start'>
                <UITypography variant='title-3' className='secondary-neutral-900' required={false}>
                  세이프티 필터
                </UITypography>
                <UITypography variant='body-2' className='secondary-neutral-600'>
                  아래에서 별도로 필터를 선택하지 않은 경우, 기본 필터링 기능만 자동으로 적용됩니다.
                </UITypography>
              </UIUnitGroup>
            </UIArticle>

            {/* 입력 필터 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                  입력 필터
                </UITypography>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <div className='flex-1'>
                    <UIInput.Text value={'입력 필터 선택'} onChange={() => {}} placeholder='' />
                  </div>
                  <div>
                    <UIButton2 className='btn-secondary-outline !min-w-[64px]'>선택</UIButton2>
                  </div>
                </UIUnitGroup>
              </UIFormField>
            </UIArticle>

            {/* 출력 필터 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                  출력 필터
                </UITypography>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <div className='flex-1'>
                    <UIInput.Text value={'출력 필터 선택'} onChange={() => {}} placeholder='태그 입력' />
                  </div>
                  <div>
                    <UIButton2 className='btn-secondary-outline !min-w-[64px]'>선택</UIButton2>
                  </div>
                </UIUnitGroup>
                <UIList
                  gap={4}
                  direction='column'
                  className='ui-list_bullet'
                  data={[
                    {
                      dataItem: (
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          스트리밍 출력에 대해서는 출력 필터 지원이 불가능합니다.
                        </UITypography>
                      ),
                    },
                  ]}
                />
              </UIFormField>
            </UIArticle>
          </UIPopupBody>
          {/* 레이어 팝업 footer */}
          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <UIButton2 className='btn-secondary-gray'>이전</UIButton2>
                <UIButton2 className='btn-secondary-blue'>다음</UIButton2>
              </UIUnitGroup>
            </UIArticle>
          </UIPopupFooter>
        </section>
      </UILayerPopup>
    </>
  );
};
