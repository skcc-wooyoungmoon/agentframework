import { type NodeValidation } from '@/components/builder/types/Validation.ts';
import { validateDuplicateInputs, validateRequiredInputs, validateRequiredConditions } from '@/components/builder/utils/ValidationUtils.ts';
import { atom } from 'jotai';
import { useAtom } from 'jotai';
import { useCallback } from 'react';
import type { ConditionItem } from '../types/Agents';
import { keyTableAtom } from '../atoms/AgentAtom';

// Validation atom
const nodeValidationsAtom = atom<NodeValidation[]>([]);

export const useNodeValidation = () => {
  const [validations, setValidations] = useAtom(nodeValidationsAtom);
  const [keyTableList] = useAtom(keyTableAtom);

  const validateNode = useCallback(
    (
      nodeId: string,
      nodeType: string,
      inputKeys: Array<{
        name: string;
        required: boolean;
        fixed_value: string | null;
        keytable_id: string | null;
      }>
    ) => {
      const requiredErrors = validateRequiredInputs(nodeType, inputKeys, keyTableList);
      const duplicateErrors = validateDuplicateInputs(nodeType, inputKeys);

      const allErrors = [...requiredErrors, ...duplicateErrors];
      const isValid = allErrors.length === 0;

      updateValidation(nodeId, isValid, allErrors, 'input');

      return { isValid, errors: allErrors };
    },
    [keyTableList]
  );

  const updateValidation = useCallback(
    (nodeId: string, isValid: boolean, errors: NodeValidation['errors'] = [], validationType: 'node' | 'input' | 'node_value' = 'node') => {
      setValidations(prev => {
        // 같은 nodeId의 다른 타입 validation은 유지
        const otherValidations = prev.filter(v => !(v.nodeId === nodeId && v.validationType === validationType));

        const newValidation: NodeValidation = {
          nodeId,
          isValid,
          errors,
          validationType,
        };

        return [...otherValidations, newValidation];
      });
    },
    [setValidations]
  );

  const validateCondition = useCallback(
    (nodeId: string, conditions: Array<ConditionItem>) => {
      const requiredErrors = validateRequiredConditions(conditions);

      const allErrors = [...requiredErrors];
      const isValid = allErrors.length === 0;

      updateValidation(nodeId, isValid, allErrors, 'node_value');

      return { isValid, errors: allErrors };
    },
    [updateValidation]
  );

  const getValidation = useCallback(
    (nodeId: string, validationType: 'node' | 'input' | 'node_value' = 'node') => {
      return validations.find(v => v.nodeId === nodeId && v.validationType === validationType);
    },
    [validations]
  );

  const clearValidation = useCallback(
    (nodeId: string, validationType?: 'node' | 'input') => {
      setValidations(prev => {
        if (validationType) {
          return prev.filter(v => !(v.nodeId === nodeId && v.validationType === validationType));
        }
        return prev.filter(v => v.nodeId !== nodeId);
      });
    },
    [setValidations]
  );

  return {
    validateNode,
    updateValidation,
    validateCondition,
    getValidation,
    clearValidation,
  };
};
