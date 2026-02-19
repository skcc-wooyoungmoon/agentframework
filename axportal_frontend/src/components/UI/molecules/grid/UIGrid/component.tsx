import { AllCommunityModule, ModuleRegistry } from 'ag-grid-community';

import 'ag-grid-community/styles/ag-grid.css';
import 'ag-grid-community/styles/ag-theme-alpine.css';
import { memo, useCallback, useEffect, useLayoutEffect, useMemo, useRef, useState } from 'react';

import { AgGridReact, type AgGridReactProps, type CustomTooltipProps } from 'ag-grid-react';

import { UIButton2, UIIcon2, UISkeleton } from '../../../atoms';
import { UIListNoData } from '../../list';
import { UIMoreMenuPopup } from '../UIMoreMenuPopup/component';

import { useEnvCheck } from '@/hooks/common/util';
import type { CellClickedEvent, ColDef, GridApi, GridReadyEvent, SelectionChangedEvent } from 'ag-grid-community';
import type { UIMoreMenuType } from '../UIMoreMenuPopup/types';
import type { UIMoreMenuConfig } from './types';
// SelectionChangedEvent,
// AG Grid 모듈 등록
ModuleRegistry.registerModules([AllCommunityModule]);

/**
 * 툴팁 컴포넌트 - 말줄임 있을 때만 표시
 */
function UIGridTooltip(params: CustomTooltipProps) {
  // 저장된 말줄임 정보 확인
  const colId = params.column && 'getColId' in params.column ? params.column.getColId() : undefined;
  const isTruncated = colId && params.data?.__truncated?.[colId];

  // 말줄임 없으면 null 반환
  if (!isTruncated) {
    return null;
  }

  return <div className='max-w-[400px] h-fit p-4 bg-white rounded-lg border border-gray-400'>{params.value}</div>;
}

/**
 * 말줄임 체크 래퍼
 */
const EllipsisCheckWrapper = memo((props: any) => {
  const cellRef = useRef<HTMLDivElement>(null);

  useLayoutEffect(() => {
    if (cellRef.current) {
      const textElement = cellRef.current.querySelector('div') || cellRef.current;
      const isTruncated = textElement.scrollWidth > textElement.clientWidth;

      // 데이터에 말줄임 정보 저장
      if (!props.data.__truncated) {
        props.data.__truncated = {};
      }
      props.data.__truncated[props.column.getColId()] = isTruncated;
    }
  });

  // 원본 cellRenderer 실행
  const OriginalRenderer = props.colDef.__originalRenderer;
  if (OriginalRenderer) {
    return (
      <div ref={cellRef} style={{ width: '100%', height: '100%' }}>
        <OriginalRenderer {...props} />
      </div>
    );
  }

  return (
    <div ref={cellRef} style={{ width: '100%', height: '100%' }}>
      {props.value}
    </div>
  );
});

/**
 * 스켈레톤 전체 너비 행 렌더러
 */
function SkeletonFullWidthRow(params: any) {
  const rowIndex = params.node?.rowIndex;
  // console.log('rowIndex:', rowIndex);
  return (
    <div className='w-full h-[48px] flex items-center'>
      <UISkeleton variant='rect' width='100%' height={48} rowIndex={rowIndex} />
    </div>
  );
}

/**
 * @author 김예리
 */
