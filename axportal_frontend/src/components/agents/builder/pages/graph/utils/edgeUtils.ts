import type { CustomEdge, CustomNode } from '@/components/agents/builder/types/Agents';
import { EDGE_COLORS, EDGE_MARKER_SIZE } from '@/components/agents/builder/types/Graphs';
import { resolveDefaultSourceHandle, resolveDefaultTargetHandle, isValidHandleId, doesHandleExist } from './handleUtils';

export const getCaseEdgeAppearance = (
    sourceHandle?: string | null,
    label?: string | null
): { style: { stroke: string; strokeWidth: number }; marker: { type: string; width: number; height: number; color: string }; label?: string } => {
    let strokeColor = EDGE_COLORS.condition;
    let markerColor = EDGE_COLORS.condition;

    if (sourceHandle === 'reviewer_pass' || sourceHandle === 'handle-condition-pass') {
        strokeColor = EDGE_COLORS.reviewerPass;
        markerColor = EDGE_COLORS.reviewerPass;
    } else if (sourceHandle === 'reviewer_fail' || sourceHandle === 'handle-condition-fail') {
        strokeColor = EDGE_COLORS.reviewerFail;
        markerColor = EDGE_COLORS.reviewerFail;
    }

    return {
        style: {
            stroke: strokeColor,
            strokeWidth: 2.5,
        },
        marker: {
            type: 'arrowclosed',
            width: EDGE_MARKER_SIZE.width,
            height: EDGE_MARKER_SIZE.height,
            color: markerColor,
        },
        label: label || undefined,
    };
};

export const convertEdgeWithAppearance = (
    edge: any,
    nodesForHandle: CustomNode[] = []
): CustomEdge => {
    const sourceHandle = edge.sourceHandle || edge.source_handle || null;
    const targetHandle = edge.targetHandle || edge.target_handle || null;
    const isCaseEdge = edge.type === 'case' || edge.data?.edge_type === 'case';

    let strokeColor = isCaseEdge ? EDGE_COLORS.condition : EDGE_COLORS.default;
    let markerColor = strokeColor;

    if (sourceHandle === 'reviewer_pass') {
        strokeColor = EDGE_COLORS.reviewerPass;
        markerColor = EDGE_COLORS.reviewerPass;
    } else if (sourceHandle === 'reviewer_fail') {
        strokeColor = EDGE_COLORS.reviewerFail;
        markerColor = EDGE_COLORS.reviewerFail;
    }

    let finalSourceHandle = sourceHandle;
    let finalTargetHandle = targetHandle;

    if (!finalSourceHandle && nodesForHandle.length > 0) {
        const sourceNode = nodesForHandle.find(n => n.id === edge.source);
        if (sourceNode) {
            finalSourceHandle = resolveDefaultSourceHandle(sourceNode) ?? null;
        }
    }

    if (!finalTargetHandle && nodesForHandle.length > 0) {
        const targetNode = nodesForHandle.find(n => n.id === edge.target);
        if (targetNode) {
            finalTargetHandle = resolveDefaultTargetHandle(targetNode) ?? null;
        }
    }

    if (finalSourceHandle && !isValidHandleId(finalSourceHandle)) {
        finalSourceHandle = null;
    }
    if (finalTargetHandle && !isValidHandleId(finalTargetHandle)) {
        finalTargetHandle = null;
    }

    if (finalSourceHandle && nodesForHandle.length > 0) {
        if (!doesHandleExist(nodesForHandle, edge.source, finalSourceHandle)) {
            finalSourceHandle = null;
        }
    }
    if (finalTargetHandle && nodesForHandle.length > 0) {
        if (!doesHandleExist(nodesForHandle, edge.target, finalTargetHandle)) {
            finalTargetHandle = null;
        }
    }

    const safeStrokeWidth = 2.5;
    const safeMarkerWidth = isNaN(Number(EDGE_MARKER_SIZE.width)) ? 10 : Number(EDGE_MARKER_SIZE.width);
    const safeMarkerHeight = isNaN(Number(EDGE_MARKER_SIZE.height)) ? 10 : Number(EDGE_MARKER_SIZE.height);

    return {
        ...edge,
        id: String(edge.id),
        source: String(edge.source),
        target: String(edge.target),
        sourceHandle: finalSourceHandle ?? undefined,
        targetHandle: finalTargetHandle ?? undefined,
        condition_label: edge.condition_label || undefined,
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
            sourceHandle: finalSourceHandle,
            targetHandle: finalTargetHandle,
            source_handle: finalSourceHandle,
            target_handle: finalTargetHandle,
            condition_label: edge.condition_label || undefined,
        },
    } as CustomEdge;
};

export const convertCategorizerEdge = (
    edge: any,
    sourceNode: any
): CustomEdge | null => {
    if (!sourceNode || sourceNode.type !== 'agent__categorizer') {
        return null;
    }

    const categories = (sourceNode.data?.categories || []) as Array<{ category: string; id: string }>;
    let sourceHandle = edge.sourceHandle || edge.source_handle || null;

    if (sourceHandle && sourceHandle.startsWith('handle-category-')) {
        const indexMatch = sourceHandle.match(/handle-category-(\d+)/);
        if (indexMatch) {
            const categoryIndex = parseInt(indexMatch[1], 10);
            if (categoryIndex >= 0 && categoryIndex < categories.length) {
                sourceHandle = `handle-category-${categoryIndex}`;
            } else if (categories.length > 0) {
                sourceHandle = 'handle-category-0';
            }
        }
    } else if (categories.length > 0) {
        let categoryName = '';
        if (edge.condition_label) {
            categoryName = edge.condition_label;
        } else if (sourceHandle) {
            categoryName = sourceHandle.replace('handle-', '');
        }

        let categoryIndex = categories.findIndex(cat => cat.id === categoryName || cat.category === categoryName);
        if (categoryIndex === -1) {
            categoryIndex = 0;
        }
        sourceHandle = `handle-category-${categoryIndex}`;
    } else {
        sourceHandle = 'handle-category-0';
    }

    const appearance = getCaseEdgeAppearance(sourceHandle, edge.condition_label);

    return {
        ...edge,
        id: String(edge.id),
        source: String(edge.source),
        target: String(edge.target),
        sourceHandle: sourceHandle ?? undefined,
        targetHandle: edge.targetHandle || edge.target_handle || undefined,
        style: appearance.style,
        markerEnd: {
            type: appearance.marker.type,
            width: appearance.marker.width,
            height: appearance.marker.height,
            color: appearance.marker.color,
        },
        data: {
            ...edge.data,
            sourceHandle: sourceHandle,
            source_handle: sourceHandle,
            condition_label: edge.condition_label || appearance.label,
        },
    } as CustomEdge;
};

export const removeDuplicateEdges = (edges: CustomEdge[]): CustomEdge[] => {
    const edgeIdMap = new Map<string, CustomEdge>();
    const edgeKeyMap = new Map<string, number>();

    edges.forEach((edge) => {
        const edgeId = String(edge.id);

        if (edgeIdMap.has(edgeId)) {
            return;
        }

        const sourceHandleForKey = edge.sourceHandle || 'none';
        const edgeKey = `${edge.source}-${edge.target}-${sourceHandleForKey}`;
        const existingCount = edgeKeyMap.get(edgeKey) || 0;

        if (existingCount > 0) {
            return;
        }

        edgeKeyMap.set(edgeKey, 1);
        edgeIdMap.set(edgeId, edge);
    });

    return Array.from(edgeIdMap.values());
};
