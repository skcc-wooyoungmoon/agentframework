import React, { useMemo, useState } from 'react';

import { UIBox, UIButton2, UIDataCnt, UILabel, UITextLabel, UITypography } from '@/components/UI/atoms';
import { UIPagination } from '@/components/UI/atoms/UIPagination';
import { UIGroup, UIInput, UIUnitGroup } from '@/components/UI/molecules';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { useModal } from '@/stores/common/modal';
import { UIDropdown } from '../../../components/UI/molecules/dropdown/UIDropdown';
import { UITabs } from '../../../components/UI/organisms/UITabs';
import { DesignLayout } from '../../components/DesignLayout';
import { UIToggle } from '@/components/UI';

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

export const DT_020201 = () => {
  const [, setActiveTab] = useState('dataset');
  const [value, setValue] = useState('12개씩 보기');
  const { openAlert } = useModal();
  const [, setSearchValues] = useState<SearchValues>({
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

  // search 타입
  const [searchValue, setSearchValue] = useState('');

  const [view, setView] = useState('grid');

  const [selectedCardIds, setSelectedCardIds] = useState<string[]>([]);

  // 그리드(카드형)
  // @ts-ignore - 임시주석처리
  const rowCardData = [
    // 임시주석처리
    // {
    //   no: 1,
    //   id: '1',
    //   modelName: '신한 MCP 서버 1',
    //   description: '정기예금 등 기본 수신상품에 대한 고객 질문과 상담',
    //   deployType: '이용가능',
    //   tagName: ['LLM', 'NLP', 'test', 'test', 'test', 'testtest', '테스트입니다테스트입니다', '테스트입니다테스트입니다'],
    //   category: '지도학습',
    //   permission: 'Public',
    //   createdDate: '2024-01-15 09:30:00',
    //   modifiedDate: '2024-01-20 14:25:00',
    //   more: 'more',
    //   isActive: true,
    // },
    // {
    //   no: 2,
    //   id: '2',
    //   modelName: '신한 MCP 서버 1',
    //   description: '정기예금 등 기본 수신상품에 대한 고객 질문과 상담',
    //   deployType: '이용가능',
    //   tagName: ['LLM'],
    //   category: '지도학습',
    //   permission: 'Private',
    //   createdDate: '날짜입력말줄임테스트Sample날짜입력말줄임테스트Sample',
    //   modifiedDate: '날짜입력말줄임테스트Sample날짜입력말줄임테스트Sample',
    //   more: 'more',
    //   isActive: false,
    // },
    // {
    //   no: 3,
    //   id: '3',
    //   modelName: '신한 MCP 서버 1',
    //   description: '정기예금 등 기본 수신상품에 대한 고객 질문과 상담',
    //   deployType: '진행중',
    //   tagName: ['LLM'],
    //   category: '지도학습',
    //   permission: 'Public',
    //   createdDate: '2024-01-05 13:45:00',
    //   modifiedDate: '2024-01-22 10:20:00',
    //   more: 'more',
    //   isActive: true,
    // },
    // {
    //   no: 4,
    //   id: '4',
    //   modelName: '신한 MCP 서버 1',
    //   description: '정기예금 등 기본 수신상품에 대한 고객 질문과 상담',
    //   deployType: '실패',
    //   tagName: ['test'],
    //   category: '지도학습',
    //   permission: 'Internal',
    //   createdDate: '2024-01-12 08:30:00',
    //   modifiedDate: '2024-01-19 15:50:00',
    //   more: 'more',
    //   isActive: false,
    // },
    // {
    //   no: 5,
    //   id: '5',
    //   modelName: '신한 MCP 서버 1',
    //   description: '정기예금 등 기본 수신상품에 대한 고객 질문과 상담',
    //   deployType: '이용가능',
    //   tagName: ['test'],
    //   category: '지도학습',
    //   permission: 'Public',
    //   createdDate: '2024-01-08 16:20:00',
    //   modifiedDate: '2024-01-21 12:35:00',
    //   more: 'more',
    //   isActive: true,
    // },
  ];

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
              message: `테스트 "${rowData.userName}" 수정 팝업을 엽니다.`,
            });
          },
        },
        {
          label: '삭제',
          action: 'delete',
          onClick: (rowData: any) => {
            openAlert({
              title: '안내',
              message: `테스트 "${rowData.userName}" 삭제 화면으로 이동합니다.`,
            });
          },
        },
      ],
      isActive: () => true, // 모든 테스트에 대해 활성화
    }),
    []
  );

  // 샘플 데이터 (rowData로 변수명 변경)
  const rowData = [
    {
      id: '1',
      accountStatus: '이용 가능',
      userName: '김신한',
      department: 'Data기획Unit',
      status: '이용 가능',
      description: '정기예금 등 기본 수신상품에 대한 고객 질문과 상담',
      publicStatus: '전체공유',
      tags: ['가나다라마바사아자', '가나다라마바사아자', '가나다라마바사아자', '가나다라마바사아자', '가나다라마바사아자'],
      catogory: '지도학습',
      createdDate: '2025.03.24 18:23:43',
      modifiedDate: '2025.03.24 18:23:43',
      more: 'more',
    },
    {
      id: '2',
      accountStatus: '이용 불가',
      userName: '이영희',
      department: 'AI개발팀',
      status: '이용 가능',
      description: '정기예금 등 기본 수신상품에 대한 고객 질문과 상담',
      publicStatus: '전체공유',
      tags: ['가나다라마바사아', '가나다라마바사아'],
      catogory: '지도학습',
      createdDate: '2025.03.23 14:15:32',
      modifiedDate: '2025.03.23 14:15:32',
      more: 'more',
    },
    {
      id: '3',
      accountStatus: '이용 가능',
      userName: '박철수',
      department: 'Data기획Unit',
      status: '휴직',
      description: '정기예금 등 기본 수신상품에 대한 고객 질문과 상담',
      publicStatus: '전체공유',
      tags: ['test'],
      catogory: '지도학습',
      createdDate: '2025.03.22 09:45:21',
      modifiedDate: '2025.03.22 09:45:21',
      more: 'more',
    },
    {
      id: '4',
      accountStatus: '진행중',
      userName: '최민수',
      department: 'AI개발팀',
      status: '재직',
      description: '정기예금 등 기본 수신상품에 대한 고객 질문과 상담',
      publicStatus: '전체공유',
      tags: ['test'],
      catogory: '지도학습',
      createdDate: '2025.03.24 16:30:15',
      modifiedDate: '2025.03.24 16:30:15',
      more: 'more',
    },
    {
      id: '5',
      accountStatus: '실패',
      userName: '정다은',
      department: 'Data분석팀',
      status: '퇴사',
      description: '정기예금 등 기본 수신상품에 대한 고객 질문과 상담',
      publicStatus: '전체공유',
      tags: ['test'],
      catogory: '지도학습',
      createdDate: '2025.03.20 11:20:43',
      modifiedDate: '2025.03.20 11:20:43',
      more: 'more',
    },
    {
      id: '6',
      accountStatus: '취소',
      userName: '홍길동',
      department: 'Data분석팀',
      status: '재직',
      description: '정기예금 등 기본 수신상품에 대한 고객 질문과 상담',
      publicStatus: '전체공유',
      tags: ['test'],
      catogory: '지도학습',
      createdDate: '2025.03.24 13:55:28',
      modifiedDate: '2025.03.24 13:55:28',
      more: 'more',
    },
    {
      id: '7',
      accountStatus: '실패',
      userName: '김미영',
      department: 'AI개발팀',
      status: '재직',
      description: '정기예금 등 기본 수신상품에 대한 고객 질문과 상담',
      publicStatus: '전체공유',
      tags: ['test'],
      catogory: '지도학습',
      createdDate: '2025.03.24 17:42:11',
      modifiedDate: '2025.03.24 17:42:11',
      more: 'more',
    },
  ];

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
        field: 'userName' as any,
        width: 190,
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
        field: 'accountStatus' as any,
        width: 120,
        cellRenderer: React.memo((params: any) => {
          const statusColors = {
            이용가능: 'complete',
            실패: 'error',
            진행중: 'progress',
            취소: 'stop',
          } as const;
          return (
            <UILabel variant='badge' intent={statusColors[params.value as keyof typeof statusColors]}>
              {params.value}
            </UILabel>
          );
        }),
      },
      {
        headerName: '설명',
        field: 'description',
        minWidth: 300,
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
        headerName: '공개범위',
        field: 'publicStatus',
        width: 120,
      },
      // 251107_퍼블수정 그리드 컬럼 속성 '태그' 영역 수정 S
      {
        headerName: '태그',
        field: 'tags' as const,
        width: 230,
        cellStyle: { paddingLeft: '16px' },
        cellRenderer: (params: any) => {
          if (!params.value || !Array.isArray(params.value) || params.value.length === 0) {
            return null;
          }
          const tagText = params.value.join(', ');
          return (
            <div title={tagText}>
              <div className='flex gap-1'>
                {params.value.slice(0, 2).map((tag: string, index: number) => (
                  <UITextLabel key={index} intent='tag' className='nowrap'>
                    {tag}
                  </UITextLabel>
                ))}
              </div>
            </div>
          );
        },
      },
      // 251107_퍼블수정 그리드 컬럼 속성 '태그' 영역 수정 E
      {
        headerName: '유형',
        field: 'catogory',
        width: 120,
      },
      {
        headerName: '생성일시',
        field: 'createdDate' as any,
        width: 180,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: '최종 수정일시',
        field: 'modifiedDate' as any,
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
    { id: 'information', label: '지식' },
    { id: 'dataset', label: '학습데이터세트' },
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
            '데이터 만들기 버튼을 통해 데이터를 만들고 목록을 클릭하여 상세정보를 조회해 보세요.',
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
          <UIArticle className='article-tabs'>
            {/* 아티클 탭 */}
            <UITabs items={tabItems} activeId='dataset' size='large' onChange={setActiveTab} />
          </UIArticle>

          <UIArticle className='article-filter'>
            {/* [251104_퍼블수정] */}
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
                          <div className='flex-1' style={{ zIndex: '10' }}>
                            <UIInput.Search
                              value={searchValue}
                              onChange={e => {
                                setSearchValue(e.target.value);
                              }}
                              placeholder='검색어 입력'
                            />
                          </div>
                        </td>
                        <th>
                          <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                            유형
                          </UITypography>
                        </th>
                        <td>
                          <div>
                            <UIDropdown
                              value={'전체'}
                              placeholder='조회 조건 선택'
                              options={[
                                { value: '1', label: '전체' },
                                { value: '2', label: '지도학습' },
                                { value: '3', label: '비지도학습' },
                                { value: '4', label: 'DPO' },
                                { value: '5', label: 'Custom' },
                              ]}
                              isOpen={dropdownStates.searchType}
                              onClick={() => handleDropdownToggle('searchType')}
                              onSelect={value => handleDropdownSelect('searchType', value)}
                            />
                          </div>
                        </td>
                      </tr>
                      <tr>
                        <th>
                          <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                            상태
                          </UITypography>
                        </th>
                        <td>
                          <div>
                            <UIDropdown
                              value={'전체'}
                              placeholder='조회 조건 선택'
                              options={[
                                { value: '1', label: '전체' },
                                { value: '2', label: '이용가능' },
                                { value: '3', label: '실패' },
                                { value: '4', label: '취소' },
                                { value: '5', label: '진행중' },
                              ]}
                              isOpen={dropdownStates.searchType}
                              onClick={() => handleDropdownToggle('searchType')}
                              onSelect={value => handleDropdownSelect('searchType', value)}
                            />
                          </div>
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
                      <div className='flex items-center gap-2'>
                        <div style={{ width: '160px', flexShrink: 0 }}>
                          <UIDropdown
                            value={String(value)}
                            options={[
                              { value: '1', label: '12개씩 보기' },
                              { value: '2', label: '36개씩 보기' },
                              { value: '3', label: '60개씩 보기' },
                            ]}
                            onSelect={(_value: string) => {
                              setValue(value);
                            }}
                            onClick={() => {}}
                            height={40}
                            variant='dataGroup'
                            disabled={true}
                          />
                        </div>
                        {/* [251110_퍼블수정] : 그리드 조회된 값이 없을경우 버튼 > disabled={true} 추가 */}
                        <UIToggle variant='dataView' checked={view === 'card'} onChange={checked => setView(checked ? 'card' : 'grid')} disabled={true} />
                      </div>
                    </div>
                  </UIUnitGroup>
                </div>
              </UIListContentBox.Header>
              <UIListContentBox.Body>
                {view === 'grid' ? (
                  <UIGrid
                    type='multi-select'
                    // rowData={rowData}
                    // columnDefs={columnDefs}
                    // [251110_퍼블수정] : 그리드 조회된 값이 없을경우 버튼 > rowData={[]}
                    rowData={[]}
                    columnDefs={columnDefs}
                    moreMenuConfig={moreMenuConfig}
                    onClickRow={(_params: any) => {}}
                    onCheck={(_selectedIds: any[]) => {}}
                  />
                ) : (
                  <UICardList
                    rowData={[]}
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
                            checked: selectedCardIds.includes(item.id),
                            onChange: (checked: boolean) => {
                              if (checked) {
                                setSelectedCardIds([...selectedCardIds, item.id]);
                              } else {
                                setSelectedCardIds(selectedCardIds.filter(id => id !== item.id));
                              }
                            },
                          }}
                          rows={[
                            { label: '태그', value: Array.isArray(item.tagName) ? item.tagName.join(', ') : item.tagName },
                            { label: '유형', value: item.category },
                            { label: '생성일시', value: item.createdDate },
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
                {/* [251110_퍼블수정] : 그리드 조회된 값이 없을경우 버튼 > disabled={true} 추가 */}
                <UIButton2 className='btn-option-outlined' style={{ width: '40px' }} disabled={true}>
                  삭제
                </UIButton2>
                <UIPagination currentPage={1} totalPages={1} onPageChange={() => {}} className='flex justify-center' disabled={rowData.length === 0} />
                {/* [251110_퍼블수정] : 그리드 조회된 값이 없을경우 > 그리드 - 상단 컬럼 영역은 조회된 값 없으면 자동으로 disabled 처리됩니다. */}
                {/* [251110_퍼블수정] : 그리드 조회된 값이 없을경우 UIPagination > totalPages={1} 조회된 값이 없어도 1 은 노출됨 기획자 확인 /  disabled={rowData.length === 0} 추가 */}
              </UIListContentBox.Footer>
            </UIListContainer>
          </UIArticle>
        </UIPageBody>
      </section>
    </DesignLayout>
  );
};
