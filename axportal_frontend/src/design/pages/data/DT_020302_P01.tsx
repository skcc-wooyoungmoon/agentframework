import { useState } from 'react';
import { UICode } from '@/components/UI/atoms/UICode';

import { UIButton2, UITypography } from '@/components/UI/atoms';
import { UIInput, UITextArea2 } from '@/components/UI/molecules/input';

import { UIArticle, UIFormField, UIPopupBody, UIPopupFooter, UIPopupHeader } from '@/components/UI/molecules';

import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { UIDropdown } from '@/components/UI/molecules/dropdown/UIDropdown';

import { UIUnitGroup } from '@/components/UI/molecules';
import { DesignLayout } from '../../components/DesignLayout';

export const DT_020302_P01: React.FC = () => {
  const [isLoaderDropdownOpen, setIsLoaderDropdownOpen] = useState(false);

  const [isPopupOpen] = useState(true); // 팝업이므로 항상 열려있음

  const handleClose = () => {
    // 팝업 닫기 동작 제거 (디자인 페이지이므로 항상 열려있음)
    // 
  };

  const loaderOptions = [
    { value: 'RecursiveCharacter', label: 'RecursiveCharacter' },
    { value: 'RecursiveCharacter2', label: 'RecursiveCharacter2' },
    { value: 'RecursiveCharacter3', label: 'RecursiveCharacter3' },
  ];
  const loaderOptions2 = [
    { value: '상품약관 분할방법', label: '상품약관 분할방법' },
    { value: '상품약관 분할방법2', label: '상품약관 분할방법2' },
    { value: '상품약관 분할방법3', label: '상품약관 분할방법3' },
  ];

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
              지식 설정 진행 중...
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
            {/* [251112_퍼블수정] 타이틀 수정 */}
            <UIPopupHeader title='지식 수정' description='' position='left' />
            <UIPopupBody></UIPopupBody>
            {/* 레이어 팝업 바디 : [참고] 이 페이지에는 왼쪽 body 영역 없음. */}
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <UIButton2 className='btn-tertiary-gray' style={{ width: '80px' }}>
                    취소
                  </UIButton2>
                  <UIButton2 className='btn-tertiary-blue' style={{ width: '80px' }} disabled={false}>
                    저장
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
          {/* <UIPopupHeader title='' description='' position='right' /> */}

          {/* 레이어 팝업 바디 */}
          <UIPopupBody>
            {/* 이름 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                  이름
                </UITypography>
                <div>
                  <UIInput.Text value={'신용대출 상품 설명서'} placeholder='이름 입력' />
                </div>
              </UIFormField>
            </UIArticle>

            {/* 설명 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb'>
                  설명
                </UITypography>
                <div>
                  <UITextArea2 value={'신용대출 상품 설명서에 대한 설명입니다.'} placeholder='설명 입력' maxLength={100} />
                </div>
              </UIFormField>
            </UIArticle>

            {/* 임베딩모델A 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                  임베딩 모델
                </UITypography>
                <UIDropdown
                  required={true}
                  value={'임베딩모델A'}
                  readonly={true}
                  options={loaderOptions}
                  isOpen={isLoaderDropdownOpen}
                  onClick={() => setIsLoaderDropdownOpen(!isLoaderDropdownOpen)}
                  onSelect={() => {
                    setIsLoaderDropdownOpen(false);
                  }}
                />
              </UIFormField>
            </UIArticle>

            {/* 벡터 DB 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                  벡터 DB
                </UITypography>
                <UIDropdown
                  required={true}
                  readonly={true}
                  value={'비정형 SE 벡터 DB'}
                  options={loaderOptions2}
                  isOpen={isLoaderDropdownOpen}
                  onClick={() => setIsLoaderDropdownOpen(!isLoaderDropdownOpen)}
                  onSelect={() => {
                    setIsLoaderDropdownOpen(false);
                  }}
                />
              </UIFormField>
            </UIArticle>

            {/* 인덱스명 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={false}>
                  인덱스명
                </UITypography>
                <UIInput.Text value={'인덱스명 이름입니다'} placeholder='인덱스명 입력' />
              </UIFormField>
            </UIArticle>

            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                  Script
                </UITypography>
                {/* 소스코드 영역 */}
                <UICode value={'여기는 에디터 화면입니다. 테스트 테스트'} language='python' theme='dark' width='100%' minHeight='300px' maxHeight='500px' readOnly={false} />
              </UIFormField>
            </UIArticle>
          </UIPopupBody>
          {/* 레이어 팝업 footer */}
          {/* <UIPopupFooter></UIPopupFooter> */}
        </section>
      </UILayerPopup>
    </>
  );
};
