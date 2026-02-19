import React, { useMemo, useState } from 'react';

import { UIBox, UIButton2, UIDataCnt, UILabel, UITypography } from '@/components/UI/atoms';
import { UIPagination } from '@/components/UI/atoms/UIPagination';
import { UITextLabel } from '@/components/UI/atoms/UITextLabel';
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

export const AD_010101 = () => {
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

  // 사용자 테이블 데이터
  const rowData = [
    {
      id: '1',
      accountStatus: '이용 가능',
      userName: '김신한',
      department: 'Data기획Unit',
      employeeStatus: '재직',
      employeeNum: '23432190',
      position: '프로',
      lastLoginAt: '2025.03.24 18:23:43',
    },
    {
      id: '2',
      accountStatus: '이용 불가',
      userName: '이영희',
      department: 'AI개발팀',
      employeeStatus: '재직',
      employeeNum: '23432190',
      position: '프로',
      lastLoginAt: '2025.03.23 14:15:32',
    },
    {
      id: '3',
      accountStatus: '이용 가능',
      userName: '박철수',
      department: 'Data기획Unit',
      employeeStatus: '휴직',
      employeeNum: '23432190',
      position: '프로',
      lastLoginAt: '2025.03.22 09:45:21',
    },
    {
      id: '4',
      accountStatus: '이용 가능',
      userName: '최민수',
      department: 'AI개발팀',
      employeeStatus: '재직',
      employeeNum: '23432190',
      position: '프로',
      lastLoginAt: '2025.03.24 16:30:15',
    },
    {
      id: '5',
      accountStatus: '이용 불가',
      userName: '정다은',
      department: 'Data분석팀',
      employeeStatus: '퇴사',
      position: '프로',
      employeeNum: '23432190',
      lastLoginAt: '2025.03.20 11:20:43',
    },
    {
      id: '6',
      userName: '홍길동',
      department: 'Data분석팀',
      employeeStatus: '재직',
      employeeNum: '23432190',
      position: '프로',
      lastLoginAt: '2025.03.24 13:55:28',
    },
    {
      id: '7',
      accountStatus: '이용 가능',
      userName: '김미영',
      department: 'AI개발팀',
      employeeStatus: '재직',
      employeeNum: '23432190',
      position: '프로',
      lastLoginAt: '2025.03.24 17:42:11',
    },
    {
      id: '8',
      accountStatus: '이용 가능',
      userName: '김미영',
      department: 'AI개발팀',
      employeeStatus: '재직',
      employeeNum: '23432190',
      position: '프로',
      lastLoginAt: '2025.03.24 17:42:11',
    },
    {
      id: '9',
      accountStatus: '이용 가능',
      userName: '김미영',
      department: 'AI개발팀',
      employeeStatus: '재직',
      employeeNum: '23432190',
      position: '프로',
      lastLoginAt: '2025.03.24 17:42:11',
    },
    {
      id: '10',
      accountStatus: '이용 가능',
      userName: '김미영',
      department: 'AI개발팀',
      employeeStatus: '재직',
      employeeNum: '23432190',
      position: '프로',
      lastLoginAt: '2025.03.24 17:42:11',
    },
    {
      id: '11',
      accountStatus: '이용 가능',
      userName: '김미영',
      department: 'AI개발팀',
      employeeStatus: '재직',
      employeeNum: '23432190',
      position: '프로',
      lastLoginAt: '2025.03.24 17:42:11',
    },
    {
      id: '12',
      accountStatus: '이용 가능',
      userName: '김미영',
      department: 'AI개발팀',
      employeeStatus: '재직',
      employeeNum: '23432190',
      position: '프로',
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
          // id='2' 또는 퇴사인 경우 분기
          if (params.data.id === '2') {
            return <UITextLabel intent='gray'>비활성화</UITextLabel>;
          } else if (params.data.employeeStatus === '퇴사') {
            return <UITextLabel intent='red'>탈퇴</UITextLabel>;
          }
          return <UITextLabel intent='blue'>활성화</UITextLabel>;
        }),
      },
      // 251113_퍼블수정 그리드 컬럼 속성 '이름' 영역 수정 S
      {
        headerName: '이름',
        field: 'userName' as any,
        minWidth: 272,
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
        flex: 1,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: '직급',
        field: 'position',
        width: 268,
      },
      {
        headerName: '인사 상태',
        field: 'employeeStatus' as any,
        width: 130,
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
        headerName: '행번',
        field: 'employeeNum',
        width: 268,
      },
      {
        headerName: '마지막 접속 일시',
        field: 'lastLoginAt' as any,
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
        <UIPageHeader title='사용자 관리' description='포탈을 이용하는 모든 사용자를 확인하고 관리할 수 있습니다.' />

        {/* 페이지 바디 */}
        <UIPageBody>
          <UIArticle className='article-filter'>
            <UIBox className='box-filter'>
              <UIGroup gap={40} direction='row'>
                <div style={{ width: 'calc(100% - 168px)' }}>
                  {/* [251124_퍼블수정] 테이블 영역 수정 */}
                  <table className='tbl_type_b'>
                    <tbody>
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
                            <div className='flex flex-1 items-center'>
                              <div className='w-[100px]'>
                                <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                                  인사 상태
                                </UITypography>
                              </div>
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
                          onSelect={(value: string) => {setValue(value);
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
                <UIGrid
                  type='default'
                  rowData={rowData}
                  columnDefs={columnDefs}
                  onClickRow={(_params: any) => {}}
                  onCheck={(_selectedIds: any[]) => {
                  }}
                />
              </UIListContentBox.Body>
              {/* [참고] className 관련
                  - 그리드 하단 (삭제) 버튼이 있는 경우 className 지정 (예시) <UIListContentBox.Footer className="ui-data-has-btn">
                  - 그리드 하단 (버튼) 없는 경우 className 없이 (예시) <UIListContentBox.Footer>
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
