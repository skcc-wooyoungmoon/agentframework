import { atom } from 'jotai';
import type { DataState } from './type';

// 기본 초기값
const initialState: DataState = {
  dataType: 'basicDataSet',
  dataset: '지도학습',
  name: '',
  description: '',
  importType: 'none',
  tags: [],
  files: [],
  uploadedFiles: [],
  uploadedFileInfos: [],
  selectedStorageData: [],
};

export const DataAtom = atom<DataState>(initialState);
