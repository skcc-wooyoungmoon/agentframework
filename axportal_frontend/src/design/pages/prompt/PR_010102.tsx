import React, { useMemo, useState } from 'react';

import { UIArticle } from '@/components/UI/molecules';

import { UIButton2 } from '../../../components/UI/atoms/UIButton2';
import { UITextLabel } from '../../../components/UI/atoms/UITextLabel';
import { UIPagination } from '../../../components/UI/atoms/UIPagination';
import { UIToggle } from '../../../components/UI/atoms/UIToggle';
import { UITypography } from '../../../components/UI/atoms/UITypography';
import { UIGrid } from '../../../components/UI/molecules/grid/UIGrid/component';
import { UIListContainer } from '../../../components/UI/molecules/list/UIListContainer/component';
import { UIListContentBox } from '../../../components/UI/molecules/list/UIListContentBox';
import { UIAccordion } from '../../../components/UI/molecules/UIAccordion';
import { UIFormField } from '../../../components/UI/molecules/UIFormField';
import { UIPageHeader } from '../../../components/UI/molecules/UIPageHeader';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIUnitGroup } from '../../../components/UI/molecules/UIUnitGroup';
import { UITextArea2 } from '@/components/UI/molecules/input';

import { DesignLayout } from '../../components/DesignLayout';

import type { ColDef } from 'ag-grid-community';
import { UIDataCnt } from '@/components/UI';
import { UIVersionCard } from '@/components/UI/molecules/UIVersionCard';

