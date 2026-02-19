import React, { useMemo, useState } from 'react';

import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UITextLabel } from '@/components/UI/atoms/UITextLabel';
import { UIGroup, UIPageFooter } from '@/components/UI/molecules';
import { UIDataCnt, UIToggle } from '@/components/UI';
import { UIPagination } from '@/components/UI/atoms/UIPagination';
import { UIInput } from '@/components/UI/molecules';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { UIGrid } from '@/components/UI/molecules/grid/UIGrid/component';
import { UIListContainer } from '@/components/UI/molecules/list/UIListContainer/component';
import { UIListContentBox } from '@/components/UI/molecules/list/UIListContentBox';
import { UIDropdown } from '../../../components/UI/molecules/dropdown/UIDropdown';
import { UITabs } from '../../../components/UI/organisms/UITabs';
import { UIBox, UIButton2, UITypography } from '@/components/UI/atoms';

import { UIGridCard } from '@/components/UI/molecules/card/UIGridCard';
import { DesignLayout } from '../../components/DesignLayout';
import { useModal } from '@/stores/common/modal';

import { UICardList } from '@/components/UI/molecules/card/UICardList';

export const DT_030501 = () => {
  const { openAlert } = useModal();
  const [, setActiveTab] = useState('tab2');
  const [selectedCardIds, setSelectedCardIds] = useState<string[]>([]);
  // 드롭다운 상태 관리
  const [dropdownStates, setDropdownStates] = useState({
    dateType: false,
    searchType: false,
    status: false,
    publicRange: false,
  });

  const handleMoreClick = (_itemId: string) => {
    // 추후 더보기 메뉴 또는 모달 표시 로직 추가
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
      isActive: () => true, // 모든 항목에 대해 활성화
    }),
    [openAlert]
  );

  // 드롭다운 핸들러
  const handleDropdownToggle = (key: keyof typeof dropdownStates) => {
    setDropdownStates(prev => ({
      ...Object.keys(prev).reduce((acc, k) => ({ ...acc, [k]: false }), {} as typeof prev),
      [key]: !prev[key],
    }));
  };

  const handleDropdownSelect = (key: string, _value: string) => {
    setDropdownStates(prev => ({ ...prev, [key]: false }));
  };

  // search 타입
  const [searchValue, setSearchValue] = useState('');

  // rowData 정의
  const rowData = [
    {
      id: 1,
      name: '[Azure Document Intelligence] axplatform-doc-intelligence-dev',
      deployType: 'Elastic Search',
      createdDate: '2025.03.24 18:23:43',
      modifiedDate: '2025.03.24 18:23:43',
      more: 'more',
      isDefault: true,
    },
    {
      id: 2,
      name: 'h-test-2',
      deployType: 'Elastic Search',
      createdDate: '2025.03.24 18:23:43',
      modifiedDate: '2025.03.24 18:23:43',
      more: 'more',
      isDefault: false,
    },
    {
      id: 3,
      name: 'DoclingServe',
      deployType: 'Elastic Search',
      createdDate: '2025.03.24 18:23:43',
      modifiedDate: '2025.03.24 18:23:43',
      more: 'more',
      isDefault: true,
    },
    {
      id: 4,
      name: '123',
      deployType: 'Elastic Search',
      createdDate: '2025.03.24 18:23:43',
      modifiedDate: '2025.03.24 18:23:43',
      more: 'more',
      isDefault: false,
    },
    {
      id: 5,
      name: 'h-test',
      deployType: 'Elastic Search',
      createdDate: '2025.03.24 18:23:43',
      modifiedDate: '2025.03.24 18:23:43',
      more: 'more',
      isDefault: true,
    },
    {
      id: 6,
      name: 'h-test-3',
      deployType: 'Elastic Search',
      createdDate: '2025.03.24 18:23:43',
      modifiedDate: '2025.03.24 18:23:43',
      more: 'more',
      isDefault: false,
    },
  ];

  // 그리드 컬럼 정의
  const columnDefs: any = useMemo(
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
          textAlign: 'center' as const,
          display: 'flex' as const,
          alignItems: 'center' as const,
          justifyContent: 'center' as const,
        },
        sortable: false,
        suppressHeaderMenuButton: true,
        suppressSizeToFit: true,
      },
      // 251113_퍼블수정 그리드 컬럼 속성 '이름' 영역 수정 S
      {
        headerName: '이름',
        field: 'name',
        minWidth: 624,
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
        headerName: '유형',
        field: 'deployType',
        width: 187,
      },
      {
        headerName: '기본설정',
        field: 'defaultConfig',
        minWidth: 120,
        cellRenderer: (params: any) => {
          return (
            <div className='flex gap-1 flex-wrap'>
              {params.data.isDefault === true ? (
                // <UILabel variant='line' intent='blue'>
                //   True
                // </UILabel>
                //  [251104_퍼블수정] : UILabel > UITextLabel 변경
                <UITextLabel intent='blue'>True</UITextLabel>
              ) : (
                <UITextLabel intent='gray'>False</UITextLabel>
                // <UILabel variant='line' intent='gray'>
                //   False
                // </UILabel>
              )}
            </div>
          );
        },
      },
      {
        headerName: '생성일시',
        field: 'createdDate' as const,
        width: 180,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '최종 수정일시',
        field: 'modifiedDate' as const,
        width: 180,
        cellStyle: { paddingLeft: '16px' },
      },

      {
        headerName: '',
        field: 'more', // 더보기 컬럼 필드명 (고정)
        width: 56,
      },
    ],
    [handleMoreClick]
  );
  const [view, setView] = useState('grid');

  // 탭 아이템 정의
  const tabItems = [
    { id: 'tab1', label: '벡터 DB' },
    { id: 'tab2', label: '프로세서' },
  ];

  // 그리드(카드형)
  const rowCardData = [
    {
      no: 1,
      id: '1',
      modelName: '신한 MCP 서버 1',
      deployType: '이용가능',
      tagName: 'LLM',
      permission: 'Public',
      createdDate: '2024-01-15 09:30:00',
      modifiedDate: '2024-01-20 14:25:00',
      statusTrue: true,
      // more: 'more',
      isActive: true,
    },
    {
      no: 2,
      id: '2',
      modelName: '신한 MCP 서버 1',
      deployType: '이용가능',
      tagName: 'LLM',
      permission: 'Private',
      createdDate: '2024-01-10 11:15:00',
      modifiedDate: '2024-01-18 16:40:00',
      statusFalse: true,
      more: 'more',
      isActive: false,
    },
    {
      no: 3,
      id: '3',
      modelName: '신한 MCP 서버 1',
      deployType: '진행중',
      tagName: 'Image Generation',
      permission: 'Public',
      createdDate: '2024-01-05 13:45:00',
      modifiedDate: '2024-01-22 10:20:00',
      statusFalse: true,
      more: 'more',
      isActive: true,
    },
    {
      no: 4,
      id: '4',
      modelName: '신한 MCP 서버 1',
      deployType: '실패',
      tagName: 'Speech Recognition',
      permission: 'Internal',
      createdDate: '2024-01-12 08:30:00',
      modifiedDate: '2024-01-19 15:50:00',
      statusTrue: true,
      more: 'more',
      isActive: false,
    },
    {
      no: 5,
      id: '5',
      modelName: '신한 MCP 서버 1',
      deployType: '이용가능',
      tagName: 'NLP',
      permission: 'Public',
      createdDate: '2024-01-08 16:20:00',
      modifiedDate: '2024-01-21 12:35:00',
      statusTrue: true,
      more: 'more',
      isActive: true,
    },
  ];

  return (
    <DesignLayout
      initialMenu={{ id: 'data', label: '데이터' }}
      initialSubMenu={{
        id: 'data-catalog',
        label: '식/학습 데이터 관리', // [251111_퍼블수정] 타이틀명칭 변경 : 데이터 카탈로그 > 지식/학습 데이터 관리
        icon: 'ico-lnb-menu-20-data-catalog',
      }}
    >
      {/* 섹션 페이지 */}
      <section className='section-page'>
        <UIPageHeader
          title='데이터 도구'
          description='데이터세트와 지식에 사용할 로더, 백터DB를 관리할 수 있습니다.'
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
            {/* 아티클 탭 */}
            <UITabs items={tabItems} activeId='tab1' size='large' onChange={setActiveTab} />
          </UIArticle>
          <UIArticle className='article-filter'>
            <UIBox className='box-filter'>
              <UIGroup gap={40} direction='row'>
                {/* [251104_퍼블수정] : 아래 div 태그에 style={{ width: 'calc(100% - 168px)' }}  추가  */}
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
                          <UIInput.Search
                            value={searchValue}
                            onChange={e => {
                              setSearchValue(e.target.value);
                            }}
                            placeholder='검색어 입력'
                          />
                        </td>
                        <th>
                          <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                            유형
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
            {/* 다중 선택 그리드 */}
            <UIListContainer>
              <UIListContentBox.Header>
                <div className='flex-shrink-0'>
                  <UIGroup gap={8} direction='row' align='start'>
                    <div style={{ width: '168px', paddingRight: '8px' }}>
                      <UIDataCnt count={6} prefix='총' unit='건' />
                    </div>
                  </UIGroup>
                </div>
                <div className='flex items-center gap-2'>
                  <div style={{ width: '160px', flexShrink: 0 }}>
                    <UIDropdown
                      value={'12개씩 보기'}
                      options={[
                        { value: '1', label: '12개씩 보기' },
                        { value: '2', label: '36개씩 보기' },
                        { value: '3', label: '60개씩 보기' },
                      ]}
                      onSelect={(_value: string) => {}}
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
                              {item.statusTrue && <UITextLabel intent='blue'>true</UITextLabel>}
                              {item.statusFalse && <UITextLabel intent='gray'>false</UITextLabel>}
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
                            { label: '유형', value: item.tagName },
                            { label: '생성일시', value: item.createdDate },
                            { label: '최종수정일시', value: item.modifiedDate },
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
                <UIPagination currentPage={1} totalPages={1} onPageChange={() => {}} className='flex justify-center' />
              </UIListContentBox.Footer>
            </UIListContainer>
          </UIArticle>
        </UIPageBody>
        <UIPageFooter></UIPageFooter>
      </section>
    </DesignLayout>
  );
};
