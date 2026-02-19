// noinspection DuplicatedCode

import { useGraphActions } from '@/components/agents/builder/hooks/useGraphActions.ts';
import { NoneConditionalEdge } from '@/components/agents/builder/pages/graph/edge';
import { type CustomEdge, type CustomNode, EdgeType, type InputKeyItem, NodeType, type OutputKeyItem } from '@/components/agents/builder/types/Agents';
import { type Data } from '@/components/agents/builder/types/InferencePrompts';
import { EDGE_TYPE_CURVE } from '@/components/agents/builder/utils/GraphUtils.ts';

type PositionProp = {
  x: number;
  y: number;
};

export const QuickStartActions = () => {
  const { createNewNode } = useGraphActions();

  const initItem: InputKeyItem = {
    name: 'query',
    required: true,
    keytable_id: '',
    fixed_value: null,
  };

  const createInputNode = (position: PositionProp) => {
    let newNode = createNewNode(NodeType.Input.name, position, []);
    const initInputItems: InputKeyItem[] = [initItem];
    if (newNode) {
      newNode.data = {
        ...newNode.data,
        type: NodeType.Input.name,
        id: newNode.id,
        name: newNode.data.name,
        description: newNode.data.description,
        input_keys: newNode.data.input_keys || initInputItems,
        innerData: newNode.data.innerData,
      };
    }
    return newNode;
  };

  const createRetrieverNode = (position: PositionProp, repo_id: string, queryKeyTableId: string) => {
    if (repo_id === null || repo_id === undefined || repo_id === '') {
      return undefined;
    }

    let newNode = createNewNode(NodeType.RetrieverRetriever.name, position, []);
    const initInputItem: InputKeyItem[] = [
      {
        name: 'query',
        required: false,
        keytable_id: queryKeyTableId,
        fixed_value: null,
      },
      {
        name: 'rewritten_queries',
        required: false,
        keytable_id: '',
        fixed_value: null,
      },
    ];
    const initOutputItem: OutputKeyItem[] = [
      {
        name: 'context',
        keytable_id: 'context_' + newNode?.id,
      },
      {
        name: 'docs',
        keytable_id: 'docs_' + newNode?.id,
      },
    ];
    if (newNode) {
      newNode.data = {
        ...newNode.data,
        type: NodeType.RetrieverRetriever.name,
        id: newNode.id,
        retriever_id: newNode.id,
        name: newNode.data.name || '',
        description: '',
        step: 'knowledge_retriever',
        kind: 'knowledge',
        knowledge_retriever: {
          repo_id: repo_id,
        },
        input_keys: initInputItem,
        output_keys: initOutputItem,
        innerData: newNode.data.innerData,
      };
    }

    return newNode;
  };

  const createGeneratorNode = (position: PositionProp, queryKeyTableId: string, llm?: any, prompt?: Data, rNodeContextKeyTableId?: string) => {
    let newNode = createNewNode(NodeType.AgentGenerator.name, position, []);
    const initInputItem: InputKeyItem[] = [
      {
        name: 'query',
        required: false,
        keytable_id: queryKeyTableId,
        fixed_value: null,
      },
      {
        name: 'context',
        required: false,
        keytable_id: rNodeContextKeyTableId ?? '',
        fixed_value: null,
      },
    ];
    const initOutputItem: OutputKeyItem[] = [
      {
        name: 'content',
        keytable_id: 'content__' + newNode?.id,
      },
    ];
    if (newNode) {
      // console.log('createGeneratorNode:', newNode);
      newNode.data = {
        ...newNode.data,
        type: NodeType.AgentGenerator.name,
        id: newNode.id,
        name: newNode.data.name,
        description: newNode.data.description,
        serving_name: llm?.name,
        serving_model: llm?.model_name,
        prompt_id: prompt?.uuid,
        fewshot_id: null,
        tool_ids: [],
        variables: [], // SKT AI Platform Python 호환성
        input_keys: initInputItem,
        output_keys: initOutputItem,
        innerData: newNode.data.innerData,
      };
    }

    return newNode;
  };

  const createOutputNode = (position: PositionProp, contentKeyTableId: string) => {
    let newNode = createNewNode(NodeType.OutputSelector.name, position, []);
    const initItem: InputKeyItem = {
      name: 'content',
      required: false,
      keytable_id: contentKeyTableId,
      fixed_value: null,
    };
    if (newNode) {
      newNode.data = {
        ...newNode.data,
        type: NodeType.OutputSelector.name,
        id: newNode.id,
        name: newNode.data.name,
        description: newNode.data.description,
        input_keys: [initItem],
        innerData: newNode.data.innerData,
      };
    }

    return newNode;
  };

  const createEdge = (inputNode?: CustomNode, retrieverNode?: CustomNode, generatorNode?: CustomNode, outputNode?: CustomNode) => {
    let newEdges: CustomEdge[] = [];
    if (retrieverNode) {
      let i_r_edge = NoneConditionalEdge(null, EDGE_TYPE_CURVE) as CustomEdge;
      i_r_edge.source = inputNode?.id || '';
      i_r_edge.target = retrieverNode?.id || '';
      i_r_edge.data = {
        send_from: inputNode?.id || '',
        send_to: retrieverNode?.id || '',
        edge_type: EdgeType.None,
        conditions: null,
        condition: null,
      };
      newEdges.push(i_r_edge);

      let r_g_edge = NoneConditionalEdge(null, EDGE_TYPE_CURVE) as CustomEdge;
      r_g_edge.source = retrieverNode?.id || '';
      r_g_edge.target = generatorNode?.id || '';
      r_g_edge.data = {
        send_from: retrieverNode?.id || '',
        send_to: generatorNode?.id || '',
        edge_type: EdgeType.CASE,
        conditions: null,
        condition: null,
      };
      newEdges.push(r_g_edge);

      let g_o_edge = NoneConditionalEdge(null, EDGE_TYPE_CURVE) as CustomEdge;
      g_o_edge.source = generatorNode?.id || '';
      g_o_edge.target = outputNode?.id || '';
      g_o_edge.data = {
        send_from: generatorNode?.id || '',
        send_to: outputNode?.id || '',
        edge_type: EdgeType.None,
        conditions: null,
        condition: null,
      };
      newEdges.push(g_o_edge);
    } else {
      let i_g_edge = NoneConditionalEdge(null, EDGE_TYPE_CURVE) as CustomEdge;
      i_g_edge.source = inputNode?.id || '';
      i_g_edge.target = generatorNode?.id || '';
      i_g_edge.data = {
        send_from: inputNode?.id || '',
        send_to: generatorNode?.id || '',
        edge_type: EdgeType.None,
        conditions: null,
        condition: null,
      };
      newEdges.push(i_g_edge);

      let g_o_edge = NoneConditionalEdge(null, EDGE_TYPE_CURVE) as CustomEdge;
      g_o_edge.source = generatorNode?.id || '';
      g_o_edge.target = outputNode?.id || '';
      g_o_edge.data = {
        send_from: generatorNode?.id || '',
        send_to: outputNode?.id || '',
        edge_type: EdgeType.None,
        conditions: null,
        condition: null,
      };
      newEdges.push(g_o_edge);
    }

    return newEdges;
  };

  return {
    createInputNode,
    createGeneratorNode,
    createRetrieverNode,
    createOutputNode,
    createEdge,
  };
};
