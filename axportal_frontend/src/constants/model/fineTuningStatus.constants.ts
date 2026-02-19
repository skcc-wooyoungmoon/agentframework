/**
 * 모델 파인튜닝 상태 관련 상수 정의
 * UILabelIntent와 매핑되는 상태값들을 관리
 */

// 상태값 매핑 상수
export const FINE_TUNING_STATUS_MAP = {
  // 초기화 상태
  initialized: {
    intent: 'neutral',
    label: '초기화',
  },
  starting: {
    intent: 'progress',
    label: '시작중',
  },
  stopping: {
    intent: 'stop',
    label: '중지중',
  },
  // 중지 상태
  stopped: {
    intent: 'neutral',
    label: '중지',
  },
  // 할당 중 상태
  progress: {
    intent: 'progress',
    label: '할당중',
  },
  // 할당 완료
  allocated: {
    intent: 'progress',
    label: '할당완료',
  },
  // 학습 중 상태
  training: {
    intent: 'progress',
    label: '학습중',
  },
  // 학습 완료 상태
  trained: {
    intent: 'complete',
    label: '학습완료',
  },
  error: {
    intent: 'error',
    label: '오류',
  },
} as const;
