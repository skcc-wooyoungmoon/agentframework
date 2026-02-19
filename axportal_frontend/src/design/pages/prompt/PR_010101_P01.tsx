import React, { useState } from 'react';

import { UIButton2, UITypography, UIIcon2, UITooltip, UIToggle } from '@/components/UI/atoms';
import { UIDropdown, UIArticle, UIPopupHeader, UIPopupBody, UIPopupFooter, UIUnitGroup, UIFormField, UIInput, UIList } from '@/components/UI/molecules';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { UITextArea2 } from '@/components/UI/molecules/input';
import { DesignLayout } from '../../components/DesignLayout';

export const PR_010101_P01: React.FC = () => {
  const [isPopupOpen, setIsPopupOpen] = useState(true); // 팝업이므로 기본적으로 열려있음
  const [datasetName, setDatasetName] = useState('');
  const [tags, setTags] = useState<string[]>(['여신팀', '김신한님']);
  const [selectedTemplate, setSelectedTemplate] = useState('none');
  const [isDropdownOpen, setIsDropdownOpen] = useState(false);
  // textarea 타입
  const [systemPromptValue, setSystemPromptValue] = useState('');
  const [userPromptValue, setUserPromptValue] = useState('');
  const [variableSettingValue, setVariableSettingValue] = useState('');
  const [tokenLimitValue, setTokenLimitValue] = useState('');
  const [variableEnabled, setVariableEnabled] = useState(false);
  const [tokenLimitEnabled, setTokenLimitEnabled] = useState(true);

  // 템플릿별 설명 데이터
  const templateDescriptions: Record<string, { title: string; description: string; variables: string }> = {
    AGENT__GENERATOR: {
      title: 'AGENT__GENERATOR 란?',
      description:
        '사용자의 질문에 직접 답변할 때 사용되는 “기본 응답 템플릿”을 만드는 기능입니다. 사용자가 입력한 문장 + 관련 컨텍스트(검색 결과 등)를 활용해 답변을 생성할 때 사용됩니다.',
      variables: '기본 변수: query, context',
    },
    AGENT__CATEGORIZER: {
      title: 'AGENT__CATEGORIZER 란?',
      description:
        '입력된 내용을 사전에 정의된 카테고리로 분류하는 템플릿을 만드는 기능입니다. 사용자의 질문이 어떤 타입인지(예: 계좌 문의/대출 문의/오류 문의 등) 분류할 때 사용됩니다.',
      variables: '기본 변수: topic_classes, topic_descripstions, query, context',
    },
    RETRIEVER__REWRITER_HYDE: {
      title: 'RETRIEVER__REWRITER_HYDE 란?',
      description: '검색 성능을 높이기 위해 “가상 문서(HYDE 문서)” 를 생성하는 템플릿입니다. 검색 엔진이 더 관련성 높은 결과를 찾을 수 있도록 돕습니다.',
      variables: '기본 변수: query',
    },
    RETRIEVER__REWRITER_MULTYQUERY: {
      title: 'RETRIEVER_REWRITER_MULTYQUERY 란?',
      description: '하나의 질문을 여러 개의 다양한 검색 쿼리로 재작성하는 기능입니다. 다양한 관점에서 질문을 표현해 검색 범위를 확장할 때 사용됩니다.',
      variables: '기본 변수: query, num_queries',
    },
    RETRIEVER__DOC_COMPRESSOR: {
      title: 'RETRIEVER__DOC_COMPRESSOR 란?',
      description: '검색된 문서들을 불필요한 내용은 제거하고 필요한 핵심 정보만 압축하는 템플릿입니다. 긴 문서를 “핵심 요약본”으로 만들때 사용됩니다.',
      variables: '기본 변수: query, context',
    },
    RETRIEVER__DOC_FILTER: {
      title: 'RETRIEVER__DOC_FILTER란?',
      description: '검색된 문서 중에서 품질이 낮거나 관련성이 낮은 문서를 제외하는 템플릿입니다. 관련 문서만 남겨 LLM이 더 정확한 답변을 하도록 돕습니다.',
      variables: '기본 변수: query, context',
    },
    TEMPLATE__TRANSLATOR_ANY_TO_KOR: {
      title: 'TEMPLATE__TRANSLATOR_ANY_TO_KOR 란?',
      description: '여러 언어로 된 입력을 한국어로 번역하는 템플릿입니다. 다국어 입력을 한글화하여 처리하고 싶을 때 사용합니다.',
      variables: '기본 변수: query, context',
    },
  };

  // 선택된 템플릿의 설명 가져오기
  const currentTemplateInfo = selectedTemplate !== 'none' ? templateDescriptions[selectedTemplate] : null;

  const handleClose = () => {
    setIsPopupOpen(false);
  };

  const handleCancel = () => {
    handleClose();
  };

  const handleSave = () => {
    handleClose();
  };

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
            <UIPopupHeader title='추론 프롬프트 등록' description='' position='left' />

            {/* 버튼 그룹  */}
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
            {/* 이름 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                  이름
                </UITypography>
                <UIInput.Text value={datasetName} placeholder='이름 입력' onChange={e => setDatasetName(e.target.value)} disabled={false} />
              </UIFormField>
            </UIArticle>

            {/* 템플릿 드롭다운 */}
            <UIArticle>
              <UIUnitGroup gap={8} direction='column'>
                <UIFormField gap={4} direction='row'>
                  <UITypography variant='title-4' required={false} className='secondary-neutral-800 text-sb'>
                    템플릿
                  </UITypography>
                  <UITooltip
                    trigger='click'
                    position='bottom-start'
                    type='notice'
                    title=''
                    items={['템플릿 선택 시 템플릿 사용 가이드가 노출됩니다.']}
                    bulletType='default'
                    showArrow={false}
                    showCloseButton={true}
                    className='tooltip-wrap ml-1'
                  >
                    <UIButton2 className='btn-text-only-16 p-0'>
                      <UIIcon2 className='ic-system-20-info' />
                    </UIButton2>
                  </UITooltip>
                </UIFormField>
                <UIDropdown
                  value={selectedTemplate}
                  onSelect={setSelectedTemplate}
                  onClick={() => setIsDropdownOpen(!isDropdownOpen)}
                  isOpen={isDropdownOpen}
                  placeholder='템플릿 선택'
                  options={[
                    { value: 'none', label: 'none' },
                    { value: 'AGENT__GENERATOR', label: 'AGENT__GENERATOR' },
                    { value: 'AGENT__CATEGORIZER', label: 'AGENT__CATEGORIZER' },
                    { value: 'RETRIEVER__REWRITER_HYDE', label: 'RETRIEVER__REWRITER_HYDE' },
                    { value: 'RETRIEVER__REWRITER_MULTYQUERY', label: 'RETRIEVER__REWRITER_MULTYQUERY' },
                    { value: 'RETRIEVER__DOC_COMPRESSOR', label: 'RETRIEVER__DOC_COMPRESSOR' },
                    { value: 'RETRIEVER__DOC_FILTER', label: 'RETRIEVER__DOC_FILTER' },
                    { value: 'TEMPLATE__TRANSLATOR_ANY_TO_KOR', label: 'TEMPLATE__TRANSLATOR_ANY_TO_KOR' },
                  ]}
                  required={false}
                  className='w-[50%]'
                />
                {currentTemplateInfo && (
                  <div className='box-fill'>
                    <UIUnitGroup gap={8} direction='column' align='start'>
                      <div style={{ display: 'flex', alignItems: 'center', gap: '0 6px' }}>
                        <UIIcon2 className='ic-system-16-info-gray' />
                        <UITypography variant='body-2' className='secondary-neutral-600 text-sb'>
                          {currentTemplateInfo.title}
                        </UITypography>
                      </div>
                      <div style={{ paddingLeft: '22px' }}>
                        <UIUnitGroup gap={8} direction='column' align='start'>
                          <UIList
                            gap={4}
                            direction='column'
                            className='ui-list_dash'
                            data={[
                              {
                                dataItem: (
                                  <UITypography variant='body-2' className='secondary-neutral-600'>
                                    {currentTemplateInfo.description}
                                  </UITypography>
                                ),
                              },
                            ]}
                          />
                          <UIList
                            gap={4}
                            direction='column'
                            className='ui-list_dash'
                            data={[
                              {
                                dataItem: (
                                  <UITypography variant='body-2' className='secondary-neutral-600'>
                                    {currentTemplateInfo.variables}
                                  </UITypography>
                                ),
                              },
                            ]}
                          />
                        </UIUnitGroup>
                      </div>
                    </UIUnitGroup>
                  </div>
                )}
              </UIUnitGroup>
            </UIArticle>

            {/* 설명 섹션 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UIFormField gap={4} direction='row'>
                  <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                    프롬프트
                  </UITypography>
                  <UITooltip
                    trigger='click'
                    position='bottom-start'
                    type='notice'
                    title='프롬프트 안내'
                    items={[
                      '{{변수명}}을 입력하면 해당 변수의 속성 설정 영역이 자동으로 표시됩니다.',
                      '워크플로우 추가 버튼으로 프롬프트에 사용할 워크플로우(XML)를 불러올 수 있습니다. 워크플로우는 프롬프트 > 워크플로우 메뉴에서 관리할 수 있습니다.',
                    ]}
                    bulletType='default'
                    showArrow={false}
                    showCloseButton={true}
                    className='tooltip-wrap ml-1'
                  >
                    <UIButton2 className='btn-text-only-16 p-0'>
                      <UIIcon2 className='ic-system-20-info' />
                    </UIButton2>
                  </UITooltip>
                </UIFormField>

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
                        value={systemPromptValue}
                        placeholder=' '
                        onChange={e => setSystemPromptValue(e.target.value)}
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
                        value={userPromptValue}
                        placeholder=' '
                        onChange={e => setUserPromptValue(e.target.value)}
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
                    items={[
                      '프롬프트에 변수가 감지되면 해당 변수의 형식과 입력 범위를 정의할 수 있는 설정 영역이 노출됩니다.',
                      '변수에 허용할 입력 형식을 정규표현식으로 지정해 유효한 값만 입력되도록 제어합니다.',
                      '변수에 입력될 최대 토큰 수를 지정해 과도한 길이의 입력을 방지합니다.',
                    ]}
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
                            value={variableSettingValue}
                            placeholder='정규표현식 입력'
                            onChange={e => setVariableSettingValue(e.target.value)}
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
                            value={tokenLimitValue}
                            placeholder='토큰 제한 수 입력'
                            onChange={e => setTokenLimitValue(e.target.value)}
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
              <UIInput.Tags tags={tags} onChange={setTags} placeholder='태그 입력' label='태그' required={true} tagMaxLength={14} /> {/* [퍼블수정] 태그 글자수 14자 */}
            </UIArticle>
          </UIPopupBody>
        </section>
      </UILayerPopup>
    </>
  );
};
