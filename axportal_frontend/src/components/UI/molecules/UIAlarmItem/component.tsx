import type { UIAlarmItemProps } from './types';

export const UIAlarmItem: React.FC<UIAlarmItemProps> = ({ id, title, description, time, isRead = false, type = 'normal', onClick, className = '', actionButton }) => {
  const handleClick = (event: React.MouseEvent<HTMLDivElement>) => {
    if (onClick) {
      onClick({ id, title, description, time, isRead }, event);
    }
  };

  return (
    <div className={'item' + (isRead ? ' is-read' : '') + ' ' + className} onClick={handleClick}>
      <div className='item-head'>
        <div className={'title' + (type === 'dot' ? ' dot' : '')}>{title}</div>
      </div>
      <div className='item-desc'>{description}</div>
      <div className='item-time flex justify-between items-center'>
        <span>{time}</span>
        {actionButton && <span>{actionButton}</span>}
      </div>
    </div>
  );
};
