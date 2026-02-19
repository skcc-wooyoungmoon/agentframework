import { ABClassNames } from '@/components/builder/components/ui';
import { type BaseRecord, type CustomHeaderColumnProps } from '@/components/builder/types/table.ts';
import React from 'react';
import { v4 as uuidv4 } from 'uuid';

const CustomHeaderColumn = <T extends BaseRecord>({
  column,
  // title,
  sortable,
  sortBy,
  setSortBy,
  updateSortParam,
  style,
  className,
}: CustomHeaderColumnProps<T>) => {
  if (column.CustomHeader) {
    const CustomHeaderComponent = column.CustomHeader;
    return (
      <th className={ABClassNames(className, 'text-gray-700 dark:text-gray-800', 'whitespace-nowrap', 'px-4 py-2')} style={style}>
        <CustomHeaderComponent column={column} />
      </th>
    );
  }

  if (!sortable) {
    return (
      <th className={`${className} text-gray-700 dark:text-gray-800`} style={style}>
        <span>{String(column.Header)}</span>
      </th>
    );
  }
  const handleSort = (e: React.MouseEvent) => {
    e.stopPropagation();

    const isDesc = !(column.id === sortBy?.id && sortBy?.desc);
    setSortBy({ id: column.id || '', desc: isDesc });
    const isDescStr = isDesc ? 'desc' : 'asc';

    if (updateSortParam) {
      updateSortParam(column.id || '', isDescStr);
    }
  };

  return (
    <th
      className={`${className} text-gray-700 dark:text-gray-800`}
      style={{
        ...style,
        padding: '8px',
      }}
      {...{
        ...(column as any).getHeaderProps(),
      }}
      key={uuidv4()}
    >
      <div className='cursor-pointer' onClick={handleSort}>
        <span className={sortBy && sortBy.id === column.id && sortBy.desc ? 'sort desc' : 'sort asc'}>
          <span className='sort-label'>{String(column.Header)} </span>
          {column.id !== 'selection' && <span className='sort-icon'></span>}
        </span>
      </div>
    </th>
  );
};

export { CustomHeaderColumn };
