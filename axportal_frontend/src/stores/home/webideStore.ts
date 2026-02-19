import { atom } from 'jotai';

export type IdeType = 'jupyter' | 'vscode' | null;
export type IdeAction = 'create' | 'terminate' | null;

// Step1 선택 값 공유 (jupyter | vscode)
export const ideTypeAtom = atom<IdeType>('jupyter');

// Dashboard에서 트리거 → IdeMoveSelPopupPage가 반응
export const ideActionAtom = atom<{ action: IdeAction; seq: number }>({
  action: null,
  seq: 0,
});

// 종료 완료 알림
export const ideTerminateSuccessAtom = atom<{ success: boolean; seq: number }>({
  success: false,
  seq: 0,
});
