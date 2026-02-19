// UIButton2.tsx
import { forwardRef, useRef, useState } from 'react';
import { UIIcon2 } from '../UIIcon2/component';

import type { UIButtonProps } from './types';

export const UIButton2 = forwardRef<HTMLButtonElement, UIButtonProps>(
  ({ children, leftIcon, rightIcon, onClick, className = '', preventDoubleClick = true, debounceDelay = 300, disabled, ...buttonProps }, ref) => {
    const [isProcessing, setIsProcessing] = useState(false);
    const lastClickTimeRef = useRef<number>(0); // debounce를 위한 마지막 클릭 시간

    const buildButtonClass = () => {
      const classes: string[] = [];

      // Add custom className if provided
      if (className) {
        classes.push(className);
      }

      return classes.filter(Boolean).join(' ');
    };

    const handleClick = async (e: React.MouseEvent<HTMLButtonElement>) => {
      // preventDoubleClick이 false면 기존 동작
      if (!preventDoubleClick) {
        onClick?.(e);
        return;
      }

      const now = Date.now();

      // debounce 체크: 일정 시간 내 재클릭이면 무시
      if (isProcessing || disabled || now - lastClickTimeRef.current < debounceDelay) {
        e.preventDefault();
        e.stopPropagation();
        return;
      }


      // 마지막 클릭 시간 업데이트
      lastClickTimeRef.current = now;

      // 처리 시작
      setIsProcessing(true);

      try {
        // onClick이 Promise를 반환할 수 있으므로 await
        await onClick?.(e);
      } finally {
        // 작업 완료 후 상태 해제
        setIsProcessing(false);
      }
    };

    return (
      <button
        type={'button'}
        onClick={handleClick}
        className={buildButtonClass()}
        disabled={disabled || (preventDoubleClick && isProcessing)}
        {...buttonProps}
        ref={ref}
      >
        {leftIcon && <UIIcon2 {...leftIcon} />}
        {children}
        {rightIcon && <UIIcon2 {...rightIcon} />}
      </button>
    );
  }
);
