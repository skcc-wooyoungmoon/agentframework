import type { AuthInfo } from '@/constants/auth';
import { useUser } from '@/stores/auth/useUser';
import { useModal } from '@/stores/common/modal';
import { useCallback } from 'react';

/**
 * 권한 체크를 위한 Hook
 * @param auth - 체크할 권한 정보
 * @returns 권한 체크 함수 및 권한 여부
 */
export const useAuthCheck = (showAlert = true) => {
  const { user } = useUser();
  const { openAlert } = useModal();

  /**
   * 권한이 있는지 확인
   * @returns 권한이 있으면 true, 없으면 false
   */
  const checkAuth = useCallback(
    (auth?: AuthInfo): boolean => {
      if (!auth?.id) {
        return true; // authId가 없으면 권한 체크 없이 통과
      }
      return user.functionAuthList.includes(auth?.id);
    },
    [user.functionAuthList]
  );

  /**
   * 권한이 있을 때만 실행하는 함수 wrapper
   *
   * @param callback - 권한이 있을 때 실행할 함수
   * @param args - 콜백 함수에 전달할 인자들
   */
  const withAuth = useCallback(
    <T extends (...args: any[]) => any>(auth?: AuthInfo, callback?: T, ...args: Parameters<T>): void => {
      if (!checkAuth(auth)) {
        if (showAlert) {
          openAlert({
            title: '안내',
            message: `${auth?.name}에 대한 권한이 없습니다.`,
            confirmText: '확인',
          });
        }
        return;
      }
      callback?.(...args);
    },
    [checkAuth, showAlert, openAlert]
  );

  /**
   * 이벤트 핸들러를 위한 권한 체크 wrapper
   * 권한이 없을 경우 preventDefault와 stopPropagation을 자동으로 호출하고 알림을 표시합니다.
   *
   */
  const withAuthEvent = useCallback(
    <T extends (e: any) => void>(auth?: AuthInfo, handler?: T): ((e: Parameters<T>[0]) => void) => {
      return (e: Parameters<T>[0]) => {
        if (!checkAuth(auth)) {
          e?.preventDefault();
          e?.stopPropagation();
          if (showAlert) {
            openAlert({
              title: '안내',
              message: `${auth?.name}에 대한 권한이 없습니다.`,
              confirmText: '확인',
            });
          }
          return;
        }
        handler?.(e);
      };
    },
    [checkAuth, showAlert, openAlert]
  );

  return {
    checkAuth,
    withAuth,
    withAuthEvent,
  };
};
