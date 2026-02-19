import { type BaseRecord } from '@/components/builder/types/table.ts';
import React from 'react';

interface SelectionCellProps<T extends BaseRecord> {
  data: T;
  isSelected: boolean;
  onSelect: (data: T) => void;
}

function SelectionCell<T extends BaseRecord>({ data, isSelected, onSelect }: SelectionCellProps<T>) {
  const handleChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    event.stopPropagation();
    onSelect(data);
  };

  return (
    <div className='flex justify-center'>
      <input className='h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded' type='checkbox' checked={isSelected} onChange={handleChange} />
    </div>
  );
}

export { SelectionCell };
