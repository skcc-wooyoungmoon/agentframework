import { keyTableAtom } from '@/components/agents/builder/atoms/AgentAtom';
import { hasChatTestedAtom } from '@/components/agents/builder/atoms/logAtom.ts';
import { Card, CardBody, CardFooter } from '@/components/agents/builder/common/index.ts';
import { LogModal } from '@/components/agents/builder/common/modal/log/LogModal.tsx';
import { getNodeStatus } from '@/components/agents/builder/utils/GraphUtils.ts';
import { useNodeTracing } from '@/components/agents/builder/hooks/useNodeTracing';
import { useAutoUpdateNodeInternals } from '@/components/agents/builder/hooks/useAutoUpdateNodeInternals';
import { useGraphActions } from '@/components/agents/builder/hooks/useGraphActions.ts';
import { BaseTable } from '@/components/agents/builder/pages/table/base/BaseTable.tsx';
import { createKeyTableColumns, keyTableColumnsConfig } from '@/components/agents/builder/pages/table/common/AgentColumn.tsx';
import { type CustomNode, type CustomNodeInnerData, type InputKeyItem, type InputNodeDataSchema, type KeyTableData, NodeType } from '@/components/agents/builder/types/Agents';
import { useModal } from '@/stores/common/modal';
import { Handle, type NodeProps, Position, useUpdateNodeInternals } from '@xyflow/react';
import { useAtom } from 'jotai/index';
import { useEffect, useMemo, useRef, useState } from 'react';

import { NodeFooter, NodeHeader } from '.';
import { useNodeHandler } from '@/components/agents/builder/hooks/useNodeHandler';

