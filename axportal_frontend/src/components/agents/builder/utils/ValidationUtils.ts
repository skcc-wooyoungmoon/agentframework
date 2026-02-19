// noinspection JSUnusedGlobalSymbols

import type { ValidationError } from '@/components/agents/builder/types/Validation.ts';
import keyTableData from '@/components/agents/builder/types/keyTableData.json';

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

export const validateRequiredInputs = (
  type: string,
  inputKeys: Array<{
    name: string;
    fixed_value: string | null;
    keytable_id: string | null;
  }>
): ValidationError[] => {
  if (type === 'condition') {
    const hasValidInput = inputKeys.some(inputKey => {
      const hasFixedValue = typeof inputKey.fixed_value === 'string' && inputKey.fixed_value.trim() !== '';
      const hasKeyTable = typeof inputKey.keytable_id === 'string' && inputKey.keytable_id.trim() !== '';
      return hasFixedValue || hasKeyTable;
    });

    if (hasValidInput) {
      return [];
    }

    return [
      {
        field: 'item',
        type: 'REQUIRED_FIELD',
        message: '조건에 사용할 입력을 하나 이상 지정해주세요.',
      },
    ];
  }

  const config = getNodeConfig(type);
  if (!config) return [];

  const errors: ValidationError[] = [];


  config.input_keys.forEach((configInput) => {
    if (configInput.required) {
      // 키 이름으로 매칭하여 검증
      const input = inputKeys.find(inputKey => inputKey.name === configInput.key);

      if (!input) {
        errors.push({
          field: configInput.key,
          type: 'REQUIRED_FIELD',
          message: `${configInput.key}을(를) 입력해주세요.`,
          details: {
            inputType: configInput.type,
            comment: configInput.comment,
          },
        });
        return;
      }

      // 빈 문자열도 null과 동일하게 처리
      const hasValidFixedValue = input?.fixed_value && typeof input.fixed_value === 'string' && input.fixed_value.trim() !== '';
      const hasValidKeytableId = input?.keytable_id && typeof input.keytable_id === 'string' && input.keytable_id.trim() !== '';

      // 필수 입력값이 실제로 입력되지 않았으면 에러 추가
      if (!hasValidFixedValue && !hasValidKeytableId) {
        errors.push({
          field: configInput.key,
          type: 'REQUIRED_FIELD',
          message: `${configInput.key}을(를) 입력해주세요.`,
          details: {
            inputType: configInput.type,
            comment: configInput.comment,
          },
        });
      }
    }
  });

  return errors;
};

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
          message: `중복된 이름: ${input.name}`,
          details: { inputIndex: index },
        });
      } else {
        namesInCurrentNode.add(input.name);
      }
    }
  });

  return errors;
};

export const hasAllRequiredInputs = (
  type: string,
  inputKeys: Array<{
    name: string;
    fixed_value: string | null;
    keytable_id: string | null;
  }>
): boolean => {
  if (type === 'condition') {
    return inputKeys.some(inputKey => {
      const hasFixedValue = typeof inputKey.fixed_value === 'string' && inputKey.fixed_value.trim() !== '';
      const hasKeyTable = typeof inputKey.keytable_id === 'string' && inputKey.keytable_id.trim() !== '';
      return hasFixedValue || hasKeyTable;
    });
  }

  const config = getNodeConfig(type);
  if (!config) return true;

  // 필수 입력값 검증: 실제로 값이 입력되었는지 확인
  return config.input_keys.every((configInput) => {
    if (!configInput.required) return true;
    
    // 키 이름으로 매칭하여 검증
    const input = inputKeys.find(inputKey => inputKey.name === configInput.key);
    
    // input이 없으면 필수값이 누락된 것으로 간주
    if (!input) {
      return false;
    }
    
    // fixed_value나 keytable_id 중 하나라도 유효한 값이 있으면 통과
    const hasValidFixedValue = input?.fixed_value && typeof input.fixed_value === 'string' && input.fixed_value.trim() !== '';
    const hasValidKeytableId = input?.keytable_id && typeof input.keytable_id === 'string' && input.keytable_id.trim() !== '';
    
    // 둘 중 하나라도 있으면 통과
    return hasValidFixedValue || hasValidKeytableId;
  });
};
