import React, { useState } from 'react';

import { UITypography, UIButton2 } from '@/components/UI/atoms';
import { UIPopupHeader, UIPopupFooter, UIArticle, UIPopupBody, UIFormField } from '@/components/UI/molecules';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { UIDropdown } from '@/components/UI/molecules/dropdown/UIDropdown';
import { DesignLayout } from '../../components/DesignLayout';
import { UIUnitGroup } from '@/components/UI/molecules';
import { UIInput } from '@/components/UI/molecules/input';

export const DT_030101_P02: React.FC = () => {
  const [isPopupOpen] = useState(true); // 팝업이므로 항상 열려있음
  const [dataset, setdataset] = useState('1');
  const [isDatasetDropdownOpen, setIsDatasetDropdownOpen] = useState(false);

  const handleClose = () => {
    // 팝업 닫기 동작 제거 (디자인 페이지이므로 항상 열려있음)
    // 
  };

  // text 타입
  const [textValue, setTextValue] = useState('');

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
            <UITypography variant='body-1' className='secondary-neutral-600'>지식 만들기 진행 중...</UITypography>
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
              <UIPopupHeader title='Ingestion Tools 만들기' description='' position='left' />
              <UIPopupBody></UIPopupBody>
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

            {/* 유형  필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={false}>
                  유형
                </UITypography>
                <UIDropdown
                  value={dataset}
                  options={[
                    { value: '1', label: 'SKT Document Intelligence' },
                    { value: '2', label: 'SKT Document Intelligence2' },
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

            {/* 모델 배포명 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                  모델 배포명
                </UITypography>
                <UIDropdown
                  value={dataset}
                  options={[
                    { value: '1', label: 'Qwen2.5-VL-7B-Instruct' },
                    { value: '2', label: 'Qwen2.5-VL-7B-Instruct2' },
                  ]}
                  isOpen={isDatasetDropdownOpen}
                  onClick={() => setIsDatasetDropdownOpen(!isDatasetDropdownOpen)}
                  onSelect={(value: string) => {
                    setdataset(value);
                    setIsDatasetDropdownOpen(false);
                  }}
                  placeholder='모델 배포명 선택'
                />
              </UIFormField>
            </UIArticle>

            {/* Prompt 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={false}>
                  Prompt
                </UITypography>
                <div>
                  <UIInput.Text
                    value={'test'}
                    onChange={e => {
                      setTextValue(e.target.value);
                    }}
                    placeholder='Prompt 입력'
                  />
                </div>
              </UIFormField>
            </UIArticle>

            {/* Max Tokens 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={false}>
                  Max Tokens
                </UITypography>
                <div>
                  <UIInput.Text
                    value={'1,000'}
                    onChange={e => {
                      setTextValue(e.target.value);
                    }}
                    placeholder='Max Tokens 입력'
                  />
                </div>
              </UIFormField>
            </UIArticle>

            {/* Dpi 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={false}>
                  Dpi
                </UITypography>
                <div>
                  <UIInput.Text
                    value={'10'}
                    onChange={e => {
                      setTextValue(e.target.value);
                    }}
                    placeholder='Dpi 입력'
                  />
                </div>
              </UIFormField>
            </UIArticle>
            {/* Timeout 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={false}>
                  Timeout
                </UITypography>
                <div>
                  <UIInput.Text
                    value={'300'}
                    onChange={e => {
                      setTextValue(e.target.value);
                    }}
                    placeholder='Timeout 입력'
                  />
                </div>
              </UIFormField>
            </UIArticle>
            {/* Max Retries 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={false}>
                  Max Retries
                </UITypography>
                <div>
                  <UIInput.Text
                    value={'5'}
                    onChange={e => {
                      setTextValue(e.target.value);
                    }}
                    placeholder='Max Retries 입력'
                  />
                </div>
              </UIFormField>
            </UIArticle>
            {/* Force Ocr 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={false}>
                  Force Ocr
                </UITypography>
                <div>
                  <UIInput.Text
                    value={'2'}
                    onChange={e => {
                      setTextValue(e.target.value);
                    }}
                    placeholder='Force Ocr 입력'
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
                {/* <UIButton2 className='btn-secondary-blue'>다음</UIButton2> */}
              </UIUnitGroup>
            </UIArticle>
          </UIPopupFooter>
        </section>
      </UILayerPopup>
    </>
  );
};
