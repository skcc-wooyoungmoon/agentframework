import { type ConditionItem } from '@/components/builder/types/Agents';
import React, { useEffect, useState } from 'react';

interface operationProps {
  selectedCondition: ConditionItem;
  // eslint-disable-next-line no-unused-vars
  onChange?: (selectedOperator: string) => void;
}

export const SelectConditionOperation = ({ selectedCondition, onChange }: operationProps) => {
  const operatorString = [
    { key: 'equal', value: 'equal', label: '==' },
    { key: 'not_equal', value: 'not_equal', label: '!=' },
    { key: 'include', value: 'in', label: 'in' },
    { key: 'not_include', value: 'not_in', label: 'not in' },
    { key: 'contains', value: 'contains', label: 'contains' },
    { key: 'not_contains', value: 'not_contains', label: 'not contains' },
    { key: 'startswith', value: 'startswith', label: 'starts with' },
    { key: 'endswith', value: 'endswith', label: 'ends with' },
  ];
  const operatorNumber = [
    { key: 'equal', value: 'equal', label: '==' },
    { key: 'not_equal', value: 'not_equal', label: '!=' },
    { key: 'less_than', value: 'less_than', label: '<' },
    { key: 'less_than_equal_to', value: 'less_than_equal_to', label: '<=' },
    { key: 'greater_than', value: 'greater_than', label: '>' },
    {
      key: 'greater_than_equal_to',
      value: 'greater_than_equal_to',
      label: '>=',
    },
    { key: 'include', value: 'in', label: 'in' },
    { key: 'not_include', value: 'not_in', label: 'not in' },
  ];
  const [selectedOperator, setSelectedOperator] = useState<string>(selectedCondition.operator || 'equal');
  const [operatorList, setOperatorList] = useState<{ key: string; value: string; label: string }[]>(operatorString);
  useEffect(() => {
    if (selectedCondition.type === 'string') {
      setOperatorList(operatorString);
    } else {
      setOperatorList(operatorNumber);
    }
    setSelectedOperator(selectedCondition.operator);
  }, [selectedCondition.type, selectedCondition.operator]);

  const handleTypeChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const selectedOperation = e.target.value;

    if (onChange) {
      onChange(selectedOperation);
    }
  };

  return (
    <>
      <select id='operator_type' name='operator_type' onChange={handleTypeChange} className='b-selectbox' value={selectedOperator}>
        <option key={''} value={''}>
          {'선택'}
        </option>
        {operatorList.map(operator => (
          <option key={operator.key} value={operator.value}>
            {operator.label}
          </option>
        ))}
      </select>
    </>
  );
};
