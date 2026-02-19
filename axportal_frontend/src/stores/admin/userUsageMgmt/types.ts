// 사용자 이용 현황 관련 타입 정의

/**
 * 사용자 활동 로그 기본 정보
 */
export interface UserActivity {
  /** 사용자 ID */
  id: string;
  
  /** 사용자명 */
  userName: string;
  
  /** 프로젝트명 */
  projectName: string;
  
  /** 역할명 */
  roleName: string;
  
  /** 메뉴경로 */
  menuPath: string;

  /** 한글화된 메뉴경로 */
  menuPathDisplay?: string;
  
  /** 액션 (생성, 수정, 삭제, 조회) */
  action: string;
  
  /** 대상자산 */
  targetAsset: string;
  
  /** 리소스타입 */
  resourceType: string;
  
  /** API엔드포인트 */
  apiEndpoint: string;
  
  /** 결과 (성공, 실패) */
  result: string;
  
  /** 에러 코드 (200: 성공, 그 외: 실패) */
  errCode: number;
  
  /** 생성일시 */
  createdAt: string;

  /** 접속 환경 */
  userAgent: string;

  /** 클라이언트 IP */
  clientIp: string;

  /** 요청 내용 */
  requestContent?: string | object;

  /** 응답 내용 */
  responseContent?: string | object;
}

/**
 * 사용자 이용 현황 통계 정보
 */
export interface UserActivityStats {
  /** 전체 사용자 수 */
  total_users: number;
  
  /** 오늘 활성 사용자 수 */
  active_users_today: number;
  
  /** 이번 주 활성 사용자 수 */
  active_users_this_week: number;
  
  /** 이번 달 활성 사용자 수 */
  active_users_this_month: number;
  
  /** 전체 활동 수 */
  total_activities: number;
  
  /** 오늘 활동 수 */
  activities_today: number;
  
  /** 이번 주 활동 수 */
  activities_this_week: number;
  
  /** 이번 달 활동 수 */
  activities_this_month: number;
  
  /** 인기 활동 목록 */
  popular_activities: Array<{
    activity_type: string;
    count: number;
  }>;
  
  /** 사용자 분포 */
  user_distribution: Array<{
    date: string;
    user_count: number;
  }>;

  /** 접속자 수 */
  visitorCount?: number;
  
  /** 월별 데이터 */
  monthlyData?: Array<{
    month: string;
    visitors: number;
  }>;
  
  /** API 통계 */
  apiStats?: {
    successRate: number;
    successCount: number;
    failureCount: number;
  };
  
  /** 로그인 성공 횟수 배열 */
  loginSuccessCounts?: Array<{
    month?: string;
    count?: number;
    visitors?: number;
    value?: number;
  }>;
  /** 총 로그인 성공 횟수 */
  totalLoginSuccessCount?: number;
  /** 기간 시작일 */
  periodStartDate?: string;
  /** 기간 종료일 */
  periodEndDate?: string;
  /** 프로젝트 타입 */
  projectType?: string;
  /** 검색 타입 */
  searchType?: string;
  /** 선택된 월 */
  selectedMonth?: string;
  /** 통계 날짜 */
  statisticsDate?: string;
  
  /** API 호출 실패 요약 */
  apiFailureSummary?: Array<{
    time?: string;
    timestamp?: string;
    menu?: string;
    service?: string;
    name?: string;
    errCode?: string;
    errorCode?: string;
    code?: string;
  }>;
  
  /** 상위 사용 메뉴 */
  topUsedMenus?: Array<{
    menu?: string;
    name?: string;
    service?: string;
    count?: number;
    usage?: number;
    percentage?: number;
  }>;
  
  /** API 성공률 */
  apiSuccessRate?: number;
  
  /** API 성공 건수 */
  apiSuccessCount?: number;
  
  /** API 실패률 */
  apiFailureRate?: number;
  
  /** API 실패 건수 */
  apiFailureCount?: number;
}

/**
 * API 응답 타입 정의
 */
export interface GetUserActivityListResponse {
  data: UserActivity[];
  payload: {
    pagination: {
      total: number;
      last_page: number;
      current_page: number;
      per_page: number;
    };
  };
}

export interface GetUserActivityStatsResponse {
  data: UserActivityStats;
}

export interface GetUserActivityDetailResponse {
  data: UserActivity;
}

/**
 * 사용자 이용 현황 목록 조회 파라미터
 */
export interface GetUserActivityListParams {
  page?: number;
  size?: number;
  searchValue?: string;
  dateType?: string;
  projectName?: string;
  result?: string;
  searchType?: string;
  fromDate?: string;
  toDate?: string;
}

/**
 * 액션 타입 상수
 */
export const USER_ACTIONS = {
  CREATE: '생성',
  UPDATE: '수정',
  DELETE: '삭제',
  READ: '조회',
  DOWNLOAD: '다운로드',
  UPLOAD: '업로드',
  DEPLOY: '배포',
  LOGIN: '로그인',
  LOGOUT: '로그아웃',
} as const;

/**
 * 결과 타입 상수
 */
export const USER_ACTIVITY_RESULTS = {
  SUCCESS: '성공',
  FAILURE: '실패',
  ERROR: '오류',
} as const;

/**
 * 리소스 타입 상수
 */
export const RESOURCE_TYPES = {
  DATASET: '데이터세트',
  MODEL: '모델',
  AGENT: '에이전트',
  PROJECT: '프로젝트',
  USER: '사용자',
  FILE: '파일',
  API: 'API',
} as const;

/**
 * 검색 필터 옵션
 */
export const USER_ACTIVITY_SEARCH_FILTERS = [
  '사용자명',
  '프로젝트명',
  '역할명',
  '메뉴경로',
  '액션',
  '대상자산',
  '리소스타입',
  '결과',
] as const;

/**
 * 액션별 한글 표시명 매핑
 */
export const ACTION_LABELS: Record<string, string> = {
  create: '생성',
  update: '수정',
  delete: '삭제',
  read: '조회',
  download: '다운로드',
  upload: '업로드',
  deploy: '배포',
  login: '로그인',
  logout: '로그아웃',
};

/**
 * 결과별 한글 표시명 매핑
 */
export const RESULT_LABELS: Record<string, string> = {
  success: '성공',
  failure: '실패',
  error: '오류',
};

/**
 * 리소스 타입별 한글 표시명 매핑
 */
export const RESOURCE_TYPE_LABELS: Record<string, string> = {
  dataset: '데이터세트',
  model: '모델',
  agent: '에이전트',
  project: '프로젝트',
  user: '사용자',
  file: '파일',
  api: 'API',
};
