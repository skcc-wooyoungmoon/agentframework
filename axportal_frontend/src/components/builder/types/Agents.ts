import type { generateMCPCatalog } from '@/services/agent/mcp/types';
import type { GetAgentToolByIdResponse } from '@/services/agent/tool/types';
import type { Edge, EdgeMarker, Node } from '@xyflow/react';

export const NodeType = {
  Note: {
    name: 'note',
    title: 'Note',
    enable_config: false,
  },
  Input: {
    name: 'input__basic',
    title: 'Input',
    enable_config: false,
  },
  OutputFormatter: {
    name: 'output__chat',
    // name: 'output__formatter',
    title: 'Output Chat',
    enable_config: false,
  },
  OutputSelector: {
    name: 'output__keys',
    // name: 'output__selector',
    title: 'Output Keys',
    enable_config: false,
  },
  AgentGenerator: {
    name: 'agent__generator',
    title: 'Generator',
    enable_config: true,
  },
  AgentApp: {
    name: 'agent__app',
    title: 'AgentApp',
    enable_config: false,
  },
  AgentReactor: {
    name: 'agent__reactor',
    title: 'ReACTor',
    enable_config: false,
  },
  AgentCategorizer: {
    name: 'agent__categorizer',
    title: 'Categorizer',
    enable_config: false,
  },
  AgentCondition: {
    name: 'condition',
    title: 'Condition',
    enable_config: false,
  },
  AgentCoder: {
    name: 'agent__coder',
    title: 'Code',
    enable_config: false,
  },
  AgentUnion: {
    name: 'union',
    title: 'Union',
    enable_config: false,
  },
  AgentMerger: {
    name: 'merger',
    title: 'Merger (Deprecated)',
    enable_config: false,
  },
  AgentReviewer: {
    name: 'agent__reviewer',
    title: 'Reviewer',
    enable_config: true,
  },
  RewriterHyDE: {
    name: 'retriever__rewriter_hyde',
    title: 'Rewriter HyDE',
    enable_config: false,
  },
  RewriterMultiQuery: {
    name: 'retriever__rewriter_multiquery',
    title: 'Rewriter MultiQuery',
    enable_config: false,
  },
  RetrieverRetriever: {
    // name: 'retriever__main',
    name: 'retriever__knowledge',
    title: 'Retriever',
    enable_config: false,
  },
  RetrieverReRanker: {
    name: 'retriever__doc_reranker',
    title: 'Doc ReRanker',
    enable_config: false,
  },
  RetrieverCompressor: {
    name: 'retriever__doc_compressor',
    title: 'Doc Compressor',
    enable_config: false,
  },
  RetrieverFilter: {
    name: 'retriever__doc_filter',
    title: 'Doc Filter',
    enable_config: false,
  },
  Tool: {
    name: 'tool',
    title: 'Tool',
    enable_config: false,
  },
  DUMMY: {
    name: 'dummy',
    title: 'Dummy',
    enable_config: false,
  },
};

export const EXCLUDE_KEY_TABLE_TYPE = [NodeType.Note.name, NodeType.OutputSelector.name, NodeType.OutputFormatter.name];
export const AGENT_NODE_TYPE_LIST = [NodeType.AgentGenerator.name, NodeType.AgentCategorizer.name];

export const EdgeType = {
  None: 'none',
  CASE: 'case',
  SMOOTHSTEP: 'smoothstep',
};

export type AgentGraph = {
  id?: string;
  name: string;
  description?: string;
  graph_config?: {
    [key: string]: NodeConfig;
  };
  graph: {
    edges: CustomEdge[];
    nodes: CustomNode[];
  };
};

export const ChatType = {
  HUMAN: 'human',
  AI: 'ai',
};

export interface NodeConfig {
  max_iterations: number;
}

export interface Agent {
  id: string;
  project_id: string;
  name: string;
  description: string;
  graph_config?: {
    [key: string]: NodeConfig;
  };
  edges: CustomEdge[];
  nodes: CustomNode[];
  created_at: string;
  updated_at: string;
  created_by?: string;
  updated_by?: string;
}

export type AgentQueryResponse = Array<Agent>;

export type KeyTableData = {
  id: string;
  name: string;
  key: string;
  value: string;
  isGlobal?: boolean | null;
  nodeId: string;
  nodeType: string;
  nodeName: string;
  node: CustomNode | null;
};

