import React from 'react';

import { type ColorType } from '@/components/builder/common/button/ColorType';

interface ButtonProps {
  text?: string;
  color?: ColorType;
  // eslint-disable-next-line no-unused-vars
  onClick?: (e?: React.MouseEvent) => void;
  className?: string;
  children?: React.ReactNode;
  disabled?: boolean;
  type?: 'button' | 'submit' | 'reset';
  icon?: string;
  style?: React.CSSProperties;
}

// ColorType을 Tailwind 색상 코드로 매핑
const mapColorToVariant = (color: ColorType): any => {
  const colorMap: Record<ColorType, any> = {
    primary: '#2563eb',
    secondary: '#4b5563',
    success: '#22c55e',
    danger: '#dc2626',
    warning: '#eab308',
    info: '#06b6d4',
    light: '#f3f4f6',
    dark: '#1f2937',
  };
  return colorMap[color] || '#f3f4f6';
};

export const DefaultButton = ({ text, color = 'primary', onClick, className = '', children, disabled = false, type = 'button', icon, style: _style }: ButtonProps) => {
  const bgColor = mapColorToVariant(color);
  return (
    <button
      style={{
        backgroundColor: bgColor,
        color: 'white',
        padding: '8px 16px',
        borderRadius: '8px',
        border: 'none',
        cursor: disabled ? 'not-allowed' : 'pointer',
        opacity: disabled ? 0.5 : 1,
        fontSize: '14px',
        fontWeight: 500,
        transition: 'all 0.2s',
        ..._style
      }}
      onClick={onClick as any}
      className={className}
      disabled={disabled}
      type={type}
      title={text}
    >
      {icon && <span style={{ marginRight: '8px' }}>{icon}</span>}
      {children || (text ?? '확인')}
    </button>
  );
};
