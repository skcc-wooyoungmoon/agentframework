import React, { useState, useMemo } from 'react';

import { UIButton2, UIDataCnt } from '@/components/UI/atoms';
import { UIBox } from '@/components/UI/atoms/UIBox';
import { UIPagination } from '@/components/UI/atoms/UIPagination';
import { UIToggle } from '@/components/UI/atoms/UIToggle';
import { UITypography } from '@/components/UI/atoms/UITypography';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIInput } from '@/components/UI/molecules/input';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIGroup } from '@/components/UI/molecules/UIGroup';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { UIUnitGroup } from '@/components/UI/molecules/UIUnitGroup';
import { useModal } from '@/stores/common/modal';

import { UIDropdown } from '../../../components/UI/molecules/dropdown/UIDropdown';
import { DesignLayout } from '../../components/DesignLayout';
import { UIGridCard } from '@/components/UI/molecules/card/UIGridCard';
import { UICardList } from '@/components/UI/molecules/card/UICardList';

interface SearchValues {
  dateType: string;
  dateRange: { startDate?: string; endDate?: string };
  searchType: string;
  searchKeyword: string;
  deployType: string;
  modelType: string;
}

export const AG_010101 = () => {
  const { openAlert } = useModal();

  const [searchValues, setSearchValues] = useState<SearchValues>({
    dateType: '생성일시',
    dateRange: { startDate: '', endDate: '' },
    searchType: '이름',
    searchKeyword: '',
    deployType: '전체',
    modelType: '전체',
  });

  // 드롭다운 상태 관리
  const [dropdownStates, setDropdownStates] = useState({
    dateType: false,
    searchType: false,
    deployType: false,
    modelType: false,
    pageSize: false,
  });

  const [value, setValue] = useState('10');
  const [view, setView] = useState('grid');

  const [selectedCardIds, setSelectedCardIds] = useState<string[]>([]);

  const handleSearch = () => {};

  // 드롭다운 핸들러
  const handleDropdownToggle = (key: keyof typeof dropdownStates) => {
    setDropdownStates(prev => ({
      ...Object.keys(prev).reduce((acc, k) => ({ ...acc, [k]: false }), {} as typeof prev),
      [key]: !prev[key],
    }));
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
    }),
    [openAlert]
  );

  // 샘플 데이터
  const rowData = [
    {
      id: '1',
      name: '고객상담 챗봇',
      description: '24시간 고객 문의 자동 응답 시스템',
      deploy: '온라인',
      scope: '전체공유',
      createdDate: '2025.03.24 18:32:43',
      modifiedDate: '2025.03.24 18:32:43',
      more: 'more',
    },
    {
      id: '2',
      name: '문서요약 AI',
      description: '긴 문서를 요약하여 핵심 내용 추출',
      deploy: '배치',
      scope: '내부공유',
      createdDate: '2025.03.23 14:20:15',
      modifiedDate: '2025.03.23 14:20:15',
      more: 'more',
    },
    {
      id: '3',
      name: '감정분석 모델',
      description: '텍스트에서 감정을 분석하여 분류',
      deploy: '실시간',
      scope: '전체공유',
      createdDate: '2025.03.22 10:15:30',
      modifiedDate: '2025.03.22 10:15:30',
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
        minWidth: 392,
        flex: 1,
        showTooltip: true,
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
        headerName: '배포여부', // [251104_퍼블수정] : '배포' > '배포여부'
        field: 'deploy',
        width: 120,
      },
      {
        headerName: '공개범위',
        field: 'scope',
        width: 120,
      },
      {
        headerName: '생성일시',
        field: 'createdDate',
        width: 180,
      },
      {
        headerName: '최종 수정일시',
        field: 'modifiedDate',
        width: 180,
      },
      {
        headerName: '',
        field: 'more',
        width: 60,
        sortable: false,
        suppressHeaderMenuButton: true,
      },
    ],
    [rowData]
  );

  // 그리드(카드형)
  const rowCardData = [
    {
      no: 1,
      id: '1',
      modelName: '대출 상담 자동화 에이전트',
      description: '질문 분류 문서 기반 응답 자동 생성',
      deploymentStatus: '개발 배포',
      scope: '내부공유',
      createdDate: '2024-01-15 09:30:00',
      modifiedDate: '2024-01-20 14:25:00',
      more: 'more',
    },
    {
      no: 2,
      id: '2',
      modelName: '대출 상담 자동화 에이전트',
      description: '질문 분류 문서 기반 응답 자동 생성',
      deploymentStatus: '개발 배포',
      scope: '내부공유',
      createdDate: '2024-01-10 11:15:00',
      modifiedDate: '2024-01-18 16:40:00',
      more: 'more',
    },
    {
      no: 3,
      id: '3',
      modelName: '대출 상담 자동화 에이전트',
      description: '질문 분류 문서 기반 응답 자동 생성',
      deploymentStatus: '개발 배포',
      scope: '내부공유',
      createdDate: '2024-01-05 13:45:00',
      modifiedDate: '2024-01-22 10:20:00',
      more: 'more',
    },
    {
      no: 4,
      id: '4',
      modelName: '대출 상담 자동화 에이전트',
      description: '질문 분류 문서 기반 응답 자동 생성',
      deploymentStatus: '개발 배포',
      scope: '내부공유',
      createdDate: '2024-01-12 08:30:00',
      modifiedDate: '2024-01-19 15:50:00',
      more: 'more',
    },
    {
      no: 5,
      id: '5',
      modelName: '대출 상담 자동화 에이전트',
      description: '질문 분류 문서 기반 응답 자동 생성',
      deploymentStatus: '개발 배포',
      scope: '내부공유',
      createdDate: '2024-01-08 16:20:00',
      modifiedDate: '2024-01-21 12:35:00',
      more: 'more',
    },
  ];

  return (
    <DesignLayout
      initialMenu={{ id: 'agent', label: '에이전트' }}
      initialSubMenu={{
        id: 'agent-builder',
        label: '빌더',
        icon: 'ico-lnb-menu-20-agent-builder',
      }}
    >
      {/* 섹션 페이지 */}
      <section className='section-page'>
        {/* 페이지 헤더 */}
        <UIPageHeader
          title='빌더'
          description={[
            '생성형 AI 모델을 활용한 나만의 AI Agent를 개발하고 개발망에 배포할 수 있습니다.',
            '개발한 AI Agent를 조회해 어떤 모델과 지식이 사용되었는지 확인해 보세요.',
          ]}
          actions={
            <>
              {/* [251222_퍼블수정]: btn-text-14-semibold-point > btn-text-18-semibold-point  (클래스명 변경 : 폰트 크기 수정함) */}
              <UIButton2 className='btn-text-18-semibold-point' leftIcon={{ className: 'ic-system-24-add', children: '' }}>
                에이전트 등록
              </UIButton2>
            </>
          }
        />

        {/* 페이지 바디 */}
        <UIPageBody>
          {/* 아티클 필터 : 검색 영역*/}
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
                          <UIUnitGroup gap={0} direction='row'>
                            <div className='flex-1'>
                              <UIInput.Search
                                value={searchValues.searchKeyword}
                                placeholder='검색어 입력'
                                onChange={e => setSearchValues(prev => ({ ...prev, searchKeyword: e.target.value }))}
                              />
                            </div>
                          </UIUnitGroup>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
                <div style={{ width: '128px' }}>
                  <UIButton2 className='btn-secondary-blue' onClick={handleSearch} style={{ width: '100%' }}>
                    조회
                  </UIButton2>
                </div>
              </UIGroup>
            </UIBox>
          </UIArticle>

          {/* 아티클 그리드 */}
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
                            isOpen={dropdownStates.pageSize}
                            onClick={() => handleDropdownToggle('pageSize')}
                            onSelect={(value: string) => {
                              setValue(value);
                              setDropdownStates(prev => ({ ...prev, pageSize: false }));
                            }}
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
                  <UIGrid type='multi-select' rowData={rowData} columnDefs={columnDefs} moreMenuConfig={moreMenuConfig} />
                ) : (
                  <UICardList
                    rowData={rowCardData}
                    flexType='grow'
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
                            { label: '배포여부', value: item.deploymentStatus },
                            { label: '공개범위', value: item.scope },
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
                <UIPagination currentPage={1} totalPages={10} onPageChange={() => {}} className='flex justify-center' />
              </UIListContentBox.Footer>
            </UIListContainer>
          </UIArticle>
        </UIPageBody>
      </section>
    </DesignLayout>
  );
};
