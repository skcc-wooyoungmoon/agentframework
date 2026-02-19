import { useState } from 'react';

import { UIButton2, UITypography } from '@/components/UI/atoms';
import { UIArticle, UIPopupBody, UIPopupFooter, UIPopupHeader, UIStepper, UIUnitGroup, UIFormField, UIList, UITextArea2, UIGroup, UIInput } from '@/components/UI/molecules';
import { UIAccordion } from '@/components/UI/molecules';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { DesignLayout } from '../../components/DesignLayout';

// 스테퍼 데이터
const stepperItems = [
  { step: 1, label: '분류 선택' },
  { step: 2, label: '배포 대상 선택' },
  { step: 3, label: '운영용 정보 입력' },
  { step: 4, label: '최종 정보 확인' },
];

// 프롬프트 아코디언 데이터
const createPromptAccordionItems = () => [
  {
    title: '벡터 DB (ExlasticSearch)',
    titleSub: '[AzureAISearch] axplatform-ai-search-dev',
    content: (
      <>
        <UIGroup direction='column' gap={16}>
          <UIFormField gap={24} direction='row'>
            <UIUnitGroup gap={8} direction='column'>
              <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                Endpoint(개발)
              </UITypography>
              <UITextArea2 value={'https://axplatform-ai-search-dev.search.window.net'} placeholder='직접 입력' style={{ height: '80px' }} disabled={true} resizable={false} />
            </UIUnitGroup>
            <UIUnitGroup gap={8} direction='column'>
              <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                Endpoint(운영)
              </UITypography>
              <UITextArea2 value={''} placeholder='운영망 정보가 없습니다. 확인 후 입력해주세요.' style={{ height: '80px' }} disabled={true} resizable={false} readOnly={true} />
            </UIUnitGroup>
          </UIFormField>
          <UIFormField gap={24} direction='row'>
            <UIUnitGroup gap={8} direction='column'>
              <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                API Key(개발)
              </UITypography>
              <UITextArea2 value={'https://axplatform-ai-search-dev.search.window.net'} placeholder='직접 입력' style={{ height: '80px' }} disabled={true} resizable={false} />
            </UIUnitGroup>
            <UIUnitGroup gap={8} direction='column'>
              <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                API Key(운영)
              </UITypography>
              <UITextArea2 value={''} placeholder='운영망 정보가 없습니다. 확인 후 입력해주세요.' style={{ height: '80px' }} disabled={true} resizable={false} readOnly={true} />
            </UIUnitGroup>
          </UIFormField>
        </UIGroup>
      </>
    ),
    defaultOpen: false,
    showNoticeIcon: false,
  },
  {
    title: '벡터 DB (Milvus)',
    titleSub: 'TestVector',
    content: (
      <>
        <UIGroup direction='column' gap={16}>
          <UIFormField gap={24} direction='row'>
            <UIUnitGroup gap={8} direction='column'>
              <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                Host(개발)
              </UITypography>
              <UITextArea2 value={'aip.sktai.io'} placeholder='직접 입력' style={{ height: '80px' }} disabled={true} resizable={false} />
            </UIUnitGroup>
            <UIUnitGroup gap={8} direction='column'>
              <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                Host(운영)
              </UITypography>
              <UITextArea2 value={''} placeholder='운영망 정보가 없습니다. 확인 후 입력해주세요.' style={{ height: '80px' }} disabled={true} resizable={false} readOnly={true} />
            </UIUnitGroup>
          </UIFormField>
          <UIFormField gap={24} direction='row'>
            <UIUnitGroup gap={8} direction='column'>
              <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                Port(개발)
              </UITypography>
              <UITextArea2 value={'3306'} placeholder='직접 입력' style={{ height: '80px' }} disabled={true} resizable={false} />
            </UIUnitGroup>
            <UIUnitGroup gap={8} direction='column'>
              <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                Port(운영)
              </UITypography>
              <UITextArea2 value={''} placeholder='운영망 정보가 없습니다. 확인 후 입력해주세요.' style={{ height: '80px' }} disabled={true} resizable={false} readOnly={true} />
            </UIUnitGroup>
          </UIFormField>
          <UIFormField gap={24} direction='row'>
            <UIUnitGroup gap={8} direction='column'>
              <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                User(개발)
              </UITypography>
              <UITextArea2 value={'admin'} placeholder='직접 입력' style={{ height: '80px' }} disabled={true} resizable={false} />
            </UIUnitGroup>
            <UIUnitGroup gap={8} direction='column'>
              <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                User(운영)
              </UITypography>
              <UITextArea2 value={''} placeholder='운영망 정보가 없습니다. 확인 후 입력해주세요.' style={{ height: '80px' }} disabled={true} resizable={false} readOnly={true} />
            </UIUnitGroup>
          </UIFormField>
          <UIFormField gap={24} direction='row'>
            <UIUnitGroup gap={8} direction='column'>
              <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                Password(개발)
              </UITypography>
              <UITextArea2 value={'shinhan000'} placeholder='직접 입력' style={{ height: '80px' }} disabled={true} resizable={false} />
            </UIUnitGroup>
            <UIUnitGroup gap={8} direction='column'>
              <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                Password(운영)
              </UITypography>
              <UITextArea2 value={''} placeholder='운영망 정보가 없습니다. 확인 후 입력해주세요.' style={{ height: '80px' }} disabled={true} resizable={false} readOnly={true} />
            </UIUnitGroup>
          </UIFormField>
          <UIFormField gap={24} direction='row'>
            <UIUnitGroup gap={8} direction='column'>
              <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                Database Name(개발)
              </UITypography>
              <UITextArea2 value={'Milvus'} placeholder='직접 입력' style={{ height: '80px' }} disabled={true} resizable={false} />
            </UIUnitGroup>
            <UIUnitGroup gap={8} direction='column'>
              <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                Database Name(운영)
              </UITypography>
              <UITextArea2 value={''} placeholder='운영망 정보가 없습니다. 확인 후 입력해주세요.' style={{ height: '80px' }} disabled={true} resizable={false} readOnly={true} />
            </UIUnitGroup>
          </UIFormField>
        </UIGroup>
      </>
    ),
    defaultOpen: false,
    showNoticeIcon: false,
  },
];

