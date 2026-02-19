// noinspection JSUnusedGlobalSymbols

import type { ValidationError } from '@/components/builder/types/Validation.ts';
import keyTableData from '@/components/builder/types/keyTableData.json';
import { type ConditionItem } from '@/components/builder/types/Agents';
import _ from 'lodash';

interface NodeConfigJSON {
  input_keys: Array<{
    key: string;
    comment: string;
    required: boolean;
    type: string;
  }>;
  output_keys: Array<{
    key: string;
    comment: string;
    required: boolean;
    type: string;
  }>;
}

interface NodeConfigMap {
  [key: string]: NodeConfigJSON;
}

// 임시 json 값. 추후 교체 필요
let nodeConfigJSON: NodeConfigMap = keyTableData;

// 추후 app.tsx 등에서 json 초기화하기 위한 함수
export const initNodeConfig = (json: NodeConfigMap) => {
  nodeConfigJSON = json;
};

export const getNodeConfig = (type: string): NodeConfigJSON | undefined => {
  if (!nodeConfigJSON) return undefined;
  return nodeConfigJSON[type];
};

// 입력 필드 검증
export const validateRequiredInputs = (
  type: string,
  inputKeys: Array<{
    name: string;
    fixed_value: string | null;
    keytable_id: string | null;
    required: boolean;
  }>,
  keyTableList?: Array<{ id: string }>
): ValidationError[] => {
  const config = getNodeConfig(type);
  if (!config) return [];

  const errors: ValidationError[] = [];

  config.input_keys.forEach((configInput, index) => {
    if(type === 'agent__generator' && configInput.key === 'context'){
      return;
    }
    if (configInput.required) {
      const input = inputKeys[index];
      const hasFixedValue = input?.fixed_value && input.fixed_value.trim() !== '';
      const hasKeyTableId = input?.keytable_id && input.keytable_id.trim() !== '';
      
      // keytable_id가 있지만 keyTableList에 존재하지 않는 경우 체크
      let hasValidKeyTable = false;
      if (hasKeyTableId && keyTableList) {
        hasValidKeyTable = keyTableList.some(entry => entry.id === input.keytable_id);
      } else if (hasKeyTableId && !keyTableList) {
        // keyTableList가 제공되지 않은 경우 keytable_id만 확인
        hasValidKeyTable = true;
      }
      
      if (!hasFixedValue && !hasValidKeyTable) {
        errors.push({
          field: configInput.key,
          type: 'REQUIRED_FIELD',
          message: `${configInput.key}을(를) 입력해주세요.`,
          details: {
            inputIndex: index,
            inputType: configInput.type,
            comment: configInput.comment,
          },
        });
      }
    }
  });

  // tool 노드의 경우 inputKeys 별도 검사
  if (type === 'tool') {
    const toolInputKeys = inputKeys.filter(inputKey => inputKey.required);
    toolInputKeys.forEach((inputKey, index) => {
      const hasFixedValue = inputKey.fixed_value && inputKey.fixed_value.trim() !== '';
      const hasKeyTableId = inputKey.keytable_id && inputKey.keytable_id.trim() !== '';
      
      // keytable_id가 있지만 keyTableList에 존재하지 않는 경우 체크
      let hasValidKeyTable = false;
      if (hasKeyTableId && keyTableList) {
        hasValidKeyTable = keyTableList.some(entry => entry.id === inputKey.keytable_id);
      } else if (hasKeyTableId && !keyTableList) {
        // keyTableList가 제공되지 않은 경우 keytable_id만 확인
        hasValidKeyTable = true;
      }
      
      if (!hasFixedValue && !hasValidKeyTable) {
        errors.push({
          field: toolInputKeys[index].name,
          type: 'REQUIRED_FIELD',
          message: `${toolInputKeys[index].name}을(를) 입력해주세요.`,
          details: {
            inputIndex: index,
            inputType: 'str',
            comment: 'Tool 입력값',
          },
        });
      }
    });
  }

  return errors;
};

