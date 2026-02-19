import { atom } from 'jotai';

// GPU 노드 정보 타입
export interface GpuNodeInfo {
  nodeName: string;
  nodeStatus: string;
  nodeData: any;
}

// 솔루션 정보 타입
export interface SolutionInfo {
  solutionName: string;
  namespace: string;
  solutionStatus: string;
  solutionData: any;
}

// 선택된 GPU 노드 정보를 저장하는 atom
export const selectedGpuNodeAtom = atom<GpuNodeInfo | null>(null);

// 선택된 솔루션 정보를 저장하는 atom
export const selectedSolutionAtom = atom<SolutionInfo | null>(null);

// 자원 관리 메인 페이지의 활성 탭을 저장하는 atom
export const resrcMgmtActiveTabAtom = atom<string>('Tab1');

// 솔루션명과 네임스페이스 매핑 타입
export interface SolutionNamespaceMap {
  [key: string]: {
    solutionName: string;
    namespace: string;
  };
}

// 솔루션명과 네임스페이스 매핑 데이터를 저장하는 atom
export const solutionNamespaceMapAtom = atom<SolutionNamespaceMap>({
  apiGateway: { solutionName: 'API G/W', namespace: 'ns-apigw-dev' },
  dAtumo: { solutionName: 'Datumo', namespace: 'ns-datumo-analytics' },
  adxp: { solutionName: 'ADXP', namespace: 'aiplatform' },
  portal: { solutionName: 'Portal', namespace: 'axportal' },
});

// Model Session 정보 타입
export interface ModelSession {
  modelName: string;
  projectId: string;
  servingId: string;
  sessionId: string;
  status: string;
  cpu_limit: number;
  cpu_request: number;
  cpu_usage: number;
  cpu_utilization: number;
  gpu_limit: number;
  gpu_request: number;
  gpu_usage: number;
  gpu_utilization: number;
  memory_limit: number;
  memory_request: number;
  memory_usage: number;
  memory_utilization: number | null;
  [key: string]: any; // 추가 필드 허용
}

// Model Sessions를 저장하는 atom
export const modelSessionsAtom = atom<ModelSession[]>([]);