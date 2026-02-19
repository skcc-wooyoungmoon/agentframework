// 프로젝트 관리 API 타입 정의

/**
 * 프로젝트 상태
 */
export const ProjectStatus = {
  ONGOING: 'ONGOING',
  COMPLETED: 'COMPLETED',
} as const;

export type ProjectStatus = (typeof ProjectStatus)[keyof typeof ProjectStatus];

/**
 * 역할 유형
 */
export const RoleType = {
  DEFAULT: 'DEFAULT',
  CUSTOM: 'CUSTOM',
} as const;

export type RoleType = (typeof RoleType)[keyof typeof RoleType];

// ================================
// 요청 타입
// ================================

/**
 * 프로젝트 역할내 구성원 목록 조회 요청 타입
 *
 * @param page - 페이지 번호 (선택적)
 * @param size - 페이지 당 항목 수 (선택적)
 * @param startDate - 조회 시작일 (선택적)
 * @param endDate - 조회 종료일 (선택적)
 * @param filterType - 검색 조건 (jkwNm: 이름, deptNm: 부서) (선택적)
 * @param keyword - 검색어 (선택적)
 * @param dmcStatus - 계정 상태 (ACTIVE, DORMANT) (선택적)
 * @param retrJkwYn - 퇴직 여부 (0: 재직, 1: 퇴직) (선택적)
 */
export type GetProjectRoleUsersRequest = {
  page?: number;
  size?: number;
  startDate?: string;
  endDate?: string;
  filterType?: 'jkwNm' | 'deptNm';
  keyword?: string;
  dmcStatus?: 'ACTIVE' | 'DORMANT';
  retrJkwYn?: 0 | 1;
};

/**
 * 프로젝트 역할 권한 목록 조회 요청 타입
 */
export type GetProjectRoleAuthoritiesRequest = {
  page?: number;
  size?: number;
  dateType?: 'fstCreatedAt' | 'lstUpdatedAt';
  startDate?: string;
  endDate?: string;
  filterType?: 'authorityNm' | 'dtlCtnt';
  keyword?: string;
  twoDepthMenu?: string;
};

// ================================
// 응답 타입
// ================================

/**
 * 프로젝트 기본 타입
 *
 * @param uuid - 프로젝트 고유 식별자
 * @param prjNm - 프로젝트명
 * @param dtlCtnt - 프로젝트 설명
 * @param statusNm - 프로젝트 상태 (ONGOING, COMPLETED)
 * @param createdAt - 최초 생성일시
 * @param updatedAt - 최종 수정일시
 */
export type ProjectType = {
  uuid: string;
  prjNm: string;
  dtlCtnt: string;
  statusNm: ProjectStatus;
  createdAt: string;
  updatedAt: string;
};

/**
 * 프로젝트 역할 구성원 기본 타입
 *
 * @param memberId - 사번
 * @param uuid - 사용자 UUID
 * @param jkwNm - 사용자명
 * @param deptNm - 부서명
 * @param jkgpNm - 직급명
 * @param dmcStatus - 계정 상태 (ACTIVE, DORMANT)
 * @param retrJkwYn - 인사 상태 (1: 재직, 0: 퇴직)
 * @param lstLoginAt - 마지막 접속 일시
 */
export type ProjectRoleUserType = {
  memberId: string;
  uuid: string;
  jkwNm: string;
  deptNm: string;
  jkgpNm: string;
  dmcStatus: string;
  retrJkwYn: string;
  lstLoginAt: string;
};

// 프로젝트 역할내 구성원 목록 조회 응답 타입
export type GetProjectRoleUsersResponse = ProjectRoleUserType;

/**
 * 프로젝트 역할 권한 타입
 */
export type ProjectRoleAuthorityType = {
  authorityId: string;
  hrnkAuthorityId?: string;
  authorityNm: string;
  oneDepthMenu: string | null;
  twoDepthMenu: string | null;
  detailContent: string | null;
  fstCreatedAt: string | null;
  lstUpdatedAt: string | null;
};

