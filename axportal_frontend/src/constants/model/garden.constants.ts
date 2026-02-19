/**
 * 모델 반입 상태 타입
 */
export const MODEL_GARDEN_STATUS_TYPE = {
  PENDING: 'PENDING',
  IMPORT_REQUEST: 'IMPORT_REQUEST',
  VACCINE_SCAN_COMPLETED: 'VACCINE_SCAN_COMPLETED',
  INTERNAL_NETWORK_IMPORT_COMPLETED: 'INTERNAL_NETWORK_IMPORT_COMPLETED',
  VULNERABILITY_CHECK_COMPLETED: 'VULNERABILITY_CHECK_COMPLETED',
  IMPORT_FAILED: 'IMPORT_FAILED',
  VULNERABILITY_CHECK_APPROVAL_IN_PROGRESS: 'VULNERABILITY_CHECK_APPROVAL_IN_PROGRESS',
  VULNERABILITY_CHECK_APPROVAL_REJECTED: 'VULNERABILITY_CHECK_APPROVAL_REJECTED',
  IMPORT_COMPLETED: 'IMPORT_COMPLETED',
  IMPORT_COMPLETED_REGISTERED: 'IMPORT_COMPLETED_REGISTERED', // 반입완료 + 카탈로그 등록 상태
  IMPORT_COMPLETED_UNREGISTERED: 'IMPORT_COMPLETED_UNREGISTERED', // 반입완료 + 카탈로그 삭제 상태
} as const;

/**
 * 모델 반입 상태 라벨 처리
 */
export const MODEL_GARDEN_STATUS = {
  PENDING: { value: 'stop', label: '반입전', detail: '반입전' }, // 이용불가 // BEFORE
  IMPORT_REQUEST: { value: 'progress', label: '반입중', detail: '반입 요청 완료' }, // 반입요청
  FILE_IMPORT_COMPLETED: { value: 'progress', label: '반입중', detail: '파일 검사 준비' }, // 파일 반입 완료
  VACCINE_SCAN_COMPLETED: { value: 'progress', label: '반입중', detail: '백신검사 완료' }, // 백신검사 완료
  INTERNAL_NETWORK_IMPORT_COMPLETED: { value: 'progress', label: '반입중', detail: '내부망반입 완료' }, // 내부망반입 완료
  VULNERABILITY_CHECK_COMPLETED: { value: 'progress', label: '반입중', detail: '취약점점검 완료' }, // 취약점점검 완료
  IMPORT_FAILED: { value: 'error', label: '이용불가', detail: '반입실패' }, // 반입실패 // BEFORE
  VULNERABILITY_CHECK_APPROVAL_IN_PROGRESS: { value: 'progress', label: '결재중', detail: '취약점점검 결재중' }, // 취약점점검 결재 중
  VULNERABILITY_CHECK_APPROVAL_REJECTED: { value: 'error', label: '이용불가', detail: '취약점점검 결재반려' }, // 취약점점검 결재 반려
  IMPORT_COMPLETED: { value: 'complete', label: '이용가능', detail: '반입완료' }, // 반입완료
  IMPORT_COMPLETED_REGISTERED: { value: 'complete', label: '이용가능', detail: '반입완료' }, // 반입완료 + 카탈로그 등록 상태
  IMPORT_COMPLETED_UNREGISTERED: { value: 'stop', label: '반입전', detail: '반입전' }, // 반입완료 + 카탈로그 삭제 상태
};

/**
 * 모델 반입 상태 옵션
 */
export const MODEL_GARDEN_STATUS_OPTIONS = [
  { value: 'all', label: '전체' },
  { value: 'BEFORE', label: '반입전' },
  { value: 'COMPLETE', label: '이용가능' },
  { value: 'PROGRESS', label: '반입중' },
  { value: 'VULNERABILITY_CHECK_APPROVAL_IN_PROGRESS', label: '결재중' },
  { value: 'VULNERABILITY_CHECK_APPROVAL_REJECTED', label: '이용불가' },
];

/**
 * 모델 반입 버튼 상태
 */
export const MODEL_GARDEN_BUTTON_STATUS = {
  // 반입전, 반입요청 결재반려
  // PENDING
  // 반입 요청, enabled
  PENDING: { label: '반입 요청', disabled: false, action: 'REQ_IN' }, // 반입전

  // 반입요청 결재중, 반입요청 결재완료, 백신검사 완료, 내부망반입 완료
  // IMPORT_REQUEST_APPROVAL_IN_PROGRESS
  // 반입 요청, disabled
  IMPORT_REQUEST: { label: '반입 요청', disabled: true, action: 'NONE' }, // 반입요청
  FILE_IMPORT_COMPLETED: { label: '반입 요청', disabled: true, action: 'NONE' }, // 파일 반입 완료
  VACCINE_SCAN_COMPLETED: { label: '반입 요청', disabled: true, action: 'NONE' }, // 백신검사 완료
  INTERNAL_NETWORK_IMPORT_COMPLETED: { label: '반입 요청', disabled: true, action: 'NONE' }, // 내부망반입 완료

  // 취약점점검 완료, 취약점점검 결재 반려
  // VULNERABILITY_CHECK_COMPLETED, VULNERABILITY_CHECK_APPROVAL_REJECTED
  // 취약점점검 결재요청, enabled
  VULNERABILITY_CHECK_COMPLETED: { label: '최종 반입 결재요청', disabled: false, action: 'REQ_VUL_APPROVAL' }, // 취약점점검 완료
  VULNERABILITY_CHECK_APPROVAL_REJECTED: { label: '최종 반입 결재요청', disabled: false, action: 'REQ_VUL_APPROVAL' }, // 취약점점검 결재반려

  // 취약점점검 결재중
  // VULNERABILITY_CHECK_APPROVAL_IN_PROGRESS
  // 취약점점검 결재요청, disabled
  VULNERABILITY_CHECK_APPROVAL_IN_PROGRESS: { label: '최종 반입 결재요청', disabled: true, action: 'NONE' }, // 취약점점검 결재중

  // 반입완료 + 카탈로그 등록 상태
  // IMPORT_COMPLETED_REGISTERED
  // 반입 요청, disabled
  IMPORT_COMPLETED_REGISTERED: { label: '반입 요청', disabled: true, action: 'NONE' }, // 반입완료 + 카탈로그 등록 상태

  // 반입완료 + 카탈로그 삭제 상태
  // IMPORT_COMPLETED_UNREGISTERED
  // 반입 요청, enabled
  IMPORT_COMPLETED_UNREGISTERED: { label: '반입 요청', disabled: false, action: 'RE_IMPORT' }, // 반입완료 + 카탈로그 삭제 상태

  // 반입실패
  // IMPORT_FAILED
  // 반입 재시도, enabled
  IMPORT_FAILED: { label: '반입 재시도', disabled: false, action: 'RE_IMPORT' }, // 반입실패
};

export const VULNERABILITY_CHECK_STATUS = {
  VULNERABILITY_CHECK_COMPLETED: { label: '결재전', value: 'stop' },
  VULNERABILITY_CHECK_APPROVAL_IN_PROGRESS: { label: '결재중', value: 'progress' },
  IMPORT_COMPLETED_REGISTERED: { label: '승인', value: 'complete' },
  VULNERABILITY_CHECK_APPROVAL_REJECTED: { label: '반려', value: 'error' },
};
