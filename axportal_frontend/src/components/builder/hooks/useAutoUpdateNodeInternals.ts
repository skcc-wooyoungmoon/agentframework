import { useEffect, useRef } from 'react';
import { useUpdateNodeInternals } from '@xyflow/react';

// 노드 내부 높이/레이아웃 변화 시 핸들/엣지 재계산을 자동 수행하는 훅
// 사용법: const containerRef = useAutoUpdateNodeInternals(id); <div ref={containerRef}>...</div>
export const useAutoUpdateNodeInternals = (nodeId: string) => {
  const updateNodeInternals = useUpdateNodeInternals();
  const containerRef = useRef<HTMLDivElement | null>(null);
  const timerRef = useRef<number | null>(null);
  const rafRef = useRef<number | null>(null);

  useEffect(() => {
    if (!containerRef.current) return;
    const el = containerRef.current;
    const Rz: any = (window as any).ResizeObserver;
    if (!Rz) return;

    const ro = new Rz(() => {
      // 즉시 1차 보정
      updateNodeInternals(nodeId);
      // 다음 프레임 보정
      if (rafRef.current) cancelAnimationFrame(rafRef.current);
      rafRef.current = requestAnimationFrame(() => updateNodeInternals(nodeId));
      // 잔여 레이아웃 변동 대비 소형 딜레이 보정
      if (timerRef.current) window.clearTimeout(timerRef.current as any);
      timerRef.current = window.setTimeout(() => updateNodeInternals(nodeId), 40) as any;
    });

    ro.observe(el);
    return () => {
      try { 
        ro.unobserve(el); 
      } catch (error) {
        // ResizeObserver unobserve 실패는 정상적인 경우일 수 있으므로 에러를 무시하고 계속 진행
        // (이미 관찰이 해제되었거나 요소가 DOM에서 제거된 경우 발생할 수 있음)
        if (process.env.NODE_ENV === 'development') {
          console.debug('ResizeObserver unobserve 실패 (무시됨):', error);
        }
      }
      if (timerRef.current) window.clearTimeout(timerRef.current as any);
      if (rafRef.current) cancelAnimationFrame(rafRef.current);
    };
  }, [nodeId, updateNodeInternals]);

  return containerRef;
};


