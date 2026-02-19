export interface ApiKeyInfo {
  id: string;
  name: string;
  description: string;
  createdAt: string;
  updatedAt: string;
  projectName: string;
  type: 'user' | 'agent' | 'etc';
  authTo: string;
  apiKey: string;
  isActive: boolean;
  expiresAt?: string;
}

export interface ApiKeySearchValues {
  page: number;
  size: number;
  dateType: string;
  dateRange: {
    startDate: string;
    endDate: string;
  };
  searchKeyword: string;
  type: {
    value: string;
    label: string;
  };
  status: string;
}

export interface ApiKeyCreateRequest {
  name: string;
  description?: string;
  type: 'user' | 'agent' | 'etc';
  authTo: string;
  expiresAt?: string;
}

export interface ApiKeyUpdateRequest {
  name?: string;
  description?: string;
  isActive?: boolean;
  expiresAt?: string;
}