// Custom Node Data
export interface CustomNodeInnerData extends Record<string, unknown> {
  title?: string;
  isRun: boolean;
  isDone?: boolean;
  isError?: boolean;
  isToggle: boolean;
  logData?: string[];

  [key: string]: unknown;
}

export interface EdgeConditionItems {
  when: string;
  operator: string | '==';
  value: string;
}

export interface EdgeSchema {
  send_from: string;
  send_to: string;
  edge_type: string | 'none';
  conditions: EdgeConditionItems[] | null;
  condition: string | null;
}

// Custom Edge Data
export interface CustomEdgeData {
  id: string;
  type: string | null;
  source: string | null;
  target: string | null;
  source_handle: string | null;
  target_handle: string | null;
  marker_start: EdgeMarker | null;
  marker_end: EdgeMarker | null;
  reconnectable: string | null;
  condition_label: string | null;
  label: string | null;
  data: EdgeSchema;
}

export interface key {
  name: string;
  value: string | any | null;
  node_type: string;
  node_id: string;
}

export interface NodeKeyInfo {
  name: string;
  value: string | any | null;
  node_type: string;
  node_id: string;
}

export interface RetrieverApiInfo {
  api_url: string | null;
  api_key: string | null;
}

export interface RetrieverVectordbInfo {
  embedding_model_id: string;
  vectordb_id: string;
}

export interface llm_config {
  api_key: string | null;
  serving_name: string;
}

export interface RetrievalStepQueryRewriter {
  llm_chain: {
    llm_config: llm_config;
    prompt: string;
  };
  include_ori_query: boolean | null;
}

export interface RetrievalOptions {
  top_k: string | number | null;
  filter?: Record<string, any> | null;
  file_ids?: string[] | null;
  keywords?: string[] | null;
  order_by?: string | null;
  threshold: string | number | null;
  vector_field: string | null;
  retrieval_mode?: string | null;
  hybrid_dense_ratio?: number | string | null;
}

export interface llm_chain {
  llm_config: llm_config;
  prompt: string;
}

export interface query_rewriter {
  llm_chain: llm_chain;
  include_ori_query: boolean;
}

export type RetrievalStepKnowledge = {
  repo_id: string;
  connect_type: string | null;
  // connect_info: RetrieverApiInfo | RetrieverVectordbInfo | null;
  retrieval_options: RetrievalOptions | null;
};

export interface RetrieverLlmChin {
  llm_config: llm_config;
  prompt: string;
}

export type QueryMessage = {
  content: string;
  // content: string | Record<string, any>;
  type: string;
};

export type QueryAgentGraph = {
  graph_id: string;
  input_data: {
    messages: QueryMessage[];
    additional_kwargs: {};
  };
};

export type ChatQueryResponse = {
  timestamp: number;
  code: number;
  detail: string;
  traceId: string | null;
  data: {
    content: string | Record<string, any>;
    additional_kwargs: Record<string, any>;
  } | null;
  payload: any | null;
};

export type AppChatQueryResponse = {
  timestamp?: number;
  // data: {
  output: {
    content: string;
    messages: any[];
    additional_kwargs: Record<string, any>;
    next_node: string | null;
  };
  metadata: {
    run_id: string;
    feedback_tokens: any[];
  };
  // }
};

export interface Items {
  key: key; //input node key
  reference_key: NodeKeyInfo | null; // input
}

export type CustomNode = Node & {
  id: string;
  type: string;
  position: { x: number; y: number };
  source_position: string | null;
  target_position: string | null;
  data: { innerData: CustomNodeInnerData } & (
    | InputNodeDataSchema
    | OutputKeysDataSchema
    | OutputChatDataSchema
    | RetrieverDataSchema
    | QueryRewriterDataSchema
    | ContextRefinerDataSchema
    | GeneratorDataSchema
    | AgentAppDataSchema
    | CoderDataSchema
    | ReACTorDataSchema
    | CategorizerDataSchema
    | ConditionDataSchema
    | UnionDataSchema
    | ReviewerDataSchema
    | ToolDataSchema
    | ReRankerDataSchema
    | Record<string, unknown>
  );
};

export type InputKeyItem = {
  name: string;
  required: boolean;
  description?: string;
  keytable_id: string | null;
  fixed_value: any;
  object_type?: 'string' | 'JSON' | 'prompt' | 'retriever' | 'LLM parameters';
};

