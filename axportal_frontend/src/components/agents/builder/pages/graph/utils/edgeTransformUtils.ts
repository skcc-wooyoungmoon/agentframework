import type { CustomEdge, CustomNode } from '@/components/agents/builder/types/Agents';
import { EDGE_COLORS, EDGE_MARKER_SIZE } from '@/components/agents/builder/types/Graphs';
import { resolveDefaultSourceHandle, resolveDefaultTargetHandle } from './handleUtils';

export const transformConditionSourceHandle = (
    edge: any,
    sourceNode: any
): { sourceHandle: string; conditionLabel: string } => {
    const conditions = (sourceNode.data?.conditions || []) as Array<{ id: string }>;
    let sourceHandle = edge.sourceHandle || edge.source_handle || null;
    let conditionLabel = edge.condition_label || (edge.data as any)?.condition_label || '';

    if (sourceHandle && sourceHandle.startsWith('handle-handle-')) {
        sourceHandle = sourceHandle.replace(/^handle-handle-/, 'handle-');
    }

    const savedSourceHandle = edge.sourceHandle || edge.source_handle || sourceHandle;

    if (savedSourceHandle && savedSourceHandle.startsWith('handle-')) {
        const conditionIdFromHandle = savedSourceHandle.replace('handle-', '');

        if (conditionIdFromHandle.includes('condition-else') || conditionIdFromHandle.endsWith('-condition-else')) {
            sourceHandle = 'handle-condition-else';
            if (!conditionLabel || !conditionLabel.includes('condition-else')) {
                const defaultCondition = sourceNode.data?.default_condition || `${sourceNode.id}-condition-else`;
                conditionLabel = defaultCondition.includes(sourceNode.id) ? defaultCondition : `${sourceNode.id}-condition-else`;
            }
        } else {
            sourceHandle = savedSourceHandle;
            if (!conditionLabel) {
                conditionLabel = savedSourceHandle.replace('handle-', '');
            }
        }
    }

    else if (conditionLabel) {
        if (conditionLabel === 'condition-else' || conditionLabel === 'ELSE' || conditionLabel.includes('condition-else')) {
            sourceHandle = 'handle-condition-else';
        } else if (conditions.length > 0) {
            const matchedCondition = conditions.find(cond =>
                cond.id === conditionLabel || cond.id.includes(conditionLabel) || conditionLabel.includes(cond.id)
            );
            if (matchedCondition) {
                sourceHandle = `handle-${matchedCondition.id}`;
            } else {
                const firstCondition = conditions.find(cond => cond.id !== 'condition-else') || conditions[0];
                sourceHandle = `handle-${firstCondition.id}`;
            }
        }
    }

    else if (conditions.length > 0) {
        const firstCondition = conditions.find(cond => cond.id !== 'condition-else') || conditions[0];
        sourceHandle = `handle-${firstCondition.id}`;
        conditionLabel = firstCondition.id;
    } else {
        sourceHandle = 'handle-condition-1';
        conditionLabel = 'condition-1';
    }

    return { sourceHandle, conditionLabel };
};

export const transformReviewerSourceHandle = (
    edge: any
): string | null => {
    const savedSourceHandle = edge.sourceHandle || edge.source_handle || null;
    const conditionLabel = edge.condition_label || (edge.data as any)?.condition_label || '';

    if (savedSourceHandle && typeof savedSourceHandle === 'string') {
        if (savedSourceHandle === 'reviewer_pass' || savedSourceHandle === 'reviewer_fail') {
            return savedSourceHandle;
        }
        if (savedSourceHandle === 'handle-condition-pass' || savedSourceHandle === 'handle-reviewer-pass' || savedSourceHandle === 'handle-pass') {
            return 'reviewer_pass';
        }
        if (savedSourceHandle === 'handle-condition-fail' || savedSourceHandle === 'handle-reviewer-fail' || savedSourceHandle === 'handle-fail') {
            return 'reviewer_fail';
        }
        if (savedSourceHandle.includes('pass') && savedSourceHandle.includes('handle')) {
            return 'reviewer_pass';
        }
        if (savedSourceHandle.includes('fail') && savedSourceHandle.includes('handle')) {
            return 'reviewer_fail';
        }
        if (savedSourceHandle.includes('pass')) {
            return 'reviewer_pass';
        }
        if (savedSourceHandle.includes('fail')) {
            return 'reviewer_fail';
        }
        return null;
    }
    if (conditionLabel) {
        if (conditionLabel === 'condition-pass' || conditionLabel === 'pass' || conditionLabel === 'reviewer_pass' || conditionLabel.includes('pass')) {
            return 'reviewer_pass';
        }
        if (conditionLabel === 'condition-fail' || conditionLabel === 'fail' || conditionLabel === 'reviewer_fail' || conditionLabel.includes('fail')) {
            return 'reviewer_fail';
        }
    }
    return null;
};

export const transformCategorizerSourceHandle = (
    edge: any,
    sourceNode: any
): string => {
    const categories = (sourceNode.data?.categories || []) as Array<{ category: string; id: string }>;
    let sourceHandle = edge.sourceHandle || edge.source_handle || null;

    if (sourceHandle && sourceHandle.startsWith('handle-category-')) {
        const indexMatch = sourceHandle.match(/handle-category-(\d+)/);
        if (indexMatch) {
            const categoryIndex = parseInt(indexMatch[1], 10);
            if (categoryIndex >= 0 && categoryIndex < categories.length) {
                return `handle-category-${categoryIndex}`;
            } else if (categories.length > 0) {
                return 'handle-category-0';
            }
        }
    }

    if (categories.length === 0) {
        return 'handle-category-0';
    }

    let categoryName = '';
    if (edge.condition_label) {
        categoryName = edge.condition_label;
    } else if (sourceHandle) {
        categoryName = sourceHandle.replace('handle-', '');
    }

    let categoryIndex = categories.findIndex(cat => cat.id === categoryName);

    if (categoryIndex === -1 && categoryName) {
        categoryIndex = categories.findIndex(cat => cat.category === categoryName);
    }

    if (categoryIndex === -1 && categoryName) {
        categoryIndex = categories.findIndex(cat =>
            cat.category.includes(categoryName) || categoryName.includes(cat.category)
        );
    }

    if (categoryIndex === -1) {
        categoryIndex = 0;
    }

    return `handle-category-${categoryIndex}`;
};

