export const MODEL_DEPLOY_STATUS = {
  Available: {
    label: '이용가능',
    intent: 'complete',
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
  Deploying: {
    label: '배포중',
    intent: 'progress',
  },
  Scaling: {
    label: '스케일링',
    intent: 'progress',
  },
  Updating: {
    label: '수정중',
    intent: 'progress',
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
    intent: 'neutral',
  },
};

export const MODEL_DEPLOY_PROVIDER = {
  Google: 'ic-model-24-google',
  META: 'ic-model-24-meta',
  Microsoft: 'ic-model-24-microsoft',
  OpenAI: 'ic-model-24-openai',
  Anthropic: 'ic-model-24-anthropic',
  GIP: 'ic-model-24-gip',
  Huggingface: 'ic-model-24-hugging-face',
  'SK Telecom': 'ic-model-24-ax',
  Etc: 'ic-model-24-etc',
};

export const MODEL_DEPLOY_RESOURCE_MAX_VALUE = {
  CPU: 8,
  MEMORY: 16,
};
