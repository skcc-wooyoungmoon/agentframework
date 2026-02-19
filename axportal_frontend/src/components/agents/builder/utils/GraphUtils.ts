// noinspection JSUnusedGlobalSymbols

import { type CustomNode, type InputKeyItem, NodeType, type OutputKeyItem } from '@/components/agents/builder/types/Agents';
import keyTableData from '@/components/agents/builder/types/keyTableData.json';
import type { Node } from '@xyflow/react';
import { v4 as uuidv4 } from 'uuid';

type KeyTableDataKeys = keyof typeof keyTableData;

export const getNodeTypeById = (nodes: Node[], nodeId: string): string | null => {
  const node = nodes.find(n => n.id === nodeId);
  return node?.type || 'default';
};

export const EDGE_TYPE_STRAIGHT = 'buttonEdgeStraight';
export const EDGE_TYPE_CURVE = 'buttonEdgeCurve';
export const EDGE_TYPE_SMOOTH = 'buttonEdgeSmoothStep';

export const getNodeId = () => uuidv4().slice(0, 8);
export const getEdgeId = () => uuidv4().slice(0, 8);

export const getNodeTitleByName = (name: string): string => {
  const node = Object.values(NodeType).find(node => node.name === name);
  return node?.title ?? 'default';
};

const isNameTaken = (name: string, currentNodes: CustomNode[]): boolean => {
  return currentNodes.some(node => node.data.name === name);
};

export const generateNodeName = (type: string, currentNodes: CustomNode[]): string => {
  let baseName = type;
  let index = 1;
  let newName = `${baseName}_${index}`;

  while (isNameTaken(newName, currentNodes)) {
    index++;
    newName = `${baseName}_${index}`;
  }

  return newName;
};

const isValidNodeType = (type: string): type is KeyTableDataKeys => {
  return type in keyTableData;
}

export const setupNodeData = (type: string, nodeName: string, nodeId: string) => {
  if (!isValidNodeType(type)) {
    // console.error(`Error: "${type}" 이 유효하지 않습니다.`);
    return { name: nodeName };
  }

  const typeConfig = keyTableData[type];

  const nodeData: {
    name: string;
    input_keys?: InputKeyItem[];
    output_keys?: OutputKeyItem[];
    code?: string;
    description?: string;
    release_version?: string;
  } = {
    name: nodeName,
  };

  if (typeConfig.input_keys && typeConfig.input_keys.length > 0) {
    nodeData.input_keys = typeConfig.input_keys.map(input => ({
      name: input.key,
      required: input.required,
      keytable_id: type === NodeType.Input.name ? `${input.key}_${nodeId}` : '',
      fixed_value: null,
    }));
  }

  if (typeConfig.output_keys && typeConfig.output_keys.length > 0) {
    nodeData.output_keys = typeConfig.output_keys.map(output => ({
      name: output.key,
      keytable_id: `${output.key}_${nodeId}`,
    }));
  }

  // Code 노드의 기본 코드 설정
  if (type === NodeType.AgentCoder.name && (typeConfig as any).field_default?.code) {
    nodeData.code = (typeConfig as any).field_default.code;
  }

  // 기본 설명 설정
  if ((typeConfig as any).field_default?.description) {
    nodeData.description = (typeConfig as any).field_default.description;
  }

  // agent__generator 노드에 release_version 및 variables 기본값 설정
  if (type === NodeType.AgentGenerator.name) {
    nodeData.release_version = 'latest';
    (nodeData as any).variables = []; // SKT AI Platform Python 호환성
  }

  // 🔥 output__chat, output__formatter, output__selector 노드에 format_string 기본값 설정
  // 타입 단언 사용: keyTableData에 없을 수 있지만 실제로는 사용되는 노드 타입
  const nodeType = type as string;
  if (nodeType === 'output__chat' || nodeType === 'output__formatter' || nodeType === 'output__selector') {
    // 기본값으로 공백 하나 설정 (나중에 연결된 노드의 output을 참조하여 자동 업데이트됨)
    (nodeData as any).format_string = ' ';
  }

  return nodeData;
};
// 🔥 샘플 프로젝트 방식: isRun, isDone, isError만 사용하여 클래스 반환
// CSS 클래스명을 반환하도록 수정 (agent-card--running, agent-card--completed, agent-card--error)
// 우선순위: isError(빨강) > isDone(파랑) > isRun(연두)
export const getNodeStatus = (
  is_run: boolean,
  is_done: boolean | undefined,
  is_error: boolean | undefined
): string | null => {
  // 🔥 에러 상태가 최우선 (빨간색)
  if (is_run === true && typeof is_error !== 'undefined' && is_error === true) {
    return 'agent-card--error'; // 에러 (빨간색)
  }

  // 🔥 완료 상태 (파란색)
  if (is_run === true && typeof is_done !== 'undefined' && is_done === true) {
    return 'agent-card--completed'; // 완료 (파란색)
  }

  // 🔥 실행 중 상태 (연두색)
  if (is_run === true && (typeof is_done === 'undefined' || is_done === false)) {
    return 'agent-card--running'; // 실행 중 (연두색)
  }

  return null;
};
