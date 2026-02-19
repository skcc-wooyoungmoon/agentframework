import React, { useState, useRef, useMemo, useEffect, useCallback } from 'react';

import { UIDataCnt, UIPagination } from '@/components/UI';
import { UIInput } from '@/components/UI/molecules';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIButton2 } from '@/components/UI/atoms';

import { UIDropdown } from '@/components/UI/molecules/dropdown/UIDropdown/component';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { useGetApproverList } from '@/services/common/payReq.service';
/**
 * 인사 상태
 */
export const WorkStatus = {
  EMPLOYED: 'EMPLOYED',
  RESIGNED: 'RESIGNED',
} as const;

export type WorkStatus = (typeof WorkStatus)[keyof typeof WorkStatus];

interface Approver {
  id: string;
  no: number;
  name: string;
  department: string;
  position: string;
  employeeNumber: string;
}

interface PayReqPopupProps {
  onSelect?: (approver: Approver) => void;
  onClose?: () => void;
}

// PayReqPopup 페이지
export const PayReqPopup: React.FC<PayReqPopupProps> = ({ onSelect, onClose }) => {
  const [searchValue, setSearchValue] = useState('');
  const [selectedApprover, setSelectedApprover] = useState<Approver | null>(null);
  const [selectedApproverId, setSelectedApproverId] = useState<string | null>(null);

  // AG Grid API 참조
  const gridApiRef = useRef<any>(null);

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

  // 검색 파라미터 상태
  const [searchParams, setSearchParams] = useState({
    page: 1,
    size: 10,
    condition: 'profile',
    keyword: '',
    status: WorkStatus.EMPLOYED,
  });

  // 검색 타입 매핑 함수
  const mapFilterType = useCallback((v: string) => {
    if (v === '이름') return 'profile';
    if (v === '부서') return 'department';
    if (v === '직급') return 'position';
    return 'profile';
  }, []);

  const username = sessionStorage.getItem('USERNAME') || '';

  // API 호출
  const { data: approverListData, refetch } = useGetApproverList({
    username: username,
    condition: searchParams.condition,
    keyword: searchParams.keyword,
    status: searchParams.status,
  });

  useEffect(() => {
    if (approverListData) {
      // console.log('approverListData:', approverListData);
    }
  }, [approverListData]);

  // API 데이터 → Grid 행 데이터 매핑
  const gridRows = useMemo(() => {
    const list: any[] = Array.isArray(approverListData)
      ? (approverListData as any[])
      : ((approverListData as any)?.content ?? []);
    
    return (list || []).map((u: any, i: number) => ({
      id: u?.memberId ?? `user-${i}`,
      no: i + 1,
      name: u?.jkwNm || '',
      department: u?.deptNm || '',
      position: u?.position || '직급 미정', // API에서 직급 정보 확인 필요
      employeeNumber: u?.employeeNumber || u?.memberId || '',
    }));
  }, [approverListData]);

  // 로컬 페이징 계산
  const { totalPages, pagedRows } = useMemo(() => {
    const size = searchParams.size ?? 10;
    const page = searchParams.page ?? 1;
    const total = Math.max(1, Math.ceil((gridRows?.length || 0) / size));
    const start = (page - 1) * size;
    const end = start + size;
    return {
      totalPages: total,
      pagedRows: (gridRows || []).slice(start, end),
    };
  }, [gridRows, searchParams.page, searchParams.size]);

  // 데이터 변경 시 현재 페이지 보정
  useEffect(() => {
    if (searchParams.page > totalPages) {
      setSearchParams(prev => ({ ...prev, page: Math.max(totalPages, 1) }));
    }
  }, [totalPages, searchParams.page]);

  // 드롭다운 핸들러
  const handleDropdownToggle = (key: keyof typeof dropdownStates) => {
    setDropdownStates(prev => ({
      ...Object.keys(prev).reduce((acc, k) => ({ ...acc, [k]: false }), {} as typeof prev),
      [key]: !prev[key],
    }));
  };

  // 그리드 컬럼 정의
  const columnDefs: any = React.useMemo(
    () => [
      {
        headerName: '',
        field: 'selection',
        width: 50,
        minWidth: 50,
        maxWidth: 50,
        cellClass: 'text-center',
        headerClass: 'text-center',
        cellRenderer: (params: any) => {
          return (
            <input
              type="radio"
              name="projectSelection"
              checked={selectedApproverId === params.data.id}
              onChange={() => handleSelectApprover(params.data)}
              onClick={e => {
                e.stopPropagation();
              }}
            />
          );
        },
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
      {
        headerName: '이름',
        field: 'name' as const,
        width: 230,
        cellStyle: { paddingLeft: '16px' },
      },
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
    [selectedApproverId]
  );

  // 결재자 선택 핸들러 (통합)
  const handleSelectApprover = (data: Approver) => {
    setSelectedApprover(data);
    setSelectedApproverId(data.id);

    // 행 선택 상태 업데이트
    if (gridApiRef.current) {
      gridApiRef.current.deselectAll();
      gridApiRef.current.forEachNode((node: any) => {
        if (node.data.id === data.id) {
          node.setSelected(true);
        }
      });
    }
  };

  // 행 선택 핸들러
  const handleRowSelect = (params: any) => {
    handleSelectApprover(params.data);
  };

  // 선택 확인 버튼 핸들러
  const handleConfirm = () => {
    if (selectedApprover && onSelect) {
      onSelect(selectedApprover);
    }
  };

  // 취소 버튼 핸들러
  const handleCancel = () => {
    if (onClose) {
      onClose();
    }
  };

  // 검색 핸들러
  const handleSearch = () => {
    const nextCondition = mapFilterType(searchTypeValue);
    setSearchParams(prev => ({
      ...prev,
      page: 1,
      keyword: searchValue,
      condition: nextCondition,
    }));
    
    setTimeout(() => {
      try {
        refetch();
      } catch (err) {
        // console.warn('refetch error', err);
      }
    }, 0);
  };

  return (
    <section className='section-modal p-4'>
      <style>
        {`
          .ag-selection-checkbox {
            display: none !important;
            width: 0 !important;
            min-width: 0 !important;
            max-width: 0 !important;
            padding: 0 !important;
            margin: 0 !important;
          }
          .ag-cell-wrapper > .ag-selection-checkbox {
            display: none !important;
            width: 0 !important;
            min-width: 0 !important;
            padding: 0 !important;
            margin: 0 !important;
          }
        `}
      </style>
      <UIArticle className='article-grid'>
        <div className='article-body'>
          <UIListContainer>
            <UIListContentBox.Header>
              <div className='flex justify-between items-center w-full'>
                <div className='flex-shrink-0'>
                  <div style={{ width: '168px', paddingRight: '8px' }}>
                    <UIDataCnt count={gridRows.length} prefix='총' />
                  </div>
                </div>
                <div className='flex gap-3'>
                  <div className='w-[180px]'>
                    <UIDropdown
                      value={searchTypeValue}
                      placeholder='조회 조건 선택'
                      options={[
                        { value: '이름', label: '이름' },
                        { value: '부서', label: '부서' },
                        { value: '직급', label: '직급' },
                      ]}
                      isOpen={dropdownStates.searchType}
                      height={40}
                      onClick={() => handleDropdownToggle('searchType')}
                      onSelect={(value: string) => {
                        setSearchTypeValue(value);
                        setDropdownStates(prev => ({ ...prev, searchType: false }));
                      }}
                    />
                  </div>
                  <div className='form-group w-[360px]'>
                    <UIInput.Search
                      value={searchValue}
                      placeholder='검색어 입력'
                      onChange={e => setSearchValue(e.target.value)}
                      onKeyDown={e => {
                        if (e.key === 'Enter') {
                          handleSearch();
                        }
                      }}
                    />
                  </div>
                </div>
              </div>
            </UIListContentBox.Header>
            <UIListContentBox.Body>
              <UIGrid
                rowData={pagedRows}
                columnDefs={columnDefs}
                /* onClickRow={(params: any) => {
                  console.log('구성원 선택 onClickRow', params);
                }} */
                onCheck={handleRowSelect}
                // 타입 단언을 사용하여 오류 우회
                {...({ getRowId: (row: any) => (row && row.no !== undefined && row.no !== null ? row.no : row.id) } as any)}
            />
            </UIListContentBox.Body>
            <UIListContentBox.Footer className='ui-data-has-btn !mt-6'>
              <div className="flex justify-between items-center w-full">
                <UIPagination 
                  currentPage={searchParams.page} 
                  totalPages={totalPages} 
                  onPageChange={(page) => {
                    setSearchParams(prev => ({ ...prev, page }));
                  }} 
                  className='flex justify-center' 
                />
                <div className="flex gap-2">
                  <UIButton2
                    className='btn-tertiary-gray'
                    onClick={handleCancel}
                  >
                    취소
                  </UIButton2>
                  <UIButton2
                    className='btn-tertiary-blue'
                    onClick={handleConfirm}
                    disabled={!selectedApprover}
                  >
                    확인
                  </UIButton2>
                </div>
              </div>
            </UIListContentBox.Footer>
          </UIListContainer>
        </div>
      </UIArticle>
    </section>
  );
};