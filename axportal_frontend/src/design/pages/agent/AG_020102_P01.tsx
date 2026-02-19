import React, { useState } from 'react';

import { UIButton2, UITypography, UIToggle } from '@/components/UI/atoms';
import { UICode } from '@/components/UI/atoms/UICode';
import { UIRadio2 } from '@/components/UI/atoms/UIRadio2';
import { UIArticle, UIPopupFooter, UIPopupHeader, UIPopupBody, UIUnitGroup, UIFormField, UIInput, UITextArea2 } from '@/components/UI/molecules';
import { UIDropdown } from '@/components/UI/molecules/dropdown/UIDropdown';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';

import { DesignLayout } from '../../components/DesignLayout';

export const AG_020102_P01: React.FC = () => {
  const [isPopupOpen, setIsPopupOpen] = useState(true);
  const [nameValue, setNameValue] = useState('기준금리 조회');
  const [textValue, setTextValue] = useState('');
  const [descriptionValue, setDescriptionValue] = useState('현재 기준금리 및 주요 은행 예·적금 금리를 조회하는 도구');
  const [selectedToolType, setSelectedToolType] = useState('custom-api');
  const [selectedMethod, setSelectedMethod] = useState('GET');
  const [isMethodDropdownOpen, setIsMethodDropdownOpen] = useState(false);
  const [apiUrlValue, setApiUrlValue] = useState('https://aip.sktai.io/api/v1/agent/tools');
  const [headerParamName, setHeaderParamName] = useState('');
  const [headerParamValue, setHeaderParamValue] = useState('');
  const [apiParamName, setApiParamName] = useState('');
  const [apiParamValue, setApiParamValue] = useState('');
  const [isToolEnabled, setIsToolEnabled] = useState(false);

  const handleClose = () => {
    setIsPopupOpen(false);
  };

  const handleToolTypeChange = (_value: string) => {
    setSelectedToolType(_value);
  };

  const methodOptions = [{ value: 'GET', label: 'GET' }];

  const handleMethodDropdownClick = () => {
    setIsMethodDropdownOpen(!isMethodDropdownOpen);
  };

  const handleMethodSelect = (value: string) => {
    setSelectedMethod(value);
    setIsMethodDropdownOpen(false);
  };

  return (
    <>
      <DesignLayout
        initialMenu={{ id: 'agent', label: '에이전트' }}
        initialSubMenu={{
          id: 'agent-tools',
          label: '에이전트도구',
          icon: 'ico-lnb-menu-20-agent',
        }}
      >
        <div className='flex items-center justify-center h-full'>
          <div className='text-center'>
            <UITypography variant='title-1' className='secondary-neutral-800 text-sb'>
              에이전트
            </UITypography>
            <UITypography variant='body-1' className='secondary-neutral-600'>
              도구 수정...
            </UITypography>
          </div>
        </div>
      </DesignLayout>

      <UILayerPopup
        isOpen={isPopupOpen}
        onClose={handleClose}
        size='fullscreen'
        showOverlay={true}
        leftContent={
          /* 좌측 Step 영역 콘텐츠 */
          <UIPopupAside>
            {/* 레이어 팝업 헤더 */}
            <UIPopupHeader title='Tool 수정' description='' position='left' />
            {/* <UIPopupBody></UIPopupBody> */}
            {/* 레이어 팝업 바디 : [참고] 이 페이지에는 왼쪽 body 영역 없음. */}
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <UIButton2 className='btn-tertiary-gray' style={{ width: '80px' }}>
                    취소
                  </UIButton2>
                  <UIButton2 className='btn-tertiary-blue' style={{ width: '80px' }}>
                    저장
                  </UIButton2>
                </UIUnitGroup>
              </UIArticle>
            </UIPopupFooter>
          </UIPopupAside>
        }
      >
        {/* 우측 Contents 영역 콘텐츠 */}
        <section className='section-popup-content'>
          <UIPopupHeader title='기본 정보' description='' position='right' />
          <UIPopupBody>
            {/* 이름 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                  이름
                </UITypography>
                <UIInput.Text value={nameValue} placeholder='이름 입력' onChange={e => setNameValue(e.target.value)} disabled />
              </UIFormField>
            </UIArticle>

            {/* 표시이름 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' required={false} className='secondary-neutral-800 text-sb'>
                  표시이름
                </UITypography>
                <UIInput.Text
                  value={textValue}
                  placeholder=' 표시이름 입력'
                  onChange={e => {
                    setTextValue(e.target.value);
                  }}
                />
              </UIFormField>
            </UIArticle>

            {/* 설명 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                  설명
                </UITypography>
                <UITypography variant='body-2' className='secondary-neutral-600'>
                  Generator 노드에서 Tool 사용시, LLM은 Tool 설명을 참고해 호출 여부를 결정합니다. 원하는 동작을 위해 Tool 설명을 구체적이고 정확하게 입력해주세요.
                </UITypography>
                <UITextArea2 value={descriptionValue} placeholder='설명 입력' onChange={e => setDescriptionValue(e.target.value)} maxLength={100} />
              </UIFormField>
            </UIArticle>

            {/* Tool 정보 타이틀 영역 */}
            <UIArticle>
              <UITypography variant='title-4' className='secondary-neutral-900'>
                Tool 정보
              </UITypography>
            </UIArticle>

            {/* 도구 유형 라디오 버튼 영역 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                  Tool 유형
                </UITypography>
                <UIUnitGroup gap={12} direction='column' align='center'>
                  <UIRadio2
                    name='toolType'
                    label='Custom API'
                    value='custom-api'
                    checked={selectedToolType === 'custom-api'}
                    onChange={(checked, value) => {
                      if (checked) handleToolTypeChange(value);
                    }}
                    disabled
                  />
                  <UIRadio2
                    name='toolType'
                    label='Custom Code'
                    value='custom-code'
                    checked={selectedToolType === 'custom-code'}
                    onChange={(checked, value) => {
                      if (checked) handleToolTypeChange(value);
                    }}
                    disabled
                  />
                </UIUnitGroup>
              </UIFormField>
            </UIArticle>

            {/* 메소드 드랍다운 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                  메소드
                </UITypography>
                <UIDropdown
                  required={true}
                  placeholder='메소드를 선택하세요'
                  value={selectedMethod}
                  options={methodOptions}
                  isOpen={isMethodDropdownOpen}
                  onClick={handleMethodDropdownClick}
                  onSelect={handleMethodSelect}
                />
              </UIFormField>
            </UIArticle>

            {/* API URL */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                  API URL
                </UITypography>
                <UIInput.Text value={apiUrlValue} placeholder='API URL 입력' onChange={e => setApiUrlValue(e.target.value)} />
              </UIFormField>
            </UIArticle>

            {/* 파라미터 구성 타이틀 영역 */}
            <UIArticle>
              <UITypography variant='title-3' className='secondary-neutral-900'>
                파라미터 구성
              </UITypography>
            </UIArticle>

            {/* 헤더 파라미터 영역 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-900'>
                  헤더 파라미터 설정
                </UITypography>
                <UIUnitGroup gap={8} direction='row'>
                  <div className='flex-1'>
                    <UIInput.Text value={headerParamName} placeholder='파라미터 이름' onChange={e => setHeaderParamName(e.target.value)} />
                  </div>
                  <div className='flex-1'>
                    <UIInput.Text value={headerParamValue} placeholder='파라미터 값' onChange={e => setHeaderParamValue(e.target.value)} />
                  </div>
                  <UIButton2 className='button-plus-blue cursor-pointer'>{''}</UIButton2>
                  <UIButton2 className='ic-system-48-delete cursor-pointer'>{''}</UIButton2>
                </UIUnitGroup>
                <UIButton2 className='button-line-blue w-[121px]'>파라미터 추가</UIButton2>
              </UIFormField>
            </UIArticle>

            {/* API 파라미터 설정 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-900'>
                  API 파라미터 설정
                </UITypography>
                <UIUnitGroup gap={8} direction='row' vAlign='center'>
                  <div className='flex-1'>
                    <UIInput.Text value={apiParamName} placeholder='파라미터 이름' onChange={e => setApiParamName(e.target.value)} />
                  </div>
                  <div className='flex-1'>
                    <UIInput.Text value={apiParamValue} placeholder='파라미터 값' onChange={e => setApiParamValue(e.target.value)} />
                  </div>
                  <UITypography variant='body-2' className='secondary-neutral-600'>
                    다이나믹
                  </UITypography>
                  <div>
                    <UIToggle checked={isToolEnabled} onChange={setIsToolEnabled} size='medium' />
                  </div>
                  <UIButton2 className='button-plus-blue cursor-pointer'></UIButton2>
                  <UIButton2 className='ic-system-48-delete cursor-pointer'></UIButton2>
                </UIUnitGroup>
                <UIButton2 className='button-line-blue w-[121px]'>파라미터 추가</UIButton2>
              </UIFormField>
            </UIArticle>
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                  코드
                </UITypography>
                {/* 소스코드 영역 */}
                <UICode value={'여기는 에디터 화면입니다. 테스트 테스트'} language='python' theme='dark' width='100%' minHeight='472px' maxHeight='500px' readOnly={false} />
              </UIFormField>
            </UIArticle>
          </UIPopupBody>
        </section>
      </UILayerPopup>
    </>
  );
};
