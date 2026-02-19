import React, { useMemo, useState, memo } from 'react';

import { UIBox, UIButton2, UIDataCnt, UITypography } from '@/components/UI/atoms';
import { UIPagination } from '@/components/UI/atoms/UIPagination';
import { UITextLabel } from '@/components/UI/atoms/UITextLabel';
import { UIGroup, UIInput, UIUnitGroup } from '@/components/UI/molecules';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { useModal } from '@/stores/common/modal';
import { UIDropdown } from '../../../components/UI/molecules/dropdown/UIDropdown';
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

export const PR_010101 = () => {
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

  const [selectedCardIds, setSelectedCardIds] = useState<string[]>([]);

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

  // 그리드(카드형)
  const rowCardData = [
    {
      no: 1,
      id: '1',
      modelName: '최대 두줄까지 타이틀 영역에 노출됩니다.최대 두줄까지 타이틀 영역에 노출됩니다.',
      tags: ['LLM'],
      connectedAgent: '2',
      createdAt: '2024-01-15 09:30:00',
      statusLabels: [
        { text: 'Release Ver.1', intent: 'blue' },
        { text: 'Lastest.1', intent: 'gray' },
      ],
      more: 'more',
    },
    {
      no: 2,
      id: '2',
      modelName: '최대 두줄까지 타이틀 영역에 노출됩니다.최대 두줄까지 타이틀 영역에 노출됩니다.',
      tags: ['LLM'],
      connectedAgent: '2',
      createdAt: '2024-01-10 11:15:00',
      statusLabels: [
        { text: 'Release Ver.1', intent: 'blue' },
        { text: 'Lastest.1', intent: 'gray' },
      ],
      more: 'more',
    },
    {
      no: 3,
      id: '3',
      modelName: '최대 두줄까지 타이틀 영역에 노출됩니다.최대 두줄까지 타이틀 영역에 노출됩니다.',
      tags: ['Image Generation'],
      connectedAgent: '2',
      createdAt: '2024-01-05 13:45:00',
      statusLabels: [
        { text: 'Release Ver.1', intent: 'blue' },
        { text: 'Lastest.1', intent: 'gray' },
      ],
      more: 'more',
    },
    {
      no: 4,
      id: '4',
      modelName: '최대 두줄까지 타이틀 영역에 노출됩니다.최대 두줄까지 타이틀 영역에 노출됩니다.',
      tags: ['Speech Recognition'],
      connectedAgent: '2',
      createdAt: '2024-01-12 08:30:00',
      statusLabels: [
        { text: 'Release Ver.1', intent: 'blue' },
        { text: 'Lastest.1', intent: 'gray' },
      ],
      more: 'more',
    },
    {
      no: 5,
      id: '5',
      modelName: '최대 두줄까지 타이틀 영역에 노출됩니다.최대 두줄까지 타이틀 영역에 노출됩니다.',
      tags: ['NLP'],
      connectedAgent: '2',
      createdAt: '2024-01-08 16:20:00',
      statusLabels: [
        { text: 'Release Ver.1', intent: 'blue' },
        { text: 'Lastest.1', intent: 'gray' },
      ],
      more: 'more',
    },
  ];

  // 샘플 데이터 (rowData로 변수명 변경)
  const rowData = [
    {
      id: '1',
      userName: '김신한',
      department: 'Data기획Unit',
      publicRange: '전체공유',
      tags: ['가나다라마바사아자', '가나다라마바사아자', '가나다라마바사아자', '가나다라마바사아자', '가나다라마바사아자'],
      version: 'Release Ver.1',
      connectedAgent: '1',
      catogory: '채팅',
      createdDate: '2025.03.24 18:23:43',
      more: 'more',
    },
    {
      id: '2',
      userName: '이영희',
      department: 'AI개발팀',
      publicRange: '전체공유',
      tags: ['가나다라마바사아', '가나다라마바사아'],
      version: 'Release Ver.1',
      connectedAgent: '1',
      catogory: '채팅',
      createdDate: '2025.03.23 14:15:32',
      more: 'more',
    },
    {
      id: '3',
      userName: '박철수',
      department: 'Data기획Unit',
      publicRange: '전체공유',
      tags: ['test'],
      version: 'Release Ver.1',
      connectedAgent: '1',
      catogory: '채팅',
      createdDate: '2025.03.22 09:45:21',
      more: 'more',
    },
    {
      id: '4',
      userName: '최민수',
      department: 'AI개발팀',
      publicRange: '전체공유',
      tags: ['test'],
      version: 'Release Ver.1',
      connectedAgent: '1',
      catogory: '채팅',
      createdDate: '2025.03.24 16:30:15',
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
        flex: 1,
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
        headerName: '버전',
        field: 'version' as const,
        width: 238,
        cellStyle: { paddingLeft: '16px' },
        cellRenderer: memo((params: any) => {
          return (
            <div className='flex items-center gap-[8px]'>
              <UITextLabel intent='blue'>{params.value}</UITextLabel>
              <UITextLabel intent='gray'>Latest.1</UITextLabel>
            </div>
          );
        }),
      },
      {
        headerName: '유형',
        field: 'catogory',
        width: 120,
      },
      {
        headerName: '공개범위',
        field: 'publicRange',
        width: 120,
        cellStyle: {
          paddingLeft: '16px',
        },
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
        headerName: '연결 에이전트',
        field: 'connectedAgent' as const,
        width: 120,
        cellStyle: { paddingLeft: '16px' },
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
        headerName: '',
        field: 'more', // 더보기 컬럼 필드명 (고정)
        width: 56,
      },
    ],
    [rowData]
  );

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
          title='추롬 프롬프트'
          description={['생성형 AI 모델의 입력으로 사용할 프롬프트를 등록하고 버전별로 등록할 수 있습니다.', '제공된 프롬프트 템플릿을 사용해서 손쉽게 프롬프트를 생성해 보세요.']}
          actions={
            <>
              {/* [251222_퍼블수정]: btn-text-14-semibold-point > btn-text-18-semibold-point  (클래스명 변경 : 폰트 크기 수정함) */}
              <UIButton2 className='btn-text-18-semibold-point' leftIcon={{ className: 'ic-system-24-add', children: '' }}>
                프롬프트 등록
              </UIButton2>
            </>
          }
        />

        {/* 페이지 바디 */}
        <UIPageBody>
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
                          <div className='flex-1'>
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
                            태그
                          </UITypography>
                        </th>
                        <td>
                          {/* [251104_퍼블수정] : style={{ width: '540px' }} 인라인 스타일 삭제 > className='flex-1' 추가 */}
                          <div className='flex-1'>
                            <UIDropdown
                              value={'전체'}
                              placeholder='조회 조건 선택'
                              options={[
                                { value: '1', label: '전체' },
                                { value: '2', label: '아이템1' },
                                { value: '3', label: '아이템2' },
                                { value: '4', label: '아이템3' },
                                { value: '5', label: '아이템4' },
                                { value: '6', label: '아이템5' },
                                { value: '7', label: '아이템6' },
                                { value: '8', label: '아이템7' },
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
                      return (
                        <UIGridCard
                          id={item.id}
                          title={item.modelName}
                          caption={item.description}
                          data={item} // 카드형 더보기 추가시
                          moreMenuConfig={moreMenuConfig} // 카드형 더보기 추가시
                          statusArea={
                            <UIGroup gap={8} direction='row'>
                              {item.statusLabels?.map((label: { text: string; intent: string }, index: number) => (
                                <UITextLabel key={index} intent={label.intent as any}>
                                  {label.text}
                                </UITextLabel>
                              ))}
                            </UIGroup>
                          }
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
                            { label: '태그', value: item.tag },
                            { label: '연결 에이전트', value: item.connectedAgent },
                            { label: '생성일시', value: item.createdAt },
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
                <UIPagination currentPage={1} totalPages={10} onPageChange={() => {}} className='flex justify-center' />
              </UIListContentBox.Footer>
            </UIListContainer>
          </UIArticle>
        </UIPageBody>
      </section>
    </DesignLayout>
  );
};
