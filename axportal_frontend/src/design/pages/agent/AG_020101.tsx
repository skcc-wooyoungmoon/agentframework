import React, { useState, useMemo } from 'react';

import { UIButton2, UITypography } from '@/components/UI/atoms';
import { UIBox } from '@/components/UI/atoms/UIBox';
import { UIDataCnt } from '@/components/UI/atoms/UIDataCnt';
import { UIPagination } from '@/components/UI/atoms/UIPagination';
import { UIToggle } from '@/components/UI/atoms/UIToggle';
import { UIInput, UIDropdown, UIUnitGroup, UIGroup } from '@/components/UI/molecules';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { useModal } from '@/stores/common/modal';

import { DesignLayout } from '../../components/DesignLayout';
import { UIGridCard } from '@/components/UI/molecules/card/UIGridCard';
import { UICardList } from '@/components/UI/molecules/card/UICardList';

interface SearchValues {
  dateType: string;
  dateRange: { startDate?: string; endDate?: string };
  searchType: string;
  searchKeyword: string;
  status: string;
  agentType: string;
}

export const AG_020101 = () => {
  const { openAlert } = useModal();
  const [searchValues, setSearchValues] = useState<SearchValues>({
    dateType: '생성일시',
    dateRange: { startDate: '2025.06.30', endDate: '2025.07.30' },
    searchType: '이름',
    searchKeyword: '',
    status: '전체',
    agentType: '전체',
  });

  const [selectedCardIds, setSelectedCardIds] = useState<string[]>([]);
  const [view, setView] = useState('grid');

  // 샘플 데이터
  const sampleData = [
    {
      id: '1',
      name: '예적금 상품 Q&A 세트',
      status: '활성화',
      description: '정기예금 등 기본 수신상품에 대한 고객 질문과 상담',
      publicRange: '전체공유',
      vectorDB: 'Pinecone',
      embadding: 'OpenAI-Text-Embedding-3-small',
      splitMethod: '지도학습',
      createdDate: '2025.03.24 18:23:43',
      catogory: 'Loader',
      modifiedDate: '2025.03.24 18:32:43',
      more: 'more',
    },
    {
      id: '2',
      name: '신용대출 조건 분류 데이터',
      status: '활성화',
      description: '대출 가능성 분류 라벨이 포함된 데이터',
      publicRange: '내부공유',
      vectorDB: 'Pinecone',
      embadding: 'OpenAI-Text-Embedding-3-small',
      splitMethod: '비지도학습',
      createdDate: '2025.03.23 15:20:15',
      catogory: 'Chunking',
      modifiedDate: '2025.03.23 15:29:15',
      more: 'more',
    },
    {
      id: '3',
      name: '모바일뱅킹 이용 가이드',
      status: '비활성화',
      description: '앱 이용법, 인증 절차 등을 담은 문서 기반 지식',
      publicRange: '전체공유',
      vectorDB: 'Pinecone',
      embadding: 'OpenAI-Text-Embedding-3-small',
      splitMethod: '지도학습',
      createdDate: '2025.03.22 14:45:22',
      catogory: 'Loader',
      modifiedDate: '2025.03.22 14:54:22',
      more: 'more',
    },
    {
      id: '4',
      name: 'ATM/창구 업무 안내 문서',
      status: '비활성화',
      description: 'ATM 한도, 수수료 안내 등 지식 문서 세트',
      publicRange: '내부공유',
      vectorDB: 'Pinecone',
      embadding: 'OpenAI-Text-Embedding-3-small',
      splitMethod: '비지도학습',
      createdDate: '2025.03.21 09:30:00',
      catogory: 'Chunking',
      modifiedDate: '2025.03.21 09:39:00',
      more: 'more',
    },
    {
      id: '5',
      name: '외화 송금 및 환율 상담 로그',
      status: '활성화',
      description: '상담 로그 기반 Q&A 세트',
      publicRange: '전체공유',
      vectorDB: 'Pinecone',
      embadding: 'OpenAI-Text-Embedding-3-small',
      splitMethod: '지도학습',
      createdDate: '2025.03.20 16:15:30',
      catogory: 'Loader',
      modifiedDate: '2025.03.20 16:24:30',
      more: 'more',
    },
  ];

  // 그리드(카드형)
  const rowCardData = [
    {
      no: 1,
      id: '1',
      modelName: '대출 상담 자동화 에이전트',
      description: '질문 분류 문서 기반 응답 자동 생성',
      publicRange: '전체공유',
      toolType: 'custom_api',
      createdDate: '2024-01-15 09:30:00',
      modifiedDate: '2024-01-20 14:25:00',
      more: 'more',
    },
    {
      no: 2,
      id: '2',
      modelName: '대출 상담 자동화 에이전트',
      description: '질문 분류 문서 기반 응답 자동 생성',
      publicRange: '전체공유',
      toolType: 'custom_api',
      createdDate: '2024-01-10 11:15:00',
      modifiedDate: '2024-01-18 16:40:00',
      more: 'more',
    },
    {
      no: 3,
      id: '3',
      modelName: '대출 상담 자동화 에이전트',
      description: '질문 분류 문서 기반 응답 자동 생성',
      publicRange: '전체공유',
      toolType: 'custom_api',
      createdDate: '2024-01-05 13:45:00',
      modifiedDate: '2024-01-22 10:20:00',
      more: 'more',
    },
    {
      no: 4,
      id: '4',
      modelName: '대출 상담 자동화 에이전트',
      description: '질문 분류 문서 기반 응답 자동 생성',
      publicRange: '전체공유',
      toolType: 'custom_api',
      createdDate: '2024-01-12 08:30:00',
      modifiedDate: '2024-01-19 15:50:00',
      more: 'more',
    },
    {
      no: 5,
      id: '5',
      modelName: '대출 상담 자동화 에이전트',
      description: '질문 분류 문서 기반 응답 자동 생성',
      publicRange: '전체공유',
      toolType: 'custom_api',
      createdDate: '2024-01-08 16:20:00',
      modifiedDate: '2024-01-21 12:35:00',
      more: 'more',
    },
  ];

  const handleSearch = () => {};

  // 더보기 메뉴 설정
  const moreMenuConfig = useMemo(
    () => ({
      items: [
        {
          label: '수정',
          action: 'modify',
          onClick: (rowData: any) => {
            openAlert({
              title: '안내',
              message: `"${rowData.name}" 수정 팝업을 엽니다.`,
            });
          },
        },
        {
          label: '삭제',
          action: 'delete',
          onClick: (rowData: any) => {
            openAlert({
              title: '안내',
              message: `"${rowData.name}" 삭제 화면으로 이동합니다.`,
            });
          },
        },
      ],
    }),
    [openAlert]
  );

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
      // 251113_퍼블수정 그리드 컬럼 속성 '이름' 영역 수정 S
      {
        headerName: '이름', // [251105_퍼블수정] : '배포명' > '이름'
        field: 'name',
        width: 272,
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
                paddingLeft: '0',
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                whiteSpace: 'nowrap',
                width: '100%',
              }}
            >
              {params.value}
            </div>
          );
        }),
      },
      {
        headerName: '공개범위',
        field: 'publicRange',
        width: 120,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: 'Tools 유형',
        field: 'catogory',
        width: 120,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: '생성일시',
        field: 'createdDate',
        width: 180,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: '최종 수정일시',
        field: 'modifiedDate',
        width: 180,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: '',
        field: 'more',
        width: 56,
        sortable: false,
        suppressHeaderMenuButton: true,
      },
    ],
    [sampleData]
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
        <UIPageHeader
          title='Tools'
          description={['Agent가 사용할 도구를 등록하고 관리할 수 있습니다.', '행내 다양한 API를 등록하거나 커스텀 코드 개발을 통해 다양한 도구를 만들어보세요.']}
          actions={
            <>
              {/* [251222_퍼블수정]: btn-text-14-semibold-point > btn-text-18-semibold-point  (클래스명 변경 : 폰트 크기 수정함) */}
              <UIButton2 className='btn-text-18-semibold-point' leftIcon={{ className: 'ic-system-24-add', children: '' }}>
                Tools 등록
              </UIButton2>
            </>
          }
        />

        {/* 페이지 바디 */}
        <UIPageBody>
          {/* 검색 영역 */}
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
                        <td colSpan={3}>
                          <div className='flex-1'>
                            <UIInput.Search
                              value={searchValues.searchKeyword}
                              placeholder='검색어 입력'
                              onChange={e => setSearchValues(prev => ({ ...prev, searchKeyword: e.target.value }))}
                            />
                          </div>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
                <div style={{ width: '128px' }}>
                  <UIButton2 className='btn-secondary-blue' style={{ width: '100%' }} onClick={handleSearch}>
                    조회
                  </UIButton2>
                </div>
              </UIGroup>
            </UIBox>
          </UIArticle>

          {/* 데이터 그리드 컴포넌트 */}
          <UIArticle className='article-grid'>
            <UIListContainer>
              <UIListContentBox.Header>
                <div className='w-full'>
                  <UIUnitGroup gap={16} direction='column'>
                    <div className='flex justify-between w-full items-center'>
                      <div className='flex-shrink-0'>
                        <div style={{ width: '168px', paddingRight: '8px' }}>
                          <UIDataCnt count={sampleData.length} prefix='총' unit='건' />
                        </div>
                      </div>
                      <div className='flex items-center gap-[8px]'>
                        <div style={{ width: '160px', flexShrink: 0 }}>
                          <UIDropdown
                            value='12개씩 보기'
                            options={[
                              { value: '12개씩 보기', label: '12개씩 보기' },
                              { value: '24개씩 보기', label: '24개씩 보기' },
                              { value: '48개씩 보기', label: '48개씩 보기' },
                            ]}
                            onSelect={(_value: string) => {}}
                            onClick={() => {}}
                            height={40}
                            variant='dataGroup'
                          />
                        </div>
                        {/* 뷰 토글 컴포넌트 */}
                        <UIToggle variant='dataView' checked={view === 'card'} onChange={checked => setView(checked ? 'card' : 'grid')} />
                      </div>
                    </div>
                  </UIUnitGroup>
                </div>
              </UIListContentBox.Header>
              <UIListContentBox.Body>
                {view === 'grid' ? (
                  <UIGrid
                    type='multi-select'
                    rowData={sampleData}
                    columnDefs={columnDefs}
                    moreMenuConfig={moreMenuConfig}
                    onClickRow={(_params: any) => {}}
                    onCheck={(_selectedIds: any[]) => {}}
                  />
                ) : (
                  <UICardList
                    rowData={rowCardData}
                    flexType='none'
                    card={(item: any) => {
                      return (
                        <UIGridCard
                          id={item.id}
                          title={item.modelName}
                          caption={item.description}
                          data={item}
                          moreMenuConfig={moreMenuConfig}
                          checkbox={{
                            checked: selectedCardIds.includes(item.id),
                            onChange: (checked: boolean, _value: string) => {
                              if (checked) {
                                setSelectedCardIds([...selectedCardIds, item.id]);
                              } else {
                                setSelectedCardIds(selectedCardIds.filter(id => id !== item.id));
                              }
                            },
                          }}
                          rows={[
                            { label: 'Tool 유형', value: item.toolType },
                            { label: '생성일시', value: item.createdDate },
                            { label: '최종수정일시', value: item.modifiedDate },
                          ]}
                        />
                      );
                    }}
                  />
                )}
              </UIListContentBox.Body>
              {/* [참고] classname 관련
                  - 그리드 하단 (삭제) 버튼이 있는 경우 classname 지정 (예시) <UIListContentBox.Footer classname="ui-data-has-btn">
                  - 그리드 하단 (버튼) 없는 경우 classname 없이 (예시) <UIListContentBox.Footer>
                */}
              <UIListContentBox.Footer className='ui-data-has-btn'>
                <UIButton2 className='btn-option-outlined' style={{ width: '40px' }}>
                  삭제
                </UIButton2>
                <UIPagination currentPage={1} totalPages={5} onPageChange={(_page: number) => {}} className='flex justify-center' />
              </UIListContentBox.Footer>
            </UIListContainer>
          </UIArticle>
        </UIPageBody>
      </section>
    </DesignLayout>
  );
};
