import { api } from '@/configs/axios.config';

export interface SourceSystemInfo {
  value: string;
  label: string;
  description?: string;
}

/**
 * 원천 시스템 목록 조회
 */
export const getSourceSystems = async (): Promise<SourceSystemInfo[]> => {
  const response = await api.get<SourceSystemInfo[]>('/dataCtlg/source-system/list');
  return response.data;
};
