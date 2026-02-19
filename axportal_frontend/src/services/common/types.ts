export interface QueryRequestContextProps {
  queryRequest: QueryState;
  setQueryRequest: (queryRequest: QueryState) => void;
}

export interface QueryState {
  page: number;
  size: number;
  sort?: string;
  filter?: string;
  search?: string;
}

export const initialQueryRequest: QueryState = {
  page: 1,
  size: 10,
  sort: undefined,
  filter: undefined,
  search: undefined,
};

export interface PaginationState {
  page: number;
  size: number;
  totalElements?: number;
  totalPages?: number;
}

export interface Response<T = any> {
  data: T;
  success: boolean;
  message?: string;
  timestamp?: string;
  path?: string;
}

export interface ApproverRes<T = any> {
  data: T;
  success: boolean;
  message?: string;
  timestamp?: string;
  path?: string;
}

export interface GetProjUserList {
  username: string;
  condition: string;
  keyword: string;
  status: string;
}

export interface ProjUserInfo {
  memberId?: string;
  jkwNm?: string;
  deptNm?: string;
  dmcStatus?: string;
  retrJkwYn?: string | boolean;
  lstLoginAt?: string;
}

export interface PaymentApprovalRequest {
  approvalInfo: {
    memberId: string;
    approvalType: string; // 업무코드
    approvalUniqueKey?: string; // 요청식별자 (중복방지 등 목적으로 각 업무에서 활용)
    approvalParamKey?: number; // 비정형 결재자 처리를 위한 키값 (ex. 프로젝트 참여)
    approvalParamValue?: string; // 비정형 결재자 처리를 위한 키값 (ex. 프로젝트 참여)
    approvalItemString: string; // 요청하는 대상/작업 이름 (알람 표시 목적)
    afterProcessParamString: string; // 후처리 변수
    approvalSummary?: string; // 결재사유 메세지
    apprivalTableInfo?: { key: string; value: string }[][];
  } | null;
  approvalTypeInfo: {
    typeNm: string;
    approvalTarget: {
      prjNm: string;
      roleNm: string;
      prjSeq: number;
      roleSeq: number;
    }[];
  };
  displayInfo: {
    typeNm: string; // 업무구분
    jkwNm: string; // 이름
    deptNm: string; // 부서
    prjNm: string; // 프로젝트명
    prjRoleNm: string; // 역할
  };
}

export interface PaymentApprovalResponse {
  approval_id: string;
  status: string;
  message: string;
}

export type UserProjectRequest = {
  memberId: string;
  projectId: string;
};

export type UserProjectResponse = {
  username: string;
  deptNm: string;
  projectNm: string;
  roleNm: string;
};

export type ApprovalLineResponse = {
  gyljLineNm: string;
  gyljLineSno: string;
  gyljjaNm: string;
};

export type ManagerInfo = {
  /** 직원명 */
  jkwNm: string;
  /** 부서명 */
  deptNm: string;
  /** 재직 여부 1 - 재직, 0 - 퇴직*/
  retrJkwYn: number;
};

export type ManagerInfoResponse = ManagerInfo;

export type ManagerInfoResponseRequest = {
  /** 타입 */
  type: 'memberId' | 'uuid';

  value: string;
  // /** 생성자 */
  // createdBy: string;
  // /** 최종 수정자 */
  // updatedBy: string;
};

export type ManagerInfoResponseBulkRequest = {
  /** 타입 */
  type: 'memberId' | 'uuid';

  /** 관리자 ID 배열 (memberId 또는 uuid) */
  values: string[];
};

export type UpdateProjectToPublicRequest = {
  type: string;
  id: string; // 클라이언트에서는 uuid로 받지만, 서버로는 id로 전달
};

export type ProjectInfoResponseResponse = {
  /** 최종 프로젝트명 (lstPrjSeq가 양수인 경우만 조회) */
  lstPrjNm?: string;
  /** 최초 프로젝트명 */
  fstPrjNm?: string;
  /** 생성자 또는 수정자 (updated_by가 null이 아니면 updated_by, null이면 created_by) */
  userBy?: string;
  /** 사용자 직원명 (jkw_nm) */
  jkwNm?: string;
  /** 사용자 부서명 (dept_nm) */
  deptNm?: string;
  /** 퇴직 직원 여부 (retr_jkw_yn: 1=재직, 0=퇴직) */
  retrJkwYn?: number;
  /** 생성일시 또는 수정일시 (lst_updated_at이 null이 아니면 lst_updated_at, null이면 fst_created_at) */
  dateAt?: string;
};

export interface CheckApprovalStatusRequest {
  approvalUniqueKey: string;
}

export interface CheckApprovalStatusResponse {
  inProgress: boolean;
}
