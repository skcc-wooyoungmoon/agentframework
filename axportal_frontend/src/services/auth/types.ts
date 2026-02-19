/*****************************************************
 * 로그인
 *****************************************************/
/**
 * @description 로그인 요청 타입
 * @author 김예리
 */
export type GetLoginRequest = {
  username: string;
  password: string;
};

export type GetLoginResponse = {
  token_type: string;
  access_token: string;
  refresh_token: string;
  expires_in: number;
  refresh_expires_in: number;
  issued_at: string;
  expires_at: string;
  axAccessToken: string;
};

/*****************************************************
 * 토큰 갱신
 *****************************************************/
export type PostRefreshRequest = {
  refreshToken: string;
};

export type PostRefreshResponse = {
  token_type: string;
  access_token: string;
  refresh_token: string;
  expires_in: number;
  refresh_expires_in: number;
  issued_at: string;
  expires_at: string;
};

/*****************************************************
 * 토큰 갱신 관련 타입
 *****************************************************/
export type PostLogoutRequest = object;

export type PostLogoutResponse = object;

export type GetMeResponse = {
  projectList: {
    prjRoleSeq: string;
    prjSeq: string;
    prjUuid: string;
    adxpGroupPath: string;
    adxpGroupNm: string;
    prjNm: string;
    prjDesc: string;
    active: boolean;
    prjRoleNm: string;
  }[];
  activeProject: {
    prjRoleSeq: string;
    prjSeq: string;
    prjUuid: string;
    adxpGroupPath: string;
    adxpGroupNm: string;
    prjNm: string;
    prjDesc: string;
    active: boolean;
    prjRoleNm: string;
  };
  userInfo: {
    memberId: string;
    grpcoC: string | null;
    grpcoNm: string | null;
    jkwNm: string;
    jkpgNm: string | null;
    jkwiC: string | null;
    jkwiNm: string | null;
    retrJkwYn: string;
    deptNm: string;
    deptNo: string | null;
    adxpUserId: string;
  };
  adxpProject: {
    prjNm: string;
    prjUuid: string;
  };
  menuAuthList: string[];
  functionAuthList: string[];
  unreadAlarmCount: number;
};
/*****************************************************
 * 사용자 등록 정보
 *****************************************************/
export type PostRegisterRequest = {
  username: string;
  password: string;
  email: string;
  first_name: string;
  last_name: string;
};
export type PostRegisterResponse = {
  id: string;
  username: string;
  email: string;
  first_name: string;
  last_name: string;
};
export type PostMolimateRegisterRequest = {
  username: string;
  newJoinYn?: string;
};
export type PostSwingSmsRequest = {
  username: string;
};
export type PostSwingSmsCheckRequest = {
  username: string;
  authEventId: string;
  randomNumber: string;
  newJoinYn?: string;
};
export type PostSwingRegisterRequest = {
  username: string;
  password: string;
  ssoAuthCode?: string;
  newJoinYn?: string;
};