// 프로젝트 역할 권한 목록 응답 타입
export type GetProjectRoleAuthoritiesResponse = ProjectRoleAuthorityType;

/**
 * 프로젝트 역할 권한 수정 요청 타입
 */
export type UpdateProjectRoleAuthoritiesRequest = {
  authorityIds: string[];
};

// ================================
// 프로젝트 구성원 역할 할당 타입
// ================================

export type ProjectUserAssignRequest = {
  assignments: Array<{
    userUuid: string;
    roleUuid: string;
  }>;
};

export type ProjectUserAssignResponse = {
  successCount: number;
  failureCount: number;
};

export type ProjectUserRoleChangeRequest = {
  users: Array<{
    userUuid: string;
    roleUuid: string;
  }>;
};

export type ProjectUserRoleChangeResponse = ProjectUserAssignResponse;

export type ProjectUserDeleteRequest = {
  userUuids: string[];
};

export type ProjectUserDeleteResponse = {
  successCount: number;
  failureCount: number;
};

/**
 * 메뉴 권한 응답 타입
 *
 * @param authorityId - 권한 ID
 * @param oneDphMenu - 상위 메뉴명
 * @param twoDphMenu - 하위 메뉴명
 */
export interface MenuPermitRes {
  authorityId: string;
  oneDphMenu: string;
  twoDphMenu: string;
}

/**
 * 메뉴 진입 설정을 위한 권한(메뉴) 목록 조회 요청 타입
 */
export interface GetMenuPermitsRequest {
  page?: number;
  size?: number;
  oneDphMenu?: string;
  twoDphMenu?: string;
  filterType?: 'all' | 'oneDphMenu' | 'twoDphMenu';
  keyword?: string;
}

/**
 * 상세 권한 목록 조회 요청 타입
 */
export interface GetPermitDetailsRequest {
  page?: number;
  size?: number;
  authorityIds?: string; // Changed from string[] to string
  twoDphMenu?: string;
  filterType?: 'all' | 'authorityNm' | 'dtlCtnt';
  keyword?: string;
}

/**
 * 상세 권한 목록 응답 타입
 */
export interface PermitDetailRes {
  authorityId: string;
  twoDphMenu: string;
  authorityNm: string;
  dtlCtnt: string;
}

/**
 * 프로젝트 목록 조회 요청 타입
 */
export type GetProjectsRequest = {
  page?: number;
  size?: number;
  filterType?: 'prjNm' | 'dtlCtnt';
  keyword?: string;
};

/**
 * 프로젝트 목록 조회 응답 타입
 */
export type GetProjectsResponse = {
  prjSeq: number;
  uuid: string;
  prjNm: string;
  dtlCtnt: string;
  fstCreatedAt: string;
  lstUpdatedAt: string;
};

type UserInfo = {
  jkwNm: string;
  deptNm: string;
};

/**
 * 프로젝트 구성원 목록 조회 요청 타입
 */
export type GetProjectUsersRequest = {
  page?: number;
  size?: number;
  filterType?: 'jkwNm' | 'deptNm' | 'memberId';
  keyword?: string;
  dmcStatus?: 'ACTIVE' | 'DORMANT';
  retrJkwYn?: '0' | '1';
};

/**
 * 프로젝트 구성원 기본 타입
 */
export type ProjectUserType = {
  memberId: string;
  uuid: string;
  jkwNm: string;
  deptNm: string;
  jkgpNm: string;
  dmcStatus: 'ACTIVE' | 'DORMANT';
  retrJkwYn: '0' | '1';
  lstLoginAt: string;
};

/**
 * 프로젝트 구성원 목록 조회 응답 타입
 */
