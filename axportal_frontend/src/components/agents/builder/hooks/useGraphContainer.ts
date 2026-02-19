import { useCallback, type MutableRefObject } from 'react';

interface UseGraphContainerProps {
    containerRef: MutableRefObject<HTMLDivElement | null>;
    setContainerSizeState: (size: { width: number; height: number }) => void;
}

interface UseGraphContainerReturn {
    setContainerSize: () => boolean;
    safeUpdateDimensions: (reactFlowInstance: any) => void;
    saveGlobalViewport: (graphId: string, viewport: { x: number; y: number; zoom: number }) => void;
    loadGlobalViewport: (graphId: string) => { x: number; y: number; zoom: number } | null;
    removeGlobalViewport: (graphId: string) => void;
}

export const useGraphContainer = ({
    containerRef,
    setContainerSizeState,
}: UseGraphContainerProps): UseGraphContainerReturn => {
    const setContainerSize = useCallback((): boolean => {
        if (!containerRef.current) return false;

        const container = containerRef.current;

        const currentWidth = container.offsetWidth || container.clientWidth || 0;
        const currentHeight = container.offsetHeight || container.clientHeight || 0;

        let parent = container.parentElement;
        while (parent && !parent.classList.contains('graph-wrap')) {
            parent = parent.parentElement;
        }

        let targetWidth = 0;
        let targetHeight = 0;

        if (parent) {
            const parentRect = parent.getBoundingClientRect();
            if (parentRect.width > 0 && parentRect.height > 0) {
                targetWidth = parentRect.width;
                targetHeight = parentRect.height;
            }
        }

        if (targetWidth === 0 || targetHeight === 0) {
            targetWidth = window.innerWidth || 100;
            targetHeight = (window.innerHeight - 70) || 100;
        }

        targetWidth = Math.max(targetWidth, 100);
        targetHeight = Math.max(targetHeight, 100);
        if (currentWidth === 0 || currentHeight === 0 ||
            Math.abs(currentWidth - targetWidth) > 1 ||
            Math.abs(currentHeight - targetHeight) > 1) {
            setContainerSizeState({ width: targetWidth, height: targetHeight });

            const containerStyles = {
                width: `${targetWidth}px`,
                height: `${targetHeight}px`,
                minWidth: '100px',
                minHeight: '100px',
                display: 'block',
                overflow: 'hidden',
                boxSizing: 'border-box',
            } as const;

            Object.entries(containerStyles).forEach(([property, value]) => {
                const cssProperty = property.replace(/[A-Z]/g, m => `-${m.toLowerCase()}`);
                container.style.setProperty(cssProperty, value, 'important');
            });
            return true;
        }

        return false;
    }, [containerRef, setContainerSizeState]);

    const safeUpdateDimensions = useCallback((reactFlowInstance: any): void => {
        if (!containerRef.current || !reactFlowInstance) return;

        const container = containerRef.current;
        const width = container.offsetWidth || container.clientWidth || 0;
        const height = container.offsetHeight || container.clientHeight || 0;

        if (width === 0 || height === 0) {
            setContainerSize();
            return;
        }

        if (reactFlowInstance.updateDimensions && typeof reactFlowInstance.updateDimensions === 'function') {
            reactFlowInstance.updateDimensions();
        }
    }, [containerRef, setContainerSize]);

    const saveGlobalViewport = useCallback((graphId: string, viewport: { x: number; y: number; zoom: number }): void => {
        const key = `graph-viewport-${graphId}`;
        const viewportString = JSON.stringify(viewport);
        if (viewportString.length > 5 * 1024 * 1024) return;
        localStorage.setItem(key, viewportString);

    }, []);

    const loadGlobalViewport = useCallback((graphId: string): { x: number; y: number; zoom: number } | null => {
        const key = `graph-viewport-${graphId}`;
        const viewportString = localStorage.getItem(key);
        if (viewportString) {
            return JSON.parse(viewportString);
        }
        return null;
    }, []);

    const removeGlobalViewport = useCallback((graphId: string): void => {
        const key = `graph-viewport-${graphId}`;
        localStorage.removeItem(key);
    }, []);

    return {
        setContainerSize,
        safeUpdateDimensions,
        saveGlobalViewport,
        loadGlobalViewport,
        removeGlobalViewport,
    };
};
