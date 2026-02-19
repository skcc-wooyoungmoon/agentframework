import React, { Fragment } from 'react';
import type { UIRadioProps } from './types';

/**
 * Radio 컴포넌트
 *
 * className 기반으로 스타일을 적용
 */
export const UIRadio2: React.FC<UIRadioProps> = ({ label, className = '', children, onChange, ...inputProps }) => {
  const handleChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    if (onChange) {
      onChange(event.target.checked, event.target.value);
    }
  };

  const buildClassName = () => {
    const classes: string[] = [];

    classes.push('rdo');

    // Add custom className if provided
    if (className) {
      classes.push(className);
    }

    return classes.filter(Boolean).join(' ');
  };

  return (
    <Fragment>
      <label className={buildClassName()}>
        <input type='radio' onChange={handleChange} {...inputProps} />
        {label && <span>{label}</span>}
      </label>
      {children && <div className='group-radio-children'>{children}</div>} {/* children 하위 텍스트 있을경우 */}
    </Fragment>
  );
};
