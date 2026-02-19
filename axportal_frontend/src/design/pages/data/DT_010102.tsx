import React from 'react';
import { useMemo, useState } from 'react';
import { UIInput } from '@/components/UI/molecules';

import { UIButton2, UIDataCnt, UITypography } from '@/components/UI/atoms';
import { UIPagination } from '@/components/UI/atoms/UIPagination';
import { UIUnitGroup } from '@/components/UI/molecules';
import { UIDropdown } from '@/components/UI/molecules/dropdown/UIDropdown';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { useModal } from '@/stores/common/modal';
import { DesignLayout } from '../../components/DesignLayout';

export const DT_010102 = () => {
  const { openAlert } = useModal();

  // search 타입
  const [searchValue, setSearchValue] = useState('');

  // dropdown 상태
  const [value, setValue] = useState('1');

  // 더보기 메뉴 설정
  const moreMenuConfig = useMemo(
    () => ({
      items: [
        {
          label: '실행',
          action: 'run',
          onClick: (rowData: any) => {
            openAlert({
              title: '안내',
              message: `테스트 "${rowData.agentName}" 실행을 시작합니다.`,
            });
          },
        },
        {
          label: '수정',
          action: 'modify',
          onClick: (rowData: any) => {
            openAlert({
              title: '안내',
              message: `테스트 "${rowData.agentName}" 수정 팝업을 엽니다.`,
            });
          },
        },
        {
          label: '복사',
          action: 'copy',
          onClick: (rowData: any) => {
            openAlert({
              title: '안내',
              message: `테스트 "${rowData.agentName}" 복사가 완료되었습니다.`,
            });
          },
        },
        {
          label: '삭제',
          action: 'delete',
          onClick: (rowData: any) => {
            openAlert({
              title: '안내',
              message: `테스트 "${rowData.agentName}" 삭제 화면으로 이동합니다.`,
            });
          },
        },
      ],
      isActive: () => true, // 모든 테스트에 대해 활성화
    }),
    []
  );

  // 드롭다운 상태 관리
  // const [dropdownStates, setDropdownStates] = useState({
  //   dateType: false,
  //   searchType: false,
  //   status: false,
  //   agentType: false,
  // });

  // 샘플 데이터
  const rowData = [
    {
      id: '1',
      agentName: '입출금이자유로운예금_상품설명서.pdf',
      title: '입출금이 자유로운 예금 상품 안내',
      attachmentName: '상품설명서',
      uuid: 'ss23ac10b-58cc-4372',
      createdDate: '2025.03.24',
      modifiedDate: '2025.03.24',
    },
    {
      id: '2',
      agentName: '입출금이자유로운예금_상품설명서.pdf',
      title: '자유입출금 상품 가이드',
      attachmentName: '상품설명서',
      uuid: 'ss23ac10b-58cc-4372',
      createdDate: '2025.03.24',
      modifiedDate: '2025.03.24',
    },
    {
      id: '3',
      agentName: '입출금이자유로운예금_상품설명서.pdf',
      title: '예금 상품 설명서',
      attachmentName: '상품설명서',
      uuid: 'ss23ac10b-58cc-4372',
      createdDate: '2025.03.24',
      modifiedDate: '2025.03.24',
    },
    {
      id: '4',
      agentName: '입출금이자유로운예금_상품설명서.pdf',
      title: '자유예금 상품 소개',
      attachmentName: '상품설명서',
      uuid: 'ss23ac10b-58cc-4372',
      createdDate: '2025.03.24',
      modifiedDate: '2025.03.24',
    },
    {
      id: '5',
      agentName: '입출금이자유로운예금_상품설명서.pdf',
      title: '입출금 자유 예금 상품',
      attachmentName: '상품설명서',
      uuid: 'ss23ac10b-58cc-4372',
      createdDate: '2025.03.24',
      modifiedDate: '2025.03.24',
    },
    {
      id: '6',
      agentName: '입출금이자유로운예금_상품설명서.pdf',
      title: '예금 상품 상세 안내',
      attachmentName: '상품설명서',
      uuid: 'ss23ac10b-58cc-4372',
      createdDate: '2025.03.24',
      modifiedDate: '2025.03.24',
    },
    {
      id: '7',
      agentName: '입출금이자유로운예금_상품설명서.pdf',
      title: '자유예금 상품 설명서',
      attachmentName: '상품설명서',
      uuid: 'ss23ac10b-58cc-4372',
      createdDate: '2025.03.24',
      modifiedDate: '2025.03.24',
    },
    {
      id: '8',
      agentName: '입출금이자유로운예금_상품설명서.pdf',
      title: '자유예금 상품 설명서',
      attachmentName: '상품설명서',
      uuid: 'ss23ac10b-58cc-4372',
      createdDate: '2025.03.24',
      modifiedDate: '2025.03.24',
    },
    {
      id: '9',
      agentName: '입출금이자유로운예금_상품설명서.pdf',
      title: '자유예금 상품 설명서',
      attachmentName: '상품설명서',
      uuid: 'ss23ac10b-58cc-4372',
      createdDate: '2025.03.24',
      modifiedDate: '2025.03.24',
    },
    {
      id: '10',
      agentName: '입출금이자유로운예금_상품설명서.pdf',
      title: '자유예금 상품 설명서',
      attachmentName: '상품설명서',
      uuid: 'ss23ac10b-58cc-4372',
      createdDate: '2025.03.24',
      modifiedDate: '2025.03.24',
    },
    {
      id: '11',
      agentName: '입출금이자유로운예금_상품설명서.pdf',
      title: '자유예금 상품 설명서',
      attachmentName: '상품설명서',
      uuid: 'ss23ac10b-58cc-4372',
      createdDate: '2025.03.24',
      modifiedDate: '2025.03.24',
    },
    {
      id: '12',
      agentName: '입출금이자유로운예금_상품설명서.pdf',
      title: '자유예금 상품 설명서',
      attachmentName: '상품설명서',
      uuid: 'ss23ac10b-58cc-4372',
      createdDate: '2025.03.24',
      modifiedDate: '2025.03.24',
    },
  ];

  // 그리드 컬럼 정의
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
        headerName: '파일 이름',
        field: 'agentName' as any,
        width: 312,
        sortable: false,
        cellStyle: {
          paddingLeft: '16px',
        },
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
        headerName: '타이틀',
        field: 'title' as any,
        minWidth: 312,
        flex: 1,
        sortable: false,
        cellStyle: {
          paddingLeft: '16px',
        },
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
        headerName: '첨부파일 이름',
        field: 'attachmentName' as any,
        width: 312,
        sortable: false,
        cellStyle: {
          paddingLeft: '16px',
        },
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
        headerName: 'UUID',
        field: 'uuid' as any,
        width: 200,
        sortable: false,
        cellStyle: {
          paddingLeft: '16px',
        },
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
        headerName: '생성일시',
        field: 'createdDate' as any,
        width: 120,
        sortable: false,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: '최종 수정일시',
        field: 'modifiedDate' as any,
        width: 120,
        sortable: false,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: '메타데이터',
        width: 120,
        sortable: false,
        cellStyle: {
          paddingLeft: '16px',
        },
        cellRenderer: () => {
          return <UIButton2 className='btn-text-14-underline-point'>메타데이터</UIButton2>;
        },
      },
    ],
    [rowData]
  );

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
        <UIPageHeader title='데이터 상세 조회' description='' />
        {/* [251111_퍼블수정] 타이틀명칭 변경 : 데이터 탐색 > 데이터 상세 */}

        {/* 페이지 바디 */}
        <UIPageBody>
          <UIArticle className='article-filter'>
            <div className='article-header'>
              <UITypography variant='title-4' className='secondary-neutral-900'>
                데이터 상세
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
                          이름
                        </UITypography>
                      </th>
                      <td colSpan={3}>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          대출약관
                        </UITypography>
                      </td>
                    </tr>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          요약
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          비정형데이터플랫폼에서 작성한 요약입니다. 없으면 빈 값으로 노출됩니다.
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          원천 시스템
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          SOL-SAM
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
                      <div className='flex-shrink-0'>
                        <div style={{ width: '168px', paddingRight: '8px' }}>
                          <UIDataCnt count={rowData.length} prefix='구성 파일 총' unit='건' />
                        </div>
                      </div>
                      <div className='flex gap-2 flex-shrink-0'>
                        <div style={{ width: '160px', flexShrink: 0 }}>
                          <UIDropdown
                            value={String(value)}
                            options={[
                              { value: '1', label: '파일명' },
                              { value: '2', label: 'UUID' },
                            ]}
                            onSelect={(value: string) => {
                              setValue(value);
                            }}
                            onClick={() => {}}
                            height={40}
                            variant='dataGroup'
                            disabled={false}
                          />
                        </div>
                        <div className='w-[360px]'>
                          <UIInput.Search
                            value={searchValue}
                            onChange={e => {
                              setSearchValue(e.target.value);
                            }}
                            placeholder='검색어 입력'
                          />
                        </div>
                      </div>
                    </div>
                  </UIUnitGroup>
                </div>
              </UIListContentBox.Header>
              <UIListContentBox.Body>
                <UIGrid
                  type='default'
                  rowData={rowData}
                  columnDefs={columnDefs}
                  moreMenuConfig={moreMenuConfig}
                  onClickRow={(_params: any) => {}}
                  onCheck={(_selectedIds: any[]) => {}}
                />
              </UIListContentBox.Body>
              <UIListContentBox.Footer>
                <UIPagination currentPage={1} totalPages={10} onPageChange={() => {}} className='flex justify-center' />
              </UIListContentBox.Footer>
            </UIListContainer>
          </UIArticle>
        </UIPageBody>
      </section>
    </DesignLayout>
  );
};
