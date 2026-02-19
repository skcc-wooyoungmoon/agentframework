import { type ConditionItem } from '@/components/builder/types/Agents';
import React from 'react';

interface conditionProps {
  selectedCondition: ConditionItem;
  // eslint-disable-next-line no-unused-vars
  onChange?: (selectedType: string) => void;
}

export const SelectConditionType = ({ selectedCondition, onChange }: conditionProps) => {
  const handleTypeChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const selectedType = e.target.value;

    if (onChange) {
      onChange(selectedType);
    }
  };

  return (
    <>
      <select id='condition_type' name='condition_type' onChange={handleTypeChange} className='b-selectbox' value={selectedCondition.type}>
        <option key='string' value='string'>
          {'문자열'}
        </option>
        <option key='number' value='number'>
          {'숫자'}
        </option>
      </select>
    </>
  );
};
