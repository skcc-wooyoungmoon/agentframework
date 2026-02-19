import React, { useCallback, useEffect, useMemo, useState } from 'react';

import { useAtom } from 'jotai';

import { UIButton2, UIDataCnt, UILabel, UITextLabel, UITypography } from '@/components/UI/atoms';
import { UIPagination } from '@/components/UI/atoms/UIPagination';
import { UIArticle, UIInput, UIPopupBody, UIPopupFooter, UIPopupHeader, UIUnitGroup } from '@/components/UI/molecules';
import { UIDropdown } from '@/components/UI/molecules/dropdown/UIDropdown/component';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { useGetProjUserGetMe, useGetProjUserList } from '@/services/home/proj/projBaseInfo.service';
import { projCreBaseInfoAtom, projCreSelectedMembersAtom } from '@/stores/home/proj/projCreWizard.atoms';
import { dateUtils } from '@/utils/common';

/**
 * 인사 상태
 */
export const WorkStatus = {
  EMPLOYED: 'EMPLOYED',
  RESIGNED: 'RESIGNED',
} as const;

export type WorkStatus = (typeof WorkStatus)[keyof typeof WorkStatus];

interface ProjCreStep2MemSelProps {
  currentStep: number;
  stepperItems: any[];
  onComfirmStep: () => void;
  onPreviousStep: () => void;
}

