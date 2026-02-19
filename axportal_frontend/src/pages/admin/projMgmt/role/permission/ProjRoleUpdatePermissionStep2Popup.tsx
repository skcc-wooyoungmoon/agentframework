import React, { useCallback, useEffect, useMemo, useState } from 'react';

import { UIButton2, UICheckbox2, UIDataCnt, UITypography } from '@/components/UI/atoms';
import { UIPagination } from '@/components/UI/atoms/UIPagination';
import { UIGroup, UIInput, UIPopupBody, UIPopupFooter, UIPopupHeader, UIStepper, UIUnitGroup } from '@/components/UI/molecules';
import { UIDropdown } from '@/components/UI/molecules/dropdown/UIDropdown/component';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { useGetMenuPermits, useGetPermitDetails, useUpdateProjectRoleAuthorities } from '@/services/admin/projMgmt/projMgmt.services';
import type { GetPermitDetailsRequest, PermitDetailRes } from '@/services/admin/projMgmt/projMgmt.types';
import { useModal } from '@/stores/common/modal';

const PAGE_SIZE = 12;

const searchFilterOptions = [
  { label: '전체', value: 'all' },
  { label: '권한명', value: 'authorityNm' },
  { label: '상세 권한', value: 'dtlCtnt' },
];

interface ProjRoleUpdatePermissionStep2PopupProps {
  isOpen: boolean;
  onClose: () => void;
  onPrevious: (selectedAuthorityIds: string[]) => void;
  onSave: () => void | Promise<void>;
  isSubmitting?: boolean;
  selectedMenuIds: string[];
  initialSelectedAuthorityIds: string[];
  projectId: string;
  roleId: string;
}

const stepperItems = [
  { id: 'step1', step: 1, label: '메뉴 진입 설정' },
  { id: 'step2', step: 2, label: '권한 추가하기' },
];

export const ProjRoleUpdatePermissionStep2Popup: React.FC<ProjRoleUpdatePermissionStep2PopupProps> = ({
  isOpen,
  onClose,
  onPrevious,
  onSave,
  isSubmitting = false,
  selectedMenuIds,
  initialSelectedAuthorityIds,
  projectId,
  roleId,
}) => {
  const { openAlert, openConfirm } = useModal();

  const { mutateAsync: updateAuthorities, isPending: isUpdatingAuthorities } = useUpdateProjectRoleAuthorities(projectId, roleId);

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

  const selectedMenuIdsKey = useMemo(() => selectedMenuIds.join(','), [selectedMenuIds]);

  // initialSelectedAuthorityIds의 변경을 감지하기 위한 키 (메모이제이션)
  const initialSelectedAuthorityIdsKey = useMemo(() => initialSelectedAuthorityIds.join(','), [initialSelectedAuthorityIds]);

  useEffect(() => {
    if (isOpen) {
      setSearchParams({
        page: 1,
        size: PAGE_SIZE,
        authorityIds: selectedMenuIds.join(','),
        twoDphMenu: '',
        filterType: 'all',
        keyword: '',
      });
      setSelectedDetailIds(new Set(initialSelectedAuthorityIds));
      setKeywordInput('');
      setIsChildMenuFilterOpen(false);
      setIsSearchFilterOpen(false);
      setSelectedFilterType('all');
    }
  }, [isOpen, selectedMenuIdsKey, initialSelectedAuthorityIdsKey]);

  useEffect(() => {
    setKeywordInput(searchParams.keyword || '');
  }, [searchParams.keyword]);

  useEffect(() => {
    if (isOpen) {
      setSelectedFilterType(searchParams.filterType || 'all');
    }
  }, [isOpen, searchParams.filterType]);

  const isSaveDisabled = isSubmitting || selectedDetailIds.size === 0 || isUpdatingAuthorities;

  const handleSave = async () => {
    if (isSaveDisabled) {
      return;
    }

    await updateAuthorities({ authorityIds: Array.from(new Set(selectedDetailIds)) });

    openAlert({
      title: '완료',
      message: '권한 설정이 완료되었습니다.',
      confirmText: '확인',
      onConfirm: () => {
        onSave();
      },
    });
  };

  const handleSearch = (updates: Partial<GetPermitDetailsRequest>) => {
    setSearchParams(prev => ({
      ...prev,
      ...updates,
      page: 1,
      filterType: selectedFilterType,
    }));
  };

  const handlePageChange = (page: number) => {
    setSearchParams(prev => ({ ...prev, page }));
  };

  const handleMenuFilterSelect = (value: string) => {
    handleSearch({ twoDphMenu: value === '전체' ? '' : value });
  };

  const handleKeywordSearch = () => {
    handleSearch({ keyword: keywordInput.trim() });
  };

  const hasAuthoritySelection = (searchParams.authorityIds?.length ?? 0) > 0;
  const currentPage = searchParams.page ?? 1;

  const { data: permitDetailData } = useGetPermitDetails(searchParams, {
    enabled: isOpen && hasAuthoritySelection,
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

  const { data: allMenuData } = useGetMenuPermits({ size: 9999 }, { enabled: isOpen && hasAuthoritySelection });

  const rowData = useMemo(() => permitDetailData?.content ?? [], [permitDetailData]);
  const hasViewKeyword = useCallback((authorityNm?: string) => authorityNm?.includes('조회') ?? false, []);
  const selectableIds = useMemo(() => rowData.filter(row => !hasViewKeyword(row.authorityNm)).map(row => row.authorityId), [rowData, hasViewKeyword]);

  const totalCount = permitDetailData?.totalElements ?? 0;
  const totalPages = permitDetailData?.totalPages ?? 0;

  // 전체 권한 데이터 기반으로 "조회" 권한 자동 선택
  // allPermitData를 사용하여 모든 페이지의 "조회" 권한을 선택함
  useEffect(() => {
    if (!isOpen || !hasAuthoritySelection || !allPermitData?.content) {
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

  const handleCancel = async () => {
    const confirmed = await openConfirm({
      bodyType: 'text',
      title: '안내',
      message: `화면을 나가시겠습니까?\n
                입력한 정보가 저장되지 않을 수 있습니다.`,
      cancelText: '아니요',
      confirmText: '예',
    });

    if (confirmed) {
      onClose();
    }
  };

  return (
    <UILayerPopup
      isOpen={isOpen}
      onClose={handleCancel}
      size='fullscreen'
      showOverlay={true}
      leftContent={
        <UIPopupAside>
          <UIPopupHeader title='권한 설정하기' position='left' />
          <UIPopupBody>
            <UIStepper items={stepperItems} currentStep={2} direction='vertical' />
          </UIPopupBody>
          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <UIButton2 className='btn-tertiary-gray' style={{ width: '80px' }} onClick={handleCancel}>
                  취소
                </UIButton2>
                <UIButton2 className='btn-tertiary-blue' style={{ width: '80px' }} onClick={handleSave} disabled={isSaveDisabled}>
                  저장
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
                <div className='w-full'>
                  <UIUnitGroup gap={16} direction='column'>
                    <div className='flex justify-between w-full items-center'>
                      <div className='w-full'>
                        <UIGroup gap={12} direction='row' align='start'>
                          <div style={{ width: '102px', display: 'flex', alignItems: 'center' }}>
                            <UIDataCnt count={totalCount} prefix='총' />
                          </div>
                          <div className='flex items-center gap-[12px]'>
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
                </div>
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
