import { useMemo, useState } from 'react';

import { UITypography } from '@/components/UI';
import { UIButton2, UIDataCnt, UIPagination } from '@/components/UI/atoms';
import {
  UIArticle,
  UIDropdown,
  UIFormField,
  UIGroup,
  UIInput,
  UIPopupBody,
  UIPopupFooter,
  UIPopupHeader,
  UIUnitGroup
} from '@/components/UI/molecules';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { type ProjectType } from '@/services/admin/projMgmt';
import {
  type RoleType,
  useGetAssignableProjectRoles,
  type UserType,
  useUpdateUserProjectRole
} from '@/services/admin/userMgmt';
import { useModal } from '@/stores/common/modal';

// ================================
// 타입 정의
// ================================

type RoleSearchValues = {
  filterType: string;
  keyword: string;
};

// ================================
// 상수 정의
// ================================

const DROPDOWN_KEYS = {
  FILTER_TYPE: 'filterType',
} as const;

const INITIAL_DROPDOWN_STATES = {
  [DROPDOWN_KEYS.FILTER_TYPE]: false,
};

const filterTypeOptions = [
  { label: '역할명', value: 'roleNm' },
  { label: '설명', value: 'dtlCtnt' },
];

// ================================
// 컴포넌트
// ================================

/**
 * 관리 > 사용자 관리 > 사용자 상세 >  (TAB) 프로젝트 정보 > 프로젝트 상세 > 사용자 역할 수정 팝업
 */
