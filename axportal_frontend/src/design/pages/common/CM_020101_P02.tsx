import React, { useState } from 'react';

import { UIDataCnt, UIPagination } from '@/components/UI';
import { UIInput } from '@/components/UI/molecules';
import { UIArticle } from '@/components/UI/molecules/UIArticle';

import { UIGrid } from '../../../components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '../../../components/UI/molecules/list';
import { UIDropdown } from '../../../components/UI/molecules/dropdown/UIDropdown';

// AG_010102_P26 페이지
export const CM_020101_P02: React.FC = () => {
  const [searchValue, setSearchValue] = useState('');

  // 드롭다운 상태 관리
  const [dropdownStates, setDropdownStates] = useState({
    dateType: false,
    searchType: false,
    condition: false,
    menu: false,
    pageSize: false,
  });

  // 각 드롭다운 값 상태
  const [searchTypeValue, setSearchTypeValue] = useState('이름');

  // 드롭다운 핸들러
  const handleDropdownToggle = (key: keyof typeof dropdownStates) => {
    setDropdownStates(prev => ({
      ...Object.keys(prev).reduce((acc, k) => ({ ...acc, [k]: false }), {} as typeof prev),
      [key]: !prev[key],
    }));
  };

  // 데이터
  const projectData = [
    {
      id: '1',
      no: 1,
      name: '김신한',
      department: 'AI UNIT',
      position: '팀장',
      employeeNumber: '23012345',
    },
    {
      id: '2',
      no: 2,
      name: '홍길동',
      department: '슈퍼SOL플랫폼부',
      position: '선임',
      employeeNumber: '23012346',
    },
    {
      id: '3',
      no: 3,
      name: '이철수',
      department: 'DT추진부',
      position: '대리',
      employeeNumber: '23012347',
    },
    {
      id: '4',
      no: 4,
      name: '박영희',
      department: '시스템관리부',
      position: '사원',
      employeeNumber: '23012348',
    },
    {
      id: '5',
      no: 5,
      name: '최민수',
      department: '디지털금융부',
      position: '차장',
      employeeNumber: '23012349',
    },
    {
      id: '6',
      no: 6,
      name: '정수진',
      department: 'AI UNIT',
      position: '주임',
      employeeNumber: '23012350',
    },
  ];

  // 그리드 컬럼 정의
  const columnDefs: any = React.useMemo(
    () => [
      {
        headerName: 'NO',
        field: 'no' as const,
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
        field: 'name' as const,
        width: 230,
        cellStyle: { paddingLeft: '16px' },
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
        field: 'department' as const,
        width: 230,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '직급',
        field: 'position' as const,
        width: 230,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '행번',
        field: 'employeeNumber' as const,
        flex: 1,
        cellStyle: { paddingLeft: '16px' },
      },
    ],
    []
  );

  return (
    <section className='section-modal'>
      <UIArticle className='article-grid'>
        <UIListContainer>
          <UIListContentBox.Header>
            <div className='flex justify-between items-center w-full'>
              <div className='flex-shrink-0'>
                <div style={{ width: '168px', paddingRight: '8px' }}>
                  <UIDataCnt count={projectData.length} prefix='총' unit='건' />
                </div>
              </div>
              <div className='flex gap-3'>
                <div className='w-[180px]'>
                  <UIDropdown
                    value={searchTypeValue}
                    placeholder='조회 조건 선택'
                    options={[
                      { value: '이름', label: '이름' },
                      { value: '아이디', label: '아이디' },
                      { value: '이메일', label: '이메일' },
                      { value: '부서', label: '부서' },
                    ]}
                    isOpen={dropdownStates.searchType}
                    height={40}
                    onClick={() => handleDropdownToggle('searchType')}
                    onSelect={(_value: string) => {
                      setSearchTypeValue(_value);
                      setDropdownStates(prev => ({ ...prev, searchType: false }));
                    }}
                  />
                </div>
                <div className='w-[360px]'>
                  <UIInput.Search
                    value={searchValue}
                    placeholder='검색어 입력'
                    onChange={e => {
                      setSearchValue(e.target.value);
                    }}
                  />
                </div>
              </div>
            </div>
          </UIListContentBox.Header>
          <UIListContentBox.Body>
            <UIGrid
              type='single-select'
              rowData={projectData}
              columnDefs={columnDefs}
              onClickRow={(_params: any) => {}}
              onCheck={(_selectedIds: any[]) => {

              }}
            />
          </UIListContentBox.Body>
          <UIListContentBox.Footer>
            <UIPagination currentPage={1} totalPages={3} onPageChange={() => {}} className='flex justify-center' />
          </UIListContentBox.Footer>
        </UIListContainer>
      </UIArticle>
    </section>
  );
};
