import { type CustomEdge, type EdgeParams } from '@/components/builder/types/Agents';
import { getEdgeId } from '@/components/builder/utils/GraphUtils.ts';
import { MarkerType } from '@xyflow/react';

export const RecursiveEdge = (params: EdgeParams): CustomEdge => {
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
  const conditionLable = categoryLabel.split('-')[categoryLabel.split('-').length - 1];

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
      stroke: params.sourceHandle === 'handle-condition-pass' ? '#22C55E' : '#ff0000',
      strokeWidth: 3,
      strokeDasharray: '5, 5',
    },
    label: categoryLabel,
    condition_label: conditionLable,
    markerEnd: {
      type: MarkerType.ArrowClosed,
      width: 10,
      height: 10,
      color: params.sourceHandle === 'handle-condition-pass' ? '#22C55E' : '#ff0000',
    },
    data: {
      ...params.data,
      category,
      condition_label: conditionLable,
    },
  };
};