// 프롬프트 아코디언 데이터2
const createPromptAccordionItems2 = () => [
  {
    title: '모델 (Serverless)',
    titleSub: 'GIP/gpt-4o-mini',
    content: (
      <>
        <UIGroup direction='column' gap={16}>
          <UIFormField gap={24} direction='row'>
            <UIUnitGroup gap={8} direction='column'>
              <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                Endpoint(개발)
              </UITypography>
              <UITextArea2 value={'https://axplatform-ai-search-dev.search.window.net'} placeholder='직접 입력' style={{ height: '80px' }} disabled={true} resizable={false} />
            </UIUnitGroup>
            <UIUnitGroup gap={8} direction='column'>
              <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                Endpoint(운영)
              </UITypography>
              <UITextArea2
                value={'A1XPROD-SEARCH-KEY-83KF92ASD9QW23XM'}
                placeholder='운영망 정보가 없습니다. 확인 후 입력해주세요.'
                style={{ height: '80px' }}
                disabled={true}
                resizable={false}
                readOnly={true}
              />
            </UIUnitGroup>
          </UIFormField>
          <UIFormField gap={24} direction='row'>
            <UIUnitGroup gap={8} direction='column'>
              <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                Endpoint(개발)
              </UITypography>
              <UITextArea2 value={'https://axplatform-ai-search-dev.search.window.net'} placeholder='직접 입력' style={{ height: '80px' }} disabled={true} resizable={false} />
            </UIUnitGroup>
            <UIUnitGroup gap={8} direction='column'>
              <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                Endpoint(운영)
              </UITypography>
              <UITextArea2
                value={'A1XPROD-SEARCH-KEY-83KF92ASD9QW23XM'}
                placeholder='운영망 정보가 없습니다. 확인 후 입력해주세요.'
                style={{ height: '80px' }}
                disabled={true}
                resizable={false}
                readOnly={true}
              />
            </UIUnitGroup>
          </UIFormField>
        </UIGroup>
      </>
    ),
    defaultOpen: false,
    showNoticeIcon: false,
  },
];

