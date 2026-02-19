import { CloseEdgeButton } from '@/components/builder/pages/graph/edge/CloseEdgeButton.tsx';
import { BaseEdge, getStraightPath, useReactFlow, type EdgeProps } from '@xyflow/react';
import { useState } from 'react';

const HOVER_EDGE_COLOR = '#2563eb'; // hover 시 색상

export function ButtonEdgeStraight({ id, sourceX, sourceY, targetX, targetY, style = {}, markerEnd, label }: EdgeProps) {
  const { setEdges } = useReactFlow();
  const [isHovered, setIsHovered] = useState(false);
  const [edgePath, labelX, labelY] = getStraightPath({
    sourceX,
    sourceY,
    targetX,
    targetY,
  });

  const onEdgeClick = () => {
    // console.log('onEdgeClick');
    setEdges(edges => edges.filter(edge => edge.id !== id));
  };

  const sanitizedStyle = { ...(style as any) };

  // 기본 스타일 유지 (색상, 두께, 점선 여부)
  const baseStroke = sanitizedStyle.stroke || '#111111';
  const baseStrokeWidth = sanitizedStyle.strokeWidth || 2.5;
  const baseStrokeDasharray = sanitizedStyle.strokeDasharray;

  // hover 시에만 색상과 두께 변경
  const currentStroke = isHovered ? HOVER_EDGE_COLOR : baseStroke;
  const currentStrokeWidth = isHovered ? baseStrokeWidth + 1 : baseStrokeWidth; // hover 시 1px 증가

  return (
    <g onMouseEnter={() => setIsHovered(true)} onMouseLeave={() => setIsHovered(false)}>
      <BaseEdge
        path={edgePath}
        markerEnd={markerEnd}
        style={{
          ...sanitizedStyle,
          stroke: currentStroke,
          strokeWidth: currentStrokeWidth,
          strokeDasharray: baseStrokeDasharray, // 점선 여부 유지
          transition: 'stroke 0.2s ease, strokeWidth 0.2s ease',
          fill: 'none',
        }}
      />
      {isHovered && <CloseEdgeButton labelX={labelX} labelY={labelY} label={label} onEdgeClick={onEdgeClick} />}
    </g>
  );
}
