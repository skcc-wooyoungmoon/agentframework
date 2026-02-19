import React, { useState, useMemo } from 'react';

import { UIButton2 } from '../../../components/UI/atoms/UIButton2';
import { UILabel } from '../../../components/UI/atoms/UILabel';
import { UIDropdown } from '../../../components/UI/molecules/dropdown/UIDropdown';
import { DesignLayout } from '../../components/DesignLayout';
import { UIBox, UITypography } from '@/components/UI/atoms';

import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIPagination } from '@/components/UI/atoms/UIPagination';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UIInput } from '@/components/UI/molecules/input';
import { UIToggle, UIDataCnt } from '@/components/UI';

import { UICardList } from '@/components/UI/molecules/card/UICardList';
import { UIGridCard } from '@/components/UI/molecules/card/UIGridCard';
import { UIGroup } from '@/components/UI/molecules';

export const LO_020101 = () => {
  // 드롭다운 상태 관리
  const [dropdownStates, setDropdownStates] = useState({
    dateType: false,
    searchType: false,
    status: false,
    agentType: false,
  });

  // 샘플 데이터
  const sampleData = [
    {
      id: '1',
      agentName: '고객상담_보험상품형',
      workType: 'GPT-4-Callcenter-Tuned',
      status: '이용가능',
      description: '보험 상품 관련 고객 문의 전문 상담 에이전트',
      deployType: 'language',
      deploymentMethod: 'self_hosting',
      connectedAgent: '전체공유',
      release: '배포',
      createdDate: '2025.03.24 18:32:43',
      modifiedDate: '2025.03.24 18:32:43',
    },
    {
      id: '2',
      agentName: '업무지원_문서처리형',
      workType: 'GPT-4-Callcenter-Tuned',
      status: '이용가능',
      description: '문서 작성, 요약, 검토 등 업무 지원 에이전트',
      deployType: 'language',
      deploymentMethod: 'self_hosting',
      connectedAgent: '전체공유',
      release: '배포',
      createdDate: '2025.03.24 18:32:43',
      modifiedDate: '2025.03.24 18:32:43',
    },
    {
      id: '3',
      agentName: '데이터분석_리포트형',
      workType: 'GPT-4-Callcenter-Tuned',
      status: '이용불가',
      description: '데이터 분석 결과를 시각적 리포트로 제공하는 에이전트',
      deployType: 'language',
      deploymentMethod: 'self_hosting',
      connectedAgent: '전체공유',
      release: '배포',
      createdDate: '2025.03.24 18:32:43',
      modifiedDate: '2025.03.24 18:32:43',
    },
    {
      id: '4',
      agentName: '질의응답_FAQ형',
      workType: 'GPT-4-Callcenter-Tuned',
      status: '배포중',
      description: '자주 묻는 질문에 대한 자동 응답 처리 에이전트',
      deployType: 'language',
      deploymentMethod: 'self_hosting',
      connectedAgent: '전체공유',
      release: '배포',
      createdDate: '2025.03.24 18:32:43',
      modifiedDate: '2025.03.24 18:32:43',
    },
    {
      id: '5',
      agentName: '업무자동화_프로세스형',
      workType: 'GPT-4-Callcenter-Tuned',
      status: '배포중',
      description: '반복적인 업무 프로세스를 자동화하는 에이전트',
      deployType: 'language',
      deploymentMethod: 'self_hosting',
      connectedAgent: '전체공유',
      release: '배포',
      createdDate: '2025.03.24 18:32:43',
      modifiedDate: '2025.03.24 18:32:43',
    },
    {
      id: '6',
      agentName: '콘텐츠생성_마케팅형',
      workType: 'GPT-4-Callcenter-Tuned',
      status: '이용가능',
      description: '마케팅 콘텐츠 생성 및 관리 전문 에이전트',
      deployType: 'language',
      deploymentMethod: 'self_hosting',
      connectedAgent: '전체공유',
      release: '배포',
      createdDate: '2025.03.24 18:32:43',
      modifiedDate: '2025.03.24 18:32:43',
    },
    {
      id: '7',
      agentName: '번역_다국어형',
      workType: 'GPT-4-Callcenter-Tuned',
      status: '이용가능',
      description: '다국어 번역 및 국제화 지원 에이전트',
      deployType: 'language',
      deploymentMethod: 'self_hosting',
      connectedAgent: '전체공유',
      release: '배포',
      createdDate: '2025.03.24 18:32:43',
      modifiedDate: '2025.03.24 18:32:43',
    },
    {
      id: '8',
      agentName: '보안관제_모니터링형',
      workType: 'GPT-4-Callcenter-Tuned',
      status: '이용가능',
      description: '시스템 보안 상태 실시간 모니터링 에이전트',
      deployType: 'language',
      deploymentMethod: 'self_hosting',
      connectedAgent: '전체공유',
      release: '배포',
      createdDate: '2025.03.24 18:32:43',
      modifiedDate: '2025.03.24 18:32:43',
    },
    {
      id: '9',
      agentName: '교육지원_학습형',
      workType: 'GPT-4-Callcenter-Tuned',
      status: '이용가능',
      description: '직원 교육 및 학습 지원 전문 에이전트',
      deployType: 'language',
      deploymentMethod: 'self_hosting',
      connectedAgent: '전체공유',
      release: '배포',
      createdDate: '2025.03.24 18:32:43',
      modifiedDate: '2025.03.24 18:32:43',
    },
    {
      id: '10',
      agentName: '일정관리_스케줄형',
      workType: 'GPT-4-Callcenter-Tuned',
      status: '이용가능',
      description: '일정 관리 및 스케줄링 자동화 에이전트',
      deployType: 'language',
      deploymentMethod: 'self_hosting',
      connectedAgent: '전체공유',
      release: '배포',
      createdDate: '2025.03.24 18:32:43',
      modifiedDate: '2025.03.24 18:32:43',
    },
  ];

  // search 타입
  const [searchValue, setSearchValue] = useState('');

  // 드롭다운 핸들러
  const handleDropdownToggle = (key: keyof typeof dropdownStates) => {
    setDropdownStates(prev => ({
      ...Object.keys(prev).reduce((acc, k) => ({ ...acc, [k]: false }), {} as typeof prev),
      [key]: !prev[key],
    }));
  };

  const handleDropdownSelect = (key: keyof typeof dropdownStates) => {
    setDropdownStates(prev => ({ ...prev, [key]: false }));
  };

  // value state 추가
  const [value, setValue] = useState('10');
  const [view, setView] = useState('grid');
  const [checkedCards, setCheckedCards] = useState<{ [key: string]: boolean }>({});

  // 그리드 컬럼 정의
  const columnDefs = useMemo(
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
          textAlign: 'center',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
        },
        sortable: false,
        suppressHeaderMenuButton: true,
      },
      {
        headerName: '배포명',
        field: 'agentName',
        width: 272,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: '모델명',
        field: 'workType',
        width: 272,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: '상태',
        field: 'status',
        width: 120,
        cellRenderer: React.memo((params: any) => {
          const getStatusIntent = (status: string) => {
            switch (status) {
              case '이용불가':
                return 'error';
              case '이용가능':
                return 'complete';
              case '배포중':
                return 'progress';
              case '반입전':
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
        minWidth: 400,
        flex: 1,
      },
      {
        headerName: '모델유형',
        field: 'deployType',
        width: 120,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: '배포유형',
        field: 'deploymentMethod',
        width: 120,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: '운영 배포 여부',
        field: 'release' as any,
        width: 120,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: '공개범위',
        field: 'connectedAgent',
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
    ],
    []
  );

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
        <UIPageHeader title='모델사용 로그' description='배포된 모델의 로그를 확인하고 관리할 수 있습니다.' />
        {/* [251111_퍼블수정] 타이틀명칭 변경 : 모델배포 로그 > 모델사용 로그 */}
        {/* 페이지 바디 */}
        <UIPageBody>
          {/* 검색 영역 */}
          {/* [251105_퍼블수정] 검색영역 수정 */}
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
                              placeholder='검색어 입력'
                              onChange={e => {
                                setSearchValue(e.target.value);
                              }}
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
                              onSelect={() => handleDropdownSelect('searchType')}
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

          {/* 그리드 */}
          <UIArticle className='article-grid'>
            <UIListContainer>
              <UIListContentBox.Header>
                <div className='article-header'>
                  <div className='grid-header-left'>
                    <UIDataCnt count={sampleData.length} prefix='총' unit='건' />
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
                      onSelect={(value: string) => {setValue(value);
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
                  <UIGrid
                    type='default'
                    rowData={sampleData}
                    columnDefs={columnDefs as any}
                    onClickRow={(_params: any) => {}}
                    onCheck={(_selectedIds: any[]) => {
                    }}
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
                            { label: '공개범위', value: item.permission },
                          ]}
                        />
                      );
                    }}
                  />
                )}
              </UIListContentBox.Body>
              <UIListContentBox.Footer>
                <UIPagination currentPage={1} totalPages={10} onPageChange={() => {}} className='flex justify-center' />
              </UIListContentBox.Footer>
            </UIListContainer>
          </UIArticle>
        </UIPageBody>
      </section>
    </DesignLayout>
  );
};
