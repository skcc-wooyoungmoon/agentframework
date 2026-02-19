import { edgesAtom } from '@/components/agents/builder/atoms/AgentAtom';
import { tracingNodeIdAtom } from '@/components/agents/builder/atoms/messagesAtom.ts';
import { CloseEdgeButton } from '@/components/agents/builder/pages/graph/edge/CloseEdgeButton.tsx';
import { BaseEdge, getSmoothStepPath, type EdgeProps } from '@xyflow/react';
import { useAtom } from 'jotai/index';
import { useEffect, useState } from 'react';

const DEFAULT_EDGE_COLOR = '#111111';
const HOVER_EDGE_COLOR = '#4675ff';
const REVIEWER_PASS_COLOR = '#22C55E';
const REVIEWER_FAIL_COLOR = '#EF4444';

export const ButtonEdgeCurve = ({
  id,
  sourceX,
  sourceY,
  targetX,
  targetY,
  sourcePosition,
  targetPosition,
  style = {},
  markerEnd,
  data,
  label,
  ...props
}: EdgeProps & { sourceHandle?: string; targetHandle?: string }) => {
  const sourceHandle = (props as any).sourceHandle;
  const [, setEdges] = useAtom(edgesAtom); // 🔥 atom의 setEdges 사용
  const [isHovered, setIsHovered] = useState(false);
  const [isNoti, setIsNoti] = useState(false);
  const [edgePath, labelX, labelY] = getSmoothStepPath({
    sourceX,
    sourceY,
    sourcePosition,
    targetX,
    targetY,
    targetPosition,
    borderRadius: 5,
  });
  const [tracingNodeId] = useAtom(tracingNodeIdAtom);

  const onEdgeClick = () => {
    setEdges(edges => edges.filter(edge => edge.id !== id));
  };

  useEffect(() => {
    if (data?.send_to && tracingNodeId.includes(String(data.send_to))) {
      setIsNoti(true);
    } else {
      setIsNoti(false);
    }
  }, [tracingNodeId, data?.send_to]);

  const sanitizedStyle = { ...(style as any) };
  if (sanitizedStyle?.strokeDasharray) {
    delete sanitizedStyle.strokeDasharray;
  }

  const getEdgeColor = () => {
    const existingStroke = sanitizedStyle.stroke;
    if (existingStroke === REVIEWER_PASS_COLOR || existingStroke === REVIEWER_FAIL_COLOR) {
      return existingStroke;
    }

    const propSourceHandle = sourceHandle || '';
    const dataSourceHandle = (data as any)?.sourceHandle || '';
    const dataSourceHandleField = (data as any)?.source_handle || '';
    const conditionLabel = (data as any)?.condition_label || '';

    const effectiveSourceHandle = propSourceHandle || dataSourceHandle || dataSourceHandleField || conditionLabel || '';

    if (effectiveSourceHandle === 'reviewer_pass' ||
      effectiveSourceHandle === 'pass' ||
      conditionLabel === 'pass' ||
      effectiveSourceHandle.includes('reviewer_pass') ||
      effectiveSourceHandle.includes('pass')) {
      return REVIEWER_PASS_COLOR;
    }

    if (effectiveSourceHandle === 'reviewer_fail' ||
      effectiveSourceHandle === 'fail' ||
      conditionLabel === 'fail' ||
      effectiveSourceHandle.includes('reviewer_fail') ||
      effectiveSourceHandle.includes('fail')) {
      return REVIEWER_FAIL_COLOR;
    }

    return existingStroke || DEFAULT_EDGE_COLOR;
  };

  const baseStroke = getEdgeColor();
  const isReviewerEdge = baseStroke === REVIEWER_PASS_COLOR || baseStroke === REVIEWER_FAIL_COLOR;
  const shouldHighlight = (isHovered || isNoti) && !isReviewerEdge;
  const currentStroke = shouldHighlight ? HOVER_EDGE_COLOR : baseStroke;

  const getMarkerEnd = () => {
    if (shouldHighlight) return '';
    if (isReviewerEdge) {
      const colorSuffix = baseStroke.replace('#', '');
      return `url(#reviewer-arrow-${colorSuffix})`;
    }
    return markerEnd;
  };

  return (
    <g
      onMouseEnter={() => setIsHovered(true)}
      onMouseLeave={() => setIsHovered(false)}
    >
      {isReviewerEdge && (
        <defs>
          <marker
            id={`reviewer-arrow-${baseStroke.replace('#', '')}`}
            markerWidth="12"
            markerHeight="12"
            refX="10"
            refY="3"
            orient="auto"
            markerUnits="strokeWidth"
          >
            <path
              d="M0,0 L0,6 L9,3 z"
              fill={currentStroke}
            />
          </marker>
        </defs>
      )}
      <BaseEdge
        path={edgePath}
        markerEnd={getMarkerEnd()}
        style={{
          ...sanitizedStyle,
          stroke: currentStroke,
          strokeWidth: 2.5,
          transition: 'stroke 0.2s ease',
          fill: 'none',
        }}
        labelBgPadding={[5, 5]}
      />
      {isHovered && (
        <CloseEdgeButton
          labelX={labelX}
          labelY={labelY}
          label={label}
          onEdgeClick={onEdgeClick}
        />
      )}
    </g>
  );
}
