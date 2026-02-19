import React, { useState } from 'react';
import { UIButton2, UITypography } from '@/components/UI/atoms';
import { UIArticle, UIPopupBody, UIPopupFooter, UIPopupHeader, UIStepper, UIFormField, UIGroup } from '@/components/UI/molecules';

import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { UIDropdown } from '@/components/UI/molecules/dropdown/UIDropdown';
import { DesignLayout } from '../../components/DesignLayout';
import { UIUnitGroup } from '@/components/UI/molecules';
import { UIInput } from '@/components/UI/molecules/input';

export const DT_020101_P06: React.FC = () => {
  const [isPopupOpen] = useState(true); // 팝업이므로 항상 열려있음

  // 스테퍼 데이터
  const stepperItems = [
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

  const [value, setValue] = useState('비정형 청킹 알고리즘');

  // 청킹 사이즈 state
  const [chunkSize, setChunkSize] = useState('');

  // 청킹 오버랩 state
  const [chunkOverlap, setChunkOverlap] = useState('');

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
            {/* 레이어 팝업 바디 */}
            <UIPopupBody>
              <UIArticle>
                <UIStepper currentStep={4} items={stepperItems} direction='vertical' />
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
          <UIPopupHeader title='청킹 설정' description='파싱 완료된 데이터의 청킹 방법을 설정해주세요' position='right' />

          {/* 레이어 팝업 바디 */}
          <UIPopupBody>
            {/* 청킹 방법 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                  청킹 방법
                </UITypography>
                <UIDropdown
                  value={String(value)}
                  options={[
                    { value: '비정형 청킹 알고리즘', label: '비정형 청킹 알고리즘' },
                    { value: '비정형 청킹 알고리즘2', label: '비정형 청킹 알고리즘2' },
                    { value: '비정형 청킹 알고리즘3', label: '비정형 청킹 알고리즘3' },
                  ]}
                  onSelect={(value: string) => {
                    setValue(value);
                  }}
                  onClick={() => {}}
                />
              </UIFormField>
            </UIArticle>

            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UIGroup direction='column' gap={4}>
                  <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={false}>
                    청킹 사이즈
                  </UITypography>
                  <UITypography variant='body-2' className='secondary-neutral-600'>
                    별도로 입력하지 않을 경우 기본값인 300으로 적용됩니다.
                  </UITypography>
                </UIGroup>
                <div>
                  <UIInput.Text
                    value={chunkSize}
                    onChange={e => {
                      setChunkSize(e.target.value);
                    }}
                    placeholder='청킹 사이즈 입력'
                  />
                </div>
              </UIFormField>
            </UIArticle>
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UIGroup direction='column' gap={4}>
                  <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={false}>
                    청킹 오버랩
                  </UITypography>
                  <UITypography variant='body-2' className='secondary-neutral-600'>
                    별도로 입력하지 않을 경우 기본값인 0으로 적용됩니다. 문장 오버랩은 인접한 청크끼리 공유할 문장 개수입니다. 예를 들어 1로 설정하면, 생성된 청크들의 앞뒤로
                    1문장씩은 중복된다는 의미입니다.
                  </UITypography>
                </UIGroup>
                <div>
                  <UIInput.Text
                    value={chunkOverlap}
                    onChange={e => {
                      setChunkOverlap(e.target.value);
                    }}
                    placeholder='청킹 오버랩 입력'
                  />
                </div>
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
