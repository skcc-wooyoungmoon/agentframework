import { atom } from 'jotai';
import { atomWithReset } from 'jotai/utils';

/**
 * IDE 생성 위자드 상태 타입
 */
export interface IdeCreWizardState {
  // Step 1: 프로젝트 선택
  selectedProjectIds: number[];
  
  // Step 2: 도구 및 이미지 선택
  selectedImageId: string;
  selectedImageType: string; // 'JUPYTER' | 'VSCODE'
  
  // Step 3: DW 계정 선택
  dwAccountUsage: string; // 'notUse' | 'use'
  dwAccountType: string; // '1' (사용자), '2' (서비스)
  selectedDwAccountId: string;

  // Step 4: 자원 선택
  resourcePreset: string;
  cpuValue: string;
  memoryValue: string;
}

/**
 * IDE 생성 위자드 초기 상태
 */
const initialIdeCreWizardState: IdeCreWizardState = {
  selectedProjectIds: [],
  selectedImageId: '',
  selectedImageType: '',
  dwAccountUsage: 'notUse',
  dwAccountType: '1',
  selectedDwAccountId: '',
  resourcePreset: 'preset-small',
  cpuValue: '1',
  memoryValue: '2',
};

/**
 * IDE 생성 위자드 데이터 관리 atom
 */
export const ideCreWizardAtom = atomWithReset<IdeCreWizardState>(initialIdeCreWizardState);

/**
 * 모든 데이터를 초기화하는 atom
 */
export const resetIdeCreWizardAtom = atom(null, (_, set) => {
  set(ideCreWizardAtom, initialIdeCreWizardState);
});
