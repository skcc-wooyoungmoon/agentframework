export interface ProjInfo {
  prjSeq: string;
  memberId: string;
  roleSeq: string;
  prjNm: string;
  dtlCtnt: string;
}

// FineTuning 관련 타입 정의
export interface ProjBaseInfo {
  projectId: string;
  prjSeq: number;
  name: string;
  description: string;
  status: string;
  createdAt: string;
  updatedAt: string;
}

export interface PutProjInfo {
  username: string;
  project: {
    id: string;
    projectName?: string;
    roleId?: string;
    createdBy?: string;
    uuid?: string;
  };
}

export interface ProjJoinInfoDetail {
  projectId: string;
  createdAt: string;
  projectMgmteInfo: string;
  createrInfo: string;
}

export interface ProjJoinDetail {
  project_id: string;
  proj_mgmte_info: string;
  creater_info: string;
  create_at: string;
}

export interface ProjInfo {
  prjSeq: string;
  memberId: string;
  roleSeq: string;
  prjNm: string;
  dtlCtnt: string;
  memberCnt: number;
  memberCount: number;
  createrInfo: string;
  uuid: string;
  selected: boolean;
}

export interface CreateProjBaseInfoRequest {
  name: string;
  description: string;
  is_sensitive: string;
  sensitive_reason: string;
  is_portal_admin: string;
  member_ids?: string[]; // 멤버 ID 배열 추가
}
export interface GetProjUserList {
  username: string;
  condition: string;
  keyword: string;
  status: string;
}

export interface ProjUserInfo {
  memberId: string;
  jkwNm: string;
  deptNm: string;
  dmcStatus: string;
  retrJkwYn: string;
  lstLoginAt: string;
  enabled: string;
}
export interface ProjUserReq {
  username: string;
  condition: string;
  keyword: string;
}

export interface JoinProjInfo {
  username: string;
  project: JoinProjInfoDetail;
}

export interface JoinProjInfoDetail {
  uuid: string;
  projectName: string;
}

export interface ProjActionRequest {
  username: string;
  project: string; // backend expects project object or minimal id; keep as any to match current usage
}
