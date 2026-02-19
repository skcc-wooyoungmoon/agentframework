import React, { useState, useMemo } from 'react';

import { UIButton2, UITypography } from '../../../components/UI/atoms';
import { UIBox } from '../../../components/UI/atoms/UIBox';
import { UIDataCnt } from '../../../components/UI/atoms/UIDataCnt';
import { UIPagination } from '../../../components/UI/atoms/UIPagination';
import { UIDropdown, UIGroup, UIInput, UIUnitGroup } from '../../../components/UI/molecules';
import { UIGrid } from '../../../components/UI/molecules/grid';
import { UITabs } from '../../../components/UI/organisms';
import { UIListContainer, UIListContentBox } from '../../../components/UI/molecules/list';
import { UIArticle } from '../../../components/UI/molecules/UIArticle';
import { UIPageBody } from '../../../components/UI/molecules/UIPageBody';
import { UIPageHeader } from '../../../components/UI/molecules/UIPageHeader';
import { DesignLayout } from '../../components/DesignLayout';

interface SearchValues {
  dateType: string;
  dateRange: { startDate?: string; endDate?: string };
  searchType: string;
  searchKeyword: string;
  userRole: string;
  userStatus: string;
  hrStatus: string;
}

export const AD_010501 = () => {
  const [activeTab, setActiveTab] = useState('project');
  const [value, setValue] = useState('10개씩 보기');

  const [searchValues, setSearchValues] = useState<SearchValues>({
    dateType: '생성일시',
    dateRange: { startDate: '', endDate: '' },
    searchType: '',
    searchKeyword: '',
    userRole: '전체',
    userStatus: '전체',
    hrStatus: '전체',
  });

  // 드롭다운 상태 관리
  const [dropdownStates, setDropdownStates] = useState({
    dateType: false,
    searchType: false,
    userRole: false,
    userStatus: false,
    hrStatus: false,
    pageSize: false,
  });

  // 탭 옵션 정의
  const tabOptions = [
    { id: 'basic', label: '기본 정보' },
    { id: 'project', label: '프로젝트 정보' },
  ];

  // 샘플 데이터 (rowData로 변수명 변경)
  const rowData = [
    {
      id: '1',
      projectName: 'AI 챗봇 개발 프로젝트',
      projectDescription: 'GPT-4 기반의 고객 상담용 AI 챗봇을 개발하는 프로젝트입니다. 자연어 처리 기술을 활용하여 고객 문의 응답 자동화를 목표로 합니다.',
      createdDate: '2025.03.24 18:32:43',
      modifiedDate: '2025.03.24 18:32:43',
      more: 'more',
    },
    {
      id: '2',
      projectName: '데이터 분석 플랫폼',
      projectDescription: '대용량 데이터 분석을 위한 플랫폼 구축 프로젝트입니다. 실시간 데이터 처리와 시각화 기능을 제공합니다.',
      createdDate: '2025.03.23 15:20:15',
      modifiedDate: '2025.03.24 10:15:22',
      more: 'more',
    },
    {
      id: '3',
      projectName: '보안 시스템 강화',
      projectDescription: '기존 보안 시스템의 취약점을 보완하고 새로운 보안 정책을 적용하는 프로젝트입니다.',
      createdDate: '2025.03.22 14:45:22',
      modifiedDate: '2025.03.23 09:30:11',
      more: 'more',
    },
    {
      id: '4',
      projectName: '모바일 앱 리뉴얼',
      projectDescription: '사용자 경험 개선을 위한 모바일 애플리케이션 전면 리뉴얼 프로젝트입니다. UI/UX 디자인과 성능 최적화가 주요 목표입니다.',
      createdDate: '2025.03.21 09:30:00',
      modifiedDate: '2025.03.22 16:45:33',
      more: 'more',
    },
    {
      id: '5',
      projectName: '클라우드 마이그레이션',
      projectDescription: '온프레미스 시스템을 클라우드 환경으로 이전하는 프로젝트입니다. 비용 절감과 확장성 향상을 기대합니다.',
      createdDate: '2025.03.20 16:15:30',
      modifiedDate: '2025.03.21 11:20:45',
      more: 'more',
    },
  ];

  const handleSearch = () => {};

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
      {
        headerName: '프로젝트명',
        field: 'projectName',
        width: 272,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: '설명',
        field: 'projectDescription',
        flex: 1,
        minWidth: 472,
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
        headerName: '생성 일시',
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
    [rowData]
  );

  return (
    <DesignLayout
      initialMenu={{ id: 'admin', label: '관리자' }}
      initialSubMenu={{
        id: 'user-management',
        label: '사용자 조회',
        icon: 'ic-lnb-menu-20-user',
      }}
    >
      <section className='section-page'>
        <UIPageHeader title='사용자 조회' description='' />

        <UIPageBody>
          {/* 탭 영역 */}
          <UIArticle>
            <UITabs items={tabOptions} activeId={activeTab} onChange={setActiveTab} size='large' />
          </UIArticle>

          {/* 검색 영역 */}
          <UIArticle className='article-filter'>
            <UIBox className='box-filter'>
              <UIGroup gap={40} direction='row'>
                <div style={{ width: 'calc(100% - 168px)' }}>
                  <table className='tbl_type_b'>
                    <tbody>
                      {/* [251124_퍼블수정] 테이블 영역 수정 : 조회 기간 영역 삭제 */}
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
                                value={searchValues.searchType}
                                placeholder='조회 조건 선택'
                                options={[
                                  { value: 'value1', label: '프로젝트명' },
                                  { value: 'value2', label: '설명' },
                                ]}
                                isOpen={dropdownStates.searchType}
                                onClick={() => handleDropdownToggle('searchType')}
                                onSelect={value => handleDropdownSelect('searchType', value)}
                              />
                            </div>
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
                  <UIButton2 className='btn-secondary-blue' style={{ width: '100%' }} onClick={handleSearch}>
                    조회
                  </UIButton2>
                </div>
              </UIGroup>
            </UIBox>
          </UIArticle>

          {/* 데이터 그리드 컴포넌트 */}
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
                    <div style={{ width: '160px', flexShrink: 0 }}>
                      <UIDropdown
                        value={String(value)}
                        disabled={true}
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
                </UIUnitGroup>
              </UIListContentBox.Header>
              <UIListContentBox.Body>
                <UIGrid
                  type='default'
                  rowData={rowData}
                  columnDefs={columnDefs}
                  onClickRow={(_params: any) => {

                  }}
                  onCheck={(_selectedIds: any[]) => {

                  }}
                />
              </UIListContentBox.Body>
              <UIListContentBox.Footer>
                <UIPagination currentPage={1} totalPages={5} onPageChange={(_page: number) => {}} className='flex justify-center' />
              </UIListContentBox.Footer>
            </UIListContainer>
          </UIArticle>
        </UIPageBody>
      </section>
    </DesignLayout>
  );
};
