import React, { useMemo, useState } from 'react';

import { UIArticle, UIFormField } from '@/components/UI/molecules';

import { UIButton2 } from '../../../components/UI/atoms/UIButton2';
import { UITextLabel } from '../../../components/UI/atoms/UITextLabel';
import { UIPagination } from '../../../components/UI/atoms/UIPagination';
import { UITypography } from '../../../components/UI/atoms/UITypography';
import { UIGrid } from '../../../components/UI/molecules/grid/UIGrid/component';
import { UIListContainer } from '../../../components/UI/molecules/list/UIListContainer/component';
import { UIListContentBox } from '../../../components/UI/molecules/list/UIListContentBox';
import { UIPageBody } from '../../../components/UI/molecules/UIPageBody';
import { UIPageHeader } from '../../../components/UI/molecules/UIPageHeader';
import { UIUnitGroup } from '../../../components/UI/molecules/UIUnitGroup';
import { UIQnaFewshot } from '../../../components/UI/organisms';
import { DesignLayout } from '../../components/DesignLayout';
import { UIDataCnt } from '@/components/UI';
import type { ColDef } from 'ag-grid-community';
import { UIVersionCard } from '@/components/UI/molecules/UIVersionCard';

export const PR_020102 = () => {
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
    {
      version: 'ver.3',
      date: '2025.03.19 15:29:31',
      tags: [
        { label: 'Latest.1', intent: 'gray' as const },
        { label: 'Latest.1', intent: 'blue' as const },
      ],
    },

    {
      version: 'ver.4',
      date: '2025.03.19 15:29:31',
      tags: [
        { label: 'Latest.1', intent: 'gray' as const },
        { label: 'Latest.1', intent: 'blue' as const },
      ],
    },
    {
      version: 'ver.5',
      date: '2025.03.19 15:29:31',
      tags: [
        { label: 'Latest.1', intent: 'gray' as const },
        { label: 'Latest.1', intent: 'blue' as const },
      ],
    },
    {
      version: 'ver.6',
      date: '2025.03.19 15:29:31',
      tags: [
        { label: 'Latest.1', intent: 'gray' as const },
        { label: 'Latest.1', intent: 'blue' as const },
      ],
    },
    {
      version: 'ver.7',
      date: '2025.03.19 15:29:31',
      tags: [
        { label: 'Latest.1', intent: 'gray' as const },
        { label: 'Latest.1', intent: 'blue' as const },
      ],
    },
    {
      version: 'ver.8',
      date: '2025.03.19 15:29:31',
      tags: [
        { label: 'Latest.1', intent: 'gray' as const },
        { label: 'Latest.1', intent: 'blue' as const },
      ],
    },
  ];

  // 데이터
  const projectData = [
    {
      id: '1',
      no: 1,
      name: '부정',
      version: 'Release Ver.1',
      tag: '태그는',
    },
    {
      id: '2',
      no: 2,
      name: '긍정',
      version: 'Release Ver.1',
      tag: '최대8글자',
    },
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
      // 251113_퍼블수정 그리드 컬럼 속성 '이름' 영역 수정 E
      {
        headerName: '설명',
        field: 'description',
        minWidth: 378,
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
        width: 120,
      },
      {
        headerName: '공개범위',
        field: 'publicRange',
        width: 131,
      },
      {
        headerName: '생성일',
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

  // QnA 데이터
  const [qnaPairs, setQnaPairs] = useState([
    {
      id: 'qna-1',
      question: '',
      answer: '',
    },
  ]);

  // QnA 핸들러 함수들
  const handleQuestionChange = (id: string, question: string) => {
    setQnaPairs(prev => prev.map(pair => (pair.id === id ? { ...pair, question } : pair)));
  };

  const handleAnswerChange = (id: string, answer: string) => {
    setQnaPairs(prev => prev.map(pair => (pair.id === id ? { ...pair, answer } : pair)));
  };

  const handleAddQna = () => {
  };

  return (
    <DesignLayout
      initialMenu={{ id: 'prompt', label: '프롬프트' }}
      initialSubMenu={{
        id: 'fewshot',
        label: '퓨샷',
        icon: 'ic-lnb-menu-20-fewshot',
      }}
    >
      {/* 섹션 페이지 */}
      <section className='section-page'>
        {/* 페이지 헤더 */}
        <UIPageHeader
          title='퓨샷 조회'
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
                        <tr className='border-b border-gray-200'>
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
                              <UITextLabel intent='blue'>Release Ver.1</UITextLabel>
                              <UITextLabel intent='gray'>Lastest.1</UITextLabel>
                            </div>
                          </td>
                        </tr>
                        <tr className='border-b border-gray-200'>
                          <th>
                            <UITypography variant='body-2' className='secondary-neutral-900'>
                              태그
                            </UITypography>
                          </th>
                          <td colSpan={3}>
                            <UITextLabel intent='tag'>agent_generator</UITextLabel>
                          </td>
                        </tr>
                      </tbody>
                    </table>
                  </div>
                </div>
              </UIArticle>

              {/* 프롬프트 섹션 */}
              <UIArticle>
                {/* [251104_퍼블수정] : 프롬프트 타이틀 삭제
                <div className='article-header'>
                  <UITypography variant='title-4' className='secondary-neutral-900'>
                    프롬프트
                  </UITypography>
                </div> */}
                <UIFormField gap={8} direction='column'>
                  <UIQnaFewshot
                    label=''
                    qnaPairs={qnaPairs}
                    required={false}
                    showAddButton={false} // [251104_퍼블수정] : qna 추가 (비노출) false 로 변경
                    showDeleteButton={index => index > 0}
                    onAddQna={handleAddQna}
                    onDeleteQna={(id: string) => {
                      setQnaPairs(qnaPairs.filter(pair => pair.id !== id));
                    }}
                    onQuestionChange={handleQuestionChange}
                    onAnswerChange={handleAnswerChange}
                    questionErrorMessage='질문을 입력해 주세요.'
                    answerErrorMessage='답변을 입력해 주세요.'
                  />
                </UIFormField>
              </UIArticle>

              {/* 연결된 에이전트 섹션 */}
              <UIArticle className='article-grid'>
                <UIListContainer>
                  <UIListContentBox.Header>
                    <div className='flex justify-between items-center w-full'>
                      <div className='flex-shrink-0'>
                        <div style={{ width: '168px', paddingRight: '8px' }}>
                          <UIDataCnt count={projectData.length} prefix='연결된 에이전트 총' unit='건' />
                        </div>
                      </div>
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
                        <tr className='border-b border-gray-200'>
                          <th>
                            <UITypography variant='body-2' className='secondary-neutral-900'>
                              생성자
                            </UITypography>
                          </th>
                          <td>
                            <UITypography variant='body-2' className='secondary-neutral-600'>
                              [퇴사] 김신한 ㅣ Data기획Unit
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
                        <tr className='border-b border-gray-200'>
                          <th>
                            <UITypography variant='body-2' className='secondary-neutral-900'>
                              최종 수정자
                            </UITypography>
                          </th>
                          <td>
                            <UITypography variant='body-2' className='secondary-neutral-600'>
                              [퇴사] 김신한 ㅣ Data기획Unit
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
                  <UIButton2 className='btn-primary-gray' onClick={() => {}}>
                    삭제
                  </UIButton2>
                  <UIButton2 className='btn-primary-blue' onClick={() => {}}>
                    수정
                  </UIButton2>
                </UIUnitGroup>
              </div>
            </div>

            {/* 오른쪽영역 */}
            <div className='grid-right-sticky'>
              <UIVersionCard versions={versionData} />
            </div>
          </div>
        </UIPageBody>
      </section>
    </DesignLayout>
  );
};
