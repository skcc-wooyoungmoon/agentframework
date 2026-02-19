import {
    AgentAppNode,
    CategorizerNode,
    CodeNode,
    CompressorNode,
    ConditionNode,
    FilterNode,
    GeneratorNode,
    InputNode,
    MergerNode,
    NoteNode,
    OutputFormatterNode,
    OutputSelectorNode,
    ReRankerNode,
    RetrieverNode,
    ReviewerNode,
    RewriterHyDeNode,
    RewriterMultiQueryNode,
    ToolNode,
    UnionNode,
} from '@/components/agents/builder/pages/graph/node';

import { ButtonEdgeCurve } from '@/components/agents/builder/pages/graph/edge/ButtonEdgeCurve.tsx';

const TEMPLATE_AUTO_CONNECT_ENABLED = false;
const EDGE_MARKER_SIZE = { width: 12, height: 12 };
const EDGE_COLORS = {
    default: '#111111',
    condition: '#000000',
    reviewerPass: '#22C55E',
    reviewerFail: '#EF4444',
};
const MAX_EDGE_LOAD_RETRIES = 3;
const EDGE_LOAD_TIMEOUT = 10000;

const NODE_TYPE: any = {
    note: NoteNode,
    input__basic: InputNode,
    output__selector: OutputSelectorNode,
    output__keys: OutputSelectorNode,
    output__formatter: OutputFormatterNode,
    output__chat: OutputFormatterNode,
    agent__generator: GeneratorNode,
    agent__categorizer: CategorizerNode,
    agent__coder: CodeNode,
    agent__app: AgentAppNode,
    agent__reviewer: ReviewerNode,
    retriever__rewriter_hyde: RewriterHyDeNode,
    retriever__rewriter_multiquery: RewriterMultiQueryNode,
    retriever__knowledge: RetrieverNode,
    retriever__doc_reranker: ReRankerNode,
    retriever__doc_compressor: CompressorNode,
    retriever__doc_filter: FilterNode,
    tool: ToolNode,
    condition: ConditionNode,
    union: UnionNode,
    merger: MergerNode,
};

const EDGE_TYPE: any = {
    none: ButtonEdgeCurve,
    case: ButtonEdgeCurve,
};

export { TEMPLATE_AUTO_CONNECT_ENABLED, EDGE_MARKER_SIZE, EDGE_COLORS, MAX_EDGE_LOAD_RETRIES, EDGE_LOAD_TIMEOUT, NODE_TYPE, EDGE_TYPE };