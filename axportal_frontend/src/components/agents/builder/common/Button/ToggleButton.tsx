import { DefaultButton } from '@/components/agents/builder/common/index.ts';
import { type ColorType } from '@/components/agents/builder/common/Button/ColorType';

export interface ToggleButtonProps {
  text: string[] | null;
  isToggle?: boolean;
  onClick: (bool: boolean) => void;
  className?: string;
  color?: ColorType;
  icon?: string[] | null;
}

export const ToggleButton = ({ text = ['Expand', 'Fold'], isToggle = false, onClick, className = '', color = 'primary', icon = [] }: ToggleButtonProps) => {
  return (
    <DefaultButton color={`${color}`} className={`${className} ${isToggle ? icon?.[0] : icon?.[1]}`} onClick={() => onClick(!isToggle)}>
      {text && text.length == 2 && (isToggle ? text[0] : text[1])}
    </DefaultButton>
  );
};
