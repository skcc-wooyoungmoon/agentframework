import { useCallback, useEffect, useMemo, useRef, useState } from 'react';
import { useAtom } from 'jotai/index';
import { edgesAtom, keyTableAtom, nodesAtom } from '@/components/agents/builder/atoms/AgentAtom';
import { useGraphActions } from '@/components/agents/builder/hooks/useGraphActions.ts';
import { useNodeValidation } from '@/components/agents/builder/hooks/useNodeValidation.ts';
import { BaseTable } from '@/components/agents/builder/pages/table/base/BaseTable.tsx';
import { createKeyTableColumns, keyTableColumnsConfig } from '@/components/agents/builder/pages/table/common/AgentColumn.tsx';
import { type CustomNodeInnerData, type InputKeyItem, type KeyTableData, type OutputKeyItem } from '@/components/agents/builder/types/Agents';
import { UIButton2 } from '@/components/UI/atoms/UIButton2';
import { useModal } from '@/stores/common/modal';
import { isEqual } from '@/components/agents/builder/utils/generalUtils';
import { useNodeHandler } from '@/components/agents/builder/hooks/useNodeHandler';

interface CustomSchemeProps {
  id: string;
  inputKeys: InputKeyItem[];
  setInputKeys: (key: InputKeyItem[], field?: string) => void;
  inputValues: string[];
  // eslint-disable-next-line no-unused-vars
  setInputValues: (key: string[]) => void;
  innerData: CustomNodeInnerData;
  outputKeys: OutputKeyItem[];
  type?: string;
  disabledKeyIn?: boolean;
  readOnly?: boolean;
  hideOutputs?: boolean;
  lockedInputKeys?: string[];
}

