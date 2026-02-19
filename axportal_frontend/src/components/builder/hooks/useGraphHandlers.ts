import { edgesAtom, nodesAtom } from '@/components/builder/atoms/AgentAtom';
import { useGraphActions } from '@/components/builder/hooks/useGraphActions.ts';
import { ConditionalEdge, NoneConditionalEdge, RecursiveEdge } from '@/components/builder/pages/graph/edge';
import { type Category, type Condition, type EdgeParams, EdgeType, NodeType } from '@/components/builder/types/Agents';
import { useDnD } from '@/components/builder/utils/DnDContext.tsx';
import { EDGE_TYPE_CURVE, getNodeTypeById } from '@/components/builder/utils/GraphUtils.ts';
import { addEdge, applyEdgeChanges, applyNodeChanges, type Connection, useReactFlow } from '@xyflow/react';
import { useAtom } from 'jotai';
import { useCallback } from 'react';

export const useGraphHandlers = () => {
  // export const useGraphHandlers = (readOnly: boolean = false) => {
  // Synchronize with Atom state.
  const [nodes, setNodes] = useAtom(nodesAtom);
  const [edges, setEdges] = useAtom(edgesAtom);
  const { getNodes, screenToFlowPosition } = useReactFlow();
  const [, setType] = useDnD();
  const { addNewNode } = useGraphActions();

  const onNodesChange = useCallback((changes: any) => setNodes(nds => applyNodeChanges(changes, nds)), [setNodes, nodes]);

  const onEdgesChange = useCallback((changes: any) => setEdges(eds => applyEdgeChanges(changes, eds)), [setEdges]);

  const onConnect = useCallback(
    (params: Connection) => {
      setEdges(eds => {
        const sourceType = getNodeTypeById(nodes, params.source);
        const sourceHandleId = params.sourceHandle || '';
        const categoryId = sourceHandleId.split('-').slice(1).join('-');
        const conditionId = sourceHandleId.split('-').slice(1).join('-');

        const sourceNode = nodes.find(node => node.id === params.source);
        const categories = (sourceNode?.data?.categories as Category[]) || [];
        const category = categories.find((cat: Category) => cat.id === categoryId);
        const categoryLabel = category?.category || '';

        const conditions = (sourceNode?.data?.conditions as Condition[]) || [];
        const condition = conditions.find((con: Condition) => con.id === conditionId);
        const conditionLabel = condition?.id || '';

        const edgeParams: EdgeParams = {
          ...params,
          source: params.source,
          target: params.target,
          condition_label: categoryLabel || conditionLabel || '',
          data: {
            send_from: params.source,
            send_to: params.target,
            edge_type:
              sourceType === NodeType.AgentCategorizer.name || sourceType === NodeType.AgentCondition.name || sourceType === NodeType.AgentReviewer.name
                ? EdgeType.SMOOTHSTEP
                : EdgeType.None,
            category,
          },
        };

        if (sourceType === NodeType.AgentCategorizer.name || sourceType === NodeType.AgentCondition.name) {
          return addEdge(ConditionalEdge(edgeParams, EDGE_TYPE_CURVE), eds);
        } else if (sourceType === NodeType.AgentReviewer.name) {
          return addEdge(RecursiveEdge(edgeParams), eds);
        } else {
          return addEdge(NoneConditionalEdge(edgeParams, EDGE_TYPE_CURVE), eds);
        }
      });
    },
    [nodes, setEdges]
  );

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

  const onDragOver = useCallback((event: { preventDefault: () => void; dataTransfer: { dropEffect: string } }) => {
    event.preventDefault();
    event.dataTransfer.dropEffect = 'move';
  }, []);

  const onDrop = useCallback(
    (event: { preventDefault: () => void; clientX: number; clientY: number; dataTransfer?: DataTransfer }) => {
      event.preventDefault();
      // 디버깅: 드롭 시 dataTransfer에 뭐가 들어왔는지 확인 (droppedType 있으면 사이드바에서 드래그한 것)
      const droppedType = event.dataTransfer?.getData?.('application/reactflow');
      // 사이드바에서 드래그한 경우에만 노드 추가 (입력 필드 텍스트 선택 드래그 등은 무시)
      if (!droppedType) {
        if (setType) setType(null);
        return;
      }

      const position = screenToFlowPosition({
        x: event.clientX,
        y: event.clientY,
      });

      addNewNode(droppedType, position);
      if (setType) setType(null);
    },
    [screenToFlowPosition, addNewNode, setType]
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