export const UserProjRoleUpdatePopup = ({
  userInfo,
  projectInfo,
  onClose,
  refetch,
  currentRoleId,
}: {
  userInfo: UserType;
  projectInfo: ProjectType;
  onClose: () => void;
  refetch: () => void;
  currentRoleId?: string;
}) => {
  const { openAlert, openConfirm } = useModal();

  const [selectedRole, setSelectedRole] = useState<string | undefined>(currentRoleId);
  const [dropdownStates, setDropdownStates] = useState(INITIAL_DROPDOWN_STATES);

  const [searchForm, setSearchForm] = useState<RoleSearchValues>({
    filterType: 'roleNm',
    keyword: '',
  });
  const [searchParams, setSearchParams] = useState({
    page: 1,
    size: 10,
    filterType: 'roleNm',
    keyword: '',
  });

  const { data: rolesData } = useGetAssignableProjectRoles(projectInfo.uuid, searchParams);
  const updateRoleMutation = useUpdateUserProjectRole(userInfo.memberId, projectInfo.uuid);

  const gridKey = rolesData ? 'role-grid-ready' : 'role-grid-init';

  // 그리드 컬럼 정의
  const columnDefs: any = useMemo(
    () => [
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
        cellRenderer: (params: any) => (searchParams.page - 1) * searchParams.size + params.node.rowIndex + 1,
        sortable: false,
        suppressHeaderMenuButton: true,
        suppressSizeToFit: true,
      },
      {
        headerName: '역할명',
        field: 'roleNm',
        width: 262,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '설명',
        field: 'dtlCtnt',
        flex: 1,
        minWidth: 420,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '생성일시',
        field: 'fstCreatedAt',
        width: 180,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '최종 수정일시',
        field: 'lstUpdatedAt',
        width: 180,
        cellStyle: { paddingLeft: '16px' },
      },
    ],
    [searchParams]
  );

  const handleSearch = () => {
    const params = {
      page: 1,
      size: searchParams.size,
      filterType: searchForm.filterType,
      keyword: searchForm.keyword || '',
    };
    setSearchParams(params);
  };

  const handleDropdownToggle = (key: keyof typeof dropdownStates) => {
    setDropdownStates(prev => ({
      ...Object.fromEntries(Object.keys(prev).map(k => [k, false])),
      [key]: !prev[key],
    }));
  };

  const handleDropdownSelect = (key: keyof RoleSearchValues, value: string) => {
    setSearchForm(prev => ({ ...prev, [key]: value }));
    handleDropdownToggle(key as keyof typeof dropdownStates);
  };

  const handleMovePrevPage = async () => {
    const ok = await openConfirm({
      title: '안내',
      message: '화면을 나가시겠어요?\n입력한 정보가 저장되지 않을 수 있습니다.',
      cancelText: '아니요',
      confirmText: '예',
    });
    if (ok) onClose();
  };

  const handleSave = async () => {
    if (!selectedRole) {
      await openAlert({ title: '안내', message: '역할을 선택해주세요.' });
      return;
    }
    if (currentRoleId && selectedRole === currentRoleId) {
      await openAlert({ title: '안내', message: '수정된 내용이 없습니다.' });
      return;
    }

    updateRoleMutation.mutate(
      { uuid: selectedRole },
      {
        onSuccess: async () => {
          await refetch();
          await openAlert({ title: '완료', message: '수정사항이 저장되었습니다.' });
          onClose();
        },

        onError: async () => {
          await openAlert({ title: '실패', message: '사용자 역할 수정을 실패했습니다.' });
        },
      }
    );
  };

  return (
    <UILayerPopup
      isOpen
      onClose={onClose}
      size='fullscreen'
      showOverlay
      leftContent={
        <UIPopupAside>
          <UIPopupHeader title='사용자 역할 수정' description='' position='left' />
          <UIPopupBody>
            <UIArticle />
          </UIPopupBody>
          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <UIButton2 className='btn-tertiary-gray' style={{ width: '80px' }} onClick={handleMovePrevPage}>
                  취소
                </UIButton2>
                <UIButton2 className='btn-tertiary-blue' style={{ width: '80px' }} onClick={handleSave}>
                  저장
                </UIButton2>
              </UIUnitGroup>
            </UIArticle>
          </UIPopupFooter>
        </UIPopupAside>
      }
    >
      <section className='section-popup-content'>
        <UIPopupHeader title='사용자 역할 수정' description='사용자에게 할당하고 싶은 역할을 선택 후 저장 버튼을 클릭 해주세요.' position='right' />
        <UIPopupBody>
          <UIArticle>
            <UIFormField gap={8} direction='column'>
              <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb'>
                프로젝트명
              </UITypography>
              <UIInput.Text value={projectInfo.prjNm} disabled />
            </UIFormField>
          </UIArticle>

          <UIArticle className='article-grid'>
            <UIListContainer>
              <UIListContentBox.Header>
                <UIUnitGroup gap={16} direction='column'>
                  <div className='flex justify-between w-full items-center'>
                    <div className='w-full'>
                      <UIGroup gap={12} direction='row' align='start'>
                        <div style={{ width: '102px', display: 'flex', alignItems: 'center' }}>
                          <UIDataCnt count={rolesData?.totalElements || 0} prefix='총' unit='건' />
                        </div>
                      </UIGroup>
                    </div>
                    <div>
                      <UIGroup gap={12} direction='row' align='start'>
                        <div>
                          <UIDropdown
                            value={searchForm.filterType}
                            options={filterTypeOptions}
                            height={40}
                            width='w-[160px]'
                            isOpen={dropdownStates.filterType}
                            onClick={() => handleDropdownToggle(DROPDOWN_KEYS.FILTER_TYPE)}
                            onSelect={value => handleDropdownSelect(DROPDOWN_KEYS.FILTER_TYPE, value)}
                          />
                        </div>
                        <div style={{ width: '360px' }}>
                          <UIInput.Search
                            value={searchForm.keyword}
                            onChange={e => setSearchForm(prev => ({ ...prev, keyword: e.target.value }))}
                            onKeyDown={e => {
                              if (e.key === 'Enter') {
                                handleSearch();
                              }
                            }}
                            placeholder='검색어 입력'
                          />
                        </div>
                      </UIGroup>
                    </div>
                  </div>
                </UIUnitGroup>
              </UIListContentBox.Header>
              <UIListContentBox.Body>
                <UIGrid
                  key={gridKey}
                  type='single-select'
                  checkKeyName='uuid'
                  rowData={rolesData?.content || []}
                  columnDefs={columnDefs}
                  selectedDataList={selectedRole ? rolesData?.content?.filter(role => role.uuid === selectedRole) || [] : []}
                  onCheck={(roles: RoleType[]) => setSelectedRole(roles[0]?.uuid)}
                />
              </UIListContentBox.Body>
              <UIListContentBox.Footer>
                <UIPagination
                  currentPage={searchParams.page}
                  totalPages={rolesData?.totalPages || 1}
                  onPageChange={page => setSearchParams(prev => ({ ...prev, page }))}
                  className='flex justify-center'
                />
              </UIListContentBox.Footer>
            </UIListContainer>
          </UIArticle>
        </UIPopupBody>
      </section>
    </UILayerPopup>
  );
};
