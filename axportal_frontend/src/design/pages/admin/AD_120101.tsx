import React, { useMemo, useState } from 'react';

import { UIBox, UIButton2, UIDataCnt, UITypography } from '@/components/UI/atoms';
import { UIPagination } from '@/components/UI/atoms/UIPagination';
import { UIGroup, UIInput, UIUnitGroup } from '@/components/UI/molecules';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { UIDropdown } from '../../../components/UI/molecules/dropdown/UIDropdown';
import { DesignLayout } from '../../components/DesignLayout';

interface SearchValues {
  dateType: string;
  dateRange: { startDate?: string; endDate?: string };
  searchType: string;
  searchKeyword: string;
  status: string;
  publicRange: string;
}

export const AD_120101 = () => {
  const [value, setValue] = useState('12개씩 보기');
  const [, setSearchValues] = useState<SearchValues>({
    dateType: '생성일시',
    dateRange: { startDate: '2025.06.30', endDate: '2025.07.30' },
    searchType: '이름',
    searchKeyword: '',
    status: '전체',
    publicRange: '전체',
  });

  // 드롭다운 상태 관리
  const [dropdownStates, setDropdownStates] = useState({
    dateType: false,
    searchType: false,
    status: false,
    publicRange: false,
  });

  // search 타입
  const [searchValue, setSearchValue] = useState('');

  // 샘플 데이터 (rowData로 변수명 변경)
  const rowData = [
    {
      id: '1',
      accountStatus: '이용 가능',
      projectName: '슈퍼SOL 챗봇 개발',
      description: '슈퍼SOL에서 사용할 챗봇을 개발',
      createdDate: '2025.03.24 18:23:43',
      modifiedDate: '2025.03.24 18:23:43',
    },
    {
      id: '2',
      accountStatus: '이용 불가',
      projectName: '데이터분석',
      description: '데이터 기반 인사이트를 도출하고 분석 업무를 수행',
      createdDate: '2025.03.23 14:15:32',
      modifiedDate: '2025.03.23 14:15:32',
    },
    {
      id: '3',
      accountStatus: '이용 가능',
      projectName: '모델검증',
      description: '모델 성능과 품질을 검증',
      createdDate: '2025.03.22 09:45:21',
      modifiedDate: '2025.03.22 09:45:21',
    },
    {
      id: '4',
      accountStatus: '이용 가능',
      projectName: '테스트',
      description: 'AI개발팀',
      createdDate: '2025.03.24 16:30:15',
      modifiedDate: '2025.03.24 16:30:15',
    },
    {
      id: '5',
      accountStatus: '이용 불가',
      projectName: '품질관리',
      description:
        '테스트테스트테스트테스트테스트테스트테스트테스트테스트테스트 환테스트테스트테스트 환테스트테스트테스트 환테스트테스트테스트 환테스트테스트테스트 환테스트테스트테스트 환테스트테스트테스트 환테스트테스트테스트 환테스트테스트테스트 환테스트테스트테스트 환테스트테스트테스트 환테스트테스트테스트 환테스트테스트테스트 환경에서 기능을 점점',
      createdDate: '2025.03.20 11:20:43',
      modifiedDate: '2025.03.20 11:20:43',
    },
    {
      id: '6',
      accountStatus: '이용 가능',
      projectName: '서비스운영',
      description:
        '테스트테스트테스트테스트테스트테스트테스트테스트테스트테스트 환테스트테스트테스트 환테스트테스트테스트 환테스트테스트테스트 환테스트테스트테스트 환테스트테스트테스트 환테스트테스트테스트 환테스트테스트테스트 환테스트테스트테스트 환테스트테스트테스트 환테스트테스트테스트 환테스트테스트테스트 환테스트테스트테스트 환경에서 기능을 점점',
      createdDate: '2025.03.24 13:55:28',
      modifiedDate: '2025.03.24 1 3:55:28',
    },
    {
      id: '7',
      accountStatus: '이용 가능',
      projectName: 'API연계',
      description: '서비스 운영 및 유지보수를 담다',
      createdDate: '2025.03.24 17:42:11',
      modifiedDate: '2025.03.24 17:42:11',
    },
    {
      id: '8',
      accountStatus: '이용 가능',
      projectName: 'API연계',
      description: '외부 시스템과의 API 연동을 담당',
      createdDate: '2025.03.24 17:42:11',
      modifiedDate: '2025.03.24 17:42:11',
    },
    {
      id: '9',
      accountStatus: '이용 가능',
      projectName: '지식관리',
      description: '지식 관리 및 문서화를 수행',
      createdDate: '2025.03.24 17:42:11',
      modifiedDate: '2025.03.24 17:42:11',
    },
    {
      id: '10',
      accountStatus: '이용 가능',
      projectName: '신기술적용',
      description: '신기술 도입과 연구를 담당',
      createdDate: '2025.03.24 17:42:11',
      modifiedDate: '2025.03.24 17:42:11',
    },
    {
      id: '11',
      accountStatus: '이용 가능',
      projectName: '리스크분석',
      description: '리스크 식별과 분석을 수행',
      createdDate: '2025.03.24 17:42:11',
      modifiedDate: '2025.03.24 17:42:11',
    },
    {
      id: '12',
      accountStatus: '이용 가능',
      projectName: '사용자지원',
      description: '사용자 지원과 고객 응대를 담당',
      createdDate: '2025.03.24 17:42:11',
      modifiedDate: '2025.03.24 17:42:11',
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
      {
        headerName: '프로젝트명',
        field: 'projectName' as any,
        width: 272,
      },
      // [251113_퍼블수정] 그리드 컬럼 속성 수정
      {
        headerName: '설명',
        field: 'description' as any,
        minWidth: 700,
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
        headerName: '생성일시',
        field: 'createdDate',
        width: 180,
      },
      {
        headerName: '최종 수정일시',
        field: 'modifiedDate' as any,
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
      initialMenu={{ id: 'data', label: '데이터' }}
      initialSubMenu={{
        id: 'admin-user',
        label: '사용자 관리',
        icon: 'ico-lnb-menu-20-admin-user',
      }}
    >
      {/* 섹션 페이지 */}
      <section className='section-page'>
        {/* 페이지 헤더 */}
        <UIPageHeader title='프로젝트 관리' description={['포탈 내 프로젝트를 확인하고 관리할 수 있습니다.', '프로젝트를 선택하여 구성원들을 초대하고 역할을 부여해 보세요.']} />

        {/* 페이지 바디 */}
        <UIPageBody>
          <UIArticle className='article-filter'>
            <UIBox className='box-filter'>
              <UIGroup gap={40} direction='row'>
                <div style={{ width: 'calc(100% - 168px)' }}>
                  <table className='tbl_type_b'>
                    <tbody>
                      {/* [251202_퍼블수정] : 조회 기간 영역삭제 */}
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
                                value={'프로젝트명'}
                                placeholder='조회 조건 선택'
                                options={[
                                  { value: '1', label: '전체' },
                                  { value: '2', label: '프로젝트명' },
                                  { value: '3', label: '설명' },
                                ]}
                                isOpen={dropdownStates.searchType}
                                onClick={() => handleDropdownToggle('searchType')}
                                onSelect={value => handleDropdownSelect('searchType', value)}
                              />
                            </div>
                            <div className='flex-1'>
                              {' '}
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
                    <div className='flex'>
                      <div style={{ width: '160px', flexShrink: 0 }}>
                        <UIDropdown
                          value={String(value)}
                          disabled={true}
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
                  </div>
                </UIUnitGroup>
              </UIListContentBox.Header>
              <UIListContentBox.Body>
                <UIGrid type='default' rowData={rowData} columnDefs={columnDefs} onClickRow={(_params: any) => {}} onCheck={(_selectedIds: any[]) => {}} />
              </UIListContentBox.Body>
              {/* [참고] classname 관련
                  - 그리드 하단 (삭제) 버튼이 있는 경우 classname 지정 (예시) <UIListContentBox.Footer classname="ui-data-has-btn">
                  - 그리드 하단 (버튼) 없는 경우 classname 없이 (예시) <UIListContentBox.Footer>
                */}
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
