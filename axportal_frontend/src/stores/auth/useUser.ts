import { useAtom } from 'jotai';
import { atomWithStorage, createJSONStorage } from 'jotai/utils';

import type { UserType } from '@/stores/auth/types';

/**
 * 사용자 초기값 정의
 */
const INITIAL_USER = {
  projectList: [],
  activeProject: {
    prjRoleSeq: '',
    prjSeq: '',
    prjUuid: '',
    adxpGroupPath: '',
    adxpGroupNm: '',
    prjNm: '',
    prjDesc: '',
    active: false,
    prjRoleNm: '',
  },
  userInfo: {
    memberId: '',
    grpcoC: null,
    grpcoNm: null,
    jkwNm: '사용자 이름 없음',
    jkpgNm: null,
    jkwiC: null,
    jkwiNm: null,
    retrJkwYn: '',
    deptNm: '',
    deptNo: null,
    adxpUserId: '',
  },
  adxpProject: {
    prjNm: '',
    prjUuid: '',
  },
  menuAuthList: [],
  functionAuthList: [],
  unreadAlarmCount: 0,
} as const satisfies UserType;

/**
 * LocalStorage 키
 */
const USER_STORAGE_KEY = 'AXPORTAL_USER';

/**
 * 사용자 정보를 관리하는 전역 상태 (localStorage에 지속 저장)
 */
const userAtom = atomWithStorage<UserType>(
  USER_STORAGE_KEY,
  INITIAL_USER,
  // SSR 안전한 storage 접근 (빌드/미리 렌더링 시 오류 방지)
  createJSONStorage(() => (typeof window !== 'undefined' ? localStorage : sessionStorage))
);

/**
 * 사용자 정보를 관리하는 커스텀 훅
 * @returns {Object} 사용자 상태와 제어 메소드들을 포함한 객체
 */
export const useUser = () => {
  const [user, setUser] = useAtom(userAtom);

  /**
   * 사용자 정보를 부분적으로 업데이트하는 메소드
   * @param newUser - 업데이트할 사용자 정보 (일부 속성만 포함 가능)
   */
  const updateUser = (newUser: Partial<UserType>) => {
    setUser(prev => ({ ...prev, ...newUser }));
  };

  /**
   * 사용자 정보를 초기값으로 초기화하는 메소드
   */
  const clearUser = () => {
    setUser(INITIAL_USER);
  };

  // const hasRole = (role: string): boolean => {
  //   return user.roles.includes(role);
  // };

  // const hasFeature = (feature: string): boolean => {
  //   return user.features.includes(feature);
  // };

  // const hasPermission = (permission: string): boolean => {
  //   return user.permissions.includes(permission);
  // };

  return {
    user,
    updateUser,
    clearUser,
    // hasRole,
    // hasFeature,
    // hasPermission,
  };
};
