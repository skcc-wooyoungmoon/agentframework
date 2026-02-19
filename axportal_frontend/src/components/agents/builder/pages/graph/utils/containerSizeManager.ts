export interface ScheduleConfig {
    immediate?: boolean;
    rafCount?: number;
}

export const waitForFrames = (frameCount: number = 2): Promise<void> => {
    return new Promise(resolve => {
        let count = 0;
        const tick = () => {
            count++;
            if (count >= frameCount) {
                resolve();
            } else {
                requestAnimationFrame(tick);
            }
        };
        requestAnimationFrame(tick);
    });
};

export const rafUntil = (
    checkFn: () => boolean,
    onSuccess: () => void,
    maxFrames: number = 60
): (() => void) => {
    let frameCount = 0;
    let rafId: number | null = null;
    let cancelled = false;

    const check = () => {
        if (cancelled) return;

        if (checkFn()) {
            onSuccess();
            return;
        }

        frameCount++;
        if (frameCount < maxFrames) {
            rafId = requestAnimationFrame(check);
        }
    };

    rafId = requestAnimationFrame(check);

    return () => {
        cancelled = true;
        if (rafId) cancelAnimationFrame(rafId);
    };
};

export const scheduleContainerSizeUpdates = (
    setContainerSize: () => void | boolean,
    config: ScheduleConfig = {}
): (() => void) => {
    const { immediate = true, rafCount = 2 } = config;

    const rafIds: number[] = [];

    if (immediate) {
        setContainerSize();
    }

    let count = 0;
    const scheduleNext = () => {
        if (count < rafCount) {
            const id = requestAnimationFrame(() => {
                setContainerSize();
                count++;
                scheduleNext();
            });
            rafIds.push(id);
        }
    };
    scheduleNext();

    return () => {
        rafIds.forEach(cancelAnimationFrame);
    };
};