export const getDefaultTargetHandle = (targetNode: any, sourceNode?: any): string | null => {
    if (!targetNode) return null;

    const targetType = targetNode.type;
    const sourceType = sourceNode?.type;

    if (targetType === 'condition') {
        return 'condition_left';
    }
    if (targetType === 'union') {
        return 'gen_left';
    }
    if (targetType === 'agent__generator') {
        return 'gen_left';
    }
    if (targetType === 'agent__reviewer') {
        if (sourceType === 'union') {
            return 'gen_left';
        }
    }
    if (targetType === 'output__chat' || targetType === 'output__formatter') {
        return 'output_formatter_left';
    }
    const customTargetNode = {
        id: targetNode.id,
        type: targetNode.type,
        data: targetNode.data || {},
    } as CustomNode;
    return resolveDefaultTargetHandle(customTargetNode) ?? null;
};

export const getDefaultSourceHandle = (sourceNode: any): string | null => {
    if (!sourceNode) return null;

    const sourceType = sourceNode.type;

    if (sourceType === 'input__basic') {
        return 'input_right';
    }
    if (sourceType === 'agent__generator') {
        return 'gen_right';
    }
    if (sourceType === 'union') {
        return 'gen_right';
    }
    const customSourceNode = {
        id: sourceNode.id,
        type: sourceNode.type,
        data: sourceNode.data || {},
    } as CustomNode;
    return resolveDefaultSourceHandle(customSourceNode) ?? null;
};

export const createEdgeStyle = (
    sourceHandle?: string | null,
    isCaseEdge?: boolean
): { strokeColor: string; markerColor: string } => {
    let strokeColor = isCaseEdge ? EDGE_COLORS.condition : EDGE_COLORS.default;
    let markerColor = strokeColor;

    if (sourceHandle === 'reviewer_pass' || sourceHandle === 'handle-condition-pass') {
        strokeColor = EDGE_COLORS.reviewerPass;
        markerColor = EDGE_COLORS.reviewerPass;
    } else if (sourceHandle === 'reviewer_fail' || sourceHandle === 'handle-condition-fail') {
        strokeColor = EDGE_COLORS.reviewerFail;
        markerColor = EDGE_COLORS.reviewerFail;
    }

    return { strokeColor, markerColor };
};

export const createFinalEdge = (
    edge: any,
    sourceHandle: string | null,
    targetHandle: string | null,
    conditionLabel: string | undefined,
    sourceNode: any,
    targetNode: any
): CustomEdge => {
    const isCaseEdge = edge.type === 'case' || (edge.data as any)?.edge_type === 'case';
    const { strokeColor, markerColor } = createEdgeStyle(sourceHandle, isCaseEdge);

    const safeStrokeWidth = 2.5;
    const safeMarkerWidth = isNaN(Number(EDGE_MARKER_SIZE.width)) ? 10 : Number(EDGE_MARKER_SIZE.width);
    const safeMarkerHeight = isNaN(Number(EDGE_MARKER_SIZE.height)) ? 10 : Number(EDGE_MARKER_SIZE.height);

    return {
        ...edge,
        id: String(edge.id),
        source: String(edge.source),
        target: String(edge.target),
        sourceHandle: sourceHandle ?? undefined,
        targetHandle: targetHandle ?? undefined,
        condition_label: conditionLabel,
        style: {
            ...(edge.style || {}),
            stroke: strokeColor || '#C4CADA',
            strokeWidth: safeStrokeWidth,
        },
        markerEnd: edge.markerEnd || {
            type: 'arrowclosed',
            width: safeMarkerWidth,
            height: safeMarkerHeight,
            color: markerColor || '#C4CADA',
        },
        data: {
            ...edge.data,
            sourceHandle,
            targetHandle,
            source_handle: sourceHandle,
            target_handle: targetHandle,
            condition_label: conditionLabel,
            sourceNodeType: sourceNode?.type,
            targetNodeType: targetNode?.type,
            loadTimestamp: Date.now(),
        },
    } as CustomEdge;
};

export const createNodeIdMaps = (
    nodes: any[]
): { nodeIdMap: Map<string, any>; nodeBaseIdMap: Map<string, any> } => {
    const nodeIdMap = new Map<string, any>();
    const nodeBaseIdMap = new Map<string, any>();

    nodes.forEach((node: any) => {
        const nodeId = String(node.id);
        nodeIdMap.set(nodeId, node);

        const baseId = nodeId.includes('_') ? nodeId.split('_')[0] : nodeId;
        if (baseId !== nodeId && !nodeBaseIdMap.has(baseId)) {
            nodeBaseIdMap.set(baseId, node);
        }
    });

    return { nodeIdMap, nodeBaseIdMap };
};

export const findNode = (
    nodeId: string,
    nodeIdMap: Map<string, any>,
    nodeBaseIdMap: Map<string, any>
): any | null => {
    let node = nodeIdMap.get(nodeId);
    if (!node) {
        const baseId = nodeId.includes('_') ? nodeId.split('_')[0] : nodeId;
        node = nodeBaseIdMap.get(baseId) || nodeIdMap.get(baseId);
    }
    return node || null;
};
