import React, { useState, useMemo } from 'react';

import { UILabel } from '../../../components/UI/atoms/UILabel';
import { UIDropdown } from '../../../components/UI/molecules/dropdown/UIDropdown';
import { DesignLayout } from '../../components/DesignLayout';

import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIBox, UIButton2, UIPagination } from '@/components/UI/atoms';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UITypography } from '../../../components/UI/atoms/UITypography';
import { UIInput } from '@/components/UI/molecules/input';
import { UIGrid } from '../../../components/UI/molecules/grid';
import { UIDataCnt } from '@/components/UI';
import { UITabs } from '../../../components/UI/organisms/UITabs';
import { UIListContainer, UIListContentBox } from '../../../components/UI/molecules/list';
import { UIGroup } from '@/components/UI/molecules';

export const MD_050101 = () => {
  const [activeTab, setActiveTab] = useState('tab1');
  // search 타입
  const [searchValue, setSearchValue] = useState('');
  const [value, setValue] = useState('12개씩 보기');
  const [view] = useState('grid');

  // 드롭다운 상태 관리
  const [dropdownStates, setDropdownStates] = useState({
    dateType: false,
    searchType: false,
    status: false,
    agentType: false,
  });

  // 샘플 데이터
  /* [251120_퍼블수정] 속성값 수정 */
  const rowData = [
    {
      id: '1',
      name: '고객상담_보험상품형',
      status: '이용가능',
      description: '보험 상품 관련 고객 문의 전문 상담 에이전트',
      workType: '상담',
      version: '13.2GB',
      modelImport: '반입완료',
    },
    {
      id: '2',
      name: '업무지원_문서처리형',
      status: '이용가능',
      description: '문서 작성, 요약, 검토 등 업무 지원 에이전트',
      workType: '업무지원',
      version: '13.2GB',
      modelImport: '반입중',
    },
    {
      id: '3',
      name: '데이터분석_리포트형',
      status: '이용가능',
      description: '데이터 분석 결과를 시각적 리포트로 제공하는 에이전트',
      workType: '데이터분석',
      version: '13.2GB',
      modelImport: '반입완료',
    },
    {
      id: '4',
      name: '질의응답_FAQ형',
      status: '이용불가',
      description: '자주 묻는 질문에 대한 자동 응답 처리 에이전트',
      workType: '질의응답',
      version: '13.2GB',
      modelImport: '반입완료',
    },
    {
      id: '5',
      name: '업무자동화_프로세스형',
      status: '반입중',
      description: '반복적인 업무 프로세스를 자동화하는 에이전트',
      workType: '업무자동화',
      version: '13.2GB',
      modelImport: '반입완료',
    },
    {
      id: '6',
      name: '콘텐츠생성_마케팅형',
      status: '반입전',
      description: '마케팅 콘텐츠 생성 및 관리 전문 에이전트',
      workType: '콘텐츠생성',
      version: '13.2GB',
      modelImport: '반입중',
    },
    {
      id: '7',
      name: '번역_다국어형',
      status: '이용가능',
      description: '다국어 번역 및 국제화 지원 에이전트',
      workType: '번역',
      version: '13.2GB',
      modelImport: '반입완료',
    },
    {
      id: '8',
      name: '보안관제_모니터링형',
      status: '이용가능',
      description: '시스템 보안 상태 실시간 모니터링 에이전트',
      workType: '보안관제',
      version: '13.2GB',
      modelImport: '반입완료',
    },
  ];

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

  // 탭 옵션 정의
  const tabOptions = [
    { id: 'tab1', label: 'self-hosting' },
    { id: 'tab2', label: 'serverless' },
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
          textAlign: 'center',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
        },
        sortable: false,
        suppressHeaderMenuButton: true,
        suppressSizeToFit: true,
      },
      // 251120_퍼블수정 그리드 컬럼 속성 수정 S
      {
        headerName: '모델명',
        field: 'name',
        width: 280,
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
      // 251113_퍼블수정 그리드 컬럼 속성 영역 수정 E
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
              case '반입중':
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
        flex: 1,
        showTooltip: true,
        minWidth: 380,
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
      // 251120_퍼블수정 그리드 컬럼 속성 수정 S
      {
        headerName: '크기',
        field: 'version',
        width: 120,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      // 251120_퍼블수정 그리드 컬럼 속성 수정 E
      {
        headerName: '모델 반입',
        field: 'modelImport',
        width: 130,
        cellStyle: {
          paddingLeft: '16px',
        },
        cellRenderer: () => {
          return <UIButton2 className='btn-text-14-underline-point'>모델 반입</UIButton2>;
        },
      },
    ],
    [rowData]
  );

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
        <UIPageHeader
          title='모델 탐색' // [251111_퍼블수정] 타이틀명칭 변경 : 모델 가든 > 모델 탐색
          description={[
            'Public 프로젝트에서 Hugging Face에 등록된 모델을 검색하고 은행 내부로 가져올 수 있습니다.',
            '모델 검색과 반입 버튼을 통해 필요한 모델을 손쉽게 반입하세요.',
          ]}
          actions={
            <>
              {/* [251222_퍼블수정]: btn-text-14-semibold-point > btn-text-18-semibold-point  (클래스명 변경 : 폰트 크기 수정함) */}
              <UIButton2 className='btn-text-18-semibold-point' leftIcon={{ className: 'ic-system-24-add', children: '' }}>
                모델 검색
              </UIButton2>
              <UIButton2 className='btn-text-18-semibold-point' leftIcon={{ className: 'ic-system-24-download', children: '' }}>
                모델 반입
              </UIButton2>
            </>
          }
        />

        {/* 페이지 바디 */}
        <UIPageBody>
          {/* 탭 영역 */}
          <UIArticle className='article-tabs'>
            <div className='flex'>
              <UITabs items={tabOptions} activeId={activeTab} onChange={setActiveTab} size='large' />
            </div>
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
                        {/* [251120_퍼블수정] 플레이스홀더 수정 */}
                        <td>
                          <div>
                            <UIInput.Search
                              value={searchValue}
                              placeholder='모델명, 설명 입력'
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
                        {/* [251120_퍼블수정] 속성값 수정 */}
                        <td>
                          <UIDropdown
                            value={'전체'}
                            placeholder='조회 조건 선택'
                            options={[
                              { value: '1', label: '전체' },
                              { value: '2', label: '반입전' },
                              { value: '3', label: '이용가능' },
                              { value: '4', label: '반입중' },
                              { value: '5', label: '결재중' },
                              { value: '6', label: '이용불가' },
                            ]}
                            isOpen={dropdownStates.searchType}
                            onClick={() => handleDropdownToggle('searchType')}
                            onSelect={() => handleDropdownSelect('searchType')}
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

          {/* 데이터 그룹 컴포넌트 */}
          <UIArticle className='article-grid'>
            {/* 전체 데이터 목록 */}
            <UIListContainer>
              <UIListContentBox.Header>
                <div className='flex-shrink-0'>
                  <UIDataCnt count={rowData.length} prefix='총' unit='건' />
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
                </div>
              </UIListContentBox.Header>
              <UIListContentBox.Body>
                {view === 'grid' ? (
                  <UIGrid rowData={rowData} columnDefs={columnDefs} onClickRow={(_params: any) => {}} />
                ) : (
                  <div className='p-4 text-center text-gray-500'>카드 뷰 준비 중...</div>
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
