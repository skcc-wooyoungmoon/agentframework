import { useState } from 'react';

import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { UIButton2, UITypography } from '@/components/UI/atoms';
import { DesignLayout } from '../../components/DesignLayout';
import { UITextLabel } from '@/components/UI/atoms/UITextLabel';
import { UIUnitGroup, UITextArea2 } from '@/components/UI/molecules';
import { UIVersionCard } from '@/components/UI/molecules/UIVersionCard';

export const PR_040102 = () => {
  // textarea 타입
  const [textareaValue, setTextareaValue] = useState(`<workflow name="loan_approval_flow">
    <step id="1" action="validate_identity"/>
    <step id="2" action="check_credit_score"/>
    <step id="3" action="approve_or_reject"/>
  </workflow>

  <workflow name="loan_approval_flow">
    <step id="1" action="validate_identity"/>
    <step id="2" action="check_credit_score"/>
    <step id="3" action="approve_or_reject"/>
  </workflow>
  `);

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

  return (
    <DesignLayout
      initialMenu={{ id: 'data', label: '데이터' }}
      initialSubMenu={{
        id: 'data-catalog',
        label: '지식/학습 데이터 관리', // [251111_퍼블수정] 타이틀명칭 변경 : 데이터 카탈로그 > 지식/학습 데이터 관리
        icon: 'ico-lnb-menu-20-data-catalog',
      }}
    >
      <section className='section-page'>
        {/* 페이지 헤더 */}
        <UIPageHeader title='워크플로우 조회' description='' />

        {/* 페이지 바디 */}
        <UIPageBody>
          <div className='grid-layout'>
            {/* 왼쪽 : Content */}
            <div className='grid-article'>
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
                              chatbot_response_flow
                            </UITypography>
                          </td>
                          <th>
                            <UITypography variant='body-2' className='secondary-neutral-900'>
                              버전
                            </UITypography>
                          </th>
                          <td>
                            <UITypography variant='body-2' className='secondary-neutral-600'>
                              <UITextLabel intent='gray'>Lastest.1</UITextLabel>
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
                            <UIUnitGroup gap={8} direction='row' align='start'>
                              <UITextLabel intent='tag'>agent_generator</UITextLabel>
                              <UITextLabel intent='tag'>test</UITextLabel>
                              <UITextLabel intent='tag'>generator</UITextLabel>
                              <UITextLabel intent='tag'>test</UITextLabel>
                            </UIUnitGroup>
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
                    워크플로우
                  </UITypography>
                </div>
                <div className='article-body'>
                  <UIUnitGroup gap={8} direction='row' align='start'>
                    <div className='flex-1'>
                      <UITextArea2 value={textareaValue} placeholder='' style={{ height: '394px' }} onChange={e => setTextareaValue(e.target.value)} />
                    </div>
                  </UIUnitGroup>
                </div>
              </UIArticle>
              {/* 테이블 */}
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
                        <tr>
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
                              2025.03.24 18:23:43
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
                              권신태ㅣAI Unitㅣ대출 상품 추천
                            </UITypography>
                          </td>
                        </tr>
                      </tbody>
                    </table>
                  </div>
                </div>
              </UIArticle>

              <div className='article-buton-group'>
                <UIButton2 className='btn-primary-gray w-[80px]'>삭제</UIButton2>
                <UIButton2 className='btn-primary-blue w-[80px]'>수정</UIButton2>
              </div>
            </div>

            {/* 오른쪽 : Content */}
            <div className='grid-right-sticky'>
              <UIVersionCard versions={versionData} />
            </div>
          </div>
        </UIPageBody>
      </section>
    </DesignLayout>
  );
};
