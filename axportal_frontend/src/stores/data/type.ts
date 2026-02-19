import type { UploadFilesResponseItem } from '@/services/data/types';

export type DataType = 'basicDataSet' | 'customDataSet';

export type TypeDataLocale = 'storage' | 'local' | 'none';

export interface DataState {
  dataType: DataType;
  name: string;
  description: string;
  dataset: string;
  tags: string[];
  importType: TypeDataLocale;
  files: string[];
  uploadedFiles: File[];
  uploadedFileInfos: UploadFilesResponseItem[]; // 업로드 API 응답 데이터
  selectedStorageData: any[]; // 데이터 저장소에서 선택된 데이터
}
