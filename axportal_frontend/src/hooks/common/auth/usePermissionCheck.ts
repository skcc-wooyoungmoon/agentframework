import { useUser } from '@/stores/auth/useUser';
import { useModal } from '@/stores/common/modal';

/**
 * Private 프로젝트 확인 유틸리티 훅
 * @returns Private 프로젝트 여부 확인 함수 및 권한 체크 함수
 */
export const usePermissionCheck = () => {
  const { user } = useUser();
  const { openAlert } = useModal();

  /**
   * 현재 선택된 프로젝트가 Public 프로젝트인지 확인
   */
  const isPublicProject = (): boolean => {
    if (!user?.projectList) return false;
    const selectedProject = user.projectList.find(project => project.active);
    return selectedProject?.prjSeq === '-999';
  };

  /**
   * 권한 체크 및 알럿 표시
   * @param action - 권한이 있을 때 실행할 함수
   */
  const checkPermissionAndShowAlert = (action: () => void): void => {
    if (!isPublicProject()) {
      openAlert({
        title: '안내',
        message: '해당 기능에 대한 권한이 없습니다.',
        confirmText: '확인',
      });
      return;
    }
    action();
  };

  return {
    isPublicProject,
    checkPermissionAndShowAlert,
  };
};
