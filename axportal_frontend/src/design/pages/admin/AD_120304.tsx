import React, { useMemo, useState } from 'react';

import { UIBox, UIButton2, UIDataCnt, UILabel, UITypography, UIIcon2 } from '@/components/UI/atoms';
import { UIPagination } from '@/components/UI/atoms/UIPagination';
import { UITextLabel } from '@/components/UI/atoms/UITextLabel';
import { UIGroup, UIInput, UIUnitGroup } from '@/components/UI/molecules';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { UIDropdown } from '../../../components/UI/molecules/dropdown/UIDropdown';
import { UITabs } from '../../../components/UI/organisms/UITabs';
import { DesignLayout } from '../../components/DesignLayout';

interface SearchValues {
  dateType: string;
  dateRange: { startDate?: string; endDate?: string };
  searchType: string;
  searchKeyword: string;
  status: string;
  publicRange: string;
}

export const AD_120304 = () => {
  const [, setActiveTab] = useState('dataset');
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
      userName: '김신한',
      department: 'Data기획Unit',
      employeeStatus: '재직',
      lastLoginAt: '2025.03.24 18:23:43',
    },
    {
      id: '2',
      accountStatus: '이용 불가',
      userName: '이영희',
      department: 'AI개발팀',
      employeeStatus: '재직',
      lastLoginAt: '2025.03.23 14:15:32',
    },
    {
      id: '3',
      accountStatus: '이용 가능',
      userName: '박철수',
      department: 'Data기획Unit',
      employeeStatus: '휴직',
      lastLoginAt: '2025.03.22 09:45:21',
    },
    {
      id: '4',
      accountStatus: '이용 가능',
      userName: '최민수',
      department: 'AI개발팀',
      employeeStatus: '재직',
      lastLoginAt: '2025.03.24 16:30:15',
    },
    {
      id: '5',
      accountStatus: '이용 불가',
      userName: '정다은',
      department: 'Data분석팀',
      employeeStatus: '퇴사',
      lastLoginAt: '2025.03.20 11:20:43',
    },
    {
      id: '6',
      accountStatus: '이용 가능',
      userName: '홍길동',
      department: 'Data분석팀',
      employeeStatus: '재직',
      lastLoginAt: '2025.03.24 13:55:28',
    },
    {
      id: '7',
      accountStatus: '이용 가능',
      userName: '김미영',
      department: 'AI개발팀',
      employeeStatus: '재직',
      lastLoginAt: '2025.03.24 17:42:11',
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
      {
        headerName: '계정 상태',
        field: 'accountStatus' as any,
        width: 120,
        cellRenderer: React.memo((params: any) => {
          const isActive = params.data.id !== '2'; // id가 '2'인 항목만 비활성화
          return <UITextLabel intent={isActive ? 'blue' : 'gray'}>{isActive ? '활성화' : '비활성화'}</UITextLabel>;
        }),
      },
      // 251113_퍼블수정 그리드 컬럼 속성 '이름' 영역 수정 S
      {
        headerName: '이름',
        field: 'userName' as any,
        minWidth: 450,
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
        headerName: '부서',
        field: 'department' as any,
        minWidth: 450,
        flex: 1,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: '인사 상태',
        field: 'employeeStatus' as any,
        width: 150,
        cellRenderer: React.memo((params: any) => {
          const statusColors = {
            재직: 'complete',
            퇴사: 'error',
            // '휴직': 'warning',
          } as const;
          return (
            <UILabel variant='badge' intent={statusColors[params.value as keyof typeof statusColors]}>
              {params.value}
            </UILabel>
          );
        }),
      },
      {
        headerName: '마지막 접속 일시',
        field: 'lastLoginAt' as any,
        width: 180,
        minWidth: 190,
        maxWidth: 190,
        // suppressSizeToFit: true,
        cellStyle: {
          paddingLeft: '16px',
        },
      },

      // [251106_퍼블수정] : 더보기 버튼 컬럼 영역 삭제
      // {
      //   headerName: '',
      //   field: 'more', // 더보기 컬럼 필드명 (고정)
      //   width: 56,
      // },
    ],
    [rowData]
  );

  // 탭 아이템 정의
  const tabItems = [
    { id: 'dataset', label: '기본 정보' },
    { id: 'rightsInformation', label: '권한 정보' },
    { id: 'memberInformation', label: '구성원 정보' },
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
        <UIPageHeader title='역할 조회' description='' />

        {/* 페이지 바디 */}
        <UIPageBody>
          <UIArticle className='article-filter pb-4'>
            {/* className='project-card bg-gray' bg-gray 배경이 회색일 경우 해당라인 클래스 추가  */}
            <div className='project-card'>
              <UIUnitGroup gap={8} direction='row' vAlign='center' className='mb-6'>
                <UIIcon2 className='ic-system-24-project' aria-hidden='true'></UIIcon2>
                <UITypography variant='title-4' className='secondary-neutral-700'>
                  대출 상품 추천
                </UITypography>
              </UIUnitGroup>
              <ul className='flex flex-col gap-4'>
                <li>
                  <UITypography variant='body-1' className='col-gray'>
                    역할명
                  </UITypography>
                  <UITypography variant='title-4' className='secondary-neutral-700'>
                    사용자 피드백 관리자
                  </UITypography>
                </li>
                <li>
                  <UITypography variant='body-1' className='col-gray'>
                    설명
                  </UITypography>
                  <UITypography variant='title-4' className='secondary-neutral-700'>
                    추천된 대출 상품에 대한 고객 피드백을 수집·분석하고, 개선 사항을 전달
                  </UITypography>
                </li>
              </ul>
            </div>
          </UIArticle>
          <UIArticle className='article-tabs'>
            {/* 아티클 탭 */}
            <UITabs items={tabItems} activeId='memberInformation' size='large' onChange={setActiveTab} />
          </UIArticle>

          {/* [251105_퍼블수정] 검색영역 수정 */}
          <UIArticle className='article-filter'>
            <UIBox className='box-filter'>
              <UIGroup gap={40} direction='row'>
                <div style={{ width: 'calc(100% - 168px)' }}>
                  <table className='tbl_type_b'>
                    <tbody>
                      {/* <tr>
                        <th>
                          <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                            조회 기간
                          </UITypography>
                        </th>
                        <td colSpan={3}>
                          <UIUnitGroup gap={32} direction='row'>
                            <div className='flex-1'>
                              <UIDropdown
                                value={'마지막 접속 일시'}
                                placeholder='조회 조건 선택'
                                options={[
                                  { value: '이름', label: '이름' },
                                  { value: '아이디', label: '아이디' },
                                  { value: '이메일', label: '이메일' },
                                  { value: '부서', label: '부서' },
                                ]}
                                isOpen={dropdownStates.searchType}
                                onClick={() => handleDropdownToggle('searchType')}
                                onSelect={value => handleDropdownSelect('searchType', value)}
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
                      </tr> */}
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
                            계정 상태
                          </UITypography>
                        </th>
                        <td colSpan={3}>
                          <UIUnitGroup gap={32} direction='row' className='items-center'>
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
                            <div className='flex flex-1 items-center'>
                              <UITypography variant='body-1' className='!w-[80px] secondary-neutral-800 text-body-1-sb'>
                                인사 상태
                              </UITypography>
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
                <div className='w-full'>
                  <UIUnitGroup gap={16} direction='column'>
                    <div className='flex justify-between w-full items-center'>
                      <div className='flex-shrink-0'>
                        <div style={{ width: '168px', paddingRight: '8px' }}>
                          <UIDataCnt count={rowData.length} prefix='총' unit='건' />
                        </div>
                      </div>
                      <div className='flex' style={{ gap: '12px' }}>
                        <div className=''>
                          {/* [251106_퍼블수정] 텍스트 수정 */}
                          <UIButton2 className='btn-tertiary-outline'>구성원 역할 변경</UIButton2>
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
                </div>
              </UIListContentBox.Header>
              <UIListContentBox.Body>
                <UIGrid
                  type='default' // [251106_퍼블수정] : 체크박스 삭제 > default
                  rowData={rowData}
                  columnDefs={columnDefs}
                  // moreMenuConfig={moreMenuConfig} //  // [251106_퍼블수정] : 더보기 버튼 삭제
                  onClickRow={(_params: any) => {}}
                  onCheck={(_selectedIds: any[]) => {}}
                />
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
