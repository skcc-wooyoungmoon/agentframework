import { UIStepper, type UIStepperItem } from '@/components/UI/molecules';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { UIPopupHeader, UIPopupBody, UIPopupFooter, UIArticle } from '@/components/UI/molecules';

import { UITypography, UIButton2 } from '@/components/UI/atoms';
import { UIUnitGroup } from '@/components/UI/molecules';

import { DesignLayout } from '../components/DesignLayout';

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
    label: '데이터세트 선택',
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

export const LayoutGuidePopup = () => {
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
            <UITypography variant='title-1' className='secondary-neutral-800 text-sb'>데이터 도구</UITypography>
            <UITypography variant='body-1' className='secondary-neutral-600'>Ingestion Tool 만들기 진행 중...</UITypography>
          </div>
        </div>
      </DesignLayout>

      {/* DesignLayout 위에 높은 z-index로 뜨는 UILayerPopup */}
      <UILayerPopup
        isOpen={true}
        size='fullscreen'
        showOverlay={true}
        leftContent={
          /* 좌측 Step 영역 콘텐츠 */
          <UIPopupAside>
              {/* 레이어 팝업 헤더 */}
              <UIPopupHeader title='파인튜닝 등록' description='이 페이지는 레이어 팝업 샘플화면 입니다.' position='left' />
              {/* 레이어 팝업 바디 */}
              <UIPopupBody>
                <UIArticle>
                  <UIStepper currentStep={4} items={stepperItems} direction='vertical' />
                </UIArticle>
              </UIPopupBody>
              {/* 레이어 팝업 footer */}
              <UIPopupFooter>
                <UIArticle>
                  <UIUnitGroup gap={8} direction='row' align='start'>
                    <UIButton2 className='btn-tertiary-gray'>취소</UIButton2>
                    <UIButton2 className='btn-tertiary-blue'>확인</UIButton2>
                  </UIUnitGroup>
                </UIArticle>
              </UIPopupFooter>
            </UIPopupAside>
        }
      >
        {/* 우측 Contents 영역 콘텐츠 - 기존 컴포넌트 사용 */}
        <div className='p-10 flex flex-col space-y-8'>
          <section className='section-popup-content'>
            {/* 레이어 팝업 헤더 */}
            <UIPopupHeader title='레이어팝업 샘플' description='이 페이지는 레이어 팝업 샘플화면 입니다.' position='right' />

            {/* 레이어 팝업 바디 */}
            <UIPopupBody>
              <UIArticle>
                <div className='article-header'>
                  <UITypography variant='title-4' className='secondary-neutral-900'>
                    아티클 헤더 (서브 타이틀)
                  </UITypography>
                </div>
                <div className='article-body'>
                  <div>아티클 컨텐츠 영역</div>
                </div>
              </UIArticle>
            </UIPopupBody>

            {/* 레이어 팝업 footer */}
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='center'>
                  <UIButton2 className='btn-secondary-gray'>취소</UIButton2>
                  <UIButton2 className='btn-secondary-blue'>확인</UIButton2>
                </UIUnitGroup>
              </UIArticle>
            </UIPopupFooter>
          </section>
        </div>
      </UILayerPopup>
    </>
  );
};
