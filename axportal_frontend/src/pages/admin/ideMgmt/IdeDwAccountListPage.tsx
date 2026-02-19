import React, { useEffect, useMemo, useState } from 'react';

import { UIGroup, UIInput, UIUnitGroup } from '@/components/UI/molecules';
import { UIBox, UIButton2, UIDataCnt, UITypography } from '@/components/UI/atoms';
import { UIPagination } from '@/components/UI/atoms/UIPagination';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIDropdown } from '@/components/UI/molecules/dropdown/UIDropdown';
import { type DwAccountRowData, useGetDwAccountList } from '@/services/admin/ideMgmt';

interface SearchValues {
  searchKeyword: string;
  category: string;
}

/**
 * 관리 > IDE 관리 > DW 계정 관리 (TAB)
 */
export const IdeDwAccountListPage = () => {
  // API 훅
  const { data: dwAccountList, isLoading } = useGetDwAccountList();

  // 검색 조건 (입력용 - 실시간)
  const [searchValues, setSearchValues] = useState<SearchValues>({
    searchKeyword: '',
    category: '전체',
  });

  // 조회된 검색 조건 (조회용 - Enter 또는 버튼 클릭 시만 변경)
  const [appliedSearchValues, setAppliedSearchValues] = useState<SearchValues>({
    searchKeyword: '',
    category: '전체',
  });

  // 페이지 사이즈
  const [pageSize, setPageSize] = useState('12개씩 보기');

  // 드롭다운 상태 관리
  const [dropdownStates, setDropdownStates] = useState({
    category: false,
  });

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

  // 검색/조회 핸들러
  const handleSearch = () => {
    setAppliedSearchValues(searchValues);
    setCurrentPage(1);
  };

  // API 응답을 그리드 행 데이터로 변환
  const rowData: DwAccountRowData[] = useMemo(() => {
    if (!dwAccountList) return [];

    return dwAccountList.map((account, index) => ({
      id: String(index + 1),
      accountId: account.accountId,
      accountType: account.role, // role → accountType 매핑
    }));
  }, [dwAccountList]);

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
        headerName: '계정 ID',
        field: 'accountId' as any,
        width: 800,
        cellStyle: {
          paddingLeft: '16px',
        },
        sortable: false,
      },
      {
        headerName: '계정 유형',
        field: 'accountType' as any,
        flex: 1,
        cellStyle: {
          paddingLeft: '16px',
        },
        sortable: false,
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
    ],
    []
  );

  // 검색/필터링이 적용된 데이터
  const filteredData = useMemo(() => {
    let result = rowData;

    // 검색어 필터링 (계정 ID)
    if (appliedSearchValues.searchKeyword.trim()) {
      const keyword = appliedSearchValues.searchKeyword.trim().toLowerCase();
      result = result.filter(item => item.accountId.toLowerCase().includes(keyword));
    }

    // 계정 유형 필터링 (대소문자 구분 없음)
    if (appliedSearchValues.category !== '전체') {
      result = result.filter(item => item.accountType.toLowerCase() === appliedSearchValues.category.toLowerCase());
    }

    return result;
  }, [rowData, appliedSearchValues.searchKeyword, appliedSearchValues.category]);

  // 페이지 상태
  const [currentPage, setCurrentPage] = useState(1);

  // 페이지 크기 변환
  const numericPageSize = useMemo(() => {
    return parseInt(pageSize.replace('개씩 보기', ''));
  }, [pageSize]);

  // 전체 페이지 수 계산
  const totalPages = useMemo(() => {
    return Math.ceil(filteredData.length / numericPageSize);
  }, [filteredData.length, numericPageSize]);

  // 페이지네이션이 적용된 데이터
  const paginatedData = useMemo(() => {
    const startIndex = (currentPage - 1) * numericPageSize;
    const endIndex = startIndex + numericPageSize;
    return filteredData.slice(startIndex, endIndex);
  }, [filteredData, currentPage, numericPageSize]);

  // 검색/필터 변경 시 페이지 리셋
  useEffect(() => {
    setCurrentPage(1);
  }, [appliedSearchValues.searchKeyword, appliedSearchValues.category]);

  return (
    <>
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
                    <td>
                      <div>
                        <UIInput.Search
                          value={searchValues.searchKeyword}
                          placeholder='계정 ID 입력'
                          onChange={e => {
                            setSearchValues(prev => ({ ...prev, searchKeyword: e.target.value }));
                          }}
                          onKeyDown={(e: React.KeyboardEvent<HTMLInputElement>) => {
                            if (e.key === 'Enter') {
                              handleSearch();
                            }
                          }}
                        />
                      </div>
                    </td>
                    <th>
                      <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                        계정 유형
                      </UITypography>
                    </th>
                    <td>
                      <div>
                        <UIDropdown
                          value={searchValues.category}
                          placeholder='전체'
                          options={[
                            { value: '전체', label: '전체' },
                            { value: 'Admin', label: 'Admin' },
                            { value: 'Analytics', label: 'Analytics' },
                            { value: 'Service', label: 'Service' },
                          ]}
                          isOpen={dropdownStates.category}
                          onClick={() => handleDropdownToggle('category')}
                          onSelect={value => handleDropdownSelect('category', value)}
                        />
                      </div>
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

      {/* 그리드 영역 */}
      <UIArticle className='article-grid'>
        <UIListContainer>
          <UIListContentBox.Header>
            <div className='w-full'>
              <UIUnitGroup gap={16} direction='column'>
                <div className='flex justify-between w-full items-center'>
                  <div className='flex-shrink-0'>
                    <div style={{ width: '168px', paddingRight: '8px' }}>
                      <UIDataCnt count={filteredData.length} prefix='총' unit='건' />
                    </div>
                  </div>
                  <div style={{ width: '160px', flexShrink: 0 }}>
                    <UIDropdown
                      value={String(pageSize)}
                      disabled={filteredData?.length === 0}
                      options={[
                        { value: '12개씩 보기', label: '12개씩 보기' },
                        { value: '36개씩 보기', label: '36개씩 보기' },
                        { value: '60개씩 보기', label: '60개씩 보기' },
                      ]}
                      onSelect={(value: string) => {
                        setPageSize(value);
                        setCurrentPage(1);
                      }}
                      onClick={() => {}}
                      height={40}
                      variant='dataGroup'
                    />
                  </div>
                </div>
              </UIUnitGroup>
            </div>
          </UIListContentBox.Header>
          <UIListContentBox.Body>
            <UIGrid type='default' rowData={paginatedData} columnDefs={columnDefs} loading={isLoading} onClickRow={(_params: any) => {}} onCheck={(_selectedIds: any[]) => {}} />
          </UIListContentBox.Body>
          <UIListContentBox.Footer>
            <UIPagination currentPage={currentPage} totalPages={totalPages || 1} onPageChange={page => setCurrentPage(page)} className='flex justify-center' />
          </UIListContentBox.Footer>
        </UIListContainer>
      </UIArticle>
    </>
  );
};
