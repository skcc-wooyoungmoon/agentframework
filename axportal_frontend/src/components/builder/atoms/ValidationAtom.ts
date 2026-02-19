import { type NodeValidation } from '@/components/builder/types/Validation.ts';
import { atom } from 'jotai';

export const nodeValidationsAtom = atom<NodeValidation[]>([]);

export const validationStatusAtom = atom(get => {
  const validations = get(nodeValidationsAtom);
  const isValid = validations.every(v => v.isValid);

  // 에러가 있는 노드들의 정보를 수집
  const errorNodes = validations
    .filter(v => !v.isValid)
    .map(v => ({
      nodeId: v.nodeId,
      errors: v.errors,
    }));

  return {
    isSubmittable: isValid,
    errorNodes,
  };
});
