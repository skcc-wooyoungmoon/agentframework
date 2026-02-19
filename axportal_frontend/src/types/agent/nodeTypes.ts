export interface NodeType {
  id: string;
  name: string;
  description: string;
  icon: string;
  category: NodeCategory;
  configSchema?: any;
  defaultConfig?: any;
}

export enum NodeCategory {
  INPUT = 'input',
  OUTPUT = 'output',
  PROCESSING = 'processing',
  TOOL = 'tool',
  DOCUMENT = 'document',
  UTILITY = 'utility',
}

export const NODE_TYPES: NodeType[] = [
  // Input 노드들
  {
    id: 'note',
    name: 'Note',
    description: '메모를 추가합니다',
    icon: '📝',
    category: NodeCategory.INPUT,
  },
  {
    id: 'input',
    name: 'Input',
    description: '사용자 입력을 받습니다',
    icon: '📥',
    category: NodeCategory.INPUT,
  },

  // Output 노드들
  {
    id: 'output_keys',
    name: 'Output Keys',
    description: '키 값을 출력합니다',
    icon: '🔑',
    category: NodeCategory.OUTPUT,
  },
  {
    id: 'output_chat',
    name: 'Output Chat',
    description: '채팅 메시지를 출력합니다',
    icon: '💬',
    category: NodeCategory.OUTPUT,
  },

  // Processing 노드들
  {
    id: 'generator',
    name: 'Generator',
    description: '텍스트를 생성합니다',
    icon: '⚙️',
    category: NodeCategory.PROCESSING,
  },
  {
    id: 'code',
    name: 'Code',
    description: '코드를 실행합니다',
    icon: '💻',
    category: NodeCategory.PROCESSING,
  },
  {
    id: 'categorizer',
    name: 'Categorizer',
    description: '카테고리를 분류합니다',
    icon: '📊',
    category: NodeCategory.PROCESSING,
  },

  // Document 노드들
  {
    id: 'rewriter_hyde',
    name: 'Rewriter HyDE',
    description: 'HyDE 방식으로 문서를 재작성합니다',
    icon: '📄',
    category: NodeCategory.DOCUMENT,
  },
  {
    id: 'rewriter_multiquery',
    name: 'Rewriter MultiQuery',
    description: '다중 쿼리로 문서를 재작성합니다',
    icon: '📄',
    category: NodeCategory.DOCUMENT,
  },
  {
    id: 'retriever',
    name: 'Retriever',
    description: '문서를 검색합니다',
    icon: '🔍',
    category: NodeCategory.DOCUMENT,
  },
  {
    id: 'doc_reranker',
    name: 'DOC ReRanker',
    description: '문서를 재순위화합니다',
    icon: '📈',
    category: NodeCategory.DOCUMENT,
  },
  {
    id: 'doc_compressor',
    name: 'DOC Compressor',
    description: '문서를 압축합니다',
    icon: '🗜️',
    category: NodeCategory.DOCUMENT,
  },
  {
    id: 'doc_filter',
    name: 'DOC Filter',
    description: '문서를 필터링합니다',
    icon: '🔧',
    category: NodeCategory.DOCUMENT,
  },

  // Tool 노드들
  {
    id: 'tool',
    name: 'Tool',
    description: '외부 도구를 사용합니다',
    icon: '🛠️',
    category: NodeCategory.TOOL,
  },

  // Utility 노드들
  {
    id: 'new_node',
    name: 'New Node',
    description: '새로운 커스텀 노드입니다',
    icon: '🆕',
    category: NodeCategory.UTILITY,
  },
];

export const getNodeTypeById = (id: string): NodeType | undefined => {
  return NODE_TYPES.find(nodeType => nodeType.id === id);
};

export const getNodeTypesByCategory = (category: NodeCategory): NodeType[] => {
  return NODE_TYPES.filter(nodeType => nodeType.category === category);
};
