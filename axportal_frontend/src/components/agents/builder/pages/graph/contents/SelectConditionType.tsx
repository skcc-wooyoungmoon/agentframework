import { type ConditionItem } from '@/components/agents/builder/types/Agents';
import React from 'react';

interface conditionProps {
  selectedCondition: ConditionItem;
  onChange?: (selectedType: string) => void;
  className?: string;
}

export const SelectConditionType = ({ selectedCondition, onChange, className = 'select max-w-24 text-xs' }: conditionProps) => {
  const handleTypeChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const selectedType = e.target.value;

    if (onChange) {
      onChange(selectedType);
    }
  };

  return (
    <>
      <select id='condition_type' name='condition_type' onChange={handleTypeChange} className={className} value={selectedCondition.type}>
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
