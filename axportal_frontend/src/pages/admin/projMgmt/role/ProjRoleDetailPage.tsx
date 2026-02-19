import { useCallback, useState } from 'react';

import { UIArticle, UIPageBody, UIPageFooter, UIPageHeader, UIUnitGroup } from '@/components/UI/molecules';
import { UITabs } from '@/components/UI/organisms/UITabs';
import { STORAGE_KEYS } from '@/constants/common/storage.constants';
import { useBackRestoredState } from '@/hooks/common/navigation';
import { ProjRoleUserListPage } from '@/pages/admin/projMgmt';
import { RoleType, useDeleteProjectRoles, useGetProjectById, useGetProjectRoleById } from '@/services/admin/projMgmt';
import { projRoleListRefetchAtom } from '@/stores/admin/projMgmt/roles';
import { useModal } from '@/stores/common/modal';
import { useQueryClient } from '@tanstack/react-query';
import { useAtomValue } from 'jotai';
import { useNavigate, useParams } from 'react-router';

import { Button } from '@/components/common/auth';
import { AUTH_KEY } from '@/constants/auth';
import { ProjRolePermissionListPage } from './permission/ProjRolePermissionListPage';
import { ProjRoleBasicDetail } from './ProjRoleBasicDetail';
import { ProjRoleSummaryCard } from './ProjRoleSummaryCard';
import { ProjRoleUpdatePopup } from './ProjRoleUpdatePopup';

const tabItems = [
  { id: 'basic', label: '기본 정보' },
  { id: 'permission', label: '권한 정보' },
  { id: 'users', label: '구성원 정보' },
];

/**
 * 프로젝트 관리 > 프로젝트 상세 > (TAB) 역할 정보 > 역할 상세 - (탭 상태 관리)
 */
export const ProjRoleDetailPage = () => {
  const navigate = useNavigate();
  const { projectId, roleId } = useParams();

  const queryClient = useQueryClient();
  const { openConfirm, openAlert } = useModal();
  const refetchRoleList = useAtomValue(projRoleListRefetchAtom);

  // 탭 상태를 sessionStorage에 저장하여 뒤로가기 시 복원
  const { filters: tabState, updateFilters: setTabState } = useBackRestoredState<{ activeTab: string }>(
    STORAGE_KEYS.SEARCH_VALUES.PROJ_ROLE_DETAIL_TAB,
    { activeTab: 'basic' }
  );

  const activeTab = tabState.activeTab;
  const setActiveTab = (newTab: string) => setTabState({ activeTab: newTab });

  const [isEditOpen, setIsEditOpen] = useState(false);

  // 역할 상세 정보 조회
  const { data: roleData, refetch } = useGetProjectRoleById({
    projectId: projectId!,
    roleId: roleId!,
  });

  // 프로젝트 정보 조회 (상단 카드용)
  const { data: projectData } = useGetProjectById(projectId!);

  const { mutateAsync: deleteProjectRoles, isPending: isDeletingRole } = useDeleteProjectRoles(projectId!);

  const roleSummary = roleData?.role
    ? {
        id: roleData.role.uuid,
        name: roleData.role.roleNm,
        description: roleData.role.dtlCtnt,
        type: roleData.role.roleType,
      }
    : null;

  const handleEditRole = () => setIsEditOpen(true);
  const handleCloseEdit = () => setIsEditOpen(false);

  const handleDeleteRole = useCallback(async () => {
    if (!roleData?.role || isDeletingRole) return;

    const confirmed = await openConfirm({
      title: '안내',
      message: '삭제하시겠어요?\n삭제한 정보는 복구할 수 없습니다.',
      cancelText: '아니요',
      confirmText: '예',
    });

    if (!confirmed) return;

    const response = await deleteProjectRoles({ roleUuids: [roleData.role.uuid] });
    const { successCount, errorMessage } = response.data;

    // 에러 메시지가 있으면 사용자에게 알림
    if (errorMessage) {
      await openAlert({
        title: '안내',
        message: errorMessage,
        confirmText: '확인',
      });
      return;
    }

    // 삭제 성공
    if (successCount > 0) {
      await openAlert({
        title: '완료',
        message: '역할이 삭제되었습니다.',
        confirmText: '확인',
        onConfirm: async () => {
          if (refetchRoleList) refetchRoleList();
          queryClient.invalidateQueries({
            queryKey: ['GET', `/admin/projects/${projectId}/roles`],
          });
          navigate(`/admin/project-mgmt/${projectId}`, { replace: true, state: { tab: 'role' } });
        },
      });
    }
  }, [deleteProjectRoles, isDeletingRole, navigate, openAlert, openConfirm, projectId, queryClient, refetchRoleList, roleData?.role]);

  return (
    <section className='section-page'>
      {/* 페이지 헤더 */}
      <UIPageHeader title='역할 조회' description='' />

      {/* 탭 영역 */}
      <UIPageBody>
        {/* 역할 요약 카드 */}
        {roleData?.role && <ProjRoleSummaryCard projectNm={projectData?.project?.prjNm} roleInfo={roleData.role} />}

        <UIArticle className='article-tabs'>
          <UITabs items={tabItems} activeId={activeTab} size='large' onChange={setActiveTab} />
        </UIArticle>

        {/* 탭 내용 영역  */}

        {/* 역할 기본 정보 */}
        {activeTab === 'basic' && roleData?.role && <ProjRoleBasicDetail projectId={projectId!} roleInfo={roleData.role} />}

        {/* 역할 권한 정보 */}
        {activeTab === 'permission' && roleSummary && <ProjRolePermissionListPage projectId={projectId!} role={roleSummary} />}

        {/* 역할 구성원 정보 */}
        {activeTab === 'users' && roleSummary && <ProjRoleUserListPage projectId={projectId!} role={roleSummary} />}
      </UIPageBody>

      {/* 페이지 푸터 */}
      {activeTab === 'basic' && roleData?.role?.roleType === RoleType.CUSTOM && (
        <UIPageFooter>
          <UIArticle>
            <UIUnitGroup gap={8} direction='row' align='center'>
              <Button auth={AUTH_KEY.ADMIN.ROLE_DELETE} className='btn-primary-gray w-[80px]' onClick={handleDeleteRole} disabled={isDeletingRole}>
                삭제
              </Button>
              <Button auth={AUTH_KEY.ADMIN.ROLE_BASIC_INFO_UPDATE} className='btn-primary-blue w-[80px]' onClick={handleEditRole}>
                수정
              </Button>
            </UIUnitGroup>
          </UIArticle>
        </UIPageFooter>
      )}

      {/* 수정 팝업 */}
      {isEditOpen && roleData?.role && (
        <ProjRoleUpdatePopup onClose={handleCloseEdit} roleData={{ name: roleData.role.roleNm, description: roleData.role.dtlCtnt }} refetch={refetch} />
      )}
    </section>
  );
};
