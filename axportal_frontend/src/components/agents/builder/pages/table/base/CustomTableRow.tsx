import { type BaseRecord, type CustomTableRowProps } from '@/components/agents/builder/types/table.ts';
import { v4 as uuidv4 } from 'uuid';

const CustomTableRow = <T extends BaseRecord>({
  row,
  columnsWithWidth,
  selectedRowId = null,
  onRowClick,
  isSelectable = false,
  isRowSelected = () => false,
  columns,
}: CustomTableRowProps<T>) => {

  const isSelected = isSelectable && isRowSelected(row.original, selectedRowId);

  const handleRowClick = (e: React.MouseEvent<HTMLTableRowElement>) => {
    if (!isSelectable || !onRowClick) {
      return;
    }
    const target = e.target as HTMLElement;
    if (target.closest('td[data-column-id="action"]') || target.closest('td[data-column-id="actions"]')) {
      return;
    }
    onRowClick(row.original);
  };

  return (
    <tr
      key={row.id || row.original?.id || uuidv4()}
      className={['transition-all duration-200 border-b border-gray-200', {
        'bg-blue-100 border-blue-400': isSelected && isSelectable,
        'hover:bg-gray-50': isSelectable && !isSelected,
        'cursor-pointer': isSelectable,
      }].filter(e => !!e).join(' ')}
      style={isSelected && isSelectable ? { backgroundColor: '#DBEAFE', borderColor: '#60A5FA' } : undefined}
      onClick={isSelectable ? handleRowClick : undefined}
    >
      {columns.map((column, index) => {
        const columnWidth = columnsWithWidth[index];
        const CustomCellComponent = column.CustomCell;
        const isLastColumn = index === columns.length - 1;

        return (
          <td
            className={['px-4 py-3 text-sm text-gray-700', {
              'border-r border-gray-200': !isLastColumn,
              'cursor-pointer': column.id !== 'action',
            }].filter(e => !!e).join(' ')}
            style={{
              width: columnWidth?.value,
              maxWidth: columnWidth?.value,
              textAlign: 'left',
            }}
            key={uuidv4()}
            data-column-id={column.id}
            onClick={e => {
              if (column.id === 'selection') {
                e.stopPropagation();
                return;
              }
              if (column.id !== 'action' && column.id !== 'actions' && isSelectable && onRowClick) {
                e.stopPropagation();
                onRowClick(row.original);
              }
            }}
            onMouseDown={e => {
              if (isSelectable && column.id !== 'action' && column.id !== 'actions') {
                e.stopPropagation();
              }
            }}
          >
            {CustomCellComponent ? (
              <CustomCellComponent data={row.original} />
            ) : (
              <span>
                {column.accessor && typeof column.accessor === 'string'
                  ? String(row.original[column.accessor as keyof T] || '')
                  : column.accessor && typeof column.accessor === 'function'
                    ? String(column.accessor(row.original) || '')
                    : '-'}
              </span>
            )}
          </td>
        );
      })}
    </tr>
  );
};

export { CustomTableRow };
