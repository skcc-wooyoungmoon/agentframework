import type { RefObject } from 'react';

const SIZE_CONFIG = {
    MIN_SIZE: 100,
    HEADER_OFFSET: 70,
    CHANGE_THRESHOLD: 1,
    PARENT_SELECTOR: 'graph-wrap',
} as const;

interface Size {
    width: number;
    height: number;
}

type SetSizeCallback = (size: Size) => void;

const getCurrentSize = (element: HTMLElement): Size => ({
    width: element.offsetWidth || element.clientWidth || 0,
    height: element.offsetHeight || element.clientHeight || 0,
});

const findParentByClass = (
    element: HTMLElement,
    className: string
): HTMLElement | null => {
    let parent = element.parentElement;
    while (parent && !parent.classList.contains(className)) {
        parent = parent.parentElement;
    }
    return parent;
};

const calculateTargetSize = (container: HTMLElement): Size => {
    const parent = findParentByClass(container, SIZE_CONFIG.PARENT_SELECTOR);

    if (parent) {
        const { width, height } = parent.getBoundingClientRect();
        if (width > 0 && height > 0) {
            return { width, height };
        }
    }

    // 뷰포트 폴백
    return {
        width: Math.max(window.innerWidth, SIZE_CONFIG.MIN_SIZE),
        height: Math.max(window.innerHeight - SIZE_CONFIG.HEADER_OFFSET, SIZE_CONFIG.MIN_SIZE),
    };
};

const shouldUpdateSize = (current: Size, target: Size): boolean => {
    const isZeroSize = current.width === 0 || current.height === 0;
    const hasSignificantChange =
        Math.abs(current.width - target.width) > SIZE_CONFIG.CHANGE_THRESHOLD ||
        Math.abs(current.height - target.height) > SIZE_CONFIG.CHANGE_THRESHOLD;

    return isZeroSize || hasSignificantChange;
};

const applyContainerStyles = (element: HTMLElement, size: Size): void => {
    const styles: Record<string, string> = {
        width: `${size.width}px`,
        height: `${size.height}px`,
        'min-width': `${SIZE_CONFIG.MIN_SIZE}px`,
        'min-height': `${SIZE_CONFIG.MIN_SIZE}px`,
        display: 'block',
        overflow: 'hidden',
        'box-sizing': 'border-box',
    };

    Object.entries(styles).forEach(([property, value]) => {
        element.style.setProperty(property, value, 'important');
    });
};

export const setContainerSize = (
    containerRef: RefObject<HTMLDivElement | null>,
    setContainerSizeState: SetSizeCallback
): boolean => {
    const container = containerRef.current;
    if (!container) return false;

    const currentSize = getCurrentSize(container);
    const targetSize = calculateTargetSize(container);

    if (!shouldUpdateSize(currentSize, targetSize)) {
        return false;
    }

    setContainerSizeState(targetSize);
    applyContainerStyles(container, targetSize);

    return true;
};

export const safeUpdateDimensions = (
    containerRef: RefObject<HTMLDivElement | null>,
    reactFlowInstance: any,
    setContainerSizeState: (size: { width: number; height: number }) => void
): void => {
    if (!containerRef.current || !reactFlowInstance) return;

    const container = containerRef.current;
    const width = container.offsetWidth || container.clientWidth || 0;
    const height = container.offsetHeight || container.clientHeight || 0;

    if (width === 0 || height === 0) {
        setContainerSize(containerRef, setContainerSizeState);
        return;
    }
    if (reactFlowInstance.updateDimensions && typeof reactFlowInstance.updateDimensions === 'function') {
        reactFlowInstance.updateDimensions();
    }
};

export const saveViewportToStorage = (graphId: string, viewport: { x: number; y: number; zoom: number }): void => {
    const key = `graph-viewport-${graphId}`;
    const viewportString = JSON.stringify(viewport);
    if (viewportString.length > 5 * 1024 * 1024) {
        return;
    }
    localStorage.setItem(key, viewportString);
};

export const loadViewportFromStorage = (graphId: string): { x: number; y: number; zoom: number } | null => {
    const key = `graph-viewport-${graphId}`;
    const viewportString = localStorage.getItem(key);
    if (viewportString) {
        return JSON.parse(viewportString);
    }

    return null;
};

export const removeViewportFromStorage = (graphId: string): void => {
    const key = `graph-viewport-${graphId}`;
    localStorage.removeItem(key);
};
