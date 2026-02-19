// src/stores/home/proj/projCreWizard.atoms.ts
import { atom } from 'jotai';
import { atomWithReset } from 'jotai/utils';

// 이미 존재하는 atom (구성원 선택)
export const projCreSelectedMembersAtom = atomWithReset<string[]>([]);
// 선택된 사용자 세부 정보를 저장할 atom 추가

// 새로 추가할 atom (프로젝트 기본 정보)
export const projCreBaseInfoAtom = atomWithReset({
  name: '',
  description: '',
  is_sensitive: 'N',
  sensitive_reason: '',
});

// 프로젝트 ID를 저장할 atom 추가
export const projCreatedIdAtom = atomWithReset<string>('');
export const projCreatedSeqAtom = atomWithReset<number | null>(null);

// 모든 상태를 초기화하는 atom
export const resetAllProjCreDataAtom = atom(null, (_, set) => {
  set(projCreSelectedMembersAtom, []);
  set(projCreBaseInfoAtom, {
    name: '',
    description: '',
    is_sensitive: 'N',
    sensitive_reason: '',
  });
  set(projCreatedIdAtom, '');
  set(projCreatedSeqAtom, null);
});
