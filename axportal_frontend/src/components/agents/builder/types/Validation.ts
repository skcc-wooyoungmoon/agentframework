export type ValidationErrorType =
  | 'DUPLICATE_NAME' // 노드 이름 중복
  | 'DUPLICATE_INPUT' // 입력 키 중복
  | 'REQUIRED_FIELD' // 필수 필드 누락
  | 'REQUIRED_INPUT_VALUE' // 필수 입력값 누락
  | 'MISSING_CONNECTION';

export interface ValidationError {
  field?: string;
  type: ValidationErrorType;
  message: string;
  details?: any;
}

export interface NodeValidation {
  nodeId: string;
  isValid: boolean;
  errors: ValidationError[];
  validationType?: 'node' | 'input';
}