// 프롬프트 아코디언 데이터3
const createPromptAccordionItems3 = () => [
  {
    title: 'Custom API ',
    titleSub: '기준금리 조회',
    content: (
      <>
        <UIGroup direction='column' gap={16}>
          <UIFormField gap={24} direction='row'>
            <UIUnitGroup gap={8} direction='column'>
              <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                URL(개발)
              </UITypography>
              <UITextArea2 value={'https://axplatform-ai-search-dev.search.window.net'} placeholder='직접 입력' style={{ height: '80px' }} disabled={true} resizable={false} />
            </UIUnitGroup>
            <UIUnitGroup gap={8} direction='column'>
              <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                URL(운영)
              </UITypography>
              <UITextArea2 value={''} placeholder='운영망 정보가 없습니다. 확인 후 입력해주세요.' style={{ height: '80px' }} resizable={false} />
            </UIUnitGroup>
          </UIFormField>
          <UIFormField gap={24} direction='row'>
            <UIUnitGroup gap={8} direction='column'>
              <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                헤더 파라미터 값(개발)
              </UITypography>
              <UIInput.Text value={'user1234'} placeholder='직접 입력' disabled={true} />
            </UIUnitGroup>
            <UIUnitGroup gap={8} direction='column'>
              <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                헤더 파라미터 값(운영)
              </UITypography>
              <UIInput.Text value={''} placeholder='운영망 정보가 없습니다. 확인 후 입력해주세요.' />
            </UIUnitGroup>
          </UIFormField>
        </UIGroup>
      </>
    ),
    defaultOpen: false,
    showNoticeIcon: false,
  },
  {
    title: 'Custom Code',
    titleSub: 'shinhancustom-text-preprocessor',
    content: (
      <>
        <UIGroup direction='column' gap={16}>
          <UIFormField gap={24} direction='row'>
            <UIUnitGroup gap={8} direction='column'>
              <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                Endpoint(개발)
              </UITypography>
              <UITextArea2 value={'https://axplatform-ai-search-dev.search.window.net'} placeholder='직접 입력' style={{ height: '80px' }} disabled={true} resizable={false} />
            </UIUnitGroup>
            <UIUnitGroup gap={8} direction='column'>
              <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                Endpoint(운영)
              </UITypography>
              <UITextArea2
                value={'A1XPROD-SEARCH-KEY-83KF92ASD9QW23XM'}
                placeholder='운영망 정보가 없습니다. 확인 후 입력해주세요.'
                style={{ height: '80px' }}
                disabled={true}
                resizable={false}
                readOnly={true}
              />
            </UIUnitGroup>
          </UIFormField>
          <UIFormField gap={24} direction='row'>
            <UIUnitGroup gap={8} direction='column'>
              <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                Endpoint(개발)
              </UITypography>
              <UITextArea2 value={'https://axplatform-ai-search-dev.search.window.net'} placeholder='직접 입력' style={{ height: '80px' }} disabled={true} resizable={false} />
            </UIUnitGroup>
            <UIUnitGroup gap={8} direction='column'>
              <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                Endpoint(운영)
              </UITypography>
              <UITextArea2
                value={'A1XPROD-SEARCH-KEY-83KF92ASD9QW23XM'}
                placeholder='운영망 정보가 없습니다. 확인 후 입력해주세요.'
                style={{ height: '80px' }}
                disabled={true}
                resizable={false}
                readOnly={true}
              />
            </UIUnitGroup>
          </UIFormField>
        </UIGroup>
      </>
    ),
    defaultOpen: false,
    showNoticeIcon: false,
  },
];

