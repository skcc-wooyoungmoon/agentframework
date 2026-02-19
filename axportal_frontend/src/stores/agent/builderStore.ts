import { atom } from 'jotai';
import type { Node, Edge } from './types';
import { stringUtils } from '@/utils/common';

// 기본 상태 정의
export interface BuilderState {
  // 현재 빌더 상태
  nodes: Node[];
  edges: Edge[];
  selectedNodeId: string | null;
  selectedEdgeId: string | null;

  // 빌더 메타데이터
  agentName: string;
  agentDescription: string;
  lastModified: Date;

  // UI 상태
  isDragging: boolean;
  isConnecting: boolean;
  zoom: number;
  pan: { x: number; y: number };
}

// 기본 초기값
const initialState: BuilderState = {
  nodes: [],
  edges: [],
  selectedNodeId: null,
  selectedEdgeId: null,
  agentName: '',
  agentDescription: '',
  lastModified: new Date(),
  isDragging: false,
  isConnecting: false,
  zoom: 1,
  pan: { x: 0, y: 0 },
};

// Jotai atoms
export const nodesAtom = atom<Node[]>(initialState.nodes);

// nodesAtom을 설정할 때 position 검증을 위한 래퍼 함수
export const setNodesAtom = atom(null, (_get, set, newNodes: Node[]) => {
  // position 검증 및 기본값 설정
  const validatedNodes = newNodes.map(node => ({
    ...node,
    position:
      node.position &&
      typeof node.position.x === 'number' &&
      typeof node.position.y === 'number'
        ? node.position
        : { x: 0, y: 0 },
  }));
  set(nodesAtom, validatedNodes);
});
export const edgesAtom = atom<Edge[]>(initialState.edges);
export const selectedNodeIdAtom = atom<string | null>(
  initialState.selectedNodeId
);
export const selectedEdgeIdAtom = atom<string | null>(
  initialState.selectedEdgeId
);
export const agentNameAtom = atom<string>(initialState.agentName);
export const agentDescriptionAtom = atom<string>(initialState.agentDescription);
export const lastModifiedAtom = atom<Date>(initialState.lastModified);
export const isDraggingAtom = atom<boolean>(initialState.isDragging);
export const isConnectingAtom = atom<boolean>(initialState.isConnecting);
export const zoomAtom = atom<number>(initialState.zoom);
export const panAtom = atom<{ x: number; y: number }>(initialState.pan);

// 파생 atoms
export const selectedNodeAtom = atom(get => {
  const nodes = get(nodesAtom);
  const selectedNodeId = get(selectedNodeIdAtom);
  return nodes.find(node => node.id === selectedNodeId) || null;
});

export const selectedEdgeAtom = atom(get => {
  const edges = get(edgesAtom);
  const selectedEdgeId = get(selectedEdgeIdAtom);
  return edges.find(edge => edge.id === selectedEdgeId) || null;
});

// 액션 atoms
export const addNodeAtom = atom(
  null,
  (get, set, nodeData: Omit<Node, 'id'>) => {
    const newNode: Node = {
      ...nodeData,
      id: `node-${Date.now()}-${stringUtils.secureRandomString(9)}`,
    };
    set(nodesAtom, [...get(nodesAtom), newNode]);
    set(lastModifiedAtom, new Date());
  }
);

export const updateNodeAtom = atom(
  null,
  (get, set, { id, updates }: { id: string; updates: Partial<Node> }) => {
    const nodes = get(nodesAtom);
    set(
      nodesAtom,
      nodes.map(node => (node.id === id ? { ...node, ...updates } : node))
    );
    set(lastModifiedAtom, new Date());
  }
);

export const removeNodeAtom = atom(null, (get, set, id: string) => {
  const nodes = get(nodesAtom);
  const edges = get(edgesAtom);
  const selectedNodeId = get(selectedNodeIdAtom);

  set(
    nodesAtom,
    nodes.filter(node => node.id !== id)
  );
  set(
    edgesAtom,
    edges.filter(edge => edge.source !== id && edge.target !== id)
  );

  if (selectedNodeId === id) {
    set(selectedNodeIdAtom, null);
  }
  set(lastModifiedAtom, new Date());
});

export const addEdgeAtom = atom(
  null,
  (get, set, edgeData: Omit<Edge, 'id'>) => {
    const newEdge: Edge = {
      ...edgeData,
      id: `edge-${Date.now()}-${stringUtils.secureRandomString(9)}`,
    };
    set(edgesAtom, [...get(edgesAtom), newEdge]);
    set(lastModifiedAtom, new Date());
  }
);

export const updateEdgeAtom = atom(
  null,
  (get, set, { id, updates }: { id: string; updates: Partial<Edge> }) => {
    const edges = get(edgesAtom);
    set(
      edgesAtom,
      edges.map(edge => (edge.id === id ? { ...edge, ...updates } : edge))
    );
    set(lastModifiedAtom, new Date());
  }
);

export const removeEdgeAtom = atom(null, (get, set, id: string) => {
  const edges = get(edgesAtom);
  const selectedEdgeId = get(selectedEdgeIdAtom);

  set(
    edgesAtom,
    edges.filter(edge => edge.id !== id)
  );

  if (selectedEdgeId === id) {
    set(selectedEdgeIdAtom, null);
  }
  set(lastModifiedAtom, new Date());
});

export const updateAgentInfoAtom = atom(
  null,
  (_get, set, { name, description }: { name: string; description: string }) => {
    set(agentNameAtom, name);
    set(agentDescriptionAtom, description);
    set(lastModifiedAtom, new Date());
  }
);

export const resetBuilderAtom = atom(null, (_get, set) => {
  set(nodesAtom, initialState.nodes);
  set(edgesAtom, initialState.edges);
  set(selectedNodeIdAtom, initialState.selectedNodeId);
  set(selectedEdgeIdAtom, initialState.selectedEdgeId);
  set(agentNameAtom, initialState.agentName);
  set(agentDescriptionAtom, initialState.agentDescription);
  set(lastModifiedAtom, new Date());
  set(isDraggingAtom, initialState.isDragging);
  set(isConnectingAtom, initialState.isConnecting);
  set(zoomAtom, initialState.zoom);
  set(panAtom, initialState.pan);
});
