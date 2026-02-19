import React, { memo, useMemo, useState } from 'react';

import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';

import { UILabel, UIProgress } from '@/components/UI/atoms';
import { UIDataCnt, UIToggle } from '@/components/UI';
import { UIPagination } from '@/components/UI/atoms/UIPagination';
import { UIInput } from '@/components/UI/molecules';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { UIGrid } from '@/components/UI/molecules/grid/UIGrid/component';
import { UIListContainer } from '@/components/UI/molecules/list/UIListContainer/component';
import { UIListContentBox } from '@/components/UI/molecules/list/UIListContentBox';
import { UIDropdown } from '../../../components/UI/molecules/dropdown/UIDropdown';

import { UIBox, UIButton2, UITypography } from '@/components/UI/atoms';
import { UIGroup } from '@/components/UI/molecules';
import { DesignLayout } from '../../components/DesignLayout';
import { useModal } from '@/stores/common/modal';
import { UIGridCard } from '@/components/UI/molecules/card/UIGridCard';
import { UICardList } from '@/components/UI/molecules/card/UICardList';

export const MD_030101 = () => {
  // 드롭다운 상태 관리
  const [dropdownStates, setDropdownStates] = useState({
    dateType: false,
    searchType: false,
    status: false,
    publicRange: false,
  });

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

  // 상태별 intent 매핑 함수
  const getStatusIntent = (status: string) => {
    switch (status) {
      case '초기화':
        return 'neutral';
      case '오류':
        return 'error';
      case '시작중':
        return 'progress';
      case '중지':
        return 'stop';
      case '중지중':
        return 'stop';
      case '완료':
        return 'complete';
      case '할당완료':
        return 'purple';
      case '학습중':
        return 'progress';
      case '할당중':
        return 'progress';
      default:
        return 'complete';
    }
  };

  // search 타입
  const [searchValue, setSearchValue] = useState('');
  const { openAlert } = useModal();

  // rowData 정의
  const rowData = [
    {
      id: 1,
      name: '고객 상담 에이전트 v2.1',
      status: '초기화',
      description: '고객 문의에 대한 자동 응답 시스템입니다. 24시간 실시간 고객 지원을 제공하며, 다양한 질문 유형에 대해 정확한 답변을 제공합니다.',
      publicRange: '전체 공개',
      createdDate: '2024.09.05 14:30',
      modifiedDate: '2024.09.12 09:15',
    },
    {
      id: 2,
      name: '문서 요약 에이전트',
      status: '오류',
      description: '긴 문서를 자동으로 요약하여 핵심 내용만을 추출하는 AI 에이전트입니다. PDF, Word, 텍스트 파일을 지원합니다.',
      publicRange: '부서 공개',
      createdDate: '2024.09.03 10:22',
      modifiedDate: '2024.09.11 16:45',
    },
    {
      id: 3,
      name: '코드 리뷰 어시스턴트',
      status: '시작중',
      description: '프로그래밍 코드의 품질을 검토하고 개선 사항을 제안하는 에이전트입니다. 다양한 프로그래밍 언어를 지원합니다.',
      publicRange: '개인 전용',
      createdDate: '2024.09.01 11:15',
      modifiedDate: '2024.09.10 14:20',
    },
    {
      id: 4,
      name: '회의록 생성기',
      status: '중지중',
      description: '음성 회의 내용을 텍스트로 변환하고 주요 안건과 결정사항을 정리하여 회의록을 자동 생성하는 에이전트입니다.',
      publicRange: '팀 공개',
      createdDate: '2024.08.28 15:40',
      modifiedDate: '2024.09.08 13:25',
    },
    {
      id: 5,
      name: '데이터 분석 도우미',
      status: '학습완료',
      description: 'Excel, CSV 데이터를 분석하여 인사이트를 제공하고 시각화 차트를 생성하는 AI 에이전트입니다.',
      publicRange: '전체 공개',
      createdDate: '2024.08.25 09:10',
      modifiedDate: '2024.09.12 08:30',
    },
    {
      id: 6,
      name: '번역 에이전트',
      status: '할당중',
      description: '다국어 번역 서비스를 제공하는 에이전트입니다. 한국어, 영어, 일본어, 중국어 등 20개 언어를 지원합니다.',
      publicRange: '전체 공개',
      createdDate: '2024.08.20 13:55',
      modifiedDate: '2024.09.11 17:10',
    },
    {
      id: 7,
      name: '이메일 자동 분류기',
      status: '중지',
      description: '받은 이메일을 자동으로 분류하고 중요도에 따라 우선순위를 설정하는 에이전트입니다.',
      publicRange: '부서 공개',
      createdDate: '2024.08.15 16:20',
      modifiedDate: '2024.09.09 11:45',
    },
    {
      id: 8,
      name: '프로젝트 일정 관리자',
      status: '학습중',
      description: '프로젝트의 일정과 마일스톤을 관리하고 팀원들에게 알림을 보내는 스마트 관리 에이전트입니다.',
      publicRange: '팀 공개',
      createdDate: '2024.08.10 10:30',
      modifiedDate: '2024.09.07 15:55',
    },
    {
      id: 9,
      name: '보고서 템플릿 생성기',
      status: '할당완료',
      description: '업무 보고서를 위한 다양한 템플릿을 자동 생성하고 데이터를 입력하여 완성된 보고서를 작성하는 에이전트입니다.',
      publicRange: '개인 전용',
      createdDate: '2024.08.05 14:15',
      modifiedDate: '2024.09.06 12:40',
    },
    {
      id: 10,
      name: '소셜미디어 관리자',
      status: '학습중',
      description: '기업의 소셜미디어 계정을 관리하고 콘텐츠를 자동으로 게시하며 반응을 모니터링하는 에이전트입니다.',
      publicRange: '전체 공개',
      createdDate: '2024.07.30 11:25',
      modifiedDate: '2024.09.05 16:30',
    },
    {
      id: 11,
      name: '재고 관리 시스템',
      status: '할당완료',
      description: '창고의 재고 상황을 실시간으로 모니터링하고 재주문 시점을 알려주는 스마트 재고 관리 에이전트입니다.',
      publicRange: '부서 공개',
      createdDate: '2024.07.25 13:10',
      modifiedDate: '2024.09.04 10:20',
    },
    {
      id: 12,
      name: '학습 콘텐츠 추천기',
      status: '완료',
      description: '사용자의 학습 패턴을 분석하여 개인 맞춤형 교육 콘텐츠를 추천하는 AI 에이전트입니다.',
      publicRange: '전체 공개',
      createdDate: '2024.07.20 15:45',
      modifiedDate: '2024.09.03 14:15',
    },
  ];

  // 더보기 메뉴 설정
  const moreMenuConfig = useMemo(
    () => ({
      items: [
        {
          label: '초기화',
          action: 'modify',
          onClick: (rowData: any) => {
            openAlert({
              title: '안내',
              message: `테스트 "${rowData.name}"`,
            });
          },
        },
        {
          label: '시작중',
          action: 'delete',
          onClick: (rowData: any) => {
            openAlert({
              title: '안내',
              message: `테스트 "${rowData.name}"`,
            });
          },
        },
        {
          label: '할당중',
          action: 'delete',
          onClick: (rowData: any) => {
            openAlert({
              title: '안내',
              message: `테스트 "${rowData.name}"`,
            });
          },
        },
        {
          label: '할당완료',
          action: 'delete',
          onClick: (rowData: any) => {
            openAlert({
              title: '안내',
              message: `테스트 "${rowData.name}"`,
            });
          },
        },
        {
          label: '학습중',
          action: 'delete',
          onClick: (rowData: any) => {
            openAlert({
              title: '안내',
              message: `테스트 "${rowData.name}"`,
            });
          },
        },
        {
          label: '완료',
          action: 'delete',
          onClick: (rowData: any) => {
            openAlert({
              title: '안내',
              message: `테스트 "${rowData.name}"`,
            });
          },
        },
        {
          label: '실패',
          action: 'delete',
          onClick: (rowData: any) => {
            openAlert({
              title: '안내',
              message: `테스트 "${rowData.name}"`,
            });
          },
        },
        {
          label: '중지',
          action: 'delete',
          onClick: (rowData: any) => {
            openAlert({
              title: '안내',
              message: `테스트 "${rowData.name}"`,
            });
          },
        },
        {
          label: '중지중',
          action: 'delete',
          onClick: (rowData: any) => {
            openAlert({
              title: '안내',
              message: `테스트 "${rowData.name}"`,
            });
          },
        },
      ],
      isActive: () => true, // 모든 테스트에 대해 활성화
    }),
    []
  );

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
        width: 272,
        minWidth: 272,
        maxWidth: 272,
        suppressSizeToFit: true,
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
        cellRenderer: React.memo((params: any) => {
          return (
            <UILabel variant='badge' intent={getStatusIntent(params.value)}>
              {params.value}
            </UILabel>
          );
        }),
      },
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
      {
        headerName: '진행율',
        field: 'progress',
        width: 370,
        cellRenderer: memo((params: any) => {
          return <UIProgress value={params.value} status={params.status} showPercent={true} />;
        }),
      },
      {
        headerName: '공개범위',
        field: 'publicRange',
        width: 180,
      },
      {
        headerName: '생성일시',
        field: 'createdDate',
        width: 180,
        cellStyle: {
          paddingLeft: 16,
        },
      },
      {
        headerName: '최종 수정일시',
        field: 'modifiedDate',
        width: 180,
        cellStyle: {
          paddingLeft: 16,
        },
      },
      {
        headerName: '',
        field: 'more', // 더보기 컬럼 필드명 (고정)
        width: 56,
      },
    ],
    []
  );

  // 그리드(카드형)
  const rowCardData = [
    {
      no: 1,
      id: '1',
      modelName: 'Mistrak-Docsummary',
      description: 'Llama 1B 모델',
      deployType: '초기화',
      createdDate: '2024-01-20 14:25:00',
      modifiedDate: '2024-01-20 14:25:00',
      progress: 0,
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
      modelName: 'Mistrak-Docsummary',
      description: 'Llama 1B 모델',
      deployType: '오류',
      createdDate: '2024-01-20 14:25:00',
      modifiedDate: '2024-01-18 16:40:00',
      progress: 50,
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
      modelName: 'Mistrak-Docsummary',
      description: 'Llama 1B 모델',
      deployType: '시작중',
      createdDate: '2024-01-20 14:25:00',
      modifiedDate: '2024-01-18 16:40:00',
      progress: 50,
      statusLabels: [
        { text: 'Release Ver.1', intent: 'blue' },
        { text: 'Lastest.1', intent: 'gray' },
      ],
      more: 'more',
      isActive: false,
    },
    {
      no: 4,
      id: '4',
      modelName: 'Mistrak-Docsummary',
      description: 'Llama 1B 모델',
      deployType: '중지중',
      createdDate: '2024-01-20 14:25:00',
      modifiedDate: '2024-01-18 16:40:00',
      progress: 50,
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
      modelName: 'Mistrak-Docsummary',
      description: 'Llama 1B 모델',
      deployType: '완료',
      createdDate: '2024-01-20 14:25:00',
      modifiedDate: '2024-01-18 16:40:00',
      progress: 50,
      statusLabels: [
        { text: 'Release Ver.1', intent: 'blue' },
        { text: 'Lastest.1', intent: 'gray' },
      ],
      more: 'more',
      isActive: false,
    },
  ];

  const [value, setValue] = useState('12개씩 보기');
  const [view, setView] = useState('grid');
  const [selectedCardIds, setSelectedCardIds] = useState<string[]>([]);

  return (
    <DesignLayout>
      {/* 섹션 페이지 */}
      <section className='section-page'>
        <UIPageHeader
          title='파인튜닝'
          description={[
            '모델의 파인튜닝 작업을 실행하고 각 작업의 상세 내용을 조회할 수 있습니다.',
            '파인튜닝 작업을 선택하여 학습에 사용한 데이터세트와 학습 로그를 확인해 보세요.',
          ]}
          actions={
            <>
              {/* [251222_퍼블수정]: btn-text-14-semibold-point > btn-text-18-semibold-point  (클래스명 변경 : 폰트 크기 수정함) */}
              <UIButton2 className='btn-text-18-semibold-point' leftIcon={{ className: 'ic-system-24-add', children: '' }}>
                파인튜닝 등록
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
                                { value: '2', label: '초기화' },
                                { value: '3', label: '시작중' },
                                { value: '4', label: '중지중' },
                                { value: '5', label: '중지' },
                                { value: '6', label: '할당중' },
                                { value: '7', label: '할당완료' },
                                { value: '8', label: '학습중' },
                                { value: '9', label: '학습완료' },
                                { value: '10', label: '완료' },
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
            {/* 다중 선택 그리드 */}
            <UIListContainer>
              <UIListContentBox.Header>
                <div className='flex-shrink-0'>
                  <UIGroup gap={8} direction='row' align='start'>
                    <div style={{ width: '168px', paddingRight: '8px' }}>
                      <UIDataCnt count={99} prefix='총' unit='건' />
                    </div>
                  </UIGroup>
                </div>
                <div className='flex items-center gap-2'>
                  <UIButton2 className='btn-tertiary-outline'>매트릭 뷰</UIButton2>
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
                  <UIGrid<any>
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
                        <UIGridCard<any>
                          id={item.id}
                          title={item.modelName}
                          caption={item.description}
                          progressValue={item.progress}
                          data={item} // 카드형 더보기 추가시
                          moreMenuConfig={moreMenuConfig} // 카드형 더보기 추가시
                          statusArea={
                            <UILabel variant='badge' intent={getStatusIntent(item.deployType)}>
                              {item.deployType}
                            </UILabel>
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
                            { label: '라벨', value: item.createdDate },
                            { label: '최종 수정일시', value: item.modifiedDate },
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
                <UIPagination currentPage={1} totalPages={3} onPageChange={() => {}} className='flex justify-center' />
              </UIListContentBox.Footer>
            </UIListContainer>
          </UIArticle>
        </UIPageBody>
      </section>
    </DesignLayout>
  );
};