export type GetProjectUsersResponse = {
  uuid: string;
  memberId: string;
  jkwNm: string;
  deptNm: string;
  roleUuid: string;
  roleNm: string;
  dmcStatus: 'ACTIVE' | 'DORMANT';
  retrJkwYn: '0' | '1';
  lstLoginAt: string;
  joinedAt?: string;
};

/**
 * 초대 가능 사용자 목록 조회 요청 타입
 */
export type GetAvailableUsersRequest = {
  page?: number;
  size?: number;
  filterType?: 'jkwNm' | 'deptNm';
  keyword?: string;
};

/**
 * 초대 가능 사용자 목록 조회 응답 타입
 */
export type GetAvailableUsersResponse = ProjectUserType;

export type ProjectDetailType = {
  prjSeq: number;
  uuid: string;
  prjNm: string;
  dtlCtnt: string;
  sstvInfInclYn: 'Y' | 'N';
  sstvInfInclDesc: string;
  fstCreatedAt: string;
  lstUpdatedAt: string;
  createdBy: UserInfo;
  updatedBy: UserInfo;
};

/**
 * 프로젝트 상세 조회 응답 타입
 */
export type GetProjectByIdResponse = {
  project: ProjectDetailType;
};

/**
 * 프로젝트 수정 요청 타입
 */
export type UpdateProjectRequest = {
  prjNm: string;
  dtlCtnt: string;
  sstvInfInclYn: 'Y' | 'N';
  sstvInfInclDesc?: string;
};

/**
 * 프로젝트 수정 응답 타입
 */
export type UpdateProjectResponse = void;

// == 프로젝트 역할 == //

/**
 * 프로젝트 역할 목록 조회 요청 타입
 */
export type GetProjectRolesRequest = {
  page?: number;
  size?: number;
  roleType?: RoleType;
  filterType?: 'roleNm' | 'dtlCtnt';
  keyword?: string;
};

/**
 * 프로젝트 역할 목록 조회 응답 타입
 */
export type GetProjectRolesResponse = {
  uuid: string;
  roleNm: string;
  dtlCtnt: string;
  roleType: RoleType;
  fstCreatedAt: string;
  lstUpdatedAt: string;
};

/**
 * 프로젝트 역할 상세 조회 요청 타입
 */
export type GetProjectRoleByIdRequest = {
  projectId: string;
  roleId: string;
};

export type RoleDetailType = {
  uuid: string;
  roleNm: string;
  dtlCtnt: string;
  roleType: RoleType;
  fstCreatedAt: string;
  lstUpdatedAt: string;
  createdBy: UserInfo;
  updatedBy: UserInfo;
};

/**
 * 프로젝트 역할 상세 조회 응답 타입
 */
export type GetProjectRoleByIdResponse = {
  role: RoleDetailType;
};

/**
 * 프로젝트 역할 생성 요청 타입
 *
 * @param roleNm - 생성할 역할명
 * @param dtlCtnt - 역할 설명 (선택)
 * @param authorityIds - 연결할 권한 ID 목록
 */
export type CreateProjectRoleRequest = {
  roleNm: string;
  dtlCtnt?: string;
  authorityIds: string[];
};

/**
 * 프로젝트 역할 생성 응답 타입
 */
export type CreateProjectRoleResponse = {
  uuid: string; // 생성된 역할 UUID
};

/**
 * 프로젝트 역할 수정 요청 타입
 *
 * @param roleNm - 수정할 역할명
 * @param dtlCtnt - 역할 설명 (선택)
 */
export type UpdateProjectRoleRequest = {
  roleNm: string;
  dtlCtnt?: string;
};

/**
 * 프로젝트 역할 수정 응답 타입
 */
export type UpdateProjectRoleResponse = void;

/**
 * 프로젝트 역할 삭제 요청 타입
 */
export type DeleteProjectRolesRequest = {
  roleUuids: string[];
};

/**
 * 프로젝트 역할 삭제 응답 타입
 */
export type DeleteProjectRolesResponse = {
  successCount: number;
  failureCount: number;
  errorMessage: string;
};
