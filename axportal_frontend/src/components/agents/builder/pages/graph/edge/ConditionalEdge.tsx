import { type CustomEdge, type EdgeParams } from '@/components/agents/builder/types/Agents';
import { getEdgeId } from '@/components/agents/builder/utils/GraphUtils.ts';
import { MarkerType } from '@xyflow/react';

export const ConditionalEdge = (params: EdgeParams, _type: string): CustomEdge => {
  const getCategory = () => {
    if (params.data?.category?.category) {
      return params.data.category;
    }

    const categoryName = params.condition_label || params.sourceHandle?.split('-').slice(1).join('-') || '';

    return {
      id: categoryName,
      category: categoryName,
      description: '',
    };
  };

  const category = getCategory();
  const categoryLabel = category.category;

  const source_handle = params.sourceHandle || `handle-${categoryLabel}`;

  return {
    ...params,
    id: getEdgeId(),
    source: String(params.source || ''),
    target: String(params.target || ''),
    type: 'case',
    source_handle,
    sourceHandle: source_handle,
    style: {
      stroke: '#000000',
      strokeWidth: 2.5,
    },
    label: categoryLabel,
    condition_label: categoryLabel,
    markerEnd: {
      type: MarkerType.ArrowClosed,
      width: 10,
      height: 10,
      color: '#000000',
    },
    data: {
      ...params.data,
      category,
      condition_label: categoryLabel,
    },
  };
};
