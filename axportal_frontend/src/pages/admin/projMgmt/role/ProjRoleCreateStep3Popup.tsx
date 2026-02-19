import React, { useCallback, useEffect, useMemo, useState } from 'react';

import { UIButton2, UICheckbox2, UIDataCnt, UITypography } from '@/components/UI/atoms';
import { UIPagination } from '@/components/UI/atoms/UIPagination';
import { UIGroup, UIInput, UIPopupBody, UIPopupFooter, UIPopupHeader, UIStepper, UIUnitGroup } from '@/components/UI/molecules';
import { UIDropdown } from '@/components/UI/molecules/dropdown/UIDropdown/component';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { useGetMenuPermits, useGetPermitDetails } from '@/services/admin/projMgmt/projMgmt.services';
import type { CreateProjectRoleRequest, GetPermitDetailsRequest, PermitDetailRes } from '@/services/admin/projMgmt/projMgmt.types';
import { useModal } from '@/stores/common/modal';

const PAGE_SIZE = 12;

const searchFilterOptions = [
  { label: '전체', value: 'all' },
  { label: '권한명', value: 'authorityNm' },
  { label: '상세 권한', value: 'dtlCtnt' },
];

interface ProjRoleCreateStep3PopupProps {
  isOpen: boolean;
  onClose: () => void;
  onPrevious: (selectedDetailIds: string[]) => void;
  onComplete: (payload: CreateProjectRoleRequest) => void | Promise<void>;
  isSubmitting?: boolean;
  step1Data: {
    roleNm: string;
    dtlCtnt: string;
  };
  step2Data: {
    selectedMenuIds: string[];
  };
  initialSelectedDetailIds?: string[];
}

// 스텝퍼 아이템 정의
const stepperItems = [
  { id: 'step1', step: 1, label: '기본 정보 입력' },
  { id: 'step2', step: 2, label: '메뉴 진입 설정' },
  { id: 'step3', step: 3, label: '권한 추가하기' },
];

