import React, { useState, useMemo } from 'react';

import { UIInput, UIDropdown, UIArticle, UIUnitGroup, UIGroup } from '@/components/UI/molecules';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { useModal } from '@/stores/common/modal';

import { UIButton2, UILabel, UITypography } from '../../../components/UI/atoms';
import { UIBox } from '../../../components/UI/atoms/UIBox';
import { UIDataCnt } from '../../../components/UI/atoms/UIDataCnt';
import { UIPagination } from '../../../components/UI/atoms/UIPagination';
import { UIToggle } from '../../../components/UI/atoms/UIToggle';
import { UITabs } from '../../../components/UI/organisms/UITabs';
import { DesignLayout } from '../../components/DesignLayout';
import { UIGridCard } from '@/components/UI/molecules/card/UIGridCard';
import { UICardList } from '@/components/UI/molecules/card/UICardList';

interface SearchValues {
  dateType: string;
  dateRange: { startDate?: string; endDate?: string };
  searchType: string;
  searchKeyword: string;
  status: string;
  publicRange: string;
}

export const DT_020301 = () => {
  const { openAlert } = useModal();
  const [activeTab, setActiveTab] = useState('knowledge');
  const [value, setValue] = useState('10개씩 보기');
  const [checkedCards, setCheckedCards] = useState<{ [key: string]: boolean }>({});

  const [view, setView] = useState('grid');

  const [searchValues, setSearchValues] = useState<SearchValues>({
    dateType: '생성일시',
    dateRange: { startDate: '2025.06.30', endDate: '2025.07.30' },
    searchType: '이름',
    searchKeyword: '',
    status: '전체',
    publicRange: '전체',
  });

  // 드롭다운 상태 관리
  const [dropdownStates, setDropdownStates] = useState({
    dateType: false,
    searchType: false,
    status: false,
    publicRange: false,
  });

  // 샘플 데이터 (rowData로 변수명 변경)
  const rowData = [
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
      modifiedDate: '2025.03.24 18:23:43',
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
      modifiedDate: '2025.03.23 15:20:15',
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
      modifiedDate: '2025.03.22 14:45:22',
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
      modifiedDate: '2025.03.21 09:30:00',
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
      modifiedDate: '2025.03.20 16:15:30',
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
              message: `테스트 "${rowData.name}" 수정 팝업을 엽니다.`,
            });
          },
        },
        {
          label: '삭제',
          action: 'delete',
          onClick: (rowData: any) => {
            openAlert({
              title: '안내',
              message: `테스트 "${rowData.name}" 삭제 화면으로 이동합니다.`,
            });
          },
        },
      ],
      isActive: () => true, // 모든 테스트에 대해 활성화
    }),
    []
  );

  // 드롭다운 핸들러
  const handleDropdownToggle = (key: keyof typeof dropdownStates) => {
    setDropdownStates(prev => ({
      ...Object.keys(prev).reduce((acc, k) => ({ ...acc, [k]: false }), {} as typeof prev),
      [key]: !prev[key],
    }));
  };

  const handleDropdownSelect = (key: keyof SearchValues, value: string) => {
    setSearchValues(prev => ({ ...prev, [key]: value }));
    setDropdownStates(prev => ({ ...prev, [key]: false }));
  };

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
        headerName: '이름',
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
        headerName: '상태',
        field: 'status',
        width: 120,
        cellRenderer: (params: any) => {
          const colorMap: { [key: string]: string } = {
            활성화: 'complete',
            비활성화: 'error',
          };
          return (
            <UILabel variant='badge' intent={colorMap[params.value] as any}>
              {params.value}
            </UILabel>
          );
        },
      },
      {
        headerName: '설명',
        field: 'description',
        minWidth: 392,
        flex: 1,
        showTooltip: true,
        suppressSizeToFit: true,
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
              // 호버 시 전체 텍스트 표시
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
        headerName: '벡터DB',
        field: 'vectorDB',
        width: 120,
        minWidth: 120,
        maxWidth: 120,
        suppressSizeToFit: true,
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
        headerName: '임베딩 모델',
        field: 'embadding',
        width: 260,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: '인덱스명',
        field: 'splitMethod',
        width: 120,
        minWidth: 120,
        maxWidth: 120,
        suppressSizeToFit: true,
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
        field: 'more', // 더보기 컬럼 필드명 (고정)
        width: 56,
      },
    ],
    [rowData]
  );

  // 탭 아이템 정의
  /* [251111_퍼블수정] 탭순서 변경 */
  const tabItems = [
    { id: 'knowledge', label: '지식' },
    { id: 'dataset', label: '학습 데이터세트' },
  ];

  const rowCardData = [
    {
      no: 1,
      id: '1',
      modelName: 'asdkansdknaskdnaskdnaksdnaksndklansdlkanwldknaslkdnalsdnalksndlaksndlkanwsdlknaslkdnalsdnalskdn',
      description: '',
      deployType: '활성화',
      vectorDb: 'MilvusMilvusMilvusMilvusMilvusMilvusMilvusMilvusMilvusMilvusMilvusMilvusMilvus',
      embeddingModel: 'aaaaaaasdasdasdasaaaaaaasdasdasdasaaaaaaasdasdasdasaaaaaaasdasdasdas',
      splitMethod: 'CustomCustomCustomCustomCustomCustomCustomCustomCustomCustomCustomCustom',
      createdDate: '20251110202511102025111020251110202511102025111020251110202511102025111020251110',
      modifiedDate: 'asdkasndkasndkansdknasdknaskdnaskdnalwkndlaksndlansdlkansdlkansdlkansldnaslkdnasld',
      // more: 'more',
      isActive: true,
    },
    {
      no: 2,
      id: '2',
      modelName: '신한 MCP 서버신한 MCP 서버신한 MCP 서버신한 MCP 서버신한 MCP 서버신한 MCP 서버',
      description: 'Anthropic의 고성능 AI 모델로, 창의적 글쓰기와 분석에 특화되어 있습니다.',
      deployType: '활성화',
      vectorDb: 'MilvusMilvusMilvusMilvusMilvusMilvusMilvusMilvusMilvusMilvusMilvusMilvusMilvus',
      embeddingModel: 'aaaaaaasdasdasdasaaaaaaasdasdasdasaaaaaaasdasdasdasaaaaaaasdasdasdas',
      splitMethod: 'CustomCustomCustomCustomCustomCustomCustomCustomCustomCustomCustomCustom',
      createdDate: '20251110202511102025111020251110202511102025111020251110202511102025111020251110',
      modifiedDate: 'asdkasndkasndkansdknasdknaskdnaskdnalwkndlaksndlansdlkansdlkansdlkansldnaslkdnasld',
      more: 'more',
      isActive: false,
    },
    {
      no: 3,
      id: '3',
      modelName: '신한 MCP 서버 1',
      description: '텍스트를 이미지로 변환하는 AI 모델입니다. 고품질 이미지 생성을 제공합니다.',
      deployType: '활성화',
      vectorDb: 'Milvus',
      embeddingModel: '인덱스명1',
      splitMethod: 'Custom Chunking',
      createdDate: '2024-01-05 13:45:00',
      modifiedDate: '2024-01-22 10:20:00',
      more: 'more',
      isActive: true,
    },
    {
      no: 4,
      id: '4',
      modelName: '신한 MCP 서버 1',
      description: '음성을 텍스트로 변환하는 음성 인식 모델입니다. 다국어를 지원합니다.',
      deployType: '실패',
      vectorDb: 'Milvus',
      embeddingModel: '인덱스명1',
      splitMethod: 'Custom Chunking',
      createdDate: '2024-01-12 08:30:00',
      modifiedDate: '2024-01-19 15:50:00',
      more: 'more',
      isActive: false,
    },
    {
      no: 5,
      id: '5',
      modelName: '신한 MCP 서버 1',
      description: 'Google의 양방향 트랜스포머 모델로, 자연어 처리 작업에 널리 사용됩니다.',
      deployType: '이용가능',
      vectorDb: 'Milvus',
      embeddingModel: '인덱스명1',
      splitMethod: 'Custom Chunking',
      createdDate: '2024-01-08 16:20:00',
      modifiedDate: '2024-01-21 12:35:00',
      more: 'more',
      isActive: true,
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
      {/* 섹션 페이지 */}
      <section className='section-page'>
        {/* 페이지 헤더 */}
        <UIPageHeader
          title='지식/학습 데이터 관리' // [251111_퍼블수정] 타이틀명칭 변경 : 데이터 카탈로그 > 지식/학습 데이터 관리
          description={[
            '생성형 AI 모델에 사용할 지식(RAG)과 학습을 위한 데이터 세트를 생성할 수 있습니다.',
            '데이터 만들기 버튼을 통해 데이터를 만들고 목록을 클릭하여 상세정보를 조회해 보세요',
          ]}
          actions={
            <>
            {/* [251222_퍼블수정]: btn-text-14-semibold-point > btn-text-18-semibold-point  (클래스명 변경 : 폰트 크기 수정함) */}
              <UIButton2 className='btn-text-18-semibold-point' leftIcon={{ className: 'ic-system-24-add', children: '' }}>
                데이터 만들기
              </UIButton2>
            </>
          }
        />

        {/* 페이지 바디 */}
        <UIPageBody>
          {/* 탭 영역 */}
          <UIArticle>
            <UITabs items={tabItems} activeId={activeTab} size='large' onChange={setActiveTab} />
          </UIArticle>

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
                        <td>
                          <div>
                            <UIInput.Search
                              value={searchValues.searchKeyword}
                              placeholder='검색어 입력'
                              onChange={e => setSearchValues(prev => ({ ...prev, searchKeyword: e.target.value }))}
                            />
                          </div>
                        </td>
                        <th style={{ width: '107px' }}>
                          <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                            상태
                          </UITypography>
                        </th>
                        <td>
                          <UIDropdown
                            value={searchValues.status}
                            placeholder='조회 조건 선택'
                            options={[
                              { value: '전체', label: '전체' },
                              { value: '아이템1', label: '아이템1' },
                              { value: '아이템2', label: '아이템2' },
                              { value: '아이템3', label: '아이템3' },
                            ]}
                            isOpen={dropdownStates.status}
                            onClick={() => handleDropdownToggle('status')}
                            onSelect={value => handleDropdownSelect('status', value)}
                          />
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
                          <UIDataCnt count={rowData.length} prefix='총' unit='건' />
                        </div>
                      </div>
                      <div className='flex items-center gap-[8px]'>
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
                    rowData={rowData}
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
                      const getStatusIntent = (status: string) => {
                        switch (status) {
                          case '이용 가능':
                            return 'complete';
                          case '진행중':
                            return 'progress';
                          case '실패':
                            return 'error';
                          default:
                            return 'complete';
                        }
                      };
                      return (
                        <UIGridCard
                          id={item.id}
                          title={item.modelName}
                          caption={item.description}
                          data={item}
                          moreMenuConfig={moreMenuConfig}
                          statusArea={
                            <UILabel variant='badge' intent={getStatusIntent(item.deployType)}>
                              {item.deployType}
                            </UILabel>
                          }
                          checkbox={{
                            checked: checkedCards[item.id] || false,
                            onChange: (checked: boolean, _value: string) => {
                              setCheckedCards(prev => ({
                                ...prev,
                                [item.id]: checked,
                              }));
                            },
                          }}
                          rows={[
                            { label: '백터DB', value: item.vectorDb },
                            { label: '임베딩 모델', value: item.embeddingModel },
                            { label: '인덱스명', value: item.splitMethod },
                          ]}
                        />
                      );
                    }}
                  />
                )}
              </UIListContentBox.Body>
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
