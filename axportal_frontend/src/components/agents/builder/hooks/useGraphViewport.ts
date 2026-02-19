import { useCallback, useRef } from 'react';

interface Viewport {
    x: number;
    y: number;
    zoom: number;
}

interface UseGraphViewportProps {
    graphId: string;
    reactFlowInstance: any;
    saveGlobalViewport: (graphId: string, viewport: Viewport) => void;
}

interface UseGraphViewportReturn {
    isUserInteraction: React.MutableRefObject<boolean>;
    lastViewport: React.MutableRefObject<Viewport>;
    fitViewApplied: React.MutableRefObject<boolean>;
    onWheel: (event: React.WheelEvent) => void;
    onNodeDragStart: (event: React.MouseEvent) => void;
    onNodeDrag: (event: React.MouseEvent) => void;
    onNodeDragStop: (event: React.MouseEvent) => void;
    onMoveStart: () => void;
    onMove: () => void;
    onMoveEnd: () => void;
    onViewportChange: (viewport: Viewport) => void;
    markFitViewApplied: () => void;
    resetFitViewApplied: () => void;
}

const scheduleInteractionEnd = (
    ref: React.MutableRefObject<number | null>,
    callback: () => void
) => {
    if (ref.current) cancelAnimationFrame(ref.current);
    ref.current = requestAnimationFrame(() => {
        ref.current = requestAnimationFrame(callback);
    });
};

const isNoDragTarget = (event: React.MouseEvent): boolean => {
    const target = event.target as HTMLElement;
    return target.tagName === 'TEXTAREA' || target.tagName === 'INPUT' || !!target.closest('.nodrag');
};

export const useGraphViewport = ({
    graphId,
    reactFlowInstance,
    saveGlobalViewport,
}: UseGraphViewportProps): UseGraphViewportReturn => {
    const isUserInteraction = useRef<boolean>(false);
    const lastViewport = useRef<Viewport>({ x: 0, y: 0, zoom: 1 });
    const fitViewApplied = useRef<boolean>(false);
    const interactionRafRef = useRef<number | null>(null);
    const viewportRestoreRafRef = useRef<number | null>(null);

    const onWheel = useCallback((event: React.WheelEvent) => {
        if (!event.ctrlKey && !event.metaKey) {
            return;
        }

        if (reactFlowInstance) {
            try {
                isUserInteraction.current = true;
                const delta = event.deltaY;
                const currentZoom = reactFlowInstance.getZoom();
                const zoomChange = delta > 0 ? -0.1 : 0.1;
                const newZoom = Math.min(Math.max(currentZoom + zoomChange, 0.3), 0.8);

                reactFlowInstance.zoomTo(newZoom);
                const currentViewport = reactFlowInstance.getViewport();
                lastViewport.current = currentViewport;
                saveGlobalViewport(graphId, currentViewport);

                scheduleInteractionEnd(interactionRafRef, () => {
                    isUserInteraction.current = false;
                });
            } catch {
                isUserInteraction.current = false;
            }
        }
    }, [graphId, reactFlowInstance, saveGlobalViewport]);

    const onNodeDragStart = useCallback((event: React.MouseEvent) => {
        if (isNoDragTarget(event)) {
            event.preventDefault();
            return;
        }
        isUserInteraction.current = true;
    }, []);

    const onNodeDrag = useCallback((event: React.MouseEvent) => {
        if (isNoDragTarget(event)) {
            event.preventDefault();
            return;
        }
        isUserInteraction.current = true;
    }, []);

    const onNodeDragStop = useCallback(() => {
        scheduleInteractionEnd(interactionRafRef, () => {
            isUserInteraction.current = false;
        });
    }, []);

    const onMoveStart = useCallback(() => {
        isUserInteraction.current = true;
    }, []);

    const onMove = useCallback(() => {
        isUserInteraction.current = true;
    }, []);

    const onMoveEnd = useCallback(() => {
        scheduleInteractionEnd(interactionRafRef, () => {
            isUserInteraction.current = false;
        });
    }, []);

    const onViewportChange = useCallback((viewport: Viewport) => {
        if (!fitViewApplied.current || isUserInteraction.current) {
            lastViewport.current = viewport;
            saveGlobalViewport(graphId, viewport);
        } else {
            const zoomChanged = Math.abs(viewport.zoom - lastViewport.current.zoom) > 0.001;
            if (zoomChanged) {
                if (viewportRestoreRafRef.current) cancelAnimationFrame(viewportRestoreRafRef.current);
                viewportRestoreRafRef.current = requestAnimationFrame(() => {
                    if (reactFlowInstance) {
                        reactFlowInstance.setViewport(lastViewport.current);
                    }
                });
            } else {
                lastViewport.current = viewport;
                saveGlobalViewport(graphId, viewport);
            }
        }
    }, [graphId, reactFlowInstance, saveGlobalViewport]);

    const markFitViewApplied = useCallback(() => {
        fitViewApplied.current = true;
    }, []);

    const resetFitViewApplied = useCallback(() => {
        fitViewApplied.current = false;
    }, []);

    return {
        isUserInteraction,
        lastViewport,
        fitViewApplied,
        onWheel,
        onNodeDragStart,
        onNodeDrag,
        onNodeDragStop,
        onMoveStart,
        onMove,
        onMoveEnd,
        onViewportChange,
        markFitViewApplied,
        resetFitViewApplied,
    };
};
