import type { ChangeEvent, KeyboardEvent } from 'react';
import React, { useCallback, useEffect, useMemo, useState } from 'react';

import { UIButton2, UIDataCnt, UILabel, UIPagination, UITextLabel } from '@/components/UI/atoms';
import {
  UIArticle,
  UIDropdown,
  UIInput,
  UIPopupBody,
  UIPopupFooter,
  UIPopupHeader,
  UIStepper,
  type UIStepperItem,
  UIUnitGroup
} from '@/components/UI/molecules';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { useGetAvailableUsersForProject } from '@/services/admin/projMgmt/projMgmt.services';
import type { GetAvailableUsersRequest, ProjectUserType } from '@/services/admin/projMgmt/projMgmt.types';
import { useModal } from '@/stores/common/modal';

type ProjUserInviteStep1UserPickPopup = {
  isOpen: boolean;
  onClose: () => void;
  // eslint-disable-next-line no-unused-vars
  onNext: (users: ProjectUserType[], searchForm: { filterType: string; keyword: string }) => void;
  projectId: string;
  initialSelectedUsers?: ProjectUserType[];
  initialSearchForm?: { filterType: string; keyword: string };
};

const ACCOUNT_STATUS_LABEL: Record<string, { label: string; intent: 'blue' | 'gray' }> = {
  ACTIVE: { label: '활성화', intent: 'blue' },
  DORMANT: { label: '비활성화', intent: 'gray' },
};

const STATUS_BADGE_MAP: Record<string, { label: string; intent: 'complete' | 'error' | 'neutral' }> = {
  '0': { label: '재직', intent: 'complete' },
  '1': { label: '퇴사', intent: 'error' },
};

// 스테퍼 데이터
const stepperItems: UIStepperItem[] = [
  {
    id: 'step1',
    label: '사용자 선택',
    step: 1,
  },
  {
    id: 'step2',
    label: '역할 할당',
    step: 2,
  },
];

/**
 * 프로젝트 관리 > 프로젝트 상세 > 구성원 정보(TAB) > 구성원 초대하기 팝업
 */
