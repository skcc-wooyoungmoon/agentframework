import React, { useState } from 'react';

import { UIButton2, UITypography, UIIcon2, UIToggle, UITooltip } from '@/components/UI/atoms';
import { UIDropdown, UIPopupHeader, UIPopupBody, UIPopupFooter, UIUnitGroup, UIArticle, UIFormField, UIInput } from '@/components/UI/molecules';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { UITextArea2 } from '@/components/UI/molecules/input';
import { DesignLayout } from '../../components/DesignLayout';

export const PR_010102_P02: React.FC = () => {
  const [isPopupOpen, setIsPopupOpen] = useState(true); // 팝업이므로 기본적으로 열려있음
  const [datasetName, setDatasetName] = useState('문서요약');
  const [selectedTemplate, setSelectedTemplate] = useState('AGENT__GENERATOR');
  const [isDropdownOpen, setIsDropdownOpen] = useState(false);
  const [variableEnabled, setVariableEnabled] = useState(false);
  const [tokenLimitEnabled, setTokenLimitEnabled] = useState(true);

  const handleClose = () => {
    setIsPopupOpen(false);
  };

  const handleCancel = () => {
    handleClose();
  };

  const handleSave = () => {
    handleClose();
  };

  // textarea 타입
  const [textareaValue, setTextareaValue] = useState('');
  const [tags, setTags] = useState<string[]>([]);

  return (
    <>
      {/* DesignLayout 기본 구조 */}
      <DesignLayout
        initialMenu={{ id: 'prompt', label: '프롬프트' }}
        initialSubMenu={{
          id: 'inference-prompt',
          label: '추론 프롬프트',
          icon: 'ico-lnb-menu-20-inference-prompt',
        }}
      >
        <div className='flex items-center justify-center h-full'>
          <div className='text-center'>
            <UITypography variant='title-1' className='secondary-neutral-800 text-sb'>
              추론 프롬프트
            </UITypography>
            <UITypography variant='body-1' className='secondary-neutral-600'>
              추론 프롬프트 수정 진행 중...
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
            <UIPopupHeader title='추론 프롬프트 수정' description='' position='left' />
            <UIPopupBody>
              <UIArticle>{/* 추가 콘텐츠 필요 시 여기에 작성 */}</UIArticle>
            </UIPopupBody>
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <UIButton2 className='btn-tertiary-gray' style={{ width: '80px' }} onClick={handleCancel}>
                    취소
                  </UIButton2>
                  <UIButton2 className='btn-tertiary-blue' style={{ width: '80px' }} onClick={handleSave} disabled={false}>
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
            {/* 이름 입력 필드 - 에러 상태 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                  이름
                </UITypography>
                <UIInput.Text value={datasetName} onChange={e => setDatasetName(e.target.value)} placeholder='이름 입력' />
              </UIFormField>
            </UIArticle>

            {/* 템플릿 드롭다운 */}
            <UIArticle>
              <UIUnitGroup gap={8} direction='column'>
                <UITypography variant='title-4' required={true} className='secondary-neutral-800 text-sb'>
                  템플릿
                </UITypography>
                <UIDropdown
                  value={selectedTemplate}
                  onSelect={value => {
                    setSelectedTemplate(value);
                    setIsDropdownOpen(false);
                  }}
                  onClick={() => setIsDropdownOpen(!isDropdownOpen)}
                  isOpen={isDropdownOpen}
                  placeholder='템플릿 선택'
                  options={[
                    { value: 'AGENT__GENERATOR', label: 'AGENT__GENERATOR' },
                    { value: 'AGENT__CATEGORIZER', label: 'AGENT__CATEGORIZER' },
                    {
                      value: 'RETRIEVER__REWRITER_HYDE',
                      label: 'RETRIEVER__REWRITER_HYDE',
                    },
                    {
                      value: 'RETRIEVER__REWRITER_MULTYQUERY',
                      label: 'RETRIEVER__REWRITER_MULTYQUERY',
                    },
                    {
                      value: 'RETRIEVER__DOC_COMPRESSOR',
                      label: 'RETRIEVER__DOC_COMPRESSOR',
                    },
                    {
                      value: 'RETRIEVER__DOC_FILTER',
                      label: 'RETRIEVER__DOC_FILTER',
                    },
                    {
                      value: 'TEMPLATE__TRANSLATOR_ANY_TO_KOR',
                      label: 'TEMPLATE__TRANSLATOR_ANY_TO_KOR',
                    },
                    {
                      value: 'TEMPLATE__TRANSLATOR_KOR_TO_ANY',
                      label: 'TEMPLATE__TRANSLATOR_KOR_TO_ANY',
                    },
                    { value: 'AGENT__REVIEWER', label: 'AGENT__REVIEWER' },
                    {
                      value: 'PLAN_AND_EXECUTE_PLANNER',
                      label: 'PLAN_AND_EXECUTE_PLANNER',
                    },
                    {
                      value: 'PLAN_AND_EXECUTE_EXECUTOR',
                      label: 'PLAN_AND_EXECUTE_EXECUTOR',
                    },
                    {
                      value: 'PLAN_AND_EXECUTE_REVIEWER',
                      label: 'PLAN_AND_EXECUTE_REVIEWER',
                    },
                  ]}
                  required={false}
                  className='w-[50%]'
                />
              </UIUnitGroup>
            </UIArticle>

            {/* 프롬프트 섹션 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                  프롬프트
                </UITypography>
                <UIUnitGroup gap={16} direction='column'>
                  {/* 시스템 프롬프트 */}
                  <div className='border border-gray-200 bg-white rounded-[16px]'>
                    <div className='flex items-center justify-between px-[32px] py-[20px]'>
                      <div className='flex items-center gap-3'>
                        <UITypography variant='title-4' className='secondary-neutral-900 text-sb'>
                          시스템 프롬프트
                        </UITypography>
                      </div>
                      <UIButton2 className='btn-text-14-semibold-point' style={{ display: 'flex', alignItems: 'center', gap: '4px' }}>
                        <UIIcon2 className='ic-system-24-add' />
                        워크플로우 추가
                      </UIButton2>
                    </div>
                    <div className='pl-[32px] pr-[32px]'>
                      <UITextArea2
                        className='w-full px-0 bg-white resize-none focus:outline-none'
                        value={
                          '**목표**\n당신은 내부 문서를 참고해 질문에 답하는 AI 비서입니다. 답변 하단에 반드시 Document의 metadata 정보로 출처를 표기하십시오.\n질문과 관련없는 부연설명을 생성하지 마십시오.'
                        }
                        placeholder='워크플로우 추가 입력'
                        onChange={e => setTextareaValue(e.target.value)}
                        noBorder={true}
                      />
                    </div>
                  </div>
                  {/* 유저 프롬프트 */}
                  <div className='border border-gray-200 bg-white rounded-[16px]'>
                    <div className='flex items-center justify-between px-[32px] py-[20px]'>
                      <div className='flex items-center gap-3'>
                        <UITypography variant='title-4' className='secondary-neutral-900 text-sb'>
                          유저 프롬프트
                        </UITypography>
                      </div>
                      <UIButton2 className='btn-text-14-semibold-point' style={{ display: 'flex', alignItems: 'center', gap: '4px' }}>
                        <UIIcon2 className='ic-system-24-add' />
                        워크플로우 추가
                      </UIButton2>
                    </div>
                    <div className='pl-[32px] pr-[32px]'>
                      <UITextArea2
                        className='w-full px-0 bg-white resize-none focus:outline-none'
                        value={
                          '**목표**\n당신은 내부 문서를 참고해 질문에 답하는 AI 비서입니다. 답변 하단에 반드시 Document의 metadata 정보로 출처를 표기하십시오.\n질문과 관련없는 부연설명을 생성하지 마십시오.'
                        }
                        placeholder='워크플로우 입력'
                        onChange={e => setTextareaValue(e.target.value)}
                        noBorder={true}
                      />
                    </div>
                  </div>
                </UIUnitGroup>
              </UIFormField>
            </UIArticle>

            {/* 변수 속성 설정 섹션 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <div className='inline-flex items-center'>
                  <UITypography variant='title-4' className='secondary-neutral-800 text-sb'>
                    변수 속성 설정
                  </UITypography>
                  <UITooltip
                    trigger='click'
                    position='bottom-start'
                    type='notice'
                    title='변수 속성 설정 안내'
                    items={['토큰은 변수마다 2,147,483,647까지 제한할 수 있습니다.']}
                    bulletType='default'
                    showArrow={false}
                    showCloseButton={true}
                    className='tooltip-wrap ml-1'
                  >
                    <UIButton2 className='btn-text-only-16 p-0'>
                      <UIIcon2 className='ic-system-20-info' />
                    </UIButton2>
                  </UITooltip>
                </div>
                <div className='variable-settings-card'>
                  <div className='variable-setting-item'>
                    {/* 라벨 */}
                    <div className='flex items-center gap-2 mb-2 h-6'>
                      <div className='w-1 h-6 flex items-center'>
                        <div className='w-1 h-1 bg-gray-500 rounded-full'></div>
                      </div>
                      <UITypography variant='body-1' className='secondary-neutral-600 text-sb'>
                        context
                      </UITypography>
                    </div>

                    {/* 컨텐츠 영역 - 2열 그리드 */}
                    <div className='grid grid-cols-2 gap-3'>
                      {/* 변수 설정 박스 */}
                      <div className='bg-white border border-gray-200 rounded-[18px] overflow-hidden'>
                        <div className='px-8 pt-8 pb-6'>
                          <div className='flex items-center gap-3'>
                            <UIToggle checked={variableEnabled} onChange={setVariableEnabled} size='small' />
                            <UITypography variant='title-4' className='secondary-neutral-800'>
                              변수 설정
                            </UITypography>
                          </div>
                        </div>
                        <div className='w-full px-8'>
                          <UITextArea2
                            className='w-full px-0 bg-white resize-none focus:outline-none'
                            value={textareaValue}
                            placeholder='정규표현식 입력'
                            onChange={e => setTextareaValue(e.target.value)}
                            noBorder={true}
                          />
                        </div>
                      </div>

                      {/* 토큰 제한 박스 */}
                      <div className='bg-white border border-gray-200 rounded-[18px] overflow-hidden'>
                        <div className='px-8 pt-8 pb-6'>
                          <div className='flex items-center gap-3'>
                            <UIToggle checked={tokenLimitEnabled} onChange={setTokenLimitEnabled} size='small' />
                            <UITypography variant='title-4' className='secondary-neutral-800'>
                              토큰 제한
                            </UITypography>
                          </div>
                        </div>
                        <div className='w-full px-8'>
                          <UITextArea2
                            className='w-full px-0 bg-white resize-none focus:outline-none'
                            value={textareaValue}
                            placeholder='토큰 제한 수 입력'
                            onChange={e => setTextareaValue(e.target.value)}
                            noBorder={true}
                          />
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </UIFormField>
            </UIArticle>

            {/* 태그 섹션 */}
            <UIArticle>
              <UIInput.Tags tags={tags} onChange={setTags} placeholder='태그 입력' label='태그' />
            </UIArticle>
          </UIPopupBody>
        </section>
      </UILayerPopup>
    </>
  );
};