export type OutputKeyItem = {
  name: string;
  keytable_id: string;
};

export type InputNodeDataSchema = {
  type: string | null;
  id: string | null;
  name: string;
  description: string | null;
  input_keys: InputKeyItem[];
};

export type OutputKeysDataSchema = {
  type: string | null;
  id: string | null;
  name: string;
  description: string | null;
  input_keys: InputKeyItem[];
};
export type UnionDataSchema = {
  type: string | null;
  id: string | null;
  name: string;
  description: string | null;
  format_string: string | null;
  input_keys: InputKeyItem[] | null;
  output_keys: OutputKeyItem[] | null;
};
export type OutputChatDataSchema = {
  type: string | null;
  id: string | null;
  name: string;
  description: string | null;
  format_string: string | null;
};

export type RetrieverDataSchema = {
  type: string | null;
  id: string | null;
  name: string | null;
  description: string | null;
  knowledge_retriever: KnowledgeRetriever;
  input_keys: InputKeyItem[];
  output_keys: OutputKeyItem[];
};

export type ReviewerDataSchema = {
  type: string | null;
  id: string | null;
  name: string | null;
  description: string | null;
  serving_name: string;
  serving_model: string;
  prompt_id: string;
  max_review_attempts: number;
  input_keys: InputKeyItem[];
  output_keys: OutputKeyItem[];
};

export type QueryRewriterDataSchema = {
  type: string | null;
  id: string | null;
  retriever_id: string | null;
  name: string | null;
  description: string | null;
  kind: string;
  query_rewriter: object;
  input_keys: InputKeyItem[];
  output_keys: OutputKeyItem[];
};

export type RewriterHyDEDataSchema = {
  id: string | null;
  name: string | null;
  type: string | null;
  input_keys: InputKeyItem[];
  description: string | null;
  output_keys: OutputKeyItem[];
  retriever_id: string | null;
  query_rewriter: query_rewriter | null;
};

export type RewriterMultiQueryDataSchema = {
  id: string | null;
  name: string | null;
  type: string | null;
  input_keys: InputKeyItem[];
  description: string | null;
  output_keys: OutputKeyItem[];
  retriever_id: string | null;
  query_rewriter: query_rewriter;
};

export type ContextRefinerDataSchema = {
  type: string | null;
  id: string | null;
  name: string | null;
  retriever_id: string | null;
  description: string | null;
  kind: string;
  context_refiner: object | null;
  input_keys: InputKeyItem[];
  output_keys: OutputKeyItem[];
};

export type GeneratorDataSchema = {
  type: string | null;
  id: string | null;
  name: string | null;
  description: string | null;
  serving_name: string;
  serving_model: string;
  prompt_id: string;
  fewshot_id: string | null;
  tool_ids: Array<string> | null;
  tools: GetAgentToolByIdResponse[];
  input_keys: InputKeyItem[];
  output_keys: OutputKeyItem[];
  mcp_catalogs: generateMCPCatalog[] | null;
  model_parameters: Record<string, any> | null;
};

export type AgentAppDataSchema = {
  type: string | null;
  id: string | null;
  name: string | null;
  description: string | null;
  agent_app_id: string;
  api_key: string;
  input_keys: InputKeyItem[];
  output_keys: OutputKeyItem[];
};

export type CompressorDataSchema = {
  type: string | null;
  id: string | null;
  name: string | null;
  description: string | null;
  input_keys: InputKeyItem[];
  output_keys: OutputKeyItem[];
  kind: string;
  retriever_id: string | null;
  context_refiner: {
    llm_chain: {
      prompt: string;
      llm_config: {
        api_key: string | null;
        serving_name: string | null;
      };
    };
    rerank_cnf: string | null;
  };
};

export type FilterDataSchema = {
  type: string | null;
  id: string | null;
  name: string | null;
  description: string | null;
  input_keys: InputKeyItem[];
  output_keys: OutputKeyItem[];
  kind: string;
  retriever_id: string | null;
  context_refiner: {
    llm_chain: {
      prompt: string;
      llm_config: {
        api_key: string | null;
        serving_name: string | null;
      };
    };
    rerank_cnf: string | null;
  };
};

export type ToolInfo = {
  id: string;
  name: string;
  tool_type: string;
  description: string;
  code: string;
};

