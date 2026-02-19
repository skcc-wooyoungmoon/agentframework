import { edgesAtom } from '@/components/agents/builder/atoms/AgentAtom';
import { CloseEdgeButton } from '@/components/agents/builder/pages/graph/edge/CloseEdgeButton.tsx';
import { BaseEdge, getSmoothStepPath, type EdgeProps } from '@xyflow/react';
import { useAtom } from 'jotai';

export function ButtonEdgeSmoothStep({ id, sourceX, sourceY, targetX, targetY, sourcePosition, targetPosition, style = {}, markerEnd, label }: EdgeProps) {
  const [, setEdges] = useAtom(edgesAtom);
  const [edgePath, labelX, labelY] = getSmoothStepPath({
    sourceX,
    sourceY,
    sourcePosition,
    targetX,
    targetY,
    targetPosition,
  });

  const onEdgeClick = () => {
    setEdges(edges => edges.filter(edge => edge.id !== id));
  };

  return (
    <>
      <BaseEdge path={edgePath} markerEnd={markerEnd} style={style} labelBgPadding={[5, 5]} />
      <CloseEdgeButton labelX={labelX} labelY={labelY} label={label} onEdgeClick={onEdgeClick} />
    </>
  );
}
