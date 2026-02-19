import { useMemo } from 'react';
import { DesignLayout } from '../../components/DesignLayout';

import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIButton2, UIDataCnt } from '@/components/UI/atoms';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UITypography } from '@/components/UI/atoms';
import { UIGroup, UIUnitGroup } from '@/components/UI/molecules';
import { UIPageFooter } from '@/components/UI/molecules/UIPageFooter';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UIPagination } from '@/components/UI/atoms/UIPagination';

export const AG_010103 = () => {
  // 더보기 메뉴 설정
  // 더보기 삭제

  // 사용 모델 그리드 컬럼 설정
  const modelColumnDefs: any = useMemo(
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
        headerName: '노드명',
        field: 'nodeName' as any,
        width: 392,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: '노드 종류',
        field: 'nodeType' as any,
        width: 392,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: '모델명',
        field: 'modelName' as any,
        flex: 1,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
    ],
    []
  );

  // 사용 지식 그리드 컬럼 설정
  const knowledgeColumnDefs: any = useMemo(
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
        headerName: '노드명',
        field: 'nodeName' as any,
        width: 392,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: '노드 종류',
        field: 'nodeType' as any,
        width: 392,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: '지식명',
        field: 'knowledgeName' as any,
        flex: 1,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
    ],
    []
  );

  // 사용 모델 로우 데이터
  const modelRowData = [
    {
      id: '1',
      nodeName: 'agent_generator_1',
      nodeType: 'Generator',
      modelName: 'GIP/gpt-4o',
    },
    {
      id: '2',
      nodeName: 'agent_retriever_1',
      nodeType: 'Retriever',
      modelName: 'GIP/gpt-4o-mini',
    },
  ];

  // 사용 지식 로우 데이터
  const knowledgeRowData = [
    {
      id: '1',
      nodeName: 'knowledge_retriever_1',
      nodeType: 'Knowledge Retriever',
      knowledgeName: '신한은행 FAQ 지식베이스',
    },
  ];

  return (
    <DesignLayout
      initialMenu={{ id: 'agent', label: '에이전트' }}
      initialSubMenu={{
        id: 'agent-tools',
        label: '에이전트의 도구',
        icon: 'ico-lnb-menu-20-agent-tools',
      }}
    >
      {/* 섹션 페이지 */}
      <section className='section-page'>
        {/* 페이지 헤더 */}
        <UIPageHeader
          title='빌더 조회'
          description=''
          actions={
            <>
              <UIGroup gap={8} direction='row' align='start'>
                <UIButton2 className='btn-tertiary-outline line-only-blue'>빌더캔버스 편집</UIButton2>
                <UIButton2 className='btn-tertiary-outline line-only-blue'>빌더캔버스 조회</UIButton2>
              </UIGroup>
            </>
          }
        />

        {/* 페이지 바디 */}
        <UIPageBody>
          <UIArticle>
            <div className='article-header'>
              <UITypography variant='title-4' className='secondary-neutral-900 text-sb'>
                에이전트 정보
              </UITypography>
            </div>
            <div className='article-body'>
              <div className='border-t border-black'>
                <table className='tbl-v'>
                  {/* [251102-퍼블수정] : colgroup [S] */}
                  <colgroup>
                    <col style={{ width: '10%' }} />
                    <col style={{ width: '40%' }} />
                    <col style={{ width: '10%' }} />
                    <col style={{ width: '40%' }} />
                  </colgroup>
                  {/* [251102-퍼블수정] : colgroup [E]  col 태그 컬럼 2개로 수정필요 */}
                  <tbody>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          이름
                        </UITypography>
                      </th>
                      {/* [251107_퍼블수정] : td 영역에 colSpan={3} 넣어주세요 */}
                      <td colSpan={3}>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          기준금리 조회
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
                          현재 기준금리 및 주요 은행 예·적금 금리를 조회하는 도구
                        </UITypography>
                      </td>
                    </tr>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          배포여부
                        </UITypography>
                      </th>
                      {/* [251107_퍼블수정] : td 영역에 colSpan={3} 넣어주세요 */}
                      <td colSpan={3}>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          배포
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
                <div className='w-full'>
                  <UIUnitGroup gap={16} direction='column'>
                    <div className='flex justify-between w-full items-center'>
                      <div className='w-full'>
                        <UIGroup gap={12} direction='row' align='start'>
                          <div style={{ width: '200px', display: 'flex', alignItems: 'center' }}>
                            <UIDataCnt count={2} prefix='사용 모델 총' unit='건' />
                          </div>
                        </UIGroup>
                      </div>
                    </div>
                  </UIUnitGroup>
                </div>
              </UIListContentBox.Header>
              <UIListContentBox.Body>
                <UIGrid rowData={modelRowData} columnDefs={modelColumnDefs} onClickRow={(_params: any) => {}} />
              </UIListContentBox.Body>
              <UIListContentBox.Footer>
                <UIPagination currentPage={1} totalPages={5} onPageChange={() => {}} className='flex justify-center' />
              </UIListContentBox.Footer>
            </UIListContainer>
          </UIArticle>

          <UIArticle className='article-grid'>
            <UIListContainer>
              <UIListContentBox.Header>
                <div className='w-full'>
                  <UIUnitGroup gap={16} direction='column'>
                    <div className='flex justify-between w-full items-center'>
                      <div className='w-full'>
                        <UIGroup gap={12} direction='row' align='start'>
                          <div style={{ width: '200px', display: 'flex', alignItems: 'center' }}>
                            <UIDataCnt count={1} prefix='사용 지식 총' unit='건' />
                          </div>
                        </UIGroup>
                      </div>
                    </div>
                  </UIUnitGroup>
                </div>
              </UIListContentBox.Header>
              <UIListContentBox.Body>
                <UIGrid rowData={knowledgeRowData} columnDefs={knowledgeColumnDefs} onClickRow={(_params: any) => {}} />
              </UIListContentBox.Body>
              <UIListContentBox.Footer>
                <UIPagination currentPage={1} totalPages={5} onPageChange={() => {}} className='flex justify-center' />
              </UIListContentBox.Footer>
            </UIListContainer>
          </UIArticle>

          <UIArticle>
            <div className='article-header'>
              <UITypography variant='title-4' className='secondary-neutral-900 text-sb'>
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
                          [재직] 박신한 | AI Unit
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          최종 수정일시
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          2025.05.10 18:23:43
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
                <UITypography variant='title-4' className='secondary-neutral-900 text-sb'>
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
              <UIButton2 className='btn-primary-blue'>수정</UIButton2>
            </UIUnitGroup>
          </UIArticle>
        </UIPageFooter>
      </section>
    </DesignLayout>
  );
};
