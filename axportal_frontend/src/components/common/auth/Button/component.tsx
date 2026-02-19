import { UIButton2 } from '@/components/UI';
import type { UIButtonProps } from '@/components/UI/atoms/UIButton2';
import { type AuthInfo } from '@/constants/auth';
import { useAuthCheck, useAuthMode } from '@/hooks/common/auth';
import { useMemo } from 'react';

export const Button = ({ children, auth, onClick, ...props }: UIButtonProps & { auth?: AuthInfo }) => {
  const { withAuthEvent, checkAuth } = useAuthCheck();
  const { isVisibleMode } = useAuthMode();

  // 노출여부
  // const show = useMemo(() => !(!isVisibleMode && !checkAuth(auth)), []);
  const show = useMemo(() => isVisibleMode || checkAuth(auth), []);

  // Enter 또는 Space 키로 버튼 활성화 방지
  const handleKeyDown = (e: React.KeyboardEvent<HTMLButtonElement>) => {
    if ((e.key === 'Enter' || e.key === ' ') && !checkAuth(auth)) {
      e.preventDefault();
      e.stopPropagation();
      return;
    }
    props.onKeyDown?.(e);
  };

  return (
    show && (
      <UIButton2
        {...props}
        onClick={withAuthEvent(auth, onClick)}
        onKeyDown={props.onKeyDown ? handleKeyDown : undefined}
        onKeyPress={props.onKeyPress ? withAuthEvent(auth, props.onKeyPress) : undefined}
        onKeyUp={props.onKeyUp ? withAuthEvent(auth, props.onKeyUp) : undefined}
        onFocus={props.onFocus ? withAuthEvent(auth, props.onFocus) : undefined}
        onBlur={props.onBlur ? withAuthEvent(auth, props.onBlur) : undefined}
        onContextMenu={props.onContextMenu ? withAuthEvent(auth, props.onContextMenu) : undefined}
        onDragStart={props.onDragStart ? withAuthEvent(auth, props.onDragStart) : undefined}
      >
        {children}
      </UIButton2>
    )
  );
};
