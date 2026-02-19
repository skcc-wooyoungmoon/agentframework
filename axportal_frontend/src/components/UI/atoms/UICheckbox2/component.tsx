import type { UICheckboxProps } from './types';

/**
 * Radio 컴포넌트
 *
 * className 기반으로 스타일을 적용
 */
export const UICheckbox2: React.FC<UICheckboxProps> = ({ label, className = '', onChange, ...inputProps }) => {
  const handleChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    event.stopPropagation(); // 이벤트 전파 방지
    if (onChange) {
      onChange(event.target.checked, event.target.value, event);
    }
  };

  const buildClassName = () => {
    const classes: string[] = [];

    classes.push('chk');

    // Add custom className if provided
    if (className) {
      classes.push(className);
    }

    return classes.filter(Boolean).join(' ');
  };

  return (
    <label className={buildClassName()}>
      <input type='checkbox' onChange={handleChange} {...inputProps} />
      {label && <span>{label}</span>}
    </label>
  );
};
