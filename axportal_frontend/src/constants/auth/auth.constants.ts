/**
 * @description 기능 권한 상수 정의
 * 카테고리별로 그룹화된 구조
 * 각 카테고리 내에서 D열(기능설명)의 각 기능을 key로 하고, 각 기능의 {id, name}을 value로 하는 구조
 *
 * 사용 예시:
 * ```tsx
 * import { AUTH_KEY } from '@/constants/auth/functionAuth.constants';
 *
 * // 특정 기능의 권한 정보 조회
 * const authInfo = AUTH_KEY.DATA.DATASET_LIST_VIEW;
 * // { id: 'A020201', name: '데이터 카탈로그 조회' }
 * ```
 */

import { RUN_MODE_TYPES } from '../common/env.constants';

export const NO_SHOW_RUN_MODE: string[] = [RUN_MODE_TYPES.PROD, RUN_MODE_TYPES.E_DEV, RUN_MODE_TYPES.DEV];

export interface AuthInfo {
  id: string;
  name: string;
}

export interface AuthCategory {
  [key: string]: AuthInfo;
}

export interface AuthKey {
  MENU: AuthCategory;
  HOME: AuthCategory;
  DATA: AuthCategory;
  MODEL: AuthCategory;
  PROMPT: AuthCategory;
  AGENT: AuthCategory;
  EVAL: AuthCategory;
  DEPLOY: AuthCategory;
  ADMIN: AuthCategory;
}

/**
 * 기능 권한 키 (카테고리별)
 */
