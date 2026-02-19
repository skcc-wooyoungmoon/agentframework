import { UIButton2 } from '@/components/UI';
import { keyTableAtom } from '@/components/builder/atoms/AgentAtom';
import { useGraphActions } from '@/components/builder/hooks';
import { BaseTable } from '@/components/builder/pages/table/base/BaseTable.tsx';
import { createKeyTableColumns, keyTableColumnsConfig } from '@/components/builder/pages/table/common/AgentColumn.tsx';
import { type KeyTableData } from '@/components/builder/types/Agents';
import { useAtom } from 'jotai/index';
import { useEffect, useState } from 'react';

type KeyTablesPopupProps = {
  initVisibleKeyTables?: boolean; // true(키테이블), false(직접 입력)
  initSelectedId?: string | null; // 키테이블 선택 시 선택된 키테이블 ID
  initTempValue?: string; // 직접 입력 시 입력된 값
  disabledKeyIn?: boolean; // true(조회), false(조회, 저장)
  onStateChange?: (state: { isKeyTable: boolean; selectedId: string | null; tempValue: string }) => void;
};

const KeyTables = ({ initVisibleKeyTables = true, initSelectedId = null, initTempValue = '', disabledKeyIn = false, onStateChange }: KeyTablesPopupProps) => {
  const [keyTableList] = useAtom(keyTableAtom);
  const { syncAllNodeKeyTable } = useGraphActions();

  const [visibleKeyTables, setVisibleKeyTables] = useState(initVisibleKeyTables); // true(키테이블), false(직접 입력)
  const [selectedId, setSelectedId] = useState<string | null>(initSelectedId); // selectedId (키테이블 선택 시 선택된 키테이블 ID)
  const [tempValue, setTempValue] = useState(initTempValue); // 직접 입력 시 입력된 값

  // 키테이블 모달이 열릴 때마다 출력
  useEffect(() => {
    syncAllNodeKeyTable();
  }, []);

  const keyTableColumns = createKeyTableColumns(selectedId, (id: string) => {
    setSelectedId(id);
  });

  // 🔥 모달 상태 변경 시 부모에게 알림 (onStateChange 의존성 제거로 무한 루프 방지)
  useEffect(() => {
    if (onStateChange) {
      onStateChange({
        isKeyTable: visibleKeyTables,
        selectedId: selectedId,
        tempValue: tempValue,
      });
    }
  }, [visibleKeyTables, selectedId, tempValue, onStateChange]);

  const handleSelect = (oneKey: any) => {
    console.log('handleSelect, oneKey : ', oneKey);
    setSelectedId(oneKey.id);
    setTempValue(oneKey.key);
  };

  return (
    <div className='flex flex-col'>
      <h2 className='text-body-2 secondary-neutral-500 pb-1.5'>동일한 key는 노드별 전역 변수로, 서로 다른 변수로 사용됩니다.</h2>
      {visibleKeyTables ? (
        <div className='max-h-[400px] overflow-y-auto'>
          <BaseTable<KeyTableData>
            data={keyTableList}
            columns={keyTableColumns}
            columnsWithWidth={keyTableColumnsConfig}
            maxHeight={'400px'}
            selectedRowId={selectedId}
            isSelectable={true}
            onRowClick={handleSelect}
          />
        </div>
      ) : (
        <div className='mb-4'>
          <label className='block text-sm font-medium mb-2'>Value</label>
          <input
            type='text'
            value={tempValue || ''}
            onChange={e => setTempValue(e.target.value)}
            placeholder='Value 입력'
            className='w-full h-[48px] leading-[48px] rounded-lg border border-gray-300 bg-white p-2 outline-none'
          />
        </div>
      )}
      {disabledKeyIn === false && (
        <div className='mt-[16px]'>
          <UIButton2
            className='btn-option-outlined'
            onClick={() => {
              setVisibleKeyTables(!visibleKeyTables);
            }}
          >
            {visibleKeyTables ? '직접 입력' : '키 테이블'}
          </UIButton2>
        </div>
      )}
    </div>
  );
};

export default KeyTables;
