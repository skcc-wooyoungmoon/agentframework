import type { CustomNode } from '@/components/agents/builder/types/Agents';
import { getNodeTitleByName } from '@/components/agents/builder/utils/GraphUtils';

interface ValidationError {
    nodeId: string;
    nodeName: string;
    nodeType: string;
    message: string;
}

export const validateGeneratorNodes = (
    nodes: CustomNode[],
    selectedLLMRepo: Record<string, any>
): ValidationError[] => {
    const errors: ValidationError[] = [];

    nodes.forEach(node => {
        if (node.type !== 'agent__generator') return;

        const nodeData = node.data || {};
        const currentLLM = selectedLLMRepo[node.id];
        const servingName = currentLLM?.servingName || (nodeData.serving_name ? String(nodeData.serving_name).trim() : '');
        const servingModel = currentLLM?.servingModel || (nodeData.serving_model ? String(nodeData.serving_model).trim() : '');

        const hasModel = servingName !== '' || servingModel !== '';

        if (!hasModel) {
            errors.push({
                nodeId: node.id,
                nodeName: (node.data?.name as string) || node.id,
                nodeType: node.type,
                message: '모델을 선택해주세요.',
            });
        }
    });

    return errors;
};

export const validateReviewerNodes = (
    nodes: CustomNode[],
    selectedLLMRepo: Record<string, any>
): ValidationError[] => {
    const errors: ValidationError[] = [];

    nodes.forEach(node => {
        if (node.type !== 'agent__reviewer') return;

        const nodeData = node.data || {};
        const currentLLM = selectedLLMRepo[node.id];
        const servingName = currentLLM?.servingName || (nodeData.serving_name ? String(nodeData.serving_name).trim() : '');
        const servingModel = currentLLM?.servingModel || (nodeData.serving_model ? String(nodeData.serving_model).trim() : '');

        const hasModel = servingName !== '' || servingModel !== '';

        if (!hasModel) {
            errors.push({
                nodeId: node.id,
                nodeName: (node.data?.name as string) || node.id,
                nodeType: node.type,
                message: '모델을 선택해주세요.',
            });
        }
    });

    return errors;
};

export const validateInputNodes = (nodes: CustomNode[]): ValidationError[] => {
    const errors: ValidationError[] = [];

    nodes.forEach(node => {
        if (node.type !== 'input__basic') return;

        const nodeData = node.data || {};
        const inputKeys =
            (nodeData.input_keys as Array<{
                name: string;
                required?: boolean;
                keytable_id?: string | null;
                fixed_value?: string | null;
            }>) || [];

        const queryKey = inputKeys.find(key => key && key.name === 'query');

        if (!queryKey) {
            errors.push({
                nodeId: node.id,
                nodeName: (node.data?.name as string) || node.id,
                nodeType: node.type,
                message: '스키마 내 query를 입력해주세요.',
            });
            return;
        }

        const keytableId = String(queryKey.keytable_id || '').trim();
        const fixedValue = String(queryKey.fixed_value || '').trim();
        const hasValue = keytableId !== '' || fixedValue !== '';

        if (!hasValue) {
            errors.push({
                nodeId: node.id,
                nodeName: (node.data?.name as string) || node.id,
                nodeType: node.type,
                message: '스키마 내 query를 입력해주세요.',
            });
        }
    });

    return errors;
};

export const validateGraphForSave = (
    nodes: CustomNode[],
    selectedLLMRepo: Record<string, any>
): { isValid: boolean; errors: string[] } => {
    const allErrors: string[] = [];

    const generatorErrors = validateGeneratorNodes(nodes, selectedLLMRepo);
    generatorErrors.forEach(error => {
        allErrors.push(`${getNodeTitleByName(error.nodeType)}(${error.nodeName}): ${error.message}`);
    });

    const reviewerErrors = validateReviewerNodes(nodes, selectedLLMRepo);
    reviewerErrors.forEach(error => {
        allErrors.push(`${getNodeTitleByName(error.nodeType)}(${error.nodeName}): ${error.message}`);
    });

    const inputErrors = validateInputNodes(nodes);
    inputErrors.forEach(error => {
        allErrors.push(`${getNodeTitleByName(error.nodeType)}(${error.nodeName}): ${error.message}`);
    });

    return {
        isValid: allErrors.length === 0,
        errors: allErrors,
    };
};