export const AUTH_KEY: AuthKey = {
  MENU: {
    ALL: { id: 'ALL', name: '모든 권한' },
    // 데이터
    DATA: { id: 'A020000', name: '데이터' },
    DATA_EXPLORE: { id: 'A020100', name: '데이터 탐색' },
    DATA_CATALOG: { id: 'A020200', name: '지식/학습 데이터 관리' },
    DATA_TOOLS: { id: 'A020300', name: '데이터 도구' },
    // 모델
    MODEL: { id: 'A030000', name: '모델' },
    MODEL_GARDEN: { id: 'A030100', name: '모델 탐색' },
    MODEL_CATALOG: { id: 'A030200', name: '모델 카탈로그' },
    FINE_TUNING: { id: 'A030300', name: '파인튜닝' },
    PLAYGROUND: { id: 'A030400', name: '플레이그라운드' },
    // 프롬프트
    PROMPT: { id: 'A040000', name: '프롬프트' },
    PROMPT_INFERENCE: { id: 'A040100', name: '추론 프롬프트' },
    PROMPT_FEW_SHOT: { id: 'A040200', name: '퓨샷' },
    PROMPT_GUARDRAIL: { id: 'A040300', name: '가드레일' },
    PROMPT_WORKFLOW: { id: 'A040400', name: '워크플로우' },
    // 에이전트
    AGENT: { id: 'A050000', name: '에이전트' },
    AGENT_BUILDER: { id: 'A050100', name: '빌더' },
    AGENT_TOOLS: { id: 'A050200', name: '도구' },
    AGENT_MCP: { id: 'A050300', name: 'MCP 서버' },
    // 평가
    EVAL: { id: 'A060000', name: '평가' },
    EVAL_MAIN: { id: 'A060100', name: '평가' },
    // 배포
    DEPLOY: { id: 'A070000', name: '배포' },
    DEPLOY_MODEL: { id: 'A070100', name: '모델 배포' },
    DEPLOY_AGENT: { id: 'A070200', name: '에이전트 배포' },
    DEPLOY_SAFETY_FILTER: { id: 'A070400', name: '세이프티 필터' },
    DEPLOY_API_KEY: { id: 'A070300', name: 'API KEY' },
    // 로그
    LOG: { id: 'A070000', name: '로그' },
    LOG_MODEL: { id: 'A070100', name: '모델 사용 로그' },
    LOG_AGENT: { id: 'A070200', name: '에이전트 사용 로그' },
    // 공지사항
    // 관리
    ADMIN: { id: 'A090000', name: '관리' },
    ADMIN_USER: { id: 'A090100', name: '사용자 관리' },
    ADMIN_PROJECT: { id: 'A090200', name: '프로젝트 관리' },
    ADMIN_RESOURCE: { id: 'A090300', name: '자원 관리' },
    ADMIN_USAGE_HISTORY: { id: 'A090400', name: '사용자 이용 현황' },
    ADMIN_NOTICE: { id: 'A090500', name: '공지사항 관리' },
    ADMIN_API_KEY: { id: 'A090600', name: 'API Key 관리' },
  },

  HOME: {
    IDE_MOVE: { id: 'A010101', name: 'IDE 이동' },
    PROJECT_CREATE: { id: 'A010201', name: '프로젝트 생성' },
  },

  DATA: {
    // 데이터 탐색
    RAW_DATA_VIEW: { id: 'A020101', name: '데이터 탐색 조회' },
    RAW_DATA_DETAIL_VIEW: { id: 'A020101', name: '데이터 탐색 조회' },
    // 지식/학습 데이터 관리
    DATASET_LIST_VIEW: { id: 'A020201', name: '지식/학습 데이터 조회' },
    DATASET_DETAIL_VIEW: { id: 'A020201', name: '지식/학습 데이터 조회' },
    KNOWLEDGE_LIST_VIEW: { id: 'A020201', name: '지식/학습 데이터 조회' },
    KNOWLEDGE_DETAIL_VIEW: { id: 'A020201', name: '지식/학습 데이터 조회' },
    DATA_CREATE: { id: 'A020202', name: '지식/학습 데이터 편집' },
    DATASET_CREATE: { id: 'A020202', name: '지식/학습 데이터 편집' },
    DATASET_UPDATE: { id: 'A020202', name: '지식/학습 데이터 편집' },
    DATASET_DELETE: { id: 'A020202', name: '지식/학습 데이터 편집' },
    KNOWLEDGE_CREATE: { id: 'A020202', name: '지식/학습 데이터 편집' },
    KNOWLEDGE_SETTING: { id: 'A020202', name: '지식/학습 데이터 편집' },
    KNOWLEDGE_DELETE: { id: 'A020202', name: '지식/학습 데이터 편집' },
    KNOWLEDGE_FILE_ADD: { id: 'A020202', name: '지식/학습 데이터 편집' },
    UNSTRUCTURED_REGISTER: { id: 'A020202', name: '지식/학습 데이터 편집' },
    UNSTRUCTURED_CHANGE_PUBLIC: { id: 'A020202', name: '지식/학습 데이터 편집' },
    // 데이터 도구
    PROCESSOR_LIST_VIEW: { id: 'A020301', name: '데이터 도구 조회' },
    PROCESSOR_DETAIL_VIEW: { id: 'A020301', name: '데이터 도구 조회' },
    //INGESTION_TOOL_LIST_VIEW: { id: 'A020301', name: '데이터 도구 조회' },
    //INGESTION_TOOL_DETAIL_VIEW: { id: 'A020301', name: '데이터 도구 조회' },
    // CUSTOM_SCRIPT_LIST_VIEW: { id: 'A020301', name: '데이터 도구 조회' },
    // CUSTOM_SCRIPT_DETAIL_VIEW: { id: 'A020301', name: '데이터 도구 조회' },
    VECTOR_DB_LIST_VIEW: { id: 'A020301', name: '데이터 도구 조회' },
    VECTOR_DB_DETAIL_VIEW: { id: 'A020303', name: '벡터 DB 상세조회' },
    // INGESTION_TOOL_CREATE: { id: 'A020302', name: '데이터 도구 편집' },
    // INGESTION_TOOL_UPDATE: { id: 'A020302', name: '데이터 도구 편집' },
    // INGESTION_TOOL_DELETE: { id: 'A020302', name: '데이터 도구 편집' },
    // CUSTOM_SCRIPT_CREATE: { id: 'A020302', name: '데이터 도구 편집' },
    // CUSTOM_SCRIPT_UPDATE: { id: 'A020302', name: '데이터 도구 편집' },
    // CUSTOM_SCRIPT_DELETE: { id: 'A020302', name: '데이터 도구 편집' },
    VECTOR_DB_CREATE: { id: 'A020302', name: '데이터 도구 편집' },
    VECTOR_DB_UPDATE: { id: 'A020302', name: '데이터 도구 편집' },
    VECTOR_DB_DELETE: { id: 'A020302', name: '데이터 도구 편집' },
  },

  MODEL: {
    // 모델 가든
    SELF_HOSTING_MODEL_SEARCH_ADD: { id: 'A030101', name: '반입 모델 검색' },
    SERVERLESS_MODEL_IMPORT: { id: 'A030102', name: '모델 반입' },
    SELF_HOSTING_MODEL_IMPORT: { id: 'A030102', name: '모델 반입' },
    SERVERLESS_MODEL_DETAIL_UPDATE: { id: 'A030104', name: 'serverless 모델 편집' },
    SERVERLESS_MODEL_DELETE: { id: 'A030104', name: 'serverless 모델 편집' },
    SELF_HOSTING_MODEL_DELETE: { id: 'A030105', name: 'self-hosting 모델 편집' },
    SERVERLESS_MODEL_LIST_VIEW: { id: 'A030106', name: '반입 모델 조회' },
    SERVERLESS_MODEL_DETAIL_VIEW: { id: 'A030106', name: '반입 모델 조회' },
    SELF_HOSTING_MODEL_LIST_VIEW: { id: 'A030106', name: '반입 모델 조회' },
    SELF_HOSTING_MODEL_DETAIL_VIEW: { id: 'A030106', name: '반입 모델 조회' },
    // 모델 카탈로그
    MODEL_CATALOG_LIST_VIEW: { id: 'A030201', name: '모델 조회' },
    MODEL_CATALOG_DETAIL_VIEW: { id: 'A030201', name: '모델 조회' },
    SERVERLESS_MODEL_REGISTER: { id: 'A030202', name: '모델 편집' },
    MODEL_CATALOG_UPDATE: { id: 'A030202', name: '모델 편집' },
    MODEL_CATALOG_DELETE: { id: 'A030202', name: '모델 편집' },
    MODEL_CATALOG_CHANGE_PUBLIC: { id: 'A030202', name: '모델 편집' },
    // 파인튜닝
    FINE_TUNING_LIST_VIEW: { id: 'A030301', name: '파인튜닝 조회' },
    FINE_TUNING_DETAIL_VIEW: { id: 'A030301', name: '파인튜닝 조회' },
    METRIC_VIEW_DETAIL_VIEW: { id: 'A030301', name: '파인튜닝 조회' },
    FINE_TUNING_CREATE: { id: 'A030302', name: '파인튜닝 편집' },
    FINE_TUNING_UPDATE: { id: 'A030302', name: '파인튜닝 편집' },
    FINE_TUNING_DELETE: { id: 'A030302', name: '파인튜닝 편집' },
    FINE_TUNING_CHANGE_PUBLIC: { id: 'A030302', name: '파인튜닝 편집' },
    // 플레이그라운드
    PLAYGROUND_USE: { id: 'A030401', name: '플레이그라운드 사용' },
  },

  PROMPT: {
    // 추론 프롬프트
    INFERENCE_PROMPT_LIST_VIEW: { id: 'A040101', name: '추론 프롬프트 조회' },
    INFERENCE_PROMPT_DETAIL_VIEW: { id: 'A040101', name: '추론 프롬프트 조회' },
    INFERENCE_PROMPT_CREATE: { id: 'A040102', name: '추론 프롬프트 편집' },
    INFERENCE_PROMPT_UPDATE: { id: 'A040102', name: '추론 프롬프트 편집' },
    INFERENCE_PROMPT_DELETE: { id: 'A040102', name: '추론 프롬프트 편집' },
    INFERENCE_PROMPT_CHANGE_PUBLIC: { id: 'A040102', name: '추론 프롬프트 편집' },
    // 퓨샷
    FEW_SHOT_LIST_VIEW: { id: 'A040201', name: '퓨샷 조회' },
    FEW_SHOT_VERSION_DEPLOY: { id: 'A040203', name: '퓨샷 버전 배포' },
    FEW_SHOT_CREATE: { id: 'A040202', name: '퓨샷 편집' },
    FEW_SHOT_UPDATE: { id: 'A040202', name: '퓨샷 편집' },
    FEW_SHOT_DELETE: { id: 'A040202', name: '퓨샷 편집' },
    FEW_SHOT_CHANGE_PUBLIC: { id: 'A040202', name: '퓨샷 편집' },
    // 가드레일
    GUARDRAIL_LIST_VIEW: { id: 'A040301', name: '가드레일 조회' },
    GUARDRAIL_DETAIL_VIEW: { id: 'A040301', name: '가드레일 조회' },
    GUARDRAIL_CREATE: { id: 'A040302', name: '가드레일 편집' },
    GUARDRAIL_UPDATE: { id: 'A040302', name: '가드레일 편집' },
    GUARDRAIL_DELETE: { id: 'A040302', name: '가드레일 편집' },
    GUARDRAIL_CHANGE_PUBLIC: { id: 'A040302', name: '가드레일 편집' },
    // 가드레일 프롬프트
    GUARDRAIL_PROMPT_LIST_VIEW: { id: 'A040303', name: '가드레일 프롬프트 조회' },
    GUARDRAIL_PROMPT_DETAIL_VIEW: { id: 'A040303', name: '가드레일 프롬프트 조회' },
    GUARDRAIL_PROMPT_CREATE: { id: 'A040304', name: '가드레일 프롬프트 편집' },
    GUARDRAIL_PROMPT_UPDATE: { id: 'A040304', name: '가드레일 프롬프트 편집' },
    GUARDRAIL_PROMPT_DELETE: { id: 'A040304', name: '가드레일 프롬프트 편집' },
    // 워크플로우
    WORKFLOW_LIST_VIEW: { id: 'A040401', name: '워크플로우 조회' },
    WORKFLOW_DETAIL_VIEW: { id: 'A040401', name: '워크플로우 조회' },
    WORKFLOW_CREATE: { id: 'A040402', name: '워크플로우 편집' },
    WORKFLOW_UPDATE: { id: 'A040402', name: '워크플로우 편집' },
    WORKFLOW_DELETE: { id: 'A040402', name: '워크플로우 편집' },
    WORKFLOW_CHANGE_PUBLIC: { id: 'A040402', name: '워크플로우 편집' },
  },

  AGENT: {
    // 빌더
    BUILDER_LIST_VIEW: { id: 'A050101', name: '빌더 조회' },
    BUILDER_DETAIL_VIEW: { id: 'A050101', name: '빌더 조회' },
    BUILDER_CANVAS_VIEW: { id: 'A050101', name: '빌더 조회' },
    AGENT_CREATE: { id: 'A050102', name: '빌더 편집' },
    BUILDER_UPDATE: { id: 'A050102', name: '빌더 편집' },
    BUILDER_DELETE: { id: 'A050102', name: '빌더 편집' },
    BUILDER_CANVAS_USE: { id: 'A050102', name: '빌더 편집' },
    BUILDER_CHANGE_PUBLIC: { id: 'A050102', name: '빌더 편집' },
    // 도구
    TOOL_LIST_VIEW: { id: 'A050201', name: 'Tools 조회' },
    TOOL_DETAIL_VIEW: { id: 'A050201', name: 'Tools 조회' },
    TOOL_CREATE: { id: 'A050202', name: 'Tools 편집' },
    TOOL_UPDATE: { id: 'A050202', name: 'Tools 편집' },
    TOOL_DELETE: { id: 'A050202', name: 'Tools 편집' },
    TOOL_CHANGE_PUBLIC: { id: 'A050202', name: 'Tools 편집' },
    // MCP 서버
    MCP_SERVER_LIST_VIEW: { id: 'A050301', name: 'MCP 서버 조회' },
    MCP_SERVER_DETAIL_VIEW: { id: 'A050301', name: 'MCP 서버 조회' },
    MCP_SERVER_CREATE: { id: 'A050302', name: 'MCP 서버 편집' },
    MCP_SERVER_UPDATE: { id: 'A050302', name: 'MCP 서버 편집' },
    MCP_SERVER_DELETE: { id: 'A050302', name: 'MCP 서버 편집' },
    MCP_SERVER_CHANGE_PUBLIC: { id: 'A050302', name: 'MCP 서버 편집' },
  },

  EVAL: {
    // 평가
    EVAL_LIST_VIEW: { id: 'A060101', name: '목록 조회' },
    EVAL_EXECUTE: { id: 'A060102', name: '평가하기' },
    EVAL_DETAIL_MOVE: { id: 'A060102', name: '평가하기' },
    RED_TEAMING_MOVE: { id: 'A060102', name: '평가하기' },
    USER_EVAL_MOVE: { id: 'A060102', name: '평가하기' },
  },

  DEPLOY: {
    // 모델 배포
    MODEL_DEPLOY_LIST_VIEW: { id: 'A070101', name: '모델 배포 조회' },
    MODEL_DEPLOY_DETAIL_VIEW: { id: 'A070101', name: '모델 배포 조회' },
    MODEL_DEPLOY_API_KEY_ISSUE: { id: 'A070101', name: '모델 배포 조회' },
    MODEL_DEPLOY_CREATE: { id: 'A070102', name: '모델 배포 편집' },
    MODEL_DEPLOY_UPDATE: { id: 'A070102', name: '모델 배포 편집' },
    MODEL_DEPLOY_CHANGE_PUBLIC: { id: 'A070102', name: '모델 배포 편집' },
    // 에이전트 배포
    AGENT_DEPLOY_LIST_VIEW: { id: 'A070201', name: '에이전트 배포 조회' },
    AGENT_DEPLOY_DETAIL_VIEW: { id: 'A070201', name: '에이전트 배포 조회' },
    AGENT_DEPLOY_VERSION_VIEW: { id: 'A070201', name: '에이전트 배포 조회' },
    AGENT_DEPLOY_API_KEY_ISSUE: { id: 'A070201', name: '에이전트 배포 조회' },
    AGENT_DEPLOY_CREATE: { id: 'A070202', name: '에이전트 배포 편집' },
    AGENT_DEPLOY_DELETE: { id: 'A070202', name: '에이전트 배포 편집' },
    AGENT_DEPLOY_UPDATE: { id: 'A070202', name: '에이전트 배포 편집' },
    AGENT_APP_DEPLOY_DELETE: { id: 'A070202', name: '에이전트 배포 편집' },
    AGENT_APP_DEPLOY_UPDATE: { id: 'A070202', name: '에이전트 배포 편집' },
    AGENT_DEPLOY_CHANGE_PUBLIC: { id: 'A070202', name: '에이전트 배포 편집' },
    // API Key
    DEPLOY_API_KEY_LIST_VIEW: { id: 'A070301', name: 'API Key 조회' },
    DEPLOY_API_KEY_DETAIL_VIEW: { id: 'A070301', name: 'API Key 조회' },
    // 세이프티 필터
    SAFETY_FILTER_LIST_VIEW: { id: 'A070401', name: '세이프티 필터 조회' },
    SAFETY_FILTER_DETAIL_VIEW: { id: 'A070401', name: '세이프티 필터 조회' },
    SAFETY_FILTER_CREATE: { id: 'A070402', name: '세이프티 필터 편집' },
    SAFETY_FILTER_UPDATE: { id: 'A070402', name: '세이프티 필터 편집' },
    SAFETY_FILTER_DELETE: { id: 'A070402', name: '세이프티 필터 편집' },
    SAFETY_FILTER_CHANGE_PUBLIC: { id: 'A070402', name: '세이프티 필터 편집' },
    // 운영 배포
    PRODUCT_DEPLOY_LIST_VIEW: { id: 'A070501', name: '운영 배포 조회' },
    PRODUCT_DEPLOY_CREATE: { id: 'A070502', name: '운영 배포' },
  },

  ADMIN: {
    // 사용자 관리
    USER_LIST_VIEW: { id: 'A090101', name: '사용자 목록 조회' },
    USER_DETAIL_VIEW: { id: 'A090101', name: '사용자 목록 조회' },
    USER_ROLE_UPDATE: { id: 'A090102', name: '사용자 역할 편집' },
    USER_ACCOUNT_ACTIVATE: { id: 'A090102', name: '사용자 역할 편집' },
    // 프로젝트 관리
    PROJECT_LIST_VIEW: { id: 'A090201', name: '프로젝트 목록 조회' },
    PROJECT_DETAIL_VIEW: { id: 'A090201', name: '프로젝트 목록 조회' },
    PROJECT_BASIC_INFO_UPDATE: { id: 'A090202', name: '프로젝트 편집' },
    ROLE_CREATE: { id: 'A090202', name: '프로젝트 편집' },
    ROLE_BASIC_INFO_UPDATE: { id: 'A090202', name: '프로젝트 편집' },
    ROLE_DELETE: { id: 'A090202', name: '프로젝트 편집' },
    ROLE_PERMISSION_ADD: { id: 'A090202', name: '프로젝트 편집' },
    ROLE_PERMISSION_DELETE: { id: 'A090202', name: '프로젝트 편집' },
    ROLE_MEMBER_ADD: { id: 'A090202', name: '프로젝트 편집' },
    ROLE_MEMBER_DELETE: { id: 'A090202', name: '프로젝트 편집' },
    PROJECT_MEMBER_ADD: { id: 'A090204', name: '프로젝트 구성원 편집' },
    PROJECT_MEMBER_DELETE: { id: 'A090204', name: '프로젝트 구성원 삭제' },
    PROJECT_DELETE: { id: 'A090203', name: '프로젝트 종료' },
    // 자원 관리
    PORTAL_RESOURCE_STATUS_VIEW: { id: 'A090301', name: '자원 현황 조회' },
    GPU_NODE_RESOURCE_STATUS_VIEW: { id: 'A090301', name: '자원 현황 조회' },
    GPU_NODE_RESOURCE_STATUS_DETAIL_VIEW: { id: 'A090301', name: '자원 현황 조회' },
    SOLUTION_NAMESPACE_RESOURCE_STATUS_VIEW: { id: 'A090301', name: '자원 현황 조회' },
    SOLUTION_NAMESPACE_RESOURCE_STATUS_DETAIL_VIEW: { id: 'A090301', name: '자원 현황 조회' },
    // 사용자 이용 현황
    USAGE_HISTORY_LIST_VIEW: { id: 'A090401', name: '사용자 이용 현황 조회' },
    USAGE_HISTORY_DETAIL_VIEW: { id: 'A090401', name: '사용자 이용 현황 조회' },
    USAGE_STATISTICS_VIEW: { id: 'A090401', name: '사용자 이용 현황 조회' },
    USAGE_STATISTICS_DETAIL_VIEW: { id: 'A090401', name: '사용자 이용 현황 조회' },
    USAGE_STATISTICS_DOWNLOAD: { id: 'A090401', name: '사용자 이용 현황 조회' },
    // 공지사항 관리
    NOTICE_LIST_VIEW: { id: 'A090501', name: '공지사항 조회' },
    NOTICE_DETAIL_VIEW: { id: 'A090501', name: '공지사항 조회' },
    NOTICE_CREATE: { id: 'A090502', name: '공지사항 편집' },
    NOTICE_UPDATE: { id: 'A090502', name: '공지사항 편집' },
    NOTICE_DELETE: { id: 'A090502', name: '공지사항 편집' },
    // API Key 관리
    API_KEY_LIST_VIEW: { id: 'A090601', name: 'API Key 조회' },
    API_KEY_DETAIL_VIEW: { id: 'A090601', name: 'API Key 조회' },
    API_KEY_QUOTA_UPDATE: { id: 'A090602', name: 'API Key 편집' },
    API_KEY_DEACTIVATE: { id: 'A090602', name: 'API Key 편집' },
    API_KEY_DELETE: { id: 'A090602', name: 'API Key 편집' },
  },
} as const;

/**
 * 모든 기능 권한을 평면적으로 조회할 수 있는 맵 (하위 호환성)
 */
export const AUTH_FLAT_KEY: Record<string, AuthInfo> = Object.values(AUTH_KEY).reduce((acc, category) => ({ ...acc, ...category }), {} as Record<string, AuthInfo>);

/**
 * 모든 기능 권한 ID 목록
 */
export const AUTH_IDS = Object.keys(AUTH_FLAT_KEY) as string[];

/**
 * 모든 기능 권한 정보 목록
 */
export const AUTH_LIST = Object.values(AUTH_FLAT_KEY) as AuthInfo[];
