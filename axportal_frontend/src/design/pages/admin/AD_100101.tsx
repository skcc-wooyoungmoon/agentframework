import React, { memo, useMemo, useState } from 'react';

import { UIInput } from '@/components/UI/molecules';
import { UIDropdown } from '@/components/UI/molecules/dropdown/UIDropdown';
import { UITabs } from '@/components/UI/organisms';
import { UIDataCnt } from '@/components/UI';

import { UIBox, UIButton2, UILabel, UITypography } from '@/components/UI/atoms';
import { UIPagination } from '@/components/UI/atoms/UIPagination';
import { UIGroup, UIUnitGroup } from '@/components/UI/molecules';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { DesignLayout } from '../../components/DesignLayout';

interface SearchValues {
  dateType: string;
  dateRange: { startDate?: string; endDate?: string };
  searchType: string;
  searchKeyword: string;
  projectName: string;
  result: string;
}

export const AD_100101 = () => {
  const [, setSearchValues] = useState<SearchValues>({
    dateType: '생성일시',
    dateRange: { startDate: '2025.06.30', endDate: '2025.07.30' },
    searchType: '이름',
    searchKeyword: '',
    projectName: '전체',
    result: '전체',
  });

  const [activeTab, setActiveTab] = useState('tools');

  // 드롭다운 상태 관리
  const [dropdownStates, setDropdownStates] = useState({
    dateType: false,
    searchType: false,
    projectName: false,
    result: false,
  });

  // search 타입
  const [searchValue, setSearchValue] = useState('');

  // 샘플 데이터
  //  [251105_퍼블수정] 컬럼 속성 수정
  const rowData = [
    {
      id: '1',
      name: '김신한',
      projectName: '슈퍼SOL 챗봇 개발',
      role: '개발자',
      apiDetails: '데이터세트 삭제',
      menuChannel: '데이터 > 데이터 세트',
      requestChannel: '데이터 > 데이터 세트',
      result: '성공',
      dateUse: '2025.03.24 18:32:43',
    },
    {
      id: '2',
      name: '이신한',
      projectName: '모바일 앱 개발',
      role: '기획자',
      apiDetails: '모델 학습',
      menuChannel: '데이터 > 데이터 세트',
      requestChannel: '데이터 > 데이터 세트',
      result: '성공',
      dateUse: '2025.03.24 18:32:43',
    },
    {
      id: '3',
      name: '박신한',
      projectName: '웹 플랫폼 구축',
      role: '디자이너',
      apiDetails: '데이터 분석',
      menuChannel: '데이터 > 데이터 세트',
      requestChannel: '데이터 > 데이터 세트',
      result: '실패',
      dateUse: '2025.03.24 18:32:43',
    },
  ];

  // 탭 옵션 정의
  const tabOptions = [
    { id: 'tools', label: '사용 이력' },
    { id: 'internal-api', label: '사용 통계' },
  ];

  // 그리드 컬럼 정의
  //  [251105_퍼블수정] 컬럼 속성 수정
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
        suppressSizeToFit: true,
      },
      // 251113_퍼블수정 그리드 컬럼 속성 '이름' 영역 수정 S
      {
        headerName: '이름',
        field: 'name',
        width: 180,
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
        headerName: '프로젝트명',
        field: 'projectName',
        minWidth: 180,
        flex: 1,
      },
      {
        headerName: '역할명',
        field: 'role',
        width: 180,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: '메뉴 경로',
        field: 'menuChannel',
        width: 180,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: '요청 경로',
        field: 'requestChannel',
        width: 200,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: 'API URL',
        field: 'apiDetails',
        width: 200,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: '응답 결과',
        field: 'result',
        width: 120,
        cellRenderer: memo((params: any) => {
          const colorMap: { [key: string]: string } = {
            성공: 'complete',
            실패: 'error',
            // cancel: 'error',
          };
          return (
            <UILabel variant='badge' intent={colorMap[params.value] as any}>
              {params.value}
            </UILabel>
          );
        }),
      },
      {
        headerName: '요청 일시',
        field: 'dateUse',
        width: 180,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
    ],
    []
  );

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

  const [value, setValue] = useState('10');

  // date 타입
  const [dateValueStart, setDateValueStart] = useState('2025.06.29');
  const [dateValueEnd, setDateValueEnd] = useState('2025.06.30');

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
        {/* 페이지 헤더 */}
        <UIPageHeader title='사용자 이용 현황' description='포탈 전체 사용자의 사용 이력 및 통계를 확인하고 관리할 수 있습니다.' />

        {/* 페이지 바디 */}
        <UIPageBody>
          {/* 탭 영역 */}
          <UIArticle className='article-tabs'>
            <div className='flex'>
              <UITabs items={tabOptions} activeId={activeTab} onChange={setActiveTab} size='large' />
            </div>
          </UIArticle>

          {/* [251105_퍼블수정] 검색영역 수정  */}
          <UIArticle className='article-filter'>
            <UIBox className='box-filter'>
              <UIGroup gap={40} direction='row'>
                <div style={{ width: 'calc(100% - 168px)' }}>
                  <table className='tbl_type_b'>
                    <tbody>
                      <tr>
                        <th>
                          <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                            조회 기간
                          </UITypography>
                        </th>
                        <td colSpan={3}>
                          <UIUnitGroup gap={32} direction='row'>
                            <div className='flex-1'>
                              <UIDropdown
                                placeholder='조회 기간 선택'
                                value={'이용 일시'}
                                options={[
                                  { value: '이용 일시', label: '이용 일시' },
                                  { value: '생성 일시', label: '생성 일시' },
                                  { value: '수정 일시', label: '수정 일시' },
                                  { value: '등록 일시', label: '등록 일시' },
                                ]}
                                isOpen={dropdownStates.dateType}
                                onClick={() => handleDropdownToggle('dateType')}
                                onSelect={value => handleDropdownSelect('dateType', value)}
                              />
                            </div>
                            <div className='flex-1' style={{ zIndex: '10' }}>
                              <UIUnitGroup gap={8} direction='row' vAlign='center'>
                                <div className='flex-1'>
                                  <UIInput.Date
                                    value={dateValueStart}
                                    onChange={e => {
                                      setDateValueStart(e.target.value);
                                    }}
                                  />
                                </div>

                                <UITypography variant='body-1' className='secondary-neutral-p w-[11px] justify-center'>
                                  ~
                                </UITypography>

                                <div className='flex-1'>
                                  <UIInput.Date
                                    value={dateValueEnd}
                                    onChange={e => {
                                      setDateValueEnd(e.target.value);
                                    }}
                                  />
                                </div>
                              </UIUnitGroup>
                            </div>
                          </UIUnitGroup>
                        </td>
                      </tr>
                      <tr>
                        <th>
                          <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                            조회 조건
                          </UITypography>
                        </th>
                        <td colSpan={3}>
                          <UIUnitGroup gap={32} direction='row'>
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
                            <div className='flex-1'>
                              <UIInput.Search
                                value={searchValue}
                                onChange={e => {
                                  setSearchValue(e.target.value);
                                }}
                                placeholder='검색어 입력'
                              />
                            </div>
                          </UIUnitGroup>
                        </td>
                      </tr>
                      <tr>
                        <th>
                          <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                            프로젝트명
                          </UITypography>
                        </th>
                        <td colSpan={3}>
                          <UIUnitGroup gap={32} direction='row' className='items-center'>
                            <div className='flex-1'>
                              <UIDropdown
                                value={'전체'}
                                placeholder='프로젝트명 선택'
                                options={[
                                  { value: '1', label: '전체' },
                                  { value: '2', label: '아이템1' },
                                  { value: '3', label: '아이템2' },
                                  { value: '4', label: '아이템3' },
                                ]}
                                isOpen={dropdownStates.projectName}
                                onClick={() => handleDropdownToggle('projectName')}
                                onSelect={value => handleDropdownSelect('projectName', value)}
                              />
                            </div>
                            <div className='flex flex-1 items-center'>
                              <UITypography variant='body-1' className='!w-[80px] secondary-neutral-800 text-body-1-sb'>
                                응답 결과
                              </UITypography>
                              <UIDropdown
                                value={'전체'}
                                placeholder='응답 결과 선택'
                                options={[
                                  { value: '1', label: '전체' },
                                  { value: '2', label: '아이템1' },
                                  { value: '3', label: '아이템2' },
                                  { value: '4', label: '아이템3' },
                                ]}
                                isOpen={dropdownStates.result}
                                onClick={() => handleDropdownToggle('result')}
                                onSelect={value => handleDropdownSelect('result', value)}
                              />
                            </div>
                          </UIUnitGroup>
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
                <div className='flex-shrink-0'>
                  <UIDataCnt count={rowData.length} prefix='총' unit='건' />
                </div>
                <div className='flex items-center gap-2'>
                  <div className='flex-shrink-0'>
                    <UIButton2 className='btn-tertiary-outline'>엑셀 다운로드</UIButton2>
                  </div>
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
                </div>
              </UIListContentBox.Header>
              <UIListContentBox.Body>
                <UIGrid
                  type='multi-select'
                  rowData={rowData}
                  columnDefs={columnDefs as any}
                  onClickRow={(_params: any) => {}}
                  onCheck={(_selectedIds: any[]) => {
                  }}
                />
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
