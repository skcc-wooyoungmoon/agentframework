import { type KeyTableData } from '@/components/agents/builder/types/Agents';
import { useEffect, useMemo } from 'react';

interface KeyTableFilterProps {
  keyTableList: KeyTableData[];
  onChange: (filtered: KeyTableData[]) => void;
}

const KeyTableFilter = ({ keyTableList, onChange }: KeyTableFilterProps) => {
  // 필터링된 리스트 (현재는 필터링 없이 전체 반환)
  const filteredList = useMemo(() => {
    return keyTableList;
  }, [keyTableList]);

  useEffect(() => {
    onChange(filteredList);
  }, [filteredList, onChange]);

  return (
    <div className='mt-7'>
      {/* <div className='flex flex-row items-center'>
        <div className='flex items-center mr-5 flex-1 min-w-0'>
          <label className='text-sm mr-2 whitespace-nowrap'>Key</label>
          <select className='b-selectbox' value={filter.key} onChange={e => setFilter({ ...filter, key: e.target.value })}>
            {keyOptions.map(option => (
              <option key={option.value} value={option.value}>
                {option.label}
              </option>
            ))}
          </select>
        </div>
        <div className='flex items-center flex-1 min-w-0'>
          <label className='text-sm mr-2 whitespace-nowrap'>Node</label>
          <select className='b-selectbox' value={filter.nodeType} onChange={e => setFilter({ ...filter, nodeType: e.target.value })}>
            {nodeTypeOptions.map(option => (
              <option key={option.value} value={option.value}>
                {option.label}
              </option>
            ))}
          </select>
        </div>
      </div>
      <div className='flex flex-row justify-end mt-3'>
        <ABButton variant='outline-primary' size='sm' onClick={() => setFilter({ key: '', nodeType: '', global: '' })}>
          {'재설정'}
        </ABButton>
      </div> */}
    </div>
  );
};

export { KeyTableFilter };
