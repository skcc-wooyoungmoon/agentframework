import React, { useState } from 'react';

import { UIButton2, UITypography } from '@/components/UI/atoms';
import { UIRadio2 } from '@/components/UI/atoms/UIRadio2';
import { UIArticle, UIPopupFooter, UIPopupHeader, UIPopupBody, UIUnitGroup, UIFormField, UIInput, UITextArea2 } from '@/components/UI/molecules';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { UIDropdown } from '@/components/UI/molecules/dropdown/UIDropdown/component';

import { DesignLayout } from '../../components/DesignLayout';

export const AG_030101_P02: React.FC = () => {
  const [isPopupOpen, setIsPopupOpen] = useState(true);
  const [nameValue, setNameValue] = useState('신한은행 MCP서버');
  const [descriptionValue, setDescriptionValue] = useState('신한은행 MCP서버 1입니다.');
  const [selectedToolType, setSelectedToolType] = useState('strteamable');
  const [apiUrlValue, setApiUrlValue] = useState('https://aip.sktai.io/api/v1/agent/tools');

  // 필터 상태
  const [hrStatusFilter, setHrStatusFilter] = useState('basic');
  const [isHrStatusOpen, setIsHrStatusOpen] = useState(false);

  // tags 타입
  const [tags, setTags] = useState<string[]>([]);

  const handleClose = () => {
    setIsPopupOpen(false);
  };

  const handleToolTypeChange = (_value: string) => {
    setSelectedToolType(_value);
  };

  // password 타입
  const [passwordValue, setPasswordValue] = useState('');

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
            <UIPopupHeader title='MCP서버 수정' description='' position='left' />
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
          <UIPopupBody>
            {/* 이름 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                  서버 이름
                </UITypography>
                <UIInput.Text value={nameValue} placeholder='서버 이름 입력' onChange={e => setNameValue(e.target.value)} />
              </UIFormField>
            </UIArticle>

            {/* 설명 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb'>
                  설명
                </UITypography>
                <UITextArea2 value={descriptionValue} placeholder='설명 입력' onChange={e => setDescriptionValue(e.target.value)} maxLength={100} />
              </UIFormField>
            </UIArticle>

            {/* 전송 유형 영역 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                  전송 유형
                </UITypography>
                <UIUnitGroup gap={12} direction='column' align='center'>
                  <UIRadio2
                    name='toolType'
                    label='Sreamable HTTP'
                    value='strteamable'
                    checked={selectedToolType === 'strteamable'}
                    onChange={(checked, value) => {
                      if (checked) handleToolTypeChange(value);
                    }}
                  />
                  <UIRadio2
                    name='toolType'
                    label='SSE'
                    value='sse'
                    checked={selectedToolType === 'sse'}
                    onChange={(checked, value) => {
                      if (checked) handleToolTypeChange(value);
                    }}
                  />
                </UIUnitGroup>
              </UIFormField>
            </UIArticle>

            {/* 서버 URL */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                  서버 URL
                </UITypography>
                <UIInput.Text value={apiUrlValue} placeholder='서버 URL 입력' onChange={e => setApiUrlValue(e.target.value)} />
              </UIFormField>
            </UIArticle>

            {/* 인증 유형 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb'>
                  인증 유형
                </UITypography>
                <UITypography variant='body-2' className='secondary-neutral-600'>
                  각 유형에 맞는 설정값을 입력한 뒤, 인증 유형 우측의 [연결] 버튼으로 연결을 성공해야 [저장] 버튼이 활성화됩니다.
                </UITypography>
                <UIUnitGroup gap={8} direction='row'>
                  <UIDropdown
                    value={hrStatusFilter}
                    options={[
                      { value: 'basic', label: 'basic' },
                      { value: 'basic2', label: 'basic2' },
                      { value: 'basic3', label: 'basic3' },
                      { value: 'basic4', label: 'basic4' },
                    ]}
                    isOpen={isHrStatusOpen}
                    onClick={() => setIsHrStatusOpen(!isHrStatusOpen)}
                    onSelect={(value: string) => {
                      setHrStatusFilter(value);
                      setIsHrStatusOpen(false);
                    }}
                  />
                  <div>
                    <UIButton2 className='btn-secondary-outline !min-w-[64px]'>연결</UIButton2>
                  </div>
                </UIUnitGroup>
              </UIFormField>
            </UIArticle>

            {/* User name */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                  User name
                </UITypography>
                <UIInput.Text value={'user 1234'} placeholder='User name 입력' />
              </UIFormField>
            </UIArticle>

            {/* Password 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                  Password
                </UITypography>
                <UIInput.Password value={passwordValue} onChange={e => setPasswordValue(e.target.value)} placeholder='Password 입력' />
              </UIFormField>
            </UIArticle>

            {/* 태그 */}
            <UIArticle>
              <UIInput.Tags tags={tags} onChange={setTags} placeholder='태그 입력' label='태그' />
            </UIArticle>
          </UIPopupBody>
        </section>
      </UILayerPopup>
    </>
  );
};
