import { edgesAtom } from '@/components/agents/builder/atoms/AgentAtom';
import { CloseEdgeButton } from '@/components/agents/builder/pages/graph/edge/CloseEdgeButton.tsx';
import { BaseEdge, getStraightPath, type EdgeProps } from '@xyflow/react';
import { useAtom } from 'jotai';

export function ButtonEdgeStraight({ id, sourceX, sourceY, targetX, targetY, markerEnd, label }: EdgeProps) {
  const [, setEdges] = useAtom(edgesAtom); // 🔥 atom의 setEdges 사용
  const [edgePath, labelX, labelY] = getStraightPath({
    sourceX,
    sourceY,
    targetX,
    targetY,
  });

  const onEdgeClick = () => {
    setEdges(edges => edges.filter(edge => edge.id !== id));
  };

  return (
    <>
      <BaseEdge path={edgePath} markerEnd={markerEnd} />
      <CloseEdgeButton labelX={labelX} labelY={labelY} label={label} onEdgeClick={onEdgeClick} />
    </>
  );
}
