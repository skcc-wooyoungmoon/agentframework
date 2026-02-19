import React, { useMemo, useState } from 'react';

import { UIDataCnt } from '@/components/UI';
import { UIBox, UIPagination, UITypography } from '@/components/UI/atoms';
import { UIButton2 } from '@/components/UI/atoms/UIButton2';
import { UIGroup } from '@/components/UI/molecules';
import { UIDropdown } from '@/components/UI/molecules/dropdown/UIDropdown';
import { UIInput } from '@/components/UI/molecules';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';

import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UITabs } from '@/components/UI/organisms/UITabs';
import { DesignLayout } from '@/design/components/DesignLayout';

export const EV_010101 = () => {
  // 뷰 상태 관리
  const [value, setValue] = useState('12개씩 보기');
  const [view] = useState('grid');

  // 평가 데이터 샘플 (피그마 기반)
  const rowData = [
    {
      no: 1,
      id: '1',
      evalName: '요약 모델 평가',
      description: '요약 성능을 평가',
      createdDate: '2025.03.24 18:23:43',
    },
    {
      no: 2,
      id: '2',
      evalName: '대출 상품 RAG 평가',
      description: '대출상품에 대한 RAG 성능을 평가',
      createdDate: '2025.03.24 18:23:43',
    },
    {
      no: 3,
      id: '3',
      evalName: '추론 모델 성능 평가',
      description: '앱 이용법, 인증 절차 등을 담은 문서 기반 지식',
      createdDate: '2025.03.24 18:23:43',
    },
    {
      no: 4,
      id: '4',
      evalName: '추론 모델 성능 평가',
      description: 'ATM 한도, 수수료 안내 등 지식 문서 세트',
      createdDate: '2025.03.24 18:23:43',
    },
    {
      no: 5,
      id: '5',
      evalName: '추론 모델 성능 평가',
      description: '상담 로그 기반 Q&A 세트',
      createdDate: '2025.03.24 18:23:43',
    },
    {
      no: 6,
      id: '6',
      evalName: '추론 모델 성능 평가',
      description: '질문(차이점) → 명확한 구분 응답 포함',
      createdDate: '2025.03.24 18:23:43',
    },
    {
      no: 7,
      id: '7',
      evalName: '금융사기 예방 교육 세트',
      description: '보이스피싱 예방 안내 및 사례 중심 문서',
      createdDate: '2025.03.24 18:23:43',
    },
    {
      no: 8,
      id: '8',
      evalName: '신한카드 자주 묻는 질문 세트',
      description: '카드 관련 FAQ 문서, 응답 포함',
      createdDate: '2025.03.24 18:23:43',
    },
    {
      no: 9,
      id: '9',
      evalName: '금융사기 예방 교육 세트',
      description: '보이스피싱 예방 안내 및 사례 중심 문서',
      createdDate: '2025.03.24 18:23:43',
    },
    {
      no: 10,
      id: '10',
      evalName: '대출 상품 RAG 평가',
      description: '대출상품에 대한 RAG 성능을 평가',
      createdDate: '2025.03.24 18:23:43',
    },
    {
      no: 11,
      id: '11',
      evalName: '추론 모델 성능 평가',
      description: '앱 이용법, 인증 절차 등을 담은 문서 기반 지식',
      createdDate: '2025.03.24 18:23:43',
    },
    {
      no: 12,
      id: '12',
      evalName: '추론 모델 성능 평가',
      description: 'ATM 한도, 수수료 안내 등 지식 문서 세트',
      createdDate: '2025.03.24 18:23:43',
    },
  ];

  // 그리드 컬럼 정의 (피그마 기반)
  const columnDefs: any = useMemo(
    () => [
      {
        headerName: 'NO',
        field: 'no' as const,
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
        field: 'evalName' as const,
        width: 240,
        cellStyle: { paddingLeft: '16px' },
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
        field: 'description' as const,
        minWidth: 392,
        flex: 1,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '생성일시',
        field: 'createdDate' as const,
        width: 180,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '평가상세',
        field: 'evalDetail',
        width: 120,
        cellStyle: {
          textAlign: 'center',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
        },
        sortable: false,
        suppressHeaderMenuButton: true,
        cellRenderer: () => {
          return <UIButton2 className='btn-text-14-underline-point'>평가상세 이동</UIButton2>;
        },
      },
    ],
    []
  );

  // search 타입
  const [searchValue, setSearchValue] = useState('');

  return (
    <DesignLayout>
      {/* 섹션 페이지 */}
      <section className='section-page'>
        {/* 페이지 헤더 */}
        <UIPageHeader
          title='평가'
          description={['모델과 에이전트를 평가하고 평가한 결과를 조회할 수 있습니다.', '평가 이동버튼을 통해 내가 만든 모델과 에이전트를 평가해 보세요.']}
          actions={
            <>
              {/* [251222_퍼블수정]: 
              btn-text-14-semibold > btn-text-18-semibold
              btn-text-14-semibold-point > btn-text-18-semibold-point  
              (클래스명 변경 : 폰트 크기 수정함) */}
              <UIButton2 className='btn-text-18-semibold' leftIcon={{ className: 'ic-system-24-link', children: '' }}>
                수동 평가 이동
              </UIButton2>
              <UIButton2 className='btn-text-18-semibold' leftIcon={{ className: 'ic-system-24-link', children: '' }}>
                대화형 평가 이동
              </UIButton2>
              <UIButton2 className='btn-text-18-semibold-point' leftIcon={{ className: 'ic-system-24-add', children: '' }}>
                평가하기
              </UIButton2>
            </>
          }
        />
        {/* 페이지 바디 */}
        <UIPageBody>
          <UIArticle>
            {/* 탭 영역 */}
            <UITabs
              items={[
                { id: 'tab1', label: '저지평가' },
                { id: 'tab2', label: '정성평가' },
                { id: 'tab3', label: '정량평가' },
              ]}
              activeId='tab1'
              size='large'
              // onChange=''
            />
          </UIArticle>

          {/* 필터 영역 */}
          <UIArticle className='article-filter'>
            <UIBox className='box-filter'>
              <UIGroup gap={40} direction='row'>
                <div style={{ width: 'calc(100% - 168px)' }}>
                  <table className='tbl_type_b'>
                    <tbody>
                      <tr>
                        <th>
                          <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                            검색
                          </UITypography>
                        </th>
                        <td className='!w-[1200px]'>
                          <UIInput.Search
                            value={searchValue}
                            onChange={e => {
                              setSearchValue(e.target.value);
                            }}
                            placeholder='이름, 설명 입력'
                          />
                          {/* 251104_퍼블수정 : laceholder='검색어 입력' > placeholder='이름, 설명 입력' */}
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
                <div style={{ width: '128px' }}>
                  <UIButton2 className='btn-secondary-blue' style={{ width: '100%' }}>
                    조회
                  </UIButton2>
                </div>
              </UIGroup>
            </UIBox>
          </UIArticle>

          <UIArticle className='article-grid'>
            {/* 전체 데이터 목록 */}
            <UIListContainer>
              <UIListContentBox.Header>
                <div className='flex-shrink-0'>
                  <UIDataCnt count={rowData.length} prefix='총' unit='건' />
                </div>
                <div className='flex items-center gap-2'>
                  <div>
                    <UIButton2 className='btn-tertiary-outline'>저지평가 가이드</UIButton2>
                  </div>
                  <div style={{ width: '160px', flexShrink: 0 }}>
                    <UIDropdown
                      value={String(value)}
                      options={[
                        { value: '1', label: '12개씩 보기' },
                        { value: '2', label: '36개씩 보기' },
                        { value: '3', label: '60개씩 보기' },
                      ]}
                      onSelect={(value: string) => {
                        setValue(value);
                      }}
                      onClick={() => {}}
                      height={40}
                      variant='dataGroup'
                    />
                  </div>
                </div>
              </UIListContentBox.Header>
              <UIListContentBox.Body>
                {view === 'grid' ? (
                  <UIGrid rowData={rowData} columnDefs={columnDefs} onClickRow={(_params: any) => {}} />
                ) : (
                  <div className='p-4 text-center text-gray-500'>카드 뷰 준비 중...</div>
                )}
              </UIListContentBox.Body>
              <UIListContentBox.Footer>
                <UIPagination currentPage={1} totalPages={3} onPageChange={() => {}} className='flex justify-center' />
              </UIListContentBox.Footer>
            </UIListContainer>
          </UIArticle>
        </UIPageBody>
      </section>
    </DesignLayout>
  );
};
