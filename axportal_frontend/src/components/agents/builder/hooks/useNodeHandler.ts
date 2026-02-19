
export const useNodeHandler = () => {
    const stopPropagation = (e: React.SyntheticEvent) => e.stopPropagation();
    const preventAndStop = (e: React.SyntheticEvent) => {
        e.preventDefault();
        e.stopPropagation();
    };
    const autoResize = (target: HTMLTextAreaElement) => {
        target.style.height = 'auto';
        target.style.height = target.scrollHeight + 'px';
    };

    return {
        stopPropagation,
        preventAndStop,
        autoResize
    }
}


