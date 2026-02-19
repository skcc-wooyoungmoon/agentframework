/**
 * @description 모델 카탈로그 목록 조회 요청 타입
 */
export type ModelCtlgListRequest = {
  page: number;
  size: number;
  sort?: string;
  filter?: string;
  search?: string;
  ids?: string;
  queryKey?: string;
};

export type ModelCtlgType = {
  id: string;
  displayName: string;
  name: string;
  type: string;
  description: string;
  size: string;
  tokenSize: string;
  inferenceParam: object | null;
  quantization: object | null;
  dtype: string;
  servingType: string;
  deployStatus: string;
  isPrivate: boolean;
  isValid: boolean;
  license: string;
  readme: string;
  path?: string;
  providerId: string;
  providerName: string;
  projectId: string;
  defaultParams: object | null;
  lastVersion: number;
  isCustom: boolean;
  customCodePath: string;
  languages: TagType[];
  tasks: TagType[];
  tags: TagType[];
  originTags?: TagType[];
  createdAt: string;
  updatedAt: string;
  trainingId?: string;
  createdBy: string;
  updatedBy: string;
  publicStatus?: string;
  endpointId?: string;

  // endpoint
  url: string;
  identifier: string;
  key: string;
};

export type TagType = {
  id: string;
  name: string;
  created_at: string;
  updated_at: string;
};

// 백엔드 DTO와 일치하는 태그 타입들
export type ModelLanguageType = {
  id?: number; // 백엔드는 Integer 타입
  name: string;
  created_at?: string; // LocalDateTime 형식
  updated_at?: string; // LocalDateTime 형식
};

export type ModelTaskType = {
  id?: number;
  name: string;
  created_at?: string;
  updated_at?: string;
};

export type ModelTagType = {
  id?: number;
  name: string;
  created_at?: string;
  updated_at?: string;
};

export type ModelEndpointType = {
  url: string; // 필수 필드
  identifier: string; // 필수 필드
  key: string; // 필수 필드
  description?: string;
};

/**
 * @description 모델 카탈로그 생성 요청 타입
 * 백엔드 CreateModelCtlgReq와 일치하도록 정의
 */
export type CreateModelCtlgRequest = {
  // 필수 필드
  displayName: string;
  name: string;
  providerId: string;

  // 선택적 필드
  id?: string;
  type?: string;
  description?: string;
  size?: string;
  tokenSize?: string;
  inferenceParam?: object;
  quantization?: object;
  dtype?: string;
  servingType?: string;
  isPrivate?: boolean;
  isValid?: boolean;
  license?: string;
  readme?: string;
  path?: string;
  projectId?: string;
  defaultParams?: object;
  lastVersion?: number;
  isCustom?: boolean;
  customCodePath?: string;
  modelGardenId: string;

  // 백엔드 DTO와 일치하는 태그 타입들 사용
  languages?: ModelLanguageType[];
  tasks?: ModelTaskType[];
  tags?: ModelTagType[];
  policy?: any[];

  // 엔드포인트는 필수 필드들이 모두 있어야 함
  endpoint?: ModelEndpointType;
};

export type GetModelProvidersResponse = ModelProviderType[];

export type ModelProviderType = {
  /**
   * 모델 제공자 ID
   */
  id: string;
  /**
   * 모델 제공자 이름
   */
  name: string;

  /**
   * 모델 제공자 설명
   */
  description: string;

  /**
   * 로고 이미지
   */
  logo: string;

  /**
   * 생성 일시
   */
  createdAt: string;

  /**
   * 수정 일시
   */
  updatedAt: string;
};

export type DeleteModelCtlgBulkRequest = {
  items: DeleteModelCtlgRequestItem[];
};

type DeleteModelCtlgRequestItem = {
  type: 'serverless' | 'self-hosting';
  id: string;
};
