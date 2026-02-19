import type { UIPlaygroundCardBoxProps } from './types';

export const UIPlaygroundCardBox: React.FC<UIPlaygroundCardBoxProps> = ({ children, className = '', message, ...props }) => {
  return (
    <div className={'playground-card-box ' + className} {...props}>
      {children}
      {className === 'error' && message && <div className='msg-error'>{message}</div>}
    </div>
  );
};
