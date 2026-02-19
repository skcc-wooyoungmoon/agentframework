import { getEdgeId } from '@/components/builder/utils/GraphUtils.ts';
import { MarkerType } from '@xyflow/react';

const EDGE_COLORS = {
  default: '#111111',
  reviewerPass: '#22C55E',
  reviewerFail: '#EF4444',
};

export const NoneConditionalEdge = (params: any, _type: string) => {
  // console.log(type);
  // console.log('NoneConditionalEdge params', params);

  // Reviewer 노드의 Handle에 따라 색상 결정
  let strokeColor = EDGE_COLORS.default; // 기본 검정

  if (params.sourceHandle === 'reviewer_pass') {
    strokeColor = EDGE_COLORS.reviewerPass; // 연두색 (PASS)
  } else if (params.sourceHandle === 'reviewer_fail') {
    strokeColor = EDGE_COLORS.reviewerFail; // 빨간색 (FAIL)
  }

  return {
    ...params,
    id: getEdgeId(),
    // type: 'buttonEdgeStraight',
    type: 'none',
    // style: { stroke: '#4675ff', strokeWidth: 3 },
    style: { stroke: strokeColor, strokeWidth: 2.5 },
    markerEnd: {
      type: MarkerType.ArrowClosed,
      width: 12,
      height: 12,
      color: strokeColor,
    },
  };
};
