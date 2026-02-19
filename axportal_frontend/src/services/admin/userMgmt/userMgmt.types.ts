// 사용자 관리 API 타입 정의

// import type { ProjectType } from '../projMgmt';

/**
 * 인사 상태
 */
export const WorkStatus = {
  EMPLOYED: 'EMPLOYED',
  RESIGNED: 'RESIGNED',
} as const;

export type WorkStatus = (typeof WorkStatus)[keyof typeof WorkStatus];

// ================================
// 요청 타입
// ================================

/**
 * 사용자 목록 조회 요청 타입
 *
 * @param page - 페이지 번호
 * @param size - 페이지 당 항목 수
 * @param startDate - 조회 시작일
 * @param endDate - 조회 종료일
 * @param filterType - 검색 타입 (profile, dept, position, username)
 * @param keyword - 검색 키워드
 * @param retrJkwYn - 인사 상태 (1:재직, 0:퇴직)
 * @param dmcStatus - 계정 상태 (ACTIVE: 활성, DORMANT: 휴면)
 */
export type GetUsersRequest = {
  page?: number;
  size?: number;
  filterType?: string;
  keyword?: string;
  retrJkwYn?: string;
  dmcStatus?: string;
};

/**
 * 사용자 상세 조회 요청 타입
 *
 * @param userId - 조회할 사용자의 고유 식별자
 */
export type GetUserByIdRequest = {
  userId: string;
};

/**
 * 사용자가 참여한 프로젝트 조회 요청 타입
 *
 * @param page - 페이지 번호 (선택적)
 * @param size - 페이지 당 항목 수 (선택적)
 * @param filterType - 검색 유형 (name, description) (선택적)
 * @param keyword - 검색어 (선택적)
 * @param statusNm - 프로젝트 상태 (ONGOING, COMPLETED) (선택적)
 */
export type GetUserProjectsRequest = {
  page?: number;
  size?: number;
  filterType?: string;
  keyword?: string;
  statusNm?: string;
};

/**
 * 사용자가 참여한 프로젝트 상세 조회 요청 타입
 *
 * @param userId - 조회할 사용자의 고유 식별자
 * @param projectId - 조회할 프로젝트의 고유 식별자
 */
export type GetUserProjectDetailRequest = {
  userId: string;
  projectId: string;
};

/**
 * 사용자 프로젝트 역할 수정 요청 타입
 *
 * @param roleId - 새로운 역할 ID
 */
export type UpdateUserProjectRoleRequest = { uuid: string };

/**
 * 할당 가능한 역할 목록 조회 요청 타입
 *
 * @param page - 페이지 번호 (1부터 시작) (선택적)
 * @param size - 페이지 크기 (선택적)
 * @param filterType - 검색 유형 (roleNm, dtlCtnt) (선택적)
 * @param keyword - 검색어 (선택적)
 * @param statusNm - 역할 상태 (ACTIVE, INACTIVE) (선택적)
 */
export type GetAssignableRolesRequest = {
  page?: number;
  size?: number;
  filterType?: string;
  keyword?: string;
  statusNm?: string; // 'status' → 'statusNm'으로 변경
};

// ================================
// 응답 타입
// ================================

/**
 * 사용자 기본 타입
 *
 * @param memberId - 사용자 ID
 * @param uuid - 사용자 고유 식별자
 * @param jkwNm - 사용자명
 * @param deptNm - 부서명
 * @param jkgpNm - 직급명
 * @param retrJkwYn - 재직 여부
 * @param hpNo - 휴대폰 번호
 * @param dmcStatus - 계정 상태
 * @param lstLoginAt - 마지막 로그인 시간
 * @param fstCreatedAt - 최초 생성일시
 * @param lstUpdatedAt - 최종 수정일시
 * @param createdBy - 생성자
 * @param updatedBy - 수정자
 */
export type UserType = {
  memberId: string;
  uuid: string;
  jkwNm: string;
  userPassword: string;
  deptNm: string;
  jkgpNm: string;
  retrJkwYn: string;
  hpNo: string;
  dmcStatus: string;
  lstLoginAt: string;
  fstCreatedAt: string | null;
  lstUpdatedAt: string | null;
  createdBy: string;
  updatedBy: string;
};

// 사용자 목록 조회 응답 타입
export type GetUsersResponse = UserType;

// 사용자 상세 조회 응답 타입
export type GetUserByIdResponse = UserType;

/**
 * 프로젝트 타입 (v2 API 응답 구조에 맞춰 수정)
 */
export type ProjectType = {
  prjSeq: string;
  uuid: string;
  prjNm: string;
  dtlCtnt: string;
  sstvInfInclYn: string;
  sstvInfInclDesc: string | null;
  fstCreatedAt: string | null;
  lstUpdatedAt: string | null;
  createdBy: string;
  updatedBy: string;
};

// 사용자가 참여한 프로젝트 조회 응답 타입
export type GetUserProjectsResponse = ProjectType;

// 사용자가 참여한 프로젝트 상세 조회 응답 타입 - V2
export type GetUserProjectDetailResponse = {
  project: {
    uuid: string;
    prjNm: string;
    dtlCtnt: string;
    statusNm: string;
    fstCreatedAt: string;
    lstUpdatedAt: string;
  };
  role: {
    uuid: string;
    roleNm: string;
    dtlCtnt: string;
    statusNm: string;
    fstCreatedAt: string;
    lstUpdatedAt: string;
  };
  userRole: {
    lstUpdatedAt: string | null;
    updatedBy: {
      jkwNm: string;
      deptNm: string;
    } | null;
  };
};

/**
 * 역할 타입 - V2
 *
 * @param uuid - 역할 고유 식별자
 * @param roleNm - 역할명
 * @param dtlCtnt - 역할 설명
 * @param roleType - 역할 유형 (DEFAULT, CUSTOM)
 * @param fstCreatedAt - 생성 일시
 * @param lstUpdatedAt - 최종 수정일시
 */
export type RoleType = {
  uuid: string;
  roleNm: string;
  dtlCtnt: string;
  roleType: string;
  fstCreatedAt: string;
  lstUpdatedAt: string;
};

// 할당 가능한 역할 목록 조회 응답 타입
export type GetAssignableRolesResponse = RoleType;

// todo 삭제 예정
// 사용자 프로젝트 역할 수정 응답 타입
// export type UpdateUserProjectRoleResponse = string;

/**
 * 사용자 상태 ACTICE로 변경 요청 타입
 */
export type PutUserStatusRequest = {
  memberId: string;
  dmcStatus: 'ACTIVE' | 'DORMANT';
};
