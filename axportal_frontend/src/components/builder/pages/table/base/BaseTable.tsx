import { ABClassNames } from '@/components/builder/components/ui';
import { CustomTableRow } from '@/components/builder/pages/table/base/CustomTableRow.tsx';
import { type BaseRecord, type BaseTableProps, type CustomTableRowProps } from '@/components/builder/types/table.ts';
import React, { useMemo, useState } from 'react';
import { v4 as uuidv4 } from 'uuid';

interface EnhancedBaseTableProps<T extends BaseRecord> extends BaseTableProps<T> {
  CustomRowComponent?: React.ComponentType<CustomTableRowProps<T>>;
  isLoading?: boolean;
  // eslint-disable-next-line no-unused-vars
  isRowSelected?: (data: T, selectedId: string | string[] | null) => boolean;
  disabledColumnIds?: string[];
  selectedItems?: T[];
  loadingDataSize?: number;
}

const BaseTable = <T extends BaseRecord>({
  isLoading,
  mode,
  data,
  columns,
  columnsWithWidth,
  // updateSortParam,
  onRowClick,
  selectedRowId = [],
  isSelectable,
  CustomRowComponent,
  maxHeight,
  disabledColumnIds,
  isRowSelected = (data, selectedId) => {
    if (Array.isArray(selectedId)) {
      return selectedId.includes(String(data.id));
    } else {
      return String(selectedId) === String(data.uuid || data.id || data.serving_id);
    }
  },
  loadingDataSize = 10,
}: EnhancedBaseTableProps<T>) => {
  const [sortBy] = useState<{ id: string; desc: boolean } | null>(null);
  const [selectedRowIdInTable] = useState<string | null>(null);

  const loadingData = React.useMemo(() => Array(loadingDataSize).fill({}), []);

  // 정렬된 데이터 계산
  const sortedData = useMemo(() => {
    if (isLoading) return loadingData;

    // data가 null, undefined이거나 배열이 아닌 경우 빈 배열 반환
    if (!data || !Array.isArray(data)) {
      return [];
    }

    if (sortBy === null || sortBy?.desc === false) {
      return [...data].sort((a, b) => (a.key > b.key ? -1 : 1));
    } else {
      return [...data].sort((a, b) => (a.key < b.key ? -1 : 1));
    }
  }, [sortBy, data, isLoading, loadingData]);

  const TableRowComponent = CustomRowComponent || CustomTableRow;

  const renderHeaderCell = (column: any, index: number) => {
    const commonStyles = {
      width: columnsWithWidth[index]?.value,
      textAlign: 'left' as const,
    };

    const commonClasses = 'px-4 py-3 text-left text-xs font-medium text-gray-700 bg-gray-50 border-b border-gray-200 uppercase tracking-wider';

    if (isLoading) {
      return (
        <th key={uuidv4()} className={commonClasses} style={commonStyles}>
          <span>{typeof column.Header === 'string' ? column.Header : column.title}</span>
        </th>
      );
    }

    if (column.CustomHeader) {
      return (
        <th key={uuidv4()} className={commonClasses} style={commonStyles}>
          {<column.CustomHeader />}
        </th>
      );
    }

    if (typeof column.Header === 'function') {
      return (
        <th key={uuidv4()} className={commonClasses} style={commonStyles}>
          {column.Header({ column, index })}
        </th>
      );
    }

    if (column.Header) {
      return (
        <th key={uuidv4()} className={commonClasses} style={commonStyles}>
          <span>{column.Header}</span>
        </th>
      );
    }

    return (
      <th key={uuidv4()} className={commonClasses} style={commonStyles}>
        <span>{column.title}</span>
      </th>
    );
  };

  return (
    <div className='w-full overflow-x-auto mt-5'>
      <div className={ABClassNames('w-full max-w-full table-fixed', maxHeight && `max-h-[${maxHeight}px]`)}>
        <table className='w-full border-collapse'>
          <thead>
            <tr>{isLoading ? columns.map((column, index) => renderHeaderCell(column, index)) : columns.map((column, index) => renderHeaderCell(column, index))}</tr>
          </thead>

          <tbody>
            {isLoading ? (
              Array.from({ length: loadingDataSize }).map((_, i) => (
                <tr key={`skeleton-${i}`}>
                  {columns.map((column, j) => (
                    <td
                      key={`skeleton-cell-${j}`}
                      style={{
                        width: columnsWithWidth[j]?.value,
                        padding: '0.75rem',
                        minWidth: columnsWithWidth[j]?.value,
                        maxWidth: columnsWithWidth[j]?.value,
                      }}
                    >
                      <div
                        className='animate-pulse rounded-lg bg-gray-200'
                        style={{
                          height: '24px',
                          width: column.id === 'action' ? '40px' : '80%',
                        }}
                      />
                    </td>
                  ))}
                </tr>
              ))
            ) : sortedData.length > 0 ? (
              sortedData.map((row, i) => (
                <TableRowComponent
                  row={{ original: row, id: row.id || i }}
                  key={`row-${i}-${row.id || i}`}
                  columns={columns} // columns prop 추가
                  columnsWithWidth={columnsWithWidth}
                  mode={mode}
                  selectedRowId={selectedRowId || selectedRowIdInTable}
                  onRowClick={onRowClick}
                  isSelectable={isSelectable}
                  isRowSelected={isRowSelected}
                  disabledColumnIds={disabledColumnIds}
                />
              ))
            ) : (
              // "데이터 없음" 메시지를 숨기고 빈 행만 표시
              <tr>
                <td colSpan={columns.length} style={{ height: '48px' }}></td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export { BaseTable };
