import React, { useCallback, useEffect, useMemo, useState } from 'react';

import { UIButton2, UICheckbox2, UIDataCnt, UITypography } from '@/components/UI/atoms';
import { UIPagination } from '@/components/UI/atoms/UIPagination';
import { UIGroup, UIInput, UIPopupBody, UIPopupFooter, UIPopupHeader, UIStepper, UIUnitGroup } from '@/components/UI/molecules';
import { UIDropdown } from '@/components/UI/molecules/dropdown/UIDropdown/component';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import type { GetMenuPermitsRequest } from '@/services/admin/projMgmt';
import { useGetMenuPermits } from '@/services/admin/projMgmt/projMgmt.services';
import type { MenuPermitRes } from '@/services/admin/projMgmt/projMgmt.types';
import { useModal } from '@/stores/common/modal';

interface ProjRoleCreateStep2PopupProps {
  isOpen: boolean;
  onClose: () => void;
  onPrevious: () => void;
  onNext: (selectedMenuIds: string[]) => void;
  initialSelectedMenuIds: string[];
}

const PAGE_SIZE = 12;

// TODO: 임시 조치 - 추후 제거 예정
// NO 3, 4, 7, 14, 18, 19에 해당하는 메뉴를 필수 선택으로 설정 (0-based 인덱스)
const MANDATORY_MENU_INDICES = [2, 3, 6, 13, 17, 18];

const searchFilterOptions = [
  { value: 'all', label: '전체' },
  { value: 'oneDphMenu', label: '상위 메뉴명' },
  { value: 'twoDphMenu', label: '하위 메뉴명' },
];