export const PR_010102 = () => {
  // 버전 데이터 카드
  const versionData = [
    {
      version: 'ver.3',
      date: '2025.03.19 15:29:31',
      tags: [
        { label: 'Latest.1', intent: 'gray' as const },
        { label: 'Latest.1', intent: 'blue' as const },
      ],
      isActive: true, // active
    },
    {
      version: 'ver.2',
      date: '2025.03.19 15:29:31',
      tags: [
        { label: 'Latest.1', intent: 'gray' as const },
        { label: 'Latest.1', intent: 'blue' as const },
      ],
    },
    {
      version: 'ver.2',
      date: '2025.03.19 15:29:31',
      tags: [
        { label: 'Latest.1', intent: 'gray' as const },
        { label: 'Latest.1', intent: 'blue' as const },
      ],
    },
    // {
    //   version: 'ver.3',
    //   date: '2025.03.19 15:29:31',
    //   tags: [
    //     { label: 'Latest.1', intent: 'gray' as const },
    //     { label: 'Latest.1', intent: 'blue' as const },
    //   ],
    // },
    // {
    //   version: 'ver.3',
    //   date: '2025.03.19 15:29:31',
    //   tags: [
    //     { label: 'Latest.1', intent: 'gray' as const },
    //     { label: 'Latest.1', intent: 'blue' as const },
    //   ],
    // },
    // {
    //   version: 'ver.3',
    //   date: '2025.03.19 15:29:31',
    //   tags: [
    //     { label: 'Latest.1', intent: 'gray' as const },
    //     { label: 'Latest.1', intent: 'blue' as const },
    //   ],
    // },
    // {
    //   version: 'ver.3',
    //   date: '2025.03.19 15:29:31',
    //   tags: [
    //     { label: 'Latest.1', intent: 'gray' as const },
    //     { label: 'Latest.1', intent: 'blue' as const },
    //   ],
    // },
    // {
    //   version: 'ver.3',
    //   date: '2025.03.19 15:29:31',
    //   tags: [
    //     { label: 'Latest.1', intent: 'gray' as const },
    //     { label: 'Latest.1', intent: 'blue' as const },
    //   ],
    // },
    // {
    //   version: 'ver.3',
    //   date: '2025.03.19 15:29:31',
    //   tags: [
    //     { label: 'Latest.1', intent: 'gray' as const },
    //     { label: 'Latest.1', intent: 'blue' as const },
    //   ],
    // },
    // {
    //   version: 'ver.3',
    //   date: '2025.03.19 15:29:31',
    //   tags: [
    //     { label: 'Latest.1', intent: 'gray' as const },
    //     { label: 'Latest.1', intent: 'blue' as const },
    //   ],
    // },
    // {
    //   version: 'ver.3',
    //   date: '2025.03.19 15:29:31',
    //   tags: [
    //     { label: 'Latest.1', intent: 'gray' as const },
    //     { label: 'Latest.1', intent: 'blue' as const },
    //   ],
    // },
    // {
    //   version: 'ver.3',
    //   date: '2025.03.19 15:29:31',
    //   tags: [
    //     { label: 'Latest.1', intent: 'gray' as const },
    //     { label: 'Latest.1', intent: 'blue' as const },
    //   ],
    // },
    // {
    //   version: 'ver.3',
    //   date: '2025.03.19 15:29:31',
    //   tags: [
    //     { label: 'Latest.1', intent: 'gray' as const },
    //     { label: 'Latest.1', intent: 'blue' as const },
    //   ],
    // },
    // {
    //   version: 'ver.3',
    //   date: '2025.03.19 15:29:31',
    //   tags: [
    //     { label: 'Latest.1', intent: 'gray' as const },
    //     { label: 'Latest.1', intent: 'blue' as const },
    //   ],
    // },
  ];

  // 연결된 에이전트 목록
  const agentData = [
    {
      id: '1',
      no: 1,
      name: 'AI 챗봇 에이전트',
      description: '고객 서비스를 위한 자동 응답 챗봇 에이전트입니다.',
      isDeployed: '배포됨',
      publicRange: '전체공유',
      createdDate: '2025.03.24 18:23:43',
      modifiedDate: '2025.06.23 18:23:43',
    },
    {
      id: '2',
      no: 2,
      name: '데이터 분석 에이전트',
      description: '대용량 데이터 분석 및 리포트 생성을 담당하는 에이전트입니다.',
      isDeployed: '미배포',
      publicRange: '전체공유',
      createdDate: '2025.03.20 14:15:22',
      modifiedDate: '2025.06.20 09:30:15',
    },
  ];

  // 그리드 컬럼 정의
  const columnDefs: ColDef[] = useMemo(
    () => [
      {
        headerName: 'NO',
        field: 'no',
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
        },
        sortable: false,
        suppressHeaderMenuButton: true,
        suppressSizeToFit: true,
      },
      // 251113_퍼블수정 그리드 컬럼 속성 '이름' 영역 수정 S
      {
        headerName: '이름',
        field: 'name',
        width: 250,
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
      // 251113_퍼블수정 그리드 컬럼 속성 '이름' 영역 수정 E
      {
        headerName: '설명',
        field: 'description',
        minWidth: 270,
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
        headerName: '배포 여부',
        field: 'isDeployed',
        width: 131,
      },
      {
        headerName: '공개범위',
        field: 'publicRange',
        width: 131,
      },
      {
        headerName: '생성일시',
        field: 'createdDate',
        width: 180,
      },
      {
        headerName: '최종 수정일시',
        field: 'modifiedDate',
        width: 180,
      },
    ],
    []
  );

  // 프롬프트 아코디언 데이터
  const promptAccordionItems = [
    {
      title: '시스템 프롬프트',
      content:
        '**목표**\n당신은 내부 문서를 참고해 질문에 답하는 AI 비서입니다. 답변 하단에 반드시 Document의 metadata 정보로 출처를 표기하십시오.\n질문과 관련없는 부연설명을 생성하지 마십시오.',
      defaultOpen: false,
      showNoticeIcon: false,
    },
    {
      title: '유저 프롬프트',
      content: 'Thought: {{agent_scratchpad}}\n**참고자료**\n{{contest}} Thought: {{agent_scratchpad}}\n**참고자료**\n{{contest}}',
      defaultOpen: false,
      showNoticeIcon: false,
    },
  ];

  // textarea 타입
  const [textareaValue, setTextareaValue] = useState('');

  // 변수 속성 설정 상태
  const [variableEnabled, setVariableEnabled] = useState(false);
  const [tokenLimitEnabled, setTokenLimitEnabled] = useState(false);

  return (
    <DesignLayout
      initialMenu={{ id: 'prompt', label: '프롬프트' }}
      initialSubMenu={{
        id: 'inference-prompt',
        label: '추론 프롬프트',
        icon: 'ico-lnb-menu-20-inference-prompt',
      }}
    >
      {/* 섹션 페이지 */}
      <section className='section-page'>
        {/* 페이지 헤더 */}
        <UIPageHeader
          title='추론 프롬프트 조회'
          description=''
          actions={
            <>
              <UIButton2 className='btn-model-detail' onClick={() => {}}>
                릴리즈
              </UIButton2>
            </>
          }
        />

        {/* 페이지 바디 */}
        <UIPageBody>
          <div className='grid-layout'>
            {/* 왼쪽 영역 */}
            <div className='grid-article'>
              {/* 기본 정보 섹션 */}
              <UIArticle>
                <div className='article-header'>
                  <UITypography variant='title-4' className='secondary-neutral-900'>
                    기본 정보
                  </UITypography>
                </div>
                <div className='article-body'>
                  <div className='border-t border-black'>
                    <table className='tbl-v'>
                      {/* [251106_퍼블수정] width값 수정 */}
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
                              이름
                            </UITypography>
                          </th>
                          <td>
                            <UITypography variant='body-2' className='secondary-neutral-600'>
                              AI Chatbot
                            </UITypography>
                          </td>
                          <th>
                            <UITypography variant='body-2' className='secondary-neutral-900'>
                              버전
                            </UITypography>
                          </th>
                          <td>
                            <div className='flex gap-2'>
                              {/* [251111_퍼블수정] : UIUnitGroup 컴포넌트로 그룹핑 */}
                              <UIUnitGroup gap={8} direction='row' align='start'>
                                <UITextLabel intent='blue'>Release Ver.1</UITextLabel>
                                <UITextLabel intent='gray'>Lastest.1</UITextLabel>
                              </UIUnitGroup>
                            </div>
                          </td>
                        </tr>
                        <tr>
                          <th>
                            <UITypography variant='body-2' className='secondary-neutral-900'>
                              유형
                            </UITypography>
                          </th>
                          <td colSpan={3}>
                            <UITypography variant='body-2' className='secondary-neutral-600'>
                              채팅
                            </UITypography>
                          </td>
                        </tr>
                        <tr>
                          <th>
                            <UITypography variant='body-2' className='secondary-neutral-900'>
                              태그
                            </UITypography>
                          </th>
                          <td colSpan={3}>
                            {/* [251111_퍼블수정] : UIUnitGroup 컴포넌트로 그룹핑 */}
                            <UIUnitGroup gap={8} direction='row' align='start'>
                              <UITextLabel intent='tag'>태그</UITextLabel>
                            </UIUnitGroup>
                          </td>
                        </tr>
                      </tbody>
                    </table>
                  </div>
                </div>
              </UIArticle>

              {/* 프롬프트 섹션 */}
              <UIArticle>
                <div className='article-header'>
                  <UITypography variant='title-4' className='secondary-neutral-900'>
                    프롬프트
                  </UITypography>
                </div>
                <div className='article-body'>
                  <UIAccordion items={promptAccordionItems} variant='box' allowMultiple={true} />
                </div>
              </UIArticle>

              {/* 변수 속성 설정 섹션 */}
              <UIArticle>
                <div className='article-header'>
                  <UITypography variant='title-4' className='secondary-neutral-800 text-sb'>
                    변수 속성 설정
                  </UITypography>
                </div>
                <div className='article-body'>
                  <UIFormField gap={8} direction='column'>
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
                                placeholder='정규표현식 입력' // [251104_퍼블수정] : '변수 입력' > '정규표현식 입력'
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
                </div>
              </UIArticle>

              {/* 연결된 에이전트 섹션 */}
              <UIArticle className='article-grid'>
                <UIListContainer>
                  <UIListContentBox.Header>
                    <div style={{ width: '168px', paddingRight: '8px' }}>
                      <UIDataCnt count={agentData.length} prefix=' 연결된 에이전트 총' unit='건' />
                    </div>
                  </UIListContentBox.Header>
                  <UIListContentBox.Body>
                    <UIGrid
                      type='default'
                      rowData={agentData}
                      columnDefs={columnDefs}
                      onClickRow={(_params: any) => {}}
                    />
                  </UIListContentBox.Body>
                  <UIListContentBox.Footer>
                    <UIPagination
                      currentPage={1}
                      totalPages={1}
                      onPageChange={(_page: number) => {
                      }}
                      className='flex justify-center'
                    />
                  </UIListContentBox.Footer>
                </UIListContainer>
              </UIArticle>

              {/* 담당자 정보 섹션 */}
              <UIArticle>
                <div className='article-header'>
                  <UITypography variant='title-4' className='secondary-neutral-900'>
                    담당자 정보
                  </UITypography>
                </div>
                <div className='article-body'>
                  <div className='border-t border-black'>
                    <table className='tbl-v'>
                      {/* [251106_퍼블수정] width값 수정 */}
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
                              생성자
                            </UITypography>
                          </th>
                          <td>
                            <UITypography variant='body-2' className='secondary-neutral-600'>
                              김신한 | AI Unit
                            </UITypography>
                          </td>
                          <th>
                            <UITypography variant='body-2' className='secondary-neutral-900'>
                              생성일시
                            </UITypography>
                          </th>
                          <td>
                            <UITypography variant='body-2' className='secondary-neutral-600'>
                              2025.03.24 18:23:43
                            </UITypography>
                          </td>
                        </tr>
                        <tr>
                          <th>
                            <UITypography variant='body-2' className='secondary-neutral-900'>
                              최종 수정자
                            </UITypography>
                          </th>
                          <td>
                            <UITypography variant='body-2' className='secondary-neutral-600'>
                              김신한 | AI Unit
                            </UITypography>
                          </td>
                          <th>
                            <UITypography variant='body-2' className='secondary-neutral-900'>
                              최종 수정일시
                            </UITypography>
                          </th>
                          <td>
                            <UITypography variant='body-2' className='secondary-neutral-600'>
                              2025.06.23 18:23:43
                            </UITypography>
                          </td>
                        </tr>
                      </tbody>
                    </table>
                  </div>
                </div>
              </UIArticle>

              {/* 프로젝트 정보 섹션 */}
              <UIArticle>
                <div className='article-header'>
                  <UIUnitGroup direction='row' align='space-between' gap={0}>
                    <UITypography variant='title-4' className='secondary-neutral-900'>
                      프로젝트 정보
                    </UITypography>
                    <UIButton2 className='btn-option-outlined'>공개설정</UIButton2>
                  </UIUnitGroup>
                </div>
                <div className='article-body'>
                  <div className='border-t border-black'>
                    <table className='tbl-v'>
                      {/* [251106_퍼블수정] width값 수정 */}
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
                              공개범위
                            </UITypography>
                          </th>
                          <td>
                            <UITypography variant='body-2' className='secondary-neutral-600'>
                              전체공유 | Public
                            </UITypography>
                          </td>
                          <th>
                            <UITypography variant='body-2' className='secondary-neutral-900'>
                              권한 수정자
                            </UITypography>
                          </th>
                          <td>
                            <UITypography variant='body-2' className='secondary-neutral-600'>
                              권신태 | AI Unit
                            </UITypography>
                          </td>
                        </tr>
                      </tbody>
                    </table>
                  </div>
                </div>
              </UIArticle>
              <div className='article-buton-group'>
                <UIUnitGroup gap={8} direction='row' align='center'>
                  <UIButton2 className='btn-primary-gray'>취소</UIButton2>
                  <UIButton2 className='btn-primary-blue'>확인</UIButton2>
                </UIUnitGroup>
              </div>
            </div>

            {/* 오른쪽 영역 */}
            <div className='grid-right-sticky'>
              <UIVersionCard versions={versionData} />
            </div>
          </div>
        </UIPageBody>
      </section>
    </DesignLayout>
  );
};