const CustomScheme = ({
  id,
  inputKeys,
  setInputKeys,
  inputValues,
  setInputValues,
  innerData,
  outputKeys: _outputKeys,
  type = 'agent__generator',
  readOnly = false,
  hideOutputs = false,
  lockedInputKeys = [],
}: CustomSchemeProps) => {
  const { validateNode, getValidation } = useNodeValidation();
  const validation = getValidation(id, 'input');
  const { syncAllNodeKeyTable, syncNodeData } = useGraphActions();
  const [keyTableList] = useAtom(keyTableAtom);
  const [nodes] = useAtom(nodesAtom);
  const [edges] = useAtom(edgesAtom);
  const stableInputKeysRef = useRef(inputKeys);
  const previousKeyTableListRef = useRef<KeyTableData[]>([]);
  const { openModal } = useModal();

  const currentNode = nodes.find(node => node.id === id);

  const getKeyTableValue = useCallback(
    (keytableId: string): string => {
      if (!keytableId || keytableId.trim() === '') {
        return '';
      }

      const incomingEdges = edges.filter(edge => edge.target === id);
      for (const edge of incomingEdges) {
        const sourceNode = nodes.find(node => node.id === edge.source);
        if (!sourceNode || !sourceNode.data) continue;

        const sourceOutputKeys = (sourceNode.data as any).output_keys || [];
        if (!Array.isArray(sourceOutputKeys)) continue;

        const matchingOutputKey = sourceOutputKeys.find((outputKey: any) => outputKey && outputKey.keytable_id === keytableId);

        if (matchingOutputKey && matchingOutputKey.name) {
          return matchingOutputKey.name;
        }
      }

      const keyTableEntry = keyTableList.find(entry => entry.id === keytableId);
      if (keyTableEntry?.key) {
        return keyTableEntry.key;
      }

      const keyNameMatch = keytableId.match(/^([^_]+)/);
      if (keyNameMatch && keyNameMatch[1]) {
        return keyNameMatch[1];
      }

      return keytableId;
    },
    [keyTableList, nodes, edges, id]
  );

  useEffect(() => {
    stableInputKeysRef.current = inputKeys;
  }, [inputKeys]);

  useEffect(() => {
    const currentInputKeys = stableInputKeysRef.current;
    const currentKeyTableIds = new Set(currentInputKeys.map(item => item.keytable_id).filter((id): id is string => Boolean(id) && id?.trim() !== ''));

    if (currentKeyTableIds.size === 0) {
      previousKeyTableListRef.current = [...keyTableList];
      return;
    }

    const relevantKeyTableEntries = keyTableList.filter(entry => currentKeyTableIds.has(entry.id) || entry.nodeId === id);
    const previousRelevantEntries = previousKeyTableListRef.current.filter(entry => currentKeyTableIds.has(entry.id) || entry.nodeId === id);

    const hasRelevantChanges =
      relevantKeyTableEntries.length !== previousRelevantEntries.length ||
      relevantKeyTableEntries.some(entry => {
        const prevEntry = previousRelevantEntries.find(e => e.id === entry.id);
        return !prevEntry || prevEntry.key !== entry.key || prevEntry.value !== entry.value;
      });
    if (hasRelevantChanges && currentKeyTableIds.size > 0) {
      const initializedInputKeys = currentInputKeys.map(item => {
        if (item.keytable_id && item.keytable_id.trim() !== '') {
          const value = getKeyTableValue(item.keytable_id);
          if (value) {
            return {
              ...item,
              keytable_id: item.keytable_id,
            };
          }
          return item;
        }
        return item;
      });

      if (!isEqual(initializedInputKeys, currentInputKeys)) {
        const hasLostKeytableIds = currentInputKeys.some((oldItem, index) => {
          const newItem = initializedInputKeys[index];
          return oldItem.keytable_id && !newItem.keytable_id;
        });

        if (!hasLostKeytableIds) {
          setInputKeys(initializedInputKeys, 'inputKeys');
          stableInputKeysRef.current = initializedInputKeys;
        }
      }
    }
    previousKeyTableListRef.current = [...keyTableList];
  }, [keyTableList, id]);

  const prevInputKeysRef = useRef(inputKeys);
  const isSavingRef = useRef(false);
  const lastSavedInputKeysRef = useRef<InputKeyItem[]>([]);

  useEffect(() => {
    const hasChanged = !isEqual(prevInputKeysRef.current, inputKeys);
    if (hasChanged && !isSavingRef.current) {
      prevInputKeysRef.current = inputKeys;
      validateNode(id, type, inputKeys);

      if (currentNode && currentNode.data) {
        const nodeData = currentNode.data as any;
        const savedInputKeys = (nodeData.input_keys as InputKeyItem[]) || [];

        const lastSavedEqual = isEqual(lastSavedInputKeysRef.current, inputKeys);

        const needsSave =
          !lastSavedEqual &&
          (savedInputKeys.length !== inputKeys.length ||
            !savedInputKeys.every((savedKey, idx) => {
              const currentKey = inputKeys[idx];
              return (
                currentKey &&
                currentKey.name === savedKey.name &&
                currentKey.required === savedKey.required &&
                (currentKey.keytable_id || '') === (savedKey.keytable_id || '') &&
                (currentKey.fixed_value || null) === (savedKey.fixed_value || null)
              );
            }));

        if (needsSave) {
          isSavingRef.current = true;
          lastSavedInputKeysRef.current = JSON.parse(JSON.stringify(inputKeys));

          const updatedData = {
            ...nodeData,
            input_keys: inputKeys.map(key => ({
              ...key,
              keytable_id: key.keytable_id || '',
              fixed_value: key.fixed_value || null,
            })),
          };

          syncNodeData(id, updatedData);
          isSavingRef.current = false;
        }
      }
    }
  }, [id, type, inputKeys, validateNode, currentNode, syncNodeData]);

  const dummyInputItem: InputKeyItem = {
    name: '',
    required: false,
    keytable_id: '',
    fixed_value: null,
  };

  const KeyTableModalBody = ({
    initialValue,
    initialKeyTableId,
    initialIsKeyTable,
    onStateChange,
  }: {
    initialValue: string;
    initialKeyTableId: string | null;
    initialIsKeyTable: boolean;
    onStateChange: (state: { isKeyTable: boolean; selectedId: string | null; tempValue: string }) => void;
  }) => {
    const [localIsKeyTable, setLocalIsKeyTable] = useState(initialIsKeyTable);
    const [localTempValue, setLocalTempValue] = useState(initialValue);
    const [localSelectedId, setLocalSelectedId] = useState<string | null>(initialKeyTableId);
    const [currentKeyTableList] = useAtom(keyTableAtom);

    useEffect(() => {
      onStateChange({
        isKeyTable: localIsKeyTable,
        selectedId: localSelectedId,
        tempValue: localTempValue,
      });
    }, [localIsKeyTable, localSelectedId, localTempValue]);

    useEffect(() => {
      if (initialKeyTableId && initialKeyTableId.trim() !== '') {
        setLocalSelectedId(initialKeyTableId);
        setLocalIsKeyTable(true);
      } else {
        setLocalSelectedId(null);
      }
    }, [initialKeyTableId]);

    useEffect(() => { }, [localSelectedId, localIsKeyTable]);

    const handleColumnSelect = useCallback((id: string) => {
      setLocalSelectedId(id);
      setLocalIsKeyTable(true);
    }, []);

    const keyTableColumns = useMemo(() => createKeyTableColumns(localSelectedId, handleColumnSelect), [localSelectedId, handleColumnSelect]);

    const handleRowClick = useCallback(
      (oneKey: any) => {
        if (oneKey?.id) {
          const newId = String(oneKey.id);
          setLocalSelectedId(() => {
            return newId;
          });
          setLocalIsKeyTable(true);
        }
      },
      [localSelectedId]
    );

    const isRowSelected = useCallback((data: KeyTableData, selectedId: string | string[] | null) => {
      if (!selectedId) return false;
      if (Array.isArray(selectedId)) {
        return selectedId.includes(String(data.id));
      }
      return String(selectedId) === String(data.id);
    }, []);

    return (
      <div className='flex flex-col'>
        {!localIsKeyTable && (
          <div className='mb-4'>
            <label className='block text-sm font-medium mb-2'>Value</label>
            <input
              type='text'
              value={localTempValue || ''}
              onChange={e => {
                setLocalTempValue(e.target.value);
              }}
              placeholder='Value 입력'
              className='w-full h-[48px] leading-[48px] rounded-lg border border-gray-300 bg-white p-2 outline-none'
            />
          </div>
        )}

        <div className='mt-[16px]'>
          <UIButton2
            className='btn-option-outlined'
            onClick={() => {
              setLocalIsKeyTable(!localIsKeyTable);
            }}
          >
            {localIsKeyTable ? '직접 입력' : '키 테이블'}
          </UIButton2>
        </div>

        {localIsKeyTable && (
          <div className='max-h-[400px] overflow-y-auto'>
            <BaseTable<KeyTableData>
              data={currentKeyTableList}
              columns={keyTableColumns}
              columnsWithWidth={keyTableColumnsConfig}
              maxHeight={'400px'}
              selectedRowId={localSelectedId}
              isSelectable={true}
              hideEmptyMessage={true}
              onRowClick={handleRowClick}
              isRowSelected={isRowSelected}
            />
          </div>
        )}
      </div>
    );
  };

  const handleOpenKeyTableModal = (index: number) => {
    syncAllNodeKeyTable();
    const tempValue = inputKeys[index]?.fixed_value || '';
    const tempKeyTableId = inputKeys[index]?.keytable_id || null;
    const isKeyTableMode = !!(tempKeyTableId && tempKeyTableId.trim() !== '') || (!tempKeyTableId && !tempValue);

    const modalState = {
      isKeyTable: isKeyTableMode,
      selectedId: tempKeyTableId,
      tempValue: tempValue,
    };

    const handleStateChange = (state: { isKeyTable: boolean; selectedId: string | null; tempValue: string }) => {
      modalState.isKeyTable = state.isKeyTable;
      modalState.selectedId = state.selectedId;
      modalState.tempValue = state.tempValue;
    };

    openModal({
      title: '키테이블',
      type: 'medium',
      body: <KeyTableModalBody initialValue={tempValue} initialKeyTableId={tempKeyTableId} initialIsKeyTable={isKeyTableMode} onStateChange={handleStateChange} />,
      showFooter: true,
      confirmText: '저장',
      onConfirm: () => {
        const updatedInputKeys = [...inputKeys];
        const { isKeyTable, selectedId, tempValue: finalTempValue } = modalState;

        if (isKeyTable && selectedId) {
          updatedInputKeys[index] = {
            ...updatedInputKeys[index],
            keytable_id: selectedId,
            fixed_value: null,
          };
        } else {
          updatedInputKeys[index] = {
            ...updatedInputKeys[index],
            keytable_id: '',
            fixed_value: finalTempValue,
          };
        }

        setInputKeys(updatedInputKeys, 'inputKeys');
      },
    });
  };

  const normalizedLockedInputKeys = useMemo(() => lockedInputKeys.map(name => (name || '').toLowerCase().trim()).filter(Boolean), [lockedInputKeys]);

  const isLockedInputName = useCallback(
    (name?: string | null) => {
      if (!name) {
        return false;
      }
      return normalizedLockedInputKeys.includes(name.toLowerCase());
    },
    [normalizedLockedInputKeys]
  );

  const handleInputKeyChange = (index: number, keyType: 'name' | 'fixed_value', value: string) => {
    if (readOnly || isLockedInputName(inputKeys[index]?.name)) {
      return;
    }
    const updatedInputKeys = [...inputKeys];
    if (keyType === 'fixed_value') {
      updatedInputKeys[index] = {
        ...updatedInputKeys[index],
        fixed_value: value,
        keytable_id: '',
      };
    } else if (keyType === 'name') {
      updatedInputKeys[index] = {
        ...updatedInputKeys[index],
        name: value,
      };
    }
    setInputKeys(updatedInputKeys, 'inputKeys');

    const newInputValues = [...inputValues];
    newInputValues[index] = value;
    setInputValues(newInputValues);
  };

  const handleRemoveInput = (index: number) => {
    setInputKeys(inputKeys.filter((_, i) => i !== index), 'inputKeys');
    setInputValues(inputValues.filter((_, i) => i !== index));
  };

  const handleAddInput = () => {
    if (readOnly) {
      return;
    }
    setInputKeys([...inputKeys, dummyInputItem], 'inputKeys');
  };

  const renderInputField = (item: InputKeyItem, index: number) => {
    const inputErrors = validation?.errors.filter(error => error.details?.inputIndex === index) || [];
    const hasError = inputErrors.length > 0;
    const isRequiredEmpty = item.required && !item.fixed_value && !item.keytable_id;
    const isLockedName = readOnly || isLockedInputName(item.name);

    const isFirstOccurrence = (name: string, currentIndex: number) => {
      return inputKeys.findIndex(key => key.name === name) === currentIndex;
    };

    const isPredefinedKey =
      (type === 'agent__generator' && index <= 1) ||
      (type === 'rewriter_multiquery' && (item.name === 'query' || item.name === 'querys')) ||
      (type === 'retriever__rewriter_hyde' && item.name === 'query' && isFirstOccurrence('query', index)) ||
      (type === 'retriever__doc_reranker' && (item.name === 'query' || item.name === 'context' || item.name === 'docs')) ||
      (type === 'retriever__doc_filter' && (item.name === 'query' || item.name === 'context')) ||
      (type === 'retriever__doc_compressor' && (item.name === 'query' || item.name === 'context'));

    const shouldDisableKeyInput = isLockedName || isPredefinedKey;
    const { stopPropagation, preventAndStop } = useNodeHandler();
    return (
      <div
        key={index}
        className={`relative ${isRequiredEmpty ? 'mb-5' : ''}`}
      >
        <div
          className={`flex items-center gap-2 rounded-lg bg-white p-2 border overflow-hidden ${isRequiredEmpty ? 'ag-border-red' : 'border-gray-300'}`}
        >
          <div className={'flex-1 min-w-0'}>
            {shouldDisableKeyInput ? (
              <input type='text' className='input w-[152px]' placeholder={item.name || '입력값'} value={item.name || ''} disabled />
            ) : (
              <div className={'flex-1 min-w-0'}>
                <input
                  type='text'
                  value={inputValues[index] || ''}
                  onChange={e => handleInputKeyChange(index, 'name', e.target.value)}
                  className={[
                    'input',
                    hasError ? 'w-full ag-border-red ring-red-500 focus:ag-border-red focus:ring-red-500' : 'w-full border-gray-300 focus:border-gray-300'
                  ].filter(e => !!e).join(' ')}
                  placeholder='Key 입력'
                  onMouseDown={stopPropagation}
                  onMouseUp={stopPropagation}
                  onSelect={stopPropagation}
                  onDragStart={preventAndStop}
                  onDrag={preventAndStop}
                />
              </div>
            )}
          </div>

          <div className='relative items-center flex-1 min-w-0 overflow-hidden'>
            {item.keytable_id && item.keytable_id.trim() !== '' ? (
              <div
                className='w-full rounded bg-gray-50 px-3 py-2 text-sm border overflow-hidden'
                style={{
                  backgroundColor: '#F1F1F4',
                  color: '#5C5B75',
                  borderColor: '#E5E5E5',
                }}>
                <div className='flex items-center justify-between gap-2 w-[140px]'>
                  <div className='flex items-center gap-2 min-w-0 flex-1 overflow-hidden'>
                    <span
                      className={`badge badge-circle badge-${item.keytable_id?.endsWith('__global') || item.keytable_id?.endsWith('_global') ? 'success' : 'primary'}`}
                      style={{
                        width: 20,
                        height: 20,
                        verticalAlign: 'middle',
                        flexShrink: 0,
                      }}
                    >
                      {item.keytable_id?.endsWith('__global') || item.keytable_id?.endsWith('_global') ? 'G' : 'L'}
                    </span>
                    <span className='' style={{ textOverflow: 'ellipsis', whiteSpace: 'nowrap', overflow: 'hidden' }}>
                      {getKeyTableValue(item.keytable_id) || item.keytable_id}
                    </span>
                  </div>
                  <button
                    title='제거'
                    className='btn btn-sm btn-icon-sm h-[20px] w-[20px] p-0 text-gray-500 hover:text-gray-500 hover:bg-gray-100 text-xl flex-shrink-0'
                    role='button'
                    aria-label='remove tag'
                    onClick={() => {
                      const updatedInputKeys = [...inputKeys];
                      updatedInputKeys[index] = {
                        ...updatedInputKeys[index],
                        fixed_value: null,
                        keytable_id: '',
                      };
                      setInputKeys(updatedInputKeys, 'inputKeys');
                    }}>
                    ×
                  </button>
                </div>
              </div>
            ) : (
              <>
                <input
                  type='text'
                  value={item.fixed_value || ''}
                  onChange={e => {
                    const updatedInputKeys = [...inputKeys];
                    updatedInputKeys[index] = {
                      ...updatedInputKeys[index],
                      fixed_value: e.target.value,
                      keytable_id: '',
                    };
                    setInputKeys(updatedInputKeys, 'inputKeys');
                  }}
                  disabled
                  className={`input w-full border-0 outline-none truncate ${item.name === 'query' && !item.fixed_value && !item.keytable_id ? '' : ''}`}
                  placeholder='Value 선택'
                />
              </>
            )}
          </div>
          {isRequiredEmpty && (
            <div className='absolute -bottom-5 left-2 text-xs ag-color-red flex items-center gap-1'>
              <svg className='w-3 h-3' fill='#ef4444' viewBox='0 0 20 20'>
                <path fillRule='evenodd' d='M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z' clipRule='evenodd' />
              </svg>
              필수 입력값을 입력하세요
            </div>
          )}
          <div className='flex gap-2 flex-shrink-0'>
            <button
              type='button'
              onClick={() => handleOpenKeyTableModal(index)}
              className='btn-icon btn btn-sm btn-light text-primary flex-shrink-0'
              style={{
                backgroundColor: '#ffffff',
                border: '1px solid #d1d5db',
                borderRadius: '6px',
                padding: '6px',
                color: '#6b7280',
                cursor: 'pointer',
                fontSize: '14px',
                transition: 'all 0.2s ease',
                minWidth: '32px',
                width: '32px',
                height: '32px',
              }}
              disabled={readOnly || (type === 'rewriter_multiquery' && (item.name === 'query' || item.name === 'querys'))}>
              <span className='text-sm'>🔍</span>
            </button>
            <button
              onClick={() => handleRemoveInput(index)}
              className='btn-icon btn btn-sm btn-light text-primary flex-shrink-0 btn-bg-del'
              style={{
                border: '1px solid #d1d5db',
                borderRadius: '6px',
                padding: '6px',
                color: '#6b7280',
                cursor: 'pointer',
                fontSize: '14px',
                transition: 'all 0.2s ease',
                minWidth: '32px',
                width: '32px',
                height: '32px',
              }}
              disabled={
                readOnly ||
                shouldDisableKeyInput ||
                (type === 'rewriter_multiquery' && (item.name === 'query' || item.name === 'querys')) ||
                (type === 'retriever__doc_reranker' && (item.name === 'query' || item.name === 'context' || item.name === 'docs')) ||
                (type === 'retriever__doc_filter' && (item.name === 'query' || item.name === 'context')) ||
                (type === 'retriever__doc_compressor' && (item.name === 'query' || item.name === 'context'))
              }
              title='삭제'>
              🗑️
            </button>
          </div>
        </div>
        {inputErrors.map((error, errorIndex) => (
          <div key={errorIndex} className='mt-1'>
            <div className={`ml-2 flex items-center gap-2 text-sm text-red-500 mt-1`}>
              <i className='ki-filled ki-information-1' aria-hidden='true' />
              <span>{error.message}</span>
            </div>
          </div>
        ))}
      </div>
    );
  };

  return (
    <div className='bg-white p-3 shadow-sm px-5'>
      <div className='flex flex-col w-full'>
        <div className='w-full'>
          <h4 className='mb-4 w-full text-left text-lg font-semibold'>{'입력'}</h4>
          <div className={`min-h-[100px] ${innerData?.isToggle ? 'hidden' : ''}`}>
            <div className='flex w-full flex-col gap-3'>
              {inputKeys.map((item, index) => renderInputField(item, index))}
              {inputKeys.length === 0 && <div className='w-full h-[20px]' />}
              {!readOnly && (
                <div className='mt-2 flex justify-center'>
                  <button onClick={handleAddInput} className='btn btn-light rounded-md border border-gray-300 px-4 py-2 text-dark'>
                    {'입력 추가'}
                  </button>
                </div>
              )}
            </div>
          </div>
        </div>

        <div className='mx-4 w-px bg-gray-300' />
        {!hideOutputs && (
          <div className='w-full flex-shrink-0'>
            <h4 className='mb-4 text-left text-lg font-semibold'>{'출력'}</h4>
            <div className='flex w-full flex-col gap-3'>
              {_outputKeys && _outputKeys.length > 0 ? (
                _outputKeys.map((item, index) => (
                  <div key={index} className='w-full h-[40px] leading-[40px] text-sm text-gray-500 px-3 bg-gray-50 rounded-lg border border-gray-200 flex items-center'>
                    {item.name || 'output'}
                  </div>
                ))
              ) : (
                <div className='w-full h-[40px]' />
              )}
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export { CustomScheme };
