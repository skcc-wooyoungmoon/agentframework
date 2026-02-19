import { atom, useAtom } from 'jotai';
import type { DeployData } from './types';

// 초기 데이터
const initialDeployData: DeployData = {
  cpuLimit: 2,
  cpuRequest: 1,
  description: '',
  gpuLimit: 0,
  gpuRequest: 0,
  maxReplicas: 1,
  memLimit: 4,
  memRequest: 2,
  minReplicas: 1,
  name: '',
  servingType: 'standalone',
  targetId: '',
  targetType: 'agent_graph',
  versionDescription: '',
  workersPerCore: 3,
  safetyFilterOptions: {
    safety_filter_input: false,
    safety_filter_output: false,
    safety_filter_input_groups: [],
    safety_filter_output_groups: [],
  },
};

export interface SafetyFilterOptions {
  safety_filter_input?: boolean;
  safety_filter_output?: boolean;
  safety_filter_input_groups?: string[];
  safety_filter_output_groups?: string[];
}

// 배포 데이터 atom
export const deployDataAtom = atom<DeployData>(initialDeployData);

// 배포 데이터 업데이트 atom
export const updateDeployDataAtom = atom(null, (get, set, update: Partial<DeployData>) => {
  const currentData = get(deployDataAtom);
  set(deployDataAtom, { ...currentData, ...update });
});

// 배포 데이터 초기화 atom
export const resetDeployDataAtom = atom(null, (_, set) => {
  // 깊은 복사로 초기화하여 참조 문제 방지
  set(deployDataAtom, {
    ...initialDeployData,
    safetyFilterOptions: {
      ...initialDeployData.safetyFilterOptions,
    },
  });
});

// 커스텀 훅
export const useDeployAgent = () => {
  const [deployData, setDeployData] = useAtom(deployDataAtom);
  const [, updateDeployData] = useAtom(updateDeployDataAtom);
  const [, resetDeployData] = useAtom(resetDeployDataAtom);

  return {
    deployData,
    setDeployData,
    updateDeployData,
    resetDeployData,
    // atom에서 직접 값을 가져와서 항상 최신 값 반환
    getFinalDeployData: () => {
      // atom에서 직접 값을 가져와서 항상 최신 값 반환
      // deployData는 클로저로 인해 오래된 값일 수 있으므로, atom에서 직접 읽어옴
      return deployData;
    },
  };
};
