import { ABClassNames } from '@/components/builder/components/ui';
import { type BaseRecord, type CustomTableRowProps } from '@/components/builder/types/table.ts';
import { v4 as uuidv4 } from 'uuid';

const CustomTableRow = <T extends BaseRecord>({
  row,
  columnsWithWidth,
  selectedRowId = null,
  onRowClick,
  isSelectable = false,
  isRowSelected = () => false,
  // disabledColumnIds = ['action', 'actions', 'selection'],
  columns,
}: CustomTableRowProps<T>) => {
  // const handleClick = (column: CustomColumn<T>) => {
  //   if (!onRowClick || disabledColumnIds.includes(column.id || '')) {
  //     return;
  //   }
  //   onRowClick(row.original);
  // };

  const isSelected = isSelectable && isRowSelected(row.original, selectedRowId);

  // 🔥 tr 전체 클릭 이벤트 핸들러
  const handleRowClick = (e: React.MouseEvent<HTMLTableRowElement>) => {
    if (!isSelectable || !onRowClick) {
      return;
    }
    // action 컬럼 클릭은 무시
    const target = e.target as HTMLElement;
    if (target.closest('td[data-column-id="action"]') || target.closest('td[data-column-id="actions"]')) {
      return;
    }
    // 🔥 td에서 이미 처리했으면 중복 호출 방지하지 않음 (td의 stopPropagation으로 처리됨)
    // console.log('🔍 CustomTableRow tr 클릭:', { rowId: row.original?.id, isSelectable, hasOnRowClick: !!onRowClick });
    onRowClick(row.original);
  };

  return (
    <tr
      key={row.id || row.original?.id || uuidv4()}
      className={ABClassNames('transition-all duration-200 border-b border-gray-200', {
        'bg-blue-100 border-blue-400': isSelected && isSelectable,
        'hover:bg-gray-50': isSelectable && !isSelected,
        'cursor-pointer': isSelectable,
      })}
      style={isSelected && isSelectable ? { backgroundColor: '#DBEAFE', borderColor: '#60A5FA' } : undefined}
      onClick={isSelectable ? handleRowClick : undefined}
    >
      {columns.map((column, index) => {
        const columnWidth = columnsWithWidth[index];
        const CustomCellComponent = column.CustomCell;
        const isLastColumn = index === columns.length - 1;

        return (
          <td
            className={ABClassNames('px-4 py-3 text-sm text-gray-700', {
              'border-r border-gray-200': !isLastColumn,
              'cursor-pointer': column.id !== 'action',
            })}
            style={{
              width: columnWidth?.value,
              maxWidth: columnWidth?.value,
              minWidth: columnWidth?.value,
              textAlign: 'left',
              overflow: 'hidden',
            }}
            key={uuidv4()}
            data-column-id={column.id}
            onClick={e => {
              // selection 컬럼 클릭 시 이벤트 전파 방지
              if (column.id === 'selection') {
                e.stopPropagation();
                return;
              }
              // action 컬럼이 아니고 선택 가능한 경우 직접 onRowClick 호출
              if (column.id !== 'action' && column.id !== 'actions' && isSelectable && onRowClick) {
                // console.log('🔍 CustomTableRow td 클릭:', {
                //   columnId: column.id,
                //   rowId: row.original?.id,
                //   isSelectable,
                //   hasOnRowClick: !!onRowClick,
                //   selectedRowId,
                // });
                // 🔥 이벤트 전파를 막아서 tr의 onClick과 중복 호출 방지
                e.stopPropagation();
                onRowClick(row.original);
              }
            }}
            onMouseDown={e => {
              // 마우스 다운 시에도 이벤트 전파 방지 (드래그 방지)
              if (isSelectable && column.id !== 'action' && column.id !== 'actions') {
                e.stopPropagation();
              }
            }}
          >
            {CustomCellComponent ? (
              <div
                style={{
                  overflow: 'hidden',
                  textOverflow: 'ellipsis',
                  whiteSpace: 'nowrap',
                  width: '100%',
                }}
              >
                <CustomCellComponent data={row.original} />
              </div>
            ) : (
              // 기본 셀 렌더링 - column.accessor를 사용하여 데이터 표시
              <span
                style={{
                  display: 'block',
                  overflow: 'hidden',
                  textOverflow: 'ellipsis',
                  whiteSpace: 'nowrap',
                  width: '100%',
                }}
              >
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