export const ProjRoleCreateStep2Popup: React.FC<ProjRoleCreateStep2PopupProps> = ({ isOpen, onClose, onPrevious, onNext, initialSelectedMenuIds }) => {
  const { openConfirm } = useModal();

  // ================================
  // State 관리
  // ================================

  /** 메뉴 목록 조회 파라미터 */
  const [searchParams, setSearchParams] = useState<GetMenuPermitsRequest>({
    page: 1,
    oneDphMenu: '',
    twoDphMenu: '',
    filterType: 'all',
    keyword: '',
  });

  /** 상위 메뉴 드롭다운 열림 상태 */
  const [isParentMenuOpen, setIsParentMenuOpen] = useState(false);
  /** 하위 메뉴 드롭다운 열림 상태 */
  const [isChildMenuOpen, setIsChildMenuOpen] = useState(false);
  /** 검색 필터 드롭다운 열림 상태 */
  const [isSearchFilterOpen, setIsSearchFilterOpen] = useState(false);

  /** 검색 입력 필드 임시 값 */
  const [keywordInput, setKeywordInput] = useState('');
  /** 선택된 메뉴 ID 목록 */
  const [selectedMenuIds, setSelectedMenuIds] = useState(new Set<string>());
  /** 선택된 검색 필터 타입 */
  const [selectedFilterType, setSelectedFilterType] = useState<GetMenuPermitsRequest['filterType']>('all');

  // ================================
  // API 호출
  // ================================

  /** 메뉴 목록 조회 (필터링 적용) */
  const { data: menuData } = useGetMenuPermits(searchParams, { enabled: isOpen });

  /** 전체 메뉴 목록 조회 (드롭다운용) */
  const { data: allMenuData } = useGetMenuPermits({ size: 9999 }, { enabled: isOpen });

  // ================================
  // 데이터 가공
  // ================================

  /** 현재 페이지 메뉴 데이터 */
  const rowData = useMemo(() => menuData?.content || [], [menuData]);
  /** 전체 페이지 수 */
  const totalPages = useMemo(() => menuData?.totalPages || 1, [menuData]);
  /** 전체 메뉴 개수 */
  const totalElements = useMemo(() => menuData?.totalElements || 0, [menuData]);

  /** 상위 메뉴 드롭다운 옵션 */
  const parentMenuOptions = useMemo(() => {
    const parentMenus = new Set(allMenuData?.content?.map(row => row.oneDphMenu) || []);
    return [{ value: '전체', label: '전체' }, ...Array.from(parentMenus).map(menu => ({ value: menu, label: menu }))];
  }, [allMenuData]);

  /** 하위 메뉴 드롭다운 옵션 */
  const childMenuOptions = useMemo(() => {
    const childMenus = new Set(allMenuData?.content?.map(row => row.twoDphMenu) || []);
    return [{ value: '전체', label: '전체' }, ...Array.from(childMenus).map(menu => ({ value: menu, label: menu }))];
  }, [allMenuData]);

  // TODO: 임시 조치 - 추후 제거 예정
  /** 필수 선택 메뉴 ID 목록 (전체 데이터 기준 인덱스에서 추출) */
  const mandatoryMenuIds = useMemo(() => {
    if (!allMenuData?.content) return new Set<string>();
    const ids = new Set<string>();
    MANDATORY_MENU_INDICES.forEach(index => {
      const menu = allMenuData.content[index];
      if (menu?.authorityId) {
        ids.add(menu.authorityId);
      }
    });
    return ids;
  }, [allMenuData]);

  // TODO: 임시 조치 - 추후 제거 예정
  /** 해당 메뉴가 필수 선택인지 판단 */
  const isMandatoryMenu = useCallback((authorityId: string) => mandatoryMenuIds.has(authorityId), [mandatoryMenuIds]);

  // TODO: 임시 조치 - 추후 제거 예정
  /** 현재 페이지에서 선택 가능한(필수가 아닌) 메뉴 ID 목록 */
  const selectableIds = useMemo(() => rowData.filter(row => !isMandatoryMenu(row.authorityId)).map(row => row.authorityId), [rowData, isMandatoryMenu]);

  // TODO: 임시 조치 - 추후 제거 예정 (전체 선택 상태 계산)
  /** 선택된 선택 가능 메뉴 개수 */
  const selectedSelectableCount = useMemo(() => selectableIds.filter(id => selectedMenuIds.has(id)).length, [selectableIds, selectedMenuIds]);
  /** 모든 선택 가능 메뉴가 선택되었는지 */
  const allSelectableSelected = useMemo(() => selectableIds.length > 0 && selectedSelectableCount === selectableIds.length, [selectableIds, selectedSelectableCount]);
  /** 일부 선택 가능 메뉴만 선택되었는지 */
  const partiallySelectableSelected = useMemo(() => selectedSelectableCount > 0 && selectedSelectableCount < selectableIds.length, [selectableIds.length, selectedSelectableCount]);

  // ================================
  // useEffect 훅
  // ================================

  useEffect(() => {
    if (isOpen) {
      setSelectedMenuIds(new Set(initialSelectedMenuIds));
      setKeywordInput(searchParams.keyword || '');
      setSelectedFilterType(searchParams.filterType || 'all');
    } else {
      // 팝업이 닫힐 때 모든 상태 초기화
      setSearchParams({
        page: 1,
        oneDphMenu: '',
        twoDphMenu: '',
        filterType: 'all',
        keyword: '',
      });
      setKeywordInput('');
      setSelectedMenuIds(new Set());
      setSelectedFilterType('all');
    }
  }, [isOpen, initialSelectedMenuIds, searchParams.filterType]);

  useEffect(() => {
    setKeywordInput(searchParams.keyword || '');
  }, [searchParams.keyword]);

  // TODO: 임시 조치 - 추후 제거 예정
  // 팝업 열릴 때 필수 메뉴 자동 선택
  useEffect(() => {
    if (!isOpen || !allMenuData?.content) return;

    setSelectedMenuIds(prev => {
      const next = new Set(prev);
      mandatoryMenuIds.forEach(id => next.add(id));
      return next;
    });
  }, [isOpen, mandatoryMenuIds, allMenuData?.content]);

  // ================================
  // 이벤트 핸들러
  // ================================

  /** 검색 실행 */
  const handleSearch = (newParams: Partial<GetMenuPermitsRequest>) => {
    setSearchParams(prev => ({ ...prev, page: 1, ...newParams, filterType: selectedFilterType }));
  };

  /** 페이지 변경 */
  const handlePageChange = (page: number) => {
    setSearchParams(prev => ({ ...prev, page }));
  };

  /** 키워드 검색 실행 */
  const handleKeywordSearch = () => {
    handleSearch({ keyword: keywordInput });
  };

  // ================================
  // Grid 설정
  // ================================

  /** 그리드 컬럼 정의 */
  const columnDefs: any = useMemo(
    () => [
      // TODO: 임시 조치 - 추후 제거 예정 (커스텀 체크박스 컬럼)
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
        cellRenderer: (params: { data?: MenuPermitRes }) => {
          const row = params.data;
          if (!row) {
            return null;
          }
          const isMandatory = isMandatoryMenu(row.authorityId);
          const checked = selectedMenuIds.has(row.authorityId);
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
          const size = PAGE_SIZE;
          return (page - 1) * size + (params.node?.rowIndex || 0) + 1;
        },
      },
      {
        headerName: '상위 메뉴명',
        field: 'oneDphMenu',
        width: 570,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: '하위 메뉴명',
        field: 'twoDphMenu',
        flex: 1,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
    ],
    [searchParams.page, allSelectableSelected, partiallySelectableSelected, selectableIds.length, isMandatoryMenu, selectedMenuIds]
  );

  // TODO: 임시 조치 - 추후 제거 예정
  /** 개별 행 체크박스 토글 (필수 메뉴는 해제 불가) */
  const handleRowToggle = useCallback(
    (row: MenuPermitRes, checked: boolean) => {
      const isMandatory = isMandatoryMenu(row.authorityId);
      setSelectedMenuIds(prev => {
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
    [isMandatoryMenu]
  );

  // TODO: 임시 조치 - 추후 제거 예정
  /** 전체 선택/해제 토글 (필수 메뉴는 해제 불가) */
  const handleToggleAll = useCallback(
    (checked: boolean) => {
      setSelectedMenuIds(prev => {
        const next = new Set(prev);
        rowData.forEach(row => {
          const isMandatory = isMandatoryMenu(row.authorityId);
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
    [rowData, isMandatoryMenu]
  );

  /** 취소 버튼 클릭 */
  const handleCancel = async () => {
    const confirmed = await openConfirm({
      bodyType: 'text',
      title: '안내',
      message: `화면을 나가시겠습니까?\n                입력한 정보가 저장되지 않을 수 있습니다.`,
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
          <UIPopupHeader title='새 역할 만들기' position='left' />
          <UIPopupBody>
            <UIStepper
              items={[
                { id: 'step1', step: 1, label: '기본 정보 입력' },
                { id: 'step2', step: 2, label: '메뉴 진입 설정' },
                { id: 'step3', step: 3, label: '권한 추가하기' },
              ]}
              currentStep={2}
              direction='vertical'
            />
          </UIPopupBody>
          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <UIButton2 className='btn-tertiary-gray' style={{ width: '80px' }} onClick={handleCancel}>
                  취소
                </UIButton2>
                <UIButton2 className='btn-tertiary-blue' style={{ width: '80px' }} disabled>
                  만들기
                </UIButton2>
              </UIUnitGroup>
            </UIArticle>
          </UIPopupFooter>
        </UIPopupAside>
      }
    >
      <section className='section-popup-content'>
        <UIPopupHeader title='메뉴 진입 설정' description='해당 역할이 이용할 수 있는 메뉴를 선택해주세요.' position='right' />
        <UIPopupBody>
          <UIArticle className='article-grid'>
            <UIListContainer>
              <UIListContentBox.Header>
                <UIUnitGroup gap={16} direction='column'>
                  <div className='flex justify-between w-full items-center'>
                    <div className='w-full'>
                      <UIGroup gap={12} direction='row' align='start'>
                        <div style={{ width: '102px', display: 'flex', alignItems: 'center' }}>
                          <UIDataCnt count={totalElements} prefix='총' />
                        </div>
                        <div className='grid grid-cols-2'>
                          <div className='flex items-center w-[270px]'>
                            <UITypography variant='body-1' className='secondary-neutral-900 w-[115px]'>
                              상위 메뉴명
                            </UITypography>
                            <UIDropdown
                              value={searchParams.oneDphMenu || '전체'}
                              options={parentMenuOptions}
                              isOpen={isParentMenuOpen}
                              onClick={() => setIsParentMenuOpen(!isParentMenuOpen)}
                              onSelect={(value: string) => {
                                handleSearch({ oneDphMenu: value === '전체' ? '' : value });
                                setIsParentMenuOpen(false);
                              }}
                              height={40}
                              width='w-[180px]'
                            />
                          </div>
                          <div className='flex items-center w-[270px]'>
                            <UITypography variant='body-1' className='secondary-neutral-900 w-[115px]'>
                              하위 메뉴명
                            </UITypography>
                            {/* 간격 오류 수정해야함 */}
                            <UIDropdown
                              value={searchParams.twoDphMenu || '전체'}
                              options={childMenuOptions}
                              isOpen={isChildMenuOpen}
                              onClick={() => setIsChildMenuOpen(!isChildMenuOpen)}
                              onSelect={(value: string) => {
                                handleSearch({ twoDphMenu: value === '전체' ? '' : value });
                                setIsChildMenuOpen(false);
                              }}
                              height={40}
                              width='w-[180px]'
                            />
                          </div>
                        </div>

                        <div style={{ display: 'flex', marginLeft: 'auto', gap: '0 12px' }}>
                          <div style={{ width: '160px', flexShrink: 0 }}>
                            <UIDropdown
                              value={selectedFilterType as string}
                              options={searchFilterOptions}
                              isOpen={isSearchFilterOpen}
                              onSelect={(value: string) => {
                                setSelectedFilterType(value as GetMenuPermitsRequest['filterType']);
                                setIsSearchFilterOpen(false);
                              }}
                              onClick={() => setIsSearchFilterOpen(!isSearchFilterOpen)}
                              height={40}
                              variant='dataGroup'
                            />
                          </div>
                          <div style={{ width: '360px', flexShrink: 0 }}>
                            <UIInput.Search
                              value={keywordInput}
                              onChange={e => setKeywordInput(e.target.value)}
                              onKeyDown={e => {
                                if (e.key === 'Enter') {
                                  handleKeywordSearch();
                                }
                              }}
                              placeholder='검색어 입력'
                            />
                          </div>
                        </div>
                      </UIGroup>
                    </div>
                  </div>
                </UIUnitGroup>
              </UIListContentBox.Header>
              <UIListContentBox.Body>
                {/* TODO: 임시 조치 - type='default'로 변경하고 커스텀 체크박스 사용 */}
                <div className='w-full grid-type-check'>
                  <UIGrid type='default' rowData={rowData} columnDefs={columnDefs} />
                </div>
              </UIListContentBox.Body>
              <UIListContentBox.Footer>
                <UIPagination currentPage={searchParams.page ?? 1} totalPages={totalPages} onPageChange={handlePageChange} className='flex justify-center' />
              </UIListContentBox.Footer>
            </UIListContainer>
          </UIArticle>
        </UIPopupBody>
        <UIPopupFooter>
          <UIArticle>
            <UIUnitGroup gap={8} direction='row' align='start'>
              <UIButton2 className='btn-secondary-gray' style={{ width: '80px' }} onClick={onPrevious}>
                이전
              </UIButton2>
              <UIButton2 className='btn-secondary-blue' disabled={selectedMenuIds.size === 0} style={{ width: '80px' }} onClick={() => onNext(Array.from(selectedMenuIds))}>
                다음
              </UIButton2>
            </UIUnitGroup>
          </UIArticle>
        </UIPopupFooter>
      </section>
    </UILayerPopup>
  );
};
