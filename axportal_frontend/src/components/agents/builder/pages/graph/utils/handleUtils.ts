import type { CustomNode } from '@/components/agents/builder/types/Agents';
import { NodeType } from '@/components/agents/builder/types/Agents';

export const resolveDefaultSourceHandle = (node?: CustomNode): string | undefined => {
    if (!node) return undefined;
    switch (node.type) {
        case NodeType.Input.name:
            return 'input_right';
        case NodeType.AgentGenerator.name:
        case 'union':
            return 'gen_right';
        case NodeType.AgentReviewer.name:
            return undefined;
        case 'agent__categorizer':
            const categories = (node.data?.categories || []) as Array<{ category: string; id: string }>;
            if (categories.length > 0) {
                return `handle-category-0`;
            }
            return undefined;
        case 'condition':
            const conditions = (node.data?.conditions || []) as Array<{ id: string }>;
            if (conditions.length > 0) {
                return `handle-${conditions[0].id}`;
            }
            return 'handle-condition-1';
        case 'retriever__knowledge':
            return `retriever_right_${node.id}`;
        case 'retriever__doc_filter':
            return 'filter_right';
        case 'retriever__doc_compressor':
            return 'compressor_right';
        case 'retriever__doc_reranker':
            return `rerank_right_${node.id}`;
        case 'retriever__rewriter_hyde':
            return `hyde_right_${node.id}`;
        case 'agent__coder':
            return 'code_right';
        case 'tool':
            return 'tool_right';
        default:
            return undefined;
    }
};

export const resolveDefaultTargetHandle = (node?: CustomNode): string | undefined => {
    if (!node) return undefined;
    switch (node.type) {
        case NodeType.AgentGenerator.name:
        case NodeType.AgentReviewer.name:
        case 'union':
            return 'gen_left';
        case 'output__chat':
        case 'output__formatter':
            return 'output_formatter_left';
        case 'agent__categorizer':
            return 'categorizer_left';
        case 'condition':
            return 'condition_left';
        case 'retriever__knowledge':
            return `retriever_left_${node.id}`;
        case 'retriever__doc_filter':
            return 'filter_left';
        case 'retriever__doc_compressor':
            return 'compressor_left';
        case 'retriever__doc_reranker':
            return `rerank_left_${node.id}`;
        case 'retriever__rewriter_hyde':
            return `hyde_left_${node.id}`;
        case 'agent__coder':
            return 'code_left';
        case 'tool':
            return 'tool_left';
        default:
            return undefined;
    }
};

export const isValidHandleId = (handleId: string | null | undefined): boolean => {
    if (!handleId || typeof handleId !== 'string') {
        return false;
    }
    if (handleId.trim() === '') {
        return false;
    }
    if (handleId.includes(' ') || handleId.includes('\n') || handleId.includes('\t')) {
        return false;
    }
    if (handleId === 'undefined' || handleId === 'null') {
        return false;
    }
    return true;
};

export const doesHandleExist = (
    nodes: CustomNode[],
    nodeId: string,
    handleId: string
): boolean => {
    const node = nodes.find(n => n.id === nodeId);
    if (!node) {
        return false;
    }

    const basicHandles = [
        'input_right',
        'gen_right',
        'gen_left',
        'reviewer_pass',
        'reviewer_fail',
        'condition_left',
        'output_formatter_left',
        'categorizer_left',
        'code_left',
        'code_right',
        'tool_left',
        'tool_right',
        'filter_left',
        'filter_right',
        'compressor_left',
        'compressor_right',
    ];
    if (basicHandles.includes(handleId)) {
        return true;
    }

    const nodeType = node.type;

    if (nodeType === 'agent__generator' || nodeType === 'union') {
        if (handleId === 'gen_left' || handleId === 'gen_right') {
            return true;
        }
    }

    if (nodeType === 'agent__coder') {
        if (handleId === 'code_left' || handleId === 'code_right' || handleId.startsWith('code_left_') || handleId.startsWith('code_right_')) {
            return true;
        }
    }

    if (nodeType === 'retriever__rewriter_hyde') {
        if (handleId === `hyde_left_${nodeId}` || handleId === `hyde_right_${nodeId}` || handleId.startsWith('hyde_left_') || handleId.startsWith('hyde_right_')) {
            return true;
        }
    }

    if (nodeType === 'retriever__doc_reranker') {
        if (handleId === `rerank_left_${nodeId}` || handleId === `rerank_right_${nodeId}` || handleId.startsWith('rerank_left_') || handleId.startsWith('rerank_right_')) {
            return true;
        }
    }

    if (nodeType === 'retriever__knowledge') {
        if (
            handleId === `retriever_left_${nodeId}` ||
            handleId === `retriever_right_${nodeId}` ||
            handleId.startsWith('retriever_left_') ||
            handleId.startsWith('retriever_right_')
        ) {
            return true;
        }
    }

    if (nodeType === 'retriever__doc_filter') {
        if (handleId === 'filter_left' || handleId === 'filter_right') {
            return true;
        }
    }

    if (nodeType === 'retriever__doc_compressor') {
        if (handleId === 'compressor_left' || handleId === 'compressor_right') {
            return true;
        }
    }

    if (nodeType === 'tool') {
        if (handleId === 'tool_left' || handleId === 'tool_right') {
            return true;
        }
    }

    if (nodeType === 'output__formatter' || nodeType === 'output__chat' || nodeType === 'output__selector') {
        if (handleId === 'output_formatter_left' || handleId === 'output_formatter_right' || handleId.startsWith('output_formatter_') || handleId.startsWith('output_selector_')) {
            return true;
        }
    }

    if (nodeType === 'agent__categorizer' && handleId.startsWith('handle-category-')) {
        const categories = (node.data?.categories || []) as Array<{ category: string; id: string }>;
        const indexMatch = handleId.match(/handle-category-(\d+)/);
        if (indexMatch) {
            const categoryIndex = parseInt(indexMatch[1], 10);
            return categoryIndex >= 0 && categoryIndex < categories.length;
        }
    }

    if (nodeType === 'condition' && handleId.startsWith('handle-')) {
        if (handleId === 'handle-condition-else' || (handleId.includes('condition-else') && handleId.replace('handle-', '').includes('condition-else'))) {
            return true;
        }
        const conditions = (node.data?.conditions || []) as Array<{ id: string }>;
        const conditionId = handleId.replace('handle-', '');
        const matched = conditions.some(cond => {
            return cond.id === conditionId || handleId === `handle-${cond.id}` || conditionId.includes(cond.id) || cond.id.includes(conditionId.replace(/^[a-f0-9]+-/, ''));
        });
        if (!matched && conditionId.includes('-condition-')) {
            const defaultCondition = (node.data?.default_condition as string) || '';
            if (defaultCondition && typeof defaultCondition === 'string' && (conditionId === defaultCondition || conditionId.includes(defaultCondition))) {
                return true;
            }
        }
        return matched;
    }
    return true;
};