// 프롬프트 아코디언 데이터4
const createPromptAccordionItems4 = () => [
  {
    title: 'MCP 서버',
    titleSub: 'server123',
    content: (
      <>
        <UIGroup direction='column' gap={16}>
          <UIFormField gap={24} direction='row'>
            <UIUnitGroup gap={8} direction='column'>
              <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                URL(개발)
              </UITypography>
              <UITextArea2 value={'https://axplatform-ai-search-dev.search.window.net'} placeholder='직접 입력' style={{ height: '80px' }} disabled={true} resizable={false} />
            </UIUnitGroup>
            <UIUnitGroup gap={8} direction='column'>
              <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                URL(운영)
              </UITypography>
              <UITextArea2 value={''} placeholder='운영망 정보가 없습니다. 확인 후 입력해주세요.' style={{ height: '80px' }} resizable={false} />
            </UIUnitGroup>
          </UIFormField>
          <UIFormField gap={24} direction='row'>
            <UIUnitGroup gap={8} direction='column'>
              <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                Token(개발)
              </UITypography>
              <UIInput.Text value={'user1234'} placeholder='직접 입력' disabled={true} />{' '}
            </UIUnitGroup>
            <UIUnitGroup gap={8} direction='column'>
              <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                Token(운영)
              </UITypography>
              <UIInput.Text value={''} placeholder='운영망 정보가 없습니다. 확인 후 입력해주세요.' />
            </UIUnitGroup>
          </UIFormField>
        </UIGroup>
      </>
    ),
    defaultOpen: false,
    showNoticeIcon: false,
  },
];

// 프롬프트 아코디언 데이터5
const createPromptAccordionItems5 = () => [
  {
    title: 'Custom Code',
    titleSub: 'loan-eligibility-rule-engine',
    content: (
      <>
        <UIGroup direction='column' gap={16}>
          <UIFormField gap={24} direction='row'>
            <UIUnitGroup gap={8} direction='column'>
              <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                Endpoint(개발)
              </UITypography>
              <UITextArea2 value={'https://axplatform-ai-search-dev.search.window.net'} placeholder='직접 입력' style={{ height: '80px' }} disabled={true} resizable={false} />
            </UIUnitGroup>
            <UIUnitGroup gap={8} direction='column'>
              <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                Endpoint(운영)
              </UITypography>
              <UITextArea2
                value={'A1XPROD-SEARCH-KEY-83KF92ASD9QW23XM'}
                placeholder='운영망 정보가 없습니다. 확인 후 입력해주세요.'
                style={{ height: '80px' }}
                disabled={true}
                resizable={false}
                readOnly={true}
              />
            </UIUnitGroup>
          </UIFormField>
          <UIFormField gap={24} direction='row'>
            <UIUnitGroup gap={8} direction='column'>
              <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                Endpoint(개발)
              </UITypography>
              <UITextArea2 value={'https://axplatform-ai-search-dev.search.window.net'} placeholder='직접 입력' style={{ height: '80px' }} disabled={true} resizable={false} />
            </UIUnitGroup>
            <UIUnitGroup gap={8} direction='column'>
              <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                Endpoint(운영)
              </UITypography>
              <UITextArea2
                value={'A1XPROD-SEARCH-KEY-83KF92ASD9QW23XM'}
                placeholder='운영망 정보가 없습니다. 확인 후 입력해주세요.'
                style={{ height: '80px' }}
                disabled={true}
                resizable={false}
                readOnly={true}
              />
            </UIUnitGroup>
          </UIFormField>
        </UIGroup>
      </>
    ),
    defaultOpen: false,
    showNoticeIcon: false,
  },
];

