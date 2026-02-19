import React, { useMemo, useState } from 'react';

import { UIButton2, UIDataCnt, UIPagination, UITypography } from '@/components/UI/atoms';
import { UIArticle, UIPopupBody, UIPopupFooter, UIPopupHeader, UIStepper, UIUnitGroup, UIFormField, UIGroup, UITextArea2, UIInput } from '@/components/UI/molecules';
import { UIAccordion } from '@/components/UI/molecules';
import { UIGrid, UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { DesignLayout } from '../../components/DesignLayout';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';

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
    title: '벡터 DB',
    titleSub: 'TestVector',
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
  {
    title: '임베딩 모델 (Serverless)',
    titleSub: 'bge-m3',
    actionButton: <UIButton2 className='btn-option-outlined'>상세 조회</UIButton2>,
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

// 프롬프트 아코디언 데이터2
const createPromptAccordionItems2 = () => [
  {
    title: '모델 (Serverless)',
    titleSub: 'GIP/gpt-4o-mini',
    actionButton: <UIButton2 className='btn-option-outlined'>상세 조회</UIButton2>,
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
              <UITextArea2 value={''} placeholder='직접 입력' style={{ height: '80px' }} resizable={false} />
            </UIUnitGroup>
          </UIFormField>
          <UIFormField gap={24} direction='row'>
            <UIUnitGroup gap={8} direction='column'>
              <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                Token(개발)
              </UITypography>
              <UIInput.Text value={'sfff1'} placeholder='직접 입력' disabled={true} />
            </UIUnitGroup>
            <UIUnitGroup gap={8} direction='column'>
              <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                Token(운영)
              </UITypography>
              <UIInput.Text value={''} placeholder='직접 입력' />
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
                Username(개발)
              </UITypography>
              <UIInput.Text value={'user1234'} placeholder='직접 입력' disabled={true} />{' '}
            </UIUnitGroup>
            <UIUnitGroup gap={8} direction='column'>
              <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                Username(운영)
              </UITypography>
              <UIInput.Text value={''} placeholder='운영망 정보가 없습니다. 확인 후 입력해주세요.' />
            </UIUnitGroup>
          </UIFormField>
          <UIFormField gap={24} direction='row'>
            <UIUnitGroup gap={8} direction='column'>
              <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                Username(개발)
              </UITypography>
              <UIInput.Text value={'asmdifnk-00'} placeholder='직접 입력' disabled={true} />
            </UIUnitGroup>
            <UIUnitGroup gap={8} direction='column'>
              <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                Password(운영)
              </UITypography>
              <UIInput.Password placeholder='운영망 정보가 없습니다. 확인 후 입력해주세요.' />
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

export const DP_050101_P04 = () => {
  const [isPopupOpen, setIsPopupOpen] = useState(true);

  const handleClose = () => {
    setIsPopupOpen(false);
  };

  // 샘플 데이터
  const rowData = [
    {
      id: '1',
      name: '금융보안',
      description: '비밀번호, 인증번호, CVV, 계좌이체, 송금, 가상계좌, 공인인증서, 전자서명, OTP, 금융비밀',
      publicStatus: '내부공유',
      createdDate: '2025.03.24 18:32:43',
      modifiedDate: '2025.03.24 18:32:43',
    },
    {
      id: '2',
      name: '개인정보',
      description: '주민등록번호, 여권번호, 운전면허번호, 010--, 이메일주소, 집주소, 계좌번호, 카드번호, 보안카드, OTP번호',
      publicStatus: '내부공유',
      createdDate: '2025.03.24 18:32:43',
      modifiedDate: '2025.03.24 18:32:43',
    },
  ];

  // 그리드 컬럼 정의 (가드레일)
  const columnDefs: any = useMemo(
    () => [
      {
        headerName: 'NO',
        field: 'id' as any,
        width: 56,
        minWidth: 56,
        maxWidth: 56,
        cellClass: 'text-center',
        headerClass: 'text-center',
        cellStyle: {
          textAlign: 'center',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
        } as any,
        sortable: false,
        suppressHeaderMenuButton: true,
        suppressSizeToFit: true,
      },
      {
        headerName: '분류',
        field: 'name' as any,
        width: 272,
        cellRenderer: React.memo((params: any) => {
          return (
            <div
              style={{
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                whiteSpace: 'nowrap',
              }}
            >
              {params.value}
            </div>
          );
        }),
      },
      {
        headerName: '금지어',
        field: 'description',
        minWidth: 712,
        flex: 1,
        showTooltip: true,
        cellRenderer: React.memo((params: any) => {
          return (
            <div
              style={{
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                whiteSpace: 'nowrap',
              }}
            >
              {params.value}
            </div>
          );
        }),
      },
      {
        headerName: '공개범위',
        field: 'publicStatus',
        width: 120,
      },
      {
        headerName: '생성일시',
        field: 'createdDate' as any,
        width: 180,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: '최종 수정일시',
        field: 'modifiedDate' as any,
        width: 180,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
    ],
    [rowData]
  );

  // 샘플 데이터 (세이프티 필터)
  const rowData1 = [
    {
      id: '1',
      name: '금융보안',
      description: '금융사기와 연관된 질문이나 답변을 차단합니다.',
      publicStatus: '내부공유',
      createdDate: '2025.03.24 18:32:43',
      modifiedDate: '2025.03.24 18:32:43',
    },
    {
      id: '2',
      name: '개인정보',
      description: '금융사기와 연관된 질문이나 답변을 차단합니다.',
      publicStatus: '내부공유',
      createdDate: '2025.03.24 18:32:43',
      modifiedDate: '2025.03.24 18:32:43',
    },
  ];

  // 그리드 컬럼 정의 (세이프티 필터)
  const columnDefs1: any = useMemo(
    () => [
      {
        headerName: 'NO',
        field: 'id' as any,
        width: 56,
        minWidth: 56,
        maxWidth: 56,
        cellClass: 'text-center',
        headerClass: 'text-center',
        cellStyle: {
          textAlign: 'center',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
        } as any,
        sortable: false,
        suppressHeaderMenuButton: true,
        suppressSizeToFit: true,
      },
      {
        headerName: '분류',
        field: 'name' as any,
        width: 272,
        cellRenderer: React.memo((params: any) => {
          return (
            <div
              style={{
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                whiteSpace: 'nowrap',
              }}
            >
              {params.value}
            </div>
          );
        }),
      },
      {
        headerName: '설명',
        field: 'description',
        minWidth: 712,
        flex: 1,
        showTooltip: true,
        cellRenderer: React.memo((params: any) => {
          return (
            <div
              style={{
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                whiteSpace: 'nowrap',
              }}
            >
              {params.value}
            </div>
          );
        }),
      },
      {
        headerName: '기본설정',
        field: 'publicStatus',
        width: 120,
      },
      {
        headerName: '생성일시',
        field: 'createdDate' as any,
        width: 180,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: '최종 수정일시',
        field: 'modifiedDate' as any,
        width: 180,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
    ],
    [rowData1]
  );

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
                <UIStepper currentStep={4} items={stepperItems} direction='vertical' />
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
          <UIPopupHeader title='최종 정보 확인' description='이행할 대상들의 최종 정보를 확인 후, 배포 버튼을 클릭해주세요.' position='right' />

          {/* 레이어 팝업 바디 */}
          <UIPopupBody>
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
                            대출 상품 추천
                          </UITypography>
                        </td>
                        <th>
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            분류
                          </UITypography>
                        </th>
                        <td>
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            지식
                          </UITypography>
                        </td>
                      </tr>
                      <tr>
                        <th>
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            지식명
                          </UITypography>
                        </th>
                        <td colSpan={3}>
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            예적금 상품 Q&A 세트
                          </UITypography>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>
            </UIArticle>

            <UIArticle className='article-grid'>
              <UIListContainer>
                <UIListContentBox.Header>
                  <div className='flex-shrink-0'>
                    <UIGroup gap={8} direction='row' align='start'>
                      <div style={{ width: '168px', paddingRight: '8px' }}>
                        <UIDataCnt count={99} prefix='가드레일 필터 총' unit='건' />
                      </div>
                    </UIGroup>
                  </div>
                </UIListContentBox.Header>
                <UIListContentBox.Body>
                  <UIGrid type='default' rowData={rowData1} columnDefs={columnDefs1} onClickRow={(_params: any) => {}} onCheck={(_selectedIds: any[]) => {}} />
                </UIListContentBox.Body>
                <UIListContentBox.Footer>
                  <UIPagination currentPage={1} totalPages={10} onPageChange={() => {}} className='flex justify-center' />
                </UIListContentBox.Footer>
              </UIListContainer>
            </UIArticle>

            <UIArticle className='article-grid'>
              <UIListContainer>
                <UIListContentBox.Header>
                  <div className='flex-shrink-0'>
                    <UIGroup gap={8} direction='row' align='start'>
                      <div style={{ width: '168px', paddingRight: '8px' }}>
                        <UIDataCnt count={99} prefix='세이프티 필터 총' unit='건' />
                      </div>
                    </UIGroup>
                  </div>
                </UIListContentBox.Header>
                <UIListContentBox.Body>
                  <UIGrid type='default' rowData={rowData} columnDefs={columnDefs} onClickRow={(_params: any) => {}} onCheck={(_selectedIds: any[]) => {}} />
                </UIListContentBox.Body>
                <UIListContentBox.Footer>
                  <UIPagination currentPage={1} totalPages={10} onPageChange={() => {}} className='flex justify-center' />
                </UIListContentBox.Footer>
              </UIListContainer>
            </UIArticle>

            <UIArticle>
              <div className='article-header'>
                <UITypography variant='title-4' className='secondary-neutral-900'>
                  지식
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
                {/* <UIButton2 className='btn-secondary-blue' disabled={true}>
                  다음
                </UIButton2> */}
              </UIUnitGroup>
            </UIArticle>
          </UIPopupFooter>
        </section>
      </UILayerPopup>
    </>
  );
};
