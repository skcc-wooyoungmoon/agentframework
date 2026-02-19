import React, { type ReactNode } from 'react';

// 커스텀 Column 타입 정의
export type CustomColumn<T extends object> = {
  Header?: string | ReactNode | ((props: { column: CustomColumn<T>; index: number }) => ReactNode);
  title?: string;
  id?: string;
  sortable?: boolean;
  CustomHeader?: React.ComponentType<{ column: CustomColumn<T> }>;
  CustomCell?: React.ComponentType<{ data: T }>;
  Cell?: (props: { row: T }) => ReactNode; // Cell 속성 추가
  accessor?: keyof T | ((row: T) => any);
};

// 커스텀 Row 타입 정의
export type CustomRow<T> = {
  original: T;
  id: string | number;
};

export type ColorType = 'default' | 'info' | 'success' | 'danger' | 'primary' | 'warning' | 'dark';

// eslint-disable-next-line no-unused-vars
export type OnRowClickType<T> = (rowData: T) => void;

export type RecordWithId = {
  [key: string]: any;
  id?: string;
  name?: string;
  api_key_id?: string;
  uuid?: string;
};

export interface BaseRecord extends RecordWithId { }

export type ColumnWidth = {
  key?: string;
  value?: string;
};

export type BaseTableProps<T extends BaseRecord> = {
  mode?: string;
  data: T[];
  columns: ReadonlyArray<CustomColumn<T>>;
  columnsWithWidth: ColumnWidth[];
  // eslint-disable-next-line no-unused-vars
  onPageChange?: (page: number) => void;
  // eslint-disable-next-line no-unused-vars
  onItemsPerPageChange?: (page: number) => void;
  // eslint-disable-next-line no-unused-vars
  updateSortParam?: (sortColumn: string, sortType: string) => void;
  onRowClick?: OnRowClickType<T>;
  selectedRowId?: string | string[] | null;
  isSelectable?: boolean;
  // customRowComponent:
  maxHeight?: string;
};

export type CustomTableRowProps<T extends BaseRecord> = {
  row: CustomRow<T>;
  mode?: string;
  columns: ReadonlyArray<CustomColumn<T>>; // readonly로 변경
  columnsWithWidth: ColumnWidth[];
  selectedRowId?: string | string[] | null;
  onRowClick?: OnRowClickType<T>;
  isSelectable?: boolean;
  customRowProps?: any;
  // eslint-disable-next-line no-unused-vars
  isRowSelected?: (data: T, selectedId: string | string[] | null) => boolean;
  disabledColumnIds?: string[];
};

export interface DataCellProps<T extends object> {
  data: T;
  field: keyof T;
  className?: string;
  renderAs?: 'info-badge' | 'text';
  align?: 'left' | 'center' | 'right';
  isAllowCopy?: boolean;
}