// 입력 필드 중복 검증
export const validateDuplicateInputs = (type: string, inputKeys: Array<{ name: string }>): ValidationError[] => {
  const config = getNodeConfig(type);
  if (!config) return [];

  const errors: ValidationError[] = [];
  const namesInCurrentNode = new Set<string>();
  const fixedInputKeys = config.input_keys.map(input => input.key);
  const fixedInputCount = config.input_keys.length;

  inputKeys.forEach((input, index) => {
    if (index >= fixedInputCount && input.name) {
      if (fixedInputKeys.includes(input.name)) {
        errors.push({
          field: input.name,
          type: 'DUPLICATE_INPUT',
          message: `'${input.name}'은 이미 사용중인 이름입니다.`,
          details: { inputIndex: index },
        });
      } else if (namesInCurrentNode.has(input.name)) {
        errors.push({
          field: input.name,
          type: 'DUPLICATE_INPUT',
          message: `'${input.name}'은 이미 사용중인 이름입니다.`,
          details: { inputIndex: index },
        });
      } else {
        namesInCurrentNode.add(input.name);
      }
    }
  });

  return errors;
};

export const validateRequiredConditions = (conditions: Array<ConditionItem>): ValidationError[] => {
  const errors: ValidationError[] = [];

  conditions.forEach((condition, index) => {
    if (condition.value.required) {
      const input_key = condition.input_key;
      const condition_value = condition.value;
      if (!condition_value?.fixed_value && !condition_value?.keytable_id) {
        errors.push({
          field: input_key.name,
          type: 'REQUIRED_FIELD',
          message: `${input_key.name} 비교값을 입력해주세요.`,
          details: { inputIndex: index },
        });
      }
    }
  });

  return errors;
};

export const hasAllRequiredInputs = (type: string, inputKeys: Array<{ name: string; fixed_value: string | null; keytable_id: string | null }>): boolean => {
  const config = getNodeConfig(type);
  if (!config) return true;

  return config.input_keys.every((configInput, index) => {
    // agent__generator의 context 필드는 검증 skip (삭제는 불가능하지만 입력 필수는 아님)
    if (type === 'agent__generator' && configInput.key === 'context') {
      return true; // 항상 통과
    }
    if (!configInput.required) return true;
    const input = inputKeys[index];
    return input?.fixed_value || input?.keytable_id;
  });
};

// 노드 내 필수입력 필드 체크
export const hasAllRequiredData = (type: string, data: any): boolean => {
  // Categorizer, Reviewer, Generator 노드
  if (type === 'agent__categorizer' || type === 'agent__reviewer' || type === 'agent__generator') {
    const requiredData = data?.serving_model || '';
    return !_.isEmpty(requiredData);
  }
  // Rewriter HyDE, Rewriter MultiQuery 노드
  if (type === 'retriever__rewriter_hyde' || type === 'retriever__rewriter_multiquery') {
    const requiredData = data?.query_rewriter?.llm_chain?.llm_config?.api_key || '';
    return !_.isEmpty(requiredData);
  }
  // Compressor, Doc Filter
  if (type === 'retriever__doc_compressor' || type === 'retriever__doc_filter') {
    const requiredData = data?.context_refiner?.llm_chain?.llm_config?.api_key || '';
    return !_.isEmpty(requiredData);
  }
  // Doc ReRanker 노드
  if (type === 'retriever__doc_reranker') {
    const requiredData = data?.context_refiner?.rerank_cnf?.model_info?.api_key || '';
    return !_.isEmpty(requiredData);
  }
  // Agent App 노드
  if (type === 'agent__app') {
    const requiredData = data?.agent_app_id || '';
    return !_.isEmpty(requiredData);
  }
  // Code 노드
  if (type === 'agent__coder') {
    const requiredData = data?.code || '';
    return !_.isEmpty(requiredData);
  }
  // Retriever 노드
  if (type === 'retriever__knowledge') {
    const requiredData = data?.knowledge_retriever?.repo_id || '';
    return !_.isEmpty(requiredData);
  }
  // Tool 노드
  if (type === 'tool') {
    const requiredData = data?.tool_id || '';
    return !_.isEmpty(requiredData);
  }

  // 체크할 노드 타입이 아닌 경우 true 반환
  return true;
};
