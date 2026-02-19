import { RoleType } from './projMgmt.types';

export const getProjectRoleTypeLabel = (type: string | RoleType) => {
  switch (type) {
    case RoleType.DEFAULT:
      return '기본';
    case RoleType.CUSTOM:
      return '사용자 정의';
    default:
      return String(type ?? '');
  }
};
