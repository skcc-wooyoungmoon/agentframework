import React from 'react';

interface NodeButtonProps {
  title: string;
  type: string;
  icon: string;
  onClick?: () => void;
  // eslint-disable-next-line no-unused-vars
  onDragStart?: (event: any, type: string) => void;
}

export const NodeButton = ({ title, type, icon, onClick, onDragStart }: NodeButtonProps) => {
  const handleButtonClick = () => {
    if (onClick) onClick();
  };

  const handleDragStart = (event: React.DragEvent<HTMLButtonElement>) => {
    if (onDragStart) {
      onDragStart(event, type);
    }
  };

  return (
    <button
      className='d-flex btn btn-light w-full items-center gap-2 border-[#DBDFE9] px-4 py-2 text-start text-[#4B5675]'
      onClick={() => {
        handleButtonClick();
      }}
      draggable={true}
      onDragStart={event => handleDragStart(event)}
    >
      <i className={['ki-filled', icon, 'text-[#444650]'].filter(e => !!e).join(' ')}></i>
      <span className='flex-1'>{title}</span>
    </button>
  );
};
