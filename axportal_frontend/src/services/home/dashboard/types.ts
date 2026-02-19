/**
 * 스케일링 그룹 목록 조회 요청 타입
 */
export type GetScalingGroupsRequest = {
  /** 활성화 여부 필터 (true: 활성화된 그룹만 조회, false: 비활성화된 그룹만 조회, undefined: 전체 조회) */
  isActive?: boolean;
};

/**
 * 스케일링 그룹 상세 정보
 */
export type ScalingGroup = {
  /** 스케일링 그룹 이름 */
  name: string;
  /** 스케일링 그룹 설명 */
  description: string;
  /** 활성화 상태 */
  isActive: boolean;
  /** 생성일시 */
  createdAt: string;
  /** 드라이버 타입 (예: "static") */
  driver: string;
  /** 드라이버 옵션 */
  driverOpts: Record<string, any>;
  /** 스케줄러 타입 (예: "fifo") */
  scheduler: string;
  /** 스케줄러 옵션 */
  schedulerOpts: Record<string, any>;
  /** 호스트 네트워크 사용 여부 */
  useHostNetwork: boolean;
  /** 웹소켓 프록시 주소 */
  wsproxyAddr: string;
  /** 웹소켓 프록시 API 토큰 */
  wsproxyApiToken: string;
  /** 상태별 총 자원 슬롯 현황 */
  agentTotalResourceSlotsByStatus: Record<string, any>;
  /** 에이전트 목록 */
  agentList?: Agent[];
};

/**
 * 에이전트 정보
 */
export type Agent = {
  /** 에이전트 고유 식별자 */
  id: string;
  /** 에이전트 네트워크 주소 */
  addr: string;
  /** 에이전트 현재 상태 */
  status: string;
  /** 소속 스케일링 그룹 */
  scalingGroup: string;
  /** 작업 스케줄링 가능 여부 */
  schedulable: boolean;
  /** 사용 가능한 자원 슬롯 (총 보유 자원량) */
  availableSlots: Record<string, any>;
  /** 현재 점유된 자원 슬롯 (현재 할당된 자원량) */
  occupiedSlots: Record<string, any>;
};

/**
 * GraphQL 오류 위치 정보
 */
export type ErrorLocation = {
  /** 라인 번호 */
  line: number;
  /** 컬럼 번호 */
  column: number;
};

/**
 * GraphQL 오류 정보
 */
export type Error = {
  /** 오류 메시지 */
  message: string;
  /** 오류 위치 정보 */
  locations: ErrorLocation[];
  /** 오류 경로 */
  path: string[];
};

/**
 * 스케일링 그룹 목록 조회 응답 타입
 */
export type GetScalingGroupsResponse = {
  /** 스케일링 그룹 목록 */
  scalingGroups: ScalingGroup[];
  /** GraphQL 오류 정보 */
  errors?: Error[];
};
