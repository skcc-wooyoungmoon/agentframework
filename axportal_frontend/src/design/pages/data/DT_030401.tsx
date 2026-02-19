import React, { useState, useMemo } from 'react';

// import { useNavigate } from 'react-router-dom';

import { UIInput, UIDropdown, UIArticle, UIUnitGroup, UIGroup } from '@/components/UI/molecules';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { useModal } from '@/stores/common/modal';

import { UIButton2, UITypography } from '../../../components/UI/atoms';
import { UIBox } from '../../../components/UI/atoms/UIBox';
import { UIDataCnt } from '../../../components/UI/atoms/UIDataCnt';
import { UIPagination } from '../../../components/UI/atoms/UIPagination';
import { UIToggle } from '../../../components/UI/atoms/UIToggle';
import { UIGridCard } from '@/components/UI/molecules/card/UIGridCard';
import { UITabs } from '../../../components/UI/organisms/UITabs';
import { DesignLayout } from '../../components/DesignLayout';
import { UICardList } from '@/components/UI/molecules/card/UICardList';

interface SearchValues {
  dateType: string;
  dateRange: {
    startDate: string;
    endDate: string;
  };
  searchType: string;
  searchKeyword: string;
  status: string;
  modelType: string;
}

export const DT_030401 = () => {
  const { openAlert } = useModal();
  const [activeTab, setActiveTab] = useState('scripts');
  const [selectedCardIds, setSelectedCardIds] = useState<string[]>([]);
  // const navigate = useNavigate();

  const [searchValues, setSearchValues] = useState<SearchValues>({
    dateType: '생성일시',
    dateRange: { startDate: '2025.06.30', endDate: '2025.07.30' },
    searchType: '이름',
    searchKeyword: '',
    status: '전체',
    modelType: '전체',
  });

  // 드롭다운 상태 관리
  const [dropdownStates, setDropdownStates] = useState({
    dateType: false,
    searchType: false,
    status: false,
    modelType: false,
  });

  const handleSearch = () => {
  };

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

  const [view, setView] = useState('grid');

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

  const statusOptions = [
    { value: '전체', label: '전체' },
    { value: '이용 가능', label: '이용 가능' },
    { value: '이용 불가', label: '이용 불가' },
  ];

  const handleDataAdd = () => {
  };

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
        headerName: '설명',
        field: 'description',
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
        headerName: '유형',
        field: 'catogory',
        width: 187,
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
        field: 'more', // 더보기 컬럼 필드명 (고정)
        width: 56,
      },
    ],
    [sampleData]
  );

  // 그리드(카드형)
  const rowCardData = [
    {
      no: 1,
      id: '1',
      modelName: '[MI] report loader(json)',
      description: 'CSV 파일을 한글이 깨지지 않도록 문자열로 읽기',
      type: 'Loader',
      createdDate: '2024-01-15 09:30:00',
      modifiedDate: '2024-01-20 14:25:00',
    },
    {
      no: 2,
      id: '2',
      modelName: '[MI] report loader(json)',
      description: 'CSV 파일을 한글이 깨지지 않도록 문자열로 읽기',
      type: 'Loader',
      createdDate: '2024-01-10 11:15:00',
      modifiedDate: '2024-01-18 16:40:00',
    },
  ];

  // 탭 아이템 정의
  const tabItems = [
    { id: 'processer', label: '프로세서' },
    { id: 'tools', label: 'Ingestion Tools' },
    { id: 'scripts', label: 'Custom Scripts' },
    { id: 'vector', label: 'Vector DB' },
  ];

  return (
    <DesignLayout
      initialMenu={{ id: 'data', label: '데이터' }}
      initialSubMenu={{
        id: 'data-catalog',
        label: '데이터 도구',
        icon: 'ico-lnb-menu-20-data-catalog',
      }}
    >
      <section className='section-page'>
        <UIPageHeader
          title='데이터 도구'
          description='데이터세트와 지식에 사용할 로더, 백터DB를 관리할 수 있습니다.'
          actions={
            <>
              <UIButton2 className='btn-text-14-semibold-point' leftIcon={{ className: 'ic-system-24-add', children: '' }} onClick={handleDataAdd}>
                데이터 도구 만들기
              </UIButton2>
            </>
          }
        />

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
                            유형
                          </UITypography>
                        </th>
                        <td>
                          <UIDropdown
                            value={searchValues.status}
                            placeholder='조회 조건 선택'
                            options={statusOptions}
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
                          <UIDataCnt count={sampleData.length} prefix='총' unit='건' />
                        </div>
                      </div>
                      <div className='flex items-center gap-[8px]'>
                        <div style={{ width: '160px', flexShrink: 0 }}>
                          <UIDropdown
                            value='12개씩 보기'
                            options={[
                              { value: '1', label: '12개씩 보기' },
                              { value: '2', label: '36개씩 보기' },
                              { value: '3', label: '60개씩 보기' },
                            ]}
                            onSelect={(_value: string) => {
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
                    rowData={sampleData}
                    columnDefs={columnDefs}
                    moreMenuConfig={moreMenuConfig}
                    onClickRow={(_params: any) => {}}
                    onCheck={(_selectedIds: any[]) => {
                    }}
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
                          data={item} // 카드형 더보기 추가시
                          moreMenuConfig={moreMenuConfig} // 카드형 더보기 추가시
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
                            { label: '유형', value: item.type },
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
