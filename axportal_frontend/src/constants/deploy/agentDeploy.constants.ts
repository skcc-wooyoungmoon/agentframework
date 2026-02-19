export const AGENT_DEPLOY_STATUS = {
  Available: {
    label: '이용가능',
    intent: 'complete',
  },
  Deploying: {
    label: '배포중',
    intent: 'progress',
  },
  Failed: {
    label: '실패',
    intent: 'error',
  },
  Error: {
    label: '실패',
    intent: 'error',
  },
  Stopped: {
    label: '중지',
    intent: 'stop',
  },
  Deleting: {
    label: '삭제중',
    intent: 'progress',
  },
  Terminated: {
    label: '종료',
    intent: 'stop',
  },
  Unknown: {
    label: '알 수 없음',
    intent: 'gray',
  },
};

/**
 * Agent Builder 배포 상태 상수
 */
export const AGENT_BUILDER_DEPLOY_STATUS = {
  LOADING: '', // API 조회 전까지 보여줄 공백 값
  NOT_DEPLOYED: '미배포',
  DEV_DEPLOYED: '개발배포',
  BOTH_DEPLOYED: '개발, 운영배포', // 운영배포된 에이전트는 개발배포에서 삭제 불가
} as const;

export type AgentBuilderDeployStatus = (typeof AGENT_BUILDER_DEPLOY_STATUS)[keyof typeof AGENT_BUILDER_DEPLOY_STATUS];