export const ProjCreStep2MemSel: React.FC<ProjCreStep2MemSelProps> = ({ onPreviousStep }) => {
  const [baseInfo] = useAtom(projCreBaseInfoAtom);
  const [selectedMembers, setSelectedMembers] = useAtom(projCreSelectedMembersAtom);
  // const [isPopupOpen] = useState(true);

  const [searchText, setSearchText] = useState('');
  // Dropdown internal value: 'name' | 'deaprtment' (mapped to API filterType)
  const [value, setValue] = useState('name');
  // const { openAlert } = useModal();
  // console.log('baseInfo 렌더링', { baseInfo });

  const [searchParams, setSearchParams] = useState({
    page: 1,
    size: 12,
    condition: 'profile',
    keyword: '',
    status: WorkStatus.EMPLOYED,
  });

  const mapFilterType = useCallback((v: string) => {
    if (v === 'name' || v === '이름') return 'profile';
    if (v === 'deaprtment' || v === '부서') return 'department';
    return v || 'profile';
  }, []);

  const username = sessionStorage.getItem('USERNAME') || '';
  const { data: projectUserData } = useGetProjUserGetMe(username);

  const { data: projUserInfo, refetch } = useGetProjUserList({
    // enabled: isPopupOpen, // 팝업이 열려있을 때만 API 호출
    username: username,
    condition: searchParams.condition,
    keyword: searchParams.keyword,
    status: searchParams.status,
  });

  useEffect(() => {
    if (projUserInfo) {
      // console.log('projUserInfo: >>', projUserInfo);
    }
    if (projectUserData) {
      // console.log('projectUserData: >>', projectUserData);
    }
  }, [projUserInfo, projectUserData]);

  // API 데이터 → Grid 행 데이터 매핑
  const gridRows = useMemo(() => {
    const list: any[] = Array.isArray(projUserInfo) ? (projUserInfo as any[]) : ((projUserInfo as any)?.content ?? []);
    return (list || []).map((u: any, i: number) => {
      const status = u?.dmcStatus;
      const employmentStatus = u?.retrJkwYn === '0' ? '재직' : '퇴사';
      const userRoleMap: Record<string, string> = {
        ACTIVE: '활성화',
        WITHDRAW: '비활성화',
        DORMANT: '탈퇴',
      };
      const userRole = userRoleMap[status] || '활성화';
      return {
        ...u,
        id: u?.memberId ?? u?.memberId,
        no: i + 1,
        name: u?.jkwNm,
        department: u?.deptNm,
        employmentStatus,
        lastLoginAt: u?.lstLoginAt,
        userRole,
      };
    });
  }, [projUserInfo]);

  // 로컬 페이징 계산 (10개 단위)
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
  }, [totalPages]);

  // 현재 페이지에서 선택된 행들만 추려서 UIGrid에 전달 (객체 동일성 보장)
  const selectedRowsInCurrentPage = useMemo(() => {
    const idSet = new Set((selectedMembers || []).map((id: any) => String(id)));
    return (pagedRows || []).filter((row: any) => idSet.has(String(row?.id ?? row?.memberId)));
  }, [pagedRows, selectedMembers]);

  // 체크박스 선택 핸들러: 선택된 "ID"를 전역 상태에 저장하여 페이지 이동 후에도 유지
  const handleCheck = useCallback(
    (rows: any[]) => {
      try {
        const getId = (item: any) => String(item?.id ?? item?.memberId ?? '');
        // 현재 페이지에 표시된 ID 집합
        const currentPageIdSet = new Set((pagedRows || []).map((r: any) => getId(r)));
        // 현재 페이지 외의 이전 선택 ID는 보존
        const prevKeptIds = (selectedMembers || []).filter((id: any) => !currentPageIdSet.has(String(id)));
        // 이번 페이지에서 체크된 행의 ID
        const checkedIds = (Array.isArray(rows) ? rows : []).map(getId);
        // 병합 + 중복 제거
        const nextIds: string[] = Array.from(new Set([...prevKeptIds.map(String), ...checkedIds.map(String)]));
        setSelectedMembers(nextIds as any);
      } catch (e) {
        // console.warn('handleCheck error', e);
      }
    },
    [setSelectedMembers, pagedRows, selectedMembers]
  );

  // UIGrid 컬럼 설정
  const columnDefs: any[] = useMemo(
    () => [
      {
        headerName: 'NO',
        field: 'no',
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
        } as const,
        sortable: false,
        suppressHeaderMenuButton: true,
      },
      {
        headerName: '계정 상태',
        field: 'userRole',
        width: 120,
        cellStyle: {
          paddingLeft: '16px',
        } as const,
        cellRenderer: React.memo((params: any) => {
          return <UITextLabel intent={params.value == '활성화' ? 'blue' : params.value == '비활성화' ? 'gray' : 'red'}>{params.value}</UITextLabel>;
        }),
      },
      {
        headerName: '이름',
        field: 'name',
        width: 272,
        cellStyle: {
          paddingLeft: '16px',
        } as const,
      },
      {
        headerName: '부서',
        field: 'department',
        flex: 1,
        cellStyle: {
          paddingLeft: '16px',
        } as const,
      },
      {
        headerName: '인사 상태',
        field: 'employmentStatus',
        width: 120,
        cellStyle: {
          paddingLeft: '16px',
        } as const,
        cellRenderer: React.memo((params: any) => {
          return (
            <UILabel variant='badge' intent={params.value == '재직' ? 'complete' : 'error'}>
              {params.value}
            </UILabel>
          );
        }),
      },
      {
        headerName: '마지막 접속일시',
        field: 'lastLoginAt',
        width: 180,
        cellStyle: {
          paddingLeft: '16px',
        } as const,
        cellRenderer: React.memo((params: any) => {
          if (!params.value) return '-';
          try {
            return dateUtils.formatDateWithPattern(params.value, 'yyyy.MM.dd HH:mm:ss', false);
          } catch {
            return params.value;
          }
        }),
      },
    ],
    []
  );
  // UIGrid 로우 데이터
  return (
    <>
      <section className='section-popup-content'>
        {/* 레이어 팝업 헤더 */}
        <UIPopupHeader title='구성원 선택' description='함께할 구성원을 선택할 수 있습니다. 선택하지 않는 경우 프로젝트는 본인 단독으로 생성됩니다.' position='right' />

        {/* 레이어 팝업 바디 */}
        <UIPopupBody>
          <UIArticle>
            <div className='article-header'>
              <UITypography variant='title-4' className='secondary-neutral-900 text-sb'>
                요청자 정보
              </UITypography>
            </div>
            <div className='article-body'>
              <div className='border-t border-black'>
                <table className='tbl-v'>
                  <colgroup>
                    <col style={{ width: '10%' }} />
                    <col style={{ width: '40%' }} />
                    <col style={{ width: '10%' }} />
                    <col style={{ width: '40%' }} />
                  </colgroup>
                  <tbody>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          이름
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {projectUserData?.jkwNm || ''}
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          부서
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {projectUserData?.deptNm || ''}
                        </UITypography>
                      </td>
                    </tr>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          프로젝트명
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {baseInfo?.name || ''}
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          역할
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          프로젝트 관리자
                        </UITypography>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </UIArticle>

          <UIArticle className='article-grid'>
            <UIListContainer>
              <UIListContentBox.Header>
                <div className='w-full'>
                  <UIUnitGroup gap={16} direction='column'>
                    <div className='flex justify-between w-full items-center'>
                      <div style={{ width: '168px', paddingRight: '8px', display: 'flex', alignItems: 'center' }}>
                        <UIDataCnt count={gridRows.length} prefix='총' unit='건' />
                      </div>
                      <div className='flex items-center gap-2'>
                        <div style={{ width: '160px', flexShrink: 0 }}>
                          <UIDropdown
                            value={String(value)}
                            options={[
                              { value: 'name', label: '이름' },
                              { value: 'deaprtment', label: '부서' },
                            ]}
                            onSelect={(value: string) => {
                              setValue(value);
                            }}
                            onClick={() => console.log('드롭다운 클릭')}
                            height={40}
                            variant='dataGroup'
                          />
                        </div>
                        <div style={{ width: '360px' }}>
                          <UIInput.Search
                            value={searchText}
                            placeholder='검색어 입력'
                            onChange={e => setSearchText(e.target.value)}
                            onKeyDown={e => {
                              if (e.key === 'Enter') {
                                const nextCondition = mapFilterType(value);
                                const nextKeyword = searchText;
                                setSearchParams(prev => ({
                                  ...prev,
                                  page: 1,
                                  keyword: nextKeyword,
                                  condition: nextCondition,
                                }));
                                setTimeout(() => {
                                  try {
                                    refetch();
                                  } catch (err) {
                                    // console.warn('refetch error', err);
                                  }
                                }, 0);
                              }
                            }}
                          />
                        </div>
                      </div>
                    </div>
                  </UIUnitGroup>
                </div>
              </UIListContentBox.Header>
              <UIListContentBox.Body>
                <UIGrid
                  key={`member-grid-${searchParams.page}`}
                  type='multi-select'
                  rowData={(pagedRows as any) || []}
                  columnDefs={columnDefs}
                  selectedDataList={(selectedRowsInCurrentPage as any) || []}
                  /* onClickRow={(params: any) => {
                    console.log('구성원 선택 onClickRow', params);
                  }} */
                  onCheck={handleCheck}
                  // 타입 단언을 사용하여 오류 우회
                  {...({ getRowId: (row: any) => (row && row.no !== undefined && row.no !== null ? row.no : row.id) } as any)}
                />
              </UIListContentBox.Body>
              <UIListContentBox.Footer>
                <UIPagination
                  currentPage={searchParams.page}
                  totalPages={totalPages}
                  onPageChange={page => {
                    setSearchParams(prev => ({ ...prev, page }));
                  }}
                  className='flex justify-center'
                />
              </UIListContentBox.Footer>
            </UIListContainer>
          </UIArticle>
        </UIPopupBody>

        {/* 레이어 팝업 footer */}
        <UIPopupFooter>
          <UIArticle>
            <UIUnitGroup gap={8} align='start'>
              <UIButton2 className='btn-secondary-gray' onClick={onPreviousStep}>
                이전
              </UIButton2>
            </UIUnitGroup>
          </UIArticle>
        </UIPopupFooter>
      </section>
    </>
  );
};
