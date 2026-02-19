import React, { useState } from 'react';

import { UIButton2, UIRadio2, UITypography } from '@/components/UI/atoms';
import { UIUnitGroup, UIPopupHeader, UIPopupBody, UIPopupFooter, UIArticle, UIFormField, UIGroup } from '@/components/UI/molecules';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { DesignLayout } from '../../components/DesignLayout';
import { UIInput, UITextArea2 } from '@/components/UI/molecules/input';
import { UIToggle } from '@/components/UI';

export const AG_020101_P02: React.FC = () => {
  const [isPopupOpen, setIsPopupOpen] = useState(true);

  const [selectedToolType, setSelectedToolType] = useState('option1');

  const handleClose = () => {
    setIsPopupOpen(false);
  };

  // text 타입
  const [textValue, setTextValue] = useState('');
  const [textValue2, setTextValue2] = useState('');
  const [textValue3, setTextValue3] = useState('');

  // 헤더 파라미터 - 각 row별 독립 상태
  const [headerParam, setHeaderParam] = useState([{ name: '', value: '' }]);

  // Query 파라미터 - 각 row별 독립 상태
  const [queryParam, setQueryParam] = useState([{ name: '', value: '', checked: false }]);

  // textarea 타입
  const [textareaValue, setTextareaValue] = useState('');

  const [isToolEnabled, setIsToolEnabled] = useState(false);

  const handleToolTypeChange = (checked: boolean, value: string) => {
    if (checked) {
      setSelectedToolType(value);
    }
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
              도구 등록...
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
          <UIPopupAside>
            {/* 레이어 팝업 헤더 */}
            <UIPopupHeader title='Tool 등록' description='' position='left' />
            {/* 레이어 팝업 footer */}
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <UIButton2 className='btn-tertiary-gray !w-[80px]'>취소</UIButton2>
                  <UIButton2 className='btn-tertiary-blue !w-[80px]'>저장</UIButton2>
                </UIUnitGroup>
              </UIArticle>
            </UIPopupFooter>
          </UIPopupAside>
        }
      >
        {/* 우측 Contents 영역 콘텐츠 */}
        <section className='section-popup-content'>
          {/* 레이어 팝업 바디 */}
          <UIPopupBody>
            {/* 서브 컨텐츠 헤더 */}
            <UIArticle>
              <UITypography variant='title-3' className='secondary-neutral-900 text-sb'>
                기본 정보
              </UITypography>
            </UIArticle>

            {/* 이름 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' required={true} className='secondary-neutral-800 text-sb'>
                  이름
                </UITypography>
                <UIInput.Text
                  value={textValue}
                  placeholder='이름 입력'
                  onChange={e => {
                    setTextValue(e.target.value);
                  }}
                />
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
                <UITextArea2 value={textareaValue} placeholder='설명 입력' maxLength={10} onChange={e => setTextareaValue(e.target.value)} />
              </UIFormField>
            </UIArticle>

            {/* 서브 컨텐츠 헤더 */}
            <UIArticle>
              <UITypography variant='title-3' className='secondary-neutral-900 text-sb'>
                Tool 정보
              </UITypography>
            </UIArticle>

            {/* Tool 정보 타이틀 영역 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' required={true} className='secondary-neutral-800 text-sb'>
                  Tool 유형
                </UITypography>
                <UIUnitGroup gap={12} direction='column' align='start'>
                  <UIRadio2 name='basic1' value='option1' label='Custom AP' checked={selectedToolType === 'option1'} onChange={handleToolTypeChange} />
                  <UIRadio2 name='basic1' value='option2' label='Custom Code' checked={selectedToolType === 'option2'} onChange={handleToolTypeChange} />
                </UIUnitGroup>
              </UIFormField>
            </UIArticle>

            {/* 메소드 드랍다운 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' required={true} className='secondary-neutral-800 text-sb'>
                  메소드
                </UITypography>
                <UIInput.Text
                  value={textValue2}
                  placeholder='GET'
                  onChange={e => {
                    setTextValue2(e.target.value);
                  }}
                />
              </UIFormField>
            </UIArticle>
            {/* API URL */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' required={true} className='secondary-neutral-800 text-sb'>
                  API URL
                </UITypography>
                <UIInput.Text
                  value={textValue3}
                  placeholder='API URL 입력'
                  onChange={e => {
                    setTextValue3(e.target.value);
                  }}
                />
              </UIFormField>
            </UIArticle>
            {/* 서브 컨텐츠 헤더 */}
            <UIArticle>
              <UITypography variant='title-3' className='secondary-neutral-900 text-sb'>
                파라미터 구성
              </UITypography>
            </UIArticle>

            {/* [퍼블수정] 레이아웃 구조 변경 S */}
            <UIArticle>
              {/* 헤더 파라미터 영역 */}
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb'>
                  헤더 피라미터 설정
                </UITypography>
                {/* [퍼블수정] 레이아웃 구조 수정 */}
                <UIUnitGroup gap={8} direction='column'>
                  {headerParam.map((param, index) => (
                    <div key={index} className='flex gap-2 items-center'>
                      <div className='flex-1'>
                        <UIInput.Text
                          value={param.name}
                          placeholder='파라미터 이름 입력'
                          onChange={e => {
                            const newHeaderParam = [...headerParam];
                            newHeaderParam[index].name = e.target.value;
                            setHeaderParam(newHeaderParam);
                          }}
                        />
                      </div>
                      <div className='flex-1'>
                        <UIInput.Text
                          value={param.value}
                          placeholder='파라미터 값 입력'
                          onChange={e => {
                            const newHeaderParam = [...headerParam];
                            newHeaderParam[index].value = e.target.value;
                            setHeaderParam(newHeaderParam);
                          }}
                        />
                      </div>
                      <UIButton2 className='btn-add-only'></UIButton2>
                      <UIButton2 className='ic-system-48-delete cursor-pointer'></UIButton2>
                    </div>
                  ))}
                  <UIButton2
                    className='button-line-blue w-[121px]'
                    onClick={() => {
                      setHeaderParam([...headerParam, { name: '', value: '' }]);
                    }}
                  >
                    파라미터 추가
                  </UIButton2>
                </UIUnitGroup>
              </UIFormField>
            </UIArticle>
            {/* Query 파라미터 설정 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb'>
                  Query 피라미터 설정
                </UITypography>
                <UIUnitGroup gap={8} direction='column'>
                  {/* [251118_퍼블수정] : 토글 위치 수정 */}
                  <UIGroup gap={12} direction='row' vAlign='center'>
                    <UITypography variant='body-1' className='secondary-neutral-800'>
                      다이나믹
                    </UITypography>
                    <UIToggle checked={isToolEnabled} onChange={setIsToolEnabled} size='medium' />
                  </UIGroup>

                  {queryParam.map((param, index) => (
                    <div key={index} className='flex gap-2 items-center'>
                      <div className='flex-1'>
                        <UIInput.Text
                          value={param.name}
                          placeholder='파라미터 이름 입력'
                          onChange={e => {
                            const newQueryParam = [...queryParam];
                            newQueryParam[index].name = e.target.value;
                            setQueryParam(newQueryParam);
                          }}
                        />
                      </div>
                      <div className='flex-1'>
                        <UIInput.Text
                          value={param.value}
                          placeholder='파라미터 값 입력'
                          onChange={e => {
                            const newQueryParam = [...queryParam];
                            newQueryParam[index].value = e.target.value;
                            setQueryParam(newQueryParam);
                          }}
                        />
                      </div>
                      <UIButton2 className='btn-add-only'></UIButton2>
                      <UIButton2 className='ic-system-48-delete cursor-pointer'></UIButton2>
                    </div>
                  ))}
                  <UIButton2
                    className='button-line-blue w-[121px]'
                    onClick={() => {
                      setQueryParam([...queryParam, { name: '', value: '', checked: false }]);
                    }}
                  >
                    파라미터 추가
                  </UIButton2>
                </UIUnitGroup>
              </UIFormField>
            </UIArticle>
          </UIPopupBody>
        </section>
      </UILayerPopup>
    </>
  );
};
