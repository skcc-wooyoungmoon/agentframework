import { getEdgeId } from '@/components/agents/builder/utils/GraphUtils.ts';
import { MarkerType } from '@xyflow/react';

const EDGE_COLORS = {
  default: '#111111',
  reviewerPass: '#22C55E',
  reviewerFail: '#EF4444',
};

export const NoneConditionalEdge = (params: any, _type: string) => {
  let strokeColor = EDGE_COLORS.default;

  if (params.sourceHandle === 'reviewer_pass') {
    strokeColor = EDGE_COLORS.reviewerPass;
  } else if (params.sourceHandle === 'reviewer_fail') {
    strokeColor = EDGE_COLORS.reviewerFail;
  }

  return {
    ...params,
    id: getEdgeId(),
    type: 'none',
    style: { stroke: strokeColor, strokeWidth: 2.5 },
    markerEnd: {
      type: MarkerType.ArrowClosed,
      width: 12,
      height: 12,
      color: strokeColor,
    },
  };
};
