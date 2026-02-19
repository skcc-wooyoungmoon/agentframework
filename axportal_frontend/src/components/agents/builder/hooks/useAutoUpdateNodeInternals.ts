import { useEffect, useRef } from 'react';
import { useUpdateNodeInternals } from '@xyflow/react';

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
      updateNodeInternals(nodeId);
      if (rafRef.current) cancelAnimationFrame(rafRef.current);
      rafRef.current = requestAnimationFrame(() => updateNodeInternals(nodeId));
      if (timerRef.current) window.clearTimeout(timerRef.current as any);
      timerRef.current = window.setTimeout(() => updateNodeInternals(nodeId), 40) as any;
    });

    ro.observe(el);
    return () => {
      try {
        ro.unobserve(el);
      } catch (error) {
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


