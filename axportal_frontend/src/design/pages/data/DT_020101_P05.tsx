import { useState } from 'react';

import { UIButton2, UITypography, UIRadio2 } from '@/components/UI/atoms';
import { UIArticle, UIFormField, UIPopupBody, UIPopupFooter, UIPopupHeader, UIStepper } from '@/components/UI/molecules';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { UIInput, UITextArea2 } from '@/components/UI/molecules/input';

import { UIUnitGroup } from '@/components/UI/molecules';
import { DesignLayout } from '../../components/DesignLayout';

export const DT_020101_P05: React.FC = () => {
  // const { openAlert } = useModal();
  const [isPopupOpen] = useState(true); // 팝업이므로 항상 열려있음
  const [knowledgeType, setKnowledgeType] = useState('option1'); // 지식 유형 선택 상태

  // 스테퍼 데이터 - 지식 유형에 따라 동적으로 변경
  const stepperItems = knowledgeType === 'option2'
    ? [
        { step: 1, label: '지식 기본 설정' },
        { step: 2, label: '지식 등록' },
      ]
    : [
        { step: 1, label: '지식 기본 설정' },
        { step: 2, label: '데이터 선택' },
        { step: 3, label: '선택 데이터 확인' },
        { step: 4, label: '청킹 설정' },
        { step: 5, label: '임베딩 설정' },
        { step: 6, label: '지식 등록' },
      ];

  const handleClose = () => {
    // 팝업 닫기 동작 제거 (디자인 페이지이므로 항상 열려있음)
    //
  };

  // text 타입
  const [, setTextValue] = useState('');

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
              지식 만들기 진행 중...
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
            <UIPopupHeader title='지식 생성' description='' position='left' />
            <UIPopupBody>
              <UIArticle>
                <UIStepper currentStep={1} items={stepperItems} direction='vertical' />
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
                    만들기
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
          <UIPopupHeader title='지식 기본 설정' description='지식 유형 선택 후 선택한 유형에 알맞는 기본 메타 정보를 입력해주세요.' position='right' />

          {/* 레이어 팝업 바디 */}
          <UIPopupBody>
            <UIArticle>
              <UIFormField gap={12} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                  지식 유형 선택
                </UITypography>
                <UIUnitGroup gap={12} direction='column' align='start'>
                  <UIRadio2
                    name='basic1'
                    value='option1'
                    label='기본 지식'
                    checked={knowledgeType === 'option1'}
                    onChange={() => setKnowledgeType('option1')}
                  />
                  <UIRadio2
                    name='basic1'
                    value='option2'
                    label='사용자 정의 지식'
                    checked={knowledgeType === 'option2'}
                    onChange={() => setKnowledgeType('option2')}
                  />
                </UIUnitGroup>
              </UIFormField>
            </UIArticle>

            {/* 역할명 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                  이름
                </UITypography>
                <div>
                  <UIInput.Text
                    value={''}
                    onChange={e => {
                      setTextValue(e.target.value);
                    }}
                    placeholder='이름 입력'
                  />
                </div>
              </UIFormField>
            </UIArticle>

            {/* 설명 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                  설명
                </UITypography>
                <UITextArea2 value={''} onChange={() => {}} placeholder='설명 입력' maxLength={100} />
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
