import type { CustomNode } from '@/components/agents/builder/types/Agents';

export const filterCoderNodeInputKeys = (node: any): any => {
    if (node.type !== 'agent__coder' || !node.data?.input_keys) {
        return node;
    }

    const inputKeys = Array.isArray(node.data.input_keys) ? node.data.input_keys : [];
    const filteredInputKeys = inputKeys.filter((item: any) => {
        const keyName = item?.name;

        if (keyName === null || keyName === undefined || String(keyName).trim() === '') {
            return false;
        }

        const keyNameStr = String(keyName).trim();
        const isConditionKey = /^condition-\d+$/.test(keyNameStr) ||
            keyNameStr === 'condition-else' ||
            /^condition-\w+$/.test(keyNameStr);

        if (isConditionKey) {
            return false;
        }

        return true;
    });

    if (filteredInputKeys.length !== inputKeys.length) {
        return {
            ...node,
            data: {
                ...node.data,
                input_keys: filteredInputKeys,
            },
        };
    }

    return node;
};

export const filterCoderNodesInputKeys = (nodes: any[]): any[] => {
    return nodes.map(filterCoderNodeInputKeys);
};

export interface NodeAtomUpdates {
    promptIdUpdates: Record<string, string | null>;
    fewShotIdUpdates: Record<string, string | null>;
    toolsUpdates: Record<string, any[] | null>;
}

export const extractNodeAtomData = (nodes: any[]): NodeAtomUpdates => {
    const promptIdUpdates: Record<string, string | null> = {};
    const fewShotIdUpdates: Record<string, string | null> = {};
    const toolsUpdates: Record<string, any[] | null> = {};

    nodes.forEach((node: any) => {
        const nodeId = String(node.id);
        const nodeData = node.data || {};

        if (nodeData.prompt_id && nodeData.prompt_id.trim() !== '') {
            promptIdUpdates[nodeId] = nodeData.prompt_id;
        } else {
            promptIdUpdates[nodeId] = null;
        }

        if (nodeData.fewshot_id && nodeData.fewshot_id.trim() !== '') {
            fewShotIdUpdates[nodeId] = nodeData.fewshot_id;
        } else {
            fewShotIdUpdates[nodeId] = null;
        }

        if (nodeData.tools && Array.isArray(nodeData.tools) && nodeData.tools.length > 0) {
            toolsUpdates[nodeId] = nodeData.tools;
        } else {
            toolsUpdates[nodeId] = null;
        }
    });

    return {
        promptIdUpdates,
        fewShotIdUpdates,
        toolsUpdates,
    };
};

export const validateNodePosition = (
    position: { x: number; y: number } | undefined,
    index: number,
    defaultSpacing: number = 200
): { x: number; y: number } => {
    if (position && typeof position.x === 'number' && typeof position.y === 'number') {
        return position;
    }

    const column = index % 3;
    const row = Math.floor(index / 3);

    return {
        x: 100 + column * defaultSpacing,
        y: 100 + row * defaultSpacing,
    };
};

export const autoSetOutputChatFormatString = (
    outputChatNode: CustomNode,
    incomingEdges: any[],
    allNodes: any[]
): CustomNode => {
    if (incomingEdges.length === 0) {
        return outputChatNode;
    }

    const currentFormatString = (outputChatNode.data as any)?.format_string || '';
    const tokenRegex = /\{\{([^}]+)\}\}/g;
    const currentTokens = Array.from(currentFormatString.matchAll(tokenRegex), (match: RegExpMatchArray) => match[1]);

    let hasValidTokens = false;
    if (currentTokens.length > 0) {
        for (const token of currentTokens) {
            for (const edge of incomingEdges) {
                const sourceNode = allNodes.find(n => String(n.id) === String(edge.source));
                if (sourceNode) {
                    const outputKeys = Array.isArray((sourceNode.data as any)?.output_keys) ? (sourceNode.data as any).output_keys : [];
                    const tokenExists = outputKeys.some((key: any) => key && key.keytable_id === token);
                    if (tokenExists) {
                        hasValidTokens = true;
                        break;
                    }
                }
            }
            if (hasValidTokens) break;
        }

        if (hasValidTokens) {
            return outputChatNode;
        }
    }

    if (currentTokens.length === 0 || !hasValidTokens) {
        const allOutputKeys: string[] = [];
        incomingEdges.forEach((edge: any) => {
            const sourceNode = allNodes.find(n => String(n.id) === String(edge.source));
            if (sourceNode) {
                const outputKeys = Array.isArray((sourceNode.data as any)?.output_keys) ? (sourceNode.data as any).output_keys : [];
                outputKeys.forEach((key: any) => {
                    if (key && key.keytable_id) {
                        allOutputKeys.push(key.keytable_id);
                    }
                });
            }
        });

        if (allOutputKeys.length > 0) {
            const desiredFormatString = `{{${allOutputKeys[0]}}}`;

            if (!currentFormatString || currentFormatString.trim() === '' || currentFormatString.trim() === ' ') {
                return {
                    ...outputChatNode,
                    data: {
                        ...outputChatNode.data,
                        format_string: desiredFormatString,
                    },
                } as CustomNode;
            }
        } else if (!currentFormatString || currentFormatString.trim() === '') {
            return {
                ...outputChatNode,
                data: {
                    ...outputChatNode.data,
                    format_string: ' ',
                },
            } as CustomNode;
        }
    }

    return outputChatNode;
};
