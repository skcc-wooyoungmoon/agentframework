import React, { useState } from 'react';

interface TooltipProps {
  title: React.ReactNode;
  children: React.ReactElement;
  placement?: 'bottom-end' | 'bottom-start' | 'bottom' | 'left-end' | 'left-start' | 'left' | 'right-end' | 'right-start' | 'right' | 'top-end' | 'top-start' | 'top';
  className?: string;
}

// Simple custom tooltip component
const DefaultTooltip = ({ title, children, placement = 'top', className = '' }: TooltipProps) => {
  const [isVisible, setIsVisible] = useState(false);

  const handleMouseEnter = () => setIsVisible(true);
  const handleMouseLeave = () => setIsVisible(false);

  const getPlacementClasses = () => {
    switch (placement) {
      case 'bottom':
        return 'top-full left-1/2 transform -translate-x-1/2 mt-1';
      case 'left':
        return 'right-full top-1/2 transform -translate-y-1/2 mr-1';
      case 'right':
        return 'left-full top-1/2 transform -translate-y-1/2 ml-1';
      default: // top
        return 'bottom-full left-1/2 transform -translate-x-1/2 mb-1';
    }
  };

  return (
    <div className={`relative inline-block whitespace-nowrap ${className}`} onMouseEnter={handleMouseEnter} onMouseLeave={handleMouseLeave}>
      {children}
      {isVisible && (
        <div className={`absolute z-50 px-2 py-1.5 text-xs font-normal text-white bg-gray-800 rounded-md shadow-lg whitespace-nowrap ${getPlacementClasses()}`}>{title}</div>
      )}
    </div>
  );
};

export { DefaultTooltip };
