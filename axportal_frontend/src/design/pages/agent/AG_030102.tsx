import React, { useMemo } from 'react';

import { DesignLayout } from '../../components/DesignLayout';
import { UIButton2, UILabel } from '@/components/UI/atoms';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UITypography, UITextLabel, UIDataCnt } from '@/components/UI/atoms';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { UIPageFooter } from '@/components/UI/molecules/UIPageFooter';
import { UIUnitGroup } from '@/components/UI/molecules';
import { UIGrid } from '@/components/UI/molecules/grid/UIGrid/component';
import { UIListContainer } from '@/components/UI/molecules/list/UIListContainer/component';
import { UIListContentBox } from '@/components/UI/molecules/list/UIListContentBox';
import { UIPagination } from '@/components/UI/atoms/UIPagination';

import type { ColDef } from 'ag-grid-community';

export const AG_030102 = () => {
  // 연결된 에이전트 목록
  const mcpData = [
    {
      id: '1',
      no: 1,
      name: 'AI 챗봇 에이전트',
      description:
        '고객 서비스를 위한 자동 응답 챗봇 에이전트입니다.고객 서비스를 위한 자동 응답 챗봇 에이전트입니다고객 서비스를 위한 자동 응답 챗봇 에이전트입니다고객 서비스를 위한 자동 응답 챗봇 에이전트입니다고객 서비스를 위한 자동 응답 챗봇 에이전트입니다고객 서비스를 위한 자동 응답 챗봇 에이전트입니다',
    },
    {
      id: '2',
      no: 2,
      name: '데이터 분석 에이전트',
      description: '대용량 데이터 분석 및 리포트 생성을 담당하는 에이전트입니다.대용량 데이터 분석 및 리포트 생성을 담당하는 에이전트입니다',
    },
    {
      id: '3',
      no: 3,
      name: '데이터 분석 에이전트',
      description: '대용량 데이터 분석 및 리포트 생성을 담당하는 에이전트입니다.',
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
      {
        headerName: '툴 이름',
        field: 'name',
        width: 272,
      },
      {
        headerName: '설명',
        field: 'description',
        flex: 1,
        minWidth: 472,
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
    ],
    []
  );

  return (
    <DesignLayout
      initialMenu={{ id: 'agent', label: '에이전트' }}
      initialSubMenu={{
        id: 'agent',
        label: '에이전트',
        icon: 'ico-lnb-menu-20-admin-user',
      }}
    >
      <section className='section-page'>
        {/* 페이지 헤더 */}
        <UIPageHeader title='MCP 서버 조회' description='' />

        {/* 페이지 바디 */}
        <UIPageBody>
          <UIArticle>
            <div className='article-header'>
              <UITypography variant='title-4' className='secondary-neutral-900'>
                MCP 서버 정보
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
                          신한은행 MCP서버 1
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          상태
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          <UILabel variant='badge' intent='complete'>
                            이용가능
                          </UILabel>
                        </UITypography>
                      </td>
                    </tr>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          설명
                        </UITypography>
                      </th>
                      <td colSpan={3}>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          신한은행 MCP서버 1 입니다.
                        </UITypography>
                      </td>
                    </tr>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          전송 유형
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          Streamable HTTP
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          태그
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          <UITextLabel intent='tag'>보고서 요약</UITextLabel>
                        </UITypography>
                      </td>
                    </tr>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          서버 URL
                        </UITypography>
                      </th>
                      <td colSpan={3}>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          https://aip.sktai.io/api/v1/agent/tools
                        </UITypography>
                      </td>
                    </tr>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          인증 유형
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          basic
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          User name
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          user1234
                        </UITypography>
                      </td>
                    </tr>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          Password
                        </UITypography>
                      </th>
                      <td colSpan={3}>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          ● ● ● ● ● ● ●
                        </UITypography>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </UIArticle>

          <UIArticle className='article-grid'>
            <div className='article-body'>
              <UIListContainer>
                <UIListContentBox.Header>
                  <div className='w-full'>
                    <UIUnitGroup gap={0} direction='row'>
                      <div className='flex justify-between w-full items-center'>
                        <div className='flex-shrink-0'>
                          <div style={{ width: '168px', paddingRight: '8px' }}>
                            <UIDataCnt count={mcpData.length} prefix='툴 목록 총' unit='건' />
                          </div>
                        </div>
                      </div>
                      <div>
                        <UIButton2 className='btn-option-outlined'>새로고침</UIButton2>
                      </div>
                    </UIUnitGroup>
                  </div>
                </UIListContentBox.Header>
                <UIListContentBox.Body>
                  <UIGrid
                    type='default'
                    rowData={mcpData}
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
            </div>
          </UIArticle>

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
                          [재직] 김신한 | Data기획Unit
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
          <UIArticle>
            <div className='article-header'>
              <UIUnitGroup direction='row' align='space-between' gap={0}>
                <UITypography variant='title-4' className='secondary-neutral-900'>
                  프로젝트 정보
                </UITypography>
                <UIButton2 className='btn-option-outlined'>공개 설정</UIButton2>
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
                          전체공유 | 공개 프로젝트
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          권한 수정자
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          권신태 ㅣ AI Unit
                        </UITypography>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </UIArticle>
        </UIPageBody>
        {/* 페이지 footer */}
        <UIPageFooter>
          <UIArticle>
            <UIUnitGroup gap={8} direction='row' align='center'>
              <UIButton2 className='btn-primary-gray'>삭제</UIButton2>
              <UIButton2 className='btn-primary-blue' disabled={false}>
                수정
              </UIButton2>
            </UIUnitGroup>
          </UIArticle>
        </UIPageFooter>
      </section>
    </DesignLayout>
  );
};
