import { UIImage } from '@/components/UI/atoms/UIImage';
import { keyTableAtom } from '@/components/agents/builder/atoms/AgentAtom';
import { DefaultButton } from '@/components/agents/builder/common/index.ts';
import { DefaultTooltip } from '@/components/agents/builder/common/tooltip/Tooltip';
import { useGraphActions } from '@/components/agents/builder/hooks/useGraphActions.ts';
import { KeyTableFilter } from '@/components/agents/builder/pages/graph/common/KeyTableFilter.tsx';
import { BaseTable } from '@/components/agents/builder/pages/table/base/BaseTable.tsx';
import { createKeyTableColumns, keyTableColumnsConfig } from '@/components/agents/builder/pages/table/common/AgentColumn.tsx';
import { type KeyTableData } from '@/components/agents/builder/types/Agents';
import { useAtom } from 'jotai/index';
import { useState } from 'react';

const KeyTables = ({ readOnly = false }: { readOnly?: boolean }) => {
  const [visibleKeyTables, setVisibleKeyTables] = useState(false);
  const [keyTableList] = useAtom(keyTableAtom);
  const { syncAllNodeKeyTable } = useGraphActions();
  const [filteredList, setFilteredList] = useState(keyTableList);
  const [selectedRowId, setSelectedRowId] = useState<string | null>(null);
  const keyTableColumns = createKeyTableColumns(selectedRowId, (id: string) => {
    setSelectedRowId(id);
  });

  const toggleKeyTables = () => {
    setVisibleKeyTables(prev => !prev);
    if (!visibleKeyTables) handleKeyTableRefresh();
  };

  const handleKeyTableRefresh = () => {
    syncAllNodeKeyTable();
  };

  return (
    <div style={{ pointerEvents: 'auto', zIndex: 1001 }} onMouseDown={e => e.stopPropagation()} onClick={e => e.stopPropagation()} onMouseUp={e => e.stopPropagation()}>
      <div>
        <DefaultTooltip title='Key Tables' placement={'bottom'}>
          <button
            type='button'
            className='!rounded-md !w-8 h-8 !px-0 flex items-center justify-center bg-white border border-[#DCE2ED] ag-btn-hover'
            style={{
              pointerEvents: readOnly ? 'none' : 'auto',
              zIndex: 1002,
              opacity: readOnly ? 0.6 : 1,
              cursor: readOnly ? 'not-allowed' : 'pointer',
            }}
            disabled={readOnly}
            onMouseDown={e => e.stopPropagation()}
            onClick={e => {
              if (readOnly) return;
              e.stopPropagation();
              toggleKeyTables();
            }}
            onMouseUp={e => e.stopPropagation()}
          >
            <UIImage src='/assets/images/lnb-menu/ico-lnb-menu-20-agent-apikey.svg' alt='key tables' className='w-5 h-5' />
          </button>
        </DefaultTooltip>
      </div>
      <div className={`${visibleKeyTables ? '' : 'hidden'} absolute right-[400px] top-20 z-50 mt-2 rounded-lg border border-gray-300 bg-white shadow-lg`} style={{ width: 650 }}>
        <div className='w-full'>
          {/* 헤더 - 제목과 X버튼 */}
          <div className='flex items-center justify-between px-4 pt-4 pb-2 border-b border-gray-200'>
            <h1 className='text-lg font-bold text-gray-800'>Key Tables</h1>
            <button
              onClick={toggleKeyTables}
              className='flex items-center justify-center w-8 h-8 rounded-md hover:bg-gray-100 transition-colors text-gray-600 hover:text-gray-900'
              aria-label='닫기'
              type='button'
            >
              <svg className='w-5 h-5' fill='none' stroke='currentColor' viewBox='0 0 24 24'>
                <path strokeLinecap='round' strokeLinejoin='round' strokeWidth={2} d='M6 18L18 6M6 6l12 12' />
              </svg>
            </button>
          </div>
          {/* 필터 UI: Key, Global, NodeType */}
          <div className='mb-4 px-4'>
            <KeyTableFilter keyTableList={keyTableList} onChange={setFilteredList} />
          </div>
          {/* <div className='px-4 pr-8'>
            <h2 className='text-xs secondary-neutral-600 pb-1.5 border-b border-gray-200'>동일한 key는 노드별 전역 변수로, 서로 다른 변수로 사용됩니다.</h2>
          </div> */}
          <div className='max-h-[300px] overflow-y-scroll px-4'>
            <h2 className='text-body-2 secondary-neutral-500 pb-1.5'>동일한 key는 노드별 전역 변수로, 서로 다른 변수로 사용됩니다.</h2>
            {filteredList.length > 0 && (
              <BaseTable<KeyTableData>
                data={filteredList}
                columns={keyTableColumns}
                columnsWithWidth={keyTableColumnsConfig}
                selectedRowId={selectedRowId}
                isSelectable={true}
                onRowClick={(row: KeyTableData) => {
                  setSelectedRowId(row.id);
                }}
              />
            )}
          </div>
          {/* 페이지네이션과 닫기 버튼을 중앙 정렬 */}
          <div className='p-4 flex justify-center'>
            <DefaultButton color={'primary'} onClick={toggleKeyTables}>
              {'닫기'}
            </DefaultButton>
          </div>
        </div>
      </div>
    </div>
  );
};

export default KeyTables;
