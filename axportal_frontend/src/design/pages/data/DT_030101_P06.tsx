import React, { useState } from 'react';
import { UITypography, UIButton2, UIRadio2 } from '@/components/UI/atoms';
import { UIPopupHeader, UIPopupFooter, UIArticle, UIPopupBody, UIFormField } from '@/components/UI/molecules';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { UIDropdown } from '@/components/UI/molecules/dropdown/UIDropdown';

import { UIInput } from '@/components/UI/molecules/input';

import { DesignLayout } from '../../components/DesignLayout';
import { UIUnitGroup } from '@/components/UI/molecules';

export const DT_030101_P06: React.FC = () => {
  const [isPopupOpen] = useState(true); // 팝업이므로 항상 열려있음
  // 폼 상태
  const [dataset, setdataset] = useState('1');
  const [isDatasetDropdownOpen, setIsDatasetDropdownOpen] = useState(false);

  const handleClose = () => {
    // 팝업 닫기 동작 제거 (디자인 페이지이므로 항상 열려있음)
    // 
  };

  // text 타입
  const [textValue, setTextValue] = useState('');

  // password 타입
  const [passwordValue, setPasswordValue] = useState('');

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
            <UIPopupHeader title={<>벡터 DB 만들기</>} description='' position='left' />
            {/* 레이어 팝업 바디 */}
            <UIPopupBody>{/* <UIArticle>바디 영역</UIArticle> */}</UIPopupBody>
            {/* 레이어 팝업 바디 : [참고] 이 페이지에는 왼쪽 body 영역 없음. */}
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <UIButton2 className='btn-tertiary-gray' style={{ width: '80px' }}>
                    취소
                  </UIButton2>
                  <UIButton2 className='btn-tertiary-blue' style={{ width: '80px' }} disabled={false}>
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
                  <UIInput.Text
                    value={textValue}
                    onChange={e => {
                      setTextValue(e.target.value);
                    }}
                    placeholder='이름 입력'
                  />
                </div>
              </UIFormField>
            </UIArticle>
            {/*  유형 선택 섹션 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                  유형 선택
                </UITypography>
                <UIDropdown
                  value={dataset}
                  options={[
                    { value: '1', label: 'Milvus' },
                    { value: '2', label: 'Elasticsearch' },
                  ]}
                  isOpen={isDatasetDropdownOpen}
                  onClick={() => setIsDatasetDropdownOpen(!isDatasetDropdownOpen)}
                  onSelect={(value: string) => {
                    setdataset(value);
                    setIsDatasetDropdownOpen(false);
                  }}
                  placeholder='유형 선택'
                />
              </UIFormField>
            </UIArticle>
            {/* Host 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                  Host
                </UITypography>
                <div>
                  <UIInput.Text
                    value={textValue}
                    onChange={e => {
                      setTextValue(e.target.value);
                    }}
                    placeholder='Host 입력'
                  />
                </div>
              </UIFormField>
            </UIArticle>

            {/* Port 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                  Port
                </UITypography>
                <div>
                  <UIInput.Text
                    value={textValue}
                    onChange={e => {
                      setTextValue(e.target.value);
                    }}
                    placeholder='Port 입력'
                  />
                </div>
              </UIFormField>
            </UIArticle>

            {/* User 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                  User
                </UITypography>
                <div>
                  <UIInput.Text
                    value={textValue}
                    onChange={e => {
                      setTextValue(e.target.value);
                    }}
                    placeholder='User 입력'
                  />
                </div>
              </UIFormField>
            </UIArticle>

            {/* Password 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                  Password 입력
                </UITypography>
                <div>
                  <UIInput.Password value={passwordValue} onChange={e => setPasswordValue(e.target.value)} placeholder='Password 입력' />
                </div>
              </UIFormField>
            </UIArticle>

            {/* 라디오 필드 - Secure */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                  Secure
                </UITypography>
                <UIUnitGroup gap={12} direction='column' align='start'>
                  <UIRadio2 name='basic1' value='option1' label='True' />
                  <UIRadio2 name='basic1' value='option2' label='False' />
                </UIUnitGroup>
              </UIFormField>
            </UIArticle>

            {/* Database Name 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                  Database Name
                </UITypography>
                <div>
                  <UIInput.Text
                    value={textValue}
                    onChange={e => {
                      setTextValue(e.target.value);
                    }}
                    placeholder='데이터베이스 이름 입력'
                  />
                </div>
              </UIFormField>
            </UIArticle>

            {/* 라디오 필드 - 기본설정 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                  기본설정
                </UITypography>
                <UIUnitGroup gap={12} direction='column' align='start'>
                  <UIRadio2 name='basic1' value='option1' label='True' />
                  <UIRadio2 name='basic1' value='option2' label='False' />
                </UIUnitGroup>
              </UIFormField>
            </UIArticle>
          </UIPopupBody>
          {/* 레이어 팝업 footer */}
          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <UIButton2 className='btn-secondary-gray'>이전</UIButton2>
                {/* <UIButton2 className='btn-secondary-blue'>다음</UIButton2> */}
              </UIUnitGroup>
            </UIArticle>
          </UIPopupFooter>
        </section>
      </UILayerPopup>
    </>
  );
};
