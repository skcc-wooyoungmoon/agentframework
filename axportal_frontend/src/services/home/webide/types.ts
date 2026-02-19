// Python 버전 응답 타입
export interface PythonVerRes {
  versions: string[];
}

// IDE 상태 응답 타입
export interface IdeRes {
  status: 'active' | 'inactive';
  url?: string;
}

// IDE 목록 응답 타입 (백엔드 IdeListRes와 매핑)
export interface IdeListRes {
  inUse: boolean; // 하나라도 있으면 true
  total: number; // 전체 건수
  items: IdeItemRes[]; // 실제 IDE 리스트
}

// IDE 아이템 응답 타입
export interface IdeItemRes {
  ideId: string;
  userId: string;
  username: string;
  ide: string;
  status: string;
  prjSeq: number;
  cpu: number;
  memory: number;
  image: string;
  createdAt: string;
  updatedAt: string;
  expireAt: string;
  ingressUrl: string;
  pythonVer: string;
}

// IDE 삭제 요청 타입
export interface IdeDeleteReq {
  ideId: string;
  userId?: string;
  username?: string;
  ide?: string;
  status?: string;
  prjSeq?: number;
  ingressUrl?: string;
  pythonVer?: string;
}

// IDE 조회 요청 타입
export interface GetIdeRequest {
  userName: string;
  ideType: string;
}

// Python 버전 조회 요청 타입
export interface GetPythonVerRequest {
  ideType: 'jupyter' | 'vscode' | null;
}

// IDE 생성 요청 타입 (백엔드 IdeReq와 매핑)
export interface CreateIdeRequest {
  prjSeq: number[];
  userId: string;
  imgUuid: string;
  ideType: string;
  dwAccount: string;
  dwAccountUsed: boolean;
  cpu: number;
  memory: number;
}

// IDE 생성 응답 타입 (백엔드 IdeCreateRes와 매핑)
export interface IdeCreateRes {
  ideId: string;
  userId: string;
  prjSeq: number[];
  ide: string; // vscode|jupyter
  ingressUrl: string; // 접속 URL
  expireAt: string; // ISO 8601 형식의 날짜 문자열
  cpu: number; // 기본 CPU
  memory: number; // 기본 MEM
  image: string; // 사용 이미지 태그
}

// DW 계정 조회 요청 타입
export interface GetDwAccountRequest {
  userId: string;
}

// DW 계정 아이템 타입 (백엔드 API 응답 구조)
export interface DwAccountItem {
  userName: string;
  empNo: string;
  groupName: string;
  deptCd: string;
  validStartDate: string;
  validEndDate: string;
  dbAccountId: string;
  dbName: string;
  dbType: string;
  ipAddr: string;
  dwDataGjdt: string;
  dwLstJukjaDt: string;
  accountStatus: string; // "Y" | "N"
}

// DW 계정 조회 응답 타입
export type DwAccountRes = DwAccountItem[];

// 전체 DW 계정 목록 조회 응답 타입
export type DwAllAccountsRes = string[];

// AccessToken 조회 응답 타입
export interface AccessTokenRes {
    access_token: string;
    refresh_token: string;
    //project_info는 사용안함
    //project_info: ProjectInfoRes;

}

// IDE 생성 가능 여부 확인 요청 타입
export interface CheckIdeCreateAvailableRequest {
  ideType: string;
}

// IDE 이미지 아이템 타입
export interface IdeImage {
  type: string;
  name: string;
  desc: string;
  id: string;
}

// IDE 이미지 목록 응답 타입
export interface IdeImageRes {
  images: IdeImage[];
}

// IDE 상태 조회 요청 타입
export interface GetIdeStatusRequest {
  memberId: string;
  keyword?: string;
  page?: number;
  size?: number;
}

// IDE 상태 조회 응답 타입 (백엔드 IdeStatusRes와 매핑)
export interface IdeStatusRes {
  ideUuid: string;
  imgG: 'VSCODE' | 'JUPYTER';
  imgNm: string;
  dwAccountId: string;
  expAt: string;
  svrUrlNm: string;
}

// IDE 사용 기간 연장 요청 타입 (백엔드 IdeExtendReq와 매핑)
export interface IdeExtendReq {
  statusUuid: string;
  extendDays: number;
}