// ProjRoleCreateStep3Popup 컴포넌트 정의
export const ProjRoleCreateStep3Popup: React.FC<ProjRoleCreateStep3PopupProps> = ({
  isOpen,
  onClose,
  onPrevious,
  onComplete,
  isSubmitting = false,
  step1Data,
  step2Data,
  initialSelectedDetailIds,
}) => {
  const { openConfirm } = useModal();

  // ================================
  // State 관리
  // ================================

  const [isChildMenuOpen, setIsChildMenuFilterOpen] = useState(false);
  const [isSearchFilterOpen, setIsSearchFilterOpen] = useState(false);

  const [keywordInput, setKeywordInput] = useState('');
  const [selectedFilterType, setSelectedFilterType] = useState<GetPermitDetailsRequest['filterType']>('all');

  const [searchParams, setSearchParams] = useState<GetPermitDetailsRequest>({
    page: 1,
    size: PAGE_SIZE,
    authorityIds: '',
    twoDphMenu: '',
    filterType: 'all',
    keyword: '',
  });

  const [selectedDetailIds, setSelectedDetailIds] = useState<Set<string>>(new Set());

  // Step 2에서 선택된 메뉴 ID 목록 (메모이제이션)
  const selectedMenuIds = useMemo(() => step2Data.selectedMenuIds ?? [], [step2Data.selectedMenuIds]);

  // selectedMenuIds의 변경을 감지하기 위한 키 (메모이제이션)
  const selectedMenuIdsKey = useMemo(() => selectedMenuIds.join(','), [selectedMenuIds]);

  // initialSelectedDetailIds의 변경을 감지하기 위한 키 (메모이제이션)
  const initialSelectedDetailIdsKey = useMemo(() => (initialSelectedDetailIds ?? []).join(','), [initialSelectedDetailIds]);

  // 팝업 닫힐 때 선택 상태 초기화
  useEffect(() => {
    if (!isOpen) {
      setSelectedDetailIds(new Set());
    }
  }, [isOpen]);

  // 팝업 열림 또는 selectedMenuIds 변경 시 상태 초기화 및 검색 파라미터 설정
  useEffect(() => {
    if (!isOpen) {
      return;
    }

    setSearchParams({
      page: 1,
      size: PAGE_SIZE,
      authorityIds: selectedMenuIds.join(','), // Step 2에서 선택된 메뉴 ID를 콤마로 구분된 문자열로 변환하여 사용
      twoDphMenu: '',
      filterType: 'all',
      keyword: '',
    });
    // 이전에 선택한 권한 ID가 있으면 복원, 없으면 빈 Set으로 초기화
    setSelectedDetailIds(new Set(initialSelectedDetailIds ?? []));
    setKeywordInput('');
    setIsChildMenuFilterOpen(false);
    setIsSearchFilterOpen(false);
    setSelectedFilterType('all');
  }, [isOpen, selectedMenuIdsKey, initialSelectedDetailIdsKey]);

  // 검색 파라미터의 keyword 변경 시 keywordInput 동기화
  useEffect(() => {
    setKeywordInput(searchParams.keyword || '');
  }, [searchParams.keyword]);

  useEffect(() => {
    if (isOpen) {
      setSelectedFilterType(searchParams.filterType || 'all');
    }
  }, [isOpen, searchParams.filterType]);

  // 역할 생성 핸들러
  const isCreateDisabled = isSubmitting || selectedDetailIds.size === 0;

  const handleCreate = async () => {
    if (isCreateDisabled) {
      return;
    }

    const authorityIds = Array.from(selectedDetailIds);

    const payload: CreateProjectRoleRequest = {
      roleNm: step1Data?.roleNm ?? '',
      dtlCtnt: step1Data?.dtlCtnt ?? '',
      authorityIds,
    };

    await onComplete(payload);
  };

  // 검색 파라미터 업데이트 핸들러
  const handleSearch = (updates: Partial<GetPermitDetailsRequest>) => {
    setSearchParams(prev => ({
      ...prev,
      ...updates,
      page: 1, // 검색 시 페이지 1로 초기화
      filterType: selectedFilterType,
    }));
  };

  // 페이지 변경 핸들러
  const handlePageChange = (page: number) => {
    setSearchParams(prev => ({ ...prev, page }));
  };

  // 하위 메뉴 필터 선택 핸들러
  const handleMenuFilterSelect = (value: string) => {
    handleSearch({ twoDphMenu: value === '전체' ? '' : value });
  };

  // 검색어 입력 후 검색 버튼 클릭 또는 Enter 키 입력 시 핸들러
  const handleKeywordSearch = () => {
    handleSearch({ keyword: keywordInput.trim() });
  };

  // Step 2에서 선택된 메뉴(권한)가 있는지 여부
  const hasAuthoritySelection = (searchParams.authorityIds?.length ?? 0) > 0;
  // 현재 페이지 및 페이지 크기
  const currentPage = searchParams.page ?? 1;

  // ================================
  // API 호출
  // ================================

  // 권한 상세 목록 API 호출 (페이지네이션 및 필터링 적용)
  // Step 2에서 선택된 메뉴 ID에 해당하는 권한 상세 정보를 가져옴
  const { data: permitDetailData } = useGetPermitDetails(searchParams, {
    enabled: isOpen && hasAuthoritySelection, // 팝업이 열려있고 선택된 메뉴가 있을 때만 호출
  });

  // 전체 권한 데이터 조회 (조회 권한 자동 선택용)
  // 페이지네이션 없이 모든 권한을 가져와서 "조회" 권한을 자동 선택함
  const { data: allPermitData } = useGetPermitDetails(
    {
      authorityIds: selectedMenuIds.join(','),
      size: 9999,
    },
    { enabled: isOpen && hasAuthoritySelection }
  );

  // 전체 메뉴 목록 API 호출 (하위 메뉴 드롭다운 채우기용)
  // 모든 메뉴 정보를 가져와서 Step 2에서 선택된 메뉴에 해당하는 하위 메뉴 필터 옵션을 구성
  const { data: allMenuData } = useGetMenuPermits({ size: 9999 }, { enabled: isOpen && hasAuthoritySelection });

  // 그리드에 표시할 데이터 (메모이제이션)
  const rowData = useMemo(() => permitDetailData?.content ?? [], [permitDetailData]);
  const hasViewKeyword = useCallback((authorityNm?: string) => authorityNm?.includes('조회') ?? false, []);
  const selectableIds = useMemo(() => rowData.filter(row => !hasViewKeyword(row.authorityNm)).map(row => row.authorityId), [rowData, hasViewKeyword]);

  // 총 항목 수 및 총 페이지 수 (메모이제이션)
  const totalCount = permitDetailData?.totalElements ?? 0;
  const actualTotalPages = permitDetailData?.totalPages;
  const totalPages = actualTotalPages ?? 1;

  // 전체 권한 데이터 기반으로 "조회" 권한 자동 선택
  // allPermitData를 사용하여 모든 페이지의 "조회" 권한을 선택함
  useEffect(() => {
    if (!isOpen || !hasAuthoritySelection) {
      setSelectedDetailIds(new Set());
      return;
    }

    if (!allPermitData?.content) {
      return;
    }

    setSelectedDetailIds(prevSelected => {
      const nextSelected = new Set(prevSelected);
      // 전체 데이터에서 "조회" 권한 모두 선택
      allPermitData.content.forEach(detail => {
        if (hasViewKeyword(detail.authorityNm)) {
          nextSelected.add(detail.authorityId);
        }
      });
      return nextSelected;
    });
  }, [isOpen, hasAuthoritySelection, allPermitData, hasViewKeyword]);

  // 현재 페이지가 총 페이지 수를 초과할 경우 페이지를 조정
  useEffect(() => {
    if (!isOpen || !hasAuthoritySelection || actualTotalPages === undefined) {
      return;
    }

    const safeTotalPages = Math.max(actualTotalPages, 1);
    if (currentPage > safeTotalPages) {
      setSearchParams(prev => ({ ...prev, page: safeTotalPages }));
    }
  }, [isOpen, hasAuthoritySelection, actualTotalPages, currentPage]);

  // 하위 메뉴 필터 드롭다운 옵션 생성 (메모이제이션)
  // allMenuData에서 Step 2에서 선택된 메뉴 ID에 해당하는 하위 메뉴명만 추출
  const menuOptions = useMemo(() => {
    const selectedIds = new Set(selectedMenuIds);
    const menuSet = new Set<string>();

    allMenuData?.content?.forEach(menu => {
      if (selectedIds.has(menu.authorityId) && menu.twoDphMenu) {
        menuSet.add(menu.twoDphMenu);
      }
    });

    return ['전체', ...Array.from(menuSet)];
  }, [allMenuData?.content, selectedMenuIds]);

  // 검색 파라미터의 twoDphMenu가 유효하지 않을 경우 초기화
  useEffect(() => {
    if (!isOpen || !hasAuthoritySelection) {
      return;
    }

    if (searchParams.twoDphMenu && !menuOptions.includes(searchParams.twoDphMenu)) {
      setSearchParams(prev => ({ ...prev, twoDphMenu: '' }));
    }
  }, [menuOptions, searchParams.twoDphMenu, isOpen, hasAuthoritySelection]);

  const handleRowToggle = useCallback(
    (row: PermitDetailRes, checked: boolean) => {
      const isMandatory = hasViewKeyword(row.authorityNm);

      setSelectedDetailIds(prev => {
        const next = new Set(prev);

        if (isMandatory) {
          next.add(row.authorityId);
          return next;
        }

        if (checked) {
          next.add(row.authorityId);
        } else {
          next.delete(row.authorityId);
        }

        return next;
      });
    },
    [hasViewKeyword]
  );

  const handleToggleAll = useCallback(
    (checked: boolean) => {
      setSelectedDetailIds(prev => {
        const next = new Set(prev);

        rowData.forEach(row => {
          const isMandatory = hasViewKeyword(row.authorityNm);
          if (isMandatory) {
            next.add(row.authorityId);
            return;
          }

          if (checked) {
            next.add(row.authorityId);
          } else {
            next.delete(row.authorityId);
          }
        });

        return next;
      });
    },
    [rowData, hasViewKeyword]
  );

  const selectedSelectableCount = useMemo(() => selectableIds.filter(id => selectedDetailIds.has(id)).length, [selectableIds, selectedDetailIds]);
  const allSelectableSelected = useMemo(() => selectableIds.length > 0 && selectedSelectableCount === selectableIds.length, [selectableIds, selectedSelectableCount]);
  const partiallySelectableSelected = useMemo(() => selectedSelectableCount > 0 && selectedSelectableCount < selectableIds.length, [selectableIds.length, selectedSelectableCount]);

  // UIGrid 컬럼 정의 (메모이제이션)
  const columnDefs: any = useMemo(
    () => [
      {
        headerName: '',
        field: 'selection',
        width: 56,
        minWidth: 56,
        maxWidth: 56,
        pinned: 'left',
        cellStyle: {
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
        } as any,
        sortable: false,
        suppressHeaderMenuButton: true,
        suppressSizeToFit: true,
        headerComponent: () => (
          <UICheckbox2
            className='box'
            checked={allSelectableSelected}
            aria-checked={partiallySelectableSelected ? 'mixed' : allSelectableSelected ? 'true' : 'false'}
            disabled={selectableIds.length === 0}
            onChange={checked => handleToggleAll(checked)}
          />
        ),
        cellRenderer: (params: { data?: PermitDetailRes }) => {
          const row = params.data;
          if (!row) {
            return null;
          }

          const isMandatory = hasViewKeyword(row.authorityNm);
          const checked = selectedDetailIds.has(row.authorityId);

          return <UICheckbox2 className='box' checked={checked} disabled={isMandatory} onChange={nextChecked => handleRowToggle(row, nextChecked)} />;
        },
      },
      {
        headerName: 'NO',
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
        valueGetter: (params: { node?: { rowIndex?: number } }) => {
          const page = searchParams.page || 1;
          const size = searchParams.size || PAGE_SIZE;
          return (page - 1) * size + (params.node?.rowIndex || 0) + 1;
        },
      },
      {
        headerName: '하위 메뉴명',
        field: 'twoDphMenu' as any,
        width: 272,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: '권한명',
        field: 'authorityNm' as any,
        width: 272,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: '상세 권한',
        field: 'dtlCtnt' as any,
        flex: 1,
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
    ],
    [allSelectableSelected, handleRowToggle, handleToggleAll, partiallySelectableSelected, searchParams.page, searchParams.size, selectableIds.length, selectedDetailIds]
  );

  // 팝업 취소 핸들러
  const handleCancel = async () => {
    const confirmed = await openConfirm({
      bodyType: 'text',
      title: '안내',
      message: `화면을 나가시겠습니까?
                입력한 정보가 저장되지 않을 수 있습니다.`,
      cancelText: '아니요',
      confirmText: '예',
    });

    if (confirmed) {
      onClose(); // 확인 시 팝업 닫기
    }
  };

  // 팝업 UI 렌더링
  return (
    <UILayerPopup
      isOpen={isOpen}
      onClose={handleCancel}
      size='fullscreen'
      showOverlay={true}
      leftContent={
        <UIPopupAside>
          <UIPopupHeader title='새 역할 만들기' position='left' />
          <UIPopupBody>
            <UIStepper items={stepperItems} currentStep={3} direction='vertical' />
          </UIPopupBody>
          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <UIButton2 className='btn-tertiary-gray' style={{ width: '80px' }} onClick={handleCancel}>
                  취소
                </UIButton2>
                <UIButton2 className='btn-tertiary-blue' style={{ width: '80px' }} onClick={handleCreate} disabled={isCreateDisabled}>
                  만들기
                </UIButton2>
              </UIUnitGroup>
            </UIArticle>
          </UIPopupFooter>
        </UIPopupAside>
      }
    >
      <section className='section-popup-content'>
        <UIPopupHeader title='권한 추가하기' description='원하는 권한을 선택 후 만들기 버튼을 눌러주세요.' position='right' />

        <UIPopupBody>
          <UIArticle className='article-grid'>
            <UIListContainer>
              <UIListContentBox.Header>
                <UIUnitGroup gap={16} direction='column'>
                  <div className='flex justify-between w-full items-center'>
                    <div className='w-full'>
                      <UIGroup gap={12} direction='row' align='start'>
                        <div style={{ width: '102px', display: 'flex', alignItems: 'center' }}>
                          <UIDataCnt count={totalCount} prefix='총' unit='건' />
                        </div>
                        <div style={{ display: 'flex', gap: '0 12px' }}>
                          <div className='flex items-center gap-2'>
                            <UITypography variant='body-1' className='secondary-neutral-900'>
                              하위 메뉴명
                            </UITypography>
                            <div className='w-[180px]'>
                              <UIDropdown
                                value={searchParams.twoDphMenu || '전체'}
                                options={menuOptions.map(option => ({ value: option, label: option }))}
                                isOpen={isChildMenuOpen}
                                onClick={() => setIsChildMenuFilterOpen(!isChildMenuOpen)}
                                onSelect={(value: string) => {
                                  handleMenuFilterSelect(value);
                                  setIsChildMenuFilterOpen(false);
                                }}
                                height={40}
                              />
                            </div>
                          </div>
                        </div>

                        <div style={{ display: 'flex', marginLeft: 'auto', gap: '0 12px' }}>
                          <div className='w-[180px]'>
                            <UIDropdown
                              value={selectedFilterType as string}
                              options={searchFilterOptions}
                              isOpen={isSearchFilterOpen}
                              onSelect={(value: string) => {
                                setSelectedFilterType(value as GetPermitDetailsRequest['filterType']);
                                setIsSearchFilterOpen(false);
                              }}
                              onClick={() => setIsSearchFilterOpen(!isSearchFilterOpen)}
                              height={40}
                              variant='dataGroup'
                            />
                          </div>
                          <div className='w-[360px]'>
                            <UIInput.Search
                              value={keywordInput}
                              onChange={e => {
                                setKeywordInput(e.target.value);
                              }}
                              placeholder='검색어 입력'
                              onKeyDown={e => {
                                if (e.key === 'Enter') {
                                  handleKeywordSearch();
                                }
                              }}
                            />
                          </div>
                        </div>
                      </UIGroup>
                    </div>
                  </div>
                </UIUnitGroup>
              </UIListContentBox.Header>
              <UIListContentBox.Body>
                <div className='w-full grid-type-check'>
                  <UIGrid type='default' rowData={rowData} columnDefs={columnDefs} />
                </div>
              </UIListContentBox.Body>
              <UIListContentBox.Footer>
                <UIPagination currentPage={currentPage} totalPages={totalPages} onPageChange={handlePageChange} className='flex justify-center' />
              </UIListContentBox.Footer>
            </UIListContainer>
          </UIArticle>
        </UIPopupBody>
        <UIPopupFooter>
          <UIArticle>
            <UIUnitGroup gap={8} direction='row' align='start'>
              <UIButton2 className='btn-secondary-gray' onClick={() => onPrevious(Array.from(selectedDetailIds))}>
                이전
              </UIButton2>
            </UIUnitGroup>
          </UIArticle>
        </UIPopupFooter>
      </section>
    </UILayerPopup>
  );
};
