import type { CellClickedEvent } from 'ag-grid-community';
import type { AgGridReactProps } from 'ag-grid-react';
import type { UIMoreMenuItem } from '../UIMoreMenuPopup/types';

export type UIMoreMenuConfig<TData> = {
  items: UIMoreMenuItem<TData>[]; // 더보기 메뉴 아이템
  shouldShowMenu?: (rowData: TData) => boolean; // 메뉴 표시 여부를 결정하는 함수
};

export type UIGridProps<TData = any, TValue = any> = {
  type?: 'default' | 'single-select' | 'multi-select';
  selectedDataList?: TData[];
  onCheck?: (datas: TData[]) => void;
  onClickRow?: (params: CellClickedEvent<TData, TValue>) => void;
  moreMenuConfig?: UIMoreMenuConfig<TData> | null;
} & Pick<AgGridReactProps<TData>, 'rowData' | 'columnDefs' | 'domLayout'>;
