import React, { useMemo, useState } from 'react';

import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIGroup, UIPageFooter } from '@/components/UI/molecules';
import { UIDataCnt, UILabel, UIToggle } from '@/components/UI';
import { UIPagination } from '@/components/UI/atoms/UIPagination';
import { UIInput } from '@/components/UI/molecules';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { UIGrid } from '@/components/UI/molecules/grid/UIGrid/component';
import { UIListContainer } from '@/components/UI/molecules/list/UIListContainer/component';
import { UIListContentBox } from '@/components/UI/molecules/list/UIListContentBox';
import { UIDropdown } from '../../../components/UI/molecules/dropdown/UIDropdown';
import { UIBox, UIButton2, UITypography } from '@/components/UI/atoms';
import { UICardList } from '@/components/UI/molecules/card/UICardList';
import { DesignLayout } from '../../components/DesignLayout';
import { UIGridCard } from '@/components/UI/molecules/card/UIGridCard';

export const DP_010101 = () => {
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
      name: '고객 상담 에이전트 v2.1',
      builderName: 'GPT-4-Callcenter-Tuned',
      status: '이용 가능',
      description: '고객 문의에 대한 자동 응답 시스템입니다. 24시간 실시간 고객 지원을 제공하며, 다양한 질문 유형에 대해 정확한 답변을 제공합니다.',
      deployType: 'serverless',
      version: 'self_hosting',
      publicRange: '배포',
      publicStatus: '전체공유',
      createdDate: '2024.09.05 14:30',
      modifiedDate: '2024.09.12 09:15',
    },
    {
      id: 2,
      name: '문서 요약 에이전트',
      builderName: 'GPT-4-Callcenter-Tuned',
      status: '진행중',
      description: '긴 문서를 자동으로 요약하여 핵심 내용만을 추출하는 AI 에이전트입니다. PDF, Word, 텍스트 파일을 지원합니다.',
      deployType: 'self_hosting',
      version: 'self_hosting',
      publicRange: '배포',
      publicStatus: '전체공유',
      createdDate: '2024.09.03 10:22',
      modifiedDate: '2024.09.11 16:45',
    },
    {
      id: 3,
      name: '코드 리뷰 어시스턴트',
      builderName: 'GPT-4-Callcenter-Tuned',
      status: '실패',
      description: '프로그래밍 코드의 품질을 검토하고 개선 사항을 제안하는 에이전트입니다. 다양한 프로그래밍 언어를 지원합니다.',
      deployType: 'serverless',
      version: 'self_hosting',
      publicRange: '배포',
      publicStatus: '전체공유',
      createdDate: '2024.09.01 11:15',
      modifiedDate: '2024.09.10 14:20',
    },
    {
      id: 4,
      name: '회의록 생성기',
      builderName: 'GPT-4-Callcenter-Tuned',
      status: '취소',
      description: '음성 회의 내용을 텍스트로 변환하고 주요 안건과 결정사항을 정리하여 회의록을 자동 생성하는 에이전트입니다.',
      deployType: 'self_hosting',
      version: 'self_hosting',
      publicRange: '배포',
      publicStatus: '전체공유',
      createdDate: '2024.08.28 15:40',
      modifiedDate: '2024.09.08 13:25',
    },
    {
      id: 5,
      name: '데이터 분석 도우미',
      builderName: 'GPT-4-Callcenter-Tuned',
      status: '이용 가능',
      description: 'Excel, CSV 데이터를 분석하여 인사이트를 제공하고 시각화 차트를 생성하는 AI 에이전트입니다.',
      deployType: 'serverless',
      version: 'self_hosting',
      publicRange: '배포',
      publicStatus: '전체공유',
      createdDate: '2024.08.25 09:10',
      modifiedDate: '2024.09.12 08:30',
    },
    {
      id: 6,
      name: '번역 에이전트',
      builderName: 'GPT-4-Callcenter-Tuned',
      status: '이용 가능',
      description: '다국어 번역 서비스를 제공하는 에이전트입니다. 한국어, 영어, 일본어, 중국어 등 20개 언어를 지원합니다.',
      deployType: 'serverless',
      version: 'self_hosting',
      publicRange: '미배포',
      publicStatus: '전체공유',
      createdDate: '2024.08.20 13:55',
      modifiedDate: '2024.09.11 17:10',
    },
    {
      id: 7,
      name: '이메일 자동 분류기',
      builderName: 'GPT-4-Callcenter-Tuned',
      status: '진행중',
      description: '받은 이메일을 자동으로 분류하고 중요도에 따라 우선순위를 설정하는 에이전트입니다.',
      deployType: 'self_hosting',
      version: 'self_hosting',
      publicRange: '미배포',
      publicStatus: '전체공유',
      createdDate: '2024.08.15 16:20',
      modifiedDate: '2024.09.09 11:45',
    },
    {
      id: 8,
      name: '프로젝트 일정 관리자',
      builderName: 'GPT-4-Callcenter-Tuned',
      status: '이용 가능',
      description: '프로젝트의 일정과 마일스톤을 관리하고 팀원들에게 알림을 보내는 스마트 관리 에이전트입니다.',
      deployType: 'serverless',
      version: 'self_hosting',
      publicRange: '미배포',
      publicStatus: '전체공유',
      createdDate: '2024.08.10 10:30',
      modifiedDate: '2024.09.07 15:55',
    },
    {
      id: 9,
      name: '보고서 템플릿 생성기',
      builderName: 'GPT-4-Callcenter-Tuned',
      status: '실패',
      description: '업무 보고서를 위한 다양한 템플릿을 자동 생성하고 데이터를 입력하여 완성된 보고서를 작성하는 에이전트입니다.',
      deployType: 'self_hosting',
      version: 'self_hosting',
      publicRange: '미배포',
      publicStatus: '전체공유',
      createdDate: '2024.08.05 14:15',
      modifiedDate: '2024.09.06 12:40',
    },
    {
      id: 10,
      name: '소셜미디어 관리자',
      builderName: 'GPT-4-Callcenter-Tuned',
      status: '이용 가능',
      description: '기업의 소셜미디어 계정을 관리하고 콘텐츠를 자동으로 게시하며 반응을 모니터링하는 에이전트입니다.',
      deployType: 'serverless',
      version: 'self_hosting',
      publicRange: '배포',
      publicStatus: '전체공유',
      createdDate: '2024.07.30 11:25',
      modifiedDate: '2024.09.05 16:30',
    },
    {
      id: 11,
      name: '재고 관리 시스템',
      builderName: 'GPT-4-Callcenter-Tuned',
      status: '진행중',
      description: '창고의 재고 상황을 실시간으로 모니터링하고 재주문 시점을 알려주는 스마트 재고 관리 에이전트입니다.',
      deployType: 'self_hosting',
      version: 'self_hosting',
      publicRange: '배포',
      publicStatus: '전체공유',
      createdDate: '2024.07.25 13:10',
      modifiedDate: '2024.09.04 10:20',
    },
    {
      id: 12,
      name: '학습 콘텐츠 추천기',
      builderName: 'GPT-4-Callcenter-Tuned',
      status: '이용 가능',
      description: '사용자의 학습 패턴을 분석하여 개인 맞춤형 교육 콘텐츠를 추천하는 AI 에이전트입니다.',
      deployType: 'serverless',
      version: 'self_hosting',
      publicRange: '배포',
      publicStatus: '전체공유',
      createdDate: '2024.07.20 15:45',
      modifiedDate: '2024.09.03 14:15',
    },
  ];

  // 더보기 메뉴 설정  (참고 : 케이스별 버튼 노출이 다릅니다. 피그마참고)
  const moreMenuConfig = useMemo(
    () => ({
      items: [
        {
          label: '중지',
          action: 'run',
          onClick: (_rowData: any) => {},
        },
        {
          label: '수정',
          action: 'modify',
          onClick: (_rowData: any) => {},
        },
        {
          label: '시작',
          action: 'copy',
          onClick: (_rowData: any) => {},
        },
        {
          label: '수정',
          action: 'copy',
          onClick: (_rowData: any) => {},
        },
        {
          label: '삭제',
          action: 'delete',
          onClick: (_rowData: any) => {},
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
      {
        headerName: '배포명',
        field: 'name',
        width: 272,
        minWidth: 272,
        maxWidth: 272,
        suppressSizeToFit: true,
      },
      {
        headerName: '모델명',
        field: 'builderName',
        width: 272,
      },
      {
        headerName: '상태',
        field: 'status',
        width: 120,
        cellRenderer: React.memo((params: any) => {
          const getStatusIntent = (status: string) => {
            switch (status) {
              case '이용 가능':
                return 'complete';
              case '진행중':
                return 'progress';
              case '실패':
                return 'error';
              case '취소':
                return 'stop';
              default:
                return 'complete';
            }
          };
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
        minWidth: 472,
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
        headerName: '모델유형',
        field: 'deployType',
        width: 120,
      },
      {
        headerName: '배포유형',
        field: 'version',
        width: 120,
      },
      {
        headerName: '운영 배포 여부',
        field: 'publicRange',
        width: 120,
      },
      {
        headerName: '공개범위',
        field: 'publicStatus',
        width: 120,
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
    [handleMoreClick]
  );
  const [view, setView] = useState('grid');
  const [checkedCards, setCheckedCards] = useState<{ [key: string]: boolean }>({});

  // 그리드(카드형)
  const rowCardData = [
    {
      no: 1,
      id: '1',
      modelName: 'GPT-NeoX-Finance-QA',
      description: '고객 상담 응대 문장 생성에 최적화된 대형 언어모델',
      deployType: '이용가능',
      modelType: 'labguage',
      deployList: 'language',
      permission: 'Public',
      more: 'more',
    },
    {
      no: 2,
      id: '2',
      modelName: 'GPT-NeoX-Finance-QA',
      description: '고객 상담 응대 문장 생성에 최적화된 대형 언어모델',
      deployType: '진행중',
      modelType: 'labguage',
      deployList: 'language',
      permission: 'Private',
      more: 'more',
    },
    {
      no: 3,
      id: '3',
      modelName: 'GPT-NeoX-Finance-QA',
      description: '고객 상담 응대 문장 생성에 최적화된 대형 언어모델',
      deployType: '실패',
      modelType: 'labguage',
      deployList: 'language',
      permission: 'Public',
      more: 'more',
    },
    {
      no: 4,
      id: '4',
      modelName: 'GPT-NeoX-Finance-QA',
      description: '고객 상담 응대 문장 생성에 최적화된 대형 언어모델',
      deployType: '이용가능',
      modelType: 'labguage',
      deployList: 'language',
      permission: 'Internal',
      more: 'more',
    },
    {
      no: 5,
      id: '5',
      modelName: 'GPT-NeoX-Finance-QA',
      description: 'Google의 양방향 트랜스포머 모델로, 자연어 처리 작업에 널리 사용됩니다.',
      deployType: '이용가능',
      modelType: 'labguage',
      deployList: 'language',
      permission: 'Public',
      more: 'more',
    },
  ];

  return (
    <DesignLayout>
      {/* 섹션 페이지 */}
      <section className='section-page'>
        <UIPageHeader title='모델 배포' description={['배포한 모델의 정보를 확인하고 관리할 수 있습니다.', '배포 모델을 선택하여 간단한 사용방법과 시스템 로그를 확인해보세요.']} />
        {/* 페이지 바디 */}
        <UIPageBody>
          {/* [251105_퍼블수정] 검색영역 수정 */}
          <UIArticle className='article-filter'>
            <UIBox className='box-filter'>
              <UIGroup gap={40} direction='row'>
                {/* [251106_퍼블수정] 검색영역 스타일 수정 */}
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
                            상태
                          </UITypography>
                        </th>
                        <td>
                          <div className='flex-1'>
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
                            { label: '모델명', value: item.modelName },
                            { label: '모델유형', value: item.modelType },
                            { label: '배포유형', value: item.deployList },
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
                <UIPagination currentPage={3923} totalPages={3930} onPageChange={() => {}} className='flex justify-center' />
              </UIListContentBox.Footer>
            </UIListContainer>
          </UIArticle>
        </UIPageBody>
        <UIPageFooter></UIPageFooter>
      </section>
    </DesignLayout>
  );
};
