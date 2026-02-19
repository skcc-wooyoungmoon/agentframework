// src/stores/home/proj/projJoinWizard.atoms.ts
import { atom } from 'jotai';
import { atomWithReset } from 'jotai/utils';

import type { ProjJoinSelected } from './types';

// 선택된 프로젝트 정보
export const projJoinSelectedProjectAtom = atomWithReset<ProjJoinSelected | null>(null);

// 참여한 프로젝트 ID
export const projJoinedIdAtom = atomWithReset<string | null>(null);

// 모든 상태 초기화를 위한 atom
export const resetAllProjJoinDataAtom = atom(null, (_, set) => {
  set(projJoinSelectedProjectAtom, null);
  set(projJoinedIdAtom, null);
});
