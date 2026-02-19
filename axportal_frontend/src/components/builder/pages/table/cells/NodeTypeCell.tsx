import { type FC } from 'react';

interface NodeTypeCellProps {
  nodeType: string;
}

export const NodeTypeCell: FC<NodeTypeCellProps> = ({ nodeType }) => {
  const normalizedType = nodeType?.toLowerCase();
  const nodeTypeMap: Record<string, string> = {
    input__basic: 'Input',
    output__chat: 'Output Chat',
    output__keys: 'Output Keys',
    agent__generator: 'Generator',
    agent__reactor: 'ReACTor',
    agent__categorizer: 'Categorizer',
    condition: 'Condition',
    agent__coder: 'Coder',
    tool: 'Tool',
    merger: 'Merger',
    retriever__rewriter_hyde: 'Retriever HyDE',
    retriever__rewriter_multiquery: 'Retriever MultiQuery',
    retriever__knowledge: 'Retriever',
    retriever__doc_reranker: 'Doc ReRanker',
    retriever__doc_compressor: 'Doc Compressor',
    retriever__doc_filter: 'Doc Filter',
  };

  // 1. 색상 팔레트
  const palette: Record<string, string> = {
    info: '#3b82f6',
    danger: '#ef4444',
    primary: '#6366f1',
    warning: '#f59e42',
    success: '#10b981',
    default: '#e5e7eb',
  };

  // 2. 타입별 색상 키워드 매핑
  const colorMap: Record<string, keyof typeof palette> = {
    input__basic: 'info',
    output__chat: 'danger',
    output__keys: 'danger',
    agent__generator: 'primary',
    agent__reactor: 'primary',
    agent__categorizer: 'primary',
    condition: 'warning',
    agent__coder: 'warning',
    tool: 'warning',
    merger: 'warning',
    retriever__rewriter_hyde: 'success',
    retriever__rewriter_multiquery: 'success',
    retriever__knowledge: 'success',
    retriever__doc_reranker: 'success',
    retriever__doc_compressor: 'success',
    retriever__doc_filter: 'success',
  };

  const label = nodeTypeMap[normalizedType] || normalizedType;
  const colorKey = colorMap[normalizedType] || 'default';
  const bgColor = palette[colorKey];

  if (!normalizedType) return null;

  return (
    <span
      style={{
        display: 'inline-block',
        padding: '2px 10px',
        borderRadius: '12px',
        background: bgColor,
        color: '#fff',
        fontWeight: 500,
        fontSize: '0.95em',
        minWidth: 60,
        textAlign: 'center',
      }}
      className='node-type-badge'
    >
      {label}
    </span>
  );
};