export function UIGrid<TData = any, TValue = any>({
  type = 'default',
  rowData,
  columnDefs,
  selectedDataList,
  checkKeyName,
  onCheck,
  onClickRow,
  moreMenuConfig,
  domLayout = 'autoHeight',
  loading = false,
  noDataMessage,
}: {
  type?: 'default' | 'single-select' | 'multi-select';
  selectedDataList?: TData[];
  checkKeyName?: string;
  onCheck?: (datas: TData[]) => void;
  onClickRow?: (params: CellClickedEvent<TData, TValue>) => void;
  moreMenuConfig?: UIMoreMenuConfig<TData> | null;
  loading?: boolean;
  noDataMessage?: string;
} & Pick<AgGridReactProps<TData>, 'rowData' | 'columnDefs' | 'domLayout'>) {
  // 컨테이너 ref
  const gridContainerRef = useRef<HTMLDivElement>(null);

  // 노출여부
  const { isProd } = useEnvCheck();

  // 그리드 API ref
  const gridApiRef = useRef<GridApi<TData> | null>(null);

  // 선택 상태 동기화 중 플래그 (onSelectionChanged 트리거 방지용)
  const isSyncingSelectionRef = useRef(false);

  // 최신 selectedDataList를 참조하기 위한 ref
  const selectedDataListRef = useRef<TData[] | undefined>(selectedDataList);

  // selectedDataList가 변경될 때마다 ref 업데이트
  useEffect(() => {
    selectedDataListRef.current = selectedDataList;
  }, [selectedDataList]);

  // 선택된 데이터 찾기
  const findSelectedData = useCallback(() => {
    if (!gridApiRef.current) return;

    isSyncingSelectionRef.current = true;

    try {
      const currentSelectedDataList = selectedDataListRef.current;

      if (currentSelectedDataList && currentSelectedDataList.length > 0) {
        if (checkKeyName) {
          // checkKeyName이 있을 경우, checkKeyName 값을 기준으로 비교
          const selectedIdSet = new Set(currentSelectedDataList.map(item => String((item as any)[checkKeyName])));
          gridApiRef.current.forEachNode(node => {
            if (node.data) {
              const nodeKey = String((node.data as any)[checkKeyName] ?? node.data);
              if (selectedIdSet.has(nodeKey)) {
                node.setSelected(true);
              } else {
                // 선택 해제된 항목은 체크 해제
                node.setSelected(false);
              }
            }
          });
        } else {
          // checkKeyName이 없을 경우, 전체 객체를 기준으로 비교
          const selectedDataListSet = new Set(currentSelectedDataList.map(item => JSON.stringify(item)));
          gridApiRef.current.forEachNode(node => {
            if (node.data) {
              const nodeStr = JSON.stringify(node.data);
              if (selectedDataListSet.has(nodeStr)) {
                node.setSelected(true);
              } else {
                // 선택 해제된 항목은 체크 해제
                node.setSelected(false);
              }
            }
          });
        }
      } else {
        // selectedDataList가 비어있으면 모든 선택 해제
        gridApiRef.current.deselectAll();
      }
    } finally {
      // 다음 이벤트 루프에서 플래그 해제
      setTimeout(() => {
        isSyncingSelectionRef.current = false;
      }, 0);
    }
  }, [checkKeyName]);

  // 그리드 준비 완료 시 API 저장
  const onGridReady = (params: GridReadyEvent<TData>) => {
    gridApiRef.current = params.api;
    findSelectedData();
  };

  // 선택 목록/데이터 변경 시 선택 상태 동기화 (페이징 등)
  useEffect(() => {
    if (!gridApiRef.current) return;
    findSelectedData();
  }, [findSelectedData, rowData]);

  // 처리
  const settingColumnDefs: ColDef<TData, TValue>[] = useMemo(() => {
    return columnDefs
      ?.map((colDef: ColDef & { showTooltip?: boolean }) => {
        //////////// 특정 환경일 경우 특정 컬럼 제외
        if (isProd) {
          if (colDef.field === 'more') {
            return null;
          }
        }
        //////////////// 특정 컬럼 처리
        // more
        if (colDef.field === 'more' && moreMenuConfig) {
          const { showTooltip: _, ...restColDef } = colDef;
          return {
            ...restColDef,
            sortable: false,
            cellRenderer: memo((params: any) => {
              return (
                <div className='flex items-center justify-center h-full w-full'>
                  <UIButton2
                    className='h-12 w-14 flex-shrink-0 bg-transparent text-[#121315] px-0 cursor-pointer flex items-center justify-center'
                    onClick={() => {
                      const cellRect = params.eGridCell.getBoundingClientRect();
                      const containerRect = gridContainerRef.current?.getBoundingClientRect();

                      if (containerRect) {
                        // 더보기 버튼의 왼쪽 아래 모서리에 팝업의 왼쪽 위 모서리가 오도록 계산
                        const relativeX = cellRect.left - containerRect.left; // 셀의 왼쪽에 정렬
                        const relativeY = cellRect.bottom - containerRect.top; // 셀 아래쪽에 위치

                        updateMoreMenu({
                          isOpen: true,
                          x: relativeX,
                          y: relativeY,
                          data: params.data,
                        });
                      }
                    }}
                  >
                    <UIIcon2 className='ic-system-24-more' />
                  </UIButton2>
                </div>
              );
            }),
          };
        }
        // !sortable : no, more
        if (colDef.field === 'no' || colDef.field === 'more' || colDef.field === 'button') {
          const { showTooltip: _, ...restColDef } = colDef;
          return {
            ...restColDef,
            sortable: false,
          };
        }

        //////////////// 특정 Option 처리
        // showTooltip을 제거하여 AG Grid에 전달하지 않음
        const { showTooltip, ...restColDef } = colDef;

        // cellRenderer가 있는 컬럼은 자동으로 말줄임 체크 후 툴팁 활성화
        if (restColDef.cellRenderer) {
          return {
            ...restColDef,
            __originalRenderer: restColDef.cellRenderer, // 원본 저장
            cellRenderer: EllipsisCheckWrapper, // 래퍼로 교체
            tooltipField: colDef.field, // 툴팁 활성화
            tooltipComponent: UIGridTooltip, // 커스텀 툴팁 (내부에서 조건 체크)
          };
        }

        // cellRenderer가 없으면 기본 설정
        return {
          ...restColDef,
        };
      })
      .filter((colDef): colDef is ColDef<TData, TValue> => colDef !== null) as ColDef<TData, TValue>[];
  }, [columnDefs, moreMenuConfig]);

  // 더보기 메뉴
  const [moreMenu, setMoreMenu] = useState<UIMoreMenuType<TData>>({
    isOpen: false,
    x: 0,
    y: 0,
    data: null,
  });

  const updateMoreMenu = (value: Partial<UIMoreMenuType<TData>>) => {
    setMoreMenu(prev => ({ ...prev, ...value }));
  };

  const checkboxStyle = type === 'single-select' ? `aggrid-single-checkbox` : '';

  // 로딩 상태일 때 스켈레톤 행 데이터 생성
  const displayRowData = useMemo(() => {
    if (loading) {
      // 5개의 스켈레톤 행 생성
      return Array.from({ length: 12 }, (_, index) => ({
        __skeleton: true,
        id: `skeleton-${index}`,
      }));
    }
    return rowData;
  }, [loading, rowData]);

  const NoDataOverlayWrapper = useMemo(() => {
    return () => <UIListNoData noDataMessage={noDataMessage} />;
  }, [noDataMessage]);

  return (
    settingColumnDefs && (
      <div ref={gridContainerRef} className={`w-full relative custom-grid ${checkboxStyle} ${rowData?.length === 0 ? 'empty-data' : ''} ${loading ? 'skeleton-loading' : ''}`}>
        <div className='w-full'>
          <AgGridReact
            onGridReady={onGridReady}
            ///////////// 테마 설정 (AG Grid v34+ 테마 오류 해결)
            theme='legacy'
            ///////////// 컬럼 (열)
            columnDefs={settingColumnDefs}
            defaultColDef={{
              resizable: false, // 사이즈 조정 X
              suppressMovable: true, // 이동 X
              valueFormatter: (params: any) => {
                // object 타입인 경우 문자열로 변환
                if (typeof params.value === 'object' && params.value !== null) {
                  return JSON.stringify(params.value);
                }
                // null이나 undefined인 경우 빈 문자열 반환
                if (params.value === null || params.value === undefined) {
                  return '';
                }
                // 그 외의 경우 그대로 반환
                return params.value;
              },
            }}
            ///////////// 데이터 (행)
            rowData={displayRowData}
            domLayout={domLayout}
            ///////////// 스켈레톤 행 설정
            isFullWidthRow={params => params.rowNode.data?.__skeleton === true}
            fullWidthCellRenderer={SkeletonFullWidthRow}
            ///////////// 데이터 없음 오버레이
            noRowsOverlayComponent={NoDataOverlayWrapper}
            rowSelection={
              type === 'default'
                ? undefined
                : {
                  mode: type === 'single-select' ? 'singleRow' : 'multiRow',
                  checkboxes: true,
                  headerCheckbox: type === 'multi-select',
                  enableClickSelection: false,
                }
            }
            ///////////// 이벤트 처리
            // getRowId={params => params.data.id} // 상태 처리시 refresh 방지
            // 체크박스 - 전체 선택 및 개별 선택 모두 처리
            // selectionChanged는 모든 선택 변경(전체 선택 포함)이 완료된 후 한 번만 호출됨
            onSelectionChanged={(params: SelectionChangedEvent<any>) => {
              if (!params.api) return;

              // console.log('params', params);
              // 선택 상태 동기화 중이면 이벤트 무시
              if (isSyncingSelectionRef.current) {
                return;
              }

              // 모든 선택 변경이 완료된 후 최신 선택 상태를 가져옴
              const selectedRows = params.api.getSelectedRows();

              setTimeout(() => {
                // 최신 selectedDataList를 ref에서 가져옴
                const currentSelectedDataList = selectedDataListRef.current || [];

                // 현재 페이지의 rowData를 기준으로 선택 상태 업데이트
                // selectedRows에 있는 항목은 선택, 없는 항목은 선택 해제
                const currentPageRowData = rowData || [];

                // checkKeyName이 있으면 checkKeyName을 기준으로 비교, 없으면 JSON.stringify로 비교
                let selectedRowsSet: Set<string>;
                let updatedCurrentPageSelections: TData[];
                let currentPageRowDataSet: Set<string>;
                let otherPageSelections: TData[];

                if (checkKeyName) {
                  // console.log('checkKeyName', checkKeyName);
                  // checkKeyName을 기준으로 비교
                  selectedRowsSet = new Set(selectedRows.map(row => String((row as any)[checkKeyName])));
                  updatedCurrentPageSelections = currentPageRowData.filter(row => selectedRowsSet.has(String((row as any)[checkKeyName])));

                  currentPageRowDataSet = new Set(currentPageRowData.map(row => String((row as any)[checkKeyName])));
                  otherPageSelections = currentSelectedDataList.filter(item => {
                    const itemKey = String((item as any)[checkKeyName] ?? item);
                    // console.log('itemKey', itemKey);
                    // console.log('currentPageRowDataSet', currentPageRowDataSet);
                    // console.log('currentPageRowDataSet.has(itemKey)', currentPageRowDataSet.has(itemKey));
                    return !currentPageRowDataSet.has(itemKey);
                  });
                } else {
                  // JSON.stringify로 비교 (기존 로직)
                  selectedRowsSet = new Set(selectedRows.map(row => JSON.stringify(row)));
                  updatedCurrentPageSelections = currentPageRowData.filter(row => selectedRowsSet.has(JSON.stringify(row)));

                  currentPageRowDataSet = new Set(currentPageRowData.map(row => JSON.stringify(row)));
                  otherPageSelections = currentSelectedDataList.filter(item => !currentPageRowDataSet.has(JSON.stringify(item)));
                }

                // 최종 선택 목록: 다른 페이지 선택 + 현재 페이지 선택
                if (type === 'single-select') {
                  // single-select: 전체에서 최대 1개만 선택
                  // 현재 페이지에서 선택한 항목이 있으면 그걸 선택하고 다른 페이지 선택 제거
                  // 현재 페이지에서 선택한 항목이 없으면 다른 페이지 선택 유지
                  if (selectedRows?.[0]) {
                    // 현재 페이지에서 새로 선택함 → 이것만 선택
                    onCheck?.([selectedRows[0]]);
                  } else {
                    // 현재 페이지에서 선택 해제함 → 다른 페이지 선택 유지
                    onCheck?.(otherPageSelections);
                  }
                } else {
                  // multi-select: 현재 페이지 선택 + 다른 페이지 선택
                  const finalSelection = [...otherPageSelections, ...updatedCurrentPageSelections];
                  onCheck?.(finalSelection);
                }

                // console.log('selectedRows', selectedRows);
              }, 0);
            }}
            // 행클릭
            onCellClicked={(params: CellClickedEvent) => {
              // 체크박스, more, button 컬럼 클릭 시 행 클릭 이벤트 무시
              const colId = params.column.getColId();
              const isCheckboxColumn = colId === 'ag-Grid-AutoColumn' || colId.includes('checkbox');

              if (params.colDef.field !== 'more' && params.colDef.field !== 'button' && !isCheckboxColumn) {
                onClickRow?.(params);
              }
            }}
            // Tooltip 옵션
            tooltipInteraction={true}
            tooltipMouseTrack={true}
            tooltipShowDelay={0}
          />
        </div>

        {/* 더보기 메뉴 팝업 */}
        {moreMenuConfig && <UIMoreMenuPopup type='grid' {...moreMenu} menuConfig={moreMenuConfig} onClose={() => updateMoreMenu({ isOpen: false })} />}
      </div>
    )
  );
}
