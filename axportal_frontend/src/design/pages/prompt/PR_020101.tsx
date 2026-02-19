import React, { useMemo, useState, memo } from 'react';

import { UIBox, UIButton2, UIDataCnt, UITypography } from '@/components/UI/atoms';
import { UIPagination } from '@/components/UI/atoms/UIPagination';
import { UITextLabel } from '@/components/UI/atoms/UITextLabel';
import { UIGroup, UIUnitGroup } from '@/components/UI/molecules';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { useModal } from '@/stores/common/modal';
import { UIDropdown } from '../../../components/UI/molecules/dropdown/UIDropdown';
import { DesignLayout } from '../../components/DesignLayout';
import { UIInput } from '@/components/UI/molecules/input';
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

export const PR_020101 = () => {
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

  // grid toggle
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

  // 샘플 데이터 (rowData로 변수명 변경)
  const rowData = [
    {
      id: '1',
      name: 'Test Few Shots',
      version: 'Lastest.1',
      publicStatus: '내부공유',
      tags: ['가나다라마바사아자', '가나다라마바사아자', '가나다라마바사아자', '가나다라마바사아자', '가나다라마바사아자'],
      connectedAgent: 2,
      createdDate: '2025.03.24 18:23:43',
      more: 'more',
    },
    {
      id: '2',
      name: 'Test Few Shots',
      version: 'Lastest.1',
      publicStatus: '내부공유',
      tags: ['가나다라마바사아', '가나다라마바사아'],
      connectedAgent: 2,
      createdDate: '2025.03.23 14:15:32',
      more: 'more',
    },
    {
      id: '3',
      name: 'Test Few Shots',
      version: 'Lastest.1',
      publicStatus: '내부공유',
      tags: ['Tag'],
      connectedAgent: 2,
      createdDate: '2025.03.22 09:45:21',
      more: 'more',
    },
    {
      id: '4',
      name: 'Test Few Shots',
      version: 'Lastest.1',
      publicStatus: '내부공유',
      tags: ['Tag'],
      connectedAgent: 2,
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
        field: 'name' as any,
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
        width: 230,
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
        headerName: '연결 에이전트',
        field: 'connectedAgent' as const,
        width: 230,
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

  // 그리드(카드형)
  const rowCardData = [
    {
      no: 1,
      id: '1',
      modelName: '신한 MCP 서버 1',
      description: '설명문구는 최대 한줄까지만 노출됩니다.',
      deployType: '이용가능',
      tagName: 'LLM',
      permission: 'Public',
      connectingAgent: '2',
      creationDate: '2024-01-20 14:25:00',
      statusLabels: [
        { text: 'Release Ver.1', intent: 'blue' },
        { text: 'Lastest.1', intent: 'gray' },
      ],
      more: 'more',
      isActive: true,
    },
    {
      no: 2,
      id: '2',
      modelName: '신한 MCP 서버 1',
      description: '설명문구는 최대 한줄까지만 노출됩니다.',
      deployType: '이용가능',
      tagName: 'LLM',
      permission: 'Private',
      connectingAgent: '2',
      creationDate: '2024-01-18 16:40:00',
      statusLabels: [
        { text: 'Release Ver.1', intent: 'blue' },
        { text: 'Lastest.1', intent: 'gray' },
      ],
      more: 'more',
      isActive: false,
    },
    {
      no: 3,
      id: '3',
      modelName: '신한 MCP 서버 1',
      description: '설명문구는 최대 한줄까지만 노출됩니다.',
      deployType: '진행중',
      tagName: 'Image Generation',
      permission: 'Public',
      connectingAgent: '2',
      creationDate: '2024-01-22 10:20:00',
      statusLabels: [
        { text: 'Release Ver.1', intent: 'blue' },
        { text: 'Lastest.1', intent: 'gray' },
      ],
      more: 'more',
      isActive: true,
    },
    {
      no: 4,
      id: '4',
      modelName: '신한 MCP 서버 1',
      description: '설명문구는 최대 한줄까지만 노출됩니다.',
      deployType: '실패',
      tagName: 'Speech Recognition',
      permission: 'Internal',
      connectingAgent: '2',
      creationDate: '2024-01-19 15:50:00',
      statusLabels: [
        { text: 'Release Ver.1', intent: 'blue' },
        { text: 'Lastest.1', intent: 'gray' },
      ],
      more: 'more',
      isActive: false,
    },
    {
      no: 5,
      id: '5',
      modelName: '신한 MCP 서버 1',
      description: '설명문구는 최대 한줄까지만 노출됩니다.',
      deployType: '이용가능',
      tagName: 'NLP',
      permission: 'Public',
      connectingAgent: '2',
      creationDate: '2024-01-21 12:35:00',
      statusLabels: [
        { text: 'Release Ver.1', intent: 'blue' },
        { text: 'Lastest.1', intent: 'gray' },
      ],
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
          title='퓨샷'
          description={['생성형 AI 모델의 사용자가 작성한 Q&A세트(퓨샷)를 등록할 수 있습니다.', '퓨샷을 등록하고 활용해 생성형 AI 모델이 더 풍부한 답변을 생성하도록 해보세요.']}
          actions={
            <>
              {/* [251222_퍼블수정]: btn-text-14-semibold-point > btn-text-18-semibold-point  (클래스명 변경 : 폰트 크기 수정함) */}
              <UIButton2 className='btn-text-18-semibold-point' leftIcon={{ className: 'ic-system-24-add', children: '' }}>
                퓨샷 등록
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
                          <div>
                            <UIInput.Search
                              value={searchValue}
                              placeholder='검색어 입력'
                              onChange={e => {
                                setSearchValue(e.target.value);
                              }}
                            />
                          </div>
                        </td>
                        <th>
                          <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                            태그
                          </UITypography>
                        </th>
                        <td>
                          <UIDropdown
                            value={'전체'}
                            placeholder='조회 조건 선택'
                            options={[
                              { value: '1', label: '전체' },
                              { value: '2', label: '아이템1' },
                              { value: '3', label: '아이템2' },
                              { value: '4', label: '아이템3' },
                            ]}
                            isOpen={dropdownStates.searchType}
                            onClick={() => handleDropdownToggle('searchType')}
                            onSelect={value => handleDropdownSelect('searchType', value)}
                          />
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
                <UIUnitGroup gap={16} direction='column'>
                  <div className='flex justify-between w-full items-center'>
                    <div className='flex-shrink-0'>
                      <div style={{ width: '168px', paddingRight: '8px' }}>
                        <UIDataCnt count={rowData.length} prefix='총' unit='건' />
                      </div>
                    </div>
                    <div className='flex' style={{ gap: '12px' }}>
                      <div style={{ width: '160px', flexShrink: 0 }}>
                        <UIDropdown
                          value={String(value)}
                          disabled={false}
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
                      <div style={{ flexShrink: 0 }}>
                        <UIToggle variant='dataView' checked={view === 'card'} onChange={checked => setView(checked ? 'card' : 'grid')} />
                      </div>
                    </div>
                  </div>
                </UIUnitGroup>
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
                          data={item}
                          moreMenuConfig={moreMenuConfig}
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
                            { label: '태그', value: item.tagName },
                            { label: '연결 에이전트', value: item.connectingAgent },
                            { label: '생성일시', value: item.creationDate },
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