export const ProjUserInviteStep1Popup: React.FC<ProjUserInviteStep1UserPickPopup> = ({ isOpen, onClose, onNext, projectId, initialSelectedUsers, initialSearchForm }) => {
  const { openConfirm } = useModal();
  // 페이지 간 선택 상태를 유지하기 위한 사용자 맵 (key -> 서버 원본 사용자 데이터)
  const [selectedUserMap, setSelectedUserMap] = useState<Record<string, ProjectUserType>>({});

  // 선택된 사용자 목록 (선택 순서와 무관하게 값 배열 활용)
  const selectedUsers = useMemo(() => Object.values(selectedUserMap), [selectedUserMap]);

  const resolveUserKey = useCallback((user: { uuid?: string; memberId?: string }) => user?.uuid || user?.memberId, []);

  useEffect(() => {
    if (!isOpen) {
      setSelectedUserMap({});
      return;
    }

    // 검색 폼 초기값 설정 (Step2에서 돌아온 경우 이전 값 유지)
    if (initialSearchForm) {
      setSearchForm(initialSearchForm);
    }

    const initialMap: Record<string, ProjectUserType> = {};
    (initialSelectedUsers ?? []).forEach(user => {
      const key = resolveUserKey(user);
      if (key) {
        initialMap[key] = user;
      }
    });

    setSelectedUserMap(initialMap);
  }, [isOpen, initialSelectedUsers, initialSearchForm, resolveUserKey]);

  // 검색 폼 상태
  const [searchForm, setSearchForm] = useState({
    filterType: 'jkwNm',
    keyword: '',
  });

  // 드롭다운 열림 상태
  const [isFilterTypeOpen, setIsFilterTypeOpen] = useState(false);

  // API 파라미터 상태
  const [searchParams, setSearchParams] = useState<GetAvailableUsersRequest & { page: number; size: number }>({
    page: 1,
    size: 12,
    filterType: 'jkwNm',
    keyword: undefined,
  });

  // ================================
  // API 호출
  // ================================

  const { data, isLoading, refetch } = useGetAvailableUsersForProject(projectId, searchParams);

  // 팝업이 열릴 때마다 페이지를 1로 초기화하고 데이터를 새로 가져와 정합성 보장
  useEffect(() => {
    if (isOpen) {
      setSearchParams(prev => ({ ...prev, page: 1 }));
      refetch();
    }
  }, [isOpen, refetch]);

  // ================================
  // 이벤트 핸들러
  // ================================

  const handleClose = useCallback(async () => {
    const confirmed = await openConfirm({
      bodyType: 'text',
      title: '안내',
      message: `화면을 나가시겠어요?
                입력한 정보가 저장되지 않을 수 있습니다.`,
      cancelText: '아니요',
      confirmText: '예',
    });

    if (confirmed) {
      onClose();
    }
  }, [openConfirm, onClose]);

  const handleNext = () => {
    if (selectedUsers.length === 0) {
      return;
    }

    onNext(selectedUsers, searchForm);
  };

  // 검색 처리 함수
  const handleSearch = useCallback(() => {
    const params = {
      page: 1,
      size: searchParams.size,
      filterType: searchForm.filterType as GetAvailableUsersRequest['filterType'],
      keyword: searchForm.keyword?.trim() || undefined,
    };

    setSearchParams(params);
  }, [searchForm.filterType, searchForm.keyword, searchParams.size]);

  const handleKeywordChange = useCallback((event: ChangeEvent<HTMLInputElement>) => {
    const { value } = event.target;
    setSearchForm(prev => ({ ...prev, keyword: value }));
  }, []);

  const handleKeywordKeyDown = useCallback(
    (event: KeyboardEvent<HTMLInputElement>) => {
      if (event.key === 'Enter') {
        handleSearch();
      }
    },
    [handleSearch]
  );

  // 표에서 사용할 사용자 목록을 API 응답에서 가공해 생성 (번호만 추가)
  const gridData = useMemo<Array<ProjectUserType & { no: number }>>(() => {
    const content = data?.content ?? [];
    const page = searchParams.page ?? 1;
    const size = searchParams.size ?? content.length;
    const offset = size > 0 ? (page - 1) * size : 0;

    return content.map((user, index) => ({
      ...user,
      no: offset + index + 1,
    }));
  }, [data?.content, searchParams.page, searchParams.size]);

  // 현재 페이지에서 선택 상태가 변경될 때 전체 선택 맵 업데이트
  const handleGridSelectionChange = useCallback(
    (currentSelectedRows: Array<ProjectUserType & { no: number }>) => {
      setSelectedUserMap(prev => {
        const next = { ...prev };
        const currentPageKeys = new Set(gridData.map(user => resolveUserKey(user)));

        // 현재 페이지에서 체크 해제된 사용자는 제거
        currentPageKeys.forEach(key => {
          if (!key) {
            return;
          }

          if (!currentSelectedRows.some(user => resolveUserKey(user) === key)) {
            delete next[key];
          }
        });

        // 현재 페이지에서 새로 선택된 사용자 추가/갱신
        currentSelectedRows.forEach(user => {
          const key = resolveUserKey(user);
          if (!key) {
            return;
          }

          // no 필드 제거 - 서버 원본 데이터만 저장 (페이지네이션 후에도 비교가 정확하도록)
          const { no, ...serverData } = user;
          next[key] = serverData as ProjectUserType;
        });

        return next;
      });
    },
    [gridData, resolveUserKey]
  );

  // 그리드 컬럼 정의
  const columnDefs: any = useMemo(
    () => [
      {
        field: 'no',
        headerName: 'NO',
        width: 56,
        minWidth: 56,
        maxWidth: 56,
        cellClass: 'text-center',
        headerClass: 'text-center',
        cellStyle: {
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          textAlign: 'center',
        },
        sortable: false,
        suppressHeaderMenuButton: true,
        suppressSizeToFit: true,
      },
      {
        field: 'dmcStatus',
        headerName: '계정 상태',
        width: 120,
        cellRenderer: React.memo((params: { value: string }) => {
          const status = ACCOUNT_STATUS_LABEL[params.value] || {
            label: params.value || '-',
            intent: 'gray' as const,
          };
          return <UITextLabel intent={status.intent}>{status.label}</UITextLabel>;
        }),
      },
      {
        field: 'jkwNm',
        headerName: '이름',
        flex: 1,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        field: 'deptNm',
        headerName: '부서',
        width: 366,
      },
      {
        field: 'retrJkwYn',
        headerName: '인사 상태',
        width: 120,
        cellRenderer: React.memo((params: { value: string }) => {
          const status = STATUS_BADGE_MAP[params.value] ?? { label: '-', intent: 'neutral' as const };
          return (
            <UILabel variant='badge' intent={status.intent}>
              {status.label}
            </UILabel>
          );
        }),
      },
      {
        field: 'lstLoginAt',
        headerName: '마지막 접속 일시',
        width: 180,
        cellStyle: { paddingLeft: '16px' },
      },
    ],
    []
  );

  // 현재 페이지에 표시된 사용자 중 선택된 사용자 목록 (페이지네이션 후에도 체크박스 유지)
  const currentPageSelectedUsers = useMemo(() => {
    return gridData.filter(user => {
      const key = resolveUserKey(user);
      return key && selectedUserMap[key];
    });
  }, [gridData, selectedUserMap, resolveUserKey]);

  // API 응답 데이터 처리
  const totalCount = data?.totalElements || 0;
  const totalPages = data?.totalPages || 1;

  return (
    <>
      <UILayerPopup
        isOpen={isOpen}
        onClose={handleClose}
        size='fullscreen'
        showOverlay={true}
        leftContent={
          /* 좌측 Step 영역 콘텐츠 */
          <UIPopupAside>
            {/* 레이어 팝업 헤더 */}
            <UIPopupHeader title='구성원 초대하기' position='left' />

            {/* 레이어 팝업 바디 */}
            <UIPopupBody>
              <UIArticle>
                <UIStepper currentStep={1} items={stepperItems} direction='vertical' />
              </UIArticle>
            </UIPopupBody>

            {/* 레이어 팝업 footer */}
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <UIButton2 className='btn-tertiary-gray' style={{ width: '80px' }} onClick={handleClose}>
                    취소
                  </UIButton2>
                  <UIButton2 className='btn-tertiary-blue' style={{ width: '80px' }} disabled>
                    완료
                  </UIButton2>
                </UIUnitGroup>
              </UIArticle>
            </UIPopupFooter>
          </UIPopupAside>
        }
      >
        <section className='section-popup-content'>
          {/* 레이어 팝업 헤더 */}
          <UIPopupHeader title='사용자 선택' description='프로젝트에 초대할 구성원을 선택해주세요.' position='right' />

          {/* 레이어 팝업 바디 */}
          <UIPopupBody>
            <UIArticle className='article-grid'>
              <UIListContainer>
                <UIListContentBox.Header>
                  <div className='flex items-center'>
                    <div style={{ width: '182px', paddingRight: '8px' }}>
                      <UIDataCnt count={totalCount} prefix='총' />
                    </div>
                  </div>

                  <div style={{ display: 'flex', marginLeft: 'auto', gap: '0 12px' }}>
                    <div style={{ width: '180px', flexShrink: 0 }}>
                      <UIDropdown
                        value={searchForm.filterType}
                        options={[
                          { value: 'jkwNm', label: '이름' },
                          { value: 'deptNm', label: '부서' },
                        ]}
                        isOpen={isFilterTypeOpen}
                        onClick={() => setIsFilterTypeOpen(prev => !prev)}
                        onSelect={(value: string) => {
                          setSearchForm(prev => ({ ...prev, filterType: value as 'jkwNm' | 'deptNm' }));
                          setIsFilterTypeOpen(false);
                        }}
                        height={40}
                        variant='dataGroup'
                      />
                    </div>

                    {/* 검색어 입력 */}
                    <div style={{ width: '360px', flexShrink: 0 }}>
                      <UIInput.Search
                        style={{
                          width: '100%',
                          boxSizing: 'border-box',
                        }}
                        value={searchForm.keyword}
                        placeholder='검색어 입력'
                        onChange={handleKeywordChange}
                        onKeyDown={handleKeywordKeyDown}
                      />
                    </div>
                  </div>
                </UIListContentBox.Header>

                <UIListContentBox.Body>
                  <UIGrid
                    type='multi-select'
                    rowData={gridData}
                    columnDefs={columnDefs}
                    domLayout={isLoading ? 'normal' : 'autoHeight'}
                    selectedDataList={currentPageSelectedUsers}
                    onCheck={handleGridSelectionChange}
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
              <UIUnitGroup gap={8} direction='row' align='start'>
                <UIButton2 className='btn-secondary-blue w-[80px]' onClick={handleNext} disabled={selectedUsers.length === 0}>
                  다음
                </UIButton2>
              </UIUnitGroup>
            </UIArticle>
          </UIPopupFooter>
        </section>
      </UILayerPopup>
    </>
  );
};
