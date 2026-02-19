import React, { useMemo, useState } from 'react';

import { UIToggle, UIDataCnt } from '@/components/UI';
import { UIButton2, UIPagination } from '@/components/UI/atoms';
import { UICardList } from '@/components/UI/molecules/card/UICardList';
import { UIGridCard } from '@/components/UI/molecules/card/UIGridCard';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { UIDropdown } from '../../../components/UI/molecules/dropdown/UIDropdown';
import { UITabs } from '../../../components/UI/organisms/UITabs';
import { DesignLayout } from '../../components/DesignLayout';

export const DT_030201 = () => {
  const [activeTab, setActiveTab] = useState('processer');

  // 샘플 데이터
  const sampleData = [
    {
      id: '1',
      name: '고객상담_보험상품형',
      description: '보험 상품 관련 고객 문의 전문 상담 에이전트',
    },
    {
      id: '2',
      name: '업무지원_문서처리형',
      description: '문서 작성, 요약, 검토 등 업무 지원 에이전트',
    },
    {
      id: '3',
      name: '데이터분석_리포트형',
      description: '데이터 분석 결과를 시각적 리포트로 제공하는 에이전트',
    },
    {
      id: '4',
      name: '질의응답_FAQ형',
      description: '자주 묻는 질문에 대한 자동 응답 처리 에이전트',
    },
    {
      id: '5',
      name: '업무자동화_프로세스형',
      description: '반복적인 업무 프로세스를 자동화하는 에이전트',
    },
    {
      id: '6',
      name: '콘텐츠생성_마케팅형',
      description: '마케팅 콘텐츠 생성 및 관리 전문 에이전트',
    },
    {
      id: '7',
      name: '번역_다국어형',
      description: '다국어 번역 및 국제화 지원 에이전트',
    },
  ];

  // 그리드 컬럼 정의
  const columnDefs = useMemo(
    () => [
      {
        headerName: 'NO',
        field: 'id',
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
      },
      // 251113_퍼블수정 그리드 컬럼 속성 '이름' 영역 수정 S
      {
        headerName: '이름',
        field: 'name',
        width: 554,
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
      // 251113_퍼블수정 그리드 컬럼 속성 '이름' 영역 수정 E
      {
        headerName: '설명',
        field: 'description',
        minWidth: 392,
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
    ],
    []
  );

  // Grid 카드형
  const rowCardData = [
    {
      id: '1',
      title: '타이틀 영역 타이틀 영역 타이틀 영역 타이틀 영역 타이틀 영역',
      caption: '캡션 영역 캡션 영역 캡션 영역 캡션 영역 캡션 영역 캡션 영역 캡션 영역',
    },
    {
      id: '2',
      title: '타이틀 영역 타이틀 영역 타이틀 영역 타이틀 영역 타이틀 영역',
      caption: '캡션 영역 캡션 영역 캡션 영역 캡션 영역 캡션 영역 캡션 영역 캡션 영역',
    },
    {
      id: '3',
      title: '타이틀 영역 타이틀 영역 타이틀 영역 타이틀 영역 타이틀 영역',
      caption: '캡션 영역 캡션 영역 캡션 영역 캡션 영역 캡션 영역 캡션 영역 캡션 영역',
    },
    {
      id: '4',
      title: '타이틀 영역 타이틀 영역 타이틀 영역 타이틀 영역 타이틀 영역',
      caption: '캡션 영역 캡션 영역 캡션 영역 캡션 영역 캡션 영역 캡션 영역 캡션 영역',
    },
    {
      id: '5',
      title: '타이틀 영역 타이틀 영역 타이틀 영역 타이틀 영역 타이틀 영역',
      caption: '캡션 영역 캡션 영역 캡션 영역 캡션 영역 캡션 영역 캡션 영역 캡션 영역',
    },
  ];

  // 탭 아이템 정의
  const tabItems = [
    { id: 'vector', label: '백터 DB' },
    { id: 'processer', label: '프로세서' },
  ];

  const [value, setValue] = useState('12개씩 보기');

  const [view, setView] = useState('grid');

  return (
    <DesignLayout
      initialMenu={{ id: 'data', label: '데이터' }}
      initialSubMenu={{
        id: 'data-catalog',
        label: '데이터 도구',
        icon: 'ico-lnb-menu-20-data-catalog',
      }}
    >
      {/* 섹션 페이지 */}
      <section className='section-page'>
        {/* 페이지 헤더 */}
        {/* [251120_퍼블수정] 검수요청 현행화 수정 */}
        <UIPageHeader
          title='데이터 도구'
          description={[
            '학습 데이터세트에 사용할 프로세서 조회와 지식에 사용할 벡터DB를 관리할 수 있습니다.',
            '벡터 DB의 경우 기본 지식은 기본 벡터DB를 사용하며, 사용자 정의 지식은 별도의 벡터DB를 등록해야 합니다.',
          ]}
          actions={
            <>
              {/* [251222_퍼블수정]: btn-text-14-semibold-point > btn-text-18-semibold-point  (클래스명 변경 : 폰트 크기 수정함) */}
              <UIButton2 className='btn-text-18-semibold-point' leftIcon={{ className: 'ic-system-24-add', children: '' }}>
                데이터도구 만들기
              </UIButton2>
            </>
          }
        />

        {/* 페이지 바디 */}
        <UIPageBody>
          <UIArticle className='article-tabs'>
            {/* 탭 영역 */}
            <UITabs items={tabItems} activeId={activeTab} size='large' onChange={setActiveTab} />
          </UIArticle>

          {/* 데이터 그룹 컴포넌트 */}
          <UIArticle className='article-grid'>
            <UIListContainer>
              <UIListContentBox.Header>
                <div className='grid-header-left'>
                  <UIDataCnt count={sampleData.length} prefix='총' unit='건' />
                </div>
                <div className='flex items-center gap-2'>
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
                  <UIToggle variant='dataView' checked={view === 'card'} onChange={checked => setView(checked ? 'card' : 'grid')} />
                </div>
              </UIListContentBox.Header>
              <UIListContentBox.Body>
                {view === 'grid' ? (
                  <UIGrid<any> type='default' rowData={sampleData} columnDefs={columnDefs as any} onClickRow={(_params: any) => {}} onCheck={(_selectedIds: any[]) => {}} />
                ) : (
                  <UICardList flexType='none' rowData={rowCardData} card={(item: any) => <UIGridCard id={item.id} title={item.title} caption={item.caption} rows={[]} />} />
                )}
              </UIListContentBox.Body>
              <UIListContentBox.Footer>
                <UIPagination currentPage={1} totalPages={1} onPageChange={() => {}} className='flex justify-center' />
              </UIListContentBox.Footer>
            </UIListContainer>
          </UIArticle>
        </UIPageBody>
      </section>
    </DesignLayout>
  );
};
