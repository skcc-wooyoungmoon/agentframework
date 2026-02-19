import { useState } from 'react';

import { UIButton2, UITypography } from '@/components/UI/atoms';
import { UIArticle, UIFormField, UIPopupBody, UIPopupFooter, UIPopupHeader, UIUnitGroup } from '@/components/UI/molecules';
import { UIDropdown } from '@/components/UI/molecules/dropdown/UIDropdown';
import { UIInput, UITextArea2 } from '@/components/UI/molecules/input';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';

import { DesignLayout } from '../../components/DesignLayout';

export const MD_050202_P01: React.FC = () => {
  const [selectedLoader, setSelectedLoader] = useState('RecursiveCharacter');
  const [selectedLoader2, setSelectedLoader2] = useState('상품약관 분할방법');
  const [isLoaderDropdownOpen, setIsLoaderDropdownOpen] = useState(false);

  const [isPopupOpen] = useState(true); // 팝업이므로 항상 열려있음

  const handleClose = () => {
    // 팝업 닫기 동작 제거 (디자인 페이지이므로 항상 열려있음)
    // 
  };

  const loaderOptions = [
    { value: 'Language', label: 'Language' },
    { value: 'Language2', label: 'Language2' },
    { value: 'Language3', label: 'Language3' },
  ];
  const loaderOptions2 = [
    { value: 'Huggingface', label: 'Huggingface' },
    { value: 'Huggingface2', label: 'Huggingface2' },
    { value: 'Huggingface3', label: 'Huggingface3' },
  ];

  // 그리드 데이터 (사용하지 않음 - 제거됨)

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
            <UITypography variant='title-1' className='secondary-neutral-800 text-sb'>기본 정보</UITypography>
            <UITypography variant='body-1' className='secondary-neutral-600'>지식 설정 진행 중...</UITypography>
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
              <UIPopupHeader title='serverless 모델 수정' description='' position='right' />
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
                </UITypography>{' '}
                <UIInput.Text value={'GIP/gpt-4o-mini'} onChange={() => {}} placeholder='이름 입력' />
              </UIFormField>
            </UIArticle>

            {/* 설명 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb'>
                  설명
                </UITypography>{' '}
                <UITextArea2 value={'GIP/gpt-4o-mini 설명'} onChange={() => {}} placeholder='설명 입력' maxLength={100} />
              </UIFormField>
            </UIArticle>

            {/* 모델 유형 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                  모델 유형
                </UITypography>
                <UIDropdown
                  required={true}
                  value={selectedLoader}
                  options={loaderOptions}
                  isOpen={isLoaderDropdownOpen}
                  onClick={() => setIsLoaderDropdownOpen(!isLoaderDropdownOpen)}
                  onSelect={value => {
                    setSelectedLoader(value);
                    setIsLoaderDropdownOpen(false);
                  }}
                />
              </UIFormField>
            </UIArticle>

            {/* 공급사 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                  공급사
                </UITypography>
                <UIDropdown
                  required={true}
                  value={selectedLoader2}
                  options={loaderOptions2}
                  isOpen={isLoaderDropdownOpen}
                  onClick={() => setIsLoaderDropdownOpen(!isLoaderDropdownOpen)}
                  onSelect={value => {
                    setSelectedLoader2(value);
                    setIsLoaderDropdownOpen(false);
                  }}
                />
              </UIFormField>
            </UIArticle>

            {/* 파라미터 수(B) 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={false}>
                  파라미터 수(B)
                </UITypography>{' '}
                <UIInput.Text value={'7'} onChange={() => {}} placeholder='파라미터 수 입력' />
              </UIFormField>
            </UIArticle>

            {/* URL 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                  URL
                </UITypography>{' '}
                <UIInput.Text value={'https://api.platform.a49.com/v1.1'} onChange={() => {}} placeholder='URL 입력' />
              </UIFormField>
            </UIArticle>

            {/* identifier 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                  identifier
                </UITypography>{' '}
                <UIInput.Text value={'azure/openai/gpt-4o-mini-2024-07-18'} onChange={() => {}} placeholder='identifier 입력' />
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
