import React, { useEffect, useState } from 'react';

import { type ConditionItem, type InputKeyItem } from '@/components/builder/types/Agents';

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
    if (selectedCondition.input_key.keytable_id === undefined || selectedCondition.input_key.keytable_id === null || selectedCondition.input_key.keytable_id === '') {
      if (inputKeys.length > 0) {
        selectedCondition.input_key.name = inputKeys[0].name ?? '';
        selectedCondition.input_key.keytable_id = inputKeys[0].keytable_id ?? '';
        setConditionKeyTableId(inputKeys[0].keytable_id ?? '');
      }
    } else {
      setConditionKeyTableId(selectedCondition.input_key.keytable_id);
    }
  }, [inputKeys, selectedCondition]);

  const handleInputKeysChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const selectedInputKeyName = e.target.name;
    const selectedInputKeyTableId = e.target.value;

    if (onChange) {
      onChange(selectedInputKeyName, selectedInputKeyTableId);
    }
  };

  return (
    <select id='condition_input_keys' name='condition_input_keys' onChange={handleInputKeysChange} className='b-selectbox' value={conditionKeyTableId}>
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
  );
};