export const OutputSelectorNode: React.FC<NodeProps<CustomNode>> = ({ data, id, type }) => {
  const { nodes, removeNode, syncNodeData, toggleNodeView, syncAllNodeKeyTable } = useGraphActions();
  const updateNodeInternals = useUpdateNodeInternals();
  const { openModal } = useModal();
  const [hasChatTested] = useAtom(hasChatTestedAtom);

  const newInnerData: CustomNodeInnerData = {
    isRun: false,
    isDone: false,
    isError: false,
    isToggle: false,
  };
  const nodeData: CustomNodeInnerData = data.innerData ?? newInnerData;

  useNodeTracing(id, data.name as string, data, nodeData);

  const isRun = useMemo(() => nodeData?.isRun ?? false, [nodeData?.isRun]);
  const isDone = useMemo(() => nodeData?.isDone ?? false, [nodeData?.isDone]);
  const isError = useMemo(() => nodeData?.isError ?? false, [nodeData?.isError]);

  const [nodeStatus, setNodeStatus] = useState<string | null>(null);
  useEffect(() => {
    setNodeStatus(getNodeStatus(isRun, isDone, isError));
  }, [isRun, isDone, isError]);

  const [nodeName, setNodeName] = useState(data.name as string);
  const [description, setDescription] = useState((data.description as string) || '');
  const nodesUpdatedRef = useRef(false);
  const [keyTableList] = useAtom(keyTableAtom);

  const dummyItem: InputKeyItem = {
    name: '',
    required: false,
    keytable_id: '',
    fixed_value: null,
  };
  const initialInputKeys = useMemo(() => {
    const initInputItems: InputKeyItem[] = [dummyItem];
    return ((data as InputNodeDataSchema).input_keys as InputKeyItem[]) || initInputItems;
  }, [(data as InputNodeDataSchema).input_keys, dummyItem]);
  const [inputKeys, setInputKeys] = useState<InputKeyItem[]>(initialInputKeys);

  const isInitialMountRef = useRef(true);
  const prevDataRef = useRef(data);

  useEffect(() => {
    if (isInitialMountRef.current) {
      isInitialMountRef.current = false;
      prevDataRef.current = data;
      return;
    }

    const dataChanged = prevDataRef.current !== data;
    if (!dataChanged) {
      return;
    }
    const newInputKeys = ((data as InputNodeDataSchema).input_keys as InputKeyItem[]) || [];

    const hasOnlyDummyInputKeys = inputKeys.length <= 1 && inputKeys.every(k => k.name === '' || k.name === 'dummy');
    const hasEmptyInputKeys = inputKeys.length === 0;

    if (newInputKeys.length > 0 && (hasOnlyDummyInputKeys || hasEmptyInputKeys)) {
      setInputKeys(newInputKeys);
    }

    prevDataRef.current = data;
  }, [data, (data as InputNodeDataSchema).input_keys]);

  const syncOutputData = () => {
    const newInnerData = {
      ...nodeData,
    };

    const newData = {
      ...data,
      type: NodeType.OutputSelector.name,
      id: id,
      name: nodeName,
      description: description,
      input_keys: inputKeys,
      innerData: newInnerData,
    };

    syncNodeData(id, newData);
  };

  useEffect(() => {
    syncOutputData();
  }, [nodeName, description, inputKeys]);

  useEffect(() => {
    updateNodeInternals(id);
  }, [id, updateNodeInternals, nodeData.isToggle]);

  const containerRef = useAutoUpdateNodeInternals(id);

  const node = nodes.find(node => node.id === id);
  if (!node) return null;

  const handleHeaderClickLog = () => {
    const nodeName = (data as any).name || data.innerData?.name || id;

    if (hasChatTested) {
      openModal({
        type: 'large',
        title: '로그',
        body: <LogModal id={'builder_log'} nodeId={String(nodeName)} />,
        showFooter: false,
      });
    }
  };

  const onClickDelete = () => {
    removeNode(id);
  };

  const handleNodeNameChange = (value: string) => {
    setNodeName(value);
  };

  const handleFooterFold = (bool: boolean) => {
    toggleNodeView(id, bool);
  };

  const handleDescriptionChange = (val: string) => {
    setDescription(val);
  };

  const handleAddInput = () => {
    setInputKeys([...inputKeys, dummyItem]);
    nodesUpdatedRef.current = true;
  };

  const handleRemoveInput = (index: number) => {
    setInputKeys(inputKeys.filter((_, i) => i !== index));
    nodesUpdatedRef.current = true;
  };

  const handleOpenKeyTableModal = (index: number) => {
    syncAllNodeKeyTable();

    const initialKeyTableId = inputKeys[index]?.keytable_id || null;
    const selectedIdRef = { current: initialKeyTableId };
    const latestKeyTableListAtOpen = [...keyTableList];

    const KeyTableModalBody = () => {
      const [localSelectedId, setLocalSelectedId] = useState<string | null>(initialKeyTableId);
      const [currentKeyTableList] = useAtom(keyTableAtom);

      useEffect(() => {
        selectedIdRef.current = localSelectedId;
      }, [localSelectedId]);

      const keyTableColumns = useMemo(
        () =>
          createKeyTableColumns(localSelectedId, (id: string) => {
            setLocalSelectedId(id);
          }),
        [localSelectedId]
      );

      return (
        <div className='flex flex-col'>
          <div className='mt-4'>
            <div className='max-h-[400px] overflow-y-auto'>
              <BaseTable<KeyTableData>
                data={currentKeyTableList}
                columns={keyTableColumns}
                columnsWithWidth={keyTableColumnsConfig}
                maxHeight={'400px'}
                selectedRowId={localSelectedId}
                isSelectable={true}
                hideEmptyMessage={true}
                onRowClick={(oneKey: any) => {
                  setLocalSelectedId(oneKey.id);
                }}
              />
            </div>
          </div>
        </div>
      );
    };

    openModal({
      title: '키테이블',
      type: 'medium',
      body: <KeyTableModalBody />,
      showFooter: true,
      cancelText: '취소',
      confirmText: '저장',
      onConfirm: () => {
        if (selectedIdRef.current) {
          const findKeyTable = latestKeyTableListAtOpen.find(key => key.id === selectedIdRef.current);
          if (findKeyTable) {
            const keyName = findKeyTable.isGlobal ? findKeyTable.key : `${findKeyTable.nodeName}_${findKeyTable.key}`;
            const updatedInputKeys = [...inputKeys];
            updatedInputKeys[index] = {
              ...updatedInputKeys[index],
              name: keyName,
              keytable_id: selectedIdRef.current,
            };
            setInputKeys(updatedInputKeys);
          }
        }
      },
    });
  };
  const { autoResize, stopPropagation, preventAndStop } = useNodeHandler();
  return (
    <div ref={containerRef}>
      <Card className={['agent-card w-full min-w-[500px] max-w-[500px]', nodeStatus].filter(e => !!e).join(' ')}>
        <Handle
          type='target'
          position={Position.Left}
          isConnectable={true}
          style={{
            width: 20,
            height: 20,
            top: '50%',
            transform: 'translateY(-50%)',
            left: -10,
            background: '#000000',
            border: '2px solid white',
            zIndex: 20,
          }}
        />
        <NodeHeader
          nodeId={id}
          type={type}
          data={nodeData}
          onClickDelete={onClickDelete}
          defaultValue={nodeName}
          onChange={handleNodeNameChange}
          onClickLog={handleHeaderClickLog}
        />

        <>
          {nodeData.isToggle && (
            <div className='bg-white px-4 py-4 border-b border-gray-200'>
              <label className='block font-semibold text-sm text-gray-700 mb-2'>{'설명'}</label>
              <div className='relative'>
                <textarea
                  className='nodrag w-full resize-none border border-gray-300 rounded-lg p-3 text-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500'
                  style={{
                    minHeight: '80px',
                    maxHeight: '100px',
                    height: 'auto',
                    overflow: 'hidden',
                  }}
                  placeholder={'설명 입력'}
                  value={description}
                  onChange={e => {
                    const value = e.target.value;
                    if (value.length <= 100) {
                      handleDescriptionChange(value);
                    }
                    autoResize(e.target);
                  }}
                  onInput={(e: any) => {
                    autoResize(e.target);
                  }}
                  maxLength={100}
                  onMouseDown={stopPropagation}
                  onMouseUp={stopPropagation}
                  onSelect={stopPropagation}
                  onDragStart={preventAndStop}
                  onDrag={preventAndStop}
                />
                <div className='absolute bottom-2 right-2 text-xs text-gray-500'>
                  <span className='text-blue-500'>{description.length}</span>/100
                </div>
              </div>
            </div>
          )}

          {!nodeData.isToggle && (
            <>
              <CardBody>
                <div className='mb-4 w-auto'>
                  <label className='fw-bold form-label mb-4 text-lg'>{'설명'}</label>
                  <div className='relative'>
                    <textarea
                      className='nodrag w-full resize-none border border-gray-300 rounded-lg p-3 focus:border-gray-400 focus:outline-none'
                      rows={4}
                      placeholder={'설명 입력'}
                      value={description}
                      onChange={e => {
                        const value = e.target.value;
                        if (value.length <= 100) {
                          handleDescriptionChange(value);
                        }
                      }}
                      maxLength={100}
                      onMouseDown={stopPropagation}
                      onMouseUp={stopPropagation}
                      onSelect={stopPropagation}
                      onDragStart={preventAndStop}
                      onDrag={preventAndStop}
                    ></textarea>
                    <div className='absolute bottom-2 right-2 text-xs text-gray-500'>
                      <span className='text-blue-500'>{description.length}</span>/100
                    </div>
                  </div>
                </div>
              </CardBody>

              <div className='bg-gray-50 px-4 py-3 border-t border-gray-200'>
                <h3 className='text-lg font-semibold text-gray-700'>Schema</h3>
              </div>

              <div className='card-body gap-5 px-4 mt-3 pb-3'>
                <div className='mx-auto flex gap-3 w-full flex-col items-center'>
                  {inputKeys.map((_item, index) => (
                    <div key={index} className='w-full'>
                      <div className='flex w-full items-center gap-2 rounded-lg bg-white p-2 rounded-lg border border-gray-300'>
                        <div className='relative flex w-full items-center'>
                          {inputKeys[index].keytable_id && inputKeys[index].name !== '' && inputKeys[index].name !== 'dummy' && inputKeys[index].keytable_id !== '' ? (
                            <>
                              <div
                                className='w-full rounded bg-gray-50 px-3 py-2 text-sm'
                                style={{
                                  backgroundColor: '#F1F1F4',
                                  color: '#5C5B75',
                                }}
                              >
                                <div className='flex items-center justify-between'>
                                  <div className='flex items-center gap-2'>
                                    <span
                                      className={`badge badge-circle badge-${inputKeys[index].keytable_id?.endsWith('_global') ? 'success' : 'primary'}`}
                                      style={{
                                        width: 20,
                                        height: 20,
                                        verticalAlign: 'middle',
                                      }}
                                    >
                                      {inputKeys[index].keytable_id?.endsWith('_global') ? 'G' : 'L'}
                                    </span>
                                    <span className='truncate'>{inputKeys[index].name}</span>
                                  </div>
                                  <button
                                    title='제거'
                                    className='btn-active-color-secondary btn btn-sm btn-icon-sm h-[20px] w-[20px] p-0 hover:text-red-700'
                                    role='button'
                                    aria-label='remove tag'
                                    onClick={() => {
                                      const updatedInputKeys = [...inputKeys];
                                      updatedInputKeys[index] = {
                                        ...updatedInputKeys[index],
                                        name: '',
                                        required: false,
                                        fixed_value: null,
                                        keytable_id: '',
                                      };
                                      setInputKeys(updatedInputKeys);
                                    }}
                                  >
                                    ×
                                  </button>
                                </div>
                              </div>
                              <button
                                type='button'
                                onClick={() => {
                                  handleOpenKeyTableModal(index);
                                }}
                                className='btn-icon btn btn-sm btn-light text-primary'
                                style={{
                                  backgroundColor: '#ffffff',
                                  border: '1px solid #d1d5db',
                                  borderRadius: '6px',
                                  padding: '6px',
                                  color: '#6b7280',
                                  cursor: 'pointer',
                                  fontSize: '14px',
                                  transition: 'all 0.2s ease',
                                  marginLeft: '8px',
                                }}
                              >
                                🔍
                              </button>
                            </>
                          ) : (
                            <>
                              <input type='text' value={''} className='input w-full border-0 outline-none' readOnly={true} placeholder='Key' />
                              <button
                                type='button'
                                onClick={() => {
                                  handleOpenKeyTableModal(index);
                                }}
                                className='btn-icon btn btn-sm btn-light text-primary'
                                style={{
                                  backgroundColor: '#ffffff',
                                  border: '1px solid #d1d5db',
                                  borderRadius: '6px',
                                  padding: '6px',
                                  color: '#6b7280',
                                  cursor: 'pointer',
                                  fontSize: '14px',
                                  transition: 'all 0.2s ease',
                                }}
                              >
                                🔍
                              </button>
                            </>
                          )}
                        </div>

                        <button
                          onClick={() => {
                            handleRemoveInput(index);
                          }}
                          className='btn-icon btn btn-sm btn-light text-primary btn-bg-del'
                          style={{
                            border: '1px solid #d1d5db',
                            borderRadius: '6px',
                            padding: '6px',
                            color: '#6b7280',
                            cursor: 'pointer',
                            fontSize: '14px',
                            transition: 'all 0.2s ease',
                          }}
                          title='삭제'
                        >
                          🗑️
                        </button>
                      </div>
                    </div>
                  ))}

                  <div className='mt-2 flex justify-center'>
                    <button onClick={handleAddInput} className='btn btn-light rounded-md border border-gray-300 px-4 py-2 text-dark'>
                      Add Output
                    </button>
                  </div>
                </div>
              </div>
            </>
          )}
        </>

        <CardFooter>
          <NodeFooter onClick={handleFooterFold} isToggle={nodeData.isToggle as boolean} />
        </CardFooter>
      </Card>
    </div>
  );
};
