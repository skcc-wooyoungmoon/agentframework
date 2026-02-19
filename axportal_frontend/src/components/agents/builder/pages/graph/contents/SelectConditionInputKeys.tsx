import React, { useEffect, useState } from 'react';

import { type ConditionItem, type InputKeyItem } from '@/components/agents/builder/types/Agents';

interface conditionProps {
  selectedCondition: ConditionItem;
  inputKeys: InputKeyItem[];

  onChange?: (selectedInputKeyName: string, selectedInputKeyTableId: string) => void;
}

export const SelectConditionInputKeys = ({ selectedCondition, inputKeys, onChange }: conditionProps) => {
  const [conditionKeyTableId, setConditionKeyTableId] = useState<string>('');

  useEffect(() => {
    const currentKeyTableId = selectedCondition.input_key?.keytable_id ?? '';
    setConditionKeyTableId(currentKeyTableId);
  }, [selectedCondition]);

  useEffect(() => {
    if (conditionKeyTableId && !inputKeys.some(item => item.keytable_id === conditionKeyTableId)) {
      setConditionKeyTableId('');
      if (onChange) {
        onChange('', '');
      }
    }
  }, [conditionKeyTableId, inputKeys, onChange]);

  const handleInputKeysChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const selectedInputKeyTableId = e.target.value;
    const selectedOptionText = e.target.options[e.target.selectedIndex]?.text ?? '';

    setConditionKeyTableId(selectedInputKeyTableId);

    if (onChange) {
      onChange(selectedInputKeyTableId ? selectedOptionText : '', selectedInputKeyTableId);
    }
  };

  return (
    <>
      <select
        id='condition_input_keys'
        name='condition_input_keys'
        onChange={handleInputKeysChange}
        className='b-selectbox'
        value={conditionKeyTableId}
      >
        <option value=''>{'전체'}</option>
        {inputKeys.map(
          inputKey =>
            inputKey.keytable_id && (
              <option key={inputKey.keytable_id} value={inputKey.keytable_id ?? ''}>
                {inputKey.name}
              </option>
            )
        )}
      </select>
    </>
  );
};