export const DP_050101_P03 = () => {
  const [isPopupOpen, setIsPopupOpen] = useState(true);
  const [cpu, setCpu] = useState('');
  const [memory, setMemory] = useState('');
  const [gpu, setGpu] = useState('');

  const handleClose = () => {
    setIsPopupOpen(false);
  };

  return (
    <>
      <DesignLayout
        initialMenu={{ id: 'home', label: '홈' }}
        initialSubMenu={{
          id: 'home-ide',
          label: 'IDE',
          icon: 'ico-lnb-menu-20-home',
        }}
      >
        <div className='flex items-center justify-center h-full'>
          <div className='text-center'>
            <UITypography variant='title-1' className='secondary-neutral-800 text-sb'>
              홈
            </UITypography>
            <UITypography variant='body-1' className='secondary-neutral-600'>
              IDE 생성...
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
            <UIPopupHeader title='운영 배포' description='' position='left' />

            {/* 레이어 팝업 바디 */}
            <UIPopupBody>
              <UIArticle>
                <UIStepper currentStep={3} items={stepperItems} direction='vertical' />
              </UIArticle>
            </UIPopupBody>

            {/* 레이어 팝업 footer */}
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <UIButton2 className='btn-aside-gray'>취소</UIButton2>
                  <UIButton2 className='btn-aside-blue'>배포</UIButton2>
                </UIUnitGroup>
              </UIArticle>
            </UIPopupFooter>
          </UIPopupAside>
        }
      >
        <section className='section-popup-content'>
          {/* 레이어 팝업 헤더 */}
          <UIPopupHeader title='운영용 정보 입력' description='외부 엔드포인트 기반 에셋이 운영 환경에서 정상적으로 동작하기 위해 운영용 정보를 입력해주세요.' position='right' />

          {/* 레이어 팝업 바디 */}
          <UIPopupBody>
            <UIArticle>
              <div className='box-fill'>
                <UIUnitGroup gap={8} direction='column' align='start'>
                  <UIList
                    gap={4}
                    direction='column'
                    className='ui-list_bullet'
                    data={[
                      {
                        dataItem: (
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            {`선배포 이력이 있는 경우, 자동 입력된 운영 정보를 확인하실 수 있습니다. 필요한 경우 수정해주세요.`}
                          </UITypography>
                        ),
                      },
                    ]}
                  />
                  <UIList
                    gap={4}
                    direction='column'
                    className='ui-list_bullet'
                    data={[
                      {
                        dataItem: (
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            {`입력한 값은 개발용 설정을 운영용으로 대체하여 최종 파일 생성에 반영됩니다.`}
                          </UITypography>
                        ),
                      },
                    ]}
                  />
                </UIUnitGroup>
              </div>
            </UIArticle>
            <UIArticle>
              <div className='article-header'>
                <UITypography variant='title-4' className='secondary-neutral-900 text-sb'>
                  배포 정보
                </UITypography>
              </div>
              <div className='article-body'>
                <div className='border-t border-black'>
                  <table className='tbl-v'>
                    <colgroup>
                      <col style={{ width: '10%' }} />
                      <col style={{ width: '40%' }} />
                      <col style={{ width: '10%' }} />
                      <col style={{ width: '40%' }} />
                    </colgroup>
                    <tbody>
                      <tr>
                        <th>
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            프로젝트명
                          </UITypography>
                        </th>
                        <td>
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            Public
                          </UITypography>
                        </td>
                        <th>
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            분류
                          </UITypography>
                        </th>
                        <td>
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            벡터 DB
                          </UITypography>
                        </td>
                      </tr>
                      <tr>
                        <th>
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            벡터 DB명
                          </UITypography>
                        </th>
                        <td colSpan={3}>
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            [AzureAISearch] axplatform-ai-search-dev
                            <br />
                            TestVector
                          </UITypography>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>
            </UIArticle>

            <UIArticle>
              <div className='article-header'>
                <UITypography variant='title-4' className='secondary-neutral-900'>
                  벡터 DB
                </UITypography>
              </div>
              <div className='article-body'>
                <UIAccordion items={createPromptAccordionItems()} variant='box' allowMultiple={true} />
              </div>
            </UIArticle>

            <UIArticle>
              <div className='article-header'>
                <UITypography variant='title-4' className='secondary-neutral-900'>
                  모델
                </UITypography>
              </div>
              <div className='article-body'>
                <UIAccordion items={createPromptAccordionItems2()} variant='box' allowMultiple={true} />
              </div>
            </UIArticle>

            {/* 퍼블 추가 S */}
            <UIArticle>
              <div className='article-header'>
                <UIGroup direction='column' gap={8}>
                  <UITypography variant='title-3' className='secondary-neutral-900'>
                    자원 할당
                  </UITypography>
                  <UITypography variant='title-4' className='secondary-neutral-900' required={true}>
                    리소스 그룹
                  </UITypography>
                </UIGroup>
              </div>
              <div className='article-body'>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <UIFormField gap={8} direction='column'>
                    <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                      CPU(Core)
                    </UITypography>
                    <UIInput.Text value={cpu} placeholder='숫자 입력' onChange={e => setCpu(e.target.value)} readOnly={false} />
                  </UIFormField>
                  <UIFormField gap={8} direction='column'>
                    <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                      Memory(GiB)
                    </UITypography>
                    <UIInput.Text value={memory} placeholder='숫자 입력' onChange={e => setMemory(e.target.value)} readOnly={false} />
                  </UIFormField>
                  {/* 참고 : GPU(fGPU) 영역만 미사용시 삭제 */}
                  <UIFormField gap={8} direction='column'>
                    <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                      GPU(fGPU)
                    </UITypography>
                    <UIInput.Text value={gpu} placeholder='숫자 입력' onChange={e => setGpu(e.target.value)} readOnly={false} />
                  </UIFormField>
                </UIUnitGroup>
              </div>
            </UIArticle>
            {/* 퍼블 추가 E */}

            <UIArticle>
              <div className='article-header'>
                <UITypography variant='title-4' className='secondary-neutral-900'>
                  Tools
                </UITypography>
              </div>
              <div className='article-body'>
                <UIAccordion items={createPromptAccordionItems3()} variant='box' allowMultiple={true} />
              </div>
            </UIArticle>

            <UIArticle>
              <div className='article-header'>
                <UITypography variant='title-4' className='secondary-neutral-900'>
                  MCP
                </UITypography>
              </div>
              <div className='article-body'>
                <UIAccordion items={createPromptAccordionItems4()} variant='box' allowMultiple={true} />
              </div>
            </UIArticle>

            <UIArticle>
              <div className='article-header'>
                <UITypography variant='title-4' className='secondary-neutral-900'>
                  그래프
                </UITypography>
              </div>
              <div className='article-body'>
                <UIAccordion items={createPromptAccordionItems5()} variant='box' allowMultiple={true} />
              </div>
            </UIArticle>

            <UIArticle>
              <div className='article-header'>
                <UITypography variant='title-4' className='secondary-neutral-900'>
                  파일 정보
                </UITypography>
              </div>
              <div className='article-body'>
                <div className='border-t border-black'>
                  <table className='tbl-v'>
                    <colgroup>
                      <col style={{ width: '10%' }} />
                      <col style={{ width: '40%' }} />
                      <col style={{ width: '10%' }} />
                      <col style={{ width: '40%' }} />
                    </colgroup>
                    <tbody>
                      <tr>
                        <th>
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            최종 파일명
                          </UITypography>
                        </th>
                        <td>
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            loan-product-agent_smartbot_20250117_prod.json
                          </UITypography>
                        </td>
                        <th>
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            파일 경로
                          </UITypography>
                        </th>
                        <td>
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            /config/prod/agents/smartbot/loan-product-
                            <br />
                            agent_smartbot_20250117_prod.json
                          </UITypography>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>
            </UIArticle>
          </UIPopupBody>

          {/* 레이어 팝업 footer */}
          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} align='start'>
                <UIButton2 className='btn-secondary-gray'>이전</UIButton2>
                <UIButton2 className='btn-secondary-blue' disabled={true}>
                  다음
                </UIButton2>
              </UIUnitGroup>
            </UIArticle>
          </UIPopupFooter>
        </section>
      </UILayerPopup>
    </>
  );
};