export type CoderDataSchema = {
  type: string | null;
  id: string | null;
  name: string | null;
  description: string | null;
  code: string;
  input_keys: InputKeyItem[] | null;
  output_keys: OutputKeyItem[] | null;
};

export type ReACTorDataSchema = {
  type: string | null;
  id: string | null;
  name: string | null;
  description: string | null;
  serving_name: string;
  serving_model: string;
  prompt_id: string;
  fewshot_id: string | null;
  tool_ids: string[] | null;
  tools: GetAgentToolByIdResponse[];
  input_keys: InputKeyItem[];
  output_keys: OutputKeyItem[];
};

export type CategorizerDataSchema = {
  type: string | null;
  id: string | null;
  name: string | null;
  description: string | null;
  serving_name: string;
  serving_model: string;
  prompt_id: string;
  categories: Array<{
    category: string;
    description: string;
  }> | null;
  input_keys: InputKeyItem[];
  output_keys: OutputKeyItem[];
};

export type ConditionItem = {
  id: string;
  type: string;
  operator: string;
  input_key: {
    name: string;
    required: boolean;
    keytable_id: string | null;
  };
  value: {
    name: string;
    required: boolean;
    fixed_value?: string | null;
    keytable_id: string | null;
  };
};

export type ConditionDataSchema = {
  type: string | null;
  id: string | null;
  name: string;
  description: string | null;
  conditions: Array<ConditionItem>;
  default_condition: string;
  input_keys: InputKeyItem[] | null;
  output_keys: OutputKeyItem[] | null;
};

// TODO 삭제
export type MergerDataSchema = {
  type: string | null;
  id: string | null;
  name: string;
  description: string | null;
  format_string: string | null;
  input_keys: InputKeyItem[] | null;
  output_keys: OutputKeyItem[] | null;
};

export type ToolDataSchema = {
  type: string | null;
  id: string | null;
  description: string | null;
  name: string | null;
  tool_id: string;
  input_keys: InputKeyItem[] | null;
  output_keys: OutputKeyItem[] | null;
};

export type ReRankerDataSchema = {
  type: string | null;
  id: string | null;
  description: string | null;
  name: string | null;
  retriever_id: string | null;
  kind: string;
  context_refiner: {
    rerank_cnf: {
      model_info: {
        serving_name: string | null;
        api_key: string | null;
      };
    };
  };
  input_keys: InputKeyItem[] | null;
  output_keys: OutputKeyItem[] | null;
};

export type KnowledgeRetriever = {
  script?: string | null;
  repo_id: string;
  repo_kind?: string;
  index_name?: string | null;
  project_id?: string | null;
  embedding_info?: Record<string, any> | null;
  knowledge_info?: Record<string, any> | null;
  retrieval_options: RetrievalOptions | null;
  vectordb_conn_info?: Record<string, any> | null;
  active_collection_id?: string | null;
};

export interface Category {
  id: string;
  category: string;
  description: string;
}

export interface Condition {
  id: string;
  type: string;
  operator: string;
  input_key: {
    name: string;
    required: boolean;
    keytable_id: string | null;
  };
  value: {
    name: string;
    required: boolean;
    fixed_value?: string | null;
    keytable_id: string | null;
  };
}

export interface EdgeParams extends Omit<Edge, 'id' | 'label'> {
  source: string;
  target: string;
  source_handle?: string;
  target_handle?: string;
  label?: string;
  condition_label?: string;
  data?: {
    category?: Category;
    conditions?: Condition[] | undefined | null;
    send_from?: string;
    send_to?: string;
    edge_type?: string;
    [key: string]: any;
  };
}

export interface CustomEdge extends Edge {
  id: string;
  source: string;
  target: string;
  type?: string;
  source_handle?: string;
  sourceHandle?: string;
  target_handle?: string;
  label?: string;
  condition_label?: string;
  style?: {
    stroke?: string;
    strokeWidth?: number;
    strokeDasharray?: string;
  };
  data?: {
    category?: Category;
    conditions?: Condition[] | undefined | null;
    send_from?: string;
    send_to?: string;
    edge_type?: string;
    [key: string]: any;
  };
}

export interface DeployedAppInfo {
  id: string;
  name: string;
  description: string;
}

export type ResourceItem = {
  resource_id: string;
  resource_type: string;
};

export type Permissions = {
  origin_resource: ResourceItem;
  target_resources: ResourceItem[];
};

export type GuardrailsInfo = {
  serving_name: string;
};
