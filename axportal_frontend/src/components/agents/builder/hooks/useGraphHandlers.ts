import {
  edgesAtom,
  nodesAtom,
} from '@/components/agents/builder/atoms/AgentAtom';
import { useGraphActions } from '@/components/agents/builder/hooks/useGraphActions.ts';
import {
  ConditionalEdge,
  NoneConditionalEdge,
} from '@/components/agents/builder/pages/graph/edge';
import {
  type Category,
  type Condition,
  type EdgeParams,
  EdgeType,
  NodeType,
} from '@/components/agents/builder/types/Agents';
import { useDnD } from '@/components/agents/builder/utils/DnDContext.tsx';
import {
  EDGE_TYPE_CURVE,
  getNodeTypeById,
} from '@/components/agents/builder/utils/GraphUtils.ts';
import {
  addEdge,
  applyEdgeChanges,
  applyNodeChanges,
  type Connection,
  useReactFlow,
} from '@xyflow/react';
import { useAtom } from 'jotai';
import { useCallback } from 'react';
import type { DragEvent } from 'react';

export const useGraphHandlers = (readOnly: boolean = false) => {
  const [nodes, setNodes] = useAtom(nodesAtom);
  const [edges, setEdges] = useAtom(edgesAtom);
  const { getNodes, getEdges, screenToFlowPosition } = useReactFlow();
  const [type, setType] = useDnD();
  const { addNewNode } = useGraphActions();

  const onNodesChange = useCallback(
    (changes: any) => {
      setNodes(nds => {
        const existingIds = new Set(nds.map(node => node.id));
        const sanitizedChanges = (Array.isArray(changes) ? changes : []).filter(change => {
          if (change?.type === 'add' && change?.item?.id) {
            const isNew = !existingIds.has(change.item.id);
            return isNew;
          }
          return true;
        });
        const result = applyNodeChanges(sanitizedChanges, nds);
        return result.map(updatedNode => {
          const originalNode = nds.find(n => n.id === updatedNode.id);
          if (originalNode && updatedNode.data) {
            if (updatedNode.data.innerData) {
              updatedNode.data.innerData = {
                ...(originalNode.data?.innerData || {}),
                ...updatedNode.data.innerData,
              };
            } else if (originalNode.data?.innerData) {
              updatedNode.data.innerData = {
                ...originalNode.data.innerData,
              };
            }
          }
          return updatedNode;
        });
      });
    },
    [setNodes]
  );

  const onEdgesChange = useCallback(
    (changes: any) => setEdges(eds => applyEdgeChanges(changes, eds)),
    [setEdges]
  );


  const onConnect = useCallback(
    (params: Connection) => {
      if (!params.source || !params.target) {
        return;
      }


      setEdges(eds => {
        const currentNodes = getNodes();
        const sourceNode = currentNodes.find(node => node.id === params.source);
        const targetNode = currentNodes.find(node => node.id === params.target);

        if (!sourceNode || !targetNode) {
          return eds;
        }

        const sourceType = getNodeTypeById(currentNodes, params.source);
        const sourceHandleId = params.sourceHandle || '';
        const categoryId = sourceHandleId.split('-').slice(1).join('-');
        const conditionId = sourceHandleId.split('-').slice(1).join('-');

        const categories = (sourceNode?.data?.categories as Category[]) || [];
        const category = categories.find(
          (cat: Category) => cat.id === categoryId
        );
        let categoryLabel = category?.category || '';

        const conditions = (sourceNode?.data?.conditions as Condition[]) || [];
        const condition = conditions.find(
          (con: Condition) => con.id === conditionId
        );
        let conditionLabel = condition?.id || '';

        if (sourceType === NodeType.AgentCategorizer.name && sourceHandleId.startsWith('handle-category-')) {
          const indexMatch = sourceHandleId.match(/handle-category-(\d+)/);
          if (indexMatch) {
            const categoryIndex = parseInt(indexMatch[1], 10);
            if (categoryIndex >= 0 && categoryIndex < categories.length) {
              categoryLabel = categories[categoryIndex].category;
            }
          }
        }

        const finalConditionLabel = categoryLabel || conditionLabel || '';

        const edgeParams: EdgeParams = {
          ...params,
          source: params.source,
          target: params.target,
          condition_label: finalConditionLabel,
          data: {
            send_from: params.source,
            send_to: params.target,
            edge_type:
              sourceType === NodeType.AgentCategorizer.name ||
                sourceType === NodeType.AgentCondition.name
                ? EdgeType.CASE
                : EdgeType.None,
            condition: null,
            conditions: null,
          },
        };

        const siblings = eds.filter(e => e.source === params.source);
        const index = siblings.length;
        const offset = ((index % 5) - 2) * 8;

        let newEdge;
        if (
          sourceType === NodeType.AgentCategorizer.name ||
          sourceType === NodeType.AgentCondition.name
        ) {
          newEdge = ConditionalEdge({ ...edgeParams, data: { ...edgeParams.data, offset } }, EDGE_TYPE_CURVE);
        } else {
          newEdge = NoneConditionalEdge({ ...edgeParams, data: { ...edgeParams.data, offset } }, EDGE_TYPE_CURVE);
        }

        return addEdge(newEdge, eds);
      });

      const currentNodes = getNodes();
      const sourceNode = currentNodes.find(node => node.id === params.source);
      const targetNode = currentNodes.find(node => node.id === params.target);

      if (!sourceNode || !targetNode) return;

      const sourceType = getNodeTypeById(currentNodes, params.source);
      const targetType = getNodeTypeById(currentNodes, params.target);

      if (sourceType === NodeType.AgentCategorizer.name || sourceType === NodeType.AgentCondition.name) {
        const sourceInputKeys = (sourceNode.data?.input_keys as any[]) || [];
        const targetInputKeys = (targetNode.data?.input_keys as any[]) || [];

        const isCoderNode = targetType === NodeType.AgentCoder.name;

        const updatedTargetInputKeys = targetInputKeys
          .map(targetInput => {
            const matchingSourceInput = sourceInputKeys.find(sourceInput =>
              sourceInput.name === targetInput.name && sourceInput.keytable_id
            );

            if (matchingSourceInput) {
              return {
                ...targetInput,
                keytable_id: matchingSourceInput.keytable_id,
              };
            }

            return targetInput;
          })
          .filter(targetInput => {
            if (isCoderNode) {
              const keyName = targetInput?.name;
              if (keyName) {
                const keyNameStr = String(keyName).trim();
                const isConditionKey = /^condition-\d+$/.test(keyNameStr) ||
                  keyNameStr === 'condition-else' ||
                  /^condition-\w+$/.test(keyNameStr);
                if (isConditionKey) {
                  return false;
                }
              }
            }
            return true;
          });
        setNodes(nds => nds.map(node => {
          if (node.id === params.target) {
            return {
              ...node,
              data: {
                ...node.data,
                input_keys: updatedTargetInputKeys,
              },
            };
          }
          return node;
        }));
      }

      if (sourceType === NodeType.AgentGenerator.name && targetType === NodeType.AgentCoder.name) {
        const sourceOutputKeys = (sourceNode.data?.output_keys as any[]) || [];
        const targetInputKeys = (targetNode.data?.input_keys as any[]) || [];

        let updatedTargetInputKeys = [...targetInputKeys];

        sourceOutputKeys.forEach(sourceOutput => {
          if (sourceOutput.keytable_id) {
            const matchingTargetInput = updatedTargetInputKeys.find(targetInput =>
              targetInput.name === sourceOutput.name
            );

            if (matchingTargetInput) {
              const index = updatedTargetInputKeys.indexOf(matchingTargetInput);
              updatedTargetInputKeys[index] = {
                ...matchingTargetInput,
                keytable_id: sourceOutput.keytable_id,
              };
            } else {
              updatedTargetInputKeys.push({
                name: sourceOutput.name,
                required: true,
                keytable_id: sourceOutput.keytable_id,
                fixed_value: null,
              });
            }
          }
        });

        setNodes(nds => nds.map(node => {
          if (node.id === params.target) {
            return {
              ...node,
              data: {
                ...node.data,
                input_keys: updatedTargetInputKeys,
              },
            };
          }
          return node;
        }));
      }
    }, [setEdges, setNodes, getNodes, getEdges]);

    const isValidConnection = useCallback(
      (connection: { target: string; source: string; sourceHandle?: string | null; targetHandle?: string | null }) => {
      try {
      if (!connection.source || !connection.target) {
      return false;
      }
      
      if (connection.source === connection.target) {
      return false;
      }
      const isSourceOutput = connection.sourceHandle?.includes('right');
      const isTargetOutput = connection.targetHandle?.includes('right');
      
      if (isSourceOutput && isTargetOutput) {
      return false;
      }
      const isSourceInput = connection.sourceHandle?.includes('left');
      const isTargetInput = connection.targetHandle?.includes('left');
      
      if (isSourceInput && isTargetInput) {
      return false;
      }
      
      const nodes = getNodes();
      const target = nodes.find(node => node.id === connection.target);
      const source = nodes.find(node => node.id === connection.source);
      
      if (!target || !source) {
      return false;
      }
      return true;
      } catch (error) {
      return false;
      }
      },
      [getNodes]
      );

  const onDragOver = useCallback(
    (event: {
      preventDefault: () => void;
      dataTransfer: { dropEffect: string };
    }) => {
      event.preventDefault();
      event.dataTransfer.dropEffect = 'move';
    },
    []
  );

  const onDrop = useCallback(
    (event: DragEvent) => {
      event.preventDefault();
      event.stopPropagation();

      if (readOnly) {
        return;
      }

      const reactFlowType = event.dataTransfer?.getData('application/reactflow');
      const nodeType = type || reactFlowType;

      if (!nodeType) {
        return;
      }

      const position = screenToFlowPosition({
        x: event.clientX,
        y: event.clientY,
      });

      addNewNode(nodeType.toString(), position);

      if (setType) {
        setType(null);
      }
    },
    [readOnly, screenToFlowPosition, type, addNewNode, setType]
  );

  return {
    nodes,
    edges,
    onNodesChange,
    onEdgesChange,
    onConnect,
    isValidConnection,
    onDragOver,
    onDrop,
  };
};
