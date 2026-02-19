import { type NodeValidation } from '@/components/agents/builder/types/Validation.ts';
import { validateDuplicateInputs, validateRequiredInputs } from '@/components/agents/builder/utils/ValidationUtils.ts';
import { atom } from 'jotai';
import { useAtom } from 'jotai';
import { useCallback } from 'react';

const nodeValidationsAtom = atom<NodeValidation[]>([]);

export const useNodeValidation = () => {
  const [validations, setValidations] = useAtom(nodeValidationsAtom);

  const updateValidation = useCallback(
    (nodeId: string, isValid: boolean, errors: NodeValidation['errors'] = [], validationType: 'node' | 'input' = 'node') => {
      setValidations(prev => {
        const otherValidations = prev.filter(v => !(v.nodeId === nodeId && v.validationType === validationType));

        const newValidation = {
          nodeId,
          isValid,
          errors,
          validationType,
        };

        const existingValidation = prev.find(v => v.nodeId === nodeId && v.validationType === validationType);
        if (existingValidation) {
          const isSame =
            existingValidation.isValid === isValid &&
            existingValidation.errors.length === errors.length &&
            existingValidation.errors.every((err, idx) =>
              err.message === errors[idx]?.message && err.field === errors[idx]?.field
            );
          if (isSame) {
            return prev;
          }
        }

        return [...otherValidations, newValidation];
      });
    },
    [setValidations]
  );

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
      const requiredErrors = validateRequiredInputs(nodeType, inputKeys);
      const duplicateErrors = validateDuplicateInputs(nodeType, inputKeys);

      const allErrors = [...requiredErrors, ...duplicateErrors];
      const isValid = allErrors.length === 0;

      updateValidation(nodeId, isValid, allErrors, 'input');

      return { isValid, errors: allErrors };
    },
    [updateValidation]
  );

  const getValidation = useCallback(
    (nodeId: string, validationType: 'node' | 'input' = 'node') => {
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

  const validationStatus = {
    isSubmittable: validations.every(v => v.isValid),
    errorNodes: validations
      .filter(v => !v.isValid)
      .map(v => ({
        nodeId: v.nodeId,
        errors: v.errors,
      }))
  };

  return {
    validateNode,
    updateValidation,
    getValidation,
    clearValidation,
    validationStatus,
  };
};
